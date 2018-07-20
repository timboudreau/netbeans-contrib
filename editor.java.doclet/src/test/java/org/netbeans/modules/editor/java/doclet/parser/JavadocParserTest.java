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
 * Software is Leon Chiver. All Rights Reserved.
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
package org.netbeans.modules.editor.java.doclet.parser;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.List;
import org.netbeans.junit.NbTestCase;
import org.netbeans.modules.editor.java.doclet.ast.Attribute;
import org.netbeans.modules.editor.java.doclet.ast.Javadoc;
import org.netbeans.modules.editor.java.doclet.ast.Tag;


/**
 * @author leon chiver
 */
public class JavadocParserTest extends NbTestCase {

    private static JavadocParser parser;

    public JavadocParserTest(String testName) {
        super(testName);
        if (parser == null) {
            parser = new JavadocParser(new StringReader(""));
        }
    }
    
    public void testPositions() throws Exception {
        String content = readFile(JavadocParserTest.class.getResourceAsStream("javadoc2.txt"));
        parser.ReInit(new StringReader(content));
        Javadoc doc = parser.doc();
        // 
        assertNull(doc.getTagAtLine(0));
        assertNull(doc.getTagAtLine(1));
        assertNull(doc.getTagAtLine(2));
        // Tag 1
        Tag tag1 = doc.getTagAtLine(3);
        assertEquals("tag1", tag1.getName());
        // Attribute 1 of tag 1
        String attr1Name = "tag1_attr1";
        int[] pos = getPosition(content, attr1Name);
        assertNotNull(pos);
        assertNull(tag1.getAttributeAtPosition(pos[0], pos[1] - 1));
        Attribute attr1 = tag1.getAttributeAtPosition(pos[0], pos[1]);
        assertEquals(attr1Name, attr1.getName());
        assertSame(attr1, tag1.getAttributeAtPosition(pos[0], pos[1] + attr1Name.length() - 1));
        attr1 = tag1.getAttributeAtPosition(pos[0], pos[1] + attr1Name.length());
        assertNull(attr1);
        assertSame(tag1, doc.getTagAtLine(4));
        String attr2Name = "tag1_attr2";
        pos = getPosition(content, attr2Name);
        assertNull(tag1.getAttributeAtPosition(pos[0], pos[1] - 1));
        // Attribute 2 of tag 1
        Attribute attr2 = tag1.getAttributeAtPosition(pos[0], pos[1]);
        assertEquals(attr2Name, attr2.getName());
        assertSame(attr2, tag1.getAttributeAtPosition(pos[0], pos[1] + attr2Name.length() - 1));
        assertNull(tag1.getAttributeAtPosition(pos[0], pos[1] + attr2Name.length()));
        // Attribute's 2 value
        assertNull(tag1.getAttributeWithValueAtPosition(pos[0], pos[1] + attr2Name.length()));
        assertSame(attr2, tag1.getAttributeWithValueAtPosition(pos[0], pos[1] + attr2Name.length() + 1));
        assertSame(attr2, tag1.getAttributeWithValueAtPosition(
                pos[0], pos[1] + attr2Name.length() + attr2.getValue().length()));
        assertNull(tag1.getAttributeWithValueAtPosition(
                pos[0], pos[1] + attr2Name.length() + 1 + attr2.getValue().length()));
        // Attribute's 3 value
        String attr3Str = "tag1_attr3  =  ";
        pos = getPosition(content, attr3Str);
        Attribute attr3 = tag1.getAttributeAtPosition(pos[0], pos[1]);
        assertEquals("tag1_attr3", attr3.getName());
        assertNull(tag1.getAttributeAtPosition(pos[0], pos[1] + attr3.getName().length()));
        assertNull(tag1.getAttributeWithValueAtPosition(pos[0], pos[1] + attr3Str.length() - 1));
        assertSame(attr3, tag1.getAttributeWithValueAtPosition(pos[0], pos[1] + attr3Str.length()));
        //
        assertSame(tag1, doc.getTagAtLine(5));
        Tag tag2 = doc.getTagAtLine(6);
        assertEquals("tag2", tag2.getName());
    }
    
