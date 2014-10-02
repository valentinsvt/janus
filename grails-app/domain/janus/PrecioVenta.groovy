package janus
class PrecioVenta implements Serializable {
    EspecialidadProveedor especialidadProveedor
    String nombre
    String sigla
    String direccion
    String fax
    String telefono
    Date fechaContacto
    String responsable
    String ruc
    String observaciones
    String indiceCostosIndirectosGarantias
    String email
    String licenciaColegioIngenieros
    String registroCamaraConstruccion
    String tituloProfecionalTitular
    static auditable = true
    static mapping = {
        table 'prve'
        cache usage: 'read-write', include: 'non-lazy'
        id column: 'prve__id'
        id generator: 'identity'
        version false
        columns {
            id column: 'prve__id'
            especialidadProveedor column: 'espc__id'
            nombre column: 'prvenmbr'
            sigla column: 'prvesgla'
            direccion column: 'prvedire'
            fax column: 'prvefaxx'
            telefono column: 'prvetelf'
            fechaContacto column: 'prvefccn'
            responsable column: 'prverspn'
            ruc column: 'prve_ruc'
            observaciones column: 'prveobsr'
            indiceCostosIndirectosGarantias column: 'prvegrnt'
            email column: 'prvemail'
            licenciaColegioIngenieros column: 'prveclig'
            registroCamaraConstruccion column: 'prvecmra'
            tituloProfecionalTitular column: 'prvettlr'
        }
    }
    static constraints = {

        nombre(size: 1..20, blank: false, nullable: false, attributes: [title: 'nombre'])
        sigla(size: 1..6, blank: true, nullable: true, attributes: [title: 'sigla'])
        ruc(size: 1..13, blank: false, nullable: false, attributes: [title: 'ruc'])
        responsable(size: 1..30, blank: false, nullable: false, attributes: [title: 'responsable'])
        especialidadProveedor(blank: true, nullable: true, attributes: [title: 'especialidadProveedor'])
        direccion(size: 1..60, blank: true, nullable: true, attributes: [title: 'direccion'])
        fax(size: 1..11, blank: true, nullable: true, attributes: [title: 'fax'])
        telefono(size: 1..40, blank: true, nullable: true, attributes: [title: 'telefono'])
        email(size: 1..40, blank: true, nullable: true, attributes: [title: 'email'])
        fechaContacto(blank: true, nullable: true, attributes: [title: 'fechaContacto'])
        indiceCostosIndirectosGarantias(size: 1..40, blank: true, nullable: true, attributes: [title: 'indiceCostosIndirectosGarantias'])
        licenciaColegioIngenieros(size: 1..10, blank: true, nullable: true, attributes: [title: 'licenciaColegioIngenieros'])
        registroCamaraConstruccion(size: 1..7, blank: true, nullable: true, attributes: [title: 'registroCamaraConstruccion'])
        tituloProfecionalTitular(size: 1..4, blank: true, nullable: true, attributes: [title: 'tituloProfecionalTitular'])
        observaciones(size: 1..60, blank: true, nullable: true, attributes: [title: 'observaciones'])
    }
}