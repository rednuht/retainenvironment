package org.jenkinsci.plugins.retainenvironment;

import hudson.Extension;
import hudson.model.AbstractProject;
import hudson.model.JobProperty;
import hudson.model.JobPropertyDescriptor;
import org.kohsuke.stapler.DataBoundConstructor;

/**
 * @author Daniel Olausson
 */
public class RetainEnvironmentProperty extends JobProperty<AbstractProject<?, ?>> {

    private final boolean active;
    private final String path;

    @DataBoundConstructor
    public RetainEnvironmentProperty(final boolean active, final String path) {
        this.active = active;
        this.path = path;
    }

    public boolean isActive() {
        return active;
    }

    public String getPath() {
        return path;
    }

    @Extension
    public static class DescriptorImpl extends JobPropertyDescriptor {
        @Override
        public String getDisplayName() {
            return "Environment Retainer";
        }

        @Override
        public boolean isApplicable(final Class jobType) {
            return AbstractProject.class.isAssignableFrom(jobType);
        }

        // TODO: create formvalidation that cannot leak files...
    }
}
