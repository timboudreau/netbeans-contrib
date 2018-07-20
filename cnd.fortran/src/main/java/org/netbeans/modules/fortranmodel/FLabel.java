

package org.netbeans.modules.fortranmodel;

/**
 * represents a label: not a statement that follows label, but a label itself
 * @author Andrey Gubichev
 */
public interface FLabel extends FStatement{
    String getLabel();

}
