package janus

import org.springframework.dao.DataIntegrityViolationException

class AuxiliarController extends janus.seguridad.Shield {

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

    def index() {
        redirect(action: "list", params: params)
    } //index

    def list() {
        [auxiliarInstanceList: Auxiliar.list(params), params: params]
    } //list


    def textosFijos (){

        def pr = janus.ReportesController

        def nota = new Nota();

        def auxiliar = new Auxiliar();

        def auxiliarFijo = Auxiliar.get(1);

        def usuario = session.usuario.id

        def persona = Persona.get(usuario)

//        println(params)

//        def obra = Obra.get(params.id)

//        def cuadrilla = FormulaPolinomica.findAllByObraAndNumeroIlike(obra,'c%')

//        println("cuadrilla:" + cuadrilla)

//        def departamento = Departamento.get(obra?.departamento?.id)

//        println("departamento: " + obra?.departamento?.id)

        def personas = Persona.list()

        def departamentos = Departamento.list()

        //totalPresupuesto

        def detalle

//        detalle= VolumenesObra.findAllByObra(obra,[sort:"orden"])


        def precios = [:]
//        def fecha = obra.fechaPreciosRubros
//        def dsps = obra.distanciaPeso
//        def dsvl = obra.distanciaVolumen
//        def lugar = obra.lugar


        def prch = 0
        def prvl = 0

//        if (obra.chofer){
//            prch = preciosService.getPrecioItems(fecha,lugar,[obra.chofer])
//            prch = prch["${obra.chofer.id}"]
//            prvl = preciosService.getPrecioItems(fecha,lugar,[obra.volquete])
//            prvl = prvl["${obra.volquete.id}"]
//        }
//        def rendimientos = preciosService.rendimientoTranposrte(dsps,dsvl,prch,prvl)
//
//        if (rendimientos["rdps"].toString()=="NaN")
//            rendimientos["rdps"]=0
//        if (rendimientos["rdvl"].toString()=="NaN")
//            rendimientos["rdvl"]=0

//        def indirecto = obra.totales/100


        def total1 = 0;
        def total2 = 0;
        def totalPrueba = 0

        def totales

        def totalPresupuesto=0;
        def totalPresupuestoBien=0;

//        def valores = preciosService.rbro_pcun_v2(obra.id)

//        def subPres = VolumenesObra.findAllByObra(obra,[sort:"orden"]).subPresupuesto.unique()


//        subPres.each { s->
//
//            total2 = 0
//
//            valores.each {
//
//                if(it.sbprdscr == s.descripcion){
//
//                    totales = it.totl
//                    totalPresupuestoBien = (total1 += totales)
//                    totalPrueba = total2 += totales
//
//                }
//            }
//
//        }

//        detalle.each {
//
//            def res = preciosService.precioUnitarioVolumenObraSinOrderBy("sum(parcial)+sum(parcial_t) precio ",obra.id,it.item.id)
//            precios.put(it.id.toString(),(res["precio"][0]+res["precio"][0]*indirecto).toDouble().round(2))
//
//
//            totales =  precios[it.id.toString()]*it.cantidad
//
//
//            totalPresupuesto+=totales;
//
//        }



//        def firmasAdicionales = Persona.findAllByDepartamento(departamento)


//        def funcionFirmar = Funcion.get(2)


//        def firmas = PersonaRol.findAllByFuncionAndPersonaInList(funcionFirmar, firmasAdicionales)




//        [obra: obra, firmas: firmas, firmasViales: firmasViales, nota: nota, auxiliar: auxiliar, auxiliarFijo: auxiliarFijo, personasFirmas: personasFirmas, personasFirmas2: personasFirmas2, totalPresupuesto: totalPresupuesto]
        [nota: nota, auxiliar: auxiliar, auxiliarFijo: auxiliarFijo, totalPresupuesto: totalPresupuesto,totalPresupuestoBien: totalPresupuestoBien, persona: persona]





    }


    def form_ajax() {
        def auxiliarInstance = new Auxiliar(params)
        if(params.id) {
            auxiliarInstance = Auxiliar.get(params.id)
            if(!auxiliarInstance) {
                flash.clase = "alert-error"
                flash.message =  "No se encontró Auxiliar con id " + params.id
                redirect(action:  "list")
                return
            } //no existe el objeto
        } //es edit
        return [auxiliarInstance: auxiliarInstance]
    } //form_ajax

