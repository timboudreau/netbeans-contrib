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
 * Contributor(s): Tim Boudreau
 * 
 * Portions Copyrighted 2007 Sun Microsystems, Inc.
 */

package org.netbeans.api.dynactions;

import java.util.Collection;
import javax.swing.JComponent;
import javax.swing.JMenuItem;
import org.netbeans.api.objectloader.ObjectLoader;
import org.netbeans.api.objectloader.ObjectReceiver;
import org.netbeans.api.progress.ProgressHandle;
import org.netbeans.api.progress.ProgressHandleFactory;
import org.openide.awt.Actions;
import org.openide.awt.DynamicMenuContent;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.actions.Presenter;

/**
 * Action which is selectively visible or invisible depending on the presence
 * or absence of an instance of ObjectLoader inside a Lookup.  If an ObjectLoader
 * is present, the type of object it will load also must be assignable to
 * the type of this action for this action to be visible.
 *
 * @author Tim Boudreau
 */
public abstract class ObjectLoaderAction<T> extends GenericContextSensitiveAction<ObjectLoader> implements Presenter.Popup {
    private final Class<T> type;
    protected ObjectLoaderAction(Class<T> type) {
        super (ObjectLoader.class);
        this.type = type;
    }
    
    protected ObjectLoaderAction(Lookup lkp, Class<T> type) {
        super (lkp, ObjectLoader.class);
        this.type = type;
    }

    @Override
    protected final void performAction(ObjectLoader ldr) {
        assert ldr != null;
        ldr.get(new R());
    }
    
    protected abstract void performed (T t);
    protected abstract String getLoadingMessage();

    @Override
    protected boolean checkEnabled(Collection<? extends ObjectLoader> coll, Class clazz) {
        boolean result = super.checkEnabled(coll, clazz);
        if (result && this.type != null) { //will be null in superclass constructor
            for (ObjectLoader ldr : coll) {
                Class other = ldr.type();
                result |= (this.type.isAssignableFrom(other));
                if (result) {
                    break;
                }
            }
        }
        return result;
    }
    
    protected void loadFailed (Exception e) {
        Exceptions.printStackTrace(e);
    }
    
    private class JMI extends JMenuItem implements DynamicMenuContent {
        public JComponent[] getMenuPresenters() {
            if (ObjectLoaderAction.this.isEnabled()) {
                return new JComponent[] { this };
            } else {
                return new JComponent[0];
            }
        }

        public JComponent[] synchMenuPresenters(JComponent[] items) {
            return getMenuPresenters();
        }
    }
    
    protected JMenuItem createPresenter() {
        JMenuItem result = new JMI();
        Actions.connect(result, this, true);
        return result;
    }

    public final JMenuItem getPopupPresenter() {
        return createPresenter();
    }    
    
    private class R implements ObjectReceiver<T> {
        private ProgressHandle handle;
        public void received(T t) {
            if (handle != null) {
                handle.finish();
            }
            performed (t);
        }

        public void failed(Exception e) {
            handle.finish();
            ObjectLoaderAction.this.loadFailed (e);
        }

        public void setSynchronous(boolean val) {
            if (!val) {
                handle = ProgressHandleFactory.createHandle(getLoadingMessage());
                handle.start();
            }
        }
    }
}
