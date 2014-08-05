<%@ page import="janus.ClaseObra" %>

<div id="create-claseObraInstance" class="span" role="main">
<g:form class="form-horizontal" name="frmSave-claseObraInstance" action="save_ext">
    <g:hiddenField name="id" value="${claseObraInstance?.id}"/>
    <g:hiddenField name="grupo" value="${grupo}"/>

    %{--<div class="control-group">--}%
        %{--<div>--}%
            %{--<span class="control-label label label-inverse">--}%
                %{--Código--}%
            %{--</span>--}%
        %{--</div>--}%

        %{--<g:if test="${claseObraInstance?.id}">--}%

            %{--<div class="controls">--}%
                %{--<g:textField name="codigo" readonly="readonly" class=" required allCaps" value="${fieldValue(bean: claseObraInstance, field: 'codigo')}"/>--}%
                %{--<span class="mandatory">*</span>--}%
                %{--<p class="help-block ui-helper-hidden"></p>--}%
            %{--</div>--}%

        %{--</g:if>--}%
        %{--<g:else>--}%
            %{--<div class="controls">--}%
                %{--<g:textField name="codigo"  id="codigo1" class=" required allCaps" value=""/>--}%
                %{--<span class="mandatory">*</span>--}%

                %{--<p class="help-block ui-helper-hidden"></p>--}%
            %{--</div>--}%
        %{--</g:else>--}%
    %{--</div>--}%

    <div class="control-group">
        <div>
            <span class="control-label label label-inverse">
                Descripción
            </span>
        </div>

        <div class="controls">
            <g:textField name="descripcion" maxlength="63" class=" required" value="${claseObraInstance?.descripcion}"/>
            <span class="mandatory">*</span>

            <p class="help-block ui-helper-hidden"></p>
        </div>
    </div>

    <div class="control-group">
        <div>
            <span class="control-label label label-inverse">
                Grupo
            </span>
        </div>

        <div class="controls">
            <g:select id="grupo" name="grupo.id" from="${janus.Grupo.list()}" optionKey="id" class="many-to-one "
                      value="${claseObraInstance?.grupo?.id}"/>

            <p class="help-block ui-helper-hidden"></p>
        </div>
    </div>

</g:form>

<script type="text/javascript">
    var url = "${resource(dir:'images', file:'spinner_24.gif')}";
    var spinner = $("<img style='margin-left:15px;' src='" + url + "' alt='Cargando...'/>")

    $("#frmSave-claseObraInstance").validate({
        errorPlacement : function (error, element) {
            element.parent().find(".help-block").html(error).show();
        },
        success        : function (label) {
            label.parent().hide();
        },
        errorClass     : "label label-important",
        submitHandler  : function (form) {
            $("[name=btnSave-claseObraInstance]").replaceWith(spinner);
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


    $("#codigo1").keydown(function (ev){

        return validarNum(ev)
    })

</script>
