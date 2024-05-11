# SyncDroid  

SyncDroid is a GUI desktop, cross-platform application for synchronizing files and folders to a server. It is written in Java and utilizes the JavaFX framework for the GUI. 

Note: This project is developed with IntelliJ IDEA.

## Installation  

Download latest release from (link to latest release), and ensure you have followed the instructions in the [Prerequisites](#prerequisites) section below.  
Refer to the Wiki for usage.

## Build  

Ensure the requirements in [Prerequisites](#prerequisites) section are fulfilled.  
  
There are two methods by which the application can be built.  
#### Method 1:  
- Import this project in IntelliJ IDEA
- Menu Bar > Build > Build Project
 - Either browse to `com.syncdroids.ui.Main.java` and run the main method
 - Or go to Menu Bar > Run... > Edit Configurations... > Add a configuration, ensure the Main class is specified as `com.syncdroids.ui.Main`. Then Menu Bar > Run > Run 'Main' (or whatever you named your configuration)  
  
#### Method 2:
- Ensure Maven is installed [(How to Install Maven)](https://www.baeldung.com/install-maven-on-windows-linux-mac).
- Open a command line window and browse (`cd`) to project root directory.
  - Run `mvn compile` to build the project
  - Run `mvn javafx:run` to launch application.
 
#### JAR generation
Run `mvn compile package`. Use the one ending with `-shaded.jar` for the completly bundled and double-clickable JAR.

## Prerequisites  

### Java  

Ensure you have Java 17 or later installed on your system. You can check this by running `java --version` in a terminal. If you do not have Java installed, you can download it from [here](https://www.oracle.com/java/technologies/downloads/).

### Starting FTP server  
This application can use Apache FtpServer to run a local FTP server on your machine. It is included in the repo with a simple (not so secure) config to emulate the existence of a remote server.  
Please note that it is OPTIONAL to use this FTP server along with the application - other FTP servers may also be used, with provided IP, port, and credentials.  

Browse to project directory and run the following commands in a terminal:

##### Windows (cmd)  
```bat
cd apache-ftpserver-1.2.0\
bin\ftpd.bat res\conf\ftpd-dev.xml
```

##### macOS/Linux (bash)  
```bash
cd apache-ftpserver-1.2.0/
chmod +x bin/ftpd.sh
./bin/ftpd.sh res/conf/ftpd-dev.xml  # You may need to run this line as sudo on Linux
```

Now the server should be running on localhost:2121 (`127.0.0.1:2121`). Use a client like FileZilla to connect to it, username: `dev` password: `development`. The server root directory is `res/synced_folder/`, and that's where files will be synced to.  
