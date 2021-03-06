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

package org.netbeans.modules.helpbuilder.tree;

import java.awt.event.ActionEvent;
import javax.swing.tree.*;
import java.beans.*;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.IOException;
import java.util.Enumeration;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Locale;
import javax.swing.AbstractAction;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionListener;
import org.netbeans.modules.helpbuilder.plaf.basic.BasicHelpCellRenderer;
import org.netbeans.modules.helpbuilder.processors.HelpSetProcessor;
import org.netbeans.modules.helpbuilder.processors.MapProcessor;
import org.netbeans.modules.helpbuilder.ui.AddIndexPanel;
import org.netbeans.modules.helpbuilder.ui.AddPanel;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;

/**
 *
 * @author Richard Gregor
 * @version   1.1
 */

public class HelpTree extends JTree implements TreeSelectionListener{
   private RemoveAction removeAction = null;
   private AddTocAction    addTocAction = null;
   private AddIndexAction  addIndexAction = null;
   private RightAction  rightAction = null;
   private UpAction     upAction = null;
   private DownAction   downAction = null;
   private LeftAction   leftAction = null;
   private EditTocAction   editTocAction = null;
   private EditIndexAction editIndexAction = null;
   
   private DefaultMutableTreeNode rootNode = null;
    
    public HelpTree(HelpTreeNode node){
        super(node);
        rootNode = (DefaultMutableTreeNode)node;
        addTreeSelectionListener(this);
        //setCellRenderer(new BasicHelpCellRenderer());
    }    
    
    
    public RemoveAction getRemoveAction(){
        if(removeAction == null)
            removeAction = new RemoveAction();
        return removeAction;
    }
    
    public AddTocAction getAddTocAction(){
        if(addTocAction == null)
            addTocAction = new AddTocAction();
        return addTocAction;
    }
    
    public AddIndexAction getAddIndexAction(){
        if(addIndexAction == null)
            addIndexAction = new AddIndexAction();
        return addIndexAction;
    }    
    
    public RightAction getRightAction(){
        if(rightAction == null)
            rightAction = new RightAction();
        return rightAction;
    }
    
    public UpAction getUpAction(){
        if(upAction == null)
            upAction = new UpAction();
        return upAction;
    }
    
    public DownAction getDownAction(){
        if(downAction == null)
            downAction = new DownAction();
        return downAction;
    }    
    
    public LeftAction getLeftAction(){
        if(leftAction == null)
            leftAction = new LeftAction();
        return leftAction;
    }    
    
    public EditTocAction getEditTocAction(){
        if(editTocAction == null)
            editTocAction = new EditTocAction();
        return editTocAction;
    }    
    
    public EditIndexAction getEditIndexAction(){
        if(editIndexAction == null)
            editIndexAction = new EditIndexAction();
        return editIndexAction;
    }  
    
