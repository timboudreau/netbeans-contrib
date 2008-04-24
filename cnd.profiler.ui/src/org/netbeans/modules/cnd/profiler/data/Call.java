/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.netbeans.modules.cnd.profiler.data;

/**
 *
 * @author eu155513
 */
public class Call extends PropertyContainer<String,Object> implements FunctionContainer {
    private final Function function;

    public Call(Function function) {
        this.function = function;
    }

    public Function getFunction() {
        return function;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Call other = (Call) obj;
        if (this.function != other.function && (this.function == null || !this.function.equals(other.function))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        return function.hashCode();
    }
}
