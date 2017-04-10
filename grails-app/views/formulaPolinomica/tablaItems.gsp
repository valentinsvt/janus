<%--
  Created by IntelliJ IDEA.
  User: gato
  Date: 10/04/17
  Time: 12:00
--%>

<div class="contenedorTabla">
    <table class="table table-condensed table-bordered table-striped table-hover" id="tblDisponibles">
        <thead>
        <tr>
            <th>Item</th>
            <th>Descripción</th>
            ${tipo == 'c' ? '<th>Precio unitario</th>' : ''}
            <th>Aporte</th>
        </tr>
        </thead>
        <tbody>
        <g:each in="${rows}" var="r">
            <tr data-item="${r.iid}" data-codigo="${r.codigo}" data-nombre="${r.item}" data-valor="${r.aporte ?: 0}" data-precio="${r.precio ?: 0}" data-grupo="${r.grupo}">
                <td>
                    ${r.codigo}
                </td>
                <td>
                    ${r.item}
                </td>
                <g:if test="${tipo == 'c'}">
                    <td class="numero">
                        <g:formatNumber number="${r.precio ?: 0}" maxFractionDigits="5" minFractionDigits="5" locale='ec'/>
                    </td>
                </g:if>
                <td class="numero">
                    <g:formatNumber number="${r.aporte ?: 0}" maxFractionDigits="5" minFractionDigits="5" locale='ec'/>
                </td>
            </tr>
        </g:each>
        </tbody>
    </table>
</div>