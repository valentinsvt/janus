<g:form class="form-horizontal" name="frmSave-suspension" action="ampliacion">
    <div class="alert alert-danger">
        <h4>Atención</h4>
        <i class="icon-info-sign icon-2x pull-left"></i>
        Una vez hecha la suspensión no se puede deshacer.
    </div>

    <div class="control-group">
        <div>
            <span class="control-label label label-inverse">
                Fecha de inicio
            </span>
        </div>

        <div class="controls">
            <elm:datepicker name="inicio" minDate="new Date(${min})" onClose="updateDias" class="required dateEC"/>
            <span class="mandatory">*</span>

            <p class="help-block">Incluido</p>

            <p class="help-block ui-helper-hidden"></p>
        </div>
    </div>

    <div class="control-group">
        <div>
            <span class="control-label label label-inverse">
                Fecha de fin
            </span>
        </div>

        <div class="controls">
            <elm:datepicker name="fin" class="required dateEC" onClose="updateDias"/>
            <span class="mandatory">*</span>

            <p class="help-block">No incluido</p>

            <p class="help-block ui-helper-hidden"></p>
        </div>
    </div>

    <div class="control-group">
        <div>
            <span class="control-label label label-inverse">
                Días de suspensión
            </span>
        </div>

        <div class="controls">
            <span id="diasSuspension">

            </span>
        </div>
    </div>

</g:form>

<script type="text/javascript">
    $("#frmSave-suspension").validate();

    $(".datepicker").keydown(function () {
        return false;
    });
</script>