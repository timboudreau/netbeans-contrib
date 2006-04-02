/*
 *                          Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License Version
 * 1.0 (the "License"). You may not use this file except in compliance with
 * the License. A copy of the License is available at http://www.sun.com/
 *
 * The Original Code is the LaTeX module.
 * The Initial Developer of the Original Code is Jan Lahoda.
 * Portions created by Jan Lahoda_ are Copyright (C) 2002-2006.
 * All Rights Reserved.
 *
 * Contributor(s): Jan Lahoda.
 */
package org.netbeans.modules.latex.guiproject;

import java.awt.Image;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JSeparator;
import javax.swing.event.ChangeListener;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectInformation;
import org.netbeans.api.project.ProjectUtils;
import org.netbeans.modules.latex.guiproject.ui.LaTeXGUIProjectCustomizer;
import org.netbeans.modules.latex.model.Utilities;
import org.netbeans.modules.latex.model.command.DocumentNode;
import org.netbeans.modules.latex.model.command.LaTeXSource;
import org.netbeans.modules.latex.model.command.impl.LaTeXSourceImpl;
import org.netbeans.modules.latex.model.structural.Model;
import org.netbeans.modules.latex.model.structural.StructuralElement;
import org.netbeans.modules.latex.model.structural.StructuralNodeFactory;
import org.netbeans.spi.navigator.NavigatorLookupHint;
import org.netbeans.spi.project.ui.LogicalViewProvider;
import org.netbeans.spi.project.ui.support.CommonProjectActions;
import org.netbeans.spi.project.ui.support.ProjectSensitiveActions;
import org.openide.ErrorManager;
import org.openide.actions.FindAction;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileStateInvalidException;
import org.openide.filesystems.FileStatusEvent;
import org.openide.filesystems.FileStatusListener;
import org.openide.filesystems.FileSystem;
import org.openide.filesystems.FileUtil;
import org.openide.filesystems.Repository;
import org.openide.loaders.DataFolder;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.loaders.FolderLookup;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.FilterNode;
import org.openide.nodes.Node;
import org.openide.util.Lookup;
import org.openide.util.RequestProcessor;
import org.openide.util.lookup.Lookups;
import org.openide.util.lookup.ProxyLookup;

/**
 *
 * @author Jan Lahoda
 */
public class LaTeXGUIProject implements Project, LogicalViewProvider, LaTeXSource.DocumentChangedListener {
    
    private FileObject dir;
    private FileObject masterFile;
    private Lookup lookup;
    
    private LaTeXSource source;
    
    private PropertyChangeSupport pcs;
    
    public static final String PROP_CONTAINED_FILES = "containedFiles";
    
    public static final String COMMAND_SHOW = "latex-show";//NOI18N
    
    private static final NavigatorLookupHint NAVIGATOR_HINT = new NavigatorHintImpl();
    
    private static final Image LaTeXGUIProjectIMAGE;
    private static final Icon LaTeXGUIProjectICON;
    
    static {
        LaTeXGUIProjectIMAGE = org.openide.util.Utilities.loadImage("org/netbeans/modules/latex/guiproject/resources/latex_gui_project_icon.gif");//NOI18N
        LaTeXGUIProjectICON  = new ImageIcon(LaTeXGUIProjectIMAGE);
    }
    
    /** Creates a new instance of LaTeXGUIProject */
    public LaTeXGUIProject(FileObject dir, FileObject masterFile) {
        this.dir = dir;
        this.masterFile = masterFile;
        source = new LaTeXSourceImpl(masterFile);
        source.addDocumentChangedListener(source.weakDocumentChangedListener(this, source));
        pcs = new PropertyChangeSupport(this);
        lookup = Lookups.fixed(new Object[] {
            new Info(),
            this,
            new ActionsFactory(this),
            GenericSources.genericOnly(this),
            source,
            new LaTeXGUIProjectOpenedHookImpl(this),
            new LaTeXAuxiliaryConfigurationImpl(this),
            new LaTeXSharabilityQuery(this),
            new LaTeXGUIProjectCustomizer(this),
        });
    }
    
    public Lookup getLookup() {
        return lookup;
    }
    
