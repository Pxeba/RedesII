package Utils

import java.io.Serializable

class Pacote: Serializable {
    var cToken: String?
    val data: String

    constructor(cToken: String? = null, cdata: String) {
        this.cToken = cToken
        data = cdata
    }
}