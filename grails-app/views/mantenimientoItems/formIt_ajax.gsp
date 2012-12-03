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
            <g:textField name="nombre" maxlength="160" style="width: 280px" class="allCaps required" value="${itemInstance?.nombre}"/>
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
                <span class="add-on">${cd1}</span>
                <span class="add-on">${cd2}</span>
                <g:textField name="codigo" maxlength="20" class="span2 allCaps required" value="${itemInstance?.codigo.replace(cd1 + ".", '').replace(cd2 + ".", '')}"/>
                <span class="mandatory">*</span>

                <p class="help-block ui-helper-hidden"></p>
            </div>
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

    <div class="control-group">
        <div>
            <span class="control-label label label-inverse">
                Peso
            </span>
        </div>

        <div class="controls">
            <g:field type="number" name="peso" maxlength="20" class=" required" style="width: 180px" value="${fieldValue(bean: itemInstance, field: 'peso')}"/>
            <span class="mandatory">*</span>

            <p class="help-block ui-helper-hidden"></p>
        </div>
    </div>

    <div class="control-group">
        <div>
            <span class="control-label label label-inverse">
                Estado
            </span>
        </div>

        <div class="controls">
            <g:textField name="estado" maxlength="1" style="width: 20px" class="" value="${itemInstance?.estado}"/>

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
                Transporte Peso
            </span>
        </div>

        <div class="controls">
            <g:field type="number" name="transportePeso" maxlength="20" style="width: 180px" class=" required" value="${fieldValue(bean: itemInstance, field: 'transportePeso')}"/>
            <span class="mandatory">*</span>

            <p class="help-block ui-helper-hidden"></p>
        </div>
    </div>

    <div class="control-group">
        <div>
            <span class="control-label label label-inverse">
                Transporte Volumen
            </span>
        </div>

        <div class="controls">
            <g:field type="number" name="transporteVolumen" maxlength="20" style="width: 180px " class=" required" value="${fieldValue(bean: itemInstance, field: 'transporteVolumen')}"/>
            <span class="mandatory">*</span>

            <p class="help-block ui-helper-hidden"></p>
        </div>
    </div>

    <div class="control-group">
        <div>
            <span class="control-label label label-inverse">
                Padre
            </span>
        </div>

        <div class="controls">
            <g:textField name="padre" maxlength="15" style="width: 140px" class="" value="${itemInstance?.padre}"/>

            <p class="help-block ui-helper-hidden"></p>
        </div>
    </div>

    <div class="control-group">
        <div>
            <span class="control-label label label-inverse">
                Inec
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
                Rendimiento
            </span>
        </div>

        <div class="controls">
            <g:textField name="rendimiento" class="" value="${itemInstance?.rendimiento}"/>

            <p class="help-block ui-helper-hidden"></p>
        </div>
    </div>

    <div class="control-group">
        <div>
            <span class="control-label label label-inverse">
                Tipo
            </span>
        </div>

        <div class="controls">
            <g:textField name="tipo" maxlength="1" style="width: 20px" class="" value="${itemInstance?.tipo}"/>

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

    <div class="control-group">
        <div>
            <span class="control-label label label-inverse">
                Registro
            </span>
        </div>

        <div class="controls">
            <g:textField name="registro" maxlength="1" style="width: 20px" class="" value="${itemInstance?.registro}"/>

            <p class="help-block ui-helper-hidden"></p>
        </div>
    </div>

    <div class="control-group">
        <div>
            <span class="control-label label label-inverse">
                Transporte
            </span>
        </div>

        <div class="controls">
            <g:textField name="transporte" maxlength="1" style="width: 20px" class="" value="${itemInstance?.transporte}"/>

            <p class="help-block ui-helper-hidden"></p>
        </div>
    </div>

    <div class="control-group">
        <div>
            <span class="control-label label label-inverse">
                Combustible
            </span>
        </div>

        <div class="controls">
            <g:textField name="combustible" maxlength="1" style="width: 20px" class="" value="${itemInstance?.combustible}"/>

            <p class="help-block ui-helper-hidden"></p>
        </div>
    </div>

    <div class="control-group">
        <div>
            <span class="control-label label label-inverse">
                Observaciones
            </span>
        </div>

        <div class="controls">
            <g:textArea cols="5" rows="4" style="resize: none" name="observaciones" maxlength="127" class="" value="${itemInstance?.observaciones}"/>

            <p class="help-block ui-helper-hidden"></p>
        </div>
    </div>

</g:form>

<script type="text/javascript">

    $(".allCaps").keyup(function () {
        this.value = this.value.toUpperCase();
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
