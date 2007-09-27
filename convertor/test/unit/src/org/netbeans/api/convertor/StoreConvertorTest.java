/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common
 * Development and Distribution License("CDDL") (collectively, the
 * "License"). You may not use this file except in compliance with the
 * License. You can obtain a copy of the License at
 * http://www.netbeans.org/cddl-gplv2.html
 * or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
 * specific language governing permissions and limitations under the
 * License.  When distributing the software, include this License Header
 * Notice in each file and include the License file at
 * nbbuild/licenses/CDDL-GPL-2-CP.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * Contributor(s):
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 *
 * If you wish your version of this file to be governed by only the CDDL
 * or only the GPL Version 2, indicate your decision by adding
 * "[Contributor] elects to include this software in this distribution
 * under the [CDDL or GPL Version 2] license." If you do not indicate a
 * single choice of license, a recipient has the option to distribute
 * your version of this file under either the CDDL, the GPL Version 2 or
 * to extend the choice of license to its licensees as provided above.
 * However, if you add GPL Version 2 code and therefore, elected the GPL
 * Version 2 license, then the option applies only if the new code is
 * made subject to such option by the copyright holder.
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
import org.netbeans.api.convertor.store.Store;
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
public class StoreConvertorTest extends NbTestCase {
    

    public StoreConvertorTest(String name) {
        super (name);
    }
    
    public static void main(String[] args) {
        TestRunner.run(new NbTestSuite(StoreConvertorTest.class));
    }
    
    protected void setUp () throws Exception {
        Lookup.getDefault().lookup(ModuleInfo.class);
        Repository.getDefault ().getDefaultFileSystem ().getRoot ();
    }
    
    private static Convertor conv;
    
    public static void setupConvertor() throws Exception {
        ModuleUtils.DEFAULT.install();
        ModuleUtils.DEFAULT.enableStoreConvertorModule(true);
    }
    
    public static void removeConvertor() throws Exception {
        ModuleUtils.DEFAULT.enableStoreConvertorModule(false);
        ModuleUtils.DEFAULT.uninstall();
    }
    
    public void testStoreConvertor() throws Exception {
        assertFalse(Convertors.canRead("http://www.netbeans.org/ns/store", "store"));
        assertFalse(Convertors.canRead("http://www.netbeans.org/ns/store", "dvd"));
        assertFalse(Convertors.canRead("http://www.netbeans.org/ns/store", "book"));
        assertFalse(Convertors.canWrite(new Store()));
        assertFalse(Convertors.canWrite(new DVD()));
        assertFalse(Convertors.canWrite(new Book()));
        
        setupConvertor();
        
        assertTrue(Convertors.canRead("http://www.netbeans.org/ns/store", "store"));
        assertTrue(Convertors.canRead("http://www.netbeans.org/ns/store", "book"));
        assertTrue(Convertors.canRead("http://www.netbeans.org/ns/store", "dvd"));
        assertTrue(Convertors.canWrite(new Store()));
        assertTrue(Convertors.canWrite(new DVD()));
        assertTrue(Convertors.canWrite(new Book()));

//        assertEquals(Convertors.getConvertorDescriptor(new Store()), new ConvertorDescriptor("http://www.netbeans.org/ns/store", "org.netbeans.api.convertor.store.Store"));

        String name = StoreConvertorTest.class.getResource("store").getFile() + "/data/Store.xml";
        InputStream is = new FileInputStream(name);
        Store sc = (Store)Convertors.read(is);
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
        sc = new Store();
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
        assertFalse(Convertors.canRead("http://www.netbeans.org/ns/store", "store"));
        assertFalse(Convertors.canRead("http://www.netbeans.org/ns/store", "dvd"));
        assertFalse(Convertors.canRead("http://www.netbeans.org/ns/store", "book"));
        assertFalse(Convertors.canWrite(new Store()));
        assertFalse(Convertors.canWrite(new DVD()));
        assertFalse(Convertors.canWrite(new Book()));
    }
    
}
