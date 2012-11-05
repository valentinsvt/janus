package janus
class ObraColumna implements Serializable {
    String descripcion
    String tipo
    String extn
    String item
    String grupo
    static mapping = {
        table 'obcl_xxxx'
        cache usage: 'read-write', include: 'non-lazy'
        id column: 'clmncdgo'
        id generator: 'identity'
        version false
        columns {
            id column: 'clmncdgo'
            descripcion column: 'clmndscr'
            tipo column: 'clmntipo'
            extn column: 'clmnextn'
            item column: 'clmnitem'
            grupo column: 'clmngrpo'
        }
    }
    static constraints = {

        tipo(size: 1..1, blank: true, nullable: true, attributes: [title: 'tipo'])
        item(size: 1..20, blank: true, nullable: true, attributes: [title: 'item'])
        grupo(size: 1..1, blank: true, nullable: true, attributes: [title: 'grupo'])
        extn(size: 1..1, blank: true, nullable: true, attributes: [title: 'extn'])
        descripcion(size: 1..60, blank: true, nullable: true, attributes: [title: 'descripcion'])
    }
}