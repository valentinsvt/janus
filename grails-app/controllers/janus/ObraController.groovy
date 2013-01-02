package janus

import org.springframework.dao.DataIntegrityViolationException

class ObraController extends janus.seguridad.Shield {

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]
    def buscadorService


    def index() {
        redirect(action: "list", params: params)
    } //index

    def list() {
        params.max = Math.min(params.max ? params.int('max') : 10, 100)
        [obraInstanceList: Obra.list(params), obraInstanceTotal: Obra.count(), params: params]
    } //list


//    def cantonPorProvincia() {
//
//
//        if (params.id == '-1') {
//
//
//            def sel = g.select(id: "selCanton", name: "canton.id", from: "", "class": "span3", optionKey: "id", optionValue: "nombre", noSelection: ["-1": "Seleccione..."])
//            render sel
//
//
//        } else {
//
//
//            def provincia = Provincia.get(params.id)
//
//
//
//            def cantones = Canton.findAllByProvincia(provincia)
//
//
//
//            def sel = g.select(id: "selCanton", name: "canton.id", from: cantones, "class": "span3", optionKey: "id", optionValue: "nombre", noSelection: ["-1": "Seleccione..."])
//            def js = "<script type='text/javascript'>"
//            js += '$("#selCanton").change(function () {'
//            js += 'var canton = $(this).val();'
//            js += '$.ajax({'
//            js += 'type    : "POST",'
//            js += 'url     : "' + createLink(action: 'parroquiaPorCanton') + '",'
//            js += 'data    : {'
//            js += 'id : canton'
//            js += '},'
//            js += 'success : function (msg) {'
//            js += '$("#selParroquia").replaceWith(msg);'
//            js += '}'
//            js += '});'
//            js += '});'
//            js += "</script>"
//            render sel + js
//
//        }
//    }
//
//
//    def parroquiaPorCanton() {
//
//
//        if (params.id == '-1') {
//
//            def sel = g.select(id: "selParroquia", name: "parroquia.id", from: "", "class": "span3", style: "width: 215px", optionKey: "id", optionValue: "nombre", noSelection: ["-1": "Seleccione..."])
//            render sel
//
//
//        } else {
//
//            def canton = Canton.get(params.id)
//            def parroquias = Parroquia.findAllByCanton(canton)
//            def sel = g.select(id: "selParroquia", name: "parroquia.id", from: parroquias, "class": "span3", style: "width: 215px", optionKey: "id", optionValue: "nombre", noSelection: ["-1": "Seleccione..."])
//            render sel
//
//        }
//
//    }


    def registroObra() {

//        println(params)

       def prov = Provincia.list();
        def campos = ["codigo": ["Código", "string"], "nombre": ["Nombre", "string"], "descripcion": ["Descripción", "string"], "oficioIngreso": ["Memo ingreso", "string"], "oficioSalida": ["Memo salida", "string"], "sitio": ["Sitio", "string"], "plazo": ["Plazo", "int"], "parroquia": ["Parroquia", "string"], "comunidad": ["Comunidad", "string"]]
        if (params.obra) {
            def obra = Obra.get(params.obra)
            def subs = VolumenesObra.findAllByObra(obra,[sort:"orden"]).subPresupuesto.unique()
            [campos: campos, prov:prov,obra:obra,subs:subs]
        } else {
            [campos: campos, prov: prov]
        }



    }

    def buscarObra(){
        def extraParr=""
        def extraCom=""
        if(params.campos instanceof java.lang.String){
            if(params.campos=="parroquia"){
                def parrs = Parroquia.findAll("from Parroquia where nombre like '%${params.criterios.toUpperCase()}%'")
                params.criterios=""
                parrs.eachWithIndex {p,i->
                    extraParr+=""+p.id
                    if(i <parrs.size()-1)
                        extraParr+=","
                }
                if(extraParr.size()<1)
                    extraParr="-1"
                params.campos=""
                params.operadores=""
            }
            if(params.campos=="comunidad"){
                def coms = Comunidad.findAll("from Comunidad where nombre like '%${params.criterios.toUpperCase()}%'")
                params.criterios=""
                coms.eachWithIndex {p,i->
                    extraCom+=""+p.id
                    if(i <coms.size()-1)
                        extraCom+=","
                }
                if(extraCom.size()<1)
                    extraCom="-1"
                params.campos=""
                params.operadores=""
            }
        }else{
            def remove = []
            params.campos.eachWithIndex{p,i->
                if(p=="comunidad"){
                    def coms = Comunidad.findAll("from Comunidad where nombre like '%${params.criterios[i].toUpperCase()}%'")

                    coms.eachWithIndex{c,j->
                        extraCom+=""+c.id
                        if(j <coms.size()-1)
                            extraCom+=","
                    }
                    if(extraCom.size()<1)
                        extraCom="-1"
                    remove.add(i)
                }
                if(p=="parroquia"){
                    def parrs = Parroquia.findAll("from Parroquia where nombre like '%${params.criterios[i].toUpperCase()}%'")

                    parrs.eachWithIndex{c,j->
                        extraParr+=""+c.id
                        if(j <parrs.size()-1)
                            extraParr+=","
                    }
                    if(extraParr.size()<1)
                        extraParr="-1"
                    remove.add(i)
                }
            }
            remove.each{
                params.criterios[it]=null
                params.campos[it]=null
                params.operadores[it]=null
            }
        }


        def extras = " "
        if (extraParr.size()>1)
            extras+=" and parroquia in (${extraParr})"
        if (extraCom.size()>1)
            extras+=" and comunidad in (${extraCom})"

        def parr = {p ->
            return p.parroquia?.nombre
        }
        def comu = {c ->
            return c.comunidad?.nombre
        }
        def listaTitulos = ["Código", "Nombre","Descripción","Fecha Reg.","M. ingreso","M. salida","Sitio","Plazo","Parroquia","Comunidad","Inspector","Revisor","Responsable","Estado Obra"]
        def listaCampos = ["codigo", "nombre","descripcion","fechaCreacionObra","oficioIngreso","oficioSalida","sitio","plazo","parroquia","comunidad","inspector","revisor","responsable","estadoObra"]
        def funciones = [null, null,null,["format": ["dd/MM/yyyy hh:mm"]],null, null,null,null, ["closure": [parr, "&"]],["closure": [comu, "&"]],null,null,null,null]
        def url = g.createLink(action: "buscarObra", controller: "obra")
        def funcionJs = "function(){"
        funcionJs += '$("#modal-busqueda").modal("hide");'
        funcionJs += 'location.href="' + g.createLink(action: 'registroObra', controller: 'obra') + '?obra="+$(this).attr("regId");'
        funcionJs += '}'
        def numRegistros = 20

        if (!params.reporte) {
            def lista = buscadorService.buscar(Obra, "Obra", "excluyente", params, true, extras) /* Dominio, nombre del dominio , excluyente o incluyente ,params tal cual llegan de la interfaz del buscador, ignore case */
            lista.pop()
            render(view: '../tablaBuscador', model: [listaTitulos: listaTitulos, listaCampos: listaCampos, lista: lista, funciones: funciones, url: url, controller: "llamada", numRegistros: numRegistros, funcionJs: funcionJs,width:1800,paginas:12])
        } else {
//            println "entro reporte"
            /*De esto solo cambiar el dominio, el parametro tabla, el paramtero titulo y el tamaño de las columnas (anchos)*/
            session.dominio = Obra
            session.funciones = funciones
            def anchos = [7,10,7,7,7,7,7,4,7,7,7,7,7,7 ] /*el ancho de las columnas en porcentajes... solo enteros*/
            redirect(controller: "reportes", action: "reporteBuscador", params: [listaCampos: listaCampos, listaTitulos: listaTitulos, tabla: "Obra", orden: params.orden, ordenado: params.ordenado, criterios: params.criterios, operadores: params.operadores, campos: params.campos, titulo: "Obras", anchos: anchos, extras: extras, landscape: true])
        }
    }


    def situacionGeografica() {
        def comunidades

        def orden;

        def colorProv, colorCant, colorParr, colorComn;


        if (params.ordenar == '1') {


            orden = "asc";

        }
        else {

            orden = "desc";

        }


        switch (params.buscarPor) {

            case "1":


                colorProv = "#00008B";

                if (params.criterio != "") {
                    comunidades = Comunidad.withCriteria {
                        parroquia {
                            canton {
                                provincia {
                                    ilike("nombre", "%" + params.criterio + "%")
                                    order("nombre", orden)
                                }
                                order("nombre", orden)
                            }
                            order("nombre", orden)
                        }
                        order("nombre", orden)
                    }
                } else {
                    comunidades = Comunidad.list(order: "nombre")


                }


                break
            case "2":

                colorCant = "#00008B";

                if (params.criterio != "") {
                    comunidades = Comunidad.withCriteria {
                        parroquia {
                            canton {

                                ilike("nombre", "%" + params.criterio + "%")
                                order("nombre", orden)

                            }
                            order("nombre", orden)
                        }
                        order("nombre", orden)
                    }
                } else {
                    comunidades = Comunidad.list(order: "nombre")
                }

                break
            case "3":


                colorParr = "#00008B";

                if (params.criterio != "") {
                    println params
                    comunidades = Comunidad.withCriteria {
                        parroquia {
                            ilike("nombre", "%" + params.criterio + "%")
                            order("nombre", orden)
                        }
                        order("nombre", orden)
                    }
                } else {
                    comunidades = Comunidad.list()
                }

                break
            case "4":
//

                colorComn = "#00008B";

                if (params.criterio != "") {
                    comunidades = Comunidad.withCriteria {


                        ilike("nombre", "%" + params.criterio + "%")
                        order("nombre", orden)


                    }
                } else {
                    comunidades = Comunidad.list()
                }

                break

        }


        [comunidades: comunidades, colorComn: colorComn, colorProv: colorProv, colorParr: colorParr, colorCant: colorCant]

    }

    def form_ajax() {
        def obraInstance = new Obra(params)
        if (params.id) {
            obraInstance = Obra.get(params.id)
            if (!obraInstance) {
                flash.clase = "alert-error"
                flash.message = "No se encontró Obra con id " + params.id
                redirect(action: "list")
                return
            } //no existe el objeto
        } //es edit
        return [obraInstance: obraInstance]
    } //form_ajax

    def save() {



        if (params.fechaOficioSalida) {
            params.fechaOficioSalida = new Date().parse("dd-MM-yyyy", params.fechaOficioSalida)
        }

        if (params.fechaPreciosRubros) {
            params.fechaPreciosRubros = new Date().parse("dd-MM-yyyy", params.fechaPreciosRubros)
        }

        if (params.fechaCreacionObra) {
            params.fechaCreacionObra = new Date().parse("dd-MM-yyyy", params.fechaCreacionObra)
        }


        params.each {k,v ->

            println(k+"\t" + v)

        }

//        println("parametros" + params)



        def obraInstance
        if (params.id) {
            obraInstance = Obra.get(params.id)
            if (!obraInstance) {
                flash.clase = "alert-error"
                flash.message = "No se encontró Obra con id " + params.id
                redirect(action: 'list')
                return
            }//no existe el objeto
            obraInstance.properties = params
        }//es edit
        else {
            obraInstance = new Obra(params)
        } //es create
        if (!obraInstance.save(flush: true)) {
            flash.clase = "alert-error"
            def str = "<h4>No se pudo guardar Obra " + (obraInstance.id ? obraInstance.id : "") + "</h4>"

            str += "<ul>"
            obraInstance.errors.allErrors.each { err ->
                def msg = err.defaultMessage
                err.arguments.eachWithIndex {  arg, i ->
                    msg = msg.replaceAll("\\{" + i + "}", arg.toString())
                }
                str += "<li>" + msg + "</li>"
            }
            str += "</ul>"

            flash.message = str
            redirect(action: 'list')
            return
        }

        if (params.id) {
            flash.clase = "alert-success"
            flash.message = "Se ha actualizado correctamente Obra "
        } else {
            flash.clase = "alert-success"
            flash.message = "Se ha creado correctamente Obra "
        }
        redirect(action: 'registroObra',params: [obra: obraInstance.id])
    } //save

    def show_ajax() {
        def obraInstance = Obra.get(params.id)
        if (!obraInstance) {
            flash.clase = "alert-error"
            flash.message = "No se encontró Obra con id " + params.id
            redirect(action: "list")
            return
        }
        [obraInstance: obraInstance]
    } //show

    def delete() {
        def obraInstance = Obra.get(params.id)
        if (!obraInstance) {
            flash.clase = "alert-error"
            flash.message = "No se encontró Obra con id " + params.id
            redirect(action: "list")
            return
        }

        try {
            obraInstance.delete(flush: true)
            flash.clase = "alert-success"
            flash.message = "Se ha eliminado correctamente Obra " + obraInstance.id
            redirect(action: "list")
        }
        catch (DataIntegrityViolationException e) {
            flash.clase = "alert-error"
            flash.message = "No se pudo eliminar Obra " + (obraInstance.id ? obraInstance.id : "")
            redirect(action: "list")
        }
    } //delete
} //fin controller
