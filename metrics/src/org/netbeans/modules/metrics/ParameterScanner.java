/*
 * ParameterScanner.java
 *
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2002 Sun
 * Microsystems, Inc. All Rights Reserved.
 *
 * Contributor(s): Thomas Ball
 *
 * Version: $Revision$
 */

package org.netbeans.modules.metrics;

import org.netbeans.modules.classfile.*;
import java.util.*;

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
