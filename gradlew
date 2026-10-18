#!/usr/bin/env sh

<i>#
# Copyright 2015 the original author or authors.
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#      https://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#

dirname_if_possible() {
    local target="$1"
    if [ -d "$target" ]; then
        cd "$target" && pwd -P
    else
        cd "$(dirname "$target")" && pwd -P
    fi
}

APP_BASE_NAME=$(basename "$0")
APP_HOME=$(dirname_if_possible "$0")

# Use the maximum available, or set MAX_FD != -1 to use that value
MAX_FD="maximum"

warn () {
    echo "$*"
}

die () {
    echo
    echo "$*"
    echo
    exit 1
}

# Determine the Java command to use to start the JVM.
if [ -n "$JAVA_HOME" ] ; then
    if [ -x "$JAVA_HOME/jre/sh/java" ] ; then
        # IBM's JDK on AIX uses this
        JAVACMD="$JAVA_HOME/jre/sh/java"
    else
        JAVACMD="$JAVA_HOME/bin/java"
    fi
    if [ ! -x "$JAVACMD" ] ; then
        die "ERROR: JAVA_HOME is set to an invalid directory: $JAVA_HOME

Please set the JAVA_HOME variable in your environment to match the
location of your Java installation."
    fi
else
    JAVACMD="java"
    which java >/dev/null 2>&1 || die "ERROR: JAVA_HOME is not set and no 'java' command could be found in your PATH.

Please set the JAVA_HOME variable in your environment to match the
location of your Java installation."
fi

# Increase the maximum file descriptors if we can
if [ "$os_name" = "CYGWIN" -o "$os_name" = "MSYS" ] ; then
    MAX_FD=maximum
fi

# Determine Gradle home - use wrapper
GRADLE_USER_HOME="${GRADLE_USER_HOME:-$HOME/.gradle}"
wrapperJar="$APP_HOME/gradle/wrapper/gradle-wrapper.jar"
wrapperProperties="$APP_HOME/gradle/wrapper/gradle-wrapper.properties"

# For Cygwin, switch paths to Windows format before running java
if [ "$os_name" = "CYGWIN" -o "$os_name" = "MSYS" ] ; then
    APP_HOME=`cygpath --path --mixed "$APP_HOME"`
    JAVACMD=`cygpath --unix "$JAVACMD"`
fi

# Execute "gradle"
exec "$JAVACMD" \
    -Dorg.gradle.appname="$APP_BASE_NAME" \
    -classpath "$wrapperJar" \
    org.gradle.wrapper.GradleWrapperMain \
    "$@"
