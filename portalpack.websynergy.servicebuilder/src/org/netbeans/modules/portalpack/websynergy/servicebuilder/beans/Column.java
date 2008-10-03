/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * 
 * Copyright 2008 Sun Microsystems, Inc. All rights reserved.
 * 
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common
 * Development and Distribution License("CDDL") (collectively, the
 * "License"). You may not use this file except in compliance with the
 * License. You can obtain a copy of the License at
 * http://www.netbeans.org/cddl-gplv2.html
 * or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
 * specific language governing permissions and limitations under the
 * License.  When distributing the software, include this License Header
 * Notice in each file and include the License file at
 * nbbuild/licenses/CDDL-GPL-2-CP.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 * 
 * If you wish your version of this file to be governed by only the CDDL
 * or only the GPL Version 2, indicate your decision by adding
 * "[Contributor] elects to include this software in this distribution
 * under the [CDDL or GPL Version 2] license." If you do not indicate a
 * single choice of license, a recipient has the option to distribute
 * your version of this file under either the CDDL, the GPL Version 2 or
 * to extend the choice of license to its licensees as provided above.
 * However, if you add GPL Version 2 code and therefore, elected the GPL
 * Version 2 license, then the option applies only if the new code is
 * made subject to such option by the copyright holder.
 * 
 * Contributor(s):
 * 
 * Portions Copyrighted 2008 Sun Microsystems, Inc.
 */

package org.netbeans.modules.portalpack.websynergy.servicebuilder.beans;

/**
 *
 * @author satyaranjan
 */
public interface Column {

        public void setComments(java.lang.String[] value);

	public void setComments(int index, java.lang.String value);

	public java.lang.String[] getComments();

	public java.util.List fetchCommentsList();

	public java.lang.String getComments(int index);

	public int sizeComments();

	public int addComments(java.lang.String value);

	public int removeComments(java.lang.String value);

	public void setName(java.lang.String value);

	public java.lang.String getName();

	public void setDbName(java.lang.String value);

	public java.lang.String getDbName();

	public void setType(java.lang.String value);

	public java.lang.String getType();

	public void setPrimary(java.lang.String value);

	public java.lang.String getPrimary();

	public void setEntity(java.lang.String value);

	public java.lang.String getEntity();

	public void setMappingKey(java.lang.String value);

	public java.lang.String getMappingKey();

	public void setMappingTable(java.lang.String value);

	public java.lang.String getMappingTable();

	public void setIdType(java.lang.String value);

	public java.lang.String getIdType();

	public void setIdParam(java.lang.String value);

	public java.lang.String getIdParam();

	public void setConvertNull(java.lang.String value);

	public java.lang.String getConvertNull();

	public void setDummyElm(String[] value);

	public void setDummyElm(int index, String value);

	public String[] getDummyElm();

	public java.util.List fetchDummyElmList();

	public String getDummyElm(int index);

	public int sizeDummyElm();

	public int addDummyElm(String value);

	public int removeDummyElm(String value);

	public void addPropertyChangeListener(java.beans.PropertyChangeListener listener);

	public void removePropertyChangeListener(java.beans.PropertyChangeListener listener);

	public Object clone();

	public Object cloneData();
    
}
