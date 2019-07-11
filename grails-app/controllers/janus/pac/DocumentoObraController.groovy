package janus.pac

import janus.Obra
import org.springframework.dao.DataIntegrityViolationException

class DocumentoObraController extends janus.seguridad.Shield {

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

    String pathBiblioteca = "archivosBiblioteca"

    def index() {
        redirect(action: "list", params: params)
    } //index

    def downloadFile() {
        def doc = DocumentoObra.get(params.id)
        def folder = pathBiblioteca
        def path = servletContext.getRealPath("/") + folder + File.separatorChar + doc.path

//        println servletContext.getRealPath("/")
        println path

        def file = new File(path)
        if(file.exists()) {
            def b = file.getBytes()

            def ext = doc.path.split("\\.")
            ext = ext[ext.size() - 1]
//        println ext

            response.setContentType(ext == 'pdf' ? "application/pdf" : "image/" + ext)
            response.setHeader("Content-disposition", "attachment; filename=" + doc.path)
            response.setContentLength(b.length)
            response.getOutputStream().write(b)
        }
    }


    def list() {
        [documentoObraInstanceList: DocumentoObra.list(params), params: params]

        def obra = Obra.get(params.id)
        def documentos = DocumentoObra.findAllByObra(obra)

        def plano = documentos.findAll { it.nombre.toLowerCase().contains("plano") }
        def justificativo = documentos.findAll { it.nombre.toLowerCase().contains("justificativo") }
        def error = ""
        if (plano.size() == 0)
            error = "<li>No se ha registrado el documento 'Plano' en la biblioteca de la obra.</li>"
        if (justificativo.size() == 0)
            error += "<li>No se ha registrado el documento 'Justificativo de cantidad de obra' en la biblioteca de la obra.</li>"

        if (error != "") {
            flash.clase = "alert-error"
            flash.message = "<ul>${error}</ul>"
        }

        return [obra: obra, documentoObraInstanceList: documentos, params: params]
    } //list

    def form_ajax() {
        def obra = Obra.get(params.obra)
        def documentoObraInstance = new DocumentoObra(params)
        if (params.id) {
            documentoObraInstance = DocumentoObra.get(params.id)
            if (!documentoObraInstance) {
                flash.clase = "alert-error"
                flash.message = "No se encontró Documento Obra con id " + params.id
                redirect(action: "list")
                return
            } //no existe el objeto
        } //es edit
        documentoObraInstance.obra = obra
        return [documentoObraInstance: documentoObraInstance, obra: obra]
    } //form_ajax

    def save() {
        def documentoObraInstance
        if (params.id) {
            documentoObraInstance = DocumentoObra.get(params.id)
            if (!documentoObraInstance) {
                flash.clase = "alert-error"
                flash.message = "No se encontró Documento Proceso con id " + params.id
                redirect(action: 'list', id: params.concurso.id)
                return
            }//no existe el objeto
            documentoObraInstance.properties = params
        }//es edit
        else {
            documentoObraInstance = new DocumentoObra(params)
        } //es create

        /***************** file upload ************************************************/
        //handle uploaded file
//        println "upload....."
//        println params
        def folder = pathBiblioteca
        def path = servletContext.getRealPath("/") + folder   //web-app/archivos
        new File(path).mkdirs()

        def f = request.getFile('archivo')  //archivo = name del input type file
        if (f && !f.empty) {
            def fileName = f.getOriginalFilename() //nombre original del archivo

            def accepted = ["jpg", 'png', "pdf", "dwg", "xls", "xlsx", "doc", "docx"]

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
            ext = ext.toLowerCase()
            if (!accepted.contains(ext)) {
                flash.message = "El archivo tiene que ser de tipo jpg, png o pdf"
                flash.clase = "alert-error"
                redirect(action: 'list', id: params.obra.id)
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
            documentoObraInstance.path = fileName
        }
        /***************** file upload ************************************************/

        if (!documentoObraInstance.save(flush: true)) {
            flash.clase = "alert-error"
            def str = "<h4>No se pudo guardar Documento Obra " + (documentoObraInstance.id ? documentoObraInstance.id : "") + "</h4>"

            str += "<ul>"
            documentoObraInstance.errors.allErrors.each { err ->
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
            flash.message = "Se ha actualizado correctamente Documento Obra " + documentoObraInstance.id
        } else {
            flash.clase = "alert-success"
            flash.message = "Se ha creado correctamente Documento Obra " + documentoObraInstance.id
        }
        redirect(action: 'list', id: params.obra.id)
    } //save

//    def save() {
//        def documentoObraInstance
//        if (params.id) {
//            documentoObraInstance = DocumentoObra.get(params.id)
//            if (!documentoObraInstance) {
//                flash.clase = "alert-error"
//                flash.message = "No se encontró Documento Obra con id " + params.id
//                redirect(action: 'list')
//                return
//            }//no existe el objeto
//            documentoObraInstance.properties = params
//        }//es edit
//        else {
//            documentoObraInstance = new DocumentoObra(params)
//        } //es create
//        if (!documentoObraInstance.save(flush: true)) {
//            flash.clase = "alert-error"
//            def str = "<h4>No se pudo guardar Documento Obra " + (documentoObraInstance.id ? documentoObraInstance.id : "") + "</h4>"
//
//            str += "<ul>"
//            documentoObraInstance.errors.allErrors.each { err ->
//                def msg = err.defaultMessage
//                err.arguments.eachWithIndex { arg, i ->
//                    msg = msg.replaceAll("\\{" + i + "}", arg.toString())
//                }
//                str += "<li>" + msg + "</li>"
//            }
//            str += "</ul>"
//
//            flash.message = str
//            redirect(action: 'list')
//            return
//        }
//
//        if (params.id) {
//            flash.clase = "alert-success"
//            flash.message = "Se ha actualizado correctamente Documento Obra " + documentoObraInstance.id
//        } else {
//            flash.clase = "alert-success"
//            flash.message = "Se ha creado correctamente Documento Obra " + documentoObraInstance.id
//        }
//        redirect(action: 'list')
//    } //save

    def show_ajax() {
        def documentoObraInstance = DocumentoObra.get(params.id)
        if (!documentoObraInstance) {
            flash.clase = "alert-error"
            flash.message = "No se encontró Documento Obra con id " + params.id
            redirect(action: "list")
            return
        }
        [documentoObraInstance: documentoObraInstance]
    } //show

    def delete() {
//        println "..." + params
        def documentoObraInstance = DocumentoObra.get(params.id)
        if (!documentoObraInstance) {
            flash.clase = "alert-error"
            flash.message = "No se encontró Documento Obra con id " + params.id
            redirect(action: "list", id: params.obra_id)
            return
        }
        def path = documentoObraInstance?.path
//        def cid = documentoObraInstance?.concursoId
        try {
            documentoObraInstance.delete(flush: true)
            flash.clase = "alert-success"
            flash.message = "Se ha eliminado correctamente Documento Obra " + documentoObraInstance.id

            def folder = pathBiblioteca
            path = servletContext.getRealPath("/") + folder + File.separatorChar + path
            def file = new File(path)
            file.delete()
            redirect(action: "list", id: params.obra_id)
        }
        catch (DataIntegrityViolationException e) {
            flash.clase = "alert-error"
            flash.message = "No se pudo eliminar Documento Obra " + (documentoObraInstance.id ? documentoObraInstance.id : "")
            params.id = params.obra_id
            redirect(action: "list", id: params.obra_id)
        }
    } //delete
} //fin controller
