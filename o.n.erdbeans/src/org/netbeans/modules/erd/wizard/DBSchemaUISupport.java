/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.

If you wish your version of this file to be governed by only the CDDL
or only the GPL Version 2, indicate your decision by adding
"[Contributor] elects to include this software in this distribution
under the [CDDL or GPL Version 2] license." If you do not indicate a
single choice of license, a recipient has the option to distribute
your version of this file under either the CDDL, the GPL Version 2 or
to extend the choice of license to its licensees as provided above.
However, if you add GPL Version 2 code and therefore, elected the GPL
Version 2 license, then the option applies only if the new code is
made subject to such option by the copyright holder.
 */

package org.netbeans.modules.erd.wizard;

import java.awt.Component;
import java.io.File;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import javax.swing.AbstractListModel;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JComboBox;
import javax.swing.JList;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.SourceGroup;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.NbBundle;

/**
 *
 * @author Andrei Badea
 */
public class DBSchemaUISupport {

    private DBSchemaUISupport() {
    }

    /**
     * Connects a combo box with the list of dbschemas in a project, making
     * the combo box display these dbschemas.
     */
    public static void connect(JComboBox comboBox, Project project, FileObject configFilesFolder) {
        comboBox.setModel(new DBSchemaModel(project, configFilesFolder));
        comboBox.setRenderer(new DBSchemaRenderer(comboBox));
    }

    private static final class DBSchemaModel extends AbstractListModel implements ComboBoxModel {

        private Map<FileObject,String> schemaDisplayNames = new HashMap<FileObject,String>();
        private FileObject[] schemas;

        private Object selectedItem;

        public DBSchemaModel(Project project, FileObject configFilesFolder) {
            SourceGroup[] sourceGroups = SourceGroupSupport.getJavaSourceGroups(project);

            // this recursive search is a potential performance problem
            for (int i = 0; i < sourceGroups.length; i++) {
                searchRoot(sourceGroups[i].getRootFolder(), sourceGroups[i].getDisplayName());
            }

            if (configFilesFolder != null) {
                String configFilesDisplayName = "sdsd";//NbBundle.getMessage(DBSchemaUISupport.class, "LBL_Node_DocBase");
                searchRoot(configFilesFolder, configFilesDisplayName);
            }

            schemas = (FileObject[])schemaDisplayNames.keySet().toArray(new FileObject[schemaDisplayNames.size()]);
           // Arrays.sort(schemas, new DBSchemaComparator());
        }

        private void searchRoot(FileObject root, String rootDisplayName) {
            Enumeration ch = root.getChildren(true);
            while (ch.hasMoreElements()) {
                FileObject f = (FileObject) ch.nextElement();
                if (f.getExt().equals(DBSchemaManager.DBSCHEMA_EXT) && !f.isFolder()) {
                    if (!schemaDisplayNames.containsKey(f)) {
                        String relativeParent = FileUtil.getRelativePath(root, f.getParent()) + File.separator;
                        if (relativeParent.startsWith("/")) { // NOI18N
                            relativeParent = relativeParent.substring(1);
                        }
                        String relative = relativeParent + f.getName();
                        String displayName =rootDisplayName+"  "+relative; //NbBundle.getMessage(DBSchemaUISupport.class,
                                //"LBL_SchemaLocation", rootDisplayName, relative);
                        schemaDisplayNames.put(f, displayName);
                    }
                }
            }
        }

        public void setSelectedItem(Object anItem) {
            selectedItem = anItem;
        }

        public Object getElementAt(int index) {
            return schemas[index];
        }

        public int getSize() {
            return schemas.length;
        }

        public Object getSelectedItem() {
            return selectedItem;
        }

        private class DBSchemaComparator implements Comparator {

            public boolean equals(Object that) {
                return that instanceof DBSchemaComparator;
            }

            public int compare(Object o1, Object o2) {
                FileObject f1 = (FileObject)o1;
                FileObject f2 = (FileObject)o2;

                String displayName1 = schemaDisplayNames.get(f1);
                String displayName2 = schemaDisplayNames.get(f2);

                return displayName1.compareTo(displayName2);
            }
        }
    }

    private static final class DBSchemaRenderer extends DefaultListCellRenderer {

        private JComboBox comboBox;

        public DBSchemaRenderer(JComboBox comboBox) {
            this.comboBox = comboBox;
        }

        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            Object displayName = null;
            ComboBoxModel model = comboBox.getModel();

            if (model instanceof DBSchemaModel && value instanceof FileObject) {
                displayName = ((DBSchemaModel)model).schemaDisplayNames.get((FileObject)value);
            } else {
                displayName = value;
            }

            return super.getListCellRendererComponent(list, displayName, index, isSelected, cellHasFocus);
        }
    }
}
