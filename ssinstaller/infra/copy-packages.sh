[ "$#" -eq 2 ] || exit 65
DEST_DIR=$1
IMAGE_DIR=$2

PLATFORMS="intel-S2 intel-Linux sparc-S2"

for platform in $PLATFORMS
do

CURRENT_DIR=$IMAGE_DIR/builds/$platform/c_installers/dvd_image_universal/install-$platform
CURRENT_PACKAGES=$CURRENT_DIR/packages-$platform
CURRENT_NB=$CURRENT_DIR/archives-$platform

DEST_PACKAGES=$DEST_DIR/$platform
DEST_NB=$DEST_DIR/nb

echo "Generating $platform in $DEST_PACKAGES"

rm -rf $DEST_PACKAGES/*
mkdir -p $DEST_PACKAGES
cp -r $CURRENT_PACKAGES/* $DEST_PACKAGES

if [ $platform = "intel-Linux" ]
then 
    continue
fi

cd $DEST_PACKAGES
for pkgname in `ls`
do
    pkgtrans -s . $pkgname.1 $pkgname
    rm -rf $pkgname
    mv $pkgname.1 $pkgname
done

echo "Generating $platform in $DEST_PACKAGES Finished"

done

rm -rf $DEST_NB
mkdir -p $DEST_NB
cp $CURRENT_NB/* $DEST_NB
cd $DEST_NB
mv atd-cluster*.zip atd-cluster.zip   
mv netbeans-6.1*.zip netbeans-6.1.zip
unzip netbeans-6.1.zip
cd netbeans
zip -r netbeans-6.1.zip *
mv netbeans-6.1.zip ..
cd ..
rm -rf netbeans
