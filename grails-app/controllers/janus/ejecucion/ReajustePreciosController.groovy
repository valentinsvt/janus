package janus.ejecucion

class ReajustePreciosController {

    def resumenAnticipo(){
        params.id=1
        def planilla = Planilla.get(params.id)
        def obra = planilla.contrato.oferta.concurso.obra
        def contrato = planilla.contrato
        def planillas = Planilla.findAllByContrato(contrato,[sort:"id"])
        def fp = janus.FormulaPolinomica.findAllByObra(obra)
        def fr = FormulaPolinomicaContractual.findAllByContrato(contrato)
        def tipo = TipoFormulaPolinomica.get(1)
        def oferta = contrato.oferta
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

        def cs =    FormulaPolinomicaContractual.findAllByContratoAndNumeroLike(contrato,"c%")
        def datos = []
        def periodoOferta = PeriodosInec.findByFechaFinGreaterThanAndFechaInicioLessThan(oferta.fechaEntrega,oferta.fechaEntrega)
        def periodos=[]
        periodos.add(periodoOferta)
        planillas.each {
            println "planilla "+it.id +" "+it.periodoIndices?.fechaInicio+" "+it.periodoIndices?.fechaInicio+"  "+it.tipoPlanilla.nombre
            if (it.tipoPlanilla.id==1){
                println "entro anticipo"
                periodos.add(PeriodosInec.findByFechaFinGreaterThanAndFechaInicioLessThan(it.fechaPresentacion,it.fechaPresentacion))
            }else{
                periodos.add(it.periodoIndices)
            }

        }
        println "periodos "+periodos


        periodos.each {per->
            def vlin = ValorReajuste.findAllByObraAndPeriodoIndice(obra,per)
            if (vlin.size()<1){

                cs.each {

                    def val = ValorIndice.findByPeriodoAndIndice(per,it.indice)?.valor
                    if (!val)
                        val = 1
                    def vr = new ValorReajuste()
                    vr.valor=val*it.valor
                    vr.indice=it.indice
                    vr.obra=obra
                    vr.periodoIndice=per
                    vr.planilla=planilla
                    if (!vr.save(flush: true)){
                        println "vr errors "+vr.errors
                    }
                    def tmp = [:]
                    tmp.put(it.numero,vr.valor)
                    datos.add(tmp)

                }
            }else{
                vlin.each {v->
                    cs.each {c->
                        if (c.indice.id.toInteger()==v.indice.id.toInteger()){
                            def tmp = [:]
                            tmp.put(c.numero,v.valor)
                            datos.add(tmp)
                        }

                    }
                }
            }
        }
        println "datos "+datos



        [datos:datos]


    }
}
