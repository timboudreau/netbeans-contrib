package org.netbeans.modules.scala.editing.nodes;

import javax.lang.model.element.Name;

public class BasicName implements Name {

    private CharSequence name;

    public BasicName(CharSequence name) {
        super();
        assert name != null : "AstName should not be null";
        this.name = name;
    }

    public char charAt(int index) {
        return name.charAt(index);
    }

    public boolean contentEquals(CharSequence arg0) {
        return name.toString().contentEquals(arg0.toString());
    }

    public int length() {
        return name.length();
    }

    public CharSequence subSequence(int start, int end) {
        return name.subSequence(start, end);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Name) {
            return contentEquals((Name) obj);
        } else if (obj instanceof CharSequence) {
            return contentEquals((CharSequence) obj);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return name.toString().hashCode();
    }

    @Override
    public String toString() {
        return name.toString();
    }
}
