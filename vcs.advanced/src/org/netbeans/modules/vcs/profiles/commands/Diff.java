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

package org.netbeans.modules.vcs.profiles.commands;

import org.openide.util.NbBundle;

import org.netbeans.api.diff.Difference;

import org.netbeans.modules.vcscore.VcsFileSystem;

/**
 * Parser of differences produced by VCS commands. Several formats are handled.
 *
 * @author  Martin Entlicher
 */
public class Diff extends AbstractDiffCommand {

    private static final String CVS_REVISION_STR = "retrieving revision";
    private static final String VSS_DIFFING = "Diffing: ";
    private static final String VSS_AGAINST = "Against: ";
    
    private static final String CA_REP = "-REP ";
    private static final String CA_INS = "-INS ";
    private static final String CA_DEL = "-DEL ";

    private int lastLine1 = 0;
    private int lastLine2 = 0;
    private int lastDiff = 0; // for STCMD and PVCS
    private int lastDiffLine1 = 0; // for STCMD and PVCS
    private int lastDiffLine2 = 0; // for STCMD and PVCS
    private int caDocShift = 0; // the "shift" of the second document line numbers
    private StringBuffer firstText = new StringBuffer();
    private StringBuffer secondText = new StringBuffer();

    //static final long serialVersionUID =8341896634226664590L;
    /** Creates new Diff */
    public Diff() {
        //setBundle(org.openide.util.NbBundle.getBundle(Diff.class));
    }

    /** Set the VCS file system to use to execute commands.
     */
    public void setFileSystem(VcsFileSystem fileSystem) {
        super.setFileSystem(fileSystem);
    }

    public void outputData(String[] elements) {
        switch(outputType) {
        case 0: match0(elements); // CVS
            break;
        case 1: match1(elements); // StarTeam
            break;
        case 2: match2(elements); // VSS
            break;
        case 3: match3(elements); // PVCS
            break;
        }
    }
    
