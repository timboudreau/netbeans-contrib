/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.netbeans.modules.remote.server;

import java.io.IOException;
import org.openide.modules.ModuleInstall;
import org.openide.util.Exceptions;

/**
 *
 * @author Tomas Zezula
 */
public class Install extends ModuleInstall {

    @Override
    public void restored() {
        super.restored();
        try {
            JavaServices.getInstance().start();
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    @Override
    public void close() {
        super.close();
        try {
            JavaServices.getInstance().stop();
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
    }
}
