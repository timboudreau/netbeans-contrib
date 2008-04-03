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
 * Contributor(s):
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun
 * Microsystems, Inc. All Rights Reserved.
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
 */

package org.netbeans.modules.javafx.platform;

import java.awt.Component;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Logger;
import javax.swing.AbstractListModel;
import javax.swing.ComboBoxModel;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import org.netbeans.api.java.platform.JavaPlatform;
import org.netbeans.api.java.platform.JavaPlatformManager;
import org.netbeans.api.java.platform.Specification;
import org.netbeans.modules.javafx.platform.platformdefinition.DefaultPlatformImpl;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.awt.HtmlRenderer;
import org.openide.modules.SpecificationVersion;
import org.openide.util.NbBundle;
import org.openide.util.Parameters;
import org.openide.util.WeakListeners;

/**
 * Support class for {@link JavaPlatform} manipulation in project customizer.
 * @author Tomas Zezula, Tomas Mysik
 */
public final class PlatformUiSupport {

    private static final SpecificationVersion JDK_1_5 = new SpecificationVersion("1.5"); //NOI18N   
    private static final Logger LOGGER = Logger.getLogger(PlatformUiSupport.class.getName());

    private PlatformUiSupport() {
    }

    /**
     * Create a {@link ComboBoxModel} of Java platforms.
     * The model listens on the {@link JavaPlatformManager} and update its
     * state according to the changes.
     * @param activePlatform the active project's platform, can be <code>null</code>.
     * @return {@link ComboBoxModel}.
     */
    public static ComboBoxModel createPlatformComboBoxModel(String activePlatform, String platformType) {
        return new PlatformComboBoxModel(activePlatform, platformType);
    }


    /**
     * Create a {@link ListCellRenderer} for rendering items of the {@link ComboBoxModel}
     * created by the {@link PlatformUiSupport#createPlatformComboBoxModel(String)} method.
     * @return {@link ListCellRenderer}.
     */
    public static ListCellRenderer createPlatformListCellRenderer() {
        return new PlatformListCellRenderer();
    }

    /**
     * Return a {@link JavaPlatform} for an item obtained from the ComboBoxModel created by
     * the {@link PlatformUiSupport#createPlatformComboBoxModel(String)} method. This method
     * can return <code>null</code> if the platform is broken.
     * @param platformKey an item obtained from {@link ComboBoxModel} created by
     *                    {@link PlatformUiSupport#createPlatformComboBoxModel(String)}.
     * @return {@link JavaPlatform} or <code>null</code> in case when platform is broken.
     * @throws IllegalArgumentException if the input parameter is not an object created by platform combobox model.
     */
    public static JavaPlatform getPlatform(Object platformKey) {
        Parameters.notNull("platformKey", platformKey); //NOI18N

        if (platformKey instanceof PlatformKey) {
            return getPlatform((PlatformKey) platformKey);
        }
        throw new IllegalArgumentException();
    }

    /**
     * Create {@link ComboBoxModel} of source levels for active platform.
     * The model listens on the platform's {@link ComboBoxModel} and update its
     * state according to the changes. It is possible to define minimal JDK version.
     * @param platformComboBoxModel the platform's model used for listenning.
     * @param initialSourceLevel initial source level value.
     * @param initialTargetLevel initial target level value.
     * @param minimalSpecificationVersion minimal JDK version to be displayed. It can be <code>null</code> if all the JDK versions
     *                          should be displayed (typically for Java SE project).
     * @return {@link ComboBoxModel} of {@link SourceLevelKey}.
     * @see #createSourceLevelComboBoxModel(ComboBoxModel, String, String)
     */
    public static ComboBoxModel createSourceLevelComboBoxModel(ComboBoxModel platformComboBoxModel,
            String initialSourceLevel, String initialTargetLevel, SpecificationVersion minimalSpecificationVersion) {
        Parameters.notNull("platformComboBoxModel", platformComboBoxModel); // NOI18N
        Parameters.notNull("initialSourceLevel", initialSourceLevel); // NOI18N
        Parameters.notNull("initialTargetLevel", initialTargetLevel); // NOI18N

        return new SourceLevelComboBoxModel(platformComboBoxModel, initialSourceLevel, initialTargetLevel,
                minimalSpecificationVersion);
    }

