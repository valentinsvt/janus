<%@ page import="janus.SubgrupoItems" %>

<div id="create" class="span" role="main">
    <g:form class="form-horizontal" name="frmSave" action="saveSg_ajax">
        <g:hiddenField name="id" value="${subgrupoItemsInstance?.id}"/>

        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    <g:if test="${subgrupoItemsInstance?.grupo?.codigo?.toInteger() > 3}">
                        Solicitante
                    </g:if>
                    <g:else>
                        Grupo
                    </g:else>
                </span>
            </div>

            <div class="controls">
                ${grupo.descripcion}
                <g:hiddenField name="grupo.id" value="${grupo.id}"/>
            </div>
        </div>

        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Código
                </span>
            </div>

            <div class="controls">
                <g:if test="${subgrupoItemsInstance.id}">
                    %{--${subgrupoItemsInstance.codigo.toString().padLeft(3, '0')}--}%
                    ${subgrupoItemsInstance.codigo.toString()}
                </g:if>
                <g:else>
                    %{--<g:textField name="codigo" class="allCaps required input-small" value="${subgrupoItemsInstance?.codigo?.toString()?.padLeft(3, '0')}" maxlength="3"/>--}%
                    <g:textField name="codigo" class="allCaps required input-small" value="${subgrupoItemsInstance?.codigo?.toString()}" maxlength="3"/>
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
                <g:textArea cols="5" rows="3" style="height: 65px; resize: none;" name="descripcion" maxlength="63" class="allCaps required input-xxlarge" value="${subgrupoItemsInstance?.descripcion}"/>
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
        rules          : {
            descripcion : {
                remote : {
                    url  : "${createLink(action:'checkDsSg_ajax')}",
                    type : "post",
                    data : {
                        id : "${subgrupoItemsInstance?.id}"
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
