package janus.oferentes

import org.codehaus.groovy.grails.orm.hibernate.cfg.GrailsDomainBinder

class ExportController extends janus.seguridad.Shield {

    def dbConnectionService
    def oferentesService
    def exportObra(){
        println "export obra "+params
        def obra = janus.Obra.get(params.obra)
        def res = oferentesService.exportDominio(janus.Obra,"obrajnid",obra)
        if (res==true)
            render "ok"
        else{
            if(res ==false){
                render "Error: ha ocurrido un error al copiar el registro."
            }else{
                render "Error: El registro ya ha sido exportado al sistema de oferentes."
            }
        }


    }




}
