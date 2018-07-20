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


package org.netbeans.modules.portalpack.portlets.genericportlets.filetype.filters;

import java.io.File;
import org.netbeans.api.project.Project;
import org.netbeans.modules.portalpack.portlets.genericportlets.core.FilterContext;
import org.netbeans.modules.portalpack.portlets.genericportlets.core.ResultContext;
import org.netbeans.modules.portalpack.portlets.genericportlets.core.codegen.BaseCodeGenerator;
import org.netbeans.modules.portalpack.portlets.genericportlets.core.codegen.WebDescriptorGenerator;
import org.netbeans.modules.portalpack.portlets.genericportlets.filetype.jsr168.impl.NewJSR168CreatePortletComponent;

/**
 *
 * @author Satyaranjan
 */
public class NewFilterCreateComponent extends NewJSR168CreatePortletComponent {
    
    public String templateName = "portlet20-filter.java";
    /** Creates a new instance of NewFilterCreateComponent */
    public NewFilterCreateComponent(Project prj) {
        super(prj);
    }
    
     public void doCreateFilterClass(String selectedDir,String className,FilterContext fc,ResultContext rc)
    {
        //ResultContext rc = new ResultContext();
        createNewClass(selectedDir, className, fc, rc);
        String clazzName = (String)rc.getAttribute(ResultContext.CLASS_NAME);
        fc.setFilterClassName(clazzName);
        doAfterFilterCreate(fc,clazzName);
    }
    
    public void doAfterFilterCreate(FilterContext fc,String clazzName)
    {
        new WebDescriptorGenerator().addNewFilter(getWebInfDir() + File.separator + "portlet.xml", fc);
    }
    
     protected BaseCodeGenerator getCodeGenerator() {
        BaseCodeGenerator bcode = new BaseCodeGenerator();
        bcode.setTemplateFileName(templateName);
        return bcode;
    }
}
