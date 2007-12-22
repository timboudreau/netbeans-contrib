/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * 
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
 * 
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common
 * Development and Distribution License("CDDL") (collectively, the
 * "License"). You may not use this file except in compliance with the
 * License. You can obtain a copy of the License at
 * http://www.netbeans.org/cddl-gplv2.html
 * or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
 * specific language governing permissions and limitations under the
 * License.  When distributing the software, include this License Header
 * Notice in each file and include the License file at
 * nbbuild/licenses/CDDL-GPL-2-CP.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 * 
 * If you wish your version of this file to be governed by only the CDDL
 * or only the GPL Version 2, indicate your decision by adding
 * "[Contributor] elects to include this software in this distribution
 * under the [CDDL or GPL Version 2] license." If you do not indicate a
 * single choice of license, a recipient has the option to distribute
 * your version of this file under either the CDDL, the GPL Version 2 or
 * to extend the choice of license to its licensees as provided above.
 * However, if you add GPL Version 2 code and therefore, elected the GPL
 * Version 2 license, then the option applies only if the new code is
 * made subject to such option by the copyright holder.
 * 
 * Contributor(s): Tom Wheeler, Tim Boudreau
 * 
 * Portions Copyrighted 2007 Sun Microsystems, Inc.
 */
package org.netbeans.examples.careditor.pojos;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import org.openide.filesystems.FileLock;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataFolder;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.CookieAction;

// FIXME -- put this into this package by accident
public final class CreateCarAction extends CookieAction {

    protected void performAction(Node[] activatedNodes) {
        try {
            DataFolder dataFolder = activatedNodes[0].getLookup().lookup(DataFolder.class);

            FileObject folderFo = dataFolder.getPrimaryFile();
            String name = FileUtil.findFreeFileName(folderFo, "samplecar", "car");

            // TODO do in an atomic filesystem action??
            // TODO is there a better way of adding binary files via New->File wizard??
            // FIXME duplicative of what is in save cookie impl
            FileObject carFo = folderFo.createData(name, "car");
            
            Car car = new Car();
            car.setMake("Ford");
            car.setModel("Taurus");
            car.setYear(2004);
            
            Person jimmy = new Person();
            jimmy.setAge(21);
            jimmy.setFirstName("Jimmy");
            jimmy.setLastName("Smith");
            car.addPassenger(jimmy);
            
            Person sally = new Person();
            sally.setAge(19);
            sally.setFirstName("Sally");
            sally.setLastName("Jones");
            car.addPassenger(sally);

            FileLock lock = carFo.lock();
            if (!lock.isValid()) { 
                throw new IOException("Invalid lock"); 
            }

            try {
                OutputStream os = carFo.getOutputStream(lock);
                ObjectOutputStream oos = new ObjectOutputStream(os);
                oos.writeObject(car);
                oos.close();
            } finally {
                lock.releaseLock();
            }
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    @Override
    protected int mode() {
        return CookieAction.MODE_EXACTLY_ONE;
    }

    @Override
    public String getName() {
        return NbBundle.getMessage(CreateCarAction.class, "CTL_CreateCarAction");
    }

    @Override
    protected Class[] cookieClasses() {
        return new Class[]{DataFolder.class};
    }

    @Override
    protected void initialize() {
        super.initialize();
        // see org.openide.util.actions.SystemAction.iconResource() javadoc for more details
        putValue("noIconInMenu", Boolean.TRUE);
    }

    @Override
    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
    }

    @Override
    protected boolean asynchronous() {
        return false;
    }
}