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
import java.io.IOException;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.naming.Context;
import org.openide.TopManager;
import org.openide.NotifyDescriptor;
import org.openide.DialogDescriptor;
import org.openide.util.actions.SystemAction;
import org.openide.actions.NewAction;
import org.openide.util.datatransfer.NewType;
import org.openide.nodes.Node;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Node.Cookie;
import org.openide.nodes.Children;
import org.openide.nodes.DefaultHandle;
import org.openide.nodes.Sheet;
import org.openide.filesystems.Repository;
import org.openide.filesystems.FileSystem;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileLock;
import org.netbeans.modules.jndi.utils.Refreshable;
import org.openide.util.NbBundle;

/** This class represents the branch with providers (factories)
 *
 *  @author Tomas Zezula
 */
public class JndiProvidersNode extends AbstractNode implements PropertyChangeListener,Cookie,Refreshable {

    /** Name for JndiIcons*/
    public static final String DRIVERS = "TITLE_DRIVERS";

    /** System actions*/
    SystemAction[] jndiactions = null;

    /** Hashtable of properties for providers*/
    Hashtable providers = new Hashtable();


    /** Creates new JndiProviderNode, installs providers if they are not instlled
     *  and reads them to hashtable
     */
    public JndiProvidersNode() {
        super ( new Children.Array ());
        this.getCookieSet().add(this);
        setName (JndiRootNode.getLocalizedString(JndiProvidersNode.DRIVERS));
        setIconBase (JndiIcons.ICON_BASE + JndiIcons.getIconName(JndiProvidersNode.DRIVERS));
        this.readProperties ();
        this.installProperties();
    }

    /** Returns name of object
     *  @return Object name of node
     */
    public Object getValue(){
        return this.getName();
    }


    /** Sets the name of node
     *  @param Object name of node
     */
    public void setValue (Object name) {
        if (name instanceof String) {
            this.setName ((String) name);
        }
    }

    /** Returns how the node feels about destroying
     *  @return boolean can / can not destroy
     */
    public boolean canDestroy () {
        return false;
    }

    /** Returns true if the node can be copy
     *  @return boolean can / can not copy
     */
    public boolean canCopy () {
        return false;
    }

    /** Returns true if the node can be cut
     *  @return boolean can / can not cut
     */
    public boolean canCut () {
        return false;
    }

    /** Returns true if the node can be removed
     *  @return boolean can / can notr rename
     */
    public boolean canRename () {
        return false;
    }

    /** Returns default system action of this node
     *  @return SystemAction 
     */
    public SystemAction getDefaultAction () {
        return null;
    }

    /** Returns system actions of this node
     *  @return SystemAction[] actions 
     */
    public SystemAction[] getActions () {
        if (this.jndiactions == null) {
            this.jndiactions = this.createActions ();
        }
        return this.jndiactions;
    }

    /** Initialization of the SystemActions
     *  @return SystemAction[] actions
     */
    public SystemAction[] createActions () {
        return new SystemAction[] {
                   SystemAction.get(NewAction.class),
                   null,
                   SystemAction.get(RefreshAction.class)
               };
    }

    /** Returns New Type of this node
     *  @return NewType[] types
     */
    public NewType[] getNewTypes () {
        return new NewType[] {new ProviderDataType(this)};
    }

    /** Returns Handle of this Node
     *  @return Handle handle
     */
    public Handle gethandle () {
        return DefaultHandle.createHandle(this);
    }


    /** Callback called by ProviderNode when is realised
     *  to remove it also from Hashtable and disk
     *  @param String key in Hashtable
     */
    public void destroyProvider (String key) throws IOException {
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
        this.providers.remove(key);
    }


    /** Callback for PropertyChangeSupport
     *  @param PropertyCahngeEvent event
     */
    public void propertyChange (final java.beans.PropertyChangeEvent event) {
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
        }catch (IOException ioe){
            notifyFileError();
            return;
        }
        finally{
            if (lock != null ) lock.releaseLock();
        }

    }

    /** Reads the propeties of providers from files in JNDI directory*/
    private void readProperties(){
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

    /** Creates ProviderNode as a child of this node */
    private void installProperties () {
        int size = this.providers.size ();
        if (size > 0) {
            java.util.Enumeration enum = this.providers.keys ();
            Node nodes[] = new Node[size];
            for (int i=0; i<size; i++) {
                String key = (String) enum.nextElement();
                nodes[i] = new ProviderNode(key);
            }
            this.getChildren().add(nodes);
        }
    }

    /** Used for notification of error that raises during file operation
     */
    private void notifyFileError(){
        TopManager.getDefault().notify ( new NotifyDescriptor.Message (JndiRootNode.getLocalizedString("EXC_Template_IOError"), NotifyDescriptor.Message.ERROR_MESSAGE));
    }

    /** Refresh the providers tree
     */
    public void refresh() {
        Repository repo = TopManager.getDefault().getRepository();
        FileSystem fs = repo.getDefaultFileSystem();
        FileObject fo = fs.getRoot().getFileObject("JNDI");
	if (fo == null)
	    return;
        java.util.Enumeration files = fo.getData(false);
        while (files.hasMoreElements()){
            fo = (FileObject) files.nextElement();
            try{
                if (fo.getExt().equals("impl") && !this.providers.containsKey(fo.getName().replace('_','.'))){
                    java.io.InputStream in = fo.getInputStream();
                    ProviderProperties p = new ProviderProperties();
                    p.load(in);
                    p.addPropertyChangeListener(this);
                    this.providers.put(p.getFactory(),p);
                    in.close();
                    this.getChildren().add(new Node[]{new ProviderNode(p.getFactory())});
                }
            }catch(java.io.IOException ioe){}
        }
    }
}