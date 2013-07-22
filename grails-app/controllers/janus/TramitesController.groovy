package janus

import janus.ejecucion.Planilla

class TramitesController {

    def buscadorService

    def index() {}

    def registro() {
        def padre = null
        if (params.padre) {
            padre = Tramite.get(params.padre.toLong())
        }
        def campos = ["codigo": ["Código", "string"], "nombre": ["Nombre", "string"]]
        return [campos: campos, padre: padre]
    }

    def registro_ajax() {
        def padre = null
        def init = null
        def tramite = new Tramite()
        if (params.padre) {
            padre = Tramite.get(params.padre.toLong())
        }
        def campos = ["codigo": ["Código", "string"], "nombre": ["Nombre", "string"]]

        if (params.planilla) {
            def planilla = Planilla.get(params.planilla)
            tramite.tipoTramite = TipoTramite.findByCodigoIlike('PGPL');
            tramite.contrato = planilla.contrato
            init = "C"
        }

        return [campos: campos, padre: padre, tramite: tramite, init: init]
    }

    def  personasPorTipo() {

        if (params.tipo) {

            def tipo = TipoTramite.get(params.tipo.toString().toLong())
            def roles = DepartamentoTramite.findAllByTipoTramite(tipo)

            def html = ""

            roles.each { rol ->
                def personas = Persona.findAllByDepartamento(rol.departamento)

                def sel = g.select(from: personas, optionKey: "id", optionValue: { it.nombre + " " + it.apellido }, name: "persona_" + rol.rolTramite.id)

                html += '<div class="control-group">'
                html += '<div>'
                html += '<span class="control-label label label-inverse">'
                html += rol.rolTramite.descripcion
                html += '</span>'
                html += '</div>'
                html += '<div class="controls">'
                html += '<div class="span3">Dpto: '
                html += rol.departamento.descripcion
                html += '</div><div class="span3">' + sel + '</div>'
                html += '<p class="help-block ui-helper-hidden"></p>'
                html += '</div>'
                html += '</div>'
            }
            render html
        } else {
            render ""
        }
    }

    def buscaObra() {

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


        def extras = " "
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
        def listaTitulos = ["Código", "Nombre", "Descripción", "Fecha Reg.", "Sitio", "Plazo", "Parroquia", "Comunidad", "Inspector", "Revisor", "Responsable", "Estado Obra"]
        def listaCampos = ["codigo", "nombre", "descripcion", "fechaCreacionObra", "sitio", "plazo", "parroquia", "comunidad", "inspector", "revisor", "responsable", "estadoObra"]
        def funciones = [null, null, null, ["format": ["dd/MM/yyyy hh:mm"]], null, null, ["closure": [parr, "&"]], ["closure": [comu, "&"]], null, null, null, null]
        def url = g.createLink(action: "buscarObra", controller: "obra")
        def funcionJs = "function(){"
        funcionJs += '$("#modal-busqueda").modal("hide");'
        funcionJs += '$("#hddTipo").val("ob_"+$(this).attr("regId"));'
        funcionJs += '$("#txtTipo").val($(this).parent().parent().find(".props").attr("prop_nombre"));'
        funcionJs += '$("#modal-busca").modal("hide");'
        funcionJs += '}'
        def numRegistros = 20

        if (!params.reporte) {
            def lista = buscadorService.buscar(Obra, "Obra", "excluyente", params, true, extras) /* Dominio, nombre del dominio , excluyente o incluyente ,params tal cual llegan de la interfaz del buscador, ignore case */
            lista.pop()
            render(view: '../tablaBuscador', model: [listaTitulos: listaTitulos, listaCampos: listaCampos, lista: lista, funciones: funciones, url: url, controller: "llamada", numRegistros: numRegistros, funcionJs: funcionJs, width: 1800, paginas: 12])
        } else {
//            println "entro reporte"
            /*De esto solo cambiar el dominio, el parametro tabla, el paramtero titulo y el tamaño de las columnas (anchos)*/
            session.dominio = Obra
            session.funciones = funciones
            def anchos = [7, 10, 7, 7, 7, 7, 7, 4, 7, 7, 7, 7, 7, 7] /*el ancho de las columnas en porcentajes... solo enteros*/
            redirect(controller: "reportes", action: "reporteBuscador", params: [listaCampos: listaCampos, listaTitulos: listaTitulos, tabla: "Obra", orden: params.orden, ordenado: params.ordenado, criterios: params.criterios, operadores: params.operadores, campos: params.campos, titulo: "Obras", anchos: anchos, extras: extras, landscape: true])
        }
    }

