/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at http://www.netbeans.org/cddl.html
 * or http://www.netbeans.org/cddl.txt.
 *
 * When distributing Covered Code, include this CDDL Header Notice in each file
 * and include the License file at http://www.netbeans.org/cddl.txt.
 * If applicable, add the following below the CDDL Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 */
package org.netbeans.modules.sfsexplorer;

import java.awt.event.ActionEvent;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.Action;
import org.netbeans.modules.sfsexplorer.ShowURLActionFactory.ShowURLAction;
import org.openide.cookies.EditorCookie;
import org.openide.cookies.OpenCookie;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileStateInvalidException;
import org.openide.filesystems.MultiFileSystem;
import org.openide.filesystems.Repository;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.nodes.FilterNode;
import org.openide.nodes.Node;
import org.openide.nodes.PropertySupport;
import org.openide.nodes.PropertySupport.ReadOnly;
import org.openide.util.NbBundle;

/**
 * 
 * @author Sandip V. Chitale (Sandip.Chitale@Sun.Com)
 */
class SFSNode extends FilterNode {
    private String platform;
    private Action[] actions;

    /**
     * 
     * @param node 
     * @param platform 
     */
    SFSNode(Node node, String platform) {
        super(node, new SFSNodeChildren(node, platform));
        this.platform = platform;
    }

    /**
     * 
     * @param context 
     * @return 
     */
    public Action[] getActions(boolean context) {
        if (!context) {
            if (actions == null) {
                Node node = SFSNode.this.getOriginal();
                return getActions(node);
            }
            return actions;
        }
        return SFSBrowserTopComponent.EMPTY_ACTIONS;
    }

    /**
     * 
     * @return 
     */
    public Node getOriginal() {
        return super.getOriginal();
    }

