/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common
 * Development and Distribution License("CDDL") (collectively, the
 * "License"). You may not use this file except in compliance with the
 * License. You can obtain a copy of the License at
 * http://www.netbeans.org/cddl-gplv2.html
 * or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
 * specific language governing permissions and limitations under the
 * License.  When distributing the software, include this License Header
 * Notice in each file and include the License file at
 * nbbuild/licenses/CDDL-GPL-2-CP.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * Contributor(s):
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 *
 * If you wish your version of this file to be governed by only the CDDL
 * or only the GPL Version 2, indicate your decision by adding
 * "[Contributor] elects to include this software in this distribution
 * under the [CDDL or GPL Version 2] license." If you do not indicate a
 * single choice of license, a recipient has the option to distribute
 * your version of this file under either the CDDL, the GPL Version 2 or
 * to extend the choice of license to its licensees as provided above.
 * However, if you add GPL Version 2 code and therefore, elected the GPL
 * Version 2 license, then the option applies only if the new code is
 * made subject to such option by the copyright holder.
 */

package org.netbeans.modules.jndi.settings;

import org.openide.DialogDisplayer;
import org.openide.ErrorManager;
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
        redProviders = new ArrayList();
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
            FileSystem fs = Repository.getDefault().getDefaultFileSystem();
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
        Repository repo = Repository.getDefault();
        FileSystem fs = repo.getDefaultFileSystem();
        FileObject fo = fs.getRoot().getFileObject("JNDI");
        if (fo == null) {
            ErrorManager.getDefault().log(NbBundle.getBundle(JndiProvidersNode.class).getString ("ERR_CanNotOpenJNDIFolder"));
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
		ErrorManager.getDefault().log(NbBundle.getBundle(JndiProvidersNode.class).getString ("ERR_ErrorReadProvider"+fo.getName()));
            }
        }
    }
    
    public void propertyChange(java.beans.PropertyChangeEvent event) {
        ProviderProperties properties = (ProviderProperties) event.getSource ();
        String filename = properties.getFactory().replace('.','_');
        FileObject fo = Repository.getDefault().getDefaultFileSystem().getRoot().getFileObject("JNDI");
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
        DialogDisplayer.getDefault().notify(new NotifyDescriptor.Message (JndiRootNode.getLocalizedString("EXC_Template_IOError"), NotifyDescriptor.Message.ERROR_MESSAGE));
    }
    
}
