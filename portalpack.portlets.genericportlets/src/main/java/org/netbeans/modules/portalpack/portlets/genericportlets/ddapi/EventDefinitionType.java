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

public interface EventDefinitionType {
	public void setDescription(int index, java.lang.String value);

	public java.lang.String getDescription(int index);

	public int sizeDescription();

	public void setDescription(java.lang.String[] value);

	public java.lang.String[] getDescription();

	public int addDescription(java.lang.String value);

	public int removeDescription(java.lang.String value);

	public void setQname(javax.xml.namespace.QName value);

	public javax.xml.namespace.QName getQname();

	public void setName(java.lang.String value);

	public java.lang.String getName();

	public void setAlias(int index, javax.xml.namespace.QName value);

	public javax.xml.namespace.QName getAlias(int index);

	public int sizeAlias();

	public void setAlias(javax.xml.namespace.QName[] value);

	public javax.xml.namespace.QName[] getAlias();

	public int addAlias(javax.xml.namespace.QName value);

	public int removeAlias(javax.xml.namespace.QName value);

	public void setValueType(java.lang.String value);

	public java.lang.String getValueType();

}
