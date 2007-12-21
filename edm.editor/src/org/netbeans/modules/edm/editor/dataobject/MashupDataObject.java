/*
 * The contents of this file are subject to the terms of the Common
 * Development and Distribution License (the License). You may not use this
 * file except in compliance with the License.  You can obtain a copy of the
 *  License at http://www.netbeans.org/cddl.html
 *
 * When distributing Covered Code, include this CDDL Header Notice in each
 * file and include the License. If applicable, add the following below the
 * CDDL Header, with the fields enclosed by brackets [] replaced by your own
 * identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * Copyright 2006 Sun Microsystems, Inc. All Rights Reserved
 *
 */

package org.netbeans.modules.edm.editor.dataobject;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import javax.swing.table.DefaultTableModel;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import com.sun.org.apache.xerces.internal.util.XMLChar;

import org.openide.ErrorManager;
import org.openide.WizardDescriptor;
import org.openide.cookies.SaveCookie;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataFolder;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectExistsException;
import org.openide.loaders.MultiDataObject;
import org.openide.nodes.CookieSet;
import org.openide.nodes.Node;
import org.openide.util.Lookup;
import org.openide.util.lookup.Lookups;
import org.openide.util.lookup.ProxyLookup;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import org.netbeans.modules.etl.ui.model.impl.ETLCollaborationModel;
import org.netbeans.modules.edm.editor.utils.MashupModelHelper;
import org.netbeans.modules.etl.ui.view.ETLEditorTopView;
import org.netbeans.modules.edm.editor.graph.MashupGraphManager;
import org.netbeans.modules.etl.model.impl.ETLDefinitionImpl;

public class MashupDataObject extends MultiDataObject {
    
    public static final String MASHUP_ICON_BASE_WITH_EXT = 
            "org/netbeans/modules/edm/editor/resources/mashup.png"; // NOI18N
    
    private MashupDataEditorSupport editorSupport;
    
    private transient ETLCollaborationModel mModel;
    
    private ETLEditorTopView view;
    
    private MashupDataNode mNode;
    
    private transient AtomicReference<Lookup> myLookup =
            new AtomicReference<Lookup>();
    
    private transient AtomicBoolean isLookupInit = new AtomicBoolean(false);
    
    private MashupGraphManager manager;
    
    public MashupDataObject(FileObject pf, MashupDataLoader loader) throws DataObjectExistsException, IOException {
        super(pf, loader);
        CookieSet cookies = getCookieSet();
        editorSupport = new MashupDataEditorSupport(this);
        cookies.add(editorSupport);
        manager = new MashupGraphManager(this);
    }
    
    public Node createNodeDelegate() {
        if(this.mNode == null) {
            this.mNode = new MashupDataNode(this);
        }
        return this.mNode;
    }
    
    /**
     * subclasses should look updateServices() and additionalInitialLookup()
     */
    public final Lookup getLookup() {
        if (myLookup.get() == null) {
            
            Lookup lookup;
            List<Lookup> list = new LinkedList<Lookup>();
            
            list.add(Lookups.fixed( new Object[]{this}));
            lookup = new ProxyLookup(list.toArray(new Lookup[list.size()]));
            myLookup.compareAndSet(null, lookup);
            isLookupInit.compareAndSet( false, true );
        }
        return myLookup.get();
    }
    
    @Override
    public void setModified(boolean modified) {
        super.setModified(modified);
        if (modified) {
            getCookieSet().add(getSaveCookie());
        } else {
            getCookieSet().remove(getSaveCookie());
        }
    }
    
    private SaveCookie getSaveCookie() {
        return new SaveCookie() {
            public void save() throws IOException {
                getMashupDataEditorSupport().synchDocument();
                getMashupDataEditorSupport().saveDocument();
            }
            
            @Override
            public int hashCode() {
                return getClass().hashCode();
            }
            
            @Override
            public boolean equals(Object other) {
                return getClass().equals(other.getClass());
            }
        };
    }
    
    protected DataObject handleCreateFromTemplate(DataFolder df, String name) throws IOException {
        MashupDataObject dataObject = (MashupDataObject) super.handleCreateFromTemplate(df, name);
        String doName = dataObject.getName();
        //make sure the the name is a valid NMTOKEN.
        if (!XMLChar.isValidNmtoken(doName)) {
            return dataObject;
        }
        
        SaveCookie sCookie = (SaveCookie) dataObject.getCookie(SaveCookie.class);
        if(sCookie != null) {
            sCookie.save();
        }
        return dataObject;
    }
    
    public MashupGraphManager getGraphManager() {
        if(manager == null) {
            manager = new MashupGraphManager(this);
        }
        return manager;
    }
    
    public void initialize(WizardDescriptor descriptor) {
        try {
            
            // get the tables list from the descriptor and add to the model.
            DefaultTableModel tblModel = (DefaultTableModel) descriptor.getProperty("model");
            String url = (String) descriptor.getProperty("mashupConnection");
            this.mModel = MashupModelHelper.getModel(getModel(), tblModel, url);
            try {
                String content = this.mModel.getETLDefinition().toXMLString("");
                editorSupport.openDocument();
                editorSupport.getDocument().remove(0, editorSupport.getDocument().getLength());
                editorSupport.getDocument().insertString(0, content, null);
            } catch(Exception ex) {
                ErrorManager.getDefault().notify(ex);
            }
            if(view == null) {
                view = new ETLEditorTopView(mModel);
            }
            if(manager == null) {
                manager = new MashupGraphManager(this);
                manager.refreshGraph();
            }
        } catch(Exception ex) {
            ErrorManager.getDefault().notify(ex);
        }
    }
    
    public ETLCollaborationModel getModel() {
        if(this.mModel == null) {
            Element elem = null;
            ETLDefinitionImpl etlDefn = null;
            mModel = new ETLCollaborationModel(this.getName());
            try {
                elem = parseFile(this.getPrimaryFile());
            } catch (Exception ex) {
                elem = null;
            }
            if(elem != null) {
                try {
                    etlDefn = new ETLDefinitionImpl(elem, null);
                } catch (Exception ex) {
                    etlDefn = new ETLDefinitionImpl(this.getName());
                }
            } else {
                etlDefn = new ETLDefinitionImpl(this.getName());
            }
            mModel.setDefinitionContent(etlDefn);
        }
        return this.mModel;
    }
    
    public ETLEditorTopView getEditorView() {
        if(this.view == null) {
            view = new ETLEditorTopView(getModel());
        }
        return this.view;
    }
    
    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public MashupDataEditorSupport getMashupDataEditorSupport() {
        return editorSupport;
    }
    
    private Element parseFile(FileObject pf) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(pf.getInputStream());
        return document.getDocumentElement();
    }
}