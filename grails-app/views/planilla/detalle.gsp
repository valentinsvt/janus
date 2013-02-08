<%--
  Created by IntelliJ IDEA.
  User: luz
  Date: 2/8/13
  Time: 12:19 PM
  To change this template use File | Settings | File Templates.
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<html>
    <head>
        <meta name="layout" content="main">
        <title>Detalle de planilla</title>

        <style type="text/css">
        th {
            vertical-align : middle !important;
        }

        tbody th {
            background : #5E8E9B !important;
        }

        td {
            vertical-align : middle !important;
        }

        .num {
            text-align : right !important;
            width      : 60px;
            /*background : #c71585 !important;*/
        }
        </style>

    </head>

    <body>

        <elm:headerPlanilla planilla="${planilla}"/>


        <table class="table table-bordered table-striped table-condensed table-hover">
            <thead>
                <tr>
                    <th rowspan="2">N.</th>
                    <th rowspan="2">Descripción del rubro</th>
                    <th rowspan="2">U.</th>
                    <th rowspan="2">Precio unitario</th>
                    <th rowspan="2">Volumen contrat.</th>
                    <th colspan="3">Cantidades</th>
                    <th colspan="3">Valores</th>
                </tr>
                <tr>
                    <th>Anterior</th>
                    <th>Actual</th>
                    <th>Acumulado</th>
                    <th>Anterior</th>
                    <th>Actual</th>
                    <th>Acumulado</th>
                </tr>
            </thead>
            <tbody>
                <g:set var="sp" value="null"/>
                <g:each in="${detalle}" var="vol">
                    <g:set var="detalle" value="${janus.ejecucion.DetallePlanilla.findByPlanillaAndVolumenObra(planilla, vol)}"/>
                    <g:set var="detalleAnt" value="${janus.ejecucion.DetallePlanilla.findByPlanillaAndVolumenObra(anterior, vol)}"/>
                    <g:if test="${sp != vol.subPresupuestoId}">
                        <tr>
                            <th colspan="2">
                                ${vol.subPresupuesto.descripcion}
                            </th>
                            <td colspan="9"></td>
                        </tr>
                        <g:set var="sp" value="${vol.subPresupuestoId}"/>
                    </g:if>
                    <tr>
                        <td class="codigo">
                            ${vol.item.codigo}
                        </td>
                        <td class="nombre">
                            ${vol.item.nombre}
                        </td>
                        <td style="text-align: center" class="unidad">
                            ${vol.item.unidad.codigo}
                        </td>
                        <td class="num precioU" data-valor="${precios[vol.id.toString()]}">
                            <g:formatNumber number="${precios[vol.id.toString()]}" format="##,##0" minFractionDigits="2" maxFractionDigits="2" locale="ec"/>
                        </td>
                        <td class="num cantidad" data-valor="${vol.cantidad}">
                            <g:formatNumber number="${vol.cantidad}" format="##,##0" minFractionDigits="2" maxFractionDigits="2" locale="ec"/>
                        </td>

                        <td>
                            %{--<g:formatNumber number="${detalleAnt?.cantidad}" format="##,##0" minFractionDigits="2" maxFractionDigits="2" locale="ec"/>--}%
                        </td>
                        <td>
                            <g:textField name="val_${vol.id}_${planilla.id}" class="input-mini"/>
                        </td>
                        <td>
                            %{--<g:formatNumber number="${detalle.cantidad + detalleAnt?.cantidad}" format="##,##0" minFractionDigits="2" maxFractionDigits="2" locale="ec"/>--}%
                        </td>

                        <td>
                            %{--<g:formatNumber number="${detalleAnt?.cantidad}" format="##,##0" minFractionDigits="2" maxFractionDigits="2" locale="ec"/>--}%
                        </td>
                        <td>
                            %{--<g:formatNumber number="${detalle.cantidad}" format="##,##0" minFractionDigits="2" maxFractionDigits="2" locale="ec"/>--}%
                        </td>
                        <td>
                            %{--<g:formatNumber number="${detalle.cantidad + detalleAnt?.cantidad}" format="##,##0" minFractionDigits="2" maxFractionDigits="2" locale="ec"/>--}%
                        </td>
                    </tr>
                </g:each>
            </tbody>
        </table>

    </body>
</html>