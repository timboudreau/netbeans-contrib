
package org.netbeans.modules.fort.model.lang.impl;

import org.netbeans.modules.cnd.apt.support.APTBaseToken;

/**
 * The class to store information about token
 * @author Andrey Gubichev
 */
public class FToken extends APTBaseToken {
    /*
     * very simple token NIL
     */
    public static final FToken NIL = new FToken();
    
    /*
     * creates a new instance of FToken
     */
    public FToken(){
        super();
    }

}
