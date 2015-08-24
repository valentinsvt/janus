<!doctype html>
<html>
<head>
    <meta name="layout" content="main">
    <title>
        Obras Contratadas
    </title>

</head>

<body>

<g:if test="${flash.message}">
    <div class="span12" style="height: 35px;margin-bottom: 10px;">
        <div class="alert ${flash.clase ?: 'alert-info'}" role="status">
            <a class="close" data-dismiss="alert" href="#">×</a>
            ${flash.message}
        </div>
    </div>
</g:if>

<div id="detalle"></div>


<a href="#" class="btn" id="regresar">
    <i class="icon-arrow-left"></i>
    Regresar
</a>


<script type="text/javascript">


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

    function cargarTabla() {
        var interval = loading("detalle")
        var datos = ""
        datos = "si=${"si"}&buscador=" + $("#buscador_con").val()
        $.ajax({type : "POST", url : "${g.createLink(controller: 'reportes4', action: 'tablaContratadas')}",
            data     : datos,
            success  : function (msg) {
                clearInterval(interval)
                $("#detalle").html(msg)
            }
        });
    }


    $(function () {

            cargarTabla();

    });



</script>
</body>
</html>

