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

package org.netbeans.modules.vcscore.annotation;

import java.util.Hashtable;
import java.util.Enumeration;
import java.util.ArrayList;
import java.util.Collections;

import org.openide.nodes.*;
import org.openide.actions.*;
import org.openide.util.actions.SystemAction;
import org.openide.util.NbBundle;
import java.beans.PropertyEditor;


/**
 *
 * @author  Milos Kleint
 */
public class AnnotPatternNode extends AbstractNode implements  java.io.Serializable {

    public static final String TYPE_TEXT = "Text";
    public static final String TYPE_VARIABLE = "Variable";
    public static final String TYPE_CONDITION = "Condition";
    public static final String TYPE_PARENT = "Parent";
    
    public static String[] VARIABLES_ARRAY = new String[] 
                                    {AnnotationSupport.ANNOTATION_PATTERN_FILE_NAME,
                                     AnnotationSupport.ANNOTATION_PATTERN_STATUS,
                                     AnnotationSupport.ANNOTATION_PATTERN_REVISION,
                                     AnnotationSupport.ANNOTATION_PATTERN_STICKY,
                                     AnnotationSupport.ANNOTATION_PATTERN_LOCKER,
                                     AnnotationSupport.ANNOTATION_PATTERN_SIZE,
                                     AnnotationSupport.ANNOTATION_PATTERN_ATTR,
                                     AnnotationSupport.ANNOTATION_PATTERN_DATE,
                                     AnnotationSupport.ANNOTATION_PATTERN_TIME };

    public static String[] VARIABLES_ARRAY_DISP_NAMES = new String[] {
                                     AnnotationSupport.ANNOTATION_PATTERN_FILE_NAME,
                                     AnnotationSupport.ANNOTATION_PATTERN_STATUS,
                                     AnnotationSupport.ANNOTATION_PATTERN_REVISION,
                                     AnnotationSupport.ANNOTATION_PATTERN_STICKY,
                                     AnnotationSupport.ANNOTATION_PATTERN_LOCKER,
                                     AnnotationSupport.ANNOTATION_PATTERN_SIZE,
                                     AnnotationSupport.ANNOTATION_PATTERN_ATTR,
                                     AnnotationSupport.ANNOTATION_PATTERN_DATE,
                                     AnnotationSupport.ANNOTATION_PATTERN_TIME };
    
    private boolean root;
    private String type = ""; // Has to be initially defined to prevent NPE
    private static final String ICON_NODE = "org/netbeans/modules/vcscore/annotation/annotationIcon";    
    private static final String ICON_NODE_TRUE = "org/netbeans/modules/vcscore/annotation/annotationIcon_true";        
    private static final String ICON_NODE_FALSE = "org/netbeans/modules/vcscore/annotation/annotationIcon_false";        
    
    private static String TRUE;
    private static String FALSE;
    private static String IF_DEFINED;
    
    private static final long serialVersionUID = 1717999205778940893L;    
    
    public AnnotPatternNode(Children children) {
        super(children);
        setRoot(false);
        setIconBase(ICON_NODE);
        setShortDescription(NbBundle.getMessage(AnnotPatternNode.class, "AnnotPatternNode.Description"));
        TRUE = NbBundle.getBundle(AnnotPatternNode.class).getString("ANNOT_NODE_NAME_TRUE");
        FALSE = NbBundle.getBundle(AnnotPatternNode.class).getString("ANNOT_NODE_NAME_FALSE");
        IF_DEFINED = NbBundle.getBundle(AnnotPatternNode.class).getString("ANNOT_NODE_NAME_IF_DEFINED");
    }
    
    
    public static AnnotPatternNode createInstance(String type) {
        Children children;
        Index.ArrayChildren indChild = null;
        if (type.equals(TYPE_CONDITION) || type.equals(TYPE_PARENT)) {
            indChild = new Index.ArrayChildren();
            children = indChild;
        } else {
            children = Children.LEAF;
        }
        AnnotPatternNode node = new AnnotPatternNode(children);
        if (type.equals(TYPE_CONDITION) || type.equals(TYPE_PARENT)) {
            node.getCookieSet().add(indChild);
        }
        node.setType(type);
        return node;
    }
    
    public void setName(String name) {
        super.setName(name);
        if (name.equals(TRUE)) {
            setIconBase(ICON_NODE_TRUE);
        }
        if (name.equals(FALSE)) {
            setIconBase(ICON_NODE_FALSE);
        }
    }
    
    public boolean isRoot() {
        return root;
    }
    
    public void setRoot(boolean root) {
        this.root = root;
    }
  
