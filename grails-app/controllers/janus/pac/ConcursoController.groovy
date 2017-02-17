package janus.pac

import janus.Administracion
import janus.Comunidad
import janus.Contrato
import janus.Obra
import janus.Parroquia
import org.springframework.dao.DataIntegrityViolationException

class ConcursoController extends janus.seguridad.Shield {

    def buscadorService
    def obraService


    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

    def generaCodigo(concursoInstance) {
        def codigo = ""
//        def conc = Concurso.count()
        def sec = 1
        def lst = Concurso.list([sort: "id", order: "desc"])
        println lst
        if (lst.size() > 1) {
            def last = lst[1].codigo?.split("-")
            println "... last: $last"
            if (last?.size() > 2) {
//                def cod = last[2].toInteger()
                def cod = 0
                try {
                    cod = last[1].toInteger()
                    sec = cod + 1
                } catch (e) {
                    println "error $e"
                }

//                println cod
//                println sec
            }
        }

        codigo += concursoInstance.pac.tipoProcedimiento.sigla + "-"
//        codigo += "GADPP" + "-"
        codigo += sec + "-DGCP-"
        codigo += new Date().format("yyyy")
//        println codigo
//        println "________________________________________________________"
        return codigo
    }


    def concursos() {
        def campos = ["codigo": ["Contrato No.", "string"], "objeto": ["Objeto", "string"], "fechaInicio": ["Fecha inicio", "string"], "presupuestoReferencial": ["Presupuesto", "string"], "obra": ["Obra", "string"]]
        [campos: campos]
    }


