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

public interface PortletApp {
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

}
