package janus.ejecucion

class ReajustePreciosController {

    def resumen(){
        params.id=1
        def planilla = Planilla.get(params.id)
        def obra = planilla.contrato.oferta.concurso.obra
        def contrato = planilla.contrato
        def fp = janus.FormulaPolinomica.findAllByObra(obra)
        def fr = FormulaPolinomicaContractual.findAllByContrato(contrato)
        def tipo = TipoFormulaPolinomica.get(1)
        if (fr.size()<2){
               fr.each {
                   it.delete(flush: true)
               }
            fp.each {
                def frpl = new FormulaPolinomicaContractual()
                frpl.valor = it.valor
                frpl.contrato = contrato
                frpl.indice = it.indice
                frpl.tipoFormulaPolinomica = tipo
                frpl.numero = it.numero
                if (!frpl.save(flush: true))
                    println "error "+errors
            }

        }



    }
}
