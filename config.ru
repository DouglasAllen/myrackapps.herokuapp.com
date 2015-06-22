require 'rack/source'
require 'sinatra'
require_relative 'apps_here/app'
require 'rack/lobster'
require 'rack/builder'
require 'rack/commonlogger'
require 'rack/showexceptions'
require 'rack-proxy'
require 'rbconfig'

app = Rack::Builder.new do
  use Rack::Reloader
  use Rack::CommonLogger
  use Rack::ShowExceptions  
			
  class Heartbeat
    def self.call(env)
      [200, { "Content-Type" => "text/html" }, ["#{Rack::Builder.methods - Object.methods}"]]
    end
  end
			
  map '/docs' do
    # This is the root of our app
    @root = File.expand_path(File.dirname(__FILE__) + "/public/htdocs/")
		use Rack::Auth::Digest::MD5, "Hello, World", "12345" do |username|
      'secret'
    end
    run lambda {|env| Rack::Directory.new(@root).call(env)} 
  end
	
  map '/' do
    use Rack::Static,
      :urls => ["/fonts", "/images", "/javascripts", "/stylesheets"],
      :root => "public/htdocs"	
    run lambda { |env| [200, { 
      'Content-Type' => "text/html",
      'Cache-Control' => 'public, max-age=86400'		
      },
      File.open('public/htdocs/aboutme.html', File::RDONLY)]}
      #['Logged in!']]}
	end
		
  map '/heartbeat' do
    run lambda {|env| [200, {'Content-Type' => 'text/html'}, ["<h1>/ /hello /lobster /docs /env /config /hb</h1>"]]}
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
	
  map '/sinatra' do
		use Rack::Static,
      :urls => ["/fonts", "/images", "/javascripts", "/stylesheets"],
      :root => "public/htdocs"	
    run App
  end	

end.to_app

run app



