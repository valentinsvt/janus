
<%@ page import="janus.Tramite" %>

<div id="create-tramiteInstance" class="span" role="main">
    <g:form class="form-horizontal" name="frmSave-tramiteInstance" action="save">
        <g:hiddenField name="id" value="${tramiteInstance?.id}"/>
                
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Obra
                </span>
            </div>

            <div class="controls">
                <g:select id="obra" name="obra.id" from="${janus.Obra.list()}" optionKey="id" class="many-to-one " value="${tramiteInstance?.obra?.id}" noSelection="['null': '']"/>
                
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
                
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Código
                </span>
            </div>

            <div class="controls">
                <g:textField name="codigo" maxlength="31" style="width: 300px" class="" value="${tramiteInstance?.codigo}"/>
                
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
                
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Tipo Tramite
                </span>
            </div>

            <div class="controls">
                <g:select id="tipoTramite" name="tipoTramite.id" from="${janus.TipoTramite.list()}" optionKey="id" class="many-to-one " value="${tramiteInstance?.tipoTramite?.id}" noSelection="['null': '']"/>
                
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
                
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Contrato
                </span>
            </div>

            <div class="controls">
                <g:select id="contrato" name="contrato.id" from="${janus.Contrato.list()}" optionKey="id" class="many-to-one " value="${tramiteInstance?.contrato?.id}" noSelection="['null': '']"/>
                
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
                
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Trámite Padre
                </span>
            </div>

            <div class="controls">
                <g:field type="number" name="tramitePadre" class=" required" value="${fieldValue(bean: tramiteInstance, field: 'tramitePadre')}"/>
                <span class="mandatory">*</span>
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
                
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Fecha
                </span>
            </div>

            <div class="controls">
                <g:textField name="fecha" class="datepicker" style="width: 90px" value="${tramiteInstance?.fecha}"/>
<script type="text/javascript">
$("#fecha").datepicker({
changeMonth: true,
changeYear: true,
showOn: "both",
buttonImage: "${resource(dir:'images', file:'calendar.png')}",
buttonImageOnly: true
});
</script>
                
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
                
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Descripcion
                </span>
            </div>

            <div class="controls">
                <g:textArea name="descripcion" cols="40" rows="5" maxlength="4095" class="" value="${tramiteInstance?.descripcion}"/>
                
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
                
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Fecha Recepcion
                </span>
            </div>

            <div class="controls">
                <g:textField name="fechaRecepcion" class="datepicker" style="width: 90px" value="${tramiteInstance?.fechaRecepcion}"/>
<script type="text/javascript">
$("#fechaRecepcion").datepicker({
changeMonth: true,
changeYear: true,
showOn: "both",
buttonImage: "${resource(dir:'images', file:'calendar.png')}",
buttonImageOnly: true
});
</script>
                
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
                
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Documentos Adjuntos
                </span>
            </div>

            <div class="controls">
                <g:textArea cols="5" rows="4" style="resize: none; height: 110px" name="documentosAdjuntos" maxlength="127" class="" value="${tramiteInstance?.documentosAdjuntos}"/>
                
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
                
    </g:form>

<script type="text/javascript">
    var url = "${resource(dir:'images', file:'spinner_24.gif')}";
    var spinner = $("<img style='margin-left:15px;' src='" + url + "' alt='Cargando...'/>")

    $("#frmSave-tramiteInstance").validate({
        errorPlacement : function (error, element) {
            element.parent().find(".help-block").html(error).show();
        },
        success        : function (label) {
            label.parent().hide();
        },
        errorClass     : "label label-important",
        submitHandler  : function(form) {
            $("[name=btnSave-tramiteInstance]").replaceWith(spinner);
            form.submit();
        }
    });
</script>
