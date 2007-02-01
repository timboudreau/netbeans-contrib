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

package org.netbeans.modules.portalpack.servers.core.util;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SwingUtilities;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.awt.StatusDisplayer;

/**
 *
 * @author Satya
 */
public class Util {
    
    private static Logger logger = Logger.getLogger(NetbeanConstants.PORTAL_LOGGER);
    /** Creates a new instance of Util */
    public Util() {
    }
    
     public static void showInformation(final String msg,  final String title){
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                NotifyDescriptor d = new NotifyDescriptor.Message(msg, NotifyDescriptor.INFORMATION_MESSAGE);
                d.setTitle(title);
                DialogDisplayer.getDefault().notify(d);
            }
        });
        
    }
    
    public static void showInformation(final String msg){
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                NotifyDescriptor d = new NotifyDescriptor.Message(msg, NotifyDescriptor.INFORMATION_MESSAGE);
                DialogDisplayer.getDefault().notify(d);
            }
        });
        
    }
    
    
    public static  Object showWarning(final String msg){
        NotifyDescriptor d = new NotifyDescriptor.Confirmation(msg, NotifyDescriptor.OK_CANCEL_OPTION, NotifyDescriptor.WARNING_MESSAGE);
        return DialogDisplayer.getDefault().notify(d);
        
        
    }
    
    public static Object   showWarning(final String msg, final String title){
        NotifyDescriptor d = new NotifyDescriptor.Confirmation(msg, title, NotifyDescriptor.OK_CANCEL_OPTION, NotifyDescriptor.WARNING_MESSAGE);
        return DialogDisplayer.getDefault().notify(d);
        
    }
    
    public static void showError(final String msg){
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                NotifyDescriptor d = new NotifyDescriptor.Message(msg, NotifyDescriptor.ERROR_MESSAGE);
                DialogDisplayer.getDefault().notify(d);
            }
        });
        
    }
    
    public static void showError(final String msg, final String title){
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                NotifyDescriptor d = new NotifyDescriptor.Message(msg, NotifyDescriptor.ERROR_MESSAGE);
                d.setTitle(title);
                DialogDisplayer.getDefault().notify(d);
            }
        });
        
    }
    
    public static void setStatusBar(final String msg){
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                StatusDisplayer.getDefault().setStatusText(msg);
            }
        });
        
    }
   
    public static boolean isHostValid(String host)
    {
        if(host == null || host.trim().length() == 0)
        {
           return false; 
        }
        
        try{
            InetAddress.getByName(host).getHostName();
        }catch(Exception ex){
            logger.log(Level.SEVERE,"Error",ex);
            return false;
        }
        return true;
    }
    
    public static boolean isIp(String value)
    {
        if(value == null || value.trim().length() == 0 || value.length() < 7)
            return false;
        
        for(int i=0;i<value.length();i++)
        {
            char c = value.charAt(i);
            if(c == '.' || Character.isDigit(c))
                continue;
            else
                return false;
        }
        return true;
    }
    
    public static boolean isValidPort(String port)
    {
        try{
            Integer.parseInt(port);
        }catch(Exception e){
            return false;
        }
        return true;
    }
    
    public static boolean validateString(String name, boolean allowSpaces) {
        if(name == null || name.trim().length() == 0){
            return false;
        }
        String value = name.trim();
        for(int i=0; i<value.length(); i++) {
            char c = value.charAt(i);
            if(!Character.isLetterOrDigit(c) && !((c == '_') || (allowSpaces && c == ' '))){
                return false;
            }
        }
        return true;
    }
    
    public static String encodeClassPath(String[] strs)
     {
         if(strs == null || strs.length == 0)
             return "";

         StringBuffer sb = new StringBuffer();
         for(int i=0;i<strs.length;i++)
         {
             sb.append(strs[i]).append(";");

         }
         return sb.toString();
     }
    
     public static String[] decodeClassPath(String str)
     {
         if(str == null || str.length() == 0)
             return new String[]{};
         List classPathList = new ArrayList();
         StringTokenizer st = new StringTokenizer(str,";");

         while(st.hasMoreTokens())
         {
             String temp = st.nextToken();
             if(temp != null || temp.trim().equals(""))
                 classPathList.add(temp);
         }

         return (String[])classPathList.toArray(new String[0]);

     }

}
