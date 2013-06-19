package janus.pac

import org.springframework.dao.DataIntegrityViolationException

class ParametroEvaluacionController extends janus.seguridad.Shield {

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

    def index() {
        redirect(action: "list", params: params)
    } //index

    def removeParametro() {
        def par = ParametroEvaluacion.get(params.id)
        try {
            par.delete(flush: true)
            render "OK"
        }
        catch (DataIntegrityViolationException e) {
            render "NO"
        }
    }

    def addParametro() {
//        println params
        def par = new ParametroEvaluacion()
        par.concurso = Concurso.get(params.cnc)
        par.orden = params.ord.toInteger()
        par.descripcion = params.par
        par.puntaje = params.pnt.toDouble()
        par.minimo = params.min.toDouble()
        if (params.pdr) {
            par.padre = ParametroEvaluacion.get(params.pdr)
        }
        if (!par.save(flush: true)) {
            println "ERROR!!!! " + par.errors
            render "NO"
        } else {
            render "OK_" + par.id
        }
        /*
        Concurso concurso
        ParametroEvaluacion padre
        Integer orden
        String descripcion
        Double puntaje
        Double minimo = 0

            'min':'3',
            'par':'df5444',
            'id':'ni_01',
            'cnc':'2',
            'ord':'1',
            'pnt':'3'
         */
    }

