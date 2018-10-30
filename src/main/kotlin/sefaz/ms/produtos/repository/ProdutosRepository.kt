package sefaz.ms.produtos.repository

import sefaz.ms.produtos.model.ProdutoSefaz
import sefaz.ms.produtos.repository.jdbc.DatabaseConnection

object ProdutosRepository {

    fun fatoresConv(): List<Int> {

        val fatoresConv = ArrayList<Int>()

        val connection = DatabaseConnection().connection

        connection.use {
            val stmt = connection?.createStatement()

            val resultSet = stmt?.executeQuery("""SELECT DISTINCT (FATOR_CONV)
                FROM MINING_SEFAZ.SRJUNIOR.UPLAN_UFAPEC_SOJAMILHO_PRODUTOS_NOVOS ORDER BY FATOR_CONV""")

            while (resultSet?.next()!!) {
                val fatorConv = resultSet.getInt("FATOR_CONV")

                fatoresConv += fatorConv
            }
        }
        return fatoresConv
    }

    fun codigosProdutos(): List<Int> {

        val codigos = ArrayList<Int>()

        val connection = DatabaseConnection().connection

        connection.use {
            val stmt = connection?.createStatement()

            val resultSet = stmt?.executeQuery("""SELECT DISTINCT (COD_PROD_SEFAZ)
                FROM MINING_SEFAZ.SRJUNIOR.UPLAN_UFAPEC_SOJAMILHO_PRODUTOS_NOVOS ORDER BY COD_PROD_SEFAZ""")

            while (resultSet?.next()!!) {
                val codigo = resultSet.getInt("COD_PROD_SEFAZ")

                codigos += codigo
            }
        }
        return codigos
    }

    fun produtos(): List<ProdutoSefaz> {

        val produtosSefaz = ArrayList<ProdutoSefaz>()

        val connection = DatabaseConnection().connection

        connection.use {

            val stmt = connection?.createStatement()

            val resultSet = stmt?.executeQuery("SELECT * FROM MINING_SEFAZ.SRJUNIOR.UPLAN_UFAPEC_SOJAMILHO_PRODUTOS_NOVOS")

            while (resultSet?.next()!!) {
                val id = resultSet.getInt("ID")
                val codProdSefaz = resultSet.getInt("COD_PROD_SEFAZ")
                val descrProd = resultSet.getString("DESCR_PROD")
                val unid = resultSet.getString("UNID")
                val fatorConv = resultSet.getInt("FATOR_CONV")
                val dtInsercao = resultSet.getTimestamp("DT_INSERCAO").toLocalDateTime()

                val prodSefaz = ProdutoSefaz(
                        id,
                        codProdSefaz,
                        descrProd,
                        unid,
                        fatorConv,
                        dtInsercao
                       )

                produtosSefaz += prodSefaz
            }
        }

        return produtosSefaz
    }

    fun salvarAlteraçoes(produtosSefaz: List<Pair<Int, Pair<Int?, Int?>>>) {

        var connection = DatabaseConnection().connection

        val updadeStatement = """
             UPDATE MINING_SEFAZ.SRJUNIOR.UPLAN_UFAPEC_SOJAMILHO_PRODUTOS_NOVOS
             SET FATOR_CONV = ?, COD_PROD_SEFAZ = ?
             WHERE ID = ?"""

            connection.use {

            connection?.autoCommit = false

            val prepStmt = connection?.prepareStatement(updadeStatement)

            for (produto in produtosSefaz) {
                prepStmt?.setInt(3, produto.first)
                prepStmt?.setInt(1, produto.second.first!!)
                prepStmt?.setInt(2, produto.second.second!!)

                prepStmt?.addBatch()
            }

            if (prepStmt != null) {
                prepStmt.executeBatch()
                connection?.commit()
                prepStmt.clearBatch()
            }
        }
    }
}
