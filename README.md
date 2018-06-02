**sdis 2nd project @feup**


**Compiling and executing the server** - Can be done on any platform (windows,linux,macOS)

In some directory, for example the desktop:
**1** - Go to the folder of the project and in ``sdis1718-t6g01-p2/core/src`` copy the "com" folder to the desktop
**2** - Go to the folder of the project and in ``sdis1718-t6g01-p2/core/assets`` copy ``server.keys`` and ``truststore`` into desktop or the same directory level of the com folder
**3** - Go to the folder of the project and in ``sdis1718-t6g01-p2/desktop/compile_server`` extract the contents of lib.zip(jar files) into a folder called lib in the desktop or same directory level of the com folder
**4** - In desktop or same directory level of the com folder create a file called ``servers.txt``
**5** - Let's say we will want to initialize 3 servers, in this ``servers.txt`` we will need to list all 3 servers in the following format(each entry should be in a different line).

``<server_name> <server_address> <server_socket_port>
<server_name2> <server_address2> <server_socket_port2>``
...
6-In the copy of the com folder in the desktop go inside ``com/sdis/tetris`` and delete all the folders and files except the directory network
7-Inside the directory network delete the TetrisClient.java file as it is not relevant to this project
8-In the terminal cd to the directory where the com and lib folders, servers.txt, server.keys and truststore reside
9-Run the following command to compile all the files necessary for the server to function

``javac -classpath .:lib/*  com/sdis/tetris/network/*.java``

10-To execute the server in the terminal we need to have the following arguments ``<server_name> <server_socket_port> <client_socket_port>``, make sure
that you're initializing the server accordingly to one of the servers listed in servers.txt, for example if we wanted to create a server of name server1 and listening to servers on port 4445 and listening to clients on port 4500:

``java -classpath .:lib/* com.sdis.tetris.network.TetrisServer server1 4445 4500``

**Extra** - To run more servers make sure that they are properly listed in the ``servers.txt`` file with the correct server ports, without this properly setup the
replication of lobbies will not work


**Executing the client** - Can be done on any platform(windows,linux,macOS)


There are no instructions to manually compile the client as we are using libgdx game engine which requires the application to be divided into multiple projects and dependencies and we didn't find a way to manually make it work without an IDE. However we included a precompiled jar that can be found
in ``sdis1718-t6g01-p2/desktop/run_client`` folder of the project, extract the contents of the zip within into any location, inside the resulting Tetris folder we can find the jar itself, client.keys, truststore, servers.txt and various other assets related to the game.
Edit the servers.txt file so that all the servers can be interacted with, this servers.txt is independent of the server one mentioned above,
in this servers.txt the servers should be listed in the following format (each entry should be in a different line).
``<server_name1><server_address1><client_socket_port1>
<server_name2><server_address2><client_socket_port2>``
...
LINUX PARTICULARITY
- cd to the extracted Tetris folder
- run the jar through the command line otherwise the servers.txt file will not be found by the application
``java -jar TetrisClient.jar``

On any other platform the jar can just be double clicked
So for example if we wanted to list the server that we created above, we would need to make the following entry in servers.txt
``server1 ip_of_the_server 4500``

**NOTE: On screens with a resolution different than 1366x768 the tetris pieces will probably appear slightly shifted from their frame. This is only a minor visual detail which does not impact gameplay, networking or any other aspect relevant to SDIS.**





