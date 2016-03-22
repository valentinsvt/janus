package janus.pac

import janus.Departamento
import janus.Presupuesto
import jxl.Cell
import jxl.Sheet
import jxl.Workbook

class PacController extends janus.seguridad.Shield {

    def buscadorService

    def index() {
        redirect(action: "registrarPac")
    }

    def formUploadPac() {

    }

    def uploadFile() {

        def requirente = params.requirente.trim()
        def memo = params.memo.trim()
        def coordinacion = params.coordinacion
        def unidad = UnidadIncop.findByCodigo("U")
        if (requirente == "" || memo == "") {
            flash.message = "Por favor, ingrese el requirente y el número de memo"
            redirect(action: 'formUploadPac')
            return
        }
        def departamento = Departamento.get(coordinacion.toLong())

        def path = servletContext.getRealPath("/") + "xlsPac/"   //web-app/archivos
        new File(path).mkdirs()

        def f = request.getFile('file')  //archivo = name del input type file
        if (f && !f.empty) {
            def fileName = f.getOriginalFilename() //nombre original del archivo
            def ext

            def parts = fileName.split("\\.")
            fileName = ""
            parts.eachWithIndex { obj, i ->
                if (i < parts.size() - 1) {
                    fileName += obj
                } else {
                    ext = obj
                }
            }

            if (ext == "xls") {
//                fileName = fileName.tr(/áéíóúñÑÜüÁÉÍÓÚàèìòùÀÈÌÒÙÇç .!¡¿?&#°"'/, "aeiounNUuAEIOUaeiouAEIOUCc_")

                fileName = "xlsPac_" + new Date().format("yyyyMMdd_HHmmss")

                def fn = fileName
                fileName = fileName + "." + ext

                def pathFile = path + fileName
                def src = new File(pathFile)

                def i = 1
                while (src.exists()) {
                    pathFile = path + fn + "_" + i + "." + ext
                    src = new File(pathFile)
                    i++
                }

                f.transferTo(new File(pathFile)) // guarda el archivo subido al nuevo path

                //procesar excel
                def htmlInfo = "", errores = "", doneHtml = "", done = 0
                def file = new File(pathFile)
                Workbook workbook = Workbook.getWorkbook(file)

                workbook.getNumberOfSheets().times { sheet ->
//                    if (sheet == 0) {
                    Sheet s = workbook.getSheet(sheet)
                    if (!s.getSettings().isHidden()) {
                        println s.getName() + "  " + sheet
                        htmlInfo += "<h2>Hoja " + (sheet + 1) + ": " + s.getName() + "</h2>"
                        errores += "<h2>Hoja " + (sheet + 1) + ": " + s.getName() + "</h2>"
                        Cell[] row = null
                        s.getRows().times { j ->
                            if (j == 0) {
                                errores+="<ol>"
                            }
                            println ">>>>>>>>>>>>>>>" + (j + 1)
                            row = s.getRow(j)
                            println "row.length: ${row.length}"
                            if (row.length > 12) {

                                def ok = ["obra", "consultoria", "consultoría"]
                                def tipoCompra = row[3].getContents()

                                if (ok.contains(tipoCompra.toLowerCase())) {

                                    def error = false

                                    def anio = row[0].getContents()
                                    def anioObj = Anio.findAllByAnio(anio.toString())
                                    if (anioObj.size() == 1) {
                                        anioObj = anioObj[0]
                                    } else if (anioObj.size() == 0) {
                                        println "no hay anio " + anio + " crea uno"
                                        errores += "<li>No se encontró el año " + anio + ", se lo ha creado.</li>"
                                        anioObj = new Anio([
                                                anio: anio.toString()
                                        ])
                                        if (!anioObj.save(flush: true)) {
                                            println "error al crear el anio: " + anioObj.errors
                                            error = true
                                        }
                                    } else {
                                        error = true
                                        errores += "<li>Se ha encontrado más de un año " + anio + ". El registro de la fila " + (j + 1) + " no fue ingresado</li>"
                                        println "hay mas de un anio " + anio + ": " + anioObj.id
                                    }
                                    def partida = row[1].getContents().toString()
                                    partida = partida.replaceAll(",", ".")
                                    def presupuesto = Presupuesto.findAllByNumero(partida)
                                    if (presupuesto.size() == 1) {
                                        presupuesto = presupuesto[0]
                                    } else if (presupuesto.size() == 0) {
                                        println "no hay presupuesto con numero " + partida + " crea uno"
                                        errores += "<li>No se encontró el presupuesto con número " + partida + ", se lo ha creado.</li>"
                                        presupuesto = new Presupuesto([
                                                numero: partida,
                                                descripcion: "Sin definir"
                                        ])
                                        if (!presupuesto.save(flush: true)) {
                                            error = true
                                            println "Error al guardar presupuesto: " + presupuesto.errors
                                            errores += "<li>Ha ocurrido un error al guardar presupuesto: " + presupuesto.errors + ". El registro de la fila " + (j + 1) + " no fue ingresado</li>"
                                        }
                                    } else {
                                        error = true
                                        println "hay mas de un presupuesto con numero " + partida + ": " + presupuesto.id
                                        errores += "<li>Se ha encontrado más de un presupuesto con número " + partida + ". El registro de la fila " + (j + 1) + " no fue ingresado</li>"
                                    }
/*
    // descomentar para subir también códigos CPAC

                                    def ccp = row[2].getContents().toString()
                                    def cpp = CodigoComprasPublicas.findAllByNumero(ccp)
                                    if (cpp.size() == 1) {
                                        cpp = cpp[0]
                                    } else if (cpp.size() == 0) {
                                        error = true
                                        println "no hay codigo compras publicas con numero " + ccp
                                        errores += "<li>No se encontró un código de compras públicas con número " + ccp + ". El registro de la fila " + (j + 1) + " no fue ingresado</li>"
                                    } else {
                                        error = true
                                        println "hay mas de un codigo compras publicas con numero " + ccp + ": " + cpp
                                        errores += "<li>Se ha encontrado más de un código de compras públicas con número " + partida + ". El registro de la fila " + (j + 1) + " no fue ingresado</li>"
                                    }
*/
                                    def tipoCompraObj = TipoCompra.findAllByDescripcionIlike(tipoCompra)
                                    if (tipoCompraObj.size() == 1) {
                                        tipoCompraObj = tipoCompraObj[0]
                                    } else if (tipoCompraObj.size() == 0) {
                                        println "no hay tipo compra con descripcion " + tipoCompra
                                        errores += "<li>No se encontró el tipo de compra " + tipoCompra + ", se lo ha creado.</li>"
                                        tipoCompraObj = new TipoCompra([
                                                descripcion: tipoCompra
                                        ])
                                        if (!tipoCompraObj.save(flush: true)) {
                                            error = true
                                            println "Error al guardar tipo compra: " + tipoCompraObj.errors
                                            errores += "<li>Ha ocurrido un error al guardar el tipo de compra: " + tipoCompraObj.errors + ". El registro de la fila " + (j + 1) + " no fue ingresado</li>"
                                        }
                                    } else {
                                        error = true
                                        println "hay mas de un tipo compra con descripcion " + tipoCompra + ": " + tipoCompraObj
                                        errores += "<li>Se ha encontrado más de un tipo de compra " + tipoCompra + ". El registro de la fila " + (j + 1) + " no fue ingresado</li>"
                                    }
                                    def descripcion = row[4].getContents()
                                    def cant = row[5].getContents()
                                    def costoUnitario = row[7].getContents()
                                    def cuatrimestre1 = row[10].getContents()
                                    def cuatrimestre2 = row[11].getContents()
                                    def cuatrimestre3 = row[12].getContents()

//                                            println row*.getContents()
                                    cant = cant.toString().replaceAll(",", "")
                                    try {
                                        cant = cant.toDouble()
                                    } catch (e) {
                                        println e
                                        error = true
                                        errores += "<li>No se pudo convertir el valor de cantidad (" + cant + ") a número. El registro de la fila " + (j + 1) + " no fue ingresado</li>"
                                    }
                                    costoUnitario = costoUnitario.toString().replaceAll(",", "")
                                    try {
                                        costoUnitario = costoUnitario.toDouble()
                                    } catch (e) {
                                        println e
                                        error = true
                                        errores += "<li>No se pudo convertir el valor de costo unitario (" + costoUnitario + ") a número. El registro de la fila " + (j + 1) + " no fue ingresado</li>"
                                    }
//                                            println "\t cant: " + cant + " cu: " + costoUnitario

                                    println "errores: ${error}"
                                    if (!error) {
                                        def total = cant * costoUnitario

                                        def tipoProcedimiento = TipoProcedimiento.findAllByMinimoLessThanEqualsAndTechoGreaterThan(total, total)
                                        if (tipoProcedimiento.size() == 1) {
                                            tipoProcedimiento = tipoProcedimiento[0]
                                        } else if (tipoProcedimiento.size() == 0) {
                                            error = true
                                            println "no hay tipoProcedimiento para el valor " + total
                                            errores += "<li>No se encontró un tipo de procedimiento para el valor " + total + ". El registro de la fila " + (j + 1) + " no fue ingresado</li>"
                                        } else {
                                            error = true
                                            println "hay mas de un tipoProcedimiento para el valor " + total + ": " + tipoProcedimiento
                                            errores += "<li>Se ha encontrado más de un tipo de procedimiento para el valor " + total + ". El registro de la fila " + (j + 1) + " no fue ingresado</li>"
                                        }

                                        if (!error) {
                                            def pacs = Pac.withCriteria {
//                                                eq("cpp", cpp)   //descomentar apra subir CPAC
                                                eq("presupuesto", presupuesto)
                                                eq("anio", anioObj)
                                                eq("descripcion", descripcion)
                                            }
                                            if (pacs.size() == 0) {
                                                def asignaciones =  Asignacion.findAllByAnioAndPrespuesto(anioObj, presupuesto)
                                                if(asignaciones.size() == 1) {
                                                    def asignacion = asignaciones.first()
                                                    asignacion.valor += total
                                                    if(!asignacion.save(flush:true)) {
                                                        println "Error al guardar actualizacion de asignacion"
                                                    }
                                                } else if(asignaciones.size() == 0) {
                                                    def asignacion = new Asignacion()
                                                    asignacion.anio = anioObj
                                                    asignacion.prespuesto = presupuesto
                                                    asignacion.valor = total
                                                    if(!asignacion.save(flush:true)) {
                                                        println "Error al guardar nueva asignacion"
                                                    }
                                                } else {
                                                    println "Existen ${asignaciones.size()} asignaciones del anio: ${anio.anio} y presupuesto ${presupuesto.descripcion}"
                                                }

                                                def pac = new Pac([
                                                        unidad: unidad,
//                                                        cpp: cpp,    //descomentar apra subir CPAC
                                                        presupuesto: presupuesto,
                                                        tipoCompra: tipoCompraObj,
                                                        departamento: departamento,
                                                        tipoProcedimiento: tipoProcedimiento,
                                                        anio: anioObj,
                                                        descripcion: descripcion,
                                                        cantidad: cant,
                                                        costo: costoUnitario,
                                                        c1: cuatrimestre1,
                                                        c2: cuatrimestre2,
                                                        c3: cuatrimestre3,
                                                        memo: memo,
                                                        requiriente: requirente
                                                ])
                                                if (pac.save(flush: true)) {
                                                    println "guardado pac con id=" + pac.id
                                                    done++
                                                } else {
                                                    println pac.errors
                                                    errores += "<li><strong>Ha ocurrido un error al guardar el pac: " + pac.errors + "</strong></li>"
                                                }
                                            } else {
                                                println "ya existia un registro: " + pacs
                                                errores += "<li><i>Ya se encontró un registro con los mismos CCP, partida presupuestaria, descripción y año. El registro de la fila " + (j + 1) + " no fue ingresado</li>"
                                            }
                                        }
                                    } //! error
                                } // es obra o consultoria
//                                    row.length.times { k ->
//                                        if (!row[k].isHidden()) {
//                                            println "k:" + k + "      " + row[k].getContents()
//                                        }// row ! hidden
//                                    } //row.legth.each
                            } //row ! empty
//                                }//row > 7 (fila 9 + )
                            if (j == s.getRows()-1) {
                                errores+="</ol>"
                            }
                        } //rows.each
                    } //sheet ! hidden
//                    }//solo sheet 0
                } //sheets.each
                if (done > 0) {
                    doneHtml = "<div class='alert alert-success'>Se han ingresado correctamente " + done + " registros</div>"
                }

                def str = doneHtml
//                str += htmlInfo
                if (errores != "") {
                    str += "<ol>" + errores + "</ol>"
                }
                str += doneHtml

                flash.message = str

                println "DONE!!"
                redirect(action: "mensajeUpload")
            } else {
                flash.message = "Seleccione un archivo Excel xls para procesar (archivos xlsx deben ser convertidos a xls primero)"
                redirect(action: 'formUploadPac')
            }
        } else {
            flash.message = "Seleccione un archivo para procesar"
            redirect(action: 'formUploadPac')
//            println "NO FILE"
        }
    }

