## Commom CLI Commands Used

```bash
### MYSQL
# pull postgres image and run the container named mysql
docker run -d --name mysql -p 4306:3306 -e MYSQL_ROOT_PASSWORD=password -e MYSQL_DATABASE=mydb -e MYSQL_USER=myuser -e MYSQL_PASSWORD=mypassword -v mysql-data:/var/lib/mysql mysql:8.0.40

### DOCKER
# stop container
docker stop mysql

# restart container in detach mode
docker start mysql

# accessing the Running MySQL container
docker ps
docker exec -it mysql bash
mysql -h localhost -u myuser -p mydb
SHOW TABLES;
SELECT * FROM table_name; # basic select query
# quit mysql
quit
# quit interactive mode
ctrl+D

# clear the screen
cntrl+L

# clean slate
docker system prune -a && docker images prune -a && docker volume prune -a

# stop running conainer
docker stop mysql

# restart container
docker restart mysql 

### MAVEN
# clean and build Spring Boot app
mvn clean && mvn install

# run Spring boot app
mvn spring-boot:run

# delete maven cache libraries in main repo
rm -rf ~/.m2/repository

# clean the local repository
mvn dependency:purge-local

# Force clean and rebuild
mvn clean install

# delete maven cache libraries in main repo
rm -rf ~/.m2/repository

# clean the local repository
mvn dependency:purge-local

# Force clean and rebuild
mvn clean install
```