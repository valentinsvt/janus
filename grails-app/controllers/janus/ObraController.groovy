package janus

import org.springframework.dao.DataIntegrityViolationException

class ObraController extends janus.seguridad.Shield {

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

    def index() {
        redirect(action: "list", params: params)
    } //index

    def list() {
        params.max = Math.min(params.max ? params.int('max') : 10, 100)
        [obraInstanceList: Obra.list(params), obraInstanceTotal: Obra.count(), params: params]
    } //list


    def cantonPorProvincia () {

            def provincia = Canton.get(params.id)

            def cantones = Parroquia.findAllByCanton(provincia)

            def sel = g.select(id: "selCanton", name: "parroquia.id", from: cantones, "class": "span12", optionKey: "id", optionValue: "nombre", noSelection: ["-1": "--Seleccione--"])
            def js = "<script type='text/javascript'>"
            js += '$("#selParroquia").change(function () {'
            js += 'var parroquia = $(this).val();'
            js += '$.ajax({'
            js += 'type    : "POST",'
            js += 'url     : "' + createLink(action: 'subgruposPorGrupo') + '",'
            js += 'data    : {'
            js += 'id : parroquia'
            js += '},'
            js += 'success : function (msg) {'
            js += '$("#selParroquia").replaceWith(msg);'
            js += '}'
            js += '});'
            js += '});'
            js += "</script>"
            render sel + js


    }


    def registroObra() {

        println(params)

        println(params.c)


        def prov = Provincia.list();

        def cant = Canton.list();

        def parr;

        def can = Canton.get(params.c)

        if (params.c != null) {

            parr = Parroquia.findAllByCanton(can)

            println(parr)


        } else {

            parr = "";
        }

        [prov: prov, cant: cant, parr: parr]

    }


    def situacionGeografica() {

//        println("params:" + params)

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
            flash.message = "Se ha actualizado correctamete Obra " + obraInstance.id
        } else {
            flash.clase = "alert-success"
            flash.message = "Se ha creado correctamete Obra " + obraInstance.id
        }
        redirect(action: 'list')
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
            flash.message = "Se ha eliminado correctamete Obra " + obraInstance.id
            redirect(action: "list")
        }
        catch (DataIntegrityViolationException e) {
            flash.clase = "alert-error"
            flash.message = "No se pudo eliminar Obra " + (obraInstance.id ? obraInstance.id : "")
            redirect(action: "list")
        }
    } //delete
} //fin controller