    def mensajeUpload() {

    }

    def registrarPac() {
        def campos = ["numero": ["Código", "string"], "descripcion": ["Descripción", "string"]]
        def actual
        if (params.anio)
            actual = Anio.get(params.anio)
        else
            actual = Anio.findByAnio(new Date().format("yyyy"))
        if (!actual)
            actual = Anio.list([sort: 'anio', order: 'desc']).pop()

        [campos: campos, actual: actual]
    }

    def buscaCpac() {
        println("params C" + params)
        def listaTitulos = ["Código", "Descripción"]
        def listaCampos = ["numero", "descripcion"]
        def funciones = [null, null]
        def url = g.createLink(action: "buscaCpac", controller: "pac")
        def funcionJs = "function(){"
        funcionJs += '$("#modal-ccp").modal("hide");'
        funcionJs += '$("#item_cpac").val($(this).attr("regId"));$("#item_codigo").val($(this).attr("prop_numero"));$("#item_codigo").attr("title",$(this).attr("prop_descripcion"))'
        funcionJs += '}'
        def numRegistros = 20
        def extras = " and movimiento=1"
        if (!params.reporte) {
            if(params.excel){
//                println("entro")
                session.dominio = CodigoComprasPublicas
                session.funciones = funciones
                def anchos = [15, 50, 70, 20, 20, 20, 20]
                /*anchos para el set column view en excel (no son porcentajes)*/
                redirect(controller: "reportes", action: "reporteBuscadorExcel", params: [listaCampos: listaCampos, listaTitulos: listaTitulos, tabla: "CodigoComprasPublicas", orden: params.orden, ordenado: params.ordenado, criterios: params.criterios, operadores: params.operadores, campos: params.campos, titulo: "CodigoComprasPublicas", anchos: anchos, extras: extras, landscape: true])

            }else{
                def lista = buscadorService.buscar(CodigoComprasPublicas, "CodigoComprasPublicas", "excluyente", params, true, extras) /* Dominio, nombre del dominio , excluyente o incluyente ,params tal cual llegan de la interfaz del buscador, ignore case */
                lista.pop()
                render(view: '../tablaBuscadorColDer', model: [listaTitulos: listaTitulos, listaCampos: listaCampos, lista: lista, funciones: funciones, url: url, controller: "llamada", numRegistros: numRegistros, funcionJs: funcionJs])
            }
        } else {
//            println "entro reporte"
            /*De esto solo cambiar el dominio, el parametro tabla, el paramtero titulo y el tamaño de las columnas (anchos)*/
            session.dominio = CodigoComprasPublicas
            session.funciones = funciones
            def anchos = [20, 80] /*el ancho de las columnas en porcentajes... solo enteros*/
            redirect(controller: "reportes", action: "reporteBuscador", params: [listaCampos: listaCampos, listaTitulos: listaTitulos, tabla: "CodigoComprasPublicas", orden: params.orden, ordenado: params.ordenado, criterios: params.criterios, operadores: params.operadores, campos: params.campos, titulo: "Código compras publcias", anchos: anchos, extras: extras, landscape: false])
        }
    }

