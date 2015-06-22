#~ require 'sinatra'
require 'find'
require 'rdiscount'
require 'liquid'

class App < Sinatra::Base	
	
  def get_files(path)
    dir_list_array = Array.new
    Find.find(path) do |f|
      dir_list_array << File.basename(f, ".*") if !File.directory?(f) 
    end
    dir_list_array
  end  

  helpers do
    def formatter(page)
      formatted = ""
      formatted = page.gsub(/[-]/, ' ').capitalize
      return formatted
    end
  end

  MDRoot = File.expand_path(File.dirname(__FILE__) +  "/views/posts/md")
  get '/markdown' do
    #~ MDRoot
    @arr = get_files(MDRoot)
    erb :markdown
  end
	
	get '/markdown/posts/md/:link' do
    halt 404 unless File.exist?(MDRoot + "/#{params[:link]}.markdown")
    #~ @time = Time.new		
    markdown :"posts/md/#{params[:link]}", :layout_engine => :erb
  end

  HTMLRoot = File.expand_path(File.dirname(__FILE__) +  "/views/posts/html")
  get '/html' do
    @arr = get_files(HTMLRoot)
    erb :html
  end

  get '/html/posts/html/:link' do
		#~ "Hello World"
		#~ HTMLRoot + "/#{params[:link]}.erb"
		halt 404 unless File.exist?(HTMLRoot + "/#{params[:link]}.erb")
    #~ @time = Time.new
    erb :"posts/html/#{params[:link]}", :layout_engine => :erb
  end

  get '/about' do
    #if you don't want to use Markdown for pages, do this:
    #erb :"pages/about"
    #Then create about.erb in views/pages

    markdown :"pages/about", :layout_engine => :erb
  end

  get '/' do
    @time = Time.new
    "The server time is #{@time}"
  end

  #~ not_found do
    #~ erb :missing
  #~ end
end