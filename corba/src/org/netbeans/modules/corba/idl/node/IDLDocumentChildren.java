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

package org.netbeans.modules.corba.idl.node;

import java.util.Vector;
import java.util.Enumeration;

import org.openide.nodes.*;
import org.openide.filesystems.*;
//import org.openide.

import org.netbeans.modules.corba.*;
import org.netbeans.modules.corba.idl.src.*;


/**
 * Class IDLDocumentChildren
 *
 * @author Karel Gardas
 */
public class IDLDocumentChildren extends Children.Keys {

    //public static final boolean DEBUG = true;
    public static final boolean DEBUG = false;
    private IDLDataObject ido;

    public static final Object NOT_KEY = new Object ();

    public static final String WAIT_ICON = "org/openide/resources/src/wait"; // NOI18N

    //private org.netbeans.modules.corba.idl.src.SimpleNode src;
    private IDLElement _M_src;

    private IDLNode _M_idl_node;

    public IDLDocumentChildren (IDLDataObject v)
    throws java.io.FileNotFoundException {
        super ();
        ido = v;
        //idlNode = node;

    }

    public IDLDocumentChildren (IDLElement tree) {
        _M_src = tree;
        if (_M_src != null)
            this.createKeys ();
    }

    public void setNode (IDLNode node) {
        _M_idl_node = node;
    }

    public void setSrc (IDLElement s) {
        if (DEBUG)
            System.out.println ("setSrc (" + s.getName () + ");"); // NOI18N
        _M_src = s;
    }

    protected org.openide.nodes.Node[] vector2nodes (Vector nodes) {
        org.openide.nodes.Node[] retval = new org.openide.nodes.Node[nodes.size ()];
        for (int i=0; i<nodes.size (); i++)
            retval[i] = (org.openide.nodes.Node)nodes.elementAt (i);

        return retval;
    }

    public void createKeys () {
        Vector keys = new Vector ();
        if (DEBUG)
	    System.out.println ("createKeys ()"); // NOI18N
	//Thread.dumpStack ();
	if (_M_idl_node != null) {
	    if (_M_idl_node.getIDLDataObject ().getStatus () == IDLDataObject.STATUS_NOT_PARSED
		|| _M_idl_node.getIDLDataObject ().getStatus () == IDLDataObject.STATUS_PARSING) {
		if (DEBUG)
		    System.out.println ("adding wait key"); // NOI18N
		keys.add (IDLDocumentChildren.NOT_KEY);
		this.setKeys (keys);
	    }

	}
        if (_M_src == null) {
	    // we must set empty keys for colapsing IDL tree when parse exception was thrown
	    if (DEBUG)
		System.out.println ("setting empty keys"); // NOI18N
	    this.setKeys (keys);
            return;
	}
	
        if (DEBUG) {
            System.out.println ("setKeys (" + _M_src.getName () + ");"); // NOI18N
            Vector tmp = _M_src.getMembers ();
            for (int i=0; i<tmp.size (); i++) {
                System.out.println ("key: " + ((IDLElement)tmp.elementAt (i)).getName ()); // NOI18N
            }
            _M_src.xDump (" "); // NOI18N
        }
        setKeys (_M_src.getMembers ());

        /*
          for (int i=0; i<src.getMembers ().size (); i++) {
          refreshKey (src.getMember (i));
          }
        */
        /*
          org.openide.nodes.Node[] nodes = getNodes ();
          for (int i=0; i<nodes.length; i++) {
          try {
           ((IDLDocumentChildren)nodes[i].getChildren ()).createKeys ();
          } catch (ClassCastException e) {
          System.out.println (e);
          }
          }
        */
        /*
          Enumeration nodes = nodes ();
          while (nodes.hasMoreElements ()) {
          try {
          ((IDLDocumentChildren)((org.openide.nodes.Node)nodes.nextElement ()).getChildren ()).createKeys ();
          } catch (ClassCastException e) {
          //if (DEBUG)
          //  e.printStackTrace ();
          }
          }
        */
        if (DEBUG)
	    System.out.println ("---end of createKeys ()----------"); // NOI18N
    }

