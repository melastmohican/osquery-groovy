import net.melastmohican.osquery.BasePlugin
import net.melastmohican.osquery.PluginManager

BasePlugin plugin = new MyTablePlugin()
PluginManager pm = PluginManager.getInstance()
pm.addPlugin(plugin)
pm.startExtension("MyTablePlugin", "0.0.1", "2.2.1", "2.2.1")
