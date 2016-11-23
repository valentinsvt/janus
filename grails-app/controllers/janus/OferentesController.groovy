package janus

import org.codehaus.groovy.runtime.DefaultGroovyMethods

class OferentesController extends janus.seguridad.Shield {

    def oferentesService

    def obras(){
        println "params "+params
        def result = oferentesService.list(params.tipo, params.oferente)
//        println "res "+result
        [result:result,oferente:params.oferente,tipo:params.tipo?.toUpperCase()]

    }


    def copiarObra(){
        println "params "+params
        def obra = Obra.get(params.obra)
        def oferente = Persona.get(params.oferente)
        def ofId=params.obraOf
        def copia= new Obra()
        Obra.properties.declaredFields.each {
//            println "campo--> "+it.getName()
            if(it.getName() != 'id') copia.properties[it.getName()]=obra.properties[it.getName()]
        }
//        println "copia: ${copia.codigo} obra código: ${obra.codigo}, id: ${copia.id}"
        copia.oferente = oferente
        copia.codigo = obra.codigo+"-OF"
        copia.estado = "R"
        copia.save(flush: true)
        oferentesService.copiaDatosObra(copia.id, ofId)
        //println "id "+copia.id

        def error = oferentesService.copiaVolumen(copia.id, ofId)
        println "formula error: $error"
        error += oferentesService.copiaFormula(copia.id, ofId)
        println " crono "
        error += oferentesService.copiaCrono(copia.id, ofId)
        println "pasa crono con error: $error"
        if(error != "")
            render "error#${error}"
        else
            render copia.id
    }


    HashMap toMap(dominio) {
        def mapa = [:]
        dominio.properties.declaredFields.each {
            if (it.getName().substring(0, 1) != "\$" && it.getName().substring(0, 1) != "") {
                mapa.put(it.getName(), it.getType())
            }
        }
        return mapa
    }


}
