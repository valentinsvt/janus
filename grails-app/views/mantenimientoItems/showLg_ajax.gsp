<div class="tituloTree">Precios de ${item.nombre} en ${lugarNombre}</div>

<div>
    <div class="btn-group pull-left">
        <a href="#" class="btn">
            <i class="icon-money"></i>
            Nuevo Precio
        </a>
        <a href="#" class="btn">
            <i class="icon-copy"></i>
            Copiar Precios
        </a>
        <a href="#" class="btn btn-success">
            <i class="icon-save"></i>
            Guardar
        </a>
    </div>
</div>
<table class="table table-striped table-bordered table-hover table-condensed" id="tablaPrecios">
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
        <g:each in="${precios}" var="precio" status="i">
            <tr>
                <g:if test="${lgar}">
                    <td>
                        ${precio.lugar.descripcion} (${precio.lugar.tipo})
                    </td>
                </g:if>
                <td>
                    <g:formatDate date="${precio.fecha}" format="dd-MM-yyyy"/>
                </td>
                <td class="textRight editable ${i == 0 ? 'selected' : ''}">
                    <g:formatNumber number="${precio.precioUnitario}" maxFractionDigits="5" minFractionDigits="5"/>
                </td>
            </tr>
        </g:each>
    </tbody>
</table>
