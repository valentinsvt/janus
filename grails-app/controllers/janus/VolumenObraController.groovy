package janus

class VolumenObraController {

    def volObra(){
        def obra = Obra.list()
        println "obra "+obra
        [obra:obra]
    }
}
