/*
 *                          Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License Version
 * 1.0 (the "License"). You may not use this file except in compliance with
 * the License. A copy of the License is available at http://www.sun.com/
 *
 * The Original Code is the LaTeX module.
 * The Initial Developer of the Original Code is Jan Lahoda.
 * Portions created by Jan Lahoda_ are Copyright (C) 2002-2004.
 * All Rights Reserved.
 *
 * Contributor(s): Jan Lahoda.
 */
package org.netbeans.modules.latex.guiproject;

import java.beans.PropertyChangeListener;
import java.io.File;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.awt.Image;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.ImageIcon;

import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectInformation;
import org.netbeans.modules.latex.guiproject.ui.ProjectSettings;
import org.netbeans.modules.latex.guiproject.ui.PropertiesDialogPanel;


import org.netbeans.modules.latex.model.Utilities;

import org.netbeans.modules.latex.model.command.DocumentNode;
import org.netbeans.modules.latex.model.command.LaTeXSource;
import org.netbeans.modules.latex.model.command.impl.LaTeXSourceImpl;
import org.netbeans.modules.latex.model.structural.Model;
import org.netbeans.modules.latex.model.structural.StructuralElement;
import org.netbeans.modules.latex.model.structural.StructuralNodeFactory;
import org.netbeans.spi.project.ActionProvider;
import org.netbeans.spi.project.AuxiliaryConfiguration;

import org.netbeans.spi.project.ui.CustomizerProvider;

import org.netbeans.spi.project.ui.LogicalViewProvider;
import org.netbeans.spi.project.ui.support.LogicalViews;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;


import org.openide.ErrorManager;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;

import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectExistsException;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.FilterNode;
import org.openide.nodes.Node;
import org.openide.util.Lookup;
import org.openide.util.lookup.Lookups;

import org.w3c.dom.Element;

/**
 *
 * @author Jan Lahoda
 */
public class LaTeXGUIProject implements Project, ProjectInformation, LogicalViewProvider, ActionProvider, CustomizerProvider {
    
    private FileObject dir;
    private FileObject masterFile;
    private Lookup lookup;
    
    private LaTeXSource source;
    
    public static final String COMMAND_SHOW = "latex-show";
    
    private static final Image LaTeXGUIProjectIMAGE;
    private static final Icon LaTeXGUIProjectICON;
    
    static {
        LaTeXGUIProjectIMAGE = org.openide.util.Utilities.loadImage("org/netbeans/modules/latex/guiproject/resources/latex_gui_project_icon.gif");
        LaTeXGUIProjectICON  = new ImageIcon(LaTeXGUIProjectIMAGE);
    }
    
    /** Creates a new instance of LaTeXGUIProject */
    public LaTeXGUIProject(FileObject dir, FileObject masterFile) {
        this.dir = dir;
        this.masterFile = masterFile;
        source = new LaTeXSourceImpl(masterFile);
        lookup = Lookups.fixed(new Object[] {this, GenericSources.genericOnly(this), source});
    }
    
    public Lookup getLookup() {
        return lookup;
    }
    
    public FileObject getProjectDirectory() {
        return dir;//.getParent();
    }
    
    public void addPropertyChangeListener(PropertyChangeListener listener) {
    }
    
    public String getDisplayName() {
        return getName();
    }
    
    public Icon getIcon() {
        return LaTeXGUIProjectICON;
    }
    
    public String getName() {
        return masterFile.getNameExt();
    }
    
    public Project getProject() {
        return this;
    }
    
    public void removePropertyChangeListener(PropertyChangeListener listener) {
    }
    
    public Node createLogicalView() {
        return new LaTeXGUIProjectNode(this);
    }
    
    public Node findPath(Node root, Object target) {
        return null;
    }
    
    public LaTeXSource getSource() {
        return source;
    }
    
    private Node createSourcesNode() {
        AbstractNode an = new AbstractNode(new LaTeXChildren());
        
        an.setDisplayName("Sources");
        
        return an;
    }
    
    private Node createStructuralNode() {
//        System.err.println("masterFile = " + masterFile );
        StructuralElement rootEl = Model.getDefault().getModel(masterFile);
        
        Node fnode = new MainStructuralNode(StructuralNodeFactory.createNode(rootEl));
        
        return fnode;
    }
    
    private Children createChildren() {
        Children.Array mainChildren = new Children.Array();
        
        mainChildren.add(new Node[] {createSourcesNode(), createStructuralNode()});
        
        return mainChildren;
    }
    
    public String[] getSupportedActions() {
        return new String[] {
            COMMAND_BUILD,
            COMMAND_CLEAN,
            COMMAND_SHOW
        };
    }
    
