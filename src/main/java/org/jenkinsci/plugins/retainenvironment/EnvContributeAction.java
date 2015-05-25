package org.jenkinsci.plugins.retainenvironment;

import hudson.EnvVars;
import hudson.model.AbstractBuild;
import hudson.model.EnvironmentContributingAction;

import java.util.Map;

/**
 *
 * @author Daniel Olausson
 */
class EnvContributeAction implements EnvironmentContributingAction {

    private final transient Map<String, String> envMap;

    public EnvContributeAction(final Map<String, String> envMap) {
        this.envMap = envMap;
    }

    @Override
    public void buildEnvVars(final AbstractBuild<?, ?> build, final EnvVars env) {
        if (env == null || envMap == null) {
            return;
        }

        for (Map.Entry<String, String> entry : envMap.entrySet()) {
            env.put(entry.getKey(), entry.getValue());
        }
    }

    public void putAllEnvsToMap(final Map<String, String> map) {
        if (envMap != null) {
            envMap.putAll(map);
        }
    }

    public String get(final String key) {
        if (envMap != null) {
            return envMap.get(key);
        }

        return null;
    }

    @Override
    public String getIconFileName() {
        return null;
    }

    @Override
    public String getDisplayName() {
        return "EnvContributeAction";
    }

    @Override
    public String getUrlName() {
        return null;
    }
}