    def save() {
        def auxiliarInstance
        if(params.id) {
            auxiliarInstance = Auxiliar.get(params.id)
            if(!auxiliarInstance) {
                flash.clase = "alert-error"
                flash.message = "No se encontró Auxiliar con id " + params.id
                redirect(action: '')
                return
            }//no existe el objeto
            auxiliarInstance.properties = params
        }//es edit
        else {
            auxiliarInstance = new Auxiliar(params)
        } //es create
        if (!auxiliarInstance.save(flush: true)) {
            flash.clase = "alert-error"
            def str = "<h4>No se pudo guardar Auxiliar " + (auxiliarInstance.id ? auxiliarInstance.id : "") + "</h4>"

            str += "<ul>"
            auxiliarInstance.errors.allErrors.each { err ->
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

        if(params.id) {
            flash.clase = "alert-success"
            flash.message = "Se ha actualizado correctamente Auxiliar " + auxiliarInstance.id
        } else {
            flash.clase = "alert-success"
            flash.message = "Se ha creado correctamente Auxiliar " + auxiliarInstance.id
        }
        redirect(action: 'list')
    } //save


    def saveTextoFijo() {

        def auxiliarInstance
        if(params.id) {
            auxiliarInstance = Auxiliar.get(params.id)
            if(!auxiliarInstance) {
                flash.clase = "alert-error"
                flash.message = "No se encontró Auxiliar con id " + params.id
                redirect(controller: 'documentosObra', action: 'documentosObra', id: params.obra)
                return
            }//no existe el objeto
            auxiliarInstance.properties = params
        }//es edit
        else {
            auxiliarInstance = new Auxiliar(params)
        } //es create
        if (!auxiliarInstance.save(flush: true)) {
            flash.clase = "alert-error"
//            def str = "<h4>No se pudo guardar Auxiliar " + (auxiliarInstance.id ? auxiliarInstance.id : "") + "</h4>"
            def str = "<h4>No se pudo guardar el texto en Textos Fijos " + (auxiliarInstance.id ? auxiliarInstance.id : "") + "</h4>"

            str += "<ul>"
            auxiliarInstance.errors.allErrors.each { err ->
                def msg = err.defaultMessage
                err.arguments.eachWithIndex {  arg, i ->
                    msg = msg.replaceAll("\\{" + i + "}", arg.toString())
                }
                str += "<li>" + msg + "</li>"
            }
            str += "</ul>"

            flash.message = str
            redirect(controller: 'documentosObra', action: 'documentosObra', id: params.obra)
            return
        }

        if(params.id) {
            flash.clase = "alert-success"
//            flash.message = "Se ha actualizado correctamente Auxiliar " + auxiliarInstance.id
            flash.message = "Se ha actualizado correctamente el texto en Textos Fijos "
        } else {
            flash.clase = "alert-success"
//            flash.message = "Se ha creado correctamente Auxiliar " + auxiliarInstance.id
            flash.message = "Se ha creado correctamente el texto en Textos Fijos "
        }
        redirect(controller: 'documentosObra', action: 'documentosObra', id: params.obra)
    } //saveTextoFijo


    def saveDoc() {
         def auxiliarInstance
        if(params.id) {
            auxiliarInstance = Auxiliar.get(params.id)
            if(!auxiliarInstance) {
                flash.clase = "alert-error"
                flash.message = "No se encontró Auxiliar con id " + params.id
                redirect(controller: 'documentosObra', action: 'documentosObra', id: params.obra)
                return
            }//no existe el objeto
            auxiliarInstance.properties = params
        }//es edit
        else {
            auxiliarInstance = new Auxiliar(params)
        } //es create
        if (!auxiliarInstance.save(flush: true)) {
            flash.clase = "alert-error"
//            def str = "<h4>No se pudo guardar Auxiliar " + (auxiliarInstance.id ? auxiliarInstance.id : "") + "</h4>"
            def str = "<h4>No se pudo guardar la nota " + (auxiliarInstance.id ? auxiliarInstance.id : "") + "</h4>"

            str += "<ul>"
            auxiliarInstance.errors.allErrors.each { err ->
                def msg = err.defaultMessage
                err.arguments.eachWithIndex {  arg, i ->
                    msg = msg.replaceAll("\\{" + i + "}", arg.toString())
                }
                str += "<li>" + msg + "</li>"
            }
            str += "</ul>"

            flash.message = str
            redirect(controller: 'documentosObra', action: 'documentosObra', id: params.obra)
            return
        }

        if(params.id) {
            flash.clase = "alert-success"
//            flash.message = "Se ha actualizado correctamente Auxiliar " + auxiliarInstance.id
            flash.message = "Se ha actualizado correctamente la nota en F. Polinómica "
        } else {
            flash.clase = "alert-success"
//            flash.message = "Se ha creado correctamente Auxiliar " + auxiliarInstance.id
            flash.message = "Se ha creado correctamente la nota en F. Polinómica"
        }
        redirect(controller: 'documentosObra', action: 'documentosObra', id: params.obra)
    } //save

    def savePiePaginaTF() {
        def auxiliarInstance
        if(params.id) {
            auxiliarInstance = Auxiliar.get(params.id)
            if(!auxiliarInstance) {
                flash.clase = "alert-error"
                flash.message = "No se encontró Auxiliar con id " + params.id
                redirect(controller: 'documentosObra', action: 'documentosObra', id: params.obra)
                return
            }//no existe el objeto
            auxiliarInstance.properties = params
        }//es edit
        else {
            auxiliarInstance = new Auxiliar(params)
        } //es create
        if (!auxiliarInstance.save(flush: true)) {
            flash.clase = "alert-error"
//            def str = "<h4>No se pudo guardar Auxiliar " + (auxiliarInstance.id ? auxiliarInstance.id : "") + "</h4>"
            def str = "<h4>No se pudo guardar el pie de página" + (auxiliarInstance.id ? auxiliarInstance.id : "") + "</h4>"

            str += "<ul>"
            auxiliarInstance.errors.allErrors.each { err ->
                def msg = err.defaultMessage
                err.arguments.eachWithIndex {  arg, i ->
                    msg = msg.replaceAll("\\{" + i + "}", arg.toString())
                }
                str += "<li>" + msg + "</li>"
            }
            str += "</ul>"

            flash.message = str
            redirect(controller: 'documentosObra', action: 'documentosObra', id: params.obra)
            return
        }

        if(params.id) {
            flash.clase = "alert-success"
//            flash.message = "Se ha actualizado correctamente Auxiliar " + auxiliarInstance.id
            flash.message = "Se ha actualizado el pie de página en Textos Fijos "
        } else {
            flash.clase = "alert-success"
//            flash.message = "Se ha creado correctamente Auxiliar " + auxiliarInstance.id
            flash.message = "Se ha creado el pie de página en Textos Fijos"
        }
        redirect(controller: 'documentosObra', action: 'documentosObra', id: params.obra)
    } //save











    def saveMemoPresu() {
//        println(params)
        def auxiliarInstance
        if(params.id) {
            auxiliarInstance = Auxiliar.get(params.id)
            if(!auxiliarInstance) {
                flash.clase = "alert-error"
                flash.message = "No se encontró Auxiliar con id " + params.id
                redirect(controller: 'documentosObra', action: 'documentosObra', id: params.obra)
                return
            }//no existe el objeto
            auxiliarInstance.properties = params
        }//es edit
        else {
            auxiliarInstance = new Auxiliar(params)
        } //es create
        if (!auxiliarInstance.save(flush: true)) {
            flash.clase = "alert-error"
//            def str = "<h4>No se pudo guardar Auxiliar " + (auxiliarInstance.id ? auxiliarInstance.id : "") + "</h4>"
            def str = "<h4>No se pudo guardar el texto en Adm. Directa " + (auxiliarInstance.id ? auxiliarInstance.id : "") + "</h4>"

            str += "<ul>"
            auxiliarInstance.errors.allErrors.each { err ->
                def msg = err.defaultMessage
                err.arguments.eachWithIndex {  arg, i ->
                    msg = msg.replaceAll("\\{" + i + "}", arg.toString())
                }
                str += "<li>" + msg + "</li>"
            }
            str += "</ul>"

            flash.message = str
            redirect(controller: 'documentosObra', action: 'documentosObra', id: params.obra)
            return
        }

        if(params.id) {
            flash.clase = "alert-success"
//            flash.message = "Se ha actualizado correctamente Auxiliar " + auxiliarInstance.id
            flash.message = "Se ha actualizado correctamente el texto en Adm. Directa"
        } else {
            flash.clase = "alert-success"
//            flash.message = "Se ha creado correctamente Auxiliar " + auxiliarInstance.id
            flash.message = "Se ha creado correctamente el texto en Adm. Directa"
        }
        redirect(controller: 'documentosObra', action: 'documentosObra', id: params.obra)
    } //save


    def saveMemoAdj() {

        println("params adj:" + params)

        def auxiliarInstance
        if(params.id) {
            auxiliarInstance = Auxiliar.get(params.id)
            if(!auxiliarInstance) {
                flash.clase = "alert-error"
                flash.message = "No se encontró Auxiliar con id " + params.id
                redirect(controller: 'documentosObra', action: 'documentosObra', id: params.obra)
                return
            }//no existe el objeto
            auxiliarInstance.properties = params
        }//es edit
        else {
            auxiliarInstance = new Auxiliar(params)
        } //es create
        if (!auxiliarInstance.save(flush: true)) {
            flash.clase = "alert-error"
//            def str = "<h4>No se pudo guardar Auxiliar " + (auxiliarInstance.id ? auxiliarInstance.id : "") + "</h4>"
            def str = "<h4>No se pudo guardar el adjunto en Adm. Directa " + (auxiliarInstance.id ? auxiliarInstance.id : "") + "</h4>"

            str += "<ul>"
            auxiliarInstance.errors.allErrors.each { err ->
                def msg = err.defaultMessage
                err.arguments.eachWithIndex {  arg, i ->
                    msg = msg.replaceAll("\\{" + i + "}", arg.toString())
                }
                str += "<li>" + msg + "</li>"
            }
            str += "</ul>"

            flash.message = str
            redirect(controller: 'documentosObra', action: 'documentosObra', id: params.obra)
            return
        }

        if(params.id) {
            flash.clase = "alert-success"
//            flash.message = "Se ha actualizado correctamente Auxiliar " + auxiliarInstance.id
            flash.message = "Se ha actualizado el texto adjunto en Adm. Directa "
        } else {
            flash.clase = "alert-success"
//            flash.message = "Se ha creado correctamente Auxiliar " + auxiliarInstance.id
            flash.message = "Se ha creado el texto adjunto en Adm. Directa "
        }
        redirect(controller: 'documentosObra', action: 'documentosObra', id: params.obra)
    } //save




    def saveText() {
        def auxiliarInstance
        if(params.id) {
            auxiliarInstance = Auxiliar.get(params.id)
            if(!auxiliarInstance) {
                flash.clase = "alert-error"
                flash.message = "No se encontró Auxiliar con id " + params.id
                redirect(controller: 'auxiliar', action: 'textosFijos')
                return
            }//no existe el objeto
            auxiliarInstance.properties = params
        }//es edit
        else {
            auxiliarInstance = new Auxiliar(params)
        } //es create
        if (!auxiliarInstance.save(flush: true)) {
            flash.clase = "alert-error"
            def str = "<h4>No se pudo guardar Auxiliar " + (auxiliarInstance.id ? auxiliarInstance.id : "") + "</h4>"

            str += "<ul>"
            auxiliarInstance.errors.allErrors.each { err ->
                def msg = err.defaultMessage
                err.arguments.eachWithIndex {  arg, i ->
                    msg = msg.replaceAll("\\{" + i + "}", arg.toString())
                }
                str += "<li>" + msg + "</li>"
            }
            str += "</ul>"

            flash.message = str
            redirect(controller: 'auxiliar', action: 'textosFijos')
            return
        }

        if(params.id) {
            flash.clase = "alert-success"
            flash.message = "Se ha actualizado correctamente Auxiliar " + auxiliarInstance.id
        } else {
            flash.clase = "alert-success"
            flash.message = "Se ha creado correctamente Auxiliar " + auxiliarInstance.id
        }
        redirect(controller: 'auxiliar', action: 'textosFijos')
    } //save




    def show_ajax() {
        def auxiliarInstance = Auxiliar.get(params.id)
        if (!auxiliarInstance) {
            flash.clase = "alert-error"
            flash.message =  "No se encontró Auxiliar con id " + params.id
            redirect(action: "list")
            return
        }
        [auxiliarInstance: auxiliarInstance]
    } //show

    def delete() {
        def auxiliarInstance = Auxiliar.get(params.id)
        if (!auxiliarInstance) {
            flash.clase = "alert-error"
            flash.message =  "No se encontró Auxiliar con id " + params.id
            redirect(action: "list")
            return
        }

        try {
            auxiliarInstance.delete(flush: true)
            flash.clase = "alert-success"
            flash.message =  "Se ha eliminado correctamente Auxiliar " + auxiliarInstance.id
            redirect(action: "list")
        }
        catch (DataIntegrityViolationException e) {
            flash.clase = "alert-error"
            flash.message =  "No se pudo eliminar Auxiliar " + (auxiliarInstance.id ? auxiliarInstance.id : "")
            redirect(action: "list")
        }
    } //delete
} //fin controller
