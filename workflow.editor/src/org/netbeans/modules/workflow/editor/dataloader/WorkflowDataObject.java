package org.netbeans.modules.workflow.editor.dataloader;

import java.io.IOException;
import org.netbeans.modules.workflow.editor.multiview.WorkflowEditorSupport;
import org.netbeans.modules.workflow.editor.multiview.WorkflowMultiviewSupport;
import org.netbeans.spi.xml.cookies.CheckXMLSupport;
import org.netbeans.spi.xml.cookies.DataObjectAdapters;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObjectExistsException;
import org.openide.loaders.MultiDataObject;
import org.openide.nodes.CookieSet;
import org.openide.nodes.Node;
import org.openide.util.Lookup;
import org.openide.text.DataEditorSupport;
import org.xml.sax.InputSource;

public class WorkflowDataObject extends MultiDataObject {

    public WorkflowDataObject(FileObject pf, WorkflowDataLoader loader) throws DataObjectExistsException, IOException {
        super(pf, loader);
        CookieSet cookies = getCookieSet();
        //cookies.add((Node.Cookie) DataEditorSupport.create(this, getPrimaryEntry(), cookies));
        
        editorSupport = new WorkflowEditorSupport(this);
        // editor support defines MIME type understood by EditorKits registry
        cookies.add(editorSupport);
        
//      Add check and validate cookies
        InputSource is = DataObjectAdapters.inputSource(this);
        cookies.add(new CheckXMLSupport(is));
        cookies.add(new WorkflowMultiviewSupport(this));
        /*//add validate action here
        set.add(new IEPValidateXMLCookie(this));
        
        set.add(new PlanReportCookie(this));
        
        SaveCookie saveCookie = set.getCookie(SaveCookie.class);
         */
        
    }

    protected @Override
    Node createNodeDelegate() {
        return new WorkflowDataNode(this, getLookup());
    }

    public @Override
    Lookup getLookup() {
        return getCookieSet().getLookup();
    }
    
    public WorkflowEditorSupport getWorkflowEditorSupport() {
        return editorSupport;
    }
    
    private transient WorkflowEditorSupport editorSupport;

    public static final String WORKFLOW_ICON_BASE_WITH_EXT = "org/netbeans/modules/workflow/editor/resources/images/workflowx16.png";
 
}
