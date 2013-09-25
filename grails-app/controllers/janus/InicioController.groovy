package janus

import janus.seguridad.Prfl

//import com.linuxense.javadbf.DBFField
//import com.linuxense.javadbf.DBFReader
//
//import com.linuxense.javadbf.*

class InicioController extends janus.seguridad.Shield {
    def dbConnectionService


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
        println prms
        return [prms: prms]
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
        return[paux: paux, par: par]
    }


}
