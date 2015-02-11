package janus.pac

class Pac {

    UnidadIncop unidad
    CodigoComprasPublicas cpp
    TipoCompra tipoCompra
    janus.Departamento departamento
    TipoProcedimiento tipoProcedimiento
    Anio anio
    janus.Presupuesto presupuesto
    ProgramaPresupuestario programa
    String descripcion
    double cantidad=1
    double costo=1
    String c1
    String c2
    String c3
    String estado
    String memo
    String requiriente
    static auditable = true
    static mapping = {
        table 'pacp'
        cache usage: 'read-write', include: 'non-lazy'
        id column: 'pacp__id'
        id generator: 'identity'
        version false
        columns {
            id column: 'pacp__id'
            unidad column: 'uncp__id'
            cpp column: 'cpac__id'
            tipoCompra column: 'tpcp__id'
            departamento column: 'dpto__id'
            tipoProcedimiento column: 'tppc__id'
            anio column: 'anio__id'
            presupuesto column: 'prsp__id'
            programa column: 'pgps__id'
            descripcion column: 'pacpdscr'
            cantidad column: 'pacpcnta'
            costo column: 'pacpcsto'
            c1 column: 'pacpctr1'
            c2 column: 'pacpctr2'
            c3 column: 'pacpctr3'
            estado column: 'pacpetdo'
            memo column: 'pacpmemp'
            requiriente column: 'pacprqri'

        }
    }
    static constraints = {
        unidad(blank:true,nullable: true)                    ;
        cpp(blank:true,nullable: true)
        tipoCompra(blank:false,nullable: false)
        departamento(blank:false,nullable: false)
        tipoProcedimiento(blank:true,nullable: true)
        anio(blank:false,nullable: false)
        presupuesto(blank:false,nullable: false)
        programa(blank:true,nullable: true)
        descripcion(nullable: true,blank: true,size: 1..562)
        c1(blank:true,nullable: true,size: 1..1)
        c2(blank:true,nullable: true,size: 1..1)
        c3(blank:true,nullable: true,size: 1..1)
        estado(blank:true,nullable: true,size: 1..1)
        memo(blank:true,nullable: true,size: 1..32)
        requiriente(blank: true,nullable: true,size: 1..100)
    }
}
