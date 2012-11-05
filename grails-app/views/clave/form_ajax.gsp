
<%@ page import="janus.Clave" %>

<div id="create-claveInstance" class="span" role="main">
    <g:form class="form-horizontal" name="frmSave-claveInstance" action="save">
        <g:hiddenField name="id" value="${claveInstance?.id}"/>
                
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Clave
                </span>
            </div>

            <div class="controls">
                <g:textField name="clave" maxlength="10" style="width: 90px" class="" value="${claveInstance?.clave}"/>
                
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
                
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Dlgd
                </span>
            </div>

            <div class="controls">
                <g:textField name="dlgd" maxlength="10" style="width: 90px" class="" value="${claveInstance?.dlgd}"/>
                
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
                
    </g:form>

<script type="text/javascript">
    var url = "${resource(dir:'images', file:'spinner_24.gif')}";
    var spinner = $("<img style='margin-left:15px;' src='" + url + "' alt='Cargando...'/>")

    $("#frmSave-claveInstance").validate({
        errorPlacement : function (error, element) {
            element.parent().find(".help-block").html(error).show();
        },
        success        : function (label) {
            label.parent().hide();
        },
        errorClass     : "label label-important",
        submitHandler  : function(form) {
            $("[name=btnSave-claveInstance]").replaceWith(spinner);
            form.submit();
        }
    });
</script>
