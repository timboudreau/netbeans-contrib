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

package org.netbeans.api.registry;

import junit.textui.TestRunner;
import org.netbeans.api.registry.fs.FileSystemContextFactory;
import org.netbeans.junit.NbTestCase;
import org.netbeans.junit.NbTestSuite;
import org.netbeans.spi.registry.BasicContext;
import org.netbeans.spi.registry.SpiUtils;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.Repository;
import org.openide.loaders.DataObject;
import org.openide.modules.ModuleInfo;
import org.openide.util.Lookup;

import javax.swing.*;
import java.awt.*;
import java.net.URL;

/**
 *
 * @author  Vitezslav Stejskal
 * @author  David Konecny
 */
public class BindingTest extends NbTestCase {
    private static final String MY_NULL = new String("MY_NULL");

    public BindingTest (String name) {
        super (name);
    }
    
    public static void main(String[] args) {
        TestRunner.run(new NbTestSuite(BindingTest.class));
    }
    
    protected void setUp () throws Exception {
        Lookup.getDefault().lookup(ModuleInfo.class);
    }
    
    public void testBindObject() throws Exception {
        implBindObject(getRootContext(), "foo", getRoot(), new JLabel("obj 11"), new JLabel("second OBJ"));
    }

    public void testBindObject2() throws Exception {
        Context ctx = getRootContext().createSubcontext("a/b/c");
        FileObject fo = findResource ("a/b/c");
        implBindObject(ctx, "foo", fo, new JLabel("objQ"), new JLabel("secondW")) ;
        getRootContext().destroySubcontext("a");
    }

    // XXX #27494 Names over 50 chars.
    public void _EXCLUDE_issue36334_testLongNameObject() throws Exception {
        implBindObject(getRootContext(), "ThisIsVeryLongNameOfInstanceFileToTestIssueDealingWithInstanceNamesWhichLenghtIsOver50Characters", getRoot(), new JLabel("objQ1"), new JLabel("secondW1"));
    }
    
    // XXX #27494 Names containing some special chars    
    public void _EXCLUDE_testStrangeNameObject() throws Exception {
        implBindObject(getRootContext(), ":[]<>?*|.\"\u0020\u007E#", getRoot(), new JLabel("objQ111"), new JLabel("secondW111"));
    }
    
    public void implBindObject(Context context, String bindingName, FileObject rootFO, Object objA, Object objA2) throws Exception {
        FileObject f;
        
        context.putObject(bindingName, objA);
        String fileName = escapeAndCut(bindingName);
        f = rootFO.getFileObject (fileName, "settings");
        assertTrue ("Instance file wasn't created for bound object", f != null);
        assertTrue (f.isData ());

        // XXX:  hold IDO instance otherwise, it can be GCed and another instance of
        // objA will be looked up from the context, this is bug of IDO
        DataObject ido = DataObject.find (f);

        Object obj2 = context.getObject(bindingName, MY_NULL);
        assertTrue ("Bound object wasn't looked up", obj2 != null);
        assertTrue ("Bind obj and lookup result are different", objA == obj2);
        
        // object and subcontext must coexist
        try {
            context.createSubcontext (bindingName);
        } catch (Exception e) {
            fail ("Context with same name as object binding must coexist");
        }

        context.destroySubcontext (bindingName);
        obj2 = context.getObject (bindingName, MY_NULL);
        assertTrue ("Subcontext destroyed, but object can't be found, obj2=" + obj2 + " objA=" + objA, obj2 == objA);

        // incorrect binding name
        {
            // this binding will not be stored
            context.putObject("ctx1/"+bindingName, objA);
            Object o = context.getObject ("ctx1/"+bindingName, MY_NULL);
            assertTrue ("ContextException expected when slash character is used in name",
                o == MY_NULL);
        }

        // rebind
        Object objB = objA2;
        context.putObject (bindingName, objB);
        Object obj = context.getObject (bindingName, MY_NULL);
        assertEquals ("New object not found", obj, objB);
        
        // unbind
        context.putObject (bindingName, null);
        f = rootFO.getFileObject (bindingName, "settings");
        assertTrue ("Instance file wasn't destroyed for unbound object", f == null);
        
        // binding doesn't exist anymore
        obj2 = context.getObject(bindingName, MY_NULL);
        assertTrue("Object is still reachable even if unbound", obj2 == MY_NULL);
        
        // unbind for non-existing binding must be OK
        context.putObject (bindingName, null);
    }

