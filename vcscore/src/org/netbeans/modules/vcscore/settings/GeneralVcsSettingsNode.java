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

package org.netbeans.modules.vcscore.settings;

import org.openide.actions.*;
import org.openide.nodes.*;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.SystemAction;
import org.openide.filesystems.*;
import org.openide.loaders.*;
import org.openide.*;
import org.openide.util.*;
import org.openide.cookies.*;
import java.beans.*;

/** A node with some children.
 *
 * @author builder
 */
public class GeneralVcsSettingsNode extends AbstractNode {

    public GeneralVcsSettingsNode() {
        super (new Children.Array());
        setIconBase("org/netbeans/modules/vcscore/settings/vcsSettings"); //NOI18N
        // Whatever is most relevant to a user:
        setDefaultAction (SystemAction.get (PropertiesAction.class));
        // Set FeatureDescriptor stuff:
        setName ("General Vcs Settings"); // or, super.setName if needed  //NOI18N
        setDisplayName(NbBundle.getMessage(GeneralVcsSettingsNode.class, "LBL_GeneralVcsSettingsNode")); //NOI18N
        setShortDescription(NbBundle.getMessage(GeneralVcsSettingsNode.class, "HINT_GeneralVcsSettingsNode")); //NOI18N
        FileSystem defFs = TopManager.getDefault().getRepository().getDefaultFileSystem();
        FileObject fo = defFs.findResource("Services/Hidden/VersionControl"); //NOI18N
        if (fo != null) {
            DataObject dobj;
            try {
                dobj = DataObject.find(fo);
            } catch (DataObjectNotFoundException exc) {
                dobj = null;
            }
            if (dobj != null && dobj instanceof DataFolder) {
                DataFolder folder = (DataFolder)dobj;
                java.util.Enumeration enum = folder.children();
                getChildren().remove(getChildren().getNodes());
                while (enum.hasMoreElements()) {
                    Object obj = enum.nextElement();
                    if (obj instanceof InstanceDataObject) {
                        InstanceDataObject ido = (InstanceDataObject)obj;
                        if (ido.isValid()) {
                            InstanceCookie cook = (InstanceCookie)ido.getCookie(InstanceCookie.class);
                            getChildren().add(new Node[] {ido.getNodeDelegate().cloneNode()});
                        }
                    }
                }
            }
                
        }
        // Add cookies, e.g.:
        /*
        getCookieSet ().add (new OpenCookie () {
		public void open () {
		    // Open something useful...
		}
	    });
        */
        // Make reorderable:
        // getCookieSet ().add (new ReorderMe ());
    }

    // Create the popup menu:
    protected SystemAction[] createActions () {
        return new SystemAction[] {
                   SystemAction.get (OpenLocalExplorerAction.class),
                   null,
                   // SystemAction.get (MyFavoriteAction.class),
                   // null,                     // separator
                   /* according to what it can do:
                   SystemAction.get (ReorderAction.class),
                   null,
                   SystemAction.get (CutAction.class),
                   SystemAction.get (CopyAction.class),
                   SystemAction.get (PasteAction.class),
                   null,
                   SystemAction.get (DeleteAction.class),
                   SystemAction.get (RenameAction.class),
                   null,
                   SystemAction.get (NewAction.class),
                   null,
                   */
//                   SystemAction.get (ToolsAction.class),
//                   null,
                   SystemAction.get (PropertiesAction.class),
               };
    }

    public HelpCtx getHelpCtx () {
        return new HelpCtx (GeneralVcsSettingsNode.class);
    }

    
    // RECOMMENDED - handle cloning specially (so as not to invoke the overhead of FilterNode):
    /*
    public Node cloneNode () {
	// Try to pass in similar constructor params to what you originally got:
        return new GeneralVcsSettingsNodeNode ();
    }
    */

    // Create a property sheet:
    protected Sheet createSheet () {
	Sheet sheet = super.createSheet ();
	// Make sure there is a "Properties" set:
	Sheet.Set props = sheet.get (Sheet.PROPERTIES); // get by name, not display name
	if (props == null) {
	    props = Sheet.createPropertiesSet ();
	    sheet.put (props);
	}
	putProperties(props);
        return sheet;
    }
    
    private void putProperties(Sheet.Set props) {
        FileSystem defFs = TopManager.getDefault().getRepository().getDefaultFileSystem();
        FileObject fo = defFs.findResource("Services/Hidden/org-netbeans-modules-vcscore-settings-GeneralVcsSettings.settings"); //NOI18N
        if (fo != null) {
            DataObject dobj;
            try {
                dobj = DataObject.find(fo);
            } catch (DataObjectNotFoundException exc) {
                dobj = null;
            }
            if (dobj != null && dobj instanceof InstanceDataObject) {
                InstanceDataObject ido = (InstanceDataObject)dobj;
                InstanceCookie cook = (InstanceCookie)ido.getCookie(InstanceCookie.class);
                try {
                    GeneralVcsSettings settings = (GeneralVcsSettings)cook.instanceCreate();
                    BeanInfo info = java.beans.Introspector.getBeanInfo(settings.getClass());
                    PropertyDescriptor[] descriptors = info.getPropertyDescriptors();
                    for (int i=0; i < descriptors.length; i++) {
                        if (descriptors[i].isHidden()) continue;
                        PropertySupport.Reflection refl = new PropertySupport.Reflection(settings, descriptors[i].getPropertyType(),
                        descriptors[i].getReadMethod(), descriptors[i].getWriteMethod());
                        refl.setDisplayName(descriptors[i].getDisplayName());
                        refl.setName(descriptors[i].getName());
                        refl.setShortDescription(descriptors[i].getShortDescription());
                        refl.setPropertyEditorClass(descriptors[i].getPropertyEditorClass());
                        Node.Property removed = props.put(refl);
                    }
                } catch (Exception exc) {
                }
            }
        }
    }

