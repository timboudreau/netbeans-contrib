/*
 * ParameterScanner.java
 *
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
 *
 * Contributor(s): Thomas Ball
 *
 * Version: $Revision$
 */

package org.netbeans.modules.metrics;

import org.netbeans.modules.classfile.*;

/**
 * Scans bytecodes for class name references.
 *
 * @author  tball
 * @version
 */
class ParameterScanner extends Scanner implements ByteCodes {

    ParameterScanner(Method m) {
        super(m);
    }

    /**
     * Scan byte codes for unreferenced parameters.
     **/
    MethodMetrics.Parameter[] scan(MethodMetrics.Parameter[] params) {
        if (params.length == 0) {
            return new MethodMetrics.Parameter[0];
        }

        offset = 0;
        int max = codeBytes.length;
	while (offset < max) {
	    int bcode = at(0);
	    if (bcode == bc_wide) {
		bcode = at(1);
		int arg = shortAt(2);
		switch (bcode) {
		    case bc_aload: case bc_astore:
		    case bc_fload: case bc_fstore:
		    case bc_iload: case bc_istore:
		    case bc_lload: case bc_lstore:
		    case bc_dload: case bc_dstore:
		    case bc_ret:
			offset += 4;
			break;

		    case bc_iinc:
			offset += 6;
			break;
		    default:
			offset++;
			break;
		}
	    } else {
		switch (bcode) {
                  case bc_aload: case bc_fload:
                  case bc_iload: case bc_lload: 
                  case bc_dload: {
                      markReferenced(params, at(1));
                      offset += 2;
                      break;
                  }

                  case bc_iload_0: case bc_lload_0:
                  case bc_fload_0: case bc_dload_0:
                  case bc_aload_0:
                      markReferenced(params, 0);
                      offset++;
                      break;

                  case bc_iload_1: case bc_lload_1:
                  case bc_fload_1: case bc_dload_1:
                  case bc_aload_1:
                      markReferenced(params, 1);
                      offset++;
                      break;

                  case bc_iload_2: case bc_lload_2:
                  case bc_fload_2: case bc_dload_2:
                  case bc_aload_2:
                      markReferenced(params, 2);
                      offset++;
                      break;

                  case bc_iload_3: case bc_lload_3:
                  case bc_fload_3: case bc_dload_3:
                  case bc_aload_3:
                      markReferenced(params, 3);
                      offset++;
                      break;

                  // ignore other opcodes
		    case bc_astore: case bc_fstore:
		    case bc_istore: case bc_lstore:
		    case bc_dstore: case bc_ret: case bc_newarray:
		    case bc_bipush: case bc_ldc:
			offset += 2;
			break;
		    
                    case bc_iinc: case bc_instanceof: 
                    case bc_checkcast: case bc_new:
		    case bc_putstatic: case bc_getstatic:
                    case bc_putfield: case bc_getfield:
		    case bc_invokevirtual: case bc_invokespecial:
                    case bc_invokestatic:
		    case bc_ifeq: case bc_ifge: case bc_ifgt:
		    case bc_ifle: case bc_iflt: case bc_ifne:
		    case bc_if_icmpeq: case bc_if_icmpne: case bc_if_icmpge:
		    case bc_if_icmpgt: case bc_if_icmple: case bc_if_icmplt:
		    case bc_if_acmpeq: case bc_if_acmpne:
		    case bc_ifnull: case bc_ifnonnull:
		    case bc_anewarray: case bc_sipush:
		    case bc_ldc_w: case bc_ldc2_w:
		    case bc_jsr: case bc_goto:
			offset += 3;
			break;

		    case bc_multianewarray:
			offset += 4;
			break;

		    case bc_jsr_w: case bc_goto_w:
		    case bc_invokeinterface:
			offset += 5;
			break;

		    case bc_tableswitch:{
			int tbl = (offset+1+3) & (~3);	// four byte boundry
			long low = intAt(tbl, 1);
			long high = intAt(tbl, 2);
			tbl += 3 << 2; 			// three int header
			offset = tbl + (int)((high - low + 1) << 2);
			break;
		    }

		    case bc_lookupswitch:{
			int tbl = (offset+1+3) & (~3);	// four byte boundry
			int npairs = (int)intAt(tbl, 1);
			int nints = npairs * 2;
			tbl += 2 << 2; 			// two int header
			offset = tbl + (nints << 2);
			break;
		    }

		    default:
			offset++;
			break;
		}
	    }
	}

        // Create list of tramps
        int n = 0;
        for (int i = 0; i < params.length; i++)
            if (!params[i].referenced)
                n++;
        MethodMetrics.Parameter[] tramps = new MethodMetrics.Parameter[n];
        if (n > 0) {
            n = 0;
            for (int i = 0; i < params.length; i++)
                if (!params[i].referenced)
                    tramps[n++] = params[i];
        }
        return tramps;
    }

    static void markReferenced(MethodMetrics.Parameter[] params, int index) {
        int n = params.length;
        for (int i = 0; i < n; i++) {
            MethodMetrics.Parameter p = params[i];
            if (p.getStackIndex() == index) {
                p.setReferenced(true);
                break;
            }
        }
    }
}