    public FileObject getProjectDirectory() {
        return dir;
    }
    
    public void addPropertyChangeListener(PropertyChangeListener l) {
        pcs.addPropertyChangeListener(l);
    }
    
    public void removePropertyChangeListener(PropertyChangeListener l) {
        pcs.removePropertyChangeListener(l);
    }
    
    /*package private*/FileObject getMasterFile() {
        return masterFile;
    }
    
    public Node createLogicalView() {
        return new LaTeXGUIProjectNode(this);
    }
    
    public Node findPath(Node root, Object target) {
        if (root.getLookup().lookup(LaTeXGUIProject.class) != this)
            return null;
        
        Node[] files = root.getChildren().getNodes(true);
        Lookup.Template searchingTemplate= new Lookup.Template(null, null, target);
        
        for (int cntr = 0; cntr < files.length; cntr++) {
            if (files[cntr].getLookup().lookup(searchingTemplate).allInstances().size() > 0)
                return files[cntr];
        }
        
        return null;
    }
    
    public LaTeXSource getSource() {
        return source;
    }
    
    private static final String SOURCE_NODE_NAME = "Sources";
    private static final Object SOURCE_NODE_TAG  = new Object();
    
    private Node createSourcesNode() {
        AbstractNode an = new AbstractNode(new LaTeXChildren(this), Lookups.singleton(SOURCE_NODE_TAG));
        
        an.setDisplayName(SOURCE_NODE_NAME);
        
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
        
        mainChildren.add(new Node[] {createSourcesNode()/*, createStructuralNode()*/});
        
        return mainChildren;
    }
    
    /*package private*/ File getProjectInternalDir() {
        return FileUtil.toFile(dir);
    }
    
    private Collection containedFilesCache;
    
    /*package private*/synchronized boolean contains(FileObject file) {
        //TODO: more effeciently:
        if (containedFilesCache == null)
            return Utilities.getDefault().compareFiles(source.getMainFile(), file);
        else {
            return containedFilesCache.contains(file);
        }
    }
    
    public synchronized Collection getContainedFiles() {
        if (containedFilesCache == null)
            return Collections.singletonList(source.getMainFile());
        else {
            return containedFilesCache;
        }
    }
    
    private synchronized void updateContainedFilesCache() {
        containedFilesCache = new HashSet(source.getDocument().getFiles());
        pcs.firePropertyChange(PROP_CONTAINED_FILES, null, null);
    }
    
    public void nodesRemoved(LaTeXSource.DocumentChangeEvent evt) {
        updateContainedFilesCache();
    }

    public void nodesChanged(LaTeXSource.DocumentChangeEvent evt) {
        updateContainedFilesCache();
    }

    public void nodesAdded(LaTeXSource.DocumentChangeEvent evt) {
        updateContainedFilesCache();
    }

    private static class MainStructuralNode extends FilterNode {
        
        public MainStructuralNode(Node original) {
            super(original);
            disableDelegation(DELEGATE_GET_DISPLAY_NAME | DELEGATE_SET_DISPLAY_NAME);
            setDisplayName("Structure");
        }
        
    }
    
    private static class LaTeXGUIProjectNode extends AbstractNode implements Runnable, FileStatusListener, PropertyChangeListener {
        
        private LaTeXGUIProject project;
        
        // icon badging >>>
        private Set files;
        private Map fileSystemListeners;
        private RequestProcessor.Task task;
        private final Object privateLock = new Object();
        private boolean iconChange;
        private boolean nameChange;        
        private ChangeListener sourcesListener;
        private Map groupsListeners;
        // icon badging <<<
        
        public LaTeXGUIProjectNode(LaTeXGUIProject project) {
            super(new LaTeXChildren(project), Lookups.fixed(new Object[] {project, NAVIGATOR_HINT, project.source, new SearchInfoImpl(project)}));
            setDisplayName(ProjectUtils.getInformation(project).getDisplayName());
            setIconBase("org/netbeans/modules/latex/guiproject/resources/latex_gui_project_icon");
            this.project = project;
            setProjectFiles(project);
            project.addPropertyChangeListener(this);
        }
        
