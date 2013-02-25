<%@ page contentType="text/html;charset=UTF-8" %>
<html>
    <head>
        <title>Planilla</title>
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

        table {
            border-collapse : collapse;
            width           : 100%;
            border          : solid 1px black;
        }

        td, th {
            border : solid 1px black;
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

        th {
            vertical-align : middle !important;
        }

        tbody th {
            background : #ECECEC !important;
            color      : #000000 !important;
        }

        .number, .num {
            text-align : right !important;
        }

        .area {
            /*border-bottom : 1px solid black;*/
            /*padding-left  : 50px;*/
            position   : relative;
            overflow-x : auto;
            min-height : 150px;
        }

        .nb {
            border-left : none !important;
        }

        .bold {
            font-weight : bold;
        }

        .row {
            margin-left : -20px;
        }

        .span5 {
            width : 250px;
            float : left;
        }

        .span3 {
            width : 80px;
            float : left;
        }

        .span2 {
            width : 70px;
            float : left;
        }

        .span1 {
            width : 60px;
            float : left;
        }

        .well {
            min-height       : 20px;
            padding          : 19px;
            margin-bottom    : 20px;
            background-color : #f5f5f5;
            border           : 1px solid #e3e3e3;
        }

        .pago {
            height : 80px;
        }

        .noborder, table.noborder, table.noborder td, table.noborder th {
            border : none !important;
        }

        .borderLeft {
            border-left : double 3px !important;
        }

        .borderTop {
            border-top : double 3px !important;
        }
        </style>
    </head>

    <body>
        <div class="hoja">
            <elm:headerPlanillaReporte planilla="${planilla}"/>

            <div class="pago">

                <div class="span12">
                    <div class="span3" style="font-weight: bold">
                        <g:if test="${planilla.tipoPlanilla.codigo == 'A'}">
                            ${planilla.contrato?.porcentajeAnticipo} % de Anticipo
                        </g:if>
                        <g:else>
                            Valor Planilla
                        </g:else>
                    </div>

                    <div class="span3">
                        <elm:numero number="${planilla?.valor}"/>
                    </div>
                </div>

                <div class="span12" style="margin-top: 10px">
                    <div class="span3" style="font-weight: bold">
                        (+) Reajuste provisional${planilla.tipoPlanilla.codigo == 'A' ? 'del anticipo' : ''}
                    </div>

                    <div class="span3">
                        <elm:numero number="${planilla?.reajuste}"/>
                    </div>
                </div>

                <div class="span12" style="margin-top: 10px">
                    <div class="span3" style="font-weight: bold">
                        SUMA:
                    </div>

                    <div class="span3">
                        <elm:numero number="${planilla?.valor + planilla?.reajuste}"/>
                    </div>
                </div>

                <div class="span12" style="margin-top: 10px; margin-bottom: 20px">
                    <div class="span3" style="font-weight: bold">
                        A FAVOR DEL CONTRATISTA:
                    </div>

                    <div class="span3">
                        <elm:numero number="${planilla?.valor + planilla?.reajuste}"/>
                    </div>
                </div>
            </div>

            <g:if test="${planilla.tipoPlanilla.codigo != 'A'}">
                <div class="area">
                    <h2>Detalle planilla</h2>

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
                                <g:set var="totalActual" value="${totalActual + val}"/>
                                <g:set var="totalAcumulado" value="${totalAcumulado + val + valAnt}"/>

                                <g:if test="${sp != vol.subPresupuestoId}">
                                    <tr>
                                        <th colspan="2">
                                            ${vol.subPresupuesto.descripcion}
                                        </th>
                                        <td colspan="3" class="espacio borderLeft"></td>
                                        <td colspan="3" class="espacio borderLeft"></td>
                                        <td colspan="3" class="espacio borderLeft"></td>
                                    </tr>
                                    <g:set var="sp" value="${vol.subPresupuestoId}"/>
                                </g:if>
                                <tr>
                                    <td class="codigo">
                                        ${vol.item.codigo}
                                    </td>
                                    <td class="nombre">
                                        ${vol.item.nombre.replaceAll("<", " menor que ").replaceAll(">", " mayor que ")}
                                    </td>
                                    <td style="text-align: center" class="unidad borderLeft">
                                        ${vol.item.unidad.codigo}
                                    </td>
                                    <td class="num precioU">
                                        <elm:numero number="${precios[vol.id.toString()]}" cero="hide"/>
                                    </td>
                                    <td class="num cantidad">
                                        <elm:numero number="${vol.cantidad}" cero="hide"/>
                                    </td>

                                    <td class="ant num cant borderLeft">
                                        <elm:numero number="${cantAnt}" cero="hide"/>
                                    </td>
                                    <td class="act num cant">
                                        <elm:numero number="${cant}" cero="hide"/>
                                    </td>
                                    <td class="acu num cant">
                                        <elm:numero number="${cant + cantAnt}" cero="hide"/>
                                    </td>
                                    <td class="ant num val borderLeft">
                                        <elm:numero number="${valAnt}" cero="hide"/>
                                    </td>
                                    <td class="act num val">
                                        <elm:numero number="${val}" cero="hide"/>
                                    </td>
                                    <td class="acu num val">
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
                                <td class="borderLeft borderTop num totalAnt">
                                    <elm:numero number="${totalAnterior}" cero="hide"/>
                                </td>
                                <td class="borderTop num totalAct">
                                    <elm:numero number="${totalActual}" cero="hide"/>
                                </td>
                                <td class="borderTop num totalAcu">
                                    <elm:numero number="${totalAcumulado}" cero="hide"/>
                                </td>
                            </tr>
                        </tfoot>
                    </table>
                </div>
            </g:if>

            <div class="area">
                <h2>Cálculo de B<sub>0</sub></h2>
                <table border="1" style="width: ${150 * periodos.size() + 150}px">
                    <thead>
                        <tr>
                            <th colspan="2">Cuadrilla Tipo</th>
                            <th>Oferta</th>
                            <th class="nb">${oferta.fechaEntrega.format("MMM-yy")}</th>
                            <th>Variación</th>
                            <th class="nb">Anticipo <br/>${planilla.fechaPresentacion.format("MMM-yy")}</th>
                            <g:if test="${periodos.size() > 2}">
                                <g:each in="${2..periodos.size() - 1}" var="per">
                                    <th>Variación</th>
                                    <th class="nb">${periodos[per].fechaInicio.format("MMM-yy")}</th>
                                </g:each>
                            </g:if>
                        </tr>
                    </thead>
                    ${tbodyB0}
                </table>

            </div>

            <div class="area" style="min-height: 190px; margin-bottom: 30px; border: none;">
                <h2>Cálculo de F<sub>r</sub> y P<sub>r</sub></h2>
                <table border="1" style="width: ${150 * periodos.size() + 150}px; margin-top: 10px; margin-bottom: 10px;">
                    <thead>
                        <tr>
                            <th rowspan="2">Componentes</th>
                            <th>Oferta</th>
                            <th colspan="${periodos.size() - 1}">Periodo de variación y aplicación de fórmula polinómica</th>
                        </tr>
                        <tr>
                            <th>${oferta.fechaEntrega.format("MMM-yy")}</th>
                            <th>Anticipo <br/>${planilla.fechaPresentacion.format("MMM-yy")}</th>
                            <g:if test="${periodos.size() > 2}">
                                <g:each in="${2..periodos.size() - 1}" var="per">
                                    <th rowspan="2">${periodos[per].fechaInicio.format("MMM-yy")}</th>
                                </g:each>
                            </g:if>
                        </tr>
                        <tr>
                            <th>Anticipo</th>
                            <th>
                                <elm:numero number="${contrato.porcentajeAnticipo}" decimales="0"/>%
                            </th>
                            <th>Anticipo</th>
                        </tr>
                    </thead>
                    ${tbodyFr}
                </table>

            </div>

        </div>
    </body>
</html>