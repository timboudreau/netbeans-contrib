/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2009 Sun Microsystems, Inc. All rights reserved.
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
package org.netbeans.modules.fisheye;

import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.ref.WeakReference;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;


final class WeakComponentAndDocumentListener implements ComponentListener, DocumentListener, PropertyChangeListener {
    private WeakReference <ComponentListener> target;
    private JTextComponent comp;
    public WeakComponentAndDocumentListener(ComponentListener target, JTextComponent comp) {
        this.target = new WeakReference <ComponentListener> (target);
        this.comp = comp;
        comp.addComponentListener(this);
        if (target instanceof DocumentListener) {
            comp.getDocument().addDocumentListener(this);
            comp.addPropertyChangeListener("document",this); //NOI18N
        }
    }
    
    private ComponentListener getTarget() {
        ComponentListener result = target.get();
        if (result == null) {
            comp.removeComponentListener(this);
            comp.getDocument().removeDocumentListener(this);
            comp.removePropertyChangeListener("document", this);
        }
        return result;
    }
    
    public void componentResized(ComponentEvent e) {
        ComponentListener c = getTarget();
        if (c != null) {
            c.componentResized(e);
        }
    }

    public void componentMoved(ComponentEvent e) {
        ComponentListener c = getTarget();
        if (c != null) {
            c.componentMoved(e);
        }
    }

    public void componentShown(ComponentEvent e) {
        ComponentListener c = getTarget();
        if (c != null) {
            c.componentShown(e);
        }
    }

    public void componentHidden(ComponentEvent e) {
        ComponentListener c = getTarget();
        if (c != null) {
            c.componentHidden(e);
        }
    }

    public void insertUpdate(DocumentEvent e) {
        ComponentListener c = getTarget();
        if (c instanceof DocumentListener) {
            ((DocumentListener) c).insertUpdate(e);
        }            
        if (c instanceof ChangeListener) {
            ((ChangeListener) c).stateChanged(new ChangeEvent(e.getDocument()));
        }
    }

    public void removeUpdate(DocumentEvent e) {
        ComponentListener c = getTarget();
        if (c instanceof DocumentListener) {
            ((DocumentListener) c).removeUpdate(e);
        }            
        if (c instanceof ChangeListener) {
            ((ChangeListener) c).stateChanged(new ChangeEvent(e.getDocument()));
        }
    }

    public void changedUpdate(DocumentEvent e) {
        ComponentListener c = getTarget();
        if (c instanceof DocumentListener) {
            ((DocumentListener) c).changedUpdate(e);
        }
        if (c instanceof ChangeListener) {
            ((ChangeListener) c).stateChanged(new ChangeEvent(e.getDocument()));
        }
    }

    public void propertyChange(PropertyChangeEvent evt) {
        if ("document".equals(evt.getPropertyName())) {
            Document doc = (Document) evt.getOldValue();
            if (doc != null) {
                doc.removeDocumentListener(this);
            }
            doc = (Document) evt.getNewValue();
            if (doc != null) {
                doc.addDocumentListener(this);
            }
        }
    }
    
}