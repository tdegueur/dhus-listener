package dhus.listener;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

// ssh dhus@91.134.230.97 'java -jar ~/dhus/dhus-listener.jar 3'
public class DhusListener {
   private static void listenToFile(String fileName)
         throws IOException, InterruptedException {
      while(true) {
         Path path = Paths.get(fileName);
         if(Files.exists(path)) {
            BufferedReader reader = 
                  Files.newBufferedReader(Paths.get(fileName), Charset.forName("UTF-8"));
            String line = reader.readLine();
            if(line != null) {
               if(line.contains("Server is ready...")) {
                  // dhus is ready
                  System.out.println("DHuS is ready!");
                  reader.close();
                  System.exit(0); // normal status returned (0)
               }
            } else {
               // end of logs reached, close the reader
               System.out.println("End of logs, resetting...");
               reader.close();
               Thread.sleep(1000);
            }
         } else {
            // log file does not exist yet
            System.out.println("Logs not found, checking again...");
            Thread.sleep(1000);
         }
      }
   }
   
   public static void main(String[] args) 
         throws IOException, InterruptedException, URISyntaxException {
      int delay = 300000;
      if(args.length > 0) {
         delay = Integer.parseInt(args[0])*1000;
      }
      
      final String fileName;
      if(args.length > 1) {
         fileName = args[1];
      } else {
         fileName = "dhus.log";
      }
      
      Thread dhusListener = new Thread() {
         @Override
         public void run() {
            try {
               listenToFile(fileName);
            } catch (IOException e) {
               throw new RuntimeException(e); 
            } catch (InterruptedException e) {
               // silently die
            }
         }
      };
      
      dhusListener.start();      // start listening to dhus logs
      Thread.sleep(delay);
      dhusListener.interrupt();  // dhus took too long to start, abort
      System.out.println("DHuS Timeout.");
      System.exit(1);            // abnormal status returned (1)
   }
}
