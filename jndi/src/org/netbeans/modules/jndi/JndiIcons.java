/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2001 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package com.netbeans.enterprise.modules.jndi;

/*JNDI*/
import javax.naming.*;
import javax.naming.directory.*;


/*JDK*/
import java.util.*;



public final class JndiIcons extends Object
{

    public final static String ICON_BASE="/com/netbeans/enterprise/modules/jndi/resources/";

    private final static String[] defaultIcons={"*","def",
						JndiRootNode.NB_ROOT,"jndi",
						"javax.naming.Context","folder",
						"java.io.File","file"};
    private static Hashtable icontable;
    

    public JndiIcons()
    {
    }
    
    
    //Returns Icon for string containig
    public static String getIconName(String name)
    {
	String iconname;
	
	
	if (icontable==null) lazy_initialize();
	iconname=(String)icontable.get(name);
	if (iconname!=null) return iconname;
	else return (String) icontable.get("*");	
    }
    
    //the same as above but for ClassNamePair    
    public static String getIconName(NameClassPair name, boolean tree)
    {
	String iconname;
	if (icontable==null) lazy_initialize();
	if (name==null) return (String) icontable.get("*");
	iconname=(String) icontable.get(name.getClassName());
	if (iconname!=null) return iconname;
	else return (String) icontable.get("*");
    }
    
    //lazy_initialization
    private static void lazy_initialize()
    {
	icontable = new Hashtable();
	for (int i=0;i<defaultIcons.length;i+=2)
	{
	    icontable.put(defaultIcons[i],defaultIcons[i+1]);
	}
    }
    
    
}