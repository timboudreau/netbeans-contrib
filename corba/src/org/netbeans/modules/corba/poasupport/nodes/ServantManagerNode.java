/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2001 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.corba.poasupport.nodes;

import java.io.IOException;
import java.awt.Component;

import org.openide.nodes.*;
import org.openide.util.HelpCtx;
import org.netbeans.modules.corba.poasupport.*;
import org.netbeans.modules.corba.poasupport.tools.*;

/** A simple node with no children.
 *
 * @author Dusan Balek
 */
public class ServantManagerNode extends POAMemberElementNode {
    
    public static final String ICON_BASE =
    "/org/netbeans/modules/corba/poasupport/resources/ServantManagerNodeIcon"; // NOI18N
    
    public ServantManagerNode(ServantManagerElement _element) {
        super (_element);
        setIconBase (ICON_BASE);
    }
    
    public HelpCtx getHelpCtx () {
        return HelpCtx.DEFAULT_HELP;
        // When you have help, change to:
        // return new HelpCtx (ServantNode.class);
    }
    
    public ServantManagerElement getServantManagerElement () {
        return (ServantManagerElement)element;
    }
    
    public void setName (String nue) {
        if (isWriteable()) {
            if (POAChecker.checkPOAMemberVarName(nue, getServantManagerElement(), (getServantManagerElement().getTypeName() != null) && (getServantManagerElement().getConstructor() != null), true))
                getServantManagerElement().setVarName(nue);
        }
    }

    public void destroy () throws IOException {
        ((POANode)getParentNode()).getPOAElement().removeServantManager();
        super.destroy ();
    }

    // Create a property sheet:
    protected Sheet createSheet () {
        Sheet sheet = super.createSheet ();
        Sheet.Set props = sheet.get (Sheet.PROPERTIES);
        if (props == null) {
            props = Sheet.createPropertiesSet ();
            sheet.put (props);
        }
        return sheet;
    }
    
    public Component getCustomizer () {
        return new POAMemberCustomizer (getServantManagerElement());
    }
    
    // RECOMMENDED - handle cloning specially (so as not to invoke the overhead of FilterNode):
    /*
    public Node cloneNode () {
        // Try to pass in similar constructor params to what you originally got:
        return new ServantNode ();
    }
     */
    
    /*
    public Transferable clipboardCopy () {
        // Add to, do not replace, the default node copy flavor:
        ExTransferable et = ExTransferable.create (super.clipboardCopy ());
        et.put (new ExTransferable.Single (DataFlavor.stringFlavor) {
                protected Object getData () {
                    return ServantNode.this.getDisplayName ();
                }
            });
        return et;
    }
    public Transferable clipboardCut () {
        // Add to, do not replace, the default node cut flavor:
        ExTransferable et = ExTransferable.create (super.clipboardCut ());
        // This is not so useful because this node will not be destroyed afterwards
        // (it is up to the paste type to decide whether to remove the "original",
        // and it is not safe to assume that getData will only be called once):
        et.put (new ExTransferable.Single (DataFlavor.stringFlavor) {
                protected Object getData () {
                    return ServantNode.this.getDisplayName ();
                }
            });
        return et;
    }
     */
    
}
