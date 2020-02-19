echo "Starting the deployment to spoq.io"
echo "----------------------------------"
cd ../../spoq/
rm -rf assets
rm -rf docs
rm *
cd -
ng build --prod --output-path ../../spoq/docs --base-href /
cp -i ../../spoq/docs/index.html ../../spoq/docs/404.html
mv ../../spoq/docs/* ../../spoq/
echo spoq.io >../../spoq/CNAME
echo "...Deployment is done"

