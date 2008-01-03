/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at http://www.netbeans.org/cddl.html
 * or http://www.netbeans.org/cddl.txt.
 *
 * When distributing Covered Code, include this CDDL Header Notice in each file
 * and include the License file at http://www.netbeans.org/cddl.txt.
 * If applicable, add the following below the CDDL Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package org.netbeans.modules.scala.editing.semantic;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Caoyuan Deng
 */
public class ErlBuiltIn {
    private static Map<String, Function> builtIns;
    
    public static Function getBuiltInFunction(String name, int arity) {
        if (builtIns == null) {
            create();
        }
        Function builtIn = builtIns.get(name + "/" + arity);
        if (builtIn != null) {
            /** @Notice: don't return a static Function instance, return a new one
             * Otherwise, the this instance's usages will be added again and again, even
             * a doc has been modified.
             */
            return new Function(name, arity);
        } else {
            return null;
        }
    }
    
    private static void create() {
        builtIns = new HashMap<String, Function>();
        
        addBuiltIn("abs"                         , 1);
        addBuiltIn("append_element"              , 2);
        addBuiltIn("apply"                       , 2);
        addBuiltIn("apply"                       , 3);
        addBuiltIn("atom_to_list"                , 1);
        addBuiltIn("binary_to_float"             , 1);
        addBuiltIn("binary_to_list"              , 1);
        addBuiltIn("binary_to_list"              , 3);
        addBuiltIn("binary_to_term"              , 1);
        addBuiltIn("bump_reductions"             , 1);
        addBuiltIn("cancel_timer"                , 1);
        addBuiltIn("check_process_code"          , 2);
        addBuiltIn("date"                        , 3);
        addBuiltIn("delete_module"               , 1);
        addBuiltIn("demonitor"                   , 1);
        addBuiltIn("demonitor"                   , 2);
        addBuiltIn("disconnect_node"             , 1);
        addBuiltIn("display"                     , 1);
        addBuiltIn("element"                     , 2);
        addBuiltIn("erase"                       , 0);
        addBuiltIn("erase"                       , 1);
        addBuiltIn("error"                       , 1);
        addBuiltIn("error"                       , 2);
        addBuiltIn("exit"                        , 1);
        addBuiltIn("exit"                        , 2);
        addBuiltIn("fault"                       , 1);
        addBuiltIn("fault"                       , 2);
        addBuiltIn("float"                       , 1);
        addBuiltIn("float_to_list"               , 1);
        addBuiltIn("fun_info"                    , 1);
        addBuiltIn("fun_info"                    , 2);
        addBuiltIn("fun_to_list"                 , 1);
        addBuiltIn("fun_exported"                , 3);
        addBuiltIn("garbage_collect"             , 0);
        addBuiltIn("garbage_collect"             , 1);
        addBuiltIn("get"                         , 0);
        addBuiltIn("get"                         , 1);
        addBuiltIn("get_cookie"                  , 0);
        addBuiltIn("get_keys"                    , 1);
        addBuiltIn("get_stacktrace"              , 0);
        addBuiltIn("group_leader"                , 0);
        addBuiltIn("group_leader"                , 2);
        addBuiltIn("halt"                        , 0);
        addBuiltIn("halt"                        , 1);
        addBuiltIn("hash"                        , 2);
        addBuiltIn("hd"                          , 1);
        addBuiltIn("hibernate"                   , 3);
        addBuiltIn("info"                        , 1);
        addBuiltIn("integer_to_list"             , 1);
        addBuiltIn("integer_to_list"             , 2);
        addBuiltIn("iolist_to_binary"            , 1);
        addBuiltIn("iolist_size"                 , 1);
        addBuiltIn("is_alive"                    , 0);
        addBuiltIn("is_builtin"                  , 3);
        addBuiltIn("is_process_alive"            , 1);
        addBuiltIn("length"                      , 1);
        addBuiltIn("link"                        , 1);
        addBuiltIn("list_to_atom"                , 1);
        addBuiltIn("list_to_binary"              , 1);
        addBuiltIn("list_to_existing_atom"       , 1);
        addBuiltIn("list_to_float"               , 1);
        addBuiltIn("list_to_integer"             , 1);
        addBuiltIn("list_to_integer"             , 2);
        addBuiltIn("list_to_pid"                 , 1);
        addBuiltIn("list_to_tuple"               , 1);
        addBuiltIn("load_module"                 , 2);
        addBuiltIn("loaded"                      , 0);
        addBuiltIn("loat_to_list"                , 0);
        addBuiltIn("localtime"                   , 0);
        addBuiltIn("localtime_to_universaltime"  , 1);
        addBuiltIn("localtime_to_universaltime"  , 2);
        addBuiltIn("make_ref"                    , 0);
        addBuiltIn("make_tuple"                  , 2);
        addBuiltIn("md5"                         , 1);
        addBuiltIn("md5_final"                   , 1);
        addBuiltIn("md5_init"                    , 0);
        addBuiltIn("md5_update"                  , 2);
        addBuiltIn("memory"                      , 0);
        addBuiltIn("memory"                      , 1);
        addBuiltIn("module_loaded"               , 1);
        addBuiltIn("monitor"                     , 2);
        addBuiltIn("monitor_node"                , 2);
        addBuiltIn("monitor_node"                , 3);
        addBuiltIn("ncat_binary"                 , 0);
        addBuiltIn("node"                        , 0);
        addBuiltIn("node"                        , 1);
        addBuiltIn("nodes"                       , 0);
        addBuiltIn("nodes"                       , 1);
        addBuiltIn("now"                         , 0);
        addBuiltIn("open_port"                   , 2);
        addBuiltIn("phash"                       , 2);
        addBuiltIn("phash2"                      , 1);
        addBuiltIn("phash2"                      , 2);
        addBuiltIn("pid_to_list"                 , 1);
        addBuiltIn("port_close"                  , 1);
        addBuiltIn("port_command"                , 2);
        addBuiltIn("port_connect"                , 2);
        addBuiltIn("port_control"                , 3);
        addBuiltIn("port_call"                   , 3);
        addBuiltIn("port_info"                   , 1);
        addBuiltIn("port_info"                   , 2);
        addBuiltIn("port_to_list"                , 1);
        addBuiltIn("ports"                       , 0);
        addBuiltIn("pre_loaded"                  , 0);
        addBuiltIn("process_display"             , 2);
        addBuiltIn("process_flag"                , 2);
        addBuiltIn("process_flag"                , 3);
        addBuiltIn("process_info"                , 1);
        addBuiltIn("process_info"                , 2);
        addBuiltIn("processes"                   , 0);
        addBuiltIn("purge_module"                , 1);
        addBuiltIn("put"                         , 2);
        addBuiltIn("read_timer"                  , 1);
        addBuiltIn("ref_to_list"                 , 1);
        addBuiltIn("register"                    , 2);
        addBuiltIn("registered"                  , 0);
        addBuiltIn("resume_process"              , 1);
        addBuiltIn("round"                       , 1);
        addBuiltIn("self"                        , 0);
        addBuiltIn("send"                        , 2);
        addBuiltIn("send"                        , 3);
        addBuiltIn("send_after"                  , 3);
        addBuiltIn("send_nosuspend"              , 2);
        addBuiltIn("send_nosuspend"              , 3);
        addBuiltIn("set_cookie"                  , 2);
        addBuiltIn("setelement"                  , 3);
        addBuiltIn("size"                        , 1);
        addBuiltIn("spawn"                       , 1);
        addBuiltIn("spawn"                       , 2);
        addBuiltIn("spawn"                       , 3);
        addBuiltIn("spawn"                       , 4);
        addBuiltIn("spawn_link"                  , 1);
        addBuiltIn("spawn_link"                  , 2);
        addBuiltIn("spawn_link"                  , 3);
        addBuiltIn("spawn_link"                  , 4);
        addBuiltIn("spawn_monitor"               , 1);
        addBuiltIn("spawn_monitor"               , 3);
        addBuiltIn("spawn_opt"                   , 2);
        addBuiltIn("spawn_opt"                   , 3);
        addBuiltIn("spawn_opt"                   , 4);
        addBuiltIn("spawn_opt"                   , 5);
        addBuiltIn("split_binary"                , 2);
        addBuiltIn("start_timer"                 , 3);
        addBuiltIn("statistics"                  , 1);
        addBuiltIn("suspend_process"             , 1);
        addBuiltIn("system_flag"                 , 2);
        addBuiltIn("system_info"                 , 1);
        addBuiltIn("system_monitor"              , 1);
        addBuiltIn("system_monitor"              , 2);
        addBuiltIn("term_to_binary"              , 1);
        addBuiltIn("term_to_binary"              , 2);
        addBuiltIn("throw"                       , 1);
        addBuiltIn("time"                        , 3);
        addBuiltIn("tl"                          , 1);
        addBuiltIn("trace"                       , 3);
        addBuiltIn("trace_delivered"             , 1);
        addBuiltIn("trace_info"                  , 2);
        addBuiltIn("trace_pattern"               , 2);
        addBuiltIn("trace_pattern"               , 3);
        addBuiltIn("trunc"                       , 1);
        addBuiltIn("tuple_to_list"               , 1);
        addBuiltIn("universaltime"               , 0);
        addBuiltIn("universaltime_to_localtime"  , 2);
        addBuiltIn("unlink"                      , 1);
        addBuiltIn("unregister"                  , 1);
        addBuiltIn("whereis"                     , 1);
        addBuiltIn("yield"                       , 0);
        
        addBuiltIn("is_atom"                     , 1);
        addBuiltIn("is_binary"                   , 1);
        addBuiltIn("is_boolean"                  , 1);
        addBuiltIn("is_constant"                 , 0);
        addBuiltIn("is_float"                    , 1);
        addBuiltIn("is_function"                 , 1);
        addBuiltIn("is_function"                 , 2);
        addBuiltIn("is_integer"                  , 1);
        addBuiltIn("is_list"                     , 1);
        addBuiltIn("is_number"                   , 1);
        addBuiltIn("is_pid"                      , 1);
        addBuiltIn("is_port"                     , 1);
        addBuiltIn("is_record"                   , 2);
        addBuiltIn("is_record"                   , 3);
        addBuiltIn("is_reference"                , 1);
        addBuiltIn("is_tuple"                    , 1);
    }
    
    private static void addBuiltIn(String name, int arity) {
        builtIns.put(name + "/" + arity, new Function(name, arity));
    }
}