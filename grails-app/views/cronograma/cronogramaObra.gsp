<!doctype html>
<html>
    <head>
        <meta name="layout" content="main">
        <title>Cronograma</title>


        <style type="text/css">
        .item_row {
            background : #999999;
        }

        .item_prc {
            background : #C0C0C0;
        }

        .item_f {
            background : #C9C9C9;
        }

        td {
            vertical-align : middle !important;
        }
        </style>

    </head>

    <body>
        <div class="tituloTree">
            Cronograma de ${obra.descripcion} (${obra.plazoEjecucionMeses} mes${obra.plazoEjecucionMeses == 1 ? "" : "es"})
        </div>

        <div class="btn-toolbar">

            <div class="btn-group">
                <a href="#" class="btn disabled" id="btnLimpiarRubro">
                    <i class="icon-check-empty"></i>
                    Limpiar Rubro
                </a>
                <a href="#" class="btn" id="btnLimpiarCronograma">
                    <i class="icon-th-large"></i>
                    Limpiar Cronograma
                </a>
                <a href="#" class="btn" id="btnDeleteCronograma">
                    <i class="icon-trash"></i>
                    Eliminar Cronograma
                </a>
            </div>

            <div class="btn-group">
                <a href="#" class="btn" id="btnGrafico">
                    <i class="icon-bar-chart"></i>
                    Gráfico de avance
                </a>
                <a href="#" class="btn" id="btnXls">
                    <i class="icon-table"></i>
                    Exportar a Excel
                </a>
            </div>
        </div>

        <table class="table table-bordered table-condensed table-hover">
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
                    <th>
                        T.
                    </th>
                    <g:each in="${0..obra.plazoEjecucionMeses - 1}" var="i">
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

                <g:each in="${detalle}" var="vol" status="i">

                    <tr class="item_row" id="${vol.id}" item="${vol.item.id}" sub="${vol.subPresupuesto.id}">
                        <td>
                            ${vol.item.codigo}
                        </td>
                        <td>
                            ${vol.item.nombre}
                        </td>
                        <td style="text-align: center">
                            ${vol.item.unidad.codigo}
                        </td>
                        <td style="text-align: right">
                            <g:formatNumber number="${vol.cantidad}" format="##,##0" minFractionDigits="2" maxFractionDigits="2" locale="ec"/>
                        </td>
                        <td style="text-align: right">
                            <g:formatNumber number="${precios[vol.id.toString()]}" format="##,##0" minFractionDigits="2" maxFractionDigits="2" locale="ec"/>
                        </td>
                        <td style="text-align: right">
                            <g:formatNumber number="${precios[vol.id.toString()] * vol.cantidad}" format="##,##0" minFractionDigits="2" maxFractionDigits="2" locale="ec"/>
                        </td>
                        <td>
                            $
                        </td>
                        <g:each in="${0..obra.plazoEjecucionMeses - 1}" var="j">
                            <td class="dol mes${j + 1}" data-mes="${j + 1}" data-rubro="${vol.id}">

                            </td>
                        </g:each>
                        <td class="num">
                            0.00 $
                        </td>
                    </tr>

                    <tr class="item_prc">
                        <td colspan="6">
                            &nbsp
                        </td>
                        <td>
                            %
                        </td>
                        <g:each in="${0..obra.plazoEjecucionMeses - 1}" var="k">
                            <td class="prct mes${k + 1}" data-mes="${k + 1}" data-rubro="${vol.id}">

                            </td>
                        </g:each>
                        <td class="num">
                            0.00 %
                        </td>
                    </tr>

                    <tr class="item_f">
                        <td colspan="6">
                            &nbsp
                        </td>
                        <td>
                            F
                        </td>
                        <g:each in="${0..obra.plazoEjecucionMeses - 1}" var="l">
                            <td class="fis mes${l + 1}" data-mes="${l + 1}" data-rubro="${vol.id}">

                            </td>
                        </g:each>
                        <td class="num">
                            0.00 F
                    </tr>

                </g:each>
            </tbody>
            <tfoot>
                <tr>
                    <td></td>
                    <td colspan="4">TOTAL PARCIAL</td>
                    <td></td>
                    <td>T</td>
                    <td>0.00</td>
                    <td>0.00</td>
                    <td></td>
                </tr>
                <tr>
                    <td></td>
                    <td colspan="4">TOTAL ACUMULADO</td>
                    <td></td>
                    <td>T</td>
                    <td>0.00</td>
                    <td>0.00</td>
                    <td></td>
                </tr>
            </tfoot>
        </table>

        <div class="modal hide fade" id="modal-cronograma">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal">×</button>

                <h3 id="modalTitle"></h3>
            </div>

            <div class="modal-body" id="modalBody">
            </div>

            <div class="modal-footer" id="modalFooter">
            </div>
        </div>

        <script type="text/javascript">
            $(function () {
                $(".disabled").click(function() {
                    return false;
                })
            });
        </script>

    </body>
</html>