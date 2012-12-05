package janus

class VariablesController {

    def variables_ajax() {
        def volquetes = []
        def choferes = []
        def grupoTransporte = DepartamentoItem.findAllByTransporteIsNotNull()
        grupoTransporte.each {
            if (it.transporte.codigo == "H")
                choferes = Item.findAllByDepartamento(it)
            if (it.transporte.codigo == "T")
                volquetes = Item.findAllByDepartamento(it)
        }

        [choferes: choferes, volquetes: volquetes]
    }

    def saveVar_ajax() {

    }
}
