package Server

fun main(args: Array<String>) {
    MultThreadServerService().listenConnections()
}


const val FIRST_STATE = 2 // after login authentication
val srcToken = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"

class MultThreadServerService {

    fun listenConnections() {
        val ram = ServerMemoryRAMData()
        while(true) {
            val socket = ram.conSer.accept()
            ram.services.add(ServerService(socket, ram))
            ram.services.last().start()
        }
    }
}




