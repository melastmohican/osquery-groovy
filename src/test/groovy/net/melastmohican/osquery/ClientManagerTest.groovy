package net.melastmohican.osquery

import org.apache.thrift.TException

import osquery.extensions.ExtensionManager
import osquery.extensions.ExtensionResponse

class ClientManagerTest extends GroovyTestCase {
	private ProcessBuilder osqueryctl = new ProcessBuilder("/usr/local/bin/osqueryctl", "start")

    void setUp() {
        System.out.println("Starting service...")
        //osqueryctl.start()
	}
	void testDummy() {
		
	}
	
	def testGetClient() throws TException{
        ClientManager cm = new ClientManager("/Users/jurgielm/.osquery/shell.em")
        cm.open()
        ExtensionManager.Client client = cm.getClient()
        println "select timestamp from time"
        ExtensionResponse res = client.query("select timestamp from time")
        println res.response
	}
}
