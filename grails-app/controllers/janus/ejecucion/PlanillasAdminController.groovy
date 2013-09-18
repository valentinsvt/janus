package janus.ejecucion

import janus.Obra

class PlanillasAdminController {

    def list(){

        def obra = Obra.get(params.id)

        def fp = janus.FormulaPolinomica.findAllByObra(obra)
//        println fp

        def planillaInstanceList = PlanillaAdmin.findAllByObra(obra, [sort: 'id'])
        return [ obra:obra, list: planillaInstanceList]
    }

}
