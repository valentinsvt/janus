
<%@ page import="janus.pac.TipoGarantia" %>

<div id="create-TipoGarantia" class="span" role="main">
    <g:form class="form-horizontal" name="frmSave-TipoGarantia" action="save">
        <g:hiddenField name="id" value="${tipoGarantiaInstance?.id}"/>
                
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Código
                </span>
            </div>
            <g:if test="${tipoGarantiaInstance?.id}">
                <div class="controls">
                    <g:textField name="codigo" maxlength="2" class="allCaps" value="${tipoGarantiaInstance?.codigo}" readonly="readonly"/>

                    <p class="help-block ui-helper-hidden"></p>
                </div>
            </g:if>
            <g:else>
                <div class="controls">
                    <g:textField name="codigo" maxlength="2" class="required allCaps" value="${tipoGarantiaInstance?.codigo}"/>
                    <span class="mandatory">*</span>
                    <p class="help-block ui-helper-hidden"></p>
                </div>
            </g:else>

        </div>
                
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Descripción
                </span>
            </div>

            <div class="controls">
                <g:textField name="descripcion" maxlength="30" class="required" value="${tipoGarantiaInstance?.descripcion}"/>
                <span class="mandatory">*</span>
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
                
    </g:form>

<script type="text/javascript">
    $("#frmSave-TipoGarantia").validate({
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


    function validarNum(ev) {
        /*
         48-57      -> numeros
         96-105     -> teclado numerico
         188        -> , (coma)
         190        -> . (punto) teclado
         110        -> . (punto) teclado numerico
         8          -> backspace
         46         -> delete
         9          -> tab
         37         -> flecha izq
         39         -> flecha der
         */
        return ((ev.keyCode >= 48 && ev.keyCode <= 57) ||
                (ev.keyCode >= 96 && ev.keyCode <= 105) ||
                ev.keyCode == 8 || ev.keyCode == 46 || ev.keyCode == 9 ||
                ev.keyCode == 37 || ev.keyCode == 39);
    }


    $("#codigo").keydown(function (ev){

        return validarNum(ev)
    })

</script>
