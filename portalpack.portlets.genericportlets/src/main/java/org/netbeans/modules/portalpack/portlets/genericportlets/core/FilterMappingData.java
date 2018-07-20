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
 * Filter Mapping representation

 * @author Satyaranjan
 */
public class FilterMappingData {

    private String name = null;
    private Type type = null;
    private String portlet = null;
    private Dispatcher[] dispatch = new Dispatcher[0];

    FilterMappingData() {}

   public FilterMappingData(String name) {
	this.name = name;
	this.type =  FilterMappingData.Type.URL;
	this.portlet = ""; //NOI18N
    }

   public FilterMappingData(String name, Type type, String portlet, Dispatcher[] d) {
	this.name = name; 
	this.type = type; 
	this.portlet = portlet; 
	this.dispatch = d; 
    } 

    public Object clone() { 
	return new FilterMappingData(name, type, portlet, dispatch); 
    }

    /**
     * Get the Name value.
     * @return the Name value.
     */
    public String getName() {
	return name;
    }

    /**
     * Set the Name value.
     * @param newName The new Name value.
     */
    public void setName(String newName) {
	this.name = newName;
    }

    /**
     * Get the Type value.
     * @return the Type value.
     */
    public Type getType() {
	return type;
    }

    /**
     * Set the Type value.
     * @param newType The new Type value.
     */
    public void setType(Type newType) {
	this.type = newType;
    }

    /**
     * Get the Pattern value.
     * @return the Pattern value.
     */
    public String getPortlet() {
	return portlet;
    }

    /**
     * Set the Pattern value.
     * @param newPattern The new Pattern value.
     */
    public void setPortlet(String portlet) {
	this.portlet = portlet;
    }

    /**
     * Get the DispatchConfig value.
     * @return the DispatchConfig value.
     */
    Dispatcher[] getDispatcher() {
	return dispatch;
    }

    /**
     * Set the DispatchConfig value.
     * @param new dc new DispatchConfig value.
     */
    void setDispatcher(Dispatcher[] d) {
	this.dispatch = d;
    }

    public String toString() { 
	StringBuffer buf = 
	    new StringBuffer("FilterMapping for filter: "); //NOI18N
	buf.append(name); 
	buf.append("\nMapping type: "); 
	buf.append(type.toString()); 
	buf.append(" for pattern: "); 
	buf.append(portlet); 
	buf.append("\nDispatch conditions: "); 
	if(dispatch.length == 0)
	    buf.append("REQUEST (not set)\n\n"); 
	else { 
	    for(int i=0; i<dispatch.length; ++i) { 
		buf.append(dispatch[i].toString()); 
		buf.append(", "); 
	    }
	    buf.append("\n\n"); 
	} 
	return buf.toString(); 
    } 

    static class Type { 
	private String name; 
	private Type(String name) { this.name = name; } 
	public String toString() { return name; } 
	public static final Type URL = new Type("URL pattern"); 
	public static final Type SERVLET = new Type("Servlet"); 
    } 

    static class Dispatcher { 
	private String name; 
	private Dispatcher(String name) { this.name = name; } 
	public String toString() { return name; } 
	public static final Dispatcher BLANK = new Dispatcher(""); 
	public static final Dispatcher REQUEST = new Dispatcher("REQUEST"); 
	public static final Dispatcher INCLUDE = new Dispatcher("INCLUDE"); 
	public static final Dispatcher FORWARD = new Dispatcher("FORWARD"); 
	public static final Dispatcher ERROR = new Dispatcher("ERROR"); 
	public static final Dispatcher findDispatcher(String s) { 
	    if(s.equals(REQUEST.toString())) return REQUEST;
	    else if(s.equals(INCLUDE.toString())) return INCLUDE;
	    else if(s.equals(FORWARD.toString())) return FORWARD;
	    else if(s.equals(ERROR.toString())) return ERROR;
	    else return BLANK; 
	} 
	public static final Dispatcher[] getAll() { 
	    Dispatcher[] d = new Dispatcher[4]; 
	    d[0] = REQUEST; 
	    d[1] = FORWARD; 
	    d[2] = INCLUDE; 
	    d[3] = ERROR; 
	    return d; 
	} 
    }
}