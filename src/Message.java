import java.util.Arrays;
import java.util.Vector;

public class Message {
    private String id;
    private long time;
    private String from;
    private String text;
    private Vector<String> headers;
    private Vector<String> body;

    public Message(String from, String text){
        //Messages must have who they're from and have a body.
        this.from = from;
        this.text = text;
        headers = new Vector<>();
        formatMsg(); //Formats the message to be sent.
    }

    public void formatMsg(){
        //Format headers
        headers.add("<h>");
        id = "wtf is a sha-256 hash";
        addHeader("Message-id", id);
        addHeader("Time-sent", Long.toString(time));
        addHeader("From", from);
        addHeader("Contents", Integer.toString(countLines(text)));
        headers.add("</h>");

        //Format body
        body = new Vector<String>(Arrays.asList(text.split("\\n"))); //Split the text by line and add to a vector.
        body.add("</e>");
    }

    //Makes headers look nice in the txt file.
    public void addHeader(String headerName, String header){
        String s = headerName + ": " + header;
        headers.add(s);
    }

    //Used to count lines of body for the "Contents" header.
    public int countLines(String str) {
        if(str == null || str.isEmpty())
        {
            return 0;
        }
        int lines = 1;
        int pos = 0;
        while ((pos = str.indexOf("\n", pos) + 1) != 0) {
            lines++;
        }
        return lines;
    }

    //Setters and Getters

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
        for(int i=0;i<headers.size();i++){
            if(headers.elementAt(i).toString().contains("Time-sent:")){
                headers.set(i, "Time-sent: " + Long.toString(time));
            }
        }
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
        body = new Vector<String>(Arrays.asList(text.split("\\n"))); //Split the text by line and add to a vector.
        body.add("</e>");

        for(int i=0;i<headers.size();i++){
            if(headers.elementAt(i).toString().contains("Contents:")){
                headers.set(i, "Contents: " + countLines(text));
            }
        }
    }

    public Vector getHeaders() {
        return headers;
    }

    public void setHeaders(Vector headers) {
        this.headers = headers;
    }

    public Vector<String> getBody() {
        return body;
    }

    public void setBody(Vector<String> body) {
        this.body = body;
    }
}
