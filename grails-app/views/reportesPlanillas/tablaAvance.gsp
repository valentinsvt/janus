${html}

<script type="text/javascript">
    $(function () {
        var sep = "^";
        var $btn = $("#btnSave");
        var $sp = spinner;
        $btn.click(function () {
            $(this).replaceWith($sp);
            var data = "id=${contrato.id}&fecha=${fecha}";
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
                    $sp.replaceWith($btn);
                }
            });
            return false;
        });

        $("#btnPrint").click(function () {
            location.href = "${createLink(action:'reporteAvance', id:contrato.id, params:[fecha:fecha])}";
            return false;
        });

    });
</script>