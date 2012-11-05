
<%@ page import="janus.FormulaPolinomica" %>

<div id="create-formulaPolinomicaInstance" class="span" role="main">
    <g:form class="form-horizontal" name="frmSave-formulaPolinomicaInstance" action="save">
        <g:hiddenField name="id" value="${formulaPolinomicaInstance?.id}"/>
                
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Obra
                </span>
            </div>

            <div class="controls">
                <g:select id="obra" name="obra.id" from="${janus.Obra.list()}" optionKey="id" class="many-to-one " value="${formulaPolinomicaInstance?.obra?.id}" noSelection="['null': '']"/>
                
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
                
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Número
                </span>
            </div>

            <div class="controls">
                <g:textField name="numero" maxlength="3" style="width: 30px"  class=" required" value="${formulaPolinomicaInstance?.numero}"/>
                <span class="mandatory">*</span>
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
                
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Valor
                </span>
            </div>

            <div class="controls">
                <g:field type="number" name="valor" class=" required" value="${fieldValue(bean: formulaPolinomicaInstance, field: 'valor')}"/>
                <span class="mandatory">*</span>
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
                
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Indice
                </span>
            </div>

            <div class="controls">
                <g:select id="indice" name="indice.id" from="${janus.Indice.list()}" optionKey="id" class="many-to-one " value="${formulaPolinomicaInstance?.indice?.id}" noSelection="['null': '']"/>
                
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
                
    </g:form>

<script type="text/javascript">
    var url = "${resource(dir:'images', file:'spinner_24.gif')}";
    var spinner = $("<img style='margin-left:15px;' src='" + url + "' alt='Cargando...'/>")

    $("#frmSave-formulaPolinomicaInstance").validate({
        errorPlacement : function (error, element) {
            element.parent().find(".help-block").html(error).show();
        },
        success        : function (label) {
            label.parent().hide();
        },
        errorClass     : "label label-important",
        submitHandler  : function(form) {
            $("[name=btnSave-formulaPolinomicaInstance]").replaceWith(spinner);
            form.submit();
        }
    });
</script>
