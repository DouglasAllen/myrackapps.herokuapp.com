run lambda {|env| [200, {"Content-Type" => "text/html"}, env.sort.map {|k,v| "#{k}=>#{v}<br/>"}]}
