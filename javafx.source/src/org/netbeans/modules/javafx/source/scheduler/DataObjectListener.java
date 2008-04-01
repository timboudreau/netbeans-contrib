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
import java.io.IOException;
import java.util.logging.Level;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.util.RequestProcessor;
import org.openide.util.WeakListeners;

/**
 * 
 * @author David Strupl (initially copied from Java Source module JavaSource.java)
 */
public final class DataObjectListener implements PropertyChangeListener {

    private DataObject dobj;
    private final FileObject fobj;
    private PropertyChangeListener wlistener;
    private JavaFXSource source;

    public DataObjectListener(FileObject fo, JavaFXSource outer) throws DataObjectNotFoundException {
        super();
        this.source = outer;
        this.fobj = fo;
        this.dobj = DataObject.find(fo);
        wlistener = WeakListeners.propertyChange(this, dobj);
        this.dobj.addPropertyChangeListener(wlistener);
    }

    public void propertyChange(PropertyChangeEvent pce) {
        DataObject invalidDO = (DataObject) pce.getSource();
        if (invalidDO != dobj) {
            return;
        }
        if (DataObject.PROP_VALID.equals(pce.getPropertyName())) {
            handleInvalidDataObject(invalidDO);
        } else if (pce.getPropertyName() == null && !dobj.isValid()) {
            handleInvalidDataObject(invalidDO);
        }
    }

    private void handleInvalidDataObject(final DataObject invalidDO) {
        RequestProcessor.getDefault().post(new Runnable() {

            public void run() {
                handleInvalidDataObjectImpl(invalidDO);
            }
        });
    }

    private void handleInvalidDataObjectImpl(DataObject invalidDO) {
        invalidDO.removePropertyChangeListener(wlistener);
        if (fobj.isValid()) {
            try {
                DataObject dobjNew = DataObject.find(fobj);
                synchronized (DataObjectListener.this) {
                    if (dobjNew == dobj) {
                        return;
                    }
                    dobj = dobjNew;
                    dobj.addPropertyChangeListener(wlistener);
                }
                source.assignDocumentListener(dobjNew);
                source.resetState(true, true);
            } catch (DataObjectNotFoundException e) {
            } catch (IOException ex) {
                // should not occur
                CompilationJob.LOGGER.log(Level.SEVERE, ex.getMessage(), ex);
            }
        }
    }
}
