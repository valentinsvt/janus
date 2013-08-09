package janus

class ComposicionController extends janus.seguridad.Shield{

    def index() {}
    def dbConnectionService

    def validacion() {
//        println "validacion "+params
        def obra = Obra.get(params.id)
        def comps = Composicion.findAllByObra(obra)
        if(comps.size()==0){
            redirect(action: "cargarDatos",id: params.id)
        }else{
            redirect(action: "tabla",id: params.id)
        }
        return
    }

    def cargarDatos(){
        def sql = "select item__id,voitpcun,voitcntd, voitcoef, voittrnp from vlobitem where obra__id=${params.id} and voitpcun is not null and voitcntd is not null order by 1"
        def obra = Obra.get(params.id)
        def cn = dbConnectionService.getConnection()
        cn.eachRow(sql.toString()){r->
//            println "r " +r
            def comp = Composicion.findAll("from Composicion  where obra=${params.id} and item=${r[0]}")
//            println "comp "+comp
            if(comp.size()==0){
                def item =Item.get(r[0])
                comp=new Composicion([obra:obra,item:item,grupo:item.departamento.subgrupo.grupo,cantidad:r[2],precio:r[1],transporte:r[4]])
                if(!comp.save(flush: true)){
                    println "error "+comp.errors
                }

            }else{
                comp=comp.pop()
                comp.cantidad+=r[2]
                if(!comp.save(flush: true)){
                    println "error "+comp.errors
                }
            }

        }
        render "ok"
    }


    def tabla(){
        if (!params.tipo) {
            params.tipo = "-1"
        }
        if (!params.sp) {
            params.sp = '-1'
        }

        def obra = Obra.get(params.id)
        if (params.tipo == "-1") {
            params.tipo = "1,2,3"
        }

        def res = Composicion.findAll("from Composicion where obra=${params.id} and grupo in (${params.tipo})")
        res.sort{it.item.codigo}

        return [res: res, obra: obra, tipo: params.tipo, rend: params.rend]

    }


    def save(){
        println "save comp "+params.data

        def parts = params.data.split("X")
        parts.each {p->
            if(p!=""){
                def data = p.split("I")
                if(data[0]!=""){
                    def comp = Composicion.get(data[0])
                    if(comp){
                        comp.cantidad=data[1].toDouble()
                        comp.save(flush: true)
                    }
                }
            }
        }

        render "ok"

    }
}
