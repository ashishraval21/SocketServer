package com.niki.server;


import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.*;

/**
 * Created by songline on 16/12/16.
 */
public class ServerSocket implements Runnable {

    static Map<String, Socket> listOfUsers = new HashMap<>();
    static Queue<Socket> linkedQueue = new LinkedList<Socket>();
    static List<Thread> threadList = new ArrayList<>(5);
    static int threadIndex = 0;
    ClientMessage message = new ClientMessage();
    ObjectInputStream inStream = null;
    ObjectOutputStream senderObjectOutputStream = null;
    Socket socket = null;
    static HashMap<String, Long> messageMap = null;
    int fiveSeconds = 5000;

    /***
     * initialize clientsocket's stream for sending and receiving data
     * @param socket
     * @throws Exception
     */
    ServerSocket(Socket socket) throws Exception {
        inStream = new ObjectInputStream(socket.getInputStream());
        senderObjectOutputStream = new ObjectOutputStream(socket.getOutputStream());
        this.socket = socket;
        messageMap = new HashMap<>();


    }

    /***
     * start server socket with port number.
     * 5 thread are in the list which are running for different users for getting message or sending response to them.
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {


        java.net.ServerSocket serverSocket = new java.net.ServerSocket(2500);
        System.out.println("Server Started ... ");
        while (true) {
            Socket socket = serverSocket.accept();
            linkedQueue.add(socket);
            Thread t = new Thread(new ServerSocket(socket));
            if (threadIndex < 5) {
                threadList.add(threadIndex++, t);
                t.start();
            }

        }
    }

    /***
     * this run methos run in a infinite loop with different stored list of socket to one by one
     * and then works for Registration purpose and send message to target users.
     */

    @Override
    public void run() {

        while (true) {

            Socket clientSocket = linkedQueue.poll();
            if (clientSocket != null && clientSocket.isConnected()) {
                try {

                    message = null;
                    if ((message = (ClientMessage) inStream.readObject()) != null) {
                    }

                    if (message != null && message.getType().equals("Register")) {
                        if (listOfUsers.containsKey(message.getSenderId())) {
                            message.setMessage("Username Not Available");
                            senderObjectOutputStream.writeObject(message);
                            senderObjectOutputStream.flush();
                        } else {

                            listOfUsers.put(message.getSenderId(), clientSocket);
                            message.setMessage("Successfully Registered");
                            message.setType("Message");
                            senderObjectOutputStream.writeObject(message);
                            senderObjectOutputStream.flush();
                        }

                    } else if ((message != null) && message.getType().equals("Message")) {

                        if (!listOfUsers.containsKey(message.getId())) {
                            senderObjectOutputStream = new ObjectOutputStream(socket.getOutputStream());
                            message.setMessage("User Not Found");
                            senderObjectOutputStream.writeObject(message);
                            senderObjectOutputStream.flush();
                        } else {
                            Socket socketReceiver = listOfUsers.get(message.getId());
                            if (socketReceiver.isConnected()) {
                                new Runnable() {

                                    public void sendMessage(ClientMessage myMessage) {
                                        try {
                                            senderObjectOutputStream = new ObjectOutputStream(socketReceiver.getOutputStream());
                                            senderObjectOutputStream.writeUnshared(myMessage);
                                            senderObjectOutputStream.flush();
                                            messageMap.put(myMessage.getSenderId() + ":" + myMessage.getMessage(), System.currentTimeMillis() + fiveSeconds);
                                        } catch (Exception e) {
                                            if (!socketReceiver.isConnected()) {
                                                System.out.println("User Disconnected");
                                            }
                                        }
                                    }

                                    @Override
                                    public void run() {

                                        // System.out.println("Here in " + message.getMessage());
                                        ClientMessage myMessage = message;
                                        if (!messageMap.containsKey(myMessage.getSenderId() + ":" + myMessage.getMessage())) {
                                            sendMessage(myMessage);

                                        } else if (messageMap.containsKey(myMessage.getSenderId() + ":" + myMessage.getMessage()) && messageMap.get(myMessage.getSenderId() + ":" + myMessage.getMessage()) < System.currentTimeMillis()) {
                                            sendMessage(myMessage);
                                        }


                                    }
                                }.run();

                            }


                        }

                    }


                    linkedQueue.offer(clientSocket);


                } catch (Exception e) {
                    linkedQueue.remove(clientSocket);
                }


            }
        }


    }
}