    public void setType(String type) {
        this.type = type;
    }
    
    public String getType() {
        return type;
    }
    
    
    
    public boolean canCopy() {
        return false;//!root;
    }
    
    public boolean canCut() {
        return false; //!root;
    }
    
    public boolean canDestroy() {
        return !root;
    }
        
    public boolean canRename() {
        return getType().equals(TYPE_TEXT);
    }
    
    
    public String getStringRepresentation() {
        if (getType().equals(TYPE_TEXT)) {
            return getName();
        }
        if (getType().equals(TYPE_VARIABLE)) {
            return "${" + getName() + "}";
        }
        if (getType().equals(TYPE_CONDITION)) {
            Node[] subNodes = getChildren().getNodes();
            AnnotPatternNode trueNode;
            AnnotPatternNode falseNode;
            StringBuffer buff = new StringBuffer("");
            if (subNodes[0].getName().equals(TRUE)) {
                trueNode = (AnnotPatternNode)subNodes[0];
                falseNode = (AnnotPatternNode)subNodes[1];
            } else {
                trueNode = (AnnotPatternNode)subNodes[1];
                falseNode = (AnnotPatternNode)subNodes[0];
            }
            buff.append("$[? ");
            buff.append(getName());
            buff.append("] [");
            buff.append(trueNode.getStringRepresentation());
            buff.append("] [");
            buff.append(falseNode.getStringRepresentation());
            buff.append("]");
            return buff.toString();
        }
        if (getType().equals(TYPE_PARENT)) {
            Node[] subNodes = getChildren().getNodes();
            if (subNodes == null) {
                return "";
            }
            StringBuffer buff = new StringBuffer("");
            for (int i = 0; i < subNodes.length; i++) {
                AnnotPatternNode node =(AnnotPatternNode)subNodes[i];
                buff.append(node.getStringRepresentation());
            }
            return buff.toString();
        }
        return "";
    }
    
    public SystemAction [] getActions() {
        ArrayList actions = new ArrayList();
        if (getType().equals(TYPE_PARENT)) {
            actions.add(SystemAction.get(AddTextAction.class));
            actions.add(SystemAction.get(AddVariableAction.class));
            actions.add(SystemAction.get(AddIfDefinedAction.class));
        }
        if (!getType().equals(TYPE_PARENT)) {
            actions.add(SystemAction.get(MoveUpAction.class));
            actions.add(SystemAction.get(MoveDownAction.class));
            actions.add(null);
            actions.add(SystemAction.get(RenameAction.class));
            actions.add(SystemAction.get(DeleteAction.class));
        }
 
        SystemAction[] array = new SystemAction [actions.size()];
        actions.toArray(array);
        return array;
    }
    
    public java.lang.String getDisplayName() {
        if (getType().equals(TYPE_PARENT)) {
            return getName();
        }
        if (getType().equals(TYPE_CONDITION)) {
            return IF_DEFINED + getName();
        }
        return getType() +  ":" + getName();
    }
    
    

    public Sheet createSheet() {
        Sheet sheet = Sheet.createDefault();
        Sheet.Set set = sheet.get(Sheet.PROPERTIES);
        if (getType().equals(TYPE_PARENT)) return sheet;
        createProperties(this, set);
        return sheet;
    }
    
    private void createProperties(final AnnotPatternNode node, final Sheet.Set set) {
        if (node.getType().equals(TYPE_TEXT)) {
            set.put(new PropertySupport.Name(node,
               NbBundle.getBundle(AnnotPatternNode.class).getString("ANNOT_PROP_NAME"),
               NbBundle.getBundle(AnnotPatternNode.class).getString("ANNOT_PROP_NAME_HINT")));
        }
        if (node.getType().equals(TYPE_VARIABLE) || node.getType().equals(TYPE_CONDITION)) {
            PropertySupport.ReadWrite rw = new PropertySupport.ReadWrite("name", String.class,
               NbBundle.getBundle(AnnotPatternNode.class).getString("ANNOT_PROP_VARIABLE"),
               NbBundle.getBundle(AnnotPatternNode.class).getString("ANNOT_PROP_VARIABLE_HINT")) {
                   public PropertyEditor getPropertyEditor() {
                       return new AnnotVariablePropertyEditor(AnnotPatternNode.VARIABLES_ARRAY_DISP_NAMES);
                   }
                   
                   public void setValue(Object value) {
                       AnnotPatternNode.this.setName(value.toString());
                   }
                   
                   public Object getValue() {
                       return AnnotPatternNode.this.getName();
                   }
            };
            set.put(rw);
        }
    }    

    
}
