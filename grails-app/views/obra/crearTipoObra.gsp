<%@ page import="janus.TipoObra" %>

<div id="create-TipoObra" class="span" role="main">
<g:form class="form-horizontal" name="frmSave-TipoObra" controller="tipoObra" action="saveTipoObra">
    <g:hiddenField name="id" value="${tipoObraInstance?.id}"/>

    <div class="control-group">
        <div>
            <span class="control-label label label-inverse">
                Código
            </span>
        </div>

        <div class="controls">
            <g:textField name="codigo" maxlength="1" class=" required" value="${tipoObraInstance?.codigo}" style="width: 40px;"/>
            <span class="mandatory">*</span>
            <p class="help-block ui-helper-hidden"></p>
        </div>
    </div>

    <div class="control-group">
        <div>
            <span class="control-label label label-inverse">
                Descripción
            </span>
        </div>

        <div class="controls">
            <g:textField name="descripcion" maxlength="63" class=" required" value="${tipoObraInstance?.descripcion}" style="width: 300px;"/>
            <span class="mandatory">*</span>
            <p class="help-block ui-helper-hidden"></p>
        </div>
    </div>

</g:form>

<script type="text/javascript">
    $("#frmSave-TipoObra").validate({
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
        },

        rules: {

            codigo: {

                remote : {
                    url  : "${createLink(controller: 'tipoObra' ,action:'checkCodigo')}",
                    type : "post"

                }
            },

            descripcion: {

                remote : {
                    url  : "${createLink(controller: 'tipoObra' ,action:'checkDesc')}",
                    type : "post"

                }
            }

        },

        messages       : {
            codigo      : {
                remote : "El código ya se ha ingresado para otro item"
            },
            descripcion : {
                remote : "La descripción ya se ha ingresado para otro item"
            }
        }


    });

    $("input").keyup(function (ev) {
        if (ev.keyCode == 13) {
            submitForm($(".btn-success"));
        }
    });
</script>
