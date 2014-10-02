package janus

class ValoresAnuales {

    Integer anio
    Double sueldoBasicoUnificado
    Double seguro
    Double tasaInteresAnual
    Double factorCostoRepuestosReparaciones
    Double costoDiesel
    Double costoLubricante
    Double costoGrasa
    double inflacion
    static auditable = true
    static mapping = {
        table 'vlan'
        cache usage: 'read-write', include: 'non-lazy'
        id column: 'vlan__id'
        id generator: 'identity'
        version false
        columns {
            id column: 'vlan__id'
            anio column: 'vlananio'
            sueldoBasicoUnificado column: 'vlan_sbu'
            seguro column: 'vlansgro'
            tasaInteresAnual column: 'vlantasa'
            factorCostoRepuestosReparaciones column: 'vlanftrr'
            costoDiesel column: 'vlancsdi'
            costoLubricante column: 'vlancslb'
            costoGrasa column: 'vlancsgr'
            inflacion column: 'vlaninfl'
        }
    }
    static constraints = {


    }
}
