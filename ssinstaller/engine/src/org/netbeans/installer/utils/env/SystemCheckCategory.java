/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * 
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
 * 
 * The contents of this file are subject to the terms of either the GNU General
 * Public License Version 2 only ("GPL") or the Common Development and Distribution
 * License("CDDL") (collectively, the "License"). You may not use this file except in
 * compliance with the License. You can obtain a copy of the License at
 * http://www.netbeans.org/cddl-gplv2.html or nbbuild/licenses/CDDL-GPL-2-CP. See the
 * License for the specific language governing permissions and limitations under the
 * License.  When distributing the software, include this License Header Notice in
 * each file and include the License file at nbbuild/licenses/CDDL-GPL-2-CP.  Sun
 * designates this particular file as subject to the "Classpath" exception as
 * provided by Sun in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the License Header,
 * with the fields enclosed by brackets [] replaced by your own identifying
 * information: "Portions Copyrighted [year] [name of copyright owner]"
 * 
 * Contributor(s):
 * 
 * The Original Software is NetBeans. The Initial Developer of the Original Software
 * is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun Microsystems, Inc. All
 * Rights Reserved.
 * 
 * If you wish your version of this file to be governed by only the CDDL or only the
 * GPL Version 2, indicate your decision by adding "[Contributor] elects to include
 * this software in this distribution under the [CDDL or GPL Version 2] license." If
 * you do not indicate a single choice of license, a recipient has the option to
 * distribute your version of this file under either the CDDL, the GPL Version 2 or
 * to extend the choice of license to its licensees as provided above. However, if
 * you add GPL Version 2 code and therefore, elected the GPL Version 2 license, then
 * the option applies only if the new code is made subject to such option by the
 * copyright holder.
 */

package org.netbeans.installer.utils.env;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.netbeans.installer.Installer;
import org.netbeans.installer.product.Registry;
import org.netbeans.installer.product.components.NativeClusterConfigurationLogic;
import org.netbeans.installer.product.components.Product;
import org.netbeans.installer.utils.ErrorManager;
import org.netbeans.installer.utils.FileUtils;
import org.netbeans.installer.utils.LogManager;
import org.netbeans.installer.utils.ResourceUtils;
import org.netbeans.installer.utils.StringUtils;
import org.netbeans.installer.utils.SystemUtils;
import org.netbeans.installer.utils.env.impl.LinuxRPMPackagesAnalyzer;
import org.netbeans.installer.utils.exceptions.InitializationException;
import org.netbeans.installer.utils.exceptions.NativeException;
import org.netbeans.installer.utils.helper.ExtendedUri;
import org.netbeans.installer.utils.helper.Platform;
import org.netbeans.installer.utils.silent.SilentLogManager;
import org.netbeans.installer.wizard.components.panels.PreInstallSummaryPanel;

public enum SystemCheckCategory implements ConfigurationChecker {
    
    PLATFORM(PLATFORM_CATEGORY_CAPTION, new PlatformCheck()),
    OS(OS_CATEGORY_CAPTION, new OSCheck()),
//    CPU(CPU_CATEGORY_CAPTION, new CPUCheck()),
    MEMORY(MEMORY_CATEGORY_CAPTION, new MemoryCheck()),
    RIGHTS(ADMIN_CATEGORY_CAPTION, new RightsCheck()),
    REMOTE(REMOTE_CATEGORY_CAPTION, new RemotePackagesCheck()),
    //PATCHES(PATCHES_CATEGORY_CAPTION, new PatchesCheck()),
    //PACKAGES(PACKAGES_CATEGORY_CAPTION, new PackagesCheck()),
    FREE_SPACE_SILENT("", new FreeSpaceSilentCheck());
    
    private String caption = null;
    private ConfigurationChecker checker = null;
    
    SystemCheckCategory(String caption, ConfigurationChecker checker) {
        this.caption = caption;
        this.checker = checker;
    }

    public String getCaption() {
        return caption;
    }

    public CheckStatus check() {
        return checker.check();
    }

    public String getShortErrorMessage() {
        return checker.getShortErrorMessage();
    }

    public String getLongErrorMessage() {
        return checker.getLongErrorMessage();
    }

    public boolean isMandatory() {
        return checker.isMandatory();
    }

    public String getDisplayString() {
        return checker.getDisplayString();
    }    
    
