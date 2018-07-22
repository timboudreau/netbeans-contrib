/*
 * FSyntaxSupport.java
 *
 *
 */

package org.netbeans.modules.fortran;
 
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.event.DocumentEvent;
import javax.swing.text.BadLocationException;
import org.netbeans.editor.Analyzer;
import org.netbeans.editor.BaseDocument;
import org.netbeans.editor.TextBatchProcessor;
import org.netbeans.editor.TokenContextPath;
import org.netbeans.editor.TokenID;
import org.netbeans.editor.Utilities;
import org.netbeans.editor.ext.ExtSyntaxSupport;
import org.netbeans.modules.cnd.api.model.CsmFunction;
import org.netbeans.modules.cnd.api.model.CsmParameter;
import org.netbeans.modules.cnd.api.model.CsmType;
import org.netbeans.modules.cnd.editor.fortran.FTokenContext;

/**
 * Utility class for Fortran syntax support
 * @author Andrey Gubichev
 */
public class FSyntaxSupport extends ExtSyntaxSupport {
    
     public FSyntaxSupport(BaseDocument doc) {
        super(doc);
    }
    private HashMap fileVariableMaps = new HashMap();
    static final int INIT = 0;
    static final int AFTER_TYPE = 1;
    static final int AFTER_VARIABLE = 2;
    static final int AFTER_COMMA = 3;
    static final int AFTER_DOT = 4;
    static final int AFTER_TYPE_LSB = 5;
    static final int AFTER_MATCHING_VARIABLE_LSB = 6;
    static final int AFTER_MATCHING_VARIABLE = 7;
    static final int AFTER_EQUAL = 8; // in decl after "var ="
    static final int AFTER_ARROW = 9;
    static final int AFTER_SCOPE = 10;

    private static final TokenID[] COMMENT_TOKENS = new TokenID[] {
                FTokenContext.LINE_COMMENT
   };

    public TokenID[] getCommentTokens() {
        return COMMENT_TOKENS;
    }
    protected void documentModified(DocumentEvent evt) {
        super.documentModified(evt);
        fileVariableMaps.clear();        
    }

  /** Return the position of the last command separator before
    * the given position.
    */    
    public int getLastCommandSeparator(final int pos) throws BadLocationException {
      if (pos == 0)
        return 0;
        final int posLine = Utilities.getLineOffset(getDocument(), pos);
        TextBatchProcessor tbp = new TextBatchProcessor() {
                public int processTextBatch(BaseDocument doc, int startPos, int endPos,
                                                                 boolean lastBatch) {
                       try {
                           int[] blks = getCommentBlocks(endPos, startPos);
                         int lastSeparatorOffset = 0;
                          lastSeparatorOffset = processTextBatch(doc, lastSeparatorOffset, 0, lastBatch);
                            return lastSeparatorOffset;
                       } catch (BadLocationException e) {
                             e.printStackTrace();
                             return -1;
                         }
                   }
        };
        int lastPos = getDocument().processText(tbp, pos, 0);     
        return lastPos;
    }
    
     public boolean isStaticBlock(int pos) {
        return false;
    }
    
    public boolean isAnnotation(int pos) {
        return false;
    }    

