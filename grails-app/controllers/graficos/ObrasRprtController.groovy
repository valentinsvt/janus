package graficos

import grails.converters.JSON

class ObrasRprtController {
    def dbConnectionService

    def index() { }

    def tipoObra() {

    }

    def tpobData() {
//        println "tpobData $params"
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
//        println "++data: $data"
//        println "++data: ${data as JSON}"

        /** valores para demostración **/
//        data = [titulo: "Inversión por Cantón", datos: "84.8,14,17.1,34.2,31.1,42.8,40,45",
//                cabecera: "cantón 1,cantón 2,cantón 3,cantón 4,cantón 5,cantón 6,cantón 7,cantón 8"]

        render data as JSON
    }

    def cantones() {
//        println "cantones $params"
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
//            println "sql2: $sql2"
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
//        println "++data: $data"
//        println "++data: ${data as JSON}"

        /** valores para demostración **/
//        data = [titulo: "Inversión por Cantón", datos: "84.8,14,17.1,34.2,31.1,42.8,40,45",
//                cabecera: "cantón 1,cantón 2,cantón 3,cantón 4,cantón 5,cantón 6,cantón 7,cantón 8"]

        render data as JSON
    }


    def estadosObras() {
//        println "estados $params"
        def cn = dbConnectionService.getConnection()
        def cn1 = dbConnectionService.getConnection()
        def cn2 = dbConnectionService.getConnection()
        def cn3 = dbConnectionService.getConnection()
        def cn4 = dbConnectionService.getConnection()
        def data = [:]
        def presupuestadas = ""
        def contratadas = ""
        def construccion = ''
        def terminadas = ""
        def cantones = ""

        data.titulo = "Estado de las Obras por Cantón"

        //cantones
        def sql = "select distinct cntn.cntn__id, cntnnmbr from obra, parr, cntn " +
                "where parr.parr__id = obra.parr__id and cntn.cntn__id = parr.cntn__id order by cntn__id desc"

        //presupuestadas
        def sql1 = "select count(*) cnta, cntn__id from obra, parr where prog__id <> 6 and obraetdo ='R' and " +
                "parr.parr__id = obra.parr__id group by cntn__id order by cntn__id desc"
        //contratadas
        def sql2 = "select count(*) cnta, cntn__id from obra, parr where prog__id <> 6 and obraetdo ='R' and " +
                "parr.parr__id = obra.parr__id and obra__id in (select distinct obra__id from cntr) " +
                "group by cntn__id order by cntn__id desc"
        //construccion
        def sql3 = "select count(*) cnta, cntn__id from obra, parr where prog__id <> 6 and obraetdo ='R' and " +
                "parr.parr__id = obra.parr__id and obra__id in (select distinct obra__id from cntr) and " +
                "obrafcin is not null " +
                "group by cntn__id order by cntn__id desc"
        //terminadas
        def sql4 = "select count(*) cnta, cntn__id from obra, parr where prog__id <> 6 and obraetdo ='R' and " +
                "parr.parr__id = obra.parr__id and obra__id in (select distinct obra__id from cntr, plnl " +
                "where plnl.cntr__id = cntr.cntr__id and tppl__id = 9) group by cntn__id order by cntn__id desc"


        cn.eachRow(sql.toString()){d->
            cantones += cantones == ''? d.cntnnmbr : "," + d.cntnnmbr

            cn1.eachRow(sql1.toString()) { c ->
                presupuestadas += presupuestadas == '' ? c.cnta : ',' + c.cnta
            }
            cn2.eachRow(sql2.toString()) { r ->
                contratadas += contratadas == '' ? r.cnta : ',' + r.cnta
            }
            cn3.eachRow(sql3.toString()) { t ->
                construccion += construccion == '' ? t.cnta : ',' + t.cnta
            }
            cn4.eachRow(sql4.toString()) { y ->
                terminadas += terminadas == '' ? y.cnta : ',' + y.cnta
            }
        }

//        println "cantones: $sql"

        data.presupuestadas = presupuestadas
        data.contratadas = contratadas
        data.construccion = construccion
        data.terminadas = terminadas
        data.cabecera = cantones
//        println "++data: $data"
//        println "++data: ${data as JSON}"

        /** valores para demostración **/
//        data = [titulo: "Inversión por Cantón", datos: "84.8,14,17.1,34.2,31.1,42.8,40,45",
//                cabecera: "cantón 1,cantón 2,cantón 3,cantón 4,cantón 5,cantón 6,cantón 7,cantón 8"]

        render data as JSON
    }


    def avanceObras (){

        //        println "estados $params"
        def cn = dbConnectionService.getConnection()
        def data = [:]
        def contratado = ""
        def economico = ""
        def fisico = ''
        def cantones = ""

        data.titulo = "Avance de las Obras por Cantón"

        //cantones
        def sql = "select distinct cntn.cntn__id, cntnnmbr from obra, parr, cntn " +
                "where parr.parr__id = obra.parr__id and cntn.cntn__id = parr.cntn__id order by cntn__id desc"

        //avances
        def sql1 = "select cntnnmbr, sum(cntrmnto) contratado, avg(avncecon)::numeric(6,2)*100 economico, " +
                "avg(avncfsco)::numeric(6,2) fisico from rp_contrato() group by cntnnmbr order by 2 desc;"


        cn.eachRow(sql1.toString()){ d->
            cantones += cantones == ''? d.cntnnmbr : "," + d.cntnnmbr
            contratado += contratado == '' ? d.contratado : ',' + d.contratado
            economico += economico == '' ? ((d.economico * d.contratado) / 100) : ',' + ((d.economico * d.contratado) / 100)
            fisico += fisico == '' ? ((d.fisico * d.contratado) / 100) : ',' + ((d.fisico * d.contratado) / 100)
        }

        data.contratado = contratado
        data.economico = economico
        data.fisico = fisico
        data.cabecera = cantones

        render data as JSON
    }
}