    public boolean isCheckPassed() {
        return check().equals(CheckStatus.OK);
    }
    
    public static List<SystemCheckCategory> getProblemCategories() {
        List<SystemCheckCategory> result = new LinkedList<SystemCheckCategory>();
        for(SystemCheckCategory category: values()) {
            if (category.isMandatory() && !category.isCheckPassed()) result.add(category);
        }
        return result;
    }
    
    public static boolean hasProblemCategories() {
        for(SystemCheckCategory category: values()) {
            if (category.isMandatory() && !category.isCheckPassed()) return true;
        }
        return false;
    }
    
    public static boolean hasErrorCategories() {
        for(SystemCheckCategory category: values()) {
            if (category.isMandatory() && category.check().equals(CheckStatus.ERROR)) return true;
        }
        return false;        
    }

}

class PlatformCheck implements ConfigurationChecker {

    private final String INCORRECT_PLATFORM_SHORT = ResourceUtils.getString(PlatformCheck.class, "SCC.platfromcheck.incorrect_platform_short"); // NOI18N
    private final String INCORRECT_PLATFORM_LONG = ResourceUtils.getString(PlatformCheck.class, "SCC.platfromcheck.incorrect_platform_long"); // NOI18N
    private final String INCORRECT_PLATFORM_MESSAGE = ResourceUtils.getString(PlatformCheck.class, "SCC.platfromcheck.incorrect_platform_message"); // NOI18N
    private final String CORRECT_PLATFORM_MESSAGE = ResourceUtils.getString(PlatformCheck.class, "SCC.platfromcheck.correct_platform_message"); // NOI18N
    private final String PLATFORMS_LIST_SEPARATOR = ", ";
    
    private Registry registry = null;
    private String incompatiblePlatform = null;
    private CheckStatus status = null;
    
    private Registry getRegistry() {        
        try {            
            Registry bundledRegistry = new Registry();
            final String bundledRegistryUri = System.getProperty(Registry.BUNDLED_PRODUCT_REGISTRY_URI_PROPERTY);
            if (bundledRegistryUri != null) {
                bundledRegistry.loadProductRegistry(bundledRegistryUri);
            } else {
                bundledRegistry.loadProductRegistry(Registry.DEFAULT_BUNDLED_PRODUCT_REGISTRY_URI);
            }
            return bundledRegistry;
        } catch (InitializationException e) {
            ErrorManager.notifyError("Cannot load bundled registry", e);
        }        
        return Registry.getInstance();
    }
       
    private boolean hasCompatiblePlatforms(Platform platform, Collection<Platform> platforms) {
        for(Platform pl: platforms) {
            if (pl.getOsFamily().equals(platform.getOsFamily()) && (pl.getHardwareArch() != null? pl.getHardwareArch().equals(platform.getHardwareArch()): true)) return true;
        }
        return false;
    }
    
    private boolean isCorrectPlatform() {
        if (registry == null) registry = getRegistry();        
        if (registry != null) {
            Platform current = EnvironmentInfoFactory.getInstance().getPlatform();            
            for(Product product: registry.getProducts()) {
                if (!hasCompatiblePlatforms(current, product.getPlatforms())) {
                    StringBuffer sb = new StringBuffer();
                    for(Platform platform: product.getPlatforms()) {
                        sb.append(platform.getDisplayName());
                        sb.append(PLATFORMS_LIST_SEPARATOR);
                    }
                    sb.setLength(sb.length() - PLATFORMS_LIST_SEPARATOR.length());
                    incompatiblePlatform = sb.toString();
                    return false;          
                }
            }
            return true;
        }
        return true;
    }
    
    public CheckStatus check() {
        if (status == null) {
            status = CheckStatus.OK;
            if (!isCorrectPlatform()) status = CheckStatus.ERROR;
        }
        return status;
    }

    public boolean isOK() {
        return check().equals(CheckStatus.OK);
    }
    
    public String getShortErrorMessage() {
        if (!isOK()) return INCORRECT_PLATFORM_SHORT;
        return "";
    }

    public String getLongErrorMessage() {
        if (!isOK()) return INCORRECT_PLATFORM_LONG;
        return "";
    }

    public boolean isMandatory() {
        return true;
    }

    public String getDisplayString() {
        return isOK()? CORRECT_PLATFORM_MESSAGE: INCORRECT_PLATFORM_MESSAGE + ": " + incompatiblePlatform;
    }
    
}

