require "rack"
require 'rack/lobster'
require 'rack/source'

app = Rack::Builder.new do
  use Rack::Reloader
  use Rack::CommonLogger
  use Rack::ShowExceptions
  use Rack::Static,
      :urls => ["/fonts", "/images", "/javascripts", "/stylesheets"],
      :root => "public/htdocs"
			
  class Heartbeat
    def self.call(env)
      [200, { "Content-Type" => "text/html" }, ["#{Rack::Builder.methods - Object.methods}"]]
    end
  end
			
  map '/docs' do
    # This is the root of our app
    @root = File.expand_path(File.dirname(__FILE__) + "/public/htdocs/")
    run lambda {|env| Rack::Directory.new(@root).call(env)} 
  end
	
  map '/' do   

    #~ use Rack::Auth::Digest::MD5, "Hello, World", "12345" do |username|
      #~ 'secret'
    #~ end
		
    run lambda { |env| [200, { 
      'Content-Type' => "text/html",
      'Cache-Control' => 'public, max-age=86400'		
      },
      File.open('public/htdocs/aboutme.html', File::RDONLY)]}
      #['Logged in!']]}
	end
		
  map '/heartbeat' do
    run lambda {|env| [200, {'Content-Type' => 'text/html'}, ["<h1>/hello /lobster /root /env /world</h1>"]]}
  end

  map '/hello' do
    run lambda {|env| [200, {'Content-Type' => 'text/html'}, ["<h1>Hello there!</h1>"]]}
  end

  map '/lobster' do
    use Rack::Lint
    app = Rack::Lobster.new
    run app
  end

  map '/config' do
    run Rack::Source.new('./config.ru')
  end

  map '/env' do  
    run lambda {|env| [200,{"Content-Type" => "text/html"}, env.sort.map {|k, v| "#{k}=>#{v}</br>"}]}
  end
	
  map '/hb' do
    run Heartbeat
  end
	
	map '/root' do
    run lambda {|env| [200,{"Content-Type" => "text/html"}, ['<a href="http://rack.github.io/">Rack</a>']]}
  end

end

s = Rack::Server.new(AccessLog: [], Port: 3000, Host: 'localhost', app: app)
p s.options
s.start