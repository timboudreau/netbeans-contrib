/*
 * FCompletionSupport.java
 *
 *
 */

package org.netbeans.modules.fortran;
 
import antlr.RecognitionException;
import antlr.TokenStreamException;
import org.netbeans.modules.fortran.generated.Fortran77Lexer;
import org.netbeans.modules.fortran.generated.Fortran77Parser;
import java.io.StringReader;
import java.util.HashSet;

/**
 *
 * @author Andrey Gubichev
 */
public class FCompletionSupport {
private static final String[] Fortran90Keywords = {"program","end","subroutine","dimension","equivalence",
                "common","real","complex","doubleprecision","integer","logical",
                "pointer","implicit","none","character","keyword","intrinsic",
                "save","data","let","assign","goto","if","then","elseif",
                "else","endif","do","enddo","continue","while", "stop","pause",
                "read","print","open","close","inquire","backspace","endfile",
                "format","call","return","parameter","entry","block","function"};

    
    /** Creates a new instance of FCompletionSupport */
    private FCompletionSupport() {
    }
    public static HashSet<String> getCompletionResult(String text){
        Fortran77Lexer lex = new Fortran77Lexer(new StringReader(text));
        Fortran77Parser parser = new Fortran77Parser(lex);
            try {
                parser.program();
            } catch (TokenStreamException ex) {
                ex.printStackTrace();
            } catch (RecognitionException ex) {
                ex.printStackTrace();
            }
        HashSet<String> vars = parser.getVariables();
        for (int i=0; i<Fortran90Keywords.length; i++)
            vars.add(Fortran90Keywords[i]);
        
        return vars;
    }
}
