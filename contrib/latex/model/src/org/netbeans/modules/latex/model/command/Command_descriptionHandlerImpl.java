/*
 *                          Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License Version
 * 1.0 (the "License"). You may not use this file except in compliance with
 * the License. A copy of the License is available at http://www.sun.com/
 *
 * The Original Code is the LaTeX module.
 * The Initial Developer of the Original Code is Jan Lahoda.
 * Portions created by Jan Lahoda_ are Copyright (C) 2002,2003.
 * All Rights Reserved.
 *
 * Contributor(s): Jan Lahoda.
 */
package org.netbeans.modules.latex.model.command;

import java.util.Stack;
import org.xml.sax.*;

/*package private*/ class Command_descriptionHandlerImpl implements Command_descriptionHandler {
    
    public static final boolean DEBUG = Boolean.getBoolean("netbeans.latex.commandpackages.debug");
    
    private Command.Param  param;
    private Command        command;
    private CommandPackage pack;
    private Environment    env;
    private Option         option;
    
    private Stack lastAttributable;
    private Stack lastArgumentable;
    private Stack lastElementable;
    
    public void start_argument(final AttributeList meta) throws SAXException {
        if (DEBUG) System.err.println("start_argument: " + meta);

        param = new Command.Param();
//        <!ATTLIST argument
//            type (free|mandatory|nonmandatory) #REQUIRED
//          >
        String type = meta.getValue("type");
        
        if ("free".equals(type)) {
            param.setType(Command.Param.FREE);
        } else {
            if ("mandatory".equals(type)) {
                param.setType(Command.Param.MANDATORY);
            } else {
                if ("nonmandatory".equals(type)) {
                    param.setType(Command.Param.NONMANDATORY);
                } else {
                    if ("special".equals(type)) {
                        param.setType(Command.Param.SPECIAL);
                    } else {
                        throw new IllegalArgumentException("Unknown value of the type attribute: " + type);
                    }
                }
            }
        }
        
        lastAttributable.push(param);
    }
    
    public void end_argument() throws SAXException {
        if (DEBUG) System.err.println("end_argument()");
        
        lastAttributable.pop();
        ((NamedAttributableWithArguments) lastArgumentable.peek()).getArguments().add(param);
        param = null;
    }
    
    public void start_command(final AttributeList meta) throws SAXException {
        if (DEBUG) System.err.println("start_command: " + meta);
        
        command = new Command();
//        <!ATTLIST command
//            name CDATA #REQUIRED
//          >
        
        command.setName(meta.getValue("name"));
        
        lastAttributable.push(command);
        lastArgumentable.push(command);
        lastElementable.push(command);
    }
    
    public void end_command() throws SAXException {
        if (DEBUG) System.err.println("end_command()");
        
        lastAttributable.pop();
        lastArgumentable.pop();
        lastElementable.pop();

        ((NamedAttributableWithSubElements) lastElementable.peek()).getCommands().put(command.getCommand(), command);
    }
    
    public void start_commands(final AttributeList meta) throws SAXException {
        if (DEBUG) System.err.println("start_commands: " + meta);
    }
    
    public void end_commands() throws SAXException {
        if (DEBUG) System.err.println("end_commands()");
    }
    
    public void start_option(final AttributeList meta) throws SAXException {
        if (DEBUG) System.err.println("start_option: " + meta);
        
        option = new Option();
        
        option.setName(meta.getValue("name"));
        lastAttributable.push(option);
    }
    
    public void end_option() throws SAXException {
        if (DEBUG) System.err.println("end_option()");
        
        lastAttributable.pop();
        
        pack.getOptions().put(option.getName(), option);
    }
    
    public void start_attribute(final AttributeList meta) throws SAXException {
        if (DEBUG) System.err.println("start_attribute: " + meta);
        
        ((AttributableImpl) lastAttributable.peek()).getAttributes().put(meta.getValue("name"), meta.getValue("value"));
    }
    
    public void end_attribute() throws SAXException {
        if (DEBUG) System.err.println("end_attribute()");
    }
    
    public void start_file(final AttributeList meta) throws SAXException {
        if (DEBUG) System.err.println("start_file: " + meta);
        
        lastAttributable = new Stack();
        lastArgumentable = new Stack();
        lastElementable  = new Stack();
        
        pack = new CommandPackage();
        
//        <!ELEMENT file (description?,attribute*,options,environments,commands)*>
//        <!ATTLIST file
//            name CDATA #REQUIRED
//            type (package|docclass) #REQUIRED
//          >

        pack.setName(meta.getValue("name"));
        
        String type = meta.getValue("type");
        
        if ("package".equals(type)) {
            pack.setType(CommandPackage.PACKAGE);
        } else {
           if ("docclass".equals(type)) {
               pack.setType(CommandPackage.DOCUMENT_CLASS);
           } else {
               throw new IllegalArgumentException("Unknown command package type: " + type);
           }
        }
        
        lastAttributable.push(pack);
        lastElementable.push(pack);
    }
    
    public void end_file() throws SAXException {
        if (DEBUG) System.err.println("end_file()");
        lastAttributable.pop();
        lastAttributable = null;
        lastArgumentable = null;
        lastElementable.pop();
        lastElementable = null;
    }
    
    public void handle_description(final String data, final AttributeList meta) throws SAXException {
        if (DEBUG) System.err.println("handle_description: " + meta);
        
        ((AttributableImpl) lastAttributable.pop()).setDescription(data);
    }
    
    public void start_environment(final AttributeList meta) throws SAXException {
        if (DEBUG) System.err.println("start_environment: " + meta);
        
        env = new Environment();
        
        env.setName(meta.getValue("name"));
        
        lastAttributable.push(env);
        lastArgumentable.push(env);
        lastElementable.push(env);
    }
    
    public void end_environment() throws SAXException {
        if (DEBUG) System.err.println("end_environment()");
        

        lastAttributable.pop();
        lastArgumentable.pop();
        lastElementable.pop();
        ((NamedAttributableWithSubElements) lastElementable.peek()).getEnvironments().put(env.getName(), env);
    }
    
    public void start_environments(final AttributeList meta) throws SAXException {
        if (DEBUG) System.err.println("start_environments: " + meta);
    }
    
    public void end_environments() throws SAXException {
        if (DEBUG) System.err.println("end_environments()");
    }
    
    public void start_options(final AttributeList meta) throws SAXException {
        if (DEBUG) System.err.println("start_options: " + meta);
    }
    
    public void end_options() throws SAXException {
        if (DEBUG) System.err.println("end_options()");
    }
    
    public CommandPackage getCommandPackage() {
        return pack;
    }

    public void handle_include(final AttributeList meta) throws SAXException {
        if (DEBUG) System.err.println("handle_include: " + meta);
        
        pack.getIncludes().add(meta.getValue("name"));
    }
    
    public void start_includes(final AttributeList meta) throws SAXException {
        if (DEBUG) System.err.println("start_includes: " + meta);
    }
    
    public void end_includes() throws SAXException {
        if (DEBUG) System.err.println("end_includes()");
    }

    public void end_value() throws SAXException {
        if (DEBUG) System.err.println("end_value()");
    }
    
    public void start_value(AttributeList meta) throws SAXException {
        if (DEBUG) System.err.println("start_value: " + meta);
        
        param.addValue(meta.getValue("value"));
    }
    
}
