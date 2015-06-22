require 'middleman-core/profiling'
if ARGV.include? '--profile'
  Middleman::Profiling.profiler = Middleman::Profiling::RubyProfProfiler.new
end
Middleman::Profiling.start

require "middleman-core/load_paths"
Middleman.setup_load_paths

require "middleman-core/cli"

# Change directory to the root
Dir.chdir(ENV["MM_ROOT"]) if ENV["MM_ROOT"]

# Start the CLI
Middleman::Cli::Base.start

# bundle exec middleman --port 5000
# it will never get here while running so just do the string in a shell.
exec 'firefox http://localhost:4567/'

#see http://willschenk.com/building-sites-with-middleman/
#       http://stackoverflow.com/questions/22744354/what-does-this-error-while-running-middleman-mean
#       https://forum.middlemanapp.com/t/what-does-this-error-while-running-middleman-mean/1243/2
#       https://fp21st.wordpress.com/2014/03/14/middleman-static-site-generator-port-change/
#       