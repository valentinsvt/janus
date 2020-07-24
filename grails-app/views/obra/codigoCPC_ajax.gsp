<%--
  Created by IntelliJ IDEA.
  User: fabricio
  Date: 24/07/20
  Time: 9:40
--%>

<html>
<head>

</head>

<body>
<div class="alert alert-info">* Máxima cantidad de registros en pantalla 40, use las opciones de búsqueda</div>
<table class="table table-bordered table-striped table-hover table-condensed" id="tabla" style="margin-top: -20px">

    <thead>

    <th>Código</th>
    <th>Descripción</th>
    <th>Seleccionar</th>
    </thead>

    <tbody>

    <g:each in="${codigos}" var="codigo" status="i">
        <tr>
            <td>${codigo.cpacnmro}</td>
            <td>${codigo.cpacdscr}</td>
            <td>
                <a href="#" class="btn btn-small btn-info btnSelCPC" rel="tooltip" title="Seleccionar Código CPC" data-id="${codigo.cpac__id}" data-nombre="${codigo.cpacdscr}" data-numero="${codigo.cpacnmro}">
                    <i class="icon-check"></i>
                </a>
            </td>
        </tr>
    </g:each>
    </tbody>

</table>

<script type="text/javascript">

    $(".btnSelCPC").click(function () {
        var id = $(this).data("id")
        var codigo = $(this).data("numero")
        var nombre = $(this).data("nombre")

        $("#codigoComprasPublicas").val(id)
        $("#item_codigo").val(codigo)

        $("#busqueda_CPC").dialog("close");
        return false;
    });

</script>

</body>
</html>