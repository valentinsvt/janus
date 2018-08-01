<%--
  Created by IntelliJ IDEA.
  User: fabricio
  Date: 2/5/13
  Time: 12:28 PM
  To change this template use File | Settings | File Templates.
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <meta name="layout" content="main">
    <script type="text/javascript"
            src="${resource(dir: 'js/jquery/plugins/box/js', file: 'jquery.luz.box.js')}"></script>
    <link href='${resource(dir: "js/jquery/plugins/box/css", file: "jquery.luz.box.css")}' rel='stylesheet'
          type='text/css'>

    <title>Registro Rol</title>
</head>

<body>

<div class="span6">

    <div class="span1" style="font-weight: bold">Dirección:</div>


    %{--<g:select name="departamento" class="departamento" from="${janus.Departamento.list()}" optionValue="descripcion"--}%
              %{--optionKey="id" style="width: 400px" noSelection="['-1': '-Escoja el departamento-']"/>--}%

    <g:select name="departamento" class="departamento" from="${janus.Direccion.list([sort: 'nombre'])}" optionValue="nombre"
              optionKey="id" style="width: 400px" noSelection="['-1': '-Escoja la direccion-']"/>



    <div class="span12" id="filaPersonas"></div>
    <hr>
    <div class="span4" id="funcionDiv" style="margin-top: 10px;">
        <div class="span2" style="margin-left: -1px; font-weight: bold">Nueva Función:</div>
        %{--<elm:select name="funcion" id="funcion" from="${janus.Funcion?.list()}" optionValue="descripcion" optionKey="id"--}%
                    %{--optionClass="${{ it?.descripcion }}" style="margin-left: -10px"/>--}%

        <elm:select name="funcion" id="funcion" from="${funciones}" optionValue="descripcion" optionKey="id"
                    optionClass="${{ it?.descripcion }}" style="margin-left: -50px"/>
    </div>

    <div class="span2 btn-group" style="margin-left: -10px; margin-top: 10px;">
        <button class="btn btnAdicionar" id="adicionar"><i class="icon-plus"></i> Adicionar</button>
        <button class="btn btnRegresar" id="regresar"><i class="icon-arrow-left"></i> Regresar</button>

    </div>


    <div class="span12" style="width: 500px">

        <table class="table table-bordered table-striped table-hover table-condensed " id="tablaFuncion">

            <thead>
            <tr>
                <th style="width: 50px">N°</th>
                <th style="width: 250px">Función</th>
                <th style="width: 20px"><i class="icon-cut"></i></th>
            </tr>

            </thead>

            <tbody id="funcionPersona">

            </tbody>

        </table>

    </div>

</div>

<div class="span6">

    %{--<div class="span4" style="margin-left: -40px">--}%
    %{--<div class="span2" style="margin-left: -40px; font-weight: bold">Nueva Función: </div>--}%
    %{--<elm:select name="funcion" id="funcion"  from="${janus.Funcion?.list()}" optionValue="descripcion" optionKey="id"--}%
    %{--optionClass="${{it?.descripcion}}"/>--}%
    %{----}%
    %{--</div>--}%

    %{--<div class="span2 btn-group" style="margin-left: -10px">--}%
    %{--<button class="btn btnAdicionar" id="adicionar"><i class="icon-plus"></i> Adicionar</button>--}%
    %{--<button class="btn btnRegresar"  id="regresar"><i class="icon-arrow-left"></i> Regresar</button>--}%

    %{--</div>--}%

</div>



