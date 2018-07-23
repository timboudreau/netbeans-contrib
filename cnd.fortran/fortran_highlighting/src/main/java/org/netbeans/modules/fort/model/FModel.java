
package org.netbeans.modules.fort.model;

import java.util.List;
import org.netbeans.modules.fort.model.lang.FLabel;
import org.netbeans.modules.fort.model.lang.FProcedure;
import org.netbeans.modules.fort.model.lang.FVariable;

/**
 * interface for fortran model
 *@author Andrey Gubichev
 */
public interface FModel {
    /**
     * @return labels in fortran file
     */
    List<FLabel> getLabels();
    /**
     * @return procedures in fortran file
     */
    List<FProcedure> getProcedures();
    /**
     * @return variables in fortran file
     */
    List<FVariable> getVariables();
}
