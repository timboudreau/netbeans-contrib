/**
 *	This generated bean class RoleMapper matches the schema element 'role-mapper'.
 *  The root bean class is LiferayPortletApp
 *
 *	Generated on Sun Mar 16 00:21:05 IST 2008
 * @Generated
 */

package org.netbeans.modules.portalpack.servers.websynergy.dd.lp.impl440;

import org.w3c.dom.*;
import org.netbeans.modules.schema2beans.*;
import java.beans.*;
import java.util.*;

// BEGIN_NOI18N

public class RoleMapper extends org.netbeans.modules.schema2beans.BaseBean
	 implements org.netbeans.modules.portalpack.servers.websynergy.dd.lp.impl440.RoleMapperInterface
{

	static Vector comparators = new Vector();
	private static final org.netbeans.modules.schema2beans.Version runtimeVersion = new org.netbeans.modules.schema2beans.Version(5, 0, 0);

	static public final String ROLE_NAME = "RoleName";	// NOI18N
	static public final String ROLE_LINK = "RoleLink";	// NOI18N

	public RoleMapper() {
		this(Common.USE_DEFAULT_VALUES);
	}

	public RoleMapper(int options)
	{
		super(comparators, runtimeVersion);
		// Properties (see root bean comments for the bean graph)
		initPropertyTables(2);
		this.createProperty("role-name", 	// NOI18N
			ROLE_NAME, 
			Common.TYPE_1 | Common.TYPE_STRING | Common.TYPE_KEY, 
			String.class);
		this.createProperty("role-link", 	// NOI18N
			ROLE_LINK, 
			Common.TYPE_1 | Common.TYPE_STRING | Common.TYPE_KEY, 
			String.class);
		this.initialize(options);
	}

	// Setting the default values of the properties
	void initialize(int options) {

	}

	// This attribute is mandatory
	public void setRoleName(String value) {
		this.setValue(ROLE_NAME, value);
	}

	//
	public String getRoleName() {
		return (String)this.getValue(ROLE_NAME);
	}

	// This attribute is mandatory
	public void setRoleLink(String value) {
		this.setValue(ROLE_LINK, value);
	}

	//
	public String getRoleLink() {
		return (String)this.getValue(ROLE_LINK);
	}

	//
	public static void addComparator(org.netbeans.modules.schema2beans.BeanComparator c) {
		comparators.add(c);
	}

	//
	public static void removeComparator(org.netbeans.modules.schema2beans.BeanComparator c) {
		comparators.remove(c);
	}
	public void validate() throws org.netbeans.modules.schema2beans.ValidateException {
		boolean restrictionFailure = false;
		boolean restrictionPassed = false;
		// Validating property roleName
		if (getRoleName() == null) {
			throw new org.netbeans.modules.schema2beans.ValidateException("getRoleName() == null", org.netbeans.modules.schema2beans.ValidateException.FailureType.NULL_VALUE, "roleName", this);	// NOI18N
		}
		// Validating property roleLink
		if (getRoleLink() == null) {
			throw new org.netbeans.modules.schema2beans.ValidateException("getRoleLink() == null", org.netbeans.modules.schema2beans.ValidateException.FailureType.NULL_VALUE, "roleLink", this);	// NOI18N
		}
	}

	// Dump the content of this bean returning it as a String
	public void dump(StringBuffer str, String indent){
		String s;
		Object o;
		org.netbeans.modules.schema2beans.BaseBean n;
		str.append(indent);
		str.append("RoleName");	// NOI18N
		str.append(indent+"\t");	// NOI18N
		str.append("<");	// NOI18N
		o = this.getRoleName();
		str.append((o==null?"null":o.toString().trim()));	// NOI18N
		str.append(">\n");	// NOI18N
		this.dumpAttributes(ROLE_NAME, 0, str, indent);

		str.append(indent);
		str.append("RoleLink");	// NOI18N
		str.append(indent+"\t");	// NOI18N
		str.append("<");	// NOI18N
		o = this.getRoleLink();
		str.append((o==null?"null":o.toString().trim()));	// NOI18N
		str.append(">\n");	// NOI18N
		this.dumpAttributes(ROLE_LINK, 0, str, indent);

	}
	public String dumpBeanNode(){
		StringBuffer str = new StringBuffer();
		str.append("RoleMapper\n");	// NOI18N
		this.dump(str, "\n  ");	// NOI18N
		return str.toString();
	}}

// END_NOI18N

