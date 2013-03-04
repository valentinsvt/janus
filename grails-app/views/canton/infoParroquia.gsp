<table>
    <tbody>

    <tr>
        <td class="label">
            <g:message code="parroquia.canton.label"
                       default="Canton"/>
        </td>
        <td class="campo">
            %{--<g:link class="linkArbol" tipo="canton_${parroquiaInstance.canton.id}" controller="canton" action="show"--}%
                    %{--id="${parroquiaInstance?.canton?.id}">--}%
                ${parroquiaInstance?.canton?.nombre?.encodeAsHTML()}
            %{--</g:link>--}%
        </td> <!-- campo -->
    </tr>

    <tr>
        <td class="label">
            <g:message code="parroquia.codigo.label"
                       default="Codigo"/>
        </td>
        <td class="campo">
            ${fieldValue(bean: parroquiaInstance, field: "codigo")}
        </td> <!-- campo -->
    </tr>

    <tr>
        <td class="label">
            <g:message code="parroquia.nombre.label"
                       default="Nombre"/>
        </td>
        <td class="campo">
            ${fieldValue(bean: parroquiaInstance, field: "nombre")}
        </td> <!-- campo -->

    </tr>

    </tbody>
</table>