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

import java.awt.Image;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
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
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectInformation;
import org.netbeans.api.project.ProjectUtils;
import org.netbeans.api.project.SourceGroup;
import org.netbeans.api.project.Sources;
import org.netbeans.modules.latex.guiproject.ui.LaTeXGUIProjectCustomizer;
import org.netbeans.modules.latex.guiproject.ui.ProjectSettings;
import org.netbeans.modules.latex.model.Utilities;
import org.netbeans.modules.latex.model.command.DocumentNode;
import org.netbeans.modules.latex.model.command.LaTeXSource;
import org.netbeans.modules.latex.model.command.impl.LaTeXSourceImpl;
import org.netbeans.modules.latex.model.structural.Model;
import org.netbeans.modules.latex.model.structural.StructuralElement;
import org.netbeans.modules.latex.model.structural.StructuralNodeFactory;
import org.netbeans.spi.navigator.NavigatorLookupHint;
import org.netbeans.spi.project.ActionProvider;
import org.netbeans.spi.project.ui.LogicalViewProvider;
import org.netbeans.spi.project.ui.support.CommonProjectActions;
import org.openide.ErrorManager;
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
import org.openide.util.WeakListeners;
import org.openide.util.lookup.Lookups;
import org.openide.util.lookup.ProxyLookup;

/**
 *
 * @author Jan Lahoda
 */
public class LaTeXGUIProject implements Project, ProjectInformation, LogicalViewProvider, ActionProvider, LaTeXSource.DocumentChangedListener {
    
    private FileObject dir;
    private FileObject masterFile;
    private Lookup lookup;
    
    private LaTeXSource source;
    
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
        lookup = Lookups.fixed(new Object[] {
            this,
            GenericSources.genericOnly(this),
            source,
            new LaTeXGUIProjectOpenedHookImpl(this),
            new LaTeXAuxiliaryConfigurationImpl(this),
            new LaTeXGUIProjectCustomizer(this),
        });
    }
    
    public Lookup getLookup() {
        return lookup;
    }
    
    public FileObject getProjectDirectory() {
        return dir;
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
    
    /*package private*/FileObject getMasterFile() {
        return masterFile;
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
        if (root.getLookup().lookup(LaTeXGUIProject.class) != this)
            return null;
        
        Lookup.Template sourceNodeTagTemplate = new Lookup.Template(null, null, SOURCE_NODE_TAG);
        Node[] secondLevelNodes = root.getChildren().getNodes(true);
        
        for (int secondLevelCntr = 0; secondLevelCntr < secondLevelNodes.length; secondLevelCntr++) {
            if (secondLevelNodes[secondLevelCntr].getLookup().lookup(sourceNodeTagTemplate).allInstances().size() > 0) { //!!!
                Node[] files = secondLevelNodes[secondLevelCntr].getChildren().getNodes(true);
                Lookup.Template searchingTemplate= new Lookup.Template(null, null, target);
                
                for (int cntr = 0; cntr < files.length; cntr++) {
                    if (files[cntr].getLookup().lookup(searchingTemplate).allInstances().size() > 0)
                        return files[cntr];
                }
            }
        }
        
        return null;
    }
    
    public LaTeXSource getSource() {
        return source;
    }
    
    private static final String SOURCE_NODE_NAME = "Sources";
    private static final Object SOURCE_NODE_TAG  = new Object();
    
    private Node createSourcesNode() {
        AbstractNode an = new AbstractNode(new LaTeXChildren(), Lookups.singleton(SOURCE_NODE_TAG));
        
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
//        System.err.println("invoked: " + command);
        if (COMMAND_BUILD.equals(command)) {
            ActionsFactory.build(this, /*"build"*/ProjectSettings.getDefault(this).getDefaultBuildCommand());
            return ;
        }
        if (COMMAND_SHOW.equals(command)) {
            ActionsFactory.build(this, /*"show"*/ProjectSettings.getDefault(this).getDefaultShowCommand());
            return ;
        }
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
//            System.err.println("file=" + file);
//            
//            for (Iterator i = containedFilesCache.iterator(); i.hasNext(); ) {
//                FileObject actualFile = (FileObject) i.next();
//                
//                System.err.println("testing file=" + actualFile + ": equals=" + actualFile.equals(file) + ", toFile.equals=" + FileUtil.toFile(actualFile).equals(FileUtil.toFile(file)));
//            }
            
            return containedFilesCache.contains(file);
        }
    }
    
    public boolean isActionEnabled(String command, Lookup context) throws IllegalArgumentException {
        return true;//TODO:....
    }

    private synchronized void updateContainedFilesCache() {
        containedFilesCache = new HashSet(source.getDocument().getFiles());
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
    
    private static class LaTeXGUIProjectNode extends AbstractNode implements Runnable, FileStatusListener, ChangeListener, PropertyChangeListener {
        
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
            super(project.createChildren(), Lookups.fixed(new Object[] {project, NAVIGATOR_HINT, project.source}));
            setDisplayName(project.getDisplayName());
            setIconBase("org/netbeans/modules/latex/guiproject/resources/latex_gui_project_icon");
            setProjectFiles(project);
        }
        
        public Action[] getActions(boolean context) {
            List actions = new ArrayList();
            
            actions.add(ActionsFactory.createShowAction());
            actions.add(null);
            actions.add(ActionsFactory.createBuildAction());
            actions.add(null);
            actions.add(CommonProjectActions.setAsMainProjectAction());
            actions.add(CommonProjectActions.closeProjectAction());
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
            
            actions.add(CommonProjectActions.customizeProjectAction());
            
            return (Action []) actions.toArray(new Action[actions.size()]);
        }

        
        protected final void setProjectFiles(Project project) {
            Sources sources = ProjectUtils.getSources(project);  // returns singleton
            if (sourcesListener == null) {
                sourcesListener = WeakListeners.change(this, sources);
                sources.addChangeListener(sourcesListener);
            }
            setGroups(Arrays.asList(sources.getSourceGroups(Sources.TYPE_GENERIC)));
        }
        
        private final void setGroups(Collection groups) {
            if (groupsListeners != null) {
                Iterator it = groupsListeners.keySet().iterator();
                while (it.hasNext()) {
                    SourceGroup group = (SourceGroup) it.next();
                    PropertyChangeListener pcl = (PropertyChangeListener) groupsListeners.get(group);
                    group.removePropertyChangeListener(pcl);
                }
            }
            groupsListeners = new HashMap();
            Set roots = new HashSet();
            Iterator it = groups.iterator();
            while (it.hasNext()) {
                SourceGroup group = (SourceGroup) it.next();
                PropertyChangeListener pcl = WeakListeners.propertyChange(this, group);
                groupsListeners.put(group, pcl);
                group.addPropertyChangeListener(pcl);
                FileObject fo = group.getRootFolder();
                roots.add(fo);
            }
            setFiles(roots);
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

        // sources change
        public void stateChanged(ChangeEvent e) {
            setProjectFiles(project);
        }

        // group change
        public void propertyChange(PropertyChangeEvent evt) {
            setProjectFiles(project);
         }

    }
    
    
    private class LaTeXChildren extends Children.Keys implements LaTeXSource.DocumentChangedListener/*TODO: Weak?*/ {
        
        public void addNotify() {
            getSource().addDocumentChangedListener(this);
            doSetKeys();
        }
        
        private void doSetKeys() {
//            Thread.dumpStack();
            DocumentNode dn = getSource().getDocument();
            List toAdd = new ArrayList();
            
            if (dn != null)
                toAdd.addAll(dn.getFiles());
            
            Object main = getSource().getMainFile();
            
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
    
}
