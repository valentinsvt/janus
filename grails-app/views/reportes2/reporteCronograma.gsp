<%@ page contentType="text/html;charset=UTF-8" %>
<html>
    <head>
        <title>Cronograma</title>

        <style type="text/css">
        @page {
            size   : 29.7cm 21cm ;  /*width height */
            margin : 1.5cm;
        }

        html {
            font-family : Verdana, Arial, sans-serif;
            font-size   : 8px;
        }

        .hoja {
            /*width      : 17.5cm;*/
            /* si es hoja vertical */
            width      : 25.5cm; /* si es hoja horizontal */
            /*background : #ffebcd;*/
            /*border     : solid 1px black;*/
            min-height : 200px;
        }

        h1, h2, h3 {
            text-align : center;
        }

        h1 {
            font-size : 14px;
        }

        h2 {
            font-size : 12px;
        }

        table {
            border-collapse : collapse;
            width           : 100%;
        }

        th, td {
            vertical-align : middle;
        }

        th {
            background : #bbb;
            font-size  : 10px;
        }

        td {
            font-size : 8px;
        }

        .even {
            background : #ddd;
        }

        .odd {
            background : #efefef;
        }

        .left {
            float : left;
        }

        .right {
            float : right;
        }

        .strong {
            font-weight : bold;
        }

        table {
            border-collapse : collapse;
        }

        .tright {
            text-align : right;
        }

        .tcenter {
            text-align : center;
        }

        .item_f td {
            border-bottom : solid 2px;
        }

        td {
            vertical-align : middle !important;
        }

        .num {
            text-align : right;
        }
        </style>

    </head>

    <body>
        <div class="hoja">
            <h1>SEP - G.A.D. PROVINCIA DE PICHINCHA</h1>

            <h2>Cronograma</h2>

            <div style="height: 30px;">
                <div class="left strong">Obra: ${obra.descripcion} (${meses} mes${meses == 1 ? "" : "es"})</div>

                <div class="right">Fecha consulta: <g:formatDate date="${new Date()}" format="dd-MM-yyyy"/></div>
            </div>

            <g:set var="sum" value="${0}"/>
            <table border="1">
                <thead>
                    <tr>
                        <th>
                            Código
                        </th>
                        <th>
                            Rubro
                        </th>
                        <th>
                            Unidad
                        </th>
                        <th>
                            Cantidad
                        </th>
                        <th>
                            Unitario
                        </th>
                        <th>
                            C.Total
                        </th>
                        %{--<th>--}%
                        %{--Días--}%
                        %{--</th>--}%
                        <th>
                            T.
                        </th>
                        <g:each in="${0..meses - 1}" var="i">
                            <th>
                                Mes ${i + 1}
                            </th>
                        </g:each>
                        <th>
                            Total Rubro
                        </th>
                    </tr>
                </thead>
                <tbody id="tabla_material">

                    <g:set var="totalMes" value="${[]}"/>

                    <g:each in="${detalle}" var="vol" status="s">

                        <g:set var="cronos" value="${janus.Cronograma.findAllByVolumenObra(vol)}"/>

                        <g:set var="totalDolRow" value="${0}"/>
                        <g:set var="totalPrcRow" value="${0}"/>
                        <g:set var="totalCanRow" value="${0}"/>

                        <tr class="item_row" id="${vol.id}" data-id="${vol.id}">
                            <td class="codigo">
                                ${vol.item.codigo}
                            </td>
                            <td class="nombre">
                                ${vol.item.nombre}
                            </td>
                            <td style="text-align: center" class="unidad">
                                ${vol.item.unidad.codigo}
                            </td>
                            <td class="num cantidad" data-valor="${vol.cantidad}">
                                <g:formatNumber number="${vol.cantidad}" format="##,##0" minFractionDigits="2" maxFractionDigits="2" locale="ec"/>
                            </td>
                            <td class="num precioU" data-valor="${precios[vol.id.toString()]}">
                                <g:formatNumber number="${precios[vol.id.toString()]}" format="##,##0" minFractionDigits="2" maxFractionDigits="2" locale="ec"/>
                            </td>
                            <g:set var="parcial" value="${precios[vol.id.toString()] * vol.cantidad}"/>
                            <td class="num subtotal" data-valor="${parcial}">
                                <g:formatNumber number="${parcial}" format="##,##0" minFractionDigits="2" maxFractionDigits="2" locale="ec"/>
                                <g:set var="sum" value="${sum + parcial}"/>
                            </td>
                            %{--<td style="text-align: center">--}%
                            %{--<span style="color:#008"><g:formatNumber number="${vol.dias}" maxFractionDigits="1" minFractionDigits="1" locale="ec"/></span>--}%
                            %{--</td>--}%
                            <td>
                                $
                            </td>
                            <g:each in="${0..meses - 1}" var="i">
                                <g:set var="prec" value="${cronos.find { it.periodo == i + 1 }}"/>
                                <td class="dol mes num mes${i + 1} rubro${vol.id}" data-mes="${i + 1}" data-rubro="${vol.id}" data-valor="0"
                                    data-tipo="dol" data-val="${prec?.precio ?: 0}" data-id="${prec?.id ?: ''}">
                                    <g:set var="totalDolRow" value="${prec ? totalDolRow + prec?.precio ?: 0 : totalDolRow}"/>
                                    %{--<g:if test="${!totalMes[i]}">--}%
                                        %{--${totalMes[i] = 0}--}%
                                    %{--</g:if>--}%
                                    %{--${totalMes[i] = prec ? totalMes[i] + prec?.precio ?: 0 : totalMes}--}%
                                    <g:formatNumber number="${prec?.precio}" format="##,##0" minFractionDigits="2" maxFractionDigits="2" locale="ec"/>
                                </td>
                            </g:each>
                            <td class="num rubro${vol.id} dol total totalRubro">
                                <span>
                                    totalDol
                                    %{--<g:formatNumber number="${totalDolRow}" format="##,##0" minFractionDigits="2" maxFractionDigits="2" locale="ec"/>--}%
                                </span> $
                            </td>
                        </tr>

                        <tr class="item_prc" data-id="${vol.id}">
                            %{--<td colspan="7">--}%
                            <td colspan="6">

                            </td>
                            <td>
                                %
                            </td>
                            <g:each in="${0..meses - 1}" var="i">
                                <g:set var="porc" value="${cronos.find { it.periodo == i + 1 }}"/>
                                <td class="prct mes num mes${i + 1} rubro${vol.id}" data-mes="${i + 1}" data-rubro="${vol.id}" data-valor="0"
                                    data-tipo="prct" data-val="${porc?.porcentaje ?: 0}" data-id="${porc?.id ?: ''}">
                                    %{--<g:set var="totalPrcRow" value="${porc ? totalPrcRow + porc : totalPrcRow}"/>--}%
                                    <g:formatNumber number="${porc?.porcentaje}" format="##,##0" minFractionDigits="2" maxFractionDigits="2" locale="ec"/>
                                </td>
                            </g:each>
                            <td class="num rubro${vol.id} prct total totalRubro">
                                <span>
                                    totalPrct
                                    %{--<g:formatNumber number="${totalPrcRow}" format="##,##0" minFractionDigits="2" maxFractionDigits="2" locale="ec"/>--}%
                                </span> %
                            </td>
                        </tr>

                        <tr class="item_f" data-id="${vol.id}">
                            %{--<td colspan="7">--}%
                            <td colspan="6">

                            </td>
                            <td>
                                F
                            </td>
                            <g:each in="${0..meses - 1}" var="i">
                                <g:set var="cant" value="${cronos.find { it.periodo == i + 1 }}"/>
                                <td class="fis mes num mes${i + 1} rubro${vol.id}" data-mes="${i + 1}" data-rubro="${vol.id}" data-valor="0"
                                    data-tipo="fis" data-val="${cant?.cantidad ?: 0}" data-id="${cant?.id ?: ''}">
                                    %{--<g:set var="totalCanRow" value="${cant ? totalCanRow + cant : totalCanRow}"/>--}%
                                    <g:formatNumber number="${cant?.cantidad}" format="##,##0" minFractionDigits="2" maxFractionDigits="2" locale="ec"/>
                                </td>
                            </g:each>
                            <td class="num rubro${vol.id} fis total totalRubro">
                                <span>
                                    totalFis
                                    %{--<g:formatNumber number="${totalCanRow}" format="##,##0" minFractionDigits="2" maxFractionDigits="2" locale="ec"/>--}%
                                </span> F
                            </td>
                        </tr>

                    </g:each>
                </tbody>
                <tfoot>
                    <tr>
                        <td></td>
                        <td colspan="4">TOTAL PARCIAL</td>
                        <td class="num">
                            <g:formatNumber number="${sum}" format="##,##0" minFractionDigits="2" maxFractionDigits="2" locale="ec"/>
                        </td>
                        %{--<td></td>--}%
                        <td>T</td>
                        <g:each in="${0..meses - 1}" var="i">
                            <td class="num mes${i + 1} totalParcial total" data-mes="${i + 1}" data-valor="0">
                                %{--${totalMes[i]}--}%
                            </td>
                        </g:each>
                        <td></td>
                    </tr>
                    <tr>
                        <td></td>
                        <td colspan="4">TOTAL ACUMULADO</td>
                        <td></td>
                        %{--<td></td>--}%
                        <td>T</td>
                        <g:each in="${0..meses - 1}" var="i">
                            <td class="num mes${i + 1} totalAcumulado total" data-mes="${i + 1}" data-valor="0">
                                0.00
                            </td>
                        </g:each>
                        <td></td>
                    </tr>
                    <tr>
                        <td></td>
                        <td colspan="4">% PARCIAL</td>
                        %{--<td></td>--}%
                        <td></td>
                        <td>T</td>
                        <g:each in="${0..meses - 1}" var="i">
                            <td class="num mes${i + 1} prctParcial total" data-mes="${i + 1}" data-valor="0">
                                0.00
                            </td>
                        </g:each>
                        <td></td>
                    </tr>
                    <tr>
                        <td></td>
                        <td colspan="4">% ACUMULADO</td>
                        <td></td>
                        %{--<td></td>--}%
                        <td>T</td>
                        <g:each in="${0..meses - 1}" var="i">
                            <td class="num mes${i + 1} prctAcumulado total" data-mes="${i + 1}" data-valor="0">
                                0.00
                            </td>
                        </g:each>
                        <td></td>
                    </tr>
                </tfoot>
            </table>

        </div>
    </body>
</html>