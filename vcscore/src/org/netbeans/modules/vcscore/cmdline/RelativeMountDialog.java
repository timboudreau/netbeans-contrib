/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2000 Sun
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
                    } else {
                        RelativeMountDialog.this.relMount = null;
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
    
    public String getRelMount() {
        return relMount;
    }
    
}
