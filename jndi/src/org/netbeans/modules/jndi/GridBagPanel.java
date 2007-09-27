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

package org.netbeans.modules.jndi;

import java.awt.Component;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import javax.swing.JPanel;



/** Panel with GridBagLayout
 *
 * @author Tomas Zezula
 */
class GridBagPanel extends JPanel {

    public GridBagPanel() {
        this.setLayout(new GridBagLayout());
    }

    /** Adds componet to panel
     *  @param component the component to be inserted
     *  @param x the horizontal position
     *  @param y the vertical position
     *  @param width the width of the component
     *  @param height the height of the component
     *  @param top the top inset
     *  @param left the left inset
     *  @param bottom the bottom inset
     *  @param right the right inset
     */
    protected void add (Component component, int x, int y, int width, int height,int fill, int anchor,int ipadx, int ipady, double weightx, double weighty, int top, int left, int bottom, int right) {
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = x;
        c.gridy = y;
        c.gridwidth = width;
        c.gridheight = height;
        c.fill = fill;
        c.anchor = anchor;
        c.anchor = anchor;
        c.weightx = weightx;
        c.weighty = weighty;
        c.ipadx=ipadx;
        c.ipady=ipady;
        c.insets = new Insets(top,left,bottom,right);
        ((GridBagLayout)this.getLayout()).setConstraints(component,c);
        this.add(component);
    }

    protected void add (Component component, int x, int y, int width, int height,int fill, int anchor,double weightx, double weighty, int top, int left, int bottom, int right){
        add (component, x, y, width, height, fill, anchor, 0, 0,weightx, weighty, top, left, bottom, right);
    }

    protected void add (Component component, int x, int y, int width, int height,int fill, int anchor, int top, int left, int bottom, int right) {
        add(component,x,y,width,height,fill,anchor,0,0,top,left,bottom,right);
    }

    protected void add (Component component, int x, int y, int width, int height,int fill, int top, int left, int bottom, int right) {
        add(component,x,y,width,height,fill,GridBagConstraints.NORTHWEST,0,0,top,left,bottom,right);
    }

    protected void add (Component component, int x, int y, int width, int height,int top, int left, int bottom, int right) {
        add(component,x,y,width,height,GridBagConstraints.BOTH,GridBagConstraints.NORTHWEST,0,0,top,left,bottom,right);
    }

    /** Adds component to panel
     *  @param component the component to be inserted
     *  @param x the horizontal position
     *  @param y the vertical position
     *  @param width the width of the component
     *  @param height the height of the component
     */
    protected void add (Component component, int x, int y, int width, int height) {
        add(component,x,y,width,height,GridBagConstraints.BOTH,GridBagConstraints.NORTHWEST,0,0,0,0,0,0);
    }
}



/*
 * <<Log>>
 *  12   Gandalf   1.11        1/14/00  Tomas Zezula    
 *  11   Gandalf   1.10        12/17/99 Tomas Zezula    
 *  10   Gandalf   1.9         12/15/99 Tomas Zezula    
 *  9    Gandalf   1.8         12/15/99 Tomas Zezula    
 *  8    Gandalf   1.7         11/27/99 Patrik Knakal   
 *  7    Gandalf   1.6         11/5/99  Tomas Zezula    
 *  6    Gandalf   1.5         10/23/99 Ian Formanek    NO SEMANTIC CHANGE - Sun
 *       Microsystems Copyright in File Comment
 *  5    Gandalf   1.4         7/9/99   Ales Novak      localization + code 
 *       requirements followed
 *  4    Gandalf   1.3         6/10/99  Ales Novak      gemstone support + 
 *       localizations
 *  3    Gandalf   1.2         6/9/99   Ian Formanek    ---- Package Change To 
 *       org.openide ----
 *  2    Gandalf   1.1         6/8/99   Ales Novak      sources beautified + 
 *       subcontext creation
 *  1    Gandalf   1.0         6/4/99   Ales Novak      
 * $
 */
