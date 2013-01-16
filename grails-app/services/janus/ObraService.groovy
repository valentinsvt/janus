package janus

class ObraService {

    def dbConnectionService


    def registrarObra(obra){
        def cn = dbConnectionService.getConnection()
        def sql = " SELECT * from rgst_obra(${obra.id})"
        def result = []
        cn.eachRow(sql.toString()){r->
            println "res "+r
        }
        return result
    }
}
