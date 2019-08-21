package janus.pac

import janus.Departamento
import janus.Presupuesto
import jxl.Cell
import jxl.Sheet
import jxl.Workbook
import org.springframework.dao.DataIntegrityViolationException

class CodigoComprasPublicasController extends janus.seguridad.Shield {

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

    def index() {
        redirect(action: "list", params: params)
    } //index

    def list() {
        [codigoComprasPublicasInstanceList: CodigoComprasPublicas.list(params), params: params]
    } //list

    def form_ajax() {
        def codigoComprasPublicasInstance = new CodigoComprasPublicas(params)
        if (params.id) {
            codigoComprasPublicasInstance = CodigoComprasPublicas.get(params.id)
            if (!codigoComprasPublicasInstance) {
                flash.clase = "alert-error"
                flash.message = "No se encontró Codigo Compras Publicas con id " + params.id
                redirect(action: "list")
                return
            } //no existe el objeto
        } //es edit
        return [codigoComprasPublicasInstance: codigoComprasPublicasInstance]
    } //form_ajax

    def save() {
        def codigoComprasPublicasInstance
        if (params.id) {
            codigoComprasPublicasInstance = CodigoComprasPublicas.get(params.id)
            if (!codigoComprasPublicasInstance) {
                flash.clase = "alert-error"
                flash.message = "No se encontró Codigo Compras Publicas con id " + params.id
                redirect(action: 'list')
                return
            }//no existe el objeto
            codigoComprasPublicasInstance.properties = params
        }//es edit
        else {
            codigoComprasPublicasInstance = new CodigoComprasPublicas(params)
        } //es create
        if (!codigoComprasPublicasInstance.save(flush: true)) {
            flash.clase = "alert-error"
            def str = "<h4>No se pudo guardar Codigo Compras Publicas " + (codigoComprasPublicasInstance.id ? codigoComprasPublicasInstance.id : "") + "</h4>"

            str += "<ul>"
            codigoComprasPublicasInstance.errors.allErrors.each { err ->
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

        if (params.id) {
            flash.clase = "alert-success"
            flash.message = "Se ha actualizado correctamente Codigo Compras Publicas " + codigoComprasPublicasInstance.id
        } else {
            flash.clase = "alert-success"
            flash.message = "Se ha creado correctamente Codigo Compras Publicas " + codigoComprasPublicasInstance.id
        }
        redirect(action: 'list')
    } //save

    def show_ajax() {
        def codigoComprasPublicasInstance = CodigoComprasPublicas.get(params.id)
        if (!codigoComprasPublicasInstance) {
            flash.clase = "alert-error"
            flash.message = "No se encontró Codigo Compras Publicas con id " + params.id
            redirect(action: "list")
            return
        }
        [codigoComprasPublicasInstance: codigoComprasPublicasInstance]
    } //show

    def delete() {
        def codigoComprasPublicasInstance = CodigoComprasPublicas.get(params.id)
        if (!codigoComprasPublicasInstance) {
            flash.clase = "alert-error"
            flash.message = "No se encontró Codigo Compras Publicas con id " + params.id
            redirect(action: "list")
            return
        }

        try {
            codigoComprasPublicasInstance.delete(flush: true)
            flash.clase = "alert-success"
            flash.message = "Se ha eliminado correctamente Codigo Compras Publicas " + codigoComprasPublicasInstance.id
            redirect(action: "list")
        }
        catch (DataIntegrityViolationException e) {
            flash.clase = "alert-error"
            flash.message = "No se pudo eliminar Codigo Compras Publicas " + (codigoComprasPublicasInstance.id ? codigoComprasPublicasInstance.id : "")
            redirect(action: "list")
        }
    } //delete



    def cargarCPC () {

    }



    def uploadFile() {
        println("params uf " + params)

        if (!params.fecha) {
            flash.message = "Ingrese la fecha"
            redirect(action: 'cargarCPC')
        }


        def path = servletContext.getRealPath("/") + "xls/"   //web-app/archivos
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

                fileName = "xlsCPC_" + new Date().format("yyyyMMdd_HHmmss")

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
                                errores += "<ol>"
                            }
                            println ">>>>>>>>>>>>>>>" + (j + 1)
                            row = s.getRow(j)
//                            println "row.length: ${row.length}"

                            if(j >= 7){
                                def codigo = row[0].getContents()
                                def descripcion = row[1].getContents()

                                println("cod " + codigo)
                                println("des " + descripcion)

                                def bCodigo = CodigoComprasPublicas.findAllByNumero(codigo)
                                if(bCodigo){
                                    println("codigo existente " + codigo)
                                }else{
                                    println("codigo NO existente " + codigo)
                                }

                            }

                        }
                    }
                }


            }else{

            }

        }

    }


    def uploadFile1() {

        println("params uf " + params)

        if (!params.fecha) {
            flash.message = "Ingrese la fecha"
            redirect(action: 'cargarCPC')
            return
        }


        def path = servletContext.getRealPath("/") + "xls/"   //web-app/archivos
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

                fileName = "xlsCPC_" + new Date().format("yyyyMMdd_HHmmss")

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

//                            def codigo = row[0].getContents()
//                            def descripcion = row[1].getContents()


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


} //fin controller
