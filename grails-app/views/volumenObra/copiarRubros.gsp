
<%@ page import="janus.Grupo" %>
<!doctype html>
<html>
<head>
    <meta name="layout" content="main">
    <title>
        Copiar Rubros
    </title>
    <script src="${resource(dir: 'js/jquery/plugins/jquery-validation-1.9.0', file: 'jquery.validate.min.js')}"></script>
    <script src="${resource(dir: 'js/jquery/plugins/jquery-validation-1.9.0', file: 'messages_es.js')}"></script>
    <script src="${resource(dir: 'js/jquery/plugins/', file: 'jquery.livequery.js')}"></script>
    <script src="${resource(dir: 'js/jquery/plugins/box/js', file: 'jquery.luz.box.js')}"></script>
    <link href="${resource(dir: 'js/jquery/plugins/box/css', file: 'jquery.luz.box.css')}" rel="stylesheet">
    <script src="${resource(dir: 'js/jquery/plugins/jQuery-contextMenu-gh-pages/src', file: 'jquery.ui.position.js')}" type="text/javascript"></script>
    <script src="${resource(dir: 'js/jquery/plugins/jQuery-contextMenu-gh-pages/src', file: 'jquery.contextMenu.js')}" type="text/javascript"></script>
    <link href="${resource(dir: 'js/jquery/plugins/jQuery-contextMenu-gh-pages/src', file: 'jquery.contextMenu.css')}" rel="stylesheet" type="text/css"/>
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

<div style="width: 99.7%;height: 600px;overflow-y: auto;float: right;" id="detalle"></div>

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
        if ($("#subPres_desc").val() * 1 > 0) {
            datos = "obra=${obra.id}&sub=" + $("#subPres_desc").val()
        } else {
            datos = "obra=${obra.id}"
        }
        $.ajax({type : "POST", url : "${g.createLink(controller: 'volumenObra',action:'tablaCopiarRubro')}",
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