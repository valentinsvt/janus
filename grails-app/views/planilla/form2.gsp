<%--
  Created by IntelliJ IDEA.
  User: luz
  Date: 2/7/13
  Time: 4:34 PM
  To change this template use File | Settings | File Templates.
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<html>
    <head>
        <meta name="layout" content="main">
        <title>
            <g:if test="${planillaInstance.id}">
                Editar planilla
            </g:if>
            <g:else>
                Nueva Planilla
            </g:else>
        </title>

        <style type="text/css">
        .formato {
            font-weight : bolder;
        }
        </style>
    </head>

    <body>

        <g:form class="show-grid" name="frmSave-Planilla" action="save" >
            <fieldset>
                <g:hiddenField name="id" value="${planillaInstance?.id}"/>
                <g:hiddenField id="contrato" name="contrato.id" value="${planillaInstance?.contrato?.id}"/>

                <div class="row">
                    <div class='span2 formato'>
                        Tipo Planilla
                    </div>

                    <div class="span4">
                        <g:select id="tipoPlanilla" name="tipoPlanilla.id" from="${tipos}" optionKey="id" optionValue="nombre" class="many-to-one " value="${planillaInstance?.tipoPlanilla?.id}" noSelection="['null': '']"/>

                        <p class="help-block ui-helper-hidden"></p>
                    </div>

                    <div class="span2 formato">
                        Estado Planilla
                    </div>

                    <div class="span4">
                        <g:select id="estadoPlanilla" name="estadoPlanilla.id" from="${janus.ejecucion.EstadoPlanilla.list()}" optionKey="id" class="many-to-one " value="${planillaInstance?.estadoPlanilla?.id}" noSelection="['null': '']"/>

                        <p class="help-block ui-helper-hidden"></p>
                    </div>
                </div>

                <div class="row">
                    <div class="span2 formato">
                        Periodo Indices
                    </div>

                    <div class="span4">
                        <g:select id="periodoIndices" name="periodoIndices.id" from="${janus.pac.PeriodoValidez.list()}" optionKey="id" class="many-to-one " value="${planillaInstance?.periodoIndices?.id}" noSelection="['null': '']"/>

                        <p class="help-block ui-helper-hidden"></p>
                    </div>

                    <div class="span2 formato">
                        Numero
                    </div>

                    <div class="span4">
                        <g:field type="number" name="numero" class=" required" value="${fieldValue(bean: planillaInstance, field: 'numero')}"/>
                        <span class="mandatory">*</span>

                        <p class="help-block ui-helper-hidden"></p>
                    </div>
                </div>

                <div class="row">
                    <div class="span2 formato">
                        Numero Factura
                    </div>

                    <div class="span4">
                        <g:textField name="numeroFactura" maxlength="15" class="" value="${planillaInstance?.numeroFactura}"/>

                        <p class="help-block ui-helper-hidden"></p>
                    </div>
                </div>

                <div class="row">
                    <div class="span2 formato">
                        Fecha Presentacion
                    </div>

                    <div class="span4">
                        <elm:datepicker name="fechaPresentacion" class="" value="${planillaInstance?.fechaPresentacion}"/>
                        <p class="help-block ui-helper-hidden"></p>
                    </div>

                    <div class="span2 formato">
                        Fecha Ingreso
                    </div>

                    <div class="span4">
                        <elm:datepicker name="fechaIngreso" class="" value="${planillaInstance?.fechaIngreso}"/>
                        <p class="help-block ui-helper-hidden"></p>
                    </div>
                </div>

                <div class="row">
                    <div class="span2 formato">
                        Fecha Pago
                    </div>

                    <div class="span4">
                        <elm:datepicker name="fechaPago" class="" value="${planillaInstance?.fechaPago}"/>
                        <p class="help-block ui-helper-hidden"></p>
                    </div>

                    <div class="span2 formato">
                        Descripcion
                    </div>

                    <div class="span4">
                        <g:textArea name="descripcion" cols="40" rows="5" maxlength="254" class="" value="${planillaInstance?.descripcion}"/>

                        <p class="help-block ui-helper-hidden"></p>
                    </div>
                </div>

                <div class="row">
                    <div class="span2 formato">
                        Valor
                    </div>

                    <div class="span4">
                        <g:field type="number" name="valor" class=" required" value="${fieldValue(bean: planillaInstance, field: 'valor')}"/>
                        <span class="mandatory">*</span>

                        <p class="help-block ui-helper-hidden"></p>
                    </div>

                    <div class="span2 formato">
                        Descuentos
                    </div>

                    <div class="span4">
                        <g:field type="number" name="descuentos" class=" required" value="${fieldValue(bean: planillaInstance, field: 'descuentos')}"/>
                        <span class="mandatory">*</span>

                        <p class="help-block ui-helper-hidden"></p>
                    </div>
                </div>

                <div class="row">
                    <div class="span2 formato">
                        Reajustada
                    </div>

                    <div class="span4">
                        <g:textField name="reajustada" class="" value="${planillaInstance?.reajustada}"/>

                        <p class="help-block ui-helper-hidden"></p>
                    </div>

                    <div class="span2 formato">
                        Reajuste

                    </div>

                    <div class="span4">
                        <g:field type="number" name="reajuste" class=" required" value="${fieldValue(bean: planillaInstance, field: 'reajuste')}"/>
                        <span class="mandatory">*</span>

                        <p class="help-block ui-helper-hidden"></p>
                    </div>
                </div>

                <div class="row">
                    <div class="span2 formato">
                        Fecha Reajuste
                    </div>

                    <div class="span4">
                        <elm:datepicker name="fechaReajuste" class="" value="${planillaInstance?.fechaReajuste}"/>
                        <p class="help-block ui-helper-hidden"></p>
                    </div>

                    <div class="span2 formato">
                        Diferencia Reajuste
                    </div>

                    <div class="span4">
                        <g:field type="number" name="diferenciaReajuste" class=" required" value="${fieldValue(bean: planillaInstance, field: 'diferenciaReajuste')}"/>
                        <span class="mandatory">*</span>

                        <p class="help-block ui-helper-hidden"></p>
                    </div>
                </div>

                <div class="row">
                    <div class="span2 formato">
                        Observaciones
                    </div>

                    <div class="span4">
                        <g:textField name="observaciones" maxlength="127" class="" value="${planillaInstance?.observaciones}"/>

                        <p class="help-block ui-helper-hidden"></p>
                    </div>

                    <div class="span2 formato">
                        Fecha Inicio
                    </div>

                    <div class="span4">
                        <elm:datepicker name="fechaInicio" class="" value="${planillaInstance?.fechaInicio}"/>
                        <p class="help-block ui-helper-hidden"></p>
                    </div>
                </div>

                <div class="row">
                    <div class="span2 formato">
                        Fecha Fin
                    </div>

                    <div class="span4">
                        <elm:datepicker name="fechaFin" class="" value="${planillaInstance?.fechaFin}"/>
                        <p class="help-block ui-helper-hidden"></p>
                    </div>

                    <div class="span2 formato">
                        Oficio Salida
                    </div>

                    <div class="span4">
                        <g:textField name="oficioSalida" maxlength="12" class="" value="${planillaInstance?.oficioSalida}"/>

                        <p class="help-block ui-helper-hidden"></p>
                    </div>
                </div>

                <div class="row">
                    <div class="span2 formato">
                        Fecha Oficio Salida
                    </div>

                    <div class="span4">
                        <elm:datepicker name="fechaOficioSalida" class="" value="${planillaInstance?.fechaOficioSalida}"/>
                        <p class="help-block ui-helper-hidden"></p>
                    </div>

                    <div class="span2 formato">
                        Oficio Pago
                    </div>

                    <div class="span4">
                        <g:textField name="oficioPago" maxlength="12" class="" value="${planillaInstance?.oficioPago}"/>

                        <p class="help-block ui-helper-hidden"></p>
                    </div>
                </div>

                <div class="row">
                    <div class="span2 formato">
                        Fecha Oficio Pago
                    </div>

                    <div class="span4">
                        <elm:datepicker name="fechaOficioPago" class="" value="${planillaInstance?.fechaOficioPago}"/>
                        <p class="help-block ui-helper-hidden"></p>
                    </div>
                </div>
            </fieldset>
        </g:form>

    </body>
</html>