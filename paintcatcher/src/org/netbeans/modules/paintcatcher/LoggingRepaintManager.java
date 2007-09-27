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
 *//*
 * LoggingRepaintManager.java
 *
 * Created on February 23, 2004, 8:24 PM
 */

package org.netbeans.modules.paintcatcher;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.RepaintManager;

/** A repaint manager which will logs information about interesting events.
 *
 * @author  Tim Boudreau
 */
class LoggingRepaintManager extends RepaintManager {
    private Filter filter;
    private Logger logger;
    private RepaintManager orig = null;
    /** Creates a new instance of LoggingRepaintManager */
    public LoggingRepaintManager(Filter f, Logger l) {
        this.filter = f;
        this.logger = l;
    }
    
    public void setEnabled (boolean val) {
        if (isEnabled() != val) {
            if (val) {
                enable();
            } else {
                disable();
            }
        }
    }
    
    public boolean isEnabled() {
        return orig != null;
    }
    
    private void enable() {
        orig = currentManager(new JLabel()); //could be null for standard impl
        setCurrentManager(this);
    }
    
    private void disable() {
        setCurrentManager(orig);
        orig = null;
    }
    
    private boolean hasValidateMatches = false;
    private boolean hasDirtyMatches = false;
    public synchronized void addDirtyRegion(JComponent c, int x, int y, int w, int h) {
        if (w != 0 && h != 0 && filter.match (c)) {
            logger.log ("addDirtyRegion " + x + "," + y + "," + w + "," + h, c);
            hasDirtyMatches = true;
        }
        super.addDirtyRegion (c, x, y, w, h);
    }
    
    public synchronized void addInvalidComponent(JComponent c) {
        if (filter.match(c)) {
            logger.log ("addInvalidComponent", c);
            hasValidateMatches = true;
        }
        super.addInvalidComponent(c);
    }
    
    public void paintDirtyRegions() {
        if (hasDirtyMatches) {
            logger.log("paintDirtyRegions");
            hasDirtyMatches = false;
        }
        super.paintDirtyRegions();
    }
    
    public void validateInvalidComponents() {
        if (hasValidateMatches) {
            logger.log("validateInvalidComponents");
            hasValidateMatches = false;
        }
        super.validateInvalidComponents();
    }    
    
}