class OSCheck implements ConfigurationChecker {
    
    private final String UNKNOWN_OS = ResourceUtils.getString(OSCheck.class, "SCC.oscheck.unknown_os"); // NOI18N
    private final String INFO_NOT_AVAILABLE_SHORT = ResourceUtils.getString(OSCheck.class, "SCC.oscheck.info_not_available_short"); // NOI18N
    private final String INFO_NOT_AVAILABLE_LONG = ResourceUtils.getString(OSCheck.class, "SCC.oscheck.info_not_available_long"); // NOI18N
    private final String NOT_SUPPORTED_SHORT = ResourceUtils.getString(OSCheck.class, "SCC.oscheck.not_supported_short"); // NOI18N
    private final String NOT_SUPPORTED_LONG = ResourceUtils.getString(OSCheck.class, "SCC.oscheck.not_supported_long"); // NOI18N
    private final String NOT_COMPATIBLE_PACKAGES_SHORT = ResourceUtils.getString(OSCheck.class, "SCC.oscheck.not_compatible_packages_short"); // NOI18N
    private final String NOT_COMPATIBLE_PACKAGES_LONG = ResourceUtils.getString(OSCheck.class, "SCC.oscheck.not_compatible_packages_long"); // NOI18N
        
    private boolean isInfoAvailable() {
        return EnvironmentInfoFactory.getInstance().getOSName() != null && EnvironmentInfoFactory.getInstance().getOSVersion() != null;
    }
    
    private boolean isSupported() {
        String name = EnvironmentInfoFactory.getInstance().getOSName();
        String version = EnvironmentInfoFactory.getInstance().getOSVersion();
        String platform = EnvironmentInfoFactory.getInstance().getPlatformArchitecture();
        return SystemRequements.getInstance().checkDistribution(name, version, platform);
    }
    
    private boolean isCompatiblePackagesType() {
        if (SystemUtils.isSolaris()) return true;
        PackageType type = EnvironmentInfoFactory.getInstance().getPackageType();
        if (type == null) return LinuxRPMPackagesAnalyzer.isRPMSupported();
        else return type.equals(PackageType.LINUX_RPM);
    }
    
    public CheckStatus check() {
        if (!isInfoAvailable()) return CheckStatus.WARNING;
        if (!isCompatiblePackagesType()) return CheckStatus.ERROR;
        if (!isSupported()) return CheckStatus.WARNING;
        return CheckStatus.OK;
    }

    public String getShortErrorMessage() {
        if (!isInfoAvailable()) return INFO_NOT_AVAILABLE_SHORT;
        if (!isCompatiblePackagesType()) return NOT_COMPATIBLE_PACKAGES_SHORT;
        if (!isSupported()) return NOT_SUPPORTED_SHORT;        
        return "";
    }

    public String getLongErrorMessage() {
        if (!isInfoAvailable()) return INFO_NOT_AVAILABLE_LONG;
        if (!isCompatiblePackagesType()) return NOT_COMPATIBLE_PACKAGES_LONG;
        if (!isSupported()) return NOT_SUPPORTED_LONG;        
        return "";
    }

    public boolean isMandatory() {
        return true;
    }

    public String getDisplayString() {
        EnvironmentInfo env = EnvironmentInfoFactory.getInstance();
        return isInfoAvailable()? (env.getOSName() + " " + env.getOSVersion()): UNKNOWN_OS;
    }
    
}

class CPUCheck implements ConfigurationChecker {

    private final String UNKNOWN_CPU = ResourceUtils.getString(CPUCheck.class, "SCC.cpucheck.unknown_cpu"); // NOI18N
    private final String INFO_NOT_AVAILABLE_SHORT = ResourceUtils.getString(CPUCheck.class, "SCC.cpucheck.info_not_available_short"); // NOI18N
    private final String INFO_NOT_AVAILABLE_LONG = ResourceUtils.getString(CPUCheck.class, "SCC.cpucheck.info_not_available_long"); // NOI18N
    private final String CPU_NOT_SUPPORTED_SHORT = ResourceUtils.getString(CPUCheck.class, "SCC.cpucheck.cpu_not_supported_short"); // NOI18N
    private final String CPU_NOT_SUPPORTED_LONG = ResourceUtils.getString(CPUCheck.class, "SCC.cpucheck.cpu_not_supported_long"); // NOI18N
    
