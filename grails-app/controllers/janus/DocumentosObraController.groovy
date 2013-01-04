package janus

import groovy.json.JsonBuilder

class DocumentosObraController {

    def index() { }


    def documentosObra () {


        def nota = new Nota();

        def auxiliar = new Auxiliar();

        def auxiliarFijo = Auxiliar.get(1);


//        println(params)

        def obra = Obra.get(params.id)

        def personas = Persona.list()

        def departamentos = Departamento.list()


        def firmas

        def firmasViales



        def c = Persona.createCriteria()
        firmas = c.list {

            or{
                ilike("cargo", "%Director%")
                ilike("cargo","%Jefe%")
                ilike("cargo", "%Subdirector%")
                ilike("cargo", "%subd%")
            }
            or{
                eq("departamento", Departamento.get(3))
                eq("departamento", Departamento.get(10))
            }
            maxResults(20)
            order("nombre", "desc")
        }

        def d = Persona.createCriteria()
        firmasViales = d.list {

            or{
                ilike("cargo", "%Director%")
                ilike("cargo","%Jefe%")
                ilike("cargo", "%Subdirector%")
                ilike("cargo", "%subd%")
            }
            or{
                eq("departamento", Departamento.get(4))
                eq("departamento", Departamento.get(10))
            }
            maxResults(20)
            order("nombre", "desc")
        }



//
//        println("firmas"+ firmas)
//        println("firmas Viales" + firmasViales)


        [obra: obra, firmas: firmas, firmasViales: firmasViales, nota: nota, auxiliar: auxiliar, auxiliarFijo: auxiliarFijo]



    }


    def getDatos () {



        def nota = Nota.get(params.id)


        def map
        if (nota) {
            map=[
                    id: nota.id,
                    descripcion: nota.descripcion?:"",
                    texto: nota.texto,
                    adicional:nota.adicional?:""
            ]
        } else {
            map=[
                    id: "",
                    descripcion: "",
                    texto: "",
                    adicional:""
            ]
        }
        def json = new JsonBuilder( map)

        render json
    }

}
