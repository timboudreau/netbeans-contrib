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


/*
 * Mark.java
 *
 * Created on March 5, 2003, 4:35 PM
 */
package org.netbeans.swing.scrollbars.spi;


/**Interface representing an immutable marked position
 *
 * @version 1.0
 * @author  Tim Boudreau
 */
public interface Mark {
    /** Get the point in the model's range from 0 to <code>
     * getMaximumMarkLocation()</code> at which this mark starts.
     * @return The start index
     */
    public int getStart ();
    /** Get the length of the mark, from its start
     * @return The length
     */
    public int getLength ();
    /** Get the text represented by the mark.  MarkedScrollbarUI
     * uses this to produce the tooltip.
     * @return The text
     */    
    public String getText ();
    /** Get a hint about how the UI should render this mark.
     * Only the string &quot;color&quot; is supported by
     * MarkedScrollbarUI.  Subclasses could support additional
     * hints.
     * @param key String key for the hint
     * @return The object representing the hint, or null if not
     * recognized
     */    
    public Object get (String key);

    /**
     * Called when the mark has been clicked, after it has been scrolled into view. It is not necessary to do
     * anything here, but it can be used to, for example, select some text, etc.
     */
    public void select();

}
