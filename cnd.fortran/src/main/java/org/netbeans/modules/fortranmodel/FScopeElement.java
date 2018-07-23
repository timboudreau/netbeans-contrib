

package org.netbeans.modules.fortranmodel;

/**
 * Element of scope:global (a main program section, module, 
 * or external subprogram), local(internal subprogram)
 *
 * @author Andrey Gubichev
 */
public interface FScopeElement extends FObject{
       FScope getScope();
}
