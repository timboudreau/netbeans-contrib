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

package org.netbeans.modules.groovy.groovyproject.ui;

import java.awt.Component;
import java.awt.Image;
import java.awt.Panel;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.StringTokenizer;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;
import org.netbeans.api.project.SourceGroup;
import org.netbeans.api.queries.VisibilityQuery;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.ChangeableDataFilter;
import org.openide.loaders.DataFilter;
import org.openide.loaders.DataFolder;
import org.openide.loaders.DataObject;
import org.openide.nodes.FilterNode;
import org.openide.nodes.Node;
import org.openide.nodes.NodeNotFoundException;
import org.openide.nodes.NodeOp;
import org.openide.util.Lookup;
import org.openide.util.WeakListeners;
import org.openide.util.lookup.Lookups;
import org.openide.util.lookup.ProxyLookup;

// XXX need unit test

/**
 * Displays a package root in a tree.
 * @see "#42151"
 * @author Jesse Glick
 */
final class TreeRootNode extends FilterNode implements PropertyChangeListener {
    
    private static final DataFilter VISIBILITY_QUERY_FILTER = new VisibilityQueryDataFilter();
    
    private final SourceGroup g;
    
    public TreeRootNode(SourceGroup g) {
        this(DataFolder.findFolder(g.getRootFolder()), g);
    }
    
    private TreeRootNode(DataFolder folder, SourceGroup g) {
        super(folder.getNodeDelegate(),
            folder.createNodeChildren(VISIBILITY_QUERY_FILTER),
            new ProxyLookup(new Lookup[] {
                folder.getNodeDelegate().getLookup(),
                Lookups.singleton(new PathFinder(g)),
                // no need for explicit search info
            }));
        this.g = g;
        g.addPropertyChangeListener(WeakListeners.propertyChange(this, g));
    }

    /** Copied from PackageRootNode with modifications. */
    private Image computeIcon(boolean opened, int type) {
        Icon icon = g.getIcon(opened);
        Image image = null;
        if (icon == null) {
            image = opened ? super.getOpenedIcon(type) : super.getIcon(type);
        } else {
            if (icon instanceof ImageIcon) {
                image = ((ImageIcon) icon).getImage();
            } else {
                image = icon2image(icon);
            }
        }
        return image;
    }
    
    private static Component CONVERTOR_COMPONENT = new Panel();    
    private static Image icon2image(Icon icon) {
        int height = icon.getIconHeight();
        int width = icon.getIconWidth();
        
        BufferedImage bImage = new BufferedImage( width, height, BufferedImage.TYPE_INT_ARGB );
        icon.paintIcon( CONVERTOR_COMPONENT, bImage.getGraphics(), 0, 0 );
        
        return bImage;
    }
    
    public Image getIcon(int type) {
        return computeIcon(false, type);
    }

    public Image getOpenedIcon(int type) {
        return computeIcon(true, type);
    }

    public String getName() {
        return g.getName();
    }

    public String getDisplayName() {
        return g.getDisplayName();
    }

    public boolean canRename() {
        return false;
    }

    public boolean canDestroy() {
        return false;
    }

    public boolean canCut() {
        return false;
    }

    public void propertyChange(PropertyChangeEvent ev) {
        // XXX handle SourceGroup.rootFolder change too
        fireNameChange(null, null);
        fireDisplayNameChange(null, null);
        fireIconChange();
        fireOpenedIconChange();
    }
    
    /** Copied from PhysicalView and PackageRootNode. */
    public static final class PathFinder {
        
        private final SourceGroup g;
        
        PathFinder(SourceGroup g) {
            this.g = g;
        }
        
        public Node findPath(Node rootNode, Object o) {
            FileObject fo;
            if (o instanceof FileObject) {
                fo = (FileObject) o;
            } else if (o instanceof DataObject) {
                fo = ((DataObject) o).getPrimaryFile();
            } else {
                return null;
            }
            FileObject groupRoot = g.getRootFolder();
            if (FileUtil.isParentOf(groupRoot, fo) /* && group.contains(fo) */) {
                String relPath = FileUtil.getRelativePath(groupRoot, fo);
                List/*<String>*/ path = new ArrayList();
                StringTokenizer strtok = new StringTokenizer(relPath, "/"); // NOI18N
                while (strtok.hasMoreTokens()) {
                   path.add(strtok.nextToken());
                }
                // XXX this is really ugly... cf. #44739 and #33330.
                path.set(path.size() - 1, fo.getName());
                
                try {
                    return NodeOp.findPath(rootNode, Collections.enumeration(path));
                } catch (NodeNotFoundException e) {
                    return null;
                }
            } else if (groupRoot.equals(fo)) {
                return rootNode;
            } else {
                return null;
            }
        }
        
    }
    
    /** Copied from PhysicalView. */
    private static final class VisibilityQueryDataFilter implements ChangeListener, ChangeableDataFilter {
        
        private static final long serialVersionUID = 1L; // in case a DataFolder.ClonedFilterHandle saves me
        
        private final EventListenerList ell = new EventListenerList();
        
        public VisibilityQueryDataFilter() {
            VisibilityQuery.getDefault().addChangeListener(this);
        }
        
        public boolean acceptDataObject(DataObject obj) {
            FileObject fo = obj.getPrimaryFile();
            return VisibilityQuery.getDefault().isVisible(fo);
        }
        
        public void stateChanged(ChangeEvent e) {
            Object[] listeners = ell.getListenerList();
            ChangeEvent event = null;
            for (int i = listeners.length - 2; i >= 0; i -= 2) {
                if (listeners[i] == ChangeListener.class) {
                    if (event == null) {
                        event = new ChangeEvent(this);
                    }
                    ((ChangeListener) listeners[i+1]).stateChanged(event);
                }
            }
        }
        
        public void addChangeListener(ChangeListener listener) {
            ell.add(ChangeListener.class, listener);
        }
        
        public void removeChangeListener(ChangeListener listener) {
            ell.remove(ChangeListener.class, listener);
        }
        
    }
}
