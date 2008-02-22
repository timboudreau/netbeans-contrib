

package org.netbeans.modules.fort.model.lang;

/**
 * represents a label: not a statement that follows label, but a label itself
 * @author Andrey Gubichev
 */
public interface FLabel extends FStatement{
    String getLabel();

}
