#!/bin/sh
#
#  Copyright note...
#
#

trap "on_exit; exit" 1 2 15 EXIT

PID=$$
TMPDIR=/tmp/ssregister.${PID}
mkdir -p ${TMPDIR}

# on exit remove all temporary data
on_exit() {
   cd /
   if [ -d "$TMPDIR" ]; then
      rm -fr $TMPDIR;
      rm -rf $PWD/servicetag
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
PRODUCTID="nb"

# REGISTRATION_DIR - a directory to store UIDs for
#      already registered instances of product

SUNSTUDIO_DIR=`uname | sed s/SunOS/SUNWspro/ | sed s/Linux/sunstudioceres/`

NETBEANS_DIR="netbeans-6.1"

REGISTRATION_DIR="${SUNSTUDIO_DIR}/registration"

# REGISTRATION_PAGE - location of a generated registration page
REGISTRATION_PAGE=`pwd`/"${REGISTRATION_DIR}/register-sunstudio.html"

HOME_SUNSTUDIO_DIR=$HOME/.sunstudio
HOME_REGISTRATION_PAGE="$HOME_SUNSTUDIO_DIR/registration/register-sunstudio.html"



# INSTANCES_REGISTRY - file that stores UIDs of already registered
#      instances of a product
INSTANCES_REGISTRY="${REGISTRATION_DIR}/instances"

PRODUCT_VENDOR="Sun Microsystems, Inc"

# BROWSERS_LIST - script will make an attempt to open a browser 
#      with generated registration page. BROWSERS_LIST defines 
#      a list of browsers to try
BROWSERS_LIST="firefox opera konqueror epiphany mozilla netscape"

REGISTER_URL="https://inv-ws-staging.central.sun.com/RegistrationWeb/register"

# STDIR - directory that contains swordfish.data and templates for
#      registration page generating (relative to sunstudio installation dir)
STDIR="./servicetag"

################################################################


#cd `dirname "$0"`
PWD=`pwd`
PATH=/usr/bin:/usr/sbin:/bin:$PWD/${SUNSTUDIO_DIR}/bin
MYNAME=`basename "$0"`


# script can be invoked with specifying locale that is used
# to determine which template file to use for registration 
# page generating
# getSupportedLocales provides a list of locales that is 'supported'
# i.e. register_XX.tmpl exists in STDIR directory

getSupportedLocales() {
   find ${BASEDIR}/${STDIR} -name register*.tmpl | sed 's/.*_\(.*\)\.tmpl/\1/'
}

# extracts specified value from swordfish data file.
# parameters: 
#    ExtractSWValue product_parent
#    ExtractSWValue sside.product_urn 
#                         // where sside - is registerable component

ExtractSWValue() {
   VALUE=`cat $SWORDFISHDATA | grep "^${1}" | cut -d= -f2`
   echo $VALUE
}

#
# returns UUID in the form xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx
#

generateUUID() {
if [ `uname` == "Linux" ]; then
  echo `uuidgen`
else
   cd ${TMPDIR}
   PROGNAME="./genuuid"

   cat << EOF > ${PROGNAME}.c
#include <uuid/uuid.h>
#include <stdio.h>

int main() {
   uuid_t uuid;
   char uuids[32];
   uuid_generate_random(uuid);
   uuid_unparse(uuid, uuids);
   printf("%s\n", uuids);
   return 0;
}
EOF
  cc -luuid ${PROGNAME}.c -o ${PROGNAME}
  echo `${PROGNAME}`
fi
}


# This script allows to install/uninstall servicetags for 
# LOCALLY installed products only.
# isLocalyInstalled checks whether BASEDIR is on local system or not.
# Routine returns:
#    "yes" if local
#    "no"  otherwise

isLocalyInstalled() {
  dfout=`df -l ${BASEDIR} 2>/dev/null`
  status=$?
  dfout=`echo $dfout | sed "s/Filesystem.*Mounted on//"`

  if [ $status -ne 0 -o "_${dfout}_" = "__" ]; then
     echo "no"
  else
     echo "yes"
  fi
}

# Default locale is "en". 
# if user has passed 'unsopported' (see getSupportedLocales()) locale
# then LOCALE is set to default one.
# Parameters: 
#    $1 - locale to validate
# Returns:
#    0 - passed locale is valid
#    1 - otherwise
# Side effects:
#    sets LOCALE variable to valid locale

validate_locale() {
   loc=`echo $1 | sed 's/_.*//'`
   if [ "_${loc}_" = "C" -o "_${loc}_" = "POSIX" ]; then
      LOCALE="en"
      return 0
   else
      for i in `getSupportedLocales`; do
         if [ "_${i}_" = "_${loc}_" ]; then
            LOCALE=${i}
            return 0
         fi
      done
   fi

   LOCALE="en"
   return 1
}

# First routine to call.
# Initializes variables like BINDIR, BASEDIR, etc.
# Also does an initialization of component-independent 
# servicetag/registration variables

init() {
   BINDIR=${PWD}
   BASEDIR="$BINDIR"
   if [ -f /bin/gawk ]; then
      AWK="gawk"
   else
      AWK="nawk"
   fi

   SWORDFISHDATA=${BASEDIR}/${STDIR}/swordfish.data
   if [ ! -f ${SWORDFISHDATA} ]; then
      echo "${SWORDFISHDATA} not found."
      exit 1
   fi

   ALLCOMPONENTS=`cat ${SWORDFISHDATA} | grep -v "^product_parent" | cut -f1 -d. | sort -u`

   DOINSTALL=0
   DOUNINSTALL=0
   DOREGISTER=0

   PRODUCT_PARENT=`ExtractSWValue product_parent`
   PARENT_URN=`ExtractSWValue product_parent_urn`
   PLATFORM_ARCH=`uname -p`  
   if [ -f /sbin/zonename ]; then
      CONTAINER=`/sbin/zonename`
   else 
      CONTAINER="global"
   fi

   SOURCE="cli"

   REGISTRATION_DATAFILE="${REGISTRATION_DIR}/servicetag.xml"
   validate_locale ${LANG}

   STSUPPORTED=0
   if [ -f /bin/stclient ]; then
      STSUPPORTED=1
   fi
}

# user may pass components to register/install ST for.
# validate_components validates the list user have passed
# 'valid' components are those that are nemtioned in swordfish.data file
# Parameters:
#    $1 $2 ... - list of components to validate
# Side effects:
# sets COMPONENTS variable to a list of validated components

validate_components() {
   COMPS=$@
   COMPONENTS=""

   for i in ${COMPS}; do
       echo $ALLCOMPONENTS | grep -w $i >/dev/null
       if [ $? -eq 0 ]; then
          COMPONENTS="${COMPONENTS} $i"
       else
          echo "Unknown component: $i" >&2
       fi
   done
}


# Extracts information from swordfish.data file and inits appropriate 
# variables
ParseSWData() {
   PRODUCT_NAME=`ExtractSWValue $1.product_name`
   PRODUCT_VERSION=`ExtractSWValue $1.product_version`
   PRODUCT_URN=`ExtractSWValue $1.product_urn`
   PRODUCT_INSTANCE_ID=`echo "id=${PRODUCT_VERSION},dir=${BASEDIR}" | sed 's/\(.\{1,255\}\).*/\1/'`
   INSTANCES_REGISTRY_FILE=`echo ${INSTANCES_REGISTRY} | sed "s/%PRODUCT_VERSION%/${PRODUCT_VERSION}/"`
   REGISTRATION_PAGE_FILE=`echo ${REGISTRATION_PAGE} | sed "s/%PRODUCT_VERSION%/${PRODUCT_VERSION}/"`
   mkdir -p `dirname "${INSTANCES_REGISTRY_FILE}"`
   mkdir -p `dirname "${REGISTRATION_PAGE_FILE}"`
}

# searches for instance ID of product with PRODUCT_URN in system registry
# Returns:
#    appropriate instance ID or 
#    "" if no appropriate servicetag found

findServiceTag() {
   UINS=`stclient -f -t $PRODUCT_URN`
   for i in ${UINS}; do
      DEFID=`stclient -g -i $i | grep product_defined_inst_id | cut -d= -f2-`
      if [ "${DEFID}" = "${PRODUCT_INSTANCE_ID}" ]; then
         echo $i
      fi
   done
   echo ""
}

#
# tries to install service tag to the system registry 
# in the case of any failure just silently ignore
#

installServiceTag() {
   #if [ "`findServiceTag`" = "" ]; then
      RC=`stclient -a -p "$PRODUCT_NAME" -e "$PRODUCT_VERSION" -t $PRODUCT_URN -I "$PRODUCT_INSTANCE_ID" -F $PARENT_URN -P "$PRODUCT_PARENT" -m "$PRODUCT_VENDOR" -A "$PLATFORM_ARCH" -z "$CONTAINER" -S "$SOURCE"`

   #   if [ $? -eq 0 ]; then
   #      echo "$RC"
   #   fi
   #else 
   #      echo "ServiceTag for this instance of $1 is already installed"
   #fi
}

#
# tries to remove service tag (identified by PRODUCT_URN) from the
# system registry

uninstallServiceTag() {
   UIN=`findServiceTag`
   if [ "$UIN" != "" ]; then
      stclient -d -i $UIN
   fi
}

# parses a string from servicetags agent
# used to initialize environment part of servicetag with 
# information provided by servicetags system support

parseAgentInfo() {
   cat ${agentInfoFile} | grep "<$1>" | sed "s/.*<$1>\(.*\)<\/$1>/\1/"
}

# if servicetags are supported by the system, registration information
# that will be send to Sun will contain environment section filled 
# with the information that is provided by stagent

initEnvironmentFromSystemRegistry() {
   curl -x "" -s http://127.0.0.1:6481/stv1/agent/ -o ${agentInfoFile}
   HOST=`parseAgentInfo host`
   HOSTID=`parseAgentInfo hostid`
   OSNAME=`parseAgentInfo system`
   OSVERSION=`parseAgentInfo release`
   PLATFORM_ARCH=`parseAgentInfo architecture`
   SYSTEMMODEL=`parseAgentInfo platform`
   SYSTEMMANUFACTURER=`parseAgentInfo manufacturer`
   CPUMANUFACTORER=`parseAgentInfo cpu_manufacturer`
   SERIALNUMBER=`parseAgentInfo serial_number`

   REGISTRY_URN=`stclient -x | grep "registry urn" | sed 's/.*urn="\(.*\)" .*/\1/'`
}

# opposite to initEnvironmentFromSystemRegistry this routine tries to
# fill environment section using a number of system utilities.

initEnvironment() {
   HOST=`uname -n`
   HOSTID=""
   if [`hostid 2> /dev/null 1> /dev/null` ]; then
       HOSTID=`hostid | sed 's/[0x]*//'`
   fi
   OSNAME=`uname -s`
   OSVERSION=`uname -r`
   SYSTEMMODEL=`uname -m`::`uname -v`
   SYSTEMMANUFACTURER=""
   CPUMANUFACTORER=""
   SERIALNUMBER=""

   if [`smbios 2> /dev/null 1>/dev/null` ]; then
      SYSTEMMANUFACTURER=`smbios -t SMB_TYPE_SYSTEM | grep Manufacturer | cut -d: -f2-`
      CPUMANUFACTORER=`smbios -t SMB_TYPE_PROCESSOR | grep Manufacturer | cut -d: -f2-`
      SERIALNUMBER=`smbios -t SMB_TYPE_CHASSIS | grep "Serial Number" | cut -d: -f2-`
   else
      CPUMANUFACTORER=`grep vendor /proc/cpuinfo | cut -d: -f2 |uniq`   
   fi

   UUID=`generateUUID`
   REGISTRY_URN="urn:st:${UUID}"
}

# when servicetags are supported by the system, script tries to 
# fetch service tag information from system registry to avoid 
# duplicate registrations
# Parameters:
#    $1 - registerable component name

fetchServiceTagFromSystemRegistry() {
   ParseSWData $1
   UIN=`findServiceTag`
   if [ "$UIN" != "" ]; then
      echo "<service_tag>"
      stclient -g -i $UIN | sed "s/\([^=.]*\)=\(.*\)/<\1>\2<\/\1>/"
      echo "</service_tag>"
      return 0
  fi
  return 1
}

# when product is registered, it's instance ID is written to 
# INSTANCES_REGISTRY_FILE 
# to avoid instance ids regeneration (in case of several attempts to
# register a product) this routine tries to read out instance ID from 
# this file. On failure it will generate new ID and add it to the 
# INSTANCES_REGISTRY_FILE

getInstanceURN() {
   line=`cat ${INSTANCES_REGISTRY_FILE} 2>/dev/null | grep "$1#${HOSTID}#$2"`
   if [ $? -ne 0 ]; then
      result="urn:st:"`generateUUID`
      echo "$1#${HOSTID}#$2#$result" >> ${INSTANCES_REGISTRY_FILE}
   else
      result=`echo $line | cut -d# -f4`
   fi

   echo $result
}

# creates <service_tag> xml part of st registry
# Parameters: 
#     $1 - registerable component name
#

createServiceTagFor() {
   ParseSWData $1

   INSTANCE_URN=`getInstanceURN $1 $PRODUCT_INSTANCE_ID`
   TIMESTAMP=`date -u '+%Y-%m-%d %H:%M:%S GMT'`
   INSTALLER_UID="-1"

   echo "<service_tag>"
   cat << EOF | sed "s/\([^=.]*\)=\(.*\)/<\1>\2<\/\1>/"
instance_urn=${INSTANCE_URN}
product_name=${PRODUCT_NAME}
product_version=${PRODUCT_VERSION}
product_urn=${PRODUCT_URN}
product_parent_urn=${PARENT_URN}
product_parent=${PRODUCT_PARENT}
product_defined_inst_id=${PRODUCT_INSTANCE_ID}
product_vendor=${PRODUCT_VENDOR}
platform_arch=${PLATFORM_ARCH}
timestamp=${TIMESTAMP}
container=${CONTAINER}
source=${SOURCE}
installer_uid=${INSTALLER_UID}
EOF
   echo "</service_tag>"
}

#
# createRegistrationDocument - gathers all required for registration 
# information and puts it to REGISTRATION_DATAFILE
#

createRegistrationDocument() {
   agentInfoFile="${TMPDIR}/environment.xml"

   if [ ${STSUPPORTED} -eq 1 ] && [ -f /usr/bin/curl ]; then
      initEnvironmentFromSystemRegistry
   else 
      initEnvironment
   fi

   cat << EOF > $REGISTRATION_DATAFILE
<?xml version="1.0" encoding="UTF-8"?>
<registration_data version="1.0">
  <environment>
    <hostname>$HOST</hostname>
    <hostId>$HOSTID</hostId>
    <osName>$OSNAME</osName>
    <osVersion>$OSVERSION</osVersion>
    <osArchitecture>$PLATFORM_ARCH</osArchitecture>
    <systemModel>$SYSTEMMODEL</systemModel>
    <systemManufacturer>$SYSTEMMANUFACTURER</systemManufacturer>
    <cpuManufacturer>$CPUMANUFACTORER</cpuManufacturer>
    <serialNumber>$SERIALNUMBER</serialNumber>
  </environment>
EOF

   echo "<registry urn=\"${REGISTRY_URN}\" version=\"1.0\">" >> $REGISTRATION_DATAFILE

   for i in $COMPONENTS; do 
      statusOK=no

      if [ ${STSUPPORTED} -eq 1 ]; then
         fetchServiceTagFromSystemRegistry $i >> $REGISTRATION_DATAFILE
         if [ $? -eq 0 ]; then 
            statusOK=yes
         fi
      fi

      if [ "$statusOK" = "no" ]; then
         createServiceTagFor $i >> $REGISTRATION_DATAFILE
      fi
   done

   cat << EOF >> $REGISTRATION_DATAFILE
</registry>
</registration_data>
EOF
}

##
#
# generateRegistrationHTML generates an HTML page to be used for 
# SysNet online registration.
#

generateRegistrationHTML() {      
   #cp "${BASEDIR}/${STDIR}/sslogo_${LOCALE}.jpg" "${REGISTRATION_DIR}"
   TEMPLATE="${BASEDIR}/${STDIR}/register_${LOCALE}.tmpl"
   SCRIPT="${TMPDIR}/genReg.awk"
   cat << EOF > ${SCRIPT}
BEGIN {
payload = ""
while ( getline str < plfile ) { gsub(/"/, "%22", str); payload = payload str }
}
/@@REGISTRATION_PAYLOAD@@/ { sub(/@@REGISTRATION_PAYLOAD@@/, payload) }
/@@PRODUCT@@/ { sub(/@@PRODUCT@@/, product) }
/@@PRODUCT_LOGO@@/ { sub(/@@PRODUCT_LOGO@@/, logo) }
/@@PRODUCT_VENDOR@@/ { sub(/@@PRODUCT_VENDOR@@/, product_vendor) }
/@@VERSION@@/ { sub(/@@VERSION@@/, version) }
/@@REGISTRATION_URL@@/ { sub(/@@REGISTRATION_URL@@/, url); }
{ print }
EOF

   cat ${TEMPLATE} | ${AWK} -v plfile=${REGISTRATION_DATAFILE} -v version="${PRODUCT_VERSION}" -v product="${PRODUCT}" -v logo="sslogo_${LOCALE}.jpg" -v product_vendor="${PRODUCT_VENDOR}" -v url="${REGISTER_URL}/${REGISTRY_URN}?product=${PRODUCTID}\\\&locale=${LOCALE}" -f ${SCRIPT} > ${REGISTRATION_PAGE_FILE}

}

#
# tries to locale one of a browser from the BROWSERS_LIST.
# Returns:
#     0 - if browser found
#     1 - if no browser found
#  and
#     browser_name 
#

find_browser() {
   for i in ${BROWSERS_LIST}; do
      which $i >/dev/null 2>&1      
      if [ $? -eq 0 ]; then
         echo `which $i`
         return 0
      fi
   done
   return 1
}

#
# opens an URL with a browser
# 

browse() {
   URL=$1
   if [ "$DISPLAY" = "" ]; then
      echo "No display was found. Registration page has been generated."
      echo "Please open following link with your browser to proceed with registration."
      echo "${URL}"
      return
   fi
   BROWSER=`find_browser`
   if [ $? -ne 0 ]; then
      echo "No browser was found. Registration page has been generated."
      echo "Please open following link with your browser to proceed with registration."
      echo "${URL}"
   else
      OUT=`${BROWSER} $URL 2>&1`
      if [ $? -ne 0 ]; then
         echo "\nThere were problems with launching ${BROWSER}:"
         echo "${OUT} \n"
         echo "Still, registration page has been generated."
         echo "Please open following link with your browser to proceed with registration."
         echo "${URL} \n"
      fi
   fi
}

unpack() {
    echo "Please wait while Sun Studio is unpacked into this directory."
    
    mkdir ${SUNSTUDIO_DIR} || exit 1;
    rm -r ${SUNSTUDIO_DIR}
    
    mkdir ${NETBEANS_DIR} || exit 1;
    rm -r ${NETBEANS_DIR}

    tail +__tail_length > ${TMPDIR}/sunstudio.tar.bz2
    bzcat ${TMPDIR}/sunstudio.tar.bz2 | tar -xf - || exit "Sun Studio instllation failed."
    rm ${TMPDIR}/sunstudio.tar.bz2
    echo "Sun Studio was successfully installed."
}


register() {
COMPONENTS=${ALLCOMPONENTS}
DOINSTALL=1
DOREGISTER=1

#ISLOCAL=`isLocalyInstalled`
#if [ $DOUNINSTALL -eq 1 -a "$ISLOCAL" = "no" -o $DOINSTALL -eq 1 -a "$ISLOCAL" = "no" ]; then
#   exit 1
#fi

for i in $COMPONENTS; do
   ParseSWData $i    
   if [ ${STSUPPORTED} -eq 1 -a $DOINSTALL -eq 1 ]; then
	installServiceTag $i
   fi
done

if [ $DOREGISTER -eq 1 -a "_${COMPONENTS}_" != "__" ]; then
   createRegistrationDocument 1>/dev/null 2>/dev/null
   generateRegistrationHTML 1>/dev/null 2>/dev/null
   mkdir -p $HOME_SUNSTUDIO_DIR
   cp -r $REGISTRATION_DIR $HOME_SUNSTUDIO_DIR 
   browse "file://$HOME_REGISTRATION_PAGE"
fi

exit 0;

}

########### Everything starts here ############

unpack
init
register
