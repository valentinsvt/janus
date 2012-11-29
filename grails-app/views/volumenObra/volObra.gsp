<%@ page import="janus.Grupo" %>
<!doctype html>
<html>
<head>
    <meta name="layout" content="main">
    <title>
        Rubros
    </title>
    <script src="${resource(dir: 'js/jquery/plugins/jquery-validation-1.9.0', file: 'jquery.validate.min.js')}"></script>
    <script src="${resource(dir: 'js/jquery/plugins/jquery-validation-1.9.0', file: 'messages_es.js')}"></script>
    <script src="${resource(dir: 'js/jquery/plugins/', file: 'jquery.livequery.js')}"></script>
    <script src="${resource(dir: 'js/jquery/plugins/box/js', file: 'jquery.luz.box.js')}"></script>
    <link href="${resource(dir: 'js/jquery/plugins/box/css', file: 'jquery.luz.box.css')}" rel="stylesheet">
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
<div class="span12 btn-group" role="navigation" style="margin-left: 0px;">
    <a href="#" class="btn  " id="btn_lista">
        <i class="icon-file"></i>
        Lista
    </a>
    <a href="${g.createLink(action: 'rubroPrincipal')}" class="btn btn-ajax btn-new">
        <i class="icon-file"></i>
        Nuevo
    </a>
    <a href="#" class="btn btn-ajax btn-new" id="guardar">
        <i class="icon-file"></i>
        Guardar
    </a>
    <a href="${g.createLink(action: 'rubroPrincipal')}" class="btn btn-ajax btn-new">
        <i class="icon-file"></i>
        Cancelar
    </a>
    <a href="#" class="btn btn-ajax btn-new">
        <i class="icon-file"></i>
        Borrar
    </a>
    <a href="#" class="btn btn-ajax btn-new" id="calcular" title="Calcular precios">
        <i class="icon-table"></i>
        Calcular
    </a>
    <a href="#" class="btn btn-ajax btn-new" id="transporte" title="Transporte">
        <i class="icon-truck"></i>
        Transporte
    </a>
    <a href="#" class="btn btn-ajax btn-new" id="imprimir" title="Imprimir">
        <i class="icon-print"></i>
        Imprimir
    </a>
</div>
<div id="list-grupo" class="span12" role="main" style="margin-top: 10px;margin-left: 0px">
    <div class="borde_abajo" style="padding-left: 45px;">
        <div class="row-fluid" style="margin-left: 0px">
            <div class="span3">
                <b>Memo:</b> ${obra?.memoCantidadObra}
            </div>
            <div class="span3">
                <b>Ubicación:</b> ${obra?.parroquia}
            </div>
            <div class="span2" >
                <b style="">Dist. peso:</b> ${obra?.distanciaPeso}
            </div>
            <div class="span2" >
                <b>Dist. volúmen:</b> ${obra?.distanciaVolumen}
            </div>
        </div>
        <div class="row-fluid" style="margin-left: 0px">
            <div class="span5">
                <b>Subpresupuesto:</b>
                <g:select name="subpresupuesto" from="${janus.SubPresupuesto.list([order:'descripcion'])}" optionKey="id" optionValue="descripcion" style="width: 300px;" id="subPres"></g:select>
            </div>
            <div class="span2" style="margin-left: 5px;">
                <b>Código</b>
                <input type="text" style="width: 80px" id="item_codigo">
            </div>
            <div class="span4" style="margin-left: 5px;">
                <b>Rubro</b>
                <input type="text" style="">
                <a href="#" class="btn btn-primary" title="agregar" style="margin-top: -10px" id="item_agregar">
                    <i class="icon-plus"></i>
                </a>
            </div>
            <div class="span2" style="margin-left: 5px;">
                <b>Cantidad</b>
                <input type="text" style="width: 80px;text-align: right" id="item_cantidad">
            </div>
            <div class="span1" style="margin-left: 5px;">
                <b>Orden</b>
                <input type="text" style="width: 30px;text-align: right" id="item_orden">
            </div>
        </div>
    </div>
    <div class="borde_abajo" style="position: relative;float: left;width: 95%;padding-left: 45px">
        <p class="css-vertical-text">Composición</p>
        <div class="linea" style="height: 98%;"></div>
        <div class="row-fluid" style="margin-left: 0px">
            <div class="span5">
                <b>Subpresupuesto:</b>
                <g:select name="subpresupuesto" from="${subPres}" optionKey="id" optionValue="descripcion" style="width: 300px;" id="subPres_desc"></g:select>
            </div>
            <div class="span1">
                <div class="btn-group" data-toggle="buttons-checkbox">
                    <button type="button" id="ver_todos" class="btn btn-info" style="font-size: 10px">Ver todos</button>
                </div>
            </div>

        </div>
        <div style="width: 97%;height: 600px;overflow-y: auto;float: right;"></div>
    </div>
</div>
</body>
</html>