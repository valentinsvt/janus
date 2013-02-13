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

        <div class="row" style="margin-bottom: 10px;">
            <div class="span9 btn-group" role="navigation">
                <g:link controller="planilla" action="list" params="[id: contrato?.id]" class="btn btn-ajax btn-new" title="Regresar a las planillas del contrato">
                    <i class="icon-arrow-left"></i>
                    Regresar
                </g:link>
            </div>

            <div class="span3" id="busqueda-Planilla"></div>
        </div>

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

                        <td class="ant num" id="ant_${vol.id}_${planilla.id}">
                            0.00
                        </td>
                        <td>
                            <g:textField name="val_${vol.id}_${planilla.id}" class="input-mini number"/>
                        </td>
                        <td class="acu num" id="acu_${vol.id}_ ${planilla.id}">
                            0.00
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

        <script type="text/javascript">
            function validarNum(ev) {
                /*
                 48-57      -> numeros
                 96-105     -> teclado numerico
                 188        -> , (coma)
                 190        -> . (punto) teclado
                 110        -> . (punto) teclado numerico
                 8          -> backspace
                 46         -> delete
                 9          -> tab
                 37         -> flecha izq
                 39         -> flecha der
                 */
                return ((ev.keyCode >= 48 && ev.keyCode <= 57) ||
                        (ev.keyCode >= 96 && ev.keyCode <= 105) ||
                        ev.keyCode == 190 || ev.keyCode == 110 ||
                        ev.keyCode == 8 || ev.keyCode == 46 || ev.keyCode == 9 ||
                        ev.keyCode == 37 || ev.keyCode == 39);
            }

            $(function () {
                $(".number").keydown(function (ev) {
                    // esta parte valida el punto: si empieza con punto le pone un 0 delante, si ya hay un punto lo ignora
                    if (ev.keyCode == 190 || ev.keyCode == 110) {
                        var val = $(this).val();
                        if (val.length == 0) {
                            $(this).val("0");
                        }
                        return val.indexOf(".") == -1;
                    } else {
                        // esta parte valida q sean solo numeros, punto, tab, backspace, delete o flechas izq/der
                        return validarNum(ev);
                    }
                }).keyup(function () {
                            var val = $(this).val();
                            // esta parte valida q no ingrese mas de 2 decimales
                            var parts = val.split(".");
                            if (parts.length > 1) {
                                if (parts[1].length > 2) {
                                    parts[1] = parts[1].substring(0, 2);
                                    var nval = parts[0] + "." + parts[1];
                                    $(this).val(nval);
                                }
                            }
                            // esta parte hace los calculos

                        });
            });
        </script>

    </body>
</html>