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

package org.netbeans.modules.clazz;

import org.openide.cookies.SourceCookie;
import org.openide.loaders.DataObject;
import org.openide.nodes.Node;
import org.openide.src.SourceElement;
import org.openide.src.nodes.ElementNodeFactory;
import org.openide.src.nodes.SourceChildren;

/**
 * LazySourceChildren is used as a Children of {@link ClassDataNode}
 * to defer creation of {@link SourceElement} and reading of class file.
 * @author Tomas Zezula
 */
final class LazySourceChildren extends SourceChildren {

    private DataObject data;

    /** Creates a new instance of LazySourceChildren 
     *  @param data the source of the {@link SourceCookie} to take the
     *  @param ElementNodeFactory factory
     *  {@link SourceElement} from
     */
    public LazySourceChildren (DataObject data, ElementNodeFactory factory) {
        super (factory);
        assert data != null;
        this.data = data;
    }

    public SourceElement getElement() {        
        SourceElement retValue = super.getElement();
        if (retValue == null) {
            retValue = initElement ();
        }
        return retValue;
    }
    
    
    protected void addNotify() {
        this.getElement();  //Lazy Initialize the SourceElement
        super.addNotify();
    }
    
    public Node[] getNodes(boolean initialize) {
        this.getElement();  //Lazy Initialize the SourceElement
        return super.getNodes (initialize);
    }
    
    public Node findChild(String name) {
        this.getElement();  //Lazy Initialize the SourceElement
        return super.findChild(name);
    }
    
    private SourceElement initElement () {
        SourceCookie sc = (SourceCookie) this.data.getCookie(SourceCookie.class);
        SourceElement srcElement = sc.getSource();
        this.setElement(srcElement);
        return srcElement;
    }    

    
    
}
