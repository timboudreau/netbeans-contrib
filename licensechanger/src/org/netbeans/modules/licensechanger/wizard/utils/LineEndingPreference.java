/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2009 Sun Microsystems, Inc. All rights reserved.
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
 * Portions Copyrighted 2009 Sun Microsystems, Inc.
 */
package org.netbeans.modules.licensechanger.wizard.utils;

/**
 * User preference for line ending conversion
 *
 * @author Tim Boudreau
 */
public enum LineEndingPreference {

	FORCE_CRLF,
	FORCE_CR,
	NO_CHANGE,
	FORCE_NEWLINE,
	SYSTEM_DEFAULT;

	public static String convertLineEndings(LineEndingPreference pref, String old, String nue) {
		boolean oldHasCrlf = old.contains("\r\n"); //NOI18N
		boolean oldHasCr = old.contains("\r"); //NOI18N
		boolean oldHasLf = old.contains("\n"); //NOI18N
		String tmp = nue;
		switch (pref) {
			case FORCE_CRLF:
				if (oldHasCr && !(oldHasCrlf)) {
					tmp = tmp.replaceAll("\r", "\n"); //NOI18N
				} else if (oldHasCr && oldHasCrlf) {
					//tmp = tmp.replaceAll("\r\n", "\n"); //NOI18N
				}
				return tmp.replaceAll("\n", "\r\n"); //NOI18N
			case FORCE_CR:
				if (oldHasCr && !(oldHasCrlf)) {
					tmp = tmp.replaceAll("\r", "\n"); //NOI18N
				} else if (oldHasCr && oldHasCrlf) {
					tmp = tmp.replaceAll("\r\n", "\n"); //NOI18N
					//tmp = tmp.replaceAll("\r", "\n"); //NOI18N
				}
				return tmp.replaceAll("\n", "\r"); //NOI18N
			case FORCE_NEWLINE:
				if (oldHasCr && !(oldHasCrlf)) {
					tmp = tmp.replaceAll("\r", "\n"); //NOI18N
				} else if (oldHasCr && oldHasCrlf) {
					tmp = tmp.replaceAll("\r\n", "\n"); //NOI18N
					tmp = tmp.replaceAll("\r", "\n"); //NOI18N
				}
				return tmp;
			case NO_CHANGE:
				// license headers all have \n, so if the file string contains
				// only cr, replace \n with \r
				if (oldHasCr && !(oldHasCrlf)) {
					return tmp.replaceAll("\n", "\r"); //NOI18N
					// if it contains \r\n (cr,lf), replace \n with \r\n, after
					// homogenizing \r\n to \n to avoid substring replacements
				} else if (oldHasCr && oldHasCrlf) {
					tmp = tmp.replaceAll("\r\n", "\n"); //NOI18N
					tmp = tmp.replaceAll("\r", "\n"); //NOI18N
					return tmp.replaceAll("\n", "\r\n"); //NOI18N
				} else {
					return tmp;
				}
			case SYSTEM_DEFAULT:
				String sep = System.getProperty("line.separator"); //NOI18N
				if (oldHasCr && !(oldHasCrlf)) {
					tmp = tmp.replaceAll("\r", "\n"); //NOI18N
				} else if (oldHasCr && oldHasCrlf) {
					tmp = tmp.replaceAll("\r\n", "\n"); //NOI18N
					tmp = tmp.replaceAll("\r", "\n"); //NOI18N
				}
				return tmp.replaceAll("\n", sep); //NOI18N
			default:
				throw new AssertionError();
		}
	}

}
