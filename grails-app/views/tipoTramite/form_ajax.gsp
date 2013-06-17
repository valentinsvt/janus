<%@ page import="janus.TipoTramite" %>

<div id="create-TipoTramite" class="span" role="main">
<g:form class="form-horizontal" name="frmSave-TipoTramite" action="save">
    <g:hiddenField name="id" value="${tipoTramiteInstance?.id}"/>

    <div class="control-group">
        <div>
            <span class="control-label label label-inverse">
                Codigo
            </span>
        </div>

        <div class="controls">
            <g:textField name="codigo" maxlength="4" class=" required" value="${tipoTramiteInstance?.codigo}"/>
            <span class="mandatory">*</span>

            <p class="help-block ui-helper-hidden"></p>
        </div>
    </div>

    <div class="control-group">
        <div>
            <span class="control-label label label-inverse">
                Descripcion
            </span>
        </div>

        <div class="controls">
            <g:textField name="descripcion" maxlength="63" class=" required" value="${tipoTramiteInstance?.descripcion}"/>
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
            <g:select id="padre" name="padre.id" from="${janus.TipoTramite.list()}" optionKey="id" class="many-to-one " value="${tipoTramiteInstance?.padre?.id}" noSelection="['null': '']"/>

            <p class="help-block ui-helper-hidden"></p>
        </div>
    </div>

    <div class="control-group">
        <div>
            <span class="control-label label label-inverse">
                Tiempo
            </span>
        </div>

        <div class="controls">
            <div class="input-append">
                <g:field type="number" name="tiempo" class=" required digits span1" value="${fieldValue(bean: tipoTramiteInstance, field: 'tiempo')}"/>
                <span class="add-on">días</span>
            </div>

            <span class="mandatory">*</span>

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
            <g:select name="tipo" from="${tipoTramiteInstance.constraints.tipo.inList}" class=" required" value="${tipoTramiteInstance?.tipo}" valueMessagePrefix="tipoTramite.tipo"/>
            <span class="mandatory">*</span>

            <p class="help-block ui-helper-hidden"></p>
        </div>
    </div>

    <div class="control-group">
        <div>
            <span class="control-label label label-inverse">
                Requiere Respuesta
            </span>
        </div>

        <div class="controls">
            <g:select name="requiereRespuesta" from="${tipoTramiteInstance.constraints.requiereRespuesta.inList}" class=" required" value="${tipoTramiteInstance?.requiereRespuesta}"
                      valueMessagePrefix="tipoTramite.requiereRespuesta"/>
            <span class="mandatory">*</span>

            <p class="help-block ui-helper-hidden"></p>
        </div>
    </div>

</g:form>

<script type="text/javascript">
    $("#frmSave-TipoTramite").validate({
        rules          : {
            codigo : {
                remote : {
                    url  : "${createLink(action:'checkCd_ajax')}",
                    type : "post",
                    data : {
                        id : "${tipoTramiteInstance?.id}"
                    }
                }
            }
        },
        messages       : {
            codigo : {
                remote : "El código ya se ha ingresado para otro tipo de trámite"
            }
        },
        errorPlacement : function (error, element) {
            element.parent().find(".help-block").html(error).show();
        },
        success        : function (label) {
            label.parent().hide();
        },
        errorClass     : "label label-important",
        submitHandler  : function (form) {
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
