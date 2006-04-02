/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2003 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.adlooks;

import javax.swing.Action;
import junit.framework.Test;
import junit.framework.TestSuite;
import org.netbeans.api.adaptable.Adaptor;
import org.netbeans.api.adaptable.Facets.DisplayName;

import org.openide.util.Lookup;

import org.netbeans.spi.looks.Look;
import org.netbeans.spi.looks.ProxyLook;
import org.netbeans.spi.looks.Selectors;
import org.openide.util.datatransfer.PasteType;

/** Check that calls thru Aspects methods get well to the Look with right
 * Look provider.
 *
 * @author Jaroslav Tulach
 */
public class BasicMethodsTest extends org.netbeans.junit.NbTestCase {
    private CntLook look;
    private Adaptor provider;
    
    
    public BasicMethodsTest (java.lang.String testName) {
        super(testName);
    }
    
    public static Test suite() {
        TestSuite suite = new org.netbeans.junit.NbTestSuite(BasicMethodsTest.class);
        
        return suite;
    }
    
    protected void setUp () {
        look = new CntLook ();
        provider = LooksImpl.create (Selectors.array (new Look[] { look }));
    }

    public void testDisplayName () {
        String name = provider.getAdaptable(new Object()).lookup(DisplayName.class).getDisplayName();
        look.assertMethods ("We called thru to getDisplayName", ProxyLook.GET_DISPLAY_NAME);
        assertEquals ("The look returns contant values", "getDisplayName", name);
    }
  
    
    static final class CntLook extends Look {
        private long methodsCalled;
        
        public CntLook () {
            super ("CntLook");
        }

        public void fire(Object obj, long mask) {
            fireChange(obj, mask);
        }
        
        public void assertMethods (String msg, long exactMask) {
            assertEquals (msg, exactMask, methodsCalled);
            methodsCalled = 0;
        }
        
        public javax.swing.Action[] getActions (Object representedObject, Lookup env) {
            Action[] retValue;
            
            retValue = super.getActions (representedObject, env);
            return retValue;
        }
        
        public void destroy (Object representedObject, Lookup env) throws java.io.IOException {
            super.destroy (representedObject, env);
        }
        
        public String getDisplayName () {
            return "CntLook";
        }
        
        public java.awt.Image getOpenedIcon (Object representedObject, int type, Lookup env) {
            java.awt.Image retValue;
            
            retValue = super.getOpenedIcon (representedObject, type, env);
            return retValue;
        }
        
        public org.openide.util.datatransfer.PasteType getDropType (Object representedObject, java.awt.datatransfer.Transferable t, int action, int index, Lookup env) {
            PasteType retValue;
            
            retValue = super.getDropType (representedObject, t, action, index, env);
            return retValue;
        }
        
        public String getDisplayName (Object representedObject, Lookup env) {
            methodsCalled |= ProxyLook.GET_DISPLAY_NAME;
            return "getDisplayName";
        }
        
        public java.awt.Image getIcon (Object representedObject, int type, Lookup env) {
            java.awt.Image retValue;
            
            retValue = super.getIcon (representedObject, type, env);
            return retValue;
        }
        
        protected void detachFrom (Object representedObject) {
            super.detachFrom (representedObject);
        }
        
        public java.awt.datatransfer.Transferable clipboardCut (Object representedObject, Lookup env) throws java.io.IOException {
            java.awt.datatransfer.Transferable retValue;
            
            retValue = super.clipboardCut (representedObject, env);
            return retValue;
        }
        
        public java.util.Collection getLookupItems (Object representedObject, Lookup oldEnv) {
            java.util.Collection retValue;
            
            retValue = super.getLookupItems (representedObject, oldEnv);
            return retValue;
        }
        
        public boolean canCopy (Object representedObject, Lookup env) {
            boolean retValue;
            
            retValue = super.canCopy (representedObject, env);
            return retValue;
        }
        
