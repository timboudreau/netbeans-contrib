

package org.netbeans.modules.fort.model.lang;

import java.util.Collection;

/**
 * Represents a program unit (i.e. main program, module, block data)
 * @author Andrey Gubichev
 */
public interface FProgramUnit extends FQualifiedNamedElement, FScope{
    /**
     * Gets all definitions for this module
     */ 
   Collection<FOffsetableDeclaration> getDeclarations();

   /** the project where the module is defined */
    FProject getProject();
}
