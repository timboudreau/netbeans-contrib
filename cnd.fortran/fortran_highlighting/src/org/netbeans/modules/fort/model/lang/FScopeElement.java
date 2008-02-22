

package org.netbeans.modules.fort.model.lang;

/**
 * Element of scope:global (a main program section, module, 
 * or external subprogram), local(internal subprogram)
 *
 * @author Andrey Gubichev
 */
public interface FScopeElement extends FObject{
       FScope getScope();
}
