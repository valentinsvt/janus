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
    height     : 15px !important;
    width      : 50px !important;
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

    var $tinyDol = $(".tiny.dol");
    var $tinyPrc = $(".tiny.prct");
    var $tinyFis = $(".tiny.fis");

    function updateTotal() {
        var sumDol = 0, sumPrc = 0, sumFis = 0;
        $tinyDol.each(function () {
            sumDol += parseFloat($.trim($(this).val()));
        });
        $tinyPrc.each(function () {
            sumPrc += parseFloat($.trim($(this).val()));
        });
        $tinyFis.each(function () {
            sumFis += parseFloat($.trim($(this).val()));
        });

        $(".totalModif.dol").text(number_format(sumDol, 2, ".", ""));
        $(".totalModif.prct").text(number_format(sumPrc, 2, ".", ""));
        $(".totalModif.fis").text(number_format(sumFis, 2, ".", ""));
    }

    updateTotal();

    $(".tiny").bind({
        keydown : function (ev) {
            return validarNum(ev);
        },
        blur    : function () {
            var val = $.trim($(this).val());
            var tipo = $(this).data("tipo");
            var periodo = $(this).data("periodo");
            if (val == "") {
                $(".tiny").each(function () {
                    if ($(this).data("periodo") == periodo) {
//                        $(this).val("0.00");
                    }
                })
            } else {
                try {
                    val = parseFloat(val);
//                    var max = parseFloat($(this).data("max"));
//                    if (val > max) {
//                        val = max;
//                    }
                    var $maxSumDol = $tinyDol.last();
                    var $maxSumPrc = $tinyPrc.last();
                    var $maxSumFis = $tinyFis.last();

                    var maxSumFis = $maxSumFis.data("max");
                    var maxSumPrc = $maxSumPrc.data("max");
                    var maxSumDol = $maxSumDol.data("max");

                    var totFis = $maxSumFis.data("total");
                    var totPrc = $maxSumPrc.data("total");
                    var totDol = $maxSumDol.data("total");

                    var $dol = $(".tiny.dol.p" + periodo);
                    var $prc = $(".tiny.prct.p" + periodo);
                    var $fis = $(".tiny.fis.p" + periodo);

                    var dol = parseFloat($dol.val());
                    var prc = parseFloat($prc.val());
                    var fis = parseFloat($fis.val());

                    var ok = false;

                    //                        console.log(val, max, $dol, dol, $fis, fis, $prc, prc, $maxSumDol, maxSumDol, $maxSumPrc, maxSumPrc, $maxSumFis, maxSumFis, totFis, totPrc, totDol);
                    //                        console.log(tipo)
                    switch (tipo) {
                        case "dol":
                            //calcular el % y cantidad
                            dol = val;
                            prc = (dol * 100) / totDol;
                            fis = totFis * (prc / 100);
                            ok = true;
                            break;
//                        case "prct":
//                            //calcular el $ y cantidad
//                            prc = val;
//                            dol = totDol * (prc / 100);
//                            fis = totFis * (prc / 100);
//                            ok = true;
//                            break;
//                        case "fis":
//                            //calcular el % y el $
//                            fis = val;
//                            prc = (fis * 100) / totFis;
//                            dol = totDol * (prc / 100);
//                            ok = true;
//                            break;
                    }
                    if (ok) {
                        dol = number_format(dol, 2, ".", "");
                        fis = number_format(fis, 2, ".", "");
                        prc = number_format(prc, 2, ".", "");
                        $dol.val(dol);
                        $fis.val(fis);
                        $prc.val(prc);

                        var sumDol = 0, sumPrc = 0, sumFis = 0;
                        $tinyDol.each(function () {
                            sumDol += parseFloat($.trim($(this).val()));
                        });
                        $tinyPrc.each(function () {
                            sumPrc += parseFloat($.trim($(this).val()));
                        });
                        $tinyFis.each(function () {
                            sumFis += parseFloat($.trim($(this).val()));
                        });
                        //                        console.log(sumDol, maxSumDol, sumDol - maxSumDol, " | ", sumPrc, maxSumPrc, sumPrc - maxSumPrc, " | ", sumFis, maxSumFis, sumFis - maxSumFis);

                        maxSumDol = parseFloat(number_format(maxSumDol, 2, ".", ""));
                        maxSumPrc = parseFloat(number_format(maxSumPrc, 2, ".", ""));
                        maxSumFis = parseFloat(number_format(maxSumFis, 2, ".", ""));

//                    if (sumDol > maxSumDol) {
//                        $tinyDol.not(".p" + periodo).val("0.00");
//                        //                            console.log($tinyDol.not(".p" + periodo));
//                    }
//                    if (sumPrc > maxSumPrc) {
//                        $tinyPrc.not(".p" + periodo).val("0.00");
//                        //                            console.log($tinyPrc.not(".p" + periodo));
//                    }
//                    if (sumFis > maxSumFis) {
//                        $tinyFis.not(".p" + periodo).val("0.00");
//                        //                            console.log($tinyFis.not(".p" + periodo));
//                    }
                    }
                    updateTotal();
                } catch (e) {
                    ////console.log(e);
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