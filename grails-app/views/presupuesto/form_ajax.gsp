
<%@ page import="janus.Presupuesto" %>

<div id="create-presupuestoInstance" class="span" role="main">
    <g:form class="form-horizontal" name="frmSave-presupuestoInstance" action="save">
        <g:hiddenField name="id" value="${presupuestoInstance?.id}"/>
                
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Número
                </span>
            </div>

            <div class="controls">
                <g:textField name="numero" maxlength="50" style="width: 300px" class=" required" value="${presupuestoInstance?.numero}"/>
                <span class="mandatory">*</span>
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
                
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Año
                </span>
            </div>

            <div class="controls">
                <g:select name="anio.id" from="${janus.pac.Anio.list([sort: "anio"])}" optionKey="id" optionValue="anio"></g:select>
                <span class="mandatory">*</span>
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Actividad
                </span>
            </div>

            <div class="controls">
                <g:textArea name="descripcion" cols="40" rows="7" maxlength="255" class=" required" style="resize: none;width: 300px;" value="${presupuestoInstance?.descripcion}"/>
                <span class="mandatory">*</span>
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Fuente financiamiento
                </span>
            </div>

            <div class="controls">
                <g:select name="fuente.id" from="${janus.FuenteFinanciamiento.list([sort: 'descripcion'])}" optionValue="descripcion" optionKey="id" value="${presupuestoInstance?.fuente?.id}"></g:select>
                <span class="mandatory">*</span>
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Programa
                </span>
            </div>

            <div class="controls">
                <g:textField name="programa"  maxlength="255" class=" required" style="resize: none;width: 300px;" value="${presupuestoInstance?.programa}"/>
                <span class="mandatory">*</span>
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Subprograma
                </span>
            </div>

            <div class="controls">
                <g:textField name="subPrograma" maxlength="255" class=" required" style="resize: none;width: 300px;" value="${presupuestoInstance?.subPrograma}"/>
                <span class="mandatory">*</span>
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Proyecto
                </span>
            </div>

            <div class="controls">
                <g:textField name="proyecto"  maxlength="255" class=" required" style="resize: none;width: 300px;" value="${presupuestoInstance?.proyecto}"/>
                <span class="mandatory">*</span>
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
                

                
    </g:form>

<script type="text/javascript">
    var url = "${resource(dir:'images', file:'spinner_24.gif')}";
    var spinner = $("<img style='margin-left:15px;' src='" + url + "' alt='Cargando...'/>")

    $("#frmSave-presupuestoInstance").validate({
        errorPlacement : function (error, element) {
            element.parent().find(".help-block").html(error).show();
        },
        success        : function (label) {
            label.parent().hide();
        },
        errorClass     : "label label-important",
        submitHandler  : function(form) {
            $("[name=btnSave-presupuestoInstance]").replaceWith(spinner);
            form.submit();
        }
    });
</script>
