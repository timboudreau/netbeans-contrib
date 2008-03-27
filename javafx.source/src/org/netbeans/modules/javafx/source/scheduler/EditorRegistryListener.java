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

package org.netbeans.modules.javafx.source.scheduler;

import org.netbeans.api.javafx.source.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import org.netbeans.api.editor.EditorRegistry;

/**
 * 
 * @author David Strupl (initially copied from Java Source module JavaSource.java)
 */
public class EditorRegistryListener implements CaretListener, PropertyChangeListener {
    
    private final static EditorRegistryListener singleton = new EditorRegistryListener ();

    private Request request;
    private JTextComponent lastEditor;

    public EditorRegistryListener() {
        super();
        EditorRegistry.addPropertyChangeListener(new PropertyChangeListener() {

            public void propertyChange(PropertyChangeEvent evt) {
                editorRegistryChanged();
            }
        });
        editorRegistryChanged();
    }

    public void editorRegistryChanged() {
        final JTextComponent editor = EditorRegistry.lastFocusedComponent();
        if (lastEditor != editor) {
            if (lastEditor != null) {
                lastEditor.removeCaretListener(this);
                lastEditor.removePropertyChangeListener(this);
                final Document doc = lastEditor.getDocument();
                JavaFXSource js = null;
                if (doc != null) {
                    js = JavaFXSource.forDocument(doc);
                }
                if (js != null) {
                    js.k24 = false;
                }
            }
            lastEditor = editor;
            if (lastEditor != null) {
                lastEditor.addCaretListener(this);
                lastEditor.addPropertyChangeListener(this);
            }
        }
    }

    public void caretUpdate(CaretEvent event) {
        if (lastEditor != null) {
            Document doc = lastEditor.getDocument();
            if (doc != null) {
                JavaFXSource js = JavaFXSource.forDocument(doc);
                if (js != null) {
                    js.resetState(false, false);
                }
            }
        }
    }

    public void propertyChange(final PropertyChangeEvent evt) {
        String propName = evt.getPropertyName();
        if ("completion-active".equals(propName)) {
            JavaFXSource js = null;
            final Document doc = lastEditor.getDocument();
            if (doc != null) {
                js = JavaFXSource.forDocument(doc);
            }
            if (js != null) {
                Object rawValue = evt.getNewValue();
                assert rawValue instanceof Boolean;
                if (rawValue instanceof Boolean) {
                    final boolean value = (Boolean) rawValue;
                    if (value) {
                        assert this.request == null;
                        this.request = CompilationJob.currentRequest.getTaskToCancel(false);
                        if (this.request != null) {
                            this.request.task.cancel();
                        }
                        js.k24 = true;
                    } else {
                        Request _request = this.request;
                        this.request = null;
                        js.k24 = false;
                        js.resetTask.schedule(js.reparseDelay);
                        CompilationJob.currentRequest.cancelCompleted(_request);
                    }
                }
            }
        }
    }
}
