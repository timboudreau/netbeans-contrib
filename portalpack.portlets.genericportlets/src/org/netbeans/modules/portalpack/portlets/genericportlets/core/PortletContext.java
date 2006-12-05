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

package org.netbeans.modules.portalpack.portlets.genericportlets.core;

/**
 *
 * @author Satya
 */
public class PortletContext {
    
    private String portletClass;
    private String portletName;
    private String portletDesc;
    private String portletShortDesc;
    private String portletTitle;
    
    private String displayName;
    private String shortTitle;
    
    private String[] modes;
   
    /** Creates a new instance of PortletContext */
    public PortletContext() {
    }
    
    public void setPortletClass(String pClass)
    {
        this.portletClass = pClass;
    }
    public String getPortletClass()
    {
        return portletClass;
    }
    
    public void setPortletName(String pName)
    {
        portletName = pName;
    }
    
    public String getPortletName()
    {
        return portletName;
    }
    
    public void setPortletDescription(String pDesc)
    {
        portletDesc = pDesc;
    }
    
    public String getPortletDescription()
    {
        return portletDesc;
    } 

    public void setPortletTitle(String portletTitle) {
        this.portletTitle = portletTitle;
    }
    
    public String getPortletTitle()
    {
        return portletTitle;
    }
    
    public String getPortletDisplayName()
    {
        return displayName;
    }
    
    public void setPortletDisplayName(String displayName)
    {
        this.displayName = displayName;
    }
    
    public void setPortletShortTitle(String shortTitle)
    {
        this.shortTitle = shortTitle;
    }
    
    public String getPortletShortTitle()
    {
        return shortTitle;
    }
    
    public void setModes(String[] m)
    {
       if(m == null || m.length ==  0)
       {
           modes = new String[0];
           return;
       }
       modes = new String[m.length];
       
       for(int i=0;i<m.length;i++)
       {
           modes[i] = m[i];
       }
    }
    public String[] getModes()
    {
        return modes;
    }
}
