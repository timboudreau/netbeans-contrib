/*
 *                          Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License Version
 * 1.0 (the "License"). You may not use this file except in compliance with
 * the License. A copy of the License is available at http://www.sun.com/
 *
 * The Original Code is the LaTeX module.
 * The Initial Developer of the Original Code is Jan Lahoda.
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
