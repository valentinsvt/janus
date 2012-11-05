<%=packageName ? "package ${packageName}\n\n" : ''%>import org.springframework.dao.DataIntegrityViolationException

class ${className}Controller extends ${packageName}.seguridad.Shield {

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

    def index() {
        redirect(action: "list", params: params)
    } //index

    def list() {
        [${propertyName}List: ${className}.list(params), params: params]
    } //list

    def form_ajax() {
        def ${propertyName} = new ${className}(params)
        if(params.id) {
            ${propertyName} = ${className}.get(params.id)
            if(!${propertyName}) {
                flash.clase = "alert-error"
                flash.message =  "No se encontró${className.replaceAll('[A-Z]') {' ' + it}} con id " + params.id
                redirect(action:  "list")
                return
            } //no existe el objeto
        } //es edit
        return [${propertyName}: ${propertyName}]
    } //form_ajax

    def save() {
        def ${propertyName}
        if(params.id) {
            ${propertyName} = ${className}.get(params.id)
            if(!${propertyName}) {
                flash.clase = "alert-error"
                flash.message = "No se encontró${className.replaceAll('[A-Z]') {' ' + it}} con id " + params.id
                redirect(action: 'list')
                return
            }//no existe el objeto
            ${propertyName}.properties = params
        }//es edit
        else {
            ${propertyName} = new ${className}(params)
        } //es create
        if (!${propertyName}.save(flush: true)) {
            flash.clase = "alert-error"
            def str = "<h4>No se pudo guardar${className.replaceAll('[A-Z]') {' ' + it}} " + (${propertyName}.id ? ${propertyName}.id : "") + "</h4>"

            str += "<ul>"
            ${propertyName}.errors.allErrors.each { err ->
                def msg = err.defaultMessage
                err.arguments.eachWithIndex {  arg, i ->
                    msg = msg.replaceAll("\\\\{" + i + "}", arg.toString())
                }
                str += "<li>" + msg + "</li>"
            }
            str += "</ul>"

            flash.message = str
            redirect(action: 'list')
            return
        }

        if(params.id) {
            flash.clase = "alert-success"
            flash.message = "Se ha actualizado correctamente${className.replaceAll('[A-Z]') {' ' + it}} " + ${propertyName}.id
        } else {
            flash.clase = "alert-success"
            flash.message = "Se ha creado correctamente${className.replaceAll('[A-Z]') {' ' + it}} " + ${propertyName}.id
        }
        redirect(action: 'list')
    } //save

    def show_ajax() {
        def ${propertyName} = ${className}.get(params.id)
        if (!${propertyName}) {
            flash.clase = "alert-error"
            flash.message =  "No se encontró${className.replaceAll('[A-Z]') {' ' + it}} con id " + params.id
            redirect(action: "list")
            return
        }
        [${propertyName}: ${propertyName}]
    } //show

    def delete() {
        def ${propertyName} = ${className}.get(params.id)
        if (!${propertyName}) {
            flash.clase = "alert-error"
            flash.message =  "No se encontró${className.replaceAll('[A-Z]') {' ' + it}} con id " + params.id
            redirect(action: "list")
            return
        }

        try {
            ${propertyName}.delete(flush: true)
            flash.clase = "alert-success"
            flash.message =  "Se ha eliminado correctamente${className.replaceAll('[A-Z]') {' ' + it}} " + ${propertyName}.id
            redirect(action: "list")
        }
        catch (DataIntegrityViolationException e) {
            flash.clase = "alert-error"
            flash.message =  "No se pudo eliminar${className.replaceAll('[A-Z]') {' ' + it}} " + (${propertyName}.id ? ${propertyName}.id : "")
            redirect(action: "list")
        }
    } //delete
} //fin controller
