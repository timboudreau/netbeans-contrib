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

package org.netbeans.modules.vcs.advanced.commands;

import java.util.*;

import org.netbeans.modules.vcscore.VcsConfigVariable;
import org.netbeans.modules.vcscore.VcsFileSystem;
import org.netbeans.modules.vcscore.cmdline.UserCommand;
import org.netbeans.modules.vcscore.cmdline.UserCommandSupport;
import org.netbeans.modules.vcscore.commands.*;
import org.netbeans.modules.vcscore.util.VcsUtilities;
import org.openide.ErrorManager;

/**
 * This class provides input of commands from property file for compatibility
 * with old versions.
 *
 * @author  Martin Entlicher
 */
public class UserCommandIOCompat extends Object {
    
    /**
     * Label of the command which will not appear in the popup menu.
     */
    private static final String DISPLAY_NAME_NOT_SHOW = "NO_LABEL";

    
    public static CommandsTree readCommands(Properties props, VcsFileSystem fileSystem) {
        ArrayList commands = new ArrayList();
        //try {
            commands = readUserCommands(props/*, org.netbeans.modules.vcscore.cmdline.UserCommand.class*/);
            /*
        } catch (InstantiationException instexc) {
            TopManager.getDefault().notifyException(instexc);
        } catch (IllegalAccessException iaexc) {
            TopManager.getDefault().notifyException(iaexc);
        }
             */
        //Children children = new Children.Array();
        UserCommand rootCmd = new UserCommand();
        rootCmd.setName("THE_ROOT_CMD");
        rootCmd.setDisplayName((String) props.getProperty("label"));
        CommandsTree commandsNode = new CommandsTree(new UserCommandSupport(rootCmd, fileSystem));
        //VcsCommandNode commandsNode = new VcsCommandNode(children, rootCmd);
        createMainCommandNodes(commandsNode, commands, fileSystem);
        return commandsNode;
    }
    

    /**
     * Read commands from properies. All properties of the form <code>cmd.Command_Name.Command_Property</code>
     * are stored into commands. All properties of the form <code>cmdl.Command_Name.Command_Property</code>
     * are stored as "empty commands" with only label and orderArr properies. These are the labels for command subsets.
     * @param props the properties to read
     * @param clazz the class type of the command.
     * @return the Vector of commands
     */
    public static ArrayList readUserCommands(Properties props) {//throws InstantiationException, IllegalAccessException {
        ArrayList result = new ArrayList(20);

        for(Iterator iter = props.keySet().iterator(); iter.hasNext();){
            String key = (String) iter.next();
            String cmdStr = null;
            if (key.startsWith("cmd.") == true && // NOI18N
                key.endsWith(".label") == true) { // NOI18N
                    cmdStr = "cmd.";
            }
            if (key.startsWith("cmdl.") == true && // NOI18N
                key.endsWith(".label") == true) { // NOI18N
                    cmdStr = "cmdl.";
            }
            if (cmdStr != null) {
                int startIndex = cmdStr.length(); // NOI18N
                int endIndex = key.length() - ".label".length(); // NOI18N

                String name = key.substring(startIndex, endIndex);
                String label = (String) props.get(key);

                UserCommand uc;
                if ("cmd.".equals(cmdStr)) {
                    uc = new UserCommand(name);
                } else {
                    uc = new UserCommand();
                }
                //Object command = clazz.newInstance();
                //if (!(command instanceof VcsCommand)) throw new InstantiationException("Bad class type. Not instance of VcsCommand.");
                //VcsCommand vc = (VcsCommand) command;
                uc.setName(name);
                if (DISPLAY_NAME_NOT_SHOW.equalsIgnoreCase(label)) {
                    uc.setDisplayName(null);
                } else {
                    uc.setDisplayName(label);
                }
                String orderStr = (String) props.get(cmdStr + name + ".order"); // NOI18N
                if (orderStr == null) orderStr = "-1";
                //D.deb("Parsing orderArr ("+uc.getName()+") = "+orderArr);
                int[] orderArr = UserCommandIOCompat.parseOrder(orderStr);
                if (orderArr == null) {
                    orderArr = new int[1];
                    orderArr[0] = -1;
                }
                //D.deb("Setting orderArr = "+UserCommand.getorderArrString(orderArrArr));
                uc.setOrder(orderArr);
                if ("cmd.".equals(cmdStr)) {
                    //BeanInfo info = vc.createCommandInfo();
                    fillCommandProperties(name, uc, props);
                }
                result.add(uc);
            }
        }
        if (result.size() == 0) return result;
        result = sortCommands(result);
        //D.deb("going to set the orderArr ..."); // NOI18N
        setOrder(result);
        UserCommand.readFinished(result);
        /*
        Method finishMethod = null;
        try {
            finishMethod = clazz.getDeclaredMethod("readFinished", new Class[] { java.util.Vector.class });
        } catch (NoSuchMethodException exc) {
            TopManager.getDefault().notifyException(exc);
        } catch (SecurityException sexc) {
            TopManager.getDefault().notifyException(sexc);
        }
        try {
            if (finishMethod != null) finishMethod.invoke(firstCommand, new Object[] { result });
        } catch (IllegalArgumentException exc) {
            TopManager.getDefault().notifyException(exc);
        } catch (InvocationTargetException itexc) {
            TopManager.getDefault().notifyException(itexc);
        }
         */
        return result;
    }