<script type="text/javascript">

    %{--//borrar--}%
    %{--function borrar($btn) {--}%
    %{--var tr = $btn.parents("tr");--}%
    %{--var idRol = $btn.attr("id");--}%

    %{--$.box({--}%
    %{--imageClass: "box_info",--}%
    %{--text      : "Esta seguro que desea eliminar esta función de la persona seleccionada?",--}%
    %{--title     : "Confirmación",--}%
    %{--iconClose : false,--}%
    %{--dialog    : {--}%
    %{--resizable    : false,--}%
    %{--draggable    : false,--}%
    %{--closeOnEscape: false,--}%
    %{--buttons      : {--}%
    %{--"Aceptar" : function () {--}%
    %{--$.ajax({--}%
    %{--type: "POST",--}%
    %{--url: "${g.createLink(controller: "personaRol", action: 'delete')}",--}%
    %{--data: { id:idRol},--}%
    %{--success: function (msg) {--}%
    %{--if(msg == "OK") {--}%
    %{--tr.remove();--}%
    %{--}--}%
    %{--}--}%

    %{--});--}%
    %{--},--}%
    %{--"Cancelar": function () {--}%


    %{--}--}%
    %{--}--}%
    %{--}--}%
    %{--});--}%
    %{--}--}%

    %{--cargarFuncion();--}%

    %{--$(".persona").change(function () {--}%


    %{--cargarFuncion();--}%

    %{--});--}%

    %{--function cargarFuncion () {--}%

    %{--var idPersona = $(".persona").val();--}%



    %{--$.ajax({--}%
    %{--type: "POST",--}%
    %{--url: "${g.createLink(action: 'obtenerFuncion')}",--}%
    %{--data : { id: idPersona--}%

    %{--} ,--}%

    %{--success: function (msg) {--}%

    %{--$("#funcionPersona").html(msg);--}%


    %{--}--}%


    %{--});--}%



    %{--}--}%

    $("#adicionar").click(function () {

        var idPersona = $(".persona").val();

        var valorAdicionar = $("#funcion option:selected").attr("class");
        var idAcicionar = $("#funcion").val();


//        console.log("-->" + idAcicionar)


        var tbody = $("#funcionPersona");
        var rows = tbody.children("tr").length;
        var continuar = true;

        tbody.children("tr").each(function () {
            var fila = $(this);
            var id = fila.data("id");
            var valor = fila.data("valor");

            if (id == idAcicionar || valor == valorAdicionar) {
                continuar = false;
            }
        });

        if (continuar) {

            //AJAX

            $.ajax({
                type: "POST",
                url: "${g.createLink(controller: "personaRol", action: 'grabarFuncion')}",
                data: { id: idPersona,

                    rol: idAcicionar
                },
                success: function (msg) {
                    var parts = msg.split("_");
                    if (parts[0] == "OK") {
                        var tr = $("<tr>");
                        var tdNumero = $("<td>");
                        var tdFuncion = $("<td>");
                        var tdAccion = $("<td>");
                        var boton = $("<a href='#' class='btn btn-danger btnBorrar'><i class='icon-trash icon-large'></i></a>");
                        var id = parts[1];
                        boton.attr("id", id);
                        boton.click(function () {
                            borrar(boton);
                        });
                        tdAccion.append(boton);

                        tr.data({
                            id: idAcicionar,
                            valor: valorAdicionar
                        });

                        tdNumero.html(rows + 1);
                        tdFuncion.html(valorAdicionar);
                        tr.append(tdNumero).append(tdFuncion).append(tdAccion);
                        tbody.append(tr);
                    }
                }
            });


        } else {
            //avisar q ya existe

            alert("La persona ya tiene asignado ese rol!")
        }

    });

    $("#regresar").click(function () {

        location.href = "${createLink(controller: 'persona', action: 'list')}";


    });

    function loadPersonas() {
        var idDep = $("#departamento").val();

//                        console.log("dep-->>" + idDep)
        $.ajax({
            type: "POST",
            url: "${g.createLink(controller: 'personaRol', action:'getPersonas')}",
            data: {id: idDep

            },
            success: function (msg) {

                $("#filaPersonas").html(msg);
//                $("#funcion").show()
            }
        });
    }


    $("#departamento").change(function () {

        if ($("#departamento").val() != -1) {

            loadPersonas();


        }


    });







</script>

</body>
</html>