    def updateInfo() {
        def concurso = Concurso.get(params.id)
        def html = ""
        html += '<table class="table table-bordered table-striped table-condensed table-hover" style="width: auto;">'
        html += "<thead>"
        html += '<tr>'
        html += '<th style="width: 40px;"></th>'
        html += '<th style="width: 40px;"></th>'
        html += '<th style="width: 40px;"></th>'
        html += '<th style="width: 530px;">Parámetro</th>'
        html += '<th style="width: 60px;">Puntaje</th>'
        html += '<th style="width: 60px;">Mínimo</th>'
        html += '<th style="width: 60px;">Asignado</th>'
        html += '</tr>'
        html += "</thead>"
        html += "<tbody>"
        def filas = []
        ParametroEvaluacion.findAllByConcursoAndPadreIsNull(concurso, [sort: 'orden']).each { par1 ->
            def par1Hijos = ParametroEvaluacion.findAllByPadre(par1)
            def filaPar1 = [
                    clase: "nivel1",
                    estilos: "",
                    celdas: []
            ]
            def celdaN11 = [
                    colspan: 1,
                    rowspan: 1,
                    clase: "orden",
                    estilos: "",
                    texto: par1.orden
            ]
            def celdaN12 = [
                    colspan: 2,
                    rowspan: 1,
                    clase: "orden",
                    estilos: "",
                    texto: ""
            ]
            def celdaPar1 = [
                    colspan: 1,
                    rowspan: 1,
                    clase: "",
                    estilos: "",
                    texto: par1.descripcion
            ]
            def celdaPnt1 = [
                    colspan: 1,
                    rowspan: 1,
                    clase: "numero",
                    estilos: "",
                    texto: g.formatNumber(number: par1.puntaje, maxFractionDigits: 2, minFractionDigits: 2, format: "##,##0", locale: "ec")
            ]
            def celdaMin1 = [
                    colspan: 1,
                    rowspan: 1,
                    clase: "numero",
                    estilos: "",
                    texto: g.formatNumber(number: par1.minimo, maxFractionDigits: 2, minFractionDigits: 2, format: "##,##0", locale: "ec")
            ]
            def celdaAsg1 = [
                    colspan: 1,
                    rowspan: 1,
                    clase: "numero",
                    estilos: "",
                    texto: g.formatNumber(number: par1Hijos.sum { it.puntaje }, maxFractionDigits: 2, minFractionDigits: 2, format: "##,##0", locale: "ec")
            ]
            filaPar1.celdas.add(celdaN11)
            filaPar1.celdas.add(celdaN12)
            filaPar1.celdas.add(celdaPar1)
            filaPar1.celdas.add(celdaPnt1)
            filaPar1.celdas.add(celdaMin1)
            filaPar1.celdas.add(celdaAsg1)
            filas.add(filaPar1)

            def rs1 = 1
            par1Hijos.each { par2 ->
                def par2Hijos = ParametroEvaluacion.findAllByPadre(par2)
                def filaPar2 = [
                        clase: "nivel2",
                        estilos: "",
                        celdas: []
                ]
                def celdaN22 = [
                        colspan: 1,
                        rowspan: 1,
                        clase: "orden",
                        estilos: "",
                        texto: par2.orden
                ]
                def celdaN23 = [
                        colspan: 1,
                        rowspan: 1,
                        clase: "orden",
                        estilos: "",
                        texto: ""
                ]
                def celdaPar2 = [
                        colspan: 1,
                        rowspan: 1,
                        clase: "",
                        estilos: "",
                        texto: par2.descripcion
                ]
                def celdaPnt2 = [
                        colspan: 1,
                        rowspan: 1,
                        clase: "numero",
                        estilos: "",
                        texto: g.formatNumber(number: par2.puntaje, maxFractionDigits: 2, minFractionDigits: 2, format: "##,##0", locale: "ec")
                ]
                def celdaMin2 = [
                        colspan: 1,
                        rowspan: 1,
                        clase: "numero",
                        estilos: "",
                        texto: g.formatNumber(number: par2.minimo, maxFractionDigits: 2, minFractionDigits: 2, format: "##,##0", locale: "ec")
                ]
                def celdaAsg2 = [
                        colspan: 1,
                        rowspan: 1,
                        clase: "numero",
                        estilos: "",
                        texto: g.formatNumber(number: par2Hijos.sum { it.puntaje }, maxFractionDigits: 2, minFractionDigits: 2, format: "##,##0", locale: "ec")
                ]
                filaPar2.celdas.add(celdaN22)
                filaPar2.celdas.add(celdaN23)
                filaPar2.celdas.add(celdaPar2)
                filaPar2.celdas.add(celdaPnt2)
                filaPar2.celdas.add(celdaMin2)
                filaPar2.celdas.add(celdaAsg2)
                filas.add(filaPar2)

                def rs2 = 1
                par2Hijos.each { par3 ->
                    def filaPar3 = [
                            clase: "nivel3",
                            estilos: "",
                            celdas: []
                    ]
                    def celdaN33 = [
                            colspan: 1,
                            rowspan: 1,
                            clase: "orden",
                            estilos: "",
                            texto: par3.orden
                    ]
                    def celdaPar3 = [
                            colspan: 1,
                            rowspan: 1,
                            clase: "",
                            estilos: "",
                            texto: par3.descripcion
                    ]
                    def celdaPnt3 = [
                            colspan: 1,
                            rowspan: 1,
                            clase: "numero",
                            estilos: "",
                            texto: g.formatNumber(number: par3.puntaje, maxFractionDigits: 2, minFractionDigits: 2, format: "##,##0", locale: "ec")
                    ]
                    def celdaMin3 = [
                            colspan: 1,
                            rowspan: 1,
                            clase: "numero",
                            estilos: "",
                            texto: g.formatNumber(number: par3.minimo, maxFractionDigits: 2, minFractionDigits: 2, format: "##,##0", locale: "ec")
                    ]
                    def celdaAsg3 = [
                            colspan: 1,
                            rowspan: 1,
                            clase: "numero",
                            estilos: "",
                            texto: ""
                    ]
                    filaPar3.celdas.add(celdaN33)
                    filaPar3.celdas.add(celdaPar3)
                    filaPar3.celdas.add(celdaPnt3)
                    filaPar3.celdas.add(celdaMin3)
                    filaPar3.celdas.add(celdaAsg3)
                    filas.add(filaPar3)

                    rs1++
                    rs2++
                }

                filaPar2.celdas[0].rowspan = rs2
                rs1++
            }
            filaPar1.celdas[0].rowspan = rs1

        }

        filas.each { fila ->
            html += dibujaFila(fila)
        }
        html += "</tbody>"
        html += "</table>"
        render html
    }

    def dibujaFila(params) {
        def str = ""

        str += "<tr class='${params.clase}' style='${params.estilos}'>"

        params.celdas.each { celda ->
            str += "<td colspan='${celda.colspan}' rowspan='${celda.rowspan}' class='${celda.clase}' style='${celda.estilos}'>"
            str += celda.texto
            str += "</td>"
        }

        str += "</tr>"

        return str
    }


