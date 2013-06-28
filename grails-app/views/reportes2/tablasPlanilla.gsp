<html>
    <head>
        <title>
            Planilla
        </title>

        <style type="text/css">
        @page {
            size   : 21cm 29.7cm ;  /*width height */
            margin : 1.5cm;
        }

        html {
            font-family : Verdana, Arial, sans-serif;
            font-size   : 8px;
        }

        .hoja {
            width      : 17.5cm;
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

        h3 {
            font-size : 11px;
        }

        table {
            border-collapse : collapse;
            width           : 100%;
            border          : solid 1px black;
        }

        td, th {
            border         : solid 1px black;
            vertical-align : middle !important;
        }

        th {
            background : #bbb;
            font-size  : 10px;
        }

        td {
            font-size : 8px;
        }

        tbody th {
            background : #ECECEC !important;
            color      : #000000 !important;
        }

        .number {
            text-align : right !important;
        }

        .area {
            /*border-bottom : 1px solid black;*/
            padding-left : 50px;
            position     : relative;
            overflow-x   : auto;
            /*min-height    : 150px;*/
        }

        .nb {
            border-left : none !important;
        }

        .bold {
            font-weight : bold;
        }

        .noborder, table.noborder, table.noborder td, table.noborder th {
            border : none !important;
        }

        .tal {
            text-align : left !important;
        }

        #list-grupo {
            page-break-before : always;
        }
        </style>

    </head>

    <body>
        <div class="hoja">
            <elm:headerPlanillaReporte planilla="${planilla}"/>

            <div class="span12" role="main" style="margin-top: 10px;margin-left: -10px">
                <div class="area">
                    <h2>Cálculo de B<sub>0</sub></h2>
                    ${tablaB0}
                </div>

                <div class="area">
                    <h2>Cálculo de P<sub>0</sub></h2>
                    ${tablaP0}
                </div> <!-- P0 -->

                <div class="area" style="min-height: 190px; margin-bottom: 10px;">
                    <h2>Cálculo de F<sub>r</sub> y P<sub>r</sub></h2>
                    ${tablaFr}
                </div> <!-- Fr y Pr -->

                <g:if test="${planilla.tipoPlanilla.codigo != 'A'}">
                    <div class="area" style="min-height: 190px; margin-bottom: 10px;">
                        <h2>Multas</h2>

                        <h3>Multa por no presentación de planilla</h3>
                        ${pMl}
                        <h3>Multa por retraso de obra</h3>
                        ${tablaMl}
                    </div> <!-- Multas -->
                </g:if>
            </div>
            <g:if test="${imprimeDetalle == '1'}">
                <div id="list-grupo" class="" role="main" style="width: 650px;">
                    <h2>Detalle</h2>
                    <table class="table table-bordered table-striped table-condensed table-hover">
                        <thead>
                            <tr>
                                <th rowspan="2">N.</th>
                                <th rowspan="2">Descripción del rubro</th>
                                <th rowspan="2" class="borderLeft">U.</th>
                                <th rowspan="2">Precio unitario</th>
                                <th rowspan="2">Volumen contrat.</th>
                                <th colspan="3" class="borderLeft">Cantidades</th>
                                <th colspan="3" class="borderLeft">Valores</th>
                            </tr>
                            <tr>
                                <th class="borderLeft">Anterior</th>
                                <th>Actual</th>
                                <th>Acumulado</th>
                                <th class="borderLeft">Anterior</th>
                                <th>Actual</th>
                                <th>Acumulado</th>
                            </tr>
                        </thead>
                        <tbody id="tbDetalle">
                            <g:set var="totalAnterior" value="${0}"/>
                            <g:set var="totalActual" value="${0}"/>
                            <g:set var="totalAcumulado" value="${0}"/>

                            <g:set var="sp" value="null"/>
                            <g:each in="${detalle}" var="vol">
                                <g:set var="det" value="${janus.ejecucion.DetallePlanilla.findByPlanillaAndVolumenObra(planilla, vol)}"/>
                                <g:set var="anteriores" value="${janus.ejecucion.DetallePlanilla.findAllByPlanillaInListAndVolumenObra(planillasAnteriores, vol)}"/>

                                <g:set var="cantAnt" value="${anteriores.sum { it.cantidad } ?: 0}"/>
                                <g:set var="valAnt" value="${anteriores.sum { it.monto } ?: 0}"/>
                                <g:set var="cant" value="${det?.cantidad ?: 0}"/>
                                <g:set var="val" value="${det?.monto ?: 0}"/>

                                <g:set var="totalAnterior" value="${totalAnterior + valAnt}"/>
                                <g:set var="totalActual" value="${totalActual + val.toDouble().round(2)}"/>
                                <g:set var="totalAcumulado" value="${totalAcumulado + val.toDouble().round(2) + valAnt}"/>

                                <g:if test="${sp != vol.subPresupuestoId}">
                                    <tr>
                                        <th colspan="2">
                                            ${sp} ${vol.subPresupuestoId} ${vol.subPresupuesto.descripcion}
                                        </th>
                                        <td colspan="3" class="espacio borderLeft"></td>
                                        <td colspan="3" class="espacio borderLeft"></td>
                                        <td colspan="3" class="espacio borderLeft"></td>
                                    </tr>
                                    <g:set var="sp" value="${vol.subPresupuestoId}"/>
                                </g:if>
                                <tr data-id="${det ? det.id : 'nuevo'}" data-vol="${vol.id}" data-cant="${cant}" data-val="${val}" data-canto="${cant}" data-valo="${val}" data-valacu="${val + valAnt}">
                                    <td class="codigo">
                                        ${vol.item.codigo}
                                    </td>
                                    <td class="nombre">
                                        ${vol.item.nombre}
                                    </td>
                                    <td style="text-align: center" class="unidad borderLeft">
                                        ${vol.item.unidad.codigo}
                                    </td>
                                    <td class="num precioU" data-valor="${precios[vol.id.toString()]}">
                                        <elm:numero number="${precios[vol.id.toString()]}" cero="hide"/>
                                    </td>
                                    <td class="num cantidad" data-valor="${vol.cantidad}">
                                        <elm:numero number="${vol.cantidad}" cero="hide"/>
                                    </td>


                                    <td class="ant num cant borderLeft" id="ant_${vol.id}_${planilla.id}" data-valor="${cantAnt}" data-valoro="${cantAnt}">
                                        <elm:numero number="${cantAnt}" cero="hide"/>
                                    </td>

                                    <td class="act num cant" data-valor="${cant}">

                                        <elm:numero number="${cant}" cero="hide"/>

                                    </td>
                                    <td class="acu num cant" id="acu_${vol.id}_ ${planilla.id}" data-valor="${cant + cantAnt}" data-valoro="${cant + cantAnt}">
                                        <elm:numero number="${cant + cantAnt}" cero="hide"/>
                                    </td>

                                    <td class="ant num val borderLeft" data-valor="${valAnt}" data-valoro="${valAnt}">
                                        <elm:numero number="${valAnt}" cero="hide"/>
                                    </td>
                                    <td class="act num val" data-valor="${val}" data-valoro="${val}">
                                        <elm:numero number="${val}" cero="hide"/>
                                    </td>
                                    <td class="acu num val" data-valor="${val + valAnt}" data-valoro="${val + valAnt}">
                                        <elm:numero number="${val + valAnt}" cero="hide"/>
                                    </td>
                                </tr>
                            </g:each>
                        </tbody>
                        <tfoot>
                            <tr style="font-size: smaller">
                                <td colspan="5" class="borderTop">
                                    <b>OBSERVACIONES:</b>
                                </td>
                                <td colspan="3" class="espacio borderLeft borderTop">
                                    <b>A) TOTAL AVANCE DE OBRA</b>
                                </td>
                                <td class="borderLeft borderTop num totalAnt" data-valor="${totalAnterior}" data-valoro="${totalAnterior}" style="font-size: larger">
                                    <elm:numero number="${totalAnterior}" cero="hide"/>
                                </td>
                                <td class="borderTop num totalAct" data-valor="${totalActual}" data-valoro="${totalActual}" style="font-size: larger">
                                    <elm:numero number="${totalActual}" cero="hide"/>
                                </td>
                                <td class="borderTop num totalAcu" data-valor="${totalAcumulado}" data-valoro="${totalAcumulado}" data-max="${contrato.monto}" style="font-size: larger">
                                    <elm:numero number="${totalAcumulado}" cero="hide"/>
                                </td>
                            </tr>
                        </tfoot>
                    </table>
                </div>
            </g:if>

        </div>
    </body>
</html>