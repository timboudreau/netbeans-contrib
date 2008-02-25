
package org.netbeans.modules.fort.model.xml;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Andrey Gubichev
 */
public class XMLUtil {
    private final static Pattern pat = 
            Pattern.compile("(%[\\p{Alpha}\\p{Digit}]+%)");
    
    public static List<String> descriptionTokenizer(String descr) {
        Matcher met = pat.matcher(descr);        
        List<String> ret = new ArrayList<String>();
        
        while(met.find()) {
            ret.add(met.group());        
        }
        
        return ret;
    }        

}
