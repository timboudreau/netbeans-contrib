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
package org.netbeans.modules.tasklist.core;

import org.openide.nodes.PropertySupport;
import org.netbeans.modules.tasklist.client.Suggestion;
import org.openide.ErrorManager;
import java.beans.PropertyEditor;
import org.netbeans.modules.tasklist.filter.SuggestionProperty;

public class SuggestionNodeProperty extends PropertySupport.ReadOnly {

  private Suggestion item;
  private SuggestionProperty property;
  private Class propertyEditorClass = null;

  public SuggestionNodeProperty(Suggestion item, SuggestionProperty property) {
    super(property.getID(), property.getValueClass(), property.getName(), property.getHint());
    this.item = item;
    this.property = property;
  }

  public SuggestionNodeProperty(Suggestion item, SuggestionProperty property, Class propertyEditorClass) {
    this(item, property);
    this.propertyEditorClass = propertyEditorClass;
  }

  public PropertyEditor getPropertyEditor() {
    if (propertyEditorClass != null) 
      try {
	return (PropertyEditor) propertyEditorClass.newInstance ();
      } catch (InstantiationException ex) {
	ErrorManager.getDefault().notify(ex);
      } catch (IllegalAccessException iex) {
	ErrorManager.getDefault().notify(iex);
      }


    return super.getPropertyEditor();
  }

  public Object getValue() {
    return property.getValue(item);
  }
}

