/*
 *                          Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License Version
 * 1.0 (the "License"). You may not use this file except in compliance with
 * the License. A copy of the License is available at http://www.sun.com/
 *
 * The Original Code is the LaTeX module.
 * The Initial Developer of the Original Code is Jan Lahoda.
 * Portions created by Jan Lahoda_ are Copyright (C) 2002-2004.
 * All Rights Reserved.
 *
 * Contributor(s): Jan Lahoda.
 */
package org.netbeans.modules.latex.bibtex;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import org.openide.ErrorManager;
import org.openide.util.RequestProcessor;

/**
 *
 * @author Jan Lahoda
 */
public class BiBTest extends AbstractAction {
    
    /** Creates a new instance of BiBTest */
    public BiBTest() {
        putValue(NAME, "Run BiBTeX Test");
    }
    
    public void actionPerformed(ActionEvent e) {
        RequestProcessor.getDefault().post(new Runnable() {
            public void run() {
                try {
                    IncrementalParserTest.main(new String[0]);
                } catch (Exception ex) {
                    ErrorManager.getDefault().notify(ex);
                }
            }
        });
    }
    
}
