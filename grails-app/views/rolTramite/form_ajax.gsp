
<%@ page import="janus.RolTramite" %>

<div id="create-rolTramiteInstance" class="span" role="main">
<g:form class="form-horizontal" name="frmSave-rolTramiteInstance" action="save">
    <g:hiddenField name="id" value="${rolTramiteInstance?.id}"/>

    <div class="control-group">
        <div>
            <span class="control-label label label-inverse">
                Código
            </span>
        </div>
        <g:if test="${rolTramiteInstance?.id}">
            <div class="controls">
                <g:textField name="codigo" maxlength="4" style="width: 40px" class="required" value="${rolTramiteInstance?.codigo}" readonly="readonly"/>
                <span class="mandatory">*</span>
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </g:if>
        <g:else>
            <div class="controls">
                <g:textField name="codigo" maxlength="4" style="width: 40px" class="required allCaps" value="${rolTramiteInstance?.codigo}"/>
                <span class="mandatory">*</span>
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </g:else>

    </div>

    <div class="control-group">
        <div>
            <span class="control-label label label-inverse">
                Descripción
            </span>
        </div>

        <div class="controls">
            <g:textArea cols="5" rows="3" style="resize: none; height: 65px" name="descripcion" maxlength="63" class=" required" value="${rolTramiteInstance?.descripcion}"/>
            <span class="mandatory">*</span>
            <p class="help-block ui-helper-hidden"></p>
        </div>
    </div>

</g:form>

<script type="text/javascript">
    var url = "${resource(dir:'images', file:'spinner_24.gif')}";
    var spinner = $("<img style='margin-left:15px;' src='" + url + "' alt='Cargando...'/>")

    $("#frmSave-rolTramiteInstance").validate({
        errorPlacement : function (error, element) {
            element.parent().find(".help-block").html(error).show();
        },
        success        : function (label) {
            label.parent().hide();
        },
        errorClass     : "label label-important",
        submitHandler  : function(form) {
            $("[name=btnSave-rolTramiteInstance]").replaceWith(spinner);
            form.submit();
        }
    });
</script>
