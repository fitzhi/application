echo  "---------------------------------------------"
echo  "     local download in /var/www/fitzhi"
echo  "---------------------------------------------"
cd /var/www
rm -rf fitzhi
rm -rf spoq
git clone https://github.com/fitzhi/spoq.git
cd spoq
rm -rf .git
cd ..
mv spoq fitzhi
