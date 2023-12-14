package freedom;


import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeoutException;


class Server
{




    
    private final static int BUFFER_SIZE = 256;

  
    private AsynchronousServerSocketChannel server;

    private final httphandler handle;


    public Server(httphandler handle) {
        this.handle = handle;
    }


    public void bootstrap() 
    throws InterruptedException, ExecutionException
    {
        try 
        {
        server = AsynchronousServerSocketChannel.open();
          
        server.bind (new InetSocketAddress("127.0.0.1", 8088));
          
         while (true) {
            Future<AsynchronousSocketChannel> future = server.accept();
            handleClient(future);
            
         }
        } 
        catch (IOException e) 
        {
            e.printStackTrace();
        }
        
    }
    /**
     * @param Future
     * @throws InterruptedException
     * @throws ExecutionException
     * @throws TimeoutException
     * @throws IOException
     */
    private void handleClient(Future<AsynchronousSocketChannel> future) throws IOException, InterruptedException, ExecutionException
  
    {
          System.out.println("new client connection");
          AsynchronousSocketChannel   clientChannel = future.get();
          
          while(clientChannel != null && clientChannel.isOpen())
          {
                ByteBuffer buffer =  ByteBuffer.allocate(BUFFER_SIZE);
                
                StringBuilder builder = new StringBuilder();
                boolean keepReading = true;
                while (keepReading) 
                {
                int readResult = clientChannel.read(buffer).get();
                
                keepReading = readResult == BUFFER_SIZE;
                 buffer.flip();

                CharBuffer charBuffer =  StandardCharsets.UTF_8.decode(buffer);

                builder.append(charBuffer);
                buffer.clear();

                }

                httpRequest request = new httpRequest(builder.toString());

                httpResponse response = new httpResponse();
                

                if (handle!=null){
                    try {

                String body = this.handle.handle(request, response);

                if (body!=null&&body.isBlank())
                { if (response.getHeaders().get("Contert-Type")==null) {
                
                    response.addHeader("Content-Type:", "text/html;charset = utf-8");
                    
                }
            response.setBody(body);
            } 
                
                
                } catch (Exception e){
                    e.printStackTrace();
                    response.setStatusCode(500);
                    response.setStatus("error");
                    response.addHeader("Content-Type:", "text/html;charset = utf-8");
                    response.setBody("Unlucky");
                }
            }
                
                else 
                {
                    response.setStatusCode(404);
                    response.setStatus("Not Found");
                    response.addHeader("Content-Type:", "text/html;charset = utf-8");
                    response.setBody("Resourse no found");
                }
                ByteBuffer resp = ByteBuffer.wrap(response.getByte());
                clientChannel.write(resp);
                clientChannel.close();

          }
        
        
    }
}
    