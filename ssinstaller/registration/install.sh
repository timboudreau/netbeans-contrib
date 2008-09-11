#!/bin/sh
#
#  Copyright note...
#
#

trap "on_exit; exit" 1 2 15 EXIT

PID=$$
TMPDIR=/tmp/ssinstall.${PID}
mkdir -p ${TMPDIR}
CWD=`pwd`
# on exit remove all temporary data
on_exit() {
   cd /
   if [ -d "$TMPDIR" ]; then
      rm -fr $TMPDIR;
      
   fi
   
   if [ -d "$CWD/servicetag" ]; then
      rm -rf $CWD/servicetag
   fi   
   
}


if [ `uname` != "__os_name" ]
then
  echo "The incorrect platform. Should be __os_name. Exiting." 
  exit 1
fi

#
# Below are some customization properties ...
#

# PRODUCT - product name. Will be mentioned on registration page.
PRODUCT="Sun Studio"

# PRODUCTID - id that is used for identifying registration page on SysNet.
PRODUCTID="ss"

# REGISTRATION_DIR - a directory to store UIDs for
#      already registered instances of product

SUNSTUDIO_DIR=`uname | sed s/SunOS/SUNWspro/ | sed s/Linux/sunstudioceres/`
NETBEANS_DIR="netbeans-6.1"

# STDIR - directory that contains swordfish.data and templates for
#      registration page generating (relative to sunstudio installation dir)
STDIR="./servicetag"

################################################################


PATH=/usr/bin:/usr/sbin:/bin
MYNAME=`basename "$0"`


unpack() {
    echo "Please wait while Sun Studio is unpacked into this directory."
    
    mkdir ${SUNSTUDIO_DIR} || exit 1;
    rm -r ${SUNSTUDIO_DIR}
    
    mkdir ${NETBEANS_DIR} || exit 1;
    rm -r ${NETBEANS_DIR}

    tail __tail_length > ${TMPDIR}/sunstudio.tar.bz2
    bzcat ${TMPDIR}/sunstudio.tar.bz2 | tar -xf - || exit "Sun Studio instllation failed."
    rm ${TMPDIR}/sunstudio.tar.bz2
    echo "Sun Studio was successfully installed."
}

########### Everything starts here ############

unpack
cd servicetag
REGISTRATION_DIR="${CWD}/${SUNSTUDIO_DIR}/prod/lib/condev"
export REGISTRATION_DIR
export SUNSTUDIO_DIR
PRODUCTS="ss nb"
export PRODUCTS
sh register.sh
exit 0;