    /**
     * Exactly like {@link #createSourceLevelComboBoxModel(ComboBoxModel, String, String, JDK)}
     * but without any minimal JDK version.
     * @param platformComboBoxModel the platform's model used for listenning.
     * @param initialSourceLevel initial source level value.
     * @param initialTargetLevel initial target level value.
     * @return {@link ComboBoxModel} of {@link SourceLevelKey}.
     * @see #createSourceLevelComboBoxModel(ComboBoxModel, String, String, JDK)
     */
    public static ComboBoxModel createSourceLevelComboBoxModel(ComboBoxModel platformComboBoxModel,
            String initialSourceLevel, String initialTargetLevel) {
        Parameters.notNull("platformComboBoxModel", platformComboBoxModel); // NOI18N
        Parameters.notNull("initialSourceLevel", initialSourceLevel); // NOI18N
        Parameters.notNull("initialTargetLevel", initialTargetLevel); // NOI18N

        return new SourceLevelComboBoxModel(platformComboBoxModel, initialSourceLevel, initialTargetLevel, null);
    }

    /**
     * Create {@link ListCellRenderer} for source levels. This method could be used when highlighting
     * of illegal source levels is needed.
     * @return {@link ListCellRenderer} for source levels.
     */
    public static ListCellRenderer createSourceLevelListCellRenderer() {
        return new SourceLevelListCellRenderer();
    }

    /**
     * This class represents a JavaPlatform in the {@link ListModel}
     * created by the {@link PlatformUiSupport#createPlatformComboBoxModel(String)} method.
     */
    public static final class PlatformKey implements Comparable {

        private String name;
        private JavaPlatform platform;

        /**
         * Create a PlatformKey for a broken platform.
         * @param name the ant name of the broken platform.
         */
        public PlatformKey(String name) {
            assert name != null;
            this.name = name;
        }

        /**
         * Create a PlatformKey for a platform.
         * @param platform the {@link JavaPlatform}.
         */
        public PlatformKey(JavaPlatform platform) {
            assert platform != null;
            this.platform = platform;
        }

        public int compareTo(Object o) {
            return this.getDisplayName().compareTo(((PlatformKey) o).getDisplayName());
        }

        @Override
        public boolean equals(Object other) {
            if (other instanceof PlatformKey) {
                PlatformKey otherKey = (PlatformKey) other;
                boolean equals;
                if (this.platform == null) {
                    equals = otherKey.platform == null;
                } else {
                    equals = this.platform.equals(otherKey.platform);
                }
                return equals && otherKey.getDisplayName().equals(this.getDisplayName());
            }
            return false;
        }

        @Override
        public int hashCode() {
            return getDisplayName().hashCode();
        }

        @Override
        public String toString() {
            return getDisplayName();
        }

        public synchronized String getDisplayName() {
            if (this.name == null) {
                this.name = this.platform.getDisplayName();
            }
            return this.name;
        }

        public boolean isDefaultPlatform() {
            if (this.platform == null) {
                return false;
            }
            return this.platform instanceof DefaultPlatformImpl;
        }

        public boolean isBroken() {
            return this.platform == null;
        }
    }

    public static final class SourceLevelKey implements Comparable {

        private final SpecificationVersion sourceLevel;
        private final boolean broken;

        public SourceLevelKey(final SpecificationVersion sourceLevel) {
            this(sourceLevel, false);
        }

        public SourceLevelKey(final SpecificationVersion sourceLevel, final boolean broken) {
            assert sourceLevel != null : "Source level cannot be null";
            this.sourceLevel = sourceLevel;
            this.broken = broken;
        }

        public SpecificationVersion getSourceLevel() {
            return this.sourceLevel;
        }

        public boolean isBroken() {
            return this.broken;
        }

        public int compareTo(final Object other) {
            assert other instanceof SourceLevelKey : "Illegal argument of SourceLevelKey.compareTo()";
            SourceLevelKey otherKey = (SourceLevelKey) other;
            return this.sourceLevel.compareTo(otherKey.sourceLevel);
        }

        @Override
        public boolean equals(final Object other) {
            return (other instanceof SourceLevelKey)
                    && ((SourceLevelKey) other).sourceLevel.equals(this.sourceLevel);
        }

        @Override
        public int hashCode() {
            return this.sourceLevel.hashCode();
        }

        @Override
        public String toString() {
            StringBuilder buffer = new StringBuilder();
            if (this.broken) {
                buffer.append("Broken: "); //NOI18N
            }
            buffer.append(this.sourceLevel.toString());
            return buffer.toString();
        }

