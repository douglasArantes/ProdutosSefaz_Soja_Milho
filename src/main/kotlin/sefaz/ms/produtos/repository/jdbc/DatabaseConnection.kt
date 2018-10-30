package sefaz.ms.produtos.repository.jdbc

import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException
import java.util.*

data class DatabaseConnection(
        //val databaseUrl: String = "jdbc:postgresql://localhost:5432/produto_sefaz",
        val databaseUrl: String = "jdbc:netezza://s1431.ms:5480/MINING_SEFAZ",
        //val user: String = "postgres",
        val user: String = "dbarantes",
        //val password: String = "1234asd"
        val password: String = "Db@r@ntes",
        val driverName: String = "org.netezza.Driver"
        ){

    var connection: Connection? = null

    init {
        val props = Properties()
        props["user"] = user
        props["password"] = password
        props["MaxPooledStatements"] = "250"

        try {
            Class.forName(driverName)
            connection = DriverManager.getConnection(databaseUrl, props)

        }catch (e: SQLException){
            e.printStackTrace()
        }
    }

    fun close(){
        try {
            if (connection != null) {
                connection!!.close()
            }
        } catch (e: SQLException) {
            e.printStackTrace()
        }

    }


}
