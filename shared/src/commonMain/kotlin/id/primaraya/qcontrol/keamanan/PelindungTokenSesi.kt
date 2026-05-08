package id.primaraya.qcontrol.keamanan

expect object PelindungTokenSesi {
    fun lindungi(tokenAsli: String): String
    fun buka(tokenTerlindungi: String): String
}
