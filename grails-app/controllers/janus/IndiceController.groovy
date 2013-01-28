package janus

import janus.pac.PeriodoValidez
import janus.pac.ValorIndice
import jxl.Cell
import jxl.Sheet
import jxl.Workbook
import jxl.WorkbookSettings
import org.springframework.dao.DataIntegrityViolationException

class IndiceController extends janus.seguridad.Shield {

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

    def index() {
        redirect(action: "list", params: params)
    } //index

    def list() {
        params.max = Math.min(params.max ? params.int('max') : 10, 100)
        [indiceInstanceList: Indice.list(params), indiceInstanceTotal: Indice.count(), params: params]
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

    //subir Archivo

    def subirIndice(){
    }

    def uploadFile() {

        println("upload: "+params)

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
                        println s.getName()

                        html += "<h2>Hoja " + (sheet + 1) + ": " + s.getName() + "</h2>"
                        Cell[] row = null

                        s.getRows().times { j ->
                            row = s.getRow(j)

                            def celdas = row.length
//                            println(row)

                            if (celdas > 0) {

                                if (j >= filaInicial){

                                    println("fila "+(j+1)+"   " +celdas)
                                    def descripcion = row[0].getContents().toString().trim()
                                    descripcion = descripcion.replaceAll(/ {2,}/,' ')
//                                    descripcion = descripcion.
                                    if (descripcion != '' && !ignorar.contains(descripcion) && !descripcion.startsWith("*")){

//                                    def valor = row[3]
//
                                        println(descripcion)


                                        def indice = Indice.findAllByDescripcionIlike(descripcion)

                                        println(indice)

                                        def bandera = false;


                                        if (indice.size() == 0){


                                            indice = new Indice([
                                                    tipoInstitucion: TipoInstitucion.get(1),
                                                    descripcion: descripcion,
                                                    codigo: descripcion[0..2]
                                            ])
                                            if(!indice.save(flush: true)) {
                                                println "ERROR al guardar el indice: "+indice.errors

                                                html+='fila '+(j+1)+' ERROR Indice no creado' + renderErrors(bean: indice)
                                            }
                                            else {

                                                html+='fila '+(j+1)+' Indice creado:'  +indice.id + "<br/>"

                                                bandera = true;

                                            }


                                        }
                                        else if (indice.size() == 1){

                                            indice = indice[0];

                                            bandera = true;

                                        }
                                        else{

                                            html+='fila '+(j+1)+' Indice duplicado:' + indice.id   + "<br/>"
                                        }

                                        println(indice)
                                        def valor
                                        if (celdas > 2){


                                            valor = row[3].getContents();
                                           try{

                                               valor = valor.toDouble()
                                               println(valor)
                                           }

                                           catch (e){

                                               println(e)
                                               bandera = false
                                               html+='fila '+(j+1)+' Error en el valor: ' + valor   + "<br/>"


                                           }
                                        }else {

                                            html+='fila '+(j+1)+' Fila no tiene valor'  + "<br/>"

                                            bandera = false;


                                        }

                                        if (bandera){

                                           def fecha= PeriodoValidez.get(params.periodo)
                                            def valores = ValorIndice.countByIndiceAndFecha(indice,fecha)

                                             if (valores == 0){

                                                 def valorIndice = new ValorIndice([

                                                         indice: indice,
                                                         valor: valor,
                                                         fecha: fecha


                                                 ])

                                                 if (!valorIndice.save(flush: true)){


                                                     println("error al guardar el valor del indice" + valorIndice.errors)

                                                     html+='fila '+(j+1)+' ERROR valor no creado' + renderErrors(bean: valorIndice)
                                                 }


                                             }else {


                                                 html+='fila '+(j+1)+' valor ya existe ' + '<br/>'
                                                 println(valores)
                                             }



                                        }


                                    } //if descrcion ok
//                                    println(valor)
                                    println("--------------")
                                }  //if fila > fila inicial
                            } //if celdas>0
                        } //rows.each
                    } //hoja !hidden
                } //hojas.each

                println(html)

//              render html

                return [html: html]

            }  else {
                flash.message = "Seleccione un archivo Excel xls para procesar (archivos xlsx deben ser convertidos a xls primero)"
                redirect(action: 'subirIndice')
            }
        } else {
            flash.message = "Seleccione un archivo para procesar"
            redirect(action: 'subirIndice')
//            println "NO FILE"
        }
    }


    //procesar archivo subido