    /**
     * 
     * @param node 
     * @return 
     */
    private Action[] getActions(Node node) {
        List<Action> actions = new LinkedList<Action>();
        MultiFileSystem multiFileSystem = (MultiFileSystem) Repository.getDefault().getDefaultFileSystem();
        FileObject root = multiFileSystem.getRoot();
        SFSBrowserTopComponent.collectActions(node, actions, platform, root);
        DataObject dataObject = node.getLookup().lookup(DataObject.class);
        if (dataObject != null) {
            FileObject fileObject = dataObject.getPrimaryFile();
            if (fileObject != null) {
                URL url = null;
                if (fileObject.getExt().equals("instance")) { // NOI18N
                    url = SFSBrowserTopComponent.getURL("http://wiki.netbeans.org/wiki/view/DevFaqInstanceDataObject"); // NOI18N
                    if (url != null) {
                        actions.add(
                                new ShowURLAction(NbBundle.getMessage(SFSNode.class, "FAQ_on_.instance_files"), url)); // TODO cache this
                    }
                }  else                    if (fileObject.getExt().equals("settings")) { // NOI18N
                        url = SFSBrowserTopComponent.getURL("http://wiki.netbeans.org/wiki/view/DevFaqDotSettingsFiles"); // NOI18N
                        if (url != null) {
                            actions.add(
                                    new ShowURLAction(NbBundle.getMessage(SFSNode.class, "FAQ_on_.settings_files"), url)); // TODO cache this
                        }
                    }  else                        if (fileObject.getExt().equals("shadow")) { // NOI18N
                            url = SFSBrowserTopComponent.getURL("http://wiki.netbeans.org/wiki/view/DevFaqDotShadowFiles"); // NOI18N
                            if (url != null) {
                                actions.add(
                                        new ShowURLAction(NbBundle.getMessage(SFSNode.class, "FAQ_on_.settings_files"), url)); // TODO cache this
                            }

                            String originalFile = String.valueOf(fileObject.getAttribute("originalFile")); // NOI18N
                            if (originalFile != null && originalFile.endsWith(".instance")) { // NOI18N
                                final String originalFileSansExt = originalFile.substring(0, originalFile.lastIndexOf(".instance")); // NOI18N
                                actions.add(new AbstractAction(NbBundle.getMessage(SFSNode.class, "Go_to_original_file_") + originalFileSansExt) {
                                    public void actionPerformed(ActionEvent e) {
                                        SFSBrowserTopComponent.select(originalFileSansExt);
                                    }
                            });
                            }
                        }
                List delegates = XMLFileSystemCache.getDelegates(multiFileSystem, fileObject);
                if (delegates.size() > 0) {
                    FileObject delegateFileObject = (FileObject) delegates.get(0);
                    if (delegateFileObject.isValid()) {
                        try {
                            DataObject delegateDataObject = DataObject.find(delegateFileObject);
                            if (delegateDataObject != null && (delegateDataObject.getCookie(OpenCookie.class)  != null || delegateDataObject.getCookie(EditorCookie.class)  != null)) {
                                try {
                                    actions.add(new SFSBrowserTopComponent.OpenDelegateAction(delegateFileObject, delegateFileObject.getFileSystem().getDisplayName()));
                                } catch (FileStateInvalidException ex) {
                                    ex.printStackTrace();
                                }
                            }
                        }  catch (DataObjectNotFoundException ex) {
                            ex.printStackTrace();
                        }
                    }
                }
            }
        }
        URL url = SFSBrowserTopComponent.getURL("http://www.netbeans.org/download/dev/javadoc/org-openide-filesystems/org/openide/filesystems/doc-files/api.html");
        if (url != null) {
            actions.add(new ShowURLAction(NbBundle.getMessage(SFSNode.class, "FileSystem_API_Details"), url));
        }
        url = null;
        if ("platform6".equals(platform)) {
            url = SFSBrowserTopComponent.getURL("http://www.netbeans.org/download/5_5/javadoc/org-openide-filesystems/org/openide/filesystems/XMLFileSystem.html");
        } else if ("platform7".equals(platform)) {
            url = SFSBrowserTopComponent.getURL("http://www.netbeans.org/download/6_0/javadoc/org-openide-filesystems/org/openide/filesystems/XMLFileSystem.html");

        }
        if (url != null) {
            actions.add(new ShowURLAction(NbBundle.getMessage(SFSNode.class, "XML_FileSystem_API_Details"), url));
        }

        return (Action[]) actions.toArray(SFSBrowserTopComponent.EMPTY_ACTIONS);
    }
    
    /**
     * 
     * @return 
     */
    public Node.PropertySet[] getPropertySets() {
        Node node = SFSNode.this.getOriginal();
        DataObject dataObject = (DataObject) node.getLookup().lookup(DataObject.class);
        PropertySet[] origS = super.getPropertySets();
        if (dataObject != null) {
            final FileObject fileObject = dataObject.getPrimaryFile();
            if (fileObject != null) {
                
                PropertySet[] newS = new PropertySet[origS.length+1];
                PropertySet a = new PropertySet(NbBundle.getMessage(SFSNode.class, "LayerSet"), NbBundle.getMessage(SFSNode.class, "Layer"), NbBundle.getMessage(SFSNode.class, "This_set_contains_information_from_which_layer_the_file_originates.")) {
                        public Node.Property[] getProperties() {
                            return new Node.Property[] {
                                new PropertySupport.ReadOnly<String>(NbBundle.getMessage(SFSNode.class, "name"), String.class, NbBundle.getMessage(SFSNode.class, "Origin"), 
                                        NbBundle.getMessage(SFSNode.class, "Shows_which_layer(s)_defines_this_file/folder.")) {
                                    public String getValue() {
                                        return XMLFileSystemCache.getInstance().getModuleName(fileObject);
                                    }
                                }
                            };
                        }
                    };
                newS[origS.length] = a;
                System.arraycopy(origS, 0, newS, 0, origS.length);
                return newS;
            }
        }
        return origS;
    }
}