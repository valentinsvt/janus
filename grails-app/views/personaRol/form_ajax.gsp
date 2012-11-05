
<%@ page import="janus.PersonaRol" %>

<div id="create-personaRolInstance" class="span" role="main">
    <g:form class="form-horizontal" name="frmSave-personaRolInstance" action="save">
        <g:hiddenField name="id" value="${personaRolInstance?.id}"/>
                
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Persona
                </span>
            </div>

            <div class="controls">
                <g:select id="persona" name="persona.id" from="${janus.Persona.list()}" optionKey="id" class="many-to-one  required" value="${personaRolInstance?.persona?.id}"/>
                <span class="mandatory">*</span>
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
                
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Función
                </span>
            </div>

            <div class="controls">
                <g:select id="funcion" name="funcion.id" from="${janus.Funcion.list()}" optionKey="id" class="many-to-one  required" value="${personaRolInstance?.funcion?.id}"/>
                <span class="mandatory">*</span>
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
                
    </g:form>

<script type="text/javascript">
    var url = "${resource(dir:'images', file:'spinner_24.gif')}";
    var spinner = $("<img style='margin-left:15px;' src='" + url + "' alt='Cargando...'/>")

    $("#frmSave-personaRolInstance").validate({
        errorPlacement : function (error, element) {
            element.parent().find(".help-block").html(error).show();
        },
        success        : function (label) {
            label.parent().hide();
        },
        errorClass     : "label label-important",
        submitHandler  : function(form) {
            $("[name=btnSave-personaRolInstance]").replaceWith(spinner);
            form.submit();
        }
    });
</script>
