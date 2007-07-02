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
import org.netbeans.modules.portalpack.portlets.genericportlets.core.codegen.CodeGenConstants;
import org.netbeans.modules.portalpack.portlets.genericportlets.filetype.filters.InitParam;
/**
 *
 * @author Satyaranjan
 */
public class FilterContext implements DataContext {
    
    private String filterName;
    private InitParam[] initParams;
    private String filterClassName;
    private String filterType;
    private String lifeCyclePhase;
    private FilterMappingData[] filterMappingData;

    public void setFilterMappingData(FilterMappingData[] filterMappingData) {
        this.filterMappingData = filterMappingData;
    }

    public FilterMappingData[] getFilterMappingData() {
        return filterMappingData;
    }

    public String getLifeCyclePhase() {
        return lifeCyclePhase;
    }

    public String getFilterType() {
        return filterType;
    }

    public void setFilterType(String filterType) {
        this.filterType = filterType;
        if(filterType.equals(CodeGenConstants.ACTION_FILTER_TYPE))
            lifeCyclePhase = CodeGenConstants.ACTION_PHASE;
        else if(filterType.equals(CodeGenConstants.RENDER_FILTER_TYPE))
            lifeCyclePhase = CodeGenConstants.RENDER_PHASE;
        else if(filterType.equals(CodeGenConstants.EVENT_FILTER_TYPE))
            lifeCyclePhase = CodeGenConstants.EVENT_PHASE;
        else if(filterType.equals(CodeGenConstants.FRAGMENT_FILTER_TYPE))
            lifeCyclePhase = CodeGenConstants.FRAGMENT_PHASE;
        else if(filterType.equals(CodeGenConstants.RESOURCE_FILTER_TYPE))
            lifeCyclePhase = CodeGenConstants.RESOURCE_PHASE;
        else
            lifeCyclePhase = "UNKNOWN";
    }
    /** Creates a new instance of FilterContext */
    public FilterContext() {
    }
    
    public String getFilterName() {
        return filterName;
    }

    public void setFilterName(String filterName) {
        this.filterName = filterName;
    }

    public InitParam[] getInitParams() {
        return initParams;
    }

    public void setInitParams(InitParam[] initParams) {
        this.initParams = initParams;
    }
    

    public String getFilterClassName() {
        return filterClassName;
    }

    public void setFilterClassName(String filterClassName) {
        this.filterClassName = filterClassName;
    }
    
       
}
