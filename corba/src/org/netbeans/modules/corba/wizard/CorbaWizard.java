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

package org.netbeans.modules.corba.wizard;

import java.io.File;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.FileReader;
import java.io.OutputStreamWriter;
import java.io.InputStreamReader;
import java.awt.*;
import java.awt.event.*;
import java.beans.*;
import java.util.StringTokenizer;
import java.util.ArrayList;
import javax.swing.event.*;
import org.openide.loaders.DataObject;
import org.openide.nodes.Node;
import org.openide.filesystems.FileLock;
import org.openide.cookies.OpenCookie;
import org.openide.cookies.EditorCookie;
import org.openide.*;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataFolder;
import org.openide.util.RequestProcessor;
import org.netbeans.modules.corba.wizard.panels.*;
import org.netbeans.modules.projects.NewProjectAction;
import java.io.IOException;
import java.lang.reflect.Method;
import org.netbeans.modules.corba.IDLDataObject;
import org.netbeans.modules.corba.CORBASupport;
import org.netbeans.modules.corba.IDLNodeCookie;
import org.netbeans.modules.corba.settings.CORBASupportSettings;

/** 
 *
 * @author  Tomas Zezula
 * @version 1.0
 */
public class CorbaWizard extends Object implements PropertyChangeListener, WizardDescriptor.Iterator {
  
    private static final int panelsCount = 5;
    // private static final boolean DEBUG = true;
    private static final boolean DEBUG = false;
  
    private int index;
    private CorbaWizardData data;
    private Dialog dialog;
    private boolean locked;
    private PackagePanel packagePanel = new PackagePanel ();
    private StartPanel startPanel = new StartPanel ();
    private ORBPanel orbPanel = new ORBPanel ();
    private IDLPanel idlPanel = new IDLPanel ();
    private IDLWizardPanel idlWizardPanel = new IDLWizardPanel ();
    private FinishPanel finishPanel = new FinishPanel ();
    private ArrayList listeners = new ArrayList ();
  
    private class WizardGenerator extends Thread {
      
        public WizardGenerator () {
        }

