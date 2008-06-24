package org.netbeans.modules.scala.editing.nodes;

import java.util.Collections;
import java.util.List;
import javax.lang.model.element.Element;
import javax.lang.model.element.Name;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.type.TypeVisitor;

/**
 * DeclaredType implementation without AstNode infomation
 * 
 * Used for remote type, which has qualifiedName field only, without AstNode information
 *
 * @author Caoyuan Deng
 * 
 */
public class BasicType implements DeclaredType {

    private Name simpleName;
    private TypeKind kind;
    private List<? extends TypeMirror> typeArguments;
    private TypeElement element;

    public BasicType() {
        this.kind = TypeKind.DECLARED;
    }

    public BasicType(TypeElement element) {
        this.kind = TypeKind.DECLARED;
        this.element = element;
    }

    public BasicType(TypeElement element, TypeKind kind) {
        this.kind = kind;
        this.element = element;
    }

    public void setSimpleName(CharSequence sName) {
        simpleName = new BasicName(sName);
    }

    public Name getSimpleName() {
        if (element != null) {
            simpleName = element.getSimpleName();
        }
        if (simpleName == null) {
            System.out.println("Hei!");
        }

        return simpleName;
    }

    public boolean isResolved() {
        return element != null;
    }

    public <R, P> R accept(TypeVisitor<R, P> arg0, P arg1) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public TypeKind getKind() {
        return kind;
    }

    public void setElement(TypeElement element) {
        this.element = element;
    }

    public Element asElement() {
        return element;
    }

    public TypeMirror getEnclosingType() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void setTypeArguments(List<? extends TypeMirror> typeArguments) {
        this.typeArguments = typeArguments;
    }

    public List<? extends TypeMirror> getTypeArguments() {
        return typeArguments == null ? Collections.<TypeMirror>emptyList() : typeArguments;
    }

    @Override
    public java.lang.String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getSimpleName());
        //sb.append(getTypeArgsName());

        return sb.toString();
    }
}
