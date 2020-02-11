package janus

import janus.seguridad.Accs
import janus.seguridad.Sesn

class Persona implements Serializable {
    Departamento departamento
    int codigo
    String cedula
    String nombre
    String apellido
    Date fechaNacimiento
    Date fechaInicio
    Date fechaFin
    String sigla
    String titulo
    String cargo

    String login
    String password
    String autorizacion
    String email

    Integer activo
    Date fechaActualizacionPass

    String firma
    String sexo

    //static hasMany = [sesiones: Sesn, accesos: Accs, alertas: janus.alertas.Alerta]
    static hasMany = [sesiones: Sesn, accesos: Accs]
    static auditable = [ignore: ['password']]


    static mapping = {
        table 'prsn'
        cache usage: 'read-write', include: 'non-lazy'
        id column: 'prsn__id'
        id generator: 'identity'
        version false
        columns {
            id column: 'prsn__id'
            departamento column: 'dpto__id'
            codigo column: 'prsncdgo'
            cedula column: 'prsncdla'
            nombre column: 'prsnnmbr'
            apellido column: 'prsnapll'
            fechaNacimiento column: 'prsnfcna'
            fechaInicio column: 'prsnfcin'
            fechaFin column: 'prsnfcfn'
            sigla column: 'prsnsgla'
            titulo column: 'prsntitl'
            cargo column: 'prsncrgo'

            login column: 'prsnlogn'
            password column: 'prsnpass'
            autorizacion column: 'prsnatrz'
            email column: 'prsnmail'

            activo column: 'prsnactv'
            fechaActualizacionPass column: 'prsnfcps'

            firma column: 'prsnfrma'
            sexo  column: 'prsnsexo'
        }
    }
    static constraints = {
        cedula(size: 1..13, unique: true, attributes: [title: 'cedula'])
        nombre(size: 1..30, attributes: [title: 'nombre'])
        apellido(size: 1..30, attributes: [title: 'apellido'])
        codigo(blank: true, nullable: true, attributes: [title: 'numero'])
        fechaNacimiento(blank: true, nullable: true, attributes: [title: 'fechaNacimiento'])
        departamento(blank: true, nullable: true, attributes: [title: 'departamento'])
        fechaInicio(blank: true, nullable: true, attributes: [title: 'fechaInicio'])
        fechaFin(blank: true, nullable: true, attributes: [title: 'fechaFin'])
        sigla(size: 1..3, blank: true, nullable: true, attributes: [title: 'sigla'])
        titulo(size: 1..4, blank: true, nullable: true, attributes: [title: 'titulo'])
        cargo(size: 1..50, blank: true, nullable: true, attributes: [title: 'cargo'])

        login(size: 1..16, unique: true, blank: false, nullable: false, attributes: [title: 'login'])
        password(size: 1..63, blank: false, nullable: false, attributes: [title: 'password'])
        autorizacion(size: 1..63, blank: true, nullable: true, attributes: [title: 'autorizacion'])
        email(blank: true, nullable: true, email: true, unique: true, attributes: [title: 'email'])

        activo(blank: false, nullable: false, attributes: [title: 'activo'])
        fechaActualizacionPass(blank: true, nullable: true, attributes: [title: 'fecha cmabio pass'])

        firma(blank: true, nullable: true)
        sexo(size: 1..1, blank: false, nullable: false, inList: ['M', 'F'], attributes: [title: 'Sexo'])
    }

    String toString() {
        return this.titulo + " " + this.nombre + " " + this.apellido
    }
}