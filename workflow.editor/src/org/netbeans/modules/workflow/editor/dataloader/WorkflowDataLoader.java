package org.netbeans.modules.workflow.editor.dataloader;

import java.io.IOException;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObjectExistsException;
import org.openide.loaders.MultiDataObject;
import org.openide.loaders.UniFileLoader;
import org.openide.util.NbBundle;

public class WorkflowDataLoader extends UniFileLoader {

    public static final String REQUIRED_MIME = "text/x-workflow";
    private static final long serialVersionUID = 1L;

    public WorkflowDataLoader() {
        super("org.netbeans.modules.workflow.editor.dataloader.WorkflowDataObject");
    }

    protected @Override
    String defaultDisplayName() {
        return NbBundle.getMessage(WorkflowDataLoader.class, "LBL_Workflow_loader_name");
    }

    protected @Override
    void initialize() {
        super.initialize();
        getExtensions().addMimeType(REQUIRED_MIME);
    }

    protected MultiDataObject createMultiObject(FileObject primaryFile) throws DataObjectExistsException, IOException {
        return new WorkflowDataObject(primaryFile, this);
    }

    protected @Override
    String actionsContext() {
        return "Loaders/" + REQUIRED_MIME + "/Actions";
    }
}
