package janus.seguridad

class SesionActiva {
        String idSesion
        String activo
        Date   fechaInicio
        Date   fechaFin
        String login
        String dirIP

        static mapping = {
            table 'sesnactv'
            cache usage: 'read-write', include: 'non-lazy'
            id column: 'actv__id'
            id generator: 'identity'
            version false
            columns {
                idSesion column: 'actvidss'
                activo column: 'actvactv'
                fechaInicio column: 'actvfcin'
                fechaFin column: 'actvfcfn'
                login column: 'actvlogn'
                dirIP column: 'actvdrip'
            }
        }

        static constraints = {
            idSesion(blank: false, size: 0..63)
            activo(blank: false, size: 0..1)
            fechaInicio(blank: false, nullable: false)
            fechaFin(blank: true, nullable: true)
            login(blank: false, size: 0..15, nullable: false)
            dirIP(blank: false, size: 0..15, nullable: false)
        }

}
