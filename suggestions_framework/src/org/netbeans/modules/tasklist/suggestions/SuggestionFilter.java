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

package org.netbeans.modules.tasklist.suggestions;

import org.netbeans.modules.tasklist.filter.AppliedFilterCondition;
import org.netbeans.modules.tasklist.filter.Filter;
import org.netbeans.modules.tasklist.filter.FilterConvertor;
import org.netbeans.modules.tasklist.filter.IntegerFilterCondition;
import org.netbeans.modules.tasklist.filter.StringFilterCondition;
import org.netbeans.modules.tasklist.filter.SuggestionProperties;
import org.netbeans.modules.tasklist.filter.SuggestionProperty;

/**
 * Filter for user tasks
 * @author Tim Lebedkov
 */
public class SuggestionFilter extends Filter {
    private static final String[] PROP_KEYS = {
        "SuggestionsRoot", // NOI18N
        "Details", // NOI18N
        "Priority", // NOI18N
        "File", // NOI18N
        "Line", // NOI18N
        "Category", // NOI18N
    };
    
  private static final SuggestionProperty[] PROPS = new SuggestionProperty[] {
    SuggestionImplProperties.PROP_SUMMARY,
    SuggestionImplProperties.PROP_DETAILS,
    SuggestionImplProperties.PROP_PRIORITY,
    SuggestionImplProperties.PROP_FILENAME,
    SuggestionImplProperties.PROP_LINE_NUMBER,
    SuggestionImplProperties.PROP_CATEGORY    
  };

  
    
    
    /** 
     * Creates a new instance of UserTaskFilter 
     *
     * @param name name of the filter
     */
    public SuggestionFilter(String name) {
        super(name);
    }
    
    public SuggestionFilter(SuggestionFilter rhs) { super(rhs); }

    private SuggestionFilter() { // for deconvertization reasons;
    }

    public Object clone() { return new SuggestionFilter(this);}

    public SuggestionProperty[] getProperties() {  return PROPS;}
    

    public AppliedFilterCondition[] createConditions(SuggestionProperty property) {
      if (property.equals(SuggestionProperties.PROP_SUMMARY)) {
	return applyConditions(property, StringFilterCondition.createConditions());
      } 
      else if (property.equals(SuggestionImplProperties.PROP_DETAILS)) {
	return applyConditions(property, StringFilterCondition.createConditions());
      } 
      else if (property.equals(SuggestionImplProperties.PROP_PRIORITY)) {
	return applyConditions(property, PriorityCondition.createConditions());
      } 
      else if (property.equals(SuggestionImplProperties.PROP_FILENAME)) {
	return applyConditions(property, StringFilterCondition.createConditions());
      } 
      else if (property.equals(SuggestionImplProperties.PROP_LINE_NUMBER)) {
	return applyConditions(property, IntegerFilterCondition.createConditions());
      } 
      else if (property.equals(SuggestionImplProperties.PROP_CATEGORY)) {
	return applyConditions(property, StringFilterCondition.createConditions());
      } 
      else 
	throw new IllegalArgumentException("wrong property");

    }

  private static class Convertor extends FilterConvertor {

    public Convertor() {
      super("SuggestionFilter");
    }

    public static SuggestionFilter.Convertor create() { return new SuggestionFilter.Convertor();}

    protected Filter createFilter() { return new SuggestionFilter();}

    protected SuggestionProperty getProperty(String propid) {
      SuggestionProperty sp = SuggestionImplProperties.getProperty(propid);
      if (sp == null) 
	return super.getProperty(propid);
      else 
	return sp;
    }
    
  }


}
