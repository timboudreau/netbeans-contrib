/*
 * DependencyScanner.java
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
class ReferenceScanner extends Scanner implements ByteCodes {
    
    private Collection classRefs = null;
    private Collection methodRefs = null;
    private int codePaths;
    private int messageSends;

    private ConstantPool cpool;
    private ClassName thisName;
    
    ReferenceScanner(Method m) {
        super(m);
        ClassFile cfile = m.getClassFile();
        cpool = cfile.getConstantPool();
        thisName = cfile.getName();
    }
    
    Iterator getClassReferences() {
        scan();
        return classRefs.iterator();
    }
    
    Iterator getMethodReferences() {
        scan();
        return methodRefs.iterator();
    }

    int getMethodReferencesCount() {
	scan();
	return methodRefs.size();
    }
    
    int getCodePathCount() {
        scan();
        return codePaths;
    }

    int getMessageSendCount() {
        scan();
        return messageSends;
    }

    /**
     * Scan byte codes for class and method references.
     **/
    private void scan() {
        if (classRefs != null)
            return;
        // Store ids rather than names to avoid multiple name building.
        Set classIDs = new HashSet();
        Set methodIDs = new HashSet();

        codePaths = 1;        // there has to be at least one...

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
                    // These bcodes have CONSTANT_Class arguments
                    case bc_instanceof: 
                    case bc_checkcast: case bc_new:
                    {
			int index = shortAt(1);
                        classIDs.add(new Integer(index));
			offset += 3;
			break;
		    }

		    case bc_putstatic: case bc_getstatic:
                    case bc_putfield: case bc_getfield: {
			int index = shortAt(1);
                        CPFieldInfo fi = (CPFieldInfo)cpool.get(index);
                        classIDs.add(new Integer(fi.getClassID()));
			offset += 3;
			break;
                    }

                    // These bcodes have CONSTANT_MethodRef_info arguments
		    case bc_invokevirtual: case bc_invokespecial:
                    case bc_invokestatic:
                        methodIDs.add(new Integer(shortAt(1)));
                        messageSends++;
			offset += 3;
			break;

		    case bc_jsr_w:
		    case bc_invokeinterface:
                        methodIDs.add(new Integer(shortAt(1)));
                        messageSends++;
			offset += 5;
			break;

                    // Branch instructions
		    case bc_ifeq: case bc_ifge: case bc_ifgt:
		    case bc_ifle: case bc_iflt: case bc_ifne:
		    case bc_if_icmpeq: case bc_if_icmpne: case bc_if_icmpge:
		    case bc_if_icmpgt: case bc_if_icmple: case bc_if_icmplt:
		    case bc_if_acmpeq: case bc_if_acmpne:
		    case bc_ifnull: case bc_ifnonnull:
		    case bc_jsr:
                        codePaths++;
			offset += 3;
			break;

                    case bc_lcmp: case bc_fcmpl: case bc_fcmpg:
                    case bc_dcmpl: case bc_dcmpg:
                        codePaths++;
			offset++;
                        break;

		    case bc_tableswitch:{
			int tbl = (offset+1+3) & (~3);	// four byte boundry
			long low = intAt(tbl, 1);
			long high = intAt(tbl, 2);
			tbl += 3 << 2; 			// three int header

                        // Find number of unique table addresses.
                        // The default path is skipped so we find the
                        // number of alternative paths here.
                        BitSet bitset = new BitSet();
                        int length = (int)(high - low + 1);
                        for (int i = 0; i < length; i++) {
                            int jumpAddr = (int)intAt (tbl, i) + offset;
                            bitset.set( jumpAddr );
                        }
                        length = bitset.length();
                        for (int i = 0; i < length; i++) {
                            if ( bitset.get(i) ) {
                                codePaths++;
                            }
                        }

			offset = tbl + (int)((high - low + 1) << 2);
			break;
		    }

		    case bc_lookupswitch:{
			int tbl = (offset+1+3) & (~3);	// four byte boundry
			int npairs = (int)intAt(tbl, 1);
			int nints = npairs * 2;
			tbl += 2 << 2; 			// two int header

                        // Find number of unique table addresses
                        BitSet bitset = new BitSet();
                        for (int i = 0; i < nints; i += 2) {
                            // use the address half of each pair
                            int jumpAddr = (int)intAt (tbl, i + 1) + offset;
                            bitset.set( jumpAddr );
                        }
                        int length = bitset.length();
                        for (int i = 0; i < length; i++) {
                            if ( bitset.get(i) ) {
                                codePaths++;
                            }
                        }
                        
			offset = tbl + (nints << 2);
			break;
		    }

                    // Ignore other bcodes.
		    case bc_anewarray: 
                        offset += 3;
                        break;

		    case bc_multianewarray: {
			offset += 4;
			break;
		    }

		    case bc_aload: case bc_astore:
		    case bc_fload: case bc_fstore:
		    case bc_iload: case bc_istore:
		    case bc_lload: case bc_lstore:
		    case bc_dload: case bc_dstore:
		    case bc_ret: case bc_newarray:
		    case bc_bipush: case bc_ldc:
			offset += 2;
			break;
		    
		    case bc_iinc: case bc_sipush:
		    case bc_ldc_w: case bc_ldc2_w:
		    case bc_goto:
			offset += 3;
			break;

		    case bc_goto_w:
			offset += 5;
			break;

		    default:
			offset++;
			break;
		}
	    }
	}
        classRefs = expandClassNames(classIDs);
        methodRefs = expandMethodNames(methodIDs);
    }

    private Collection expandClassNames(Set ids) {
        List names = new ArrayList(ids.size());
        Iterator iter = ids.iterator();
        while (iter.hasNext()) {
            int id = ((Integer)iter.next()).intValue();
            ClassName cn = ClassName.getClassName(cpool.getClass(id).getName());
            if (!cn.equals(thisName))
                names.add(cn);
        }
        return names;
    }
    
    private Collection expandMethodNames(Set ids) {
        List names = new ArrayList(ids.size());
        Iterator iter = ids.iterator();
        while (iter.hasNext()) {
            StringBuffer sb = new StringBuffer();

            // build a "<class>.<method>(<param list>)" string
            int id = ((Integer)iter.next()).intValue();
            CPMethodInfo mi = (CPMethodInfo)cpool.get(id);
            ClassName cname = mi.getClassName();
            if (cname.equals(thisName))
                continue;  // ignore self references

            sb.append(cname.getSimpleName());
            sb.append('.');

            // Create a full method name without the return type.
            String mn = mi.getFullMethodName();
            int i = mn.indexOf(' ');
            if (i != -1)
                mn = mn.substring(i+1);
            sb.append(mn);

            names.add(sb.toString());
        }
        return names;
    }
}
