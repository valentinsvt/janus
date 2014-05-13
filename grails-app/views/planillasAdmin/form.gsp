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
                <g:link action="list" id="${obra.id}" class="btn">
                    <i class="icon-arrow-left"></i>
                    Cancelar
                </g:link>


                <a href="#" id="btnSave" class="btn btn-success">
                    <i class="icon-save"></i>
                    Guardar
                </a>

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

        <g:form name="frmSave-Planilla" action="save">
            <fieldset>
                <g:hiddenField name="id" value="${planillaInstance?.id}"/>
                <g:hiddenField id="obra" name="obra.id" value="${planillaInstance?.obra?.id}"/>
                %{--<g:hiddenField name="numero" value="${fieldValue(bean: planillaInstance, field: 'numero')}"/>--}%

                <div class="alert alert-info">
                    <h3>Administración directa</h3>

                    <g:if test="${existe.contains('P')}">
                        <p>
                            Las planillas de tipo "Avance de obra" se utilizan para registrar el avance de la obra.
                        </p>
                    </g:if>
                    <g:if test="${existe.contains('M')}">
                        <p>
                            La planilla de tipo "Resumen de materiales" se utiliza para registrar los materiales utilizados en la obra.
                        </p>
                    </g:if>

                </div>

                <div class="row">
                    <div class='span2 formato'>
                        Tipo de Planilla
                    </div>

                    <div class="span4">
                        <g:select id="tipoPlanilla" name="tipoPlanilla.id" from="${tiposPlanilla}" optionKey="key" optionValue="value" class="many-to-one span3 required" value="${planillaInstance?.tipoPlanilla?.id}"/>
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

                </div>

                <div class="row">
                    <div class="span2 formato">
                        Memorando de entrada
                    </div>

                    <div class="span4">
                        <g:textField name="oficioEntradaPlanilla" class="span3 required allCaps" value="${planillaInstance.oficioEntradaPlanilla}" maxlength="20"/>
                        <span class="mandatory">*</span>

                        <p class="help-block ui-helper-hidden"></p>
                    </div>

                    <div class="span2 formato">
                        Fecha de memorando
                    </div>

                    <div class="span4">
                        %{--<elm:datepicker name="fechaOficioEntradaPlanilla" class=" span3 required" minDate="${minDatePres}" maxDate="new Date()" value="${planillaInstance?.fechaOficioEntradaPlanilla}"/>--}%
                        <elm:datepicker name="fechaOficioEntradaPlanilla" class=" span3 required" value="${planillaInstance?.fechaOficioEntradaPlanilla}"
                                        minDate="new Date(${fechaMin.format('yyyy')},${fechaMin.format('MM').toInteger() - 1},${fechaMin.format('dd')},0,0,0,0)"
                                        maxDate="new Date()"/>
                        <span class="mandatory">*</span>

                        <p class="help-block ui-helper-hidden"></p>
                    </div>
                </div>


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