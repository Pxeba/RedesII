package Server

import Utils.Pacote

interface RequestHandler {

    fun handle(pacote: Pacote): String
}