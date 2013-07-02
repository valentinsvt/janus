package janus

//import com.linuxense.javadbf.DBFField
//import com.linuxense.javadbf.DBFReader
//
//import com.linuxense.javadbf.*



class InicioController extends janus.seguridad.Shield {

    def index() {



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
