/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at http://www.netbeans.org/cddl.html
 * or http://www.netbeans.org/cddl.txt.
 *
 * When distributing Covered Code, include this CDDL Header Notice in each file
 * and include the License file at http://www.netbeans.org/cddl.txt.
 * If applicable, add the following below the CDDL Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.latex.errorstripe;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.text.Document;
import org.netbeans.spi.editor.errorstripe.UpToDateStatus;
import org.netbeans.spi.editor.errorstripe.UpToDateStatusProvider;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;

/**
 *
 * @author Jan Lahoda
 */
public class LaTeXUpToDateStatusProvider extends UpToDateStatusProvider implements PropertyChangeListener{
    
    private boolean isParsing = false;
    private boolean sourceIsUpToDate = true;

    /** Creates a new instance of LaTeXUpToDateStatusProvider */
    public LaTeXUpToDateStatusProvider(Document document) {
        DataObject stream = (DataObject) document.getProperty(Document.StreamDescriptionProperty);
        
        //XXX:
//        if (stream != null) {
//            FileObject file = stream.getPrimaryFile();
//            
//            source = LaTeXSource.get(file);
//            
//            source.addPropertyChangeListener(this);
//        }
    }
    
    public UpToDateStatus getUpToDate() {
        if (sourceIsUpToDate)
            return UpToDateStatus.UP_TO_DATE_OK;
        
        return isParsing ? UpToDateStatus.UP_TO_DATE_PROCESSING : UpToDateStatus.UP_TO_DATE_DIRTY;
    }
    
    
    public void propertyChange(PropertyChangeEvent evt) {
        //XXX:
//        if ("upToDate".equals(evt.getPropertyName())) {
//            sourceIsUpToDate = source.isUpToDate();
//            firePropertyChange("upToDate", null, getUpToDate());
//            return ;
//        }
//        
//        if ("parsing".equals(evt.getPropertyName())) {
//            isParsing = ((Boolean) evt.getNewValue()).booleanValue();
//            firePropertyChange(PROP_UP_TO_DATE, null, getUpToDate());
//            return ;
//        }
    }
    
}