    def buscarConcurso() {
        //println("params" + params)
        def extraObra = ""
        if (params.campos instanceof java.lang.String) {
            if (params.campos == "obra") {
                def obras = Obra.findAll("from Obra where nombre like '%${params.criterios.toUpperCase()}%' or codigo like '%${params.criterios.toUpperCase()}%'")
                params.criterios = ""
                obras.eachWithIndex { ob, j ->
                    extraObra += "" + ob.id
                    if (j < obras.size() - 1)
                        extraObra += ","
                }
                if (extraObra.size() < 1)
                    extraObra = "-1"
                params.campos = ""
                params.operadores = ""
            }
        } else {
            def remove = []
            params.campos.eachWithIndex { p, i ->
                if (p == "obra") {
                    def obras = Obra.findAll("from Obra where nombre like '%${params.criterios[i].toUpperCase()}%'  or codigo like '%${params.criterios.toUpperCase()}%'")
                    obras.eachWithIndex { ob, j ->
                        extraObra += "" + ob.id
                        if (j < obras.size() - 1)
                            extraObra += ","
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

        def pac = { concurso ->
            return concurso?.obra?.codigo
        }
        def nombreObra = { concurso ->
            return concurso?.obra?.nombre
        }
        def registro = { concurso ->
            return (concurso?.estado != "R") ? "N" : "R"
        }
        def numero = { concurso ->
            return (concurso?.numeroCertificacion) ? concurso.numeroCertificacion : ""
        }

        def listaTitulos = ["NÚMERO", "OBJETO", "OBRA/CONSULTORIA", "PAC", "MONTO", "ESTADO", "N° CERTIFICACIÓN PRESUPUESTARIA"]
        def listaCampos = ["codigo", "objeto", "obra", "pac", "presupuestoReferencial", "estado", "numeroCertificacion"]
        def funciones = [null, null, ["closure": [nombreObra, "&"]], ["closure": [pac, "&"]], null, ["closure": [registro, "&"]], ["closure": [numero, "&"]]]
        def url = g.createLink(action: "buscarConcurso", controller: "concurso")
        def funcionJs = "function(){"
        funcionJs += '$("#modal-busqueda").modal("hide");'
        funcionJs += 'location.href="' + g.createLink(action: 'form_ajax', controller: 'concurso') + '/"+$(this).attr("regId");'
        funcionJs += '}'
        def numRegistros = 20
        def extras = " "
        if (extraObra.size() > 0)
            extras += " and obra in (${extraObra})"
//        println "extras " + extras

        if (!params.reporte) {
            if (params.excel) {
                session.dominio = Concurso
                session.funciones = funciones
                def anchos = [35, 70, 70, 35, 20, 10]
                /*anchos para el set column view en excel (no son porcentajes)*/
                redirect(controller: "reportes", action: "reporteBuscadorExcel", params: [listaCampos: listaCampos, listaTitulos: listaTitulos, tabla: "Concurso", orden: params.orden, ordenado: params.ordenado, criterios: params.criterios, operadores: params.operadores, campos: params.campos, titulo: "REPORTE DE PROCESOS DE CONTRATACION", anchos: anchos, extras: extras, landscape: true])
            } else {
                //println("pdf")
                def lista = buscadorService.buscar(Concurso, "Concurso", "excluyente", params, true, extras)
                /* Dominio, nombre del dominio , excluyente o incluyente ,params tal cual llegan de la interfaz del buscador, ignore case */
                lista.pop()
                render(view: '../tablaBuscador', model: [listaTitulos: listaTitulos, listaCampos: listaCampos, lista: lista, funciones: funciones, url: url, controller: "obra", numRegistros: numRegistros, funcionJs: funcionJs, width: 1800, paginas: 12])
            }

        } else {
           // println "entro reporte"
            /*De esto solo cambiar el dominio, el parametro tabla, el paramtero titulo y el tamaño de las columnas (anchos)*/
            session.dominio = Concurso
            session.funciones = funciones
            def anchos = [12, 38, 25, 10, 5, 5, 10]
            /*el ancho de las columnas en porcentajes... solo enteros*/
            redirect(controller: "reportes", action: "reporteBuscador", params: [listaCampos: listaCampos, listaTitulos: listaTitulos, tabla: "Concurso", orden: params.orden, ordenado: params.ordenado, criterios: params.criterios, operadores: params.operadores, campos: params.campos, titulo: "REPORTE DE PROCESOS DE CONTRATACION", anchos: anchos, extras: extras, landscape: true])
        }
    }

    def index() {
        redirect(action: "list", params: params)
    } //index

    def list() {
        def campos = ["descripcion": ["Descripción", "string"]]
        if (!params.sort) {
            params.sort = "id"
            params.order = "desc"
        }
        return [concursoInstanceList: Concurso.list(params), params: params, campos: campos]
    } //list

    def nuevoProceso() {
        def pac = Pac.get(params.id)
        def admin = Administracion.findAllByFechaInicioLessThanEqualsAndFechaFinGreaterThanEquals(new Date(), new Date())
        def concurso = new Concurso()
        concurso.pac = pac
        if (admin.size() == 1) {
            concurso.administracion = admin[0]
        } else if (admin.size() > 1) {
            println "hay mas de una admin: " + admin
        } else {
            println "no hay admin q asignar"
        }
        concurso.costoBases = 0
        concurso.objeto = pac.descripcion

        if (concurso.objeto.size() > 255) {
            concurso.objeto = concurso.objeto[0..254]
        }

        if (concurso.save(flush: true)) {
//            println "saved ok"
            def codigo = generaCodigo(concurso)
            concurso.codigo = codigo
            if (!concurso.save(flush: true)) {
                println "error al guarda el codigo " + codigo + " en el concurso " + concurso.id
                println concurso.errors
            }

            flash.clase = "alert-success"
            flash.message = "Proceso creado"
            redirect(action: 'list')
        } else {
            println "not saved"
            println concurso.errors
            flash.clase = "alert-error"
            flash.message = "Ha ocurrido un error al crear el proceso"
            redirect(action: 'list')
        }
    }

    def show() {
        def concursoInstance = Concurso.get(params.id)
        if (!concursoInstance) {
            flash.clase = "alert-error"
            flash.message = "No se encontró Concurso con id " + params.id
            redirect(action: "list")
            return
        }
        def of = Oferta.findAllByConcurso(concursoInstance).size()
        [concursoInstance: concursoInstance, of: of]
    }

    def buscaPac() {
        println("params" + params)
        def listaTitulos = ["Descripción", "Departamento", "Presupuesto"]
        def listaCampos = ["descripcion", "departamento", "presupuesto"]
        def funciones = [null, null]
        def url = g.createLink(action: "buscaPac", controller: "concurso")
//        def funcionJs = ""
        def funcionJs = "function(){"
        funcionJs += '$("#modal-pac").modal("hide");'
        funcionJs += 'var id=$(this).attr("regId");'
//        funcionJs += 'console.log(id);'
        funcionJs += 'var url = "' + createLink(controller: 'concurso', action: 'nuevoProceso') + '/"+id;'
        funcionJs += 'location.href = url;'
        funcionJs += '}'
        def numRegistros = 20
        def extras = ""
        if (!params.reporte) {
            if(params.excel){
                session.dominio = Pac
                session.funciones = funciones
                def anchos = [40,40,20]
                /*anchos para el set column view en excel (no son porcentajes)*/

//                redirect(controller: "reportes", action: "reporteBuscadorExcel", params: [listaCampos: listaCampos, listaTitulos: listaTitulos, tabla: "Concurso", orden: params.orden, ordenado: params.ordenado, criterios: params.criterios, operadores: params.operadores, campos: params.campos, titulo: "REPORTE PAC, anchos: anchos, extras: extras, landscape: true])

                  redirect(controller: "reportes", action: "reporteBuscadorExcel", params: [listaCampos: listaCampos, listaTitulos: listaTitulos, tabla: "Pac", orden: params.orden, ordenado: params.ordenado, criterios: params.criterios, operadores: params.operadores, campos: params.campos, titulo: "REPORTE DE PAC", anchos: anchos, extras: extras, landscape: true])

            } else {
                def lista2 = buscadorService.buscar(Pac, "Pac", "excluyente", params, true, extras)
                /* Dominio, nombre del dominio , excluyente o incluyente ,params tal cual llegan de la interfaz del buscador, ignore case */
                lista2.pop()
                def lista = []
                lista2.each { l ->
                    if (Concurso.countByPac(l) == 0) {
                        lista.add(l)
                    }
                }
                render(view: '../tablaBuscadorColDer', model: [listaTitulos: listaTitulos, listaCampos: listaCampos, lista: lista, funciones: funciones, url: url, controller: "llamada", numRegistros: numRegistros, funcionJs: funcionJs])
            }
          } else {
//            println "entro reporte"
            /*De esto solo cambiar el dominio, el parametro tabla, el paramtero titulo y el tamaño de las columnas (anchos)*/
            session.dominio = Pac
            session.funciones = funciones
            def anchos = [40, 40, 20] /*el ancho de las columnas en porcentajes... solo enteros*/
            redirect(controller: "reportes", action: "reporteBuscador", params: [listaCampos: listaCampos, listaTitulos: listaTitulos, tabla: "Pac", orden: params.orden, ordenado: params.ordenado, criterios: params.criterios, operadores: params.operadores, campos: params.campos, titulo: "Rubros", anchos: anchos, extras: extras, landscape: true])
        }
    }

    def setEtapa() {
        println "set etapa  " + params
        def con = Concurso.get(params.id)
        switch (params.tipo) {
            case "1":
                con.fechaEtapa1 = new Date().parse("dd-MM-yyyy", params.fecha)
                break;
            case "2":
                con.fechaEtapa2 = new Date().parse("dd-MM-yyyy", params.fecha)
                break;
            case "3":
                con.fechaEtapa3 = new Date().parse("dd-MM-yyyy", params.fecha)
                con.fechaFinPreparatorio = con.fechaEtapa3
                break;
        }
        con.save(flush: true)
        render "ok"

    }


    def form_ajax() {
//        println "aqui "
        def campos = ["codigo": ["Código", "string"], "nombre": ["Nombre", "string"], "descripcion": ["Descripción", "string"], "oficioIngreso": ["Memo ingreso", "string"], "oficioSalida": ["Memo salida", "string"], "sitio": ["Sitio", "string"], "plazo": ["Plazo", "int"], "parroquia": ["Parroquia", "string"], "comunidad": ["Comunidad", "string"], "canton": ["Canton", "string"]]
        def duracionPrep = 0
        def duracionPre = 0
        def duracionCon = 0
        def maxPrep = 5
        def maxPre = 5
        def maxCon = 5


        def concursoInstance = new Concurso(params)
//        def prf = Administracion.findByDescripcion('Prefecto Provincial')
//        concursoInstance.administracion = prf

//        println("--->"  + concursoInstance?.administracion?.nombrePrefecto)

        if (params.id) {
            concursoInstance = Concurso.get(params.id.toLong())
            if (!concursoInstance) {
                flash.clase = "alert-error"
                flash.message = "No se encontró Concurso con id " + params.id
                redirect(action: "list")
                return
            }
            session.concurso=concursoInstance
            def ahora = new Date()
            maxPrep = concursoInstance.pac.tipoProcedimiento?.preparatorio
            maxPre = concursoInstance.pac.tipoProcedimiento?.precontractual
            maxCon = concursoInstance.pac.tipoProcedimiento?.contractual
            println "max prep " + maxPrep
            if (concursoInstance.fechaInicioPreparatorio != null) {
                use(groovy.time.TimeCategory) {
                    if (concursoInstance.fechaFinPreparatorio == null)
                        duracionPrep = ahora - concursoInstance.fechaInicioPreparatorio
                    else
                        duracionPrep = concursoInstance.fechaFinPreparatorio - concursoInstance.fechaInicioPreparatorio

                }
                duracionPrep = duracionPrep.days
            }
            if (concursoInstance.fechaInicioPrecontractual != null) {
                use(groovy.time.TimeCategory) {
                    if (concursoInstance.fechaFinPrecontractual == null)
                        duracionPre = ahora - concursoInstance.fechaInicioPrecontractual
                    else
                        duracionPre = concursoInstance.fechaFinPrecontractual - concursoInstance.fechaInicioPrecontractual

                }
                duracionPre = duracionPre.days
            }
            if (concursoInstance.fechaInicioContractual != null) {
                use(groovy.time.TimeCategory) {
                    if (concursoInstance.fechaFinContractual == null)
                        duracionCon = ahora - concursoInstance.fechaInicioContractual
                    else
                        duracionCon = concursoInstance.fechaFinContractual - concursoInstance.fechaInicioContractual

                    duracionCon = duracionCon.days
                }
            }
            //no existe el objeto
        } //es edit
        return [concursoInstance: concursoInstance, campos: campos, duracionPrep: duracionPrep, duracionPre: duracionPre, duracionCon: duracionCon, maxPrep: maxPrep, maxPre: maxPre, maxCon: maxCon]
    } //form_ajax


    def iniciarPreparatorio() {
        println "iniciar prep " + params

        def concurso = Concurso.get(params.id)
        concurso.memoRequerimiento = params.memo
        def fecha = new Date().parse("dd-MM-yyyy", params.fecha)
        concurso.fechaInicioPreparatorio = fecha
        if (!concurso.save(flush: true)) {
            render "error"
            return
        } else {
            render "ok"
            return
        }
    }


    def buscarObra() {
//        println "buscarObra params: $params"
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

        def limite
        if(session.concurso){
            def con = Concurso.get(session.concurso.id)
            limite = con.pac.tipoProcedimiento.techo
        }else{
            limite = TipoProcedimiento.findBySigla("MCD").techo
        }

//        println "limite "+limite+"  "
        def extras = " and (valor<${limite}  or  liquidacion = 1) and estadoSif='R' "
        if (extraParr.size() > 1)
            extras += " and parroquia in (${extraParr})"
        if (extraCom.size() > 1)
            extras += " and comunidad in (${extraCom})"

        extras += " and estado='R' "

        def parr = { p ->
            return p.parroquia?.nombre
        }
        def comu = { c ->
            return c.comunidad?.nombre
        }
        def listaTitulos = ["Código", "Nombre", "Descripción", "Fecha Reg.", "M. ingreso", "M. salida", "Sitio", "Plazo", "Parroquia", "Comunidad", "Inspector", "Revisor", "Responsable", "Estado Obra"]
        def listaCampos = ["codigo", "nombre", "descripcion", "fechaCreacionObra", "oficioIngreso", "oficioSalida", "sitio", "plazo", "parroquia", "comunidad", "inspector", "revisor", "responsable", "estadoObra"]
        def funciones = [null, null, null, ["format": ["dd/MM/yyyy hh:mm"]], null, null, null, null, ["closure": [parr, "&"]], ["closure": [comu, "&"]], null, null, null, null]
        def url = g.createLink(action: "buscarObra", controller: "obra")
        def funcionJs = "function(){"
        funcionJs += '$("#modal-busqueda").modal("hide");'
        funcionJs += '$("#obra_id").val($(this).attr("regId"));'
        funcionJs += 'cargarDatos();'
        funcionJs += '}'
        def numRegistros = 20

        if (!params.reporte) {
            def lista = buscadorService.buscar(Obra, "Obra", "excluyente", params, true, extras)
            /* Dominio, nombre del dominio , excluyente o incluyente ,params tal cual llegan de la interfaz del buscador, ignore case */
            lista.pop()
            render(view: '../tablaBuscador', model: [listaTitulos: listaTitulos, listaCampos: listaCampos, lista: lista, funciones: funciones, url: url, controller: "llamada", numRegistros: numRegistros, funcionJs: funcionJs, width: 1800, paginas: 12])
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

    def datosObra() {
        def obra = Obra.get(params.obra)
        def monto = obraService.montoObra(obra)
        def plazo = obra.plazoEjecucionMeses * 30 + obra.plazoEjecucionDias
        render "" + obra.codigo + "&&" + obra.nombre + "&&" + plazo + "&&" + monto
    }


    def save() {
        println "save concurso... " + params

        if (params.codigo) {
            params.codigo = params.codigo.toUpperCase()
        }
        if (params.fechaInicio) {
            params.fechaInicio = new Date().parse("dd-MM-yyyy HH:mm", params.fechaInicio)
        }
        if (params.fechaLimitePreguntas) {
            params.fechaLimitePreguntas = new Date().parse("dd-MM-yyyy HH:mm", params.fechaLimitePreguntas)
        }
        if (params.fechaPublicacion) {
            params.fechaPublicacion = new Date().parse("dd-MM-yyyy HH:mm", params.fechaPublicacion)
            println params.fechaPublicacion
        }
        if (params.fechaLimiteEntregaOfertas) {
            params.fechaLimiteEntregaOfertas = new Date().parse("dd-MM-yyyy HH:mm", params.fechaLimiteEntregaOfertas)
        }
        if (params.fechaLimiteRespuestas) {
            params.fechaLimiteRespuestas = new Date().parse("dd-MM-yyyy HH:mm", params.fechaLimiteRespuestas)
        }
        if (params.fechaLimiteRespuestaConvalidacion) {
            params.fechaLimiteRespuestaConvalidacion = new Date().parse("dd-MM-yyyy HH:mm", params.fechaLimiteRespuestaConvalidacion)
        }
        if (params.fechaLimiteSolicitarConvalidacion) {
            params.fechaLimiteSolicitarConvalidacion = new Date().parse("dd-MM-yyyy HH:mm", params.fechaLimiteSolicitarConvalidacion)
        }
        if (params.fechaInicioPuja) {
            params.fechaInicioPuja = new Date().parse("dd-MM-yyyy HH:mm", params.fechaInicioPuja)
        }
        if (params.fechaCalificacion) {
            params.fechaCalificacion = new Date().parse("dd-MM-yyyy HH:mm", params.fechaCalificacion)
        }
        if (params.fechaAdjudicacion) {
            params.fechaAdjudicacion = new Date().parse("dd-MM-yyyy HH:mm", params.fechaAdjudicacion)
        }
        if (params.fechaFinPuja) {
            params.fechaFinPuja = new Date().parse("dd-MM-yyyy HH:mm", params.fechaFinPuja)
        }

        if (params.fechaAperturaOfertas) {
            params.fechaAperturaOfertas = new Date().parse("dd-MM-yyyy HH:mm", params.fechaAperturaOfertas)
        }
        if (params.fechaLimiteResultadosFinales) {
            params.fechaLimiteResultadosFinales = new Date().parse("dd-MM-yyyy HH:mm", params.fechaLimiteResultadosFinales)
        }
        if (params.fechaInicioEvaluacionOferta) {
            params.fechaInicioEvaluacionOferta = new Date().parse("dd-MM-yyyy HH:mm", params.fechaInicioEvaluacionOferta)
        }
        if (params.fechaAceptacionProveedor) {
            params.fechaAceptacionProveedor = new Date().parse("dd-MM-yyyy HH:mm", params.fechaAceptacionProveedor)
        }

        if (params.fechaInicioPreparatorio) {
            params.fechaInicioPreparatorio = new Date().parse("dd-MM-yyyy", params.fechaInicioPreparatorio)
        }
        if (params.fechaFinPreparatorio) {
            params.fechaFinPreparatorio = new Date().parse("dd-MM-yyyy", params.fechaFinPreparatorio)
        }
        if (params.fechaInicioPrecontractual) {
            params.fechaInicioPrecontractual = new Date().parse("dd-MM-yyyy", params.fechaInicioPrecontractual)
        }
        if (params.fechaFinPrecontractual) {
            params.fechaFinPrecontractual = new Date().parse("dd-MM-yyyy", params.fechaFinPrecontractual)
        }
        if (params.fechaInicioContractual) {
            params.fechaInicioContractual = new Date().parse("dd-MM-yyyy", params.fechaInicioContractual)
        }
        if (params.fechaFinContractual) {
            params.fechaFinContractual = new Date().parse("dd-MM-yyyy", params.fechaFinContractual)
        }
        if (params.fechaNotificacionAdjudicacion) {
            params.fechaNotificacionAdjudicacion = new Date().parse("dd-MM-yyyy HH:mm", params.fechaNotificacionAdjudicacion)
        }
        if (params.memoRequerimiento) {
            params.memoRequerimiento = params.memoRequerimiento.toString().toUpperCase()
        }
        if (params.memoSif) {
            params.memoSif = params.memoSif.toString().toUpperCase()
        }

//        println "params "+params
        def concursoInstance
        if (params.id) {
            concursoInstance = Concurso.get(params.id)
            if (!concursoInstance) {
                flash.clase = "alert-error"
                flash.message = "No se encontró Concurso con id " + params.id
                redirect(action: 'list')
                return
            }//no existe el objeto
            concursoInstance.properties = params
        }//es edit
        else {
            concursoInstance = new Concurso(params)
        } //es create


        if (!concursoInstance.save(flush: true)) {
            println "errores concurso " + concursoInstance.errors
            flash.clase = "alert-error"
            def str = "<h4>No se pudo guardar Concurso " + (concursoInstance.id ? concursoInstance.id : "") + "</h4>"

            str += "<ul>"
            concursoInstance.errors.allErrors.each { err ->
                def msg = err.defaultMessage
                err.arguments.eachWithIndex { arg, i ->
                    msg = msg.replaceAll("\\{" + i + "}", arg.toString())
                }
                str += "<li>" + msg + "</li>"
            }
            str += "</ul>"

            flash.message = str
            redirect(action: 'list')
            return
        }

        if (!concursoInstance.codigo) {
            def codigo = generaCodigo(concursoInstance)
            concursoInstance.codigo = codigo
            if (!concursoInstance.save(flush: true)) {
                println "error al guarda el codigo " + codigo + " en el concurso " + concursoInstance.id
                println concursoInstance.errors
            }
        }

        if (params.id) {
            flash.clase = "alert-success"
            flash.message = "Se ha actualizado correctamente Concurso " + concursoInstance.id
        } else {
            flash.clase = "alert-success"
            flash.message = "Se ha creado correctamente Concurso " + concursoInstance.id
        }
        redirect(action: 'list')
    } //save

    def show_ajax() {
        def concursoInstance = Concurso.get(params.id)
        if (!concursoInstance) {
            flash.clase = "alert-error"
            flash.message = "No se encontró Concurso con id " + params.id
            redirect(action: "list")
            return
        }
        [concursoInstance: concursoInstance]
    } //show

    def delete() {
        def concursoInstance = Concurso.get(params.id)
        if (!concursoInstance) {
            flash.clase = "alert-error"
            flash.message = "No se encontró Concurso con id " + params.id
            redirect(action: "list")
            return
        }

        try {
            concursoInstance.delete(flush: true)
            flash.clase = "alert-success"
            flash.message = "Se ha eliminado correctamente Concurso " + concursoInstance.id
            redirect(action: "list")
        }
        catch (DataIntegrityViolationException e) {
            flash.clase = "alert-error"
            flash.message = "No se pudo eliminar Concurso " + (concursoInstance.id ? concursoInstance.id : "")
            redirect(action: "list")
        }
    } //delete

    def registrar() {
//        println "registrar " + params
        def con = Concurso.get(params.id)
        con.estado = params.estado
        if (!con.save(flush: true))
            render "error"
        else
            render "ok"

    }


} //fin controller
