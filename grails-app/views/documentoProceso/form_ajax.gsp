<%@ page import="janus.pac.DocumentoProceso" %>

<div id="create-DocumentoProceso" class="span" role="main" xmlns="http://www.w3.org/1999/html">
    <g:uploadForm class="form-horizontal" name="frmSave-DocumentoProceso" action="save">
        <g:hiddenField name="id" value="${documentoProcesoInstance?.id}"/>
        <g:hiddenField name="concurso.id" value="${concurso.id}"/>
        <g:hiddenField name="contrato" value="${contrato?.id}"/>
        <g:hiddenField name="show" value="${show}"/>

        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Etapa
                </span>
            </div>

            <div class="controls">
                <g:select id="etapa" name="etapa.id" from="${janus.pac.Etapa.list()}" optionKey="id" class="many-to-one "
                          value="${documentoProcesoInstance?.etapa?.id?:4}"
                          optionValue="descripcion"/>
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>

        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Archivo
                </span>
            </div>

            <div class="controls">
                <g:if test="${documentoProcesoInstance?.path}">
                    <span>
                        ${documentoProcesoInstance?.path ? documentoProcesoInstance?.path : 'No se encuentra cargado ningún archivo!' }
                    </span>
                </g:if>
                <g:else>
                    <input type="file" id="archivo" name="archivo" class="${documentoProcesoInstance?.path ? '' : 'required'}"/>
                </g:else>
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>

        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Nombre
                </span>
            </div>

            <div class="controls">
                <g:textArea name="nombre" cols="40" rows="2" maxlength="255" class="required"
                            value="${documentoProcesoInstance?.nombre}"
                            style="width:280px;"/>
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>

        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Resumen
                </span>
            </div>

            <div class="controls">
                <g:textArea name="resumen" cols="40" rows="2" maxlength="1024" class="" value="${documentoProcesoInstance?.resumen}"
                            style="width:280px;"/>
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>

        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Descripción
                </span>
            </div>

            <div class="controls">
                <g:textField name="descripcion" maxlength="63" class="required"
                             value="${documentoProcesoInstance?.descripcion?:respaldo}"
                             style="width:280px;"/>
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>

        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Palabras Clave
                </span>
            </div>

            <div class="controls">
                <g:textField name="palabrasClave" maxlength="63" class="" value="${documentoProcesoInstance?.palabrasClave}"
                             style="width:280px;"/>
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>

    </g:uploadForm>
</div>

<script type="text/javascript">
    $("#frmSave-DocumentoProceso").validate({
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

    $("input").keyup(function (ev) {
        if (ev.keyCode == 13) {
            submitForm($(".btn-success"));
        }
    });
</script>
