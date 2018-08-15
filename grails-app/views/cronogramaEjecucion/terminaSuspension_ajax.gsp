<g:form class="form-horizontal" name="frmSave-terminaSuspension" >
    <div class="alert alert-danger">
        <h4>Atención</h4>
        <i class="icon-info-sign icon-2x pull-left"></i>
        Una vez terminada la suspensión no se puede deshacer.
        <br/>
        ${suspension}
    </div>

    <div class="control-group">
        <div>
            <span class="control-label label label-inverse">
                Fecha de fin
            </span>
        </div>

        <div class="controls">
            <elm:datepicker name="fcfn"  minDate="new Date(${min})"  class="required dateEC" onClose="updateDias"/>
            <span class="mandatory">*</span>
            <span style="margin-left: 20px">Dia en el que se reinicia la obra</span>
            <p class="help-block ui-helper-hidden"></p>
        </div>
    </div>

    <div class="control-group">
        <div>
            <span class="control-label label label-inverse">
                Observaciones
            </span>
        </div>

        <div class="controls">
            <g:textField name="observaciones" class="span5"/>

            <p class="help-block ui-helper-hidden"></p>
        </div>
    </div>

</g:form>

<script type="text/javascript">
    $("#frmSave-terminaSuspension").validate();

    $(".datepicker").keydown(function () {
        return false;
    });
</script>