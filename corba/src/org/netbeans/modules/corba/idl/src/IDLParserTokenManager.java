/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2001 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

//package com.netbeans.enterprise.modules.corba.idl.parser;
package com.netbeans.enterprise.modules.corba.idl.src;
import java.util.Vector;

public class IDLParserTokenManager implements IDLParserConstants
{
private final int jjStopStringLiteralDfa_0(int pos, long active0, long active1)
{
   switch (pos)
   {
      case 0:
         if ((active0 & 0x8000000L) != 0L)
            return 46;
         if ((active0 & 0xfe4fffff00022400L) != 0L || (active1 & 0x7L) != 0L)
         {
            jjmatchedKind = 67;
            return 1;
         }
         return -1;
      case 1:
         if ((active0 & 0xbe4fffff00020400L) != 0L || (active1 & 0x6L) != 0L)
         {
            if (jjmatchedPos != 1)
            {
               jjmatchedKind = 67;
               jjmatchedPos = 1;
            }
            return 1;
         }
         if ((active0 & 0x4000000000002000L) != 0L || (active1 & 0x1L) != 0L)
            return 1;
         return -1;
      case 2:
         if ((active0 & 0x3e4ff7ff00022400L) != 0L || (active1 & 0x7L) != 0L)
         {
            jjmatchedKind = 67;
            jjmatchedPos = 2;
            return 1;
         }
         if ((active0 & 0x8000080000000000L) != 0L)
            return 1;
         return -1;
      case 3:
         if ((active0 & 0x2005012100000000L) != 0L)
            return 1;
         if ((active0 & 0x1e4af6de00022400L) != 0L || (active1 & 0x7L) != 0L)
         {
            jjmatchedKind = 67;
            jjmatchedPos = 3;
            return 1;
         }
         return -1;
      case 4:
         if ((active0 & 0x444a00020000L) != 0L || (active1 & 0x1L) != 0L)
            return 1;
         if ((active0 & 0x1e4ab29400002400L) != 0L || (active1 & 0x6L) != 0L)
         {
            jjmatchedKind = 67;
            jjmatchedPos = 4;
            return 1;
         }
         return -1;
      case 5:
         if ((active0 & 0xe0a028400002000L) != 0L || (active1 & 0x4L) != 0L)
         {
            jjmatchedKind = 67;
            jjmatchedPos = 5;
            return 1;
         }
         if ((active0 & 0x1040b01000000400L) != 0L || (active1 & 0x2L) != 0L)
            return 1;
         return -1;
      case 6:
         if ((active0 & 0xe08008000002000L) != 0L)
         {
            jjmatchedKind = 67;
            jjmatchedPos = 6;
            return 1;
         }
         if ((active0 & 0x2020400000000L) != 0L || (active1 & 0x4L) != 0L)
            return 1;
         return -1;
      case 7:
         if ((active0 & 0xc00000000002000L) != 0L)
         {
            jjmatchedKind = 67;
            jjmatchedPos = 7;
            return 1;
         }
         if ((active0 & 0x208008000000000L) != 0L)
            return 1;
         return -1;
      default :
         return -1;
   }
}
private final int jjStartNfa_0(int pos, long active0, long active1)
{
   return jjMoveNfa_0(jjStopStringLiteralDfa_0(pos, active0, active1), pos + 1);
}
private final int jjStopAtPos(int pos, int kind)
{
   jjmatchedKind = kind;
   jjmatchedPos = pos;
   return pos + 1;
}
private final int jjStartNfaWithStates_0(int pos, int kind, int state)
{
   jjmatchedKind = kind;
   jjmatchedPos = pos;
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) { return pos + 1; }
   return jjMoveNfa_0(state, pos + 1);
}
private final int jjMoveStringLiteralDfa0_0()
{
   switch(curChar)
   {
      case 37:
         return jjStopAtPos(0, 28);
      case 38:
         return jjStopAtPos(0, 21);
      case 40:
         return jjStopAtPos(0, 30);
      case 41:
         return jjStopAtPos(0, 31);
      case 42:
         return jjStopAtPos(0, 26);
      case 43:
         return jjStopAtPos(0, 24);
      case 44:
         return jjStopAtPos(0, 15);
      case 45:
         return jjStopAtPos(0, 25);
      case 47:
         return jjStartNfaWithStates_0(0, 27, 46);
      case 58:
         jjmatchedKind = 14;
         return jjMoveStringLiteralDfa1_0(0x10000L, 0x0L);
      case 59:
         return jjStopAtPos(0, 9);
      case 60:
         jjmatchedKind = 52;
         return jjMoveStringLiteralDfa1_0(0x800000L, 0x0L);
      case 61:
         return jjStopAtPos(0, 18);
      case 62:
         jjmatchedKind = 53;
         return jjMoveStringLiteralDfa1_0(0x400000L, 0x0L);
      case 70:
         return jjMoveStringLiteralDfa1_0(0x200000000L, 0x0L);
      case 79:
         return jjMoveStringLiteralDfa1_0(0x100000000000L, 0x0L);
      case 84:
         return jjMoveStringLiteralDfa1_0(0x100000000L, 0x0L);
      case 91:
         return jjStopAtPos(0, 55);
      case 93:
         return jjStopAtPos(0, 56);
      case 94:
         return jjStopAtPos(0, 20);
      case 97:
         return jjMoveStringLiteralDfa1_0(0x400080000000000L, 0x0L);
      case 98:
         return jjMoveStringLiteralDfa1_0(0x20000000000L, 0x0L);
      case 99:
         return jjMoveStringLiteralDfa1_0(0x1010000020000L, 0x4L);
      case 100:
         return jjMoveStringLiteralDfa1_0(0x2001000000000L, 0x0L);
      case 101:
         return jjMoveStringLiteralDfa1_0(0x804000000000000L, 0x0L);
      case 102:
         return jjMoveStringLiteralDfa1_0(0x800000000L, 0x0L);
      case 105:
         return jjMoveStringLiteralDfa1_0(0x4000000000002000L, 0x1L);
      case 108:
         return jjMoveStringLiteralDfa1_0(0x2000000000L, 0x0L);
      case 109:
         return jjMoveStringLiteralDfa1_0(0x400L, 0x0L);
      case 111:
         return jjMoveStringLiteralDfa1_0(0x9000040000000000L, 0x0L);
      case 114:
         return jjMoveStringLiteralDfa1_0(0x200000000000000L, 0x2L);
      case 115:
         return jjMoveStringLiteralDfa1_0(0x48a04000000000L, 0x0L);
      case 116:
         return jjMoveStringLiteralDfa1_0(0x400000000L, 0x0L);
      case 117:
         return jjMoveStringLiteralDfa1_0(0x408000000000L, 0x0L);
      case 118:
         return jjMoveStringLiteralDfa1_0(0x2000000000000000L, 0x0L);
      case 123:
         return jjStopAtPos(0, 11);
      case 124:
         return jjStopAtPos(0, 19);
      case 125:
         return jjStopAtPos(0, 12);
      case 126:
         return jjStopAtPos(0, 29);
      default :
         return jjMoveNfa_0(0, 0);
   }
}
private final int jjMoveStringLiteralDfa1_0(long active0, long active1)
{
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) {
      jjStopStringLiteralDfa_0(0, active0, active1);
      return 1;
   }
   switch(curChar)
   {
      case 58:
         if ((active0 & 0x10000L) != 0L)
            return jjStopAtPos(1, 16);
         break;
      case 60:
         if ((active0 & 0x800000L) != 0L)
            return jjStopAtPos(1, 23);
         break;
      case 62:
         if ((active0 & 0x400000L) != 0L)
            return jjStopAtPos(1, 22);
         break;
      case 65:
         return jjMoveStringLiteralDfa2_0(active0, 0x200000000L, active1, 0L);
      case 82:
         return jjMoveStringLiteralDfa2_0(active0, 0x100000000L, active1, 0L);
      case 97:
         return jjMoveStringLiteralDfa2_0(active0, 0x1000000000000L, active1, 0x2L);
      case 98:
         return jjMoveStringLiteralDfa2_0(active0, 0x100000000000L, active1, 0L);
      case 99:
         return jjMoveStringLiteralDfa2_0(active0, 0x40000000000L, active1, 0L);
      case 101:
         return jjMoveStringLiteralDfa2_0(active0, 0x20a000000000000L, active1, 0L);
      case 104:
         return jjMoveStringLiteralDfa2_0(active0, 0x14000000000L, active1, 0L);
      case 108:
         return jjMoveStringLiteralDfa2_0(active0, 0x800000000L, active1, 0L);
      case 110:
         if ((active0 & 0x4000000000000000L) != 0L)
         {
            jjmatchedKind = 62;
            jjmatchedPos = 1;
         }
         return jjMoveStringLiteralDfa2_0(active0, 0x1004488000002000L, active1, 0x1L);
      case 111:
         return jjMoveStringLiteralDfa2_0(active0, 0x2000023000020400L, active1, 0x4L);
      case 116:
         return jjMoveStringLiteralDfa2_0(active0, 0x440200000000000L, active1, 0L);
      case 117:
         return jjMoveStringLiteralDfa2_0(active0, 0x8000000000000000L, active1, 0L);
      case 119:
         return jjMoveStringLiteralDfa2_0(active0, 0x800000000000L, active1, 0L);
      case 120:
         return jjMoveStringLiteralDfa2_0(active0, 0x800000000000000L, active1, 0L);
      case 121:
         return jjMoveStringLiteralDfa2_0(active0, 0x400000000L, active1, 0L);
      default :
         break;
   }
   return jjStartNfa_0(0, active0, active1);
}
private final int jjMoveStringLiteralDfa2_0(long old0, long active0, long old1, long active1)
{
   if (((active0 &= old0) | (active1 &= old1)) == 0L)
      return jjStartNfa_0(0, old0, old1); 
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) {
      jjStopStringLiteralDfa_0(1, active0, active1);
      return 2;
   }
   switch(curChar)
   {
      case 76:
         return jjMoveStringLiteralDfa3_0(active0, 0x200000000L, active1, 0L);
      case 85:
         return jjMoveStringLiteralDfa3_0(active0, 0x100000000L, active1, 0L);
      case 97:
         return jjMoveStringLiteralDfa3_0(active0, 0x200010000000000L, active1, 0L);
      case 99:
         return jjMoveStringLiteralDfa3_0(active0, 0x800000000000000L, active1, 0L);
      case 100:
         return jjMoveStringLiteralDfa3_0(active0, 0x400L, active1, 0L);
      case 101:
         return jjMoveStringLiteralDfa3_0(active0, 0x1000000000000000L, active1, 0L);
      case 102:
         return jjMoveStringLiteralDfa3_0(active0, 0x2000000000000L, active1, 0L);
      case 105:
         return jjMoveStringLiteralDfa3_0(active0, 0x2000c00000000000L, active1, 0x2L);
      case 106:
         return jjMoveStringLiteralDfa3_0(active0, 0x100000000000L, active1, 0L);
      case 110:
         return jjMoveStringLiteralDfa3_0(active0, 0x2000020000L, active1, 0x4L);
      case 111:
         return jjMoveStringLiteralDfa3_0(active0, 0x24800000000L, active1, 0x1L);
      case 112:
         return jjMoveStringLiteralDfa3_0(active0, 0x400000000L, active1, 0L);
      case 113:
         return jjMoveStringLiteralDfa3_0(active0, 0x8000000000000L, active1, 0L);
      case 114:
         return jjMoveStringLiteralDfa3_0(active0, 0x40200000000000L, active1, 0L);
      case 115:
         return jjMoveStringLiteralDfa3_0(active0, 0x1008000000000L, active1, 0L);
      case 116:
         if ((active0 & 0x8000000000000000L) != 0L)
            return jjStartNfaWithStates_0(2, 63, 1);
         return jjMoveStringLiteralDfa3_0(active0, 0x400040000002000L, active1, 0L);
      case 117:
         return jjMoveStringLiteralDfa3_0(active0, 0x4001000000000L, active1, 0L);
      case 121:
         if ((active0 & 0x80000000000L) != 0L)
            return jjStartNfaWithStates_0(2, 43, 1);
         break;
      default :
         break;
   }
   return jjStartNfa_0(1, active0, active1);
}
private final int jjMoveStringLiteralDfa3_0(long old0, long active0, long old1, long active1)
{
   if (((active0 &= old0) | (active1 &= old1)) == 0L)
      return jjStartNfa_0(1, old0, old1); 
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) {
      jjStopStringLiteralDfa_0(2, active0, active1);
      return 3;
   }
   switch(curChar)
   {
      case 69:
         if ((active0 & 0x100000000L) != 0L)
            return jjStartNfaWithStates_0(3, 32, 1);
         break;
      case 83:
         return jjMoveStringLiteralDfa4_0(active0, 0x200000000L, active1, 0L);
      case 97:
         return jjMoveStringLiteralDfa4_0(active0, 0x2000800000000L, active1, 0L);
      case 98:
         return jjMoveStringLiteralDfa4_0(active0, 0x1000000000L, active1, 0L);
      case 100:
         if ((active0 & 0x2000000000000000L) != 0L)
            return jjStartNfaWithStates_0(3, 61, 1);
         return jjMoveStringLiteralDfa4_0(active0, 0x200000000000000L, active1, 0L);
      case 101:
         if ((active0 & 0x1000000000000L) != 0L)
            return jjStartNfaWithStates_0(3, 48, 1);
         return jjMoveStringLiteralDfa4_0(active0, 0x800140400002000L, active1, 0L);
      case 103:
         if ((active0 & 0x2000000000L) != 0L)
            return jjStartNfaWithStates_0(3, 37, 1);
         break;
      case 105:
         return jjMoveStringLiteralDfa4_0(active0, 0x40008000000000L, active1, 0L);
      case 108:
         return jjMoveStringLiteralDfa4_0(active0, 0x20000000000L, active1, 0L);
      case 109:
         if ((active0 & 0x4000000000000L) != 0L)
            return jjStartNfaWithStates_0(3, 50, 1);
         break;
      case 111:
         return jjMoveStringLiteralDfa4_0(active0, 0x400000000000L, active1, 0L);
      case 114:
         if ((active0 & 0x10000000000L) != 0L)
            return jjStartNfaWithStates_0(3, 40, 1);
         return jjMoveStringLiteralDfa4_0(active0, 0x400004000000000L, active1, 0L);
      case 115:
         return jjMoveStringLiteralDfa4_0(active0, 0x20000L, active1, 0x2L);
      case 116:
         return jjMoveStringLiteralDfa4_0(active0, 0x800000000000L, active1, 0x4L);
      case 117:
         return jjMoveStringLiteralDfa4_0(active0, 0x8200000000400L, active1, 0x1L);
      case 119:
         return jjMoveStringLiteralDfa4_0(active0, 0x1000000000000000L, active1, 0L);
      default :
         break;
   }
   return jjStartNfa_0(2, active0, active1);
}
private final int jjMoveStringLiteralDfa4_0(long old0, long active0, long old1, long active1)
{
   if (((active0 &= old0) | (active1 &= old1)) == 0L)
      return jjStartNfa_0(2, old0, old1); 
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) {
      jjStopStringLiteralDfa_0(3, active0, active1);
      return 4;
   }
   switch(curChar)
   {
      case 69:
         if ((active0 & 0x200000000L) != 0L)
            return jjStartNfaWithStates_0(4, 33, 1);
         break;
      case 97:
         return jjMoveStringLiteralDfa5_0(active0, 0x1000000000000000L, active1, 0L);
      case 99:
         return jjMoveStringLiteralDfa5_0(active0, 0xb00000000000L, active1, 0L);
      case 100:
         return jjMoveStringLiteralDfa5_0(active0, 0x400000000L, active1, 0L);
      case 101:
         return jjMoveStringLiteralDfa5_0(active0, 0x8020000000000L, active1, 0x6L);
      case 103:
         return jjMoveStringLiteralDfa5_0(active0, 0x8000000000L, active1, 0L);
      case 105:
         return jjMoveStringLiteralDfa5_0(active0, 0x400000000000000L, active1, 0L);
      case 108:
         return jjMoveStringLiteralDfa5_0(active0, 0x1000000400L, active1, 0L);
      case 110:
         if ((active0 & 0x400000000000L) != 0L)
            return jjStartNfaWithStates_0(4, 46, 1);
         return jjMoveStringLiteralDfa5_0(active0, 0x40000000000000L, active1, 0L);
      case 111:
         return jjMoveStringLiteralDfa5_0(active0, 0x200000000000000L, active1, 0L);
      case 112:
         return jjMoveStringLiteralDfa5_0(active0, 0x800000000000000L, active1, 0L);
      case 114:
         return jjMoveStringLiteralDfa5_0(active0, 0x2000L, active1, 0L);
      case 116:
         if ((active0 & 0x20000L) != 0L)
            return jjStartNfaWithStates_0(4, 17, 1);
         else if ((active0 & 0x800000000L) != 0L)
            return jjStartNfaWithStates_0(4, 35, 1);
         else if ((active0 & 0x4000000000L) != 0L)
            return jjStartNfaWithStates_0(4, 38, 1);
         else if ((active0 & 0x40000000000L) != 0L)
            return jjStartNfaWithStates_0(4, 42, 1);
         else if ((active1 & 0x1L) != 0L)
            return jjStartNfaWithStates_0(4, 64, 1);
         break;
      case 117:
         return jjMoveStringLiteralDfa5_0(active0, 0x2000000000000L, active1, 0L);
      default :
         break;
   }
   return jjStartNfa_0(3, active0, active1);
}
private final int jjMoveStringLiteralDfa5_0(long old0, long active0, long old1, long active1)
{
   if (((active0 &= old0) | (active1 &= old1)) == 0L)
      return jjStartNfa_0(3, old0, old1); 
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) {
      jjStopStringLiteralDfa_0(4, active0, active1);
      return 5;
   }
   switch(curChar)
   {
      case 97:
         return jjMoveStringLiteralDfa6_0(active0, 0x20000000000L, active1, 0L);
      case 98:
         return jjMoveStringLiteralDfa6_0(active0, 0x400000000000000L, active1, 0L);
      case 101:
         if ((active0 & 0x400L) != 0L)
            return jjStartNfaWithStates_0(5, 10, 1);
         else if ((active0 & 0x1000000000L) != 0L)
            return jjStartNfaWithStates_0(5, 36, 1);
         return jjMoveStringLiteralDfa6_0(active0, 0x400000000L, active1, 0L);
      case 102:
         return jjMoveStringLiteralDfa6_0(active0, 0x2000L, active1, 0L);
      case 103:
         if ((active0 & 0x40000000000000L) != 0L)
            return jjStartNfaWithStates_0(5, 54, 1);
         break;
      case 104:
         if ((active0 & 0x800000000000L) != 0L)
            return jjStartNfaWithStates_0(5, 47, 1);
         break;
      case 108:
         return jjMoveStringLiteralDfa6_0(active0, 0x2000000000000L, active1, 0L);
      case 110:
         return jjMoveStringLiteralDfa6_0(active0, 0x208008000000000L, active1, 0L);
      case 115:
         if ((active1 & 0x2L) != 0L)
            return jjStartNfaWithStates_0(5, 65, 1);
         break;
      case 116:
         if ((active0 & 0x100000000000L) != 0L)
            return jjStartNfaWithStates_0(5, 44, 1);
         else if ((active0 & 0x200000000000L) != 0L)
            return jjStartNfaWithStates_0(5, 45, 1);
         return jjMoveStringLiteralDfa6_0(active0, 0x800000000000000L, active1, 0L);
      case 120:
         return jjMoveStringLiteralDfa6_0(active0, 0L, active1, 0x4L);
      case 121:
         if ((active0 & 0x1000000000000000L) != 0L)
            return jjStartNfaWithStates_0(5, 60, 1);
         break;
      default :
         break;
   }
   return jjStartNfa_0(4, active0, active1);
}
private final int jjMoveStringLiteralDfa6_0(long old0, long active0, long old1, long active1)
{
   if (((active0 &= old0) | (active1 &= old1)) == 0L)
      return jjStartNfa_0(4, old0, old1); 
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) {
      jjStopStringLiteralDfa_0(5, active0, active1);
      return 6;
   }
   switch(curChar)
   {
      case 97:
         return jjMoveStringLiteralDfa7_0(active0, 0x2000L, active1, 0L);
      case 99:
         return jjMoveStringLiteralDfa7_0(active0, 0x8000000000000L, active1, 0L);
      case 101:
         return jjMoveStringLiteralDfa7_0(active0, 0x8000000000L, active1, 0L);
      case 102:
         if ((active0 & 0x400000000L) != 0L)
            return jjStartNfaWithStates_0(6, 34, 1);
         break;
      case 105:
         return jjMoveStringLiteralDfa7_0(active0, 0x800000000000000L, active1, 0L);
      case 108:
         return jjMoveStringLiteralDfa7_0(active0, 0x200000000000000L, active1, 0L);
      case 110:
         if ((active0 & 0x20000000000L) != 0L)
            return jjStartNfaWithStates_0(6, 41, 1);
         break;
      case 116:
         if ((active0 & 0x2000000000000L) != 0L)
            return jjStartNfaWithStates_0(6, 49, 1);
         else if ((active1 & 0x4L) != 0L)
            return jjStartNfaWithStates_0(6, 66, 1);
         break;
      case 117:
         return jjMoveStringLiteralDfa7_0(active0, 0x400000000000000L, active1, 0L);
      default :
         break;
   }
   return jjStartNfa_0(5, active0, active1);
}
private final int jjMoveStringLiteralDfa7_0(long old0, long active0, long old1, long active1)
{
   if (((active0 &= old0) | (active1 &= old1)) == 0L)
      return jjStartNfa_0(5, old0, old1); 
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) {
      jjStopStringLiteralDfa_0(6, active0, 0L);
      return 7;
   }
   switch(curChar)
   {
      case 99:
         return jjMoveStringLiteralDfa8_0(active0, 0x2000L);
      case 100:
         if ((active0 & 0x8000000000L) != 0L)
            return jjStartNfaWithStates_0(7, 39, 1);
         break;
      case 101:
         if ((active0 & 0x8000000000000L) != 0L)
            return jjStartNfaWithStates_0(7, 51, 1);
         break;
      case 111:
         return jjMoveStringLiteralDfa8_0(active0, 0x800000000000000L);
      case 116:
         return jjMoveStringLiteralDfa8_0(active0, 0x400000000000000L);
      case 121:
         if ((active0 & 0x200000000000000L) != 0L)
            return jjStartNfaWithStates_0(7, 57, 1);
         break;
      default :
         break;
   }
   return jjStartNfa_0(6, active0, 0L);
}
private final int jjMoveStringLiteralDfa8_0(long old0, long active0)
{
   if (((active0 &= old0)) == 0L)
      return jjStartNfa_0(6, old0, 0L);
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) {
      jjStopStringLiteralDfa_0(7, active0, 0L);
      return 8;
   }
   switch(curChar)
   {
      case 101:
         if ((active0 & 0x2000L) != 0L)
            return jjStartNfaWithStates_0(8, 13, 1);
         else if ((active0 & 0x400000000000000L) != 0L)
            return jjStartNfaWithStates_0(8, 58, 1);
         break;
      case 110:
         if ((active0 & 0x800000000000000L) != 0L)
            return jjStartNfaWithStates_0(8, 59, 1);
         break;
      default :
         break;
   }
   return jjStartNfa_0(7, active0, 0L);
}
private final void jjCheckNAdd(int state)
{
   if (jjrounds[state] != jjround)
   {
      jjstateSet[jjnewStateCnt++] = state;
      jjrounds[state] = jjround;
   }
}
private final void jjAddStates(int start, int end)
{
   do {
      jjstateSet[jjnewStateCnt++] = jjnextStates[start];
   } while (start++ != end);
}
private final void jjCheckNAddTwoStates(int state1, int state2)
{
   jjCheckNAdd(state1);
   jjCheckNAdd(state2);
}
private final void jjCheckNAddStates(int start, int end)
{
   do {
      jjCheckNAdd(jjnextStates[start]);
   } while (start++ != end);
}
private final void jjCheckNAddStates(int start)
{
   jjCheckNAdd(jjnextStates[start]);
   jjCheckNAdd(jjnextStates[start + 1]);
}
static final long[] jjbitVec0 = {
   0x0L, 0x0L, 0xffffffffffffffffL, 0xffffffffffffffffL
};
private final int jjMoveNfa_0(int startState, int curPos)
{
   int[] nextStates;
   int startsAt = 0;
   jjnewStateCnt = 83;
   int i = 1;
   jjstateSet[0] = startState;
   int j, kind = 0x7fffffff;
   for (;;)
   {
      if (++jjround == 0x7fffffff)
         ReInitRounds();
      if (curChar < 64)
      {
         long l = 1L << curChar;
         MatchLoop: do
         {
            switch(jjstateSet[--i])
            {
               case 0:
                  if ((0x3ff000000000000L & l) != 0L)
                     jjCheckNAddStates(0, 5);
                  else if (curChar == 35)
                     jjCheckNAddStates(6, 9);
                  else if (curChar == 47)
                     jjAddStates(10, 11);
                  else if (curChar == 34)
                     jjCheckNAddStates(12, 14);
                  else if (curChar == 39)
                     jjAddStates(15, 16);
                  else if (curChar == 46)
                     jjCheckNAdd(9);
                  if ((0x3fe000000000000L & l) != 0L)
                  {
                     if (kind > 69)
                        kind = 69;
                     jjCheckNAddTwoStates(6, 7);
                  }
                  else if (curChar == 48)
                     jjAddStates(17, 18);
                  if (curChar == 48)
                  {
                     if (kind > 68)
                        kind = 68;
                     jjCheckNAddTwoStates(3, 4);
                  }
                  break;
               case 46:
                  if (curChar == 42)
                     jjCheckNAddTwoStates(50, 51);
                  else if (curChar == 47)
                     jjCheckNAddTwoStates(47, 48);
                  break;
               case 1:
                  if ((0x3ff000000000000L & l) == 0L)
                     break;
                  if (kind > 67)
                     kind = 67;
                  jjstateSet[jjnewStateCnt++] = 1;
                  break;
               case 2:
                  if (curChar != 48)
                     break;
                  if (kind > 68)
                     kind = 68;
                  jjCheckNAddTwoStates(3, 4);
                  break;
               case 3:
                  if ((0xff000000000000L & l) == 0L)
                     break;
                  if (kind > 68)
                     kind = 68;
                  jjCheckNAddTwoStates(3, 4);
                  break;
               case 5:
                  if ((0x3fe000000000000L & l) == 0L)
                     break;
                  if (kind > 69)
                     kind = 69;
                  jjCheckNAddTwoStates(6, 7);
                  break;
               case 6:
                  if ((0x3ff000000000000L & l) == 0L)
                     break;
                  if (kind > 69)
                     kind = 69;
                  jjCheckNAddTwoStates(6, 7);
                  break;
               case 8:
                  if (curChar == 46)
                     jjCheckNAdd(9);
                  break;
               case 9:
                  if ((0x3ff000000000000L & l) == 0L)
                     break;
                  if (kind > 71)
                     kind = 71;
                  jjCheckNAddStates(19, 21);
                  break;
               case 11:
                  if ((0x280000000000L & l) != 0L)
                     jjCheckNAdd(12);
                  break;
               case 12:
                  if ((0x3ff000000000000L & l) == 0L)
                     break;
                  if (kind > 71)
                     kind = 71;
                  jjCheckNAddTwoStates(12, 13);
                  break;
               case 14:
                  if (curChar == 39)
                     jjAddStates(15, 16);
                  break;
               case 15:
                  if ((0xffffff7fffffdbffL & l) != 0L)
                     jjCheckNAdd(16);
                  break;
               case 16:
                  if (curChar == 39 && kind > 73)
                     kind = 73;
                  break;
               case 18:
                  if ((0x8000008400000000L & l) != 0L)
                     jjCheckNAdd(16);
                  break;
               case 19:
                  if (curChar == 48)
                     jjCheckNAddTwoStates(20, 16);
                  break;
               case 20:
                  if ((0xff000000000000L & l) != 0L)
                     jjCheckNAddTwoStates(20, 16);
                  break;
               case 21:
                  if ((0x3fe000000000000L & l) != 0L)
                     jjCheckNAddTwoStates(22, 16);
                  break;
               case 22:
                  if ((0x3ff000000000000L & l) != 0L)
                     jjCheckNAddTwoStates(22, 16);
                  break;
               case 23:
                  if (curChar == 48)
                     jjAddStates(22, 23);
                  break;
               case 25:
                  if ((0x3ff000000000000L & l) != 0L)
                     jjCheckNAddTwoStates(25, 16);
                  break;
               case 27:
                  if (curChar == 34)
                     jjCheckNAddStates(12, 14);
                  break;
               case 28:
                  if ((0xfffffffbffffdbffL & l) != 0L)
                     jjCheckNAddStates(12, 14);
                  break;
               case 30:
                  if ((0x8000008400000000L & l) != 0L)
                     jjCheckNAddStates(12, 14);
                  break;
               case 31:
                  if (curChar == 34 && kind > 74)
                     kind = 74;
                  break;
               case 32:
                  if (curChar == 48)
                     jjCheckNAddStates(24, 27);
                  break;
               case 33:
                  if ((0xff000000000000L & l) != 0L)
                     jjCheckNAddStates(24, 27);
                  break;
               case 34:
                  if ((0x3fe000000000000L & l) != 0L)
                     jjCheckNAddStates(28, 31);
                  break;
               case 35:
                  if ((0x3ff000000000000L & l) != 0L)
                     jjCheckNAddStates(28, 31);
                  break;
               case 36:
                  if (curChar == 48)
                     jjAddStates(32, 33);
                  break;
               case 38:
                  if ((0x3ff000000000000L & l) != 0L)
                     jjCheckNAddStates(34, 37);
                  break;
               case 40:
                  if (curChar == 48)
                     jjAddStates(17, 18);
                  break;
               case 42:
                  if ((0x3ff000000000000L & l) == 0L)
                     break;
                  if (kind > 70)
                     kind = 70;
                  jjAddStates(38, 39);
                  break;
               case 45:
                  if (curChar == 47)
                     jjAddStates(10, 11);
                  break;
               case 47:
                  if ((0xfffffffffffffbffL & l) != 0L)
                     jjCheckNAddTwoStates(47, 48);
                  break;
               case 48:
                  if (curChar == 10 && kind > 5)
                     kind = 5;
                  break;
               case 49:
                  if (curChar == 42)
                     jjCheckNAddTwoStates(50, 51);
                  break;
               case 50:
                  if ((0xfffffbffffffffffL & l) != 0L)
                     jjCheckNAddTwoStates(50, 51);
                  break;
               case 51:
                  if (curChar == 42)
                     jjAddStates(40, 41);
                  break;
               case 52:
                  if ((0xffff7fffffffffffL & l) != 0L)
                     jjCheckNAddTwoStates(53, 51);
                  break;
               case 53:
                  if ((0xfffffbffffffffffL & l) != 0L)
                     jjCheckNAddTwoStates(53, 51);
                  break;
               case 54:
                  if (curChar == 47 && kind > 6)
                     kind = 6;
                  break;
               case 55:
                  if (curChar == 35)
                     jjCheckNAddStates(6, 9);
                  break;
               case 56:
                  if ((0x100000200L & l) != 0L)
                     jjCheckNAddTwoStates(56, 57);
                  break;
               case 57:
                  if ((0x3ff000000000000L & l) != 0L)
                     jjCheckNAddStates(42, 46);
                  break;
               case 58:
                  if ((0x3ff000000000000L & l) != 0L)
                     jjCheckNAddStates(47, 50);
                  break;
               case 59:
                  if ((0x100000200L & l) != 0L)
                     jjCheckNAddTwoStates(59, 60);
                  break;
               case 60:
                  if (curChar == 34)
                     jjCheckNAdd(61);
                  break;
               case 61:
                  if ((0xfffffffbffffffffL & l) != 0L)
                     jjCheckNAddTwoStates(61, 62);
                  break;
               case 62:
                  if (curChar == 34)
                     jjCheckNAddStates(51, 53);
                  break;
               case 63:
                  if (curChar == 10 && kind > 7)
                     kind = 7;
                  break;
               case 64:
                  if ((0x3ff000000000000L & l) != 0L)
                     jjCheckNAddStates(54, 57);
                  break;
               case 65:
                  if ((0x100000200L & l) != 0L)
                     jjCheckNAddStates(58, 60);
                  break;
               case 66:
                  if ((0x3ff000000000000L & l) != 0L)
                     jjCheckNAddTwoStates(66, 63);
                  break;
               case 67:
                  if ((0x100000200L & l) != 0L)
                     jjCheckNAddStates(61, 65);
                  break;
               case 68:
                  if ((0x100000200L & l) != 0L)
                     jjCheckNAddTwoStates(68, 69);
                  break;
               case 69:
                  if ((0x3ff000000000000L & l) == 0L)
                     break;
                  if (kind > 8)
                     kind = 8;
                  jjCheckNAddStates(66, 69);
                  break;
               case 70:
                  if ((0x100000200L & l) == 0L)
                     break;
                  if (kind > 8)
                     kind = 8;
                  jjCheckNAddStates(70, 72);
                  break;
               case 71:
                  if ((0x53ffc00500000000L & l) == 0L)
                     break;
                  if (kind > 8)
                     kind = 8;
                  jjCheckNAddTwoStates(71, 72);
                  break;
               case 72:
                  if (curChar != 10)
                     break;
                  if (kind > 8)
                     kind = 8;
                  jjCheckNAdd(72);
                  break;
               case 73:
                  if ((0x3ff000000000000L & l) != 0L)
                     jjCheckNAddStates(0, 5);
                  break;
               case 74:
                  if ((0x3ff000000000000L & l) != 0L)
                     jjCheckNAddTwoStates(74, 75);
                  break;
               case 75:
                  if (curChar != 46)
                     break;
                  if (kind > 71)
                     kind = 71;
                  jjCheckNAddStates(73, 75);
                  break;
               case 76:
                  if ((0x3ff000000000000L & l) == 0L)
                     break;
                  if (kind > 71)
                     kind = 71;
                  jjCheckNAddStates(73, 75);
                  break;
               case 77:
                  if ((0x3ff000000000000L & l) != 0L)
                     jjCheckNAddTwoStates(77, 8);
                  break;
               case 78:
                  if ((0x3ff000000000000L & l) != 0L)
                     jjCheckNAddTwoStates(78, 79);
                  break;
               case 80:
                  if ((0x280000000000L & l) != 0L)
                     jjCheckNAdd(81);
                  break;
               case 81:
                  if ((0x3ff000000000000L & l) == 0L)
                     break;
                  if (kind > 72)
                     kind = 72;
                  jjCheckNAddTwoStates(81, 82);
                  break;
               default : break;
            }
         } while(i != startsAt);
      }
      else if (curChar < 128)
      {
         long l = 1L << (curChar & 077);
         MatchLoop: do
         {
            switch(jjstateSet[--i])
            {
               case 0:
               case 1:
                  if ((0x7fffffe87fffffeL & l) == 0L)
                     break;
                  if (kind > 67)
                     kind = 67;
                  jjCheckNAdd(1);
                  break;
               case 4:
                  if ((0x20100000201000L & l) != 0L && kind > 68)
                     kind = 68;
                  break;
               case 7:
                  if ((0x20100000201000L & l) != 0L && kind > 69)
                     kind = 69;
                  break;
               case 10:
                  if ((0x2000000020L & l) != 0L)
                     jjAddStates(76, 77);
                  break;
               case 13:
                  if ((0x104000001040L & l) != 0L && kind > 71)
                     kind = 71;
                  break;
               case 15:
                  if ((0xffffffffefffffffL & l) != 0L)
                     jjCheckNAdd(16);
                  break;
               case 17:
                  if (curChar == 92)
                     jjAddStates(78, 81);
                  break;
               case 18:
                  if ((0x54404610000000L & l) != 0L)
                     jjCheckNAdd(16);
                  break;
               case 24:
                  if (curChar == 120)
                     jjCheckNAdd(25);
                  break;
               case 25:
                  if ((0x7e0000007eL & l) != 0L)
                     jjCheckNAddTwoStates(25, 16);
                  break;
               case 26:
                  if (curChar == 88)
                     jjCheckNAdd(25);
                  break;
               case 28:
                  if ((0xffffffffefffffffL & l) != 0L)
                     jjCheckNAddStates(12, 14);
                  break;
               case 29:
                  if (curChar == 92)
                     jjAddStates(82, 85);
                  break;
               case 30:
                  if ((0x54404610000000L & l) != 0L)
                     jjCheckNAddStates(12, 14);
                  break;
               case 37:
                  if (curChar == 120)
                     jjCheckNAdd(38);
                  break;
               case 38:
                  if ((0x7e0000007eL & l) != 0L)
                     jjCheckNAddStates(34, 37);
                  break;
               case 39:
                  if (curChar == 88)
                     jjCheckNAdd(38);
                  break;
               case 41:
                  if (curChar == 120)
                     jjCheckNAdd(42);
                  break;
               case 42:
                  if ((0x7e0000007eL & l) == 0L)
                     break;
                  if (kind > 70)
                     kind = 70;
                  jjCheckNAddTwoStates(42, 43);
                  break;
               case 43:
                  if ((0x20100000201000L & l) != 0L && kind > 70)
                     kind = 70;
                  break;
               case 44:
                  if (curChar == 88)
                     jjCheckNAdd(42);
                  break;
               case 47:
                  jjAddStates(86, 87);
                  break;
               case 50:
                  jjCheckNAddTwoStates(50, 51);
                  break;
               case 52:
               case 53:
                  jjCheckNAddTwoStates(53, 51);
                  break;
               case 58:
                  if ((0x7fffffe87fffffeL & l) != 0L)
                     jjAddStates(47, 50);
                  break;
               case 61:
                  jjAddStates(88, 89);
                  break;
               case 69:
                  if ((0x7fffffe87fffffeL & l) == 0L)
                     break;
                  if (kind > 8)
                     kind = 8;
                  jjCheckNAddStates(66, 69);
                  break;
               case 71:
                  if ((0x7fffffe97fffffeL & l) == 0L)
                     break;
                  if (kind > 8)
                     kind = 8;
                  jjCheckNAddTwoStates(71, 72);
                  break;
               case 79:
                  if ((0x2000000020L & l) != 0L)
                     jjAddStates(90, 91);
                  break;
               case 82:
                  if ((0x104000001040L & l) != 0L && kind > 72)
                     kind = 72;
                  break;
               default : break;
            }
         } while(i != startsAt);
      }
      else
      {
         int i2 = (curChar & 0xff) >> 6;
         long l2 = 1L << (curChar & 077);
         MatchLoop: do
         {
            switch(jjstateSet[--i])
            {
               case 15:
                  if ((jjbitVec0[i2] & l2) != 0L)
                     jjstateSet[jjnewStateCnt++] = 16;
                  break;
               case 28:
                  if ((jjbitVec0[i2] & l2) != 0L)
                     jjAddStates(12, 14);
                  break;
               case 47:
                  if ((jjbitVec0[i2] & l2) != 0L)
                     jjAddStates(86, 87);
                  break;
               case 50:
                  if ((jjbitVec0[i2] & l2) != 0L)
                     jjCheckNAddTwoStates(50, 51);
                  break;
               case 52:
               case 53:
                  if ((jjbitVec0[i2] & l2) != 0L)
                     jjCheckNAddTwoStates(53, 51);
                  break;
               case 61:
                  if ((jjbitVec0[i2] & l2) != 0L)
                     jjAddStates(88, 89);
                  break;
               default : break;
            }
         } while(i != startsAt);
      }
      if (kind != 0x7fffffff)
      {
         jjmatchedKind = kind;
         jjmatchedPos = curPos;
         kind = 0x7fffffff;
      }
      ++curPos;
      if ((i = jjnewStateCnt) == (startsAt = 83 - (jjnewStateCnt = startsAt)))
         return curPos;
      try { curChar = input_stream.readChar(); }
      catch(java.io.IOException e) { return curPos; }
   }
}
static final int[] jjnextStates = {
   74, 75, 77, 8, 78, 79, 56, 57, 68, 69, 46, 49, 28, 29, 31, 15, 
   17, 41, 44, 9, 10, 13, 24, 26, 28, 29, 33, 31, 28, 29, 35, 31, 
   37, 39, 28, 29, 38, 31, 42, 43, 52, 54, 57, 58, 59, 60, 63, 58, 
   59, 60, 63, 63, 64, 67, 65, 66, 63, 64, 65, 66, 63, 65, 66, 63, 
   64, 67, 69, 70, 71, 72, 70, 71, 72, 76, 10, 13, 11, 12, 18, 19, 
   21, 23, 30, 32, 34, 36, 47, 48, 61, 62, 80, 81, 
};
public static final String[] jjstrLiteralImages = {
"", null, null, null, null, null, null, null, null, "\73", 
"\155\157\144\165\154\145", "\173", "\175", "\151\156\164\145\162\146\141\143\145", "\72", "\54", 
"\72\72", "\143\157\156\163\164", "\75", "\174", "\136", "\46", "\76\76", "\74\74", 
"\53", "\55", "\52", "\57", "\45", "\176", "\50", "\51", "\124\122\125\105", 
"\106\101\114\123\105", "\164\171\160\145\144\145\146", "\146\154\157\141\164", 
"\144\157\165\142\154\145", "\154\157\156\147", "\163\150\157\162\164", 
"\165\156\163\151\147\156\145\144", "\143\150\141\162", "\142\157\157\154\145\141\156", "\157\143\164\145\164", 
"\141\156\171", "\117\142\152\145\143\164", "\163\164\162\165\143\164", 
"\165\156\151\157\156", "\163\167\151\164\143\150", "\143\141\163\145", 
"\144\145\146\141\165\154\164", "\145\156\165\155", "\163\145\161\165\145\156\143\145", "\74", "\76", 
"\163\164\162\151\156\147", "\133", "\135", "\162\145\141\144\157\156\154\171", 
"\141\164\164\162\151\142\165\164\145", "\145\170\143\145\160\164\151\157\156", "\157\156\145\167\141\171", 
"\166\157\151\144", "\151\156", "\157\165\164", "\151\156\157\165\164", 
"\162\141\151\163\145\163", "\143\157\156\164\145\170\164", null, null, null, null, null, null, null, null, };
public static final String[] lexStateNames = {
   "DEFAULT", 
};
static final long[] jjtoToken = {
   0xfffffffffffffe01L, 0x7ffL, 
};
static final long[] jjtoSkip = {
   0x1feL, 0x0L, 
};
private ASCII_CharStream input_stream;
private final int[] jjrounds = new int[83];
private final int[] jjstateSet = new int[166];
protected char curChar;
public IDLParserTokenManager(ASCII_CharStream stream)
{
   if (ASCII_CharStream.staticFlag)
      throw new Error("ERROR: Cannot use a static CharStream class with a non-static lexical analyzer.");
   input_stream = stream;
}
public IDLParserTokenManager(ASCII_CharStream stream, int lexState)
{
   this(stream);
   SwitchTo(lexState);
}
public void ReInit(ASCII_CharStream stream)
{
   jjmatchedPos = jjnewStateCnt = 0;
   curLexState = defaultLexState;
   input_stream = stream;
   ReInitRounds();
}
private final void ReInitRounds()
{
   int i;
   jjround = 0x80000001;
   for (i = 83; i-- > 0;)
      jjrounds[i] = 0x80000000;
}
public void ReInit(ASCII_CharStream stream, int lexState)
{
   ReInit(stream);
   SwitchTo(lexState);
}
public void SwitchTo(int lexState)
{
   if (lexState >= 1 || lexState < 0)
      throw new TokenMgrError("Error: Ignoring invalid lexical state : " + lexState + ". State unchanged.", TokenMgrError.INVALID_LEXICAL_STATE);
   else
      curLexState = lexState;
}

