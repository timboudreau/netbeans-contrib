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

package org.netbeans.modules.jndi.settings;

import org.openide.options.SystemOption;
import org.openide.nodes.Node;
import org.netbeans.modules.jndi.JndiRootNode;
import org.netbeans.modules.jndi.JndiAbstractNode;
import org.netbeans.modules.jndi.JndiProvidersNode;
import java.util.ArrayList;
import java.util.Hashtable;


/**
 * This class is a cyctem option for Jndi module
 * @author  tzezula
 * @version 
 */
public class JndiSystemOption extends SystemOption {

    public static final int DEFAULT_TIMEOUT = 4000;

    /** serialVersionUID */
    private static final long serialVersionUID = -4589004604197297781L;

    private static int timeOut;

    /** Creates new JndiSystemOption */
    public JndiSystemOption() {
        this.timeOut=DEFAULT_TIMEOUT;
    }

    /** Returns the value of timeout for connect operation
     *  @return int time out
     */
    public int getTimeOut(){
        return this.timeOut;
    }

    /** Sets time out for connect operation
     *  @param int timeOut
     */
    public void setTimeOut (int timeOut) {
        int oldTimeOut = this.timeOut;
        this.timeOut = timeOut;
        firePropertyChange("timeOut",new Integer(oldTimeOut),new Integer(timeOut));
    }

    /** Returns the dusplay name of this setting
     *  @return String display name
     */
    public String displayName() {
        return JndiRootNode.getLocalizedString("Module_Name");
    }

    /** Reads Jndi module settings
     *  @input ObjectInput in
     */
    public void readExternal (java.io.ObjectInput in){
        try {
            timeOut = ((Integer)in.readObject()).intValue();
            ArrayList redProviders = (ArrayList) in.readObject();
            JndiRootNode node = JndiRootNode.getDefault();
            if ( node != null ) node.initStartContexts(redProviders);
        }catch (java.io.IOException ioe){timeOut=DEFAULT_TIMEOUT;}
        catch (java.lang.ClassNotFoundException cnfe) {timeOut=DEFAULT_TIMEOUT;}
    }

    /** Stors the settings for Jndi module
     *  @param ObjectOutput out
     */
    public void writeExternal (java.io.ObjectOutput out){
        JndiRootNode node = JndiRootNode.getDefault();
        ArrayList array = null;
        if (node != null){
            Node[] nodes = node.getChildren().getNodes();
            array = new ArrayList();
            for (int i = 0; i < nodes.length; i++) {
                if (!nodes[i].getName().equals(JndiRootNode.getLocalizedString(JndiProvidersNode.DRIVERS))) {
                    try {
                        Hashtable contextProperties = ((JndiAbstractNode) nodes[i]).getInitialDirContextProperties();
                        array.add(contextProperties);
                    }catch (javax.naming.NamingException ne){}
                }
            }
        }
        try{
            out.writeObject( new Integer(timeOut));
            if (array != null) out.writeObject( array);
        }catch (java.io.IOException ioe) {}
    }


    /** Per project option
     *  @return boolean false
     */
    public final boolean isGlobal(){
        return false;
    }
}