        public void run () {

            BufferedReader in = null;
            PrintWriter out = null;
            FileLock lock = null;
            String line = null;

            CorbaWizard.this.dialog.setVisible (false);
            CorbaWizard.this.dialog.dispose();
            try {
                DataFolder pkg = CorbaWizard.this.data.getDestinationPackage ();
                Object idlSource = CorbaWizard.this.data.getSource ();
                int mode = CorbaWizard.this.data.getGenerate ();

                // Create file to hold data
                String name = CorbaWizard.this.data.getName();
          
                FileObject destination = pkg.getPrimaryFile().createData (name,"idl");  // No I18N
                lock = destination.lock();
                out = new PrintWriter ( new OutputStreamWriter ( destination.getOutputStream (lock)));

                //Create IDL file
                if ((mode & CorbaWizardData.IDL) == CorbaWizardData.IDL) {
                    // From wizard
                    TopManager.getDefault().setStatusText(CorbaWizardAction.getLocalizedString("MSG_CreatingIDL"));
                    if (idlSource instanceof org.netbeans.modules.corba.wizard.nodes.IdlFileNode) {
                        try {
                            ((org.netbeans.modules.corba.wizard.nodes.IdlFileNode)idlSource).generate (out);
                        }finally {
                            if (out != null)
                                out.close();
                            if (lock != null) 
                                lock.releaseLock ();      
                        }
                    }
                }
                else {
                    // Import
                    TopManager.getDefault().setStatusText(CorbaWizardAction.getLocalizedString("MSG_ImportingIDL"));
                    if (idlSource instanceof File) {
                        // From File
                        try {
                            in = new BufferedReader ( new FileReader ( (File) idlSource));
                            while ((line = in.readLine()) != null) {
                                out.println (line);
                            }
                        }finally {
                            if (in != null)
                                try { in.close();} catch (IOException ioe2){}
                            if (out != null)
                                out.close();
                            if (lock != null) 
                                lock.releaseLock ();      
                        }
                    }
                    else if (idlSource instanceof Node[]) {
                        // From IR
                        Node[] nodes = (Node[]) idlSource;
                        try {
                            for (int i=0; i< nodes.length; i++) {
                                if (nodes[i] instanceof org.netbeans.modules.corba.browser.ir.util.Generatable)
                                    ((org.netbeans.modules.corba.browser.ir.util.Generatable)nodes[i]).generateCode (out);
                            }
                        }finally {
                            if (out != null)
                                out.close();
                            if (lock != null) 
                                lock.releaseLock ();      
                        }
                    }
                }
                
                // Open IDL file in editor
                DataObject idlDataObject = DataObject.find (destination);
                EditorCookie editorCookie = (EditorCookie) idlDataObject.getCookie (EditorCookie.class);
                editorCookie.open();
                
                // Create Impl files
                if ((mode & CorbaWizardData.IMPL) == CorbaWizardData.IMPL) {
                    TopManager.getDefault().setStatusText (CorbaWizardAction.getLocalizedString("MSG_CreatingImpl"));
                    IDLDataObject dataObject = (IDLDataObject) DataObject.find (destination);
                    IDLNodeCookie idlCookie = (IDLNodeCookie) dataObject.getCookie (IDLNodeCookie.class);
                    idlCookie.GenerateImpl (dataObject);
                }
                
                // Create Client
                if ((mode & CorbaWizardData.CLIENT) == CorbaWizardData.CLIENT) {
                    TopManager.getDefault().setStatusText (CorbaWizardAction.getLocalizedString("MSG_CreatingClient"));
                    DataFolder templates = TopManager.getDefault().getPlaces().folders().templates();
                    DataObject template = findDataObject (templates,"CORBA/ClientMain");    // No I18N
                    DataObject client = template.createFromTemplate (pkg,name+"Client");
                    OpenCookie openCookie = (OpenCookie) client.getCookie (OpenCookie.class);
                    if (openCookie != null)
                        openCookie.open();
                }
                
                // Create Server 
                if ((mode & CorbaWizardData.SERVER) == CorbaWizardData.SERVER) {
                    TopManager.getDefault().setStatusText (CorbaWizardAction.getLocalizedString("MSG_CreatingServer"));
                    DataFolder templates = TopManager.getDefault().getPlaces().folders().templates();
                    DataObject template = findDataObject (templates,"CORBA/ServerMain");    // No I18N
                    DataObject server = template.createFromTemplate (pkg,name+"Server");
                    OpenCookie openCookie = (OpenCookie) server.getCookie (OpenCookie.class);
                    if (openCookie != null)
                        openCookie.open();
                } 
            }catch (IOException ioe) {
                // Handle Error Here
                ioe.printStackTrace ();
            }
            finally {
                CorbaWizard.this.rollBack();
            }
        }

        /** Finds the DataObject given by name in the hierarchy
         *  @param DataFolder folder, the root folder
         *  @param String name, name of DataObject, '/' as separator
         *  @return DataObject if exists, null otherwise
         */
        private DataObject findDataObject (DataFolder folder, String name) {
            if (name.length() == 0)
                return null;
            return findDataObject (folder, new StringTokenizer (name, "/"));
        }

        private DataObject findDataObject (DataFolder folder, StringTokenizer tk) {
            String nameComponent = tk.nextToken();
            DataObject[] list = folder.getChildren();
            for (int i=0; i< list.length; i++) {
                if (list[i].getName().equals(nameComponent)){
                    if (!tk.hasMoreTokens())
                        return list[i];
                    else if (list[i] instanceof DataFolder) 
                        return findDataObject ((DataFolder)list[i], tk);
                    else 
                        return null;
                }
            }
            return null;
        }
    }

    /** Creates new CorbaWizard */
    public CorbaWizard() {
        this.index = 0;
        this.data = new CorbaWizardData ();
    }
  
  
    /** Returns current panel
     *  @return WizardDescriptor.Panel
     */
    public WizardDescriptor.Panel current() {
        switch (this.index) {
        case 0:
            return packagePanel;
        case 1:
            return startPanel;
        case 2:
            return orbPanel;
        case 3:
            if ((this.data.getGenerate() & CorbaWizardData.IDL) == CorbaWizardData.IDL) { 
                return idlWizardPanel;
            }
            else {
                return idlPanel;
            }
        case 4:
            return finishPanel;
        default:
            return null;
        }
    }
  
