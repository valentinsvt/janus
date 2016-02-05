<%@ page import="janus.Grupo" %>
<!doctype html>
<html>
<head>
    <meta name="layout" content="main">
    <title>
        Importar Obra
    </title>
    <script src="${resource(dir: 'js/jquery/plugins/jquery-validation-1.9.0', file: 'jquery.validate.min.js')}"></script>
    <script src="${resource(dir: 'js/jquery/plugins/jquery-validation-1.9.0', file: 'messages_es.js')}"></script>
    <script src="${resource(dir: 'js/jquery/plugins/', file: 'jquery.livequery.js')}"></script>
    <script src="${resource(dir: 'js/jquery/plugins/box/js', file: 'jquery.luz.box.js')}"></script>
    <link href="${resource(dir: 'js/jquery/plugins/box/css', file: 'jquery.luz.box.css')}" rel="stylesheet">
    <script src="${resource(dir: 'js/jquery/plugins/jQuery-contextMenu-gh-pages/src', file: 'jquery.ui.position.js')}" type="text/javascript"></script>
</head>

<body>
<div class="span12" id="mensaje">
    <g:if test="${flash.message}">
        <div class="alert ${flash.clase ?: 'alert-info'}" role="status">
            <a class="close" data-dismiss="alert" href="#">×</a>
            ${flash.message}
        </div>
    </g:if>
</div>

<div class="tituloTree">
    Obras presentes en el sistema de oferentes
</div>

%{--
<div class="row">
    <div class="span12 btn-group" role="navigation" >
        <a href="${g.createLink(controller: 'obra', action: 'registroObra', params: [obra: obra?.id])}" class="btn btn-ajax btn-new" id="atras" title="Regresar a la obra">
            <i class="icon-arrow-left"></i>
            Regresar
        </a>
    </div>
</div>
--}%


<div id="list-grupo" class="span12" role="main" style="margin-top: 10px;margin-left: 0px">
    <div class="borde_abajo" style="padding-left: 45px;position: relative;">
        <h4>Filtrar Obras por:</h4>
        <div class="linea" style="height: 98%;"></div>
        <div class="row-fluid" style="margin-left: 0px">
            <div class="span1">
                <b>Registradas:</b>
            </div>
            <div class="span1" style="width: 40px;">
                <input type="checkbox" id="reg" value="R" ${(tipo=="R")?"checked":""}>
            </div>
            <div class="span1">
                <b>Oferente:</b>
            </div>
            <div class="span2">
                <input type="text" id="oferente" value="${oferente}" style="width: 95%">
            </div>
            <div class="span2" style="width: 100px">
                <a href="#" class="btn btn-ajax btn-new" id="buscar" title="Buscar">
                    <i class="icon-search"></i>
                    Buscar
                </a>
            </div>
            <div class="span2">
                <a href="#" class="btn btn-ajax btn-new" id="limpiar" title="Limpiar">
                    <i class="icon-refresh"></i>
                    Limpiar
                </a>
            </div>

        </div>

    </div>

    <div class="borde_abajo" style="position: relative;float: left;width: 95%;padding-left: 45px">

        <table class="table table-bordered table-striped table-condensed table-hover">
            <thead>
            <tr>
                <th>CODIGO</th>
                <th>NOMBRE</th>
                <th>OFERENTE</th>
                <th>ESTADO</th>
                <th></th>
            </tr>
            </thead>
            <tbody>
            <g:each in="${result}" var="r" status="i">
                <tr>
                    <td>
                        ${r["obracdgo"]}
                    </td>
                    <td>

                        ${r["obranmbr"]}
                    </td>
                    <td>
                        ${r[2]}
                    </td>
                    <td style="text-align: center;font-weight: bold">
                        ${r["obraetdo"]}
                    </td>
                    <td>
                        <g:if test="${r['obraetdo']=='R'}">
                            <a href="#" class="btn btn-ajax btn-new copiar" oferente="${r['prsnjnid']}" obra="${r['obrajnid']}"  obraOf="${r['obra__id']}" title="copiar">
                                <i class="icon-copy"></i>
                                Copiar
                            </a>
                        </g:if>
                    </td>
                </tr>
            </g:each>
            </tbody>
        </table>

    </div>
</div>

<script type="text/javascript">
    $("#buscar").click(function(){
        var tipo = ""
        if($("#reg").is(':checked')){
            tipo="&tipo=r"
        }
        location.href="${g.createLink(action: 'obras')}?oferente="+$("#oferente").val()+tipo
    })
    $("#limpiar").click(function(){
        $("#reg").removeAttr("checked")
        $("#oferente").val("")
    })
    $(".copiar").click(function(){
        var obra= $(this).attr("obra")
        var oferente =$(this).attr("oferente")
        var obraOf =$(this).attr("obraOf")

        if(confirm("Esta seguro?")){
            $("#dlgLoad").dialog("open")
            $.ajax({type : "POST", url : "${g.createLink(controller: 'oferentes',action:'copiarObra')}",
                data     : "obra="+obra+"&oferente="+oferente+"&obraOf="+obraOf,
                success  : function (msg) {
                    $("#dlgLoad").dialog("close")
                    var partes = msg.split('#');
                     if(partes[0] !="error"){
                                location.href="${g.createLink(controller: 'obra',action: 'registroObra')}?obra="+msg
                     }else{
                         $.box({
                             imageClass : "box_info",
                             text       : "Error al copiar la obra: \n" + partes[1],
                             title      : "Alerta",
                             iconClose  : false,
                             dialog     : {
                                 resizable : false,
                                 draggable : false,
                                 buttons   : {
                                     "Aceptar" : function () {
                                         window.location.reload(true)
                                     }
                                 }
                             }
                         });
                     }
                }
            });
        }
    })
</script>

</body>
</html>