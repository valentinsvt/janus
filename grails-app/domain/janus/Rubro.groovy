package janus
class Rubro implements Serializable {
    Item rubro
    Item item
    Date fecha
    double cantidad
    double rendimiento = 1
    static auditable = true
    static mapping = {
        table 'rbro'
        cache usage: 'read-write', include: 'non-lazy'
        id column: 'rbro__id'
        id generator: 'identity'
        version false
        columns {
            id column: 'rbro__id'
            rubro column: 'rbrocdgo'
            item column: 'item__id'
            fecha column: 'rbrofcha'
            cantidad column: 'rbrocntd'
            rendimiento column: 'rbrorndt'
        }
    }
    static constraints = {
        item(blank: false, nullable: false, attributes: [title: 'item'])
        cantidad(blank: true, attributes: [title: 'cantidad'])
        rubro(blank: false, nullable: false, attributes: [title: 'rubro'])
        fecha(blank: true, nullable: true, attributes: [title: 'fecha'])
    }



//
//    def textoCorregido (texto) {
//
//        def text = (texto ?: '')
////        println "--------------------------------------------------------------"
////        println text
//        text = text.replaceAll("&lt;", "*lt*")
//        text = text.replaceAll("&gt;", "*gt*")
////        println "--------------------------------------------------------------"
////        text = util.clean(str: text)
//        text = text.decodeHTML()
//        text = text.replaceAll("\\*lt\\*", "&lt;")
//        text = text.replaceAll("\\*gt\\*", "&gt;")
//
//    return text
//
//    }

}