    public void invokeAction(String command, Lookup context) throws IllegalArgumentException {
        System.err.println("invoked: " + command);
        if (COMMAND_BUILD.equals(command)) {
            ActionsFactory.build(this, "build");
            return ;
        }
        if (COMMAND_SHOW.equals(command)) {
            ActionsFactory.build(this, "show");
            return ;
        }
    }
    
    /*package private*/ File getProjectInternalDir() {
        return FileUtil.toFile(dir);
    }
    
    /*package private*/boolean contains(FileObject file) {
        //TODO: more effeciently:
        DocumentNode doc = source.getDocument();
        
        if (doc == null)
            return Utilities.getDefault().compareFiles(source.getMainFile(), file);
        else
            return doc.getFiles().contains(file);
    }
    
    public boolean isActionEnabled(String command, Lookup context) throws IllegalArgumentException {
        return true;//TODO:....
    }

    public void showCustomizer() {
        ProjectSettings settings = ProjectSettings.getDefault(this);
        PropertiesDialogPanel panel = new PropertiesDialogPanel(settings);
        DialogDescriptor dd = new DialogDescriptor(panel, "Properties");
        DialogDisplayer.getDefault().createDialog(dd).show();
        
        panel.store();
        
        if (dd.getValue() == DialogDescriptor.OK_OPTION) {
            settings.commit();
        } else {
            settings.rollBack();
        }
    }
    
    private static class MainStructuralNode extends FilterNode {
        
        public MainStructuralNode(Node original) {
            super(original);
            disableDelegation(DELEGATE_GET_DISPLAY_NAME | DELEGATE_SET_DISPLAY_NAME);
            setDisplayName("Structure");
        }
        
    }
    
    private static class LaTeXGUIProjectNode extends AbstractNode {
        
        private LaTeXGUIProject project;
        
        public LaTeXGUIProjectNode(LaTeXGUIProject project) {
            super(project.createChildren(), Lookups.fixed(new Object[] {project}));
            setDisplayName(project.getDisplayName());
            setIconBase("org/netbeans/modules/latex/guiproject/resources/latex_gui_project_icon");
        }
        
        public Action[] getActions(boolean context) {
            //TODO: context???:
            return new Action[] {
                ActionsFactory.createShowAction(),
                null,
                ActionsFactory.createBuildAction(),
                null,
                LogicalViews.setAsMainProjectAction(),
                LogicalViews.closeProjectAction(),
                null,
                LogicalViews.customizeProjectAction(),
            };
        }
    }
    
    
    private class LaTeXChildren extends Children.Keys implements LaTeXSource.DocumentChangedListener/*TODO: Weak?*/ {
        
        public void addNotify() {
            getSource().addDocumentChangedListener(this);
            doSetKeys();
        }
        
        private void doSetKeys() {
            Thread.dumpStack();
            DocumentNode dn = getSource().getDocument();
            List toAdd = new ArrayList();
            
            if (dn != null)
                toAdd.addAll(dn.getFiles());
            
            Object main = getSource().getMainFile();
            
            if (toAdd.contains(main)) {
                toAdd.remove(main);
            }
            
            toAdd.add(0, main);
            
            System.err.println("toAdd=" + toAdd);
            setKeys(toAdd);
        }
        
        protected Node[] createNodes(Object key) {
            try {
            DataObject od = DataObject.find((FileObject) key);
            
            return new Node[] {new SourceFileNode(od.getNodeDelegate())};
            } catch (DataObjectNotFoundException e) {
                ErrorManager.getDefault().notify(e);
                return new Node[0];
            }
        }
        
        public void nodesAdded(LaTeXSource.DocumentChangeEvent evt) {
            doSetKeys();
        }
        
        public void nodesRemoved(LaTeXSource.DocumentChangeEvent evt) {
            doSetKeys();
        }
        
        public void nodesChanged(LaTeXSource.DocumentChangeEvent evt) {
            doSetKeys();
        }

    }
    
    private static class SourceFileNode extends FilterNode {
        public SourceFileNode(Node shadow) {
            super(shadow, Children.LEAF);
        }
        
        public String getHtmlDisplayName() {
            return null;
        }
    }
    
//    private static class AuxiliaryConfigurationImpl implements AuxiliaryConfiguration {
//
//        public Element getConfigurationFragment(String elementName, String namespace, boolean shared) {
//            if (elementName == null || elementName.indexOf(':') != -1 || namespace == null) {
//                throw new IllegalArgumentException("Illegal elementName and/or namespace"); // NOI18N
//            }
//            return helper.getConfigurationFragment(elementName, namespace, shared);
//        }
//
//        public void putConfigurationFragment(Element fragment, boolean shared) throws IllegalArgumentException {
//        }
//
//        public boolean removeConfigurationFragment(String elementName, String namespace, boolean shared) throws IllegalArgumentException {
//        }
//        
//    }
}
