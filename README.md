# SyncDroid  

SyncDroid is a simple GUI desktop, cross-platform application for synchronizing files and folders to a server. It is written in Java and C and uses the JavaFX framework for the GUI, and C [need to insert C library here]. 

Note: This project is developed with IntelliJ IDEA.

## Installation  

Download latest release from (link to latest release), and ensure you have followed the instructions in the [Prerequisites](#prerequisites) section below.  

(this needs to be updated later on after application is published, should just be a link to the latest release jar and double click)

## Build  

Follow the instructions in the [Prerequisites](#prerequisites) section below.

(this needs to be updated later on with instructions on how to build the application from source)

## Prerequisites  

### Java  

Ensure you have Java 17 or later installed on your system. You can check this by running `java --version` in a terminal. If you do not have Java installed, you can download it from [here](https://www.oracle.com/java/technologies/downloads/).

### Starting FTP server  
This application uses Apache FtpServer to run a local FTP server on your machine. It is included in the repo with a simple (not so secure) config to emulate the existence of a remote server.  

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
