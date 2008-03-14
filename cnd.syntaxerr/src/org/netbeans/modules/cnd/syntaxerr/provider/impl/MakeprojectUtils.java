/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * 
 * Copyright 2008 Sun Microsystems, Inc. All rights reserved.
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
 * Contributor(s):
 * 
 * Portions Copyrighted 2008 Sun Microsystems, Inc.
 */

package org.netbeans.modules.cnd.syntaxerr.provider.impl;

import org.netbeans.modules.cnd.api.compilers.CompilerSet;
import org.netbeans.modules.cnd.api.compilers.CompilerSetManager;
import org.netbeans.modules.cnd.api.compilers.Tool;
import org.netbeans.modules.cnd.api.project.NativeFileItem;
import org.netbeans.modules.cnd.makeproject.api.compilers.BasicCompiler;
import org.netbeans.modules.cnd.makeproject.api.configurations.BasicCompilerConfiguration;
import org.netbeans.modules.cnd.makeproject.api.configurations.ConfigurationDescriptor;
import org.netbeans.modules.cnd.makeproject.api.configurations.Folder;
import org.netbeans.modules.cnd.makeproject.api.configurations.Item;
import org.netbeans.modules.cnd.makeproject.api.configurations.ItemConfiguration;
import org.netbeans.modules.cnd.makeproject.api.configurations.MakeConfiguration;
import org.netbeans.modules.cnd.makeproject.api.configurations.MakeConfigurationDescriptor;
import org.openide.loaders.DataObject;

/**
 * The utility class for makeproject related static functions
 * @author Vladimir Kvashin
 */
public class MakeprojectUtils {

    /** Converts Item to NativeFileItem */
    public static NativeFileItem toNativeFileItem(Item item) {
        //FIXUP: there should be a function in makeproject that does this!
        return (item instanceof NativeFileItem) ? (NativeFileItem) item : null;
    }
    
    /** Converts NativeFileItem to Item */
    public static Item toItem(NativeFileItem item) {
        //FIXUP: there should be a function in makeproject that does this!
        return (item instanceof Item) ? (Item) item : null;
    }
    
    public static String getCompilerPath(DataObject dao, NativeFileItem nativeFileItem) {
        Item item = toItem(nativeFileItem);
        if( item != null ) {
            MakeConfiguration activeConf = getActiveConfiguration(item);
            if( activeConf != null ) {
                ItemConfiguration itemConf = item.getItemConfiguration(activeConf);
                CompilerSet compilerSet = CompilerSetManager.getDefault().getCompilerSet(activeConf.getCompilerSet().getValue());
                if( compilerSet != null ) {
                    Tool tool = compilerSet.getTool(itemConf.getTool());
                    return tool.getPath();
                }
            }
        }
        return null;
    }
    
    public static String getCompilerOptions(NativeFileItem nativeFileItem) {
        Item item = toItem(nativeFileItem);
        if( item != null ) {
            MakeConfiguration activeConf = getActiveConfiguration(item);
            if( activeConf != null ) {
                ItemConfiguration itemConf = item.getItemConfiguration(activeConf);
                CompilerSet compilerSet = CompilerSetManager.getDefault().getCompilerSet(activeConf.getCompilerSet().getValue());
                if( compilerSet != null ) {
                    BasicCompiler compiler = (BasicCompiler)compilerSet.getTool(itemConf.getTool());
                    BasicCompilerConfiguration compilerConfiguration = itemConf.getCompilerConfiguration();
                    String options = compilerConfiguration.getOptions(compiler);
                    if( options.startsWith("$(") ) { //NOI18N
                        int pos = options.indexOf(")"); //NOI18N
                        if( pos > 0 ) {
                            return options.substring(pos+1);
                        } else {
                            return null;
                        }
                    } else {
                        return options;
                    }
                }
            }
        }
        return null;
    }
    
    private static MakeConfiguration getActiveConfiguration(Item item) {
        Folder folder = item.getFolder();
        if( folder != null ) {
            ConfigurationDescriptor configurationDescriptor = folder.getConfigurationDescriptor();
            if( configurationDescriptor instanceof MakeConfigurationDescriptor ) {
                return (MakeConfiguration) ((MakeConfigurationDescriptor) configurationDescriptor).getConfs().getActive();
            }
        }
        return null;
    }
    
//    private static ItemConfiguration getItemConfiguration(NativeFileItem nativeFileItem) {
//        Item item = toItem(nativeFileItem);
//        if( item != null ) {
//            return getItemConfiguration(item);
//        }
//        return null;
//    }
    
//    private static ItemConfiguration getItemConfiguration(Item item) {
//        MakeConfiguration activeConf = getActiveConfiguration(item);
//        if( activeConf != null ) {
//            return item.getItemConfiguration(activeConf);
//        }
//        return null;
//    }
    
}

