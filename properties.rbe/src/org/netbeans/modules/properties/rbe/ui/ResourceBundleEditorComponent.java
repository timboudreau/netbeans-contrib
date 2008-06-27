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
 * Contributor(s): Denis Stepanov
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun
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
package org.netbeans.modules.properties.rbe.ui;

import java.awt.Image;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import javax.swing.ActionMap;
import javax.swing.BoxLayout;
import javax.swing.SwingUtilities;
import javax.swing.text.DefaultEditorKit;
import org.netbeans.modules.properties.PropertiesDataObject;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.cookies.SaveCookie;
import org.openide.explorer.ExplorerManager;
import org.openide.explorer.ExplorerUtils;
import org.openide.loaders.DataObject;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;
import org.openide.util.Utilities;
import org.openide.util.WeakListeners;
import org.openide.util.lookup.AbstractLookup;
import org.openide.util.lookup.InstanceContent;
import org.openide.util.lookup.ProxyLookup;
import org.openide.windows.CloneableTopComponent;

/**
 * The Resourcebundle editor top component
 * @author Denis Stepanov <denis.stepanov at gmail.com>
 */
public class ResourceBundleEditorComponent extends CloneableTopComponent implements SaveCookie,
        ExplorerManager.Provider, PropertyChangeListener {

    public final static String PREFERRED_ID = "ResourceBundleEditorComponent";
    public final static String PROPERTIES_EXT = ".properties";
    /** Properties data object */
    private PropertiesDataObject dataObject;
    /** The explorer manager */
    private ExplorerManager explorerManager;
    /** The lookup instance content */
    private InstanceContent ic;

//    public ResourceBundleEditorComponent() {
//    }

    /** The tree view */
    public ResourceBundleEditorComponent(PropertiesDataObject dataObject) {
        this.dataObject = dataObject;
        initialize();
    }

    protected void initialize() {
        dataObject.addPropertyChangeListener(WeakListeners.propertyChange(this, dataObject));

        explorerManager = new ExplorerManager();
        ic = new InstanceContent();
        ic.add(dataObject);

        ActionMap actionMap = getActionMap();
        actionMap.put(DefaultEditorKit.copyAction, ExplorerUtils.actionCopy(explorerManager));
        actionMap.put(DefaultEditorKit.pasteAction, ExplorerUtils.actionPaste(explorerManager));
        actionMap.put(DefaultEditorKit.cutAction, ExplorerUtils.actionCut(explorerManager));
        actionMap.put("delete", ExplorerUtils.actionDelete(explorerManager, true));

        associateLookup(new ProxyLookup(ExplorerUtils.createLookup(explorerManager, actionMap), new AbstractLookup(ic)));

        updateName();
        setToolTipText(NbBundle.getMessage(ResourceBundleEditorComponent.class, "CTL_ResourceBundleEditorComponent"));
        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        add(new UIWindow(new RBE(dataObject)));
    }

    protected void updateName() {
        if (dataObject.isModified()) {
            setName(dataObject.getName() + PROPERTIES_EXT + " *");
        } else {
            setName(dataObject.getName() + PROPERTIES_EXT);
        }
    }

    public void save() throws IOException {
        SaveCookie saveCookie = dataObject.getLookup().lookup(SaveCookie.class);
        if (saveCookie != null) {
            try {
                saveCookie.save();
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
    }

    @Override
    public boolean canClose() {
//
//        closeEntry((PropertiesFileEntry) dataObject.getPrimaryEntry());
//        for (Iterator it = dataObject.secondaryEntries().iterator(); it.hasNext();) {
//            closeEntry((PropertiesFileEntry) it.next());
//        }
//        PropertiesEditorSupport editorSupport = dataObject.getCookie(PropertiesEditorSupport.class);
//        if(editorSupport != null){
//            return editorSupport.close();
//        }

        if (dataObject != null && !dataObject.isModified()) {
            return true;
        }

        //TODO: move to the bundle
        String title = "Close?";
        String question = "Do you want to close?";
        String optionSave = "Save";
        String optionDiscard = "Discard";

        NotifyDescriptor descr = new DialogDescriptor(
                question,
                title,
                true,
                new Object[]{
                    optionSave,
                    optionDiscard,
                    NotifyDescriptor.CANCEL_OPTION
                },
                optionSave,
                DialogDescriptor.DEFAULT_ALIGN,
                null,
                null);

        descr.setMessageType(NotifyDescriptor.QUESTION_MESSAGE);


        Object answer = DialogDisplayer.getDefault().notify(descr);
        if (optionSave.equals(answer)) {
            try {
                save();
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            }
        }


        return optionSave.equals(answer) || optionDiscard.equals(answer);
    }

    @Override
    public Image getIcon() {
        return Utilities.loadImage("org/netbeans/modules/properties/rbe/resources/propertiesObject.png"); // NOI18N

    }

    @Override
    public int getPersistenceType() {
        return PERSISTENCE_NEVER;
    }

    @Override
    protected String preferredID() {
        return PREFERRED_ID;
    }

    public ExplorerManager getExplorerManager() {
        return explorerManager;
    }

    public void propertyChange(PropertyChangeEvent evt) {
        if (dataObject == evt.getSource() && DataObject.PROP_MODIFIED.equals(evt.getPropertyName())) {
            if ((Boolean) evt.getNewValue()) {
                ic.add(this);
            } else {
                ic.remove(this);
            }
            SwingUtilities.invokeLater(new Runnable() {

                public void run() {
                    updateName();
                }
            });
        }
    }

    @Override
    protected CloneableTopComponent createClonedObject() {
        return new ResourceBundleEditorComponent(dataObject);
    }

//    @Override
//    public void writeExternal(ObjectOutput oo) throws IOException {
//        super.writeExternal(oo);
//        oo.writeObject(dataObject);
//
//    }
//
//    @Override
//    public void readExternal(ObjectInput oi) throws IOException, ClassNotFoundException {
//        super.readExternal(oi);
//        dataObject = (PropertiesDataObject) oi.readObject();
//        initialize();
//    }
}
