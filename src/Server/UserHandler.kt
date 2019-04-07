package Server

import Utils.Pacote

interface UserHandlerDelegate {
    fun saveCliente(cliente: Cliente)
    fun getRestaurantes(): String
    fun getProducts(restauranteIndex: Int) : String
    fun ClientCallsRestaurant(cliente: Cliente, rIndex: Int, pIndexValue: String)
}

class UserHandler: RequestHandler {

    lateinit var delegate: UserHandlerDelegate
    private val cliente: Cliente
    private var restauranteIndex = 0

    constructor(user: User) {
        cliente = Cliente(arrayListOf(), "", user)
    }

    override fun handle(pacote: Pacote): String {

        return when(cliente.user.state) {
            3 -> {
                "Informe seu nome: "
            }
            4 -> {
                cliente.name = pacote.data
                "Por favor digite o numero de um de nossos restaurantes ativos:" + "\n" + delegate.getRestaurantes()

            }
            5 -> {
                 restauranteIndex = pacote.data.toInt()-1
                "Agora, escolha por um produto:" + "\n" + delegate.getProducts(restauranteIndex)

            }
            6 -> {
                 delegate.ClientCallsRestaurant(cliente, restauranteIndex, pacote.data)
                "Logo logo seu pedido chegará, por favor aguarde :)" + "\n"
            }
            7 -> {
                "O entregador " + pacote.data + " acaba de pegar seu pedido, aguarde mais um pouco o.o"
            }
            8 -> {
                "O Entregador afirma ter entregado sua encomenda, confirme se seu produto chegou(y/n)"
            }
            9 -> {
                "Volte sempre!"
            }
            else -> "-1" + "\n"
        }
    }


}