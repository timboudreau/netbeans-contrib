
package org.netbeans.modules.fort.model.lang.impl;


import antlr.collections.AST;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import org.netbeans.modules.fort.model.lang.FFile;
import org.netbeans.modules.fort.model.lang.FObject;
import org.netbeans.modules.fort.model.lang.FOffsetable;

/**
 * base class for FOffsetable
 */
public abstract class OffsetableBase implements FOffsetable, FObject {
    private  FFile fileRef; 
    private AST ast;
    private final Position startPosition;
    private final Position endPosition;

    protected OffsetableBase(AST ast, FFile file) {

            this.fileRef = file;
        this.ast = ast;
        FortAST startAST = getStartAst(ast);
        startPosition = (startAST == null) ? 
            new Position(0,0,0) :
            new Position(startAST.getLine(), startAST.getColumn(), startAST.getOffset());
        FortAST endAST = getEndAst(ast);
        endPosition = (endAST == null) ? 
            new Position(0,0,0) : 
            new Position(endAST.getEndLine(), endAST.getEndColumn(), endAST.getEndOffset());
    }
    
    protected OffsetableBase(FFile file) {
        this(null, file);
    }
    
   
    protected OffsetableBase(FFile file, Position start, Position end) {

        this.fileRef = file;    
        this.startPosition = new Position(start);
        this.endPosition = new Position(end);
    }
    
    /**
     * @return start offset of position
     */
    public int getStartOffset() {
        return getStartPosition().getOffset();
    }
    
    /**
     * @return end offset of position
     */
    public int getEndOffset() {
        return getEndPosition().getOffset();
    }

    /**
     * @return start position
     */
    public Position getStartPosition() {
        return startPosition;
    }
    
    /**
     * @return end position
     */
    public Position getEndPosition() {
        return endPosition;
    }
    
    private FortAST getStartAst(AST node) {
        if( node != null ) {
            FortAST FAst = getFirstAST(node);
            if( FAst != null ) {
                return FAst;
            }
        }
        return null;
    }

    protected FortAST getEndAst(AST node) {
         //TODO:implement using AstUtil
        return null;
    }
    
    private static FortAST getFirstAST(AST node) {
        if( node != null ) {
            if( node instanceof FortAST ) {
                return (FortAST) node;
            }
            else {
                return getFirstAST(node.getFirstChild());
            }
        }
        return null;
    }
    /**
     * @return file 
     */
    public FFile getContainingFile() {
        return getFFile();
    }

    /**
     * @return text from start to end position
     */
    public String getText() {
        return getContainingFile().getText(getStartOffset(), getEndOffset());
    }

    
    private FFile getFFile() {
        FFile file = this.fileRef;
        return file;
    }

    protected void write(DataOutput output) throws IOException {
        startPosition.toStream(output);
        endPosition.toStream(output);     
    }
    
    protected OffsetableBase(DataInput input) throws IOException {
        startPosition = new Position(input);
        endPosition = new Position(input);
        fileRef = null;
    }
    
}
