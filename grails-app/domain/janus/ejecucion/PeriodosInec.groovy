package janus.ejecucion

import org.codehaus.groovy.runtime.DateGroovyMethods

class PeriodosInec implements Serializable {
    String descripcion
    Date fechaInicio
    Date fechaFin
    String periodoCerrado = "N"
    static auditable = true
    static mapping = {
        table 'prin'
        cache usage: 'read-write', include: 'non-lazy'
        id column: 'prin__id'
        id generator: 'identity'
        version false
        columns {
            id column: 'prin__id'
            descripcion column: 'prindscr'
            fechaInicio column: 'prinfcin'
            fechaFin column: 'prinfcfn'
            periodoCerrado column: 'princrre'
        }
    }
    static constraints = {
        descripcion(size: 1..31, blank: false, attributes: [title: 'descripcion'])
        fechaInicio(blank: false, attributes: [title: 'fechaInicio'])
        fechaFin(blank: true, nullable: true, attributes: [title: 'fechaFin'])
        periodoCerrado(size: 1..1, blank: false, attributes: [title: 'periodoCerrado'], inList: ["N", "S"]  )
    }

    String toString(){
        "${descripcion}: ${DateGroovyMethods.format(fechaInicio, 'yyyy/MM/dd')} - ${DateGroovyMethods.format(fechaFin, 'yyyy/MM/dd')}"
    }
}