/*
 *                          Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License Version
 * 1.0 (the "License"). You may not use this file except in compliance with
 * the License. A copy of the License is available at http://www.sun.com/
 *
 * The Original Code is the LaTeX module.
 * The Initial Developer of the Original Code is Jan Lahoda.
 * Portions created by Jan Lahoda_ are Copyright (C) 2002,2003.
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
    
    protected void performAction(Node[] activatedNodes) {
        try {
            PositionCookie pc = (PositionCookie) activatedNodes[0].getCookie(PositionCookie.class);
            SourcePosition position = pc.getPosition();
            FileObject fileObject = (FileObject) position.getFile(); //!!!!
            DataObject file = DataObject.find(fileObject);
            
            LineCookie lc = (LineCookie) file.getCookie(LineCookie.class);
            
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
        PositionCookie pc = (PositionCookie) activatedNode.getCookie(PositionCookie.class);
        
        if (pc == null)
            return false;
        
        return pc.getPosition() != null;
    }
    
}
