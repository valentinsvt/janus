<%@ page contentType="text/html;charset=UTF-8" %>
<html>
    <head>
        <meta name="layout" content="main">
        <title>Mantenimiento de Indices</title>

        <script type="text/javascript" src="${resource(dir: 'js', file: 'tableHandlerBody.js')}"></script>

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
        </style>
    </head>

    <body>
        <div class="btn-toolbar" style="margin-top: 5px;">
            <div class="btn-group">
                <a href="${g.createLink(action: 'valorIndice')}" class="btn " title="Regresar">
                    <i class="icon-arrow-left"></i>
                    Ver valores de Indices
                </a>
            </div>
        </div>

        <fieldset class="borde">
            <div class="row">
                <div class="span4" align="center">Período de Índices</div>
            </div>

            <div class="row">
                <div class="span4" align="center">
                    <g:select class="span2" name="periodoIndices"
                              from="${janus.ejecucion.PeriodosInec.findAllByPeriodoCerrado("N", [sort: 'fechaInicio'])}" optionKey="id"
                              optionValue="${{ it.descripcion }}"
                              disabled="false" style="margin-left: 20px; width: 300px; margin-right: 50px"/>
                </div>

                <div class="btn-group span1" style="margin-left: 5px; margin-right: 10px; width: 200px;">
                    <a href="#" class="btn btn-consultar"><i class="icon-search"></i> Ver</a>
                    <a href="#" class="btn btn-actualizar btn-success"><i class="icon-save"></i> Guardar</a>

                </div>
                %{--<div> <b>NOTA:</b> No se puede guardar valores iguales a <b> 0</b></div>--}%
            </div>
        </fieldset>

        <fieldset class="borde" style="width: 1100px">

            <div id="divTabla" style="height: 760px; overflow-y:auto; overflow-x: hidden;">

            </div>


%{--
            <fieldset class="borde hide" style="width: 1170px; height: 58px" id="error">

                <div class="alert alert-error">

                    <h4 style="margin-left: 450px">No existen datos!!</h4>

                    <div style="margin-left: 420px">
                        Ingrese los parámetros de búsqueda!

                    </div>
                </div>

            </fieldset>
--}%
        </fieldset>

        <script type="text/javascript">


            function consultar() {
                $("#divTabla").html("");

                var prin = $("#periodoIndices").val();

//                console.log("antes de ajax .. periodo de indices:" + prin);

                $.ajax({
                    type    : "POST",
                    url     : "${createLink(action: 'tablaValores')}",
                    data    : { prin: prin, max: 100, pag: 1 },
                    success : function (msg) {
                        $("#divTabla").html(msg);
                        $("#dlgLoad").dialog("close");
                    }
                });

            }

            $(function () {
                $(".btn-consultar").click(function () {
                    $("#error").hide();
                    $("#dlgLoad").dialog("open");
                    consultar();
                    $("#divTabla").show();
                });

                $(".btn-actualizar").click(function () {
                    $("#dlgLoad").dialog("open");
                    var data = "";

                    $(".editable").each(function () {
                        var id = $(this).data("id");
                        var prin = $(this).data("prin");
                        var indc = $(this).data("indc");
                        var valor = $(this).data("valor");
                        var data1 = $(this).data("original");
                        ////console.log(chk);

                        if ((parseFloat(data1) != parseFloat(valor))) {
                            if (data != "") {
                                data += "&";
                            }
                            var val = valor ? valor : data1;
                            data += "item=" + id + "_" + prin + "_" + indc + "_" + valor;
                        }
                        //console.log("item: " + data)
                    });


                    $.ajax({
                        type    : "POST",
                        url     : "${createLink(action: 'actualizaVlin')}",
                        data    : data,
                        success : function (msg) {
                            $("#dlgLoad").dialog("close");
                            var parts = msg.split("_");
                            var ok = parts[0];
                            var no = parts[1];

                            $(ok).each(function () {
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