/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2004 Sun
 * Microsystems, Inc. All Rights Reserved.
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
