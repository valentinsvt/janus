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

        <div class="span2 noMargin" align="center">Lista de Precios</div>

        <div class="span2 noMargin" align="center">Fecha</div>

        <div class="span1 noMargin" align="center">Todos</div>
        <div class="span1 noMargin" align="center">Ver</div>

    </div>

    <div class="span12 noMargin">

        <div class="span2 noMargin" align="center">
            <g:select class="listPrecio span2" name="listaPrecio"
                      from="${janus.Lugar.list([sort: 'descripcion'])}" optionKey="id"
                      optionValue="${{it.descripcion + ' (' + it.tipo + ')'}}"
                      noSelection="['-1': 'Seleccione']"
                      disabled="false"/>
        </div>

        <div class="span2 noMargin" align="center">
            <elm:datepicker name="fecha" class="fecha datepicker input-small" />
        </div>

        <div class="span1 noMargin" align="center">
            <g:checkBox name="todosPrecios" id="todos" checked="false"/>
        </div>

        <div class="span2 noMargin" align="center">
            <g:select name="tipo" from="${janus.Grupo.findAllByIdLessThanEquals(3)}" class="span2" optionKey="id" optionValue="descripcion" noSelection="['-1':'Todos']" />
        </div>

        <div class="btn-group span1">
            <a href="#" class="btn btn-consultar"><i class="icon-search"></i>Consultar</a>
            <a href="#" class="btn btn-generar"><i class="icon-edit"></i>Nuevos Precios</a>
            <a href="#" class="btn btn-cargar"><i class="icon-edit"></i> Precios a la Fecha</a>
            <a href="#" class="btn btn-actualizar"><i class="icon-refresh"></i>Actualizar</a>
        </div>
    </div>

</fieldset>


<fieldset class="borde">

    <div id="tablaPrecios">

    </div>

</fieldset>


<script type="text/javascript">


    function consultar() {
        var lgar = $("#listaPrecio").val();
        var fcha = $("#fecha").val();
//            console.log("fcha" + fcha)
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
            }
        });
    }

    $(function () {
        $("#todos").click(function () {
            var fecha2 = new Date().toString("dd-MM-yyyy");
//            console.log(fecha2);
            if ($("#todos").attr("checked") == "checked") {
//                $("#listaPrecio").attr("disabled", true);
//                $("#listaPrecio").attr("value", -1);
                $("#fecha").attr("value", fecha2);
            }
            else {
//                $("#listaPrecio").attr("disabled", false)
            }
        });

        $(".btn-consultar").click(function () {
            consultar();
        });
    });
</script>
</body>
</html>