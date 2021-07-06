# sz-blog-vaadin

# Archived because of shut down.

This project is the website of the schools news paper schiffsschraube. 

The project is a standard Maven project, so you can import it to your IDE of choice. [Read more how to set up a development environment](https://vaadin.com/docs/v14/flow/installing/installing-overview.html) for Vaadin projects (Windows, Linux, macOS). 

This project was created from https://start.vaadin.com.

## Running the Application
There are two ways to run the application:  
 - To run from the command line, use `mvn` and open [http://localhost:80](http://localhost:80) in your browser.
 - Another way is to to run the `Application` class directly from your IDE.

#### Intellij IDEA
- On the right side of the window, select Maven --> Plugins--> `spring-boot` --> `spring-boot:run` goal
- Optionally, you can disable tests by clicking on a `Skip Tests mode` blue button.

Clicking on the green run button will start the application.

After the application has started, you can view your it at http://localhost:80 in your browser.

#### Eclipse
- Right click on a project folder and select `Run As` --> `Maven build..` . After that a configuration window is opened.
- In the window set the value of the **Goals** field to `spring-boot:run` 
- You can optionally select `Skip tests` checkbox
- All the other settings can be left to default

Once configurations are set clicking `Run` will start the application

or

- go to [this website](https://www.jetbrains.com/idea/) and download intelliJ :)

## Project structure

- `MainView.java` in `src/main/java` contains the server-side blog-side.
- `views` package in `src/main/java` contains the intern server-side Java views of your application.
- `views` folder in `frontend/src/` contains the client-side JavaScript views of your application.
- `util` folder in `src/main/java` contains the post structure and `UserData.java`, the class for storing the users' data for the current session.
- `db` folder in `src/main/java` contains `DataBase.java`, the DataBase structure (MongoDB).
