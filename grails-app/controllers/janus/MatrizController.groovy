package janus

class MatrizController extends janus.seguridad.Shield {

    def preciosService
    def dbConnectionService

    def index() {}

    def matrizPolinomica(){
        /*Todo cambiar para que funcione por obra*/
        def obra = params.id
        def cn = dbConnectionService.getConnection()
        def cn2 = dbConnectionService.getConnection()
        def sql = "SELECT clmncdgo,clmndscr,clmntipo from obcl_${session.usuario} order by 1"
        def columnas = []
        def filas = []
        cn.eachRow(sql.toString()){r->
            columnas.add([r[0],r[1],r[2]])
        }
        sql ="SELECT * from obrb_${session.usuario} order by orden"
        def cont = 1
        cn.eachRow(sql.toString()){r->
            def tmp = [cont,r[0].trim(),r[1],r[2],r[3]]
            def sq =""
            columnas.each {c->
                if(c[2]!="R"){
                    sq = "select valor from obvl_${session.usuario} where clmncdgo=${c[0]} and codigo='${r[0].trim()}'"
                    cn2.eachRow(sq.toString()){v->
                        tmp.add(v[0])
                    }
                }

            }


            filas.add(tmp)
            cont++
        }


        [filas:filas,cols:columnas,obraId:params.id]
    }

}
