/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2004 Sun
 * Microsystems, Inc. All Rights Reserved.
 */


package org.netbeans.modules.tasklist.docscan;

import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.lookup.Lookups;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.netbeans.api.project.*;
import org.netbeans.api.project.ui.OpenProjects;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Collections;

/**
 * Utility class defining project chooser.
 *
 * @author Petr Kuzel
 */
class Choosers {
    // handle select folder life-time
    public static Node icons = null;

    /**
     * Logical view over opened projects
     */
    public static Node projectView() {

        Children.SortedArray kids = new Children.SortedArray();
        kids.setComparator(new Comparator() {
            public int compare(Object o1, Object o2) {
                return ((Node) o1).getDisplayName().compareToIgnoreCase(((Node) o2).getDisplayName());
            }
        });

        Project[] projects = OpenProjects.getDefault().getOpenProjects();
        for (int pi = 0; pi < projects.length; pi++) {
            Project project = projects[pi];
            Sources sources = ProjectUtils.getSources(project);
            SourceGroup[] group = sources.getSourceGroups(Sources.TYPE_GENERIC);

            if (group.length == 0) continue;

            // here we work with assumption that if project has only one
            // source group it is project folder itself and that the project
            // folder source group is named by the project

            if (group.length > 1) {
                kids.add(new Node[] {new ProjectNode(project)});
            } else {
                FileObject folder = group[0].getRootFolder();
                if (folder.isFolder() == false) continue;
                kids.add(new Node[]{new FolderNode(folder, group[0])});
                prepareFolderIcons(folder);
            }
        }

        final Node content = new AbstractNode(kids) {
            public void setName(String name) {
                super.setName(name);
                super.setIconBase("org/netbeans/modules/tasklist/docscan/repository");  // NOI18N
            }
        };

        content.setName(Util.getString("projects"));
        return content;
    }

    /** Hack no way how to get L&F specifics folder icons. Get it from random folder node. */
    private static void prepareFolderIcons(FileObject fo) {
        if (icons == null) {
            try {
                DataObject dobj = DataObject.find(fo);
                icons = dobj.getNodeDelegate();
            } catch (DataObjectNotFoundException e) {
                // ignore
            }
        }
    }

    /**
     * Used for project with multiple generic source groups
     */
    public static class ProjectNode extends AbstractNode {

        private final Project project;

        public ProjectNode(Project project) {
            this(new Children.SortedArray(), project);
        }

        private ProjectNode(Children.SortedArray children, Project project) {
            super(children);
            this.project = project;

            children.setComparator(new Comparator() {
                public int compare(Object o1, Object o2) {
                    return ((Node) o1).getDisplayName().compareToIgnoreCase(((Node) o2).getDisplayName());
                }
            });

            Sources sources = ProjectUtils.getSources(project);
            SourceGroup[] group = sources.getSourceGroups(Sources.TYPE_GENERIC);
            Arrays.sort(group, new Comparator() {
                public int compare(Object o1, Object o2) {
                    return ((SourceGroup) o1).getDisplayName().compareToIgnoreCase(((SourceGroup) o2).getDisplayName());
                }
            });

            for (int i = 0; i < group.length; i++) {
                FileObject folder = group[i].getRootFolder();
                if (folder.isFolder() == false) continue;
                children.add(new Node[]{new FolderNode(folder, group[i])});
                prepareFolderIcons(folder);
            }
        }

        public String getDisplayName() {
            ProjectInformation pi = getProjectInformation();
            return pi != null ? pi.getDisplayName() : FileUtil.getFileDisplayName(project.getProjectDirectory());
        }

        public Image getIcon(int type) {
            ProjectInformation pi = getProjectInformation();
            if (pi != null) {
                return convertIconToImage(pi.getIcon());
            }
            if (icons != null) {
                return icons.getIcon(type);
            } else {
                return super.getIcon(type);
            }
        }

        public Image getOpenedIcon(int type) {
            return getIcon(type);
        }

        private ProjectInformation getProjectInformation() {
            return (ProjectInformation) project.getLookup().lookup(ProjectInformation.class);
        }

    }

    /**
     * Visualizes folder structure.
     */
    public static class FolderNode extends AbstractNode {

        private final FileObject fileObject;
        private SourceGroup group;

        public FolderNode(FileObject fileObject, SourceGroup root) {
            super(new FolderContent(fileObject, root), Lookups.singleton(fileObject));
            this.fileObject = fileObject;
            group = root;
        }

        public FolderNode(FileObject fileObject) {
            super(new FolderContent(fileObject), Lookups.singleton(fileObject));
            this.fileObject = fileObject;
        }

        public String getDisplayName() {
            if (group != null) {
                return group.getDisplayName();
            } else {
                return fileObject.getName();
            }
        }

        public Image getIcon(int type) {

            if (group != null) {
                Icon icon  = group.getIcon(false);
                if (icon != null) {
                    return convertIconToImage(icon);
                }
            }

            // XXX how to dynamically get icon (that is subject to L&F)
            if (icons != null) {
                return icons.getIcon(type);
            } else {
                return super.getIcon(type);
            }
        }

        public Image getOpenedIcon(int type) {

            if (group != null) {
                Icon icon  = group.getIcon(true);
                if (icon != null) {
                    return convertIconToImage(icon);
                }
            }

            // XXX how to dynamically get icon (that is subject to L&F)
            if (icons != null) {
                return icons.getOpenedIcon(type);
            } else {
                return super.getOpenedIcon(type);
            }
        }

        private static class FolderContent extends Children.Keys {

            private final FileObject fileObject;
            private final SourceGroup group;

            public FolderContent(FileObject fileObject) {
                this(fileObject, null);
            }

            public FolderContent(FileObject fileObject, SourceGroup group) {
                this.fileObject = fileObject;
                this.group = group;
            }

            protected void addNotify() {
                FileObject[] fo = fileObject.getChildren();
                Arrays.sort(fo, new Comparator() {
                    public int compare(Object o1, Object o2) {
                        return ((FileObject) o1).getNameExt().compareToIgnoreCase(((FileObject) o2).getNameExt());
                    }
                });
                setKeys(Arrays.asList(fo));
            }

            protected void removeNotify() {
                setKeys(Collections.EMPTY_SET);
            }

            protected Node[] createNodes(Object key) {
                FileObject fo = (FileObject) key;
                if (fo.isFolder() && (group == null || group.contains(fo))) {
                    return new Node[]{new FolderNode(fo)};
                } else {
                    return new Node[0];
                }
            }
        }
    }


    private static Component CONVERTOR_COMPONENT = new Panel();

    /**
     * Converts Icon to Image
     */
    private static Image convertIconToImage(Icon icon) {

        if ( icon instanceof ImageIcon ) {
            return ((ImageIcon)icon).getImage();
        }

        int height = icon.getIconHeight();
        int width = icon.getIconWidth();

        BufferedImage bImage = new BufferedImage( width, height, BufferedImage.TYPE_INT_ARGB );
        icon.paintIcon( CONVERTOR_COMPONENT, bImage.getGraphics(), 0, 0 );

        return bImage;
    }
}
