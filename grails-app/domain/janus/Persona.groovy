package janus

import janus.seguridad.Sesn
import janus.seguridad.Accs

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
        }
    }
    static constraints = {
        cedula(size: 1..10, attributes: [title: 'cedula'])
        nombre(size: 1..30, attributes: [title: 'nombre'])
        apellido(size: 1..30, attributes: [title: 'apellido'])
        codigo(blank: false, attributes: [title: 'numero'])
        fechaNacimiento(blank: true, nullable: true, attributes: [title: 'fechaNacimiento'])
        departamento(blank: true, nullable: true, attributes: [title: 'departamento'])
        fechaInicio(blank: true, nullable: true, attributes: [title: 'fechaInicio'])
        fechaFin(blank: true, nullable: true, attributes: [title: 'fechaFin'])
        sigla(size: 1..3, blank: true, nullable: true, attributes: [title: 'sigla'])
        titulo(size: 1..4, blank: true, nullable: true, attributes: [title: 'titulo'])
        cargo(size: 1..50, blank: true, nullable: true, attributes: [title: 'cargo'])
    }
}