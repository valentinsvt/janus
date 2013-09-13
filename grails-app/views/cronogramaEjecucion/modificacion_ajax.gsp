<style type="text/css">
table {
    font-size     : 12px !important;
    margin-bottom : 10px !important;
}

table th {
    font-size : 14px !important;
}

.tiny {
    font-size  : 12px !important;
    height     : 10px !important;
    width      : 40px !important;
    text-align : right !important;
}

</style>
<g:form class="form-horizontal" name="frmSave-modificacion" action="modificacion">
    <g:if test="${msg}">
        <div class="alert alert-error">
            ${msg}
        </div>
    </g:if>
    <g:else>
        ${html}
    </g:else>
</g:form>

<script type="text/javascript">
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
                ev.keyCode == 190 || ev.keyCode == 110 ||
                ev.keyCode == 8 || ev.keyCode == 46 || ev.keyCode == 9 ||
                ev.keyCode == 37 || ev.keyCode == 39);
    }

    $(".tiny").bind({
        keydown : function (ev) {
            return validarNum(ev);
        },
        keyup   : function (ev) {

            if (validarNum(ev)) {
                var val = $.trim($(this).val());
                if (val == "") {
                    $("#tf_prct").val("");
                    $("#tf_precio").val("");
                } else {
                    try {
                        val = parseFloat(val);
                        var max = parseFloat($(this).data("max"));
                        if (val > max) {
                            val = max;
                        }
                        var $precio = $("#tf_precio");
                        var total = parseFloat($(this).data("total"));
                        var prct = (val * 100) / total;
                        var dol = $precio.data("max") * (prct / 100);
                        $("#tf_prct").val(number_format(prct, 2, ".", "")).data("val", prct);
                        $precio.val(number_format(dol, 2, ".", "")).data("val", dol);
                        if (ev.keyCode != 110 && ev.keyCode != 190) {
                            $("#tf_cant").val(val).data("val", val);
                        }
                    } catch (e) {
                        ////console.log(e);
                    }
                }
            }
        }
    });

    $("#frmSave-modificacion").validate({
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

    $("#dias").keydown(function (ev) {
        return validarNum(ev);
    });
</script>