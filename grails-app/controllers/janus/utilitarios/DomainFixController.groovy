package janus.utilitarios

class DomainFixController {

    def migracionService

    def index() {

    }

    def valoresDominios(){
//        println "valores dominio "+params
        flash.message = migracionService.arreglaDominios(params.dominio)
        redirect(action: "index")
        return
    }


    def secuencias(){

        render    migracionService.arreglarSecuencias()
    }
}
