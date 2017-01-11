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
