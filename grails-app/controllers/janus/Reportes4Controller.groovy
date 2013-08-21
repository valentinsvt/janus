package janus



class Reportes4Controller {

    def index() {}
    def dbConnectionService
    def registradas () {

    }

    def tablaRegistradas () {

        println(params)

     def obras

        def sql
        def cn
        def res





        if(params.estado == '1'){

//           obras = Obra.findAllByEstadoAndOficioIngresoIsNotNull("R")

            sql = "SELECT\n" +
                    "*\n" +
                    "FROM obra where obraetdo='R' and obraofig is NOT NULL and ${params.buscador} ILIKE ('%${params.criterio}%') "

            cn = dbConnectionService.getConnection()

            res = cn.rows(sql.toString())

        }
        if(params.estado == '2'){

//           obras = Obra.findAllByOficioIngresoIsNotNull()


            sql = "SELECT\n" +
                    "*\n" +
                    "FROM obra where obraofig is NOT NULL and ${params.buscador} ILIKE ('%${params.criterio}%') "

            cn = dbConnectionService.getConnection()

            res = cn.rows(sql.toString())

        }
        if(params.estado == '3'){

//           obras = Obra.findAllByEstado("R")

           sql = "SELECT\n" +
                   "*\n" +
                   "FROM obra where obraetdo='R' and ${params.buscador} ILIKE ('%${params.criterio}%') "

            cn = dbConnectionService.getConnection()

            res = cn.rows(sql.toString())

        }
        println(sql)
//        println(res)

       println("obras:" + obras)

        return [obras: obras, res: res]
    }

}