        public Action[] getActions(boolean context) {
            List actions = new ArrayList();
            
            actions.add(ActionsFactory.createShowAction());
            actions.add(null);
            actions.add(ProjectSensitiveActions.projectCommandAction(ActionsFactory.COMMAND_BUILD, "Build Project", null));
            actions.add(ProjectSensitiveActions.projectCommandAction(ActionsFactory.COMMAND_REBUILD, "Clean and Build Project", null));
            actions.add(ProjectSensitiveActions.projectCommandAction(ActionsFactory.COMMAND_CLEAN, "Clean Project", null));
            actions.add(null);
            actions.add(CommonProjectActions.setAsMainProjectAction());
            actions.add(CommonProjectActions.closeProjectAction());

            actions.add(null);
            actions.add(FindAction.get(FindAction.class));
            actions.add(null);
            
            // honor 57874 contact
            
            try {
                Repository repository  = Repository.getDefault();
                FileSystem sfs = repository.getDefaultFileSystem();
                FileObject fo = sfs.findResource("Projects/Actions");  // NOI18N
                if (fo != null) {
                    DataObject dobj = DataObject.find(fo);
                    FolderLookup actionRegistry = new FolderLookup((DataFolder)dobj);
                    Lookup.Template query = new Lookup.Template(Object.class);
                    Lookup lookup = actionRegistry.getLookup();
                    Iterator it = lookup.lookup(query).allInstances().iterator();
                    if (it.hasNext()) {
                        actions.add(null);
                    }
                    while (it.hasNext()) {
                        Object next = it.next();
                        if (next instanceof Action) {
                            actions.add(next);
                        } else if (next instanceof JSeparator) {
                            actions.add(null);
                        }
                    }
                }
            } catch (DataObjectNotFoundException ex) {
                // data folder for exitinf fileobject expected
                ErrorManager.getDefault().notify(ex);
            }
            
            actions.add(null);
            actions.add(CommonProjectActions.customizeProjectAction());
            
            return (Action []) actions.toArray(new Action[actions.size()]);
        }

        
        protected final void setProjectFiles(LaTeXGUIProject project) {
            setFiles(new HashSet(project.getContainedFiles()));
        }
        
        protected final void setFiles(Set files) {
            if (fileSystemListeners != null) {
                Iterator it = fileSystemListeners.keySet().iterator();
                while (it.hasNext()) {
                    FileSystem fs = (FileSystem) it.next();
                    FileStatusListener fsl = (FileStatusListener) fileSystemListeners.get(fs);
                    fs.removeFileStatusListener(fsl);
                }
            }
                        
            fileSystemListeners = new HashMap();
            this.files = files;
            if (files == null) return;

            Iterator it = files.iterator();
            Set hookedFileSystems = new HashSet();
            while (it.hasNext()) {
                FileObject fo = (FileObject) it.next();
                try {
                    FileSystem fs = fo.getFileSystem();
                    if (hookedFileSystems.contains(fs)) {
                        continue;
                    }
                    hookedFileSystems.add(fs);
                    FileStatusListener fsl = FileUtil.weakFileStatusListener(this, fs);
                    fs.addFileStatusListener(fsl);
                    fileSystemListeners.put(fs, fsl);
                } catch (FileStateInvalidException e) {
                    ErrorManager err = ErrorManager.getDefault();
                    err.annotate(e, "Can not get " + fo + " filesystem, ignoring...");  // NO18N
                    err.notify(ErrorManager.INFORMATIONAL, e);
                }
            }
        }

        public String getDisplayName () {
            String s = super.getDisplayName ();

            if (files != null && files.iterator().hasNext()) {
                try {
                    FileObject fo = (FileObject) files.iterator().next();
                    s = fo.getFileSystem ().getStatus ().annotateName (s, files);
                } catch (FileStateInvalidException e) {
                    ErrorManager.getDefault().notify(ErrorManager.INFORMATIONAL, e);
                }
            }

            return s;
        }