    def buscaPrsp() {
        println "buscaPrsp $params"
        def listaTitulos = ["Código", "Descripción", "Fuente", "programa", "Subprograma", "Proyecto"]
        def listaCampos = ["numero", "descripcion", "fuente", "programa", "subPrograma", "proyecto"]
        def funciones = [null, null]
        def url = g.createLink(action: "buscaPrsp", controller: "asignacion")
        def funcionJs = "function(){"
        funcionJs += '$("#modal-ccp").modal("hide");'
        funcionJs += '$("#item_prsp").val($(this).attr("regId"));$("#item_presupuesto").val($(this).attr("prop_numero"));' +
                '$("#item_presupuesto").attr("title",$(this).attr("prop_descripcion")+" Fuente: "+$(this).attr("prop_fuente")+" - Programa:' +
                ' "+$(this).attr("prop_programa")+" - Subprograma: "+$(this).attr("prop_subPrograma")+" - Proyecto: "+$(this).attr("prop_proyecto"));' +
                'cargarTecho();'
        funcionJs += '}'
        def numRegistros = 20
        def extras = ""

//        println("params" + params)

        if (!params.reporte) {
            if(params.excel){
                session.dominio = Presupuesto
                session.funciones = funciones
                def anchos = [15, 50, 70, 20, 20, 20]
                /*anchos para el set column view en excel (no son porcentajes)*/
                redirect(controller: "reportes", action: "reporteBuscadorExcel", params: [listaCampos: listaCampos, listaTitulos: listaTitulos, tabla: "Presupuesto", orden: params.orden, ordenado: params.ordenado, criterios: params.criterios, operadores: params.operadores, campos: params.campos, titulo: "Presupuesto", anchos: anchos, extras: extras, landscape: true])
            }else{
                def lista = buscadorService.buscar(janus.Presupuesto, "Presupuesto", "excluyente", params, true, extras) /* Dominio, nombre del dominio , excluyente o incluyente ,params tal cual llegan de la interfaz del buscador, ignore case */
                lista.pop()
                render(view: '../tablaBuscadorColDer', model: [listaTitulos: listaTitulos, listaCampos: listaCampos, lista: lista, funciones: funciones, url: url, controller: "llamada", numRegistros: numRegistros, funcionJs: funcionJs])
            }
        } else {
            /*De esto solo cambiar el dominio, el parametro tabla, el paramtero titulo y el tamaño de las columnas (anchos)*/
            session.dominio = Presupuesto
            session.funciones = funciones
            def anchos = [20, 20,20,10,10,10] /*el ancho de las columnas en porcentajes... solo enteros*/
            redirect(controller: "reportes", action: "reporteBuscador", params: [listaCampos: listaCampos, listaTitulos: listaTitulos, tabla: "Presupuesto", orden: params.orden, ordenado: params.ordenado, criterios: params.criterios, operadores: params.operadores, campos: params.campos, titulo: "Partidas presupuestarias", anchos: anchos, extras: extras, landscape: false])
        }
    }




