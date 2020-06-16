<%--
  Created by IntelliJ IDEA.
  User: fabricio
  Date: 16/06/20
  Time: 10:45
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<html>

<head>
    <meta name="layout" content="main">
    <title>Items en uso</title>

    %{--<script type="text/javascript" src="${resource(dir: 'js', file: 'tableHandlerBody.js')}"></script>--}%
    %{--<script type="text/javascript" src="${resource(dir: 'js', file: 'tableHandler.js')}"></script>--}%
    %{--<link rel="stylesheet" href="${resource(dir: 'css', file: 'tableHandler.css')}">--}%
</head>

<body>

<div class="btn-toolbar" style="margin-top: 5px;">
    <div class="btn-group">
        <div class="span2">
            <a href="${g.createLink(controller: 'mantenimientoItems', action: 'registro')}" class="btn " title="Regresar">
                <i class="icon-arrow-left"></i>
                Regresar
            </a>
        </div>
        <div class="btn-group span1" style=" width: 200px;">
            <a href="#" class="btn btn-consultar"><i class="icon-search"></i>Consultar</a>
            <a href="#" class="btn btn-actualizar btn-success"><i class="icon-save"></i>Guardar</a>
        </div>
    </div>
</div>


%{--<fieldset class="borde">--}%

    <div style="margin-top: 10px; min-height: 650px" class="vertical-container">
        <table class="table table-bordered table-hover table-condensed" style="width: 100%">
            <thead>
            <tr>
                <th class="alinear" style="width: 10%">Código</th>
                <th class="alinear" style="width: 45%">Nombre</th>
                <th class="alinear" style="width: 10%">Unidad</th>
                <th class="alinear" style="width: 10%">Fecha</th>
                <th class="alinear" style="width: 10%">Estado</th>
                <th style="width: 15%">Registrar
                    <a href="#" class="btn " title="Todos" id="seleccionar"><i class="icon-check"></i>Todos</a>
                </th>
            </tr>
            </thead>
        </table>

        <div id="tabla">
        </div>
    </div>

    %{--<div id="divTabla" style="height: 760px; overflow-y:auto; overflow-x: hidden;">--}%

    %{--</div>--}%


    %{--<fieldset class="borde hide" style="width: 1170px; height: 58px" id="error">--}%

        %{--<div class="alert alert-error">--}%
            %{--<h4 style="margin-left: 450px">No existen datos!!</h4>--}%
        %{--</div>--}%
    %{--</fieldset>--}%

%{--</fieldset>--}%


<script type="text/javascript">

    $(function () {

        $("#seleccionar").click(function(){
            $(".chequear")[0].checked ? $(".chequear").prop("checked", false) : $(".chequear").prop("checked", true);
        });

        function consultar() {
            var lgar = $("#listaPrecio").val();

            var tipo = $("#tipo").val();

            $.ajax({
                type    : "POST",
                url     : "${createLink(controller: 'mantenimientoItems', action:'tablaItemsUso_ajax')}",
                data    : {
                },
                success : function (msg) {
                    $("#tabla").html(msg);
                    $("#dlgLoad").dialog("close");
                }
            });

        }

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
            %{--$("#dlgLoad").dialog("open");--}%
            %{--var data = "";--}%

            %{--$(".editable").each(function () {--}%
                %{--var id = $(this).attr("id");--}%
                %{--var valor = $(this).data("valor");--}%
                %{--var data1 = $(this).data("original");--}%

                %{--var chk = $(this).siblings(".chk").children("input").is(":checked");--}%

                %{--if (chk || (parseFloat(valor) > 0 && parseFloat(data1) != parseFloat(valor))) {--}%
                    %{--if (data != "") {--}%
                        %{--data += "&";--}%
                    %{--}--}%
                    %{--var val = valor ? valor : data1;--}%
                    %{--data += "item=" + id + "_" + val + "_" + chk;--}%
                %{--}--}%
            %{--});--}%

            %{--$.ajax({--}%
                %{--type    : "POST",--}%
                %{--url     : "${createLink(action: 'actualizarRegistro')}",--}%
                %{--data    : data,--}%
                %{--success : function (msg) {--}%
                    %{--$("#dlgLoad").dialog("close");--}%
                    %{--var parts = msg.split("_");--}%
                    %{--var ok = parts[0];--}%
                    %{--var no = parts[1];--}%

                    %{--$(ok).each(function () {--}%
                        %{--$(this).removeClass("editable").removeClass("selected");--}%
                        %{--var $tdChk = $(this).siblings(".chk");--}%
                        %{--var chk = $tdChk.children("input").is(":checked");--}%
                        %{--if (chk) {--}%
                            %{--$tdChk.html('<i class="icon-ok"></i>');--}%
                        %{--}--}%
                    %{--});--}%
                    %{--$(".editable").first().addClass("selected");--}%
                    %{--doHighlight({elem : $(ok), clase : "ok"});--}%
                    %{--doHighlight({elem : $(no), clase : "no"});--}%
                %{--}--}%
            %{--});--}%
            %{--return false;--}%
        });

    });
</script>
</body>
</html>