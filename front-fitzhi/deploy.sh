echo "Starting the deployment to spoq.io"
echo "----------------------------------"
if [ ! -d "../../spoq/" ] 
then
    echo "Directory '../../spoq/' DOES NOT exist. We clone the SPOQ repository."
    cd ../.. 
    git clone https://github.com/fitzhi/spoq.git
    cd spoq
else
    echo "Directory '../../spoq/' is found."
    cd ../../spoq/
fi
echo "We clean the 'spoq' directory."
rm -rf assets
rm -rf docs
rm *
cd ../application/front-fitzhi
sh ./gen-build-ts.sh
npm run prod
cp -i ../../spoq/docs/index.html ../../spoq/docs/404.html
mv ../../spoq/docs/* ../../spoq/
echo spoq.io >../../spoq/CNAME
echo "We PUSH the new application."
cd ../../spoq
git add -A && git commit -m 'new building Release'
git push
cd ../application/front-fitzhi
echo "...Deployment is done"

