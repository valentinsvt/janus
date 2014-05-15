<%--
  Created by IntelliJ IDEA.
  User: luz
  Date: 3/27/13
  Time: 3:18 PM
  To change this template use File | Settings | File Templates.
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<html>
    <head>
        <meta name="layout" content="main">
        <title>Mant. de Precios por volumen</title>


        <script type="text/javascript" src="${resource(dir: 'js', file: 'tableHandlerBody.js')}"></script>
        %{--<script type="text/javascript" src="${resource(dir: 'js', file: 'tableHandler.js')}"></script>--}%

        <link rel="stylesheet" href="${resource(dir: 'css', file: 'tableHandler.css')}"/>

        <style type="text/css">
        th {
            vertical-align : middle !important;
            font-size      : 12px;
        }

        td {
            padding : 3px;
        }

        .number {
            text-align : right !important;
            width      : 100px;
        }

        .unidad {
            text-align : center !important;
        }

        .editable {
            background    : url(${resource(dir:'images', file:'edit.gif')}) right no-repeat;
            padding-right : 18px !important;
        }

        .changed {
            background-color : #C3DBC3 !important;
        }

        .old {
            color : #21337f;
        }
        </style>

    </head>

    <body>

        <div class="btn-toolbar" style="margin-top: 5px;">
            <div class="btn-group">
                <a href="${g.createLink(controller: 'mantenimientoItems', action: 'precios')}" class="btn " title="Regresar">
                    <i class="icon-arrow-left"></i>
                    Regresar
                </a>
            </div>
        </div>

        <fieldset class="borde">
            <div class="row">
                <div class="span4" align="center">Tipo de lista de Precios</div>

                <div class="span2" align="center">Fecha</div>
            </div>

            <div class="row">
                <div class="span4" align="center">
                    <g:select class="listPrecio span2" name="listaPrecio"
                              from="${janus.TipoLista.findAllByIdInList([3L, 4L, 5L], [sort: 'descripcion'])}" optionKey="id"
                              optionValue="${{ it.descripcion + ' (' + it.codigo + ')' }}"
                              noSelection="['-1': 'Todos']"
                              disabled="false" style="margin-left: 20px; width: 300px; margin-right: 50px"/>
                </div>

                <div class="span2" align="center">
                    <elm:datepicker name="fecha" class="fecha datepicker input-small" value="" maxDate="'+1y'"/>
                </div>

                <div class="btn-group span1" style="margin-left: 5px; margin-right: 10px; width: 200px;">
                    <a href="#" class="btn btn-consultar"><i class="icon-search"></i> Ver</a>
                    <a href="#" class="btn btn-actualizar btn-success"><i class="icon-save"></i> Guardar</a>
                </div>
            </div>
        </fieldset>

        <fieldset class="borde" %{--style="width: 1170px"--}%>

            <div id="divTabla" style="height: 760px; overflow-y:auto; overflow-x: hidden;">

            </div>


            <fieldset class="borde hide" style="width: 1170px; height: 58px" id="error">

                <div class="alert alert-error">

                    <h4 style="margin-left: 450px">No existen datos!!</h4>

                    <div style="margin-left: 420px">
                        Ingrese los parámetros de búsqueda!

                    </div>
                </div>

            </fieldset>
        </fieldset>

        <script type="text/javascript">


            function consultar() {
                $("#divTabla").html("");
                var $fecha = $("#fecha");

                var lgar = $("#listaPrecio").val();
                var fcha = $fecha.val();

                if (fcha == "") {
                    fcha = new Date().toString("dd-MM-yyyy")
                    $fecha.val(fcha);
                }
                $.ajax({
                    type    : "POST",
                    url     : "${createLink(action:'tablaVolumen')}",
                    data    : {
                        lgar  : lgar,
                        fecha : fcha,
                        max   : 100,
                        pag   : 1
                    },
                    success : function (msg) {
                        $("#divTabla").html(msg);
                        $("#dlgLoad").dialog("close");
                    }
                });

            }

            $(function () {
                $(".btn-consultar").click(function () {
                    var lgar = $("#listaPrecio").val();
                    $("#error").hide();
                    $("#dlgLoad").dialog("open");
                    consultar();
                    $("#divTabla").show();
                });

                $(".btn-actualizar").click(function () {
                    $("#dlgLoad").dialog("open");
                    var data = "";

                    var fcha = $("#fecha").val();

//                    ////console.log("fecha" + fcha)

                    $(".editable").each(function () {
                        var id = $(this).data("id");
                        var lugar = $(this).data("lugar");
                        var item = $(this).data("item");
                        var valor = $(this).data("valor");
                        var data1 = $(this).data("original");
//                        ////console.log(chk);
//                        ////console.log(data1)

                        if ((parseFloat(valor) > 0 && parseFloat(data1) != parseFloat(valor))) {
                            if (data != "") {
                                data += "&";
                            }
                            var val = valor ? valor : data1;
                            data += "item=" + id + "_" + item + "_" + lugar + "_" + valor + "_" + fcha;
                        }
                    });

                    $.ajax({
                        type    : "POST",
                        url     : "${createLink(action: 'actualizarVol')}",
                        data    : data,
                        success : function (msg) {
                            $("#dlgLoad").dialog("close");
                            var parts = msg.split("_");
                            var ok = parts[0];
                            var no = parts[1];

                            $(ok).each(function () {
                                var fec = $(this).siblings(".fecha");
                                fec.text($("#fecha").val());
                                var $tdChk = $(this).siblings(".chk");
                                var chk = $tdChk.children("input").is(":checked");
                                if (chk) {
                                    $tdChk.html('<i class="icon-ok"></i>');
                                }
                            });

                            doHighlight({elem : $(ok), clase : "ok"});
                            doHighlight({elem : $(no), clase : "no"});
                        }
                    });
                });

            });
        </script>

    </body>
</html>