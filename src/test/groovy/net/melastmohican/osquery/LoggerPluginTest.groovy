package net.melastmohican.osquery

import org.apache.thrift.TException

import osquery.extensions.ExtensionResponse
import osquery.extensions.ExtensionStatus

class LoggerPluginTest extends GroovyTestCase {
	def PluginManager pm = PluginManager.getInstance()
    private SimpleLoggerPlugin plugin

    static class SimpleLoggerPlugin extends LoggerPlugin {
		def logs = []

        @Override 
        def name() {
            "simplelogger"
		}

        @Override 
        def logString(value) {
            logs << value
            return new ExtensionStatus(0, "OK", 0L)
		}
	}
	
    void setUp() {
        plugin = new SimpleLoggerPlugin()
	}

    void testAddPlugin() {
        pm.addPlugin(plugin)
        def registry = pm.registry()
		assert registry
        assert "logger" in  registry
        assert  registry["logger"].containsKey("simplelogger")
	}

    void testCallLogString() {
        def request = ["string" : "test"]
        ExtensionResponse result = plugin.call(request)
        assert "test" in plugin.logs
	}

    void testPluginManagerCallLogString() throws TException{
        def request = ["string": "test"]
        pm.addPlugin(plugin)
        def result = pm.call("logger", "simplelogger", request)
        assert "test" in plugin.logs
	}
}