    public void testParse() throws Exception {
        parser.ReInit(JavadocParserTest.class.getResourceAsStream("javadoc1.txt"));
        Javadoc doc = parser.doc();
        
        // Check tag count
        assertEquals(5, doc.getTags().size());
        
        // Get param tag 
        List params = doc.getTagsByName("param");
        // Check param1
        Tag p1 = (Tag) params.get(0);
        assertEquals(1, p1.getBeginLine());
        assertEquals(5, p1.getNameBeginColumn());
        assertEquals(9, p1.getNameEndColumn());
        List p1Attrs = p1.getAttributeList();
        assertEquals(1, p1Attrs.size());
        // Attribute a1
        Attribute p1A = (Attribute) p1Attrs.get(0);
        assertEquals("a1", p1A.getName());
        assertEquals(1, p1A.getLine());
        assertEquals(20, p1A.getBeginColumn());
        assertEquals(21, p1A.getEndColumn());
        
        // Check param2
        Tag p2 = (Tag) params.get(1);
        assertEquals(2, p2.getBeginLine());
        List p2Attrs = p2.getAttributeList();
        assertEquals(1, p2Attrs.size());
        // Attribute a2
        Attribute p2A = (Attribute) p2Attrs.get(0);
        assertEquals("a2", p2A.getName());
        assertEquals(2, p2A.getLine());
        assertEquals(11, p2A.getBeginColumn());
        assertEquals(12, p2A.getEndColumn());
        
        // Check param3 
        Tag p3 = (Tag) params.get(2);
        assertEquals(9, p3.getBeginLine());
        assertEquals(2, p3.getNameBeginColumn());
        assertEquals(6, p3.getNameEndColumn());
        List p3Attrs = p3.getAttributeList();
        assertEquals(3, p3Attrs.size());
        // Attribute a3
        Attribute p3A1 = (Attribute) p3Attrs.get(0);
        assertEquals("a3", p3A1.getName());
        // Attribute some
        Attribute p3A2 = (Attribute) p3Attrs.get(1);
        assertEquals("some", p3A2.getName());
        // Attribute comment
        Attribute p3A3 = (Attribute) p3Attrs.get(2);
        assertEquals("comment", p3A3.getName());
        
        // Check deprecated tag
        List dep = doc.getTagsByName("deprecated");
        assertEquals(1, dep.size());
        Tag d = (Tag) dep.get(0);
        assertEquals(0, d.getBeginLine());
        assertEquals(10, d.getNameBeginColumn());
        assertEquals(19, d.getNameEndColumn());
        
        // Check hibernate tag
        List hib = doc.getTagsByName("hibernate.property");
        assertEquals(1, hib.size());
        Tag h = (Tag) hib.get(0);
        assertEquals(4, h.getBeginLine());
        assertEquals(14, h.getNameBeginColumn());
        assertEquals(31, h.getNameEndColumn());
        List hAttr = h.getAttributeList();
        assertEquals(5, hAttr.size());
        // Attribute name
        Attribute hA1 = (Attribute) hAttr.get(0);
        assertEquals("name", hA1.getName());
        assertEquals(4, hA1.getLine());
        assertEquals(33, hA1.getBeginColumn());
        assertEquals(36, hA1.getEndColumn());
        // Name's value
        assertEquals("'gigi'", hA1.getValue());
        assertEquals(4, hA1.getValueLine());
        assertEquals(38, hA1.getValueBeginColumn());
        assertEquals(43, hA1.getValueEndColumn());
        // Attribute column
        Attribute hA2 = (Attribute) hAttr.get(1);
        assertEquals("column", hA2.getName());
        assertEquals(4, hA2.getLine());
        assertEquals(45, hA2.getBeginColumn());
        assertEquals(50, hA2.getEndColumn());
        // Column's value
        assertEquals("DB_COLUMN", hA2.getValue());
        assertEquals(4, hA2.getValueLine());
        assertEquals(54, hA2.getValueBeginColumn());
        assertEquals(62, hA2.getValueEndColumn());
        // Attribute size
        Attribute hA3 = (Attribute) hAttr.get(2);
        assertEquals("size", hA3.getName());
        assertEquals(6, hA3.getLine());
        assertEquals(4, hA3.getBeginColumn());
        assertEquals(7, hA3.getEndColumn());
        // Size' value
        assertEquals("\"20\"", hA3.getValue());
        assertEquals(7, hA3.getValueLine());
        assertEquals(5, hA3.getValueBeginColumn());
        assertEquals(8, hA3.getValueEndColumn()); 
    }
    
    private String readFile(InputStream is) throws Exception {
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        StringBuffer buff = new StringBuffer();
        String line;
        while ((line = br.readLine()) != null) {
            buff.append(line).append('\n');
        }
        return buff.toString();
    }
    
    private int[] getPosition(String str, String substring) throws Exception {
        BufferedReader br = new BufferedReader(new StringReader(str));
        String line;
        int row = 0;
        while ((line = br.readLine()) != null) {
            int index = line.indexOf(substring);
            if (index != -1) {
                return new int[] { row, index + 1 };
            }
            row++;
        }
        return null;
    }
}
