package graficos

import grails.converters.JSON

class ObrasRprtController {
    def dbConnectionService

    def index() { }

    def tipoObra() {

    }

    def tpobData() {
        println "tpobData $params"
        def cn = dbConnectionService.getConnection()
        def data = [:]
        def cantones = ""
        def valores = ""

        data.titulo = "Inversión por Cantón (%)"

        def sql = "select sum(cntrmnto) maximo from rp_contrato() group by cntnnmbr order by 1 desc limit 1"
        def maximo = cn.rows(sql.toString())[0].maximo?:1
        sql = "select cntnnmbr, (sum(cntrmnto)/${maximo}*100)::numeric(6,2) pcnt " +
                "from rp_contrato() group by cntnnmbr order by 2 desc;"
//        println "cantones: $sql"
        cn.eachRow(sql.toString()) { d ->
            cantones += cantones == ''? d.cntnnmbr : "," + d.cntnnmbr
            valores  += valores == ''? d.pcnt : "," + d.pcnt
        }
        data.datos = valores
        data.cabecera = cantones
        println "++data: $data"
        println "++data: ${data as JSON}"

        /** valores para demostración **/
//        data = [titulo: "Inversión por Cantón", datos: "84.8,14,17.1,34.2,31.1,42.8,40,45",
//                cabecera: "cantón 1,cantón 2,cantón 3,cantón 4,cantón 5,cantón 6,cantón 7,cantón 8"]

        render data as JSON
    }

    def cantones() {
        println "cantones $params"
        def cn = dbConnectionService.getConnection()
        def cn1 = dbConnectionService.getConnection()
        def data = [:]
        def vias = ""
        def riego = ""
        def infra = ""
        def cantones = ""

        data.titulo = "Inversión por Tipo de Obra y Cantón"

        def sql = "select distinct cntn.cntn__id, cntnnmbr from obra, parr, cntn " +
                "where parr.parr__id = obra.parr__id and cntn.cntn__id = parr.cntn__id order by 1 desc"
        cn.eachRow(sql.toString()) { d ->
            cantones += cantones == ''? d.cntnnmbr : "," + d.cntnnmbr
            def sql2 = "select sum(obravlor) suma, progdscr from obra, parr, prog where parr.parr__id = obra.parr__id and " +
                    "prog.prog__id = obra.prog__id and cntn__id = ${d.cntn__id} and prog.prog__id <> 6 group by progdscr"
            println "sql2: $sql2"
            cn1.eachRow(sql2.toString()) { c ->
                if(c.progdscr == 'VIAS') {
                    vias += vias == ''? c.suma : "," + c.suma
                }
                if(c.progdscr == 'INFRAESTRUCTURA') {
                    infra += infra == ''? c.suma : "," + c.suma
                }
                if(c.progdscr == 'RIEGO') {
                    riego += riego == ''? c.suma : "," + c.suma
                }
            }
        }

//        println "cantones: $sql"
        data.vias = vias
        data.infra = infra
        data.riego = riego
        data.cabecera = cantones
        println "++data: $data"
        println "++data: ${data as JSON}"

        /** valores para demostración **/
//        data = [titulo: "Inversión por Cantón", datos: "84.8,14,17.1,34.2,31.1,42.8,40,45",
//                cabecera: "cantón 1,cantón 2,cantón 3,cantón 4,cantón 5,cantón 6,cantón 7,cantón 8"]

        render data as JSON
    }
}