private final Token jjFillToken()
{
   Token t = Token.newToken(jjmatchedKind);
   t.kind = jjmatchedKind;
   String im = jjstrLiteralImages[jjmatchedKind];
   t.image = (im == null) ? input_stream.GetImage() : im;
   t.beginLine = input_stream.getBeginLine();
   t.beginColumn = input_stream.getBeginColumn();
   t.endLine = input_stream.getEndLine();
   t.endColumn = input_stream.getEndColumn();
   return t;
}

int curLexState = 0;
int defaultLexState = 0;
int jjnewStateCnt;
int jjround;
int jjmatchedPos;
int jjmatchedKind;

public final Token getNextToken() 
{
  int kind;
  Token specialToken = null;
  Token matchedToken;
  int curPos = 0;

  EOFLoop :
  for (;;)
  {   
   try   
   {     
      curChar = input_stream.BeginToken();
   }     
   catch(java.io.IOException e)
   {        
      jjmatchedKind = 0;
      matchedToken = jjFillToken();
      return matchedToken;
   }

   try { 
      while (curChar <= 32 && (0x100002600L & (1L << curChar)) != 0L)
         curChar = input_stream.BeginToken();
   }
   catch (java.io.IOException e1) { continue EOFLoop; }
   jjmatchedKind = 0x7fffffff;
   jjmatchedPos = 0;
   curPos = jjMoveStringLiteralDfa0_0();
   if (jjmatchedKind != 0x7fffffff)
   {
      if (jjmatchedPos + 1 < curPos)
         input_stream.backup(curPos - jjmatchedPos - 1);
      if ((jjtoToken[jjmatchedKind >> 6] & (1L << (jjmatchedKind & 077))) != 0L)
      {
         matchedToken = jjFillToken();
         return matchedToken;
      }
      else
      {
         continue EOFLoop;
      }
   }
   int error_line = input_stream.getEndLine();
   int error_column = input_stream.getEndColumn();
   String error_after = null;
   boolean EOFSeen = false;
   try { input_stream.readChar(); input_stream.backup(1); }
   catch (java.io.IOException e1) {
      EOFSeen = true;
      error_after = curPos <= 1 ? "" : input_stream.GetImage();
      if (curChar == '\n' || curChar == '\r') {
         error_line++;
         error_column = 0;
      }
      else
         error_column++;
   }
   if (!EOFSeen) {
      input_stream.backup(1);
      error_after = curPos <= 1 ? "" : input_stream.GetImage();
   }
   throw new TokenMgrError(EOFSeen, curLexState, error_line, error_column, error_after, curChar, TokenMgrError.LEXICAL_ERROR);
  }
}

}
