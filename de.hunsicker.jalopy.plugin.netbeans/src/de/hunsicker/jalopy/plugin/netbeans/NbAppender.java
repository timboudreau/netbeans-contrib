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

import de.hunsicker.jalopy.plugin.AbstractAppender;

import org.apache.log4j.Level;
import org.apache.log4j.Priority;
import org.apache.log4j.spi.LoggingEvent;
import org.openide.cookies.LineCookie;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.text.Annotatable;
import org.openide.text.Annotation;
import org.openide.text.Line;
import org.openide.windows.IOProvider;
import org.openide.windows.InputOutput;
import org.openide.windows.OutputListener;
import org.openide.windows.OutputWriter;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import java.io.File;

import java.util.StringTokenizer;
import java.util.regex.Matcher;


/**
 * Appender which displays messages in a NetBeans output window.
 *
 * @author <a href="http://jalopy.sf.net/contact.html">Marco Hunsicker</a>
 * @author Frank-Michael Moser
 */
final class NbAppender extends AbstractAppender {
	private static final String TYPE_ERROR = "error";
	private static final String TYPE_WARN = "warn";
	private static final String INDENT = "        ";
	private InputOutput _sink;
	private OutputWriter _errors;
	private OutputWriter _infos;

	/**
	 * Creates a new NbAppender object.
	 */
	public NbAppender() {
		_sink = IOProvider.getDefault().getIO("Jalopy", false);
		_infos = _sink.getOut();
		_errors = _sink.getErr();
		_sink.setErrSeparated(false);
		_sink.setFocusTaken(false);
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param ev DOCUMENT ME!
	 */
	public void append(LoggingEvent ev) {
		// check for Emacs-style messages
		Matcher result = parseMessage(ev);

		// parsing failed, so we issue a standard-message
		if (result == null) {
			_infos.println(this.layout.format(ev));

			// we're done
			return;
		}

		String filename = result.group(POS_FILENAME);
		int lineno = 0;

		try {
			lineno = Integer.parseInt(result.group(POS_LINE));
		}
		catch (NumberFormatException neverOccurs) {
			;
		}

		String text = result.group(POS_TEXT);

		switch (ev.getLevel().toInt()) {
			case Level.WARN_INT:
			case Level.FATAL_INT:
			case Level.ERROR_INT:
				outputMessage(_errors, ev.getLevel(), filename, lineno, text);
				break;
			default:
				outputMessage(_infos, ev.getLevel(), filename, lineno, text);
				break;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void clear() {
		try {
			_infos.reset();
			_errors.reset();
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private String formatFilename(String filename, int lineno) {
		StringBuffer buf = new StringBuffer(100);

		// first the filename and location
		buf.append(filename);

		if (lineno > 0) {
			buf.append('[');
			buf.append(lineno);
			buf.append(']');
		}

		buf.append(':');

		return buf.toString();
	}

	private String formatMessage(String text) {
		StringBuffer buf = new StringBuffer(text.length());

		// is this a multiline message?
		if (text.indexOf('\n') > -1) {
			// then the message first
			StringTokenizer tokens = new StringTokenizer(text, "\n"
				/* NOI18N */ );

			buf.append(INDENT
			/* NOI18N */ );
			buf.append(tokens.nextToken());
			buf.append('\n');

			// and the stack trace after
			while (tokens.hasMoreElements()) {
				String line = tokens.nextToken();

				if ((line != null) && line.trim().startsWith("at"
						/* NOI18N */ )) {
					buf.append(line);
					buf.append('\n');
				}
			}

			buf.deleteCharAt(buf.length() - 1); // remove the last '\n'
		}
		else {
			buf.append(INDENT
			/* NOI18N */ );
			buf.append(text);
		}

		return buf.toString();
	}

	private void outputMessage(
		OutputWriter out, Priority level, String filename, int lineno,
		String text) {
		try {
			if (lineno == 0) {
				switch (level.toInt()) {
					case Level.WARN_INT:
						out.print("[WARN] "
						/* NOI18N */ );
						out.println(formatFilename(filename, 0));
						out.println(INDENT
						/* NOI18N */  + text);
						out.flush();

						break;
					case Level.FATAL_INT:
					case Level.ERROR_INT:
						out.print("[ERROR] "
						/* NOI18N */ );
						out.println(formatFilename(filename, 0));
						out.println(INDENT
						/* NOI18N */  + text);
						out.flush();

						break;
					case Level.DEBUG_INT:
						out.print("[DEBUG] "
						/* NOI18N */ );
						out.println(formatFilename(filename, 0));
						out.println(INDENT
						/* NOI18N */  + text);
						out.flush();

						break;
					case Level.INFO_INT:default:
						out.print("[INFO] "
						/* NOI18N */ );
						out.println(formatFilename(filename, 0));
						out.println(INDENT
						/* NOI18N */  + text);
						out.flush();

						break;
				}
			}
			else {
				switch (level.toInt()) {
					case Level.WARN_INT:
						out.print("[WARN] "
						/* NOI18N */ );
						out.println(
							formatFilename(filename, lineno),
							new OutputHandler(
								TYPE_WARN, filename, lineno, text));
						out.println(INDENT
						/* NOI18N */  + text);
						out.flush();

						break;
					case Level.FATAL_INT:
					case Level.ERROR_INT:
						out.print("[ERROR] "
						/* NOI18N */ );
						int linebreakPos = text.indexOf('\n'); // multiline message?

						out.println(
							formatFilename(filename, lineno),
							new OutputHandler(
								TYPE_ERROR, filename, lineno,
								(linebreakPos > -1)
								? text.substring(0, linebreakPos) : text));
						out.println(formatMessage(text));
						out.flush();

						break;
					case Level.INFO_INT:default:
						out.print("[INFO] "
						/* NOI18N */ );
						out.println(formatFilename(filename, 0));
						out.println(INDENT
						/* NOI18N */  + text);
						out.flush();

						break;
				}
			}
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private static class LineAnnotation extends Annotation {
		final String message;
		final String type;

		public LineAnnotation(String message, String type) {
			this.message = message;
			this.type = "de-hunsicker-jalopy-plugin-netbeans-"
				/* NOI18N */  + type;
		}

		public String getAnnotationType() {
			return this.type;
		}

		public String getShortDescription() {
			return this.message;
		}
	}


	/**
	 * The click-handler which marks lines for errors and warnings.
	 */
	private static class OutputHandler implements OutputListener,
		PropertyChangeListener {
		Annotation ann;
		String filename;
		String text;
		String type;
		int lineno;

		public OutputHandler(
			String type, String filename, int lineno, String text) {
			this.type = type;
			this.filename = filename;
			this.lineno = lineno;
			this.text = text;
		}

		public void outputLineAction(org.openide.windows.OutputEvent ev) {
			// don't add the same annotation twice
			if (this.ann == null) {
				FileObject file =
					FileUtil.toFileObject(new File(this.filename));

				try {
					DataObject objWithError = DataObject.find(file);
					LineCookie cookie =
						(LineCookie) objWithError.getCookie(LineCookie.class);
					Line.Set lineSet = cookie.getLineSet();
					final Line line = lineSet.getOriginal(this.lineno - 1);

					this.ann = new LineAnnotation(this.text, this.type);
					line.addPropertyChangeListener(this);
					this.ann.attach(line);
					this.ann.moveToFront();
					line.show(Line.SHOW_GOTO); // open the editor if necessary
				}
				catch (DataObjectNotFoundException ex) {
					ex.printStackTrace();
				}
			}
		}

		public void outputLineCleared(org.openide.windows.OutputEvent ev) {
			detach();
		}

		public void outputLineSelected(org.openide.windows.OutputEvent ev) {
		}

		public void propertyChange(PropertyChangeEvent ev) {
			if (this.ann != null) {
				String property = ev.getPropertyName();

				if (
					(property == null) ||
							property.equals(Annotatable.PROP_TEXT) ||
							property.equals(Annotatable.PROP_DELETED)) {
					detach();
				}
			}
		}

		private void detach() {
			if (this.ann != null) {
				((Annotatable) this.ann.getAttachedAnnotatable()).removePropertyChangeListener(
					this);
				this.ann.detach();
				this.ann = null;
			}
		}
	}

	/**
	 * Returns the standard output writer of the I/O connection to the Jalopy
	 * output tab
	 *
	 * @return the standard output writer of the I/O connection
	 */
	public OutputWriter getOut() {
		return _infos;
	}

	/**
	 * Returns the error output writer of the I/O connection to the Jalopy
	 * output tab
	 *
	 * @return the error output writer of the I/O connection
	 */
	public OutputWriter getErr() {
		return _errors;
	}

	/**
	 * Returns the I/O connection to the Jalopy output tab
	 *
	 * @return the I/O connection to the Jalopy output tab
	 */
	public InputOutput getIO() {
		return _sink;
	}
}
