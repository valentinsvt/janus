<table class="table table-bordered table-striped table-hover table-condensed" id="tabla">

    <thead>

    <th style="background-color: ${colorProv};">Provincia</th>
    <th style="background-color: ${colorCant};">Cantón</th>
    <th style="background-color: ${colorParr};">Parroquia</th>
    <th style="background-color: ${colorComn};">Comunidad</th>

    </thead>

    <tbody>

                <g:each in="${comunidades}" var="comn" status="i">
                    <tr>

                        <td>${comn.parroquia.canton.provincia.nombre}</td>
                        <td>${comn.parroquia.canton.nombre}</td>
                        <td>${comn.parroquia.nombre}</td>
                        <td>${comn.nombre}</td>


                    </tr>

    </g:each>
    </tbody>

</table>
