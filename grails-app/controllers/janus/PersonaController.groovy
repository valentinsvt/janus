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


    def passOferente() {

        def usroInstance = Persona.get(params.id)
        if (!usroInstance) {
            flash.clase = "alert-error"
            flash.message = "No se encontró Persona con id " + params.id
            redirect(action: "list")
            return
        }
        [usroInstance: usroInstance]


    }


    def savePass() {
        println params
        def user = Persona.get(params.id)
        if (params.password.trim() != "") {
            user.password = params.password.trim().encodeAsMD5()
        }
//        if (params.autorizacion.trim() != "") {
//            user.autorizacion = params.autorizacion.trim().encodeAsMD5()
//        }
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
        redirect(action: 'listOferente')
    }


    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

    def index() {
        redirect(action: "list", params: params)
    } //index



    def cambiarEstado () {

         println(params.id)

            def persona = Persona.get(params.id)

          println(persona)

            persona.activo = params.activo.toInteger()

//           println("id: " + persona.id)
//           println("activo: " + persona.activo)

            if (persona.save(flush: true))
                render "ok"
            flash.message = "El estado de la persona ha sido cambiado!"

//        redirect(action: 'list')

        return

    }



    def list() {

        def perfil = Prfl.get(4);


        def departamento = Departamento.get(13)

////        def perfil1 = Prfl.get(1);
////
////        def perfil2 = Prfl.get(2);
//
//        def lista = Persona.withCriteria {
////           ne('sesiones', perfil)
//
//            sesiones{
//
//                ne('id', 4.toLong())
//
//            }
//
//        }


//        [personaInstanceList: Persona.list(params), personaInstanceTotal: Persona.count(), params: params]
        [personaInstanceList: Persona.findAllByDepartamentoNotEqual(departamento), personaInstanceTotal: Persona.count(), params: params]
    } //list

    def listOferente(){

        def perfil = Prfl.get(4);


        [personaInstanceList: Persona.list(params), personaInstanceTotal: Persona.count(), params: params, sesion: Sesn.findAllByPerfil(perfil), sesion2 : Sesn.findByPerfil(perfil)]
        //list

    }


    def form_ajax() {

        def perfilOferente = Prfl.get(4);

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
        return [personaInstance: personaInstance, perfilOferente: perfilOferente]
    } //form_ajax



    def formOferente () {


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


    }


    def save() {

        println(params.password)

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
        def perfilesAct2 = Sesn.findAllByUsuario(personaInstance).perfil.id*.toString()
        //perfiles q llegaron como parametro
        def perfilesNue = params.perfiles
        def perfilesAdd = [], perfilesDel = []

        if (!perfilesAct){

            perfilesAct.each { per ->
                if (!perfilesNue.contains(per)) {
                    perfilesDel.add(per)
                }
            }
        }
        perfilesNue.each { per ->
            if (!perfilesAct.contains(per)) {
                perfilesAdd.add(per)
            }
        }

        println("por agregar" + perfilesAdd)
        println("nuevos" + perfilesNue)
        println("actuales"+ perfilesAct2)

        perfilesNue.each { i->


            if (!perfilesAct){

                def sesn = new Sesn()
                sesn.perfil = Prfl.get(i)
                sesn.usuario = personaInstance
                if (!sesn.save(flush: true)) {
                    println "error al grabar sesn perfil: " + i + " persona " + personaInstance.id
                }
            }
            else {

//                perfilesNue.each{ i->

//                    println("pa: " + perfilesAct2.contains(i))

                    if (!perfilesAct2.contains(i)) {

                        println("entro2")

                        def sesn = new Sesn()
                        sesn.perfil = Prfl.get(i)
                        sesn.usuario = personaInstance
                        if (!sesn.save(flush: true)) {
                            println "error al grabar sesn perfil: " + i + " persona " + personaInstance.id
                        }
                    }

//                }

            }


        }
        perfilesDel.each {
            def sesn = Sesn.findByUsuarioAndPerfil(personaInstance, Prfl.get(it))
            if (sesn){

                sesn.delete()

            }
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


    def saveOferente() {
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

        println(params.id)

        if (params.id) {
            personaInstance = Persona.get(params.id)

            def sesiones = Sesn.findByUsuario(personaInstance)

            if (!personaInstance) {
                flash.clase = "alert-error"
                flash.message = "No se encontró Persona con id " + params.id
                redirect(action: 'listOferente')
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
            redirect(action: 'listOferente')
            return
        }

        //guardo los perfiles de la persona
        //saco los perfiles q ya tiene
        def perfilesAct = Sesn.findAllByUsuario(personaInstance).id*.toString()
        //perfiles q llegaron como parametro
        def perfilesNue = params.perfiles

        println("-->>>" + params.perfiles)
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

            def sesiones = Sesn.findAllByUsuario(personaInstance)

//            println("sesiones:" + sesiones.id)

            if (!sesiones){

                println("no")

                def sesn = new Sesn()
                sesn.perfil = Prfl.get(it)
                sesn.usuario = personaInstance
                if (!sesn.save(flush: true)) {
                    println "error al grabar sesn perfil: " + it + " persona " + personaInstance.id
                }

            }


            else {

//                    println("si")

//                    sesiones.usuario.properties = params
//                    sesiones.save(flush: true){
//
//                        println("actualizado");
//
//                    }


            }

        }
        perfilesDel.each {


            def sesn = Sesn.findByUsuarioAndPerfil(personaInstance, Prfl.get(it))
            if (sesn){

                sesn.delete()
            }



        }

        if (params.id) {
            flash.clase = "alert-success"
            flash.message = "Se ha actualizado correctamente el Oferente " + personaInstance.nombre + " " + personaInstance.apellido
        } else {
            flash.clase = "alert-success"
            flash.message = "Se ha creado correctamente el Oferente " + personaInstance.nombre + " " + personaInstance.apellido
        }
        redirect(action: 'listOferente')
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



    def showOferente () {


        def personaInstance = Persona.get(params.id)
        if (!personaInstance) {
            flash.clase = "alert-error"
            flash.message = "No se encontró Persona con id " + params.id
            redirect(action: "list")
            return
        }
        [personaInstance: personaInstance]

    }


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
