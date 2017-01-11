package net.melastmohican.osquery

import osquery.extensions.ExtensionResponse
import osquery.extensions.ExtensionStatus

abstract class LoggerPlugin extends BasePlugin {
	
	@Override 
    def registryName() {
       "logger"
	}

    @Override 
    def call(request) {
        if ("string" in request) {
            return new ExtensionResponse(logString(request["string"]), [])
		} else if ("snapshot" in request) {
            return new ExtensionResponse(logSnapshot(request["snapshot"]), [])
		} else if ("health" in request) {
            return new ExtensionResponse(logHealth(request["health"]), [])
		} else if ("init" in request) {
            return new ExtensionResponse(new ExtensionStatus(1, "Use Glog for status logging", 0L), [])
		} else if ("status" in request) {
            return new ExtensionResponse(new ExtensionStatus(1, "Use Glog for status logging", 0L), [])
		}
        return new ExtensionResponse(new ExtensionStatus(1, "Logger plugin request action undefined", 0L), [])
	}

    @Override 
    def routes() {
    	[]
	}
    
	abstract def logString(value) 

    def logHealth(value) {
        return new ExtensionStatus(0, "OK", 0L)
	}

    def logSnapshot(value) {
        return this.logString(value)
	}
}
