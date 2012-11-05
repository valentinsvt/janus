
<%@ page import="janus.Cronograma" %>

<div id="create-cronogramaInstance" class="span" role="main">
    <g:form class="form-horizontal" name="frmSave-cronogramaInstance" action="save">
        <g:hiddenField name="id" value="${cronogramaInstance?.id}"/>
                
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Nombre
                </span>
            </div>

            <div class="controls">
                <g:textField name="nombre" maxlength="10" style="width: 100px" class="" value="${cronogramaInstance?.nombre}"/>
                
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
                <g:textField name="codigo" maxlength="10" style="width: 100px" class="" value="${cronogramaInstance?.codigo}"/>
                
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
                
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Obra
                </span>
            </div>

            <div class="controls">
                <g:select id="obra" name="obra.id" from="${janus.Obra.list()}" optionKey="id" class="many-to-one " value="${cronogramaInstance?.obra?.id}" noSelection="['null': '']"/>
                
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
                
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Orden
                </span>
            </div>

            <div class="controls">
                <g:textField name="orden" maxlength="10" style="width: 100px" class="" value="${cronogramaInstance?.orden}"/>
                
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
                
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Codigo Cronograma
                </span>
            </div>

            <div class="controls">
                <g:textField name="codigoCronograma" maxlength="10" style="width: 100px" class="" value="${cronogramaInstance?.codigoCronograma}"/>
                
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
                
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Cantidad
                </span>
            </div>

            <div class="controls">
                <g:textField name="cantidad" maxlength="10" style="width: 100px" class="" value="${cronogramaInstance?.cantidad}"/>
                
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
                
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Precio Unitario
                </span>
            </div>

            <div class="controls">
                <g:textField name="precioUnitario" maxlength="10" style="width: 100px" class="" value="${cronogramaInstance?.precioUnitario}"/>
                
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
                
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Sbtl
                </span>
            </div>

            <div class="controls">
                <g:textField name="sbtl" maxlength="10" style="width: 100px" class="" value="${cronogramaInstance?.sbtl}"/>
                
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
                
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Tipo Cronograma
                </span>
            </div>

            <div class="controls">
                <g:textField name="tipoCronograma" maxlength="10" class="" value="${cronogramaInstance?.tipoCronograma}"/>
                
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
                
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Tipo Periodo
                </span>
            </div>

            <div class="controls">
                <g:textField name="tipoPeriodo" maxlength="10" class="" value="${cronogramaInstance?.tipoPeriodo}"/>
                
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
                
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Periodo
                </span>
            </div>

            <div class="controls">
                <g:textField name="periodo" maxlength="10" class="" value="${cronogramaInstance?.periodo}"/>
                
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
                
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Crnop1
                </span>
            </div>

            <div class="controls">
                <g:textField name="crno__p1" maxlength="10" class="" value="${cronogramaInstance?.crno__p1}"/>
                
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
                
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Crnop2
                </span>
            </div>

            <div class="controls">
                <g:textField name="crno__p2" maxlength="10" class="" value="${cronogramaInstance?.crno__p2}"/>
                
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
                
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Crnop50
                </span>
            </div>

            <div class="controls">
                <g:textField name="crno_p50" maxlength="10" class="" value="${cronogramaInstance?.crno_p50}"/>
                
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
                
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Ttal
                </span>
            </div>

            <div class="controls">
                <g:textField name="ttal" maxlength="10" class="" value="${cronogramaInstance?.ttal}"/>
                
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
                
    </g:form>

<script type="text/javascript">
    var url = "${resource(dir:'images', file:'spinner_24.gif')}";
    var spinner = $("<img style='margin-left:15px;' src='" + url + "' alt='Cargando...'/>")

    $("#frmSave-cronogramaInstance").validate({
        errorPlacement : function (error, element) {
            element.parent().find(".help-block").html(error).show();
        },
        success        : function (label) {
            label.parent().hide();
        },
        errorClass     : "label label-important",
        submitHandler  : function(form) {
            $("[name=btnSave-cronogramaInstance]").replaceWith(spinner);
            form.submit();
        }
    });
</script>
