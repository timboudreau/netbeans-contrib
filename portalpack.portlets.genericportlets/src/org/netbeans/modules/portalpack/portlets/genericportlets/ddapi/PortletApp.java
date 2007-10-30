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


package org.netbeans.modules.portalpack.portlets.genericportlets.ddapi;

import java.io.OutputStream;
import org.netbeans.modules.schema2beans.BaseBean;

public interface PortletApp {
    
        public final static String VERSION_1_0 = "1.0";
        public final static String VERSION_2_0 = "2.0";
	public void setPortlet(int index,PortletType value);

	public PortletType getPortlet(int index);

	public int sizePortlet();

	public void setPortlet(PortletType[] value);

	public PortletType[] getPortlet();

	public int addPortlet(org.netbeans.modules.portalpack.portlets.genericportlets.ddapi.PortletType value);

	public int removePortlet(org.netbeans.modules.portalpack.portlets.genericportlets.ddapi.PortletType value);

	public PortletType newPortletType();

	public void setCustomPortletMode(int index,CustomPortletModeType value);

	public CustomPortletModeType getCustomPortletMode(int index);

	public int sizeCustomPortletMode();

	public void setCustomPortletMode(CustomPortletModeType[] value);

	public CustomPortletModeType[] getCustomPortletMode();

	public int addCustomPortletMode(org.netbeans.modules.portalpack.portlets.genericportlets.ddapi.CustomPortletModeType value);

	public int removeCustomPortletMode(org.netbeans.modules.portalpack.portlets.genericportlets.ddapi.CustomPortletModeType value);

	public CustomPortletModeType newCustomPortletModeType();

	public void setCustomWindowState(int index,CustomWindowStateType value);

	public CustomWindowStateType getCustomWindowState(int index);

	public int sizeCustomWindowState();

	public void setCustomWindowState(CustomWindowStateType[] value);

	public CustomWindowStateType[] getCustomWindowState();

	public int addCustomWindowState(org.netbeans.modules.portalpack.portlets.genericportlets.ddapi.CustomWindowStateType value);

	public int removeCustomWindowState(org.netbeans.modules.portalpack.portlets.genericportlets.ddapi.CustomWindowStateType value);

	public CustomWindowStateType newCustomWindowStateType();

	public void setUserAttribute(int index,UserAttributeType value);

	public UserAttributeType getUserAttribute(int index);

	public int sizeUserAttribute();

	public void setUserAttribute(UserAttributeType[] value);

	public UserAttributeType[] getUserAttribute();

	public int addUserAttribute(org.netbeans.modules.portalpack.portlets.genericportlets.ddapi.UserAttributeType value);

	public int removeUserAttribute(org.netbeans.modules.portalpack.portlets.genericportlets.ddapi.UserAttributeType value);

	public UserAttributeType newUserAttributeType();

	public void setSecurityConstraint(int index,SecurityConstraintType value);

	public SecurityConstraintType getSecurityConstraint(int index);

	public int sizeSecurityConstraint();

	public void setSecurityConstraint(SecurityConstraintType[] value);

	public SecurityConstraintType[] getSecurityConstraint();

	public int addSecurityConstraint(org.netbeans.modules.portalpack.portlets.genericportlets.ddapi.SecurityConstraintType value);

	public int removeSecurityConstraint(org.netbeans.modules.portalpack.portlets.genericportlets.ddapi.SecurityConstraintType value);

	public SecurityConstraintType newSecurityConstraintType();
        
        //************ New methods for portlet 2.0
        public java.net.URI getPortletDefaultNamespace();
        
        public void setResourceBundle(java.lang.String value);

	public java.lang.String getResourceBundle();

	public void setFilter(int index, FilterType value);

	public org.netbeans.modules.portalpack.portlets.genericportlets.ddapi.FilterType getFilter(int index);

	public int sizeFilter();

	public void setFilter(org.netbeans.modules.portalpack.portlets.genericportlets.ddapi.FilterType[] value);

	public org.netbeans.modules.portalpack.portlets.genericportlets.ddapi.FilterType[] getFilter();

	public int addFilter(org.netbeans.modules.portalpack.portlets.genericportlets.ddapi.FilterType value);

	public int removeFilter(org.netbeans.modules.portalpack.portlets.genericportlets.ddapi.FilterType value);

	public org.netbeans.modules.portalpack.portlets.genericportlets.ddapi.FilterType newFilterType();

