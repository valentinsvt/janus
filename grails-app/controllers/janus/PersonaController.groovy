package janus

import janus.seguridad.Prfl
import janus.seguridad.Sesn
import org.springframework.dao.DataIntegrityViolationException

class PersonaController extends janus.seguridad.Shield {

    def checkUniqueUser() {
//        println params
        if (params.id) {
//            println "EDIT"
            def user = Persona.get(params.id)
            if (user.login.trim() == params.login.trim()) {
//                println "1"
                render true
            } else {
                def users = Persona.countByLogin(params.login.trim())
                if (users == 0) {
//                    println "2"
                    render true
                } else {
//                    println "3"
                    render false
                }
            }
        } else {
//            println "CREATE"
            def users = Persona.countByLogin(params.login.trim())
            if (users == 0) {
//                println "4"
                render true
            } else {
//                println "5"
                render false
            }
        }
    }

    def checkUniqueCi() {
//        println params
        if (params.id) {
//            println "EDIT"
            def user = Persona.get(params.id)
            if (user.cedula.trim() == params.cedula.trim()) {
//                println "1"
                render true
            } else {
                def users = Persona.countByCedula(params.cedula.trim())
                if (users == 0) {
//                    println "2"
                    render true
                } else {
//                    println "3"
                    render false
                }
            }
        } else {
//            println "CREATE"
            def users = Persona.countByCedula(params.cedula.trim())
            if (users == 0) {
//                println "4"
                render true
            } else {
//                println "5"
                render false
            }
        }
    }

    def checkUserPass() {
//        println params
        if (params.id) {
//            println "EDIT"
            def user = Persona.get(params.id)
            if (user.password == params.passwordAct.trim().encodeAsMD5()) {
//                println "1"
//                println true
                render true
            } else {
//                println false
                render false
            }
        } else {
//            println false
            render false
        }
    }

    def checkUserAuth() {
//        println params
        if (params.id) {
//            println "EDIT"
            def user = Persona.get(params.id)
            if (user.autorizacion == params.autorizacionAct.trim().encodeAsMD5()) {
//                println "1"
                render true
            } else {
                render false
            }
        } else {
            render false
        }
    }

    def pass_ajax() {
        def usroInstance = Persona.get(params.id)
        if (!usroInstance) {
            flash.clase = "alert-error"
            flash.message = "No se encontró Persona con id " + params.id
            redirect(action: "list")
            return
        }
        [usroInstance: usroInstance]
    } //pass

    def savePass() {
        println params
        def user = Persona.get(params.id)
        if (params.password.trim() != "") {
            user.password = params.password.trim().encodeAsMD5()
        }
        if (params.autorizacion.trim() != "") {
            user.autorizacion = params.autorizacion.trim().encodeAsMD5()
        }
        if (!user.save(flush: true)) {
            flash.clase = "alert-error"
            def str = "<h4>No se pudo guardar Persona " + (user.id ? user.login : "") + "</h4>"

            str += "<ul>"
            user.errors.allErrors.each { err ->
                def msg = err.defaultMessage
                err.arguments.eachWithIndex {  arg, i ->
                    msg = msg.replaceAll("\\{" + i + "}", arg.toString())
                }
                str += "<li>" + msg + "</li>"
            }
            str += "</ul>"

            flash.message = str
        } else {
            flash.clase = "alert-success"
            flash.message = "Se ha guardado correctamente Persona " + user.login
        }
        redirect(action: 'list')
    }


    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

    def index() {
        redirect(action: "list", params: params)
    } //index

    def list() {
        [personaInstanceList: Persona.list(params), personaInstanceTotal: Persona.count(), params: params]
    } //list

    def form_ajax() {
        def personaInstance = new Persona(params)
        if (params.id) {
            personaInstance = Persona.get(params.id)
            if (!personaInstance) {
                flash.clase = "alert-error"
                flash.message = "No se encontró Persona con id " + params.id
                redirect(action: "list")
                return
            } //no existe el objeto
        } //es edit
        return [personaInstance: personaInstance]
    } //form_ajax