    // Permit new subnodes to be created:
    /*
    public NewType[] getNewTypes () {
	return new NewType[] { new NewType () {
		public String getName () {
                    return NbBundle.getMessage (GeneralVcsSettingsNodeNode.class, "LBL_NewType");
		}
		// If you have help:
		// public HelpCtx getHelpCtx () {
                //     return GeneralVcsSettingsNodeNode.class.getName () + ".newType";
		// }
		public void create () throws IOException {
		    // do whatever you need, e.g.:
                    getGeneralVcsSettingsNodeChildren ().addKey (someNewKey);
		    // Throw an IOException if you are creating an underlying
		    // object and this fails.
		}
	    }
	};
    }
    */

    // Permit things to be pasted into this node:
    /*
    protected void createPasteTypes (final Transferable t, List l) {
	// Make sure to pick up super impl, which adds intelligent node paste type:
	super.createPasteTypes (t, l);
	if (t.isDataFlavorSupported (DataFlavor.stringFlavor)) {
	    l.add (new PasteType () {
		    public String getName () {
                        return NbBundle.getMessage (GeneralVcsSettingsNodeNode.class, "LBL_PasteType");
		    }
		    // If you have help:
		    // public HelpCtx getHelpCtx () {
                    //     return GeneralVcsSettingsNodeNode.class.getName () + ".pasteType";
		    // }
		    public Transferable paste () throws IOException {
			try {
			    String data = (String) t.getTransferData (DataFlavor.stringFlavor);
			    // Or, you can look for nodes and related things in the transferable, using e.g.:
			    // Node n = NodeTransfer.node (t, NodeTransfer.COPY);
			    // Node[] ns = NodeTransfer.nodes (t, NodeTransfer.MOVE);
			    // MyCookie cookie = (MyCookie) NodeTransfer.cookie (t, NodeTransfer.COPY, MyCookie.class);
			    // do something, e.g.:
                            getGeneralVcsSettingsNodeChildren ().addKey (data);
			    // Throw an IOException if you are creating an underlying
			    // object and this fails.
			    // To leave the clipboard as is:
			    return null;
			    // To clear the clipboard:
			    // return ExTransferable.EMPTY;
			} catch (UnsupportedFlavorException ufe) {
			    // Should not happen, since t said it supported this flavor, but:
			    throw new IOException (ufe.getMessage ());
			}
		    }
		});
	}
    }
    */

    // Handle renaming:
    /*
    public boolean canRename () {
	return true;
    }
    public void setName (String nue) {
	// Update visible name, fire property changes:
	super.setName (nue);
	// perform additional actions, i.e. rename underlying object
    }
    */

    // Handle deleting:
    /*
    public boolean canDestroy () {
	return true;
    }
    public void destroy () throws IOException {
	// Actually remove the node itself and fire property changes:
	super.destroy ();
	// perform additional actions, i.e. delete underlying object
	// (and don't forget about objects represented by your children!)
    }
    */

    // Handle copying and cutting specially:
    /*
    public boolean canCopy () {
	return true;
    }
    public boolean canCut () {
	return true;
    }
    public Transferable clipboardCopy () {
	// Add to, do not replace, the default node copy flavor:
	ExTransferable et = ExTransferable.create (super.clipboardCopy ());
	et.put (new ExTransferable.Single (DataFlavor.stringFlavor) {
		protected Object getData () {
                    return GeneralVcsSettingsNodeNode.this.getDisplayName ();
		}
	    });
	return et;
    }
    public Transferable clipboardCut () {
	// Add to, do not replace, the default node cut flavor:
	ExTransferable et = ExTransferable.create (super.clipboardCut ());
	// This is not so useful because this node will not be destroyed afterwards
	// (it is up to the paste type to decide whether to remove the "original",
	// and it is not safe to assume that getData will only be called once):
	et.put (new ExTransferable.Single (DataFlavor.stringFlavor) {
		protected Object getData () {
                    return GeneralVcsSettingsNodeNode.this.getDisplayName ();
		}
	    });
	return et;
    }
    */

    // Permit user to customize whole node at once (instead of per-property):
    /*
    public boolean hasCustomizer () {
	return true;
    }
    public Component getCustomizer () {
	return new MyCustomizingPanel (this);
    }
    */

    // Permit node to be reordered (you may also want to put
    // MoveUpAction and MoveDownAction on the subnodes, if you can,
    // but ReorderAction on the parent is enough):
    /*
    private class ReorderMe extends Index.Support {
      
	public Node[] getNodes () {
            return GeneralVcsSettingsNodeNode.this.getChildren ().getNodes ();
	}
      
	public int getNodesCount () {
	    return getNodes ().length;
	}
      
	// This assumes that there is exactly one child node per key.
	// If you are using e.g. Children.Array, you can use shortcut implementations
	// of the Index cookie.
	public void reorder (int[] perm) {
	    // Remember: {2, 0, 1} cycles three items forwards.
            List old = GeneralVcsSettingsNodeNode.this.getGeneralVcsSettingsNodeChildren ().myKeys;
	    if (list.size () != perm.length) throw new IllegalArgumentException ();
	    List nue = new ArrayList (perm.length);
	    for (int i = 0; i < perm.length; i++)
		nue.set (i, old.get (perm[i]));
            GeneralVcsSettingsNodeNode.this.getGeneralVcsSettingsNodeChildren ().setKeys (nue);
	}
      
    }
    */

}
