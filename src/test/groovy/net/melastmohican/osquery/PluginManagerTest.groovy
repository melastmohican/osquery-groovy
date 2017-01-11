package net.melastmohican.osquery

import net.melastmohican.osquery.BasePluginTest.SimplePlugin
import net.melastmohican.osquery.TablePluginTest.SimpleTablePlugin

import org.apache.thrift.TException

import osquery.extensions.ExtensionException
import osquery.extensions.ExtensionStatus

class PluginManagerTest extends GroovyTestCase {
	PluginManager pm = PluginManager.getInstance()

    void testAddPlugin() {
        BasePlugin plugin = new SimplePlugin("simple1")
        pm.addPlugin(plugin)
	}

    void testPing() throws TException {
        ExtensionStatus status = pm.ping()
        assert 0 == status.code
	}
    
    void testCall() throws TException{
        BasePlugin plugin = new SimplePlugin("simple2")
        pm.addPlugin(plugin)
        def resp = pm.call("config", "simple2", [:])
        assert "OK" == resp.status.message
	}

    def testStartExtension() throws ExtensionException {	
        BasePlugin plugin = new SimpleTablePlugin()
        pm.addPlugin(plugin)
        pm.startExtension("SimpleTable", "0.0.1", "2.2.1", "2.2.1")
	}
}
