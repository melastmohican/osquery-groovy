package net.melastmohican.osquery

import java.util.AbstractMap.SimpleEntry

import osquery.extensions.ExtensionResponse
import osquery.extensions.ExtensionStatus

abstract class TablePlugin extends BasePlugin {
	public static class TableColumn extends SimpleEntry<String, String> {
		public TableColumn(String key, String value) {
			super(key, value)
		}
	}

	@Override 
    def registryName() {
        "table"
	}

    @Override 
    def ExtensionResponse call(request) {
        if (!("action" in request)) {
            return new ExtensionResponse(new ExtensionStatus(1, "Table plugins must include a request action", 0L), [])
		}

        if (request["action"] == "generate") {
            return new ExtensionResponse(new ExtensionStatus(0, "OK", 0L), generate())
		} else if (request["action"] == "columns") {
            return new ExtensionResponse(new ExtensionStatus(0, "OK", 0L), routes())
		}
        return new ExtensionResponse(new ExtensionStatus(1, "Table plugin request action undefined", 0L), [])
	}

    @Override 
    def routes() {
        def routes = []
		columns().each { column ->
			def route = [id : "column", name : column.key, type : column.value, op : "0"]
			routes << route
		}
	   routes
	}

    abstract List<TableColumn> columns() 
    abstract def generate() 
}
