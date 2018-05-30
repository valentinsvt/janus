package janus

import janus.seguridad.Prfl

//import com.linuxense.javadbf.DBFField
//import com.linuxense.javadbf.DBFReader
//
//import com.linuxense.javadbf.*

class InicioController extends janus.seguridad.Shield {
    def dbConnectionService
    def oferentesService

    def index() {
        def cn = dbConnectionService.getConnection()
        def prms = []
        def acciones = "'rubroPrincipal', 'registroObra', 'registrarPac', 'verContrato'"
        def tx = "select accnnmbr from prms, accn where prfl__id = " + Prfl.findByNombre(session.perfil.toString()).id +
                " and accn.accn__id = prms.accn__id and accnnmbr in (${acciones})"
        cn.eachRow(tx) { d ->
            prms << d.accnnmbr
        }
        cn.close()
        def empr = Parametros.get(1)

        //println "formula "
        //oferentesService.copiaFormula(1457,1485)
       // println " crono "
        //oferentesService.copiaCrono(1457,1485)
        return [prms: prms, empr: empr]
    }


    def inicio() {
        redirect(action: "index")
    }

    def parametros = {

    }

    def arbol () {

    }

    def manualObras = {

    }

    def variables () {
        def paux = Parametros.get(1);
        def par = Parametros.list()
        def total1 = (paux?.indiceAlquiler ?: 0) + (paux?.administracion ?: 0) + (paux?.indiceCostosIndirectosMantenimiento ?: 0) + (paux?.indiceProfesionales ?: 0) + (paux?.indiceSeguros ?: 0)  + (paux?.indiceSeguridad ?: 0)
        def total2 = (paux?.indiceCampo ?: 0) + (paux?.indiceCostosIndirectosCostosFinancieros ?: 0) + (paux?.indiceCostosIndirectosGarantias ?: 0) + (paux?.indiceCampamento ?: 0)
        def total3 = (total1 ?:0 ) + (total2 ?: 0) + (paux?.impreso ?: 0) + (paux?.indiceUtilidad ?: 0)
        
        paux.totales = total3
        paux.save(flush: true)

        return[paux: paux, par: par, totalCentral: total1, totalObra: total2]
    }


}
