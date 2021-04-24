import java.io.IOException;
import java.net.Inet4Address;
import java.net.NetworkInterface;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) throws IOException {
        Client client = new Client();
        //System.out.println("I am " + client.sendMessage(client.getUsername()));

        //System.out.println(client.sendMessage("TIME?"));
        //client.getMessage("GET? SHA-256 big shaq");
        //client.getMessage("GET? SHA-256 bc18ecb5316e029af586fdec9fd533f413b16652bafe079b23e021a6d8ed69aa");
        //client.listMessages("LIST? 1619236022 1");
        //System.out.println(Inet4Address.getLocalHost().getHostName());
        //System.out.println(Inet4Address.getLocalHost().getHostName() + " @ " + client.getClientSocket().getInetAddress().getHostAddress());
        client.stopConnection();

        /**
        Message msg = new Message(client.getUsername(), "Hello" + '\n' + "Timetest");

        for(int i = 0; i < msg.getHeaders().size(); i++){
            //System.out.println(client.sendMessage(msg.getHeaders().elementAt(i).toString()));
            client.getOut().println(msg.getHeaders().elementAt(i).toString());
        }

        for(int i = 0; i< msg.getBody().size(); i++){
            //System.out.println(client.sendMessage(msg.getBody().elementAt(i).toString()));
            client.getOut().println(msg.getBody().elementAt(i));

        }
        **/


        /*
        Scanner sc = new Scanner(System.in);
        while (true){
            System.out.println(client.sendMessage(sc.nextLine()));
        }
        */

    }
}
