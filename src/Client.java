import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Inet4Address;
import java.net.Socket;
import java.util.Scanner;

/*
CLIENT WILL ESTABLISH A CONTINUOUS CONNECTION WITH SERVER, SEND MULTIPLE MESSAGES AND THEN ISSUE A TERMINATION REQUEST FOR THE SERVER.
 */


public class Client {
    private Socket clientSocket;
    private PrintWriter out; //Client output stream, for binding to server input stream.
    private BufferedReader in; //Client input stream, for binding to server output stream.
    private String username; //The name of the client, used for the 'from' header in messages.
    private String identifier; //The hostname (machine name) and IP address of the client.
    private int version; //Used in the PROTOCOL? request to identify PM version.


    public Client() throws IOException{
        //Asks the user to specify a username before connecting to the server. This is what will show up in the "from" header in any messages this client sends.
        System.out.println("Identify yourself: ");
        Scanner sc = new Scanner(System.in);
        username = sc.nextLine();
        version = 1;
        startConnection("127.0.0.1", 20111);
    }

    public void startConnection(String ip, int port) throws IOException {
        clientSocket = new Socket(ip, port);
        identifier = username + " using " + Inet4Address.getLocalHost().getHostName() + " @ " + clientSocket.getInetAddress().getHostAddress();
        out = new PrintWriter(clientSocket.getOutputStream(), true); //Accesses output and input streams to write and read messages to and from the server.
        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream())); //Client input stream is bound to server output stream and vice versa.

        //Handling the PROTOCOL? request.
        out.println("PROTOCOL? " + version + " " + identifier);
        System.out.println(in.readLine());
    }

    //Sends a message, prints the response.
    public String sendMessage(String msg) throws IOException {
        out.println(msg);
        String resp = in.readLine();
        return resp;
    }

    //Technically, this can return multiple messages, which should never actually happen, but in the extremely rare case it does, the client is prepared to deal with it.
    public void getMessage(String msg) throws IOException {
        out.println(msg); //Sends over the ID of the message to look for.
        String resp = in.readLine(); //Gets the server's response.
        if(resp.equals("FOUND")){ //If this is received, then a match has been found, and now the client should listen to any subsequent lines sent from the server as they will contain the headers/body of the message that they requested.
            System.out.println(resp);
            while(!resp.equals("</eOF>")){ //The client will now listen out for server responses until this character stream has been received, which signals that the server has reached the end of the file and is finished reading.
                resp = in.readLine(); //The response will then be assigned to this string for processing by the client.
                if(!resp.equals("</eOF>")) { //If the string is this character sequence then it means that the end of the file was reached.
                    System.out.println(resp); //The response (which will be part of the requested message) is then printed.
                }
            }
        }else if(resp.equals("SORRY")){ //If this response is received, then the message was not found.
            System.out.println(resp); //This response is then printed out.
            out.println("It's okay <3"); //The client has to give a response to the server so...
            out.println("Done!"); //The server will still send the "</eOF>" character sequence once it's reached the end of the file, so this is here to acknowledge that.
        }

    }

    public void listMessages(String msg) throws IOException {
        //Check to see if the time is NOT in the future and the headers aren't 0.
        String[] msgSplit = msg.split(" ");
        Long since = Long.parseLong(msgSplit[1]);
        String headerNo = msgSplit[2];
        if(since > (System.currentTimeMillis() / 1000L) || Integer.parseInt(headerNo) == 0){
            System.out.println("Invalid time or header number.");
        }else {
            //System.out.println(msg);
            out.println(msg); //Sends the request to the server
            String resp = in.readLine(); //Receive server response
            Scanner sc = new Scanner(System.in); //Request user input
            for (int i = 0; i < Integer.parseInt(resp); i++) {
                out.println(sc.nextLine()); //For the amount of headers specified to send, send that many headers over
            }
            int count = 0;
            resp = in.readLine(); //Get server response
            if (resp.contains("MESSAGES")) { //It's highly unlikely it will never be this, but just in case, the check is there.
                count = Integer.parseInt(resp.split(" ")[1]); //Assigns the number of messages to the 'count' variable
                System.out.println(resp); //Also prints out the response
            }

            if (count > 0) { //If the count > 0 then there's messages to receive...
                for (int i = 0; i < count; i++) {
                    resp = in.readLine(); //Each message-id will be read and then printed.
                    System.out.println(resp);
                }
            }
        }
    }

    public void stopConnection() throws IOException {
        out.println("BYE!");
        in.close();
        out.close();
        clientSocket.close();
    }

    /*
    @Test //Test case method that will return whether it passed or failed to the output window. Used for debugging really.
    public void givenClient1_whenServerResponds_thenCorrect() throws IOException {
        Client client1 = new Client();
        client1.startConnection("127.0.0.1", 20111);
        String msg1 = client1.sendMessage("hello");
        String msg2 = client1.sendMessage("world");
        String terminate = client1.sendMessage("."); //This last line will be the signal for the server to terminate the connection.

        //Testing the server's responses (which will just be echoes of whatever was sent)
        assertEquals(msg1, "hello");
        assertEquals(msg2, "world");
        assertEquals(terminate, "bye");
    }

    @Test
    public void givenClient2_whenServerResponds_thenCorrect() throws IOException {
        Client client2 = new Client();
        client2.startConnection("127.0.0.1", 20111);
        String msg1 = client2.sendMessage("hello");
        String msg2 = client2.sendMessage("world");
        String terminate = client2.sendMessage(".");

        assertEquals(msg1, "hello");
        assertEquals(msg2, "world");
        assertEquals(terminate, "bye");
    }
    */
    public Socket getClientSocket() {
        return clientSocket;
    }

    public PrintWriter getOut() {
        return out;
    }

    public BufferedReader getIn() {
        return in;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}