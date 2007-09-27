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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
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

package org.netbeans.core.registry;


import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileSystem;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Collection;
import java.util.Set;
import java.util.HashSet;
import java.awt.*;
import java.net.URL;

final class PrimitiveBinding extends ObjectBinding {
    public static final String PRIMITIVE_BINDING_PREFIX = "BINDING:";

    private FileObject folder;
    private String bindingName;

    public String getBindingName() {
        return bindingName;
    }

    public Object createInstance() throws IOException {
        Object o = folder.getAttribute(PRIMITIVE_BINDING_PREFIX + bindingName);
        if (o != null) {
            o = convertToObject(o);
        }
        return o;
    }

    // MultiFilesystem stores attributes of folders which do not exist
    // on writable layer yet on root file object of the writable filesystem
    // in the format of "folder\folder\attribute". Check that here:    
    static  boolean isCustomizedAndAttachedToRoot (FileSystem customFS, FileObject folder, String bindingName) {
        String name = folder.getPath().replace('/', '\\');
        return (customFS.getRoot().getAttribute(name+"\\"+PRIMITIVE_BINDING_PREFIX+bindingName) != null) ? true : false;                
    }

    // MultiFilesystem stores attributes of folders which do not exist
    // on writable layer yet on root file object of the writable filesystem
    // in the format of "folder\folder\attribute". Check that here:        
    static  void deleteIfCustomizedAndAttachedToRoot (FileSystem customFS, FileObject folder, String bindingName) throws IOException {
        String name = folder.getPath().replace('/', '\\');
        FileObject root = customFS.getRoot();
        if (root.getAttribute(name+"\\"+PRIMITIVE_BINDING_PREFIX+bindingName) != null) {
            root.setAttribute(name+"\\"+PRIMITIVE_BINDING_PREFIX+bindingName, null);               
        }        
    }
    
    static ObjectBinding get(FileObject folder, String bindingName) {
        return isValid(folder, bindingName) ? new PrimitiveBinding(folder, bindingName) : null;
    }

    static Collection getAll(FileObject folder) {
        Set retVal = new HashSet();
        Enumeration en = folder.getAttributes();
        while (en.hasMoreElements()) {
            String attr = (String) en.nextElement();
            if (attr.startsWith(PRIMITIVE_BINDING_PREFIX)) {
                ObjectBinding ob = PrimitiveBinding.get(folder, attr.substring(PRIMITIVE_BINDING_PREFIX.length()));
                if (ob != null) retVal.add(ob);
            }
        }
        return retVal;
    }


    /**
     * may return null
     */
    static ObjectBinding create(FileObject folder, String bindingName, Object object) {
        if (!isPrimitive(object)) return null;

        object = convertToPrimitive(object);
        try {
            folder.setAttribute(PRIMITIVE_BINDING_PREFIX + bindingName, object);
        } catch (IOException e) {
            // then this method returns null;
        }

        return isValid(folder, bindingName) ? new PrimitiveBinding(folder, bindingName) : null;
    }

    private PrimitiveBinding(FileObject folder, String bindingName) {
        super(folder);
        this.folder = folder;
        this.bindingName = bindingName;
    }

    public void delete() throws IOException {
        if (isEnabled()) {
            // delete primitive binding attributes            
            deleteAllAttributes();
            // delete primitive binding
            folder.setAttribute(PRIMITIVE_BINDING_PREFIX + bindingName, null);
        }
    }

    private void deleteAllAttributes() throws IOException {
        Enumeration en = folder.getAttributes();
        while (en.hasMoreElements()) {
            String attrName = (String) en.nextElement();
            if (attrName.startsWith(ContextImpl.PRIMITIVE_BINDING_ATTR_PREFIX + bindingName + "/")) {
                folder.setAttribute(attrName, null);
            }
        }
    }

    public boolean isEnabled() {
        return isValid(folder, bindingName);
    }

    public ObjectBinding write(Object object) throws IOException {
        return super.write(object);
    }

    private static boolean isValid(FileObject folder, String bindingName) {
        return (folder.getAttribute(PRIMITIVE_BINDING_PREFIX + bindingName) != null);
    }

    private static Object convertToPrimitive(Object o) {
        if (!((o instanceof Font) || (o instanceof Color))) {
            // all other simple or primitive data types (including URL)
            // are handled directly by FS attribute types
            return o;
        }

        String value;
        if (o instanceof Font) {
            Font f = (Font) o;
            String strStyle;

            if (f.isBold()) {
                strStyle = f.isItalic() ? "bolditalic" : "bold";
            } else {
                strStyle = f.isItalic() ? "italic" : "plain";
            }
            value = "Font[" + f.getFamily() + "-" + strStyle + "-" + f.getSize() + "]";
        } else {
            value = "Color[" + Integer.toString(((Color) o).getRGB()) + "]";
        }
        return value;
    }

    static boolean isPrimitive(Object o) {
        if (o instanceof String ||
                o instanceof Integer ||
                o instanceof Long ||
                o instanceof Boolean ||
                o instanceof Float ||
                o instanceof Double ||
                o instanceof Font ||
                o instanceof URL ||
                o instanceof Color) {
            return true;
        } else {
            return false;
        }
    }

    private static Object convertToObject(Object o) {
        if ((!(o instanceof String)) || (!(((String) o).endsWith("]")))) {
            return o;
        }
        try {
            String string = (String) o;
            String val = string.substring(0, string.length() - 1);
            if (val.startsWith("Font[")) {
                val = val.substring("Font[".length());
                return Font.decode(val);
            } else if (val.startsWith("Color[")) {
                val = val.substring("Color[".length());
                return Color.decode(val);
            }
        } catch (Exception e) {
            // ignore it
        }
        return o;
    }

}
