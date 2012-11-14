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

    <div class="span12">

        <div class="span2" align="center">Lista de Precios</div>

        <div class="span1" style="margin-left: 130px" align="center">Fecha</div>

        <div class="span1" align="center" style="margin-left: 40px">Todos</div>

    </div>

    <div class="span12">

        <div class="span2" align="center"><g:select class="listPrecio" name="listaPrecio"
                                                    from="${janus.Lugar.list([sort: "id"])}"
                                                    noSelection="['-1': 'Seleccione']" optionKey="id"
                                                    optionValue="descripcion" style="width: 270px"
                                                    disabled="false"/></div>

        <div class="span1" align="center" style="margin-left: 130px"><elm:datepicker name="fecha"
                                                                                     class="fecha datepicker"
                                                                                     style="width: 90px"/></div>

        <div class="span1" align="center" style="margin-left: 40px"><g:checkBox name="todosPrecios" id="todos"
                                                                                checked="false"/></div>

        <div class="btn-group span1">
            <a href="#" class="btn btn-consultar"><i class="icon-search"></i>Consultar</a>
            <a href="#" class="btn btn-generar"><i class="icon-edit"></i>Generar Nuevos Precios</a>
            <a href="#" class="btn btn-cargar"><i class="icon-edit"></i> Cargar Precios a la Fecha</a>
            <a href="#" class="btn btn-actualizar"><i class="icon-refresh"></i>Actualizar</a>
        </div>
    </div>

</fieldset>


<fieldset class="borde">

    <div id="tablaPrecios">

    </div>

</fieldset>


<script type="text/javascript">

    $(function () {


        $("#todos").click(function () {


            var fecha2 = new Date().toString("dd-MM-yyyy");


            console.log(fecha2);



            if ($("#todos").attr("checked") == "checked") {

                $("#listaPrecio").attr("disabled", true);
                $("#listaPrecio").attr("value", -1);
                $("#fecha").attr("value", fecha2);


            }
            else {


                $("#listaPrecio").attr("disabled", false)
            }


        })


    });


    $(function () {

        $(".btn-consultar").click(function () {


            consultar();

        });


        function consultar() {

            var lgar = $("#listaPrecio").val();

            var fcha = $("#fecha").val();


            console.log("fcha" + fcha)

            var todos = "";

            if ($("#todos").attr("checked") == "checked") {

                todos = 1
            }

            else {

                todos = 2
            }

            $.ajax({
                type:"POST",
                url:"${createLink(action:'tabla')}",
                data:{
                    lgar:lgar,
                    fecha:fcha,
                    todos:todos,
                    max:10,
                    pag:1
                },
                success:function (msg) {
                    $("#tablaPrecios").html(msg);
                }
            });

        }

    });

</script>
</body>
</html>