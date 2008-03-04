#!/bin/sh -v
################################################################################
BUILD_NUMBER=200802181203
NB_HOME=`cd ../../../..; pwd`
OUTPUT_DIR="$NB_HOME/dist_new"

ANT_OPTS="-Xmx1024m -Dhttp.proxyHost=webcache.norway.sun.com -Dhttp.proxyPort=8080 "

CACHE_DIR=$NB_HOME/ssinstaller/infra/build/cache

NB_BUILDS_HOST=http://bits.nbextras.org/download/trunk/nightly/2008-02-18_13-01-22/zip/moduleclusters

JDK_HOME=/usr/java
NB_FILES_PREFIX=netbeans-trunk-nightly