    /**
     * Matches output compatible with Unix diff command.
     * @params elements The input to parse.
     */
    public void matchUnixLike(String[] elements) {
        if (elements[0].startsWith("< ")) {
            firstText.append(elements[0].substring(2) + "\n");
            return ;
        } else if (elements[0].startsWith("> ")) {
            secondText.append(elements[0].substring(2) + "\n");
            return ;
        } else if (elements[0].startsWith("--")) {
            return ;
        } else {
            setTextOnLastDifference(firstText.toString(), secondText.toString());
            firstText.delete(0, firstText.length());
            secondText.delete(0, secondText.length());
        }
        int index = 0, commaIndex = 0;
        int n1 = 0, n2 = 0, n3 = 0, n4 = 0;
        String nStr;
        if ((index = elements[0].indexOf('a')) >= 0) {
            //DiffAction action = new DiffAction();
            try {
                n1 = Integer.parseInt(elements[0].substring(0, index));
                index++;
                commaIndex = elements[0].indexOf(',', index);
                if (commaIndex < 0) {
                    nStr = elements[0].substring(index, elements[0].length());
                    if (checkEmpty(nStr, elements[0])) return;
                    n3 = Integer.parseInt(nStr);
                    n4 = n3;
                } else {
                    nStr = elements[0].substring(index, commaIndex);
                    if (checkEmpty(nStr, elements[0])) return;
                    n3 = Integer.parseInt(nStr);
                    nStr = elements[0].substring(commaIndex+1, elements[0].length());
                    if (nStr == null || nStr.length() == 0) n4 = n3;
                    else n4 = Integer.parseInt(nStr);
                }
            } catch (NumberFormatException e) {
                if (this.stderrListener != null) {
                    String[] debugOut = { "NumberFormatException "+e.getMessage() };
                    stderrListener.outputData(debugOut);
                }
                return;
            }
            addDifference(new Difference(Difference.ADD, n1, 0, n3, n4));
            //diff.addAddAction(n1, n3, n4);
        } else if ((index = elements[0].indexOf('d')) >= 0) {
            commaIndex = elements[0].lastIndexOf(',', index);
            try {
                if (commaIndex < 0) {
                    n1 = Integer.parseInt(elements[0].substring(0, index));
                    n2 = n1;
                } else {
                    nStr = elements[0].substring(0, commaIndex);
                    if (checkEmpty(nStr, elements[0])) return;
                    n1 = Integer.parseInt(nStr);
                    nStr = elements[0].substring(commaIndex+1, index);
                    if (checkEmpty(nStr, elements[0])) return;
                    n2 = Integer.parseInt(nStr);
                }
                nStr = elements[0].substring(index+1, elements[0].length());
                if (checkEmpty(nStr, elements[0])) return;
                n3 = Integer.parseInt(nStr);
            } catch (NumberFormatException e) {
                if (this.stderrListener != null) {
                    String[] debugOut = { "NumberFormatException "+e.getMessage() };
                    stderrListener.outputData(debugOut);
                }
                return;
            }
            addDifference(new Difference(Difference.DELETE, n1, n2, n3, 0));
            //diff.addDeleteAction(n1, n2, n3);
        } else if ((index = elements[0].indexOf('c')) >= 0) {
            commaIndex = elements[0].lastIndexOf(',', index);
            try {
                if (commaIndex < 0) {
                    n1 = Integer.parseInt(elements[0].substring(0, index));
                    n2 = n1;
                } else {
                    nStr = elements[0].substring(0, commaIndex);
                    if (checkEmpty(nStr, elements[0])) return;
                    n1 = Integer.parseInt(nStr);
                    nStr = elements[0].substring(commaIndex+1, index);
                    if (checkEmpty(nStr, elements[0])) return;
                    n2 = Integer.parseInt(nStr);
                }
                index++;
                commaIndex = elements[0].indexOf(',', index);
                if (commaIndex < 0) {
                    nStr = elements[0].substring(index, elements[0].length());
                    if (checkEmpty(nStr, elements[0])) return;
                    n3 = Integer.parseInt(nStr);
                    n4 = n3;
                } else {
                    nStr = elements[0].substring(index, commaIndex);
                    if (checkEmpty(nStr, elements[0])) return;
                    n3 = Integer.parseInt(nStr);
                    nStr = elements[0].substring(commaIndex+1, elements[0].length());
                    if (nStr == null || nStr.length() == 0) n4 = n3;
                    else n4 = Integer.parseInt(nStr);
                }
            } catch (NumberFormatException e) {
                if (this.stderrListener != null) {
                    String[] debugOut = { "NumberFormatException "+e.getMessage() };
                    stderrListener.outputData(debugOut);
                }
                return;
            }
            addDifference(new Difference(Difference.CHANGE, n1, n2, n3, n4));
            //diff.addChangeAction(n1, n2, n3, n4);
        }
    }

    /**
     * Matches output in CA-LIBRARIAN format.
     * @params elements The input to parse.
     */
    public void matchCALibrarian(String[] elements) {
    }

    /**
     * Matches output compatible with cvs diff command.
     * @params elements The input to parse.
     */
    public void match0(String[] elements) {
        if (elements[0].indexOf(CVS_REVISION_STR) == 0) {
            String rev = elements[0].substring(CVS_REVISION_STR.length()).trim();
            if (diffOutRev1 == null) diffOutRev1 = rev;
            else diffOutRev2 = rev;
        } else {
            matchUnixLike(elements);
        }
    }
    
    public void match2(String[] elements) {
        if (elements[0].indexOf(VSS_DIFFING) == 0) {
            if (elements[0].charAt(VSS_DIFFING.length()) == '$') {
                int index = elements[0].lastIndexOf(';');
                if (index > 0) {
                    String rev = elements[0].substring(index + 1);
                    diffOutRev1 = rev;
                }
            }
        } else if (elements[0].indexOf(VSS_AGAINST) == 0) {
            if (elements[0].charAt(VSS_AGAINST.length()) == '$') {
                int index = elements[0].lastIndexOf(';');
                if (index > 0) {
                    String rev = elements[0].substring(index + 1);
                    diffOutRev2 = rev;
                }
            }
        } else {
            matchUnixLike(elements);
        }
    }

