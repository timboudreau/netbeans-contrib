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

import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.netbeans.modules.portalpack.servers.core.api.PSDeploymentManager;
import org.netbeans.modules.portalpack.servers.core.common.enterprise.NodeTypeConstants;
import org.netbeans.modules.portalpack.servers.core.nodes.actions.ActionUtil;
import org.netbeans.modules.portalpack.servers.core.nodes.actions.AddChannelAction;
import org.netbeans.modules.portalpack.servers.core.nodes.actions.CreateContainerAction;
import org.netbeans.modules.portalpack.servers.core.nodes.actions.RefreshPortletsAction;
import org.netbeans.modules.portalpack.servers.core.nodes.actions.RefreshCookie;
import org.netbeans.modules.portalpack.servers.core.nodes.actions.ShowAvailableChannelAction;
import org.netbeans.modules.portalpack.servers.core.nodes.actions.ShowExistingChannelAction;
import org.netbeans.modules.portalpack.servers.core.nodes.actions.ShowSelectedChannelAction;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.Lookup;
import org.openide.util.actions.SystemAction;
import org.openide.util.datatransfer.PasteType;

public class ChannelHolderNode extends BaseNode implements Node.Cookie {
    
    private String key = "";
    private String dn = "";
    private String type = "CHANNEL_HOLDER";
    private PSDeploymentManager manager;
  
    private static String ICON_BASE = "org/netbeans/modules/portalpack/servers/core/resources/mfolder.gif";
    public ChannelHolderNode(PSDeploymentManager manager,Lookup lookup,String containerName, String dn){
        super(new ChannelChildrenNode(manager,lookup,NodeTypeConstants.CHANNEL,containerName,dn));
        
        this.key = containerName;
        this.dn = dn;
        this.manager = manager;
        
        setDisplayName("Channels");  // NOI18N
        setIconBaseWithExtension(ICON_BASE);
        setShortDescription(getShortDescription());
        getCookieSet().add(new RefreshChannelChildren((ChannelChildrenNode)getChildren()));
        getCookieSet().add(this);
    }
    
    public javax.swing.Action[] getActions(boolean context) {
         //here key is container name. if containername == null means topLevel channels node.
         if(key != null)
         {       
            if(manager != null)
                return manager.getPSNodeConfiguration().getChannelFolderActions();
            else
                return new SystemAction[] {
           
                SystemAction.get(RefreshPortletsAction.class),
                SystemAction.get(ShowExistingChannelAction.class),
                SystemAction.get(ShowAvailableChannelAction.class),
                SystemAction.get(ShowSelectedChannelAction.class)
            };
         }else{
             if(manager != null)
                 return manager.getPSNodeConfiguration().getTopChannelFolderActions();
             else
                return new SystemAction[] {
           
                    SystemAction.get(RefreshPortletsAction.class),
                    SystemAction.get(AddChannelAction.class),
                 }; 
         }
    }
    
    public String getKey() {
        return key;
    }
    
    public String getDn() {
        return dn;
    }
    
    public String getType() {
        return type;
    }
    
    public PSDeploymentManager getDeploymentManager() {
        return manager;
    }
    
    public void setDisplayText(String filterType) {
        setDisplayName("Channels ["+filterType+"]");
    }
    
    public PSDeploymentManager getManager()
    {
        return manager;
    }
    
    //implements drag/drop facitlity
    public PasteType getDropType(final Transferable t, int action, int index) {
        if(!manager.getPSNodeConfiguration().allowDragAndDrop())
            return null;
        
        try{
            final java.lang.Object obj = t.getTransferData(new java.awt.datatransfer.DataFlavor("application/x-java-openide-nodednd; class=org.openide.nodes.Node",
                                                                                          "application/x-java-openide-nodednd"));
            if(obj instanceof ChannelNode)
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
                           // String channelName = channelNode.getKey();
                            List selectedList = new ArrayList();
                            selectedList.add(channelName);
                            //manager.getTaskHandler().setSelectedChannels(((ChannelNode)obj).getDn(), selectedList, getKey());;
                            if(channelName.equals(getKey()))
                                return null;
                            ActionUtil.addChannelToSelectedList(getDn(),channelName,getKey(),getManager().getTaskHandler());
                            Children children = getChildren();
                            if(children instanceof ChannelChildrenNode)
                            {
                                ((ChannelChildrenNode)children).setChannelFilterType(ChannelChildrenNode.SELECTED_TYPE);
                                setDisplayText(((ChannelChildrenNode)children).getChannelFilterType());
                                //((ChannelChildrenNode)children).updateKeys();
                               // Node[] childrenNodes = children.getNodes();
                               //refresh ChannelHolderNode
                              
                                
                            }
                            ActionUtil.refresh(ChannelHolderNode.this);
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
}



class RefreshChannelChildren implements RefreshCookie {
    ChannelChildrenNode children;
    
    RefreshChannelChildren(ChannelChildrenNode children){
        this.children = children;
        
    }
    
    public void refresh() {
        children.updateKeys();
    }
}

