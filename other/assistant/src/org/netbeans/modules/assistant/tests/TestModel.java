/*
 * TestModel.java
 *
 * Created on October 25, 2002, 11:47 AM
 */

package org.netbeans.modules.assistant.tests;

import org.netbeans.modules.assistant.*;

import java.net.*;
/**
 *
 * @author  rg125988
 */
public class TestModel{
    static URL url,url1,url2,url3,descURL;   
    static DefaultAssistantModel model;
    
    /** Creates a new instance of TestModel */
    public TestModel() {
        Class clazz = null;
        try{
            clazz = Class.forName("org.netbeans.modules.assistant.AssistantView");
        }catch(Exception e){
            debug("class error");
        }
        java.net.URL url = null;
        java.net.URL url1 = null;
        java.net.URL url2 = null;
        java.net.URL url3 = null;
        java.net.URL descURL = null;
        try{
            url = getClass().getResource("/org/netbeans/modules/assistant/pages/DefaultContent.html");
            url1 = getClass().getResource("/org/netbeans/modules/assistant/pages/DefaultContent1.html");
            url2 = getClass().getResource("/org/netbeans/modules/assistant/pages/DefaultContent2.html");
            url3 = getClass().getResource("/org/netbeans/modules/assistant/pages/DefaultContent3.html");
            descURL = getClass().getResource("/org/netbeans/modules/assistant/pages/DefaultDescription.html");                        
        }catch(Exception e){
            debug("exception in test model: "+e);
        }
    }
    
    public static AssistantModel getModel(){
        AssistantSection[] sections = new AssistantSection[3];
        sections[0] = new AssistantSection("IDE Navigator");
        sections[1] = new AssistantSection("Dynamic Help");
        sections[2] = new AssistantSection("Description");        
        
        sections[0].add(new javax.swing.tree.DefaultMutableTreeNode(new AssistantItem("test1","TestOne",url3)));
        sections[0].add(new javax.swing.tree.DefaultMutableTreeNode(new AssistantItem("test2","TestTwo",url1)));
        sections[1].add(new javax.swing.tree.DefaultMutableTreeNode(new AssistantItem("test3","TestThree",url2)));
        AssistantItem item = new AssistantItem("desc", "text of description", descURL,AssistantItem.DESCRIPTION);
        sections[2].add(new javax.swing.tree.DefaultMutableTreeNode(item));
        AssistantID id = new AssistantID("default_id");
        id.addSection(sections);        
        
        AssistantContext ctx = new AssistantContext(id);
        model = new DefaultAssistantModel(ctx);
        model.setCurrentID(id);
        
        sections = new AssistantSection[2];
        sections[0] = new AssistantSection("IDE Navigator");
        sections[1] = new AssistantSection("Dynamic Help");
        sections[0].add(new javax.swing.tree.DefaultMutableTreeNode(new AssistantItem("test1","TopComponent Javadoc",url3)));
        sections[0].add(new javax.swing.tree.DefaultMutableTreeNode(new AssistantItem("test2","new TopComponent...",url1)));
        sections[1].add(new javax.swing.tree.DefaultMutableTreeNode(new AssistantItem("test3","What is TopComponent",url2)));
        id = new AssistantID("org.openide.windows.TopComponent");
        id.addSection(sections);
        ctx.addID(id);
        
        sections = new AssistantSection[2];
        sections[0] = new AssistantSection("IDE Navigator");
        sections[1] = new AssistantSection("Dynamic Help");
        sections[0].add(new javax.swing.tree.DefaultMutableTreeNode(new AssistantItem("test1","Explorer Javadoc",url3)));
        sections[0].add(new javax.swing.tree.DefaultMutableTreeNode(new AssistantItem("test2","new Explorer...",url1)));
        sections[1].add(new javax.swing.tree.DefaultMutableTreeNode(new AssistantItem("test3","What is Explorer",url2)));
        id = new AssistantID("org.openide.explorer.ExplorerPanel");
        id.addSection(sections);
        ctx.addID(id);
        
        sections = new AssistantSection[2];
        sections[0] = new AssistantSection("IDE Navigator");
        sections[1] = new AssistantSection("Dynamic Help");
        sections[0].add(new javax.swing.tree.DefaultMutableTreeNode(new AssistantItem("test1","Editor Javadoc",url3)));
        sections[0].add(new javax.swing.tree.DefaultMutableTreeNode(new AssistantItem("test2","new EditorSupport...",url1)));
        sections[1].add(new javax.swing.tree.DefaultMutableTreeNode(new AssistantItem("test3","What is Editor for?",url2)));
        id = new AssistantID("editing.editorwindow");
        id.addSection(sections);
        ctx.addID(id);

        sections = new AssistantSection[2];
        sections[0] = new AssistantSection("IDE Navigator");
        sections[1] = new AssistantSection("Dynamic Help");
        sections[0].add(new javax.swing.tree.DefaultMutableTreeNode(new AssistantItem("test1","Toolbox API",url3)));
        sections[0].add(new javax.swing.tree.DefaultMutableTreeNode(new AssistantItem("test2","new ToolboxSupport...",url1)));
        sections[1].add(new javax.swing.tree.DefaultMutableTreeNode(new AssistantItem("test3","What is Toolbox for?",url2)));
        id = new AssistantID("toolbox");
        id.addSection(sections);
        ctx.addID(id);
        return model;
    }
    private boolean debug = false;
    private void debug(String msg){
        if(debug)
            System.err.println("AssistantContentViewer: "+msg);
    }
}
