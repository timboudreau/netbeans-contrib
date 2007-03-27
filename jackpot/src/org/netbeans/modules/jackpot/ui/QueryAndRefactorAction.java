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

package org.netbeans.modules.jackpot.ui;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.text.ChoiceFormat;
import java.text.MessageFormat;
import java.util.HashSet;
import java.util.ResourceBundle;
import java.util.Set;
import org.netbeans.api.project.FileOwnerQuery;
import org.netbeans.modules.jackpot.Inspection;
import org.netbeans.modules.jackpot.JackpotModule;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectInformation;
import org.netbeans.api.project.ProjectUtils;
import org.netbeans.modules.jackpot.engine.BuildErrorsException;
import org.netbeans.spi.jackpot.EmptyScriptException;
import org.netbeans.spi.jackpot.ScriptParsingException;
import org.openide.ErrorManager;
import org.openide.awt.StatusDisplayer;
import org.openide.loaders.DataObject;
import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.NodeAction;
import org.openide.windows.InputOutput;
import org.openide.windows.OutputWriter;

public class QueryAndRefactorAction extends NodeAction {
    Project[] projects;
    Inspection[] inspections;
    String querySetName;
    private Thread t;    
    
    public void performAction(Node[] nodes) {
        projects = getProjects(nodes);
        QueryAndRefactorPanel panel = new QueryAndRefactorPanel();
        if (panel.showDialog(makeTitle())) {
            inspections = panel.getSelectedInspections();
            querySetName = panel.getQuerySetName();
            t = new Thread(new CommandRunner());
            t.setPriority(Thread.NORM_PRIORITY);
            t.start();
        }
    }
    
    private String makeTitle() {
        StringBuffer sb = new StringBuffer(getString("LBL_Inspect_Title"));
        sb.append(" - ");
        boolean first = true;
        for (Project p : projects) {
            if (first)
                first = false;
            else
                sb.append(", ");
            ProjectInformation info = ProjectUtils.getInformation(p);
            sb.append(info.getDisplayName());
        }
        return sb.toString();
    }
    
    public String getName() {
        return NbBundle.getMessage(QueryAndRefactorAction.class, "CTL_QueryAndRefactorAction");
    }
    
    protected String getString(String key) {
        return NbBundle.getMessage(QueryAndRefactorAction.class, key);
    }

    protected String iconResource() {
        return "org/netbeans/modules/jackpot/resources/QueryRefactor.png"; //NOI18N
    }
    
    protected void initialize() {
        super.initialize();
        // see org.openide.util.actions.SystemAction.iconResource() javadoc for more details
        putValue("noIconInMenu", Boolean.TRUE);
    }
    
    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
    }
    
    protected boolean asynchronous() {
        return false;
    }

    protected boolean enable (Node[] nodes) {
        return getProjects(nodes).length > 0;
    }
    
    /**
     * Returns the list of projects to be queried.
     */
    public Project[] getProjects(Node[] nodes) {
        Set<Project> projects = new HashSet<Project>();
        for (Node node : nodes) {
            Project p = (Project)node.getLookup().lookup(Project.class);
            if (p == null) {
                projects.clear();  // non-project node in list, abort
                break;
            }
	    projects.add(p);
        }
        if (nodes.length > 0 && projects.size() == 0) // check for project files
            for (Node node : nodes) {
            DataObject dobj = (DataObject) node.getCookie(DataObject.class);
            if (dobj!=null) {
                Project p = FileOwnerQuery.getOwner(dobj.getPrimaryFile());
                if (p == null) {
                    projects.clear();  // non-project file node in list, abort
                    break;
                }
                projects.add(p);
            }            
        }
        return projects.toArray(new Project[0]);
    }
   
    class CommandRunner implements Runnable {
        public void run() {
            StatusDisplayer.getDefault().setStatusText("");
            try {
                JackpotModule module = JackpotModule.getInstance();
                module.createEngine(projects);
                if (inspections.length == 1)
                    module.runCommand(inspections[0]);
                else
                    module.runCommands(querySetName, inspections);
            } catch (EmptyScriptException e) {
                String msg = NbBundle.getMessage(QueryAndRefactorAction.class, "MSG_NoRules");
                StatusDisplayer.getDefault().setStatusText(msg);
            } catch (ScriptParsingException rpe) {
                printParseException(rpe.getMessage());
            } catch (BuildErrorsException bee) {
                String msg = getBuildErrorsMsg(bee);
                StatusDisplayer.getDefault().setStatusText(msg);
            } catch (ThreadDeath td) {
                return;
            } catch (Exception ex) {
                ErrorManager.getDefault().notify(ErrorManager.EXCEPTION, ex);
            }
        }
    }
    
    private void printParseException(String errorText) {
        try {
            BufferedReader in = new BufferedReader(new StringReader(errorText));
            InputOutput log = JackpotModule.getLogWindow();
            log.select();
            OutputWriter out = log.getOut();
            String line;
            while ((line = in.readLine()) != null) {
                Hyperlink link = Hyperlink.parse(line);
                if (link != null)
                    out.println(line, link, true);
                else
                    out.println(line);
            }
            out.close();
        } catch (IOException e) {
            // should never happen with a StringReader source.
            throw new AssertionError(); 
        }
    }
    
    private String getBuildErrorsMsg(BuildErrorsException e) {
        ResourceBundle bundle = NbBundle.getBundle(QueryAndRefactorAction.class);
        try {
            int errors = Integer.parseInt(e.getMessage());
            ChoiceFormat cf = new ChoiceFormat(bundle.getString("FMT_BuildErrors"));
            String errStr = cf.format(errors);
            return MessageFormat.format(bundle.getString("MSG_BuildErrors"), errors, errStr);
        } catch (NumberFormatException nfe) {
            String errStr = e.getLocalizedMessage();
            return MessageFormat.format(bundle.getString("MSG_BuildErrorLog"), errStr);
        }
    }
}
