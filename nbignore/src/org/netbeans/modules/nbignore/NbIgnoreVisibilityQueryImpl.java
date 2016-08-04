/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2016 Oracle and/or its affiliates. All rights reserved.
 *
 * Oracle and Java are registered trademarks of Oracle and/or its affiliates.
 * Other names may be trademarks of their respective owners.
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
 * nbbuild/licenses/CDDL-GPL-2-CP.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the GPL Version 2 section of the License file that
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
 * Contributor(s):
 */
package org.netbeans.modules.nbignore;

import java.io.File;
import javax.swing.event.ChangeListener;
import org.netbeans.spi.queries.VisibilityQueryImplementation;
import org.netbeans.spi.queries.VisibilityQueryImplementation2;
import org.openide.filesystems.FileChangeAdapter;
import org.openide.filesystems.FileEvent;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.ChangeSupport;
import org.openide.util.lookup.ServiceProvider;
import org.openide.util.lookup.ServiceProviders;

/**
 * Ignore (hide) folders that contain .nbignore file.
 */
@ServiceProviders({
    @ServiceProvider(service = VisibilityQueryImplementation2.class, position = 8765),
    @ServiceProvider(service = VisibilityQueryImplementation.class, position = 8765)
})
public class NbIgnoreVisibilityQueryImpl implements VisibilityQueryImplementation2 {

    private static final String FILE_NAME = ".nbignore"; // NOI18N

    private final ChangeSupport changeSupport = new ChangeSupport(this);


    public NbIgnoreVisibilityQueryImpl() {
        FileUtil.addFileChangeListener(new FsListener(changeSupport));
    }

    @Override
    public boolean isVisible(File file) {
        return !(new File(file, FILE_NAME).exists());
    }

    @Override
    public boolean isVisible(FileObject file) {
        if (file.isFolder()) {
            return file.getFileObject(FILE_NAME) == null;
        }
        return true;
    }

    @Override
    public void addChangeListener(ChangeListener listener) {
        changeSupport.addChangeListener(listener);
    }

    @Override
    public void removeChangeListener(ChangeListener listener) {
        changeSupport.removeChangeListener(listener);
    }

    //~ Inner classes

    private static final class FsListener extends FileChangeAdapter {

        private final ChangeSupport changeSupport;


        FsListener(ChangeSupport changeSupport) {
            assert changeSupport != null;
            this.changeSupport = changeSupport;
        }

        @Override
        public void fileDataCreated(FileEvent fe) {
            checkNewOrDeletedFile(fe);
        }

        @Override
        public void fileDeleted(FileEvent fe) {
            checkNewOrDeletedFile(fe);
        }

        private void checkNewOrDeletedFile(FileEvent fe) {
            if (fe.getFile().getNameExt().equals(FILE_NAME)) {
                changeSupport.fireChange();
            }
        }

    }

}
