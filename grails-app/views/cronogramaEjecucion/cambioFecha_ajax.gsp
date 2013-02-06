<g:form class="form-horizontal" name="frmSave-suspension" action="ampliacion">
    <div class="control-group">
        <div>
            <span class="control-label label label-inverse">
                Fecha de fin
            </span>
        </div>

        <div class="controls">
            <elm:datepicker name="fecha" minDate="new Date(${min})" maxDate="new Date(${max})" class="required dateEC"/>
            <span class="mandatory">*</span>

            <p class="help-block ui-helper-hidden"></p>
        </div>
    </div>
</g:form>

<script type="text/javascript">
    $("#frmSave-suspension").validate();

    $(".datepicker").keydown(function() {
        return false;
    });

</script>