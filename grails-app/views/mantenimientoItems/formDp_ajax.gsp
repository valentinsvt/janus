<%@ page import="janus.DepartamentoItem" %>

<div id="create" class="span" role="main">
    <g:form class="form-horizontal" name="frmSave" action="saveDp_ajax">
        <g:hiddenField name="id" value="${departamentoItemInstance?.id}"/>

        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Subgrupo
                </span>
            </div>

            <div class="controls">
                ${subgrupo.descripcion}
                <g:hiddenField name="subgrupo.id" value="${subgrupo.id}"/>

            </div>
        </div>

        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Código
                </span>
            </div>

            <div class="controls">
                <g:field type="number" name="codigo" class="allCaps required" value="${fieldValue(bean: departamentoItemInstance, field: 'codigo')}"/>
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
                <g:textArea cols="5" rows="3" name="descripcion" style="resize: none; height: 50px" maxlength="50" class="allCaps required" value="${departamentoItemInstance?.descripcion}"/>
                <span class="mandatory">*</span>

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
                <g:select id="transporte" name="transporte.id" from="${janus.Transporte.list()}" optionKey="id" optionValue="descripcion"
                          class="many-to-one " value="${departamentoItemInstance?.transporte?.id}" noSelection="['null': '']"/>

                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
    </g:form>
</div>
<script type="text/javascript">

    $(".allCaps").keyup(function () {
        this.value = this.value.toUpperCase();
    });

    $("#frmSave").validate({
        rules          : {
            descripcion : {
                remote : {
                    url  : "${createLink(action:'checkDsDp_ajax')}",
                    type : "post",
                    data : {
                        id : "${departamentoItemInstance?.id}"
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
