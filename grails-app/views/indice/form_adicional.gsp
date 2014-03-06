
<%@ page import="janus.Indice" %>

<style type="text/css">
    .az {
           color : #008;
       }
</style>

<div id="create-Indice" class="span" role="main">
    <g:form class="form-horizontal" name="frmSave-Indice" action="grabar">
        <g:hiddenField name="id" value="${indiceInstance?.id}"/>

        <div class="well well-small" style="font-size: 10px; margin-top: -10px;">
            <p class="az"><strong>Ley Orgánica del Sistema Nacional de Contratación Pública R.O. 395</strong>&nbsp;&nbsp;&nbsp;REAJUSTE DE PRECIOS</p>
            <strong>Artículo 83.- Índices.-</strong> Para la aplicación de las fórmulas, los precios e índices de
            precios serán proporcionados por el Instituto Nacional de Estadísticas y Censos (INEC), mensualmente,
            dentro de los diez días del mes siguiente, de acuerdo con su propia reglamentación. Para estos efectos,
            el <span class="az">Instituto Nacional de Contratación Pública</span> mantendrá permanente coordinación con el INEC.<br/>
            Si por la naturaleza del contrato, el Instituto Nacional de Estadísticas y Censos no pudiere
            proporcionar los precios e índices de precios, la respectiva entidad, solicitará al INEC la calificación
            de aquellos, tomándolos de publicaciones especializadas. El INEC, en el término de cinco días contado
            desde la recepción de la solicitud, calificará la idoneidad de los precios e índices de precios de
            dichas publicaciones especializadas propuestas. En caso de que dicho instituto no lo haga en el
            término señalado, se considerarán calificados tales precios e índice de precios, para efectos de su
            inclusión en la fórmula polinómica, <strong>bajo la responsabilidad de la entidad.</strong>
        </div>

        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Tipo Indice
                </span>
            </div>

            <div class="controls">
                <g:select id="tipoIndice" name="tipoIndice.id" from="${janus.TipoIndice.list()}" optionKey="id" class="many-to-one " value="${indiceInstance?.tipoIndice?.id}" noSelection="['null': '']"/>
                
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
                <g:textField name="codigo" maxlength="20" class="" value="${indiceInstance?.codigo}"/>
                
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
                <g:textField name="descripcion" maxlength="131" class=" required" value="${indiceInstance?.descripcion}" style="width:360px;"/>
                <span class="mandatory">*</span>
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
                
    </g:form>

<script type="text/javascript">
    $("#frmSave-Indice").validate({
        errorPlacement : function (error, element) {
            element.parent().find(".help-block").html(error).show();
        },
        success        : function (label) {
            label.parent().hide();
        },
        errorClass     : "label label-important",
        submitHandler  : function(form) {
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
