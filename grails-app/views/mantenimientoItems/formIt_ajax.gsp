<%@ page import="janus.Item" %>

<div id="create" class="span" role="main">
<g:form class="form-horizontal" name="frmSave" action="saveIt_ajax">
    <g:hiddenField name="id" value="${itemInstance?.id}"/>
    <div class="control-group">
        <div>
            <span class="control-label label label-inverse">
                Subgrupo
            </span>
        </div>

        <div class="controls">
            <g:hiddenField name="departamento.id" value="${departamento.id}"/>
            ${departamento.descripcion}
        </div>
    </div>

    <div class="control-group">
        <div>
            <span class="control-label label label-inverse">
                Nombre
            </span>
        </div>

        <div class="controls">
            <g:textField name="nombre" maxlength="160" class="allCaps required input-xxlarge" value="${itemInstance?.nombre}"/>
            <span class="mandatory">*</span>

            <p class="help-block ui-helper-hidden"></p>
        </div>
    </div>

    <div class="control-group">
        <div>
            <span class="control-label label label-inverse">
                Código
            </span>
        </div>

        <div class="controls">
            <div class="input-prepend">
                <g:set var="cd1" value="${departamento.subgrupo.codigo.toString().padLeft(3, '0')}"/>
                <g:set var="cd2" value="${departamento.codigo.toString().padLeft(3, '0')}"/>
                <g:set var="cd" value="${itemInstance?.codigo}"/>
                <g:if test="${itemInstance.id && cd}">
                    <g:set var="cd" value="${cd?.replace(cd1 + ".", '').replace(cd2 + ".", '')}"/>
                </g:if>
                <span class="add-on">${cd1}</span>
                <span class="add-on">${cd2}</span>
                <g:textField name="codigo" maxlength="20" class="allCaps required input-small" value="${cd}"/>
                <span class="mandatory">*</span>

                <p class="help-block ui-helper-hidden"></p>
            </div>
            %{--<span class="mandatory">*</span>--}%

            %{--<p class="help-block ui-helper-hidden"></p>--}%
        </div>
    </div>

    <div class="control-group">
        <div>
            <span class="control-label label label-inverse">
                Unidad
            </span>
        </div>

        <div class="controls">
            <g:select id="unidad" name="unidad.id" from="${janus.Unidad.list([sort: 'descripcion'])}" optionKey="id" optionValue="descripcion"
                      class="many-to-one " value="${itemInstance?.unidad?.id}" noSelection="['': '']"/>

            <p class="help-block ui-helper-hidden"></p>
        </div>
    </div>

    <g:if test="${grupo.toString() == '1'}">
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Transporte
                </span>
            </div>

            <div class="controls">
                <g:select from="['P': 'Peso', 'V': 'Volumen']" name="transporte" class="input-medium" value="${itemInstance?.transporte}" optionKey="key" optionValue="value"/>

                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>

        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Peso
                </span>
            </div>

            <div class="controls">
                <div class="input-append">
                    <g:field type="number" name="peso" maxlength="20" class=" required input-small" value="${fieldValue(bean: itemInstance, field: 'peso')}"/>
                    <span class="add-on" id="spanPeso">
                        ${itemInstance?.transporte == 'V' ? 'M<sup>3</sup>' : 'Ton'}
                    </span>
                </div>
                <span class="mandatory">*</span>

                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
    </g:if>
    <g:else>
        <g:hiddenField name="peso" maxlength="20" class=" required input-small" value="0"/>
    </g:else>

    <div class="control-group">
        <div>
            <span class="control-label label label-inverse">
                Estado
            </span>
        </div>

        <div class="controls">
            <g:select from="['A': 'Activo', 'B': 'Dado de baja']" name="estado" class="input-medium" value="${itemInstance?.estado}" optionKey="key" optionValue="value"/>

            <p class="help-block ui-helper-hidden"></p>
        </div>
    </div>

    <div class="control-group">
        <div>
            <span class="control-label label label-inverse">
                Fecha
            </span>
        </div>

        <div class="controls">
            <elm:datepicker name="fecha" class="datepicker" style="width: 90px" value="${itemInstance?.fecha}"/>

            <p class="help-block ui-helper-hidden"></p>
        </div>
    </div>

    <div class="control-group">
        <div>
            <span class="control-label label label-inverse">
                Indice INEC
            </span>
        </div>

        <div class="controls">
            <g:textField name="inec" maxlength="1" style="width: 20px" class="" value="${itemInstance?.inec}"/>

            <p class="help-block ui-helper-hidden"></p>
        </div>
    </div>

    <div class="control-group">
        <div>
            <span class="control-label label label-inverse">
                Nombre corto
            </span>
        </div>

        <div class="controls">
            <g:textField name="campo" maxlength="29" style="width: 300px" class="allCaps" value="${itemInstance?.campo}"/>

            <p class="help-block ui-helper-hidden"></p>
        </div>
    </div>

    <g:if test="${grupo.toString() == '1'}">
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Combustible
                </span>
            </div>

            <div class="controls">
                <g:select from="['S': 'Sí', 'N': 'No']" name="combustible" class="input-small" value="${itemInstance?.combustible}" optionKey="key" optionValue="value"/>

                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
    </g:if>

    <div class="control-group">
        <div>
            <span class="control-label label label-inverse">
                Observaciones
            </span>
        </div>

        <div class="controls">
            <g:textArea cols="5" rows="4" style="resize: none; height: 65px;" name="observaciones" maxlength="127" class="input-xxlarge" value="${itemInstance?.observaciones}"/>

            <p class="help-block ui-helper-hidden"></p>
        </div>
    </div>

</g:form>

<script type="text/javascript">

    //    $(".allCaps").keyup(function () {
    //        this.value = this.value.toUpperCase();
    //    });

    $("#transporte").change(function () {
        var v = $(this).val();
        var l = "";
        if (v == 'P') {
            l = "Ton";
        } else {
            l = "M<sup>3</sup>";
        }
        $("#spanPeso").html(l);
    });

    $("#frmSave").validate({
        rules          : {
            codigo : {
                remote : {
                    url  : "${createLink(action:'checkCdIt_ajax')}",
                    type : "post",
                    data : {
                        id  : "${itemInstance?.id}",
                        dep : "${departamento.id}"
                    }
                }
            },
            nombre : {
                remote : {
                    url  : "${createLink(action:'checkNmIt_ajax')}",
                    type : "post",
                    data : {
                        id : "${itemInstance?.id}"
                    }
                }
            },
            campo  : {
                remote : {
                    url  : "${createLink(action:'checkCmIt_ajax')}",
                    type : "post",
                    data : {
                        id : "${itemInstance?.id}"
                    }
                },
                regex  : /^[A-Za-z\d_]+$/
            }
        },
        messages       : {
            codigo : {
                remote : "El código ya se ha ingresado para otro item"
            },
            nombre : {
                remote : "El nombre ya se ha ingresado para otro item"
            },
            campo  : {
                regex  : "El nombre corto no permite caracteres especiales",
                remote : "El nombre ya se ha ingresado para otro item"
            }
        },
        errorPlacement : function (error, element) {
            element.parent().find(".help-block").html(error).show();
        },
        success        : function (label) {
            label.parent().hide();
        },
        errorClass     : "label label-important"
    });
</script>
