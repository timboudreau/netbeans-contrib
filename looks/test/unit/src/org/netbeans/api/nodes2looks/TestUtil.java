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

import java.net.URL;
import org.openide.nodes.Node;
import org.netbeans.spi.looks.Look;
import org.netbeans.spi.looks.LookSelector;
import org.openide.filesystems.FileSystem;
import org.openide.filesystems.XMLFileSystem;
import org.xml.sax.SAXException;


/** Usefull utility methods for Tests
 *
 * @author  phrebejk
 */
public class TestUtil {

    /** Creates a new instance of TestUtil */
    private TestUtil() {
    }

    public static Object getRepresentedObject( Node node ) {

        if ( node instanceof LookNode ) {
            return ((LookNode)node).getRepresentedObject();
        }
        else {
            return null;
        }
        
        /*
        Lookup lookup = node.getLookup();
        
        if ( lookup == null ) {
            return null;
        }
        
        Look.NodeSubstitute subst = (Look.NodeSubstitute)lookup.lookup( Look.NodeSubstitute.class );
        
        if ( subst == null ) {
            return null;
        }
        
        return subst.getRepresentedObject();
         */
        
    }
    
    /** [PENDING] This should be rewritten to LD, LS switcher later
     */    
    public static void setLook( Node node, Look look ) {
        
        if ( node instanceof LookNode ) {
            LookNode lookNode = (LookNode)node;
            lookNode.setLook( look );
        }
        else {
            throw new IllegalArgumentException( node + "does not support look switching (is not LookNode)" );
        }
        
    }

    /** [PENDING] This should be rewritten to LD, LS switcher later
     */    
    public static Look getLook( Node node ) {
        
        if ( node instanceof LookNode ) {
            LookNode lookNode = (LookNode)node;
            return lookNode.getLook();
        }
        else {
            throw new IllegalArgumentException( node + "does not support look switching (is not LookNode)" );
        }
        
    }
    
    /** [PENDING] This should be rewritten to LD, LS switcher later
     */    
    public static LookSelector getLookSelector( Node node ) {
        
        if ( node instanceof LookNode ) {
            LookNode lookNode = (LookNode)node;
            return lookNode.getLookSelector();
        }
        else {
            throw new IllegalArgumentException( node + "does not support look switching (is not LookNode)" );
        }
        
    }
    
    
    /** Sets up the registry to use the default XML layer of Looks       
     */ 
    public static void setUpRegistryToDefault() {
        
        URL url = org.netbeans.modules.looks.Accessor.class.getResource ("mf-layer.xml");

        try {
            FileSystem defaultFs = new XMLFileSystem( url );
            org.netbeans.modules.looks.RegistryBridge.setDefault( defaultFs.getRoot() );
        }
        catch ( SAXException e ) {
            IllegalStateException ex = new IllegalStateException( "Cant initialize defaut filesystem" );
            ex.initCause( e );
            throw ex;
        }
    }
}
