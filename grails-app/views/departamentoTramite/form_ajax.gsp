
<%@ page import="janus.DepartamentoTramite" %>

<div id="create-DepartamentoTramite" class="span" role="main">
    <g:form class="form-horizontal" name="frmSave-DepartamentoTramite" action="save">
        <g:hiddenField name="id" value="${departamentoTramiteInstance?.id}"/>
                
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Tipo de Trámite
                </span>
            </div>

            <div class="controls">
                <g:select id="tipoTramite" name="tipoTramite.id" from="${janus.TipoTramite.list()}" optionKey="id"
                          class="many-to-one  required" value="${departamentoTramiteInstance?.tipoTramite?.id}" style="width: 400px;"/>
                <span class="mandatory">*</span>
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
                
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Rol que desempeña
                </span>
            </div>

            <div class="controls">
                <g:select id="rolTramite" name="rolTramite.id" from="${janus.RolTramite.list()}" optionKey="id"
                          class="many-to-one  required" value="${departamentoTramiteInstance?.rolTramite?.id}" style="width: 400px;"/>
                <span class="mandatory">*</span>
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
                
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Coordinación
                </span>
            </div>

            <div class="controls">
                <g:select id="departamento" name="departamento.id" from="${janus.Departamento.list()}" optionKey="id"
                          class="many-to-one  required" value="${departamentoTramiteInstance?.departamento?.id}" style="width: 400px;"/>
                <span class="mandatory">*</span>
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
                
    </g:form>

<script type="text/javascript">
    $("#frmSave-DepartamentoTramite").validate({
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
        }
    });

    $("input").keyup(function (ev) {
        if (ev.keyCode == 13) {
            submitForm($(".btn-success"));
        }
    });
</script>