    /**
     * Matches output compatible with stcmd (StarTeam command) diff command.
     * @params elements The input to parse.
     */
    public void match1(String[] elements) {
        final int LINE1_POS = 8;
        final int LINE2_POS = 16;
        if (elements[0] == null || elements[0].length() < LINE2_POS) return;
        char firstChar = elements[0].charAt(0);
        if (firstChar != ' ' && firstChar != '-' && firstChar != '+' || elements[0].substring(0, 2).equals("--")) {
            lastLine1 = lastLine2 = 0;
            return;
        }
        int n1 = lastLine1;
        int n2 = lastLine2;
        String n1Str = elements[0].substring(1, LINE1_POS).trim();
        String n2Str = elements[0].substring(LINE1_POS, LINE2_POS).trim();
        try {
            if (firstChar != '+') n1 = Integer.parseInt(n1Str);
            if (firstChar != '-') n2 = Integer.parseInt(n2Str);
        } catch (NumberFormatException e) {
            if (this.stderrListener != null) {
                String[] debugOut = { "NumberFormatException "+e.getMessage() };
                stderrListener.outputData(debugOut);
            }
        }
        lastLine1 = n1;
        lastLine2 = n2;
        if (elements[0].charAt(0) == '+' && elements[0].charAt(1) != '-') {     // ADD
            if (lastDiff > 0) return; // last was add
            else if (lastDiff < 0) {  // last was delete, it ends now
                //DiffAction action = new DiffAction();
                //action.setDeleteAction(lastDiffLine1, n1, lastDiffLine2);
                addDifference(new Difference(Difference.DELETE, lastDiffLine1, n1, lastDiffLine2, 0));
                //diff.addDeleteAction(lastDiffLine1, n1, lastDiffLine2);
                //diffActions.add(action);
            }
            lastDiff = 1;  // anything > 0
            lastDiffLine1 = n1;
            lastDiffLine2 = n2;
        } else if (elements[0].charAt(0) == '-' && elements[0].charAt(1) != '-') { // DELETE
            if (lastDiff < 0) return; // last was delete
            else if (lastDiff > 0) {  // last was add, it ends now
                //DiffAction action = new DiffAction();
                //action.setAddAction(lastDiffLine1, lastDiffLine2, n2);
                addDifference(new Difference(Difference.ADD, lastDiffLine1, 0, lastDiffLine2, n2));
                //diff.addAddAction(lastDiffLine1, lastDiffLine2, n2);
                //diffActions.add(action);
            }
            lastDiff = -1;  // anything < 0
            lastDiffLine1 = n1;
            lastDiffLine2 = n2;
        } else {
            if (lastDiff > 0) {        // ADD
                //DiffAction action = new DiffAction();
                //action.setAddAction(lastDiffLine1, lastDiffLine2, n2 - 1);
                addDifference(new Difference(Difference.ADD, lastDiffLine1, 0, lastDiffLine2, n2 - 1));
                //diff.addAddAction(lastDiffLine1, lastDiffLine2, n2 - 1);
                //diffActions.add(action);
            } else if (lastDiff < 0) { // DELETE
                //DiffAction action = new DiffAction();
                //action.setDeleteAction(lastDiffLine1, n1 - 1, lastDiffLine2);
                addDifference(new Difference(Difference.DELETE, lastDiffLine1, n1 - 1, lastDiffLine2, 0));
                //diff.addDeleteAction(lastDiffLine1, n1 - 1, lastDiffLine2);
                //diffActions.add(action);
            }
            lastDiff = 0;
        }
    }
    
    private void addCADiffAction() {
        switch (lastDiff) {
            case  1: addDifference(new Difference(Difference.CHANGE, lastLine1, lastLine2, lastDiffLine1, lastDiffLine2, null, secondText.toString()));
                     //diff.addChangeAction(lastLine1, lastLine2, lastDiffLine1, lastDiffLine2);
                     caDocShift -= lastLine2 - lastLine1 - (lastDiffLine2 - lastDiffLine1);
                     //System.out.println("Change("+lastLine1+", "+lastLine2+", "+lastDiffLine1+", "+lastDiffLine2+"), caDocShift = "+caDocShift);
                     break;
            case -1: addDifference(new Difference(Difference.DELETE, lastLine1, lastLine2, lastDiffLine1, 0, null, secondText.toString()));
                     //diff.addDeleteAction(lastLine1, lastLine2, lastDiffLine1);
                     caDocShift -= lastLine2 + 1 - lastLine1;
                     //System.out.println("Delete("+lastLine1+", "+lastLine2+", "+lastDiffLine1+"), caDocShift = "+caDocShift);
                     break;
            case  2: addDifference(new Difference(Difference.ADD, lastLine1, 0, lastDiffLine1, lastDiffLine2, null, secondText.toString()));
                     //diff.addAddAction(lastLine1, lastDiffLine1, lastDiffLine2);
                     caDocShift += lastDiffLine2 + 1 - lastDiffLine1;
                     //System.out.println("Add("+lastLine1+", "+lastDiffLine1+", "+lastDiffLine2+"), caDocShift = "+caDocShift);
                     break;
        }
        secondText.delete(0, secondText.length());
        lastDiff = 0;
    }

