REPOSITORY_DIR=/home/ubuntu/app/deploy

if [ -d $REPOSITORY_DIR ]; then
    sudo rm -rf $REPOSITORY_DIR
fi
sudo mkdir -vp $REPOSITORY_DIR

cp build/libs/*.jar $REPOSITORY_DIR
cp appspec.yml $REPOSITORY_DIR
cp deploy-before.sh $REPOSITORY_DIR
cp deploy.sh $REPOSITORY_DIR

cd $REPOSITORY_DIR
zip -r deploy.zip *