package janus.ejecucion

class TipoFormulaPolinomica implements Serializable {

    String codigo
    String descripcion

    static auditable = true
    static mapping = {

        table 'tpfp'
        cache usage: 'read-write', include: 'non-lazy'
        id column: 'tpfp__id'
        id generator: 'identity'
        version false
        columns {
         id column: 'tpfp__id'
         codigo column: 'tpfpcdgo'
         descripcion column: 'tpfpdscr'
        }

    }
    static constraints = {
      codigo(blank: true, nullable: true)
      descripcion(blank: true, nullable: true)

    }
}
