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
 * 
 * Contributor(s):
 * 
 * Portions Copyrighted 2008 Sun Microsystems, Inc.
 */

package org.netbeans.modules.clearcase;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import org.netbeans.junit.NbTestCase;
import org.netbeans.modules.clearcase.client.ClearcaseClient;
import org.netbeans.modules.clearcase.ui.add.AddAction;
import org.netbeans.modules.clearcase.ui.checkout.CheckoutAction;
import org.netbeans.modules.versioning.spi.VCSInterceptor;
import org.netbeans.modules.versioning.util.Utils;
import org.openide.util.Exceptions;

/**
 *
 * @author Tomas Stupka
 */
public class DeleteTest extends NbTestCase {
   
    private ClearcaseInterceptor interceptor;
    private File testRoot;
    private FileStatusCache cache;
    
    public DeleteTest(String testName) throws IOException {
        super(testName);
        System.setProperty("org.netbeans.modules.clearcase.client.mockup.vobRoot", "/tmp/vob");
        testRoot = new File(System.getProperty("org.netbeans.modules.clearcase.client.mockup.vobRoot"), "deletetest"); //Utils.getTempFolder();
                           
        //System.setProperty("org.netbeans.modules.clearcase.client.mockup.vobRoot", vobRoot.getAbsolutePath());
    }            

    @Override
    protected void setUp() throws Exception {
        Utils.deleteRecursively(testRoot);
        testRoot.mkdirs();
        cache = new FileStatusCache();
        interceptor = new ClearcaseInterceptor();
        super.setUp();
    }

    @Override
    protected void tearDown() throws Exception {
        Utils.deleteRecursively(testRoot);
        super.tearDown();
    }

    public void testDeleteNotMananaged() throws IOException, SecurityException, NoSuchMethodException, IllegalAccessException, IllegalAccessException, IllegalArgumentException, IllegalArgumentException, InvocationTargetException {
        File file = File.createTempFile("file1", null);
        file.createNewFile();
        
        refreshImmediatelly(file);
        
        FileInformation info = cache.getInfo(file);
        assertEquals(FileInformation.STATUS_NOTVERSIONED_NOTMANAGED, info.getStatus());
        
        assertFalse(delete(file)); // interceptor refused to handle the file
       
    }
    
    public void testDeleteViewPrivate() throws IOException, SecurityException, NoSuchMethodException, IllegalAccessException, IllegalAccessException, IllegalArgumentException, IllegalArgumentException, InvocationTargetException {
        File file = new File(testRoot, "file");
        file.createNewFile();
        
        refreshImmediatelly(file);
        
        // create notmanaged file 
        FileInformation info = cache.getInfo(file);
        assertEquals(FileInformation.STATUS_NOTVERSIONED_NEWLOCALLY, info.getStatus());
        
        // test
        assertFalse(delete(file)); // interceptor refused to handle the file
    }

    // XXX try to emulate also remotely checkedout scenario
    public void testDeleteUptodateFileUptodateParent() throws IOException, SecurityException, NoSuchMethodException, IllegalAccessException, IllegalAccessException, IllegalArgumentException, IllegalArgumentException, InvocationTargetException {
        File parent = new File(testRoot, "parent");
        parent.mkdirs();
        
        // create uptodate file and folder
        File file = new File(parent, "file");
        file.createNewFile();        
        ClearcaseClient.CommandRunnable cr = AddAction.addFiles(new File[]{parent, file}, null, true);
        cr.waitFinished();
        refreshImmediatelly(parent);
    
        FileInformation info = cache.getInfo(file);
        assertEquals(FileInformation.STATUS_VERSIONED_UPTODATE, info.getStatus());        
        info = cache.getInfo(parent);
        assertEquals(FileInformation.STATUS_VERSIONED_UPTODATE, info.getStatus());
        
        // delete file
        assertTrue(delete(file));
        refreshImmediatelly(parent);
        refreshImmediatelly(file);
        
        // test
        assertFalse(file.exists());                                         // file is deleted        
        info = cache.getInfo(file);
        assertEquals(FileInformation.STATUS_UNKNOWN, info.getStatus());     // chache keeps track of it as unknown
        
        info = cache.getInfo(parent);       
        assertEquals(FileInformation.STATUS_VERSIONED_CHECKEDOUT, info.getStatus());
    }
    
