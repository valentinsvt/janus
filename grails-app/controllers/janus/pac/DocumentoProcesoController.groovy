package janus.pac

import janus.Contrato
import janus.Obra
import org.springframework.dao.DataIntegrityViolationException

class DocumentoProcesoController extends janus.seguridad.Shield {

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

    String pathBiblioteca = "archivosBiblioteca"

    def errores() {
        return [params: params]
    }

    def index() {
        redirect(action: "list", params: params)
    } //index

    def downloadFile() {
        def doc = DocumentoProceso.get(params.id)
        def folder = pathBiblioteca
        def path = servletContext.getRealPath("/") + folder + File.separatorChar + doc.path

//        println servletContext.getRealPath("/")
//        println path

        def file = new File(path)
        if (file.exists()) {
            def b = file.getBytes()

            def ext = doc.path.split("\\.")
            ext = ext[ext.size() - 1]
//        println ext

            response.setContentType(ext == 'pdf' ? "application/pdf" : "image/" + ext)
            response.setHeader("Content-disposition", "attachment; filename=" + doc.path)
            response.setContentLength(b.length)
            response.getOutputStream().write(b)
        } else {

//            redirect(action: "errores")
            flash.clase = "alert-error"
            flash.message = "No se encontró el archivo " + " '" + doc.path + "'"
            redirect(action: "errores")


        }
    }

    def copiarDocumentos() {
        def contrato = null
        def concurso = Concurso.get(params.id)
        def documentosContrato = DocumentoProceso.findAllByConcurso(concurso)
        if (params.contrato) {
            contrato = Contrato.get(params.contrato)
//            println "copiar los docs de la obra aqui : Plano y Justificativo "
            /* copiar los docs de la obra aqui : Plano y Justificativo */
            def obra = contrato.obra
            if(obra.codigo.contains('-OF')) {
                def cdgo = obra.codigo[0..obra.codigo.indexOf('-OF') - 1]
//                println "nuevo código: $cdgo"
                obra = Obra.findByCodigo(cdgo)
            }
//            println "obra --> ${obra.codigo}"

            def documentos = DocumentoObra.findAllByObra(obra)
            def plano = documentos.findAll { it.nombre.toLowerCase().contains("plano") }
            def justificativo = documentos.findAll { it.nombre.toLowerCase().contains("justificativo") }

            def planoContrato = documentosContrato.findAll { it.nombre.toLowerCase().contains("plano") }
            def justificativoContrato = documentosContrato.findAll { it.nombre.toLowerCase().contains("justificativo") }

            def error = ""
            def msg = ""


            documentos.each {doc->
                def docu = DocumentoProceso.findAllByConcursoAndNombre(concurso,doc.nombre)
                if(!docu){
                    def pl = new DocumentoProceso(
                            etapa: Etapa.get(4),
                            concurso: concurso,
                            descripcion: doc.descripcion,
                            palabrasClave: doc.palabrasClave,
                            resumen: doc.resumen,
                            nombre: doc.nombre,
                            path: doc.path)
                    if (!pl.save(flush: true)) {
                        error += "<li>No se pudo copiar el archivo de plano de la obra: " + renderErrors(bean: pl) + "</li>"
                    } else {
                        msg += "<li>Se ha copiado exitosamente el archivo de plano de la obra</li>"
                    }
                }
            }

            if (error != "") {
                flash.clase = "alert-error"
                flash.message = "<ul>${error}</ul>"
            }
            if (msg != "") {
                flash.clase = "alert-success"
                flash.message = "<ul>${msg}</ul>"
            }
        }

        render "Ok"
    }

