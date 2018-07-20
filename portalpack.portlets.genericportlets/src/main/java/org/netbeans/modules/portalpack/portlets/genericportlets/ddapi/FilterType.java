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

public interface FilterType {
	public void setDescription(int index, java.lang.String value);

	public java.lang.String getDescription(int index);

	public int sizeDescription();

	public void setDescription(java.lang.String[] value);

	public java.lang.String[] getDescription();

	public int addDescription(java.lang.String value);

	public int removeDescription(java.lang.String value);

	public void setDisplayName(int index, java.lang.String value);

	public java.lang.String getDisplayName(int index);

	public int sizeDisplayName();

	public void setDisplayName(java.lang.String[] value);

	public java.lang.String[] getDisplayName();

	public int addDisplayName(java.lang.String value);

	public int removeDisplayName(java.lang.String value);

	public void setFilterName(java.lang.String value);

	public java.lang.String getFilterName();

	public void setFilterClass(java.lang.String value);

	public java.lang.String getFilterClass();

	public void setLifecycle(int index, java.lang.String value);

	public java.lang.String getLifecycle(int index);

	public int sizeLifecycle();

	public void setLifecycle(java.lang.String[] value);

	public java.lang.String[] getLifecycle();

	public int addLifecycle(java.lang.String value);

	public int removeLifecycle(java.lang.String value);

	public void setInitParam(int index, org.netbeans.modules.portalpack.portlets.genericportlets.ddapi.InitParamType value);

	public org.netbeans.modules.portalpack.portlets.genericportlets.ddapi.InitParamType getInitParam(int index);

	public int sizeInitParam();

	public void setInitParam(org.netbeans.modules.portalpack.portlets.genericportlets.ddapi.InitParamType[] value);

	public org.netbeans.modules.portalpack.portlets.genericportlets.ddapi.InitParamType[] getInitParam();

	public int addInitParam(org.netbeans.modules.portalpack.portlets.genericportlets.ddapi.InitParamType value);

	public int removeInitParam(org.netbeans.modules.portalpack.portlets.genericportlets.ddapi.InitParamType value);

	public org.netbeans.modules.portalpack.portlets.genericportlets.ddapi.InitParamType newInitParamType();

}