//    def procesa() {
//
//
//                    if (row.length > 0) {
//                        if (i == 0) {
//                            row.length.times { j ->
//                                if (!row[j].isHidden()) {
//                                    cols[j] = row[j].getContents()
//                                }//if row ! hidden
//                            } //foreach cell
////                            println cols
//                        } //if i==0
//                        else {
//                            datos[i - 1] = []
//                            row.length.times { j ->
//                                if (!row[j].isHidden()) {
//                                    datos[i - 1][j] = row[j].getContents()
//                                    if (cols[j] =~ "mail") {
//                                        mails[i - 1] = row[j].getContents()
//                                    }
//                                } //cel ! hidden
//                            } //foreach cell
//                        } //i>0
//                    }//if row.length > 0
////                    print "\n"
//                }//foreach row
//
//                datos.eachWithIndex { dd, d ->
//                    def t = template
//                    cols.eachWithIndex { cc, c ->
////                        println cc + "  " + dd[c]
//                        t = t.replaceAll("&lt;" + cc + "&gt;", dd[c])
//                    }
//                    def t2 = t
//                    tipoCarta.imagenes.each { img ->
//                        t2 = t2.replaceAll(grailsAttributes.getApplicationUri(request) + "/imgs/" + img.path, "cid:" + img.cid)
//                    }
//                    t2 = t2.replaceAll('\\?_debugResources=[a-z]&[a-z;0-9=]+\\"', '"')
//                    println "SENDING " + (d + 1) + "/" + datos.size() + " TO: " + mails[d]
//                    def b = true
////
////                    mailService.sendMail {
////                        to "luzma_87@yahoo.com"
////                        subject "Hello"
////                        html '<b>Hello</b> World'
////                    }
//                    try {
//                        mailService.sendMail {
//                            multipart true
//                            to mails[d]
//                            subject subjectMail
////                            html t2
//                            html g.render(template: "mail", model: [t: t2])
//
//                            tipoCarta.imagenes.each { img ->
//                                attachBytes grailsAttributes.getApplicationUri(request) + "/imgs/" + img.path, 'image/jpg', new File(servletContext.getRealPath("/") + "imgs/" + img.path).readBytes()
//                                inline img.cid, 'image/jpg', new File(servletContext.getRealPath("/") + "imgs/" + img.path)
//                            }
//                        }
//                    } catch (e) {
//                        b = false
//                        println "error : " + e
//                    }
//
//                    println(b ? "SENT!" : "NOT SENT...")
//
//                    htmlInfo += "<div class='completo ui-widget-content ui-corner-all'>"
//                    htmlInfo += "<div class='mail open ui-widget-header ui-corner-top'>"
//                    htmlInfo += "Para: " + mails[d] + " ; Subject: " + subjectMail + " ; Estado: " + (b ? "Enviado" : "No enviado")
//                    htmlInfo += "</div>"
//                    htmlInfo += "<div class='text ui-widget-content ui-corner-bottom'>" + t + "</div>"
//                    htmlInfo += "</div>"
//
//                    def usu = Usuario.get(session.usuario.id)
//                    def env = new Envio()
//                    env.usuario = usu
//                    env.tipoCarta = tipoCarta
//                    env.texto = t
//                    env.enviado = b
//                    env.fecha = new Date()
//                    env.mail = mails[d]
//                    if (!env.save()) {
//                        println "error save env: " + env.errors
//                    }
//                }
//            } // sheet ! hidden
//        } //foreach sheet
//        session.html = htmlInfo
//        redirect(controller: 'procesaExcel', action: "show")
//    }
//







    def save() {
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
                err.arguments.eachWithIndex {  arg, i ->
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
        }
        redirect(action: 'list')
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
            flash.message = "Se ha eliminado correctamente Indice " + indiceInstance.id
            redirect(action: "list")
        }
        catch (DataIntegrityViolationException e) {
            flash.clase = "alert-error"
            flash.message = "No se pudo eliminar Indice " + (indiceInstance.id ? indiceInstance.id : "")
            redirect(action: "list")
        }
    } //delete
} //fin controller