        public String getDisplayName() {
            String tmp = sourceLevel.toString();
            if (JDK_1_5.compareTo(sourceLevel) <= 0) {
                tmp = tmp.replaceFirst("^1\\.([5-9]|\\d\\d+)$", "$1"); //NOI18N
            }
            return NbBundle.getMessage(PlatformUiSupport.class, "LBL_JDK", tmp);
        }
    }

    private static final class PlatformComboBoxModel extends AbstractListModel
            implements ComboBoxModel, PropertyChangeListener {
        private static final long serialVersionUID = 1L;

        private final JavaPlatformManager pm;
        private PlatformKey[] platformNamesCache;
        private String initialPlatform;
        private PlatformKey selectedPlatform;
        private String type;

        public PlatformComboBoxModel(String initialPlatform, String type) {
            this.pm = JavaPlatformManager.getDefault();
            this.pm.addPropertyChangeListener(WeakListeners.propertyChange(this, this.pm));
            this.initialPlatform = initialPlatform;
            this.type = type;
        }

        public int getSize() {
            PlatformKey[] platformNames = getPlatformNames();
            return platformNames.length;
        }

        public Object getElementAt(int index) {
            PlatformKey[] platformNames = getPlatformNames();
            assert index >= 0 && index < platformNames.length;
            return platformNames[index];
        }

        public Object getSelectedItem() {
            getPlatformNames(); // force setting of selectedPlatform if it is not already done
            return selectedPlatform;
        }

        public void setSelectedItem(Object obj) {
            selectedPlatform = (PlatformKey) obj;
            fireContentsChanged(this, -1, -1);
        }

        public void propertyChange(PropertyChangeEvent event) {
            if (JavaPlatformManager.PROP_INSTALLED_PLATFORMS.equals(event.getPropertyName())) {
                synchronized (this) {
                    platformNamesCache = null;
                }
                fireContentsChanged(this, -1, -1);
            }
        }

        private synchronized PlatformKey[] getPlatformNames() {
            if (platformNamesCache == null) {
                JavaPlatform[] platforms = pm.getPlatforms(null, new Specification(type, null)); //NOI18N
                Set<PlatformKey> orderedNames = new TreeSet<PlatformKey>();
                boolean activeFound = false;
                for (JavaPlatform platform : platforms) {
                    if (platform.getInstallFolders().size() > 0) {
                        PlatformKey pk = new PlatformKey(platform);
                        orderedNames.add(pk);
                        if (!activeFound && initialPlatform != null) {
                            String antName = platform.getProperties().get("platform.ant.name"); //NOI18N
                            if (initialPlatform.equals(antName)) {
                                if (selectedPlatform == null) {
                                    selectedPlatform = pk;
                                    initialPlatform = null;
                                }
                                activeFound = true;
                            }
                        }
                    }
                }
                if (!activeFound) {
                    if (initialPlatform == null) {
                        if (selectedPlatform == null || !orderedNames.contains(selectedPlatform)) {
                            selectedPlatform = new PlatformKey(JavaPlatformManager.getDefault().getDefaultPlatform());
                        }
                    } else {
                        PlatformKey pk = new PlatformKey(initialPlatform);
                        orderedNames.add(pk);
                        if (selectedPlatform == null) {
                            selectedPlatform = pk;
                        }
                    }
                }
                platformNamesCache = orderedNames.toArray(new PlatformKey[0]);
            }
            return platformNamesCache;
        }

    }

    private static final class PlatformListCellRenderer implements ListCellRenderer {

        private final ListCellRenderer delegate;

