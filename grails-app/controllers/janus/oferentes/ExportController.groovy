package janus.oferentes

import janus.Persona
import janus.VolumenesObra

class ExportController extends janus.seguridad.Shield {

    def dbConnectionService
    def oferentesService

    def exportObra() {
//        println "@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ export obra " + params
        def obra = janus.Obra.get(params.obra)

        def oferente = Persona.get(params.oferente)
        def cn = dbConnectionService.getConnectionOferentes()
        /** todo: hacer que si ya existe en oferentes no se inserte lapersona. **/
//        def of_oferente = cn.rows("select count(*) cnta from prsn where ")
//        if()

        def r = oferentesService.exportDominio(janus.Persona, "prsnjnid", oferente)

        println ">>>>" + r
        if (r !=-1) {
            def oferenteId = r
            def res = oferentesService.exportDominio(janus.Obra, "obrajnid", obra, params.oferente,
                    "ofrt__id", r, "ofrt__id", "select * from obra where obrajnid=${obra.id} and ofrt__id=${r}")
            //println "res "+res
            if (res !=-1) {
                def obraJnId=res
                def vols = VolumenesObra.findAllByObra(obra)
                def concurso = janus.pac.Concurso.findByObra(obra)
                def fechaOferta = concurso.fechaLimiteEntregaOfertas.format("yyyy-MM-dd")
                println "sql "+  "update obra set obracdcn=${concurso.codigo}, obrafcof='${fechaOferta}' where obra__id=${obraJnId}"
                oferentesService.sqlOferentes("update obra set obracdcn='${concurso.codigo}', obrafcof='${fechaOferta}', " +
                        "obraetdo = 'N',indidrob=0,indiprmo=0,indimntn=0,indignrl=0,indiadmn=0,indiimpr=0,indigrnt=0," +
                        "indiutil=0,indicsfn=0,indivhcl=0,inditotl=inditmbr where obra__id=${obraJnId}",2)


//                println "volumen!!!------------------------------------------ "
                vols.each {v->
//                    println "volumen "+v.item+"  "+v.cantidad
                    res = oferentesService.exportDominio(janus.Item, "itemjnid", v.item, oferenteId, "ofrt__id",r,
                            "ofrt__id", "select * from item where ofrt__id = ${oferenteId} and itemcdgo = '${v.item.codigo}'")
                    println "IT............................ "+res
                    if(res!=-1){
                        def itemId=res
                        res = oferentesService.exportDominioSinReferencia(janus.SubPresupuesto, v.subPresupuesto, false,
                                false, "select * from sbpr where sbprdscr='${v.subPresupuesto.descripcion}' " +
                                "and grpo__id=${v.subPresupuesto.grupo.id}  ")
//                        println "SB............................ "+res
                        if(res==-1){
                            render "NO_Error: ha ocurrido un error al copiar el registro."
                            println "-1 ??? sub "+v.subPresupuesto.id
                            return
                        }
                        res = oferentesService.sqlOferentes("insert into vlob (vlob__id, sbpr__id, item__id, " +
                                "obra__id, vlobcntd, vlobordn, vlobdias) values (default, ${res}, ${itemId}, " +
                                "${obraJnId}, ${v.cantidad}, ${v.orden}, ${v.dias})", 1)
//                        println "VLOB............................ "+res

                    }else{
                        render "NO_Error: ha ocurrido un error al copiar el registro."
                        println "-1 ??? item "+ v.item
                        return
                    }
                }
                render "OK_Obra exportada correctamente"
                return
            }else {
                render "NO_Error: ha ocurrido un error al copiar el registro."
                return
            }
        } else {
            render "NO_Error: No se pudo exportar el oferente para exportar la obra."
            return
        }
    }

    def exportOferentes() {
//        println "export oferentes " + params

        if (params.id.class == java.lang.String) {
            params.id = [params.id]
        }

//        println "export oferentes " + params

        def msg = ""

        params.id.each { id ->
            def oferente = Persona.get(id)
            def r = oferentesService.exportDominio(janus.Persona, "prsnjnid", oferente)
            if (r == true) {
                msg += "<li>Oferente ${id} exportado OK</li>"
            } else {
                if (r == false) {
                    msg += "<li>Error: ha ocurrido un error al copiar el registro ${id}</li>"
                } else {
                    msg += "<li>Error: El registro ${id} ya ha sido exportado al sistema de oferentes.</li>"
                }
            }
        }

        render "<ul>${msg}</ul>"
    }


}
