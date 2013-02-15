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
                <g:link controller="planilla" action="list" params="[id: planillaInstance.contrato?.id]" class="btn btn-ajax btn-new" rel="tooltip" title="Regresar a las planillas">
                    <i class="icon-arrow-left"></i>
                    Regresar
                </g:link>
                <a href="#" id="btnPagar" class="btn btn-success" rel="tooltip" title="Pagar planilla">
                    <i class="icon-money"></i>
                    Pagar
                </a>
            </div>

            <div class="span3" id="busqueda-Planilla"></div>
        </div>

        <div class="well">
            <h4>Planilla a pagar</h4>

            <div class="row">
                <div class="span4 lbl">Planilla número ${planillaInstance.numero} de ${planillaInstance.tipoPlanilla?.nombre}</div>

                <div class="span6">${planillaInstance.descripcion}</div>
            </div>

            <div class="row">
                <div class="span2 lbl">Fecha de presentación</div>

                <div class="span1"><g:formatDate date="${planillaInstance?.fechaPresentacion}" format="dd-MM-yyyy"/></div>

                <div class="span2 lbl">Fecha de ingreso</div>

                <div class="span1"><g:formatDate date="${planillaInstance?.fechaIngreso}" format="dd-MM-yyyy"/></div>
                <g:if test="${planillaInstance?.fechaPago}">
                    <div class="span2 lbl">Fecha pago</div>

                    <div class="span1"><g:formatDate date="${planillaInstance?.fechaPago}" format="dd-MM-yyyy"/></div>
                </g:if>
            </div>


            <div class="row ${planillaInstance.numeroFactura || planillaInstance.estadoPlanilla || planillaInstance.periodoIndices || planillaInstance.fechaInicio || planillaInstance.fechaFin ? '' : 'hide'}">
                <g:if test="${planillaInstance?.numeroFactura}">
                    <div class="span1 lbl">N. factura</div>

                    <div class="span2"><g:fieldValue bean="${planillaInstance}" field="numeroFactura"/></div>
                </g:if>
                <g:if test="${planillaInstance?.estadoPlanilla}">
                    <div class="span1 lbl">Estado</div>

                    <div class="span1">${planillaInstance?.estadoPlanilla?.nombre}</div>
                </g:if>
                <g:if test="${planillaInstance?.periodoIndices}">
                    <div class="span1 lbl">Periodo</div>

                    <div class="span1">${planillaInstance?.periodoIndices?.descripcion}</div>
                </g:if>
                <g:if test="${planillaInstance?.fechaInicio || planillaInstance?.fechaFin}">
                    <div class="span4">
                        <g:formatDate date="${planillaInstance?.fechaInicio}" format="dd-MM-yyyy"/>
                        al
                        <g:formatDate date="${planillaInstance?.fechaFin}" format="dd-MM-yyyy"/>
                    </div>
                </g:if>
            </div>

            <div class="row ${planillaInstance.valor || planillaInstance.descuentos ? '' : 'hide'}">
                <g:if test="${planillaInstance?.valor}">
                    <div class="span1 lbl">Valor</div>

                    <div class="span1">
                        <g:formatNumber number="${planillaInstance.valor}" maxFractionDigits="2" minFractionDigits="2" format="##,##0" locale="ec"/>
                    </div>
                </g:if>
                <g:if test="${planillaInstance?.descuentos}">
                    <div class="span1 lbl">Descuentos</div>

                    <div class="span1">
                        <g:formatNumber number="${planillaInstance.descuentos}" maxFractionDigits="2" minFractionDigits="2" format="##,##0" locale="ec"/>
                    </div>
                </g:if>

            </div>
            <g:if test="${planillaInstance.observaciones}">
                <div class="row">
                    <div class="span1 lbl">Observaciones</div>

                    <div class="span10"><g:fieldValue bean="${planillaInstance}" field="observaciones"/></div>
                </div>
            </g:if>
        </div> <!-- well -->

        <g:form class="form-horizontal" name="frmSave-Planilla" action="savePago">
            <g:hiddenField name="id" value="${planillaInstance?.id}"/>

            <div class="control-group">
                <div>
                    <span class="control-label label label-inverse">
                        Fecha de pago
                    </span>
                </div>

                <div class="controls">
                    <elm:datepicker name="fechaPago" class="required" value="" maxDate="new Date()" onSelect="igual"/>

                    <p class="help-block ui-helper-hidden"></p>
                </div>
            </div>

            <g:if test="${planillaInstance.tipoPlanilla.codigo == 'A'}">
                <div class="control-group">
                    <div>
                        <span class="control-label label label-inverse">
                            Fecha de inicio de obra
                        </span>
                    </div>

                    <div class="controls">
                        <elm:datepicker name="fechaInicioObra" class="required" value="" maxDate="new Date()"/>

                        <p class="help-block ui-helper-hidden"></p>
                    </div>
                </div>
            </g:if>
        </g:form>


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
        </script>

    </body>
</html>