        public PlatformListCellRenderer() {
            delegate = HtmlRenderer.createRenderer();
        }

        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected,
                boolean cellHasFocus) {
            String name;
            if (value == null) {
                name = ""; //NOI18N
            } else {
                assert value instanceof PlatformKey : "Wrong model";
                PlatformKey key = (PlatformKey) value;
                if (key.isBroken()) {
                    name = "<html><font color=\"#A40000\">" //NOI18N
                            + NbBundle.getMessage(
                                    PlatformUiSupport.class, "TXT_BrokenPlatformFmt", key.getDisplayName());
                } else {
                    name = key.getDisplayName();
                }
            }
            return delegate.getListCellRendererComponent(list, name, index, isSelected, cellHasFocus);
        }
    }

    private static final class SourceLevelComboBoxModel extends AbstractListModel
            implements ComboBoxModel, ListDataListener {
        private static final long serialVersionUID = 1L;

        private static final String VERSION_PREFIX = "1."; // the version prefix // NOI18N
        private static final int INITIAL_VERSION_MINOR = 2; // 1.2

        private final ComboBoxModel platformComboBoxModel;
        private final SpecificationVersion minimalSpecificationVersion;
        private SpecificationVersion selectedSourceLevel;
        private SpecificationVersion originalSourceLevel;
        private SourceLevelKey[] sourceLevelCache;
        private PlatformKey activePlatform;

        public SourceLevelComboBoxModel(ComboBoxModel platformComboBoxModel, String initialSourceLevel,
                String initialTargetLevel, SpecificationVersion minimalSpecificationVersion) {
            this.platformComboBoxModel = platformComboBoxModel;
            activePlatform = (PlatformKey) this.platformComboBoxModel.getSelectedItem();
            this.platformComboBoxModel.addListDataListener(this);
            if (initialSourceLevel != null && initialSourceLevel.length() > 0) {
                try {
                    originalSourceLevel = new SpecificationVersion(initialSourceLevel);
                } catch (NumberFormatException nfe) {
                    // if the javac.source has invalid value, do not preselect and log it.
                    LOGGER.warning("Invalid javac.source: " + initialSourceLevel);
                }
            }
            if (initialTargetLevel != null && initialTargetLevel.length() > 0) {
                try {
                    SpecificationVersion originalTargetLevel = new SpecificationVersion(initialTargetLevel);
                    if (originalSourceLevel == null || originalSourceLevel.compareTo(originalTargetLevel)<0) {
                        originalSourceLevel = originalTargetLevel;
                    }
                } catch (NumberFormatException nfe) {
                    // if the javac.target has invalid value, do not preselect and log it
                    LOGGER.warning("Invalid javac.target: "+initialTargetLevel);
                }
            }
            selectedSourceLevel = originalSourceLevel;
            this.minimalSpecificationVersion = minimalSpecificationVersion;
        }

        public int getSize() {
            SourceLevelKey[] sLevels = getSourceLevels();
            return sLevels.length;
        }

        public Object getElementAt(int index) {
            SourceLevelKey[] sLevels = getSourceLevels();
            assert index >= 0 && index < sLevels.length;
            return sLevels[index];
        }

        public Object getSelectedItem() {
            for (SourceLevelKey key : getSourceLevels()) {
                if (key.getSourceLevel().equals(selectedSourceLevel)) {
                    return key;
                }
            }
            return null;
        }

        public void setSelectedItem(Object obj) {
            selectedSourceLevel = (obj == null ? null : ((SourceLevelKey) obj).getSourceLevel());
            fireContentsChanged(this, -1, -1);
        }

        public void intervalAdded(ListDataEvent e) {
        }

        public void intervalRemoved(ListDataEvent e) {
        }

        public void contentsChanged(ListDataEvent e) {
            PlatformKey selectedPlatform = (PlatformKey) platformComboBoxModel.getSelectedItem();
            JavaPlatform platform = getPlatform(selectedPlatform);
            if (platform != null) {
                SpecificationVersion version = platform.getSpecification().getVersion();
                if (selectedSourceLevel != null
                        && selectedSourceLevel.compareTo(version) > 0
                        && !shouldChangePlatform(selectedSourceLevel, version)) {
                    // restore original
                    platformComboBoxModel.setSelectedItem(activePlatform);
                    return;
                } else {
                    originalSourceLevel = null;
                }
            }
            activePlatform = selectedPlatform;
            resetCache();
        }

        private void resetCache() {
            synchronized (this) {
                sourceLevelCache = null;
            }
            fireContentsChanged(this, -1, -1);
        }

        private SourceLevelKey[] getSourceLevels() {
            if (sourceLevelCache == null) {
                PlatformKey selectedPlatform = (PlatformKey) platformComboBoxModel.getSelectedItem();
                JavaPlatform platform = getPlatform(selectedPlatform);
                List<SourceLevelKey> sLevels = new ArrayList<SourceLevelKey>();
                // if platform == null => broken platform, the source level range is unknown
                // the source level combo box should be empty and disabled
                boolean selSourceLevelValid = false;
                if (platform != null) {
                    SpecificationVersion version = platform.getSpecification().getVersion();
                    int index = getMinimalIndex(version);
                    SpecificationVersion template =
                            new SpecificationVersion(VERSION_PREFIX + Integer.toString(index++));
                    boolean origSourceLevelValid = false;

                    while (template.compareTo(version) <= 0) {
                        if (template.equals(originalSourceLevel)) {
                            origSourceLevelValid = true;
                        }
                        if (template.equals(selectedSourceLevel)) {
                            selSourceLevelValid = true;
                        }
                        sLevels.add(new SourceLevelKey(template));
                        template = new SpecificationVersion(VERSION_PREFIX + Integer.toString(index++));
                    }
                    if (originalSourceLevel != null && !origSourceLevelValid) {
                        if (originalSourceLevel.equals(selectedSourceLevel)) {
                            selSourceLevelValid = true;
                        }
                        sLevels.add(new SourceLevelKey(originalSourceLevel, true));
                    }
                }
                sourceLevelCache = sLevels.toArray(new SourceLevelKey[sLevels.size()]);
                if (!selSourceLevelValid) {
                    selectedSourceLevel = sourceLevelCache.length == 0
                            ? null : sourceLevelCache[sourceLevelCache.length - 1].getSourceLevel();
                }
            }
            return sourceLevelCache;
        }

        private int getMinimalIndex(SpecificationVersion platformVersion) {
            int index = INITIAL_VERSION_MINOR;
            if (minimalSpecificationVersion != null) {
                SpecificationVersion min = new SpecificationVersion(
                            VERSION_PREFIX + Integer.toString(index));
                while (min.compareTo(platformVersion) <= 0) {
                    if (min.equals(minimalSpecificationVersion)) {
                        return index;
                    }
                    min = new SpecificationVersion(
                            VERSION_PREFIX + Integer.toString(++index));
                }
            }
            return index;
        }

        private boolean shouldChangePlatform(SpecificationVersion selectedSourceLevel,
                SpecificationVersion platformSourceLevel) {
            JButton changeOption = new JButton(NbBundle.getMessage(PlatformUiSupport.class, "CTL_ChangePlatform"));
            changeOption.getAccessibleContext().setAccessibleDescription(
                    NbBundle.getMessage(PlatformUiSupport.class, "AD_ChangePlatform"));
            String message = MessageFormat.format(
                    NbBundle.getMessage(PlatformUiSupport.class, "TXT_ChangePlatform"),
                    new Object[] {selectedSourceLevel.toString(), platformSourceLevel.toString()});
            return DialogDisplayer.getDefault().notify(new NotifyDescriptor(
                    message,
                    NbBundle.getMessage(PlatformUiSupport.class, "TXT_ChangePlatformTitle"),
                    NotifyDescriptor.DEFAULT_OPTION,
                    NotifyDescriptor.WARNING_MESSAGE,
                    new Object[] {
                        changeOption,
                        NotifyDescriptor.CANCEL_OPTION
                    },
                    changeOption)) == changeOption;
        }
    }

    private static final class SourceLevelListCellRenderer implements ListCellRenderer {

        private ListCellRenderer delegate;

        public SourceLevelListCellRenderer() {
            delegate = HtmlRenderer.createRenderer();
        }

        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected,
                boolean cellHasFocus) {
            String message;
            if (value == null) {
                message = "";   //NOI18N
            } else {
                assert value instanceof SourceLevelKey;
                SourceLevelKey key = (SourceLevelKey) value;
                if (key.isBroken()) {
                    message = "<html><font color=\"#A40000\">" //NOI18N
                            + NbBundle.getMessage(
                                    PlatformUiSupport.class, "TXT_InvalidSourceLevel", key.getDisplayName());
                } else {
                    message = key.getDisplayName();
                }
            }
            return delegate.getListCellRendererComponent(list, message, index, isSelected, cellHasFocus);
        }
    }

    private static JavaPlatform getPlatform(PlatformKey platformKey) {
        return platformKey.platform;
    }

    public static JavaPlatform findPlatform(String displayName) {
        JavaPlatform[] platforms = JavaPlatformManager.getDefault().getPlatforms(
                displayName, new Specification("JavaFX", null)); //NOI18N
        if (platforms.length == 0) {
            return null;
        }
        return platforms[0];
    }
}