    public void testBindPrimitive() throws Exception {
        implBindPrimitive(getRootContext(), "foo", getRoot(), new String("11"), new String("789"));
    }

    public void testBindPrimitive2() throws Exception {
        Context ctx = getRootContext().createSubcontext("a/b/c");
        FileObject fo = findResource ("a/b/c");
        implBindPrimitive(ctx, "foo", fo, new String("171"), new String("7879")) ;
        getRootContext().destroySubcontext("a");
    }

    // XXX #27494 Names over 50 chars.
    public void testLongNamePrimitive() throws Exception {
        implBindPrimitive(getRootContext(), "ThisIsVeryLongNameOfInstanceFileToTestIssueDealingWithInstanceNamesWhichLenghtIsOver50Characters", getRoot(), new String("119"), new String("9789"));
    }
    
    // XXX #27494 Names containing some special chars    
    public void _EXCLUDE_testStrangeNamePrimitive() throws Exception {
        implBindPrimitive(getRootContext(), ":[]<>?*|.\"\u0020\u007E#", getRoot(), new String("1175"), new String("7895"));
    }
    
    public void implBindPrimitive(Context context, String bindingName, FileObject rootFO, String objA, String objA2) throws Exception {
        FileObject f;
        
        context.putObject(bindingName, objA);

        String obj2 = (String)context.getObject(bindingName, MY_NULL);
        assertTrue ("Bound object wasn't looked up", obj2 != null);
        assertTrue ("Bind obj and lookup result are different: A="+objA+" B="+obj2, objA.equals(obj2));
        
        // object and subcontext must coexist
        try {
            context.createSubcontext (bindingName);
        } catch (Exception e) {
            fail ("Context with same name as object binding must coexist");
        }

        context.destroySubcontext (bindingName);
        obj2 = (String)context.getObject (bindingName, MY_NULL);
        assertTrue ("Subcontext destroyed, but object can't be found, obj2=" + obj2 + " objA=" + objA, obj2.equals(objA));

        // incorrect binding name
        {
            // this binding will not be stored
            context.putObject("ctx1/"+bindingName, objA);
            String o = (String)context.getObject ("ctx1/"+bindingName, MY_NULL);
            assertTrue ("ContextException expected when slash character is used in name",
                o == MY_NULL);
        }

        // rebind
        String objB = (String)objA2;
        context.putObject (bindingName, objB);
        String obj = (String)context.getObject (bindingName, MY_NULL);
        assertEquals ("New object not found", obj, objB);
        
        // unbind
        context.putObject (bindingName, null);
        f = rootFO.getFileObject (bindingName, "settings");
        assertTrue ("Instance file wasn't destroyed for unbound object", f == null);
        
        // binding doesn't exist anymore
        obj2 = (String)context.getObject(bindingName, MY_NULL);
        assertTrue("Object is still reachable even if unbound", obj2 == MY_NULL);
        
        // unbind for non-existing binding must be OK
        context.putObject (bindingName, null);
    }
    
    public void testPrimitive() throws Exception {
        implTestPrimitive(getRootContext());
    }
    
    public void testPrimitive2() throws Exception {
        Context ctx = getRootContext().createSubcontext("a/b/c");
        FileObject fo = findResource ("a/b/c");
        implTestPrimitive(ctx);
        getRootContext().destroySubcontext("a");
    }

