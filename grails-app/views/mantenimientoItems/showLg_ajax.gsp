<div class="tituloTree">Precios de ${item.nombre} en ${lugarNombre}</div>
<table class="table table-striped table-bordered table-hover table-condensed">
    <thead>
        <tr>
            <g:if test="${lgar}">
                <th>Lugar</th>
            </g:if>
            <th>Fecha</th>
            <th>Precio</th>
        </tr>
    </thead>
    <tbody>
        <g:each in="${precios}" var="precio">
            <tr>
                <g:if test="${lgar}">
                    <td>
                        ${precio.lugar.descripcion} (${precio.lugar.tipo})
                    </td>
                </g:if>
                <td><g:formatDate date="${precio.fecha}" format="dd-MM-yyyy"/></td>
                <td class="textRight"><g:formatNumber number="${precio.precioUnitario}" maxFractionDigits="2" minFractionDigits="2"/></td>
            </tr>
        </g:each>
    </tbody>
</table>
