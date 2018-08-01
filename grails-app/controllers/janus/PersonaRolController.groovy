package janus

import org.springframework.dao.DataIntegrityViolationException

class PersonaRolController extends janus.seguridad.Shield {

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

    def index() {
        redirect(action: "list", params: params)
    } //index

    def list() {
        params.max = Math.min(params.max ? params.int('max') : 10, 100)
        [personaRolInstanceList: PersonaRol.list(params), personaRolInstanceTotal: PersonaRol.count(), params: params]
    } //list


    def registroPersonaRol (){

        def funciones = Funcion.findAllByCodigoNotLikeAndCodigoNotLike("D","O")

        def listaDireccion = Direccion.list()

        return[funciones: funciones, listaDireccion: listaDireccion]


    }


    def getPersonas () {

//        println(params)

//        def departamento = Departamento.get(params.id)

//        def personas = Persona.findAllByDepartamento(departamento)

        def direccion = Direccion.get(params.id)

        def departamentos = Departamento.findAllByDireccion(direccion)

        def personas = Persona.findAllByDepartamentoInListAndActivo(departamentos, 1, [sort: 'nombre'])

        return [personas : personas]
    }


    def getSeleccionados () {


        def direccion = Direccion.get(params.id)

        def departamentos = Departamento.findAllByDireccion(direccion)

        def personas = Persona.findAllByDepartamentoInList(departamentos, [sort: 'nombre'])

//        println("personas:" + personas)

        return [personas: personas]

    }


    def obtenerFuncion (){



        def persona = Persona.get(params.id);

        def rol = PersonaRol.findAllByPersona(persona)

        return [persona: persona, rol: rol]

    }


    def obtenerFuncionDirector () {

        def persona = Persona.get(params.id);

        def funcion = Funcion.get(9)

        def rol = PersonaRol.findByPersonaOrFuncion(persona, funcion)


        return [persona: persona, rol: rol]

    }



    def grabarFuncion () {
        def personaRol = new PersonaRol()
        personaRol.persona = Persona.get(params.id)
        personaRol.funcion = Funcion.get(params.rol)
        if (!personaRol.save([flush: true])) {
            render "NO"
            println "ERROR al guardar rolPersona: "+personaRol.errors
        } else {
            render "OK_"+personaRol.id
        }
    }

    def form_ajax() {
        def personaRolInstance = new PersonaRol(params)
        if (params.id) {
            personaRolInstance = PersonaRol.get(params.id)
            if (!personaRolInstance) {
                flash.clase = "alert-error"
                flash.message = "No se encontró PersonaRol con id " + params.id
                redirect(action: "list")
                return
            } //no existe el objeto
        } //es edit
        return [personaRolInstance: personaRolInstance]
    } //form_ajax

    def save() {
        def personaRolInstance
        if (params.id) {
            personaRolInstance = PersonaRol.get(params.id)
            if (!personaRolInstance) {
                flash.clase = "alert-error"
                flash.message = "No se encontró PersonaRol con id " + params.id
                redirect(action: 'list')
                return
            }//no existe el objeto
            personaRolInstance.properties = params
        }//es edit
        else {
            personaRolInstance = new PersonaRol(params)
        } //es create
        if (!personaRolInstance.save(flush: true)) {
            flash.clase = "alert-error"
            def str = "<h4>No se pudo guardar PersonaRol " + (personaRolInstance.id ? personaRolInstance.id : "") + "</h4>"

            str += "<ul>"
            personaRolInstance.errors.allErrors.each { err ->
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
            flash.message = "Se ha actualizado correctamente PersonaRol " + personaRolInstance.id
        } else {
            flash.clase = "alert-success"
            flash.message = "Se ha creado correctamente PersonaRol " + personaRolInstance.id
        }
        redirect(action: 'list')
    } //save

    def show_ajax() {
        def personaRolInstance = PersonaRol.get(params.id)
        if (!personaRolInstance) {
            flash.clase = "alert-error"
            flash.message = "No se encontró PersonaRol con id " + params.id
            redirect(action: "list")
            return
        }
        [personaRolInstance: personaRolInstance]
    } //show

    def delete() {
        def personaRolInstance = PersonaRol.get(params.id)
        if (!personaRolInstance) {
//            flash.clase = "alert-error"
//            flash.message = "No se encontró PersonaRol con id " + params.id
//            redirect(action: "list")
//            return
            render "NO"
        }

        try {
            personaRolInstance.delete(flush: true)
//            flash.clase = "alert-success"
//            flash.message = "Se ha eliminado correctamente PersonaRol " + personaRolInstance.id
//            redirect(action: "list")
            render "OK"
        }
        catch (DataIntegrityViolationException e) {
//            flash.clase = "alert-error"
//            flash.message = "No se pudo eliminar PersonaRol " + (personaRolInstance.id ? personaRolInstance.id : "")
//            redirect(action: "list")
            render "NO"
        }
    } //delete


    //asignación del director

    def asignarDirector () {



        def listaDireccion = Direccion.list()




        return [listaDireccion: listaDireccion]



    }


    def sacarFunciones () {


        def direccion = Direccion.get(params.id)

        def departamentos = Departamento.findAllByDireccion(direccion)

        def personas = Persona.findAllByDepartamentoInList(departamentos, [sort: 'nombre'])


        def funcion = Funcion.get(9)


        def roles = PersonaRol.findAllByPersonaInListAndFuncion(personas, funcion )

        render roles.size()

//        println("roles" + roles)

//        return [roles: roles]



    }






} //fin controller
