package janus

class Nota implements Serializable {

    String obraTipo

    String descripcion;
    String texto
    String adicional


    static mapping = {

        table 'nota'
        cache usage: 'read-write', include: 'non-lazy'
        id column: 'nota__id'
        id generator: 'identity'
        version false
        columns {
            id column: 'nota__id'
            obraTipo column: 'obratipo'

            descripcion column: 'notadscr'
            texto column: 'notatext'
            adicional column: 'notaadcn'
    }
    }

    static constraints = {

        obraTipo(maxSize: 1, blank: true, nullable: true, attributes: [title: 'obraTipo'])
        descripcion(size: 1..253, blank:true, attributes: [title: 'descripcion'])
        texto(size: 1..253, blank: true, attributes: [title: 'texto'])
        adicional(size: 1..253, blank: true, nullable: true, attributes: [title: 'adicional'])


    }
}
