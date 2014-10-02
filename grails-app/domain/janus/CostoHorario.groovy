package janus

class CostoHorario {

    Item item
    double potencia = 0
    double valorNuevo = 0
    double llantas = 0
    double vidaEconómicaAlta = 0
    double horasAnoAlta = 0
    double vidaLlantasAnoAlta = 0
    double vidaEconómicaBaja  = 0
    double horasAnoBaja  = 0
    double vidaLlantasAnoBaja  = 0

    static auditable = true
    static mapping = {
        table 'csho'
        cache usage: 'read-write', include: 'non-lazy'
        id column: 'csho__id'
        id generator: 'identity'
        version false
        columns {
            id column: 'csho__id'
            potencia  column: 'cshoptnc'
            valorNuevo column: 'cshovlnv'
            llantas column: 'cshollnt'
            vidaEconómicaAlta column: 'cshovdea'
            horasAnoAlta column: 'cshohoal'
            vidaLlantasAnoAlta column: 'cshovlao'
            vidaEconómicaBaja column: 'cshovdeb'
            horasAnoBaja    column: 'cshohoab'
            vidaLlantasAnoBaja column: 'cshovlab'

        }
    }
    static constraints = {

    }


}