    def list() {
        def contrato = null
        def concurso = Concurso.get(params.id)
        def documentosContrato = DocumentoProceso.findAllByConcurso(concurso)
        if (params.contrato) {
            contrato = Contrato.get(params.contrato)
//            println "copiar los docs de la obra aqui : Plano y Justificativo "
            /* copiar los docs de la obra aqui : Plano y Justificativo */
            def obra = contrato.obra
/*
            def documentos = DocumentoObra.findAllByObra(obra)
            def plano = documentos.findAll { it.nombre.toLowerCase().contains("plano") }
            def justificativo = documentos.findAll { it.nombre.toLowerCase().contains("justificativo") }

            def planoContrato = documentosContrato.findAll { it.nombre.toLowerCase().contains("plano") }
            def justificativoContrato = documentosContrato.findAll { it.nombre.toLowerCase().contains("justificativo") }

            def error = ""
            def msg = ""
            if (planoContrato.size() == 0) {
                if (plano.size() == 0) {
                    error += "<li>No se ha registrado el documento 'Plano' en la biblioteca de la obra.</li>"
                } else {
                    if (plano.size() > 1) {
                        error += "<li>Se han encontrado ${plano.size()} documentos de plano de la obra. Se ha copiado el primero encontrado.</li>"
                    }
                    plano = plano.first()
                    def pl = new DocumentoProceso(
                            etapa: Etapa.get(4),
                            concurso: concurso,
                            descripcion: plano.descripcion,
                            palabrasClave: plano.palabrasClave,
                            resumen: plano.resumen,
                            nombre: plano.nombre,
                            path: plano.path)
                    if (!pl.save(flush: true)) {
                        error += "<li>No se pudo copiar el archivo de plano de la obra: " + renderErrors(bean: pl) + "</li>"
                    } else {
                        msg += "<li>Se ha copiado exitosamente el archivo de plano de la obra</li>"
                    }
                }
            }
            if (justificativoContrato.size() == 0) {
                if (justificativo.size() == 0) {
                    error += "<li>No se ha registrado el documento 'Justificativo de cantidad de obra' en la biblioteca de la obra.</li>"
                } else {
                    if (justificativo.size() > 1) {
                        error += "<li>Se han encontrado ${justificativo.size()} documentos de justificativo de cantidad de la obra. Se ha copiado el primero encontrado.</li>"
                    }
                    justificativo = justificativo.first()
                    def js = new DocumentoProceso(
                            etapa: Etapa.get(4),
                            concurso: concurso,
                            descripcion: justificativo.descripcion,
                            palabrasClave: justificativo.palabrasClave,
                            resumen: justificativo.resumen,
                            nombre: justificativo.nombre,
                            path: justificativo.path)
                    if (!js.save(flush: true)) {
                        error += "<li>No se pudo copiar el archivo de justificativo de cantidad de la obra: " + renderErrors(bean: js) + "</li>"
                    } else {
                        msg += "<li>Se ha copiado exitosamente el archivo de justificativo de cantidad de la obra</li>"
                    }
                }
            }
*/
/*
            if (error != "") {
                flash.clase = "alert-error"
                flash.message = "<ul>${error}</ul>"
            }
            if (msg != "") {
                flash.clase = "alert-success"
                flash.message = "<ul>${msg}</ul>"
            }
*/
        }

        return [concurso: concurso, documentoProcesoInstanceList: documentosContrato, params: params, contrato: contrato]
    } //list

    def form_ajax() {
        def concurso = Concurso.get(params.concurso)
        def contrato = Contrato.get(params.contrato)
        def show = params.show
        def documentoProcesoInstance = new DocumentoProceso(params)
        if (params.id) {
            documentoProcesoInstance = DocumentoProceso.get(params.id)
            if (!documentoProcesoInstance) {
                flash.clase = "alert-error"
                flash.message = "No se encontró Documento Proceso con id " + params.id
                redirect(action: "list")
                return
            } //no existe el objeto
        } //es edit
        documentoProcesoInstance.concurso = concurso
        def dscr = ""
        if(params.docuResp == 'R') dscr = 'Respaldo para obras adicionales'
        if(params.docuResp == 'C') dscr = 'Respaldo para Costo mas prcentaje'
        return [documentoProcesoInstance: documentoProcesoInstance, concurso: concurso, contrato: contrato, show: show,
        respaldo: dscr]
    } //form_ajax

