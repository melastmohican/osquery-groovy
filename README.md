# osquery-groovy
## Overview
This project contains the Groovy bindings for creating osquery extensions in Groovy. The extension can register table, config or logger plugins.
Plugin can quickly enable the integration of data which is not yet available as a part of base osquery. 

## Prerequisites
Osquery must be installed on the computer you are running this software. Osquery should be run as the same user the user which runs the code shown here.

## How to
**Consider the following example:**

MyTablePlugin.groovy
```groovy
import net.melastmohican.osquery.TablePlugin
import net.melastmohican.osquery.TablePlugin.TableColumn

class MyTablePlugin extends TablePlugin {
	@Override
	List<TableColumn> columns() {
		[
			new TableColumn("foo", "TEXT"),
			new TableColumn("baz", "TEXT")
		]
	}

	@Override
	def name() {
		"mytable"
	}

	@Override
	def  generate() {
		[
			["foo" : "bar","baz": "baz"],
			["foo" : "bar","baz": "baz"]
		]
	}
}
```
Main.grrovy
```groovy
import net.melastmohican.osquery.BasePlugin
import net.melastmohican.osquery.PluginManager

BasePlugin plugin = new MyTablePlugin()
PluginManager pm = PluginManager.getInstance()
pm.addPlugin(plugin)
pm.startExtension("MyTablePlugin", "0.0.1", "2.2.1", "2.2.1")
```
**To test this code start an osquery shell:**
```
osqueryi --nodisable_extensions
osquery> select value from osquery_flags where name = 'extensions_socket';
```
|value
|---
|/Users/USERNAME/.osquery/shell.em

**Then start the Groovy extension:**
```
groovy Main.groovy -Dextension.socket=/Users/USERNAME/.osquery/shell.em MyTablePlugin
```
This will register a table called "mytable". As you can see, the table will
return two rows:
```
osquery> select * from mytable;
```
| foo | baz |
|---|---|
| bar | baz |
| bar | baz |
```
osquery>
```
## Execute queries in Groovy
The same Thrift bindings can be used to create a Groovy client for the osqueryd or
osqueryi's extension socket. 
```groovy
ClientManager cm =  new ClientManager()
cm.open()
ExtensionManager.Client client = cm.getClient()
println "select timestamp from time"
ExtensionResponse res = client.query("select timestamp from time")
println res.response
```