    private boolean isInfoAvailable() {
        return EnvironmentInfoFactory.getInstance().getCPUInformation() != null && EnvironmentInfoFactory.getInstance().getCPUClock() != 0;
    }
    
    private boolean isCPUCompatible() {
        String model = EnvironmentInfoFactory.getInstance().getCPUInformation();
        float frequency = EnvironmentInfoFactory.getInstance().getCPUClock();
        return SystemRequements.getInstance().checkCPU(model, frequency);
    }
    
    public CheckStatus check() {
        if (!isInfoAvailable()) return CheckStatus.WARNING;
        if (!isCPUCompatible()) return CheckStatus.WARNING;
        return CheckStatus.OK;
    }

    public String getShortErrorMessage() {
        if (!isInfoAvailable()) return INFO_NOT_AVAILABLE_SHORT;
        if (!isCPUCompatible()) return CPU_NOT_SUPPORTED_SHORT;
        return "";
    }

    public String getLongErrorMessage() {
        if (!isInfoAvailable()) return INFO_NOT_AVAILABLE_LONG;
        if (!isCPUCompatible()) return CPU_NOT_SUPPORTED_LONG;
        return "";
    }

    public boolean isMandatory() {
        return true;
    }

    public String getDisplayString() {
        return isInfoAvailable()? EnvironmentInfoFactory.getInstance().getCPUInformation(): UNKNOWN_CPU;
    }
    
}

class MemoryCheck implements ConfigurationChecker {

    private final String UNKNOWN_MEMORY_SIZE = ResourceUtils.getString(MemoryCheck.class, "SCC.memorycheck.unknown_memory_size"); // NOI18N
    private final String INFO_NOT_AVAILABLE_SHORT = ResourceUtils.getString(MemoryCheck.class, "SCC.memorycheck.info_not_available_short"); // NOI18N
    private final String INFO_NOT_AVAILABLE_LONG = ResourceUtils.getString(MemoryCheck.class, "SCC.memorycheck.info_not_available_long"); // NOI18N
    private final String NOT_ENOUGHT_MEMORY_SHORT = ResourceUtils.getString(MemoryCheck.class, "SCC.memorycheck.not_enought_memory_short"); // NOI18N
    private final String NOT_ENOUGHT_MEMORY_LONG = ResourceUtils.getString(MemoryCheck.class, "SCC.memorycheck.not_enought_memory_long"); // NOI18N
    private final String MEGABYTES = ResourceUtils.getString(MemoryCheck.class, "SCC.memorycheck.megabytes"); // NOI18N
    
    private boolean isInfoAvailable() {
        return EnvironmentInfoFactory.getInstance().getPhisicalMemorySize() != 0;
    }
    
    private boolean isMemoryEnought() {
        return EnvironmentInfoFactory.getInstance().getPhisicalMemorySize() >= SystemRequements.getInstance().getMemoryMinimum();
    }
    
    public CheckStatus check() {
        if (!isInfoAvailable()) return CheckStatus.WARNING;
        if (!isMemoryEnought()) return CheckStatus.WARNING;
        return CheckStatus.OK;
    }

    public String getShortErrorMessage() {
        if (!isInfoAvailable()) return INFO_NOT_AVAILABLE_SHORT;
        if (!isMemoryEnought()) return NOT_ENOUGHT_MEMORY_SHORT;
        return "";
    }

    public String getLongErrorMessage() {
        if (!isInfoAvailable()) return INFO_NOT_AVAILABLE_LONG;
        if (!isMemoryEnought()) return NOT_ENOUGHT_MEMORY_LONG;
        return "";
    }

    public boolean isMandatory() {
        return true;
    }

    public String getDisplayString() {
        return isInfoAvailable()? (EnvironmentInfoFactory.getInstance().getPhisicalMemorySize() / 1024) + " " + MEGABYTES: UNKNOWN_MEMORY_SIZE;
    }
    
}

class RightsCheck implements ConfigurationChecker {

    private final String NO_ADMIN_RIGHTS_SHORT = ResourceUtils.getString(RightsCheck.class, "SCC.rightscheck.no_admin_rights_short"); // NOI18N
    private final String NO_ADMIN_RIGHTS_LONG = ResourceUtils.getString(RightsCheck.class, "SCC.rightscheck.no_admin_rights_long"); // NOI18N
    private final String ADMIN_RIGHTS_MESSAGE = ResourceUtils.getString(RightsCheck.class, "SCC.rightscheck.admin_rights_message"); // NOI18N
    private final String NO_ADMIN_RIGHTS_MESSAGE = ResourceUtils.getString(RightsCheck.class, "SCC.rightscheck.no_admin_rights_message"); // NOI18N
    
