<%--
  Created by IntelliJ IDEA.
  User: fabricio
  Date: 1/17/13
  Time: 4:34 PM
  To change this template use File | Settings | File Templates.
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<html>
    <head>

        <meta name="layout" content="main">
        <script src="${resource(dir: 'js/jquery/plugins/jquery-validation-1.9.0', file: 'jquery.validate.min.js')}"></script>
        <script src="${resource(dir: 'js/jquery/plugins/jquery-validation-1.9.0', file: 'messages_es.js')}"></script>

        <script src="${resource(dir: 'js/jquery/plugins/', file: 'jquery.livequery.js')}"></script>

        <link rel="stylesheet" href="${resource(dir: 'css', file: 'tableHandler.css')}">

        <style type="text/css">

        .formato {
            font-weight : bolder;
        }

        </style>


        <title>Fórmula Polinómica</title>
    </head>

    <body>

        <div class="row" style="margin-bottom: 10px;">
            <div class="span9 btn-group" role="navigation">
                <g:link controller="contrato" action="registroContrato" params="[contrato: contrato?.id]" class="btn btn-ajax btn-new" title="Regresar al contrato">
                    <i class="icon-double-angle-left"></i>
                    Contrato
                </g:link>
                <a href="#" class="btn btn-success" id="btnSave"><i class="icon-save"></i> Guardar</a>

                %{--<g:select name="listaFormulas" id="lista" from="${formulas}" optionValue="key" optionKey="value" style="margin-left: 40px; margin-top: 10px; margin-right: 10px"/>--}%
                <g:select name="listaFormulas" id="lista" from="${formulas}" optionValue="descripcion" optionKey="id" style="margin-left: 40px; margin-top: 10px; margin-right: 10px"/>
                <a href="#" class="btn btn-info" id="btnCopiar"><i class="icon-save"></i> Copiar fórmula polinómica</a>
            </div>

        </div>

        <div id="divTabla">
        </div>

        <script type="text/javascript" src="${resource(dir: 'js', file: 'tableHandlerBody.js')}"></script>
        <script type="text/javascript" src="${resource(dir: 'js', file: 'tableHandler.js')}"></script>

        <script type="text/javascript">
            decimales = 3;
            tabla = $(".table");

            beforeDoEdit = function (sel, tf) {
                var tipo = sel.data("tipo");
                tf.data("tipo", tipo);
            };

            textFieldBinds = {
                keyup : function () {
                    var tipo = $(this).data("tipo");
                    var td = $(this).parents("td");
                    var val = $(this).val();
                    var thTot = $("th." + tipo);
                    var tds = $(".editable[data-tipo=" + tipo + "]").not(td);

                    var tot = parseFloat(val);
                    tds.each(function () {
                        tot += parseFloat($(this).data("valor"));
                    });
                    thTot.text(tot);
                }
            };

            $(".editable").first().addClass("selected");

            $("#btnSave").click(function () {
//                var btn = $(this);
                var str = "";
                $(".editable").each(function () {
                    var td = $(this);
                    var id = td.data("id");
                    var valor = parseFloat(td.data("valor"));
                    var orig = parseFloat(td.data("original"));

                    if (valor != orig) {
                        if (str != "") {
                            str += "&";
                        }
                        str += "valor=" + id + "_" + valor;
                    }
                });
                if (str != "") {
//                    btn.hide().after(spinner);
                    $.ajax({
                        type    : "POST",
                        url     : "${createLink(action:'saveCambiosPolinomica')}",
                        data    : str,
                        success : function (msg) {
//                            spinner.remove();
//                            btn.show();
                            var parts = msg.split("_");
                            var ok = parts[0];
                            var no = parts[1];
                            doHighlight({elem : $(ok), clase : "ok"});
                            doHighlight({elem : $(no), clase : "no"});
                        }
                    });
                }
                return false;
            });

            $("#tabs").tabs({
                heightStyle : "fill",
                activate    : function (event, ui) {
                    ui.newPanel.find(".editable").first().addClass("selected");
                }
            });

            //cargar tabla de fórmulas polinómicas

            function cargarTabla(id) {

//                var interval = loading("detalle")

                $.ajax({type : "POST", url : "${g.createLink(controller: 'contrato',action:'tablaFormula_ajax')}",
                    data     : {
                        id: id
                    },
                    success  : function (msg) {
                        $("#divTabla").html(msg);
                    }
                });
            }

            $("#lista").change(function () {
                var idFormula = $(this).val();
                cargarTabla(idFormula);
            });

            cargarTabla( $("#lista").val());


            function loading(div) {
                y = 0;
                $("#" + div).html("<div class='tituloChevere' id='loading'>Sistema Janus - Cargando, Espere por favor</div>")
                var interval = setInterval(function () {
                    if (y == 30) {
                        $("#detalle").html("<div class='tituloChevere' id='loading'>Cargando, Espere por favor</div>")
                        y = 0
                    }
                    $("#loading").append(".");
                    y++
                }, 500);
                return interval
            }


            $("#btnCopiar").click(function () {

                if (confirm("Está seguro de copiar la fórmula polinómica?")) {

                    $.ajax({
                        type : "POST",
                        url : "${g.createLink(controller: 'contrato',action:'copiarFormula')}",
                        data     : {
                            id: $("#lista").val()
                        },
                        success  : function (msg) {
                            var alerta;
                            if(msg == 'si'){
                                alert("Fórmula polinómica copiada correctamente");
                                window.location.reload(true)
                            }else{
                                alert("Error al copiar la fórmula polinómica")
                            }
                        }
                    });
                }

            });


        </script>

    </body>
</html>