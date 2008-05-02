/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2008 Sun Microsystems, Inc. All rights reserved.
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
 *
 * Contributor(s):
 *
 * Portions Copyrighted 2008 Sun Microsystems, Inc.
 */

package org.netbeans.modules.autoproject.java;

import java.awt.Image;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.netbeans.api.java.project.JavaProjectConstants;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectUtils;
import org.netbeans.api.project.SourceGroup;
import org.netbeans.api.project.Sources;
import org.netbeans.spi.java.project.support.ui.PackageView;
import org.netbeans.spi.project.ui.LogicalViewProvider;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;
import org.openide.util.Union2;
import org.openide.util.Utilities;
import org.openide.util.lookup.Lookups;

/**
 * Displays source roots etc. for the project.
 */
class LogicalViewImpl implements LogicalViewProvider {

    private final Project p;

    public LogicalViewImpl(Project p) {
        this.p = p;
    }

    public Node createLogicalView() {
        // XXX use NodeFactorySupport, ProjectNodeWrapper, etc.; see stuff in ant.freeform
        return new AbstractNode(new SourceChildren(), Lookups.singleton(p)) {
            @Override
            public String getDisplayName() {
                return ProjectUtils.getInformation(p).getDisplayName();
            }
            @Override
            public Image getIcon(int type) {
                return Utilities.icon2Image(ProjectUtils.getInformation(p).getIcon());
            }
            @Override
            public Image getOpenedIcon(int type) {
                return getIcon(type);
            }
            // XXX context menu should show usual stuff, plus Ant targets
        };
    }

    private class SourceChildren extends Children.Keys<Union2<SourceGroup,FileObject>> implements ChangeListener {

        private final Sources src = ProjectUtils.getSources(p);

        @Override
        protected void addNotify() {
            refreshKeys();
            src.addChangeListener(this);
        }

        @Override
        protected void removeNotify() {
            setKeys(Collections.<Union2<SourceGroup,FileObject>>emptySet());
            src.removeChangeListener(this);
        }

        @Override
        protected Node[] createNodes(Union2<SourceGroup,FileObject> key) {
            if (key.hasFirst()) {
                return new Node[] {PackageView.createPackageView(key.first())};
            } else {
                try {
                    return new Node[] {DataObject.find(key.second()).getNodeDelegate().cloneNode()};
                } catch (DataObjectNotFoundException ex) {
                    Exceptions.printStackTrace(ex);
                    return null;
                }
            }
        }

        public void stateChanged(ChangeEvent e) {
            refreshKeys();
        }

        private void refreshKeys() {
            List<Union2<SourceGroup, FileObject>> keys = new ArrayList<Union2<SourceGroup, FileObject>>();
            for (SourceGroup g : src.getSourceGroups(JavaProjectConstants.SOURCES_TYPE_JAVA)) {
                keys.add(Union2.<SourceGroup, FileObject>createFirst(g));
            }
            FileObject f = p.getProjectDirectory().getFileObject("build.xml");
            if (f != null) {
                keys.add(Union2.<SourceGroup, FileObject>createSecond(f));
            }
            setKeys(keys);
        }

    }

    public Node findPath(Node root, Object target) {
        return null;// XXX
    }

}
