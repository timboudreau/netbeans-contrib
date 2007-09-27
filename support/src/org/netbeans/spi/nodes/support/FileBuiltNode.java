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