    private static void fillCommandProperties(String name, VcsCommand vc, Properties props) {
        String[] propertyNames = vc.getPropertyNames();
        //System.out.println("fillCommandProperties("+name+"): propertyNames = "+propertyNames+", prop.length = "+propertyNames.length);
        //PropertyDescriptor[] descriptors = info.getPropertyDescriptors();
        for(int j = 0; j < propertyNames.length; j++) {
            String attrName = propertyNames[j];
            String attrValue = (String) props.get("cmd." + name + "." + attrName);
            //System.out.println("attrName = "+attrName+", attrValue = "+attrValue);
            //Class propClazz = descriptors[j].getPropertyType();
            Object value;
            if (attrValue != null) {
                Object oldValue = vc.getProperty(attrName);
                if (oldValue instanceof Boolean) {
                    if (attrValue.equalsIgnoreCase("TRUE")) {
                        value = Boolean.TRUE;
                    } else if (attrValue.equalsIgnoreCase("FALSE")) {
                        value = Boolean.FALSE;
                    } else {
                        value = null;
                    }
                } else if (oldValue instanceof Integer) {
                    try {
                        int intValue = Integer.parseInt(attrValue);
                        value = new Integer(intValue);
                    } catch (NumberFormatException exc) {
                        ErrorManager.getDefault().notify(exc);
                        value = null;
                    }
                } else {
                    value = attrValue;
                }
            } else {
                value = attrValue;
            }
            /*
            } else if (propClazz.equals(Vector.class)) {
                Vector vector = new Vector();
                StringTokenizer tokenizer = new StringTokenizer(attrValue, ",");
                while(tokenizer.hasMoreTokens()) {
                    String token = tokenizer.nextToken();
                    if (token.length() > 0 && token.charAt(0) == ']') {
                        token = token.substring(1);
                    }
                    if (token.endsWith("]")) {
                        token = token.substring(0, token.length() - 1);
                    }
                    vector.add(token);
                }
                value = vector;
            }
             */
            /* The property ON_ROOT changed its meaning !! It means ALSO on root from now !! */
            if (VcsCommand.PROPERTY_ON_ROOT.equals(attrName)) {
                if (VcsCommandIO.getBooleanPropertyAssumeTrue(vc, VcsCommand.PROPERTY_ON_DIR)) {
                    if (Boolean.TRUE.equals(value)) {
                        vc.setProperty(VcsCommand.PROPERTY_ON_DIR, Boolean.FALSE);
                        vc.setProperty(VcsCommand.PROPERTY_ON_FILE, Boolean.FALSE);
                    }
                    value = Boolean.TRUE;
                }
            }
            if (VcsCommand.PROPERTY_ON_DIR.equals(attrName)) {
                vc.setProperty(VcsCommand.PROPERTY_ON_ROOT, Boolean.TRUE);
            }
            vc.setProperty(attrName, value);
            //System.out.println("setting property of '"+vc+"': "+attrName+" = '"+value+"'");
        }
    }

