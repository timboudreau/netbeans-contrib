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
 * Portions Copyrighted 2007 Sun Microsystems, Inc.
 */

package org.netbeans.api.dynactions;

import javax.swing.JComponent;
import javax.swing.JMenuItem;
import org.openide.awt.Actions;
import org.openide.awt.DynamicMenuContent;
import org.openide.util.Lookup;
import org.openide.util.actions.Presenter;

/**
 * A context-sensitive action which is invisible in popup menus when it
 * is disabled.
 * 
 * @author Tim Boudreau
 */
public abstract class DynamicAction<T> extends GenericContextSensitiveAction<T> implements Presenter.Popup {
    protected DynamicAction (Lookup lkp, Class<T> clazz) {
        super (lkp, clazz);
    }

    private class JMI extends JMenuItem implements DynamicMenuContent {
        public JComponent[] getMenuPresenters() {
            if (DynamicAction.this.isEnabled()) {
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
    
    /*
    private static final String ATTR_TRIGGER_CLASS = "triggerClass";
    private static final String ATTR_DELEGATE_CLASS = "delegateClass";
    public static DynamicAction create (FileObject obj) {
        Lookup lkp = Utilities.actionsGlobalContext();
        String triggerType = (String) obj.getAttribute(ATTR_TRIGGER_CLASS);
        if (triggerType == null) {
            throw new IllegalArgumentException ("FileObject should have the " +
                    "attribute " + ATTR_TRIGGER_CLASS + " set to a fully " +
                    "qualified name of a loadable class");
        }
        String delegateType = (String) obj.getAttribute(ATTR_DELEGATE_CLASS);
        if (delegateType == null) {
            throw new IllegalArgumentException ("FileObject should have the " +
                    "attribute " + ATTR_DELEGATE_CLASS + " set to a fully " +
                    "qualified name of a loadable class that implements " +
                    "DynamicAction.Delegate");
        }
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        ClassLoader defaultCl = Lookup.getDefault().lookup(ClassLoader.class);
        Thread.currentThread().setContextClassLoader(defaultCl);
        try {
            Class triggerClass = Class.forName(triggerType);
            
            DynamicAction result = new DynamicAction (lkp, triggerClass) {

                @Override
                protected void performAction(Object t) {
                    
                }
                
            };
        } catch (ClassNotFoundException ex) {
            Exceptions.printStackTrace(ex);
        } finally {
            Thread.currentThread().setContextClassLoader(cl);
        }
    }
    
    public interface Delegate<T> {
        public void performAction (T t);
    } 
    */
}
