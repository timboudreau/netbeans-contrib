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

/*
 * ProfileChildren.java
 *
 * Created on 13. øíjen 2000, 10:07
 */

package org.netbeans.modules.corba.ioranalyzer;

import java.io.*;
import java.util.ArrayList;
import org.openide.TopManager;
import org.openide.NotifyDescriptor;
import org.openide.nodes.*;
import org.openide.loaders.DataObject;
import org.openide.filesystems.*;
import org.openide.util.NbBundle;
/**
 *
 * @author  root
 * @version 
 */
public class ProfileChildren extends Children.Keys {
    
    IORDataObject dataObject;
    IORData iorData;

    /** Creates new ProfileChildren */
    public ProfileChildren(IORDataObject dataObject) {
        this.dataObject = dataObject;
        this.iorData = null;
    }
    
    
    public void addNotify () {
        try {
            lazyInit ();
            this.createKeys();
        }catch (org.omg.CORBA.BAD_PARAM bp) {
            this.setKeys (new java.lang.Object[0]);
            handleBadIOR();
        }
    }
    
    public void createKeys () {
        ArrayList profiles = iorData.getProfiles();
        ProfileKey[] keys = new ProfileKey[profiles.size()];
        for (int i=0; i< keys.length; i++) {
            Object profile = profiles.get(i);
            if (profile instanceof IORProfile) {
                keys[i] = new IOPProfileKey (i, (IORProfile)profile);
            }
            else if (profile instanceof IORTaggedProfile) {
                keys[i] = new TaggedProfileKey (i, (IORTaggedProfile)profile);
            }
        }
        this.setKeys (keys);
    }
    
    
    public Node[] createNodes (Object key) {
        if (key instanceof IOPProfileKey) {
            return new Node[] {new ProfileNode (((IOPProfileKey)key).index, ((IOPProfileKey)key).value)};
        }
        else if (key instanceof TaggedProfileKey) {
            return new Node[] { new TaggedNode (((TaggedProfileKey)key).index, ((TaggedProfileKey)key).value)};
        }
        else return new Node[0];
    }
    
    public void update () {
        this.iorData = null;
        this.addNotify();
    }
    
    public Integer getProfileCount () {
        try {
            lazyInit ();
            return new Integer(this.iorData.getProfiles().size());
        }catch (org.omg.CORBA.BAD_PARAM bp) {
            handleBadIOR();
            return null;
        }
    }
    
    public String getRepositoryId () {
        try {
            lazyInit();
            return this.iorData.getRepositoryId();
        }catch (org.omg.CORBA.BAD_PARAM bp) {
            handleBadIOR();
            return null;
        }
    }
    
    public Boolean isLittleEndian () {
        try {
            lazyInit();
            return new Boolean (this.iorData.isLittleEndian());
        }catch (org.omg.CORBA.BAD_PARAM bp) {
            handleBadIOR();
            return null;
        }
    }
    
    private void lazyInit () {
        if (this.iorData == null) {
            try {
                this.iorData = new IORData ( dataObject.getContent());
            } catch (IllegalStateException illegalState) {
		illegalState.printStackTrace (System.out);
                throw new org.omg.CORBA.BAD_PARAM ();
            }
            catch (IllegalArgumentException illegalArgument) {
		illegalArgument.printStackTrace (System.out);
                throw new org.omg.CORBA.BAD_PARAM ();
            }
        }
    }
    
    private void handleBadIOR () {
	java.awt.EventQueue.invokeLater ( new Runnable () {
		public void run () {
        		TopManager.getDefault ().notify ( new NotifyDescriptor.Message (NbBundle.getBundle(ProfileChildren.class).getString("TXT_InvalidIOR"), NotifyDescriptor.ERROR_MESSAGE));
		}
	});
    }

}