    public void implTestPrimitive(Context ctx) throws Exception {
        String s = "bsdfmdbmfd";
        ctx.putString("b1", s);
        String s_ = ctx.getString("b1", null);
        assertTrue("Values do not match", s_.equals(s));
        assertTrue("Object type does not match", ctx.getObject("b1", null) instanceof String);
        
        int i = 1889;
        ctx.putInt("b2", i);
        int i_ = ctx.getInt("b2", 1112);
        assertTrue("Values do not match", i_ == i);
        assertTrue("Object type does not match", ctx.getObject("b2", null) instanceof Integer);
        
        long l = 188918891L;
        ctx.putLong("b3", l);
        long l_ = ctx.getLong("b3", 11121112L);
        assertTrue("Values do not match", l_ == l);
        assertTrue("Object type does not match", ctx.getObject("b3", null) instanceof Long);
        
        float f = 18891111.1889F;
        ctx.putFloat("b4", f);
        float f_ = ctx.getFloat("b4", 1112.1112F);
        assertTrue("Values do not match: "+f_+" "+f, f_ == f);
        assertTrue("Object type does not match", ctx.getObject("b4", null) instanceof Float);
        
        double d = 181889333333.18989989891889D;
        ctx.putDouble("b5", d);
        double d_ = ctx.getDouble("b5", 11121112.11121112D);
        assertTrue("Values do not match", d_ == d);
        assertTrue("Object type does not match", ctx.getObject("b5", null) instanceof Double);
        
        boolean b = true;
        ctx.putBoolean("b6", b);
        boolean b_ = ctx.getBoolean("b6", false);
        assertTrue("Values do not match", b_ == b);
        assertTrue("Object type does not match", ctx.getObject("b6", null) instanceof Boolean);
        
        Font ff = new Font("Dialog", Font.ITALIC, 31);
        ctx.putFont("b7", ff);
        Font ff_ = ctx.getFont("b7", null);
        assertEquals("Values do not match", ff, ff_ );
        assertTrue("Object type does not match", ctx.getObject("b7", null) instanceof Font);
        
        Color c = new Color(10, 20, 30);
        ctx.putColor("b8", c);
        Color c_ = ctx.getColor("b8", new Color(1, 2, 3));
        assertTrue("Values do not match", c_.getRGB() ==  c.getRGB());
        assertTrue("Object type does not match", ctx.getObject("b8", null) instanceof Color);
        
        URL u = new URL("http://www.netbeans.org/download/");
        ctx.putURL("b9", u);
        URL u_ = ctx.getURL("b9", null);
        assertEquals("Values do not match", u_ , u);
        assertTrue("Object type does not match", ctx.getObject("b9", null) instanceof URL);
        
        String sa[] = new String[]{"aaa", "bbb", "ccc"};
        ctx.putStringArray("b0", '#', sa);
        String sa_[] = ctx.getStringArray("b0", '#', new String[]{"1", "2", "3"});
        assertEquals("Values do not match", sa_[0] , "aaa");
        assertEquals("Values do not match", sa_[1] , "bbb");
        assertEquals("Values do not match", sa_[2] , "ccc");
        
    }


