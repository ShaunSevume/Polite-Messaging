import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;

public class Server {
    private ServerSocket serverSocket;
    private String identifier; //The hostname (machine name) and IP address of the server.
    private int version; //Used in the PROTOCOL? request to identify PM version.

    public Server() throws IOException {
        start(20111); //OR you could just try catch lol
    }

    public void start(int port) throws IOException {
        serverSocket = new ServerSocket(port); //Creates a socket and binds it to a port. ALL clients will have to connect through this port.
        identifier = "Server using " + InetAddress.getLocalHost().getHostName() + "@" + InetAddress.getLocalHost().getHostAddress();
        version = 1;
        while (true) //Forces the server to constantly listen for new connection requests (infinite loop)
            new ClientHandler(serverSocket.accept(),version,identifier).start();  //For each client that wants to connect to the server, a new thread is created to handle that connection. This way, the server can cater to multiple clients at once in parallel.
    }

    public void stop() throws IOException {
        serverSocket.close(); //Closing the server will end the connection with ALL clients, as they are all bound to the same port.
    }

    //A new instance of this is created for each new client that connects to the server. This allows it to deal with each client's request individually and in parallel.
    private static class ClientHandler extends Thread {
        private Socket clientSocket;
        private int version;
        private String identifier;
        private PrintWriter out; //Server output stream, bound to client input stream.
        private BufferedReader in; //Server input stream, bound to client output stream.
        private FileWriter writer; //FileWriter for writing any messages sent by clients to 'messages.txt'.
        private BufferedReader reader; //BufferedReader (to be wrapped around FileReader) for reading messages requested by the client from 'messages.txt'.
        private boolean keepAlive; //Used to keep the thread alive until the connection dies.
        private boolean writing; //Used to indicate that the server is currently writing to 'messages.txt'.


        public ClientHandler(Socket socket, int version, String identifier) {
            System.out.println("Someone connected");
            this.clientSocket = socket;
            this.version = version;
            this. identifier = identifier;
        }

