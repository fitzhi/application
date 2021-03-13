# Online references
# https://www.digitalocean.com/community/tutorials/how-to-install-nginx-on-ubuntu-16-04
# https://www.digitalocean.com/community/tutorials/how-to-create-a-self-signed-ssl-certificate-for-nginx-in-ubuntu-16-04

adduser vercinginx
usermod -aG sudo vercinginx

# connect user as vercinginx

# Install nginx
sudo apt-get update
sudo apt-get install nginx

# Check the installation and verify the firewall
sudo ufw app list

# Opening the HTTP port for testing purpose
sudo ufw allow 'Nginx HTTP'
sudo ufw app list

# Check the running Web Server
systemctl status nginx

# sudo systemctl stop nginx
# sudo systemctl start nginx
# sudo systemctl restart nginx

# reload the server configuration
sudo systemctl reload nginx

# Creation of the key
sudo openssl req -x509 -nodes -days 365 -newkey rsa:2048 -keyout /etc/ssl/private/nginx-selfsigned.key -out /etc/ssl/certs/nginx-selfsigned.crt

# Creation of a strong Diffie-Hellman group for a 'Perfect Forward Secrecy'
sudo openssl dhparam -out /etc/ssl/certs/dhparam.pem 2048

# Create a secured snippet
sudo nano /etc/nginx/snippets/self-signed.conf
# we add the 2 lines
#ssl_certificate /etc/ssl/certs/nginx-selfsigned.crt;
#ssl_certificate_key /etc/ssl/private/nginx-selfsigned.key;

# Create a Strong encryption snippet
sudo nano /etc/nginx/snippets/ssl-params.conf
#
# Copy the content of file ssl-params.cof
#

#
# We add to the content of /etc/nginx/sites-available/default
# the lines in the file 'default' 
#

sudo ufw allow 'Nginx Full'
sudo ufw delete allow 'Nginx HTTP'
sudo ufw status

# Check if all settings are valid
sudo nginx -t

# Restart the server nginx
sudo systemctl restart nginx