    def cargarTecho() {
        println "cargarTecho params: $params"
        def prsp = janus.Presupuesto.get(params.id)
        def anio = Anio.get(params.anio)
        def techo = Asignacion.findByAnioAndPrespuesto(anio, prsp)
        if (!techo)
            techo = "0.00"
        else
            techo = techo.valor
        def pacs = Pac.findAllByPresupuestoAndAnio(prsp, anio)
        println "uso de asignación: $pacs.costo"
        def usado = 0
        pacs.each {
//            println "procesa $it"
//            println "procesa: ${it.id}, ${it.costo} con ${params?.pac_id}"
            if(it.id != (params?.pac_id? params?.pac_id?.toInteger() : 0)) {
                usado += it.costo * it.cantidad
            }
        }
        println "techo: $techo, usado: $usado"
        render "" + techo + ";" + usado
    }


    def tabla() {

        def pac
        def dep
        def anio
        if (!params.todos) {
            anio = Anio.get(params.anio)
            if (params.dpto) {
                dep = janus.Departamento.get(params.dpto)
                pac = Pac.findAllByDepartamentoAndAnio(dep, anio, [sort: "id"])
                dep = dep.descripcion
                anio = anio.anio
            } else {
                pac = Pac.findAllByAnio(Anio.get(params.anio), [sort: "id"])
                dep = "Todos"
                anio = anio.anio
            }
        } else {
            dep = "Todos"
            anio = "Todos"
            pac = Pac.list([sort: "id"])
        }

        [pac: pac, todos: params.todos, dep: dep, anio: anio]

    }


    def regPac() {
//        println params

        def pac
        if (params.id != "" && params.id)
            pac = Pac.get(params.id)
        else
            pac = new Pac()
        pac.properties = params
        if (!pac.save(flush: true))
            println "errors " + pac.errors
        else
            render "ok"


    }

    def eliminarPac() {
//        println "eliminar pac "+params
        def pac = Pac.get(params.id)
        pac.delete(flush: true)
        render "ok"
    }


}
