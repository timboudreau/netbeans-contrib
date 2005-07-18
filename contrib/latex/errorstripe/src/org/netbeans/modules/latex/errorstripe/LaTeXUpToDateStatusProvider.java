/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2005 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.latex.errorstripe;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.text.Document;
import org.netbeans.modules.latex.model.command.LaTeXSource;
import org.netbeans.spi.editor.errorstripe.UpToDateStatus;
import org.netbeans.spi.editor.errorstripe.UpToDateStatusProvider;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;

/**
 *
 * @author Jan Lahoda
 */
public class LaTeXUpToDateStatusProvider extends UpToDateStatusProvider implements PropertyChangeListener{
    
    private LaTeXSource source;
    private boolean isParsing = false;
    private boolean sourceIsUpToDate = true;

    /** Creates a new instance of LaTeXUpToDateStatusProvider */
    public LaTeXUpToDateStatusProvider(Document document) {
        DataObject stream = (DataObject) document.getProperty(Document.StreamDescriptionProperty);
        
        if (stream != null) {
            FileObject file = stream.getPrimaryFile();
            
            source = LaTeXSource.get(file);
            
            source.addPropertyChangeListener(this);
        }
    }
    
    public UpToDateStatus getUpToDate() {
        if (sourceIsUpToDate)
            return UpToDateStatus.UP_TO_DATE_OK;
        
        return isParsing ? UpToDateStatus.UP_TO_DATE_PROCESSING : UpToDateStatus.UP_TO_DATE_DIRTY;
    }
    
    
    public void propertyChange(PropertyChangeEvent evt) {
        if ("upToDate".equals(evt.getPropertyName())) {
            sourceIsUpToDate = source.isUpToDate();
            firePropertyChange("upToDate", null, getUpToDate());
            return ;
        }
        
        if ("parsing".equals(evt.getPropertyName())) {
            isParsing = ((Boolean) evt.getNewValue()).booleanValue();
            firePropertyChange(PROP_UP_TO_DATE, null, getUpToDate());
            return ;
        }
    }
    
}
