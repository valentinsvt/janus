package janus.pac

class RegistroParametrosController {

    def index() {}


    def registro(){
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
        html += '</tr>'
        html += "</thead>"
        html += "<tbody>"
        def filas = []
        def temp=[]
        ParametroEvaluacion.findAllByConcursoAndPadreIsNull(concurso, [sort: 'orden']).each { par1 ->
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
                    texto: g.formatNumber(number: par1.puntaje, maxFractionDigits: 2, minFractionDigits: 2)
            ]
            def celdaMin1 = [
                    colspan: 1,
                    rowspan: 1,
                    clase: "numero",
                    estilos: "",
                    texto: g.formatNumber(number: par1.minimo, maxFractionDigits: 2, minFractionDigits: 2)
            ]
            filaPar1.celdas.add(celdaN11)
            filaPar1.celdas.add(celdaN12)
            filaPar1.celdas.add(celdaPar1)
            filaPar1.celdas.add(celdaPnt1)
            filaPar1.celdas.add(celdaMin1)
            filas.add(filaPar1)


            def rs1 = 1
            ParametroEvaluacion.findAllByPadre(par1).each { par2 ->
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
                        clase: "numero max",
                        estilos: "",
                        texto: g.formatNumber(number: par2.puntaje, maxFractionDigits: 2, minFractionDigits: 2)
                ]
                def celdaMin2 = [
                        colspan: 1,
                        rowspan: 1,
                        clase: "numero editable",
                        estilos: "",
                        texto: g.formatNumber(number: par2.minimo, maxFractionDigits: 2, minFractionDigits: 2),
                        id:par2.id
                ]
                filaPar2.celdas.add(celdaN22)
                filaPar2.celdas.add(celdaN23)
                filaPar2.celdas.add(celdaPar2)
                filaPar2.celdas.add(celdaPnt2)
                filaPar2.celdas.add(celdaMin2)
//                filas.add(filaPar2)
                def filaPadre = filaPar2

                def rs2 = 1
//                println "fila padre 1 "+filaPadre.celdas[4]
                ParametroEvaluacion.findAllByPadre(par2).each { par3 ->
                    filaPadre.celdas[4].clase="numero"
//                    println "fila padre 2 "+filaPadre.celdas[4]

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
                            clase: "numero max",
                            estilos: "",
                            texto: g.formatNumber(number: par3.puntaje, maxFractionDigits: 2, minFractionDigits: 2)
                    ]
                    def celdaMin3 = [
                            colspan: 1,
                            rowspan: 1,
                            clase: "numero editable",
                            estilos: "",
                            texto: g.formatNumber(number: par3.minimo, maxFractionDigits: 2, minFractionDigits: 2),
                            id:par3.id
                    ]
                    filaPar3.celdas.add(celdaN33)
                    filaPar3.celdas.add(celdaPar3)
                    filaPar3.celdas.add(celdaPnt3)
                    filaPar3.celdas.add(celdaMin3)
//                    filas.add(filaPar3)
                    temp.add(filaPar3)
                    rs1++
                    rs2++

                }

                filas.add(filaPadre)
                temp.each {tmp->
                    filas.add(tmp)
                }
                temp=[]
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
        [html:html,concurso:concurso]
    }
    def dibujaFila(params) {
        def str = ""

        str += "<tr class='${params.clase}' style='${params.estilos}'>"

        params.celdas.each { celda ->
            if (celda.clase=~"editable"){
                str += "<td colspan='${celda.colspan}' rowspan='${celda.rowspan}' class='${celda.clase}' style='${celda.estilos}'>"
                str+= "<input type='text' class='i_t' max='10'  iden='${celda.id}'> "
                str += "</td>"
            }else{
                str += "<td colspan='${celda.colspan}' rowspan='${celda.rowspan}' class='${celda.clase}' style='${celda.estilos}'>"
                str += celda.texto
                str += "</td>"
            }

        }

        str += "</tr>"

        return str
    }

    def guardarParametros(){
        println "guardar par "+params
        if (params.datos.trim().size()>0){
            def p = params.datos.split(";")
            p.each {
                def parts = p.split("&")
                /*aqui guardar*/
            }
        }
        render "ok"
    }
}
