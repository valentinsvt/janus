<%@ page contentType="text/html;charset=UTF-8" %>
<html>

<head>

    <meta name="layout" content="main">
    <title>Mantenimiento de Precios</title>


    <style type="text/css">


    .selected {

        border: solid 2px blue !important;

    }


    </style>

</head>

<body>

<fieldset class="borde">

    <div class="span12 noMargin">

        <div class="span4 noMargin" align="center">Lista de Precios</div>

        <div class="span1 noMargin" align="center">Fecha</div>

        <div class="span2 noMargin" align="center">Todos</div>

        <div class="span2 noMargin" align="center">Ver</div>

    </div>

    <div class="span12 noMargin">

        <div class="span4 noMargin" align="center">
            <g:select class="listPrecio span2" name="listaPrecio"
                      from="${janus.Lugar.list([sort: 'descripcion'])}" optionKey="id"
                      optionValue="${{it.descripcion + ' (' + it.tipo + ')'}}"
                      noSelection="['-1': 'Seleccione']"
                      disabled="false" style="margin-left: 20px; width: 300px; margin-right: 50px"/>
        </div>

        <div class="span1 noMargin" align="center">
            <elm:datepicker name="fecha" class="fecha datepicker input-small" value=""/>
        </div>

        <div class="span1" align="center" style="margin-left: 50px; margin-right: 40px">
            <g:checkBox name="todosPrecios" id="todos" checked="false" class="span1"/>
        </div>

        <div class="span2 noMargin" align="center">
            <g:select name="tipo" from="${janus.Grupo.findAllByIdLessThanEquals(3)}" class="span2" optionKey="id"
                      optionValue="descripcion" noSelection="['-1': 'Todos']"/>
        </div>

        <div class="btn-group span1" style="margin-left: 10px; margin-right: 10px">
            <a href="#" class="btn btn-consultar"><i class="icon-search"></i>Consultar</a>

            <a href="#" class="btn btn-cargar"><i class="icon-edit"></i> Precios a la Fecha</a>
            <a href="#" class="btn btn-actualizar"><i class="icon-refresh"></i>Actualizar</a>
        </div>
    </div>

</fieldset>


<fieldset class="borde" style="width: 1170px">

    <div id="tablaPrecios">

    </div>

</fieldset>

<fieldset class="borde hide" style="width: 1170px; height: 58px" id="error">

    <div class="alert alert-error">

        <h4 style="margin-left: 450px">No existen datos!!</h4>

        <div style="margin-left: 420px">
            Ingrese los parámetros de búsqueda!

        </div>
    </div>

</fieldset>

<script type="text/javascript">


    function consultar() {
        var lgar = $("#listaPrecio").val();
        var fcha = $("#fecha").val();


        if (fcha == "") {

            fcha = new Date().toString("dd-MM-yyyy")

            $("#fecha").val(fcha);
        }

        var todos = "";
        if ($("#todos").attr("checked") == "checked") {
            todos = 1
        } else {
            todos = 2
        }
        var tipo = $("#tipo").val();

        $.ajax({
            type:"POST",
            url:"${createLink(action:'tabla')}",
            data:{
                lgar:lgar,
                fecha:fcha,
                todos:todos,
                tipo:tipo,
                max:100,
                pag:1
            },
            success:function (msg) {
                $("#tablaPrecios").html(msg);
                $("#dlgLoad").dialog("close");
            }
        });


    }

    $(function () {
        $("#todos").click(function () {
            var fecha2 = new Date().toString("dd-MM-yyyy");
//            console.log(fecha2);
            if ($("#todos").attr("checked") == "checked") {
//
                $("#fecha").attr("value", fecha2);
            }
            else {
//               )
            }
        });


        $(".btn-consultar").click(function () {

            var lgar = $("#listaPrecio").val();

            if (lgar != -1) {

                $("#error").hide();
                $("#dlgLoad").dialog("open");
                consultar();
                $("#tablaPrecios").show();
            }
            else {

                $("#tablaPrecios").hide();

                $("#error").show();


            }


        });

        $(".btn-cargar").click(function () {

            $("#dlgLoad").dialog("open");


            var fcha = new Date().toString("dd-MM-yyyy");

            $("#fecha").val(fcha);


            var todos = 3;


            $.ajax({
                type:"POST",
                url:"${createLink(action:'tabla')}",
                data:{
                    lgar:-1,
                    fecha:fcha,
                    todos:todos,
                    tipo:-1,
                    max:100,
                    pag:1
                },
                success:function (msg) {
                    $("#tablaPrecios").html(msg);
                    $("#dlgLoad").dialog("close");
                }
            });


        });

        $(".btn-actualizar").click(function () {
            $("#dlgLoad").dialog("open");
            var data = "";
            $(".editable").each(function () {
                var id = $(this).attr("id");
                var valor = $(this).data("valor");

                if (parseFloat(valor) > 0 && parseFloat($(this).data("original")) != parseFloat(valor)) {
                    if (data != "") {
                        data += "&";
                    }
                    data += "item=" + id + "_" + valor;
                }
            });

            $.ajax({
                type:"POST",
                url:"${createLink(action: 'actualizar')}",
                data:data,
                success:function (msg) {
                    $("#dlgLoad").dialog("close");
                    var parts = msg.split("_");
                    var ok = parts[0];
                    var no = parts[1];
                    $(ok).css({
                        background:"#C5DDC5"
                    });
                    $(no).css({
                        background:"#DBC5C5"
                    });

                    console.log(msg);
                }
            });
        });


    });
</script>
</body>
</html>