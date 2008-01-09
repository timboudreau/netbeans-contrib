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

public interface PortletType {
	public void setDescription(int index, java.lang.String value);

	public java.lang.String getDescription(int index);

	public int sizeDescription();

	public void setDescription(java.lang.String[] value);

	public java.lang.String[] getDescription();

	public int addDescription(java.lang.String value);

	public int removeDescription(java.lang.String value);

	public void setPortletName(java.lang.String value);

	public java.lang.String getPortletName();

	public void setDisplayName(int index, java.lang.String value);

	public java.lang.String getDisplayName(int index);

	public int sizeDisplayName();

	public void setDisplayName(java.lang.String[] value);

	public java.lang.String[] getDisplayName();

	public int addDisplayName(java.lang.String value);

	public int removeDisplayName(java.lang.String value);

	public void setPortletClass(java.lang.String value);

	public java.lang.String getPortletClass();

	public void setInitParam(int index,InitParamType value);

	public InitParamType getInitParam(int index);

	public int sizeInitParam();

	public void setInitParam(InitParamType[] value);

	public InitParamType[] getInitParam();

	public int addInitParam(org.netbeans.modules.portalpack.portlets.genericportlets.ddapi.InitParamType value);

	public int removeInitParam(org.netbeans.modules.portalpack.portlets.genericportlets.ddapi.InitParamType value);

	public InitParamType newInitParamType();

	public void setExpirationCache(int value);

	public int getExpirationCache();

	public void setSupports(int index, SupportsType value);

	public SupportsType getSupports(int index);

	public int sizeSupports();

	public void setSupports(SupportsType[] value);

	public SupportsType[] getSupports();

	public int addSupports(org.netbeans.modules.portalpack.portlets.genericportlets.ddapi.SupportsType value);

	public int removeSupports(org.netbeans.modules.portalpack.portlets.genericportlets.ddapi.SupportsType value);

	public SupportsType newSupportsType();

	public void setSupportedLocale(int index, java.lang.String value);

	public java.lang.String getSupportedLocale(int index);

	public int sizeSupportedLocale();

	public void setSupportedLocale(java.lang.String[] value);

	public java.lang.String[] getSupportedLocale();

	public int addSupportedLocale(java.lang.String value);

	public int removeSupportedLocale(java.lang.String value);

	public void setResourceBundle(java.lang.String value);

	public java.lang.String getResourceBundle();

	public void setPortletInfo(PortletInfoType value);

	public PortletInfoType getPortletInfo();

	public PortletInfoType newPortletInfoType();

//	public void setPortletInfo2(PortletInfoType value);

//	public PortletInfoType getPortletInfo2();

	public void setPortletPreferences(PortletPreferencesType value);

	public PortletPreferencesType getPortletPreferences();

	public PortletPreferencesType newPortletPreferencesType();

	public void setSecurityRoleRef(int index,SecurityRoleRefType value);

	public SecurityRoleRefType getSecurityRoleRef(int index);

	public int sizeSecurityRoleRef();

	public void setSecurityRoleRef(SecurityRoleRefType[] value);

	public SecurityRoleRefType[] getSecurityRoleRef();

	public int addSecurityRoleRef(org.netbeans.modules.portalpack.portlets.genericportlets.ddapi.SecurityRoleRefType value);

	public int removeSecurityRoleRef(org.netbeans.modules.portalpack.portlets.genericportlets.ddapi.SecurityRoleRefType value);

	public SecurityRoleRefType newSecurityRoleRefType();
        
        //*** new methods added for portlet 2.0 spec
             
        public void setSupportedProcessingEvent(int index, org.netbeans.modules.portalpack.portlets.genericportlets.ddapi.EventDefinitionReferenceType value);

	public EventDefinitionReferenceType getSupportedProcessingEvent(int index);

	public int sizeSupportedProcessingEvent();

	public void setSupportedProcessingEvent(EventDefinitionReferenceType[] value);

	public EventDefinitionReferenceType[] getSupportedProcessingEvent();

	public int addSupportedProcessingEvent(org.netbeans.modules.portalpack.portlets.genericportlets.ddapi.EventDefinitionReferenceType value);

	public int removeSupportedProcessingEvent(org.netbeans.modules.portalpack.portlets.genericportlets.ddapi.EventDefinitionReferenceType value);

	public EventDefinitionReferenceType newEventDefinitionReferenceType();

	public void setSupportedPublishingEvent(int index, EventDefinitionReferenceType value);

	public EventDefinitionReferenceType getSupportedPublishingEvent(int index);

	public int sizeSupportedPublishingEvent();

	public void setSupportedPublishingEvent(EventDefinitionReferenceType[] value);

	public EventDefinitionReferenceType[] getSupportedPublishingEvent();

	public int addSupportedPublishingEvent(org.netbeans.modules.portalpack.portlets.genericportlets.ddapi.EventDefinitionReferenceType value);

	public int removeSupportedPublishingEvent(org.netbeans.modules.portalpack.portlets.genericportlets.ddapi.EventDefinitionReferenceType value);

	public void setSupportedPublicRenderParameter(int index, java.lang.String value);

	public java.lang.String getSupportedPublicRenderParameter(int index);

	public int sizeSupportedPublicRenderParameter();

	public void setSupportedPublicRenderParameter(java.lang.String[] value);

	public java.lang.String[] getSupportedPublicRenderParameter();

	public int addSupportedPublicRenderParameter(java.lang.String value);

	public int removeSupportedPublicRenderParameter(java.lang.String value);

	public void setContainerRuntimeOption(int index, ContainerRuntimeOptionType value);

	public ContainerRuntimeOptionType getContainerRuntimeOption(int index);

	public int sizeContainerRuntimeOption();

	public void setContainerRuntimeOption(ContainerRuntimeOptionType[] value);

	public ContainerRuntimeOptionType[] getContainerRuntimeOption();

	public int addContainerRuntimeOption(org.netbeans.modules.portalpack.portlets.genericportlets.ddapi.ContainerRuntimeOptionType value);

	public int removeContainerRuntimeOption(org.netbeans.modules.portalpack.portlets.genericportlets.ddapi.ContainerRuntimeOptionType value);

	public ContainerRuntimeOptionType newContainerRuntimeOptionType();       
}
