package Client

import Server.ClientType
import Utils.Pacote
import Utils.SocketService
import java.net.Socket

class ClientService: SocketService {
    val clientType: ClientType

    constructor(csocket: Socket, ctype: ClientType): super(csocket) {
        clientType = ctype
    }

    fun startConnection() {
        sendMsg(Pacote(null, clientType.value.toString()))
    }

    fun requestClientInput(pacote: Pacote) {
        println(pacote.data)
        val userResponse: String
        if(pacote.data.isEmpty()) { userResponse = "" } else { userResponse = readLine()!! }

        sendMsg(Pacote(pacote.cToken, userResponse))
    }

    override fun onMsgReceived(pacote: Pacote) {
        requestClientInput(pacote)
    }

    override fun sendMsg(pacote: Pacote) {
        super.sendMsg(pacote)
        waitMsg()
    }
}