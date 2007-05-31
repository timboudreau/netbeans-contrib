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
import org.netbeans.api.convertor.dvd.DVD;
import org.netbeans.api.convertor.shoppingcart.ShoppingCart;
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
public class ShoppingCartConvertorTest extends NbTestCase {
    

    public ShoppingCartConvertorTest(String name) {
        super (name);
    }
    
    public static void main(String[] args) {
        TestRunner.run(new NbTestSuite(ShoppingCartConvertorTest.class));
    }
    
    protected void setUp () throws Exception {
        Lookup.getDefault().lookup(ModuleInfo.class);
        Repository.getDefault ().getDefaultFileSystem ().getRoot ();
    }
    
    private static Convertor conv;
    private static Convertor conv2;
    private static Convertor conv3;
    
    public static void setupConvertor() throws Exception {
        ModuleUtils.DEFAULT.install();
        ModuleUtils.DEFAULT.enableDVDConvertorModule(true);
        ModuleUtils.DEFAULT.enableBookModule(true);
        ModuleUtils.DEFAULT.enableShoppingCartConvertorModule(true);
    }
    
    public static void removeConvertor() throws Exception {
        ModuleUtils.DEFAULT.enableShoppingCartConvertorModule(false);
        ModuleUtils.DEFAULT.enableDVDConvertorModule(false);
        ModuleUtils.DEFAULT.enableBookModule(false);
        ModuleUtils.DEFAULT.uninstall();
    }
    
    public void testShoppingCartConvertor() throws Exception {
        assertFalse(Convertors.canRead("http://www.netbeans.org/ns/shoppingcart", "shoppingcart"));
        assertFalse(Convertors.canWrite(new ShoppingCart()));
        
        setupConvertor();
        
        assertTrue(Convertors.canRead("http://www.netbeans.org/ns/shoppingcart", "shoppingcart"));
        assertTrue(Convertors.canWrite(new ShoppingCart()));

//        assertEquals(Convertors.getConvertorDescriptor(new ShoppingCart()), new ConvertorDescriptor("http://www.netbeans.org/ns/shoppingcart", "org.netbeans.api.convertor.shoppingcart.ShoppingCart"));

        String name = ShoppingCartConvertorTest.class.getResource("shoppingcart").getFile() + "/data/ShoppingCart.xml";
        InputStream is = new FileInputStream(name);
        ShoppingCart sc = (ShoppingCart)Convertors.read(is);
        Book b = (Book)sc.books.iterator().next();
        DVD d = (DVD)sc.dvds.iterator().next();
        assertEquals(d.ID, 125);
        assertEquals(d.title, "Tetsuo");
        assertEquals(d.publisher, "TartanTerror");
        assertEquals(d.price, 19);
        assertEquals(b.ID, 4564);
        assertEquals(b.title, "Electroboy");
        assertEquals(b.author, "Andy Behrman");
        assertEquals(b.publisher, "Random House");
        assertEquals(b.price, 36);
        is.close();
        
        
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        sc = new ShoppingCart();
        Book book = new Book(256, "Better than Life", "Grant Naylor", "Penguin Books", 399);
        DVD dvd = new DVD(856, "Zentropa", "TartanClassic", 265);
        sc.addBook(book);
        sc.addDVD(dvd);
        Convertors.write(os, sc);
        
        byte[] ba = os.toByteArray();
        os.close();
        
        ByteArrayInputStream bis = new ByteArrayInputStream(ba);
        Object o = Convertors.read(bis);
        assertEquals(sc, o);
        
        removeConvertor();
        assertFalse(Convertors.canRead("http://www.netbeans.org/ns/shoppingcart", "shoppingcart"));
        assertFalse(Convertors.canWrite(new ShoppingCart()));
    }
    
}
