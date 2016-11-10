import io.gatling.core.Predef._
import io.gatling.http.Predef._
import scala.concurrent.duration._
import java.util.concurrent.ThreadLocalRandom

class SimpleODataTests extends Simulation {

	// configuration
	// with dummy browser informations
	val httpConf = http
	    .baseURL("http://192.168.0.11:8081/odata/v1") // TODO get base url dynamically
	    .acceptHeader("text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
	    .doNotTrackHeader("1")
	    .acceptLanguageHeader("en-US,en;q=0.5")
	    .acceptEncodingHeader("gzip, deflate")
	    .userAgentHeader("Mozilla/5.0 (Macintosh; Intel Mac OS X 10.8; rv:16.0) Gecko/20100101 Firefox/16.0")
	    .basicAuth("root", "rootpassword") // create user root on centos1 dhus

	val sets = csv("entitysets.csv").queue // contains the names of entity sets

	object EntitySets {
		val browse = exec(http("Service")
				.get("/").check(status.is(200)))
			.pause(1)
			.feed(sets)
			.exec(http("Entity Set ${entitySetName}")
				.get("/${entitySetName}").check(status.is(200)))
	}

	val browseEntitySets = scenario("Browse Entity Sets").exec(EntitySets.browse)

	setUp(
    	browseEntitySets.inject(rampUsers(9) over (10 seconds)) // scenario will be executed i times over n seconds
  	).assertions(global.failedRequests.percent.is(0))
  	.protocols(httpConf)
}