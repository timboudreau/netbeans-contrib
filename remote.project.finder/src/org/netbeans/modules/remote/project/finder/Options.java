/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.netbeans.modules.remote.project.finder;

import java.io.File;
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
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Tomas Zezula
 */
@ServiceProvider(service = OptionProcessor.class)
public final class Options extends OptionProcessor {

    private static final int DEFAULT_DEPTH = 2;

    private static final Option WORK_SPACE = Option.requiredArgument(
        Option.NO_SHORT_NAME,
        "workspace"); //NOI18N

    private static final Option DEPTH = Option.requiredArgument(
        Option.NO_SHORT_NAME,
        "depth");

    public Options() {
    }    

    @Override
    @NonNull
    protected Set<Option> getOptions() {
        return Collections.unmodifiableSet(new HashSet<Option>(
            Arrays.asList(
                WORK_SPACE,
                DEPTH,
                Option.always())));
    }

    @Override
    protected void process(Env env, Map<Option, String[]> optionValues) throws CommandException {
        String[] folders = optionValues.get(WORK_SPACE);
        if (folders == null || folders.length != 1) {
            error(env, -1, "No workspace given.");
        }
        final File f = new File(folders[0]);
        if (!f.isDirectory() || !f.canRead()) {
            error(env, -2, "Workspace does not exist.");
        }
        int depth = DEFAULT_DEPTH;
        String[] depthStr = optionValues.get(DEPTH);
        if (depthStr != null) {
            if (depthStr.length != 1) {
                error(env, -3, "Wrong workspace depth.");
            }
            try {
                depth = Integer.parseInt(depthStr[0]);
                if (depth < 1) {
                    throw new NumberFormatException();
                }
            } catch (NumberFormatException nfe) {
                error(env, -3, "Wrong workspace depth.");
            }
        }
        WorkSpaceUpdater.getDefault().configure(f, depth);
    }

    private static void error(
        @NonNull final Env env,
        final int retCode,
        @NonNull final String message) throws CommandException {
        env.usage();
        throw new CommandException(retCode, message);
    }
}
