cd `dirname "$0"`
TARDIR=`pwd`/build/tars
RESDIR=`pwd`/build/result
SRCDIR=`pwd`
LENGTH=`wc -l register.sh | sed s/register.sh// | sed s/' '//g`
LENGTH=`expr $LENGTH + 1`
echo $LENGTH
DISTRS="intel-S2 sparc-S2 intel-Linux"
#DISTRS="intel-Linux"
#rm -rf $TARS 
IMAGES_DIR=/shared/dp/sstrunk/latest/output/image_tars
#IMAGES_DIR=/shared/dp/sstrunk
rm -rf build
mkdir -p $RESDIR
for distr in $DISTRS
do
    echo Generating $distr
    mkdir -p $TARDIR/$distr
    cd $TARDIR/$distr
    DISTR_NAME=$RESDIR/sunstudio-$distr.sh
    bzcat $IMAGES_DIR/*.$distr.tar.bz2 | /usr/sfw/bin/gtar -xf - 
    cp -r $SRCDIR/servicetag $TARDIR/$distr
    tar cf $TARDIR/sunstudio.$distr.tar *
    bzip2 $TARDIR/sunstudio.$distr.tar
    cat $SRCDIR/register.sh | sed s/__tail_length/"$LENGTH \$0"/ >  $DISTR_NAME
    cat $TARDIR/sunstudio.$distr.tar.bz2 >>  $DISTR_NAME
    chmod u+x  $DISTR_NAME
    echo
done