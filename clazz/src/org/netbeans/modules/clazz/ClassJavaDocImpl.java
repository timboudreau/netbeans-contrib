/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2000 Sun
 * Microsystems, Inc. All Rights Reserved.
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
