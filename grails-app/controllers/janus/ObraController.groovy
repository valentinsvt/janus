package janus

import janus.pac.DocumentoObra
import janus.pac.TipoProcedimiento
import org.springframework.dao.DataIntegrityViolationException

class ObraController extends janus.seguridad.Shield {

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]
    def buscadorService
    def obraService
    def dbConnectionService

    def index() {
        redirect(action: "registroObra", params: params)
    } //index

    def list() {
        params.max = Math.min(params.max ? params.int('max') : 10, 100)
        [obraInstanceList: Obra.list(params), obraInstanceTotal: Obra.count(), params: params]
    } //list

    def biblioteca() {

    }

    def iniciarObraAdm() {
        //println "incio obra dm " + params
        def obra = Obra.get(params.obra)
        def fecha
        try {
            fecha = new Date().parse("dd-MM-yyyy", params.fecha)
            if (fecha > obra.fechaCreacionObra) {
                if (!obra.fechaInicio) {
                    obra.tipo = "D"
                    obra.fechaInicio = fecha
                    obra.observacionesInicioObra = params.obs
                    obra.save(flush: true)
                    render "ok"
                    return

                } else {
                    render "error"
                    return
                }

            } else {
                render "La fecha de inicio de la obra debe ser mayor a ${obra.fechaCreacionObra.format('dd-MMM-yyyy')}"
                return
            }
        } catch (e) {
            println "error fecha " + e
            render "error"
            return
        }


    }


    def obrasFinalizadas() {


        def perfil = session.perfil.id

//        return [perfil: perfil]

        def campos = ["codigo": ["Código", "string"], "nombre": ["Nombre", "string"], "descripcion": ["Descripción", "string"], "oficioIngreso": ["Memo ingreso", "string"], "oficioSalida": ["Memo salida", "string"], "sitio": ["Sitio", "string"], "plazoEjecucionMeses": ["Plazo", "number"], "parroquia": ["Parroquia", "string"], "comunidad": ["Comunidad", "string"], "departamento": ["Dirección", "string"], "fechaCreacionObra": ["Fecha", "date"]]
        [campos: campos, perfil: perfil]
    }

    def buscarObraFin() {
//        println "buscar obra fin"

        def extraParr = ""
        def extraCom = ""
        def extraDep = ""

        if (params.campos instanceof java.lang.String) {
            if (params.campos == "parroquia") {
                def parrs = Parroquia.findAll("from Parroquia where nombre like '%${params.criterios.toUpperCase()}%'")
                params.criterios = ""
                parrs.eachWithIndex { p, i ->
                    extraParr += "" + p.id
                    if (i < parrs.size() - 1)
                        extraParr += ","
                }
                if (extraParr.size() < 1)
                    extraParr = "-1"
                params.campos = ""
                params.operadores = ""
            }
            if (params.campos == "comunidad") {
                def coms = Comunidad.findAll("from Comunidad where nombre like '%${params.criterios.toUpperCase()}%'")
                params.criterios = ""
                coms.eachWithIndex { p, i ->
                    extraCom += "" + p.id
                    if (i < coms.size() - 1)
                        extraCom += ","
                }
                if (extraCom.size() < 1)
                    extraCom = "-1"
                params.campos = ""
                params.operadores = ""
            }
            if (params.campos == "departamento") {
                def dirs = Direccion.findAll("from Direccion where nombre like '%${params.criterios.toUpperCase()}%'")
                def deps = Departamento.findAllByDireccionInList(dirs)
                params.criterios = ""
                deps.eachWithIndex { p, i ->
                    extraDep += "" + p.id
                    if (i < deps.size() - 1)
                        extraDep += ","
                }
                if (extraDep.size() < 1)
                    extraDep = "-1"
                params.campos = ""
                params.operadores = ""
            }
        } else {
            def remove = []
            params.campos.eachWithIndex { p, i ->
                if (p == "comunidad") {
                    def coms = Comunidad.findAll("from Comunidad where nombre like '%${params.criterios[i].toUpperCase()}%'")

                    coms.eachWithIndex { c, j ->
                        extraCom += "" + c.id
                        if (j < coms.size() - 1)
                            extraCom += ","
                    }
                    if (extraCom.size() < 1)
                        extraCom = "-1"
                    remove.add(i)
                }
                if (p == "parroquia") {
                    def parrs = Parroquia.findAll("from Parroquia where nombre like '%${params.criterios[i].toUpperCase()}%'")

                    parrs.eachWithIndex { c, j ->
                        extraParr += "" + c.id
                        if (j < parrs.size() - 1)
                            extraParr += ","
                    }
                    if (extraParr.size() < 1)
                        extraParr = "-1"
                    remove.add(i)
                }
                if (p == "departamento") {
                    def dirs = Direccion.findAll("from Direccion where nombre like '%${params.criterios.toUpperCase()}%'")
                    def deps = Departamento.findAllByDireccionInList(dirs)

                    deps.eachWithIndex { c, j ->
                        extraDep += "" + c.id
                        if (j < deps.size() - 1)
                            extraDep += ","
                    }
                    if (extraDep.size() < 1)
                        extraDep = "-1"
                    remove.add(i)
                }
            }
            remove.each {
                params.criterios[it] = null
                params.campos[it] = null
                params.operadores[it] = null
            }
        }


        def extras = " and liquidacion=0 and fechaFin is not null"
        if (extraParr.size() > 1)
            extras += " and parroquia in (${extraParr})"
        if (extraCom.size() > 1)
            extras += " and comunidad in (${extraCom})"
        if (extraDep.size() > 1)
            extras += " and departamento in (${extraDep})"

//        println "extas "+extras
        def parr = { p ->
            return p.parroquia?.nombre
        }
        def comu = { c ->
            return c.comunidad?.nombre
        }
        def listaTitulos = ["CODIGO", "NOMBRE", "DIRECCION", "FECHA REG.", "SITIO", "PARROQUIA", "COMUNIDAD", "FECHA INICIO", "FECHA FIN"]
        def listaCampos = ["codigo", "nombre", "departamento", "fechaCreacionObra", "sitio", "parroquia", "comunidad", "fechaInicio", "fechaFin"]
        def funciones = [null, null, null, ["format": ["dd/MM/yyyy"]], null, ["closure": [parr, "&"]], ["closure": [comu, "&"]], ["format": ["dd/MM/yyyy"]], ["format": ["dd/MM/yyyy"]]]
        def url = g.createLink(action: "buscarObraFin", controller: "obra")
        def funcionJs = "function(){"
        funcionJs += '$("#modal-busqueda").modal("hide");'
        funcionJs += 'location.href="' + g.createLink(action: 'registroObra', controller: 'obra') + '?obra="+$(this).attr("regId");'
        funcionJs += '}'
        def numRegistros = 20
//        println "params " + params.reporte + "  " + params.excel

        if (!params.reporte) {
            if (params.excel) {
                session.dominio = Obra
                session.funciones = funciones
                def anchos = [15, 50, 70, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20]
                /*anchos para el set column view en excel (no son porcentajes)*/
                redirect(controller: "reportes", action: "reporteBuscadorExcel", params: [listaCampos: listaCampos, listaTitulos: listaTitulos, tabla: "Obra", orden: params.orden, ordenado: params.ordenado, criterios: params.criterios, operadores: params.operadores, campos: params.campos, titulo: "Obras", anchos: anchos, extras: extras, landscape: true])
            } else {
                def lista = buscadorService.buscar(Obra, "Obra", "excluyente", params, true, extras)
                /* Dominio, nombre del dominio , excluyente o incluyente ,params tal cual llegan de la interfaz del buscador, ignore case */
                lista.pop()
                render(view: '../tablaBuscador', model: [listaTitulos: listaTitulos, listaCampos: listaCampos, lista: lista, funciones: funciones, url: url, controller: "obra", numRegistros: numRegistros, funcionJs: funcionJs, width: 1800, paginas: 12])
            }

        } else {
//            println "entro reporte"
            /*De esto solo cambiar el dominio, el parametro tabla, el paramtero titulo y el tamaño de las columnas (anchos)*/
            session.dominio = Obra
            session.funciones = funciones
            def anchos = [7, 16, 23, 8, 10, 10, 10, 8, 8]
            /*el ancho de las columnas en porcentajes... solo enteros*/
            redirect(controller: "reportes", action: "reporteBuscador", params: [listaCampos: listaCampos, listaTitulos: listaTitulos, tabla: "Obra", orden: params.orden, ordenado: params.ordenado, criterios: params.criterios, operadores: params.operadores, campos: params.campos, titulo: "REPORTE DE OBRAS FINALIZADAS", anchos: anchos, extras: extras, landscape: true])
        }
    }


    def regitrarObra() {
        def obra = Obra.get(params.id)
        def obrafp = new ObraFPController()

        def msg = ""
        def vols = VolumenesObra.findAllByObra(obra)
        if (vols.size() < 1) {
            msg = "Error: la obra no tiene volumenes de obra registrados"
            render msg
            return
        }
        def crono = 0
        vols.each {
            def tmp = Cronograma.findAllByVolumenObra(it)
            tmp.each { tm ->
                crono += tm.porcentaje
            }
//            println "volObra: " + it.item.codigo + " tmp " + tmp.volumenObra.id + "  " + tmp.porcentaje + "  " + tmp.precio + "  " + tmp.cantidad
//            println "crono " + crono
            if (crono.toDouble().round(2) != 100.00) {
                msg += "<br><span class='label-azul'>Error:</span> La suma de porcentajes del volumen de obra: ${it.item.codigo} (${crono.toDouble().round(2)}) en el cronograma es diferente de 100%"
            }
            crono = 0
        }
        if (msg != "") {
            render msg
            return
        }

        def res = obrafp.verificaMatriz(obra.id)
        if (res != "") {
            msg = res
//            println "1 res "+msg
            render msg
            return
        }

        res = obrafp.verifica_precios(obra.id)
        if (res.size() > 0) {
            msg = "<span style='color:red'>Errores detectados</span><br> <span class='label-azul'>No se encontraron precios para los siguientes items:</span><br>"
            msg += res.collect { "<b>ITEM</b>: $it.key ${it.value.join(", <b>Lista</b>: ")}" }.join('<br>')
            render msg
            return
        }
//        println "2 res "+msg

        def fps = FormulaPolinomica.findAllByObra(obra)
//        println "fps "+fps
        def totalP = 0
        fps.each { fp ->
            if (fp.numero =~ "p") {
//                println "sumo "+fp.numero+"  "+fp.valor
                totalP += fp.valor
            }
        }

        def totalC = 0
        fps.each { fp ->
            if (fp.numero =~ "c") {
//                println "sumo "+fp.numero+"  "+fp.valor
                totalC += fp.valor
            }
        }
//        println "totp "+totalP
        def valorMenorCuantia = TipoProcedimiento.findBySigla("MCD")?.techo
        def consultoria = janus.Parametros.findByEmpresaLike(message(code: 'ambiente2'))
        if(consultoria) valorMenorCuantia = 0
        println "es consultoría: ${consultoria}"
        def valorObra = obra.valor
        if (valorObra <= valorMenorCuantia) {
            if (obra.tipo != 'D') {
                if (totalP.toDouble().round(6) != 1.000) {
                    render "La suma de los coeficientes de la formula polinómica (${totalP}) es diferente a 1.000"
                    return
                }
                if (totalC.toDouble().round(6) != 1.000) {
                    render "La suma de los coeficientes de la Cuadrilla tipo (${totalC}) es diferente a 1.000"
                    return
                }
            }
        }

        def documentos = DocumentoObra.findAllByObra(obra)
        if (documentos.size() < 2) {
            render "Debe ingresar al menos 2 documentos en la biblioteca de la obra: 'Plano' y 'Justificativo de cantidad de obra'"
            return
        } else {
            def plano = documentos.findAll { it.nombre.toLowerCase().contains("plano") }
            if (plano.size() == 0) {
                render "Debe ingresar un documento en la biblioteca de la obra con nombre 'Plano'"
                return
            }
            def justificacion = documentos.findAll { it.nombre.toLowerCase().contains("justificativo") }
            if (justificacion.size() == 0) {
                render "Debe ingresar un documento en la biblioteca de la obra con nombre 'Justificativo de cantidad de obra'"
                return
            }
        }


        obraService.registrarObra(obra)
        obra.estado = "R"
        obra.desgloseTransporte = null  //obliga a genrar matriz con valores históricos almacenados por grst_obra
        if (obra.save(flush: true)) {
            render "ok"
            return
        }
    }

    def desregitrarObra() {
        def obra = Obra.get(params.id)
        obra.estado = "N"
        if (obra.save(flush: true))
            render "ok"
        else
            println "error: " + obra.errors
        return
    }

    def calculaPlazo() {
        //println "calculaPlazo: " + params
        def obra = Obra.get(params.id)

        if (!params.personas) params.personas = obra.plazoPersonas
        if (!params.maquinas) params.maquinas = obra.plazoMaquinas
        if (!params.save) params.save = "0"

        def sqlM = "select itemcdgo, itemnmbr, sum(itemcntd) itemcntd, sum(itemcntd/8) dias " +
                "from obra_comp_v2(${params.id}) where grpo__id = 2 and itemcntd > 0 group by itemcdgo, itemnmbr order by dias desc"
        def sqlR = "select itemcdgo, itemnmbr, unddcdgo, sum(rbrocntd) rbrocntd, sum(dias) dias " +
                "from plazo(${params.id},${params.personas},${params.maquinas},${params.save}) group by itemcdgo, itemnmbr, unddcdgo"
        //println sqlM
        //println sqlR
        def cn = dbConnectionService.getConnection()
        def resultM = cn.rows(sqlM.toString())
        def resultR = cn.rows(sqlR.toString())

        //println "\n\n"
        //println resultM
        //println resultR
        //println "\n\n"

        if (params.save.toString() == "0") {
            return [obra: obra, resultM: resultM, resultR: resultR, params: params]
        } else {
            obra.plazoPersonas = params.personas.toInteger()
            obra.plazoMaquinas = params.maquinas.toInteger()
            obra.plazoEjecucionMeses = params.plazoMeses.toInteger()
            obra.plazoEjecucionDias = params.plazoDias.toInteger()

            if (!obra.save(flush: true)) {
                println "error: " + obra.errors
                flash.clase = "alert-error"
                flash.message = "Ha ocurrido un error al guardar el plazo de la obra"
                return [obra: obra, resultM: resultM, resultR: resultR, params: params]
            } else {
                flash.clase = "alert-success"
                flash.message = "Plazo actualizado correctamente"
                redirect(action: "registroObra", params: [obra: obra.id])
            }
        }
    }

    def savePlazo() {
        def obra = Obra.get(params.id)
        obra.plazoEjecucionMeses = params.plazoMeses.toInteger()
        obra.plazoEjecucionDias = params.plazoDias.toInteger()
        if (!obra.save(flush: true)) {
            println "error: " + obra.errors
            flash.clase = "alert-error"
            flash.message = "Ha ocurrido un error al modificar el plazo de la obra"
        } else {
            flash.clase = "alert-success"
            flash.message = "Plazo actualizado correctamente"
        }
        redirect(action: "registroObra", params: [obra: obra.id])
    }

    def updateCoords() {

    }

    def existeFP() {
        def obra = Obra.get(params.obra.toLong())
        def fps = FormulaPolinomica.countByObra(obra)
        render fps != 0
    }

    def cambiarAdminDir() {
        def obra = Obra.get(params.id)
        obra.tipo = 'D'
        if (!obra.save(flush: true)) {
            flash.message = g.renderErrors(bean: obra)
        }
        redirect(action: "registroObra", params: [obra: obra.id])
    }

    def saveMemoSIF() {
        def obra = Obra.get(params.obra)
        def memo = params.memo.trim().toUpperCase()
        obra.memoSif = memo
        if (obra.save(flush: true)) {
            render "OK_${memo}"
        } else {
            render "NO_" + renderErrors(bean: obra)
        }
    }

    def aprobarSif() {
        def obra = Obra.get(params.obra)
        obra.estadoSif = "R"
        obra.save(flush: true)
        flash.message = "Memo S.I.F. aprobado"
        render "ok"
    }

    def registroObra() {

        def cn = dbConnectionService.getConnection()
//        println "---" + params
//        println "---" + params
        def obra
        def perfil = session.perfil
        def persona = Persona.get(session.usuario.id)
        def direccion = Direccion.get(persona?.departamento?.direccion?.id)
        def grupo = Grupo.findByDireccion(direccion)
        def departamentos = Departamento.findAllByDireccion(direccion)
        def programa
        def tipoObra
        def claseObra
        def duenoObra = 0
        def funcionElab = Funcion.findByCodigo('E')
        def personasUtfpu1 = Persona.findAllByDepartamento(Departamento.findByCodigo('UTFPU'))
        def personasUtfpu = PersonaRol.findAllByFuncionAndPersonaInList(funcionElab, personasUtfpu1)
        def responsableObra

        def fechaPrecio = new Date()
        cn.eachRow("select max(rbpcfcha) fcha from rbpc, item where rbpc.item__id = item.item__id and " +
                "itemnmbr ilike '%cemento%port%'") { d ->
            fechaPrecio = d.fcha
        }

        def sbprMF = [:]


//        println "fecha: " + fechaPrecio

//filtro original combos programa tipo clase

//        if(grupo != null){
//            programa = Programacion.findAllByGrupo(grupo)
//            tipoObra = TipoObra.findAllByGrupo(grupo)
//            claseObra = ClaseObra.findAllByGrupo(grupo)
//        }else {
//            programa = -1
//            tipoObra = -1
//            claseObra = -1
//        }


        programa = Programacion.list();
        tipoObra = TipoObra.list();
        claseObra = ClaseObra.list();

//        println("grupo" + grupo)
//        println("direccion" + direccion)
//        println("subpresupuest" + subPresupuesto1)
//        println("direccion" + direccion.id)
//        println("programa" + programa)
//        println("tipo" + tipoObra)
//        println("clase" + claseObra)


        def matrizOk = false

        def prov = Provincia.list();
        def campos = ["codigo": ["Código", "string"], "nombre": ["Nombre", "string"], "descripcion": ["Descripción", "string"], "oficioIngreso": ["Memo ingreso", "string"], "oficioSalida": ["Memo salida", "string"], "sitio": ["Sitio", "string"], "plazoEjecucionMeses": ["Plazo", "number"], "canton": ["Canton", "string"], "parroquia": ["Parroquia", "string"], "comunidad": ["Comunidad", "string"], "departamento": ["Dirección", "string"], "fechaCreacionObra": ["Fecha", "date"], "estado": ["Estado", "string"], "valor": ["Monto", "number"]]
        if (params.obra) {
            obra = Obra.get(params.obra)
            cn.eachRow("select distinct sbpr__id from mfrb where obra__id = ${obra.id} order by sbpr__id".toString()) { d ->
                if(d.sbpr__id == 0)
                    sbprMF << ["0" : 'Todos los subpresupuestos']
                else
                    sbprMF << ["${d.sbpr__id}" : SubPresupuesto.get(d.sbpr__id).descripcion]
            }

//            def subs = VolumenesObra.findAllByObra(obra, [sort: "orden"]).subPresupuesto.unique()
            def subs = VolumenesObra.findAllByObra(obra).subPresupuesto.unique().sort{it.id}
            def volumen = VolumenesObra.findByObra(obra)
            def formula = FormulaPolinomica.findByObra(obra)

            def sqlVer = "SELECT voit__id id FROM  vlobitem WHERE obra__id= ${params.obra}"
            def verif = cn.rows(sqlVer.toString())
            def verifOK = false

            if (verif != []) {

                verifOK = true
            }

            def sqlMatriz = "select count(*) cuantos from mfcl where obra__id = ${params.obra}"
            def matriz = cn.rows(sqlMatriz.toString())[0].cuantos
            if (matriz > 0) {
                matrizOk = true
            }
            def concurso = janus.pac.Concurso.findByObra(obra)
//            println "concursos: $concurso?.fechaLimiteEntregaOfertas"
            if (concurso) {
                if (!concurso.fechaLimiteEntregaOfertas)
                    concurso = null

            }
            cn.close()

            duenoObra = esDuenoObra(obra) ? 1 : 0

            println "dueÑo: $duenoObra, concurso: $concurso"

            [campos: campos, prov: prov, obra: obra, subs: subs, persona: persona, formula: formula, volumen: volumen,
             matrizOk: matrizOk, verif: verif, verifOK: verifOK, perfil: perfil, programa: programa, tipoObra: tipoObra,
             claseObra: claseObra, grupoDir: grupo, dire  : direccion, depar: departamentos, concurso: concurso,
             personasUtfpu: personasUtfpu, duenoObra: duenoObra, sbprMF:sbprMF]
        } else {

            duenoObra = 0


            [campos: campos, prov: prov, persona: persona, matrizOk: matrizOk, perfil: perfil, programa: programa,
             tipoObra: tipoObra, claseObra: claseObra, grupoDir: grupo, dire: direccion, depar: departamentos,
             fcha: fechaPrecio, personasUtfpu: personasUtfpu, duenoObra: duenoObra, sbprMF:sbprMF]
        }
    }

    def esDuenoObra(obra) {
        return obraService.esDuenoObra(obra, session.usuario.id)

/*
        def dueno = false
        def funcionElab = Funcion.findByCodigo('E')
        def personasUtfpu = PersonaRol.findAllByFuncionAndPersonaInList(funcionElab, Persona.findAllByDepartamento(Departamento.findByCodigo('UTFPU')))
        def responsableRol = PersonaRol.findByPersonaAndFuncion(obra?.responsableObra, funcionElab)

        if (responsableRol) {
            if (obra?.responsableObra?.departamento?.direccion?.id == Persona.get(session.usuario.id).departamento?.direccion?.id) {
                dueno = true
            } else {
                dueno = personasUtfpu.contains(responsableRol) && session.usuario.departamento.codigo == 'UTFPU'
            }
        }
        dueno
*/
    }


    def generaNumeroFP() {

        println("FP:" + params)
        /*
        El sistema debe generar un número de fórmula polinómica de liquidación en el formato: FP-nnn-CEV-13-LIQ,
        para oferentes:  FP-nnn-CEV-13-OFE. Para las otras obras el formato se mantiene (FP-nnn-CEV-13).

                1. Obra normal          int obra.liquidacion = 0        FP-nnn-CEV-13
                2. Obra liquidación     int obra.liquidacion = 1        FP-nnn-CEV-13-LIQ
                3. Obra de oferentes    int obra.liquidacion = 2        FP-nnn-CEV-13-OFE
         */

        def obra = Obra.get(params.obra)
        if (!obra) {
            render "NO_No se encontró la obra"
            return
        }
        if (obra.formulaPolinomica && obra.formulaPolinomica != "") {
            render "OK_" + obra.formulaPolinomica
            return
        }
        def dpto = obra.departamento
//        println "........." + obra.id
//        println "........." + dpto
//        println "........." + dpto.documento
//        println "........." + dpto.fechaUltimoDoc
//        println "........." + dpto.codigo

        def numActual = dpto.documento
        def num = numActual ?: 0 + 1
        if (dpto.fechaUltimoDoc && dpto.fechaUltimoDoc.format("yy") != new Date().format("yy")) {
            num = 1
        }
        def numero = "FP-" + num
        if (dpto.codigo) {
            numero += "-" + dpto.codigo
        } else {
            dpto.codigo = dpto.id
            numero += "-" + dpto.codigo
        }
        numero += "-" + (new Date().format("yy"))

        if (obra.liquidacion == 1) {
            numero += "-LIQ"
        } else if (obra.liquidacion == 2) {
            numero += "-OFE"
        }
        println("numero:" + numero)
        obra.formulaPolinomica = numero
        if (obra.save(flush: true)) {
            dpto.documento = num
            if (!dpto.save(flush: true)) {
                println "Error al guardar el num de doc en del dpto: " + dpto.errors
                render "NO_" + renderErrors(bean: dpto)
                obra.formulaPolinomica = null
                obra.save(flush: true)
            } else {
                render "OK_" + numero
            }
        } else {
            println "Error al generar el numero FP: " + obra.errors
            render "NO_" + renderErrors(bean: obra)
        }
    }

    def buscarObra() {
//        println "buscar obra "+params
        def extraParr = ""
        def extraCom = ""
        def extraDep = ""
        def extraCan = ""

        if (params.campos instanceof java.lang.String) {
            if (params.criterios != "") {
                if (params.campos == "parroquia") {
                    def parrs = Parroquia.findAll("from Parroquia where nombre like '%${params.criterios.toUpperCase()}%'")
                    params.criterios = ""
                    parrs.eachWithIndex { p, i ->
                        extraParr += "" + p.id
                        if (i < parrs.size() - 1)
                            extraParr += ","
                    }
                    if (extraParr.size() < 1)
                        extraParr = "-1"
                    params.campos = ""
                    params.operadores = ""
                }
                if (params.campos == "canton") {
                    println "busca canton"
                    def cans = Canton.findAll("from Canton where nombre like '%${params.criterios.toUpperCase()}%'")
                    params.criterios = ""
                    cans.eachWithIndex { p, i ->
                        def parrs = Parroquia.findAllByCanton(p)
                        parrs.eachWithIndex { pa, k ->
                            extraCan += "" + pa.id
                            if (k < parrs.size() - 1)
                                extraCan += ","
                        }
                    }
                    if (extraCan.size() < 1)
                        extraCan = "-1"
//                    println "extra can "+extraCan
                    params.campos = ""
                    params.operadores = ""
                }
                if (params.campos == "comunidad") {
                    def coms = Comunidad.findAll("from Comunidad where nombre like '%${params.criterios.toUpperCase()}%'")
                    params.criterios = ""
                    coms.eachWithIndex { p, i ->
                        extraCom += "" + p.id
                        if (i < coms.size() - 1)
                            extraCom += ","
                    }
                    if (extraCom.size() < 1)
                        extraCom = "-1"
                    params.campos = ""
                    params.operadores = ""
                }
                if (params.campos == "departamento") {
                    def dirs = Direccion.findAll("from Direccion where nombre like '%${params.criterios.toUpperCase()}%'")
                    def deps = Departamento.findAllByDireccionInList(dirs)
                    params.criterios = ""
                    deps.eachWithIndex { p, i ->
                        extraDep += "" + p.id
                        if (i < deps.size() - 1)
                            extraDep += ","
                    }
                    if (extraDep.size() < 1)
                        extraDep = "-1"
                    params.campos = ""
                    params.operadores = ""
                }
            }

        } else {
            def remove = []
            params.campos.eachWithIndex { p, i ->
                if (params.criterios[i] != "") {
                    if (p == "comunidad") {
                        def coms = Comunidad.findAll("from Comunidad where nombre like '%${params.criterios[i].toUpperCase()}%'")

                        coms.eachWithIndex { c, j ->
                            extraCom += "" + c.id
                            if (j < coms.size() - 1)
                                extraCom += ","
                        }
                        if (extraCom.size() < 1)
                            extraCom = "-1"
                        remove.add(i)
                    }
                    if (p == "canton") {
                        def cans = Canton.findAll("from Canton where nombre like '%${params.criterios[i].toUpperCase()}%'")

                        cans.eachWithIndex { c, j ->
                            def parrs = Parroquia.findAllByCanton(c)
                            parrs.eachWithIndex { pa, k ->
                                extraCan += "" + pa.id
                                if (k < parrs.size() - 1)
                                    extraCan += ","
                            }
                        }
                        if (extraCan.size() < 1)
                            extraCan = "-1"
                        remove.add(i)
                    }
                    if (p == "parroquia") {
                        def parrs = Parroquia.findAll("from Parroquia where nombre like '%${params.criterios[i].toUpperCase()}%'")

                        parrs.eachWithIndex { c, j ->
                            extraParr += "" + c.id
                            if (j < parrs.size() - 1)
                                extraParr += ","
                        }
                        if (extraParr.size() < 1)
                            extraParr = "-1"
                        remove.add(i)
                    }
                    if (p == "departamento") {
                        def dirs = Direccion.findAll("from Direccion where nombre like '%${params.criterios.toUpperCase()}%'")
                        def deps = Departamento.findAllByDireccionInList(dirs)

                        deps.eachWithIndex { c, j ->
                            extraDep += "" + c.id
                            if (j < deps.size() - 1)
                                extraDep += ","
                        }
                        if (extraDep.size() < 1)
                            extraDep = "-1"
                        remove.add(i)
                    }
                }

            }
            remove.each {
                params.criterios[it] = null
                params.campos[it] = null
                params.operadores[it] = null
            }
        }


        def extras = " and liquidacion=0"
        if (extraParr.size() > 0)
            extras += " and parroquia in (${extraParr})"
        if (extraCan.size() > 0)
            extras += " and parroquia in (${extraCan})"
        if (extraCom.size() > 0)
            extras += " and comunidad in (${extraCom})"
        if (extraDep.size() > 0)
            extras += " and departamento in (${extraDep})"

//        println "extas "+extras
        def parr = { p ->
            return p.parroquia?.nombre
        }
        def comu = { c ->
            return c.comunidad?.nombre
        }
        def listaTitulos = ["CODIGO", "NOMBRE", "DESCRIPCION", "DIRECCION", "FECHA REG.", "M. INGRESO", "M. SALIDA", "SITIO", "PLAZO", "PARROQUIA", "COMUNIDAD", "INSPECTOR", "REVISOR", "RESPONSABLE", "ESTADO", "MONTO"]
        def listaCampos = ["codigo", "nombre", "descripcion", "departamento", "fechaCreacionObra", "oficioIngreso", "oficioSalida", "sitio", "plazoEjecucionMeses", "parroquia", "comunidad", "inspector", "revisor", "responsableObra", "estado", "valor"]
        def funciones = [null, null, null, null, ["format": ["dd/MM/yyyy hh:mm"]], null, null, null, null, ["closure": [parr, "&"]], ["closure": [comu, "&"]], null, null, null, null]
        def url = g.createLink(action: "buscarObra", controller: "obra")
        def funcionJs = "function(){"
        funcionJs += '$("#modal-busqueda").modal("hide");'
        funcionJs += 'location.href="' + g.createLink(action: 'registroObra', controller: 'obra') + '?obra="+$(this).attr("regId");'
        funcionJs += '}'
        def numRegistros = 20
        println "params " + params.reporte + "  " + params.excel

        if (!params.reporte) {
            if (params.excel) {
                session.dominio = Obra
                session.funciones = funciones
                def anchos = [15, 50, 70, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 10]
                /*anchos para el set column view en excel (no son porcentajes)*/
                redirect(controller: "reportes", action: "reporteBuscadorExcel", params: [listaCampos: listaCampos, listaTitulos: listaTitulos, tabla: "Obra", orden: params.orden, ordenado: params.ordenado, criterios: params.criterios, operadores: params.operadores, campos: params.campos, titulo: "Obras", anchos: anchos, extras: extras, landscape: true])
            } else {
                def lista = buscadorService.buscar(Obra, "Obra", "excluyente", params, true, extras)
                /* Dominio, nombre del dominio , excluyente o incluyente ,params tal cual llegan de la interfaz del buscador, ignore case */
                lista.pop()
                render(view: '../tablaBuscador', model: [listaTitulos: listaTitulos, listaCampos: listaCampos, lista: lista, funciones: funciones, url: url, controller: "obra", numRegistros: numRegistros, funcionJs: funcionJs, width: 1800, paginas: 12])
            }

        } else {
//            println "entro reporte"
            /*De esto solo cambiar el dominio, el parametro tabla, el paramtero titulo y el tamaño de las columnas (anchos)*/
            session.dominio = Obra
            session.funciones = funciones
            def anchos = [7, 10, 7, 7, 7, 7, 7, 7, 4, 7, 7, 7, 7, 7, 7, 7]
            /*el ancho de las columnas en porcentajes... solo enteros*/
            redirect(controller: "reportes", action: "reporteBuscador", params: [listaCampos: listaCampos, listaTitulos: listaTitulos, tabla: "Obra", orden: params.orden, ordenado: params.ordenado, criterios: params.criterios, operadores: params.operadores, campos: params.campos, titulo: "Lista de obras", anchos: anchos, extras: extras, landscape: true])
        }
    }

    def buscarObraLq() {
        println "buscar obra LQ"
        def extraParr = ""
        def extraCom = ""
        if (params.campos instanceof java.lang.String) {
            if (params.campos == "parroquia") {
                def parrs = Parroquia.findAll("from Parroquia where nombre like '%${params.criterios.toUpperCase()}%'")
                params.criterios = ""
                parrs.eachWithIndex { p, i ->
                    extraParr += "" + p.id
                    if (i < parrs.size() - 1)
                        extraParr += ","
                }
                if (extraParr.size() < 1)
                    extraParr = "-1"
                params.campos = ""
                params.operadores = ""
            }
            if (params.campos == "comunidad") {
                def coms = Comunidad.findAll("from Comunidad where nombre like '%${params.criterios.toUpperCase()}%'")
                params.criterios = ""
                coms.eachWithIndex { p, i ->
                    extraCom += "" + p.id
                    if (i < coms.size() - 1)
                        extraCom += ","
                }
                if (extraCom.size() < 1)
                    extraCom = "-1"
                params.campos = ""
                params.operadores = ""
            }
        } else {
            def remove = []
            params.campos.eachWithIndex { p, i ->
                if (p == "comunidad") {
                    def coms = Comunidad.findAll("from Comunidad where nombre like '%${params.criterios[i].toUpperCase()}%'")

                    coms.eachWithIndex { c, j ->
                        extraCom += "" + c.id
                        if (j < coms.size() - 1)
                            extraCom += ","
                    }
                    if (extraCom.size() < 1)
                        extraCom = "-1"
                    remove.add(i)
                }
                if (p == "parroquia") {
                    def parrs = Parroquia.findAll("from Parroquia where nombre like '%${params.criterios[i].toUpperCase()}%'")

                    parrs.eachWithIndex { c, j ->
                        extraParr += "" + c.id
                        if (j < parrs.size() - 1)
                            extraParr += ","
                    }
                    if (extraParr.size() < 1)
                        extraParr = "-1"
                    remove.add(i)
                }
            }
            remove.each {
                params.criterios[it] = null
                params.campos[it] = null
                params.operadores[it] = null
            }
        }


        def extras = " and liquidacion=1"
        if (extraParr.size() > 1)
            extras += " and parroquia in (${extraParr})"
        if (extraCom.size() > 1)
            extras += " and comunidad in (${extraCom})"

        def parr = { p ->
            return p.parroquia?.nombre
        }
        def comu = { c ->
            return c.comunidad?.nombre
        }
        def listaTitulos = ["CODIGO", "NOMBRE", "DESCRIPCION", "FECHA REG.", "M. INGRESO", "M. SALIDA", "SITIO", "PLAZO", "PARROQUIA", "COMUNIDAD", "INSPECTOR", "REVISOR", "RESPONSABLE", "ESTADO"]
        def listaCampos = ["codigo", "nombre", "descripcion", "fechaCreacionObra", "oficioIngreso", "oficioSalida", "sitio", "plazo", "parroquia", "comunidad", "inspector", "revisor", "responsableObra", "estado"]
        def funciones = [null, null, null, ["format": ["dd/MM/yyyy hh:mm"]], null, null, null, null, ["closure": [parr, "&"]], ["closure": [comu, "&"]], null, null, null, null]
        def url = g.createLink(action: "buscarObraLq", controller: "obra")
        def funcionJs = "function(){"
        funcionJs += '$("#modal-busqueda").modal("hide");'
        funcionJs += 'location.href="' + g.createLink(action: 'registroObra', controller: 'obra') + '?obra="+$(this).attr("regId");'
        funcionJs += '}'
        def numRegistros = 20
        println "params " + params.reporte + "  " + params.excel

        if (!params.reporte) {
            if (params.excel) {
                session.dominio = Obra
                session.funciones = funciones
                def anchos = [15, 50, 70, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20]
                /*anchos para el set column view en excel (no son porcentajes)*/
                redirect(controller: "reportes", action: "reporteBuscadorExcel", params: [listaCampos: listaCampos, listaTitulos: listaTitulos, tabla: "Obra", orden: params.orden, ordenado: params.ordenado, criterios: params.criterios, operadores: params.operadores, campos: params.campos, titulo: "Obras", anchos: anchos, extras: extras, landscape: true])
            } else {
                def lista = buscadorService.buscar(Obra, "Obra", "excluyente", params, true, extras)
                /* Dominio, nombre del dominio , excluyente o incluyente ,params tal cual llegan de la interfaz del buscador, ignore case */
                lista.pop()
                render(view: '../tablaBuscador', model: [listaTitulos: listaTitulos, listaCampos: listaCampos, lista: lista, funciones: funciones, url: url, controller: "obra", numRegistros: numRegistros, funcionJs: funcionJs, width: 1800, paginas: 12])
            }

        } else {
//            println "entro reporte"
            /*De esto solo cambiar el dominio, el parametro tabla, el paramtero titulo y el tamaño de las columnas (anchos)*/
            session.dominio = Obra
            session.funciones = funciones
            def anchos = [7, 10, 7, 7, 7, 7, 7, 4, 7, 7, 7, 7, 7, 7]
            /*el ancho de las columnas en porcentajes... solo enteros*/
            redirect(controller: "reportes", action: "reporteBuscador", params: [listaCampos: listaCampos, listaTitulos: listaTitulos, tabla: "Obra", orden: params.orden, ordenado: params.ordenado, criterios: params.criterios, operadores: params.operadores, campos: params.campos, titulo: "Obras", anchos: anchos, extras: extras, landscape: true])
        }
    }


    def mapaObra() {
        def obra = Obra.get(params.id)
        def persona = Persona.get(session.usuario.id)

        def coordsParts = obra.coordenadas.split(" ")
        def lat, lng

        lat = (coordsParts[0] == 'N' ? 1 : -1) * (coordsParts[1].toInteger() + (coordsParts[2].toDouble() / 60))
        lng = (coordsParts[3] == 'N' ? 1 : -1) * (coordsParts[4].toInteger() + (coordsParts[5].toDouble() / 60))

        def duenoObra = 0

        duenoObra = esDuenoObra(obra) ? 1 : 0

        return [obra: obra, lat: lat, lng: lng, duenoObra: duenoObra, persona: persona]
    }

    def saveCoords() {
        def obra = Obra.get(params.id)
        obra.coordenadas = params.coords
        if (obra.save(flush: true)) {
            render "OK"
        } else {
            println "ERROR al guardar las coordenadas de la obra desde mapa"
            println obra.errors
            render "NO"
        }
    }


    def getPersonas() {

//        println(params)

        def obra = Obra.get(params.obra)
        def usuario = session.usuario.id
        def persona = Persona.get(usuario)
        def rolUsuario = PersonaRol.findByPersona(persona)
        //old
        def departamento = Departamento.get(params.id)
        def personas = Persona.findAllByDepartamento(departamento)

        //nuevo

//        def direccion = Direccion.get(params.id)
//
//        def departamentos = Departamento.findAllByDireccion(direccion)
//
//        def personas = Persona.findAllByDepartamentoInList(departamentos, [sort: 'nombre'])

//        println("pers" + personas)

//        def funcionInsp = Funcion.get(3)
//        def funcionRevi = Funcion.get(5)
//        def funcionResp = Funcion.get(1)

        def funcionInsp = Funcion.findByCodigo('I')
        def funcionRevi = Funcion.findByCodigo('R')
        def funcionResp = Funcion.findByCodigo('S')



        def personasRolInsp = PersonaRol.findAllByFuncionAndPersonaInList(funcionInsp, personas)
        def personasRolRevi = PersonaRol.findAllByFuncionAndPersonaInList(funcionRevi, personas)
        def personasRolResp = PersonaRol.findAllByFuncionAndPersonaInList(funcionResp, personas)

//        println("---->>" + personasRolResp)
//        println("---->>" + personas)

        println(personasRolInsp)
        println(personasRolRevi)
        println(personasRolResp)
//
//        println(personasRolInsp.persona)
//        println(personasRolRevi.persona)
//        println(personasRolResp.persona)

//        println(personas)

        return [personas: personas, personasRolInsp: personasRolInsp.persona, personasRolRevi: personasRolRevi.persona, personasRolResp: personasRolResp.persona, obra: obra, persona: persona]
    }


    def getPersonas2() {

//        println("--->" + params)

        def obra = Obra.get(params.obra)
        def usuario = session.usuario.id
        def persona = Persona.get(usuario)


        def rolUsuario = PersonaRol.findByPersona(persona)

        def direccion = Direccion.get(params.id)

        def departamentos = Departamento.findAllByDireccion(direccion)

//        def departamentos = Departamento.get(params.idDep)

//        println("depar " + departamentos)

        def personas = Persona.findAllByDepartamentoInList(departamentos, [sort: 'nombre'])

//        def personas = Persona.findAllByDepartamento(departamentos)

//        def personas = Persona.findAllByDepartamento(Departamento.get(params.idDep))

//        println("personas " + personas)

        def funcionInsp = Funcion.findByCodigo('I')
        def funcionRevi = Funcion.findByCodigo('R')
        def funcionResp = Funcion.findByCodigo('S')
        def funcionElab = Funcion.findByCodigo('E')

        def personasRolInsp = PersonaRol.findAllByFuncionAndPersonaInList(funcionInsp, personas)
        def personasRolRevi = PersonaRol.findAllByFuncionAndPersonaInList(funcionRevi, personas)
        def personasRolResp = PersonaRol.findAllByFuncionAndPersonaInList(funcionResp, personas)
        def personasRolElab = PersonaRol.findAllByFuncionAndPersonaInList(funcionElab, personas)

        def personasUtfpu1 = Persona.findAllByDepartamento(Departamento.findByCodigo('UTFPU'))

        def personasUtfpu = PersonaRol.findAllByFuncionAndPersonaInList(funcionElab, personasUtfpu1)


        def responsableObra
        def duenoObra = 0

//        println("---->>" + personasRolResp)
//        println("---->>" + personas)

//        println(personasRolInsp)
//        println(personasRolRevi)
//        println(personasRolResp)
//        println(personasRolElab)
////
//        println(personasRolInsp.persona)
//        println(personasRolRevi.persona)
//        println(personasRolResp.persona)

//        println(personas)


        if (obra) {
            responsableObra = obra?.responsableObra

            def responsableRol = PersonaRol.findByPersonaAndFuncion(responsableObra, funcionElab)

            if (responsableRol) {
                personasUtfpu.each {
                    if (it.id == responsableRol.id) {
                        duenoObra = 1
                    } else {

                    }
                }
            } else {

            }


        } else {

//            responsableObra = obra?.responsableObra
//
//            personasUtfpu.each{
//                if(it.id == responsableObra){
//                    duenoObra = 1
//                }else {
//
//                }
//            }


            duenoObra = 0

        }




        return [personas       : personas, personasRolInsp: personasRolInsp.persona, personasRolRevi: personasRolRevi.persona,
                personasRolResp: personasRolResp.persona, personasRolElab: personasRolElab.persona, obra: obra, persona: persona, personasUtfpu: personasUtfpu.persona, duenoObra: duenoObra]
    }

    def getSalida() {

//        println("params:" + params)

        def direccion = Direccion.get(params.direccion)
        def departamentos = Departamento.findAllByDireccion(direccion)
        def obra = Obra.get(params.obra)

        return [dire: direccion, depar: departamentos, obra: obra]
    }

    def situacionGeografica() {
//        println "situacionGeografica" + params
        def comunidades
        def orden;
        def colorProv, colorCant, colorParr, colorComn;
        def select = "select provnmbr, cntnnmbr, parrnmbr, cmndnmbr, prov.prov__id, cntn.cntn__id, " +
                "parr.parr__id, cmnd.cmnd__id from prov, cntn, parr, cmnd"
        def txwh = "where cntn.prov__id = prov.prov__id and parr.cntn__id = cntn.cntn__id and cmnd.parr__id = parr.parr__id"
        def campos = ['provnmbr', 'cntnnmbr', 'parrnmbr', 'cmndnmbr']
        def cmpo = params.buscarPor.toInteger()
        def sqlTx = ""

        if (params.ordenar == '1') {
            orden = "asc";
        } else {
            orden = "desc";
        }

        txwh += " and ${campos[cmpo - 1]} ilike '%${params.criterio}%'"

        sqlTx = "${select} ${txwh} order by ${campos[cmpo - 1]} ".toString()

        def cn = dbConnectionService.getConnection()
        comunidades = cn.rows(sqlTx)
        [comunidades: comunidades, colorComn: colorComn, colorProv: colorProv, colorParr: colorParr, colorCant: colorCant]

    }

    def form_ajax() {
        def obraInstance = new Obra(params)
        if (params.id) {
            obraInstance = Obra.get(params.id)
            if (!obraInstance) {
                flash.clase = "alert-error"
                flash.message = "No se encontró Obra con id " + params.id
                redirect(action: "list")
                return
            } //no existe el objeto
        } //es edit
        return [obraInstance: obraInstance]
    } //form_ajax

    def save() {

//        println "save " + params

        def usuario = session.usuario.id

        def persona = Persona.get(usuario)

//        def dpto = persona.departamento
//        def numero = null

//        println("usuario" + usuario)
//        println("dep" + persona.departamento.id)

        params.oficioIngreso = params.oficioIngreso.toUpperCase()
        params.memoCantidadObra = params.memoCantidadObra.toUpperCase()
        params.oficioSalida = params.oficioSalida.toUpperCase()
        params.memoSalida = params.memoSalida.toUpperCase()
        params.codigo = params.codigo.toUpperCase()

        if (params.anchoVia) {
            params.anchoVia = params.anchoVia.toDouble()

        } else {

            params.anchoVia = 0
        }

        if (params.longitudVia) {

            params.longitudVia = params.longitudVia.replaceAll(",", "").toDouble()
        } else {

            params.longitudVia = 0

        }

        if (params.formulaPolinomica) {
            params.formulaPolinomica = params.formulaPolinomica.toUpperCase()
        }


        if (params.fechaOficioSalida) {
            params.fechaOficioSalida = new Date().parse("dd-MM-yyyy", params.fechaOficioSalida)
        }

        if (params.fechaPreciosRubros) {
            params.fechaPreciosRubros = new Date().parse("dd-MM-yyyy", params.fechaPreciosRubros)
        }

        if (params.fechaCreacionObra) {
            params.fechaCreacionObra = new Date().parse("dd-MM-yyyy", params.fechaCreacionObra)
        }


        if (params.id) {
            if (session.perfil.codigo == 'ADDI' || session.perfil.codigo == 'COGS') {
                params.departamento = Departamento.get(params.per.id)
            } else {
                params.departamento = Departamento.get(params.departamento.id)
            }

        }
        params."departamento.id" = params.departamento.id

//        println("depto" + params.departamento.id)
//        println("depto aaaa " + params."departamento.id")

        def obraInstance


        if (params.id) {

            obraInstance = Obra.get(params.id)

            if (!obraInstance) {
                flash.clase = "alert-error"
                flash.message = "No se encontró Obra con id " + params.id
                redirect(action: 'registroObra')
                return
            }//no existe el objeto

            def oriM = obraInstance.plazoEjecucionMeses
            def oriD = obraInstance.plazoEjecucionDias

            def valM = params.plazoEjecucionMeses
            def valD = params.plazoEjecucionDias

            if ((params.crono == "1" || params.crono == 1) && (oriM.toDouble() != valM.toDouble() || oriD.toDouble() != valD.toDouble())) {
                //Elimina el cronograma
//                println "Elimina el cronograma"
                VolumenesObra.findAllByObra(obraInstance, [sort: "orden"]).each { vol ->
                    Cronograma.findAllByVolumenObra(vol).each { crono ->
                        crono.delete()
                    }
                }
            }

//            println("-->" +params.departamento.id)

            obraInstance.properties = params

            obraInstance.departamento = params.departamento

//            println("-->" +params.departamento.id)

        }//es edit
        else {
            if(!Obra.findByCodigo(params.codigo)){

                obraInstance = new Obra(params)

                def departamento

                if (session.perfil.codigo == 'ADDI' || session.perfil.codigo == 'COGS') {
                    departamento = Departamento.get(persona?.departamento?.id)
                } else {
                    departamento = Departamento.get(params.departamento.id)
                }

                obraInstance.departamento = departamento

                def par = Parametros.list()
                if (par.size() > 0)
                    par = par.pop()

                obraInstance.indiceCostosIndirectosObra = par.indiceCostosIndirectosObra
                obraInstance.indiceCostosIndirectosPromocion = par.indiceCostosIndirectosPromocion
                obraInstance.indiceCostosIndirectosMantenimiento = par.indiceCostosIndirectosMantenimiento
                obraInstance.administracion = par.administracion
                obraInstance.indiceCostosIndirectosGarantias = par.indiceCostosIndirectosGarantias
                obraInstance.indiceCostosIndirectosCostosFinancieros = par.indiceCostosIndirectosCostosFinancieros
                obraInstance.indiceCostosIndirectosVehiculos = par.indiceCostosIndirectosVehiculos

                obraInstance.impreso = par.impreso
                obraInstance.indiceUtilidad = par.indiceUtilidad
                obraInstance.indiceCostosIndirectosTimbresProvinciales = par.indiceCostosIndirectosTimbresProvinciales

                obraInstance.indiceAlquiler = par.indiceAlquiler
                obraInstance.indiceProfesionales = par.indiceProfesionales
                obraInstance.indiceSeguros = par.indiceSeguros
                obraInstance.indiceSeguridad = par.indiceSeguridad
                obraInstance.indiceCampo = par.indiceCampo
                obraInstance.indiceCampamento = par.indiceCampamento

                /** variables por defecto para las nuevas obras **/
                obraInstance.lugar = Lugar.findAll('from Lugar  where tipoLista=1')[0]
                obraInstance.listaVolumen0 = Lugar.findAll('from Lugar  where tipoLista=3')[0]
                obraInstance.distanciaPeso = 10
                obraInstance.distanciaVolumen = 30


//                obraInstance.indiceGastosGenerales = (obraInstance.indiceCostosIndirectosObra + obraInstance.indiceCostosIndirectosPromocion + obraInstance.indiceCostosIndirectosMantenimiento +
//                        obraInstance.administracion + obraInstance.indiceCostosIndirectosGarantias + obraInstance.indiceCostosIndirectosCostosFinancieros + obraInstance.indiceCostosIndirectosVehiculos)


                obraInstance.indiceGastosGenerales = (obraInstance?.indiceAlquiler + obraInstance?.administracion + obraInstance?.indiceProfesionales + obraInstance?.indiceCostosIndirectosMantenimiento + obraInstance?.indiceSeguros + obraInstance?.indiceSeguridad)

                obraInstance.indiceGastoObra = (obraInstance?.indiceCampo + obraInstance?.indiceCostosIndirectosCostosFinancieros + obraInstance?.indiceCostosIndirectosGarantias + obraInstance?.indiceCampamento)

//                obraInstance.totales = (obraInstance.impreso + obraInstance.indiceUtilidad + obraInstance.indiceCostosIndirectosTimbresProvinciales + obraInstance.indiceGastosGenerales)
                obraInstance.totales = (obraInstance.impreso + obraInstance.indiceUtilidad + obraInstance.indiceGastoObra + obraInstance.indiceGastosGenerales)

                /* si pefiles administración directa o cogestion pone obratipo = 'D' */
                if (session.perfil.codigo == 'ADDI' || session.perfil.codigo == 'COGS') {
                    obraInstance.tipo = 'D'
                }
            }else {

//                println("entro codigo no")

                flash.clase = "alert-error"
                flash.message = " No se pudo guardar la obra,  código duplicado!"
                redirect(action: 'registroObra')
                return

            }



        } //es create

        obraInstance.estado = "N"
//        obraInstance.departamento.id = params.departamento.id

        if (!obraInstance.save(flush: true)) {

//            println("--->>>>>>>>>>>>>>>>>>>")
            flash.clase = "alert-error"
            def str = "<h4>No se pudo guardar Obra " + (obraInstance.id ? obraInstance.id : "") + "</h4>"

            str += "<ul>"
            obraInstance.errors.allErrors.each { err ->
                def msg = err.defaultMessage
                err.arguments.eachWithIndex { arg, i ->
                    msg = msg.replaceAll("\\{" + i + "}", arg.toString())
                }
                str += "<li>" + msg + "</li>"
            }
            str += "</ul>"

            flash.message = str
            redirect(action: 'registroObra')
            return
        } else {
//            println("entro")
        }

        if (params.id) {
            flash.clase = "alert-success"
            flash.message = "Se ha actualizado correctamente Obra "
        } else {
            flash.clase = "alert-success"
            flash.message = "Se ha creado correctamente Obra "
        }
        redirect(action: 'registroObra', params: [obra: obraInstance.id])
    } //save

    //guardar copia
    def saveCopia() {
        if (params.fechaOficioSalida) {
            params.fechaOficioSalida = new Date().parse("dd-MM-yyyy", params.fechaOficioSalida)
        }

        if (params.fechaPreciosRubros) {
            params.fechaPreciosRubros = new Date().parse("dd-MM-yyyy", params.fechaPreciosRubros)
        }

        if (params.fechaCreacionObra) {
            params.fechaCreacionObra = new Date().parse("dd-MM-yyyy", params.fechaCreacionObra)
        }

        def obraInstance
        def volumenInstance
      //  def copiaObra
        def obra = Obra.get(params.id);
        def nuevoCodigo = params.nuevoCodigo.toUpperCase()
        def volumenes = VolumenesObra.findAllByObra(obra);

        obraInstance = Obra.get(params.id)

        def revisarCodigo = Obra.findByCodigo(nuevoCodigo)

        if (revisarCodigo != null) {
//            println("entro1")
            render "NO_No se puede copiar la Obra " + " " + obra.nombre + " " + "porque posee un codigo ya existente."
            return

        } else {
//            println("entro2")
            obraInstance = new Obra()
            obraInstance.properties = obra.properties
            obraInstance.codigo = nuevoCodigo
            obraInstance.estado = 'N'
            obraInstance.departamento = session.usuario.departamento
            obraInstance.memoSif = null
            obraInstance.fechaInicio = null
            obraInstance.fechaFin = null

//            println "busca direccion de usuario ${session.usuario}"

            def persona = Persona.get(session.usuario.id)
            if(session.usuario.departamento?.codigo != 'UTFPU'){
                def direccion = Direccion.get(persona.departamento.direccion.id)
                def departamentos = Departamento.findAllByDireccion(direccion)
                def personas = Persona.findAllByDepartamentoInList(departamentos, [sort: 'nombre'])
                def funcionInsp = Funcion.findByCodigo('I')
                def funcionRevi = Funcion.findByCodigo('R')
                def funcionElab = Funcion.findByCodigo('E')
                def personasRolInsp = PersonaRol.findAllByFuncionAndPersonaInList(funcionInsp, personas)
                def personasRolRevi = PersonaRol.findAllByFuncionAndPersonaInList(funcionRevi, personas)
                def personasRolElab = PersonaRol.findAllByFuncionAndPersonaInList(funcionElab, personas)

                obraInstance.inspector = personasRolInsp.first().persona
                obraInstance.revisor = personasRolRevi.first().persona
                obraInstance.responsableObra = personasRolElab.first().persona
            } else {
                obraInstance.responsableObra = persona   // cambia de dueño al usuario que copia de la UTFPU
            }



            if (!obraInstance.save(flush: true)) {
                flash.clase = "alert-error"
                def str = "<h4>No se pudo copiar la Obra " + (obraInstance.id ? obraInstance.id : "") + "</h4>"

                str += "<ul>"
                obraInstance.errors.allErrors.each { err ->
                    def msg = err.defaultMessage
                    err.arguments.eachWithIndex { arg, i ->
                        msg = msg.replaceAll("\\{" + i + "}", arg.toString())
                    }
                    str += "<li>" + msg + "</li>"
                }
                str += "</ul>"

                render 'NO_' + str
//            return(action: 'registroObra')
                return
            }
            volumenes.each { volOr ->
                volumenInstance = new VolumenesObra()
//                println("VO:" + volOr)
                volumenInstance.properties = volOr.properties
//                println("VI:" + volumenInstance)
                volumenInstance.obra = obraInstance
                volumenInstance.save(flush: true)
            }
            render 'OK_' + "Obra copiada"
        }

    } //save


    def show_ajax() {
        def obraInstance = Obra.get(params.id)
        if (!obraInstance) {
            flash.clase = "alert-error"
            flash.message = "No se encontró Obra con id " + params.id
            redirect(action: "list")
            return
        }
        [obraInstance: obraInstance]
    } //show

    def crearTipoObra() {

//        println(params)

        def grupo = params.grupo

        def tipoObraInstance = new TipoObra(params)
        if (params.id) {
            tipoObraInstance = TipoObra.get(params.id)
            if (!tipoObraInstance) {
                flash.clase = "alert-error"
                flash.message = "No se encontró Tipo Obra con id " + params.id
                redirect(action: "list")
                return
            }
        }
        return [tipoObraInstance: tipoObraInstance, grupo: grupo]
    }


    def delete() {

//        println("delete:" + params.id)

        def obraInstance = Obra.get(params.id)
        if (!obraInstance) {
            flash.clase = "alert-error"
            flash.message = "No se encontró Obra con id " + params.id
            render("no")
            return
        }

        try {
            obraInstance.delete(flush: true)
            flash.clase = "alert-success"
            flash.message = "Se ha eliminado correctamente Obra " + obraInstance.nombre
            render("ok")
        }
        catch (DataIntegrityViolationException e) {
            flash.clase = "alert-error"
            flash.message = "No se pudo eliminar Obra " + (obraInstance.id ? obraInstance.id : "")
            render("no")
        }
    } //delete


    def formIva_ajax () {

    }


    def guardarIva_ajax () {

        def paux = Parametros.first()
        def nuevoIva = params.iva_name

        paux.iva = nuevoIva.toInteger()

        try{
            paux.save(flush: true)
            render "ok"
        }catch (e){
            render "no"
        }
    }

    def revisarSizeRubros_ajax () {
//        println "revisarSizeRubros_ajax $params"
        def obra = Obra.get(params.id)
        def tamano = VolumenesObra.findAllByObra(obra, [sort: 'orden']).item.unique().size()
        def tamano1 = VolumenesObra.findAllByObra(obra, [sort: 'orden']).item.unique()

        if(tamano > 100){
            render "ok"
        }else{
            render "no"
        }
    }


} //fin controller
