

package org.netbeans.modules.fort.model.lang;

import java.util.Collection;

/**
 *
 * @author Andrey Gubichev
 */
public interface FProject {
    /**
     * Finds module (blockdata, main program) by its qualified name
     */
    FProgramUnit findUnit( String qualifiedName );
    /**
     * Finds compound classifier (derived type)& declaration by its qualified name
     */
    FDeclaration findClassifier(String qualifiedName);

    /**
     * Finds file by its absolute path
     */
    FFile findFile(String absolutePath);

    /**
     * Gets the collection of source project files.
     */
    Collection<FFile> getUnits();
}
