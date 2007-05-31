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

package org.netbeans.api.convertor;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.FileReader;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Set;
import java.util.Collection;
import java.util.Iterator;
import javax.swing.JLabel;
import org.netbeans.junit.*;
import junit.textui.TestRunner;
import org.netbeans.spi.convertor.Convertor;
import org.openide.filesystems.Repository;
import org.openide.modules.ModuleInfo;
import org.openide.util.Lookup;


/**
 *
 * @author  David Konecny
 */
public class ConvertorsTest extends NbTestCase {
    

    public ConvertorsTest(String name) {
        super (name);
    }
    
    public static void main(String[] args) {
        TestRunner.run(new NbTestSuite(ConvertorsTest.class));
    }
    
    protected void setUp () throws Exception {
        Lookup.getDefault().lookup(ModuleInfo.class);
        Repository.getDefault ().getDefaultFileSystem ().getRoot ();
    }
    
    public void testCanRead() throws Exception {
        assertFalse(Convertors.canRead("http://www.dot.com/ns/smth", "tag"));
    }
    
    public void testCanWrite() throws Exception {
        assertFalse(Convertors.canWrite(new JLabel("aaa")));
    }
    
    public void testListeners() throws Exception {
        ModuleUtils.DEFAULT.install();
        
        Listener l = new Listener();
        Convertors.getDefault().addPropertyChangeListener(l);
        Collection col = Convertors.getDefault().getConvertorDescriptors();
        int initialSize = col.size();
        ModuleUtils.DEFAULT.enableBookModule(true);
        col = Convertors.getDefault().getConvertorDescriptors();
        assertTrue("Number of items in list of convertors does not much", col.size() == 1+initialSize);
        assertTrue("Event not received", l.e.size() == 1);
        assertTrue("Event name incorrect", ((PropertyChangeEvent)l.e.get(0)).getPropertyName().equals(Convertors.CONVERTOR_DESCRIPTORS));
        assertTrue("Number of old events incorrect", ((Set)((PropertyChangeEvent)l.e.get(0)).getOldValue()).size() == 0+initialSize);
        assertTrue("Number of new events incorrect", ((Set)((PropertyChangeEvent)l.e.get(0)).getNewValue()).size() == 1+initialSize);
        assertEquals("Event has incorrect new value", col, ((PropertyChangeEvent)l.e.get(0)).getNewValue());
        l.reset();
    
        ModuleUtils.DEFAULT.enableDVDConvertorModule(true);
        col = Convertors.getDefault().getConvertorDescriptors();
        assertTrue("Number of items in list of convertors does not much", col.size() == 2+initialSize);
        assertTrue("Event not received", l.e.size() == 1);
        assertTrue("Event name incorrect", ((PropertyChangeEvent)l.e.get(0)).getPropertyName().equals(Convertors.CONVERTOR_DESCRIPTORS));
        assertTrue("Number of old events incorrect", ((Set)((PropertyChangeEvent)l.e.get(0)).getOldValue()).size() == 1+initialSize);
        assertTrue("Number of new events incorrect", ((Set)((PropertyChangeEvent)l.e.get(0)).getNewValue()).size() == 2+initialSize);
        assertEquals("Event has incorrect new value", col, ((PropertyChangeEvent)l.e.get(0)).getNewValue());
        l.reset();

        ModuleUtils.DEFAULT.enableShoppingCartConvertorModule(true);
        col = Convertors.getDefault().getConvertorDescriptors();
        assertTrue("Number of items in list of convertors does not much", col.size() == 3+initialSize);
        assertTrue("Event not received", l.e.size() == 1);
        assertTrue("Event name incorrect", ((PropertyChangeEvent)l.e.get(0)).getPropertyName().equals(Convertors.CONVERTOR_DESCRIPTORS));
        assertTrue("Number of old events incorrect", ((Set)((PropertyChangeEvent)l.e.get(0)).getOldValue()).size() == 2+initialSize);
        assertTrue("Number of new events incorrect", ((Set)((PropertyChangeEvent)l.e.get(0)).getNewValue()).size() == 3+initialSize);
        assertEquals("Event has incorrect new value", col, ((PropertyChangeEvent)l.e.get(0)).getNewValue());
        l.reset();
        
        ModuleUtils.DEFAULT.enableShoppingCartConvertorModule(false);
        col = Convertors.getDefault().getConvertorDescriptors();
        assertTrue("Number of items in list of convertors does not much", col.size() == 2+initialSize);
        assertTrue("Event not received", l.e.size() == 1);
        assertTrue("Event name incorrect", ((PropertyChangeEvent)l.e.get(0)).getPropertyName().equals(Convertors.CONVERTOR_DESCRIPTORS));
        assertTrue("Number of old events incorrect", ((Set)((PropertyChangeEvent)l.e.get(0)).getOldValue()).size() == 3+initialSize);
        assertTrue("Number of new events incorrect", ((Set)((PropertyChangeEvent)l.e.get(0)).getNewValue()).size() == 2+initialSize);
        assertEquals("Event has incorrect new value", col, ((PropertyChangeEvent)l.e.get(0)).getNewValue());
        l.reset();
        
        ModuleUtils.DEFAULT.enableDVDConvertorModule(false);
        col = Convertors.getDefault().getConvertorDescriptors();
        assertTrue("Number of items in list of convertors does not much", col.size() == 1+initialSize);
        assertTrue("Event not received", l.e.size() == 1);
        assertTrue("Event name incorrect", ((PropertyChangeEvent)l.e.get(0)).getPropertyName().equals(Convertors.CONVERTOR_DESCRIPTORS));
        assertTrue("Number of old events incorrect", ((Set)((PropertyChangeEvent)l.e.get(0)).getOldValue()).size() == 2+initialSize);
        assertTrue("Number of new events incorrect", ((Set)((PropertyChangeEvent)l.e.get(0)).getNewValue()).size() == 1+initialSize);
        assertEquals("Event has incorrect new value", col, ((PropertyChangeEvent)l.e.get(0)).getNewValue());
        l.reset();

        ModuleUtils.DEFAULT.enableBookModule(false);
        col = Convertors.getDefault().getConvertorDescriptors();
        assertTrue("Number of items in list of convertors does not much", col.size() == 0+initialSize);
        assertTrue("Event not received", l.e.size() == 1);
        assertTrue("Event name incorrect", ((PropertyChangeEvent)l.e.get(0)).getPropertyName().equals(Convertors.CONVERTOR_DESCRIPTORS));
        assertTrue("Number of old events incorrect", ((Set)((PropertyChangeEvent)l.e.get(0)).getOldValue()).size() == 1+initialSize);
        assertTrue("Number of new events incorrect", ((Set)((PropertyChangeEvent)l.e.get(0)).getNewValue()).size() == 0+initialSize);
        assertEquals("Event has incorrect new value", col, ((PropertyChangeEvent)l.e.get(0)).getNewValue());
        l.reset();

        ModuleUtils.DEFAULT.uninstall();
        Convertors.getDefault().removePropertyChangeListener(l);
    }
    
    private static class Listener implements PropertyChangeListener {

        ArrayList e = new ArrayList();
        
        public Listener() {
        }
        
        public void reset() {
            e = new ArrayList();
        }
        
        public void propertyChange(java.beans.PropertyChangeEvent evt) {
            e.add(evt);
        }
        
    }
    
}
