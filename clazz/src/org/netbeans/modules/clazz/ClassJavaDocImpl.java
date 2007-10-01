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

package org.netbeans.modules.clazz;


import org.openide.src.*;

/** Empty implementation of JavaDoc for sourceless data objects.
 *
 * @author  Petr Hrebejk
 */
class ClassJavaDocImpl extends Object implements JavaDoc {

    private static final JavaDocTag[] TAGS_EMPTY = new JavaDocTag[] {};
    private static final JavaDocTag.See[] SEE_TAGS_EMPTY = new JavaDocTag.See[] {};
    private static final JavaDocTag.Param[] PARAM_TAGS_EMPTY = new JavaDocTag.Param[] {};
    private static final JavaDocTag.Throws[] THROWS_TAGS_EMPTY = new JavaDocTag.Throws[] {};
    private static final JavaDocTag.SerialField[] SERIALFIELD_TAGS_EMPTY = new JavaDocTag.SerialField[] {};

    /** Creates new ClassJavaDocImpl */
    public ClassJavaDocImpl() {
    }

    /** Get the entire text of the comment.
     * @return the whole text
     */
    public String getRawText() {
        return ""; // NOI18N
    }

    /** Set the raw text of the comment.
     * @param s the whole text to set
     * @exception SourceException if the modification cannot be performed
     */
    public void setRawText(String s) throws SourceException {
        throw new SourceException();
    }

    /** Get the actual text, cleared of all (non-inline) tags.
     * @return the plain text
     */
    public String getText() {
        return ""; // NOI18N
    }

    /** Set the actual text.
     * @param s the actual text, without any (non-inline) tags
     * @exception SourceException if the modification cannot be performed
     */
    public void setText(String s) throws SourceException {
        throw new SourceException();
    }

    /** Clears the javadoc from the source.
     */
    public void clearJavaDoc() throws SourceException {
        throw new SourceException();
    }

    /** Test if this javadoc is empty.
     * @return true if it is not generated to the source.
     */
    public boolean isEmpty() {
        return true;
    }

    /** Gets all tags from comment.
     */
    public JavaDocTag[] getTags() {
        return TAGS_EMPTY;
    }

    /** Gets all tags of given name
     */
    public JavaDocTag[] getTags(String name) {
        return TAGS_EMPTY;
    }

    /** Adds removes or sets tags used in this comment
     * @param elems the new initializers
     * @param action {@link #ADD}, {@link #REMOVE}, or {@link #SET}
     * @exception SourceException if impossible
     */
    public void changeTags(JavaDocTag[] tags,int action) throws SourceException {
        throw new SourceException();
    }

    /** Gets all @see tags
     */
    public JavaDocTag.See[] getSeeTags() {
        return SEE_TAGS_EMPTY;
    }

    /** The JavaDoc of a class.
    * Class javadoc adds no special tags.
    */
    static class Class extends ClassJavaDocImpl implements JavaDoc.Class {
    }

    /** The JavaDoc of a field.
    * <p>Currently adds special @SerialField tag
    */
    static class Field extends ClassJavaDocImpl implements JavaDoc.Field {
        /** Gets SerialField tags.
        */
        public JavaDocTag.SerialField[] getSerialFieldTags() {
            return SERIALFIELD_TAGS_EMPTY;
        }
    }

    /** The JavaDoc of a method. Adds two special tags: @para tag and @throws tag.
    */
    static class Method extends ClassJavaDocImpl implements JavaDoc.Method {

        /** Gets param tags.
        */
        public JavaDocTag.Param[] getParamTags() {
            return PARAM_TAGS_EMPTY;
        }

        /** Gets throws tags.
        */
        public JavaDocTag.Throws[] getThrowsTags() {
            return THROWS_TAGS_EMPTY;
        }
    }
}
