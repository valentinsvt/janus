package janus


class InicioController extends janus.seguridad.Shield {

    def index() {

//        //println "session empresa index: " + session.empresa
//        def color = new Color()
//        color.descripcion="prueba"
//        color.save()
//        color.delete()
//        color=Color.get(54)
//        color.descripcion=" "+new Date().format("dd/MM hh:mm")
//        color.save()


    }


    def inicio() {
        redirect(action: "index")
    }

    def parametros = {

    }

    def arbol () {


    }


}
