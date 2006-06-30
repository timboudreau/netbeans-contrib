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

package org.netbeans.modules.corba.idl.node;

import org.openide.nodes.Children;
import org.openide.nodes.Sheet;
import org.openide.nodes.PropertySupport;

import org.openide.util.actions.SystemAction;
import org.openide.actions.OpenAction;

import org.netbeans.modules.corba.idl.src.IDLElement;
import org.netbeans.modules.corba.idl.src.AttributeElement;

/**
 * Class IDLAttributeNode
 *
 * @author Karel Gardas
 */
public class IDLAttributeNode extends IDLAbstractNode {

    AttributeElement _attribute;
    private static final String ATTRIBUTE_ICON_BASE =
        "org/netbeans/modules/corba/idl/node/attribute"; // NOI18N

    public IDLAttributeNode (AttributeElement value) {
        //super (new IDLDocumentChildren ((SimpleNode)value));
        super (Children.LEAF);
        setIconBase (ATTRIBUTE_ICON_BASE);
        _attribute = value;
        setCookieForDataObject (_attribute.getDataObject ());
    }

    public IDLElement getIDLElement () {
        return _attribute;
    }
    /*
      public String getDisplayName () {
      if (_attribute != null) {
      //return ((Identifier)_attribute.getMember (0)).getName ();
      return _attribute.getName ();
      }
      else
      return ""; // NOI18N
      }
    */
    public String getName () {
        return "attribute"; // NOI18N
    }

    public SystemAction getDefaultAction () {
        SystemAction result = super.getDefaultAction();
        return result == null ? SystemAction.get(OpenAction.class) : result;
    }

    protected Sheet createSheet () {
        Sheet s = Sheet.createDefault ();
        Sheet.Set ss = s.get (Sheet.PROPERTIES);
        ss.put (new PropertySupport.ReadOnly ("name", String.class, IDLNodeBundle.NAME, IDLNodeBundle.NAME_OF_ATTRIBUTE) { // NOI18N
		public Object getValue () {
		    return _attribute.getName ();
		}
	    });
        ss.put (new PropertySupport.ReadOnly ("type", String.class, IDLNodeBundle.TYPE, IDLNodeBundle.TYPE_OF_ATTRIBUTE) { // NOI18N
		public Object getValue () {
		    return (_attribute.getType ()).getName ();
		}
	    });
        /*
          ss.put (new PropertySupport.ReadOnly ("other", String.class, "other", 
          "other attribute whith same type") {
          public Object getValue () {
          String other;
          if (_attribute.getOther () != null) 
          if (_attribute.getOther ().size () > 0) {
          other = (String)_attribute.getOther ().elementAt (0);
          for (int i=1; i<_attribute.getOther ().size (); i++)
          other = other + ", " + (String)_attribute.getOther ().elementAt (i);
          return other;
          }
          else 
          return "";
          else
          return "";
          }
          });
        */
        ss.put (new PropertySupport.ReadOnly ("readonly", String.class, IDLNodeBundle.READONLY, // NOI18N
                                              IDLNodeBundle.READONLY_ATTRIBUTE) {
                    public Object getValue () {
                        if (_attribute.getReadOnly ())
                            return IDLNodeBundle.YES;
                        else
                            return IDLNodeBundle.NO;
                    }
                });

        return s;
    }


}

/*
 * $Log
 * $
 */
