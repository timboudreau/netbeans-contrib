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

package org.netbeans.modules.ada.project;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.netbeans.api.project.FileOwnerQuery;
import org.netbeans.api.project.ProjectManager;
import org.netbeans.api.project.SourceGroup;
import org.netbeans.api.project.Sources;
import org.netbeans.spi.project.support.ant.AntProjectHelper;
import org.netbeans.spi.project.support.ant.PropertyEvaluator;
import org.netbeans.spi.project.support.ant.SourcesHelper;
import org.openide.util.ChangeSupport;
import org.openide.util.Mutex;

/**
 *
 * @author Andrea Lucarelli
 */
public class AdaSources implements Sources, ChangeListener, PropertyChangeListener {

    public static final String SOURCES_TYPE_ADA = "ADASOURCE"; // NOI18N

    private final ChangeSupport changeSupport;
    private final AntProjectHelper helper;
    private final PropertyEvaluator evaluator;
    private final SourceRoots sourceRoots;
    private final SourceRoots testRoots;
    private Sources delegate;
    private SourcesHelper sourcesHelper;
    private boolean externalRootsRegistered;

    public AdaSources (final AntProjectHelper helper, final PropertyEvaluator eval, final SourceRoots sources, final SourceRoots tests) {
        assert helper != null;
        assert eval != null;
        assert sources != null;
        assert tests != null;
        this.helper = helper;
        this.evaluator = eval;
        this.sourceRoots = sources;
        this.testRoots = tests;
        this.changeSupport = new ChangeSupport(this);
        this.sourceRoots.addPropertyChangeListener(this);
        this.testRoots.addPropertyChangeListener(this);        
        this.evaluator.addPropertyChangeListener(this);
        initSources();
    }
    
    

    public SourceGroup[] getSourceGroups(final String type) {
        return ProjectManager.mutex().readAccess(new Mutex.Action<SourceGroup[]>() {
            public SourceGroup[] run() {
                Sources _delegate;
                synchronized (AdaSources.this) {
                    if (delegate == null) {                    
                        delegate = initSources();
                        delegate.addChangeListener(AdaSources.this);
                    }
                    _delegate = delegate;
                }
                SourceGroup[] groups = _delegate.getSourceGroups(type);                
                return groups;
            }
        });
    }    
    
    private Sources initSources() {
        this.sourcesHelper = new SourcesHelper(helper, evaluator);   //Safe to pass APH        
        register(sourceRoots);
        register(testRoots);
        externalRootsRegistered = false;
        ProjectManager.mutex().postWriteRequest(new Runnable() {
            public void run() {                
                if (!externalRootsRegistered) {
                    sourcesHelper.registerExternalRoots(FileOwnerQuery.EXTERNAL_ALGORITHM_TRANSIENT);
                    externalRootsRegistered = true;
                }
            }
        });
        return this.sourcesHelper.createSources();
    }

    private void register(SourceRoots roots) {
        String[] propNames = roots.getRootProperties();
        String[] rootNames = roots.getRootNames();
        for (int i = 0; i < propNames.length; i++) {
            String prop = propNames[i];
            String displayName = roots.getRootDisplayName(rootNames[i], prop);
            String loc = "${" + prop + "}"; // NOI18N
            sourcesHelper.addPrincipalSourceRoot(loc, displayName, null, null); // NOI18N
            sourcesHelper.addTypedSourceRoot(loc, SOURCES_TYPE_ADA, displayName, null, null);
        }
    }

    public void addChangeListener(ChangeListener changeListener) {
        changeSupport.addChangeListener(changeListener);
    }

    public void removeChangeListener(ChangeListener changeListener) {
        changeSupport.removeChangeListener(changeListener);
    }

    private void fireChange() {
        synchronized (this) {
            if (delegate != null) {
                delegate.removeChangeListener(this);
                delegate = null;
            }
        }
        changeSupport.fireChange();
    }
    
    public void propertyChange(PropertyChangeEvent evt) {
        String propName = evt.getPropertyName();
        if (SourceRoots.PROP_ROOT_PROPERTIES.equals(propName)) {
            this.fireChange();
        }
    }
    
    public void stateChanged (ChangeEvent event) {
        this.fireChange();
    }
    

}
