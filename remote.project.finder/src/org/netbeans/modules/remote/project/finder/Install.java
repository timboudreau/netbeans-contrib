/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.netbeans.modules.remote.project.finder;

import org.openide.modules.ModuleInstall;

/**
 *
 * @author Tomas Zezula
 */
public class Install extends ModuleInstall {    

    @Override
    public void restored() {
        super.restored();
        WorkSpaceUpdater.getDefault().start();
    }

    @Override
    public void close() {
        super.close();
        WorkSpaceUpdater.getDefault().stop();
    }
}
