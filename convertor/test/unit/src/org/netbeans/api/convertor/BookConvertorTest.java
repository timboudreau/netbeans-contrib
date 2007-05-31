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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import org.netbeans.junit.*;
import junit.textui.TestRunner;
import org.netbeans.api.convertor.book.Book;
import org.netbeans.modules.convertor.PropertiesConvertor;
import org.netbeans.spi.convertor.Convertor;
import org.openide.filesystems.Repository;
import org.openide.modules.ModuleInfo;
import org.openide.util.Lookup;
import org.w3c.dom.Document;


/**
 *
 * @author  David Konecny
 */
public class BookConvertorTest extends NbTestCase {
    

    public BookConvertorTest(String name) {
        super (name);
    }
    
    public static void main(String[] args) {
        TestRunner.run(new NbTestSuite(BookConvertorTest.class));
    }
    
    protected void setUp () throws Exception {
        Lookup.getDefault().lookup(ModuleInfo.class);
        Repository.getDefault ().getDefaultFileSystem ().getRoot ();
    }
    
    private static Convertor conv;
    
    public static void setupConvertor() throws Exception {
        ModuleUtils.DEFAULT.install();
        ModuleUtils.DEFAULT.enableBookModule(true);
    }
    
    public static void removeConvertor() throws Exception {
        ModuleUtils.DEFAULT.enableBookModule(false);
        ModuleUtils.DEFAULT.uninstall();
    }
    
    public void testPropertiesBookConvertor() throws Exception {
        assertFalse(Convertors.canRead("http://www.netbeans.org/ns/book", "book"));
        assertFalse(Convertors.canWrite(new Book()));
        
        setupConvertor();
        
        assertTrue(Convertors.canRead("http://www.netbeans.org/ns/book", "book"));
        assertTrue(Convertors.canWrite(new Book()));

//        assertEquals(Convertors.getConvertorDescriptor(new Book()), new ConvertorDescriptor("http://www.netbeans.org/ns/book", "org.netbeans.api.convertor.book.Book"));

        String name = BookConvertorTest.class.getResource("book").getFile() + "/data/Book.xml";
        InputStream is = new FileInputStream(name);
        Book b = (Book)Convertors.read(is);
        assertEquals(b.ID, 4564);
        assertEquals(b.title, "Electroboy");
        assertEquals(b.author, "Andy Behrman");
        assertEquals(b.publisher, "Random House");
        assertEquals(b.price, 36);
        is.close();
        
        
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        Book book = new Book(951, "Better than Life", "Grant Naylor", "Penguin Books", 99);
        Convertors.write(os, book);
        
        byte[] ba = os.toByteArray();
        os.close();
        
        ByteArrayInputStream bis = new ByteArrayInputStream(ba);
        Object o = Convertors.read(bis);
        assertEquals(book, o);
        
        removeConvertor();
        assertFalse(Convertors.canRead("http://www.netbeans.org/ns/book", "book"));
        assertFalse(Convertors.canWrite(new Book()));
    }
    
}