    protected org.openide.nodes.Node[] createNodes (Object key) {

        //if (DEBUG) {
	//try {
	if (DEBUG)
	    System.out.println ("createNodes (" + key + ");"); // NOI18N
	//} catch (Exception e) {
	//e.printStackTrace ();
	//}
	//}
        org.openide.nodes.Node[] ret_nodes;
        Vector nodes = new Vector ();

	if (key.equals (IDLDocumentChildren.NOT_KEY)) {
	    AbstractNode __wait = new AbstractNode(Children.LEAF);
	    __wait.setName(CORBASupport.WAIT);
	    __wait.setIconBase(WAIT_ICON);
	    nodes.add (__wait);
	    return vector2nodes (nodes);
	}

        IDLElement child = (IDLElement)key;

        if (child instanceof ModuleElement) {
            if (DEBUG)
                System.out.println ("found module"); // NOI18N
            nodes.addElement (new IDLModuleNode ((ModuleElement) child));

            return vector2nodes (nodes);
        }

        if (child instanceof InterfaceElement) {
            if (DEBUG)
                System.out.println ("found interface"); // NOI18N
            nodes.addElement (new IDLInterfaceNode ((InterfaceElement) child));
            return vector2nodes (nodes);
        }

        if (child instanceof InterfaceForwardElement) {
            if (DEBUG)
                System.out.println ("found forward interface"); // NOI18N
            nodes.addElement (new IDLInterfaceForwardNode ((InterfaceForwardElement) child));
            return vector2nodes (nodes);
        }

        if (child instanceof ValueForwardElement) {
            if (DEBUG)
		System.out.println ("found forward value"); // NOI18N
            nodes.addElement (new IDLValueForwardNode ((ValueForwardElement) child));
            return vector2nodes (nodes);
        }

        if (child instanceof ValueBoxElement) {
            if (DEBUG)
		System.out.println ("found value box"); // NOI18N
            nodes.addElement (new IDLValueBoxNode ((ValueBoxElement) child));
            return vector2nodes (nodes);
        }

	if (child instanceof ValueElement) {
	    if (DEBUG)
		System.out.println ("found valuetype"); // NOI18N
	    nodes.addElement (new IDLValueNode ((ValueElement) child));          
	    return vector2nodes (nodes);
	}

	if (child instanceof ValueAbsElement) {
	    if (DEBUG)
		System.out.println ("found abstract valuetype"); // NOI18N
	    nodes.addElement (new IDLValueAbsNode ((ValueAbsElement) child));
	    return vector2nodes (nodes);
	}

	if (child instanceof StateMemberElement) {
	    if (DEBUG)
		System.out.println ("found state member"); // NOI18N
	    Vector __members = child.getMembers ();
	    for (int __i=0; __i<__members.size (); __i++) {
		if (__members.elementAt (__i) instanceof DeclaratorElement) {
		    nodes.addElement (new IDLStateMemberNode 
			((DeclaratorElement)__members.elementAt (__i), 
			 (StateMemberElement) child));
		} 
	    }
	    //nodes.addElement (new IDLStateMemberNode ((StateMemberElement) child));          
	    return vector2nodes (nodes);
	}

	if (child instanceof InitDclElement) {
	    if (DEBUG)
		System.out.println ("found factory"); // NOI18N
	    nodes.addElement (new IDLInitDclNode ((InitDclElement) child));
	    return vector2nodes (nodes);
	}

        if (child instanceof OperationElement) {
            if (DEBUG)
                System.out.println ("found operation"); // NOI18N
            nodes.addElement (new IDLOperationNode ((OperationElement) child));

            return vector2nodes (nodes);
        }

        if (child instanceof AttributeElement) {
            if (DEBUG)
                System.out.println ("found attribute"); // NOI18N
            nodes.addElement (new IDLAttributeNode ((AttributeElement) child));

            return vector2nodes (nodes);
        }

        // test if this is important for creating struct or union or enum node or isn't
        // it is important for struct (enums and unoins) nested for example in union
        if (child instanceof StructTypeElement) {
            if (DEBUG)
                System.out.println ("found struct type element"); // NOI18N
            nodes.addElement (new IDLStructTypeNode ((TypeElement) child));

            return vector2nodes (nodes);
        }

        if (child instanceof UnionTypeElement) {
            if (DEBUG)
                System.out.println ("found union type element"); // NOI18N
            nodes.addElement (new IDLUnionTypeNode ((UnionTypeElement) child));

            return vector2nodes (nodes);
        }

        if (child instanceof UnionMemberElement) {
            Object first_member = child.getMember (0);
            if (first_member instanceof StructTypeElement) {
                nodes.addElement (new IDLStructTypeNode ((TypeElement) first_member));
            }
            if (first_member instanceof EnumTypeElement) {
                nodes.addElement (new IDLEnumTypeNode ((TypeElement) first_member));
            }
            if (first_member instanceof UnionTypeElement) {
                nodes.addElement (new IDLUnionTypeNode ((TypeElement) first_member));
            }
            if (DEBUG)
                System.out.println ("found union member element"); // NOI18N
            nodes.addElement (new IDLUnionMemberNode ((UnionMemberElement) child));

            return vector2nodes (nodes);
        }

        if (child instanceof EnumTypeElement) {
            if (DEBUG)
                System.out.println ("found enum type element"); // NOI18N
            nodes.addElement (new IDLEnumTypeNode ((TypeElement) child));

            return vector2nodes (nodes);
        }

        if (child instanceof ConstElement) {
            if (DEBUG)
                System.out.println ("found const element"); // NOI18N
            nodes.addElement (new IDLConstNode ((ConstElement) child));

            return vector2nodes (nodes);
        }

        if (child instanceof TypeElement) {
            if (DEBUG)
                System.out.println ("found type element"); // NOI18N
            Vector members = ((TypeElement) child).getMembers ();
            if (DEBUG)
                System.out.println ("members" + members); // NOI18N
            if ((members.elementAt (0) instanceof StructTypeElement ||
                    members.elementAt (0) instanceof UnionTypeElement ||
                    members.elementAt (0) instanceof EnumTypeElement)) {

                if (members.size () == 1) {
                    if (DEBUG)
                        System.out.println ("1----------------------------"); // NOI18N
                    // constructed type whithout type children
                    if (members.elementAt (0) instanceof StructTypeElement)
                        nodes.addElement
                        (new IDLStructTypeNode
                         (((TypeElement) ((TypeElement) child).getMembers ().elementAt (0))));
                    if (members.elementAt (0) instanceof UnionTypeElement)
                        nodes.addElement
                        (new IDLUnionTypeNode
                         (((UnionTypeElement) ((TypeElement) child).getMembers ().elementAt (0))));
                    if (members.elementAt (0) instanceof EnumTypeElement)
                        nodes.addElement
                        (new IDLEnumTypeNode
                         (((TypeElement) ((TypeElement) child).getMembers ().elementAt (0))));

                }
                else {
                    if (DEBUG)
                        System.out.println ("2--------------------------"); // NOI18N
                    // constructed type whith type children
                    Vector tmp_members = ((TypeElement) child).getMembers ();
                    // add constructed type
                    String name;
                    IDLType type;
                    if (tmp_members.elementAt (0) instanceof StructTypeElement)
                        nodes.addElement
                        (new IDLStructTypeNode
                         (((TypeElement) ((TypeElement) child).getMembers ().elementAt (0))));
                    if (tmp_members.elementAt (0) instanceof UnionTypeElement)
                        nodes.addElement
                        (new IDLUnionTypeNode
                         (((UnionTypeElement) ((TypeElement) child).getMembers ().elementAt (0))));
                    if (tmp_members.elementAt (0) instanceof EnumTypeElement)
                        nodes.addElement
                        (new IDLEnumTypeNode
                         (((TypeElement) ((TypeElement) child).getMembers ().elementAt (0))));
                    //nodes.addElement (new IDLTypeNode
                    //	   ((TypeElement)((TypeElement) child).getMembers ().elementAt (0)));
                    //name = ((TypeElement) ((TypeElement) child).getMembers ().elementAt (0)).getName ();
                    type = ((TypeElement) ((TypeElement) child).getMembers ().elementAt (0)).getType ();
                    // add constructed type instances
                    if (DEBUG) {
                        //System.out.println ("name: " + name); // NOI18N
                        System.out.println ("type: " + type); // NOI18N
                    }
                    for (int j=1; j<tmp_members.size (); j++) {
                        if (DEBUG)
                            System.out.println ("adding declarator: " + j); // NOI18N
                        nodes.addElement (new IDLDeclaratorNode
                                          ((DeclaratorElement)tmp_members.elementAt (j)));

                        //if (DEBUG)
                        //System.out.println ("adding member: " + j); // NOI18N
                        //MemberElement tme = new MemberElement (-1);
                        //tme.addMember ((Identifier)tmp_members.elementAt (j));
                        //tme.setName (((Identifier)tmp_members.elementAt (j)).getName ());
                        //tme.setType (type);
                        //tme.setType ("type"); // NOI18N
                        //((Element)tme.getMember (0)).setName (name);
                        //((Element)tme.getMember (0)).setType (type);
                        //nodes.addElement (new IDLMemberNode
                        //		      (tme));

                    }
                }
            }
            else {
                if (DEBUG)
                    System.out.println ("3-----------------------"); // NOI18N
                // simple types => make MemberNodes

                Vector tmp_members = ((TypeElement) child).getMembers ();
                IDLType type = ((TypeElement) child).getType ();
                for (int j=0; j<tmp_members.size (); j++) {
                    if (tmp_members.elementAt (j) instanceof DeclaratorElement)
                        nodes.addElement (new IDLDeclaratorNode
                                          ((DeclaratorElement)tmp_members.elementAt (j)));

                    //MemberElement tme = new MemberElement (-1);
                    //tme.addMember ((Identifier)tmp_members.elementAt (j));
                    //tme.setName (((Identifier)tmp_members.elementAt (j)).getName ());
                    //tme.setType (type);
                    //tme.setType (type);
                    //if (!tme.getType ().equals (tme.getName ())) {
                    // if not member of not_simple type
                    //nodes.addElement (new IDLMemberNode
                    //(tme));
                    //}
                }
            }
            return vector2nodes (nodes);
        }

        if (child instanceof ExceptionElement) {
            if (DEBUG)
                System.out.println ("found exception"); // NOI18N
            nodes.addElement (new IDLExceptionNode ((ExceptionElement) child));
            return vector2nodes (nodes);
        }
        //if (child instanceof DeclaratorElement) {
        //if (DEBUG)
        //System.out.println ("found member"); // NOI18N
        //nodes.addElement (new IDLDeclaratorNode
        //((DeclaratorElement) child));
        //return vector2nodes (nodes);
        //}

        if (child instanceof MemberElement) {
            if (DEBUG)
                System.out.println ("found member"); // NOI18N
            if (child.getMember (0) instanceof DeclaratorElement) {
                for (int j = 0; j<child.getMembers ().size (); j++) {
                    // standard MemberElement with ids
                    nodes.addElement (new IDLDeclaratorNode
                                      ((DeclaratorElement) child.getMember (j)));
                    if (DEBUG)
                        System.out.println ("adding node for " // NOI18N
                                            + ((DeclaratorElement) child.getMember (j)).getName ()
                                            + ": " // NOI18N
                                            + ((DeclaratorElement) child.getMember (j)).getType ());
                }
            }
            else {
                // recursive MemberElement
                Vector tmp_members = child.getMembers ();
                if (child.getMembers ().elementAt (0) instanceof StructTypeElement)
                    nodes.addElement (new IDLStructTypeNode
                                      ((TypeElement) child.getMember (0)));
                if (child.getMembers ().elementAt (0) instanceof UnionTypeElement)
                    nodes.addElement (new IDLUnionTypeNode
                                      ((UnionTypeElement) child.getMember (0)));
                if (child.getMembers ().elementAt (0) instanceof EnumTypeElement)
                    nodes.addElement (new IDLEnumTypeNode
                                      ((TypeElement) child.getMember (0)));

                //nodes.addElement (new IDLTypeNode
                //		((TypeElement) child.getMember (0)));
                //nodes.addElement (new IDLMemberNode
                //		((MemberElement) child.getMember (1))

                IDLType type = ((MemberElement) child).getType ();
                for (int j=1; j<tmp_members.size (); j++) {
                    if (tmp_members.elementAt (j) instanceof DeclaratorElement) {
                        if (DEBUG)
                            System.out.println ("adding declarator: " + j); // NOI18N
                        nodes.addElement (new IDLDeclaratorNode
                                          ((DeclaratorElement)tmp_members.elementAt (j)));
                    }
                    /*
                      if (tmp_members.elementAt (j) instanceof Identifier) {
                      if (DEBUG)
                      System.out.println ("adding member: " + j);
                      MemberElement tme = new MemberElement (-1);
                      tme.addMember ((Identifier)tmp_members.elementAt (j));
                      tme.setName (((Identifier)tmp_members.elementAt (j)).getName ());
                      tme.setType (type);
                      //tme.setType ("type");
                      //((Element)tme.getMember (0)).setName (name);
                      //((Element)tme.getMember (0)).setType (type);
                      nodes.addElement (new IDLMemberNode (tme));
                      }
                    */
                }
            }
            return vector2nodes (nodes);
        }
        if (DEBUG)
            System.out.println ("return empty nodes"); // NOI18N
        return new org.openide.nodes.Node[0];
    }

    /*
      class FileListener extends FileChangeAdapter {
      public void fileChanged (FileEvent e) {
      if (DEBUG)
      System.out.println ("idl file was changed.");

      //IDLDocumentChildren.this.parse ();
      //IDLDocumentChildren.this.createKeys ();

      IDLDocumentChildren.this.startParsing ();
      }
      
      public void fileRenamed (FileRenameEvent e) {
      if (DEBUG)
      System.out.println ("IDLDocumentChildren.FileListener.FileRenamed (" + e + ")");
      }
      }
    */ 
}
/*
 * $Log
 * $
 */

