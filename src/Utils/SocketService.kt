package Utils

import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.net.ServerSocket
import java.net.Socket
import java.io.IOException
import kotlin.reflect.jvm.internal.impl.descriptors.ModuleDescriptor.DefaultImpls.accept



abstract  class SocketService: Thread {

    private var socketCli: Socket = Socket()
    private val stepInputStream: Boolean = false

    constructor(csocket: Socket) {
        socketCli = csocket
    }

    open fun sendMsg(msg: Pacote) {
        val sCliOut = ObjectOutputStream(socketCli.getOutputStream())
        sCliOut.writeObject(msg)
        sCliOut.flush()
    }

    fun waitMsg() {
        while (true) {
            if(stepInputStream) {

            }
            val receivedRequest = ObjectInputStream(socketCli.getInputStream())
            val receivedObject = receivedRequest.readObject() as Pacote
            onMsgReceived(receivedObject)
            print(receivedObject.toString())
        }
    }

    abstract fun onMsgReceived(pacote: Pacote)
}