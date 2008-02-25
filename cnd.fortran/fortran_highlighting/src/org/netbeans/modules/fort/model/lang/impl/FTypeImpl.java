
package org.netbeans.modules.fort.model.lang.impl;

import antlr.collections.AST;
import java.io.DataInput;
import java.io.IOException;
import org.netbeans.modules.fort.model.lang.FDeclaration;
import org.netbeans.modules.fort.model.lang.FFile;
import org.netbeans.modules.fort.model.lang.FType;
import org.netbeans.modules.fort.model.lang.FTypeParamValue;

/**
 * Class to implement FType
 * @author Andrey Gubichev
 */
public class FTypeImpl extends OffsetableBase implements FType{

    private  String classifierText;
    private  FDeclaration classifier;
    private  boolean isPointer;
    private  boolean isDimension;
    FTypeImpl(FDeclaration decl,  AST ast, FFile file, boolean pointer, boolean dim) {
        super(ast, file);
        this.setClassifier(decl);
        isPointer = pointer;
        isDimension = dim;
        if (decl == null) {
            this.setClassifier(initClassifier(ast));
            this.classifierText = initClassifierText(ast);
        } else {
            String typeName = decl.getName();
            if (typeName == null || typeName.length()==0){
                this.classifierText = initClassifierText(ast);
            } else {
                this.classifierText = typeName;
            }
        }
    }
    FTypeImpl(AST classifier, FFile file, boolean dimension, boolean pointer) {
        super(classifier, file);
        this.isDimension = dimension;
        this.isPointer = pointer;
        this.classifier =initClassifier(classifier);
        this.classifierText = initClassifierText(classifier);
    }        
        
        
    private FDeclaration initClassifier(AST node) {
        //TODO:implement!
        return null;
    }
    
    private String initClassifierText(AST node) {
        if( node == null ) {
            return classifier == null ? "" : classifier.getName();
        }
        else {
            StringBuilder sb = new StringBuilder();
            addText(sb, node);
            return sb.toString();        }
    }
    protected FortAST getEndAst(AST node) {
        AST ast = node;
        if( ast == null ) {
            return null;
        }
        ast = getLastNode(ast);
        if( ast instanceof FortAST ) {
            return (FortAST) ast;
        }
        return super.getEndAst(node);
    }

    
    private static void addText(StringBuilder sb, AST ast) {
        if( ! (ast instanceof FortAST) ) {
            if( sb.length() > 0 ) {
                sb.append(' ');
            }
            sb.append(ast.getText());
        }
        for( AST token = ast.getFirstChild(); token != null; token = token.getNextSibling() ) {
            addText(sb,  token);
        }
    }
   
    private AST getLastNode(AST first) {
        AST last = first;
        for( last = first; last != null; last = last.getNextSibling() )
            ;
        return last;
    }

    private void setClassifier(FDeclaration classifier) {
        this.classifier = classifier;  
    }

    /**
     * 
     * @return if type is a pointer
     */
    public boolean isPointer() {
       return isPointer;
    }

    /**
     * 
     * @return true if it is a dimension
     */
    public boolean isDimension() {
       return isDimension;
    }

    /**
     * 
     * @return classifier's text
     */
    public String getCanonicalText() {
        return classifierText;
    }


    /**
     * creates a new instance of FTypeImpl
     */
    public FTypeImpl(DataInput input) throws IOException {
        super(input);
        this.isPointer = input.readBoolean();
        this.isDimension = input.readBoolean();
        this.classifierText = input.readUTF();
        assert this.classifierText != null;
    }

    /**
     * 
     * @return TYPE kind
     */
    public FDeclaration.Kind getKind() {
        return FDeclaration.TYPE;
    }

}
