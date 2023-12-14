package freedom;


import java.util.Collections;
import java.util.HashMap;
import java.util.Map;


public class httpRequest {


    private final static String DELIMITER = "/r/n/r/n";

    private final static String NEW_LINE = "r/n";

    private final static String HEADER_DELIMITER = ":";

    private final String message;

    private final Map<String,String> headers;

    private final httpMethod method;

    private final String url;

     private final String body;

    public httpRequest(String message)
    {
        this.message=message;

        String[] parts = message.split(DELIMITER);

        String head = parts[0]; 

        String[] headers = head.split(NEW_LINE);

        String[] firstLine = headers[0].split(" ");

        method  = httpMethod.valueOf(firstLine[0]);

        url = firstLine[1];

        this.headers = Collections.unmodifiableMap(

            new HashMap<>() {{

                for ( int i=1; i<headers.length ; i++) 
                {

               String[] headerPart = headers[i].split(HEADER_DELIMITER, 2);
               put(headerPart[0].trim(),headerPart[1].trim());
                }

        }}

        );

        String bodyLength = this.headers.get("Content-Length");

        int length = bodyLength != null ? Integer.parseInt(bodyLength):0;

        this.body = parts.length > 1 ? parts[1].trim().substring(0, length): " ";

    }



    public String getMessage() {
        return this.message;
    }


    public Map<String,String> getHeaders() {
        return this.headers;
    }


    public httpMethod getMethod() {
        return this.method;
    }


    public String getUrl() {
        return this.url;
    }


    public String getBody() {
        return this.body;
    }


}
