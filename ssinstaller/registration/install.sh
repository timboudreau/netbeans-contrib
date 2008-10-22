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
INSTALLER_NAME=`basename $0`

BASE_DIR=$CWD

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


# STDIR - directory that contains swordfish.data and templates for
#      registration page generating (relative to sunstudio installation dir)
STDIR="./servicetag"

################################################################


PATH=/usr/bin:/usr/sbin:/bin
MYNAME=`basename "$0"`


print_usage() {
echo "Syntax is $INSTALLER_NAME [options]"
echo "Options: "
echo "  --accept-sla" 
echo "    Accept license. This option is required to install Sun Studio."
echo "  --print-sla"
echo "    Print license."
#echo "  --installation-dir <directory>" 
#echo "    Install Sun Studio into this directory."
echo "End."
}

error() {
    MESSAGE=$1
    echo $MESSAGE
    exit 1
}

print_license() {
more << "EOF"
__license
EOF
}

parse_args() {
while [ "$#" -gt "0" ]
do
    case $1 in
--accept-sla)
    ACCEPT_LICENSE=1
    ;;
--print-sla)
    print_license;
    exit 0;
    ;;
#--installation-dir)
#    shift
#    BASE_DIR="$1"
#    ;;
--help)
    print_usage;
    exit 0;
    ;;
*)
    echo "Unkonw option: $1"
    print_usage;
    exit 1
    ;;
    esac
    shift
done
if [ "$ACCEPT_LICENSE" = "" ]
then 
    print_usage;
    error "Error. You should accept license to install Sun Studio."
fi

SUNSTUDIO_DIR=`uname | sed s/SunOS/SUNWspro/ | sed s/Linux/sunstudioceres/`
NETBEANS_DIR="netbeans-6.5ss"

}

unpack() {
    echo "Please wait while Sun Studio is unpacked."
    
    mkdir ${SUNSTUDIO_DIR} || exit 1;    
    mkdir ${NETBEANS_DIR} || exit 1;
    
    
    rm -r ${SUNSTUDIO_DIR}
    rm -r ${NETBEANS_DIR}

    tail __tail_length > ${TMPDIR}/sunstudio.tar.bz2
    cd ${BASE_DIR}
    bzcat ${TMPDIR}/sunstudio.tar.bz2 | tar -xf - || exit "Sun Studio installation failed."
    rm ${TMPDIR}/sunstudio.tar.bz2
    rm uninstall_Sun_Studio_Ceres.class
    echo "Sun Studio was successfully installed into the directories ${SUNSTUDIO_DIR} and ${NETBEANS_DIR}."
}

########### Everything starts here ############

parse_args $*;
unpack
REGISTRATION_DIR="$BASE_DIR/${SUNSTUDIO_DIR}/prod/lib/condev"
export REGISTRATION_DIR

DATA_DIR="$BASE_DIR/${SUNSTUDIO_DIR}/prod/lib/condev"
export DATA_DIR

mkdir -p $DATA_DIR

SUNSTUDIO_DIR="$BASE_DIR/${SUNSTUDIO_DIR}/prod/lib/condev"
export SUNSTUDIO_DIR

PRODUCTS="nb ss"
export PRODUCTS

SOURCE="script"
export SOURCE

# Disabled until registration is not completed.
cd $REGISTRATION_DIR
sh register.sh 2>/dev/null
exit 0;
