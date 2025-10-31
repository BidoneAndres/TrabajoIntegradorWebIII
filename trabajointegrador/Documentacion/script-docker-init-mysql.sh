docker volume create integrador_data

docker run -d \
  --name integrador \
  --restart always \
  -e MYSQL_ROOT_PASSWORD=12345678 \
  -e MYSQL_DATABASE=integrador\
  -e MYSQL_USER=integrador \
  -e MYSQL_PASSWORD=integrador \
  -p 33306:3306 \
  -v integrador_data:/var/lib/mysql \
  -v /etc/localtime:/etc/localtime:ro \
  mysql:8.0.43 \
	--character-set-server=utf8mb4 \
	--collation-server=utf8mb4_0900_ai_ci \
	--default-authentication-plugin=mysql_native_password

#docker exec -it integrador ls

#docker exec -it integrador bash

#mysql -uroot -p12345678
#DROP USER IF EXISTS 'integrador'@'localhost';
#DROP USER IF EXISTS 'integrador'@'127.0.0.1';
#DROP USER IF EXISTS 'integrador'@'::1';
#DROP USER IF EXISTS 'integrador'@'%';

#CREATE USER integrador@'%' IDENTIFIED BY 'integrador’';
#GRANT ALL PRIVILEGES ON *.* TO integrador@'%' WITH GRANT OPTION;
#FLUSH PRIVILEGES;

