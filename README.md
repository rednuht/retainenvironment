# Retain Environment Plugin

This plugin lets you retain the environment between build steps.

## How it works
From a user specified path the plugin will provide an environment file name. This file name is then injected into the build environment as **ENVFILE**.
It is then up to the user to save the environment variables to this file as key value pairs separated with an equals sign(=).

The file is read after each build step execution and the environment variables are injected into the build environment. At this point the plugin deletes the file to automatically clean up after itself.

### Environment file details
The file is named on the following format: **\<job name>-\<build number>.env** and is stored at the path specified by the user.

### Environment path details
The path specified by the user can contain environment variables which are expanded before the file name is built and later injected.

