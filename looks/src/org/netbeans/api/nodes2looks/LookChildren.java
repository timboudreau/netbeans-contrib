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

package org.netbeans.api.nodes2looks;

import java.util.Collections;
import java.util.List;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.netbeans.spi.looks.LookSelector;
import org.netbeans.spi.looks.Look;

/** <B>WARNING</B> - This API is not finished and is subject to change<BR>
 * Please don't use this API for other purposes than testing.
 * <P>
 * Temporary implementation of Children of a LookNode. The class contains
 * only code wich is necessary for testing looks and may be extended later.
 *
 * @author Petr Hrebejk
 */
final class LookChildren extends Children.Keys {

    /** @param brutal needs clean
     */
    void refreshChildren( boolean brutal ) {
        if (brutal) {
            setKeys (Collections.EMPTY_LIST);
        }
        
        MUTEX.postWriteRequest(new Runnable() {
            public void run() {
                setKeys( getKeys() );
            }
        });
    }
    
    protected void addNotify() {
        setKeys( getKeys () );
    }
    
    protected void removeNotify() {
        setKeys( Collections.EMPTY_LIST );
    }
    
    protected Node[] createNodes( Object key ) {
        if (key == null) return new Node[0];
        
        Node n = getNode ();
        if (! (n instanceof LookNode)) {
            throw new InternalError();
        }

        LookNode ln = (LookNode)n;
                
        
        LookSelector s = ln.getLookSelectorForChildren();
                
        // Find look for the represented object
        Look l = ln.getCache().find ( ln, key );
        
        try {
            LookNode node = new LookNode( key, l, s, ln.getCache() );
            return new Node[] { node };
        }
        catch ( Throwable t ) {
            t.printStackTrace();
            return new Node[] {};
        }
                
    }
    
    
    // Private methods ---------------------------------------------------------
    
    
    private List getKeys() {
        List keys = ((LookNode)getNode ()).getChildObjects ();
        return keys == null ? Collections.EMPTY_LIST : keys;
    }
    
        
}
