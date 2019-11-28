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
            <elm:datepicker name="ini" minDate="new Date(${min})" maxDate="new Date(${max})" onClose="updateDias" class="required dateEC"/>
            <span class="mandatory">*</span>
            <span style="margin-left: 12px">Dia en el que se inicia la suspensión</span>
            <p class="help-block ui-helper-hidden"></p>
        </div>
    </div>

    <div class="control-group">
        <div>
            <span class="control-label label label-inverse">
                Fecha de fin (opcional)
            </span>
        </div>

        <div class="controls">
            <elm:datepicker name="fin" class="dateEC" onClose="updateDias"/>
            <span style="margin-left: 20px">Dia en el que se reinicia la obra</span>
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

    <div class="control-group">
        <div>
            <span class="control-label label label-inverse">
                Memorando No.
            </span>
        </div>

        <div class="controls">
            <g:textField name="memo" class="required allCaps"/>
            <span class="mandatory">*</span>

            <p class="help-block ui-helper-hidden"></p>
        </div>
    </div>

    <div class="control-group">
        <div>
            <span class="control-label label label-inverse">
                Motivo
            </span>
        </div>

        <div class="controls">
            <g:textArea name="motivo" class="required span6" maxlength="255"  style="height: 65px; width: 750px; resize: none"/>
            <span class="mandatory">*</span>

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
            <g:textField name="observaciones" class="required span8"/>
            <span class="mandatory">*</span>

            <p class="help-block ui-helper-hidden"></p>
        </div>
    </div>

</g:form>

<script type="text/javascript">
    $("#frmSave-suspension").validate();

    $(".datepicker").keydown(function () {
        return false;
    });
</script>