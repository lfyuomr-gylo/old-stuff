##Kangaroo Messenger Server

###What is this?

This is the server side of 
[kangaroo messenger](https://github.com/lfyuomr-gylo/KangarooMessengerClient).

###How to run it?
First of all, to run the server, you need 
[vagrant](https://www.vagrantup.com/).

If you've already installed it, follow the instructions below:

1. Clone [postgres in vagrant](https://wiki.postgresql.org/wiki/PostgreSQL_For_Development_With_Vagrant) 
configuration repo:

  ```
  git clone https://github.com/jackdb/pg-app-dev-vm db
  cd db
  rm -rf .git README.md LICENSE
  ```
2. Configure data base: 
  
  ```
  cd Vagrant-setup
  sed -i -- 's/myapp/kangouser/g' bootstrap.sh 
  sed -i -- 's/dbpass/kangoapp/g' bootstrap.sh
  cd ..
  ```
3. Set up vagrant:
  
  ```
  vagrant up
  ```
4. Connect to DB and open postgres command line:
  
  ```
  vagrant ssh
  sudo su - postgres
  PGUSER=kangouser PGPASSWORD=kangoapp psql -h localhost kangouser
  ```
5. Create table. To do this you nees to run the code from initDB.sql
in command line. After that, close command line and disconnect from VM(press
ctrl+D 3 times)
6. Build server
  
  ```
  cd ..
  mvn clean compile assembly:single
  ```
7. Run(JRE 8 required)
  
  ```
  java -jar target/kangaroo-messenger-client-1.0-SNAPSHOT-jar-with-dependencies.jar
  ```
