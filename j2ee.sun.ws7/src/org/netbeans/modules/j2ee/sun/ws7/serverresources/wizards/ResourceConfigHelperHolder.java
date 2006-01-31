/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2005 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

/*
 * ResourceConfigHelperHolder.java
 */

package org.netbeans.modules.j2ee.sun.ws7.serverresources.wizards;

import java.util.Vector;

import org.netbeans.modules.j2ee.sun.ws7.serverresources.wizards.WS70WizardConstants;


/**
 *
 * Code reused from Appserver common API module 
 */
public class ResourceConfigHelperHolder implements WS70WizardConstants {
    private ResourceConfigHelper mainHelper = null;
    private Vector associated = new Vector();

       
    public ResourceConfigHelperHolder() {
        mainHelper = new ResourceConfigHelper(1);
    }
    
    public ResourceConfigHelperHolder (ResourceConfigHelper helper) {
        mainHelper = helper;
    }
    
    public ResourceConfigHelper addAssociatedHelper() {
        ResourceConfigHelper helper = new ResourceConfigHelper(1);
        associated.add(helper);
        return helper;
    }
     
    public ResourceConfigHelper getMainHelper() {
        return mainHelper;
    }
    
    public ResourceConfigHelper getJDBCHelper() {
        mainHelper.getData().setResourceName(__JdbcResource);
        return mainHelper;
    }
    public ResourceConfigHelper getMailHelper() {
        mainHelper.getData().setResourceName(__MailResource);
        return mainHelper;
    }
    public ResourceConfigHelper getCustomResourceHelper() {
        mainHelper.getData().setResourceName(__CustomResource);
        return mainHelper;
    }
    public ResourceConfigHelper getExternalJndiResourceHelper() {
        mainHelper.getData().setResourceName(__ExternalJndiResource);
        return mainHelper;
    }        
    
    public Vector getAssociatedHelpers() {
        return associated;
    }
    
    public void removeAssociatedHelpers() {
        if (associated.size() > 0)
            associated = new Vector();
    }
    
    public void removeLastAssociatedHelper() {
        if (associated.size() > 0)
            associated.remove(associated.size() - 1);
    }    
 
}
