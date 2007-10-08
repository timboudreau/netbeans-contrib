/*
 * EditSetConsistencyRule.java
 * 
 * Created on Oct 4, 2007, 10:56:41 PM
 * 
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.ucla.netbeans.module.bpel.setconsistency.actions;

import java.util.Iterator;
import java.util.List;
import org.netbeans.modules.bpel.model.api.BpelModel;
import org.netbeans.modules.bpel.model.api.XmlComment;
import org.netbeans.modules.bpel.model.spi.BpelModelFactory;
import org.netbeans.modules.xml.retriever.catalog.Utilities;
import org.netbeans.modules.xml.xam.ModelSource;
import org.netbeans.modules.xml.xam.dom.AbstractDocumentComponent;
import org.openide.loaders.DataObject;
import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.actions.CookieAction;
import org.openide.windows.IOProvider;
import org.openide.windows.OutputWriter;
import org.ucla.netbeans.module.bpel.setconsistency.Util;

/**
 *
 * @author radval
 */
public class EditSetConsistencyRuleAction extends CookieAction {

    @Override
    protected int mode() {
        return CookieAction.MODE_ONE;
    }

    @Override
    protected Class<?>[] cookieClasses() {
        return new Class[] {DataObject.class};
    }

    @Override
    protected void performAction(Node[] activatedNodes) {
        OutputWriter writer =  IOProvider.getDefault().getStdOut();
        writer.println("In EditSetConsistencyRuleAction");
        if(activatedNodes.length > 0) {
            DataObject bpelDataObject = activatedNodes[0].getCookie(DataObject.class);
            
            if(bpelDataObject != null) {
                writer.println("In bpel file "+ bpelDataObject.getName());
                
                BpelModel model = Util.getModel(bpelDataObject);
                
                if(model != null) {
                    writer.println("Got BPEL Model");
                    
                    org.netbeans.modules.bpel.model.api.Process process = model.getProcess();
                    if(process != null) {
                        writer.println("Got BPEL process" + process.getName());
                       
                        //print existing comments
                        String message1 = "Printing Existing Comments";
                        printComments(process, model, writer, message1);
                        //add new comment
                        String message2 = "Adding a new set consistency rule in a new comment";
                        writer.println(message2);
                        addNewComment(process);
                        //print comments again
                        String message3 = "Printing All Comments Again";
                        printComments(process, model, writer, message3);
                        
                    }
                }
            }
        }
    }

    
    @Override
    public String getName() {
        return NbBundle.getMessage(EditSetConsistencyRuleAction.class, "EditSetConsistencyRuleAction_DisplayName");
    }

    @Override
    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
    }

    

    private void addComment() {

    }

    private void printComments(org.netbeans.modules.bpel.model.api.Process process,
                                          BpelModel model,
                                          OutputWriter writer,
                                          String message) {
      
       List<XmlComment> comments = process.getXmlComments();
       Iterator<XmlComment> it = comments.iterator();
       int i = 0;
       model.startTransaction();
       writer.println(message);
       while(it.hasNext()) {
           XmlComment comment = it.next();
           String commentStr = comment.getCommentText();
           writer.println("comment: " + i + " : " + comment.getCommentText());
           i++;
       }

       model.endTransaction();
    }

    private XmlComment getMatchingComment(org.netbeans.modules.bpel.model.api.Process process,
                                          BpelModel model) {
        XmlComment matchingComment = null;                                  

        List<XmlComment> comments = process.getXmlComments();
       Iterator<XmlComment> it = comments.iterator();
       while(it.hasNext()) {
           XmlComment comment = it.next();
           String commentStr = comment.getCommentText();
           if(commentStr.startsWith("<SetConsistencyRules")) {
                matchingComment = comment;
                break;
           }
       }
        
       return matchingComment;
    }

    private XmlComment addNewComment(org.netbeans.modules.bpel.model.api.Process process) {
        XmlComment newComment = null;                                  
        try {
            newComment =process.addXmlComment("<SetConsistencyRules><SetConsistencyRule name=\"firstRule\" /> </SetConsistencyRules>");
        } catch(Exception ex) {
            ex.printStackTrace();;
        }
       
        return newComment;
    }
}