    def buscaContrato() {

        def extraObra = ""
        if (params.campos instanceof java.lang.String) {
            if (params.campos == "nombre") {
                def obras = Obra.findAll("from Obra where nombre like '%${params.criterios.toUpperCase()}%'")
                params.criterios = ""
                obras.eachWithIndex { p, i ->
                    def concursos = janus.pac.Concurso.findAllByObraAndEstado(p, "R")
                    concursos.each { co ->
                        def ofertas = janus.pac.Oferta.findAllByConcurso(co)
                        ofertas.eachWithIndex { o, k ->
                            extraObra += "" + o.id
                            if (k < ofertas.size() - 1)
                                extraObra += ","
                        }

                    }

                }
                if (extraObra.size() < 1)
                    extraObra = "-1"
                params.campos = ""
                params.operadores = ""
            }
        } else {
            def remove = []
            params.campos.eachWithIndex { p, i ->
                if (p == "nombre") {
                    def obras = Obra.findAll("from Obra where nombre like '%${params.criterios[i].toUpperCase()}%'")

                    obras.eachWithIndex { ob, j ->
                        def concursos = janus.pac.Concurso.findAllByObraAndEstado(ob, "R")
                        concursos.each { co ->
                            def ofertas = janus.pac.Oferta.findAllByConcurso(co)
                            ofertas.eachWithIndex { o, k ->
                                extraObra += "" + o.id
                                if (k < ofertas.size() - 1)
                                    extraObra += ","
                                remove.add(i)
                            }

                        }

                    }
                    if (extraObra.size() < 1)
                        extraObra = "-1"

                }
            }
            remove.each {
                params.criterios[it] = null
                params.campos[it] = null
                params.operadores[it] = null
            }
        }

//        println "extra obra "+extraObra

        def codObra = { contrato ->
            return contrato?.oferta?.concurso?.obra?.codigo
        }
        def provObra = { contrato ->
            return contrato?.oferta?.proveedor?.nombre
        }
        def plazObra = { contrato ->
            return contrato?.oferta?.concurso?.obra?.plazo
        }
        def nombreObra = { contrato ->
            return contrato?.oferta?.concurso?.obra?.nombre
        }

        def listaTitulos = ["N. Contrato", "Nombre Obra", "Código Obra", "Monto", "% Anticipo", "Anticipo", "Contratista", "Fecha contrato", "Plazo"]
        def listaCampos = ["codigo", "obra", "codigoObra", "monto", "porcentajeAnticipo", "anticipo", "proveedorObra", "fechaInicio", "plazo"]
        def funciones = [null, ["closure": [nombreObra, "&"]], ["closure": [codObra, "&"]], null, null, null, ["closure": [provObra, "&"]], ["format": ["dd/MM/yyyy hh:mm"]], null]
        def url = g.createLink(action: "buscarContrato", controller: "contrato")
        def funcionJs = "function(){"
        funcionJs += '$("#modal-busqueda").modal("hide");'
        funcionJs += '$("#hddTipo").val("cn_"+$(this).attr("regId"));'
        funcionJs += '$("#txtTipo").val($(this).attr("prop_obra"));'
        funcionJs += '$("#modal-busca").modal("hide");'
        funcionJs += '}'
        def numRegistros = 20
        def extras = " "
        if (extraObra.size() > 0)
            extras += " and oferta in (${extraObra})"
//        println "extras "+extras

        if (!params.reporte) {
            def lista = buscadorService.buscar(Contrato, "Contrato", "excluyente", params, true, extras) /* Dominio, nombre del dominio , excluyente o incluyente ,params tal cual llegan de la interfaz del buscador, ignore case */

            lista.pop()

            render(view: '../tablaBuscadorColDer', model: [listaTitulos: listaTitulos, listaCampos: listaCampos, lista: lista, funciones: funciones, url: url, controller: "llamada", numRegistros: numRegistros, funcionJs: funcionJs])
        } else {
//            println "entro reporte"
            /*De esto solo cambiar el dominio, el parametro tabla, el paramtero titulo y el tamaño de las columnas (anchos)*/
            session.dominio = Contrato
            session.funciones = funciones
            def anchos = [10, 20, 10, 10, 5, 10, 20, 10, 5] /*el ancho de las columnas en porcentajes... solo enteros*/
            redirect(controller: "reportes", action: "reporteBuscador", params: [listaCampos: listaCampos, listaTitulos: listaTitulos, tabla: "Contrato", orden: params.orden, ordenado: params.ordenado, criterios: params.criterios, operadores: params.operadores, campos: params.campos, titulo: "Contratos", anchos: anchos, extras: extras, landscape: true])
        }
    }


