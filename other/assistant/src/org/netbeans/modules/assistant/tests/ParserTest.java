/*
 * ParserTest.java
 *
 * Created on October 30, 2002, 11:46 AM
 */

package org.netbeans.modules.assistant.tests;

import org.netbeans.modules.assistant.*;
import java.net.*;
/**
 *
 * @author  rg125988
 */
public class ParserTest {
    
    /** Creates a new instance of ParserTest */
    public ParserTest() {        
        URL url = getClass().getResource("assistant.xml");
        AssistantContext ctx = new AssistantContext(url);
        ctx.toString();
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        new ParserTest();
    }
    
}
