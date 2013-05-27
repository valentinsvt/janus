
<%@ page import="janus.PersonasTramite" %>

<div id="create-personasTramiteInstance" class="span" role="main">
    <g:form class="form-horizontal" name="frmSave-personasTramiteInstance" action="save">
        <g:hiddenField name="id" value="${personasTramiteInstance?.id}"/>
                
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Persona
                </span>
            </div>

            <div class="controls">
                <g:select id="persona" name="persona.id" from="${janus.Persona.list()}" optionKey="id" class="many-to-one  required" value="${personasTramiteInstance?.persona?.id}"/>
                <span class="mandatory">*</span>
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
                
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Tramite
                </span>
            </div>

            <div class="controls">
                <g:select id="tramite" name="tramite.id" from="${janus.Tramite.list()}" optionKey="id" class="many-to-one  required" value="${personasTramiteInstance?.tramite?.id}"/>
                <span class="mandatory">*</span>
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
                
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Rol Tramite
                </span>
            </div>

            <div class="controls">
                <g:select id="rolTramite" name="rolTramite.id" from="${janus.RolTramite.list()}" optionKey="id" class="many-to-one " value="${personasTramiteInstance?.rolTramite?.id}" noSelection="['null': '']"/>
                
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
                
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Coordinación
                </span>
            </div>

            <div class="controls">
                <g:select id="departamento" name="departamento.id" from="${janus.Departamento.list()}" optionKey="id" class="many-to-one " value="${personasTramiteInstance?.departamento?.id}" noSelection="['null': '']"/>
                
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
                
    </g:form>

<script type="text/javascript">
    var url = "${resource(dir:'images', file:'spinner_24.gif')}";
    var spinner = $("<img style='margin-left:15px;' src='" + url + "' alt='Cargando...'/>")

    $("#frmSave-personasTramiteInstance").validate({
        errorPlacement : function (error, element) {
            element.parent().find(".help-block").html(error).show();
        },
        success        : function (label) {
            label.parent().hide();
        },
        errorClass     : "label label-important",
        submitHandler  : function(form) {
            $("[name=btnSave-personasTramiteInstance]").replaceWith(spinner);
            form.submit();
        }
    });
</script>
