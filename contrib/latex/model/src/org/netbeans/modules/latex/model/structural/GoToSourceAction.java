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
 * The Original Software is the LaTeX module.
 * The Initial Developer of the Original Software is Jan Lahoda.
 * Portions created by Jan Lahoda_ are Copyright (C) 2002-2007.
 * All Rights Reserved.
 *
 * Contributor(s): Jan Lahoda.
 */
package org.netbeans.modules.latex.model.structural;

import java.io.IOException;
import org.netbeans.modules.latex.model.command.SourcePosition;
import org.openide.ErrorManager;
import org.openide.cookies.LineCookie;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;
import org.openide.nodes.Node;
import org.openide.text.Line;
import org.openide.util.HelpCtx;
import org.openide.util.actions.NodeAction;

/**SPI
 *
 * @author Jan Lahoda
 */
public class GoToSourceAction extends NodeAction {
    
    /** Creates a new instance of GoToSourceAction */
    public GoToSourceAction() {
    }
    
    public HelpCtx getHelpCtx() {
        return new HelpCtx(GoToSourceAction.class);
    }
    
    public String getName() {
        return "Go To Source";
    }
    
    public void performAction(Node[] activatedNodes) {
        try {
            PositionCookie pc = activatedNodes[0].getCookie(PositionCookie.class);
            SourcePosition position = pc.getPosition();
            FileObject fileObject = (FileObject) position.getFile(); //!!!!
            DataObject file = DataObject.find(fileObject);
            
            LineCookie lc = file.getCookie(LineCookie.class);
            
            if (lc == null)
                return ;
            
            int line = position.getLine();
            
            lc.getLineSet().getCurrent(line).show(Line.SHOW_GOTO);
        } catch (IOException e) {
            ErrorManager.getDefault().notify(e);
        }
    }
    
    public boolean asynchronous() {
        return false;
    }
    
    protected boolean enable(Node[] activatedNodes) {
        if (activatedNodes.length != 1)
            return false;
        
        Node activatedNode = activatedNodes[0];
        PositionCookie pc = activatedNode.getCookie(PositionCookie.class);
        
        if (pc == null)
            return false;
        
        return pc.getPosition() != null;
    }
    
}
