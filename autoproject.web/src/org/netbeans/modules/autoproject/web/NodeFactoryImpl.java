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

package org.netbeans.modules.autoproject.web;

import java.util.ArrayList;
import java.util.List;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectUtils;
import org.netbeans.api.project.SourceGroup;
import org.netbeans.api.project.Sources;
import org.netbeans.spi.project.ui.support.NodeFactory;
import org.netbeans.spi.project.ui.support.NodeList;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.nodes.Node;
import org.openide.util.ChangeSupport;
import org.openide.util.Exceptions;

/**
 * Displays source roots etc. for the project.
 */
public class NodeFactoryImpl implements NodeFactory {

    /** public for layer */
    public NodeFactoryImpl() {}

    public NodeList<?> createNodes(Project p) {
        return new SourceChildren(p);
    }

    private class SourceChildren implements NodeList<SourceGroup>, ChangeListener {

        private final Project p;
        private final Sources src;
        private final ChangeSupport cs = new ChangeSupport(this);

        public SourceChildren(Project p) {
            this.p = p;
            src = ProjectUtils.getSources(p);
        }

        public void addNotify() {
            src.addChangeListener(this);
        }

        public void removeNotify() {
            src.removeChangeListener(this);
        }

        public Node node(SourceGroup key) {
            try {
                return DataObject.find(key.getRootFolder()).getNodeDelegate().cloneNode();
            } catch (DataObjectNotFoundException ex) {
                Exceptions.printStackTrace(ex);
                return null;
            }
        }

        public void stateChanged(ChangeEvent e) {
            cs.fireChange();
        }

        public List<SourceGroup> keys() {
            List<SourceGroup> keys = new ArrayList<SourceGroup>();
            for (SourceGroup g : src.getSourceGroups(SourcesImpl.SOURCES_TYPE_DOCROOT)) {
                keys.add(g);
            }
            for (SourceGroup g : src.getSourceGroups(SourcesImpl.SOURCES_TYPE_WEBINF)) {
                keys.add(g);
            }
            return keys;
        }

        public void addChangeListener(ChangeListener l) {
            cs.addChangeListener(l);
        }

        public void removeChangeListener(ChangeListener l) {
            cs.removeChangeListener(l);
        }

    }

}
