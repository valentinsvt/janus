<table class="table table-bordered table-striped table-condensed ">
    <thead>
        <tr>
            <th>Administrador</th>
            <th>Fecha Inicio</th>
            <th>Fecha Fin</th>
        </tr>
    </thead>
    <tbody class="paginate" id="tbAdmin">
        <g:each in="${administradorContratoInstanceList}" status="i" var="administradorContratoInstance">
            <g:set var="clase" value="${administradorContratoInstance.fechaInicio <= new Date().clearTime() && administradorContratoInstance.fechaFin >= new Date().clearTime() ? 'info' : ''}"/>

            <tr class="${clase}">
                <td>${administradorContratoInstance.administrador.apellido + ' ' + administradorContratoInstance.administrador.nombre}</td>
                <td><g:formatDate date="${administradorContratoInstance.fechaInicio}" format="dd-MM-yyyy"/></td>
                <td><g:formatDate date="${administradorContratoInstance.fechaFin}" format="dd-MM-yyy"/></td>
            </tr>
        </g:each>
    </tbody>
</table>