    public CheckStatus check() {
        if (!EnvironmentInfoFactory.getInstance().isUserAdmin()) return CheckStatus.ERROR;
        return CheckStatus.OK;
    }

    public String getShortErrorMessage() {
        if (!EnvironmentInfoFactory.getInstance().isUserAdmin()) return NO_ADMIN_RIGHTS_SHORT;
        return "";
    }

    public String getLongErrorMessage() {
        if (!EnvironmentInfoFactory.getInstance().isUserAdmin()) return NO_ADMIN_RIGHTS_LONG;
        return "";
    }

    public boolean isMandatory() {
        return true;
    }

    public String getDisplayString() {
        return EnvironmentInfoFactory.getInstance().isUserAdmin()? ADMIN_RIGHTS_MESSAGE: NO_ADMIN_RIGHTS_MESSAGE;
    }
    
}

class RemotePackagesCheck implements ConfigurationChecker {

    private final String OK_CHECK_RESULT_MESSAGE = ResourceUtils.getString(RemotePackagesCheck.class, "SCC.remotepackagescheck.ok_check_result_message"); // NOI18N
    private final String ERROR_CHECK_RESULT_MESSAGE = ResourceUtils.getString(RemotePackagesCheck.class, "SCC.remotepackagescheck.error_check_result_message"); // NOI18N
    private final String REMOTE_PACKAGES_NOT_AVAILABLE_SHORT = ResourceUtils.getString(RemotePackagesCheck.class, "SCC.remotepackagescheck.remote_packages_not_available_short"); // NOI18N
    private final String REMOTE_PACKAGES_NOT_AVAILABLE_LONG = ResourceUtils.getString(RemotePackagesCheck.class, "SCC.remotepackagescheck.remote_packages_not_available_long"); // NOI18N
    
    private CheckStatus status = null;
    
    private boolean isFileExists(URI uri) {
        return (new File(uri)).exists();
    }
    
    private Registry getRegistry() {        
        try {            
            Registry bundledRegistry = new Registry();
            final String bundledRegistryUri = System.getProperty(Registry.BUNDLED_PRODUCT_REGISTRY_URI_PROPERTY);
            if (bundledRegistryUri != null) {
                bundledRegistry.loadProductRegistry(bundledRegistryUri);
            } else {
                bundledRegistry.loadProductRegistry(Registry.DEFAULT_BUNDLED_PRODUCT_REGISTRY_URI);
            }
            return bundledRegistry;
        } catch (InitializationException e) {
            ErrorManager.notifyError("Cannot load bundled registry", e);
        }        
        return Registry.getInstance();
    }
    
    public CheckStatus check() {
        if (status == null) {
            final Registry registry = getRegistry();
            status = CheckStatus.OK;
            for(Product product: registry.getProducts()) {
                for(ExtendedUri euri: product.getDataUris()) {
                    String euriStr = euri.getRemote().toString();
                    if (euriStr.startsWith(ExtendedUri.RESOURCE_SCHEME)) continue;
                    if (euriStr.startsWith(ExtendedUri.FILE_SCHEME) && !isFileExists(euri.getRemote())) status = CheckStatus.ERROR;
                    if (euriStr.startsWith(ExtendedUri.HTTP_SCHEME)) {
                        try {
                            if (!EnvironmentInfoFactory.getInstance().isRemoteFileAvailable(euri.getRemote().toURL())) status = CheckStatus.ERROR;
                        } catch (MalformedURLException ex) {
                            LogManager.log(ex);
                        }
                    }
                }
            }
        }
        return status;
    }

    public String getShortErrorMessage() {
        if (check() != null && !check().equals(CheckStatus.OK)) return REMOTE_PACKAGES_NOT_AVAILABLE_SHORT;
        return "";
    }

    public String getLongErrorMessage() {
        if (check() != null && !check().equals(CheckStatus.OK)) return REMOTE_PACKAGES_NOT_AVAILABLE_LONG;
        return "";
    }

