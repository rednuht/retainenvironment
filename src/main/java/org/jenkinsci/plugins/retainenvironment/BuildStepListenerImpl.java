package org.jenkinsci.plugins.retainenvironment;

import hudson.EnvVars;
import hudson.Extension;
import hudson.model.AbstractBuild;
import hudson.model.BuildListener;
import hudson.model.BuildStepListener;
import hudson.tasks.BuildStep;
import hudson.util.LogTaskListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;


/**
 * @author Daniel Olausson
 */
@Extension
public class BuildStepListenerImpl extends BuildStepListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(BuildStepListenerImpl.class);
    private static final String RETAIN_ENV_FILE_KEY_NAME = "ENVFILE";

    @Override
    public void started(final AbstractBuild build, final BuildStep bs, final BuildListener listener) {
        final RetainEnvironmentProperty retainEnvironmentProperty = getEnvPropertyForBuild(build);
        if (retainEnvironmentProperty.isActive()) {
            EnvContributeAction envContributeAction = build.getAction(EnvContributeAction.class);
            // first build step
            if (envContributeAction == null) {
                EnvVars envVars = null;
                try {
                    envVars = build.getEnvironment(new LogTaskListener(java.util.logging.Logger.getLogger(BuildStepListenerImpl.class.getName()), Level.INFO));
                } catch (IOException | InterruptedException e) {
                    LOGGER.warn("Cannot get envVars from build", e);
                }

                if (envVars != null) {
                    final String path = envVars.expand(retainEnvironmentProperty.getPath());
                    final File dirs = new File(path);
                    dirs.mkdirs();
                    final File file = new File(dirs, build.getParent().getName() + "-" + build.getNumber() + ".env");

                    final Map<String, String> map = new HashMap<>();
                    map.put(RETAIN_ENV_FILE_KEY_NAME, file.getPath());

                    envContributeAction = new EnvContributeAction(map);
                    build.addAction(envContributeAction);
                }
            }
        }
    }

    @Override
    public void finished(final AbstractBuild build, final BuildStep bs, final BuildListener listener, final boolean canContinue) {
        final RetainEnvironmentProperty retainEnvironmentProperty = getEnvPropertyForBuild(build);
        if (retainEnvironmentProperty.isActive()) {
            final EnvContributeAction envContributeAction = build.getAction(EnvContributeAction.class);
            if (envContributeAction != null) {
                final String path = envContributeAction.get(RETAIN_ENV_FILE_KEY_NAME);
                final File envFile = new File(path);
                if (envFile.exists()) {
                    try {
                        final Map<String, String> map = new HashMap<>();
                        final List<String> lines = Files.readAllLines(envFile.toPath(), Charset.defaultCharset());
                        for (String line : lines) {
                            final String[] split = line.split("=");
                            if (split.length > 1) {
                                map.put(split[0].trim(), split[1].trim());
                            }
                        }
                        envContributeAction.putAllEnvsToMap(map);
                    } catch (IOException e) {
                        LOGGER.error("Error while reading retainer file", e);
                    }
                    envFile.delete();
                }
            }
        }
    }

    private RetainEnvironmentProperty getEnvPropertyForBuild(final AbstractBuild build) {
        return (RetainEnvironmentProperty) build.getProject().getProperty(RetainEnvironmentProperty.class);
    }
}
