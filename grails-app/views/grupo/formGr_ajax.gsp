<%@ page import="janus.SubgrupoItems" %>

<div id="create" class="span" role="main">
    <g:form class="form-horizontal" name="frmSave" action="saveGr_ajax">
        %{--<g:hiddenField name="id" value="${subgrupoItemsInstance?.id}"/>--}%
        <g:hiddenField name="id" value="${grupo?.id}"/>

        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Dirección
                </span>
            </div>

            <div class="controls">
                    <g:select name="direccion.id" from="${janus.Direccion.list()}" optionKey="id" optionValue="nombre" style="width: 540px" value="${grupo?.direccion?.id}"/>
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
                <g:if test="${grupo?.id}">
                    %{--${grupo?.codigo?.toString()?.padLeft(3, '0')}--}%
                    ${grupo?.codigo?.toString()}
                </g:if>
                <g:else>
                    %{--<g:textField name="codigo" class="allCaps required input-small" value="${grupo?.codigo?.toString()?.padLeft(3, '0')}" maxlength="3"/>--}%
                    <g:textField name="codigo" class="allCaps required input-small" value="${grupo?.codigo?.toString()}" maxlength="3"/>
                    <span class="mandatory">*</span>

                    <p class="help-block ui-helper-hidden"></p>
                </g:else>
            </div>
        </div>

        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Descripción
                </span>
            </div>

            <div class="controls">
                <g:textArea cols="5" rows="3" style="height: 65px; resize: none;" name="descripcion" maxlength="31" class="allCaps required input-xxlarge" value="${grupo?.descripcion}"/>
                <span class="mandatory">*</span>

                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>

    </g:form>
</div>

<script type="text/javascript">

    //    $(".allCaps").keyup(function () {
    //        this.value = this.value.toUpperCase();
    //    });

    $("#frmSave").validate({
        //$("#descripcion").val($("#direccion.id").val())
        rules          : {
            descripcion : {
                remote : {
                    url  : "${createLink(action:'checkGr_ajax')}",
                    type : "post",
                    data : {
                        id : "${grupo?.id}"
                    }
                }
            }
        },
        messages       : {
            descripcion : {
                remote : "La descripción ya se ha ingresado para otro item"
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
