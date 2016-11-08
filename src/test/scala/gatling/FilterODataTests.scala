import io.gatling.core.Predef._
import io.gatling.core.feeder.FeederBuilder
import io.gatling.http.Predef._
import scala.concurrent.duration._
import java.util.concurrent.ThreadLocalRandom

class FilterODataTests extends Simulation {

	// configuration
	// with dummy browser informations
	val httpConf = http
	    .baseURL("http://localhost:8081/odata/v1") // TODO get base url dynamically
	    .acceptHeader("text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
	    .doNotTrackHeader("1")
	    .acceptLanguageHeader("en-US,en;q=0.5")
	    .acceptEncodingHeader("gzip, deflate")
	    .userAgentHeader("Mozilla/5.0 (Macintosh; Intel Mac OS X 10.8; rv:16.0) Gecko/20100101 Firefox/16.0")
	    .basicAuth("root", "rootpassword") // create user root on centos1 dhus

	val sets = csv("entitysets.csv").queue // fields: entitySetName
	val operators = csv("operators.csv").queue // fields: operator

	sealed class EntitySet protected (val entitySetName : String, val properties : FeederBuilder[_], val propertyCount : Int) {
		val filter = repeat(propertyCount) {
			exec(http("Service")
				.get("/"))
			.pause(1)
			.feed(properties)
			.exec(http(entitySetName)
				.get("/"+entitySetName+"?$filter=${property} eq ''"))
		} 
	}

	object Connections extends EntitySet("Connections", csv("Connections-properties.csv").queue, 9)
	object Networks extends EntitySet("Networks", csv("Networks-properties.csv").queue, 1)
	object SystemRoles extends EntitySet("SystemRoles", csv("SystemRoles-properties.csv").queue, 2)
	object Products extends EntitySet("Products", csv("Products-properties.csv").queue, 14)
	object Attributes extends EntitySet("Attributes", csv("Attributes-properties.csv").queue, 6)
	object Synchronizers extends EntitySet("Synchronizers", csv("Synchronizers-properties.csv").queue, 18)
	object Users extends EntitySet("Users", csv("Users-properties.csv").queue, 14)
	object Nodes extends EntitySet("Nodes", csv("Nodes-properties.csv").queue, 6)
	object NetworkStatistics extends EntitySet("NetworkStatistics", csv("NetworkStatistics-properties.csv").queue, 3)
	object Ingests extends EntitySet("Ingests", csv("Ingests-properties.csv").queue, 6)
	object UserSynchronizers extends EntitySet("UserSynchronizers", csv("UserSynchronizers-properties.csv").queue, 15)
	object Collections extends EntitySet("Collections", csv("Collections-properties.csv").queue, 2)
	object Restrictions extends EntitySet("Restrictions", csv("Restrictions-properties.csv").queue, 3)
	object Classes extends EntitySet("Classes", csv("Classes-properties.csv").queue, 2)
	
	val browseEntitySets = scenario("Filter Entity Sets").exec(Products.filter)

	setUp(
    	browseEntitySets.inject(rampUsers(9) over (10 seconds)) // scenario will be executed i times over n seconds
  	).protocols(httpConf)
}