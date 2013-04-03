package janus.oferentes

import janus.Persona

class ExportController extends janus.seguridad.Shield {

    def dbConnectionService
    def oferentesService

    def exportObra() {
//        println "export obra " + params
        def obra = janus.Obra.get(params.obra)

        def oferente = Persona.get(params.oferente)
        def r = oferentesService.exportDominio(janus.Persona, "prsnjnid", oferente)
//        println ">>>>" + r
        if (r == true || r == -1) {
            def personaSql = "select prsn__id id from prsn where prsnjnid=${params.oferente}"
            def cn = dbConnectionService.getConnectionOferentes()
            def prsn = -1
//        println "validacion " + validacion
            cn.eachRow(personaSql.toString()) { row ->
//                println "r " + row
                prsn = row.id
            }

            def res = oferentesService.exportDominio(janus.Obra, "obrajnid", obra, params.oferente, "ofrt__id", prsn, "ofrt__id", "select count(*) from obra where obrajnid=${obra.id} and ofrt__id=${prsn}")
            if (res == true)
                render "OK_Obra exportada correctamente"
            else {
                if (res == false) {
                    render "NO_Error: ha ocurrido un error al copiar el registro."
                } else {
                    render "NO_Error: El registro ya ha sido exportado al sistema de oferentes."
                }
            }
        } else {
            render "NO_Error: No se pudo exportar el oferente para exportar la obra."
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
