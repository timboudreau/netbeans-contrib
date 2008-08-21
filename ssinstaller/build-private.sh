#!/bin/sh -v
############################################################################
# This file is temporary used by nightly.
################################################################################


# The directory to save binaries 
OUTPUT_DIR="`pwd`/build"
export OUTPUT_DIR

# The Studio build to create installer
SUNSTUDIO_BITS_ROOT=/shared/dp/sstrunk/latest
export SUNSTUDIO_BITS_ROOT

# The path to the product xml file
PRODUCTS_XML_FILE=`pwd`/ProductDescription.xml
export PRODUCTS_XML_FILE

# Remote installer use this url
BUNDLES_URL=file://../packaged
export BUNDLES_URL

# version is used in name
SS_VERSION="X"
export SS_VERSION

# the distributive version
#DISTRS="intel-S2"

case `uname` in
    SunOS)  
        PLATFORM=`uname -p`
        if [ "$PLATFORM" = "sparc" ]; then
            DISTRS=sparc-S2
        else
            DISTRS=intel-S2
        fi
    ;;
    Linux)
        DISTRS=intel-Linux
    ;;
esac

export DISTRS

SS_PACKAGES_DIR=$SUNSTUDIO_BITS_ROOT/builds/$DISTRS/c_installers/dvd_image_universal/install-$DISTRS/packages-$DISTRS
export SS_PACKAGES_DIR

NB_ZIP_DIR=$SUNSTUDIO_BITS_ROOT/builds/$DISTRS/c_installers/dvd_image_universal/install-$DISTRS/archives-$DISTRS
export NB_PACKAGES_DIR