         public String getHtmlDisplayName() {
            if (files != null && files.iterator().hasNext()) {
                try {
                    FileObject fo = (FileObject) files.iterator().next();
                    FileSystem.Status stat = fo.getFileSystem().getStatus();
                    if (stat instanceof FileSystem.HtmlStatus) {
                        FileSystem.HtmlStatus hstat = (FileSystem.HtmlStatus) stat;

                        String result = hstat.annotateNameHtml (
                           super.getDisplayName(), files);

                        //Make sure the super string was really modified
                        if (result != null && !result.equals(getDisplayName())) {
                            return result;
                        }
                    }
                } catch (FileStateInvalidException e) {
                    ErrorManager.getDefault().notify(ErrorManager.INFORMATIONAL, e);
                }
            }
            return getDisplayName();
        }
         
        public Image getIcon (int type) {
            Image img = super.getIcon(type);

            if (files != null && files.iterator().hasNext()) {
                try {
                    FileObject fo = (FileObject) files.iterator().next();
                    img = fo.getFileSystem ().getStatus ().annotateIcon (img, type, files);
                } catch (FileStateInvalidException e) {
                    ErrorManager.getDefault().notify(ErrorManager.INFORMATIONAL, e);
                }
            }

            return img;
        }
        public Image getOpenedIcon (int type) {
            Image img = super.getOpenedIcon(type);

            if (files != null && files.iterator().hasNext()) {
                try {
                    FileObject fo = (FileObject) files.iterator().next();
                    img = fo.getFileSystem ().getStatus ().annotateIcon (img, type, files);
                } catch (FileStateInvalidException e) {
                    ErrorManager.getDefault().notify(ErrorManager.INFORMATIONAL, e);
                }
            }

            return img;
        }

        public void run() {
            boolean fireIcon;
            boolean fireName;
            synchronized (privateLock) {
                fireIcon = iconChange;
                fireName = nameChange;
                iconChange = false;
                nameChange = false;
            }
            if (fireIcon) {
                fireIconChange();
                fireOpenedIconChange();
            }
            if (fireName) {
                fireDisplayNameChange(null, null);
            }
        }

        public void annotationChanged(FileStatusEvent event) {
            if (task == null) {
                task = RequestProcessor.getDefault().create(this);
            }

            synchronized (privateLock) {
                if ((iconChange == false && event.isIconChange())  || (nameChange == false && event.isNameChange())) {
                    Iterator it = files.iterator();
                    while (it.hasNext()) {
                        FileObject fo = (FileObject) it.next();
                        if (event.hasChanged(fo)) {
                            iconChange |= event.isIconChange();
                            nameChange |= event.isNameChange();
                        }
                    }
                }
            }

            task.schedule(50);  // batch by 50 ms
        }

        public void propertyChange(PropertyChangeEvent evt) {
            setProjectFiles(project);
         }

    }
    
    
    private static class LaTeXChildren extends Children.Keys implements LaTeXSource.DocumentChangedListener/*TODO: Weak?*/ {

        private LaTeXGUIProject project;

        public LaTeXChildren(LaTeXGUIProject project) {
            this.project = project;
        }

        public void addNotify() {
            project.getSource().addDocumentChangedListener(this);
            doSetKeys();
        }
        
        private void doSetKeys() {
//            Thread.dumpStack();
            DocumentNode dn = project.getSource().getDocument();
            List toAdd = new ArrayList();
            
            if (dn != null)
                toAdd.addAll(dn.getFiles());
            
            Object main = project.getSource().getMainFile();
            
            toAdd.remove(main);
            toAdd.add(0, main);
            
//            System.err.println("toAdd=" + toAdd);
            setKeys(toAdd);
        }
        
        protected Node[] createNodes(Object key) {
            try {
            DataObject od = DataObject.find((FileObject) key);
            
            return new Node[] {new SourceFileNode(od.getNodeDelegate(), (FileObject) key)};
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
        public SourceFileNode(Node shadow, FileObject file) {
            super(shadow, Children.LEAF, new ProxyLookup(new Lookup[] {shadow.getLookup(), Lookups.singleton(file)}));
        }
    }
    
    private class Info implements ProjectInformation {
        
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
            return LaTeXGUIProject.this;
        }
        
        public void removePropertyChangeListener(PropertyChangeListener listener) {
        }
        
    }
    
}
