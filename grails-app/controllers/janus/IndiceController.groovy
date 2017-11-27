package janus

import janus.ejecucion.PeriodosInec
import janus.ejecucion.ValorIndice
import janus.pac.Anio
import jxl.Cell

//import janus.pac.PeriodoValidez
import jxl.Sheet
import jxl.Workbook
import jxl.WorkbookSettings
import org.springframework.dao.DataIntegrityViolationException

class IndiceController extends janus.seguridad.Shield {

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]
    def dbConnectionService

    def index() {
        redirect(action: "list", params: params)
    } //index

    def list() {
//        [indiceInstanceList: Indice.list(params), params: params]
        def indiceInstance = Indice.withCriteria {
            and {
                order('tipoIndice', 'desc')
                order('descripcion', 'asc')
            }
        }
        [indiceInstanceList: indiceInstance, params: params]
    } //list

    def form_ajax() {
        def indiceInstance = new Indice(params)
        if (params.id) {
            indiceInstance = Indice.get(params.id)
            if (!indiceInstance) {
                flash.clase = "alert-error"
                flash.message = "No se encontró Indice con id " + params.id
                redirect(action: "list")
                return
            } //no existe el objeto
        } //es edit
        return [indiceInstance: indiceInstance]
    } //form_ajax

    def form_adicional() {
        def indiceInstance = new Indice(params)
        return [indiceInstance: indiceInstance]
    } //form_ajax


    def subirIndice() {
    }

    def uploadFile() {
//
//        println("upload: " + params)

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
                fileName = fileName.tr(/áéíóúñÑÜüÁÉÍÓÚàèìòùÀÈÌÒÙÇç .!¡¿?&#°"'/, "aeiounNUuAEIOUaeiouAEIOUCc_")

                def fn = fileName
                fileName = fileName + "." + ext

                def pathFile = path + fileName
                def src = new File(pathFile)

                def i = 1
//                while (src.exists()) {
//                    pathFile = path + fn + "_" + i + "." + ext
//                    src = new File(pathFile)
//                    i++
//                }
//            println pathFile
                def file = new File(pathFile)
                f.transferTo(file) // guarda el archivo subido al nuevo path

                //AQUI el archivo ya esta copiado en web-app/xls/nombreArchivo.xls

                def filaInicial = 5;
                def html = "";
                WorkbookSettings ws = new WorkbookSettings();
                ws.setEncoding("Cp1252");
//                Workbook workbook = Workbook.getWorkbook(new File(my_name), ws);

                Workbook workbook = Workbook.getWorkbook(file, ws)

                def ignorar = ['MISCELANEOS', 'D E N O M I N A C I Ó N']
                workbook.getNumberOfSheets().times { sheet ->
                    Sheet s = workbook.getSheet(sheet)
                    if (!s.getSettings().isHidden()) {
//                        println s.getName()
                        html += "<h2>Hoja " + (sheet + 1) + ": " + s.getName() + "</h2>"
                        Cell[] row = null
                        s.getRows().times { j ->
                            row = s.getRow(j)
                            def celdas = row.length
//                            println(row)
                            if (celdas > 0) {
                                if (j >= filaInicial) {
//                                    println("fila " + (j + 1) + "   " + celdas)
                                    def descripcion = row[0].getContents().toString().trim()
                                    descripcion = descripcion.replaceAll(/ {2,}/, ' ')
//                                    descripcion = descripcion.
                                    if (descripcion != '' && !ignorar.contains(descripcion) && !descripcion.startsWith("*")) {
//                                    def valor = row[3]
//                                        println(descripcion)
                                        def indice = Indice.findAllByDescripcionIlike(descripcion)
//                                        println(indice)
                                        def bandera = false;
                                        if (indice.size() == 0) {
                                            def codigo = descripcion[0..2]
                                            def ind = Indice.findAllByCodigo(codigo)
                                            if (ind.size() > 0) {
                                                def par = descripcion.split(" ")
                                                if (par.size() > 1) {
                                                    if (par[1]?.trim() != "") {
//                                                        println "par " + par[1]
                                                        if (par[1].size() > 1)
                                                            codigo += "-" + par[1][0..1]
                                                        else {
                                                            codigo += "-" + par[1]
                                                        }

                                                    }
                                                }
                                            }
                                            indice = new Indice([
                                                    tipoIndice: TipoIndice.get(1),
                                                    descripcion: descripcion,
                                                    codigo: codigo
                                            ])
                                            if (!indice.save(flush: true)) {
                                                println "indice controller l 139: "+"ERROR al guardar el indice: " + indice.errors
                                                html += 'fila ' + (j + 1) + ' ERROR Indice no creado' + renderErrors(bean: indice)
                                            } else {
                                                html += 'fila ' + (j + 1) + ' Indice creado:' + indice.id + "<br/>"
                                                bandera = true;
                                            }
                                        } else if (indice.size() == 1) {
                                            indice = indice[0];
                                            bandera = true;
                                        } else {
                                            html += 'fila ' + (j + 1) + ' Indice duplicado:' + indice.id + "<br/>"
                                        }
//                                        println(indice)
                                        def valor
                                        if (celdas > 2) {
                                            valor = row[3].getContents();
                                            try {
                                                valor = valor.toDouble()
//                                                println(valor)
                                            }
                                            catch (e) {
//                                                println(e)
                                                bandera = false
                                                html += 'fila ' + (j + 1) + ' Error en el valor: ' + valor + "<br/>"
                                            }
                                        } else {
                                            html += 'fila ' + (j + 1) + ' Fila no tiene valor' + "<br/>"
                                            bandera = false;
                                        }
                                        if (bandera) {
                                            def fecha = janus.ejecucion.PeriodosInec.get(params.periodo)
                                            def valores = ValorIndice.countByIndiceAndPeriodo(indice, fecha)
                                            if (valores == 0) {
                                                def valorIndice = new ValorIndice([
                                                        indice: indice,
                                                        valor: valor,
                                                        periodo: fecha
                                                ])
                                                if (!valorIndice.save(flush: true)) {
                                                    println("indice controller l 178: "+"error al guardar el valor del indice" + valorIndice.errors)
                                                    html += 'fila ' + (j + 1) + ' ERROR valor no creado' + renderErrors(bean: valorIndice)
                                                }
                                            } else {
//                                                def ind =  ValorIndice.findByIndiceAndPeriodo(indice,fecha)
//                                                ind.valor= valor
//                                                ind.save(flush: true)
                                                html += 'fila ' + (j + 1) + ' valor ya existe ' + '<br/>'
//                                                println(valores)
                                            }
                                        }
                                    } //if descrcion ok
//                                    println(valor)
//                                    println("--------------")
                                }  //if fila > fila inicial
                            } //if celdas>0
                        } //rows.each
                    } //hoja !hidden
                } //hojas.each
//                println(html)

//              render html

                return [html: html]

            } else {
                flash.message = "Seleccione un archivo Excel xls para procesar (archivos xlsx deben ser convertidos a xls primero)"
                redirect(action: 'subirIndice')
            }
        } else {
            flash.message = "Seleccione un archivo para procesar"
            redirect(action: 'subirIndice')
//            println "NO FILE"
        }
    }


    def grabar() {
        println params
        guardar()
        //redirect(controller: 'FormulaPolinomica', action: 'coeficientes')
        render "ok"
    }

    def save() {
        /*def indiceInstance
        if (params.id) {
            indiceInstance = Indice.get(params.id)
            if (!indiceInstance) {
                flash.clase = "alert-error"
                flash.message = "No se encontró Indice con id " + params.id
                redirect(action: 'list')
                return
            }//no existe el objeto
            indiceInstance.properties = params
        }//es edit
        else {
            indiceInstance = new Indice(params)
        } //es create
        if (!indiceInstance.save(flush: true)) {
            flash.clase = "alert-error"
            def str = "<h4>No se pudo guardar Indice " + (indiceInstance.id ? indiceInstance.id : "") + "</h4>"

            str += "<ul>"
            indiceInstance.errors.allErrors.each { err ->
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
            flash.message = "Se ha actualizado correctamente Indice " + indiceInstance.id
        } else {
            flash.clase = "alert-success"
            flash.message = "Se ha creado correctamente Indice " + indiceInstance.id
        }*/
        guardar()
        redirect(action: 'list')
    } //save

    def guardar() {
        def indiceInstance
        if (params.id) {
            indiceInstance = Indice.get(params.id)
            if (!indiceInstance) {
                flash.clase = "alert-error"
                flash.message = "No se encontró Indice con id " + params.id
                redirect(action: 'list')
                return
            }//no existe el objeto
            indiceInstance.properties = params
        }//es edit
        else {
            indiceInstance = new Indice(params)
        } //es create
        if (!indiceInstance.save(flush: true)) {
            flash.clase = "alert-error"
            def str = "<h4>No se pudo guardar Indice " + (indiceInstance.id ? indiceInstance.id : "") + "</h4>"

            str += "<ul>"
            indiceInstance.errors.allErrors.each { err ->
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
            flash.message = "Se ha actualizado correctamente Indice " + indiceInstance.descripcion
        } else {
            flash.clase = "alert-success"
            flash.message = "Se ha creado correctamente Indice " + indiceInstance.descripcion
        }
    } //save

    def show_ajax() {
        def indiceInstance = Indice.get(params.id)
        if (!indiceInstance) {
            flash.clase = "alert-error"
            flash.message = "No se encontró Indice con id " + params.id
            redirect(action: "list")
            return
        }
        [indiceInstance: indiceInstance]
    } //show

    def delete() {
        def indiceInstance = Indice.get(params.id)
        if (!indiceInstance) {
            flash.clase = "alert-error"
            flash.message = "No se encontró Indice con id " + params.id
            redirect(action: "list")
            return
        }

        try {
            indiceInstance.delete(flush: true)
            flash.clase = "alert-success"
            flash.message = "Se ha eliminado correctamente Indice " + indiceInstance.descripcion
            redirect(action: "list")
        }
        catch (DataIntegrityViolationException e) {
            flash.clase = "alert-error"
            flash.message = "No se pudo eliminar Indice " + (indiceInstance.id ? indiceInstance.id : "")
            redirect(action: "list")
        }
    } //delete

    def valorIndice = {
        def cn = dbConnectionService.getConnection()
        def anio = Anio.findByAnio(new Date().format('yyyy'))

        def tx_sql = "select * from sp_vlin(${janus.pac.Anio.get(params?.anio ?: anio?.id)?.anio})"
        def datos = cn.rows(tx_sql.toString())

        cn.close()

        //println "año: $anio.id"
        [datos: datos, anio: params.anio?: anio?.id]
    }

    def editarIndices() {

    }

    def tablaValores() {
        def cn = dbConnectionService.getConnection()
        def cn1 = dbConnectionService.getConnection()
        //println params

//        def sqlTx = "SELECT indc__id, indcdscr, 0 valor from indc order by indcdscr limit 10"
        def sqlTx = "SELECT indc__id, indcdscr, 0 valor from indc order by tpin__id desc, indcdscr"
        def txValor = ""
        def cont = 0
        def editar = ""
        def prin = params.prin.toInteger()
        // obtiene el periodo anterior
        def fcha = PeriodosInec.get(prin).fechaInicio - 1
        def prinAnterior = PeriodosInec.findByFechaFinBetween(fcha, fcha + 2)?.id
        def periodos = [prinAnterior, prin]
//        println prinAnterior

        def html = "<table class=\"table table-bordered table-striped table-hover table-condensed\" id=\"tablaPrecios\">"
        html += "<thead>"
        html += "<tr>"
        html += "<th>Indice_id</th>"
        html += "<th>Nombre del Indice</th>"
        html += "<th>${PeriodosInec.get(prinAnterior)?.descripcion}</th>"
        html += "<th>${PeriodosInec.get(prin).descripcion}</th>"
        html += "<th>Copiar Anterior</th>"

        def body = ""
        cn.eachRow(sqlTx.toString()) { d ->
            body += "<tr>"
            body += "<td>${d.indc__id}</td>"
            body += "<td>${d.indcdscr}</td>"

            cont = 0
            def prec = "", p = 0, rubro = "new"
            while (cont < 2) {
                rubro = "new"
                txValor = "select vlin__id, vlinvalr, vlin.prin__id from vlin, prin where vlin.prin__id = ${periodos[cont]} and " +
                    "vlin.indc__id = ${d.indc__id} and prin.prin__id = vlin.prin__id order by prinfcin"
                //println txValor
                prec = g.formatNumber(number: 0.0, maxFractionDigits: 2, minFractionDigits: 2, locale: "ec")
                p = 0.0
                editar = periodos[cont]? "editable" : ""
                cn1.eachRow(txValor.toString()) { v ->
                    if (v.vlinvalr != null) {
                        prec = g.formatNumber(number: v.vlinvalr, maxFractionDigits: 2, minFractionDigits: 2, locale: "ec")
                        p = v.vlinvalr
                        rubro = v.vlin__id
                    }
                }
                body += "<td class='${editar} number' data-original='${p}' data-prin='${periodos[cont]}' " +
                        "data-id='${rubro}' data-indc='${d.indc__id}' data-valor='${p}'>" + prec + '</td>'
                if(cont==1){
                    body += "<td style='text-align:center'><a class='btn btn-small btn-show btn-ajax btCopia' href='#' rel='tooltip' title='Copiar' " +
                            "</a>Copiar</td>"
                }


                cont++
            }
/*
            while (cont < 2) {
                prec = g.formatNumber(number: 0.0, maxFractionDigits: 5, minFractionDigits: 5, locale: "ec")
                p = 0.0
                editar = periodos[cont]? "editable" : ""
                body += "<td class='${editar} number' data-original='${p}' data-prin='${periodos[cont]}' " +
                    "data-id='${rubro}' data-indc='${d.indc__id}' data-valor='0.0'>" + prec + '</td>'
                cont++
            }
*/

        }
        html += "</tr>"
        html += "</thead>"
        html += "<tbody>"
        //println html

        cn.close()
        cn1.close()
        html += body
        html += "<script type=\'text/javascript\'>  \$(function () { \$('.btCopia').click(function () {" +
//                "console.log('--->' + \$(this).parent().prev().data('valor'));" +
//                "console.log('--->' + \$(this).parent().prev().prev().data('valor'));" +
                "var vl = \$(this).parent().prev().prev().data('valor');" +
                "\$(this).parent().prev().data('valor',vl).text(vl);" +
                "}); });</script>"

        html += "</tbody>"
        html += "</table>"
        //println html
        [html: html]
    }

    def actualizaVlin() {
        println "actualizaVlin: " + params
//        println("clase " + params?.item?.class)
        //formato de id:###/new _ prin _ indc _ valor
        if(params?.item?.class == java.lang.String) {
            params?.item = [params?.item]
        }

        def oks = "", nos = ""
        params.item.each {
//            println "Procesa: " + it

            def vlor = it.split("_")
            def nuevo = new ValorIndice()
//            println "vlor: " + vlor
            def existe = ValorIndice.findByPeriodoAndIndice(PeriodosInec.get(vlor[1].toInteger()), Indice.get(vlor[2].toInteger()))
            println "inidice: ${existe?.indice}"
/*
            if (vlor[0] != "new") {
                nuevo = ValorIndice.get(vlor[0].toInteger())
            }
*/
            if(existe){
                nuevo = ValorIndice.get(existe.id)
            }

            nuevo.periodo = PeriodosInec.get(vlor[1])
            nuevo.indice = Indice.get(vlor[2])
            nuevo.valor = vlor[3].toDouble()
//            println "periodo: ${nuevo.periodo}, indice: ${nuevo.indice}, valor: ${nuevo.valor}"
            if (!nuevo.save(flush: true)) {
                println "indice controller l 395: "+"error " + vlor
                if (nos != "") {
                    nos += ","
                }
                nos += "#" + vlor[0]
            } else {
                if (oks != "") {
                    oks += ","
                }
                oks += "#" + vlor[0]
            }
//            println nuevo.valor
        }
        render "ok"
    }

    def borrarIndices() {

    }

    def repetidos() {
        println "repetidos: $params"
        def cn = dbConnectionService.getConnection()
        def cn1 = dbConnectionService.getConnection()
        //println params

//        def sqlTx = "SELECT indc__id, indcdscr, 0 valor from indc order by indcdscr limit 10"
        def sqlTx = "select indc__id, prin__id from vlin group by prin__id, indc__id having count(*) > 1 " +
                "order by indc__id"
        def txValor = ""
        def dscr = ""
        def prin = params.prin.toInteger()
        // obtiene el periodo anterior
        def registros = 0

        def html = "<table class=\"table table-bordered table-striped table-hover table-condensed\" id=\"tablaPrecios\">"
        html += "<thead>"
        html += "<tr>"
        html += "<th>Id</th>"
        html += "<th>Nombre del Indice</th>"
        html += "<th>Periodo</th>"
        html += "<th>Valor</th>"
        html += "<th>Borrar</th>"
        html += "</thead>"

        def body = ""
        cn.eachRow(sqlTx.toString()) { d ->
            body += "<tr>"

            def prec = "", p = 0, rubro = "new"
            txValor = "select vlin__id, vlinvalr, vlin.prin__id, prindscr, indcdscr from vlin, prin, indc " +
                    "where vlin.indc__id = ${d.indc__id} and vlin.prin__id = ${d.prin__id} and " +
                    "prin.prin__id = vlin.prin__id and " +
                    "indc.indc__id = vlin.indc__id order by indcdscr, prinfcin"
//            println txValor
            prec = g.formatNumber(number: 0.0, maxFractionDigits: 2, minFractionDigits: 2, locale: "ec")
            p = 0.0
            cn1.eachRow(txValor.toString()) { v ->
                if (v.vlinvalr != null) {
                    prec = g.formatNumber(number: v.vlinvalr, maxFractionDigits: 2, minFractionDigits: 2, locale: "ec")
                    p = v.vlinvalr
                    rubro = v.vlin__id
                } else {
                    prec = 0
                    p = 0
                    rubro = v.vlin__id
                }
                dscr = v.indcdscr
                body += "<td>${v.vlin__id}</td><td>${dscr}</td><td>${v.prindscr}</td><td class='number' data-id='${v.vlin__id}'>" +
                        prec + '</td>'
                body += "<td style='text-align:center'><a class='btn btn-small btn-info btn-ajax btCopia' " +
                     "href='#' rel='tooltip' title='Copiar' </a>Borrar</td></tr>"
                registros++
            }
        }
        html += "<tbody>"
        //println html

        cn.close()
        cn1.close()
        html += body
        html += "Registros repetidos: ${registros}"
        html += "<script type=\'text/javascript\'>  \$(function () { \$('.btCopia').click(function () {" +
                "var id = \$(this).parent().prev().data('id');" +
                "\$.ajax({" +
                "type: 'POST', url: \"${createLink(action: 'borrarVlin')}\", data: { vlin_id: id }, " +
                "success : function (msg) {" +
                "  location.reload();" +
                "}" +
                "});" +
                "}); });</script>"

        html += "</tbody>"
        html += "</table>"
        [html: html]
    }

    def borraRepetidos() {
        def cn = dbConnectionService.getConnection()
        if (cn.execute("select * from sp_borra_vlin()")) {
            flash.message = "Se ha borrado los regisitros Repetidos"
            render "ok"
        } else {
            render "error"
        }
    }

    def borrarVlin() {
        println "borrarVlin: $params"
        ValorIndice.get(params.vlin_id)?.delete()
        render "ok"
    }


} //fin controller
