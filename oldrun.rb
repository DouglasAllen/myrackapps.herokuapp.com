require "rack"

Rack::Server.start

#~ require 'rack-webconsole'

#~ use Rack::Webconsole.inject_jquery = true

#~ use Rack::Auth::Digest::MD5, "Hello, World", "12345" do |username|
  #~ 'secret'
#~ end

#~ run proc { |env|
  #~ [200, { 'Content-Type' => "text/html" }, ['Logged in!']]
#~ }

#~ class MySinatraApp < Sinatra::Application
  #~ use Rack::Webconsole
  #~ # . . .
#~ end