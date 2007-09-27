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

package org.netbeans.spi.looks;

import java.util.Enumeration;
import java.util.TooManyListenersException;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.netbeans.junit.NbTest;
import org.netbeans.junit.NbTestCase;
import org.netbeans.junit.NbTestSuite;
import org.netbeans.spi.looks.ChangeableLookProvider;
import org.netbeans.spi.looks.GoldenValue;
import org.netbeans.spi.looks.Look;
import org.openide.util.Enumerations;

/** Tests whether all vales returned from a Node are identical with
 * the values server by associated look
 */
public class DecoratorSelectorEventsTest extends NbTestCase {

    // Golden values for the three looks which will be composed
    
    private GoldenValue goldenValues[][];
    private static GoldenValue gvForTypes[] = GoldenValue.createGoldenValues();
    
    // Methods of testCase -----------------------------------------------------
    
    public DecoratorSelectorEventsTest(String testName) {
        super(testName);
    }
    
    // Test methods ------------------------------------------------------------
    
    public void testEventFromDelegate() {
        
        
        // Create all necessary looks
        Look primaryLook = new SampleLook( "CL1" );
        Look decoratingLook = new SampleLook( "CL2" );
        Look replacementLook = new SampleLook( "CL3" );
        TestChangeableProvider provider = new TestChangeableProvider( primaryLook );
        LookSelector primarySelector = Selectors.selector( provider );                
        // Create selector which decorates
        LookSelector decoratingSelector = Selectors.decorator( primarySelector, decoratingLook );
        
        // Add listeners
        NamespaceSelectorEventsTest.TestLookSelectorListener tlsl1 = 
            new NamespaceSelectorEventsTest.TestLookSelectorListener();
        NamespaceSelectorEventsTest.TestLookSelectorListener tlsl2 = 
            new NamespaceSelectorEventsTest.TestLookSelectorListener();                
        org.netbeans.modules.looks.Accessor.DEFAULT.addSelectorListener( primarySelector, tlsl1 ); 
        org.netbeans.modules.looks.Accessor.DEFAULT.addSelectorListener( decoratingSelector, tlsl2 ); 
        
        // Fire the change and test
        provider.fireChange( replacementLook );
        
        assertEquals( "One event in primarySelector", 1, tlsl1.events.size() );
        assertEquals( "One event in decoratorSelector", 1, tlsl2.events.size() );
        
        
    }
    
    // Private classes ---------------------------------------------------------
    
    private static class TestChangeableProvider implements ChangeableLookProvider {
        
        private Look look;
        
        private ChangeListener listener;
        
        public TestChangeableProvider( Look look ) {
            this.look = look;
        }
        
        public void addChangeListener(ChangeListener listener) throws TooManyListenersException {
            if ( this.listener != null ) {
                throw new TooManyListenersException();
            }
            else {
                this.listener = listener;
            }
        }
        
        public Object getKeyForObject(Object representedObject) {
            return this;
        }
        
        public Enumeration getLooksForKey(Object key) {
            return Enumerations.singleton(look);
        }
        
        public void fireChange( Look look ) {
            this.look = look;
            listener.stateChanged( new ChangeEvent( this ) );
        }
        
    }
    
    
}
