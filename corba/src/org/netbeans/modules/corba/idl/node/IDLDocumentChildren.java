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

package com.netbeans.enterprise.modules.corba.idl.node;

import java.util.Vector;

import org.openide.nodes.*;
import org.openide.filesystems.*;
//import org.openide.

import com.netbeans.enterprise.modules.corba.*;
import com.netbeans.enterprise.modules.corba.idl.src.*;


/**
 * Class IDLDocumentChildren
 *
 * @author Karel Gardas
 */
public class IDLDocumentChildren extends Children.Keys {

   //public static final boolean DEBUG = true;
   public static final boolean DEBUG = false;
   private IDLDataObject ido;
   

   //private com.netbeans.enterprise.modules.corba.idl.src.SimpleNode src;
   private Element src;

   private IDLNode idlNode;

   public IDLDocumentChildren (IDLDataObject v) 
      throws java.io.FileNotFoundException {
      super ();
      ido = v;
      //idlNode = node;

   }

   public IDLDocumentChildren (Element tree) {
      src = tree;
      if (src != null)
	 createKeys ();
   }

   public void setNode (IDLNode node) {
      idlNode = node;
   }

   public void setSrc (Element s) {
      src = s;
   }

   public void createKeys () {
      Vector keys = new Vector ();
      if (DEBUG)
	 System.out.println ("createKeys ()");
      if (src == null)
	 return;
      int counter = src.getMembers ().size ();
      if (DEBUG)
	 System.out.println ("members: " + src.getMembers ());
      //com.netbeans.enterprise.modules.corba.idl.src.Node child;
      Element child;
      if (DEBUG)
	 System.out.println ("children: " + counter);
      for (int i=0; i<counter; i++) {
	 if (DEBUG)
	    System.out.println ("----cycle: " + i + "--------");
	 child = (Element)src.getMember (i);
	 if (child instanceof InterfaceElement) {
	    if (DEBUG)
	       System.out.println ("found interface");
	    keys.addElement ((org.openide.nodes.Node)new IDLInterfaceNode 
			     ((InterfaceElement) child));
	    continue;
	 }
	 if (child instanceof OperationElement) {
	    if (DEBUG)
	       System.out.println ("found operation");
	    keys.addElement ((org.openide.nodes.Node)new IDLOperationNode 
			     ((OperationElement) child));
	    continue;
	 }
	 if (child instanceof AttributeElement) {
	    if (DEBUG)
	       System.out.println ("found attribute");
	    keys.addElement ((org.openide.nodes.Node)new IDLAttributeNode 
			     ((AttributeElement) child));
	    continue;
	 }

	 // test if this is important for creating struct or union or enum node or isn't
	 // it is important for struct (enums and unoins) nested for example in union 
	 if (child instanceof StructTypeElement) {
	    if (DEBUG)
	       System.out.println ("found struct type element");
	    keys.addElement ((org.openide.nodes.Node)new IDLStructTypeNode 
			     ((TypeElement) child));
	    continue;
	 }
	 if (child instanceof UnionTypeElement) {
	    if (DEBUG)
	       System.out.println ("found union type element");
	    keys.addElement ((org.openide.nodes.Node)new IDLUnionTypeNode 
			     ((UnionTypeElement) child));
	    continue;
	 }
	 if (child instanceof UnionMemberElement) {
	    if (DEBUG)
	       System.out.println ("found union member element");
	    keys.addElement ((org.openide.nodes.Node)new IDLUnionMemberNode
                             ((UnionMemberElement) child));
	    continue;
	 }
	 if (child instanceof EnumTypeElement) {
	    if (DEBUG)
	       System.out.println ("found enum type element");
	    keys.addElement ((org.openide.nodes.Node)new IDLEnumTypeNode 
			     ((TypeElement) child));
	    continue;
	 }

	 if (child instanceof ConstElement) {
	    if (DEBUG)
               System.out.println ("found const element");
            keys.addElement ((org.openide.nodes.Node)new IDLConstNode
                             ((ConstElement) child));
	 }
	 if (child instanceof TypeElement) {
	    if (DEBUG)
	       System.out.println ("found type element");
	    Vector members = ((TypeElement) child).getMembers ();
	    if (DEBUG)
	       System.out.println ("members" + members);
	    if ((members.elementAt (0) instanceof StructTypeElement ||
		 members.elementAt (0) instanceof UnionTypeElement ||
		 members.elementAt (0) instanceof EnumTypeElement)) {
	       if (members.size () == 1) {
		  if (DEBUG)
		     System.out.println ("1----------------------------");
		  // constructed type whithout type children
		  if (members.elementAt (0) instanceof StructTypeElement)
		     keys.addElement ((org.openide.nodes.Node)new IDLStructTypeNode
				      (((TypeElement) ((TypeElement) child).getMembers ().elementAt (0))));
		  if (members.elementAt (0) instanceof UnionTypeElement)
		     keys.addElement ((org.openide.nodes.Node)new IDLUnionTypeNode
				      (((UnionTypeElement) ((TypeElement) child).getMembers ().elementAt (0))));
		  if (members.elementAt (0) instanceof EnumTypeElement)
		     keys.addElement ((org.openide.nodes.Node)new IDLEnumTypeNode
				      (((TypeElement) ((TypeElement) child).getMembers ().elementAt (0))));

	       }
	       else {
		  if (DEBUG)
		     System.out.println ("2--------------------------");
		  // constructed type whith type children
		  Vector tmp_members = ((TypeElement) child).getMembers ();
		  // add constructed type
		  String name, type;
		  if (tmp_members.elementAt (0) instanceof StructTypeElement)
		     keys.addElement ((org.openide.nodes.Node)new IDLStructTypeNode
				      (((TypeElement) ((TypeElement) child).getMembers ().elementAt (0))));
		  if (tmp_members.elementAt (0) instanceof UnionTypeElement)
		     keys.addElement ((org.openide.nodes.Node)new IDLUnionTypeNode
				      (((UnionTypeElement) ((TypeElement) child).getMembers ().elementAt (0))));
		  if (tmp_members.elementAt (0) instanceof EnumTypeElement)
		     keys.addElement ((org.openide.nodes.Node)new IDLEnumTypeNode
				      (((TypeElement) ((TypeElement) child).getMembers ().elementAt (0))));
		  //keys.addElement ((org.openide.nodes.Node)new IDLTypeNode
		  //	   ((TypeElement)((TypeElement) child).getMembers ().elementAt (0)));
		  //name = ((TypeElement) ((TypeElement) child).getMembers ().elementAt (0)).getName ();
		  type = ((TypeElement) ((TypeElement) child).getMembers ().elementAt (0)).getType ();
		  // add constructed type instances
		  if (DEBUG) {
		     //System.out.println ("name: " + name);
		     System.out.println ("type: " + type);
		  }
		  for (int j=1; j<tmp_members.size (); j++) {
		     if (DEBUG)
                        System.out.println ("adding declarator: " + j);
		     keys.addElement ((org.openide.nodes.Node)new IDLDeclaratorNode
				      ((DeclaratorElement)tmp_members.elementAt (j)));
		     /*
		     if (DEBUG)
			System.out.println ("adding member: " + j);
		     MemberElement tme = new MemberElement (-1);
		     //tme.addMember ((Identifier)tmp_members.elementAt (j));
		     tme.setName (((Identifier)tmp_members.elementAt (j)).getName ());
		     tme.setType (type);
		     //tme.setType ("type");
		     //((Element)tme.getMember (0)).setName (name);
		     //((Element)tme.getMember (0)).setType (type);
		     keys.addElement ((org.openide.nodes.Node)new IDLMemberNode  
		     		      (tme));
		     */
		  }
	       }
	    }
	    else {
	       if (DEBUG)
		  System.out.println ("3-----------------------");
	       // simple types => make MemberNodes
	       
	       Vector tmp_members = ((TypeElement) child).getMembers ();
	       String type = ((TypeElement) child).getType ();
	       for (int j=0; j<tmp_members.size (); j++) {
		  if (tmp_members.elementAt (j) instanceof DeclaratorElement)
		     keys.addElement ((org.openide.nodes.Node)new IDLDeclaratorNode
				      ((DeclaratorElement)tmp_members.elementAt (j)));
		  /*
		  MemberElement tme = new MemberElement (-1);
		  //tme.addMember ((Identifier)tmp_members.elementAt (j));
		  tme.setName (((Identifier)tmp_members.elementAt (j)).getName ());
		  tme.setType (type);
		  //tme.setType (type);
		  if (!tme.getType ().equals (tme.getName ())) {
		     // if not member of not_simple type
		     keys.addElement ((org.openide.nodes.Node)new IDLMemberNode
				      (tme));
		  }
		  */
	       }
	    }
	    continue;
	 }
	 if (child instanceof ExceptionElement) {
	    if (DEBUG)
	       System.out.println ("found exception");
	    keys.addElement ((org.openide.nodes.Node)new IDLExceptionNode 
			     ((ExceptionElement) child));
	    continue;
	 }
	 /*
	 if (child instanceof DeclaratorElement) {
	    if (DEBUG)
	       System.out.println ("found member");
	    keys.addElement ((org.openide.nodes.Node)new IDLDeclaratorNode 
			     ((DeclaratorElement) child));
	    continue;
	 }
	 */

	 if (child instanceof MemberElement) {
	    if (DEBUG)
	       System.out.println ("found member");
	    if (child.getMember (0) instanceof DeclaratorElement) {
	       for (int j = 0; j<child.getMembers ().size (); j++) {
		  // standard MemberElement with ids
		  keys.addElement ((org.openide.nodes.Node)new IDLDeclaratorNode 
				   ((DeclaratorElement) child.getMember (j)));
		  if (DEBUG)
		     System.out.println ("adding node for "
					 + ((DeclaratorElement) child.getMember (j)).getName ()
					 + ": "
					 + ((DeclaratorElement) child.getMember (j)).getType ());
	       }
	    }
	    else {
	       // recursive MemberElement
	       Vector tmp_members = child.getMembers ();
	       if (child.getMembers ().elementAt (0) instanceof StructTypeElement)
		  keys.addElement ((org.openide.nodes.Node)new IDLStructTypeNode
				   ((TypeElement) child.getMember (0)));
	       if (child.getMembers ().elementAt (0) instanceof UnionTypeElement)
		  keys.addElement ((org.openide.nodes.Node)new IDLUnionTypeNode
				   ((UnionTypeElement) child.getMember (0)));
	       if (child.getMembers ().elementAt (0) instanceof EnumTypeElement)
		  keys.addElement ((org.openide.nodes.Node)new IDLEnumTypeNode
				   ((TypeElement) child.getMember (0)));
	       
	       //keys.addElement ((org.openide.nodes.Node)new IDLTypeNode
	       //		((TypeElement) child.getMember (0)));
	       //keys.addElement ((org.openide.nodes.Node)new IDLMemberNode
	       //		((MemberElement) child.getMember (1))
	       String type = ((MemberElement) child).getType ();
	       for (int j=1; j<tmp_members.size (); j++) {
		  if (DEBUG)
		     System.out.println ("adding declarator: " + j);
		  keys.addElement ((org.openide.nodes.Node)new IDLDeclaratorNode
				   ((DeclaratorElement)tmp_members.elementAt (j)));
		  /*
		  if (DEBUG)
		     System.out.println ("adding member: " + j);
		  MemberElement tme = new MemberElement (-1);
		  //tme.addMember ((Identifier)tmp_members.elementAt (j));
		  tme.setName (((Identifier)tmp_members.elementAt (j)).getName ());
		  tme.setType (type);
		  //tme.setType ("type");
		  //((Element)tme.getMember (0)).setName (name);
		  //((Element)tme.getMember (0)).setType (type);
		  keys.addElement ((org.openide.nodes.Node)new IDLMemberNode  
				   (tme));
		  */
	       }
	    }
	 }
      }

      setKeys (keys);
      if (DEBUG)
	 System.out.println ("---end of createKeys ()----------");
   }

   protected org.openide.nodes.Node[] createNodes (Object key) {
      return new org.openide.nodes.Node[] { (org.openide.nodes.Node) key};
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