    def save() {
//        println("params " + params)
        def documentoProcesoInstance
        if (params.id) {
            documentoProcesoInstance = DocumentoProceso.get(params.id)
            if (!documentoProcesoInstance) {
                flash.clase = "alert-error"
                flash.message = "No se encontró Documento Proceso con id " + params.id
                if (params.contrato) {
                    redirect(action: 'list', id: params.concurso.id, params: [contrato: params.contrato, show: params.show])
                } else {
                    redirect(action: 'list', id: params.concurso.id)
                }
                return
            }//no existe el objeto
            documentoProcesoInstance.properties = params
        }//es edit
        else {
            documentoProcesoInstance = new DocumentoProceso(params)
        } //es create

        /***************** file upload ************************************************/
        //handle uploaded file
//        println "upload....."
//        println params
        def folder = pathBiblioteca
        def path = servletContext.getRealPath("/") + folder   //web-app/archivos
        new File(path).mkdirs()

        def f = request.getFile('archivo')  //archivo = name del input type file

        if(!f && documentoProcesoInstance?.path){

        }else{

            //        println("---> " + f?.getOriginalFilename())
            if (f && !f.empty && f.getOriginalFilename() != '') {
                def fileName = f.getOriginalFilename() //nombre original del archivo

                def accepted = ["jpg", 'png', "pdf"]

//            def tipo = f.

                def ext = ''

                def parts = fileName.split("\\.")
                fileName = ""
                parts.eachWithIndex { obj, i ->
                    if (i < parts.size() - 1) {
                        fileName += obj
                    } else {
                        ext = obj
                    }
                }

                if (!accepted.contains(ext)) {
                    flash.message = "El archivo tiene que ser de tipo jpg, png o pdf"
                    flash.clase = "alert-error"
                    redirect(action: 'list', id: params.concurso.id)
                    return
                }

                fileName = fileName.tr(/áéíóúñÑÜüÁÉÍÓÚàèìòùÀÈÌÒÙÇç .!¡¿?&#°"'/, "aeiounNUuAEIOUaeiouAEIOUCc_")
                def archivo = fileName
                fileName = fileName + "." + ext

                def i = 0
                def pathFile = path + File.separatorChar + fileName
                def src = new File(pathFile)

                while (src.exists()) { // verifica si existe un archivo con el mismo nombre
                    fileName = archivo + "_" + i + "." + ext
                    pathFile = path + File.separatorChar + fileName
                    src = new File(pathFile)
                    i++
                }

                f.transferTo(new File(pathFile)) // guarda el archivo subido al nuevo path
                documentoProcesoInstance.path = fileName
            }else{
                flash.clase = "alert-error"
                flash.message = "Error al guardar el documento. No se ha cargado ningún archivo!"
                if (params.contrato) {
                    redirect(action: 'list', id: params.concurso.id, params: [contrato: params.contrato, show: params.show])
                } else {
                    redirect(action: 'list', id: params.concurso.id)
                }
                return
            }
        }



        /***************** file upload ************************************************/

        if (!documentoProcesoInstance.save(flush: true)) {
            flash.clase = "alert-error"
            def str = "<h4>No se pudo guardar Documento Proceso " + (documentoProcesoInstance.id ? documentoProcesoInstance.id : "") + "</h4>"

            str += "<ul>"
            documentoProcesoInstance.errors.allErrors.each { err ->
                def msg = err.defaultMessage
                err.arguments.eachWithIndex { arg, i ->
                    msg = msg.replaceAll("\\{" + i + "}", arg.toString())
                }
                str += "<li>" + msg + "</li>"
            }
            str += "</ul>"

            flash.message = str
            redirect(action: 'list', id: params.concurso.id)
            return
        }

        if (params.id) {
            flash.clase = "alert-success"
            flash.message = "Se ha actualizado correctamente Documento Proceso " + documentoProcesoInstance.id
        } else {
            flash.clase = "alert-success"
            flash.message = "Se ha creado correctamente Documento Proceso " + documentoProcesoInstance.id
        }
        if (params.contrato) {
            redirect(action: 'list', id: params.concurso.id, params: [contrato: params.contrato, show: params.show])
        } else {
            redirect(action: 'list', id: params.concurso.id)
        }
    } //save

    def show_ajax() {
        def documentoProcesoInstance = DocumentoProceso.get(params.id)
        if (!documentoProcesoInstance) {
            flash.clase = "alert-error"
            flash.message = "No se encontró Documento Proceso con id " + params.id
            redirect(action: "list")
            return
        }
        [documentoProcesoInstance: documentoProcesoInstance]
    } //show

    def delete() {
        def documentoProcesoInstance = DocumentoProceso.get(params.id)
        if (!documentoProcesoInstance) {
            flash.clase = "alert-error"
            flash.message = "No se encontró Documento Proceso con id " + params.id
            redirect(action: "list")
            return
        }
        def path = documentoProcesoInstance.path
        def cid = documentoProcesoInstance.concursoId
        try {
            documentoProcesoInstance.delete(flush: true)
            flash.clase = "alert-success"
            flash.message = "Se ha eliminado correctamente Documento Proceso " + documentoProcesoInstance.id
            def folder = "archivos"
            path = servletContext.getRealPath("/") + folder + File.separatorChar + path
            def file = new File(path)
            file.delete()

            if (params.contrato) {
                redirect(action: "list", id: cid, params: [contrato: params.contrato, show: params.show])
            } else {
                redirect(action: "list", id: cid)
            }
        }
        catch (DataIntegrityViolationException e) {
            flash.clase = "alert-error"
            flash.message = "No se pudo eliminar Documento Proceso " + (documentoProcesoInstance.id ? documentoProcesoInstance.id : "")

            if (params.contrato) {
                redirect(action: "list", id: cid, params: [contrato: params.contrato])
            } else {
                redirect(action: "list", id: cid)
            }
        }
    } //delete
} //fin controller
