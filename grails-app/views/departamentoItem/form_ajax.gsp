
<%@ page import="janus.DepartamentoItem" %>

<div id="create-departamentoItemInstance" class="span" role="main">
    <g:form class="form-horizontal" name="frmSave-departamentoItemInstance" action="save">
        <g:hiddenField name="id" value="${departamentoItemInstance?.id}"/>

        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Código
                </span>
            </div>

            <div class="controls">
                <g:field type="number" name="codigo" class=" required" value="${fieldValue(bean: departamentoItemInstance, field: 'codigo')}"/>
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
                <g:textArea cols="5" rows="3" name="descripcion" style="resize: none; height: 50px" maxlength="50" class=" required" value="${departamentoItemInstance?.descripcion}"/>
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
                <g:select id="transporte" name="transporte.id" from="${janus.Transporte.list()}" optionKey="id" class="many-to-one " value="${departamentoItemInstance?.transporte?.id}" noSelection="['null': '']"/>
                
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
                

        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Subgrupo
                </span>
            </div>

            <div class="controls">
                <g:select id="subgrupo" name="subgrupo.id" from="${janus.SubgrupoItems.list()}" optionKey="id" class="many-to-one  required" value="${departamentoItemInstance?.subgrupo?.id}"/>
                <span class="mandatory">*</span>
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
                
    </g:form>

<script type="text/javascript">
    var url = "${resource(dir:'images', file:'spinner_24.gif')}";
    var spinner = $("<img style='margin-left:15px;' src='" + url + "' alt='Cargando...'/>")

    $("#frmSave-departamentoItemInstance").validate({
        errorPlacement : function (error, element) {
            element.parent().find(".help-block").html(error).show();
        },
        success        : function (label) {
            label.parent().hide();
        },
        errorClass     : "label label-important",
        submitHandler  : function(form) {
            $("[name=btnSave-departamentoItemInstance]").replaceWith(spinner);
            form.submit();
        }
    });
</script>
