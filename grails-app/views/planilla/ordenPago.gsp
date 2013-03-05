<%--
  Created by IntelliJ IDEA.
  User: luz
  Date: 2/14/13
  Time: 11:31 AM
  To change this template use File | Settings | File Templates.
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <meta name="layout" content="main">
    <title>Pagar planilla</title>
    <script src="${resource(dir: 'js/jquery/plugins/jquery-validation-1.9.0', file: 'jquery.validate.min.js')}"></script>
    <script src="${resource(dir: 'js/jquery/plugins/jquery-validation-1.9.0', file: 'messages_es.js')}"></script>

    <style type="text/css">
    .row {
        margin-bottom : 10px;
    }

    .lbl {
        font-weight : bold;;
    }
    </style>

</head>

<body>
<%@ page import="janus.ejecucion.Planilla" %>

<div class="row">
    <div class="span9 btn-group" role="navigation">
        <g:link controller="contrato" action="registroContrato" params="[contrato: planillaInstance.contrato?.id]" class="btn btn-ajax btn-new" title="Regresar al contrato">
            <i class="icon-double-angle-left"></i>
            Contrato
        </g:link>
        <g:link controller="planilla" action="list" params="[id: planillaInstance.contrato?.id]" class="btn btn-ajax btn-new" title="Regresar a las planillas del contrato">
            <i class="icon-angle-left"></i>
            Planillas
        </g:link>

        <g:if test="${planillaInstance?.fechaOrdenPago == null}">

            <a href="#" id="btnPagar" class="btn btn-success" rel="tooltip" title="Pagar planilla">
                <i class="icon-money"></i>
                Ordenar pago
            </a>

        </g:if>
        <g:else>

        </g:else>


        <a href="#" id="btnPdf" class="btn" title="Imprimir PDF"><i class="icon-print"></i>
            PDF
        </a>


        %{--<a href="#" id="btntablas" class="btn" title="Imprimir Tablas"><i class="icon-print"> </i>--}%
        %{--Tablas--}%
        %{--</a>--}%

    </div>

    <div class="span3" id="busqueda-Planilla"></div>
</div>

<elm:headerPlanilla planilla="${planillaInstance}"/>

<div class="pago">

    <fieldset>

        <g:if test="${planillaInstance.tipoPlanilla.codigo == 'A'}">

            <div class="span12">

                <div class="span3" style="font-weight: bold">

                    ${planillaInstance.contrato?.porcentajeAnticipo} % de Anticipo

                </div>

                <div class="span3">

                    <elm:numero number="${planillaInstance?.valor}"/>

                </div>

            </div>

            <div class="span12" style="margin-top: 10px">

                <div class="span3" style="font-weight: bold">

                    (+) Reajuste provisional del anticipo

                </div>

                <div class="span3">

                    <elm:numero number="${planillaInstance?.reajuste}"/>

                </div>

            </div>

            <div class="span12" style="margin-top: 10px">

                <div class="span3" style="font-weight: bold">

                    SUMA:

                </div>

                <div class="span3">

                    <elm:numero number="${planillaInstance?.valor + planillaInstance?.reajuste}"/>

                </div>

            </div>

            <div class="span12" style="margin-top: 10px; margin-bottom: 20px">

                <div class="span3" style="font-weight: bold">

                    A FAVOR DEL CONTRATISTA:

                </div>

                <div class="span3">

                    <elm:numero number="${planillaInstance?.valor + planillaInstance?.reajuste}"/>

                </div>

            </div>

        </g:if>




        <g:else>
            <div class="span12">

                <div class="span3" style="font-weight: bold">

                    Valor Planilla

                </div>

                <div class="span3">

                    <elm:numero number="${planillaInstance?.valor}"/>

                </div>

            </div>

            <div class="span12" style="margin-top: 10px">

                <div class="span3" style="font-weight: bold">

                    (+) Reajuste provisional del anticipo

                </div>

                <div class="span3">

                    <elm:numero number="${planillaInstance?.reajuste}"/>

                </div>

            </div>

            <div class="span12" style="margin-top: 10px">

                <div class="span3" style="font-weight: bold">

                    SUMA:

                </div>

                <div class="span3">

                    <elm:numero number="${planillaInstance?.valor + planillaInstance?.reajuste}"/>

                </div>

            </div>

            <div class="span12" style="margin-top: 10px; margin-bottom: 20px">

                <div class="span3" style="font-weight: bold">

                    A FAVOR DEL CONTRATISTA:

                </div>

                <div class="span3">

                    <elm:numero number="${planillaInstance?.valor + planillaInstance?.reajuste}"/>

                </div>

            </div>
        </g:else>

    </fieldset>

</div>

<g:if test="${planillaInstance?.fechaPago != null}">

    <div class="span12" style="margin-top: 10px; margin-bottom: 20px">
        <div class="span3" style=" font-weight: bold">
            Fecha orden de pago
        </div>

        <div class="span3"><g:formatDate date="${planillaInstance?.fechaOrdenPago}" format="dd-MM-yyyy"/>

        </div>
    </div>


</g:if>

<g:else>
    <g:form class="form-horizontal" name="frmSave-Planilla" action="saveOrdenPago">
        <g:hiddenField name="id" value="${planillaInstance?.id}"/>

        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Fecha orden de pago
                </span>
            </div>

            <div class="controls">
                <elm:datepicker name="fechaOrdenPago" class="required" value=""  onSelect="igual"/>

                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>

    </g:form>
</g:else>

<script type="text/javascript">

    $("#frmSave-Planilla").validate({
    });

    $("#btnPagar").click(function () {
        $(this).replaceWith(spinner);
        $("#frmSave-Planilla").submit();
        return false;
    });

    function igual() {
        $("#fechaInicioObra").val($("#fechaPago").val());
    }

    $(".datepicker").keydown(function () {
        return false;
    });

    %{--$("#btnPdf").click(function () {--}%

    %{--location.href="${createLink(controller: 'reportes', action: 'anticipoReporte', id: planillaInstance?.id)}"--}%
    %{--});--}%

    $("#btnPdf").click(function () {
        var actionUrl = "${createLink(controller:'pdf',action:'pdfLink')}?filename=planilla.pdf&url=${createLink(controller: 'reportes', action: 'anticipoReporte')}";
        location.href = actionUrl + "?id=${planillaInstance?.id}";

        var wait = $("<div style='text-align: center;'> Estamos procesando su reporte......Por favor espere......</div>");
        wait.prepend(spinnerBg);

        var btnClose = $('<a href="#" data-dismiss="modal" class="btn">Cerrar</a>');
        $("#modalHeader").removeClass("btn-edit btn-show btn-delete");
        $("#modalTitle").html("Procesando");
        $("#modalBody").html(wait);
        $("#modalFooter").html("").append(btnClose);
    });



</script>

</body>
</html>