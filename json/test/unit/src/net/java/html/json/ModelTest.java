/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2012 Oracle and/or its affiliates. All rights reserved.
 *
 * Oracle and Java are registered trademarks of Oracle and/or its affiliates.
 * Other names may be trademarks of their respective owners.
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
 * nbbuild/licenses/CDDL-GPL-2-CP.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the GPL Version 2 section of the License file that
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
 * Portions Copyrighted 2012 Sun Microsystems, Inc.
 */
package net.java.html.json;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import static org.testng.Assert.*;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 *
 * @author Jaroslav Tulach <jtulach@netbeans.org>
 */
@Model(className = "Modelik", properties = {
    @Property(name = "value", type = int.class),
    @Property(name = "count", type = int.class),
    @Property(name = "unrelated", type = long.class),
    @Property(name = "names", type = String.class, array = true),
    @Property(name = "values", type = int.class, array = true),
    @Property(name = "people", type = Person.class, array = true),
})
public class ModelTest {
    private Modelik model;
    private static Modelik leakedModel;
    
    @BeforeMethod
    public void createModel() {
        model = new Modelik();
    }
    
    @Test public void classGeneratedWithSetterGetter() {
        model.setValue(10);
        assertEquals(10, model.getValue(), "Value changed");
    }
    
    @Test public void computedMethod() {
        model.setValue(4);
        assertEquals(16, model.getPowerValue());
    }
    
    @Test public void equalsAndHashCode() {
        Modelik m1 = new Modelik(10, 20, 30, "changed", "firstName");
        Modelik m2 = new Modelik(10, 20, 30, "changed", "firstName");
        
        assertTrue(m1.equals(m2), "They are the same");
        assertEquals(m1.hashCode(), m2.hashCode(), "Hashcode is the same");
        
        m1.setCount(33);
        
        assertFalse(m1.equals(m2), "No longer the same");
        assertFalse(m1.hashCode() == m2.hashCode(), "No longe is hashcode is the same");
    }
    
    @Test public void arrayIsMutable() {
        assertEquals(model.getNames().size(), 0, "Is empty");
        model.getNames().add("Jarda");
        assertEquals(model.getNames().size(), 1, "One element");
    }

    @Test public void arrayChangesNotNotifiedUntilInitied() {
        model.getNames().add("Hello");
        model.getNames().remove("Hello");
        assertTrue(model.getNames().isEmpty(), "No empty");
    }
    
    @Test public void arrayChangesNotified() {
        model.getNames().add("Hello");
        
        Iterator<String> it = model.getNames().iterator();
        assertEquals(it.next(), "Hello");
        it.remove();
        
        ListIterator<String> lit = model.getNames().listIterator();
        lit.add("Jarda");
    }

    @Test public void autoboxedArray() {
        model.getValues().add(10);
        
        assertEquals(model.getValues().get(0), Integer.valueOf(10), "Really ten");
    }

    @Test public void derivedArrayProp() {
        model.setCount(10);
        
        List<String> arr = model.getRepeat();
        assertEquals(arr.size(), 10, "Ten items: " + arr);
        
        model.setCount(5);
        
        arr = model.getRepeat();
        assertEquals(arr.size(), 5, "Five items: " + arr);

    }
    
    @Test public void derivedPropertiesAreNotified() {
        model.setValue(33);
        
        
        
        model.setUnrelated(44);
    }

    @Test public void computedPropertyCannotWriteToModel() {
        leakedModel = model;
        try {
            String res = model.getNotAllowedWrite();
            fail("We should not be allowed to write to the model: " + res);
        } catch (IllegalStateException ex) {
            // OK, we can't read
        }
    }

    @Test public void computedPropertyCannotReadToModel() {
        leakedModel = model;
        try {
            String res = model.getNotAllowedRead();
            fail("We should not be allowed to read from the model: " + res);
        } catch (IllegalStateException ex) {
            // OK, we can't read
        }
    }
    
    @ComputedProperty
    static int powerValue(int value) {
        return value * value;
    }
    
    @ComputedProperty
    static String notAllowedRead() {
        return "Not allowed callback: " + leakedModel.getUnrelated();
    }

    @ComputedProperty
    static String notAllowedWrite() {
        leakedModel.setUnrelated(11);
        return "Not allowed callback!";
    }
    
    @ComputedProperty
    static List<String> repeat(int count) {
        return Collections.nCopies(count, "Hello");
    }
    
    public @Test void hasPersonPropertyAndComputedFullName() {
        List<Person> arr = model.getPeople();
        assertEquals(arr.size(), 0, "By default empty");
        Person p = null;
        if (p != null) {
            String fullNameGenerated = p.getFullName();
            assertNotNull(fullNameGenerated);
        }
    }
}
