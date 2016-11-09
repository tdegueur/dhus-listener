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
import org.apache.olingo.odata2.api.edm.EdmException;
import org.apache.olingo.odata2.api.ep.EntityProvider;
import org.apache.olingo.odata2.api.ep.EntityProviderException;

public class EntitySets {
   
   HttpURLConnection connection;
   
   private EntitySets(HttpURLConnection connection) {
      this.connection = connection;
   }
   
   public static EntitySets getInstance(String serviceUrl) throws IOException {
      String authStringEnc = new String(Base64.getEncoder().encode("root:rootpassword".getBytes()));
      
      URL url = new URL(serviceUrl);
      HttpURLConnection connection = (HttpURLConnection) url.openConnection();
      connection.setRequestMethod("GET");
      connection.setRequestProperty("Accept", "application/xml");
      connection.setRequestProperty("Authorization", "Basic " + authStringEnc);
      
      return new EntitySets(connection);
   }
   
   private Edm getEdm() throws EntityProviderException, IOException {
      InputStream content = connection.getInputStream();
      Edm edm = EntityProvider.readMetadata(content, false);
      return edm;
   }
   
   private List<String> getNames(Edm edm) throws EdmException {
      List<String> names = new ArrayList<String>();
      names.add("entitySets");
      for(EdmEntitySet entitySet : edm.getEntitySets()) {
         names.add(entitySet.getName());
      }
      
      return names;
   }
   
   private Map<String, List<String>> getProperties(Edm edm) throws EdmException {
      Map<String, List<String>> entitySetsProperties = new HashMap<String, List<String>>();
      for(EdmEntitySet entitySet : edm.getEntitySets()) {
         List<String> csvLine = new ArrayList<>();
         csvLine.add("property, eqValue, neValue");
         
         for(String propertyName : entitySet.getEntityType().getPropertyNames()) {
            csvLine.add(propertyName+", '', ''");
         }
         
         entitySetsProperties.put(
               entitySet.getName(),
               csvLine);
      }
      
      return entitySetsProperties;
   }
   
   public static void main(String[] args) throws Exception {
      EntitySets entitySets = EntitySets.getInstance("http://localhost:8081/odata/v1/$metadata");
      Edm edm = entitySets.getEdm();
      
      
      List<String> names = entitySets.getNames(edm);
      Path file = Paths.get("src/test/resources/gatling/entitysets.csv");
      Files.write(file, names, Charset.forName("UTF-8"));
      System.out.println("EntitySets done.");
      
      Map<String, List<String>> properties = entitySets.getProperties(edm);
      for(String entitySetName : properties.keySet()) {
         Path propertyFile = Paths.get("src/test/resources/gatling/"+entitySetName+"-properties.csv");
         Files.write(propertyFile, properties.get(entitySetName), Charset.forName("UTF-8"));
      }
      System.out.println("Properties done.");
   }
}
