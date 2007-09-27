#!/bin/sh
# DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
#
# Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
#
# The contents of this file are subject to the terms of either the GNU
# General Public License Version 2 only ("GPL") or the Common
# Development and Distribution License("CDDL") (collectively, the
# "License"). You may not use this file except in compliance with the
# License. You can obtain a copy of the License at
# http://www.netbeans.org/cddl-gplv2.html
# or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
# specific language governing permissions and limitations under the
# License.  When distributing the software, include this License Header
# Notice in each file and include the License file at
# nbbuild/licenses/CDDL-GPL-2-CP.  Sun designates this
# particular file as subject to the "Classpath" exception as provided
# by Sun in the GPL Version 2 section of the License file that
# accompanied this code. If applicable, add the following below the
# License Header, with the fields enclosed by brackets [] replaced by
# your own identifying information:
# "Portions Copyrighted [year] [name of copyright owner]"
#
# Contributor(s):
#
# The Original Software is NetBeans. The Initial Developer of the Original
# Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
# Microsystems, Inc. All Rights Reserved.
#
# If you wish your version of this file to be governed by only the CDDL
# or only the GPL Version 2, indicate your decision by adding
# "[Contributor] elects to include this software in this distribution
# under the [CDDL or GPL Version 2] license." If you do not indicate a
# single choice of license, a recipient has the option to distribute
# your version of this file under either the CDDL, the GPL Version 2 or
# to extend the choice of license to its licensees as provided above.
# However, if you add GPL Version 2 code and therefore, elected the GPL
# Version 2 license, then the option applies only if the new code is
# made subject to such option by the copyright holder.


# loader for rmid wrapper
#
# $Id$

#
# customization
#

# the value set here can be overriden by $JAVA_PATH or the -jdkhome switch
jdkhome=""

jreflags="-Xms8m"

#
# end of customization
#


PRG=$0

#
# resolve symlinks
#

while [ -h "$PRG" ]; do
    ls=`ls -ld "$PRG"`
    link=`expr "$ls" : '^.*-> \(.*\)$' 2>/dev/null`
    if expr "$link" : '^/' 2> /dev/null >/dev/null; then
	PRG="$link"
    else
	PRG="`dirname $PRG`/$link"
    fi
done

progdir=`dirname $PRG`
progname=`basename $0`

idehome="$progdir/.."

# absolutize idehome

oldpwd=`pwd` ; cd "${idehome}"; idehome=`pwd`; cd "$oldpwd"; unset oldpwd

thread_flag=""

jargs=${jreflags}
jargs="$jargs \"-Dnetbeans.home=$idehome\""
jargs="$jargs \"-Djava.security.policy=$idehome/bin/rmid.policy\""

args=""


#
# defaults
#

# if JAVA_PATH is set it overrides the default in the script

if [ ! -z "$JAVA_PATH" ] ; then
    jdkhome=$JAVA_PATH
fi

#
# parse arguments
#

while [ $# -gt 0 ] ; do
    case "$1" in
        -jdkhome) shift; if [ $# -gt 0 ] ; then jdkhome=$1; fi;;
        -J*) jargs="$jargs `expr $1 : '-J\(.*\)'`";;
        *) args="$args $1" ;;
    esac
    shift
done

#
# check JDK
#

if [ -z "$jdkhome" ] ; then
    echo "Cannot find JDK. Please set the JAVA_PATH environment variable to point"
    echo "to your JDK installation directory, or use the -jdkhome switch"
    echo ""
    exit 1
fi

if [ ! -x "${jdkhome}/bin/java" ] ; then
    echo "Cannot find JDK at ${jdkhome}. Please set the JAVA_PATH"
    echo "environment variable to point to your JDK installation directory,"
    echo "or use the -jdkhome switch"
    echo ""
    exit 1
fi


#
# increase file descriptor's limit, on Solaris it's set to 64, too small for
# fastjavac
#

ulimit -n 1024
    
wrapper_classpath="\"${idehome}/lib/ext/rmi-ext.jar\""
debug=-Dsun.rmi.server.activation.debugExec=true
    
    #
    # let's go
    #
    
eval $jdkhome/bin/java $thread_flag $jargs $debug -classpath $wrapper_classpath org.netbeans.rmi.RMIDWrapperImpl $args

