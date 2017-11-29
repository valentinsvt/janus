<%--
  Created by IntelliJ IDEA.
  User: gato
  Date: 29/11/17
  Time: 11:27
--%>

<%@ page import="janus.Programacion" %>

%{--<div id="create-Programacion" class="span" role="main">--}%
<g:form class="form-horizontal" name="frmSave-Programacion" action="save">
    <g:hiddenField name="id" value="${volumen?.id}"/>

    <div class="control-group">
        <div>
            <span class="control-label label label-inverse">
                Cantidad Actual
            </span>
        </div>

        <div class="controls">
            <g:textField name="cantidad" maxlength="40" class=""
                         value="${cantidad}" disabled=""/>
            <p class="help-block ui-helper-hidden"></p>
        </div>
    </div>

    <div class="control-group">
        <div>
            <span class="control-label label label-inverse">
               Cantidad a modificar
            </span>
        </div>

        <div class="controls">
            <g:textField name="volumenCantidad" maxlength="10" class=""
                         value="${volumen?.cantidadComplementaria}"/>
            <p class="help-block ui-helper-hidden"></p>
        </div>
    </div>
</g:form>
%{--</div>--}%

<script type="text/javascript">
//    $("#frmSave-Programacion").validate({
//        errorPlacement: function (error, element) {
//            element.parent().find(".help-block").html(error).show();
//        },
//        success: function (label) {
//            label.parent().hide();
//        },
//        errorClass: "label label-important",
//        submitHandler: function (form) {
//            $(".btn-success").replaceWith(spinner);
//            form.submit();
//        }
//    });

    $("input").keyup(function (ev) {
        if (ev.keyCode == 13) {
            submitForm($(".btn-success"));
        }
    });
</script>
