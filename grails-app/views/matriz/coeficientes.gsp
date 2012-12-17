<%@ page import="janus.Grupo" %>
<!doctype html>
<html>
<head>
    <meta name="layout" content="main">
    <title>
        Volumenes de obra
    </title>
    <script src="${resource(dir: 'js/jquery/plugins/jquery-validation-1.9.0', file: 'jquery.validate.min.js')}"></script>
    <script src="${resource(dir: 'js/jquery/plugins/jquery-validation-1.9.0', file: 'messages_es.js')}"></script>
    <script src="${resource(dir: 'js/jquery/plugins/', file: 'jquery.livequery.js')}"></script>
    <script src="${resource(dir: 'js/jquery/plugins/box/js', file: 'jquery.luz.box.js')}"></script>
    <link href="${resource(dir: 'js/jquery/plugins/box/css', file: 'jquery.luz.box.css')}" rel="stylesheet">
    <script src="${resource(dir: 'js/jquery/plugins/jQuery-contextMenu-gh-pages/src',file: 'jquery.ui.position.js')}" type="text/javascript"></script>
    <script src="${resource(dir: 'js/jquery/plugins/jQuery-contextMenu-gh-pages/src',file: 'jquery.contextMenu.js')}" type="text/javascript"></script>
    <link href="${resource(dir: 'js/jquery/plugins/jQuery-contextMenu-gh-pages/src',file: 'jquery.contextMenu.css')}" rel="stylesheet" type="text/css" />
</head>
<body>
<div class="span12">
    <g:if test="${flash.message}">
        <div class="alert ${flash.clase ?: 'alert-info'}" role="status">
            <a class="close" data-dismiss="alert" href="#">×</a>
            ${flash.message}
        </div>
    </g:if>
</div>
<div class="tituloTree">
    Coeficientes de la fórmula polinómica de la obra: ${obra.descripcion + " ("+obra.codigo+")"}
</div>

<div class="span12 btn-group" role="navigation" style="margin-left: 0px;">

    <a href="${g.createLink(controller: 'obra',action: 'registroObra',params: [obra:obra?.id])}" class="btn btn-ajax btn-new" id="atras" title="Regresar a la obra">
        <i class="icon-arrow-left"></i>
        Regresar
    </a>
</div>
<div id="list-grupo" class="span12" role="main" style="margin-top: 10px;margin-left: 0px">

    <div style="width: 400px;height: 600px;float: left">
        <table class="table table-bordered table-striped table-condensed table-hover">
            <thead>
            <tr>
                <th>Coef</th>
                <th>Nombre del indice</th>
                <th>Valor</th>
            </tr>
            </thead>
            <tbody>
            <g:each in="${fp}" var="f">
                <g:if test="${f.numero =~ 'p'}">
                    <tr>
                        <td>${f.numero}</td>
                        <td>${f.indice.descripcion}</td>
                        <td>${f.valor}</td>
                    </tr>
                </g:if>
            </g:each>
            </tbody>
        </table>

    </div>
    <div style="width: 400px;height: 600px;float: left">
        <table class="table table-bordered table-striped table-condensed table-hover">
            <thead>
            <tr>
                <th>Coef</th>
                <th>Nombre del indice</th>
                <th>Valor</th>
            </tr>
            </thead>
            <tbody>
            <g:each in="${fp}" var="f">
                <g:if test="${f.numero =~ 'c'}">
                    <tr>
                        <td>${f.numero}</td>
                        <td>${f.indice.descripcion}</td>
                        <td>${f.valor}</td>
                    </tr>
                </g:if>
            </g:each>
            </tbody>
        </table>
    </div>

</div>

<div class="modal grande hide fade" id="modal-rubro" style="overflow: hidden;">
    <div class="modal-header btn-info">
        <button type="button" class="close" data-dismiss="modal">×</button>

        <h3 id="modalTitle"></h3>
    </div>

    <div class="modal-body" id="modalBody">
        <bsc:buscador name="rubro.buscador.id" value="" accion="buscaRubro" controlador="volumenObra" campos="${campos}" label="Rubro" tipo="lista"/>
    </div>

    <div class="modal-footer" id="modalFooter">
    </div>
</div>

</body>
</html>