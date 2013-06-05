<%@ page contentType="text/html;charset=UTF-8" %>
<html>

    <head>

        <meta name="layout" content="main">
        <title>Registrar Precios</title>


        <script type="text/javascript" src="${resource(dir: 'js', file: 'tableHandlerBody.js')}"></script>
        <script type="text/javascript" src="${resource(dir: 'js', file: 'tableHandler.js')}"></script>

        <link rel="stylesheet" href="${resource(dir: 'css', file: 'tableHandler.css')}">

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
                <div class="span4" align="center">Lista de Precios</div>

                <div class="span2" align="center">Ver</div>
            </div>

            <div class="row">
                <div class="span4" align="center">
                    <g:select class="listPrecio span2" name="listaPrecio"
                              from="${janus.Lugar.list([sort: 'descripcion'])}" optionKey="id"
                              optionValue="${{ it.descripcion }}"
                              noSelection="['-1': 'Seleccione']"
                              disabled="false" style="width: 300px;"/>
                </div>
                %{--<div class="span1" align="center">--}%
                %{--<elm:datepicker name="fecha" class="fecha datepicker input-small" value=""/>--}%
                %{--</div>--}%

                %{--<div class="span1" align="center" style="margin-left: 50px; margin-right: 40px">--}%
                %{--<g:checkBox name="todosPrecios" id="todos" checked="false" class="span1"/>--}%
                %{--</div>--}%

                <div class="span2" align="center">
                    <g:select name="tipo" from="${janus.Grupo.findAllByIdLessThanEquals(3)}" class="span2" optionKey="id"
                              optionValue="descripcion" noSelection="['-1': 'Todos']"/>
                </div>

                %{--<div class="btn-group span1" style="margin-left: 100px; margin-right: 10px; width: 230px;" data-toggle="buttons-checkbox">--}%
                %{--<a href="#" class="btn active" id="reg">Registrados</a>--}%
                %{--<a href="#" class="btn active" id="nreg">No registrados</a>--}%
                %{--</div>--}%

                <div class="btn-group span1" style=" width: 200px;">
                    <a href="#" class="btn btn-consultar"><i class="icon-search"></i>Consultar</a>
                    <a href="#" class="btn btn-actualizar btn-success"><i class="icon-save"></i>Guardar</a>
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
                var lgar = $("#listaPrecio").val();

                var tipo = $("#tipo").val();

                $.ajax({
                    type    : "POST",
                    url     : "${createLink(action:'tablaRegistrar')}",
                    data    : {
                        lgar : lgar,
                        tipo : tipo,
                        max  : 100,
                        pag  : 1
                    },
                    success : function (msg) {
                        $("#divTabla").html(msg);
                        $("#dlgLoad").dialog("close");
                    }
                });

            }

            $(function () {
//        $("#todos").click(function () {
//            var fecha2 = new Date().toString("dd-MM-yyyy");
////            ////console.log(fecha2);
//            if ($("#todos").attr("checked") == "checked") {
////
//                $("#fecha").attr("value", fecha2);
//            }
//            else {
////               )
//            }
//        });

                $(".btn-consultar").click(function () {

                    var lgar = $("#listaPrecio").val();

                    if (lgar != -1) {

                        $("#error").hide();
                        $("#dlgLoad").dialog("open");
                        consultar();
                        $("#divTabla").show();
                    }
                    else {

                        $("#divTabla").html("").hide();

                        $("#error").show();

                    }

                });

                $(".btn-actualizar").click(function () {
                    $("#dlgLoad").dialog("open");
                    var data = "";

                    $(".editable").each(function () {
                        var id = $(this).attr("id");
                        var valor = $(this).data("valor");
                        var data1 = $(this).data("original");

                        var chk = $(this).siblings(".chk").children("input").is(":checked");

                        if (chk || (parseFloat(valor) > 0 && parseFloat(data1) != parseFloat(valor))) {
                            if (data != "") {
                                data += "&";
                            }
                            var val = valor ? valor : data1;
                            data += "item=" + id + "_" + val + "_" + chk;
                        }
                    });

                    $.ajax({
                        type    : "POST",
                        url     : "${createLink(action: 'actualizarRegistro')}",
                        data    : data,
                        success : function (msg) {
                            $("#dlgLoad").dialog("close");
                            var parts = msg.split("_");
                            var ok = parts[0];
                            var no = parts[1];

                            $(ok).each(function () {
                                $(this).removeClass("editable").removeClass("selected");
                                var $tdChk = $(this).siblings(".chk");
                                var chk = $tdChk.children("input").is(":checked");
                                if (chk) {
                                    $tdChk.html('<i class="icon-ok"></i>');
                                }
                            });
                            $(".editable").first().addClass("selected");
                            doHighlight({elem : $(ok), clase : "ok"});
                            doHighlight({elem : $(no), clase : "no"});
                        }
                    });
                    return false;
                });

            });
        </script>
    </body>
</html>