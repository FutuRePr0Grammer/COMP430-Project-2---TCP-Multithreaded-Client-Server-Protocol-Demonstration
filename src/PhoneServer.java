/*David Rochon
*  Code to be used in project 2 - must make Phone client class. Should do the following:
* (1)Send three types of messages. STORE with name and phone number, GET, and REMOVE
* This is the talk server example. I got it working, and will work as example for real project 2
 */

import java.io.*;
import java.util.*;
import java.net.*;

public class PhoneServer {
    // The port number on which the server will be listening
    private static int port = 2014;
    // The server socket.
    private static ServerSocket listener = null;
    // The client socket.
    private static Socket clientSocket = null;

    public static void main(String[] args) throws Exception {

        listen();
    }
    /** Monitor a port for connections. Each time one is established, pass resulting Socket to
     handleConnection.
     */
    public static  void listen()
    {
        //Open a server socket on the specified port number
        try
        {
            listener = new ServerSocket(port);
        }
        catch (IOException ioe){ System.out.println("IOException: " + ioe); }
        System.out.println(" PhoneServer is up and running\n");
        while(true)
        {

            try
            {
                clientSocket = listener.accept(); //accept connection request from a client

                //construct a client request thread to process the client request
                ClientThread request = new ClientThread(clientSocket);

                //start the thread
                request.start();
            }
            catch (IOException ioe){ System.out.println("IOException: " + ioe); }

        }//while

    }//listen
}//PhoneServer

class ClientThread extends Thread
{
    Socket socket;

    //constructor
    public ClientThread(Socket socket)
    {
        this.socket = socket;
    }

    //implement the run method
    public void run()
    {
        handleConnection(socket);
    }
    //implement the handleConnection method here.
    protected void handleConnection(Socket socket)
    {
        try {
            //TODO: Add boolean functionality with loop.
            //TODO: Figure out how to prevent \n from making me hit enter twice to print things
            String s="";
            //boolean isConnected = true;

            //creating arraylists to store the names and phone numbers, respectively
            ArrayList names = new ArrayList();
            ArrayList numbers = new ArrayList();
            String greeting = "100 OK";


            //to get input from the client
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            //Out to the client -- Enable auto-flush
            PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);


            /*System.out.println("got connection from " +
                    socket.getInetAddress().getHostName() + "\n");*/
            System.out.println("HELO");

            out.println(" PhoneServer for CS-430 by David Rochon" +
                    " To close connection type, END");

            out.println(greeting);

            /*String connectionGreet = in.readLine();
            if(connectionGreet.equals("HELO")){
                out.println(greeting);
            }
            else {
                out.println("400 Bad Request");
                isConnected = false;
            }*/

            while (true)
            {
                if((s = in.readLine()).length() != 0)
                {
                    String[] inputSplit = s.split(" ");
                    /*for(int i = 0; i < inputSplit.length; i++){
                        out.println(inputSplit[i]);
                    }*/
                    if (s.equals("END"))
                    {
                        out.println("END called");
                        break;
                    }
                    else if(inputSplit[0].equals("STORE")){
                        out.println("100 OK");
                        names.add(inputSplit[1]);
                        numbers.add(inputSplit[2]);
                    }
                    else if(inputSplit[0].equals("REMOVE")){
                        out.println("100 OK");
                        for(int i = 0; i < names.size(); i++){
                            if(inputSplit[1].equals(names.get(i))){
                                names.remove(i);
                                numbers.remove(i);
                                /*debugging statements*/
                                //out.println(names);
                                //out.println(numbers);
                            }
                        }
                    }
                    else if(inputSplit[0].equals("GET")){
                        for(int i = 0; i < names.size(); i++){
                            if(inputSplit[1].equals(names.get(i))){
                                //Debugging statement
                                //out.println("GET " + inputSplit[1]);
                                out.print(numbers.get(i));
                                out.println();
                            }
                            else{
                                //out.println("WrongGET " + inputSplit[1]);
                                out.print("300 Not Found");
                                out.println();
                            }
                        }
                    }
                    else{
                        out.print("400 Bad Request");
                        out.println();
                    }

                }
            }


            socket.close();
        }
        catch (IOException e) { }
    }
}

