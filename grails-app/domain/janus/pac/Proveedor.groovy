package janus.pac

import janus.EspecialidadProveedor

class Proveedor {

    EspecialidadProveedor especialidad
    String tipo //persona natural o jurídica
    String ruc //ruc o cédula dependiendo del tipo
    String nombre //nombre de la empresa (nulo si es persona natural)
    String nombreContacto //nombre del contacto o de la persona natural
    String apellidoContacto //apellido del contacto o de la persona natural
    String garante
    String direccion
    String fax
    String telefonos //ejemple 097438273 – 096234124 - 022234123
    Date fechaContacto //fecha de contacto o registro
    String email
    String licencia //número de licencia profesional del colegio de ingenieros
    String registro //número de registro en la cámara de la construcción
    String titulo //título profesional del titular
    String estado //activo o inactivo
    String observaciones

    static mapping = {
        table 'prve'
        cache usage: 'read-write', include: 'non-lazy'
        id column: 'prve__id'
        id generator: 'identity'
        version false
        columns {
            id column: 'prve__id'

            especialidad column: 'espc__id'
            tipo column: 'prvetipo'
            ruc column: 'prve_ruc'
            nombre column: 'prvenmbr'
            nombreContacto column: 'prvenbct'
            apellidoContacto column: 'prveapct'
            garante column: 'prvegrnt'
            direccion column: 'prvedire'
            fax column: 'prvefaxx'
            telefonos column: 'prvetelf'
            fechaContacto column: 'prvefccn'
            email column: 'prvemail'
            licencia column: 'prveclig'
            registro column: 'prvecmra'
            titulo column: 'prvettlr'
            estado column: 'prveetdo'
            observaciones column: 'prveobsr'
        }
    }
    static constraints = {
        especialidad(blank: true, nullable: true)
        tipo(blank: true, nullable: true, maxSize: 1, inList: ['N', 'J'])
        ruc(blank: true, nullable: true, maxSize: 13)
        nombre(blank: true, nullable: true, maxSize: 20)
        nombreContacto(blank: true, nullable: true, maxSize: 31)
        apellidoContacto(blank: true, nullable: true, maxSize: 31)
        garante(blank: true, nullable: true, maxSize: 40)
        direccion(blank: true, nullable: true, maxSize: 60)
        fax(blank: true, nullable: true, maxSize: 11)
        telefonos(blank: true, nullable: true, maxSize: 40)
        fechaContacto(blank: true, nullable: true)
        email(blank: true, nullable: true, maxSize: 40)
        licencia(blank: true, nullable: true, maxSize: 10)
        registro(blank: true, nullable: true, maxSize: 7)
        titulo(blank: true, nullable: true, maxSize: 4)
        estado(blank: true, nullable: true, maxSize: 1)
        observaciones(blank: true, nullable: true, maxSize: 127)
    }

    String toString() {
        return "${this.nombre}"
    }
}
