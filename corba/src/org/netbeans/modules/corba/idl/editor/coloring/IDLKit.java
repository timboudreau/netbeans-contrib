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

package com.netbeans.enterprise.modules.corba.idl.editor.coloring;

import java.awt.Font;
import java.awt.event.ActionEvent;
import com.netbeans.editor.BaseKit;
import com.netbeans.editor.BaseAction;
import com.netbeans.editor.Syntax;
import com.netbeans.editor.Utilities;
import javax.swing.Action;
import javax.swing.text.JTextComponent;
import javax.swing.text.TextAction;
import javax.swing.text.Caret;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.JEditorPane;

import com.netbeans.developer.modules.text.NbEditorDocument;
import com.netbeans.developer.modules.text.KitSupport;
/** 
* Editor kit implementation for Idl content type
*
* @author Miloslav Metelka, Karel Gardas
* @version 0.01
*/

public class IDLKit extends BaseKit {

  static final long serialVersionUID =-64995352874400403L;
  /** Create new instance of syntax coloring parser */
  public Syntax createSyntax() {
    return new IDLSyntax();
  }

  public Document createDefaultDocument() {
    return new NbEditorDocument(this.getClass());
  }

  public void install(JEditorPane c) {
    super.install(c);
    KitSupport.updateActions(c); // update IDE find and goto action
  }


}

/*
 * <<Log>>
 *  2    Gandalf   1.1         11/27/99 Patrik Knakal   
 *  1    Gandalf   1.0         11/9/99  Karel Gardas    
 * $
 */