    public void testDeleteUptodateFileCheckedoutParent() throws IOException, SecurityException, NoSuchMethodException, IllegalAccessException, IllegalAccessException, IllegalArgumentException, IllegalArgumentException, InvocationTargetException {
        File parent = new File(testRoot, "parent");
        parent.mkdirs();
        
        // create uptodate file and folder
        File file = new File(parent, "file");
        file.createNewFile();        
        ClearcaseClient.CommandRunnable cr = AddAction.addFiles(new File[]{parent, file}, null, true);
        cr.waitFinished();
        refreshImmediatelly(parent);
        refreshImmediatelly(file);
        CheckoutAction.ensureMutable(parent);
        refreshImmediatelly(parent);
        
        FileInformation info = cache.getInfo(file);
        assertEquals(FileInformation.STATUS_VERSIONED_UPTODATE, info.getStatus());        
        info = cache.getInfo(parent);
        assertEquals(FileInformation.STATUS_VERSIONED_CHECKEDOUT, info.getStatus());
        
        // delete file
        assertTrue(delete(file));
        refreshImmediatelly(file);
        refreshImmediatelly(parent);
        
        // test
        assertFalse(file.exists());                                         // file is deleted        
        info = cache.getInfo(file);
        assertEquals(FileInformation.STATUS_UNKNOWN, info.getStatus());     // chache keeps track of it as unknown
        
        info = cache.getInfo(parent);       
        assertEquals(FileInformation.STATUS_VERSIONED_CHECKEDOUT, info.getStatus());
    }
    
    public void testDeleteCheckedoutFileUptodateParent() throws IOException, SecurityException, NoSuchMethodException, IllegalAccessException, IllegalAccessException, IllegalArgumentException, IllegalArgumentException, InvocationTargetException {
        File parent = new File(testRoot, "parent");
        parent.mkdirs();
        
        // create uptodate file and folder
        File file = new File(parent, "file");
        file.createNewFile();        
        ClearcaseClient.CommandRunnable cr = AddAction.addFiles(new File[]{parent, file}, null, true);
        cr.waitFinished();
        refreshImmediatelly(parent);
        refreshImmediatelly(file);
        CheckoutAction.ensureMutable(file);
        refreshImmediatelly(file);
        
        FileInformation info = cache.getInfo(file);
        assertEquals(FileInformation.STATUS_VERSIONED_CHECKEDOUT, info.getStatus());        
        info = cache.getInfo(parent);
        assertEquals(FileInformation.STATUS_VERSIONED_UPTODATE, info.getStatus());
        
        // delete file
        assertTrue(delete(file));
        refreshImmediatelly(file);
        refreshImmediatelly(parent);
        
        // test
        assertFalse(file.exists());                                         // file is deleted        
        info = cache.getInfo(file);
        assertEquals(FileInformation.STATUS_UNKNOWN, info.getStatus());     // chache keeps track of it as unknown
        
        info = cache.getInfo(parent);       
        assertEquals(FileInformation.STATUS_VERSIONED_CHECKEDOUT, info.getStatus());
    }

    public void testDeleteCheckedoutFileCheckedoutParent() throws IOException, SecurityException, NoSuchMethodException, IllegalAccessException, IllegalAccessException, IllegalArgumentException, IllegalArgumentException, InvocationTargetException {
        File parent = new File(testRoot, "parent");
        parent.mkdirs();
        
        // create uptodate file and folder
        File file = new File(parent, "file");
        file.createNewFile();        
        ClearcaseClient.CommandRunnable cr = AddAction.addFiles(new File[]{parent, file}, null, true);
        cr.waitFinished();
        refreshImmediatelly(parent);
        refreshImmediatelly(file);
        CheckoutAction.ensureMutable(file);
        CheckoutAction.ensureMutable(parent);
        refreshImmediatelly(parent);
        refreshImmediatelly(file);
        
        FileInformation info = cache.getInfo(file);
        assertEquals(FileInformation.STATUS_VERSIONED_CHECKEDOUT, info.getStatus());        
        info = cache.getInfo(parent);
        assertEquals(FileInformation.STATUS_VERSIONED_CHECKEDOUT, info.getStatus());
        
        // delete file
        assertTrue(delete(file));
        refreshImmediatelly(file);
        refreshImmediatelly(parent);
        
        // test
        assertFalse(file.exists());                                         // file is deleted        
        info = cache.getInfo(file);
        assertEquals(FileInformation.STATUS_UNKNOWN, info.getStatus());     // chache keeps track of it as unknown
        
        info = cache.getInfo(parent);       
        assertEquals(FileInformation.STATUS_VERSIONED_CHECKEDOUT, info.getStatus());
    }
    
    private boolean delete(File file) throws IOException {
        boolean delete = interceptor.beforeDelete(file);
        if (delete) {
            interceptor.doDelete(file);
        } else {
            return false; // false -> clearcase interceptor refused handling the delete
        }
        interceptor.afterDelete(file);
        
        waitALittleBit(2000); // the doDelete works asynchronusly. lets give him some time ...
        
        return true; // interceptor handled the delete
    }

    private void refreshImmediatelly(File file) throws SecurityException, NoSuchMethodException, IllegalAccessException, IllegalAccessException, IllegalArgumentException, IllegalArgumentException, InvocationTargetException {
        Method m = cache.getClass().getDeclaredMethod("refresh", new Class[] {File.class, boolean.class});
        m.setAccessible(true);
        m.invoke(cache, new Object[] {file, true});
    } 

    private void waitALittleBit(long l) {
        try {
            Thread.sleep(l);    // this is so slow ...
        } catch (InterruptedException ex) {
            Exceptions.printStackTrace(ex);
        }
    }
}
