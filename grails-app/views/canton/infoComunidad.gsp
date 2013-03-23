<table>
    <tbody>

    <tr>
        <td class="label">
            <g:message code="comunidad.parroquia.abel"
                       default="Parroquia"/>

        </td>
        <td class="campo">
            <g:link class="linkArbol" tipo="parroquia_${comunidadInstance.parroquia.id}" controller="parroquia" action="show"
            id="${comunidadInstance?.parroquia?.id}">
            ${comunidadInstance?.parroquia?.nombre?.encodeAsHTML()}
            </g:link>
        </td>
    </tr>

    <tr>
        <td class="label">
            <g:message code="comunidad.numero.label"
                       default="Número"/>
        </td>
        <td class="campo">
            ${fieldValue(bean: comunidadInstance, field: "numero")}
        </td> <!-- campo -->
    </tr>

    <tr>
        <td class="label">
            <g:message code="comunidad.nombre.label"
                       default="Comunidad"/>
        </td>
        <td class="campo">
            ${fieldValue(bean: comunidadInstance, field: "nombre")}
        </td> <!-- campo -->
    </tr>

    </tbody>
</table>