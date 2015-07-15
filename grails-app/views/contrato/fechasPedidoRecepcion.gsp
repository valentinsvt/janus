<%--
  Created by IntelliJ IDEA.
  User: luz
  Date: 6/25/13
  Time: 1:32 PM
  To change this template use File | Settings | File Templates.
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<html>
    <head>

        <meta name="layout" content="main">
        <script src="${resource(dir: 'js/jquery/plugins/jquery-validation-1.9.0', file: 'jquery.validate.min.js')}"></script>
        <script src="${resource(dir: 'js/jquery/plugins/jquery-validation-1.9.0', file: 'messages_es.js')}"></script>

        <script src="${resource(dir: 'js/jquery/plugins/', file: 'jquery.livequery.js')}"></script>
        <script src="${resource(dir: 'js/jquery/plugins/box/js', file: 'jquery.luz.box.js')}"></script>
        <link href="${resource(dir: 'js/jquery/plugins/box/css', file: 'jquery.luz.box.css')}" rel="stylesheet">
        <title>Fechas de pedido de recepción</title>
    </head>

    <body>
        <g:if test="${flash.message}">
            <div class="row">
                <div class="span12">
                    <div class="alert ${flash.clase ?: 'alert-info'}" role="status">
                        <a class="close" data-dismiss="alert" href="#">×</a>
                        ${flash.message}
                    </div>
                </div>
            </div>
        </g:if>

        <div class="row" style="margin-bottom: 10px;">
            <div class="span9 btn-group" role="navigation">
                <g:link controller="contrato" action="verContrato" params="[id: contrato?.id]" class="btn btn-ajax btn-new" title="Regresar al contrato">
                    <i class="icon-double-angle-left"></i>
                    Contrato
                </g:link>
                <g:link controller="planilla" action="listFiscalizador" id="${contrato?.id}" class="btn" title="Planillas">
                    <i class="icon-arrow-left"></i>
                    Planillas
                </g:link>
                <a href="#" class="btn btn-success" id="btnSave"><i class="icon-save"></i> Guardar</a>
            </div>
        </div>

        <div class="tituloChevere" style="margin-bottom: 10px;">Fecha de pedido de recepción</div>

        <div id="create-Contrato" class="span" role="main">
            <g:form class="form-horizontal" name="frmSave" action="saveFechas" id="${contrato.id}">
                <div class="control-group">
                    <div>
                        <span class="control-label label label-inverse">
                            del contratista
                        </span>
                    </div>

                    <div class="controls">
                        <elm:datepicker name="fechaPedidoRecepcionContratista" value="${contrato.fechaPedidoRecepcionContratista}"/>
                        <span class="mandatory">*</span>

                        <p class="help-block ui-helper-hidden"></p>
                    </div>
                </div>

                <div class="control-group">
                    <div>
                        <span class="control-label label label-inverse">
                            del fiscalizador
                        </span>
                    </div>

                    <div class="controls">
                        <elm:datepicker name="fechaPedidoRecepcionFiscalizador" value="${contrato.fechaPedidoRecepcionFiscalizador}"/>
                        <span class="mandatory">*</span>

                        <p class="help-block ui-helper-hidden"></p>
                    </div>
                </div>
            </g:form>
        </div>

        <script type="text/javascript">
            $(function () {
                $("#btnSave").click(function () {
                    $(this).replaceWith(spinner);
                    $("#frmSave").submit();
                });
            });
        </script>

    </body>
</html>