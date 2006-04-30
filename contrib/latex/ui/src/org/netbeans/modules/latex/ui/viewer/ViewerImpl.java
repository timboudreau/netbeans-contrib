/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 */
package org.netbeans.modules.latex.ui.viewer;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import javax.swing.SwingUtilities;
import org.netbeans.modules.latex.model.platform.FilePosition;
import org.netbeans.modules.latex.model.platform.Viewer;
import org.openide.filesystems.FileObject;

/**
 *
 * @author Jan Lahoda
 */
public class ViewerImpl implements Viewer {
    
    /** Creates a new instance of ViewerImpl */
    public ViewerImpl() {
    }

    private Map<FileObject, DocumentTopComponent> file2ViewerComponent = new HashMap<FileObject, DocumentTopComponent>();
    private Map<DocumentTopComponent, FileObject> viewerComponent2File = new HashMap<DocumentTopComponent, FileObject>();

    public void show(final FileObject file, final FilePosition startPosition) throws NullPointerException {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                DocumentTopComponent component = file2ViewerComponent.get(file);
                
                if (component == null) {
                    file2ViewerComponent.put(file, component = new DocumentTopComponent(file, ViewerImpl.this));
                    viewerComponent2File.put(component, file);
                    component.open();
                }

                component.setFilePosition(startPosition);
                component.requestActive();
            }
        });
    }

    void componentClosed(DocumentTopComponent component) {
        FileObject file = viewerComponent2File.get(component);

        file2ViewerComponent.remove(file);
        viewerComponent2File.remove(component);
    }

    public String getName() {
        return "in-ide";
    }

    public String getDisplayName() {
        return "In IDE PDF Viewer";
    }

    public boolean isSupported() {
        return true;
    }

    public boolean accepts(URI uri) {
        return uri.getPath().endsWith(".pdf");
    }

}
