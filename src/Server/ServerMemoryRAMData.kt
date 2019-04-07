package Server

import java.io.Serializable
import java.net.ServerSocket

class Produto: Serializable {
    var name: String
    var preco: String? = null
    var qtd: String? = null

    constructor(pName: String) {
        name = pName
    }
}

open class User(val token: String, var state: Int, val cType: ClientType)

data class Restaurante(val produtos: MutableList<Produto>? = null, var name: String, val user: User)
data class Entregador(val entregas: MutableList<Entrega>? = null, var name: String, val user: User)
data class Cliente(val pedidos: MutableList<Entrega>? = null, var name: String, val user: User)
data class Entrega(val cliente: Cliente, val restaurante: Restaurante, var entregador: Entregador? = null)

enum class ClientType(val value: Int) {
    Cliente(1), Restaurante(2), Entregador(3)
}

data class ServerMemoryRAMData(val conSer: ServerSocket = ServerSocket(4321),
                               var restaurantes: MutableList<Restaurante> = arrayListOf(),
                               var entregas: MutableList<Entrega> = arrayListOf(),
                               var entregadores: MutableList<Entregador> = arrayListOf(),
                               var clientes: MutableList<Cliente> = arrayListOf(),
                               var users: MutableList<User>? = null,
                               var services: MutableList<ServerService> = arrayListOf())

