package janus

import janus.ejecucion.TipoPlanilla

class PlanillaController {

    def index() {

        def contrato = Contrato.get(params.id)
        def anticipo = TipoPlanilla.findByCodigo('A')
        def planillas

    }

}
