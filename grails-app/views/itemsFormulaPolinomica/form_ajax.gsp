
<%@ page import="janus.ItemsFormulaPolinomica" %>

<div id="create-itemsFormulaPolinomicaInstance" class="span" role="main">
    <g:form class="form-horizontal" name="frmSave-itemsFormulaPolinomicaInstance" action="save">
        <g:hiddenField name="id" value="${itemsFormulaPolinomicaInstance?.id}"/>
                
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Item
                </span>
            </div>

            <div class="controls">
                <g:select id="item" name="item.id" from="${janus.Item.list()}" optionKey="id" class="many-to-one  required" value="${itemsFormulaPolinomicaInstance?.item?.id}"/>
                <span class="mandatory">*</span>
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
                
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Fórmula Polinómica
                </span>
            </div>

            <div class="controls">
                <g:select id="formulaPolinomica" name="formulaPolinomica.id" from="${janus.FormulaPolinomica.list()}" optionKey="id" class="many-to-one  required" value="${itemsFormulaPolinomicaInstance?.formulaPolinomica?.id}"/>
                <span class="mandatory">*</span>
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
                
    </g:form>

<script type="text/javascript">
    var url = "${resource(dir:'images', file:'spinner_24.gif')}";
    var spinner = $("<img style='margin-left:15px;' src='" + url + "' alt='Cargando...'/>")

    $("#frmSave-itemsFormulaPolinomicaInstance").validate({
        errorPlacement : function (error, element) {
            element.parent().find(".help-block").html(error).show();
        },
        success        : function (label) {
            label.parent().hide();
        },
        errorClass     : "label label-important",
        submitHandler  : function(form) {
            $("[name=btnSave-itemsFormulaPolinomicaInstance]").replaceWith(spinner);
            form.submit();
        }
    });
</script>
