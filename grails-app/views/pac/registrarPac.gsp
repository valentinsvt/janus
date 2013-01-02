<%@ page import="janus.Grupo" %>
<!doctype html>
<html>
<head>
    <meta name="layout" content="main">
    <title>
        P.A.C.
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


<div class="span12 btn-group" role="navigation" style="margin-left: 0px;">


    <a href="#" class="btn btn-ajax btn-new" id="calcular" title="Calcular precios">
        <i class="icon-table"></i>
        Calcular
    </a>

</div>
<div id="list-grupo" class="span12" role="main" style="margin-top: 10px;margin-left: 0px">
    <div class="borde_abajo" style="padding-left: 45px;position: relative;">
        <p class="css-vertical-text">P.A.C.</p>
        <div class="linea" style="height: 98%;"></div>
        <div class="row-fluid" style="margin-left: 0px">
            <div class="span3">
                <b>Departamento:</b>
                <g:select name="presupuesto.id" from="${janus.Departamento.list([order:'descripcion'])}" optionKey="id" optionValue="descripcion" style="width: 250px;;font-size: 10px" id="item_depto"></g:select>
            </div>
            <div class="span1" >
                <b>Año:</b>
                <g:select name="anio" from="${janus.pac.Anio.list()}" id="item_anio" optionValue="anio" optionKey="id" style="width: 80px;font-size: 10px"></g:select>
            </div>
        </div>
        <div class="row-fluid" style="margin-left: 0px">
            <div class="span3">
                <b>Partida presupuestaria:</b>
                <g:select name="presupuesto.id" from="${janus.Presupuesto.list([order:'descripcion'])}" optionKey="id" optionValue="descripcion" style="width: 250px;;font-size: 10px" id="item_presupuesto"></g:select>
            </div>
            <div class="span1" >
                <b>Memo:</b>
                <input type="text" style="width: 60px;text-align: right" id="item_memo" >
            </div>
            <div class="span3">
                <b>Programa:</b>
                <g:select name="programa.id" from="${janus.pac.ProgramaPresupuestario.list([order:'descripcion'])}" optionKey="id" optionValue="descripcion" style="width: 250px;;font-size: 10px" id="item_programa"></g:select>
            </div>
            <div class="span2" >
                <b>Código C.P.:</b>
                <input type="text" style="width: 110px;;font-size: 10px" id="item_codigo">
                <input type="hidden" style="width: 60px" id="item_id">
            </div>
            <div class="span3">
                <b>Tipo compra:</b>
                <g:select name="tipo" from="${janus.pac.TipoCompra.list()}" optionKey="id" optionValue="descripcion" style="width: 250px;;font-size: 10px" id="item_tipo"></g:select>
            </div>
        </div>
        <div class="row-fluid" style="margin-left: 0px">

            <div class="span4" style="">
                <b>Descripción:</b>
                <input type="text" style="width: 330px;font-size: 10px" id="item_desc">

            </div>
            <div class="span1" >
                <b>Cantidad:</b>
                <input type="text" style="width: 60px;text-align: right" id="item_cantidad" value="1">
            </div>
            <div class="span2" >
                <b>Costo unitario:</b>
                <input type="text" style="width: 140px;text-align: right" id="item_precio" value="1">
            </div>
            <div class="span2" >
                <b>Unidad:</b>
                <g:select name="unidad.id" from="${janus.pac.UnidadIncop.list()}" id="item_unidad"  optionKey="id" optionValue="codigo" style="width: 123px;font-size: 10px"></g:select>
            </div>
            <div class="span2">
                <b>Cuatrimestre:</b>
                <div class="btn-group" data-toggle="buttons-checkbox">
                    <button type="button" id="item_c1" class="btn btn-info " style="font-size: 10px">C.1</button>
                    <button type="button" id="item_c2" class="btn btn-info " style="font-size: 10px">C.2</button>
                    <button type="button" id="item_c3" class="btn btn-info " style="font-size: 10px">C.3</button>
                </div>
            </div>
            <div class="span1" style="margin-left: 10px;padding-top:30px">
                <input type="hidden" value="" id="vol_id">
                <a href="#" class="btn btn-primary" title="agregar" style="margin-top: -10px" id="item_agregar">
                    <i class="icon-plus"></i>
                </a>
            </div>
        </div>
    </div>
    <div class="borde_abajo" style="position: relative;float: left;width: 95%;padding-left: 45px">
        <p class="css-vertical-text">Detalle</p>
        <div class="linea" style="height: 98%;"></div>

        <div style="width: 99.7%;height: 550px;overflow-y: auto;float: right;" id="detalle"></div>
        <div style="width: 99.7%;height: 30px;overflow-y: auto;float: right;text-align: right" id="total">
            <b>TOTAL:</b> <div id="divTotal" style="width: 150px;float: right;height: 30px;font-weight: bold;font-size: 12px;margin-right: 20px"></div>
        </div>
    </div>
</div>

<div class="modal grande hide fade" id="modal-ccp" style="overflow: hidden;">
    <div class="modal-header btn-info">
        <button type="button" class="close" data-dismiss="modal">×</button>

        <h3 id="modalTitle"></h3>
    </div>

    <div class="modal-body" id="modalBody">
        %{--<bsc:buscador name="rubro.buscador.id" value="" accion="buscaRubro" controlador="volumenObra" campos="${campos}" label="Rubro" tipo="lista"/>--}%
    </div>

    <div class="modal-footer" id="modalFooter">
    </div>
</div>
<script type="text/javascript">
    $("#item_codigo").dblclick(function () {
        var btnOk = $('<a href="#" data-dismiss="modal" class="btn">Cerrar</a>');
        $("#modalTitle").html("Código compras públicas");
        $("#modalFooter").html("").append(btnOk);
        $("#modal-ccp").modal("show");
        $("#buscarDialog").unbind("click")
        $("#buscarDialog").bind("click", enviar)

    });
</script>
</body>
</html>