    /**
     * Sort a vector of commands or variables by the orderArr property.
     * @param commands the commands or variables to sort
     * @return new sorted vector of commands or variables
     */
    public static ArrayList sortCommands(ArrayList commands) {
        //D.deb("sortCommands ()"); // NOI18N
        ArrayList sorted;
        //D.deb("commands = "+ commands); // NOI18N
        if (commands == null) return commands;
        Object[] cmds = null;
        cmds = (Object[]) commands.toArray();
        //D.deb("Doing sort ..."); // NOI18N
        java.util.Arrays.sort(cmds, new Comparator() {
                                  public int compare(Object o1, Object o2) {
                                      if (o1 instanceof UserCommand) {
                                          int[] orderArr1 = ((UserCommand) o1).getOrder();
                                          int[] orderArr2 = ((UserCommand) o2).getOrder();
                                          int l1 = orderArr1.length;
                                          int l2 = orderArr2.length;
                                          for(int i = 0; i < l1 && i < l2; i++) {
                                              if (orderArr1[i] < orderArr2[i]) return -1;
                                              if (orderArr1[i] > orderArr2[i]) return +1;
                                          }
                                          // THIS SHOULD NOT OCCURE !!
                                          return 0;
                                          //return ((UserCommand) o1).getorderArr() - ((UserCommand) o2).getorderArr();
                                      }
                                      if (o1 instanceof VcsConfigVariable)
                                          return ((VcsConfigVariable) o1).getOrder() - ((VcsConfigVariable) o2).getOrder();
                                      return 0; // the elements are not known to me
                                  }
                                  public boolean equals(Object o) {
                                      return false;
                                  }
                              });
        //D.deb("Sort finished."); // NOI18N
        sorted = new ArrayList();
        for(int i = 0; i < cmds.length; i++) {
            sorted.add(cmds[i]);
        }
        //D.deb("sorted vector = "+sorted); // NOI18N
        return sorted;
    }

    public static void shiftCommands(ArrayList commands, int index, int shift) {
        for(int i = index; i < commands.size(); i++) {
            UserCommand uc = (UserCommand) commands.get(i);
            if (uc != null) {
                int[] orderArr = uc.getOrder();
                orderArr[0] += shift;
                uc.setOrder(orderArr);
            }
        }
    }

    /**
     * Set the orderArr property of each element in the vector to the
     * proper values if some values are negative.
     * The Vector has to be sorted by <CODE>sortCommands</CODE>.
     * @param commands the vector of <CODE>UserCommand</CODE> elements.
     */
    public static void setOrder(ArrayList commands) {
        //D.deb("setorderArr()"); // NOI18N
        int len = commands.size();
        if (len <= 0) return;
        int nonNegativeIndex = 0;
        UserCommand uc = (UserCommand) commands.get(nonNegativeIndex);
        while (uc != null && uc.getOrder()[0] < 0) {
            nonNegativeIndex++;
            uc = (nonNegativeIndex < len) ? (UserCommand) commands.get(nonNegativeIndex) : null;
        }
        //D.deb("nonNegativeIndex = "+nonNegativeIndex); // NOI18N
        if (nonNegativeIndex == 0) return; // All values are non negative
        if (uc != null) {
            int first = uc.getOrder()[0];
            if (first < nonNegativeIndex) {
                shiftCommands(commands, nonNegativeIndex, nonNegativeIndex - first);
            }
        }
        for(int i = 0; i < nonNegativeIndex; i++) {
            uc = (UserCommand) commands.get(i);
            //D.deb("setting orderArr for "+uc+" to "+i); // NOI18N
            int[] orderArr = {i};
            uc.setOrder(orderArr);
        }
    }

