/*
 * NBSTest.java
 *
 * Created on September 1, 2006, 2:46 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.netbeans.modules.languages.java;


import org.netbeans.api.languages.ParseException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;
import junit.framework.TestCase;
import org.netbeans.modules.languages.Language;
import org.netbeans.modules.languages.NBSLanguageReader;
import org.netbeans.modules.languages.parser.AnalyserAnalyser;
import org.netbeans.api.languages.ParseException;
import org.netbeans.modules.languages.parser.Petra;


/**
 *
 * @author Jan Jancura
 */
public class NBSTest extends TestCase {
    
    public NBSTest (String testName) {
        super (testName);
    }
    
    public void testFirst () {
        InputStream is = getClass ().getClassLoader ().getResourceAsStream ("org/netbeans/modules/languages/java/Java.nbs");
        try {
            Language l = NBSLanguageReader.readLanguage ("test", is, "test/x-java");
            List r = l.getAnalyser ().getRules ();
            AnalyserAnalyser.printRules (r, null);
            AnalyserAnalyser.printUndefinedNTs (r, null);
            Map f = Petra.first2 (r);
            AnalyserAnalyser.printDepth (f, null);
            AnalyserAnalyser.printConflicts (f, null);
            AnalyserAnalyser.printF (f, null);
        } catch (ParseException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
