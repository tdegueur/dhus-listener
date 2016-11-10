package dhus.listener;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.olingo.odata2.api.edm.Edm;
import org.apache.olingo.odata2.api.edm.EdmEntitySet;
import org.apache.olingo.odata2.api.edm.EdmEntitySetInfo;
import org.apache.olingo.odata2.api.edm.EdmException;
import org.apache.olingo.odata2.api.ep.EntityProvider;
import org.apache.olingo.odata2.api.ep.EntityProviderException;
import org.apache.olingo.odata2.api.servicedocument.ServiceDocument;

public class EntitySets {
   
   // TODO make login and password a parameter
   private static HttpURLConnection getConnection(String serviceUrl) throws IOException {
      String authStringEnc = new String(Base64.getEncoder().encode("root:rootpassword".getBytes()));
      
      URL url = new URL(serviceUrl);
      HttpURLConnection connection = (HttpURLConnection) url.openConnection();
      connection.setRequestMethod("GET");
      connection.setRequestProperty("Accept", "application/xml");
      connection.setRequestProperty("Authorization", "Basic " + authStringEnc);
      
      return connection;
   }
   
   public static Edm getEdm(String url) throws EntityProviderException, IOException {
      HttpURLConnection connection = getConnection(url);
      InputStream content = connection.getInputStream();
      return EntityProvider.readMetadata(content, false);
   }
   
   public static ServiceDocument getServiceDocument(String url) throws IOException, EntityProviderException {
      HttpURLConnection connection = getConnection(url);
      InputStream content = connection.getInputStream();
      return EntityProvider.readServiceDocument(content, "application/xml");
   }
   
   public static List<String> getNames(ServiceDocument serviceDocument) throws EntityProviderException {
      List<String> names = new ArrayList<String>();
      names.add("entitySetName");
      for(EdmEntitySetInfo entitySetInfo : serviceDocument.getEntitySetsInfo()) {
         names.add(entitySetInfo.getEntitySetName());
      }
      return names;
   }
   
   public static Map<String, List<String>> getProperties(ServiceDocument serviceDocument, Edm edm) throws EntityProviderException, EdmException {
      List<String> names = getNames(serviceDocument);
      Map<String, List<String>> entitySetsProperties = new HashMap<String, List<String>>();
      for(EdmEntitySet entitySet : edm.getEntitySets()) {
         if(names.contains(entitySet.getName())) {
            List<String> csvLine = new ArrayList<>();
            csvLine.add("property, eqValue, neValue");
            
            for(String propertyName : entitySet.getEntityType().getPropertyNames()) {
               csvLine.add(propertyName+", '', ''");
            }
            
            entitySetsProperties.put(
                  entitySet.getName(),
                  csvLine);
         }
      }
      
      return entitySetsProperties;
   }
   
   public static void main(String[] args) throws Exception {
      // handle service url (first argument)
      String serviceUrl;
      if(args.length >= 1) {
         serviceUrl = args[0];
      } else {
         serviceUrl = "http://localhost:8081/odata/v1/";
      }
      
      // handle output path (second argument)
      String outputPath;
      if(args.length >= 2) {
         outputPath = args[1];
      } else {
         outputPath = "src/test/resources/gatling/";
      }
      
      ServiceDocument serviceDocument = EntitySets.getServiceDocument(serviceUrl);
      Edm edm = EntitySets.getEdm(serviceUrl+"$metadata");
      
      List<String> names = EntitySets.getNames(serviceDocument);
      Path file = Paths.get(outputPath+"entitysets.csv");
      Files.write(file, names, Charset.forName("UTF-8"));
      System.out.println("EntitySets done.");
      
      Map<String, List<String>> properties = EntitySets.getProperties(serviceDocument, edm);
      for(String entitySetName : properties.keySet()) {
         Path propertyFile = Paths.get(outputPath+entitySetName+"-properties.csv");
         Files.write(propertyFile, properties.get(entitySetName), Charset.forName("UTF-8"));
      }
      System.out.println("Properties done.");
   }
}