    public boolean isMandatory() {
        final Registry registry = getRegistry();
        for(Product product: registry.getProducts()) {
            for(ExtendedUri euri: product.getDataUris()) {
                String euriStr = euri.getRemote().toString();
                if (euriStr.startsWith(ExtendedUri.RESOURCE_SCHEME)) continue;
                if (euriStr.startsWith(ExtendedUri.FILE_SCHEME)) return true;
                if (euriStr.startsWith(ExtendedUri.HTTP_SCHEME)) return true;
            }
        }
        return false;
    }
    
    public String getDisplayString() {
        if (check() != null && check().equals(CheckStatus.OK)) return OK_CHECK_RESULT_MESSAGE;
        return ERROR_CHECK_RESULT_MESSAGE;
    }
    
}

class PatchesCheck implements ConfigurationChecker {

    private final String NECESSARY_PATCHES_INSTALLED_MESSAGE = ResourceUtils.getString(PatchesCheck.class, "SCC.patchescheck.patches_installed"); // NOI18N;
    private final String NECESSARY_PATCHES_NOT_INSTALLED_MESSAGE = ResourceUtils.getString(PatchesCheck.class, "SCC.patchescheck.patches_not_installed"); // NOI18N;
    private final String PATCHES_NOT_INSTALLED_SHORT = ResourceUtils.getString(PatchesCheck.class, "SCC.patchescheck.patches_not_installed_message_short"); // NOI18N;
    private final String PATCHES_NOT_INSTALLED_LONG = ResourceUtils.getString(PatchesCheck.class, "SCC.patchescheck.patches_not_installed_message_long"); // NOI18N;
    
    private Set<String> notInstalledPatches = new HashSet<String>();
    private boolean isActualPatchesInfo = false;
    
    private void checkPatches() {
        EnvironmentInfo env = EnvironmentInfoFactory.getInstance();
        if (!SystemRequements.getInstance().hasPathesInfo(env.getOSName(), env.getOSVersion(), env.getPlatform().getHardwareArch())) return;
        for(String patch: SystemRequements.getInstance().getPatches(env.getOSName(), env.getOSVersion(), env.getPlatform().getHardwareArch())) {
            if (!env.isPatchInstalled(patch)) notInstalledPatches.add(patch);
        }
        isActualPatchesInfo = true;
    }
    
    public CheckStatus check() {
        if (!isActualPatchesInfo) checkPatches();
        if (!notInstalledPatches.isEmpty()) return CheckStatus.WARNING;
        return CheckStatus.OK;
    }

    public String getShortErrorMessage() {
        if (!isActualPatchesInfo) checkPatches();
        if (!notInstalledPatches.isEmpty()) return PATCHES_NOT_INSTALLED_SHORT;
        return "";        
    }

    public String getLongErrorMessage() {
        if (!isActualPatchesInfo) checkPatches();
        if (!notInstalledPatches.isEmpty()) {
            StringBuffer sb = new StringBuffer();
            sb.append(PATCHES_NOT_INSTALLED_LONG);
            sb.append(" ");
            for(String patch: notInstalledPatches) {
                sb.append(patch);
                sb.append(", ");
            }
            sb.setLength(sb.length() - 2);
            sb.append(".");
            return sb.toString();
        }
        return "";
    }

    public boolean isMandatory() {
        return SystemUtils.isSolaris();
    }

    public String getDisplayString() {
        if (check().equals(CheckStatus.OK)) return NECESSARY_PATCHES_INSTALLED_MESSAGE;
        return NECESSARY_PATCHES_NOT_INSTALLED_MESSAGE;
    }
    
}



class FreeSpaceSilentCheck implements ConfigurationChecker {

    private final String ERROR_NON_EXISTENT_ROOT_PROPERTY = "SCC.spacecheck.error.non.existent.root";
    private final String ERROR_NOT_ENOUGH_SPACE_PROPERTY = "SCC.spacecheck.error.not.enough.space";
    private final String ERROR_CANNOT_CHECK_SPACE_PROPERTY = "SCC.spacecheck.error.cannot.check.space";
    private final String ERROR_CANNOT_WRITE_PROPERTY = "SCC.spacecheck.error.cannot.write";
    private final String ERROR_FSROOTS_PROPERTY = "SCC.spacecheck.error.fsroots";
    
    private Boolean checkCache = null;
    private String errorMessage = null;
    
