
package org.netbeans.modules.fort.model;

/**
 * fortran syntax provider
 * @author Andrey Gubichev
 */
public interface FSyntaxProvider {    
    /**
     * @return used syntax model
     */
    FSyntax getSyntax(FModel model);
}
