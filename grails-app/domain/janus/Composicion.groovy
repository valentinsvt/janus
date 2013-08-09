package janus

class Composicion {

    Item item
    Obra obra
    Grupo grupo
    double cantidad =0
    double precio=0
    double transporte=0

    static auditable = [ignore: ["orden"]]
    static mapping = {
        table 'comp'
        cache usage: 'read-write', include: 'non-lazy'
        id column: 'comp__id'
        id generator: 'identity'
        version false
        columns {
            id column: 'comp__id'
            item column: 'item__id'
            obra column: 'obra__id'
            cantidad column: 'compcntd'
            precio column: 'compprco'
            transporte column: 'comptrnp'
            grupo column: 'grpo__id'

        }
    }
    static constraints = {
        obra(blank: false, attributes: [title: 'obra'])
        item(blank: false, attributes: [title: 'item'])

        grupo(nullable: false,blank:false)
    }
}
