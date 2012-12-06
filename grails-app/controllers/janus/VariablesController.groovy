package janus

class VariablesController {

    def variables_ajax() {
//        println params

        def obra = Obra.get(params.obra)

        def volquetes = []
        def choferes = []
        def grupoTransporte = DepartamentoItem.findAllByTransporteIsNotNull()
        grupoTransporte.each {
            if (it.transporte.codigo == "H")
                choferes = Item.findAllByDepartamento(it)
            if (it.transporte.codigo == "T")
                volquetes = Item.findAllByDepartamento(it)
        }

        [choferes: choferes, volquetes: volquetes, obra: obra]
    }

    def saveVar_ajax() {
        def obra = Obra.get(params.id)
        obra.properties = params
        if (obra.save(flush: true)) {
            render "OK"
        } else {
            println obra.errors
            render "NO"
        }
    }
}
