# DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
#
# Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
#
# The contents of this file are subject to the terms of either the GNU
# General Public License Version 2 only ("GPL") or the Common
# Development and Distribution License("CDDL") (collectively, the
# "License"). You may not use this file except in compliance with the
# License. You can obtain a copy of the License at
# http://www.netbeans.org/cddl-gplv2.html
# or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
# specific language governing permissions and limitations under the
# License.  When distributing the software, include this License Header
# Notice in each file and include the License file at
# nbbuild/licenses/CDDL-GPL-2-CP.  Sun designates this
# particular file as subject to the "Classpath" exception as provided
# by Sun in the GPL Version 2 section of the License file that
# accompanied this code. If applicable, add the following below the
# License Header, with the fields enclosed by brackets [] replaced by
# your own identifying information:
# "Portions Copyrighted [year] [name of copyright owner]"
#
# Contributor(s):
#
# The Original Software is NetBeans. The Initial Developer of the Original
# Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
# Microsystems, Inc. All Rights Reserved.
#
# If you wish your version of this file to be governed by only the CDDL
# or only the GPL Version 2, indicate your decision by adding
# "[Contributor] elects to include this software in this distribution
# under the [CDDL or GPL Version 2] license." If you do not indicate a
# single choice of license, a recipient has the option to distribute
# your version of this file under either the CDDL, the GPL Version 2 or
# to extend the choice of license to its licensees as provided above.
# However, if you add GPL Version 2 code and therefore, elected the GPL
# Version 2 license, then the option applies only if the new code is
# made subject to such option by the copyright holder.

require 'uri'
require 'net/http'
require 'rexml/document'

class Faqpage
  def initialize (title, url)
    @title = title
    @url = url
  end

  def to_s
    return @title + ' (' + @url + ')'
  end
  
  def ==(other)
    @url = other.url
  end
  
  def hash
    @url.hash
  end
  
  def name
    /.*(DevFaq.*)/.match(@url)[1]
  end
  
  def title
    @title
  end
end

base = 'http://wiki.netbeans.org'
faqsite = base + "/NetBeansDeveloperFAQ"
content = Net::HTTP.get(URI.parse(faqsite))
puts "Loading content from #{faqsite}"
content.gsub!('<hr>', '<hr/>')
content.gsub!('<hr >', '<hr/>')
puts content
doc = REXML::Document.new content
matches = {}
titleexp = /.*?>(.*?)<.*/
faqexp = /.*DevFaq.*/
allcontent = '<html><head><title>NetBeans Master Developer FAQ</title></head><body><h1>NetBeans Master Developer FAQ</h1><ul>'
allcontent += "Generated " + Time.now.gmtime.to_s + "<p>"
REXML::XPath.each(doc,'//div//li/a[@class="wikipage"]') do |match|
  path = match.attribute('href')
  if (faqexp.match(match.to_s))
    spath = path.to_s
    #skip two huge and unwieldy FAQ items - the app client one should
    #probably not be in the FAQ, but in the tutorials section of the web site
    if ('http://wiki.netbeans.org/wiki/view/DevFaqWindowsInternals' != spath &&
        'http://wiki.netbeans.org/wiki/view/DevFaqAppClientOnNbPlatformTut' != spath  &&
        'http://wiki.netbeans.org/wiki/view/DevFaqTutorialsAPIJa' != spath &&
        'http://wiki.netbeans.org/wiki/view/DevFaqTutorialsAPI' != spath) 
      title = titleexp.match(match.to_s)[1]
      page = Faqpage.new(title, spath)
      matches[spath] = page
      puts "Found #{page}"
    end
  end
end

matches.each { |url, faqpage|
   allcontent += '<li><a href="#' + faqpage.name() + '">' + faqpage.title() + "</a></li>\n"
}

allcontent += "</ul>\n"
matches.keys.sort.each { |url| 
  puts "FETCHING #{url}"
  content = Net::HTTP.get(URI.parse(url))
  doc = REXML::Document.new content
  pagecontentDoc = REXML::XPath.first(doc, '//div[@id="pagecontent"]')
  pagecontent = pagecontentDoc.to_s
  contentdoc = REXML::XPath.each(pagecontentDoc, '//a[@class="wikipage"]') do |match|
    linkAttr = match.attribute('href')
    if (linkAttr) 
      puts "Check link #{linkAttr}"
      link = linkAttr.to_s
      if (link && /\/wiki\/view.*?/.match(link)) 
        link = base + link
        if (matches[link])
          link = "#" + matches[link].name()
        end
        puts "Substitute #{linkAttr} with #{link}"
        pagecontent.gsub!(linkAttr.to_s, link)
        #Faq entries are inconsistent about title form, normalize
        pagecontent.gsub!('<h1', '<h2')
        pagecontent.gsub!('<h3', '<h2')
        pagecontent.gsub!('<h4', '<h2')
        pagecontent.gsub!('</h1', '</h2')
        pagecontent.gsub!('</h3', '</h2')
        pagecontent.gsub!('</h4', '</h2')
        pagecontent.gsub!('<hr>', '<hr/>')
        pagecontent.gsub!('<hr >', '<hr/>')
        
      end
    end
  end
  allcontent += '<a name="' + matches[url].name() + '">'
  allcontent += pagecontent
}
allcontent += '</body></html>'

out=($*[0] or '/tmp/faq.html')
puts "WRITING #{out}"
open(out, 'w') { |file| file.puts(allcontent) }