        public void run() {
            keepAlive = true; //Keep the thread alive!
            writing = false; //Not writing anything yet...
            while (keepAlive) { // ._.

                try { //Since the run() method of threads aren't allowed to throw exceptions, the ENTIRE block of code has to be in a try-catch statement.

                    //Binding input and output streams of client and server
                    out = new PrintWriter(clientSocket.getOutputStream(), true);
                    in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

                    //Handling the PROTOCOL? request.
                    System.out.println(in.readLine());
                    out.println("PROTOCOL? " + version + " " + identifier);

                    String inputLine; //Any line the client sends will be assigned to this variable.
                    while ((inputLine = in.readLine()) != null) { //Loop runs as long as the stream stays connected.

                        //Exit signal
                        if ("BYE!".equals(inputLine)) {
                            break;
                        }
                        //out.println(inputLine); //Echoes back whatever (this might be a problem)

                        //Detects whether the client is trying to send a message over (which will have to be saved). All messages will start with the special string sequence "<h>".
                        if (inputLine.equals("<h>")) {
                            writing = true; //This changes to true, as the server will now be writing. While this is true, any further lines received from the client will be written to 'messages.txt', until the special string sequence "</e>" is received to signify the end of the message.

                            //Opens a FileWriter to write subsequent messages received from the client to 'messages.txt'. The writing is handled further down in the code.
                            try {
                                writer = new FileWriter("messages.txt", true);
                            } catch (IOException e) {
                                e.printStackTrace();
                                break;
                            }
                        }

                        //A time request from the client will return the current time at the server's end as a unix timestamp. A check is in place to see whether the server is currently writing or not, which determines whether this command is processed or written instead.
                        else if (inputLine.equals("TIME?") && !writing) {
                            out.println("NOW " + (System.currentTimeMillis() / 1000L));
                        }

                        //This command will prompt the server to open the txt file and read through it in an attempt to find the requested message, but only if it is currently not writing.
                        else if (inputLine.contains("GET? ") && !writing) {
                            try {
                                boolean found = false;
                                reader = new BufferedReader(new FileReader("messages.txt")); //Opens a new FileReader and wraps a BufferedReader around it so we can access the readLine() method
                                String readLine; //The current line being read from the file.
                                String msgID = inputLine.substring(5); //The command takes up the first 13 characters of the string it is sent in. Everything afterwards will be the message ID.

                                while ((readLine = reader.readLine()) != null) { //Loops until the end of the file
                                    if (readLine.contains("Message-id:") && readLine.contains(msgID)) { //Checks to see whether the line is a Message-id header and whether the ID matches the one the client requested.
                                        found = true;
                                        out.println("FOUND"); //If so, this is sent to notify the client that the server is about to send some data back. This will trigger a while loop on the clientside to listen out for further server responses until it signals that it has reached the end of the file.
                                        while (!readLine.equals("</e>")) { //This will keep the server sending everything it reads from here on until it reaches the "</e>" character sequence, which signals the end of a message.
                                            if (readLine.equals("</h>")) { //This sequence signals the end of the headers, however it does not need to be sent across so if it is read, it is simply skipped.
                                                readLine = reader.readLine(); //Skipping the "</h>" or "end of headers" character.
                                            }
                                            out.println(readLine); //Sends back whatever line is currently being read from the txt file. This will be all the headers and body of the requested message.
                                            readLine = reader.readLine(); //Moves to the next line.
                                        }

                                        //out.println("</eOF>");
                                    }
                                }
                                if (!found) {
                                    out.println("SORRY");
                                }
                                out.println("</eOF>");  //Returns this to signal the end of the file has been reached and the client can stop listening now.
                                reader.close(); //Closes the filereader.
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }

                        //Wow.
                        else if (inputLine.contains("LIST?")) {
                            //Gets the "since" time value and converts it to a long, and gets the number of headers.
                            String[] inputSplit = inputLine.split(" ");

                            if(inputSplit.length != 3){ //Response invalid, therefore end the loop
                                break;
                            }
                            Long since = Long.parseLong(inputSplit[1]);
                            String headerNo = inputSplit[2];

                            if(Integer.parseInt(headerNo) == 0){
                                //Print all the messages.
                                try {
                                    reader = new BufferedReader(new FileReader("messages.txt"));
                                    String currLine;
                                    while ((currLine = reader.readLine()) != null) { //Reads the file until the end
                                        if(!currLine.equals("<h>") && !currLine.equals("</h>") && !currLine.equals("</e>")){
                                            out.println(currLine);
                                            }
                                        }
                                    out.println("</eOF");
                                }catch(IOException e){
                                    e.printStackTrace();
                                    break;
                                }
                            }

                            out.println(headerNo); //Returns the number of headers to the client to sync for loop execution number (this will be the number the integer 'i' will iterate up to on both sides).
                            Vector<String> headers = new Vector<>(); //A vector to store all the headers sent by the client since they will need to be computed at once.
                            for (int i = 0; i < Integer.parseInt(headerNo); i++) { //For the amount of headers specified to begin with, add it to the vector of headers.
                                headers.add(in.readLine()); //This runs in sync with a for loop on the clientside that will persistently ask for user input until they send over the correct number of headers.
                            }
                            try {
                                reader = new BufferedReader(new FileReader("messages.txt")); //Opens a new FileReader and wraps a BufferedReader around it so we can access the readLine() method
                                String currLine; //The current line being read from the file.
                                Vector<String> msgs = new Vector<>(); //Will store the ID's of any messages that fit the criteria of the search,
                                while ((currLine = reader.readLine()) != null) { //Reads the file until the end
                                    if (currLine.equals("<h>")) { //This signifies the start of a new message.
                                        boolean fail = false; //Each new message must be tested to see whether it passes or not, hence this reinitialises this boolean every time a new message is found.
                                        Long time;
                                        Vector<String> currentMsg = new Vector<>(); //Will contain an entire message from Message-id header to the last line of body, so each message can be evaluated in its entirety.
                                        Vector<String> currHeads = new Vector<>(); //Will store any headers found in the current message that match the headers the client was looking for.
                                        currLine = reader.readLine(); //Reads the next line (first actual header, Message-id).
                                        while (!currLine.equals("</e>")) { //Adds each line to the currentMsg vector until the end of message character sequence is reached.
                                            currentMsg.add(currLine); //See above.
                                            if (currLine.contains("Time-sent:")) { //Checks to see whether the header is the Time-sent header of a message, as it must be evaluated as part of the client's search criteria.
                                                time = Long.parseLong(currLine.split(" ")[1]); //The next word will be the unix value for time if the header is indeed Time-sent, as it is formatted like this universally across all messages.
                                                if (time < since) {
                                                    fail = true; //The message fails the criteria check if the time is before the time the client requested (message is too old).
                                                }
                                            }

                                            //Checks to see whether the current line is a header the user searched for. If it is, it is added to the vector of strings that keeps ahold of it.
                                            for (int i = 0; i < headers.size(); i++) {
                                                if (currLine.equals(headers.elementAt(i))) {
                                                    currHeads.add(currLine);
                                                }
                                            }
                                            currLine = reader.readLine(); //Reads the next line.
                                        }

                                        //If the size of currHeads is the same as the amount of headers the user specified, then we know its a match, because currHeads will only contain headers that were matched with the criteria in the first place. If it contains all of them, the size will match the number specified. If not, then the current message fails the criteria check.
                                        if (currHeads.size() != Integer.parseInt(headerNo)) {
                                            fail = true;
                                        }
                                        if (!fail) {
                                            msgs.add(currentMsg.elementAt(0)); //If the current message didn't fail the check, its header is added to the vector of strings that will be outputted back to the client.
                                        }
                                                                        }
                                }

                                out.println("MESSAGES " + msgs.size()); //Tells the client how many messages were found

                                if (msgs.size() > 0) { //If there were any messages found then the server will send over the id's. The client will know how many its going to receive from the number of messages the server said it had found. This is how their for loops sync up.
                                    for (int i = 0; i < msgs.size(); i++) {
                                        out.println(msgs.elementAt(i)); //Sends each message id back to the client.
                                    }
                                }

                            } catch (IOException e) {
                                e.printStackTrace();
                                break;
                            }


                        } else if(writing) {
                            //Do nothing
                        }else{
                            out.println(inputLine); //Echoes back
                        }

                        //This block of code is triggered if the boolean 'writing' is set to true. This can only be the case if the "<h>" character sequence is received.
                        if (writing) {
                            writer.write(inputLine); //The most recent line sent by the client will be written to the txt file.
                            writer.write('\n'); //A new line will also be added for the next header or line of body.

                            if (inputLine.equals("</e>")) { //This being sent over signals that the end of the message has been reached. After this, there will be nothing further to write.
                                writing = false; //Sets this to false so the server no longer writes any further lines sent by the client to the txt file.
                                writer.close(); //Closes the filewriter.
                            }
                        }
                    }

                    //When the client disconnects (signalled by the server breaking out of the above loop), the sockets will be closed.
                    in.close();
                    out.close();
                    clientSocket.close();
                    System.out.println("Someone disconnected politely.");
                    keepAlive = false; //As the client has disconnected, this thread can now die in peace.
                } catch (IOException e) {
                    //e.printStackTrace();

                    //If for whatever reason the client disconnects in an unintended way (any way that isn't through sending the disconnect request, then the thread can die anyway.
                    System.out.println("Someone disconnected impolitely!");
                    keepAlive = false;
                }
            }
        }
    }

    public static void main(String[] args) throws IOException {
        Server server = new Server();
        server.start(20111); //OR you could just try catch lol
    }
}