    public void valueChanged(javax.swing.event.TreeSelectionEvent e) {
        TreePath path = HelpTree.this.getSelectionPath();
        if(path != null){
            DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode)path.getLastPathComponent();
            if(selectedNode != null){
                removeAction.setEnabled(true);
                getEditTocAction().setEnabled(true);
                getEditIndexAction().setEnabled(true);
                DefaultMutableTreeNode parent = (DefaultMutableTreeNode)selectedNode.getParent();
                //      if(parent != null){
                if(parent.getIndex(selectedNode) > 0){
                    rightAction.setEnabled(true);
                    upAction.setEnabled(true);
                }else{
                    rightAction.setEnabled(false);
                    if(parent.getParent() != null)
                        upAction.setEnabled(true);
                    else
                        upAction.setEnabled(false);
                }
                DefaultMutableTreeNode superParent = (DefaultMutableTreeNode)parent.getParent();
                
                if(parent.getIndex(selectedNode)<(parent.getChildCount()-1))
                    downAction.setEnabled(true);
                else if((superParent != null)&&(superParent.getChildAfter(parent) != null))
                    downAction.setEnabled(true);
                else
                    downAction.setEnabled(false);
                //      }
                if(parent != rootNode)
                    leftAction.setEnabled(true);
                else
                    leftAction.setEnabled(false);
            }
        }else{
            removeAction.setEnabled(false);
            getEditTocAction().setEnabled(false);
            getEditIndexAction().setEnabled(false);
            leftAction.setEnabled(false);
            upAction.setEnabled(false);
            rightAction.setEnabled(false);
            downAction.setEnabled(false);
        }
    }
    
    /**
     * AddAction class.
     *
     */
    public class AddTocAction extends AbstractAction{
        AddPanel addPanel;
        
        public AddTocAction(){
            super(java.util.ResourceBundle.getBundle("org/netbeans/modules/helpbuilder/ui/Bundle").getString("btnAdd"));            
            addPanel = new AddPanel();            
        }
        
        public void actionPerformed(ActionEvent ev){
            debug("add toc");
            addPanel.clear();
            DialogDescriptor dd = new DialogDescriptor(addPanel,addPanel.getName());            
            dd.setOptions(addPanel.getOptions());            
            Object ret = DialogDisplayer.getDefault().notify(dd);
            if(ret == AddPanel.OK_OPTION){                
                TocTreeNode node = new TocTreeNode(new TocTreeItem(addPanel.getName(),addPanel.getMapTarget(),addPanel.getUrlSpec(),addPanel.isHomeID(),Locale.getDefault()));
                String target = addPanel.getMapTarget();
                if(addPanel.isHomeID())
                    HelpSetProcessor.getDefault().setHomeID(target);                
                String url = addPanel.getUrlSpec();
                if((target != null)&&(target.length() > 0))
                    if((url != null)&&(url.length() > 0))
                        MapProcessor.getDefault().addMap(new MapProcessor.Map(target, url));                
                DefaultTreeModel model = (DefaultTreeModel)getModel();                        
                TreePath path = HelpTree.this.getSelectionPath();
                DefaultMutableTreeNode parentNode = rootNode;
                if(path != null){
                    DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode)path.getLastPathComponent();
                    if((path != null) && (selectedNode != null))
                        parentNode = (DefaultMutableTreeNode)selectedNode.getParent();
                    model.insertNodeInto(node,parentNode,parentNode.getIndex(selectedNode)+1);
                    
                }else{
                    model.insertNodeInto(node, parentNode,  parentNode.getChildCount());                    
                } 
                setActiveNode(node);                
                
            }
        }
    }    
    
    /**
     * AddIndexAction class.
     *
     */
    public class AddIndexAction extends AbstractAction{
        AddIndexPanel addPanel;
        
        public AddIndexAction(){
            super(java.util.ResourceBundle.getBundle("org/netbeans/modules/helpbuilder/ui/Bundle").getString("btnAdd"));            
            addPanel = new AddIndexPanel();            
        }
        
        public void actionPerformed(ActionEvent ev){
            debug("add index");
            addPanel.clear();
            DialogDescriptor dd = new DialogDescriptor(addPanel,addPanel.getName());            
            dd.setOptions(addPanel.getOptions());            
            Object ret = DialogDisplayer.getDefault().notify(dd);
            if(ret == AddIndexPanel.OK_OPTION){                
                IndexTreeNode node = new IndexTreeNode(new IndexTreeItem(addPanel.getName(),addPanel.getMapTarget(),addPanel.getUrlSpec(),false,Locale.getDefault()));
                DefaultTreeModel model = (DefaultTreeModel)getModel();                        
                TreePath path = HelpTree.this.getSelectionPath();
                String target = addPanel.getMapTarget();
                String url = addPanel.getUrlSpec();
                if((target != null)&&(target.length() > 0))
                    if((url != null)&&(url.length() > 0))
                        MapProcessor.getDefault().addMap(new MapProcessor.Map(target, url)); 
                DefaultMutableTreeNode parentNode = rootNode;
                if(path != null){
                    DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode)path.getLastPathComponent();
                    if((path != null) && (selectedNode != null))
                        parentNode = (DefaultMutableTreeNode)selectedNode.getParent();
                    model.insertNodeInto(node,parentNode,parentNode.getIndex(selectedNode)+1);
                    
                }else{
                    model.insertNodeInto(node, parentNode,  parentNode.getChildCount());                    
                } 
                setActiveNode(node);
                
            }
        }
    }    
    
    /**
     * EditAction class.
     *
     */
    public class EditTocAction extends AbstractAction{
        AddPanel editPanel;
        
        public EditTocAction(){
            super(java.util.ResourceBundle.getBundle("org/netbeans/modules/helpbuilder/ui/Bundle").getString("btnEdit"));
            editPanel = new AddPanel();
            setEnabled(false);
        }
        
        public void actionPerformed(ActionEvent ev){
            debug("edit toc");
            editPanel.clear();            
            editPanel.setName(java.util.ResourceBundle.getBundle("org/netbeans/modules/helpbuilder/ui/Bundle").getString("Title_EditPanel"));            
            TreePath path = HelpTree.this.getSelectionPath();
            DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode)path.getLastPathComponent(); 
            TocTreeItem item = (TocTreeItem) selectedNode.getUserObject();
            editPanel.setName(item.getName());
            String oldUrl = item.getURLSpec();
            editPanel.setUrlSpec(oldUrl);
            String oldTarget = item.getTarget();
            editPanel.setMapTarget(oldTarget);
            editPanel.setHomeID(item.isHomeID());
            
            DialogDescriptor dd = new DialogDescriptor(editPanel,editPanel.getName());            
            dd.setOptions(editPanel.getOptions());            
            Object ret = DialogDisplayer.getDefault().notify(dd);
            if(ret == AddPanel.OK_OPTION){
                item.setName(editPanel.getName());
                String url = editPanel.getUrlSpec();
                item.setURLSpec(url);
                String target = editPanel.getMapTarget();
                if((target != null)&&(target.length() > 0)){
                    if((url != null)&&(url.length() > 0)){
                        MapProcessor.getDefault().addMap(new MapProcessor.Map(target, url)); 
                        MapProcessor.getDefault().removeMap(oldTarget, oldUrl);
                    }
                }
                item.setTarget(target);
                item.setHomeID(editPanel.isHomeID());
                ((DefaultTreeModel)getModel()).reload(selectedNode); 
                setActiveNode(selectedNode);
            }
        }
    }
    
    /**
     * EditIndexAction class.
     *
     */
    public class EditIndexAction extends AbstractAction{
        AddIndexPanel editPanel;
        
        public EditIndexAction(){
            super(java.util.ResourceBundle.getBundle("org/netbeans/modules/helpbuilder/ui/Bundle").getString("btnEdit"));
            editPanel = new AddIndexPanel();
            setEnabled(false);
        }
        
        public void actionPerformed(ActionEvent ev){
            debug("edit index");
            editPanel.clear();            
            editPanel.setName(java.util.ResourceBundle.getBundle("org/netbeans/modules/helpbuilder/ui/Bundle").getString("Title_EditPanel"));
            
            TreePath path = HelpTree.this.getSelectionPath();
            DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode)path.getLastPathComponent(); 
            IndexTreeItem item = (IndexTreeItem) selectedNode.getUserObject();
            editPanel.setName(item.getName());
            String oldUrl = item.getURLSpec();
            String oldTarget = item.getTarget();
            editPanel.setMapTarget(oldTarget);
            
            DialogDescriptor dd = new DialogDescriptor(editPanel,editPanel.getName());            
            dd.setOptions(editPanel.getOptions());            
            Object ret = DialogDisplayer.getDefault().notify(dd);
            if(ret == AddPanel.OK_OPTION){
                item.setName(editPanel.getName());
                String url = editPanel.getUrlSpec();
                item.setURLSpec(url);
                String target = editPanel.getMapTarget();
                if((target != null)&&(target.length() > 0)){
                    if((url != null)&&(url.length() > 0)){
                        MapProcessor.getDefault().addMap(new MapProcessor.Map(target, url)); 
                        MapProcessor.getDefault().removeMap(oldTarget, oldUrl);
                    }
                }
                item.setTarget(target);
                ((DefaultTreeModel)getModel()).reload(selectedNode); 
                setActiveNode(selectedNode);
            }
        }
    }
    
    /**
     * RemoveAction class.
     *
     */
    class RemoveAction extends AbstractAction{
        
        public RemoveAction(){
            super(java.util.ResourceBundle.getBundle("org/netbeans/modules/helpbuilder/ui/Bundle").getString("btnRemove"));
            setEnabled(false);            
        }
        
        public void actionPerformed(ActionEvent ev){
            debug("remove");
            DefaultMutableTreeNode node = null;
            DefaultTreeModel model = (DefaultTreeModel)getModel();
            TreePath[] paths = getSelectionPaths();
            for(int i = 0 ; i < paths.length; i++){
                if(paths[i] != null){
                    node = (DefaultMutableTreeNode)paths[i].getLastPathComponent();
                    DefaultMutableTreeNode parent = (DefaultMutableTreeNode)node.getParent();
                    if(parent != null){
                        int index = parent.getIndex(node);
                        if(index > 0)
                            setActiveNode((DefaultMutableTreeNode)parent.getChildAt(index -1));
                        else
                            setActiveNode(parent);
                    }
                    Object item = node.getUserObject();
                    String url;
                    String target;
                    if(item instanceof TocTreeItem){
                        url = ((TocTreeItem)item).getURLSpec();
                        target = ((TocTreeItem)item).getTarget();
                    }else{
                        url = ((IndexTreeItem)item).getURLSpec();
                        target = ((IndexTreeItem)item).getTarget();
                    }   
                    if((url != null) && (url.length() >0))   
                        MapProcessor.getDefault().removeMap(target, url);                     
                    model.removeNodeFromParent(node);  
                }
            }            
               
        }
    }   
    
    
    
     /**
     * RightAction class.
     *
     */
    public class RightAction extends AbstractAction{
        
        public RightAction(){
            super(java.util.ResourceBundle.getBundle("org/netbeans/modules/helpbuilder/ui/Bundle").getString("btnRight"));
            setEnabled(false);
        }
        
        public void actionPerformed(ActionEvent ev){
            debug("right");                        
            DefaultTreeModel model = (DefaultTreeModel)getModel();                        
            TreePath path = HelpTree.this.getSelectionPath();
            DefaultMutableTreeNode parentNode = rootNode;
            DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode)path.getLastPathComponent(); 
            if(selectedNode != null)
                parentNode = (DefaultMutableTreeNode)selectedNode.getParent();
            if(parentNode != null){
                int index = parentNode.getIndex(selectedNode);
                if( index > 0){
                    parentNode = (DefaultMutableTreeNode)parentNode.getChildBefore(selectedNode);
                    model.removeNodeFromParent(selectedNode);
                    model.insertNodeInto(selectedNode,parentNode,parentNode.getChildCount());
                }else{
                    //do nothing                    
                }
            }            
            setActiveNode(selectedNode);
        }
    }
    
    /**
     * LeftAction class.
     *
     */
    public class LeftAction extends AbstractAction{
        
        public LeftAction(){
            super(java.util.ResourceBundle.getBundle("org/netbeans/modules/helpbuilder/ui/Bundle").getString("btnLeft"));
            setEnabled(false);
        }
        
        public void actionPerformed(ActionEvent ev){
            debug("left");                        
            DefaultTreeModel model = (DefaultTreeModel)getModel();                        
            TreePath path = HelpTree.this.getSelectionPath();
            DefaultMutableTreeNode parentNode = rootNode;
            DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode)path.getLastPathComponent(); 
            if(selectedNode != null)
                parentNode = (DefaultMutableTreeNode)selectedNode.getParent();            
            if(parentNode != null){
                DefaultMutableTreeNode superParent = (DefaultMutableTreeNode)parentNode.getParent();
                if(superParent != null){
                    int index = superParent.getIndex(parentNode);
                    model.removeNodeFromParent(selectedNode);
                    model.insertNodeInto(selectedNode,superParent,index+1);
                }else{
                    //do nothing                    
                }
            }            
            
            setActiveNode(selectedNode);
        }
    }
    
     /**
     * UpAction class.
     *
     */
    public class UpAction extends AbstractAction{
        
        public UpAction(){
            super(java.util.ResourceBundle.getBundle("org/netbeans/modules/helpbuilder/ui/Bundle").getString("btnUp"));
            setEnabled(false);
        }
        
        public void actionPerformed(ActionEvent ev){
            debug("up");                        
            DefaultTreeModel model = (DefaultTreeModel)getModel();                        
            TreePath path = HelpTree.this.getSelectionPath();
            DefaultMutableTreeNode parentNode = rootNode;
            DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode)path.getLastPathComponent(); 
            if((path != null) && (selectedNode != null))
                parentNode = (DefaultMutableTreeNode)selectedNode.getParent();
            if(parentNode != null){
                int index = parentNode.getIndex(selectedNode);
                if( index > 0){
                    model.removeNodeFromParent(selectedNode);
                    model.insertNodeInto(selectedNode,parentNode,index-1);
                }else{
                    DefaultMutableTreeNode superParent = (DefaultMutableTreeNode)parentNode.getParent();
                    model.removeNodeFromParent(selectedNode);
                    model.insertNodeInto(selectedNode, superParent, superParent.getIndex(parentNode));                   
                }
            }                        
            
            setActiveNode(selectedNode);
        }
    }
    
     /**
     * DownAction class.
     *
     */
    public class DownAction extends AbstractAction{
        
        public DownAction(){
            super(java.util.ResourceBundle.getBundle("org/netbeans/modules/helpbuilder/ui/Bundle").getString("btnDown"));
            setEnabled(false);
        }
        
        public void actionPerformed(ActionEvent ev){
            debug("down");                        
            DefaultTreeModel model = (DefaultTreeModel)getModel();                        
            TreePath path = HelpTree.this.getSelectionPath();
            DefaultMutableTreeNode parentNode = rootNode;
            DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode)path.getLastPathComponent(); 
            if((path != null) && (selectedNode != null))
                parentNode = (DefaultMutableTreeNode)selectedNode.getParent();
            if(parentNode != null){
                int index = parentNode.getIndex(selectedNode);
                if( index < parentNode.getChildCount() -1 ){
                    model.removeNodeFromParent(selectedNode);
                    model.insertNodeInto(selectedNode,parentNode,index+1);
                }else{
                    DefaultMutableTreeNode superParent = (DefaultMutableTreeNode)parentNode.getParent();
                    model.removeNodeFromParent(selectedNode);
                    model.insertNodeInto(selectedNode, superParent, superParent.getIndex(parentNode)+1);                    
                }
            }            
            
            setActiveNode(selectedNode);
        }
    }
    
    public void setActiveNode(DefaultMutableTreeNode node){
        TreePath newPath = new TreePath(node.getPath());
        makeVisible(newPath);
        setSelectionPath(newPath);
        scrollPathToVisible(newPath);
    }
    
    private static final boolean debug = false;
    private static void debug(String msg) {
  	if (debug) {
  	    System.err.println("HelpTree: "+msg);
	}
    }
    

}
