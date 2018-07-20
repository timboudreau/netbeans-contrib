/* 
  * CDDL HEADER START
  * The contents of this file are subject to the terms
  * of the Common Development and Distribution License 
  * (the License). You may not use this file except in
  * compliance with the License.
  *
  * You can obtain a copy of the License at
  * http://www.sun.com/cddl/cddl.html and legal/CDDLv1.0.txt
  * See the License for the specific language governing
  * permission and limitations under the License.
  *
  * When distributing Covered Code, include this CDDL 
  * Header Notice in each file and include the License file  
  * at legal/CDDLv1.0.txt.                                                           
  * If applicable, add the following below the CDDL Header,
  * with the fields enclosed by brackets [] replaced by
  * your own identifying information: 
  * "Portions Copyrighted [year] [name of copyright owner]"
  *
  * Copyright 2006 Sun Microsystems Inc. All Rights Reserved
  * CDDL HEADER END
 */
 

package org.netbeans.modules.portalpack.portlets.taglib;

import javax.servlet.jsp.tagext.TagData;
import javax.servlet.jsp.tagext.TagExtraInfo;
import javax.servlet.jsp.tagext.VariableInfo;

/**
 * This class is used to declare scripting variables from a JSP tag. 
 * The variables are : renderRequest, renderResponse, and portletConfig.
 * 
 * This class applicable for Portlet v2.0
 */
public class DefineObjectsTagExtraInfo extends TagExtraInfo {
    
    @Override
    public VariableInfo[] getVariableInfo(TagData data) {
        
        VariableInfo[] vInfos = new VariableInfo[13];
        
        vInfos[0] = new VariableInfo(
                PortletTaglibConstants.RENDER_REQUEST_VAR,
                "javax.portlet.RenderRequest",true, VariableInfo.AT_END);
        vInfos[1] = new VariableInfo(
                PortletTaglibConstants.RENDER_RESPONSE_VAR,
                "javax.portlet.RenderResponse",true, VariableInfo.AT_END);
        vInfos[2] = new VariableInfo(
                PortletTaglibConstants.PORTLET_CONFIG_VAR,
                "javax.portlet.PortletConfig",true, VariableInfo.AT_END);
        vInfos[3] = new VariableInfo(
                PortletTaglibConstants.ACTION_REQUEST_VAR, 
                "javax.portlet.ActionRequest", true, VariableInfo.AT_END);
        vInfos[4] = new VariableInfo(
                PortletTaglibConstants.ACTION_RESPONSE_VAR, 
                "javax.portlet.ActionResponse", true, VariableInfo.AT_END);
        vInfos[5] = new VariableInfo(
                PortletTaglibConstants.EVENT_REQUEST_VAR, 
                "javax.portlet.EventRequest", true, VariableInfo.AT_END);
        vInfos[6] = new VariableInfo(
                PortletTaglibConstants.EVENT_RESPONSE_VAR, 
                "javax.portlet.EventResponse", true, VariableInfo.AT_END);
        vInfos[7] = new VariableInfo(
                PortletTaglibConstants.RESOURCE_REQUEST_VAR, 
                "javax.portlet.ResourceRequest", true, VariableInfo.AT_END);
        vInfos[8] = new VariableInfo(
                PortletTaglibConstants.RESOURCE_RESPONSE_VAR, 
                "javax.portlet.ResourceResponse", true, VariableInfo.AT_END);
        vInfos[9] = new VariableInfo(
                PortletTaglibConstants.PORTLET_SESSION_VAR, 
                "javax.portlet.PortletSession", true, VariableInfo.AT_END);
        vInfos[10] = new VariableInfo(
                PortletTaglibConstants.PORTLET_SESSION_SCOPE_VAR, 
                "java.util.Map<String,String[]>", true, VariableInfo.AT_END);
        vInfos[11] = new VariableInfo(
                PortletTaglibConstants.PORTLET_PREFERENCES_VAR, 
                "javax.portlet.PortletPreferences", true, VariableInfo.AT_END);
        vInfos[12] = new VariableInfo(
                PortletTaglibConstants.PORTLET_PREFERENCES_VALUES_VAR, 
                "java.util.Map<String,String[]>", true, VariableInfo.AT_END);
        return vInfos;
    }
    
    
}