        public String getShortDescription (Object representedObject, Lookup env) {
            String retValue;
            
            retValue = super.getShortDescription (representedObject, env);
            return retValue;
        }
        
        public boolean equals (Object obj) {
            boolean retValue;
            
            retValue = super.equals (obj);
            return retValue;
        }
        
        public java.awt.datatransfer.Transferable clipboardCopy (Object representedObject, Lookup env) throws java.io.IOException {
            java.awt.datatransfer.Transferable retValue;
            
            retValue = super.clipboardCopy (representedObject, env);
            return retValue;
        }
        
        public java.awt.datatransfer.Transferable drag (Object representedObject, Lookup env) throws java.io.IOException {
            java.awt.datatransfer.Transferable retValue;
            
            retValue = super.drag (representedObject, env);
            return retValue;
        }
        
        public org.openide.util.datatransfer.PasteType[] getPasteTypes (Object representedObject, java.awt.datatransfer.Transferable t, Lookup env) {
            PasteType[] retValue;
            
            retValue = super.getPasteTypes (representedObject, t, env);
            return retValue;
        }
        
        public boolean canDestroy (Object representedObject, Lookup env) {
            boolean retValue;
            
            retValue = super.canDestroy (representedObject, env);
            return retValue;
        }
        
        public org.openide.nodes.Node.PropertySet[] getPropertySets (Object representedObject, Lookup env) {
            org.openide.nodes.Node.PropertySet[] retValue;
            
            retValue = super.getPropertySets (representedObject, env);
            return retValue;
        }
        
        public void rename (Object representedObject, String newName, Lookup env) throws java.io.IOException {
            super.rename (representedObject, newName, env);
        }
        
        public org.openide.util.HelpCtx getHelpCtx (Object representedObject, Lookup env) {
            org.openide.util.HelpCtx retValue;
            
            retValue = super.getHelpCtx (representedObject, env);
            return retValue;
        }
        
        public javax.swing.Action getDefaultAction (Object representedObject, Lookup env) {
            Action retValue;
            
            retValue = super.getDefaultAction (representedObject, env);
            return retValue;
        }
        
        public boolean canRename (Object representedObject, Lookup env) {
            boolean retValue;
            
            retValue = super.canRename (representedObject, env);
            return retValue;
        }
        
        public org.openide.util.datatransfer.NewType[] getNewTypes (Object representedObject, Lookup env) {
            org.openide.util.datatransfer.NewType[] retValue;
            
            retValue = super.getNewTypes (representedObject, env);
            return retValue;
        }
        
        public boolean hasCustomizer (Object representedObject, Lookup env) {
            boolean retValue;
            
            retValue = super.hasCustomizer (representedObject, env);
            return retValue;
        }
        
        protected void attachTo (Object representedObject) {
            super.attachTo (representedObject);
        }
        
        public java.util.List getChildObjects (Object representedObject, Lookup env) {
            java.util.List retValue;
            
            retValue = super.getChildObjects (representedObject, env);
            return retValue;
        }
        
        public boolean isLeaf (Object representedObject, Lookup env) {
            boolean retValue;
            
            retValue = super.isLeaf (representedObject, env);
            return retValue;
        }
        
        public boolean canCut (Object representedObject, Lookup env) {
            boolean retValue;
            
            retValue = super.canCut (representedObject, env);
            return retValue;
        }
        
        public java.awt.Component getCustomizer (Object representedObject, Lookup env) {
            java.awt.Component retValue;
            
            retValue = super.getCustomizer (representedObject, env);
            return retValue;
        }
        
        public String getName (Object representedObject, Lookup env) {
            String retValue;
            
            retValue = super.getName (representedObject, env);
            return retValue;
        }
        
        public javax.swing.Action[] getContextActions (Object representedObject, Lookup env) {
            Action[] retValue;
            
            retValue = super.getContextActions (representedObject, env);
            return retValue;
        }
        
    }
}
