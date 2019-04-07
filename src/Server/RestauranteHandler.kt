package Server

import Utils.Pacote
import Utils.Produto

interface RestauranteHandlerDelegate {
    fun saveRestaurante(restaurante: Restaurante)
    fun restaurantCallsDeliver(restaurante: Restaurante)
    fun restaurantConfirmDeliverIsReady(dName: String)
}

class RestauranteHandler: RequestHandler {

    lateinit var delegate: RestauranteHandlerDelegate
    private val restaurante: Restaurante

    constructor(user: User) {
        restaurante = Restaurante(arrayListOf(), "", user)
    }

    override fun handle(pacote: Pacote): String {

        return when(restaurante.user.state) {
            3 -> {
                return "Informe o nome do seu restaurante!"
            }
            4 -> {
                if(restaurante.name.isEmpty() ) { restaurante.name = pacote.data; delegate.saveRestaurante(restaurante) }

                return "Por favor nos informe sobre os produtos que você gostaria de anunciar em nosso aplicativo" + "\n" +
                    "Para cada produto precisamos de: nome, preço, e quantidade" + "\n" + "digite o nome do Produto:" + "\n"

            }
            5 -> {
                restaurante.produtos!!.add(Produto(pacote.data))
                return "Preço:" + "\n"
            }
            6 -> {
                restaurante.produtos!!.last().preco = pacote.data
                return "Quantidade" + "\n"
            }
            7 -> {
                restaurante.produtos!!.last().qtd = pacote.data
                return "Deseja adicionar novos produtos?(y/n)" + "\n"
            }
            8 -> {
                if(pacote.data == "y") { restaurante.user.state = 3}
                restaurante.user.state++
                handle(pacote)
            }
            9 -> {
                return "Deseja começar a receber pedidos?(y/n)" + "\n"
            }
            10 -> {
                if(pacote.data == "y") { restaurante.user.state = 11}
                restaurante.user.state++
                handle(pacote)
            }
            11 ->  return "Volte sempre!" + "\n"
            12 -> {
                return "Você agora esta ativo para os nossos usuários, boas vendas!!"
            }
            13 -> {
                delegate.restaurantCallsDeliver(restaurante)
                return "Um pedido de " + restaurante.produtos!!.get(pacote.data.toInt()-1) + " acaba de ser realizado, o entregador, um entregador irá a seu encontro"
            }
            14 -> {
                delegate.restaurantConfirmDeliverIsReady(pacote.data)
                "O entregador " + pacote.data + " chegará em instantes. envie y assim que ele levar o pedido para que notifiquemos o cliente"
            }
            else -> "Nao entendi, poderia repetir por gentileza?" + "\n"
        }
    }
}