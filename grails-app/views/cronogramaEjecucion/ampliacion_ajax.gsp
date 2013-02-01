<g:form class="form-horizontal" name="frmSave-ampliacion" action="ampliacion">
    <div class="alert alert-danger">
        <h4>Atención</h4>
        <i class="icon-info-sign icon-2x pull-left"></i>
        Una vez hecha la ampliación no se puede deshacer.
    </div>

    <div class="control-group">
        <div>
            <span class="control-label label label-inverse">
                Días de ampliación
            </span>
        </div>

        <div class="controls">
            <g:textField name="dias" class="required digits"/>
            <span class="mandatory">*</span>

            <p class="help-block ui-helper-hidden"></p>
        </div>
    </div>

</g:form>

<script type="text/javascript">
    $("#dias").keydown(function (ev) {
        return validarNum(ev);
    });
</script>