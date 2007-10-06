/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
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
 * Contributor(s):
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
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
 */

package org.netbeans.modules.tasklist.docscan;

import org.netbeans.modules.tasklist.filter.AppliedFilterCondition;
import org.netbeans.modules.tasklist.filter.Filter;
import org.netbeans.modules.tasklist.filter.FilterConvertor;
import org.netbeans.modules.tasklist.filter.StringFilterCondition;
import org.netbeans.modules.tasklist.filter.SuggestionProperty;
import org.netbeans.modules.tasklist.suggestions.PriorityCondition;

/**
 * Filter for user tasks
 */
final class SourceTasksFilter extends Filter {

    // these are the properties (columns) the filter filters
    SuggestionProperty [] PROPS = new SuggestionProperty[] {
      SourceTaskProperties.PROP_TASK,
      SourceTaskProperties.PROP_PRIORITY,
      SourceTaskProperties.PROP_FILENAME};


    /** 
     * Creates a new instance of UserTaskFilter 
     *
     * @param name name of the filter
     */
    public SourceTasksFilter(String name) {
        super(name);
    }
    
    public SourceTasksFilter(final SourceTasksFilter rhs) {
        super(rhs);
        
    }
    
    public Object clone() {
        return new SourceTasksFilter(this);
    }

    /** for deconvertization **/
    private SourceTasksFilter() {}


    public SuggestionProperty[] getProperties() { return PROPS;}

  // map from properties to conditions      
    public AppliedFilterCondition[] createConditions(SuggestionProperty property) {
      if (property.equals(SourceTaskProperties.PROP_TASK)) {
	return applyConditions(property, StringFilterCondition.createConditions());
      } 
      else if (property.equals(SourceTaskProperties.PROP_PRIORITY)) {
	return applyConditions(property, PriorityCondition.createConditions());
      } 
      else if (property.equals(SourceTaskProperties.PROP_FILENAME)) {
	return applyConditions(property, StringFilterCondition.createConditions());
      } else
	throw new IllegalArgumentException("Unknown property for SourceTasksFilter : " + property.getID());
    }
	

  private static class Convertor extends FilterConvertor {

    public Convertor() {
      super("SourceTasksFilter");
    }

    public static SourceTasksFilter.Convertor create() { return new SourceTasksFilter.Convertor();}

    protected Filter createFilter() { return new SourceTasksFilter();}

    protected SuggestionProperty getProperty(String propid) {
      SuggestionProperty sp = SourceTaskProperties.getProperty(propid);
      if (sp == null) 
	return super.getProperty(propid);
      else 
	return sp;
    }
    
  }
}
