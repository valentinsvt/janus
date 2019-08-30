<%@ page import="janus.Departamento" %>
<fieldset>
    <legend>Nuevo Fiscalizador</legend>

    <div class="alert alert-error hide" id="divError">

    </div>

    %{--<g:select id="fiscalizador" name="fiscalizador.id" from="${janus.Persona.findAllByActivo(1, [sort: 'apellido'])}"--}%
    <g:select id="fiscalizador" name="fiscalizador.id" from="${janus.Persona.findAllByActivoAndDepartamentoInList(1,
            janus.Departamento.findAllByCodigoInList(['FISC', 'DFZLAB', 'DFZCCO', 'DGFDIA', 'DGFDV']), [sort: 'apellido'])}"
              optionKey="id" class="many-to-one required" optionValue="${{ it.apellido + ' ' + it.nombre }}"
              noSelection="['null': 'Seleccione ...']" style="width:300px; margin-right: 20px;"/>
    Desde: <elm:datepicker value="${new Date()}" name="desde" class="input-small"/>
    <a href="#" class="btn btn-success" id="btnAddFisc" style="margin-top: -9px; margin-left: 20px;"><i class="icon-plus"></i> Agregar</a>
</fieldset>

<div id="tabla"></div>

<script type="text/javascript">
    function loadTabla() {
        $("#tabla").html("");
        var contrato = $("#fiscalizador").data("contrato");
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

        $("#btnAddFisc").click(function () {
            var $fisc = $("#fiscalizador");
            var contrato = $fisc.data("contrato");
            var fisc = $fisc.val();
            var desde = $("#desde").val();

            $.ajax({
                type    : "POST",
                url     : "${createLink(action: 'addFisc')}",
                data    : {
                    contrato : contrato,
                    fisc     : fisc,
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