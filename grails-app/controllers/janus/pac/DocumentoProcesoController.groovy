package janus.pac

import org.springframework.dao.DataIntegrityViolationException

class DocumentoProcesoController extends janus.seguridad.Shield {

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

    def index() {
        redirect(action: "list", params: params)
    } //index

    def downloadFile() {
        def doc = DocumentoProceso.get(params.id)
        def folder = "archivos"
        def path = servletContext.getRealPath("/") + folder + File.separatorChar + doc.path

//        println servletContext.getRealPath("/")
//        println path

        def file = new File(path)
        def b = file.getBytes()

        def ext = doc.path.split("\\.")
        ext = ext[ext.size() - 1]
//        println ext

        response.setContentType(ext == 'pdf' ? "application/pdf" : "image/" + ext)
        response.setHeader("Content-disposition", "attachment; filename=" + doc.path)
        response.setContentLength(b.length)
        response.getOutputStream().write(b)
    }

    def list() {
        def concurso = Concurso.get(params.id)
        def documentos = DocumentoProceso.findAllByConcurso(concurso)
        return [concurso: concurso, documentoProcesoInstanceList: documentos, params: params]
    } //list

    def form_ajax() {
        def concurso = Concurso.get(params.concurso)
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
        return [documentoProcesoInstance: documentoProcesoInstance, concurso: concurso]
    } //form_ajax

    def save() {
        def documentoProcesoInstance
        if (params.id) {
            documentoProcesoInstance = DocumentoProceso.get(params.id)
            if (!documentoProcesoInstance) {
                flash.clase = "alert-error"
                flash.message = "No se encontró Documento Proceso con id " + params.id
                redirect(action: 'list', id: params.concurso.id)
                return
            }//no existe el objeto
            documentoProcesoInstance.properties = params
        }//es edit
        else {
            documentoProcesoInstance = new DocumentoProceso(params)
        } //es create

        /***************** file upload ************************************************/
        //handle uploaded file
        println "upload....."
        println params
        def folder = "archivos"
        def path = servletContext.getRealPath("/") + folder   //web-app/archivos
        new File(path).mkdirs()

        def f = request.getFile('archivo')  //archivo = name del input type file
        if (f && !f.empty) {
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
        redirect(action: 'list', id: params.concurso.id)
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

            redirect(action: "list", id: cid)
        }
        catch (DataIntegrityViolationException e) {
            flash.clase = "alert-error"
            flash.message = "No se pudo eliminar Documento Proceso " + (documentoProcesoInstance.id ? documentoProcesoInstance.id : "")
            redirect(action: "list")
        }
    } //delete
} //fin controller
