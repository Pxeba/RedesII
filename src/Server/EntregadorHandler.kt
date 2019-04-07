package Server

import Utils.Pacote

interface EntregadorHandlerDelegate {
    fun saveDeliver(entregador: Entregador)
    fun deliverCallsRestaurant(dName: String)
}

class EntregadorHandler: RequestHandler {

    lateinit var delegate: EntregadorHandlerDelegate
    private val entregador: Entregador

    constructor(user: User) {
        entregador = Entregador(arrayListOf(), "", user)
    }
    override fun handle(pacote: Pacote): String {

        return when(entregador.user.state) {
            3 -> "Olá, qual seu nome?"
            4 ->  { entregador.name = pacote.data; delegate.saveDeliver(entregador); "Ola, gostaria de dar inicio as entregas?(y/n)" + "\n" }
            5 -> "Fique em aguardo, logo logo algum pedido chegará !" + "\n"
            6 -> "Opa, um pedido acaba de chegar! você deve aceitá-lo(y/n)" + "\n"
            7 -> "Tudo certo, o tempo limite para entrega é de 30 minutos, boa sorte :)" + "\n"
            8 -> {
                delegate.deliverCallsRestaurant(entregador.name)
                "Você tem um novo pedido!! " + "\n diriga-se ao restaurante " + pacote.data
            }
            9 -> "O restaurante confirmou sua presença, agora diriga-se ao cliente " + pacote.data + " e assim que encontra-lo digite y para confirmar a entrega!"
            10 -> "Aguardando confirmação do cliente.."
            11 -> if(pacote.data == "y") { "Parabens, pedido finalizado, agradecemos pelo seu excelente trabalho" } else { "Cliente negou entrega" }
            else -> "-1" + "\n"
        }
    }
}