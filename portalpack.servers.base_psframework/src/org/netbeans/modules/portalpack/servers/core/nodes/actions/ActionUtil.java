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

package org.netbeans.modules.portalpack.servers.core.nodes.actions;

import org.netbeans.modules.portalpack.servers.core.api.PSTaskHandler;
import org.netbeans.modules.portalpack.servers.core.util.NetbeanConstants;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import org.openide.nodes.Node;
import org.openide.windows.WindowManager;

/**
 *
 * @author Satya
 */
public class ActionUtil {
    
    private static Logger logger = Logger.getLogger(NetbeanConstants.PORTAL_LOGGER);
    public static void addChannelToSelectedList(String dn,String channelName, String container,PSTaskHandler handler)
    {
            //check if the channel is already there in selected list
            List selectedList = null;
            try {
                selectedList = handler.getSelectedChannels(dn,container);
            } catch (Exception ex) {
                logger.log(Level.SEVERE,"Error",ex);
                JOptionPane.showMessageDialog(WindowManager.getDefault().getMainWindow(),org.openide.util.NbBundle.getMessage(ActionUtil.class, "MSG_COULD_NOT_BE_ADDED_TO_THE_SELECTED_LIST"));
                return;
            }
            
            if(selectedList.contains(channelName))
            {
                logger.log(Level.FINE,"Channel is already prsenet in selected List....");
                return;
            }
            //Add to available list
            
            List availableList = null;
            try {
                
                availableList = handler.getAvailableChannels(dn,container);
            } catch (Exception ex) {
                logger.log(Level.SEVERE,"Error",ex);
                JOptionPane.showMessageDialog(WindowManager.getDefault().getMainWindow(),org.openide.util.NbBundle.getMessage(ActionUtil.class, "MSG_COULD_NOT_BE_ADDED_TO_AVAILABLE_LIST"));
                return;
            }
            
            logger.log(Level.FINE,"Available list ::: "+availableList);
            if(availableList != null)
                availableList.add(channelName);
            try {
                
                handler.setAvailableChannels(dn,availableList,container);
            } catch (Exception ex) {
                logger.log(Level.SEVERE,"Error",ex);
                 JOptionPane.showMessageDialog(WindowManager.getDefault().getMainWindow(),org.openide.util.NbBundle.getMessage(ActionUtil.class, "MSG_COULD_NOT_BE_ADDED_TO_AVAILABLE_LIST"));
            }
            
            //add to selected list
                 
            logger.log(Level.FINE,"Selected list ::: "+selectedList);
            if(selectedList != null)
                selectedList.add(channelName);
            try {
                
                handler.setSelectedChannels(dn,selectedList,container);
            } catch (Exception ex) {
                logger.log(Level.SEVERE,"Error",ex);
                 JOptionPane.showMessageDialog(WindowManager.getDefault().getMainWindow(),org.openide.util.NbBundle.getMessage(ActionUtil.class, "MSG_COULD_NOT_BE_ADDED_TO_THE_SELECTED_LIST"));
            }
    }
    
    
    public static void removeChannelFromSelectedList(String dn,String channelName, String container,PSTaskHandler handler)
    {
            
            List selectedList = null;
            try {
                
                selectedList = handler.getSelectedChannels(dn,container);
            } catch (Exception ex) {
                logger.log(Level.SEVERE,"Error",ex);
                JOptionPane.showMessageDialog(WindowManager.getDefault().getMainWindow(),org.openide.util.NbBundle.getMessage(ActionUtil.class, "MSG_COULD_NOT_BE_ADDED_TO_THE_SELECTED_LIST"));
                return;
            }

             //remove from selected list
                 
            logger.log(Level.FINE,"Selected list ::: "+selectedList);
            if(selectedList != null)
                selectedList.remove(channelName);
            try {
                
                handler.setSelectedChannels(dn,selectedList,container);
            } catch (Exception ex) {
                logger.log(Level.SEVERE,"Error",ex);
                 JOptionPane.showMessageDialog(WindowManager.getDefault().getMainWindow(),org.openide.util.NbBundle.getMessage(ActionUtil.class, "MSG_COULD_NOT_BE_REMOVED_FROM_SELECTED_LIST"));
            }
           
            //remove from available list
            
            List availableList = null;
            try {
                
                availableList = handler.getAvailableChannels(dn,container);
            } catch (Exception ex) {
                logger.log(Level.SEVERE,"Error",ex);
                JOptionPane.showMessageDialog(WindowManager.getDefault().getMainWindow(),org.openide.util.NbBundle.getMessage(ActionUtil.class, "MSG_COULD_NOT_BE_REMOVED_FROM_AVAILABLE_LIST"));
                return;
            }
            
            logger.log(Level.FINE,"Available list ::: "+availableList);
            if(availableList != null)
                availableList.remove(channelName);
            try {
                
                handler.setAvailableChannels(dn,availableList,container);
            } catch (Exception ex) {
                logger.log(Level.SEVERE,"Error",ex);
                 JOptionPane.showMessageDialog(WindowManager.getDefault().getMainWindow(),org.openide.util.NbBundle.getMessage(ActionUtil.class, "MSG_COULD_NOT_BE_REMOVED_FROM_AVAILABLE_LIST"));
            }
    }
    
    public static void refresh(Node node) {
        
        RefreshCookie refresh1 = (RefreshCookie)node.getCookie(RefreshCookie.class);
        if(refresh1 != null) {
            logger.log(Level.FINE,"Refresh.."+refresh1.getClass().getName());
            refresh1.refresh();
            return;
        }else {
            
             Node parentNode = node.getParentNode();
             if(parentNode == null)
                 return;
             refresh(parentNode);
        }
    }
}