    public void testObjectRef() throws Exception {
        BasicContext rootCtx = FileSystemContextFactory.createContext(getRoot());
        BasicContext basicCtx = rootCtx.createSubcontext("someCOTOXO");
        BasicContext basicCtx2 = basicCtx.createSubcontext("subocontexto");
        Context ctx = SpiUtils.createContext(basicCtx);
        Context ctx2 = SpiUtils.createContext(basicCtx2);
        
        Object o = new JLabel("my label Y");
        ctx.putObject("obj25", o);
        ObjectRef or = SpiUtils.createObjectRef(basicCtx, "obj25");

        ctx.putObject("ref", or);;
        Object val = ctx.getObject("ref", MY_NULL);
        assertTrue("Retrieved object cannot be null", val != MY_NULL);
        assertTrue("Retrived object is not the same", val instanceof JLabel);
        assertTrue("Retrived object is not the same", ((JLabel)val).getText().equals("my label Y"));

        ObjectRef or2 = ctx.getRef("ref");
        assertTrue("Retrieved object cannot be null", or2 != null);
        assertTrue("Retrived object is not the same", or2.getBindingName().equals(or.getBindingName()));
        assertTrue("Retrived object is not the same", or2.getContextAbsoluteName().equals(or.getContextAbsoluteName()));
        
        ObjectRef or3 = ctx.getRef("obj25");
        assertTrue("Retrieved object must be null", or3 == null);
        
        ObjectRef or4 = SpiUtils.createObjectRef(basicCtx, "ref");
        ctx2.putObject("refref", or4);
        ObjectRef or5 = SpiUtils.createObjectRef(basicCtx2, "refref");
        ctx2.putObject("refrefref", or5);
        val = ctx2.getObject("refrefref", MY_NULL);
        assertTrue("Retrieved object cannot be null", val != MY_NULL);
        assertTrue("Retrived object is not the same", val instanceof JLabel);
        assertTrue("Retrived object is not the same", ((JLabel)val).getText().equals("my label Y"));

        ObjectRef or6 = ctx2.getRef("refrefref");
        assertTrue("Retrieved object cannot be null", or6 != null);
        assertTrue("Retrived object is not the same", or6.equals(or5));
        
        getRootContext().destroySubcontext("someCOTOXO");
    }
    

// copied from org.netbeans.modules.registry.olddssimpl.Utils.
// this can be easily broken if not properly synchronized with changes
// in InstanceDataObject, org.netbeans.modules.registry.olddssimpl.Utils and
// here.
    private static final char OPEN = '[';
    private static final char CLOSE = ']';
    private static final int MAX_FILENAME_LENGTH = 50;
    private static String escapeAndCut (String name) {
        int maxLen = MAX_FILENAME_LENGTH;
        
        String ename = escape(name);
        if (ename.length() <= maxLen)  return ename;
        String hash = Integer.toHexString(ename.hashCode());
        maxLen = (maxLen > hash.length()) ? (maxLen-hash.length()) / 2 :1;
        String start = ename.substring(0, maxLen);        
        String end = ename.substring(ename.length() - maxLen);                    

        return start + hash + end;
    }
    
    private static String escape (String text) {
        boolean spacenasty = text.startsWith(" ") || text.endsWith(" ") || text.indexOf("  ") != -1; // NOI18N
        int len = text.length ();
        StringBuffer escaped = new StringBuffer (len);
        for (int i = 0; i < len; i++) {
            char c = text.charAt (i);
            // For some reason Windoze throws IOException if angle brackets in filename...
            if (c == '/' || c == ':' || c == '\\' || c == OPEN || c == CLOSE || c == '<' || c == '>' ||
                    // ...and also for some other chars (#16479):
                    c == '?' || c == '*' || c == '|' ||
                    (c == ' ' && spacenasty) ||
                    c == '.' || c == '"' || c < '\u0020' || c > '\u007E' || c == '#') {
                // Hex escape.
                escaped.append ('#');
                String hex = Integer.toString (c, 16).toUpperCase ();
                if (hex.length () < 4) escaped.append ('0');
                if (hex.length () < 3) escaped.append ('0');
                if (hex.length () < 2) escaped.append ('0');
                escaped.append (hex);
            } else {
                escaped.append (c);
            }
        }
        return escaped.toString ();
    }

    protected Context getRootContext() {
        return Context.getDefault();
    }

    protected FileObject getRoot() {
        return Repository.getDefault ().getDefaultFileSystem ().getRoot ();
    }
    
    protected FileObject findResource(String resource) {
        return Repository.getDefault ().getDefaultFileSystem().findResource (resource);
    }
}
