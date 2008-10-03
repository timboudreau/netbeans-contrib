#!/bin/sh 
############################################################################
# This file is temporary used by nightly.
################################################################################


# The directory to save binaries 
OUTPUT_DIR="`pwd`/build"
export OUTPUT_DIR


# Remote installer use this url
BUNDLES_URL=file://../packaged
export BUNDLES_URL

# version is used in name
SS_VERSION="Express"
export SS_VERSION

# the distributive version
#DISTRS="intel-S2"

case `uname` in
    SunOS)  
        PLATFORM=`uname -p`
        INST_DIR=opt
	if [ "$PLATFORM" = "sparc" ]; then
            DISTRS=sparc-S2
        else
            DISTRS=intel-S2
        fi
    ;;
    Linux)
        DISTRS=intel-Linux
	INST_DIR=opt/sun
    ;;
esac

export DISTRS


# The Studio build to create installer
SUNSTUDIO_BITS_ROOT=/shared/dp/sstrunk/latest
SS_PACKAGES_DIR=${SS_PACKAGES_DIR-$SUNSTUDIO_BITS_ROOT/builds/$DISTRS/c_installers/dvd_image_universal/install-$DISTRS/packages-$DISTRS}
export SS_PACKAGES_DIR

NB_ARCHIVE_DIR=${NB_ARCHIVE_DIR-$SUNSTUDIO_BITS_ROOT/builds/$DISTRS/c_installers/dvd_image_universal/install-$DISTRS/archives-$DISTRS}
export NB_ARCHIVE_DIR

IMAGE_DIR=${SUNSTUDIO_BITS_ROOT}/inst/${DISTRS}.inst/${INST_DIR}
export IMAGE_DIR

# The path to the product xml file
PRODUCTS_XML_FILE=${PRODUCTS_XML_FILE-`pwd`/ProductDescription.xml}
export PRODUCTS_XML_FILE

LICENSE_FILE=${LICENSE_FILE-`pwd`/SunStudioExpress_Eval_License.txt}
export LICENSE_FILE

echo "Sun Studio packages: $SS_PACKAGES_DIR"
echo "NetBeans : $NB_ARCHIVE_DIR" 
echo "XML Description: $PRODUCTS_XML_FILE"
echo "License File: $LICENSE_FILE"
