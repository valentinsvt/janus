<table>
    <tbody>

    %{--<tr>--}%
        %{--<td class="label">--}%
            %{--<g:message code="canton.provincia.label"--}%
                       %{--default="Provincia"/>--}%

        %{--</td>--}%
        %{--<td class="campo">--}%
            %{--<g:link class="linkArbol" tipo="provincia_${cantonInstance.provincia.id}" controller="provincia" action="show"--}%
            %{--id="${cantonInstance?.provincia?.id}">--}%
            %{--${cantonInstance?.provincia?.nombre?.encodeAsHTML()}--}%
            %{--</g:link>--}%
        %{--</td> <!-- campo -->--}%
    %{--</tr>--}%

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
                       default="Nombre"/>
        </td>
        <td class="campo">
            ${fieldValue(bean: comunidadInstance, field: "nombre")}
        </td> <!-- campo -->
    </tr>

    </tbody>
</table>