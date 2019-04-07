package Server

import Utils.Pacote
import Utils.SocketService
import java.io.ObjectInputStream
import java.net.Socket
import kotlin.streams.asSequence

class ServerService: SocketService, RestauranteHandlerDelegate, EntregadorHandlerDelegate, UserHandlerDelegate {

    private val ram: ServerMemoryRAMData
    private val socketCli: Socket
    private lateinit var user: User

    lateinit var userHandler: UserHandler
    lateinit var restauranteHandler: RestauranteHandler
    lateinit var entregadorHandler: EntregadorHandler


    constructor(socket: Socket, ram: ServerMemoryRAMData) : super(socket) {
        this.ram = ram
        this.socketCli = socket
    }


    override fun run() {
        prepare()
        waitMsg()
    }

    private fun prepare() {
        val receivedRequest = ObjectInputStream(socketCli.getInputStream())
        val pacote = receivedRequest.readObject() as Pacote
        val clientType = ClientType.values().find { it.value == pacote.data.toInt() }!!

        if(ram.users == null) { ram.users = arrayListOf() }
        user = User(generateToken(), 0, clientType)
        ram.users!!.add(user)

        when(user.cType) {
            ClientType.Cliente -> { userHandler = UserHandler(user); userHandler.delegate = this }
            ClientType.Restaurante -> { restauranteHandler = RestauranteHandler(user); restauranteHandler.delegate = this }
            ClientType.Entregador ->{ entregadorHandler = EntregadorHandler(user); entregadorHandler.delegate = this }
        }

        onMsgReceived(pacote)
    }

    override fun onMsgReceived(pacote: Pacote) {
        if(user.state in 0..2) { handleLogin(pacote, user)}
        else { handleUserMsg(pacote) }
    }

    private fun handleLogin(pacote: Pacote, user: User) {
        when(user.state) {
            0 -> { sendMsg(Pacote(user.token, "Escolha uma opção: 1- Login/ 2- Cadastrar")); user.state++ }
            1 -> { sendMsg(Pacote(user.token, "Escreva login e senha juntos no formato 'login/senha'")); user.state++ }
            2 -> {
                if(ram.users!!.map { it.token == pacote.data }.isEmpty()) {
                    sendMsg(Pacote(user.token, "Novo usuário registradoedc"))
                }
                else {
                    sendMsg(Pacote(user.token, "Login autenticado com sucesso"))
                }
                user.state++
            }
        }
    }

    private fun generateToken(): String {
        return java.util.Random().ints(32, 0, srcToken.length)
                .asSequence()
                .map(srcToken::get)
                .joinToString("")
    }

    fun handleUserMsg(pacote: Pacote) {
        val handledMsg = when ( user.cType ) {
            ClientType.Cliente -> userHandler.handle(pacote)
            ClientType.Entregador -> entregadorHandler.handle(pacote)
            ClientType.Restaurante -> restauranteHandler.handle(pacote)
        }

        user.state++
        sendMsg(Pacote(user.token, handledMsg))
    }


    override fun saveRestaurante(restaurante: Restaurante) {
        ram.restaurantes.add(restaurante)
    }

    override fun restaurantCallsDeliver(restaurante: Restaurante) {
        val entregador = ram.entregadores.first()
        ram.entregas.first { it.restaurante.user.token == restaurante.user.token }.entregador = entregador
        ram.services.first { it.user.token == entregador.user.token }.handleUserMsg(Pacote(null, restaurante.name))

    }

    override fun restaurantConfirmDeliverIsReady(dName: String) {
        val entregaAtual = ram.entregas.first { it.entregador!!.user.token == user.token }
        val entregador = entregaAtual.entregador
        val cliente = entregaAtual.cliente

        ram.services.first { it.user.token == cliente.user.token }.sendMsg(Pacote(null, dName))
        ram.services.first { it.user.token == entregador!!.user.token }.handleUserMsg(Pacote(null, cliente.name))
    }

    //Mark: UserHandlerDelegate Methods

    override fun saveCliente(cliente: Cliente) {
        ram.clientes.add(cliente)
    }

    override fun getRestaurantes(): String {
        var restaurantesString = ""
        for(i in 0..ram.restaurantes.size-1) { restaurantesString = restaurantesString + (i+1) + " " + ram.restaurantes.get(i).name + "\n"}
        return restaurantesString
    }

    override fun getProducts(restauranteIndex: Int): String {
        val restaurante = ram.restaurantes.getOrNull(restauranteIndex)
        restaurante.let {
            var produtosString = ""
            for(i in 0..restaurante!!.produtos!!.size-1) { produtosString = produtosString + (i+1) + " " + restaurante.produtos!!.get(i).name + "\n" }
            return produtosString
        }

        return "restaurante not found error"
    }

    override fun ClientCallsRestaurant(cliente: Cliente, rIndex: Int, pIndexValue : String) {
        val restaurante = ram.restaurantes.get(rIndex)
        ram.entregas.add(ram.entregas.size, Entrega(cliente, restaurante, null))
        ram.services.first { it.user.token == restaurante.user.token }.handleUserMsg(Pacote(null, pIndexValue))
    }

    //Mark: EntregadorHandlerDelegate Methods

    override fun saveDeliver(entregador: Entregador) {
        ram.entregadores.add(entregador)
    }

    override fun deliverCallsRestaurant(dName: String) {
        val restaurante = ram.entregas.first { it.entregador!!.user.token == user.token }.restaurante
        ram.services.first { it.user.token == restaurante.user.token }.handleUserMsg(Pacote(null, dName))
    }
}