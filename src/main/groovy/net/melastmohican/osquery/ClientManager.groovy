package net.melastmohican.osquery

import org.apache.thrift.protocol.TBinaryProtocol
import org.apache.thrift.protocol.TProtocol
import org.apache.thrift.transport.TIOStreamTransport
import org.apache.thrift.transport.TTransport
import org.apache.thrift.transport.TTransportException
import org.newsclub.net.unix.AFUNIXSocket
import org.newsclub.net.unix.AFUNIXSocketAddress

import osquery.extensions.ExtensionManager

final class ClientManager {
	static final String DEFAULT_SOCKET_PATH = "/var/osquery/osquery.em"
    static final String SHELL_SOCKET_PATH = System.getProperty("user.home") + "/.osquery/shell.em"

    private String socketPath = null
    private TTransport transport
    private TProtocol protocol
     
	def ClientManager() throws IOException {
        this(ClientManager.DEFAULT_SOCKET_PATH)
		
	}

   def ClientManager(String socketPath) throws IOException {
        this.socketPath = socketPath
        AFUNIXSocket socket = AFUNIXSocket.connectTo(new AFUNIXSocketAddress(new File(socketPath)))
        this.transport = new TIOStreamTransport(socket.getInputStream(), socket.getOutputStream())
        this.protocol = new TBinaryProtocol(transport)
	}

    def open() throws TTransportException{
        this.transport.open()
	}

    def close() {
        if (this.transport != null) {
            this.transport.close()
		}
	}
 
    ExtensionManager.Client getClient() throws IOException{
        return new ExtensionManager.Client(protocol)
	}
}
