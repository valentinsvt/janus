package janus

class OferentesController extends janus.seguridad.Shield {

    def oferentesService

    def obras(){
        println "params "+params
        def result = oferentesService.list(params.tipo,params.oferente)
//        println "res "+result
        [result:result,oferente:params.oferente,tipo:params.tipo?.toUpperCase()]

    }


}
