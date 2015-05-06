package janus.ejecucion

class MultasPlanilla {


    Planilla planilla
    TipoMulta tipoMulta
    String descripcion
    double valorCronograma
    int dias
    double monto
    Date fechaMaxima
    String periodo

    static auditable = true
    static mapping = {
        table 'mlpl'
        cache usage: 'read-write', include: 'non-lazy'
        id column: 'mlpl__id'
        id generator: 'identity'
        version false
        columns {
            id column: 'mlpl__id'
            planilla column: 'plnl__id'
            tipoMulta column: 'tpml__id'
            descripcion column: 'mlpldscr'
            valorCronograma column: 'mlplcrng'
            monto column: 'mlplmnto'
            dias column: 'mlpldias'
            fechaMaxima column: 'mlplfcmx'
            periodo column: 'mlplprdo'
        }
    }

    static constraints = {
        planilla(blank:true, nullable: true)
        tipoMulta(blank:true, nullable: true)
        descripcion(blank:true, nullable: true)
        valorCronograma(blank:true, nullable: true)
        monto(blank:true, nullable: true)
        dias(blank:true, nullable: true)
        fechaMaxima(blank:true, nullable: true)
        periodo(blank:true, nullable: true)
    }
}
