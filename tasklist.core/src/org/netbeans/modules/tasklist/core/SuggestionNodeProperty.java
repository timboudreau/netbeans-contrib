/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2003 Sun
 * Microsystems, Inc. All Rights Reserved.
 */
package org.netbeans.modules.tasklist.core;

import org.openide.nodes.Node;
import org.openide.nodes.PropertySupport;
import org.netbeans.modules.tasklist.client.Suggestion;
import org.netbeans.modules.tasklist.client.SuggestionProperty;
import org.openide.ErrorManager;
import java.beans.PropertyEditor;

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

