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
                <g:set var="cd1" value="${subgrupo.codigo.toString().padLeft(3, '0')}"/>
                <g:if test="${departamentoItemInstance.id}">
                    <g:if test="${!mos.contains(subgrupo.id)}">
                        ${cd1}.</g:if>${departamentoItemInstance?.codigo?.toString()?.padLeft(3, '0')}
                </g:if>
                <g:else>
                    <g:if test="${!mos.contains(subgrupo.id)}">
                        <div class="input-prepend">
                            <span class="add-on">${cd1}</span>
                            <g:textField name="codigo" class="allCaps required input-small" maxlength="3" value="${departamentoItemInstance.id ? departamentoItemInstance?.codigo?.toString()?.padLeft(3, '0') : ''}"/>
                            <span class="mandatory">*</span>

                            <p class="help-block ui-helper-hidden"></p>
                        </div>
                    </g:if>
                    <g:else>
                        <g:textField name="codigo" class="allCaps required input-small" maxlength="3" value="${departamentoItemInstance.id ? departamentoItemInstance.codigo.toString().padLeft(3, '0') : ''}"/>
                        <span class="mandatory">*</span>

                        <p class="help-block ui-helper-hidden"></p>
                    </g:else>
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
                <g:textArea cols="5" rows="3" name="descripcion" style="resize: none; height: 50px" maxlength="63" class="allCaps required input-xxlarge" value="${departamentoItemInstance?.descripcion}"/>
                <span class="mandatory">*</span>

                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>

        <g:if test="${subgrupo?.grupo?.codigo?.toInteger() != 1}">
            <div class="control-group">
                <div>
                    <span class="control-label label label-inverse">
                        Grupo asociado a transporte
                    </span>
                </div>

                <div class="controls">
                    <g:select id="transporte" name="transporte.id" from="${janus.Transporte.list()}" optionKey="id" optionValue="descripcion"
                              class="many-to-one " value="${departamentoItemInstance?.transporte?.id}" noSelection="['null': '']"/>

                    <p class="help-block ui-helper-hidden"></p>
                </div>
            </div>
        </g:if>
    </g:form>
</div>
<script type="text/javascript">

    //    $(".allCaps").keyup(function () {
    //        this.value = this.value.toUpperCase();
    //    });

    $("#frmSave").validate({
        rules          : {
            codigo      : {
                remote : {
                    url  : "${createLink(action:'checkCdDp_ajax')}",
                    type : "post",
                    data : {
                        id : "${departamentoItemInstance?.id}",
                        sg : "${subgrupo.id}"
                    }
                }
            },
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
            codigo      : {
                remote : "El código ya se ha ingresado para otro item"
            },
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
