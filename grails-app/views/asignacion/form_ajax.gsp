
<%@ page import="janus.pac.Asignacion" %>
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
    <style type="text/css">
    td{
        font-size: 10px !important;
    }
    th{
        font-size: 11px !important;
    }
    </style>
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


%{--<div class="span12 btn-group" role="navigation" style="margin-left: 0px;">--}%


%{--<a href="#" class="btn btn-ajax btn-new" id="calcular" title="Calcular precios">--}%
%{--<i class="icon-table"></i>--}%
%{--Calcular--}%
%{--</a>--}%

%{--</div>--}%
<div id="list-grupo" class="span12" role="main" style="margin-top: 10px;margin-left: 0px">

<div id="create-Asignacion" class="span" role="main">
    <g:form class="form-horizontal" name="frmSave-Asignacion" action="save">
        <g:hiddenField name="id" value="${asignacionInstance?.id}"/>
                
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Prespuesto
                </span>
            </div>

            <div class="controls">

                <input type="text" style="width: 190px;;font-size: 10px" id="item_presupuesto">
                <input type="hidden" style="width: 60px" id="item_prsp" name="prespuesto.id">
                <a href="#" class="btn btn-warning" title="Crear nueva partida" style="margin-top: -10px" id="item_agregar_prsp">
                    <i class="icon-edit"></i>
                </a>

            </div>
        </div>
                
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Anio
                </span>
            </div>

            <div class="controls">
                <g:select id="anio" name="anio.id" from="${janus.pac.Anio.list()}" optionKey="id" class="many-to-one  required" value="${asignacionInstance?.anio?.id}"/>
                <span class="mandatory">*</span>
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
                
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Valor
                </span>
            </div>

            <div class="controls">
                <g:field type="number" name="valor" class=" required" value="${fieldValue(bean: asignacionInstance, field: 'valor')}"/>
                <span class="mandatory">*</span>
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
                
    </g:form>

<div class="modal grande hide fade" id="modal-ccp" style="overflow: hidden;">
    <div class="modal-header btn-info">
        <button type="button" class="close" data-dismiss="modal">×</button>

        <h3 id="modalTitle"></h3>
    </div>

    <div class="modal-body" id="modalBody">
        <bsc:buscador name="pac.buscador.id" value="" accion="buscaCpac" controlador="pac" campos="${campos}" label="cpac" tipo="lista"/>
    </div>

    <div class="modal-footer" id="modalFooter">
    </div>
</div>
<div class="modal hide fade" id="modal-presupuesto">
    <div class="modal-header btn-warning">
        <button type="button" class="close" data-dismiss="modal">×</button>

        <h3 id="modalTitle-presupuesto"></h3>
    </div>

    <div class="modal-body" id="modalBody-presupuesto">
    </div>

    <div class="modal-footer" id="modalFooter-presupuesto">
    </div>
</div>

<script type="text/javascript">
    $("#frmSave-Asignacion").validate({
        errorPlacement : function (error, element) {
            element.parent().find(".help-block").html(error).show();
        },
        success        : function (label) {
            label.parent().hide();
        },
        errorClass     : "label label-important",
        submitHandler  : function(form) {
            $(".btn-success").replaceWith(spinner);
            form.submit();
        }
    });

    $("input").keyup(function (ev) {
        if (ev.keyCode == 13) {
            submitForm($(".btn-success"));
        }
    });
</script>
