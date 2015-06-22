# xml.rb
class XML
  # Create an instance of this class, specifying a stream or object to
  # hold the output. This can be any object that responds to <<(String).
  def initialize
    @xml = ''
  end
  
  attr_reader :xml
  
  # Output the specified object as CDATA, return nil
  def content text
    @xml << text.to_s
    nil
  end

  # Output the specified object as a comment, return nil.
  def comment(text)
    @xml << "<!-- #{text} -->"
    nil
  end
  
  # Output a tag with the specified name and attributes.
  # If there is a block invoke it to output or return content.
  # Return nil.
  def tag tagname, attributes = {}
    # Output the tag name
    @xml << "<#{tagname}"
    
    # Output the attributes
    attributes.each{|a, v| @xml << " #{a}='#{v}'"}
    
    if block_given?
      # This block has content
      @xml << '>'                  # End the opening tag
      cont = yield                 # Invoke the block to output or return content
      (@xml << cont.to_s) if cont  # If any content returned
      @xml << "</#{tagname}>"      # Output it as a string and Close the tag
    else
      # Otherwise, this is an empty tag, so just close it.
      @xml << '/>'
    end
    nil # Tags output themselves, so they don't return any content
  end
  
  # The code below is what changes this from an ordinary class into a DSL.
  # First: any unknown method is treated as the name of a tag.
  alias method_missing tag
  
  # Second: run a block in a new instance of the class.
  def self.generate &blk
    o = XML.new
    o.instance_eval &blk
    o.xml
  end
end
 