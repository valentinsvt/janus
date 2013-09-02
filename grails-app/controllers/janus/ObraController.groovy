package janus

import org.springframework.dao.DataIntegrityViolationException

class ObraController extends janus.seguridad.Shield {

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]
    def buscadorService
    def obraService
    def dbConnectionService

    def index() {
        redirect(action: "list", params: params)
    } //index

    def list() {
        params.max = Math.min(params.max ? params.int('max') : 10, 100)
        [obraInstanceList: Obra.list(params), obraInstanceTotal: Obra.count(), params: params]
    } //list

    def biblioteca() {

    }


    def obrasFinalizadas(){
        def campos = ["codigo": ["Código", "string"], "nombre": ["Nombre", "string"], "descripcion": ["Descripción", "string"], "oficioIngreso": ["Memo ingreso", "string"], "oficioSalida": ["Memo salida", "string"], "sitio": ["Sitio", "string"], "plazoEjecucionMeses": ["Plazo", "number"], "parroquia": ["Parroquia", "string"], "comunidad": ["Comunidad", "string"], "departamento": ["Dirección", "string"], "fechaCreacionObra": ["Fecha", "date"]]
        [campos:campos]
    }
    def buscarObraFin(){
        println "buscar obra fin"
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
        def listaTitulos = ["CODIGO", "NOMBRE", "DESCRIPCION", "DIRECCION", "FECHA REG.", "M. INGRESO", "M. SALIDA", "SITIO", "PLAZO", "PARROQUIA", "COMUNIDAD", "INSPECTOR", "REVISOR", "RESPONSABLE", "ESTADO"]
        def listaCampos = ["codigo", "nombre", "descripcion", "departamento", "fechaCreacionObra", "oficioIngreso", "oficioSalida", "sitio", "plazoEjecucionMeses", "parroquia", "comunidad", "inspector", "revisor", "responsableObra", "estado"]
        def funciones = [null, null, null, null, ["format": ["dd/MM/yyyy hh:mm"]], null, null, null, null, ["closure": [parr, "&"]], ["closure": [comu, "&"]], null, null, null, null]
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
            def anchos = [7, 10, 7, 7, 7, 7, 7, 7, 4, 7, 7, 7, 7, 7, 7]
            /*el ancho de las columnas en porcentajes... solo enteros*/
            redirect(controller: "reportes", action: "reporteBuscador", params: [listaCampos: listaCampos, listaTitulos: listaTitulos, tabla: "Obra", orden: params.orden, ordenado: params.ordenado, criterios: params.criterios, operadores: params.operadores, campos: params.campos, titulo: "Lista de obras", anchos: anchos, extras: extras, landscape: true])
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
                msg += "<br><span class='label-azul'>Error:</span> La suma de porcentajes de el volumen de obra: ${it.item.codigo} (${crono.toDouble().round(2)}) en el cronograma es diferente de 100%"
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
        if (totalP.toDouble().round(6) != 1.000) {
            render "La suma de los coeficientes de la formula polinómica (${totalP}) es diferente a 1.000"
            return
        }
        if (totalC.toDouble().round(6) != 1.000) {
            render "La suma de los coeficientes de la Cuadrilla tipo (${totalC}) es diferente a 1.000"
            return
        }


        obraService.registrarObra(obra)
        obra.estado = "R"
        obra.desgloseTransporte = null  //obliga a genrar matriz con valores históricos almacenado spor grst_obra
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
//        println "calculaPlazo: " + params
        def obra = Obra.get(params.id)

        if (!params.personas) params.personas = obra.plazoPersonas
        if (!params.maquinas) params.maquinas = obra.plazoMaquinas
        if (!params.save) params.save = "0"

        def sqlM = "select itemcdgo, itemnmbr, sum(itemcntd) itemcntd, sum(itemcntd/8) dias " +
                "from obra_comp_v2(${params.id}) where grpo__id = 2 and itemcntd > 0 group by itemcdgo, itemnmbr order by dias desc"
        def sqlR = "select itemcdgo, itemnmbr, unddcdgo, sum(rbrocntd) rbrocntd, sum(dias) dias " +
                "from plazo(${params.id},${params.personas},${params.maquinas},${params.save}) group by itemcdgo, itemnmbr, unddcdgo"
        println sqlM
        println sqlR
        def cn = dbConnectionService.getConnection()
        def resultM = cn.rows(sqlM.toString())
        def resultR = cn.rows(sqlR.toString())

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

    def registroObra() {

        def obra

        def usuario = session.usuario.id

        def perfil = session.perfil

        def persona = Persona.get(usuario)

        def matrizOk = false

        def prov = Provincia.list();
        def campos = ["codigo": ["Código", "string"], "nombre": ["Nombre", "string"], "descripcion": ["Descripción", "string"], "oficioIngreso": ["Memo ingreso", "string"], "oficioSalida": ["Memo salida", "string"], "sitio": ["Sitio", "string"], "plazoEjecucionMeses": ["Plazo", "number"], "canton": ["Canton", "string"], "parroquia": ["Parroquia", "string"], "comunidad": ["Comunidad", "string"], "departamento": ["Dirección", "string"], "fechaCreacionObra": ["Fecha", "date"],"estado":["Estado","string"],"valor":["Monto","number"]]
        if (params.obra) {
            obra = Obra.get(params.obra)

            def subs = VolumenesObra.findAllByObra(obra, [sort: "orden"]).subPresupuesto.unique()
            def volumen = VolumenesObra.findByObra(obra)

            def formula = FormulaPolinomica.findByObra(obra)

            def cn = dbConnectionService.getConnection()

            def sqlVer = "SELECT\n" +
                    "voit__id             id\n" +
                    "FROM  vlobitem \n" +
                    "WHERE obra__id= ${params.obra} \n"
            def verif = cn.rows(sqlVer.toString())
            def verifOK = false

            if (verif != []) {

                verifOK = true
            }

            def sqlMatriz = "select count(*) cuantos from mfcl where obra__id=${params.obra}"
            def matriz = cn.rows(sqlMatriz.toString())[0].cuantos
            if (matriz > 0) {
                matrizOk = true
            }
//            println matriz + "matriz ok: " + matrizOk
            [campos: campos, prov: prov, obra: obra, subs: subs, persona: persona, formula: formula, volumen: volumen, matrizOk: matrizOk, verif: verif, verifOK: verifOK, perfil: perfil]
        } else {
            [campos: campos, prov: prov, persona: persona, matrizOk: matrizOk, perfil: perfil]
        }


    }

    def buscarObra() {
        println "buscar obra"
        def extraParr = ""
        def extraCom = ""
        def extraDep = ""
        def extraCan = ""

        if (params.campos instanceof java.lang.String) {
            if(params.criterios!=""){
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
                if(params.criterios[i]!=""){
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
                    if (params.campos == "canton") {
                        def cans = Canton.findAll("from Canton where nombre like '%${params.criterios.toUpperCase()}%'")
                        params.criterios = ""
                        cans.eachWithIndex { c, j ->
                            def parrs = Parroquia.findAllByCanton(p)
                            parrs.eachWithIndex { pa, k ->
                                extraCan += "" + pa.id
                                if (k < parrs.size() - 1)
                                    extraCan += ","
                            }
                        }
                        if (extraCan.size() < 1)
                            extraCan = "-1"
                        params.campos = ""
                        params.operadores = ""
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
        def listaTitulos = ["CODIGO", "NOMBRE", "DESCRIPCION", "DIRECCION", "FECHA REG.", "M. INGRESO", "M. SALIDA", "SITIO", "PLAZO", "PARROQUIA", "COMUNIDAD", "INSPECTOR", "REVISOR", "RESPONSABLE", "ESTADO","MONTO"]
        def listaCampos = ["codigo", "nombre", "descripcion", "departamento", "fechaCreacionObra", "oficioIngreso", "oficioSalida", "sitio", "plazoEjecucionMeses", "parroquia", "comunidad", "inspector", "revisor", "responsableObra", "estado","valor"]
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
                def anchos = [15, 50, 70, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20,10]
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
            def anchos = [7, 10, 7, 7, 7, 7, 7, 7, 4, 7, 7, 7, 7, 7, 7,7]
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

        def coordsParts = obra.coordenadas.split(" ")
        def lat, lng

        lat = (coordsParts[0] == 'N' ? 1 : -1) * (coordsParts[1].toInteger() + (coordsParts[2].toDouble() / 60))
        lng = (coordsParts[3] == 'N' ? 1 : -1) * (coordsParts[4].toInteger() + (coordsParts[5].toDouble() / 60))

        return [obra: obra, lat: lat, lng: lng]
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

        def departamento = Departamento.get(params.id)

        def personas = Persona.findAllByDepartamento(departamento)

        def funcionInsp = Funcion.get(3)
        def funcionRevi = Funcion.get(4)
        def funcionResp = Funcion.get(5)

        def personasRolInsp = PersonaRol.findAllByFuncionAndPersonaInList(funcionInsp, personas)
        def personasRolRevi = PersonaRol.findAllByFuncionAndPersonaInList(funcionRevi, personas)
        def personasRolResp = PersonaRol.findAllByFuncionAndPersonaInList(funcionResp, personas)
//
//        println(personasRolInsp)
//        println(personasRolRevi)
//        println(personasRolResp)
//
//        println(personasRolInsp.persona)
//        println(personasRolRevi.persona)
//        println(personasRolResp.persona)

//        println(personas)

        return [personas: personas, personasRolInsp: personasRolInsp.persona, personasRolRevi: personasRolRevi.persona, personasRolResp: personasRolResp.persona, obra: obra, persona: persona]
    }

    def situacionGeografica() {
        def comunidades

        def orden;

        def colorProv, colorCant, colorParr, colorComn;


        if (params.ordenar == '1') {


            orden = "asc";

        } else {

            orden = "desc";

        }


        switch (params.buscarPor) {

            case "1":


                colorProv = "#00008B";

                if (params.criterio != "") {
                    comunidades = Comunidad.withCriteria {
                        parroquia {
                            canton {
                                provincia {
                                    ilike("nombre", "%" + params.criterio + "%")
                                    order("nombre", orden)
                                }
                                order("nombre", orden)
                            }
                            order("nombre", orden)
                        }
                        order("nombre", orden)
                    }
                } else {
                    comunidades = Comunidad.list(order: "nombre")


                }


                break
            case "2":

                colorCant = "#00008B";

                if (params.criterio != "") {
                    comunidades = Comunidad.withCriteria {
                        parroquia {
                            canton {

                                ilike("nombre", "%" + params.criterio + "%")
                                order("nombre", orden)

                            }
                            order("nombre", orden)
                        }
                        order("nombre", orden)
                    }
                } else {
                    comunidades = Comunidad.list(order: "nombre")
                }

                break
            case "3":


                colorParr = "#00008B";

                if (params.criterio != "") {
//                    println params
                    comunidades = Comunidad.withCriteria {
                        parroquia {
                            ilike("nombre", "%" + params.criterio + "%")
                            order("nombre", orden)
                        }
                        order("nombre", orden)
                    }
                } else {
                    comunidades = Comunidad.list()
                }

                break
            case "4":
//

                colorComn = "#00008B";

                if (params.criterio != "") {
                    comunidades = Comunidad.withCriteria {


                        ilike("nombre", "%" + params.criterio + "%")
                        order("nombre", orden)


                    }
                } else {
                    comunidades = Comunidad.list()
                }

                break

        }


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

//        println("usuario" + usuario)
//        println("dep" + persona.departamento.id)


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

            obraInstance.properties = params
        }//es edit
        else {

            obraInstance = new Obra(params)

//            def departamento = Departamento.get(params.departamento)

            obraInstance.departamento = persona.departamento

//            obraInstance.properties = params


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



            obraInstance.indiceGastosGenerales = (obraInstance.indiceCostosIndirectosObra + obraInstance.indiceCostosIndirectosPromocion + obraInstance.indiceCostosIndirectosMantenimiento +
                    obraInstance.administracion + obraInstance.indiceCostosIndirectosGarantias + obraInstance.indiceCostosIndirectosCostosFinancieros + obraInstance.indiceCostosIndirectosVehiculos)

            obraInstance.totales = (obraInstance.impreso + obraInstance.indiceUtilidad + obraInstance.indiceCostosIndirectosTimbresProvinciales + obraInstance.indiceGastosGenerales)

        } //es create
        obraInstance.estado = "N"
        if (!obraInstance.save(flush: true)) {
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

        def copiaObra

        def obra = Obra.get(params.id);

        def nuevoCodigo = params.nuevoCodigo

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
                //

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
        return [tipoObraInstance: tipoObraInstance]
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

} //fin controller
