/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2001 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.jndi;

import java.util.Hashtable;
import javax.naming.NameClassPair;


/** This class is an icon holder.
 *   
 * @author Tomas Zezula
 */
abstract class JndiIcons extends Object {

    /** The directory with Jndi icons*/
    public final static String ICON_BASE = "/org/netbeans/modules/jndi/resources/";
    /** The array of pairs (Class,IconName)*/
    private final static String[] defaultIcons = {"*","interface",
            JndiRootNode.NB_ROOT,"jndi",
            JndiProvidersNode.DRIVERS,"providerfolder",
            ProviderNode.DRIVER,"driver",
            ProviderNode.DISABLED_DRIVER,"disableddriver",
            JndiDisabledNode.DISABLED_CONTEXT_ICON,"disabled",
            WaitNode.WAIT_ICON,"wait",
            "javax.naming.Context","folder",
            "java.io.File","file"};
    /** Hashtable with Class name as key, Icon name as value*/
    private static Hashtable icontable;


    /** Returns icon name for string containig the name of Class
     *  @param name  name oc Class
     *  @return name of icon
     */
    public static String getIconName(String name) {
        String iconname;

        if (icontable == null) {
            lazyInitialize();
        }
        iconname = (String) icontable.get(name);
        if (iconname != null) {
            return iconname;
        } else {
            return (String) icontable.get("*");
        }
    }

    /** Returns the name of icon for NameClassPair
     *  @param name  NameClassPair for which the icon should be returned  
     *  @return String name of icon
     */
    public static String getIconName(NameClassPair name) {

        String iconname;

        if (icontable == null) {
            lazyInitialize();
        }

        if (name == null) {
            return (String) icontable.get("*");
        }

        iconname = (String) icontable.get(name.getClassName());
        if (iconname != null) {
            return iconname;
        } else {
            return (String) icontable.get("*");
        }
    }

    /**lazy_initialization
     */
    private static void lazyInitialize() {
        icontable = new Hashtable();
        for (int i=0; i < defaultIcons.length; i += 2) {
            icontable.put(defaultIcons[i], defaultIcons[i+1]);
        }
    }
}