    protected String validateSpace() {
        try {
            if(!Boolean.getBoolean(SystemUtils.NO_SPACE_CHECK_PROPERTY)) {
                final List<File> roots =
                        SystemUtils.getFileSystemRoots();
                final List<Product> toInstall =
                        Registry.getInstance().getProductsToInstall();
                final Map<File, Long> spaceMap =
                        new HashMap<File, Long>();

                LogManager.log("Available roots : " + StringUtils.asString(roots));

                File downloadDataDirRoot = FileUtils.getRoot(
                        Installer.getInstance().getLocalDirectory(), roots);
                long downloadSize = 0;
                for (Product product: toInstall) {
                    downloadSize+=product.getDownloadSize();
                }
                // the critical check point - we download all the data
                spaceMap.put(downloadDataDirRoot, new Long(downloadSize));
                long lastDataSize = 0;
                for (Product product: toInstall) {

                    File installLocation = product.getInstallationLocation();
                    try {
                        if (product.getLogic() instanceof NativeClusterConfigurationLogic) {
                            installLocation = Registry.getInstance().getProducts(NativeClusterConfigurationLogic.SS_BASE_UID).get(0).getInstallationLocation();
                        }
                    } catch (InitializationException ex) {
                        LogManager.log(ex);
                    }
                  //  LogManager.log("   Prouct [" + product. + "] <- " + installLocation);
                    final File root = FileUtils.getRoot(installLocation, roots);
                    final long productSize = product.getRequiredDiskSpace();

                    LogManager.log("    [" + root + "] <- " + installLocation);

                    if ( root != null ) {
                        Long ddSize =  spaceMap.get(downloadDataDirRoot);
                        // remove space that was freed after the remove of previos product data
                        spaceMap.put(downloadDataDirRoot,
                                Long.valueOf(ddSize - lastDataSize));

                        // add space required for next product installation
                        Long size = spaceMap.get(root);
                        size = Long.valueOf(
                                (size != null ? size.longValue() : 0L) +
                                productSize);
                        spaceMap.put(root, size);
                        lastDataSize = product.getDownloadSize();
                    } else {
                        return StringUtils.format(
                                ResourceUtils.getString(FreeSpaceSilentCheck.class, ERROR_NON_EXISTENT_ROOT_PROPERTY),
                                product, installLocation);
                    }
                }

                for (File root: spaceMap.keySet()) {
                    try {
                        final long availableSpace =
                                SystemUtils.getFreeSpace(root);
                        final long requiredSpace =
                                spaceMap.get(root) + PreInstallSummaryPanel.REQUIRED_SPACE_ADDITION;

                        if (availableSpace < requiredSpace) {
                            return StringUtils.format(
                                    ResourceUtils.getString(FreeSpaceSilentCheck.class, ERROR_NOT_ENOUGH_SPACE_PROPERTY),                                    
                                    root,
                                    StringUtils.formatSize(requiredSpace - availableSpace));
                        }
                    } catch (NativeException e) {
                        ErrorManager.notifyError(
                                ResourceUtils.getString(FreeSpaceSilentCheck.class, ERROR_CANNOT_CHECK_SPACE_PROPERTY),                                    
                                e);
                    }
                }
            }

            final List<Product> toUninstall =
                    Registry.getInstance().getProductsToUninstall();
            for (Product product: toUninstall) {
                if (!FileUtils.canWrite(product.getInstallationLocation())) {
                    return StringUtils.format(
                            ResourceUtils.getString(FreeSpaceSilentCheck.class, ERROR_CANNOT_WRITE_PROPERTY),                                                                
                            product,
                            product.getInstallationLocation());
                }
            }

        } catch (IOException e) {
            ErrorManager.notifyError(
                    ResourceUtils.getString(FreeSpaceSilentCheck.class, ERROR_FSROOTS_PROPERTY),
                    e);
        }

        return null;
    }
    
    public CheckStatus check() {
        if (checkCache == null) {
            errorMessage = validateSpace(); 
            checkCache = (errorMessage == null);
        }
        if (checkCache == false) return CheckStatus.ERROR;
        return CheckStatus.OK;
    }

    public String getShortErrorMessage() {
        return "";
    }

    public String getLongErrorMessage() {
        if (checkCache == false) return errorMessage;
        return "";
    }

    public boolean isMandatory() {
        return SilentLogManager.isLogManagerActive();
    }

    public String getDisplayString() {
        return "";
    }
    
}