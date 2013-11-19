/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.netbeans.modules.remote.server;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.netbeans.api.annotations.common.NonNull;
import org.netbeans.api.sendopts.CommandException;
import org.netbeans.spi.sendopts.Env;
import org.netbeans.spi.sendopts.Option;
import org.netbeans.spi.sendopts.OptionProcessor;
import org.openide.util.Exceptions;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Tomas Zezula
 */
@ServiceProvider(service = OptionProcessor.class)
public class Options extends OptionProcessor {

    private static final int DEFAULT_PORT = 8001;

    private static final Option PORT = Option.requiredArgument(
        Option.NO_SHORT_NAME,
        "port");    //NOI18N

    @Override
    protected Set<Option> getOptions() {
        return Collections.unmodifiableSet(new HashSet<Option>(Arrays.asList(
            new Option[]{
                PORT,
                Option.always()
            })));
    }

    @Override
    protected void process(Env env, Map<Option, String[]> optionValues) throws CommandException {
        int port = DEFAULT_PORT;
        final String[] portStr = optionValues.get(PORT);
        if (portStr != null) {
            if (portStr.length != 1) {
                error(env, -1, "No port given.");   //NOI18N
            }
            try {
                port = Integer.parseInt(portStr[0]);
                if (port < 1024 || port > 0xffff) {
                    throw new NumberFormatException();
                }
            } catch (NumberFormatException nfe) {
                error(env, -2, "Invalid port: " + portStr[0]);  //NOI18N
            }            
        }
        try {
            JavaServices.getInstance().configure(port);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
    }


    private static void error(
        @NonNull final Env env,
        final int retCode,
        @NonNull final String message) throws CommandException {
        env.usage();
        throw new CommandException(retCode, message);
    }

}
