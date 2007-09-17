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
 * Portions Copyrighted 2007 Sun Microsystems, Inc.
 */
package org.netbeans.modules.perspective;

import java.util.List;
import org.netbeans.modules.perspective.utils.PerspectiveManagerImpl;
import org.netbeans.modules.perspective.views.Perspective;
import org.openide.util.Lookup;

/**
 *
 * @author Anuradha G
 */
public abstract class PerspectiveManager {

    public static PerspectiveManager getDefault() {
        PerspectiveManager lookup = Lookup.getDefault().lookup(PerspectiveManager.class);
        if (lookup == null) {
            lookup = PerspectiveManagerImpl.getInstance();
        }
        return lookup;
    }

    public abstract void registerPerspective(int index, Perspective perspective);
    public abstract void deregisterPerspective(Perspective perspective);
    public abstract Perspective getSelected();
    public abstract Perspective findPerspectiveByID(String id) ;
    public abstract Perspective findPerspectiveByAlias(String alias);
    public abstract List<Perspective> getPerspectives() ;
    public abstract void setSelected(Perspective perspective);
}