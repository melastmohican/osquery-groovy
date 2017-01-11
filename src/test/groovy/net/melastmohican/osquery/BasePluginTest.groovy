package net.melastmohican.osquery

import osquery.extensions.ExtensionResponse
import osquery.extensions.ExtensionStatus

class BasePluginTest extends GroovyTestCase {
	private BasePlugin plugin

    static class SimplePlugin extends BasePlugin {
		private final String name

        def SimplePlugin() {
            this.name = "pass"
		}

        def SimplePlugin(String name) {
            this.name = name
		}

        @Override 
        def name() {
            this.name
		}

        @Override 
        def registryName() {
            "config"
		}

        @Override 
        def call(request) {
            new ExtensionResponse(new ExtensionStatus(0, "OK", 0), routes())
		}

        @Override 
        def routes() {
            []
		}
	}

	void setUp() {
        plugin = new SimplePlugin()
		return
	}

    void testRoutes() {
        def routes = plugin.routes()
        assert 0 == routes.size()
	}

    void testCall() {
        ExtensionResponse resp = plugin.call([])
        assert "OK" == resp.status.message
	}
}
