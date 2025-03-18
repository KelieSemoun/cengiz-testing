# Cengiz Testing (Fullstack application testing)

In this repository, you will find a tested fullstack application. Meaning the front-end, back-end and end-to-end tests are included and will be shown how to execute them.

Clone the project first :

> git clone https://github.com/KelieSemoun/cengiz-testing

## Database Installation

Create a MySQL Database using MySQL CLI with the following commands :
```
CREATE DATABASE myDatabase;
USE myDatabase;
```
SQL script for creating the schema is available `ressources/sql/script.sql`

Go to backend/src/main/resources and create a file named "env.properties" where you put the following lines :
```
DB_USER=[YOUR DATABASE USERNAME]
DB_PASSWORD=[YOUR DATABASE PASSWORD]
DB_DATABASE_NAME=[YOUR DATABASE NAME]
```
## Application Installation

### Front-end

Using NodeJS 16.20.2 and Angular CLI 14.2.1, in `cengiz-testing/front`, run :

> npm install

Start the application with :

> npm run start

### Back-end

Go to cengiz-testing/back and run the following command :

> mvn clean install

And then run :

> mvn spring-boot:run;

## Application Tests

### Front-end

To test all files and see the coverage report, run the following command in `cengiz-testing/front` :

> .\node_modules\\.bin\jest --coverage

### Back-end

To test all files, run the following command in `cengiz-testing/back` :

> mvn verify

This will generate 3 folders in the follwing folder `cengiz-testing/back/target/site/` :

> jacoco-aggregate (Merging of Unit Tests and Integration Tests)
> jacoco-it (Integration Tests)
> jacoco-ut (Unit Tests)

Open the corresponding folder and open in a browser the corresponding `index.html` file.

### End-to-End

To test all files, run the following command in `cengiz-testing/front` (NOTE : Google Chrome is required otherwise the command will not work) :

> npm run e2e:ci

To generate the coverage report, run the following command (NOTE : YOU NEED TO HAVE RAN THE PREVIOUS COMMAND FOR THIS ONE TO WORK)

> npm run e2e:coverage