    def registrar() {
//        println params

        if (params.fecha) {
            params.fecha = new Date().parse("dd-MM-yyyy", params.fecha)
        }

        def tipo = params.hddTipo
        def p = tipo.split("_")
//        println ">>" + p
        if (p[0] == "ob") {
            params.obra = Obra.get(p[1].toLong())
        } else if (p[0] == "cn") {
            params.contrato = Contrato.get(p[1].toLong())
        }
//        println params
        def errores = ""

        params.estado = EstadoTramite.findByCodigo("C")

        def tramite = new Tramite(params)
        if (!tramite.save(flush: true)) {
            println "Error al guardar el tramite: "
            println tramite.errors
            errores += "<li>Ha ocurrido un error al guardar el trámite</li>"
        }

        def personas = params.findAll { it.key.contains("persona") }
        personas.each { key, val ->
            def parts = key.split("_")
            def rolId = parts[1]
            def rol = RolTramite.get(rolId.toLong())
            def personaTramite = new PersonasTramite([
                    tramite: tramite,
                    rolTramite: rol,
                    persona: Persona.get(val.toLong())
            ])
            if (!personaTramite.save(flush: true)) {
                println "Error al guardar persona tramite: "
                println personaTramite.errors
                errores += "<li>Ha ocurrido un error al guardar el trámite</li>"
            }
        }

        if (params.planilla) {
//            println "grabando planilla " + params.planilla
            def planilla = janus.ejecucion.Planilla.get(params.planilla)
            planilla.fechaOrdenPago = tramite.fecha
            planilla.memoOrdenPago = tramite.memo
            if (!planilla.save(flush: true))
                println "errores " + planilla.errors
        }

        if (errores == "") {
            errores = "OK"
        }
        render errores
    }

    def show_ajax() {
        def tramiteInstance = Tramite.get(params.id)
        if (!tramiteInstance) {
            flash.clase = "alert-error"
            flash.message = "No se encontró Tramite con id " + params.id
            redirect(action: "list")
            return
        }
        [tramiteInstance: tramiteInstance]
    } //show

    def registrar2() {
//        println params

        if (params.fecha) {
            params.fecha = new Date().parse("dd-MM-yyyy", params.fecha)
        }

        def tipo = params.hddTipo
        def p = tipo.split("_")
//        println ">>" + p
        if (p[0] == "ob") {
            params.obra = Obra.get(p[1].toLong())
        } else if (p[0] == "cn") {
            params.contrato = Contrato.get(p[1].toLong())
        }
//        println params
        def errores = ""

        params.estado = EstadoTramite.findByCodigo("C")

        def tramite = new Tramite(params)
        if (!tramite.save(flush: true)) {
            println "Error al guardar el tramite: "
            println tramite.errors
            errores += "<li>Ha ocurrido un error al guardar el trámite</li>"
        }

        def personas = params.findAll { it.key.contains("persona") }
        personas.each { key, val ->
            def parts = key.split("_")
            def rolId = parts[1]
            def rol = RolTramite.get(rolId.toLong())
            def personaTramite = new PersonasTramite([
                    tramite: tramite,
                    rolTramite: rol,
                    persona: Persona.get(val.toLong())
            ])
            if (!personaTramite.save(flush: true)) {
                println "Error al guardar persona tramite: "
                println personaTramite.errors
                errores += "<li>Ha ocurrido un error al guardar el trámite</li>"
            }
        }

        if (errores == "") {
            errores = "OK"
        }
        redirect(action: "list")
    }

    def list() {
        def usu = Persona.get(session.usuario.id)

        def tramites

        if (!params.finalizados || params.finalizados.toUpperCase() != "N") {
            params.finalizados = "S"
        }

        if (params.finalizados.toUpperCase() == "N") {
            tramites = PersonasTramite.withCriteria {
                eq("persona", usu)
                tramite {
                    ne("estado", EstadoTramite.findByCodigo("F"))
                }
            }.tramite.unique()
        } else {
            tramites = PersonasTramite.findAllByPersona(usu).tramite.unique()
        }

        tramites = tramites.sort {
            it.fecha.plus(it.tipoTramite.tiempo)
        }
//        println tramites
        def campos = ["codigo": ["Código", "string"], "nombre": ["Nombre", "string"]]
        return [tramites: tramites, usu: usu, campos: campos, finalizados: params.finalizados]
    }

    def updateEstado() {
        def tramite = Tramite.get(params.id.toLong())
        def estado = EstadoTramite.findByCodigo(params.tipo)
        tramite.estado = estado

        def r = "OK"

        switch (params.tipo) {
            case "I":
                tramite.fechaEnvio = new Date()
                break;
            case "P":
                tramite.fechaRecepcion = new Date()
                break;
            case "F":
                tramite.fechaFinalizacion = new Date()
                break;
            case "R":
                tramite.fechaRespuesta = new Date()
                r = "R"
                break;
        }

        if (!tramite.save(flush: true)) {
            println "Error al actualizar el estado del tramite ${tramite.id} a ${estado.codigo}: " + tramite.errors
            render "NO"
        } else {
            render r
        }
    }

}