	public void setFilterMapping(int index, org.netbeans.modules.portalpack.portlets.genericportlets.ddapi.FilterMappingType value);

	public org.netbeans.modules.portalpack.portlets.genericportlets.ddapi.FilterMappingType getFilterMapping(int index);

	public int sizeFilterMapping();

	public void setFilterMapping(org.netbeans.modules.portalpack.portlets.genericportlets.ddapi.FilterMappingType[] value);

	public org.netbeans.modules.portalpack.portlets.genericportlets.ddapi.FilterMappingType[] getFilterMapping();

	public int addFilterMapping(org.netbeans.modules.portalpack.portlets.genericportlets.ddapi.FilterMappingType value);

	public int removeFilterMapping(org.netbeans.modules.portalpack.portlets.genericportlets.ddapi.FilterMappingType value);

	public org.netbeans.modules.portalpack.portlets.genericportlets.ddapi.FilterMappingType newFilterMappingType();

	public void setPortletDefaultNamespace(java.net.URI value);

	public void setEventDefinition(int index, org.netbeans.modules.portalpack.portlets.genericportlets.ddapi.EventDefinitionType value);

	public org.netbeans.modules.portalpack.portlets.genericportlets.ddapi.EventDefinitionType getEventDefinition(int index);

	public int sizeEventDefinition();

	public void setEventDefinition(org.netbeans.modules.portalpack.portlets.genericportlets.ddapi.EventDefinitionType[] value);

	public org.netbeans.modules.portalpack.portlets.genericportlets.ddapi.EventDefinitionType[] getEventDefinition();

	public int addEventDefinition(org.netbeans.modules.portalpack.portlets.genericportlets.ddapi.EventDefinitionType value);

	public int removeEventDefinition(org.netbeans.modules.portalpack.portlets.genericportlets.ddapi.EventDefinitionType value);

	public org.netbeans.modules.portalpack.portlets.genericportlets.ddapi.EventDefinitionType newEventDefinitionType();

	public void setPublicRenderParameter(int index, org.netbeans.modules.portalpack.portlets.genericportlets.ddapi.PublicRenderParameterType value);

	public org.netbeans.modules.portalpack.portlets.genericportlets.ddapi.PublicRenderParameterType getPublicRenderParameter(int index);

	public int sizePublicRenderParameter();

	public void setPublicRenderParameter(org.netbeans.modules.portalpack.portlets.genericportlets.ddapi.PublicRenderParameterType[] value);

	public org.netbeans.modules.portalpack.portlets.genericportlets.ddapi.PublicRenderParameterType[] getPublicRenderParameter();

	public int addPublicRenderParameter(org.netbeans.modules.portalpack.portlets.genericportlets.ddapi.PublicRenderParameterType value);

	public int removePublicRenderParameter(org.netbeans.modules.portalpack.portlets.genericportlets.ddapi.PublicRenderParameterType value);

	public org.netbeans.modules.portalpack.portlets.genericportlets.ddapi.PublicRenderParameterType newPublicRenderParameterType();

	public void setContainerRuntimeOption(int index, org.netbeans.modules.portalpack.portlets.genericportlets.ddapi.ContainerRuntimeOptionType value);

	public org.netbeans.modules.portalpack.portlets.genericportlets.ddapi.ContainerRuntimeOptionType getContainerRuntimeOption(int index);

	public int sizeContainerRuntimeOption();

	public void setContainerRuntimeOption(org.netbeans.modules.portalpack.portlets.genericportlets.ddapi.ContainerRuntimeOptionType[] value);

	public org.netbeans.modules.portalpack.portlets.genericportlets.ddapi.ContainerRuntimeOptionType[] getContainerRuntimeOption();

	public int addContainerRuntimeOption(org.netbeans.modules.portalpack.portlets.genericportlets.ddapi.ContainerRuntimeOptionType value);

	public int removeContainerRuntimeOption(org.netbeans.modules.portalpack.portlets.genericportlets.ddapi.ContainerRuntimeOptionType value);

	public org.netbeans.modules.portalpack.portlets.genericportlets.ddapi.ContainerRuntimeOptionType newContainerRuntimeOptionType();
        
        //public void merge(BaseBean bean,int mode);
        //TODO remove it
        public String getVersion();
        
        public void write(java.io.OutputStream out) throws java.io.IOException;

}
