<table class="table table-bordered table-striped table-condensed ">
    <thead>
        <tr>
            <th>Fiscalizador</th>
            <th>Fecha Inicio</th>
            <th>Fecha Fin</th>
        </tr>
    </thead>
    <tbody class="paginate" id="tbAdmin">
        <g:each in="${fiscalizadorContratoInstanceList}" status="i" var="fiscalizadorContratoInstance">
            <g:set var="clase" value="${fiscalizadorContratoInstance.fechaInicio <= new Date().clearTime() && fiscalizadorContratoInstance.fechaFin >= new Date().clearTime() ? 'info' : ''}"/>

            <tr class="${clase}">
                <td>${fiscalizadorContratoInstance.fiscalizador.apellido + ' ' + fiscalizadorContratoInstance.fiscalizador.nombre}</td>
                <td><g:formatDate date="${fiscalizadorContratoInstance.fechaInicio}" format="dd-MM-yyyy"/></td>
                <td><g:formatDate date="${fiscalizadorContratoInstance.fechaFin}" format="dd-MM-yyy"/></td>
            </tr>
        </g:each>
    </tbody>
</table>