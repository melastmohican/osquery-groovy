package net.melastmohican.osquery

import osquery.extensions.ExtensionResponse
import osquery.extensions.ExtensionStatus

abstract class ConfigPlugin extends BasePlugin {
	@Override 
    def registryName() {
        return "config"
	}
    @Override 
   	def call(request) {
        if (!("action" in request)) {
            return new ExtensionResponse(new ExtensionStatus(1, "Table plugins must include a request action", 0L), [])
		}
        
        if (request["action"] == "genConfig") {
            return new ExtensionResponse(new ExtensionStatus(0, "OK", 0L), content())
		}

        return new ExtensionResponse(new ExtensionStatus(1, "Config plugin request action undefined", 0L), [])
	}

    @Override 
    def routes() {
        []
	}
    
	abstract def content() 
}
