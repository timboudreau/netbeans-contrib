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

import org.openide.TopManager;
import org.openide.NotifyDescriptor;
import org.openide.options.SystemOption;
import org.openide.nodes.Node;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileLock;
import org.openide.filesystems.Repository;
import org.openide.filesystems.FileSystem;
import org.openide.util.NbBundle;
import org.netbeans.modules.jndi.JndiRootNode;
import org.netbeans.modules.jndi.JndiAbstractNode;
import org.netbeans.modules.jndi.JndiProvidersNode;
import org.netbeans.modules.jndi.ProviderProperties;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.HashMap;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;


/**
 * This class is a cyctem option for Jndi module
 * @author  tzezula
 * @version 
 */
public class JndiSystemOption extends SystemOption implements PropertyChangeListener {

    public static final int DEFAULT_TIMEOUT = 4000;

    /** serialVersionUID */
    private static final long serialVersionUID = -4589004604197297781L;

    private static int timeOut;
    
    private transient ArrayList redProviders;
    
    private transient HashMap providers;

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
        firePropertyChange("timeOut",new Integer(oldTimeOut),new Integer(timeOut)); // No I18N
    }

    /** Returns the dusplay name of this setting
     *  @return String display name
     */
    public String displayName() {
        return JndiRootNode.getLocalizedString("Module_Name");  // No I18N
    }

    /** Reads Jndi module settings
     *  @input ObjectInput in
     */
    public void readExternal (java.io.ObjectInput in){
        try {
            timeOut = ((Integer)in.readObject()).intValue();
            redProviders = (ArrayList) in.readObject();
            if (redProviders == null)
                redProviders = new ArrayList ();
        }catch (java.io.IOException ioe){timeOut=DEFAULT_TIMEOUT;}
        catch (java.lang.ClassNotFoundException cnfe) {timeOut=DEFAULT_TIMEOUT;}
    }

    /** Stors the settings for Jndi module
     *  @param ObjectOutput out
     */
    public void writeExternal (java.io.ObjectOutput out){
        try{
            out.writeObject( new Integer(timeOut));
            out.writeObject(redProviders);
        }catch (java.io.IOException ioe) {}
    }


    /** Per project option
     *  @return boolean false
     */
    public final boolean isGlobal(){
        return false;
    }
    
    /** Returns initial providers
    */
    public ArrayList getInitialContexts () {
	return this.redProviders;
    }
    
    public void setInitialContexts (ArrayList list) {
        Object oldValue = this.redProviders;
        this.redProviders = list;
        firePropertyChange ("initialContexts",null,null);    // No I18N
    }
    
    
    public void destroyProvider (String key) throws java.io.IOException {
        FileLock lock = null;
        try{
            FileSystem fs = TopManager.getDefault().getRepository().getDefaultFileSystem();
            FileObject fo = fs.getRoot().getFileObject("JNDI");
            String filename = key.replace('.','_');
            fo = fo.getFileObject(filename,"impl");
            if (fo != null){
                lock = fo.lock();
                fo.delete(lock);
            }
        }finally{
            if (lock != null) lock.releaseLock();
        }
        this.providers.remove (key);
    }
    
    
    public HashMap getProviders (boolean reload) {
        if (this.providers == null || reload)
            this.readProperties ();
        return this.providers;
    }
    
    /** Reads the propeties of providers from files in JNDI directory*/
    private void readProperties(){
        if (this.providers == null)
            this.providers = new HashMap ();
        else
            this.providers.clear();
        Repository repo = TopManager.getDefault().getRepository();
        FileSystem fs = repo.getDefaultFileSystem();
        FileObject fo = fs.getRoot().getFileObject("JNDI");
        if (fo == null) {
            TopManager.getDefault().getErrorManager().log(NbBundle.getBundle(JndiProvidersNode.class).getString ("ERR_CanNotOpenJNDIFolder"));
            return;
	}
        java.util.Enumeration files = fo.getData(false);
        while (files.hasMoreElements()){
            fo = (FileObject) files.nextElement();
            try{
                if (fo.getExt().equals("impl")){
                    java.io.InputStream in = fo.getInputStream();
                    ProviderProperties p = new ProviderProperties();
                    p.load(in);
                    p.addPropertyChangeListener(this);
                    this.providers.put(p.getFactory(),p);
                    in.close();
                }
            }catch(java.io.IOException ioe){
		TopManager.getDefault().getErrorManager().log(NbBundle.getBundle(JndiProvidersNode.class).getString ("ERR_ErrorReadProvider"+fo.getName()));
            }
        }
    }
    
    public void propertyChange(java.beans.PropertyChangeEvent event) {
        ProviderProperties properties = (ProviderProperties) event.getSource ();
        String filename = properties.getFactory().replace('.','_');
        FileObject fo = TopManager.getDefault().getRepository().getDefaultFileSystem().getRoot().getFileObject("JNDI");
        if (fo == null){
            notifyFileError();
            return;
        }
        fo = fo.getFileObject(filename,"impl");
        if (fo == null){
            notifyFileError();
            return;
        }
        FileLock lock = null;
        try{
            lock = fo.lock ();
            java.io.OutputStream out = fo.getOutputStream(lock);
            properties.store (out,JndiRootNode.getLocalizedString("FILE_COMMENT"));
            out.flush();
            out.close();
        }catch (java.io.IOException ioe){
            notifyFileError();
            return;
        }
        finally{
            if (lock != null ) lock.releaseLock();
        }
    }
    
    
    /** Used for notification of error that raises during file operation
     */
    private void notifyFileError(){
        TopManager.getDefault().notify ( new NotifyDescriptor.Message (JndiRootNode.getLocalizedString("EXC_Template_IOError"), NotifyDescriptor.Message.ERROR_MESSAGE));
    }
    
}
