package net.melastmohican.osquery

import org.apache.thrift.TException

import osquery.extensions.ExtensionResponse

class ConfigPluginTest extends GroovyTestCase {
	PluginManager pm = PluginManager.getInstance()
    private BasePlugin plugin

    static class SimpleConfigPlugin extends ConfigPlugin {
		@Override 
        def name() {
            "simpleconfig"
		}

        @Override 
        def content() {
            def result = [["source1" : "{\n\t\"schedule\": {\n\t\t\"time_1\": {\n\t\t\t\"query\": \"select * from time\",\n\t\t\t\"interval\": 1,\n\t\t},\n\t},\n}",
                        "source2" : "{\n\t\"schedule\": {\n\t\t\"time_2\": {\n\t\t\t\"query\": \"select * from time\",\n\t\t\t\"interval\": 2,\n\t\t},\n\t},\n}"]]
	         result
		}
	}

    void setUp() {
        plugin = new SimpleConfigPlugin()
	}

    void testAddPlugin() {
        pm.addPlugin(plugin)
        def registry = pm.registry()
		assert registry
        assert "config" in registry
        assert registry["config"].containsKey('simpleconfig')
	}

    void testCallActionGenConfig() {
        def expected = [["source1" : "{\n\t\"schedule\": {\n\t\t\"time_1\": {\n\t\t\t\"query\": \"select * from time\",\n\t\t\t\"interval\": 1,\n\t\t},\n\t},\n}",
                        "source2" : "{\n\t\"schedule\": {\n\t\t\"time_2\": {\n\t\t\t\"query\": \"select * from time\",\n\t\t\t\"interval\": 2,\n\t\t},\n\t},\n}"]]

        def request = [action : "genConfig"]

        ExtensionResponse result = plugin.call(request)
        assert expected == result.response
	}


    void testPluginManagerCallActionGenConfig() throws TException {
        def expected = [["source1" : "{\n\t\"schedule\": {\n\t\t\"time_1\": {\n\t\t\t\"query\": \"select * from time\",\n\t\t\t\"interval\": 1,\n\t\t},\n\t},\n}",
                        "source2" : "{\n\t\"schedule\": {\n\t\t\"time_2\": {\n\t\t\t\"query\": \"select * from time\",\n\t\t\t\"interval\": 2,\n\t\t},\n\t},\n}"]]

        def request = [action : "genConfig"]

        pm.addPlugin(plugin)
        ExtensionResponse result = pm.call("config", "simpleconfig", request)
        assert expected == result.response
	}
}
