<table>
    <tbody>

    <tr>
        <td class="label">
            <g:message code="canton.provincia.label"
                       default="Provincia"/>

        </td>
        <td class="campo">
            %{--<g:link class="linkArbol" tipo="provincia_${cantonInstance.provincia.id}" controller="provincia" action="show"--}%
                    %{--id="${cantonInstance?.provincia?.id}">--}%
                ${cantonInstance?.provincia?.nombre?.encodeAsHTML()}
            %{--</g:link>--}%
        </td> <!-- campo -->
    </tr>

    <tr>
        <td class="label">
            <g:message code="canton.numero.label"
                       default="Número de cantón:"/>
        </td>
        <td class="campo">
            ${fieldValue(bean: cantonInstance, field: "numero")}
        </td> <!-- campo -->
    </tr>

    <tr>
        <td class="label">
            <g:message code="canton.nombre.label"
                       default="Nombre del cantón:"/>
        </td>
        <td class="campo">
            ${fieldValue(bean: cantonInstance, field: "nombre")}
        </td> <!-- campo -->
    </tr>

    </tbody>
</table>