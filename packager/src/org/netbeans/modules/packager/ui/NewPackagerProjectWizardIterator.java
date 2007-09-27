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

package org.netbeans.modules.packager.ui;

import java.awt.Component;
import java.util.List;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import javax.swing.JComponent;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectManager;
import org.netbeans.modules.packager.PackagerProject;
import org.netbeans.spi.project.support.ant.AntProjectHelper;
import org.openide.ErrorManager;
import org.openide.WizardDescriptor;
import org.openide.filesystems.FileLock;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.NbBundle;


/**
 * Wizard to create a new Packager project.  Dynamically includes platform
 * configuration panels depending on the platforms checked in the first
 * panel.
 *
 * @author Tim Boudreau
 */
public class NewPackagerProjectWizardIterator implements WizardDescriptor.InstantiatingIterator, ChangeListener {

    static final int TYPE_APP = 0;
    static final int TYPE_LIB = 1;
    static final int TYPE_EXT = 2;

    private static final String MANIFEST_FILE = "manifest.mf";

    private static final long serialVersionUID = 1L;
    
    private int type;
    
    /** Create a new wizard iterator. */
    public NewPackagerProjectWizardIterator() {
        this(TYPE_APP);
    }
    
    public NewPackagerProjectWizardIterator(int type) {
        this.type = type;
    }
        
    public Set/*<FileObject>*/ instantiate () throws IOException {
        Set resultSet = new HashSet ();
        
        File dirF = (File)wiz.getProperty(PackagerProject.KEY_DIR);      
        String name = (String)wiz.getProperty(PackagerProject.KEY_NAME);       
        Project[] projects = (Project[])wiz.getProperty(PackagerProject.KEY_INDIRECT_DEPENDENCIES);
        
        //Store all of the keyed data from the wizard in properties
        Map data = new HashMap();
        for (int i=0; i < PackagerProject.ALL_KEYS.length; i++) {
            String key = PackagerProject.ALL_KEYS[i];
            Object value = wiz.getProperty(key);
            data.put (key, value);
        }
        
        AntProjectHelper helper = PackagerProject.createProject (dirF, name, 
            projects, data);
        
        FileObject dir = FileUtil.toFileObject(dirF);
        resultSet.add (dir);
        return resultSet;
    }
    
    private transient WizardDescriptor wiz;
    public void uninitialize(WizardDescriptor wiz) {
        String[] keys = PackagerProject.ALL_KEYS;
        for (int i=0; i < keys.length; i++) {
            wiz.putProperty(keys[i], null);
        }
        this.wiz = null;
    }
    
    public synchronized void initialize(WizardDescriptor wiz) {
        firstPanel = new CustomizerAsWizardPanel();
        JComponent jc = (JComponent) firstPanel.getComponent();
        
        jc.putClientProperty("WizardPanel_contentSelectedIndex", //NOI18N
            new Integer(0)); // NOI18N

        // Step name (actually the whole list for reference).
        jc.putClientProperty("WizardPanel_contentData", new String[] {
            firstPanel.getComponent().getName() }); // NOI18N
        
        
        this.wiz = wiz;
        syncState ();
        firstPanel.addChangeListener (this);
    }    
    
    private boolean[] lastValues = new boolean[4];
    private static final String[] platformKeys = new String[] {
        PackagerProject.KEY_UNIX,
        PackagerProject.KEY_MAC,
        PackagerProject.KEY_WINDOWS,
        PackagerProject.KEY_WEBSTART
    };
    
    private static final String[] stepsDescriptions = new String[] {
        NbBundle.getMessage (NewPackagerProjectWizardIterator.class,
            "LAB_ConfigureUnix"),
        NbBundle.getMessage (NewPackagerProjectWizardIterator.class,
            "LAB_ConfigureMac"),
        NbBundle.getMessage (NewPackagerProjectWizardIterator.class,
            "LAB_ConfigureWindows"),
        NbBundle.getMessage (NewPackagerProjectWizardIterator.class,
            "LAB_ConfigureJnlp"),
           
    };
    
    private CustomizerAsWizardPanel firstPanel = null; 
    private List panelsInUse = new ArrayList();
    private List descriptions = new ArrayList();
    private int index = 0;
    
