

package org.netbeans.modules.fortranmodel;

import java.util.Collection;

/**
 * represents USE statement
 * @author Andrey Gubichev
 */
public interface FUseStatement extends FOffsetableDeclaration{
     FModule getReferencedModule();
     
     /**Collection of entities that are specified with ONLY qualifier*/
     Collection<FDeclaration> OnlyList();
}
