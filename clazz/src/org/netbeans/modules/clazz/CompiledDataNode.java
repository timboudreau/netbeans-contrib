/*
 * CompiledDataNode.java
 *
 * Created on August 10, 2000, 4:30 PM
 */

package org.netbeans.modules.clazz;

import java.lang.reflect.InvocationTargetException;
import java.io.*;
import java.util.*;

import org.openide.loaders.ExecSupport;
import org.openide.nodes.Sheet;
import org.openide.nodes.PropertySupport;
import org.openide.ErrorManager;
import org.openide.TopManager;
import org.openide.util.NbBundle;
import org.openide.filesystems.FileObject;

/**
 * This class overrides a few methods from ClassDataNode presenting the contents
 * as a sourceless class. It also adds .class - specific behaviour such as
 * execution and parameters property.
 *
 * @author  sdedic
 * @version 
 */
public class CompiledDataNode extends ClassDataNode {
    private final static String PROP_IS_EXECUTABLE = "isExecutable"; // NOI18N
    private final static String PROP_FILE_PARAMS = "fileParams"; // NOI18N
    private final static String PROP_EXECUTION = "execution"; // NOI18N
    private final static String EXECUTION_SET_NAME     = "Execution"; // NOI18N
    
    /** Icon bases for icon manager */
    protected final static String CLASS_BASE =
        "/org/netbeans/modules/clazz/resources/class"; // NOI18N
    private final static String CLASS_MAIN_BASE =
        "/org/netbeans/modules/clazz/resources/classMain"; // NOI18N
    private final static String ERROR_BASE =
        "/org/netbeans/modules/clazz/resources/classError"; // NOI18N
    private final static String BEAN_BASE =
        "/org/netbeans/modules/clazz/resources/bean"; // NOI18N
    private final static String BEAN_MAIN_BASE =
        "/org/netbeans/modules/clazz/resources/beanMain"; // NOI18N


    /** Creates new CompiledDataNode */
    public CompiledDataNode(final CompiledDataObject obj) {
        super(obj);
    }
    
    private CompiledDataObject getCompiledDataObject() {
	return (CompiledDataObject)getDataObject();
    }

    boolean isExecutable() {
        return getCompiledDataObject().isExecutable();
    }

    protected Sheet createSheet () {
        Sheet s = super.createSheet();
        ResourceBundle bundle = NbBundle.getBundle(ClassDataNode.class);
        Sheet.Set ps = s.get(Sheet.PROPERTIES);

        ps.put(new PropertySupport.ReadOnly (
                   PROP_IS_EXECUTABLE,
                   Boolean.TYPE,
                   bundle.getString ("PROP_isExecutable"),
                   bundle.getString ("HINT_isExecutable")
               ) {
                   public Object getValue () throws InvocationTargetException {
                       return new Boolean(getCompiledDataObject().isExecutable());
                   }
               });
        ExecSupport es = (ExecSupport)getCookie(ExecSupport.class);
        if (es != null) {
            Sheet.Set exps = new Sheet.Set();
            exps.setName(EXECUTION_SET_NAME);
            exps.setDisplayName(bundle.getString ("PROP_executionSetName"));
            exps.setShortDescription(bundle.getString ("HINT_executionSetName"));
            es.addProperties (exps);
            s.put(exps);
        }
        return s;
    }

    protected String initialIconBase() {
        return CLASS_BASE;
    }

    /** Find right icon for this node. */
    protected void resolveIcons () {
        CompiledDataObject dataObj = getCompiledDataObject();
//        try {
            FileObject fo=dataObj.getPrimaryFile();

            if (!(dataObj.getClassName().equals(fo.getPackageName('.')))) {
                setIconBase(ERROR_BASE);
            } else if (dataObj.isJavaBean ()) {
                if (dataObj.isExecutable ())
                    setIconBase(BEAN_MAIN_BASE);
                else
                    setIconBase(BEAN_BASE);
            } else if (dataObj.isExecutable ())
                setIconBase(CLASS_MAIN_BASE);
            else
                setIconBase(CLASS_BASE);
/*        } catch (IOException ex) {
            // log exception only and set error tooltip
            TopManager.getDefault().getErrorManager().notify(
                ErrorManager.INFORMATIONAL, ex
            );
            setIconBase(ERROR_BASE);
            setErrorToolTip(ex);
        } catch (ClassNotFoundException ex) {
            // log exception only and set error tooltip
            TopManager.getDefault().getErrorManager().notify(
                ErrorManager.INFORMATIONAL, ex
            );
            setIconBase(ERROR_BASE);
            setErrorToolTip(ex);
        }
*/        iconResolved = true;
    }

}