    private static void createMainCommandNodes(CommandsTree commandsNode, ArrayList commands,
                                               VcsFileSystem fileSystem) {
        if (commands.size() > 0) {
            VcsCommand cmd = (VcsCommand) commands.get(0);
            if (cmd.getPropertyNames().length == 0) {
                commands.remove(0);
                CommandsTree root = commandsNode;
                commandsNode = new CommandsTree(new UserCommandSupport((UserCommand) cmd, fileSystem));
                //Children children = commandsNode.getChildren();
                //commandsNode = new VcsCommandNode(new Children.Array(), cmd);
                //children.add(new VcsCommandNode[] { commandsNode });
                root.add(commandsNode);
            }
        }
        createCommandNodes(commandsNode, commands, 0, new int[0], fileSystem);
    }
    
    private static int createCommandNodes(CommandsTree commandsNode, ArrayList commands,
                                          int from, int[] lastOrder, VcsFileSystem fileSystem) {
        //Children children = commandsNode.getChildren();
        int len = commands.size();
        int l = lastOrder.length;
        int i;
        int lastOrderEnd = 0;
        for(i = from; i < len; i++) {
            UserCommand uc = (UserCommand) commands.get(i);
            int[] order = uc.getOrder();
            if (order.length <= l) break;
            int j = 0;
            for(; j < l; j++) {
                if (lastOrder[j] != order[j]) break;
            }
            if (j < l) break;
            for(int k = lastOrderEnd+1; k < order[l]; k++) {
                // SEPARATOR
                commandsNode.add(CommandsTree.EMPTY);
                //children.add (new VcsCommandNode[] { new VcsCommandNode(Children.LEAF, null) });
            }
            lastOrderEnd = order[l];
            if (order.length >= l + 2) {
                int[] suborder = new int[order.length - 1];
                for(int k = 0; k < order.length - 1; k++) {
                    suborder[k] = order[k];
                }
                //VcsCommandNode subCommands = new VcsCommandNode(new Children.Array(), uc);
                CommandsTree subCommands = new CommandsTree(new UserCommandSupport(uc, fileSystem));
                i += createCommandNodes(subCommands, commands, i + 1, suborder, fileSystem);
                commandsNode.add(subCommands);
                //children.add(new Node[] { subCommands });
            } else {
                commandsNode.add(new CommandsTree(new UserCommandSupport(uc, fileSystem)));
                //JMenuItem item = createItem(uc.getName());
                //children.add(new Node[] { new VcsCommandNode(Children.LEAF, uc) });
                //parent.add(item);
            }
        }
        return i - from;
    }

    /**
     * Get an array of integers from the String of integers separated by dots.
     * @param orderArrStr the String of integers separated by dots
     * @return the array of integers or null when the parsing fails
     */
    public static int[] parseOrder(String orderArrStr) {
        int index = 0;
        int len = VcsUtilities.numChars(orderArrStr, '.') + 1;
        //D.deb("parseorderArr("+orderArrStr+"): len = "+len);
        int[] orderArr = new int[len];
        for(int i = 0; index < orderArrStr.length(); i++) {
            int index2 = orderArrStr.indexOf('.', index);
            if (index2 < 0) index2 = orderArrStr.length();
            int num = -1;
            try {
                num = Integer.parseInt(orderArrStr.substring(index, index2));
            } catch (NumberFormatException e) {
                // There is no orderArr information
                return null;
            }
            orderArr[i] = num;
            //D.deb("orderArr["+i+"] = "+num);
            index = index2 + 1;
        }
        return orderArr;
    }

}
