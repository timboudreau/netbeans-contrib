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
 * The Original Software is Marco Hunsicker. The Initial Developer of the Original
 * Software is Marco Hunsicker. All rights reserved.
 *
 * Copyright (c) 2002 Marco Hunsicker
 */
package de.hunsicker.jalopy.plugin.netbeans;

import de.hunsicker.jalopy.plugin.Editor;
import de.hunsicker.jalopy.plugin.ProjectFile;

import org.openide.text.Line;
import org.openide.text.NbDocument;

import java.util.Collections;
import java.util.List;

import javax.swing.JEditorPane;
import javax.swing.text.StyledDocument;


/**
 * The NetBeans Editor implementation.
 *
 * @author <a href="http://jalopy.sf.net/contact.html">Marco Hunsicker</a>
 * @author Frank-Michael Moser
 */
final class NbEditor implements Editor {

	/** The editor view.*/
	JEditorPane pane;

	/** The underlying Java source file. */
	ProjectFile file;

	/** Provides per-line access to the editor contents. */
	Line.Set lines;

	/** The editor document. */
	StyledDocument document;

	/**
	 * Creates new NbEditor object.
	 *
	 * @param file the underlying Java source file.
	 * @param pane the physical editor pane.
	 * @param lines set with the actual editor lines.
	 */
	public NbEditor(ProjectFile file, JEditorPane pane, Line.Set lines) {
		this.file = file;
		this.pane = pane;
		this.lines = lines;
		this.document = (StyledDocument) pane.getDocument();
	}

	/**
	 * {@inheritDoc}
	 */
	public void setCaretPosition(int offset) {
		this.pane.setCaretPosition(offset);
	}

	/**
	 * {@inheritDoc}
	 */
	public void setCaretPosition(int line, int column) {
		try {

			int offset =
				(NbDocument.findLineOffset(this.document, line - 1) + column) -
				1;

			setCaretPosition(offset);
		}
		catch (Throwable ex) {
			;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public int getCaretPosition() {
		return this.pane.getCaretPosition();
	}

	/**
	 * {@inheritDoc}
	 */
	public int getColumn() {
		try {
			return NbDocument.findLineColumn(this.document, getCaretPosition()) +
			1;
		}
		catch (Throwable ex) {
			return 1;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public ProjectFile getFile() {
		return this.file;
	}

	/**
	 * {@inheritDoc}
	 */
	public int getLength() {
		return this.document.getLength();
	}

	/**
	 * {@inheritDoc}
	 */
	public int getLine() {
		try {
			return NbDocument.findLineNumber(this.document, getCaretPosition()) +
			1;
		}
		catch (Throwable ex) {
			return 1;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public int getLineCount() {
		return this.lines.getLines().size();
	}

	/**
	 * {@inheritDoc}
	 */
	public String getSelectedText() {
		return this.pane.getSelectedText();
	}

	/**
	 * {@inheritDoc}
	 */
	public void setSelection(int startOffset, int endOffset) {
		this.pane.setSelectionStart(startOffset);
		this.pane.setSelectionEnd(endOffset);
	}

	/**
	 * {@inheritDoc}
	 */
	public int getSelectionEnd() {
		return this.pane.getSelectionEnd();
	}

	/**
	 * {@inheritDoc}
	 */
	public int getSelectionStart() {
		return this.pane.getSelectionStart();
	}

	/**
	 * {@inheritDoc}
	 */
	public void setText(final String text) {
		this.pane.setText(text);
	}

	/**
	 * {@inheritDoc}
	 */
	public String getText() {
		return this.pane.getText();
	}

	/**
	 * {@inheritDoc}
	 */
	public void attachAnnotations(List annotations) {
	}

	/**
	 * {@inheritDoc}
	 */
	public List detachAnnotations() {
		return Collections.EMPTY_LIST;
	}

	/**
	 * {@inheritDoc}
	 */
	public void paste(String text) {
		this.pane.replaceSelection(text);
	}

	/**
	 * {@inheritDoc}
	 */
	public void requestFocus() {
		this.pane.requestFocus();
	}

	/**
	 * {@inheritDoc}
	 */
	public void selectAll() {
		this.pane.selectAll();
	}
}
