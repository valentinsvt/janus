package janus

class ObraService {

    def dbConnectionService


    def registrarObra(obra){
        def cn = dbConnectionService.getConnection()
        def sql = " SELECT * from rgst_obra_v2(${obra.id})"
        def result = []
        cn.eachRow(sql.toString()){r->
            println "res "+r
        }
        cn.close()
        return result
    }

    def montoObra(obra){
        def cn = dbConnectionService.getConnection()
        def sql = " SELECT sum(totl) from rbro_pcun_v2(${obra.id})"
//        println "sql tot obra "+sql
        def result =0
        cn.eachRow(sql.toString()){r->
            result=r[0]
        }
        cn.close()
        return result


    }

    def borrarFP(obra) {

        def ob = Obra.get(obra)
        def fp = FormulaPolinomica.findAllByObra(ob, [sort: "numero"])    // incluir subpresupuesto
        def ok = true

        fp.each { f ->
            def children = ItemsFormulaPolinomica.findAllByFormulaPolinomica(f)
            children.each { ch ->
                try {
                    ch.delete(flush: true)
                } catch (e) {
                    ok = false
                    println "servicio borrarFP error al borrar ITFP ${ch.id}"
                    println e.printStackTrace()
                }
            }
            try {
                f.delete(flush: true)
            } catch (e) {
                ok = false
                println "servicio borrarFP error al borrar FP ${f.id}"
                println e.printStackTrace()
            }
        }
        return ok
    }

    def esDuenoObra(obra, usro) {
        def dueno = false
        def funcionElab = Funcion.findByCodigo('E')
        def personasUtfpu = PersonaRol.findAllByFuncionAndPersonaInList(funcionElab, Persona.findAllByDepartamento(Departamento.findByCodigo('UTFPU')))
        def responsableRol = PersonaRol.findByPersonaAndFuncion(obra?.responsableObra, funcionElab)
        if (responsableRol) {
//            if (obra?.responsableObra?.departamento?.direccion?.id == Persona.get(session.usuario.id).departamento?.direccion?.id) {
            if (obra?.responsableObra?.departamento?.direccion?.id == Persona.get(usro).departamento?.direccion?.id) {
                dueno = true
            } else {
                dueno = personasUtfpu.contains(responsableRol) && (Persona.get(usro).departamento?.codigo == 'UTFPU')
            }
        }
        dueno
    }



}