    /**
     * Changing supported platforms changes the number of steps, so we will
     * rebuild the list of panels and descriptions to match.
     */
    private synchronized void syncState () {
        boolean[] values = new boolean[4];
        for (int i=0; i < platformKeys.length; i++) {
            values[i] = checkProp (platformKeys[i]);
        }
        //If nothing has changed we're done
        if (Arrays.equals(values, lastValues)) {
            return;
        }
        lastValues = values;
        
        //Rebuild the list
        panelsInUse.clear();
        panelsInUse.add (firstPanel);
        descriptions.clear();
        descriptions.add (NbBundle.getMessage(
            NewPackagerProjectWizardIterator.class, "LAB_ConfigureProject")); //NOI18N
        
        //Find all of the panels now to be used and add them and their
        //descriptions to the list
        for (int i=0; i < values.length; i++) {
            if (values[i]) {
                WizardDescriptor.Panel pnl = getPanel(i);
                panelsInUse.add (pnl);
                descriptions.add (stepsDescriptions[i]);
            }
        }
        
        String[] steps = new String[descriptions.size()];
        steps = (String[]) descriptions.toArray(steps);
        
        //Now set up the client properties for steps, names, etc.  So much for
        //lazy init.
        int idx=0;
        for (Iterator i = panelsInUse.iterator(); i.hasNext();) {
            WizardDescriptor.Panel pnl = (WizardDescriptor.Panel) i.next();
            
            JComponent jc = (JComponent) pnl.getComponent(); //So much for lazy init
            jc.setName ((String) descriptions.get(idx));

            jc.putClientProperty("WizardPanel_contentSelectedIndex", //NOI18N
                new Integer(idx)); // NOI18N

            // Step name (actually the whole list for reference).
            jc.putClientProperty("WizardPanel_contentData", steps); // NOI18N

            idx++;
        }
        fire();
    }
    
    private WizardDescriptor.Panel[] panelsByPlatform = new WizardDescriptor.Panel[4];
    private WizardDescriptor.Panel getPanel (int platformIndex) {
        if (panelsByPlatform[platformIndex] == null) {
            switch (platformIndex) {
                case 0 :
                    panelsByPlatform[platformIndex] = null; //new UnixPanel()...
                    break;
                case 1 :
                    panelsByPlatform[platformIndex] = new MacOptionsWizardPanel();
                    break;
                case 2 :
                    panelsByPlatform[platformIndex] = null; //new WindowsPanel()...
                    break;
                case 3 :
                    panelsByPlatform[platformIndex] = new JnlpOptionsWizardPanel();
                    break;
                default :
                    throw new IllegalArgumentException ("Unexpected: " +  //NOI18N
                        platformIndex);
            }
            WizardDescriptor.Panel result = panelsByPlatform[platformIndex];
        }
        return panelsByPlatform[platformIndex];
    }
    
    private boolean checkProp (String key) {
        return wiz != null && Boolean.TRUE.equals(wiz.getProperty(key));
    }
    
    
    public String name() {
        return MessageFormat.format (NbBundle.getMessage(NewPackagerProjectWizardIterator.class,"LAB_IteratorName"),
            new Object[] {new Integer (index + 1), new Integer (panelsInUse.size()) });
    }

    public boolean hasNext() {
        return index < panelsInUse.size() - 1;
    }
    
    public boolean hasPrevious() {
        return index > 0;
    }
    
    public void nextPanel() {
        if (!hasNext()) throw new NoSuchElementException();
        index++;
    }
    
    public void previousPanel() {
        if (!hasPrevious()) throw new NoSuchElementException();
        index--;
    }
    
    public WizardDescriptor.Panel current () {
        if (panelsInUse.size() == 0) {
            //we haven't been initialized yet
            return firstPanel;
        } else {
            return (WizardDescriptor.Panel) panelsInUse.get(index);
        }
    }
    
    private final Set/*<ChangeListener>*/ listeners = new HashSet(1);
    public final void addChangeListener(ChangeListener l) {
        synchronized (listeners) {
            listeners.add(l);
        }
    }
    public final void removeChangeListener(ChangeListener l) {
        synchronized (listeners) {
            listeners.remove(l);
        }
    }
    
    protected final void fire() {
        Iterator it;
        synchronized (listeners) {
            it = new HashSet(listeners).iterator();
        }
        ChangeEvent ev = new ChangeEvent(this);
        while (it.hasNext()) {
            ((ChangeListener)it.next()).stateChanged(ev);
        }
    }
       
    public synchronized void stateChanged(ChangeEvent changeEvent) {
        syncState();
    }
}