    def tree() {
        def concurso = Concurso.get(params.id)
        def parametros = ParametroEvaluacion.findAllByConcursoAndPadreIsNull(concurso, [sort: 'orden'])

        if (parametros.size() == 0) {
            def defaults = ["Experiencia", "Experiencia específica", "Experiencia general", "Experiencia personal técnico", "Indices financieros", "Instrumentos y equipos disponibles",
                    "Metodología de desarrollo", "Metodología y cronograma", "Otros", "Personal técnico", "Plan de trabajo", "Trabajos similares"]
            def max = 100
            def pntj = (max / defaults.size()).toInteger()
            def resto = max

            defaults.eachWithIndex { desc, i ->
                def pnt = pntj
                if (i == defaults.size() - 1) {
                    pnt = resto
                }
                def default1 = new ParametroEvaluacion(
                        concurso: concurso,
                        padre: null,
                        orden: 1,
                        descripcion: desc,
                        puntaje: pnt,
                        minimo: 0
                )
                if (!default1.save()) {
                    println "Error al guardar el default " + i + ": " + default1.errors
                }
                resto -= pnt
            }
            parametros = ParametroEvaluacion.findAllByConcursoAndPadreIsNull(concurso, [sort: 'orden'])
        }

        return [parametroEvaluacionInstanceList: parametros, params: params, concurso: concurso]
    } //tree

    def list() {
        def concurso = Concurso.get(params.id)
        def parametros = ParametroEvaluacion.findAllByConcurso(concurso)
        return [parametroEvaluacionInstanceList: parametros, params: params, concurso: concurso]
    } //list

    def form_ajax() {
        def parametroEvaluacionInstance = new ParametroEvaluacion(params)
        if (params.id) {
            parametroEvaluacionInstance = ParametroEvaluacion.get(params.id)
            if (!parametroEvaluacionInstance) {
                flash.clase = "alert-error"
                flash.message = "No se encontró Parametro Evaluacion con id " + params.id
                redirect(action: "list")
                return
            } //no existe el objeto
        } //es edit
        return [parametroEvaluacionInstance: parametroEvaluacionInstance]
    } //form_ajax

    def save() {
        def parametroEvaluacionInstance
        if (params.id) {
            parametroEvaluacionInstance = ParametroEvaluacion.get(params.id)
            if (!parametroEvaluacionInstance) {
                flash.clase = "alert-error"
                flash.message = "No se encontró Parametro Evaluacion con id " + params.id
                redirect(action: 'list')
                return
            }//no existe el objeto
            parametroEvaluacionInstance.properties = params
        }//es edit
        else {
            parametroEvaluacionInstance = new ParametroEvaluacion(params)
        } //es create
        if (!parametroEvaluacionInstance.save(flush: true)) {
            flash.clase = "alert-error"
            def str = "<h4>No se pudo guardar Parametro Evaluacion " + (parametroEvaluacionInstance.id ? parametroEvaluacionInstance.id : "") + "</h4>"

            str += "<ul>"
            parametroEvaluacionInstance.errors.allErrors.each { err ->
                def msg = err.defaultMessage
                err.arguments.eachWithIndex { arg, i ->
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
            flash.message = "Se ha actualizado correctamente Parametro Evaluacion " + parametroEvaluacionInstance.id
        } else {
            flash.clase = "alert-success"
            flash.message = "Se ha creado correctamente Parametro Evaluacion " + parametroEvaluacionInstance.id
        }
        redirect(action: 'list')
    } //save

    def show_ajax() {
        def parametroEvaluacionInstance = ParametroEvaluacion.get(params.id)
        if (!parametroEvaluacionInstance) {
            flash.clase = "alert-error"
            flash.message = "No se encontró Parametro Evaluacion con id " + params.id
            redirect(action: "list")
            return
        }
        [parametroEvaluacionInstance: parametroEvaluacionInstance]
    } //show

    def delete() {
        def parametroEvaluacionInstance = ParametroEvaluacion.get(params.id)
        if (!parametroEvaluacionInstance) {
            flash.clase = "alert-error"
            flash.message = "No se encontró Parametro Evaluacion con id " + params.id
            redirect(action: "list")
            return
        }

        try {
            parametroEvaluacionInstance.delete(flush: true)
            flash.clase = "alert-success"
            flash.message = "Se ha eliminado correctamente Parametro Evaluacion " + parametroEvaluacionInstance.id
            redirect(action: "list")
        }
        catch (DataIntegrityViolationException e) {
            flash.clase = "alert-error"
            flash.message = "No se pudo eliminar Parametro Evaluacion " + (parametroEvaluacionInstance.id ? parametroEvaluacionInstance.id : "")
            redirect(action: "list")
        }
    } //delete
} //fin controller
