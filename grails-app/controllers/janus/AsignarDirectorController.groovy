package janus

import org.springframework.dao.DataIntegrityViolationException

class AsignarDirectorController  {



        static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

        def index() {
            redirect(action: "list", params: params)
        } //index

        def list() {
            params.max = Math.min(params.max ? params.int('max') : 10, 100)
            [personaRolInstanceList: PersonaRol.list(params), personaRolInstanceTotal: PersonaRol.count(), params: params]
        } //list


        def registroPersonaRol (){


        }


        def getPersonas () {

            def departamento = Departamento.get(params.id)

            def personas = Persona.findAllByDepartamento(departamento)


            return [personas : personas]



        }


        def getSeleccionados () {


            def direccion = Direccion.get(params.id)

            def departamentos = Departamento.findAllByDireccion(direccion)

            def personas = Persona.findAllByDepartamentoInList(departamentos, [sort: 'nombre'])

//           println("personas:" + personas)

            def funcionDirector = Funcion.findByCodigo('D')

            def dptoDireccion = Departamento.findAllByDireccion(direccion)

//            println("dptoDire" + dptoDireccion)

            def personalDireccion = Persona.findAllByDepartamentoInList(dptoDireccion, [sort: 'nombre'])

//            println("personaDireccion" + personalDireccion)
//
//            def obtenerDirector = PersonaRol.findByFuncionAndPersonaInList(funcionDirector, personalDireccion)
//
//            println("od" + obtenerDirector)

            def obtenerDirector


            if(personalDireccion != []){


               obtenerDirector = PersonaRol.findByFuncionAndPersonaInList(funcionDirector, personalDireccion)


            }else {


                obtenerDirector = null
            }




            return [personas: personas, obtenerDirector: obtenerDirector]

        }



    def mensaje () {

//        println("params" + params)

        def direccion = Direccion.get(params.id)

        def personas = []
        def departamentos = []


        if(direccion){

            departamentos = Departamento.findAllByDireccion(direccion)

        }else{

            departamentos = []
        }



        if(departamentos){
            personas = Persona.findAllByDepartamentoInList(departamentos, [sort: 'nombre'])

        }else{
         personas = []

        }

        def funcionDirector = Funcion.findByCodigo('D')



//        def dptoDireccion = Departamento.findAllByDireccion(direccion)

        def personalDireccion = Persona.findAllByDepartamentoInList(departamentos, [sort: 'nombre'])


        def obtenerDirector


        if(personalDireccion != []){


            obtenerDirector = PersonaRol.findByFuncionAndPersonaInList(funcionDirector, personalDireccion)


        }else {


            obtenerDirector = null
        }


//        println("personas" + personas + "direc" + obtenerDirector)


        return [personas: personas, obtenerDirector: obtenerDirector]



    }


        def obtenerFuncion (){



            def persona = Persona.get(params.id);

            def rol = PersonaRol.findAllByPersona(persona)

            return [persona: persona, rol: rol]

        }


        def obtenerFuncionDirector () {

            def persona = Persona.get(params.id);

            def funcion = Funcion.findByCodigo('D')

            def rol = PersonaRol.findByPersonaAndFuncion(persona, funcion)


            return [persona: persona, rol: rol]

        }


        def grabarFuncion () {
            println("params" + params)
            def personaRol = new PersonaRol()
            if(params.id){
                personaRol.persona = Persona.get(params.id)
                personaRol.funcion = Funcion.get(params.rol)
            }

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

                render "NO"
            }

            try {
                personaRolInstance.delete(flush: true)

                render "OK"
            }
            catch (DataIntegrityViolationException e) {

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


            def funcion = Funcion.findByCodigo('D')


            def roles = PersonaRol.findAllByPersonaInListAndFuncion(personas, funcion )

            render roles.size()


        }



    } //fin controller

