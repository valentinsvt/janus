<html>
<head>

</head>

<body>

<table class="table table-bordered table-striped table-hover table-condensed" id="tabla">

    <thead>

    <th style="background-color: ${colorProv};">Provincia</th>
    <th style="background-color: ${colorCant};">Cantón</th>
    <th style="background-color: ${colorParr};">Parroquia</th>
    <th style="background-color: ${colorComn};">Comunidad</th>
    <th>Seleccionar</th>
    </thead>

    <tbody>

    <g:each in="${comunidades}" var="comn" status="i">
        <tr>

            %{--<td class="provincia">${comn.parroquia.canton.provincia.nombre}</td>--}%
            <td class="provincia">${comn.provnmbr}</td>
            %{--<td class="canton">${comn.parroquia.canton.nombre}</td>--}%
            <td class="canton">${comn.cntnnmbr}</td>
            %{--<td class="parroquia">${comn.parroquia.nombre}</td>--}%
            <td class="parroquia">${comn.parrnmbr}</td>
            %{--<td class="comunidad">${comn.nombre}</td>--}%
            <td class="comunidad">${comn.cmndnmbr}</td>
            <td><div style="float: right; margin-right: 5px;" class="ok btnpq ui-state-default ui-corner-all"
                     %{--id="reg_${i}" regId="${comn?.id}" parroquia="${comn?.parroquia?.id}" parroquiaN="${comn?.parroquia?.nombre}"--}%
                     %{--canton="${comn?.parroquia?.canton?.id}"  comN="${comn?.nombre}" comunidad="${comn?.id}" cantN="${comn?.parroquia?.canton?.nombre}" txtReg="${comn.toString()}" ${comunidades}>--}%
                     id="reg_${i}" regId="${comn?.cmnd__id}" parroquia="${comn?.parr__id}" parroquiaN="${comn?.parrnmbr}"
                     canton="${comn?.cntn__id}"  comN="${comn?.cmndnmbr}" comunidad="${comn?.cmnd__id}" cantN="${comn?.cntnnmbr}" >
                <span class="ui-icon ui-icon-circle-check"></span>
            </div></td>

        </tr>

    </g:each>
    </tbody>

</table>

<script type="text/javascript">


    $(".btnpq").click(function () {


        var comunidad = $(this).attr("comunidad");

        $("#hiddenParroquia").val( $(this).attr("parroquia"));
        $("#parrNombre").val($(this).attr("parroquiaN"));

        $("#hiddenComunidad").val($(this).attr("comunidad"));
        $("#comuNombre").val($(this).attr("comN"));

        $("#hiddenCanton").val($(this).attr("canton"));
        $("#cantNombre").val($(this).attr("cantN"));

        var parroquia = $(this).attr("parroquia");

        var canton = $(this).attr("canton");

        $("#busqueda").dialog("close");

        return false;

    });

</script>

</body>
</html>