package janus.ejecucion

class ReajustePreciosController {

    def resumenAnticipo() {
        // TODO: borrar esto
        if (!params.id) {
            params.id = 1
        }
        def planilla = Planilla.get(params.id)
        def obra = planilla.contrato.oferta.concurso.obra
        def contrato = planilla.contrato
        def planillas = Planilla.findAllByContrato(contrato, [sort: "id"])
        def fp = janus.FormulaPolinomica.findAllByObra(obra)
        def fr = FormulaPolinomicaContractual.findAllByContrato(contrato)
        def tipo = TipoFormulaPolinomica.get(1)
        def oferta = contrato.oferta
        if (fr.size() < 2) {
            fr.each {
                it.delete(flush: true)
            }
            fp.each {
                if (it.valor > 0) {
                    def frpl = new FormulaPolinomicaContractual()
                    frpl.valor = it.valor
                    frpl.contrato = contrato
                    frpl.indice = it.indice
                    frpl.tipoFormulaPolinomica = tipo
                    frpl.numero = it.numero
                    if (!frpl.save(flush: true))
                        println "error " + errors
                }
            }
        }

        def cs = FormulaPolinomicaContractual.findAllByContratoAndNumeroLike(contrato, "c%", [sort: "numero"])

        def datos = []
        def periodoOferta = PeriodosInec.findAll("from PeriodosInec where fechaInicio <='${oferta.fechaEntrega}' and fechaFin >= '${oferta.fechaEntrega}'")
//        println "perof "+periodoOferta+"  " +oferta.fechaEntrega+" ofer "+oferta
        def periodos = []
        periodos.add(periodoOferta[0])
        planillas.each {
//            println "planilla "+it.id +" "+it.periodoIndices?.fechaInicio+" "+it.periodoIndices?.fechaFin+"  "+it.tipoPlanilla.nombre
            if (it.tipoPlanilla.id == 1) {
                def prin = PeriodosInec.findAll("from PeriodosInec where fechaInicio <='${it.fechaPresentacion}' and fechaFin >= '${it.fechaPresentacion}'")
//                println "periodo  anticipo "+prin+"  "+it.fechaPresentacion
                periodos.add(prin[0])
            } else {
                periodos.add(it.periodoIndices)
            }

        }
//        println "periodos "+periodos

        def tot = 0
        periodos.each { per ->
            def vlin = ValorReajuste.findAllByObraAndPeriodoIndice(obra, per)

            if (vlin.size() < 1) {
                def tmp = [:]
                tot = 0
                cs.each {

                    def val = ValorIndice.findByPeriodoAndIndice(per, it.indice)?.valor
                    if (!val)
                        val = 1
                    def vr = new ValorReajuste()
                    vr.valor = val * it.valor
                    vr.indice = it.indice
                    vr.obra = obra
                    vr.periodoIndice = per
                    vr.planilla = planilla
                    if (!vr.save(flush: true)) {
                        println "vr errors " + vr.errors
                    }
                    tmp.put(it.numero, vr.valor)
                    tot += vr.valor * it.valor

                }
                if (tmp.size() > 0) {
                    tmp.put("tot", tot)
                    datos.add(tmp)
                }
            } else {
                def tmp = [:]
                tot = 0
                vlin.each { v ->
                    cs.each { c ->
                        if (c.indice.id.toInteger() == v.indice.id.toInteger()) {
                            tmp.put(c.numero, v.valor)
                            tot += v.valor * c.valor
                        }
                    }
//                    println "tmp "+tmp
                }
                if (tmp.size() > 0) {
                    tmp.put("tot", tot)
                    datos.add(tmp)
                }
            }
        }
//        println "cs "+cs.numero
        datos.each {
//            println "it " + it
        }

        def cant = []
        0.upto(datos.size() - 1) {
            cant.add(it)
        }
//        println "cant "+cant

        [datos: datos, cs: cs, cant: cant, periodos: periodos, fechaOferta: oferta.fechaEntrega, fechaAnticipo: planilla.fechaPresentacion]


    }
}
