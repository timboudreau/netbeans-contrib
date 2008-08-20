#!/bin/bash

# 
# DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
# 
# Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
# 
# The contents of this file are subject to the terms of either the GNU General Public
# License Version 2 only ("GPL") or the Common Development and Distribution
# License("CDDL") (collectively, the "License"). You may not use this file except in
# compliance with the License. You can obtain a copy of the License at
# http://www.netbeans.org/cddl-gplv2.html or nbbuild/licenses/CDDL-GPL-2-CP. See the
# License for the specific language governing permissions and limitations under the
# License.  When distributing the software, include this License Header Notice in
# each file and include the License file at nbbuild/licenses/CDDL-GPL-2-CP.  Sun
# designates this particular file as subject to the "Classpath" exception as provided
# by Sun in the GPL Version 2 section of the License file that accompanied this code.
# If applicable, add the following below the License Header, with the fields enclosed
# by brackets [] replaced by your own identifying information:
# "Portions Copyrighted [year] [name of copyright owner]"
# 
# Contributor(s):
# 
# The Original Software is NetBeans. The Initial Developer of the Original Software
# is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun Microsystems, Inc. All
# Rights Reserved.
# 
# If you wish your version of this file to be governed by only the CDDL or only the
# GPL Version 2, indicate your decision by adding "[Contributor] elects to include
# this software in this distribution under the [CDDL or GPL Version 2] license." If
# you do not indicate a single choice of license, a recipient has the option to
# distribute your version of this file under either the CDDL, the GPL Version 2 or
# to extend the choice of license to its licensees as provided above. However, if you
# add GPL Version 2 code and therefore, elected the GPL Version 2 license, then the
# option applies only if the new code is made subject to such option by the copyright
# holder.
# 

################################################################################
# build-private.sh should define the following properties
################################################################################
BUILD_NUMBER=0
#
#OUTPUT_DIR=
#
#ANT_OPTS=
#
#BINARY_CACHE_HOST=
#NB_BUILDS_HOST=
#GLASSFISH_BUILDS_HOST=
#OPENESB_BUILDS_HOST=
#SJSAM_BUILDS_HOST=
#PORTALPACK_BUILDS_HOST=
#
#
#NB_FILES_PREFIX=
#
#JDK_HOME=
#
#CVS_ROOT=
#
#
#ADDITIONAL_PARAMETERS=
#
################################################################################

################################################################################
# get the path to the current directory and change to it
DIRNAME=`dirname $0`
cd ${DIRNAME}

################################################################################
# load the properties
source build-private.sh
#source ../../../../build-private.sh 


CACHE_DIR=${OUTPUT_DIR}/cache
INSTALLED_BITS="file://$CACHE_DIR/packages"
NB_BUILDS_HOST="file://$CACHE_DIR/packages/nb"
NB_FILES_PREFIX=netbeans-6.1

#rm -rf $OUTPUT_DIR
#
bash copy-packages.sh $CACHE_DIR/packages $SUNSTUDIO_BITS_ROOT

case $DISTRS in 
    intel-S2)
    	CURRENT_PLATFORM=solaris-x86
    ;;
    sparc-S2)
        CURRENT_PLATFORM=solaris-sparc
    ;;
    intel-Linux)
        CURRENT_PLATFORM=linux
    ;;
esac

cd ${DIRNAME}
################################################################################
# define the temp file location
TEMP_FILE=${WORK_DIR}/temp.sh.tmp

################################################################################
# define the log file location and create the directory for logs
#LOGS_DIR=${DIRNAME}/logs
#LOG_FILE=logs/${BUILD_NUMBER}.log
#[ ! -d ${LOGS_DIR} ] && mkdir -p ${LOGS_DIR}

################################################################################
# define the environment for running ant
export ANT_OPTS

