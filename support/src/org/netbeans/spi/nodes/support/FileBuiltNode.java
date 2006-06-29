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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 */
package org.netbeans.spi.nodes.support;

import java.awt.Image;
import java.util.ArrayList;
import java.util.List;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.netbeans.api.queries.FileBuiltQuery;
import org.netbeans.api.queries.FileBuiltQuery.Status;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;
import org.openide.nodes.FilterNode;
import org.openide.nodes.Node;
import org.openide.util.RequestProcessor;
import org.openide.util.Utilities;

/**
 *
 * @author Jan Lahoda
 */
class FileBuiltNode extends FilterNode implements ChangeListener {
    
    private static final Image NEEDS_COMPILE = Utilities.loadImage("org/netbeans/modules/support/resources/needs-compile.gif");
    
    private Status status;
    
    /** Creates a new instance of FileBuiltNode */
    public FileBuiltNode(Node original) {
        super(original);
        
        FileObject file = (FileObject) original.getLookup().lookup(FileObject.class);
        
        if (file == null) {
            DataObject od = (DataObject) original.getLookup().lookup(DataObject.class);
            
            if (od != null) {
                file = od.getPrimaryFile();
            }
        }
        
        if (file != null) {
            status = FileBuiltQuery.getStatus(file);
        }
        
        if (status != null) {
            status.addChangeListener(this);
        }
    }

    public void stateChanged(ChangeEvent e) {
        fireIconChange();
        fireOpenedIconChange();
    }

    public Image getIcon(int type) {
        Image i = super.getIcon(type);
        
        return enhanceIcon(i);
    }

    public Image getOpenedIcon(int type) {
        Image i = super.getOpenedIcon(type);
        
        return enhanceIcon(i);
    }

    private Image enhanceIcon(Image i) {
        if (status == null || status.isBuilt())
            return i;
        
        return Utilities.mergeImages(i, NEEDS_COMPILE, 16, 0);
    }
    
    private static List queue = new ArrayList();
    
    private static void enqueue(FileBuiltNode node) {
        synchronized (queue) {
            queue.add(node);
            
            WORKER_TASK.schedule(50);
        }
    }
    
    private static RequestProcessor WORKER = new RequestProcessor("Compile Badge Worker", 1);
    private static RequestProcessor.Task WORKER_TASK = WORKER.create(new Runnable() {
        public void run() {
            synchronized (queue) {
                while (!queue.isEmpty()) {
                    FileBuiltNode node = (FileBuiltNode) queue.remove(0);
                    
                    node.fireIconChange();
                    node.fireOpenedIconChange();
                }
            }
        }
    });
    
}
