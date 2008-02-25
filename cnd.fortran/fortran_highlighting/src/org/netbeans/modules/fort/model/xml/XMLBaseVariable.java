
package org.netbeans.modules.fort.model.xml;

import org.netbeans.modules.cnd.api.model.CsmFile;
import org.netbeans.modules.cnd.api.model.CsmOffsetable.Position;
import org.netbeans.modules.fort.model.lang.FDeclaration.Kind;
import org.netbeans.modules.fort.model.lang.FExpression;
import org.netbeans.modules.fort.model.lang.FScope;
import org.netbeans.modules.fort.model.lang.FType;
import org.netbeans.modules.fort.model.lang.FVariable;

/**
 *
 * @author Andrey Gubichev
 */
public class XMLBaseVariable implements FVariable {
    private FType varType;
    private FExpression initValue;
    private String declText;
    
    public XMLBaseVariable(FType t, FExpression exp, String txt){
        varType = t;
        initValue = exp;
        declText = txt;
    }
    public FType getType() {
        return varType;
    }

    public FExpression getInitialValue() {
        return initValue;
    }

    public String getDeclarationText() {
        return declText;
    }

    public Kind getKind() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public String getName() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public String getQualifiedName() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public FScope getScope() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public CsmFile getContainingFile() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public int getStartOffset() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public int getEndOffset() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Position getStartPosition() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Position getEndPosition() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public String getText() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