    /** Can the iterater return next panel
     *  @return boolean 
     */
    public boolean hasNext() {
        return (index < (panelsCount -1) );
    }
  
    /** Can the iterator return previous panel
     *  @return boolean
     */
    public boolean hasPrevious () {
        return index > 0;
    }
  
    /** Returns total count of panels
     *  @return int count
     */
    private int totalCount () {
        return panelsCount;
    }
  
    /** Return index of current panel
     *  @return int current index
     */
    private int currentIndex () {
        return index;
    }
  
    /** Returns the name of Wizard
     *  @return String wizard name
     */
    public String name () {
        return Integer.toString(index+1)+"/"+Integer.toString(panelsCount); // No I18N
    }
  
    /** Returns the next panel
     *  @return WizardDescriptor.Panel 
     */
    public synchronized void nextPanel () {
        if (index < panelsCount)
            this.index++;
    }
  
    /** Returns the previous panel
     *  @return WizardDescriptor.Panel
     */
    public synchronized void  previousPanel () {
        if (index > 0)
            this.index--;
    }
  
    /** Starts the wizard
     *  @see CorbaWizardAction
     */
    public void run () {
        if (DEBUG)
            System.out.println("Starting CORBA Wizard...");
        WizardDescriptor descriptor = new WizardDescriptor (CorbaWizard.this, data);
        descriptor.setClosingOptions (new Object[] {DialogDescriptor.CANCEL_OPTION});
        descriptor.setTitleFormat(new java.text.MessageFormat ("CORBA Wizard[{1}]"));
        descriptor.addPropertyChangeListener (CorbaWizard.this);
        dialog = TopManager.getDefault().createDialog (descriptor);
        dialog.show();
        if (descriptor.getValue() == WizardDescriptor.FINISH_OPTION){
        }
    }
  
    /** Adds ChangeListener
     *  @param ChangeListener listener
     */
    public synchronized void addChangeListener (ChangeListener listener){
        if (DEBUG)
            System.out.println("addChangeListener added");
        this.listeners.add (listener);
    }
  
    /** Removes ChangeListener
     * @param ChangeListener listener
     */
    public synchronized void removeChangeListener (ChangeListener listener){
        this.listeners.remove (listener);
    }
  
    /** Callback for CorbaWizardDescriptor
     *  @param PropertyChangeListener event
     */
    public void propertyChange(final PropertyChangeEvent event) {
        if (event.getPropertyName().equals(DialogDescriptor.PROP_VALUE)){  
            Object option = event.getNewValue();
            if (option == WizardDescriptor.FINISH_OPTION) {
                WizardGenerator wg = this.new WizardGenerator ();
                wg.start ();
            }
            else if (option == WizardDescriptor.CANCEL_OPTION) {
                this.rollBack();
                dialog.setVisible(false);
                dialog.dispose ();
            }
        }
    }


    protected void fireEvent () {
        ArrayList list;
        synchronized (this) {
            list = (ArrayList) this.listeners.clone();
        }
        ChangeEvent event = new ChangeEvent (this);
        for (int i=0; i<list.size(); i++)
            ((ChangeListener)list.get (i)).stateChanged (event);
    }
    
    
    private void rollBack () {
        CORBASupportSettings css = this.data.getSettings();
        if (css != null) {
            if (this.data.getDefaultServerBindingValue() != null)
                css.getActiveSetting().setServerBindingFromString(this.data.getDefaultServerBindingValue());
            if (this.data.getDefaultClientBindingValue() != null)
                css.getActiveSetting().setClientBindingFromString (this.data.getDefaultClientBindingValue());
            if (this.data.getDefaultTie())
                css.getActiveSetting().setSkeletons (CORBASupport.TIE);
            else
                css.getActiveSetting().setSkeletons (CORBASupport.INHER);
            if (this.data.getDefaultOrbValue() != null)
                css.setOrb(this.data.getDefaultOrbValue());
            }
    }

  
  
}
