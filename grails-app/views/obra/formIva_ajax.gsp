<%--
  Created by IntelliJ IDEA.
  User: gato
  Date: 02/06/16
  Time: 01:04 PM
--%>

<g:form class="form-horizontal" name="frmIva" action="save_ext">
    <div class="control-group">
        <div>
            <span class="control-label label label-inverse">
                IVA
            </span>
        </div>

        <div class="controls">
            <g:textField name="iva_name" maxlength="2" class=" required number" value="${janus.Parametros.first().iva}"/>
            <span class="mandatory">*</span>

            <p class="help-block ui-helper-hidden"></p>
        </div>
    </div>
</g:form>


<script type="text/javascript">



//    $("#frmIva").validate({
//        errorPlacement : function (error, element) {
//            element.parent().find(".help-block").html(error).show();
//        },
//        success        : function (label) {
//            label.parent().hide();
//        },
//        errorClass     : "label label-important",
//        submitHandler  : function (form) {
//            $("[name=btnSave-claseObraInstance]").replaceWith(spinner);
//            form.submit();
//        }
//    });


</script>