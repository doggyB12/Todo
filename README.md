# Todo Application (Option A: Spring Boot)
This is a simple Todo application built with Java and Spring Boot. The application allows users to create, read, update, and delete (CRUD) todo items.
# How to Test
1) Clone the repository:
   ```bash
   git clone https://github.com/doggyB12/Todo.git
   ```
   or dowloand file and extract.
3) This assigment use MySql Workbench:
   
   Set the password in MySql Workbench and password default: 12345678
   or you can change password in :
   ```bash
   src/main/resources/application.properties
   in line 5: spring.datasource.password=12345678
   ```
   change to your password setting in MySql Workbench
5) Reload maven in pom.xml.
6) Running the Application
   You can run project and use this link to test api:
   ```bash
   localhost:8080/swagger-ui/index.html
   ```
7) Unit test file
   Test files are in:
   ```bash
   src/test/java/com/project/Todo
   ```
   Because repository implements from JpaRepository -> maybe do not need to write test :)
   Files include:
   ```bash
       src
       ├── main
       └── test
           └── java
               └── com.project.Todo
                   ├── controller
                   │   └── TodoAPITest.java
                   ├── dto
                   │   └── TodoDTOTest.java
                   └── service
                       └── TodoServiceTest.java
   ```
8) Test Coverage
   To check the test coverage in IntelliJ IDEA:
   Right-click on the src/test/java directory or a specific test class.
   Select Run 'All Tests' with Coverage or Run 'SpecificTest' with Coverage.
   After the tests run, you can view the coverage report.

