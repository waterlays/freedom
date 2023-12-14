package freedom;


import java.util.concurrent.ExecutionException;


/**
 *
 * @author Kapella
 */
public class App 
{

    public static void main(String[] args) throws InterruptedException, ExecutionException
     {
        new Server(( req,  resp) -> 
        {
            return ("123");

        }).bootstrap();
    }

}


    
    