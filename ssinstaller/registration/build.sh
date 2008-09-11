cd `dirname "$0"`
TARDIR=`pwd`/build/tars
RESDIR=`pwd`/build/result
SRCDIR=`pwd`

# The length of install.sh is used to untar archive 
LENGTH=`wc -l install.sh | sed s/install.sh// | sed s/' '//g`
LENGTH=`expr $LENGTH + 1`

DISTRS="intel-S2 sparc-S2 intel-Linux"

# The images of Sun Studio to create distribution
#BUILD_NUMBER=`ls -lA /shared/dp/sstrunk/biweekly | sed s/.*' '//`
#IMAGES_DIR=/shared/dp/sstrunk/latest/inst
IMAGES_DIR=/export/home/lm153972/ws/images/empty
#BUILD_DATE=`ls -lA /shared/dp/sstrunk/${BUILD_NUMBER} | sed s/.*' '//`
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

    DISTR_NAME="$RESDIR/StudioExpress-${TARGET_OS}-${TARGET_PLATFORM}-${BUILD_DATE}-ii.sh"   
    echo Generating $DISTR_NAME
    
    DIRS=`ls $IMAGES_DIR/$distr`
    ARGS=""
    for dir in $DIRS
    do 
	ARGS="$ARGS -C $IMAGES_DIR/$distr $dir" 
    done
    tar cf $TARDIR/sunstudio.$distr.tar  -C $SRCDIR servicetag $ARGS 
    bzip2 $TARDIR/sunstudio.$distr.tar
    cat $SRCDIR/install.sh | sed s/__os_name/"${TARGET_OS}"/ |  sed s/__tail_length/"$TAIL_ARG \$0"/ >  $DISTR_NAME
    cat $TARDIR/sunstudio.$distr.tar.bz2 >>  $DISTR_NAME
    chmod u+x  $DISTR_NAME
    echo
done