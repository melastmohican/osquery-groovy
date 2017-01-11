package net.melastmohican.osquery

import java.util.List

import net.melastmohican.osquery.TablePlugin.TableColumn

import org.apache.thrift.TException

import osquery.extensions.ExtensionResponse

class TablePluginTest extends GroovyTestCase {
	def PluginManager pm = PluginManager.getInstance()
    private BasePlugin plugin

    static class SimpleTablePlugin extends TablePlugin {
		@Override 
        List<TableColumn> columns() {
			def col1 = new TableColumn("foo", "TEXT")
			def col2 = new TableColumn("baz", "TEXT")
            return [col1, col2]
		}

        @Override 
        def name() {
            "simpletable"
		}

        @Override 
        def generate() {
            def result = [["foo" : "bar","baz": "baz"],["foo" : "bar","baz": "baz"]]
            return result
		}
	}

	void setUp() {
        plugin = new SimpleTablePlugin()
	}

    void testRoutes() {
        def expected = [ ["id": "column", "name" : "foo", "type" : "TEXT", "op" : "0"], ["id": "column", "name" : "baz", "type" : "TEXT", "op" : "0"]]
		println expected
	    def routes = plugin.routes()
		println routes
        assert expected == routes
	}

    void testAddPlugin() {
        pm.addPlugin(plugin)
        def registry = pm.registry()
		assert registry
        assert "table" in registry
        assert registry["table"].containsKey( "simpletable")
	}

    void testCallActionGenerate() {
		def expected = [["foo": "bar", "baz": "baz"], ["foo": "bar", "baz": "baz"]]
		println expected
		def request = ["action" : "generate"]
        ExtensionResponse result = plugin.call(request)
		println result
        assert expected == result.response
	}
    
    void testCallActionColums() {
        def expected =  [ ["id": "column", "name" : "foo", "type" : "TEXT", "op" : "0"], ["id": "column", "name" : "baz", "type" : "TEXT", "op" : "0"]]
		println expected
        def request = ["action": "columns"]
        ExtensionResponse result = plugin.call(request)
		println result
        assert expected == result.response
	}

    void testPluginManagerCallActionGenerate() throws TException {
        def expected = [["foo": "bar", "baz": "baz"], ["foo": "bar", "baz": "baz"]]
		println expected
        def request = ["action": "generate"]
        pm.addPlugin(plugin)
        def result = pm.call("table", "simpletable", request)
		println result
        assert expected == result.response
	}

    void testPluginManagerCallActionColums() throws TException{
        def expected = [ ["id": "column", "name" : "foo", "type" : "TEXT", "op" : "0"], ["id": "column", "name" : "baz", "type" : "TEXT", "op" : "0"]]
		println expected
        def request = ["action" : "columns"]
        pm.addPlugin(plugin)
        ExtensionResponse result = pm.call("table", "simpletable", request)
		println result
        assert expected == result.response
	}
}
