/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2004 Sun
 * Microsystems, Inc. All Rights Reserved.
 */
/*
 * Util.java
 *
 * Created on April 28, 2004, 12:13 AM
 */

package org.netbeans.modules.hexedit;

import org.netbeans.modules.hexedit.HexTableModel;

import java.util.*;
import java.lang.reflect.Method;
import java.text.MessageFormat;

/**
 * A few utility methods
 *
 * @author  Tim Boudreau
 */
class Util {
    private Util() {}
    private static final String PARTIAL_STRING = "---"; //NOI18N

    public static String convertToString (Object obj) {
        if (obj == null) {
            return "";
        }
        if (obj instanceof Character) {
            return ((Character) obj).toString();
        }
        if (obj == HexTableModel.PARTIAL_VALUE) {
            return PARTIAL_STRING;
        }

        String converted;
        if (obj instanceof Number && (!(obj instanceof Long))) {
            if (obj instanceof Byte) {
                converted = Integer.toHexString (((Number) obj).byteValue());
            } else if (obj instanceof Short) {
                converted = Integer.toHexString (((Number) obj).shortValue());
            } else {
                converted = Integer.toHexString( ((Number) obj).intValue() );
            }
        } else if (obj instanceof Long) {
            converted = Long.toHexString(((Long) obj).longValue());
        } else {
            converted = obj instanceof String ? (String) obj : obj.toString();
        }
        int targetLength = targetLengthFor (obj.getClass());

        if (converted.length() < targetLength) {
            char[] c = new char[targetLength - converted.length()];
            Arrays.fill (c, '0');
            StringBuffer sb = new StringBuffer ();
            sb.append (c);
            sb.append (converted);
            converted = sb.toString();
        } else if (converted.length() > targetLength) {
            converted = converted.substring (converted.length() - targetLength);
        }
        return converted;
    }
    
    public static int targetLengthFor (Class c) {
        int targetLength;
        if (c == Long.class) {
            targetLength = 16;
        } else if (c == Integer.class) {
            targetLength = 8;
        } else if (c == Short.class) {
            targetLength = 4;
        } else if (c == Byte.class || c == Character.class) {
            targetLength = 2;
        } else {
            targetLength = 0; //??
        }
        return targetLength;
    }
    
    public static int byteCountFor (Class c) {
        int targetLength;
        if (c == Long.class) {
            targetLength = 8;
        } else if (c == Integer.class) {
            targetLength = 4;
        } else if (c == Short.class) {
            targetLength = 2;
        } else if (c == Byte.class || c == Character.class) {
            targetLength = 1;
        } else {
            throw new IllegalArgumentException (c.getName());
        }
        return targetLength;
    }
    
    public static Object convertFromString (String s, Class c) {
        if (s == PARTIAL_STRING) {
            return null;
        }
        if (c == Character.class) {
            if (s.length() != 1) {
                throw new IllegalArgumentException (getMessage("MSG_TOO_MANY"));
            }
            return new Character(s.charAt(0));
        }
        
        s = s.toUpperCase().trim();
        
        int len = targetLengthFor (c);
        if (s.length() > len) {
            if (s.length() == len+1 && s.indexOf('-') > 0) {
                //negative sign is legal
            } else {
                throw new IllegalArgumentException (formatMessage("MSG_TOO_MANY_FOR_TYPE",
                        new Object[] {new Integer(len), c}));
            }
        }
        
        
        char[] chars = s.toCharArray();
        Set set = new HashSet (Arrays.asList(legalChars));
        for (int i=0; i < chars.length; i++) {
            if (!set.contains(new Character(chars[i]))) {
                throw new IllegalArgumentException (formatMessage("MSG_BAD_CHARS",
                        new Object[] { new Character(chars[i])}));
            }
        }


        if (c == Long.class) {
            return new Long (Long.parseLong(s, 16));
        } else if (c == Integer.class) {
            return new Integer (Integer.parseInt(s, 16));
        } else if (c == Short.class) {
            return new Short (Short.parseShort(s, 16));
        } else if (c == Byte.class) {
            return new Byte (Byte.parseByte(s, 16));
        }
        return null;
    }

    static Character[] legalChars = new Character[] {
        new Character('-'), //NOI18N
        new Character('0'), //NOI18N
        new Character('1'), //NOI18N
        new Character('2'), //NOI18N
        new Character('3'), //NOI18N
        new Character('4'), //NOI18N
        new Character('5'), //NOI18N
        new Character('6'), //NOI18N
        new Character('7'), //NOI18N
        new Character('8'), //NOI18N
        new Character('9'), //NOI18N
        new Character('A'), //NOI18N
        new Character('B'), //NOI18N
        new Character('C'), //NOI18N
        new Character('D'), //NOI18N
        new Character('E'), //NOI18N
        new Character('F'), //NOI18N
    };

    public static String getMessage (String key) {
        if (inNetBeans()) {
            return getWithNbBundle(key);
        }
        try {
            return ResourceBundle.getBundle("org.netbeans.modules.hexedit.Bundle", Locale.getDefault()).getString(key); //NOI18N
        } catch (MissingResourceException mre) {
            mre.printStackTrace();
            try {
                return ResourceBundle.getBundle("org.netbeans.modules.hexedit.Bundle", Locale.US).getString(key); //NOI18N
            } catch (MissingResourceException mre2) {
                mre2.printStackTrace();
                return key;
            }
        }
    }

    public static String formatMessage (String key, Object[] params) {
        String fmt = getMessage (key);
        return MessageFormat.format(fmt, params);
    }

    static Class NBBUNDLE = null;
    static Boolean nb = null;
    private static boolean inNetBeans() {
        if (nb == null) {
            try {
                NBBUNDLE = Class.forName ("org.openide.util.NbBundle");  //NOI18N
                nb = Boolean.TRUE;
            } catch (Exception e) {
                nb = Boolean.FALSE;
            }
        }
        return nb.booleanValue();
    }

    private static Method getMessage = null;
    private static String getWithNbBundle (String key) {
        if (getMessage == null) {
            try {
                getMessage = NBBUNDLE.getDeclaredMethod("getMessage", new Class[] {String.class});
            } catch (Exception e) {
                e.printStackTrace(); //XXX for testing
                nb = Boolean.FALSE;
                return key;
            }
        }
        try {
            return (String) getMessage.invoke(null, new Object[] {key});
        } catch (Exception e) {
            e.printStackTrace(); //XXX for testing
            return key;
        }
    }
}
