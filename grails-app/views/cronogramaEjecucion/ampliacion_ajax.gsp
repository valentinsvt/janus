<g:form class="form-horizontal" name="frmSave-ampliacion" action="ampliacion">
    <div class="alert alert-danger">
        <h4>Atención</h4>
        <i class="icon-info-sign icon-2x pull-left"></i>
        Una vez hecha la ampliación no se puede deshacer.
    </div>

%{--<div class="control-group">--}%
%{--<div>--}%
%{--<span class="control-label label label-inverse">--}%
%{--Número--}%
%{--</span>--}%
%{--</div>--}%

%{--<div class="controls">--}%
%{--<g:textField name="numero" class="required"/>--}%
%{--<span class="mandatory">*</span>--}%

%{--<p class="help-block ui-helper-hidden"></p>--}%
%{--</div>--}%
%{--</div>--}%

    <div class="control-group">
        <div>
            <span class="control-label label label-inverse">
                Días de ampliación
            </span>
        </div>

        <div class="controls">
            <g:textField name="dias" class="required digits" style="width: 100px;"/>
            <span class="mandatory">*</span>

            <p class="help-block ui-helper-hidden"></p>
        </div>
    </div>

    <div class="control-group">
        <div>
            <span class="control-label label label-inverse">
                Memo N.
            </span>
        </div>

        <div class="controls">
            <g:textField name="memo" class="required allCaps" style="width: 240px;"/>
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
            <g:textField name="motivo" class="required" style="width: 500px;"/>
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
            <g:textField name="observaciones" style="width: 500px;"/>
            %{--<span class="mandatory">*</span>--}%

            <p class="help-block ui-helper-hidden"></p>
        </div>
    </div>

</g:form>

<script type="text/javascript">
    $("#frmSave-ampliacion").validate({
        errorPlacement : function (error, element) {
            element.parent().find(".help-block").html(error).show();
        },
        success        : function (label) {
            label.parent().hide();
        },
        errorClass     : "label label-important",
        submitHandler  : function (form) {
            $(".btn-success").replaceWith(spinner);
            form.submit();
        }
    });

    $("#dias").keydown(function (ev) {
        return validarNum(ev);
    });
</script>