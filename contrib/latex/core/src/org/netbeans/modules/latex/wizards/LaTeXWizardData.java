/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at http://www.netbeans.org/cddl.html
 * or http://www.netbeans.org/cddl.txt.
 *
 * When distributing Covered Code, include this CDDL Header Notice in each file
 * and include the License file at http://www.netbeans.org/cddl.txt.
 * If applicable, add the following below the CDDL Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * The Original Software is the LaTeX module.
 * The Initial Developer of the Original Software is Jan Lahoda.
 * Portions created by Jan Lahoda_ are Copyright (C) 2002,2003.
 * All Rights Reserved.
 *
 * Contributor(s): Jan Lahoda.
 */
package org.netbeans.modules.latex.wizards;

/**
 *
 * @author Jan Lahoda
 */
public class LaTeXWizardData {

    private String   docclass;
    private String   fontSize;
    private String   paperSize;
    private String   inputEnc;
    private String   author;
    private String   title;
    private String[] options;

    /** Creates a new instance of LaTeXWizardData */
    public LaTeXWizardData() {
    }

    /** Getter for property author.
     * @return Value of property author.
     *
     */
    public java.lang.String getAuthor() {
        return author;
    }    
    
    /** Setter for property author.
     * @param author New value of property author.
     *
     */
    public void setAuthor(java.lang.String author) {
        this.author = author;
    }    
    
    /** Getter for property docclass.
     * @return Value of property docclass.
     *
     */
    public java.lang.String getDocclass() {
        return docclass;
    }
    
    /** Setter for property docclass.
     * @param docclass New value of property docclass.
     *
     */
    public void setDocclass(java.lang.String docclass) {
        this.docclass = docclass;
    }
    
    /** Getter for property fontSize.
     * @return Value of property fontSize.
     *
     */
    public java.lang.String getFontSize() {
        return fontSize;
    }
    
    /** Setter for property fontSize.
     * @param fontSize New value of property fontSize.
     *
     */
    public void setFontSize(java.lang.String fontSize) {
        this.fontSize = fontSize;
    }
    
    /** Getter for property inputEnc.
     * @return Value of property inputEnc.
     *
     */
    public java.lang.String getInputEnc() {
        return inputEnc;
    }
    
    /** Setter for property inputEnc.
     * @param inputEnc New value of property inputEnc.
     *
     */
    public void setInputEnc(java.lang.String inputEnc) {
        this.inputEnc = inputEnc;
    }
    
    /** Getter for property options.
     * @return Value of property options.
     *
     */
    public java.lang.String[] getOptions() {
        return this.options;
    }
    
    /** Setter for property options.
     * @param options New value of property options.
     *
     */
    public void setOptions(java.lang.String[] options) {
        this.options = options;
    }
    
    /** Getter for property paperSize.
     * @return Value of property paperSize.
     *
     */
    public java.lang.String getPaperSize() {
        return paperSize;
    }
    
    /** Setter for property paperSize.
     * @param paperSize New value of property paperSize.
     *
     */
    public void setPaperSize(java.lang.String paperSize) {
        this.paperSize = paperSize;
    }
    
    /** Getter for property title.
     * @return Value of property title.
     *
     */
    public java.lang.String getTitle() {
        return title;
    }
    
    /** Setter for property title.
     * @param title New value of property title.
     *
     */
    public void setTitle(java.lang.String title) {
        this.title = title;
    }
    
}
