/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at http://www.netbeans.org/cddl.html
 * or http://www.netbeans.org/cddl.txt.
 *
 * When distributing Covered Code, include this CDDL Header Notice in each file
 * and include the License file at http://www.netbeans.org/cddl.txt.
 * If applicable, add the following below the CDDL Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 */
package org.netbeans.modules.portalpack.portlets.genericportlets.node.ddloaders;

import org.netbeans.modules.schema2beans.BaseBean;
import org.openide.loaders.DataNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.Lookup;

/**
 *
 * @author Satyaranjan
 */
public class ElementNode extends DataNode {

    private static final String IMAGE_ICON_BASE = "org/netbeans/modules/portalpack/portlets/genericportlets/resources/portletapp.gif";
    private BaseBean bean;

    /**
     *
     * @param obj
     */
    public ElementNode(PortletXMLDataObject obj, BaseBean bean) {
        super(obj, Children.LEAF);
        this.bean = bean;
        setIconBaseWithExtension(IMAGE_ICON_BASE);

    }

    public ElementNode(PortletXMLDataObject obj, Lookup lookup) {
        super(obj, Children.LEAF, lookup);
        setIconBaseWithExtension(IMAGE_ICON_BASE);
    }
    /* public Action[] getActions(boolean context) {
    javax.swing.Action[]  newActions = new javax.swing.Action[3] ;
    newActions[0]=(null);
    newActions[1]= (SystemAction.get(ShowInStoryBoardAction.class));
    newActions[2] = (SystemAction.get(ResetStoryBoardAction.class));
    return newActions;
    //return super.getActions(context);
    }*/
    /*public Transferable drag() throws IOException {
    return super.drag();
    }*/
    //    /** Creates a property sheet. */
  /*  protected Sheet createSheet() {
    Sheet s = super.createSheet();
    Sheet.Set ss = s.get(Sheet.PROPERTIES);
    if (ss == null) {
    ss = Sheet.createPropertiesSet();
    s.put(ss);
    }
    
    
    BeanProp[] beanProp = (BeanProp[]) bean.beanProps();
    
    for(int i=0;i<beanProp.length;i++) {
    Object[] values = beanProp[i].getValues();
    for(int k=0;k<values.length;k++) {
    try             {
    org.openide.nodes.Node.Property props = new org.openide.nodes.PropertySupport.Name(this);
    
    props.setName(beanProp[i].getName());
    props.setValue(((BaseBean)values[k]);
    ss.put(props);
    } catch (IllegalAccessException ex) {
    Exceptions.printStackTrace(ex);
    } catch (IllegalArgumentException ex) {
    Exceptions.printStackTrace(ex);
    } catch (InvocationTargetException ex) {
    Exceptions.printStackTrace(ex);
    }
    }
    }
    // TODO add some relevant properties: ss.put(...)
    return s;
    }
     */
}
