<fieldset>
    <legend>Nuevo Administrador</legend>

    <div class="alert alert-error hide" id="divError">

    </div>

    <g:select id="administrador" name="administrador.id" from="${janus.Persona.findAllByActivo(1, [sort: 'apellido'])}" optionKey="id" class="many-to-one required"
              optionValue="${{ it.apellido + ' ' + it.nombre }}"/>
    desde <elm:datepicker value="${new Date()}" name="desde" class="input-small"/>
    <a href="#" class="btn btn-success" id="btnAddAdmin" style="margin-top: -9px;"><i class="icon-plus"></i> Agregar</a>
</fieldset>

<div id="tabla"></div>

<script type="text/javascript">
    function loadTabla() {
        $("#tabla").html("");
        var contrato = $("#administrador").data("contrato");
        $.ajax({
            type    : "POST",
            url     : "${createLink(action: 'tabla')}",
            data    : {
                contrato : contrato
            },
            success : function (msg) {
                $("#tabla").html(msg);
            }
        });
    }
    $(function () {
//        console.log("ASdf", $("#administrador"), $("#administrador").data("contrato"));
//        loadTabla();
        setTimeout(function () {
            loadTabla()
        }, 200);

        $("#btnAddAdmin").click(function () {
            var $admin = $("#administrador");
            var contrato = $admin.data("contrato");
            var admin = $admin.val();
            var desde = $("#desde").val();

            $.ajax({
                type    : "POST",
                url     : "${createLink(action: 'addAdmin')}",
                data    : {
                    contrato : contrato,
                    admin    : admin,
                    desde    : desde
                },
                success : function (msg) {
                    var p = msg.split("_");
                    if (p[0] == "NO") {
                        log(p[1], true);
                    } else {
                        loadTabla();
                    }
                }
            });

        });
    });

</script>