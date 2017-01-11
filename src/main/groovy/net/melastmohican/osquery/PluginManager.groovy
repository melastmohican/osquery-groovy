package net.melastmohican.osquery

import org.apache.thrift.TException
import org.apache.thrift.protocol.TBinaryProtocol
import org.apache.thrift.protocol.TProtocolFactory
import org.apache.thrift.server.TServer
import org.apache.thrift.server.TSimpleServer
import org.apache.thrift.server.TServer.Args
import org.apache.thrift.transport.TServerSocket
import org.apache.thrift.transport.TTransportFactory
import org.newsclub.net.unix.AFUNIXServerSocket
import org.newsclub.net.unix.AFUNIXSocketAddress

import osquery.extensions.ExtensionException
import osquery.extensions.ExtensionManager
import osquery.extensions.ExtensionResponse
import osquery.extensions.ExtensionStatus
import osquery.extensions.InternalExtensionInfo
import osquery.extensions.Extension.Iface
import osquery.extensions.Extension.Processor


final class PluginManager implements Iface {
	static final String EXTENSION_SOCKET = System.properties["extension.socket"] != null ? System.properties["extension.socket"] : ClientManager.DEFAULT_SOCKET_PATH
    final List<String> registryTypes = Arrays.asList("config", "logger", "table")

    private plugins = [:]
    private registry = [:]
    private Long uuid = null

    private def PluginManager() {
		println EXTENSION_SOCKET
	}
    
	private static class SingletonHolder {
		private static final PluginManager INSTANCE = new PluginManager()
	}

	static PluginManager getInstance() {
        return SingletonHolder.INSTANCE
	}

    ExtensionStatus ping() throws TException{
        ExtensionStatus status = new ExtensionStatus(0, "OK", uuid != null?uuid.longValue():0L)
        return status
	}


    ExtensionResponse call(String registry, String item, Map<String, String> request) throws TException {
        if (!( registry in registryTypes)) {
            return new ExtensionResponse(new ExtensionStatus(1, "A registry of an unknown type was called: " + registry, uuid), [])
		}
        return plugins[registry][item].call(request)
	}

    def addPlugin(BasePlugin plugin) {
        if (!(plugin.registryName() in registry)) {
            //registry << [(plugin.registryName()) : [:]]
			registry[plugin.registryName()] =  [:]
		}
	    if (!(plugin.name() in registry[plugin.registryName()])) {
		    //registry[plugin.registryName()] << [(plugin.name()) : []]
	        registry[plugin.registryName()][plugin.name()] = plugin.routes()
		}
		
	    if (!(plugin.name() in plugins)){
            plugins[plugin.registryName()] = [:]
		}
		
		if (!(plugin.name() in plugins[plugin.registryName()])) {
            plugins[plugin.registryName()][plugin.name()] = plugin
		}
	}

    def shutdown() {
        System.exit(0)
	}

    def registry() {
        this.registry
	}

    def startExtension(name, version, sdkVersion, minSdkVersion) throws IOException {
        ExtensionManager.Client client = new ClientManager(EXTENSION_SOCKET).getClient()
        InternalExtensionInfo info = new InternalExtensionInfo(name, version, sdkVersion, minSdkVersion)
        try {
            ExtensionStatus status = client.registerExtension(info, registry)
            if (status.getCode() == 0) {
                this.uuid = status.uuid
                Processor<PluginManager> processor = new Processor<PluginManager>(this)
                String serverSocketPath = EXTENSION_SOCKET + "." + String.valueOf(uuid)
                File socketFile = new File(serverSocketPath)
                if (socketFile.exists()) {
                    socketFile.delete()
				}

                AFUNIXServerSocket socket = AFUNIXServerSocket.bindOn(new AFUNIXSocketAddress(socketFile))
                socketFile.setExecutable(true, false)
                socketFile.setWritable(true, false)
                socketFile.setReadable(true, false)
                TServerSocket transport = new TServerSocket(socket)
                TTransportFactory transportFactory = new TTransportFactory()
                TProtocolFactory protocolFactory = new TBinaryProtocol.Factory()
                TServer server = new TSimpleServer(new Args(transport).processor(processor).transportFactory(transportFactory).protocolFactory(protocolFactory))

                System.out.println("Starting the server...")
				server.serve()
			} else {
                throw new ExtensionException(1, status.getMessage(), uuid)
			}
		} catch (TException e) {
            throw new ExtensionException(1, "Could not connect to socket", uuid)
		}
	}

    def deregisterExtension() throws IOException{
        ExtensionManager.Client client = new ClientManager().getClient()
        if (uuid == null) {
            throw new ExtensionException(1, "Extension Manager does not have a valid UUID", uuid)
		}

        try {
            ExtensionStatus status = client.deregisterExtension(uuid)
            if (status.getCode() != 0) {
                throw new ExtensionException(1, status.getMessage(), uuid)
			}
		} catch (TException e) {
            throw new ExtensionException(1, "Could not connect to socket", uuid)
		}
	}
}
