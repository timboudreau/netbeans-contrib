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
 * Portions Copyrighted 2007 Sun Microsystems, Inc.
 */
package org.netbeans.modules.portalpack.saw.palette.items;

import org.netbeans.modules.web.api.webmodule.WebModule;
import org.openide.util.NbBundle;

/**
 *
 * @author Vihang
 */
public class SAWMethodFactory {
     private static SAWMethodFactory factory = null;

    
   
    private SAWMethodFactory() {
    }
     /**
     * This method returns a singleton instance of the SAWMethodFactory class.
     *
     * @return SAWMethodFactory
     */
    public static SAWMethodFactory getInstance(){
        if(factory == null){
            return new SAWMethodFactory();    
        }
        return factory;
    }
    public SAWImplementationType getSAWImplementationTypeInstance(String implementationType) throws Exception {     
	
		//String implementationType =  prop.getProperty("ImplementationType"); 
                String classToInstantTiate = new String();          
                classToInstantTiate= NbBundle.getBundle(SAWMethodFactory.class).getString("SAWImplClass_" + implementationType);
              	SAWImplementationType sawImplementationType = null;
		
		try {
			sawImplementationType = (SAWImplementationType)Class.forName(classToInstantTiate).newInstance();
		} catch (InstantiationException e) {
			throw new Exception("Unable to instantiate business process impl class due to InstantiationException ",e);
		} catch (IllegalAccessException e) {
			throw new Exception("Unable to instantiate business process impl class due to IllegalAccessException ",e);
		} catch (ClassNotFoundException e) {
			throw new Exception("Unable to instantiate business process impl class due to ClassNotFoundException ",e);		
		}
    	
    	/* 
        }
        */
        return sawImplementationType;
    }
}
