cd `dirname "$0"`

cd ..
. build-private.sh
cd registration

TARDIR=`pwd`/build/tars
RESDIR=$OUTPUT_DIR/bundles
SRCDIR=`pwd`

# The length of install.sh is used to untar archive 
LENGTH=`wc -l install.sh | sed 's/^[ \t]*//;s/[ \t]*$//' | cut -f1 -d' '`
LENGTH2=`wc -l ${LICENSE_FILE} | sed 's/^[ \t]*//;s/[ \t]*$//' | cut -f1 -d' '`

echo $LENGTH
echo $LENGTH2

LENGTH=`expr $LENGTH + $LENGTH2`

BUILD_DATE=none

rm -rf build
mkdir -p $RESDIR
mkdir -p $TARDIR
for distr in $DISTRS
do
    case $distr in
    intel-S2)
	TARGET_OS=SunOS
	TARGET_PLATFORM=x86
	IMAGE_SUB=opt
	TAIL_ARG="+$LENGTH"
    ;;
    sparc-S2)
	TARGET_OS=SunOS
	TARGET_PLATFORM=sparc
	IMAGE_SUB=opt
	TAIL_ARG="+$LENGTH"
    ;;
    intel-Linux)
	TARGET_OS=Linux
	TARGET_PLATFORM=x86
	IMAGE_SUB=opt/sun
	TAIL_ARG="--lines=+$LENGTH"
    ;;
    esac

    DISTR_NAME="$RESDIR/StudioExpress-${TARGET_OS}-${TARGET_PLATFORM}-script.sh"   
    echo Generating $DISTR_NAME from $IMAGE_DIR
    
    DIRS=`ls $IMAGE_DIR`
    ARGS=""
    for dir in $DIRS
    do 
	ARGS="$ARGS -C $IMAGE_DIR $dir" 
    done
    tar cf $TARDIR/sunstudio.$distr.tar  -C $SRCDIR servicetag $ARGS 
    bzip2 $TARDIR/sunstudio.$distr.tar
    cat $SRCDIR/install.sh | while read f 
    do  
        if [ "$f" = "__license" ] 
	then
	    cat ${LICENSE_FILE} 
	else 
	    echo $f
	fi
    done | sed s/__os_name/"${TARGET_OS}"/ |  sed s/__tail_length/"$TAIL_ARG \$0"/ >  $DISTR_NAME
    cat $TARDIR/sunstudio.$distr.tar.bz2 >>  $DISTR_NAME
    chmod u+x  $DISTR_NAME
    echo
done