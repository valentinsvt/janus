package janus

class Reportes4Controller {

    def index() {}

    def registradas () {

    }

    def tablaRegistradas () {

        println(params)

     def obras

        if(params.estado == '1'){

           obras = Obra.findAllByEstadoOrOficioIngresoIsNotNull("R")

        }
        if(params.estado == '2'){

           obras = Obra.findAllByOficioIngresoIsNotNull()

        }
        if(params.estado == '3'){

           obras = Obra.findAllByEstado("R")
        }

       println("obras:" + obras)

        return [obras: obras]
    }

}
