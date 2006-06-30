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

/*
 * WS70MailResourceBeanDataNode.java
 */

package org.netbeans.modules.j2ee.sun.ws7.serverresources.beans;

import java.beans.PropertyEditor;

import org.openide.util.Utilities;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;

import org.openide.nodes.Node;
import org.openide.nodes.Sheet;
import org.openide.nodes.BeanNode;
import org.openide.nodes.PropertySupport;

import org.openide.filesystems.FileObject;

import org.netbeans.modules.j2ee.sun.ide.editors.NameValuePairsPropertyEditor;
import org.netbeans.modules.j2ee.sun.ws7.serverresources.loaders.SunWS70ResourceDataObject;

import org.netbeans.modules.j2ee.sun.ws7.serverresources.dd.WS70Resources;
/**
 *
 * Code reused from Appserver common API module 
 */
public class WS70MailResourceBeanDataNode extends WS70BaseResourceNode implements java.beans.PropertyChangeListener{
    private WS70MailResourceBean resource = null;
   
    /**
     * Creates a new instance of WS70MailResourceBeanDataNode
     */
    public WS70MailResourceBeanDataNode(SunWS70ResourceDataObject obj, WS70MailResourceBean key) {
        super(obj);
        resource = key;
        setIconBaseWithExtension("org/netbeans/modules/j2ee/sun/ws7/resources/ResNodeNodeIcon.gif"); //NOI18N
        setShortDescription (NbBundle.getMessage (WS70MailResourceBeanDataNode.class, "DSC_MailNode"));//NOI18N
        key.addPropertyChangeListener(this);
        
        Class clazz = key.getClass ();
        try{
            createProperties(key, Utilities.getBeanInfo(clazz));
        } catch (Exception e){
            e.printStackTrace();
        } 
    }
 
    
    protected WS70MailResourceBeanDataNode getWS70MailResourceBeanDataNode(){
        return this;
    }
    
    protected WS70MailResourceBean getWS70MailResourceBean(){
        return resource;
    }
    
    public void propertyChange(java.beans.PropertyChangeEvent evt) {
        FileObject resFile = getWS70MailResourceBeanDataNode().getDataObject().getPrimaryFile();
        WS70ResourceUtils.saveNodeToXml(resFile, resource.getGraph());
    }
    
    public WS70Resources getBeanGraph(){
        return resource.getGraph();
    }
    
    public HelpCtx getHelpCtx() {
        return null; // new HelpCtx("AS_Res_Mail");//NOI18N
    }
    
    protected void createProperties(Object bean, java.beans.BeanInfo info) {
        BeanNode.Descriptor d = BeanNode.computeProperties(bean, info);
        Sheet sets = getSheet();
        Sheet.Set pset = Sheet.createPropertiesSet();
        pset.put(d.property);
        //pset.put(p);
//        pset.setValue("helpID", "AS_Res_Mail_Props"); //NOI18N
        sets.put(pset);
    }
        
}
