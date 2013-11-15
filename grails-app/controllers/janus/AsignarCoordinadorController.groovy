package janus

import org.springframework.dao.DataIntegrityViolationException

class AsignarCoordinadorController {

    def index() {}


    def getDepartamento () {

//        println(params)

     def direccion = Direccion.get(params.id)

     def departamento = Departamento.findAllByDireccion(direccion)

        return[departamento: departamento]

    }


    def asignarCoordinador () {



    }


    def getPersonas () {

    def departamento = Departamento.get(params.id)

    def personas

    if (departamento != null ){
        personas = Persona.findAllByDepartamento(departamento)
    }else {
        personas = []
    }

    return [personas: personas]



    }


    def sacarFunciones () {


        def departamento = Departamento.get(params.id)

        def personas = Persona.findAllByDepartamento(departamento, [sort: 'nombre'])


        def funcion = Funcion.get(10)


        def roles = PersonaRol.findAllByPersonaInListAndFuncion(personas, funcion )

        render roles.size()


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


    def obtenerFuncionCoor () {

        def persona = Persona.get(params.id);

        def funcion = Funcion.get(10)

        def rol = PersonaRol.findByPersonaAndFuncion(persona, funcion)


        return [persona: persona, rol: rol]

    }


    def mensajeCoordinador () {


        def departamento = Departamento.get(params.id)

        def personas


        if(departamento != null){

            personas = Persona.findAllByDepartamento(departamento)
        }else{

            personas = []


        }


        def funcionCoor = Funcion.get(10)

        def getCoordinador

        if(personas != []){

            getCoordinador = PersonaRol.findByFuncionAndPersonaInList(funcionCoor, personas)


        }else {

            getCoordinador = null

        }

        return [personas: personas, getCoordinador: getCoordinador]

    }

}
