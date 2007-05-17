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

package org.netbeans.modules.portalpack.servers.core.nodes;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.netbeans.modules.portalpack.servers.core.api.PSDeploymentManager;
import org.netbeans.modules.portalpack.servers.core.common.enterprise.NodeTypeConstants;
import org.netbeans.modules.portalpack.servers.core.nodes.actions.ActionUtil;
import org.netbeans.modules.portalpack.servers.core.nodes.actions.AddPortletAction;
import org.netbeans.modules.portalpack.servers.core.nodes.actions.CreateContainerAction;
import org.netbeans.modules.portalpack.servers.core.nodes.actions.DeleteContainerAction;
import org.netbeans.modules.portalpack.servers.core.nodes.actions.ShowAdminToolAction;
import org.netbeans.modules.portalpack.servers.core.nodes.actions.ViewChannelsAction;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.nodes.Sheet;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.actions.SystemAction;
import org.openide.util.datatransfer.PasteType;

/**
 *
 * @author Satya
 */
public class ContainerNode extends BaseNode implements Node.Cookie {
    
    private static String ICON_BASE = "org/netbeans/modules/portalpack/servers/core/resources/container.gif"; // NOI18N
    private String key = "";
    private String displayValue = "";
    private PSDeploymentManager manager = null;
    private String baseDn = "";
    public ContainerNode()
    {
       super(new Children.Array());   
    }
    public ContainerNode(PSDeploymentManager manager, Lookup lookup,String key,String displayValue,String baseDn) {
       
        super(new ContainersChildrenNode(manager,lookup,NodeTypeConstants.CONTAINER_NODE,baseDn));
        this.key = key;
        this.displayValue = displayValue;
        this.manager = manager;
        this.baseDn = baseDn;
        getCookieSet().add(this);
        setIconBaseWithExtension(ICON_BASE);
        setDisplayName(displayValue);
        setShortDescription(getShortDescription());
        getCookieSet().add(this);
         
    }
    
    public ContainerNode(PSDeploymentManager manager, Lookup lookup,String key,String displayValue,String baseDn,boolean leafNode) {
       
        super(Children.LEAF);
        this.key = key;
        this.displayValue = displayValue;
        this.manager = manager;
        this.baseDn = baseDn;
        getCookieSet().add(this);
        setIconBaseWithExtension(ICON_BASE);
        setDisplayName(displayValue);
        setShortDescription(getShortDescription());
        getCookieSet().add(this);
         
    }

    /*@Override
    public String getHtmlDisplayName() {
        return "<font color='0000FF' style='Bold'>" + getDisplayName() + "</font>"; 
    }*/
        
    public PSDeploymentManager getDeploymentManager()
    {
        return manager;
    }
    
    public String getShortDescription() {
        return key; // NOI18N
    }
    
    public String getDn()
    {
        return baseDn;
    }
    
    public String getParentKey()
    {
        if(key.length() <= displayValue.length())
            return null;
        else if(key.indexOf(displayValue) != -1)
        {
            int index = key.lastIndexOf(displayValue);
            
            if(index == 0)
                return key;
            
            String parent = key.substring(0,index-1);
            
            return parent;
            
        }
        return null;
    }
    
    public String getValue(){
        return displayValue;
    }
    
    public javax.swing.Action[] getActions(boolean context) {
        
        if(manager != null){
            return manager.getPSNodeConfiguration().getContainerActions();
        }
            
        javax.swing.Action[]  newActions = new javax.swing.Action[1] ;
        newActions[0]=(null);    
        return newActions;
    }
    
    
    
    
    public boolean hasCustomizer() {
        return true;
    }

    public String getKey() {
        return key;
    }

    public String getType() {
        return NodeTypeConstants.CONTAINER;
    }

    @Override
    public PasteType getDropType(final Transferable t, int action, int index) {
        if(!manager.getPSNodeConfiguration().allowDragAndDrop())
            return null;
        
        try{
            final java.lang.Object obj = t.getTransferData(new java.awt.datatransfer.DataFlavor("application/x-java-openide-nodednd; class=org.openide.nodes.Node",
                                                                                          "application/x-java-openide-nodednd"));
            if(obj instanceof ChannelNode || obj instanceof ContainerNode)
            {
                final String channelName = ((BaseNode)obj).getKey();
                if(obj instanceof ChannelNode)
                {
                    final ChannelNode channelNode = (ChannelNode)obj;
                     if(!channelNode.getParentChannelChildrenNode().getChannelFilterType().equals(ChannelChildrenNode.TOP_CHANNELS))
                    return null;
                }else{
                    //do nothing incase of container;
                }
               // System.out.println("Channel Node is getting draggggggggggggggggggged........");
                return new PasteType() {
                    public Transferable paste() throws IOException {
                        try{
                      //      String channelName = channelNode.getKey();
                            List selectedList = new ArrayList();
                            selectedList.add(channelName);
                            //manager.getTaskHandler().setSelectedChannels(((ChannelNode)obj).getDn(), selectedList, getKey());;
                            if(channelName.equals(getKey()))
                                return null;
                            ActionUtil.addChannelToSelectedList(getDn(),channelName,getKey(),getManager().getTaskHandler());
                            Children children = getChildren();
                            if(children instanceof ContainersChildrenNode)
                            {
                                //((ContainersChildrenNode)children).setChannelFilterType(ChannelChildrenNode.SELECTED_TYPE);
                                //((ChannelChildrenNode)children).updateKeys();
                                Node[] childrenNodes = children.getNodes();
                               //refresh ChannelHolderNode
                                
                                for(int i=0;i<childrenNodes.length;i++)
                                {
                                    if(childrenNodes[i] instanceof ChannelHolderNode)
                                    {
                                        ChannelHolderNode holderNode = (ChannelHolderNode)childrenNodes[i];
                                        Children ch = holderNode.getChildren();
                                        if(ch instanceof ChannelChildrenNode)
                                        {
                                            ((ChannelChildrenNode)ch).setChannelFilterType(ChannelChildrenNode.SELECTED_TYPE);
                                            holderNode.setDisplayText(((ChannelChildrenNode)ch).getChannelFilterType());
                                        }
                                        
                                        //()childrenNodes[i]
                                        ActionUtil.refresh(childrenNodes[i]);
                                        break;
                                    }
                                }
                            }
                        }
                        catch (Exception ex) {
                            ex.printStackTrace();
                        }
                        return t;

                }};
                
            }
            return null;
        }
        catch (UnsupportedFlavorException ex) {
            ex.printStackTrace();
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }
        return null;
    }
    
    private PSDeploymentManager getManager(){
        return manager;
    }

    @Override
    protected Sheet createSheet() {
        return manager.getTaskHandler().createContainerPropertySheet(this);
    }
    
}
