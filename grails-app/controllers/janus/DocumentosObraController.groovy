package janus

class DocumentosObraController {

    def index() { }


    def documentosObra () {


        def nota = new Nota();


//        println(params)

        def obra = Obra.get(params.id)

        def personas = Persona.list()

        def departamentos = Departamento.list()

//        println(departamentos)


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


        [obra: obra, firmas: firmas, firmasViales: firmasViales, nota: nota]



    }


    def getDatos () {

        def nota = Nota.get(params.id)

        def json = "{"

        if(nota) {
            json+='"id":  ' + nota.id + ','
            json+='"descripcion":  "' + (nota.descripcion ?: "") + '",'
            json+='"texto":  "' + nota.texto + '",'
            json+='"adicional":  "' + (nota.adicional ?: "") + '"'
        }
        else {
            json+='"id":  "",'
            json+='"descripcion":  "",'
            json+='"texto":  "",'
            json+='"adicional":  ""'
        }
        json+="}"

        render json
    }

}
