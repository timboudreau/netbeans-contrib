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

package org.netbeans.modules.vcscore.cmdline;

import org.openide.*;
import org.openide.util.HelpCtx;

/**
 *
 * @author  Martin Entlicher
 */
public class RelativeMountDialog extends DialogDescriptor {

    private RelativeMountPanel mountPanel;
    private String rootDir;
    private String relMount;
    private String[] relMounts;

    /** Creates new RelativeMountDialog */
    public RelativeMountDialog() {
        this(new RelativeMountPanel(),
             org.openide.util.NbBundle.getBundle(RelativeMountDialog.class).getString("RelativeMountDialog.title"));
    }
    
    public RelativeMountDialog(Object innerPane, String title) {
        this(innerPane, title, null);
    }
    
    public RelativeMountDialog(Object innerPane, String title, HelpCtx help) {
        super(innerPane, title);
        if (help != null) setHelpCtx(help);
        mountPanel = (RelativeMountPanel) innerPane;
        setButtonListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent ev) {
                if (ev.getID() == java.awt.event.ActionEvent.ACTION_PERFORMED) {
                    if (NotifyDescriptor.OK_OPTION.equals(ev.getSource())) {
                        RelativeMountDialog.this.relMount = mountPanel.getRelMount();
                        RelativeMountDialog.this.relMounts = mountPanel.getRelMounts();
                    } else {
                        RelativeMountDialog.this.relMount = null;
                        RelativeMountDialog.this.relMounts = null;
                    }
                }
            }
        });
    }

    public void setDir(String rootDir, String relMount) {
        this.rootDir = rootDir;
        this.relMount = relMount;
        mountPanel.initTree(rootDir, relMount);
    }
    
    public void setDir(String rootDir, String[] relMounts) {
        this.rootDir = rootDir;
        this.relMounts = relMounts;
        mountPanel.initTree(rootDir, relMounts);
    }
    
    public String getRelMount() {
        return relMount;
    }
    
    public String[] getRelMounts() {
        return relMounts;
    }
    
}
