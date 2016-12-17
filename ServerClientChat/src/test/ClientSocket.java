package test;

import com.niki.server.ClientMessage;
import com.sun.security.ntlm.Client;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Scanner;

/**
 * Created by songline on 16/12/16.
 */
public class ClientSocket {

    private Socket socket = null;
    private ObjectInputStream inputStream = null;
    private ObjectOutputStream outputStream = null;
    private boolean register = false;
    private boolean messageFlag = false;
    public static String uname = "";

    /***
     * start communicate with server with host and port number.
     * ask for username first if successfully registered then it will be start messaging to users.
     * @throws Exception
     */

    public void startCommunicateWithServer() throws Exception {
        socket = new Socket("127.0.0.1", 2500);
        Scanner sc = new Scanner(System.in);

        if (!messageFlag && socket.isConnected()) {
            try {

                ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
                ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());


                do {
                    System.out.println("Enter UserName");
                    uname = sc.next();
                    ClientMessage message = new ClientMessage();
                    message.setType("Register");
                    message.setSenderId(uname);
                    objectOutputStream.writeObject(message);
                    objectOutputStream.flush();
                    objectOutputStream.reset();
                    if ((message = (ClientMessage) objectInputStream.readObject()) != null) {
                        System.out.println(message.getSenderId() + " " + message.getMessage());
                        if (message.getMessage().contains("Registered")) {
                            register = true;

                            System.out.println("============================= Message Format ============================================================");
                            System.out.println();
                            System.out.println("EX. <YOUR MESSAGE>  <TargetId> . ||   Please Note There is a Space between YOUR MESSAGE and TargetId. ");
                            System.out.println();
                            System.out.println("============================== Start Messagging =========================================================");


                        }

                    }
                } while (!register);

                new Thread() {
                    @Override
                    public void run() {
                        try {

                            while (true) {
                                ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
                                ClientMessage message = new ClientMessage();
                                if ((message = (ClientMessage) objectInputStream.readObject()) != null) {
                                    System.out.println(message.getSenderId() + " : " + message.getMessage());
                                }
                            }
                        } catch (Exception e) {
                            if (!socket.isConnected()) {
                                System.out.println("Server Shutdown");
                            }
                        }
                    }
                }.start();

                messageFlag = true;
                new Runnable() {
                    @Override
                    public void run() {
                        InputStreamReader r = new InputStreamReader(System.in);
                        BufferedReader br = new BufferedReader(r);
                        while (true) {
                            try {
                                String messageSend = br.readLine();
                                if (messageSend.split(" ").length > 1) {
                                    String receiver = messageSend.substring(messageSend.lastIndexOf(" ") + 1);
                                    String finalMessage = messageSend.substring(0, messageSend.lastIndexOf(" "));
                                    ClientMessage message = new ClientMessage();
                                    message.setId(receiver);
                                    message.setMessage(finalMessage);
                                    message.setType("Message");
                                    message.setSenderId(uname);
                                    objectOutputStream.writeObject(message);
                                    objectOutputStream.flush();

                                } else {
                                    System.out.println("Please Enter Receiver Id in the message end.");
                                }

                            } catch (Exception e) {
                                if (!socket.isConnected()) {
                                    System.out.println("Server Shutdown");
                                }
                            }
                        }
                    }
                }.run();


            } catch (Exception e) {
                if (!socket.isConnected()) {
                    System.out.println("Server Shutdown");
                }
            }


        }
    }


    public static void main(String[] args) throws Exception {

        ClientSocket clientSocket = new ClientSocket();
        clientSocket.startCommunicateWithServer();

    }
}
