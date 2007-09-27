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

package org.netbeans.modules.helpbuilder.tree;

import java.util.Vector;
import java.util.Locale;
import java.io.IOException;
import java.net.URL;

/**
 * A class for individual tree items.
 *
 * @author Richard Gregor
 * @version   1.1
 */

public class IndexTreeItem extends HelpTreeItem {

    private HelpTreeItem parent = null;
    private Vector children = new Vector();
    private String url= null;
    private String target = null;
    private Locale locale = null;
    private String mergeType = null;
    private boolean homeID = false;

    /**
     * Creates item with name
     *
     * @param name The name of item
     */
    public IndexTreeItem(String name){     
        super(name);
    }
    
    /**
     * Creates empty item
     */
    public IndexTreeItem(){
        super(null);
    }
    /**
     * Creates HelpTreeItem.
     *
     * @param name The name of item
     * @param target The target of item
     * @param url The external representation of url
     * @param homeID Set true if item is used as home page
     * @param locale The Locale of this item
     */
    public IndexTreeItem(String name, String target, String url, boolean homeID, Locale locale){
        this.name = name;
        this.target = target;
        this.url = url;
        this.homeID = homeID;
        this.locale = locale;
    }    
 
    /**
     * Returns the id for this item.
     */
    public String getTarget() {
        return target;
    }
    
    /**
     * Sets the id for this item.
     */
    public void setTarget(String target) {
        this.target = target;
    }
    
    /**
     *Returns the external representation of url for this item.
     */
    public String getURLSpec(){
        return url;
    }    

    /**
     *Sets the external representation of url for this item.
     */
    public void setURLSpec(String url){
        this.url = url;
    }    
    
    /**
     * Sets the merge type
     */
    public void setMergeType(String mergeType){
        this.mergeType = mergeType;
    }
    
    /**
     * Returns the merge type for the item
     */
    public String getMergeType(){
        return mergeType;
    }
 
    /**
     * Returns whether item is homeID or not
     */
    public boolean isHomeID(){
        return homeID;
    }
    
    /**
     * Sets this item as homeID
     */
    public void setHomeID(boolean value){
        this.homeID = value;
    }

    /**
     * Returns a String used when displaying the object.
     * Used by CellRenderers.
     *
     * @see TOCCellRenderer
     */
    public String toString() {
        if(isHomeID())
            return "<html><font color=green>"+name+"</font><html>";
        else
            return (name);
    }      
}


