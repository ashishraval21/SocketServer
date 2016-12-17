# SocketServer

This project is Java Socket based Messaging Server. It can be used send messages to connected clients. The server discards duplicate messages which are sent to the same client within 5 Seconds Timeline.

To get started you will the need to have following:

* Java (jdk 1.7 or later)


To get up and running do these steps:

* Clone this git repo in your system
* Go the project folder and run ```ServerSocket.java``` to start the messaging server


By default the server runs ```localhost``` interface and port ```2500```. You can change the port number  of the server socket as per your need.

### Hows its Works

* the socket server will start for all the client request and processing it.
* now N number of client socket try to connect with server.
* server will ask for username that has to be unique for mapping client socket.
* once client will send username server check will mapping table (Hashmap) if that name is available or not. if available user can start messaging to another list of users. if not users get message with "UserName not available" and try to register  again with another username.
* if server added username it will come messgae with "Successfully Registered" , then user can start messaging with users.
