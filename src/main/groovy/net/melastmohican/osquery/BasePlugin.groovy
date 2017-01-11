package net.melastmohican.osquery

abstract class BasePlugin {
	abstract def name() 
    abstract def registryName() 
    abstract def call(request) 
    
	def routes() {
		[]
	}
}
