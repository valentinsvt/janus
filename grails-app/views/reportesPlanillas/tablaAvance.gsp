${html}

<script type="text/javascript">
    $(function () {
        var sep = "^";
        var $sp = spinner;
        $(".btnSave").click(function () {
//            $(this).replaceWith($sp);
            $("#btnSpin").show();
            var data = "id=${contrato.id}&plnl=${plnl}";
            $(".texto").each(function () {
                data += "&texto=" + $(this).data("num") + sep + $(this).val();
            });
            $(".clima").each(function () {
                var $tarde = $(this).parents("td").next().children("select");
                data += "&clima=" + $(this).data("fecha") + sep + $(this).val();
                data += sep + $tarde.val();
            });
            $.ajax({
                type    : "POST",
                url     : "${createLink(action: 'saveAvance')}",
                data    : data,
                success : function (msg) {
                    if (msg == "OK") {
                        log("Se han almacenado los datos correctamente");
                    } else {
                        log(msg, true);
                    }
                    $("#btnSpin").hide(2000);
                }
            });
            return false;
        });

        $(".btnPrint").click(function () {
            location.href = "${createLink(action:'reporteAvance', id:contrato.id, params:[plnl:plnl])}";
            return false;
        });

        $(".btnPrintTotal").click(function () {
            location.href = "${createLink(action:'reporteAvanceTotal', id:contrato.id, params:[plnl:plnl])}";
            return false;
        });

    });
</script>