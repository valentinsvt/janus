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
        <script src="${resource(dir: 'js/jquery/plugins/jquery-validation-1.9.0', file: 'jquery.validate.min.js')}"></script>
        <script src="${resource(dir: 'js/jquery/plugins/jquery-validation-1.9.0', file: 'messages_es.js')}"></script>

        <style type="text/css">
        .formato {
            font-weight : bolder;
        }

        select.label-important, textarea.label-important {
            background  : none !important;
            color       : #555 !important;
            text-shadow : none !important;
        }
        </style>
    </head>

    <body>

        <div class="btn-toolbar" style="margin-bottom: 20px;">
            <div class="btn-group">
                <g:link action="list" id="${contrato.id}" class="btn">
                    <i class="icon-arrow-left"></i>
                    Cancelar
                </g:link>

                <g:if test="${anticipoPagado && !liquidado}">
                    <a href="#" id="btnSave" class="btn btn-success">
                        <i class="icon-save"></i>
                        Guardar
                    </a>
                </g:if>
            </div>
        </div>

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

        <g:if test="${anticipoPagado}">
            <g:if test="${!liquidado}">
                <g:form name="frmSave-Planilla" action="save">
                    <fieldset>
                        <g:hiddenField name="id" value="${planillaInstance?.id}"/>
                        <g:hiddenField id="contrato" name="contrato.id" value="${planillaInstance?.contrato?.id}"/>
                        %{--<g:hiddenField name="numero" value="${fieldValue(bean: planillaInstance, field: 'numero')}"/>--}%

                        <div class="alert alert-info">
                            <g:if test="${tipos.find { it.codigo == 'A' }}">
                                <p>
                                    La planilla de tipo "${tipos.find {
                                        it.codigo == 'A'
                                    }.nombre}" se utiliza para registrar el reajuste al momento del inicio de la obra
                                </p>
                            </g:if>

                            <g:if test="${tipos.find { it.codigo == 'P' }}">
                                <p>
                                    Las planillas de tipo "${tipos.find {
                                        it.codigo == 'P'
                                    }.nombre}" se utilizan para registrar el avance de la obra. La última de este tipo es considerada como planilla de liquidación
                                </p>
                            </g:if>
                            <g:if test="${tipos.find { it.codigo == 'L' }}">
                                <p>
                                    La planilla de tipo "${tipos.find {
                                        it.codigo == 'L'
                                    }.nombre}" se utiliza para registrar el reajuste definitivo de la obra
                                </p>
                            </g:if>
                            <g:if test="${tipos.find { it.codigo == 'C' }}">
                                <p>
                                    Las planillas de tipo "${tipos.find {
                                        it.codigo == 'C'
                                    }.nombre}" se utilizan para registrar costos por rubros no incluídos en el volumen de obra
                                </p>
                            </g:if>
                        </div>

                        <div class="row">
                            <div class='span2 formato'>
                                Tipo de Planilla
                            </div>

                            <div class="span4">
                                <g:select id="tipoPlanilla" name="tipoPlanilla.id" from="${tipos}" optionKey="id" optionValue="nombre" class="many-to-one span3 required" value="${planillaInstance?.tipoPlanilla?.id}"/>
                                <span class="mandatory">*</span>

                                <p class="help-block ui-helper-hidden"></p>
                            </div>

                            <div class="span2 formato periodo hide">
                                Periodo
                            </div>

                            <div class="span4 periodo hide">
                                <g:select id="periodoPlanilla" name="periodoPlanilla" from="${periodos}" optionKey="key" class="many-to-one span3"
                                          optionValue="value"/>
                                <span class="mandatory">*</span>

                                <p class="help-block ui-helper-hidden"></p>
                            </div>
                        </div>

                        <div class="row">

                            <div class="span2 formato">
                                Número planilla
                            </div>

                            <div class="span4">
                                <g:textField name="numero" maxlength="30" class="span3 required allCaps" value="${fieldValue(bean: planillaInstance, field: 'numero')}"/>
                                %{--<span class="uneditable-input span3">${planillaInstance.numero}</span>--}%

                                <p class="help-block ui-helper-hidden"></p>
                            </div>

                            <div class="span2 formato">
                                Fiscalizador
                            </div>

                            <div class="span4">
                                %{--<g:set var="fisc" value="${janus.Departamento.get(1)}"/>--}%
                                %{--<g:select name="fiscalizador.id" from="${janus.Persona.findAllByDepartamento(fisc)}" value="${planillaInstance?.fiscalizador ? planillaInstance.fiscalizadorId : fiscalizadorAnterior}" optionKey="id" optionValue="${{ it.nombre + " " + it.apellido }}"/>--}%

                                ${contrato.fiscalizador.titulo} ${contrato.fiscalizador.nombre} ${contrato.fiscalizador.apellido}

                                <p class="help-block ui-helper-hidden"></p>
                            </div>

                            %{--<div class="span2 formato">--}%
                            %{--Número Factura--}%
                            %{--</div>--}%

                            %{--<div class="span4">--}%
                            %{--<g:textField name="numeroFactura" maxlength="15" class=" span3" value="${planillaInstance?.numeroFactura}"/>--}%

                            %{--<p class="help-block ui-helper-hidden"></p>--}%
                            %{--</div>--}%
                        </div>

                        <div class="row">
                            <div class="span2 formato">
                                Oficio de entrada
                            </div>

                            <div class="span4">
                                <g:textField name="oficioEntradaPlanilla" class="span3 required allCaps" value="${planillaInstance.oficioEntradaPlanilla}" maxlength="20"/>
                                <span class="mandatory">*</span>

                                <p class="help-block ui-helper-hidden"></p>
                            </div>

                            <div class="span2 formato">
                                Fecha de oficio de entrada
                            </div>

                            <div class="span4">
                                %{--<elm:datepicker name="fechaOficioEntradaPlanilla" class=" span3 required" minDate="${minDatePres}" maxDate="new Date()" value="${planillaInstance?.fechaOficioEntradaPlanilla}"/>--}%
                                <elm:datepicker name="fechaOficioEntradaPlanilla" class=" span3 required" value="${planillaInstance?.fechaOficioEntradaPlanilla}" maxDate="new Date(${fechaMax.format('yyyy')},${fechaMax.format('MM').toInteger() - 1},${fechaMax.format('dd')},0,0,0,0)"/>
                                <span class="mandatory">*</span>

                                <p class="help-block ui-helper-hidden"></p>
                            </div>
                        </div>

                        <div class="row">

                            <div class="span2 formato">
                                Fecha Ingreso
                            </div>

                            <div class="span4">
                                %{--<elm:datepicker name="fechaIngreso" class=" span3 required" onSelect="fechas" minDate="${minDatePres}" maxDate="new Date()" value="${planillaInstance?.fechaIngreso}"/>--}%
                                <elm:datepicker name="fechaIngreso" class=" span3 required" onSelect="fechas" value="${planillaInstance?.fechaIngreso}" maxDate="new Date(${fechaMax.format('yyyy')},${fechaMax.format('MM').toInteger() - 1},${fechaMax.format('dd')},0,0,0,0)"/>
                                <span class="mandatory">*</span>

                                <p class="help-block ui-helper-hidden"></p>
                            </div>

                            <div class="span2 hide presentacion formato">
                                Fecha Presentacion
                            </div>

                            <div class="span4 hide presentacion">
                                %{--<elm:datepicker name="fechaPresentacion" class=" span3 required" minDate="${minDatePres}" maxDate="${maxDatePres}" value="${planillaInstance?.fechaPresentacion}"/>--}%
                                <elm:datepicker name="fechaPresentacion" class=" span3 required" value="${planillaInstance?.fechaPresentacion}" maxDate="new Date(${fechaMax.format('yyyy')},${fechaMax.format('MM').toInteger() - 1},${fechaMax.format('dd')},0,0,0,0)"/>
                                <span class="mandatory">*</span>

                                <p class="help-block ui-helper-hidden"></p>
                            </div>
                        </div>

                        <g:if test="${!esAnticipo}">
                            <div class="row hide" style="margin-bottom: 10px;" id="divMultaDisp">
                                <div class='span2 formato'>
                                    Multa por no acatar las disposiciones del fiscalizador
                                </div>

                                <div class="span4">
                                    <g:field type="number" name="diasMultaDisposiciones" class="input-mini required digits" value="${planillaInstance.diasMultaDisposiciones}" maxlength="3"/> días
                                </div>

                                <div class='span2 formato'>
                                    Avance físico
                                </div>

                                <div class="span4">
                                    <g:field type="number" name="avanceFisico" class="input-mini required number" value="${planillaInstance.avanceFisico}" maxlength="3"/>
                                </div>
                            </div>
                        </g:if>

                        <g:if test="${esAnticipo}">
                            <div class="row" style="margin-bottom: 10px;">
                                <div class='span2 formato'>
                                    Valor
                                </div>

                                <div class="span4">
                                    $<g:formatNumber number="${contrato.anticipo}" minFractionDigits="2" maxFractionDigits="2" format="##,##0" locale="ec"/>
                                    (anticipo del <g:formatNumber number="${contrato.porcentajeAnticipo}" maxFractionDigits="0" minFractionDigits="0"/>%
                                    de $<g:formatNumber number="${contrato.monto}" minFractionDigits="2" maxFractionDigits="2" format="##,##0" locale="ec"/>)
                                </div>
                            </div>
                        </g:if>

                        <div class="row">
                            <div class="span2 formato">
                                Descripción
                            </div>

                            <div class="span10">
                                <g:textArea name="descripcion" cols="40" rows="2" maxlength="254" class="span9 required" value="${planillaInstance?.descripcion}" style="resize: none;"/>
                                <span class="mandatory">*</span>

                                <p class="help-block ui-helper-hidden"></p>
                            </div>
                        </div>

                        %{--<div class="row">--}%
                        %{--<div class="span2 formato">--}%
                        %{--Oficio Salida--}%
                        %{--</div>--}%

                        %{--<div class="span4">--}%
                        %{--<g:textField name="oficioSalida" maxlength="12" class=" span3 " value="${planillaInstance?.oficioSalida}"/>--}%

                        %{--<p class="help-block ui-helper-hidden"></p>--}%
                        %{--</div>--}%

                        %{--<div class="span2 formato">--}%
                        %{--Fecha Oficio Salida--}%
                        %{--</div>--}%

                        %{--<div class="span4">--}%
                        %{--<elm:datepicker name="fechaOficioSalida" class=" span3" value="${planillaInstance?.fechaOficioSalida}"/>--}%
                        %{--<p class="help-block ui-helper-hidden"></p>--}%
                        %{--</div>--}%
                        %{--</div>--}%

                        %{--<div class="row">--}%
                        %{--<div class="span2 formato">--}%
                        %{--Memo Salida--}%
                        %{--</div>--}%

                        %{--<div class="span4">--}%
                        %{--<g:textField name="memoSalida" maxlength="12" class=" span3" value="${planillaInstance?.memoSalida}"/>--}%

                        %{--<p class="help-block ui-helper-hidden"></p>--}%
                        %{--</div>--}%

                        %{--<div class="span2 formato">--}%
                        %{--Fecha Memo Salida--}%
                        %{--</div>--}%

                        %{--<div class="span4">--}%
                        %{--<elm:datepicker name="fechaMemoSalida" class=" span3" value="${planillaInstance?.fechaMemoSalida}"/>--}%
                        %{--<p class="help-block ui-helper-hidden"></p>--}%
                        %{--</div>--}%
                        %{--</div>--}%

                        <div class="row">
                            <div class="span2 formato">
                                Observaciones
                            </div>

                            <div class="span10">
                                <g:textArea name="observaciones" maxlength="127" class="span9" value="${planillaInstance?.observaciones}"/>

                                <p class="help-block ui-helper-hidden"></p>
                            </div>

                        </div>

                    </fieldset>
                </g:form>
            </g:if>
            <g:else>
                <div class="alert alert-warning">
                    <h4>Alerta</h4>

                    <p style="margin-top: 10px;">
                        <i class="icon-warning-sign icon-2x pull-left"></i>
                        Ya se ha efectuado la planilla de liquidación del reajuste, no puede crear nuevas planillas.
                    </p>
                </div>
            </g:else>
        </g:if>
        <g:else>
            <div class="alert alert-warning">
                <h4>Alerta</h4>

                <p style="margin-top: 10px;">
                    <i class="icon-warning-sign icon-2x pull-left"></i>
                    La planilla de anticipo no ha sido pagada. Por favor páguela para continuar.
                </p>
            </div>
        </g:else>

        <script type="text/javascript">

            function checkPeriodo() {
                if ($("#tipoPlanilla").val() == "3") { //avance
                    $(".periodo,.presentacion,#divMultaDisp").show();
                } else {
                    $("#divMultaDisp").hide();
                    if ($("#tipoPlanilla").val() == "2" || $("#tipoPlanilla").val() == "5") {
                        $(".presentacion").show();
                        $(".periodo").hide();
                    } else {
                        $(".periodo").hide();
                        $(".presentacion").hide();
                    }
                }
            }

            function fechas() {
                if ($.trim($("#fechaPresentacion").val()) == "") {
                    $("#fechaPresentacion").val($("#fechaIngreso").val());
                }
            }

            $(function () {
                checkPeriodo();

                $("#frmSave-Planilla").validate({
                    errorPlacement : function (error, element) {
                        element.parent().find(".help-block").html(error).show();
                    },
                    errorClass     : "label label-important"
                });

                $("#btnSave").click(function () {
                    if ($("#frmSave-Planilla").valid()) {
                        $(this).replaceWith(spinner);
                        $("#frmSave-Planilla").submit();
                    }
                });

                $("#tipoPlanilla").change(function () {
                    checkPeriodo();
                });
            });

        </script>

    </body>
</html>