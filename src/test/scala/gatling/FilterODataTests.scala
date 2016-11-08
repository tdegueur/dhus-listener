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

	/* 
	 TODO 
	 	- add feeders that provide filter arguments
	 	- use operator filter in scenario
	*/

	sealed class EntitySet protected (val entitySetName : String, val properties : FeederBuilder[_], val propertyCount : Int) {
		val filter = repeat(propertyCount) {
			exec(http("Service")
				.get("/"))
			.pause(1)
			.feed(properties)
			.exec(http(entitySetName)
				.get("/"+entitySetName+"?$filter=${property} Eq ${eqValue}"))
			.pause(1)
			.exec(http(entitySetName)
				.get("/"+entitySetName+"?$filter=${property} Ne ${neValue}"))
		} 
	}

	// fields in properties feeders: property, eqValue, neValue
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
	
	val filterConnections = scenario("Filter Connections").exec(Connections.filter)
	val filterNetworks = scenario("Filter Networks").exec(Networks.filter)
	val filterSystemRoles = scenario("Filter SystemRoles").exec(SystemRoles.filter)
	val filterProducts = scenario("Filter Products").exec(Products.filter)
	val filterAttributes = scenario("Filter Attributes").exec(Attributes.filter)
	val filterSynchronizers = scenario("Filter Synchronizers").exec(Synchronizers.filter)
	val filterUsers = scenario("Filter Users").exec(Users.filter)
	val filterNodes = scenario("Filter Nodes").exec(Nodes.filter)
	val filterNetworkStatistics = scenario("Filter NetworkStatistics").exec(NetworkStatistics.filter)
	val filterIngests = scenario("Filter Ingests").exec(Ingests.filter)
	val filterUserSynchronizers = scenario("Filter UserSynchronizers").exec(UserSynchronizers.filter)
	val filterCollections = scenario("Filter Collections").exec(Collections.filter)
	val filterRestrictions = scenario("Filter Restrictions").exec(Restrictions.filter)
	val filterClasses = scenario("Filter Classes").exec(Classes.filter)

	setUp(
    	filterConnections.inject(rampUsers(1) over (20 seconds)) // scenario will be executed i times over n seconds
    	filterNetworks.inject(rampUsers(1) over (20 seconds))
    	filterSystemRoles.inject(rampUsers(1) over (20 seconds))
    	filterProducts.inject(rampUsers(1) over (20 seconds))
    	filterAttributes.inject(rampUsers(1) over (20 seconds))
    	filterSynchronizers.inject(rampUsers(1) over (20 seconds))
    	filterUsers.inject(rampUsers(1) over (20 seconds))
    	filterNodes.inject(rampUsers(1) over (20 seconds))
    	filterNetworkStatistics.inject(rampUsers(1) over (20 seconds))
    	filterIngests.inject(rampUsers(1) over (20 seconds))
    	filterUserSynchronizers.inject(rampUsers(1) over (20 seconds))
    	filterCollections.inject(rampUsers(1) over (20 seconds))
    	filterRestrictions.inject(rampUsers(1) over (20 seconds))
    	filterClasses.inject(rampUsers(1) over (20 seconds))
  	).protocols(httpConf)
}