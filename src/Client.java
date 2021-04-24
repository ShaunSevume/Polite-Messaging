import org.junit.jupiter.api.Test;

import javax.swing.*;

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

public class Client extends javax.swing.JFrame{
    private Socket clientSocket;
    private PrintWriter out; //Client output stream, for binding to server input stream.
    private BufferedReader in; //Client input stream, for binding to server output stream.
    private String username; //The name of the client, used for the 'from' header in messages.
    private String identifier; //The hostname (machine name) and IP address of the client.
    private int version; //Used in the PROTOCOL? request to identify PM version.
    private Message toBeSent;

    //GUI components
    private javax.swing.JButton SendBtn;
    private javax.swing.JButton hedEdit;
    private javax.swing.JTextArea inputArea;
    private javax.swing.JScrollPane inputScrollPane;
    private javax.swing.JTextArea outputArea;
    private javax.swing.JScrollPane outputScrollPane;
    private javax.swing.JToggleButton toggleMsg;

    public Client() throws IOException{
        //Asks the user to specify a username before connecting to the server. This is what will show up in the "from" header in any messages this client sends.
        String username = new JOptionPane().showInputDialog("Enter username/email address.");
        this.username = username;
        version = 1;
        initComponents();
        startConnection("127.0.0.1", 20111);
    }

    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Client.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Client.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Client.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Client.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    new Client().setVisible(true);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void initComponents() {

        outputScrollPane = new javax.swing.JScrollPane();
        outputArea = new javax.swing.JTextArea();
        toggleMsg = new javax.swing.JToggleButton();
        SendBtn = new javax.swing.JButton();
        hedEdit = new javax.swing.JButton();
        inputScrollPane = new javax.swing.JScrollPane();
        inputArea = new javax.swing.JTextArea();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setMinimumSize(new java.awt.Dimension(773, 491));
        setLocationRelativeTo(null);

        outputArea.setColumns(20);
        outputArea.setRows(5);
        outputScrollPane.setViewportView(outputArea);

        toggleMsg.setText("MESSAGE");
        toggleMsg.setMaximumSize(new java.awt.Dimension(107, 25));
        toggleMsg.setMinimumSize(new java.awt.Dimension(107, 25));
        toggleMsg.setPreferredSize(new java.awt.Dimension(107, 25));
        toggleMsg.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                toggleMsgActionPerformed(evt);
            }
        });

        SendBtn.setText("Send");
        SendBtn.setMaximumSize(new java.awt.Dimension(107, 25));
        SendBtn.setMinimumSize(new java.awt.Dimension(107, 25));
        SendBtn.setPreferredSize(new java.awt.Dimension(107, 25));
        SendBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                SendBtnActionPerformed(evt);
            }
        });

        hedEdit.setText("Edit Headers");
        hedEdit.setMaximumSize(new java.awt.Dimension(107, 25));
        hedEdit.setMinimumSize(new java.awt.Dimension(107, 25));
        hedEdit.setPreferredSize(new java.awt.Dimension(107, 25));
        hedEdit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                hedEditActionPerformed(evt);
            }
        });

        inputArea.setColumns(20);
        inputArea.setRows(5);
        inputScrollPane.setViewportView(inputArea);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(outputScrollPane)
                                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                                .addComponent(inputScrollPane)
                                                .addGap(18, 18, 18)
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                                        .addComponent(toggleMsg, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                        .addComponent(hedEdit, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                        .addComponent(SendBtn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                                .addContainerGap())
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addGap(8, 8, 8)
                                .addComponent(outputScrollPane)
                                .addGap(18, 18, 18)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                        .addGroup(layout.createSequentialGroup()
                                                .addComponent(toggleMsg, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(hedEdit, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(SendBtn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                        .addComponent(inputScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(10, 10, 10))
        );

        pack();
    }// </editor-fold>

    private void hedEditActionPerformed(java.awt.event.ActionEvent evt) {
        new EditHeaders().setVisible(true);
    }

    private void SendBtnActionPerformed(java.awt.event.ActionEvent evt) {
        // TODO add your handling code here:
    }

    private void toggleMsgActionPerformed(java.awt.event.ActionEvent evt) {
        if(toggleMsg.getText().equals("MESSAGE")){
            toggleMsg.setText("REQUEST");
            hedEdit.setVisible(false);
        }else if(toggleMsg.getText().equals("REQUEST")){
            toggleMsg.setText("MESSAGE");
            hedEdit.setVisible(true);
        }
    }
    public void startConnection(String ip, int port) throws IOException {
        clientSocket = new Socket(ip, port);
        identifier = username + " using " + Inet4Address.getLocalHost().getHostName() + " @ " + clientSocket.getInetAddress().getHostAddress();
        out = new PrintWriter(clientSocket.getOutputStream(), true); //Accesses output and input streams to write and read messages to and from the server.
        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream())); //Client input stream is bound to server output stream and vice versa.

        //Handling the PROTOCOL? request.
        String protocol = "PROTOCOL? " + version + " " + identifier;
        out.println(protocol);
        System.out.println(in.readLine());
        newMsg();
    }

    public String showOnGUI(String s){
        return s;
    }

    public void newMsg(){
        toBeSent = new Message(username, "");
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

    public class EditHeaders extends javax.swing.JFrame {

        public EditHeaders() {
            try {
                for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                    if ("Nimbus".equals(info.getName())) {
                        javax.swing.UIManager.setLookAndFeel(info.getClassName());
                        break;
                    }
                }
            } catch (ClassNotFoundException ex) {
                java.util.logging.Logger.getLogger(EditHeaders.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
            } catch (InstantiationException ex) {
                java.util.logging.Logger.getLogger(EditHeaders.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
            } catch (IllegalAccessException ex) {
                java.util.logging.Logger.getLogger(EditHeaders.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
            } catch (javax.swing.UnsupportedLookAndFeelException ex) {
                java.util.logging.Logger.getLogger(EditHeaders.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
            }
            initComponents();

        }

//        private void refreshMsg(){
//            outputHeaders.setText("");
//            for(int i = 0; i < toBeSent.getHeaders().size(); i++){
//                outputHeaders.append(toBeSent.getHeaders().elementAt(i).toString());
//            }
//        }

        private void initComponents() {

            addHead = new javax.swing.JButton();
            resetHead = new javax.swing.JButton();
            closeBtn = new javax.swing.JButton();
            outputHeaderScrollpane = new javax.swing.JScrollPane();
            outputHeaders = new javax.swing.JTextArea();
            inputHeader = new javax.swing.JTextField();

            setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
            setLocationRelativeTo(null);

            addHead.setText("Add Header");
            addHead.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    addHeadActionPerformed(evt);
                }
            });

            resetHead.setText("Reset Headers");
            resetHead.setMaximumSize(new java.awt.Dimension(107, 25));
            resetHead.setMinimumSize(new java.awt.Dimension(107, 25));
            resetHead.setPreferredSize(new java.awt.Dimension(107, 25));
            resetHead.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    resetHeadActionPerformed(evt);
                }
            });

            closeBtn.setText("Close");
            closeBtn.setMaximumSize(new java.awt.Dimension(107, 25));
            closeBtn.setMinimumSize(new java.awt.Dimension(107, 25));
            closeBtn.setPreferredSize(new java.awt.Dimension(107, 25));
            closeBtn.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    closeBtnActionPerformed(evt);
                }
            });

            outputHeaders.setEditable(false);
            outputHeaders.setColumns(20);
            outputHeaders.setRows(5);
            outputHeaderScrollpane.setViewportView(outputHeaders);

            javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
            getContentPane().setLayout(layout);
            layout.setHorizontalGroup(
                    layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                    .addContainerGap()
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(outputHeaderScrollpane, javax.swing.GroupLayout.DEFAULT_SIZE, 502, Short.MAX_VALUE)
                                            .addGroup(layout.createSequentialGroup()
                                                    .addComponent(inputHeader)
                                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                                            .addComponent(resetHead, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                            .addComponent(closeBtn, javax.swing.GroupLayout.DEFAULT_SIZE, 119, Short.MAX_VALUE)
                                                            .addComponent(addHead, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                                    .addContainerGap())
            );
            layout.setVerticalGroup(
                    layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                    .addContainerGap()
                                    .addComponent(outputHeaderScrollpane, javax.swing.GroupLayout.DEFAULT_SIZE, 255, Short.MAX_VALUE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addGroup(layout.createSequentialGroup()
                                                    .addComponent(addHead)
                                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                    .addComponent(resetHead, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                    .addComponent(closeBtn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                            .addComponent(inputHeader, javax.swing.GroupLayout.PREFERRED_SIZE, 86, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGap(10, 10, 10))
            );

            pack();
        }// </editor-fold>

        private void closeBtnActionPerformed(java.awt.event.ActionEvent evt) {
            dispose();
        }

        private void resetHeadActionPerformed(java.awt.event.ActionEvent evt) {
            // TODO add your handling code here:
        }

        private void addHeadActionPerformed(java.awt.event.ActionEvent evt) {
            // TODO add your handling code here:
            inputHeader.setText("");
        }

        // Variables declaration - do not modify
        private javax.swing.JButton addHead;
        private javax.swing.JButton closeBtn;
        private javax.swing.JTextField inputHeader;
        private javax.swing.JScrollPane outputHeaderScrollpane;
        private javax.swing.JTextArea outputHeaders;
        private javax.swing.JButton resetHead;
        // End of variables declaration
    }

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