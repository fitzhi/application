cd ../../spoq/
rm -rf assets
rm -rf docs
rm *
cd -
ng build --prod --crossOrigin=use-credentials --output-path ../../spoq/docs --base-href /
cp -i ../../spoq/docs/index.html ../../spoq/docs/404.html
mv ../../spoq/docs/* ../../spoq/