    public int[] getFunctionBlock(int[] identifierBlock) throws BadLocationException {
        int[] retValue = super.getFunctionBlock(identifierBlock);
        return retValue;
    }
     public static class FDeclarationTokenProcessor
        implements DeclarationTokenProcessor, VariableMapTokenProcessor {

        protected FSyntaxSupport sup;

        /** Position of the begining of the declaration to be returned */
        protected int decStartPos = -1;

        protected int decArrayDepth;

        /** Starting position of the declaration type */
        protected int typeStartPos;

        /** Position of the end of the type */
        protected int typeEndPos;

        /** Offset of the name of the variable */
        protected int decVarNameOffset;

        /** Length of the name of the variable */
        protected int decVarNameLen;

        /** Currently inside parenthesis, i.e. comma delimits declarations */
        protected int parenthesisCounter;

        /** Depth of the array when there is an array declaration */
        protected int arrayDepth;

        protected char[] buffer;

        protected int bufferStartPos;

        protected String varName;

        protected int state;

        /** Map filled with the [varName, type/classifier] pairs */
        protected HashMap varMap;


        /** Construct new token processor
        * @param varName it contains valid varName name or null to search
        *   for all variables and construct the variable map.
        */
        public FDeclarationTokenProcessor(FSyntaxSupport sup, String varName) {
            this.sup = sup;
            this.varName = varName;
            if (varName == null) {
                varMap = new HashMap();
            }
        }

        public int getDeclarationPosition() {
            return decStartPos;
        }

        public Map getVariableMap() {
            return varMap;
        }

        protected void processDeclaration() {
            // XXX review!
            if (varName == null) { // collect all variables
                String decType = new String(buffer, typeStartPos - bufferStartPos,
                                            typeEndPos - typeStartPos);
                if (decType.indexOf(' ') >= 0) {
                    decType = Analyzer.removeSpaces(decType);
                }
                String decVarName = new String(buffer, decVarNameOffset, decVarNameLen);
                
               decStartPos = typeStartPos;
            }
        }

        public boolean token(TokenID tokenID, TokenContextPath tokenContextPath,
        int tokenOffset, int tokenLen) {
            int pos = bufferStartPos + tokenOffset;

	    // Check whether we are really recognizing the java tokens
	    if (!tokenContextPath.contains(FTokenContext.contextPath)) {
		state = INIT;
		return true;
	    }

            switch (tokenID.getNumericID()) {
                case FTokenContext.KW_CHARACTER_ID:
                case FTokenContext.KW_DOUBLE_ID:
                case FTokenContext.KW_DOUBLEPRECISION_ID:
                case FTokenContext.KW_INTEGER_ID:
                case FTokenContext.KW_LOGICAL_ID:
                case FTokenContext.KW_REAL_ID:
                    typeStartPos = pos;
                    arrayDepth = 0;
                    typeEndPos = pos + tokenLen;
                    state = AFTER_TYPE;
                    break;
/*
                case CCTokenContext.DOT_ID:
                case CCTokenContext.DOTMBR_ID:    
                    switch (state) {
                        case AFTER_TYPE: // allowed only inside type
                            state = AFTER_DOT;
                            typeEndPos = pos + tokenLen;
                            break;
                            
                        case AFTER_EQUAL:
                        case AFTER_VARIABLE:
                            break;

                        default:
                            state = INIT;
                            break;
                    }
                    break;

                case CCTokenContext.ARROW_ID:
                case CCTokenContext.ARROWMBR_ID: 
                    switch (state) {
                        case AFTER_TYPE: // allowed only inside type
                            state = AFTER_ARROW;
                            typeEndPos = pos + tokenLen;
                            break;
                            
                        case AFTER_EQUAL:
                        case AFTER_VARIABLE:
                            break;

                        default:
                            state = INIT;
                            break;
                    }
                    break;

                case CCTokenContext.SCOPE_ID:
                    switch (state) {
                        case AFTER_TYPE: // allowed only inside type
                            state = AFTER_SCOPE;
                            typeEndPos = pos + tokenLen;
                            break;
                            
                        case AFTER_EQUAL:
                        case AFTER_VARIABLE:
                            break;

                        default:
                            state = INIT;
                            break;
                    }
                    break;                    


                case CCTokenContext.LBRACKET_ID:
                    switch (state) {
                        case AFTER_TYPE:
                            state = AFTER_TYPE_LSB;
                            arrayDepth++;
                            break;

                        case AFTER_MATCHING_VARIABLE:
                            state = AFTER_MATCHING_VARIABLE_LSB;
                            decArrayDepth++;
                            break;

                        case AFTER_EQUAL:
                            break;
                            
                        default:
                            state = INIT;
                            break;
                    }
                    break;

                case CCTokenContext.RBRACKET_ID:
                    switch (state) {
                        case AFTER_TYPE_LSB:
                            state = AFTER_TYPE;
                            break;

                        case AFTER_MATCHING_VARIABLE_LSB:
                            state = AFTER_MATCHING_VARIABLE;
                            break;

                        case AFTER_EQUAL:
                            break;
                            
                        default:
                            state = INIT;
                            break;
                    }
                    break; // both in type and varName
 */

                case FTokenContext.LPAREN_ID:
                    parenthesisCounter++;
                    if (state != AFTER_EQUAL) {
                        state = INIT;
                    }
                    break;

                case FTokenContext.RPAREN_ID:
                    if (state == AFTER_MATCHING_VARIABLE) {
                        processDeclaration();
                    }
                    if (parenthesisCounter > 0) {
                        parenthesisCounter--;
                    }
                    if (state != AFTER_EQUAL) {
                        state = INIT;
                    }
                    break;
//
//                case FTokenContext.LBRACE_ID:
//                case FTokenContext.RBRACE_ID:
//                    if (parenthesisCounter > 0) {
//                        parenthesisCounter--; // to tolerate opened parenthesis
//                    }
//                    state = INIT;
//                    break;

//                case CCTokenContext.COMMA_ID:
//                    if (parenthesisCounter > 0) { // comma is declaration separator in parenthesis
//                        if (parenthesisCounter == 1 && state == AFTER_MATCHING_VARIABLE) {
//                            processDeclaration();
//                        } 
//                        if (state != AFTER_EQUAL) {
//                            state = INIT;
//                        }
//                    } else { // not in parenthesis
//                        switch (state) {
//                            case AFTER_MATCHING_VARIABLE:
//                                processDeclaration();
//                                // let it flow to AFTER_VARIABLE
//                            case AFTER_VARIABLE:
//                            case AFTER_EQUAL:
//                                state = AFTER_COMMA;
//                                break;
//
//                            default:
//                                state = INIT;
//                                break;
//                        }
//                    }
//                    break;
                    
//
//                case CCTokenContext.NEW_ID:
//                    if (state != AFTER_EQUAL) {
//                        state = INIT;
//                    }
//                    break;
                    
                case FTokenContext.EQ_ID:
                    switch (state) {
                        case AFTER_MATCHING_VARIABLE:
                            processDeclaration();
                            // flow to AFTER_VARIABLE
                            
                        case AFTER_VARIABLE:
                            state = AFTER_EQUAL;
                            break;
                            
                        case AFTER_EQUAL:
                            break;
                            
                        default:
                            state = INIT;
                    }
                    break;
//
//                case CCTokenContext.SEMICOLON_ID:
//                    if (state == AFTER_MATCHING_VARIABLE) {
//                        processDeclaration();
//                    }
//                    state = INIT;
//                    break;

                case FTokenContext.IDENTIFIER_ID:
                    switch (state) {
                        case AFTER_TYPE:
                        case AFTER_COMMA:
                            if (varName == null || Analyzer.equals(varName, buffer, tokenOffset, tokenLen)) {
                                decArrayDepth = arrayDepth;
                                decVarNameOffset = tokenOffset;
                                decVarNameLen = tokenLen;
                                state = AFTER_MATCHING_VARIABLE;
                            } else {
                                state = AFTER_VARIABLE;
                            }
                            break;

                        case AFTER_VARIABLE: // error
                            state = INIT;
                            break;
                            
                        case AFTER_EQUAL:
                            break;

//                        case AFTER_DOT:
//                            typeEndPos = pos + tokenLen;
//                            state = AFTER_TYPE;
//                            break;
//
//                        case AFTER_ARROW:
//                            typeEndPos = pos + tokenLen;
//                            state = AFTER_VARIABLE;
//                            break;
//
//                        case AFTER_SCOPE: // only valid after type
//                            typeEndPos = pos + tokenLen;
//                            state = AFTER_TYPE;
//                            break;
                            
                        case INIT:
                            typeStartPos = pos;
                            arrayDepth = 0;
                            typeEndPos = pos + tokenLen;
                            state = AFTER_TYPE;
                            break;

                        default:
                            state = INIT;
                            break;
                    }
                    break;

                case FTokenContext.WHITESPACE_ID: // whitespace ignored
                    break;
//                    
//                case CCTokenContext.COLON_ID: // 1.5 enhanced for loop sysntax
//                    processDeclaration();

//                case CCTokenContext.INSTANCEOF_ID:
                default:
                    state = INIT;
            }

            return true;
        }

        public int eot(int offset) {
            return 0;
        }

        public void nextBuffer(char[] buffer, int offset, int len,
                               int startPos, int preScan, boolean lastBuffer) {
            this.buffer = buffer;
            bufferStartPos = startPos - offset;
        }

     }    
    
}
