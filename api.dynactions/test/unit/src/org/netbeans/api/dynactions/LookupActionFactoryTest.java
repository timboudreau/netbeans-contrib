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
 * Portions Copyrighted 2007 Sun Microsystems, Inc.
 */

package org.netbeans.api.dynactions;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import javax.swing.Action;
import org.netbeans.api.objectloader.CacheStrategies;
import org.netbeans.api.objectloader.ObjectLoader;
import org.netbeans.junit.MockServices;
import org.netbeans.junit.NbTestCase;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileSystem;
import org.openide.filesystems.MultiFileSystem;
import org.openide.filesystems.Repository;
import org.openide.filesystems.XMLFileSystem;
import org.openide.util.ContextGlobalProvider;
import org.openide.util.Lookup;
import org.openide.util.lookup.Lookups;
import org.xml.sax.SAXException;

/**
 *
 * @author Tim Boudreau
 */
public class LookupActionFactoryTest extends NbTestCase {
    
    public LookupActionFactoryTest(String testName) {
        super(testName);
    }            

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        MockServices.setServices(MF.class);
    }

    public void testSanity() {
        System.out.println("testSanity");
        FileSystem fs = Repository.getDefault().getDefaultFileSystem();
        FileObject fob = fs.getRoot().getFileObject("check.txt");
        assertNotNull ("Default filesystem broken", fob);
    }
    
    public void testGetActions() {
        System.out.println("testGetActions");
        ActionFactory factory = ActionFactory.lookup(new Prov(), "root1");
        Action[] actions = factory.getActions();
        Set <Action> s = new HashSet <Action> (Arrays.asList(actions));
        System.out.println("CONTENTS: " + s);
        assertTrue (containsInstance (Action1.class, s));
        assertTrue (containsInstance (Action2.class, s));
        assertTrue (containsInstance (OtherObjSensitiveAction.class, s));
        assertEquals (3, actions.length);
    }
    
    public void testNoActionsIfLookupEmpty() {
        System.out.println("testNoActionsIfLookupEmpty");
        ActionFactory factory = ActionFactory.lookup(new Prov(true), "root1");
        Action[] actions = factory.getActions();
        Set <Action> s = new HashSet <Action> (Arrays.asList(actions));
        System.out.println("CONTENTS: " + s);
        assertFalse  (containsInstance (Action1.class, s));
        assertFalse (containsInstance (Action2.class, s));
        assertFalse (containsInstance (OtherObjSensitiveAction.class, s));
        assertEquals (0, actions.length);
    }
    
    public void testActionInvocation() throws InterruptedException {
        System.out.println("testActionInvocation");
        Prov prov = new Prov();
        ActionFactory factory = ActionFactory.lookup(prov, "root1");
        Action[] actions = factory.getActions();
        OtherObjSensitiveAction a = findInstance (actions, OtherObjSensitiveAction.class);
        OtherObjSensitiveAction a1 = (OtherObjSensitiveAction) a.createContextAwareInstance(prov.getLookup());
        a1.actionPerformed(null);
        synchronized (a1) {
            a1.wait(10000 );
        }
        
        Action1 aa = findInstance (actions, Action1.class);
        Action1 aa1 = (Action1) aa.createContextAwareInstance(prov.lkp);
        aa1.actionPerformed(null);
        synchronized (aa1) {
            aa1.wait(10000);
        }
        assertNotNull (aa1.last());
        
        Action2 aaa = findInstance (actions, Action2.class);
        Action2 aaa1 = (Action2) aaa.createContextAwareInstance(prov.lkp);
        aaa1.actionPerformed(null);
        synchronized (aaa1) {
            aaa1.wait(10000);
        }
        assertNotNull (aaa1.last());

        assertNotNull (a1.last);
    }
    
    private <T extends Action> T findInstance (Action[] actions, Class<T> clazz) {
        for (Action a : actions) {
            if (a.getClass().equals(clazz)) {
                return (T) a;
            }
        }
        fail (clazz + " not found");
        throw new AssertionError();
    }
    
    private boolean containsInstance (Class clazz, Collection c) {
        boolean result = false;
        for (Object o : c) {
            result |= clazz.isInstance(o);
            if (result) {
                break;
            }
        }
        return result;
    }
    
    private static final class Prov implements Lookup.Provider, ContextGlobalProvider {
        private final Lookup lkp = Lookups.fixed(new Obj(), new Ldr());
        private boolean empty = false;
        Prov() {
            this (false);
        }
        
        Prov (boolean empty) {
            this.empty = empty;
        }
        public Lookup getLookup() {
            return empty ? Lookup.EMPTY : lkp;
        }

        public Lookup createGlobalContext() {
            return empty ? Lookup.EMPTY : lkp;
        }
    }
    
    private static final class Ldr extends ObjectLoader<OtherObj> {
        public Ldr() {
            super (OtherObj.class, CacheStrategies.HARD);
        }
        
        @Override
        protected OtherObj load() throws IOException {
            return new OtherObj();
        }
    }

    public static class MF extends MultiFileSystem {
        public MF() {
            try {
                URL url = LookupActionFactoryTest.class.getResource("data.xml");
                setDelegates(new FileSystem[]{new XMLFileSystem(url)});
            } catch (SAXException ex) {
                throw new IllegalStateException (ex);
            }
        }
    }
}
