
<%@ page import="janus.pac.CodigoComprasPublicas" %>

<div id="create-CodigoComprasPublicas" class="span" role="main">
    <g:form class="form-horizontal" name="frmSave-CodigoComprasPublicas" action="save">
        <g:hiddenField name="id" value="${codigoComprasPublicasInstance?.id}"/>
                
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Numero
                </span>
            </div>

            <div class="controls">
                <g:textField name="numero" maxlength="32" class=" required" value="${codigoComprasPublicasInstance?.numero}"/>
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
                <g:textField name="descripcion" maxlength="64" class="" value="${codigoComprasPublicasInstance?.descripcion}"/>
                
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
                <elm:datepicker name="fecha" class="" value="${codigoComprasPublicasInstance?.fecha}"/>

                
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
                
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Nivel
                </span>
            </div>

            <div class="controls">
                <g:field type="number" name="nivel" class=" required" value="${fieldValue(bean: codigoComprasPublicasInstance, field: 'nivel')}"/>
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
                <g:select id="padre" name="padre.id" from="${janus.pac.CodigoComprasPublicas.list()}" optionKey="id" class="many-to-one  required" value="${codigoComprasPublicasInstance?.padre?.id}"/>
                <span class="mandatory">*</span>
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
                
    </g:form>

<script type="text/javascript">
    $("#frmSave-CodigoComprasPublicas").validate({
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