run() {
    ################################################################################
    # run the build
    ant build \
            \"-Dbuild.number=${BUILD_NUMBER}\" \
	    \"-Dss.name=sunstudio\"\
	    \"-Dss.version=${SS_VERSION}\"\
	    \"-Dproducts.xml=${PRODUCTS_XML_FILE}\"\
            \"-Doutput.dir=${OUTPUT_DIR}\" \
            \"-Dcurrent.platform.name=${CURRENT_PLATFORM}\" \
            \"-Dss.platform.name=${DISTRS}\" \
	    \"-Dbinary.cache.host=${BINARY_CACHE_HOST}\" \
            \"-Dinstalled.bits.dir=${INSTALLED_BITS}\" \
            \"-Dnb.builds.host=${NB_BUILDS_HOST}\" \
            \"-Dnb.files.prefix=${NB_FILES_PREFIX}\" \
            \"-Dnb.locales=${LOCALES}\" \
            \"-Dnb.build.type=${NB_BUILD_TYPE}\" \
            \"-Djdk.home=${JDK_HOME}\" \
            \"-Dcvs.root=${CVS_ROOT}\" \
            \"-Dcvs.timestamp=${CVS_STAMP}\"\
            \"-Dcvs.branch=${CVS_BRANCH}\"\
            \"-Dregistries.home=${REGISTRIES_HOME}\" \
            \"-Djarsigner.enabled=${USE_JARSIGNER}\" \
            \"-Djarsigner.keystore=${JARSIGNER_KEYSTORE}\" \
            \"-Djarsigner.alias=${JARSIGNER_ALIAS}\" \
            \"-Djarsigner.storepass=${JARSIGNER_STOREPASS}\" \
            \"-Dpack200.enabled=${USE_PACK200}\" \
            \"-Dnbi.cache.dir=${CACHE_DIR}\" \
            ${ADDITIONAL_PARAMETERS} \
            $*

            ERROR_CODE=$?

            if [ $ERROR_CODE != 0 ]; then
                 echo "ERROR: $ERROR_CODE - NBI installers build failed"
                 exit $ERROR_CODE;
            fi

    ant bundle \
            \"-Dbuild.number=${BUILD_NUMBER}\" \
	    \"-Dss.name=sunstudio\"\
	    \"-Dss.version=${SS_VERSION}\"\
            \"-Dproducts.xml=${PRODUCTS_XML_FILE}\"\
	    \"-Doutput.dir=${OUTPUT_DIR}\" \
            \"-Dbundles.url=${BUNDLES_URL}\" \
            \"-Dcurrent.platform.name=${CURRENT_PLATFORM}\" \
            \"-Dss.platform.name=${DISTRS}\" \
	    \"-Dbinary.cache.host=${BINARY_CACHE_HOST}\" \
            \"-Dnb.builds.host=${NB_BUILDS_HOST}\" \
            \"-Dnb.files.prefix=${NB_FILES_PREFIX}\" \
            \"-Dnb.locales=${LOCALES}\" \
            \"-Dnb.build.type=${NB_BUILD_TYPE}\" \
            \"-Djdk.home=${JDK_HOME}\" \
            \"-Dcvs.root=${CVS_ROOT}\" \
            \"-Dcvs.timestamp=${CVS_STAMP}\" \
            \"-Dcvs.branch=${CVS_BRANCH}\"\
            \"-Dregistries.home=${REGISTRIES_HOME}\" \
            \"-Djarsigner.enabled=${USE_JARSIGNER}\" \
            \"-Djarsigner.keystore=${JARSIGNER_KEYSTORE}\" \
            \"-Djarsigner.alias=${JARSIGNER_ALIAS}\" \
            \"-Djarsigner.storepass=${JARSIGNER_STOREPASS}\" \
            \"-Dpack200.enabled=${USE_PACK200}\" \
            \"-Dnbi.cache.dir=${CACHE_DIR}\" \
            ${ADDITIONAL_PARAMETERS} \
            $*

            ERROR_CODE=$?

            if [ $ERROR_CODE != 0 ]; then
                 echo "ERROR: $ERROR_CODE - NBI installers build failed"
                 exit $ERROR_CODE;
            fi
}

run $*


