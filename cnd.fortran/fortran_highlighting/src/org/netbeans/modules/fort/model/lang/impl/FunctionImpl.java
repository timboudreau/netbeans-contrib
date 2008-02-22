
package org.netbeans.modules.fort.model.lang.impl;

import antlr.collections.AST;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import org.netbeans.modules.fort.model.lang.FCompoundStatement;
import org.netbeans.modules.fort.model.lang.FDeclaration;
import org.netbeans.modules.fort.model.lang.FFile;
import org.netbeans.modules.fort.model.lang.FProcedure;
import org.netbeans.modules.fort.model.lang.FProject;
import org.netbeans.modules.fort.model.lang.FScope;
import org.netbeans.modules.fort.model.lang.FType;
import org.netbeans.modules.fort.model.lang.FVariable;

/**
 * The class to implement FProcedure interface
 * @author Andrey Gubichev
 */
public class FunctionImpl extends OffsetableBase implements FProcedure{
 
    private String name;
    private final FType returnType;
    private final List<FVariable>  parameters;
    private final boolean isVoidParameterList;
    private String signature;
    
    private /*final*/ FScope scopeRef;  
    /**
     * creates a new instance of FunctionImpl
     */
    public FunctionImpl(AST ast, FFile file, FScope scope) {
        this(ast, file, scope, true);
    }
    
    private static final boolean CHECK_SCOPE = false;
    protected FunctionImpl(AST ast, FFile file, FScope scope, boolean register) {
        super(ast, file);
        this.scopeRef = scope;
      
        name = initName(ast);
        returnType = initReturnType(ast);
        
        List<FVariable> params = initParameters(ast);
            this.parameters = params;

        if (params == null || params.size() == 0) {
            isVoidParameterList = isVoidParameter(ast);
        } else {
            isVoidParameterList = false;
        }
        
        if( name == null ) {
            name = "<null>"; 
        }
        if (register) {
            registerInProject();
        }
    }
    
    protected String initName(AST node) {
        return findFunctionName(node);
    }
    
    protected boolean isVoidParameterList(){
        return isVoidParameterList;
    }

    private static String extractName(AST token){
        int type = token.getType();
            AST last = AstUtil.getLastChild(token);
            if( last != null) {
                return last.getText();
            }     
        return "";
    }
    
    private static String findFunctionName(AST ast) {
        AST token = AstUtil.findMethodName(ast);
        if (token != null){
            return extractName(token);
        }
        return "";
    }
    
    protected void registerInProject() {
        FProject project = (FProject) getContainingFile().getProject();
        if( project instanceof ProjectBase ) {
            ((ProjectBase) project).registerDeclaration(this);
        }
    }
    
    private void unregisterInProject() {
        FProject project = (FProject) getContainingFile().getProject();
        if( project instanceof ProjectBase ) {
            ((ProjectBase) project).unregisterDeclaration(this);
        }
    }
   /** Gets this element name
     * @return name
     */
    public String getName() {
        return name;
    }
   /** Sets this element name
     * @param name
     */    
    protected final void setName(String name) {
        this.name = name;
    }
    
   /**
    * the kind is FUNCTION
    */
    public FDeclaration.Kind getKind() {
        return FDeclaration.FUNCTION;
    }
    
    /** Gets this function's declaration text
     * @return declaration text
     */
    public String getDeclarationText() {
        return "";
    }
    /**
     * @return return type
     */
    public FType getReturnType() {
        return returnType;
    }
    
    private static AST getTypeToken(AST node) {
         return node.getFirstChild();
    }
    
    private boolean isVoidParameter(AST node) {
       AST findAST = null;
       for( AST token = node.getFirstChild(); token != null; token = token.getNextSibling() ) {
            if( token.getType() == 0 ) {
                findAST = token;
            }
        }
        return isVoidParameterAST(findAST);
    }
    private List<FVariable> initParameters(AST node) {
       AST findAST = null;
       for( AST token = node.getFirstChild(); token != null; token = token.getNextSibling() ) {
            if( token.getType() == 0 ) {
                findAST = token;
            }
        }
       //TODO:implement;
        return null; //renderParameters(ast, getContainingFile());
    }
 
    private static boolean isVoidParameterAST(AST ast) {
        if( ast != null ) {
            AST token = ast.getFirstChild();
                AST firstChild = token.getFirstChild();
                if( firstChild != null ) {
                    if(firstChild.getNextSibling() == null ) {
                        AST grandChild = firstChild.getFirstChild();
                        if( grandChild != null ) {
                            return true;
                        }
                    }
                }
        }
        return false;
    }
    
    /**
     * @return list of parameters
     */
    public List<FVariable>  getParameters() {
        return parameters;
    }

    /**
     * @return function's scope
     */    
   public FScope getScope() {
        FScope scope = this.scopeRef;
        return scope;
    }

    private FType initReturnType(AST node) {
        FType ret = null;
        AST token = getTypeToken(node);
        if( token != null ) {
            //TODO:implement!
           // ret = AstRenderer.renderType(token, getContainingFile());
        }

        return ret;
    }
    /**
     * @return function's signature
     */        
    public String getSignature() {
        if( signature == null ) {
            signature = createSignature();
        }
        return signature;
    }

    
    private String createSignature() {
      StringBuilder sb = new StringBuilder(getName());
        sb.append('(');
        for( Iterator iter = getParameters().iterator(); iter.hasNext(); ) {
            FVariable param = (FVariable) iter.next();
            FType type = param.getType();
            if( type != null )  {
                sb.append(type.getCanonicalText());
                if( iter.hasNext() ) {
                    sb.append(',');
                }
            } 
        }
        sb.append(')');

        return sb.toString();
    }

    /**
     * write to output
     */
    public void write(DataOutput output) throws IOException{
        super.write(output);
        assert this.name != null;
        output.writeUTF(this.name);
        output.writeChars(this.returnType.getKind().getValue());
        output.writeBoolean(isVoidParameterList);
    }

    /**
     * @return function's body
     */
    public FCompoundStatement getBody() {
        throw new UnsupportedOperationException("Not supported yet.");
    }


    /**
     * @return function's full name
     */
    public String getQualifiedName() {
        //TODO:implement
        throw new UnsupportedOperationException("Not supported yet.");
    }


}
