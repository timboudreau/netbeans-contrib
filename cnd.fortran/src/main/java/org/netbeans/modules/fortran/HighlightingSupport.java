/*
 * Test.java
 * 
 * Created on 25.08.2007, 13:23:41
 * 
 */

package org.netbeans.modules.fortran;
import org.netbeans.api.languages.*;


/**
 *
 * @author Arkady Galyash
 */
public class HighlightingSupport {
        private static final String MIME_TYPE = "text/fortran";
        private static CharInput input;
        private static int nested = 0;

    public HighlightingSupport() {
    }
    
    private static void skipWS()
    {
      while (!input.eof () &&
                input.next () == ' '
        ) {
            input.read ();
        }
    }
    
    private static void readRestOfIdentifier()
    {
      while (!input.eof() && ((input.next() == ' ' || (input.next()>='A' && input.next()<='Z') 
              || (input.next()>='0' && input.next()<='9')))
        ) {
            input.read ();
        }
    }
    
    private static void readIdentifier()
    {
      if (!input.eof() && (input.next() >= 'A' && input.next() <= 'Z'))
        input.read();
      readRestOfIdentifier();
    }
    
    private static void readNumber()
    {
      while (!input.eof() && ((input.next() == ' ' || (input.next()>='0' && input.next()<='9')))
        ) {
            input.read ();
        }
    }
    
    private static void readExpr()
    {
      if (!input.eof() && (input.next() == '+' || input.next() == '-'))
        input.read();
      skipWS();
      while (!input.eof() && ((input.next() == ' ' || ((input.next()>='0' && input.next()<='9')))
              || input.next() == '.')
        ) {
            input.read ();
        }
    }
    public static Object[] lexIf(CharInput input){
      if (!input.eof() && input.next() == '('){
        input.read();
        nested++;
        return new Object[] {
                ASTToken.create (MIME_TYPE, "operator", "", 0),
                "AFTER_IF"
        };
      }
      else
        if (!input.eof() && input.next() == ')'){
          input.read();
          nested--;
          if (nested == 0)
            return new Object[] {
                ASTToken.create (MIME_TYPE, "ERR", "", 0),
                "FIRST_WORD"
            };
          else
            return new Object[] {
                  ASTToken.create (MIME_TYPE, "operator", "", 0),
                 "AFTER_IF"
           };
        }
        else
          return null;
    }
    public static Object[] test(CharInput input) {
      HighlightingSupport.input = input;
      if (!input.eof() && input.next() == 'D')
      {
        input.read();
        skipWS();
        if (!input.eof() && input.next() == 'O')
        {
            input.read();
            int doEnd = input.getIndex();
            readNumber();
            readIdentifier();
            skipWS();
            int identEnd = input.getIndex();
            if (!input.eof() && input.next() == '=')
            {
              input.read();
              skipWS();
              readExpr();
              if (!input.eof() && input.next() == ',')
              {
                input.setIndex(doEnd);
                return  new Object[] {
                ASTToken.create (MIME_TYPE, "DO", "", 0),
                "LAST"
                };
              }
              else
              {
                input.setIndex(identEnd);
                return new Object[] {
                ASTToken.create (MIME_TYPE, "identifier", "", 0),
                "LAST"
                };
              }
            }else{
                if (!input.eof() && input.next() == '(')
                {
                  input.setIndex(doEnd);
                  return new Object[] {
                  ASTToken.create (MIME_TYPE, "DO", "", 0),
                  "BEFORE_WHILE"
                  };
                }else{
                  input.setIndex(identEnd);
                  return new Object[] {
                  ASTToken.create (MIME_TYPE, "identifier", "", 0),
                  "LAST"
                  };                
                }
            }
        }
        readRestOfIdentifier();
        return new Object[] {
                ASTToken.create (MIME_TYPE, "identifier", "", 0),
                "LAST"
        };
      }
      readIdentifier();
      return new Object[] {
                ASTToken.create (MIME_TYPE, "identifier", "", 0),
                "LAST"
        };
    }
}
