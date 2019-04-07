package Client

import Server.ClientType
import java.net.InetAddress
import java.net.Socket

fun main(args: Array<String>) {
   ClientController().startConnection()
}

class ClientController {

    companion object {
        private val socket = Socket(InetAddress.getByName("192.168.0.4"), 4321)
    }

    fun startConnection() {
        val clientType = requestUserType()
        ClientService(ClientController.socket, clientType).startConnection()
    }

    private fun requestUserType(): ClientType {
        println("Eu sou:")
        println("1-Cliente" + "\n" + "2-Restaurante" + "\n" + "3-Entregador")
        val cType = readLine()!!.toInt()
        while(cType !in 1..3) {
            val cType = readLine()!!.toInt()
        }
        return ClientType.values().find { it.value == cType }!!
    }
}