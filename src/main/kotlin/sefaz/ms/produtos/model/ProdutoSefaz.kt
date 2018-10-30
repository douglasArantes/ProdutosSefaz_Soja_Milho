package sefaz.ms.produtos.model

import java.time.LocalDateTime

data class ProdutoSefaz(
        val id: Int,
        var codProdSefaz: Int,
        val descrProd: String,
        val unid: String,
        var fatorConv: Int,
        val dtInsercao: LocalDateTime

)