    /**
     * Matches output of PVCS vdiff in CA-LIBRARIAN format.
     * @params elements The input to parse.
     */
    public void match3(String[] elements) {
        //System.out.println("match3("+elements[0]+")");
        if (elements.length == 0) return ;
        if (elements[0] == null) elements[0] = "";
        int n1, n2;
        if (elements[0].indexOf(CA_REP) == 0) {
            if (lastDiff != 0) addCADiffAction();
            int index = CA_REP.length();
            int comma = elements[0].indexOf(',', index);
            if (comma < 0) return ;
            try {
                n1 = Integer.parseInt(elements[0].substring(index, comma));
                n2 = Integer.parseInt(elements[0].substring(comma + 1));
            } catch (NumberFormatException e) {
                if (this.stderrListener != null) {
                    String[] debugOut = { "NumberFormatException "+e.getMessage() };
                    stderrListener.outputData(debugOut);
                }
                return;
            }
            lastDiff = 1; // Change
            lastLine1 = n1; // lines in the first document
            lastLine2 = n2; // lines in the first document
            lastDiffLine1 = n1 + caDocShift;
            lastDiffLine2 = lastDiffLine1 - 1;
        } else if (elements[0].indexOf(CA_INS) == 0) {
            if (lastDiff != 0) addCADiffAction();
            int index = CA_INS.length();
            try {
                n1 = Integer.parseInt(elements[0].substring(index));
            } catch (NumberFormatException e) {
                if (this.stderrListener != null) {
                    String[] debugOut = { "NumberFormatException "+e.getMessage() };
                    stderrListener.outputData(debugOut);
                }
                return;
            }
            lastDiff = 2; // Add
            lastLine1 = n1; // lines in the first document
            //lastLine2 = n2; // lines in the first document
            lastDiffLine1 = n1 + 1+ caDocShift;
            lastDiffLine2 = lastDiffLine1 - 1;
        } else if (elements[0].indexOf(CA_DEL) == 0) {
            if (lastDiff != 0) addCADiffAction();
            int index = CA_DEL.length();
            int comma = elements[0].indexOf(',', index);
            try {
                if (comma >= 0) {
                    n1 = Integer.parseInt(elements[0].substring(index, comma));
                    n2 = Integer.parseInt(elements[0].substring(comma + 1));
                } else {
                    n1 = Integer.parseInt(elements[0].substring(index));
                    n2 = n1;
                }
            } catch (NumberFormatException e) {
                if (this.stderrListener != null) {
                    String[] debugOut = { "NumberFormatException "+e.getMessage() };
                    stderrListener.outputData(debugOut);
                }
                return;
            }
            lastDiff = -1; // Delete
            lastLine1 = n1; // lines in the first document
            lastLine2 = n2; // lines in the first document
            lastDiffLine1 = n1 - 1 + caDocShift;
            lastDiffLine2 = lastDiffLine1;
            addCADiffAction();
        } else {
            lastDiffLine2++;
            // All text output concerns the second file.
            secondText.append(elements[0] + "\n");
        }
    }

    /** Perform a cleanup of actions after diff finishes.
     */
    protected void diffFinished() {
        switch(outputType) {
        case 3: // PVCS
            if (lastDiff != 0) addCADiffAction();
            break;
        }
        if (firstText.length() > 0 || secondText.length() > 0) {
            setTextOnLastDifference(firstText.toString(), secondText.toString());
        }
    }
    
    protected String getTitleHeadRevision() {
        return NbBundle.getMessage(Diff.class, "Diff.titleHeadRevision");
    }
    
    protected String getTitleWorkingRevision() {
        return NbBundle.getMessage(Diff.class, "Diff.titleWorkingFile");
    }
    
    protected String getTitleRevision(String revNumber) {
        return NbBundle.getMessage(Diff.class, "Diff.titleRevision", revNumber);
    }
    
}
