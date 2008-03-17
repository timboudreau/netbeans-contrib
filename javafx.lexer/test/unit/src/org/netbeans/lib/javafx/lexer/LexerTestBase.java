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

package org.netbeans.lib.javafx.lexer;

import java.util.ConcurrentModificationException;
import javax.swing.text.Document;
import org.netbeans.api.javafx.lexer.JavaFXTokenId;
import org.netbeans.api.lexer.Language;
import org.netbeans.api.lexer.TokenHierarchy;
import org.netbeans.api.lexer.TokenSequence;
import org.netbeans.lib.lexer.test.LexerTestUtilities;
import org.netbeans.lib.lexer.test.ModificationTextDocument;
import static org.junit.Assert.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The base class for the JUnit 4 Lexer Tests.
 * @author Victor G. Vasilyev
 */
public class LexerTestBase {
    private static final boolean DEBUG = true; // TODO get it from propery file
    private static final Logger LOG = DEBUG ?
            Logger.getLogger(JavaFXLexerStateController.class.getName()) : null;

    private Document doc;
    private TokenHierarchy<?> hi;
    private TokenSequence<?> ts;

    protected void setUp() {
        initJavaFXDocument();
        initTokenHierarchy();
        initTokenSequence();
    }

    protected void tearDown() {
        doc =null;
        hi = null;
        ts = null;
    }

    protected void assertNextTokenIs(JavaFXTokenId id, String text, int offset) {
        assertTrue(ts.moveNext());
        LexerTestUtilities.assertTokenEquals(ts,id, text, offset);
    }
    
    protected void setSource(String text)  throws Exception {
        if(DEBUG) {
            System.out.println("LexerTest source=[{" + text + "}]");
            LOG.log(Level.INFO, "LexerTest source=[{0}]", text);
        }
        doc.insertString(0, text, null);
        // Last token sequence should throw exception - new must be obtained
        try {
            ts.moveNext();
            fail("TokenSequence.moveNext() did not throw exception as expected.");
        } catch (ConcurrentModificationException e) {
            // Expected exception
        }
        ts = hi.tokenSequence();
    }

    private void initJavaFXDocument() {
        doc = new ModificationTextDocument();
        assertNotNull(doc );
        // Assign a language to the document
        doc.putProperty(Language.class,JavaFXTokenId.language());
    }
    
    private void initTokenSequence() {
        ts = hi.tokenSequence();
        assertFalse(ts.moveNext());
    }
    
    private void initTokenHierarchy() {
        hi = TokenHierarchy.get(doc);
        assertNotNull("Null token hierarchy for document", hi);
    }

}
