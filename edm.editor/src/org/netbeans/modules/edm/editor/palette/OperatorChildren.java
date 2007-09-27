/*
 * The contents of this file are subject to the terms of the Common
 * Development
The contents of this file are subject to the terms of either the GNU
General Public License Version 2 only ("GPL") or the Common
Development and Distribution License("CDDL") (collectively, the
"License"). You may not use this file except in compliance with the
License. You can obtain a copy of the License at
http://www.netbeans.org/cddl-gplv2.html
or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
specific language governing permissions and limitations under the
License.  When distributing the software, include this License Header
Notice in each file and include the License file at
nbbuild/licenses/CDDL-GPL-2-CP.  Sun designates this
particular file as subject to the "Classpath" exception as provided
by Sun in the GPL Version 2 section of the License file that
accompanied this code. If applicable, add the following below the
License Header, with the fields enclosed by brackets [] replaced by
your own identifying information:
"Portions Copyrighted [year] [name of copyright owner]"

Contributor(s):
 *
 * Copyright 2006 Sun Microsystems, Inc. All Rights Reserved.

If you wish your version of this file to be governed by only the CDDL
or only the GPL Version 2, indicate your decision by adding
"[Contributor] elects to include this software in this distribution
under the [CDDL or GPL Version 2] license." If you do not indicate a
single choice of license, a recipient has the option to distribute
your version of this file under either the CDDL, the GPL Version 2 or
to extend the choice of license to its licensees as provided above.
However, if you add GPL Version 2 code and therefore, elected the GPL
Version 2 license, then the option applies only if the new code is
made subject to such option by the copyright holder.
 *
 */

package org.netbeans.modules.edm.editor.palette;

import java.util.ArrayList;
import java.util.List;

import org.openide.nodes.Index;
import org.openide.nodes.Node;

/**
 *
 * @author nithya
 */
public class OperatorChildren extends Index.ArrayChildren {

    private Category category;

    private String[][] items = new String[][]{
        {"0", "Table Operators", "org/netbeans/modules/edm/editor/resources/join_view.png", "Join"},
        {"1", "Table Operators", "org/netbeans/modules/edm/editor/resources/groupby.gif", "Group By"},
        
     /*  {"2", "String Operators", "org/netbeans/modules/sql/framework/ui/resources/images/length.png", "Length"},        
        {"3", "String Operators", "org/netbeans/modules/sql/framework/ui/resources/images/replace.gif", "Replace"},
        {"4", "String Operators", "org/netbeans/modules/sql/framework/ui/resources/images/rightTrim.png", "Right Trim"},
        {"5", "String Operators", "org/netbeans/modules/sql/framework/ui/resources/images/stringToHex.png", "StringToHex"},        
        {"6", "String Operators", "org/netbeans/modules/sql/framework/ui/resources/images/leftTrim.png", "Left Trim"},
        {"7", "String Operators", "org/netbeans/modules/sql/framework/ui/resources/images/numberToHex.png", "Number To Hex"},
        {"8", "String Operators", "org/netbeans/modules/sql/framework/ui/resources/images/substring.gif", "Substring"},        
        {"9", "String Operators", "org/netbeans/modules/sql/framework/ui/resources/images/uppercase.gif", "UpperCase"},
        {"10", "String Operators", "org/netbeans/modules/sql/framework/ui/resources/images/lowercase.gif", "LowerCase"},
        {"11", "String Operators", "org/netbeans/modules/sql/framework/ui/resources/images/concat.gif", "Concatenation"},
        
        {"12", "Cleansing Operators", "org/netbeans/modules/sql/framework/ui/resources/images/normalizePerson.png", "Normalize Name"},        
        {"13", "Cleansing Operators", "org/netbeans/modules/sql/framework/ui/resources/images/parseAddress.png", "Parse Address"},
        {"14", "Cleansing Operators", "org/netbeans/modules/sql/framework/ui/resources/images/parseBusinessName.png", "Parse Business Name"},
        
        {"15", "SQL Specific Operators", "org/netbeans/modules/sql/framework/ui/resources/images/Case.png", "Case"},        
        {"16", "SQL Specific Operators", "org/netbeans/modules/sql/framework/ui/resources/images/castAs.png", "Cast As"},
        {"17", "SQL Specific Operators", "org/netbeans/modules/sql/framework/ui/resources/images/coalesce.png", "Coalesce"},
        {"18", "SQL Specific Operators", "org/netbeans/modules/sql/framework/ui/resources/images/Count.png", "Count"},        
        {"19", "SQL Specific Operators", "org/netbeans/modules/sql/framework/ui/resources/images/null.png", "Null"},
        {"20", "SQL Specific Operators", "org/netbeans/modules/sql/framework/ui/resources/images/nullif.png", "NullIf"},
        {"21", "SQL Specific Operators", "org/netbeans/modules/sql/framework/ui/resources/images/userFunction.png", "UserFunction"},
        
        {"22", "Date Operators", "org/netbeans/modules/sql/framework/ui/resources/images/NOW2.png", "Now"},        
        {"23", "Date Operators", "org/netbeans/modules/sql/framework/ui/resources/images/DateToChar.png", "DateToString"},
        {"24", "Date Operators", "org/netbeans/modules/sql/framework/ui/resources/images/datePart.png", "DatePart"},
        {"25", "Date Operators", "org/netbeans/modules/sql/framework/ui/resources/images/DateAddition.png", "Date Addition"},        
        {"26", "Date Operators", "org/netbeans/modules/sql/framework/ui/resources/images/DateSubtraction.png", "Date Subtraction"},
        {"27", "Date Operators", "org/netbeans/modules/sql/framework/ui/resources/images/String.png", "StringToDate"},
       
        {"28", "Function Operators", "org/netbeans/modules/sql/framework/ui/resources/images/average.png", "Average"},        
        {"29", "Function Operators", "org/netbeans/modules/sql/framework/ui/resources/images/division.png", "Division"},
        {"30", "Function Operators", "org/netbeans/modules/sql/framework/ui/resources/images/max.png", "Max"},
        {"31", "Function Operators", "org/netbeans/modules/sql/framework/ui/resources/images/min.png", "Min"},
        {"32", "Function Operators", "org/netbeans/modules/sql/framework/ui/resources/images/modulo.png", "Modulo"},        
        {"33", "Function Operators", "org/netbeans/modules/sql/framework/ui/resources/images/multiplication.png", "Multiplication"},
        {"34", "Function Operators", "org/netbeans/modules/sql/framework/ui/resources/images/sign.png", "Sign"},
        {"35", "Function Operators", "org/netbeans/modules/sql/framework/ui/resources/images/subtraction.gif", "Subtraction"},
        {"36", "Function Operators", "org/netbeans/modules/sql/framework/ui/resources/images/SUM.png", "Sum"},
        {"37", "Function Operators", "org/netbeans/modules/sql/framework/ui/resources/images/addition.gif", "Add"},*/
        
    };

    /**
     * 
     * @param Category 
     */
    public OperatorChildren(Category Category) {
        this.category = Category;
    }

    /**
     * 
     * @return childrenNodes List<Node>
     */
    protected java.util.List<Node> initCollection() {
        List<Node> childrenNodes = new ArrayList<Node>( items.length );
        for( int i=0; i<items.length; i++ ) {
            if( category.getName().equals( items[i][1] ) ) {
                Operator item = new Operator();
                item.setNumber(new Integer(items[i][0]));
                item.setCategory(items[i][1]);
                item.setImage(items[i][2]);
                item.setName(items[i][3]);
                childrenNodes.add(new OperatorNode(item));
            }
        }
        return childrenNodes;
    }

}