    def save() {
        if (params.fechaInicio) {
            params.fechaInicio = new Date().parse("dd-MM-yyyy", params.fechaInicio)
        }
        if (params.fechaFin) {
            params.fechaFin = new Date().parse("dd-MM-yyyy", params.fechaFin)
        }
        if (params.fechaNacimiento) {
            params.fechaNacimiento = new Date().parse("dd-MM-yyyy", params.fechaNacimiento)
        }
        if (params.fechaPass) {
            params.fechaPass = new Date().parse("dd-MM-yyyy", params.fechaPass)
        }
        if (params.password) {
            params.password = params.password.encodeAsMD5()
        }
        if (params.autorizacion) {
            params.autorizacion = params.autorizacion.encodeAsMD5()
        }

        def personaInstance
        if (params.id) {
            personaInstance = Persona.get(params.id)
            if (!personaInstance) {
                flash.clase = "alert-error"
                flash.message = "No se encontró Persona con id " + params.id
                redirect(action: 'list')
                return
            }//no existe el objeto
            personaInstance.properties = params
        }//es edit
        else {
            personaInstance = new Persona(params)
        } //es create
        if (!personaInstance.save(flush: true)) {
            flash.clase = "alert-error"
            def str = "<h4>No se pudo guardar Persona " + (personaInstance.id ? personaInstance.nombre + " " + personaInstance.apellido : "") + "</h4>"

            str += "<ul>"
            personaInstance.errors.allErrors.each { err ->
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

        //guardo los perfiles de la persona
        //saco los perfiles q ya tiene
        def perfilesAct = Sesn.findAllByUsuario(personaInstance).id*.toString()
        //perfiles q llegaron como parametro
        def perfilesNue = params.perfiles
        def perfilesAdd = [], perfilesDel = []

        perfilesAct.each { per ->
            if (!perfilesNue.contains(per)) {
                perfilesDel.add(per)
            }
        }
        perfilesNue.each { per ->
            if (!perfilesAct.contains(per)) {
                perfilesAdd.add(per)
            }
        }

        perfilesAdd.each {
            def sesn = new Sesn()
            sesn.perfil = Prfl.get(it)
            sesn.usuario = personaInstance
            if (!sesn.save(flush: true)) {
                println "error al grabar sesn perfil: " + it + " persona " + personaInstance.id
            }
        }
        perfilesDel.each {
            def sesn = Sesn.findByUsuarioAndPerfil(personaInstance, Prfl.get(it))
            sesn.delete()
        }

        if (params.id) {
            flash.clase = "alert-success"
            flash.message = "Se ha actualizado correctamente Persona " + personaInstance.nombre + " " + personaInstance.apellido
        } else {
            flash.clase = "alert-success"
            flash.message = "Se ha creado correctamente Persona " + personaInstance.nombre + " " + personaInstance.apellido
        }
        redirect(action: 'list')
    } //save

    def show_ajax() {
        def personaInstance = Persona.get(params.id)
        if (!personaInstance) {
            flash.clase = "alert-error"
            flash.message = "No se encontró Persona con id " + params.id
            redirect(action: "list")
            return
        }
        [personaInstance: personaInstance]
    } //show

    def delete() {
        def personaInstance = Persona.get(params.id)
        if (!personaInstance) {
            flash.clase = "alert-error"
            flash.message = "No se encontró Persona con id " + params.id
            redirect(action: "list")
            return
        }

        try {
            personaInstance.delete(flush: true)
            flash.clase = "alert-success"
            flash.message = "Se ha eliminado correctamente Persona " + personaInstance.nombre + " " + personaInstance.apellido
            redirect(action: "list")
        }
        catch (DataIntegrityViolationException e) {
            flash.clase = "alert-error"
            flash.message = "No se pudo eliminar Persona " + (personaInstance.id ? personaInstance.nombre + " " + personaInstance.apellido : "")
            redirect(action: "list")
        }
    } //delete
} //fin controller
