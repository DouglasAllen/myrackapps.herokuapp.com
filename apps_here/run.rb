require "./app"

app = Rack::Builder.new do
	run Sinatra::Application
end

s = Rack::Server.new(AccessLog: [], Port: 3000, Host: 'localhost', app: app)
s.options.each {|o| p o}
s.start