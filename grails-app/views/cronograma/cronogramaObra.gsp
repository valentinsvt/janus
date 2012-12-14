<!doctype html>
<html>
    <head>
        <meta name="layout" content="main">


        <link href="${resource(dir: 'js/jquery/plugins/box/css', file: 'jquery.luz.box.css')}" rel="stylesheet">
        <script src="${resource(dir: 'js/jquery/plugins/box/js', file: 'jquery.luz.box.js')}"></script>

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

        .num {
            text-align : right !important;
            width      : 60px;
            /*background : #c71585 !important;*/
        }

        .spinner {
            width : 60px;
        }

        .radio {
            margin : 0 !important;
        }

        .sm {
            margin-bottom : 10px !important;
        }

        .totalRubro {
            width : 75px;
        }

        .item_row.rowSelected {
            background : #75B2DE !important;
        }

        .item_prc.rowSelected {
            background : #84BFEA !important;
        }

        .item_f.rowSelected {
            background : #94CDF7 !important;
        }

        .graf {
            width  : 870px;
            height : 410px;
            /*background : #e6e6fa;*/
        }

            /*.btn {*/
            /*z-index : 9999 !important;*/
            /*}*/

            /*.modal-backdrop {*/
            /*z-index : 9998 !important;*/
            /*}*/

            /*.modal {*/
            /*z-index : 9999 !important;*/
            /*}*/
        </style>

    </head>

    <body>

        <g:set var="meses" value="${obra.plazo}"/>
        <g:set var="sum" value="${0}"/>

        <div class="tituloTree">
            Cronograma de ${obra.descripcion} (${meses} mes${obra.plazoEjecucionMeses == 1 ? "" : "es"})
        </div>

        <div class="btn-toolbar">
            <div class="btn-group">
                <a href="${g.createLink(controller: 'obra',action: 'registroObra',params: [obra:obra?.id])}" class="btn btn-ajax btn-new" id="atras" title="Regresar a la obra">
                    <i class="icon-arrow-left"></i>
                    Regresar
                </a>
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
                    Gráficos de avance
                </a>
            %{--<a href="#" class="btn" id="btnGraficoEco">--}%
            %{--<i class="icon-bar-chart"></i>--}%
            %{--Gráfico de avance económico--}%
            %{--</a>--}%
            %{--<a href="#" class="btn" id="btnGraficoFis">--}%
            %{--<i class="icon-bar-chart"></i>--}%
            %{--Gráfico de avance físico--}%
            %{--</a>--}%
                <g:link action="excel" class="btn" id="${obra.id}">
                    <i class="icon-table"></i>
                    Exportar a Excel
                </g:link>
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

            %{--<g:set var="totalMes" value="${[]}"/>--}%

                <g:each in="${detalle}" var="vol" status="s">

                    <g:set var="cronos" value="${janus.Cronograma.findAllByVolumenObra(vol)}"/>

                %{--<g:set var="totalDolRow" value="${0}"/>--}%
                %{--<g:set var="totalPrcRow" value="${0}"/>--}%
                %{--<g:set var="totalCanRow" value="${0}"/>--}%

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
                        <td>
                            $
                        </td>
                        <g:each in="${0..meses - 1}" var="i">
                            <g:set var="prec" value="${cronos.find { it.periodo == i + 1 }}"/>
                            <td class="dol mes num mes${i + 1} rubro${vol.id}" data-mes="${i + 1}" data-rubro="${vol.id}" data-valor="0"
                                data-tipo="dol" data-val="${prec?.precio ?: 0}" data-id="${prec?.id ?: ''}">
                                %{--<g:set var="totalDolRow" value="${prec ? totalDolRow + prec : totalDolRow}"/>--}%
                                %{--<g:if test="${!totalMes[i]}">--}%
                                %{--${totalMes[i] = 0}--}%
                                %{--</g:if>--}%
                                %{--${totalMes[i] = prec ? totalMes[i] + prec : totalMes}--}%
                                <g:formatNumber number="${prec?.precio}" format="##,##0" minFractionDigits="2" maxFractionDigits="2" locale="ec"/>
                            </td>
                        </g:each>
                        <td class="num rubro${vol.id} dol total totalRubro">
                            <span>
                                %{--<g:formatNumber number="${totalDolRow}" format="##,##0" minFractionDigits="2" maxFractionDigits="2" locale="ec"/>--}%
                            </span> $
                        </td>
                    </tr>

                    <tr class="item_prc" data-id="${vol.id}">
                        <td colspan="6">
                            &nbsp
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
                                %{--<g:formatNumber number="${totalPrcRow}" format="##,##0" minFractionDigits="2" maxFractionDigits="2" locale="ec"/>--}%
                            </span> %
                        </td>
                    </tr>

                    <tr class="item_f" data-id="${vol.id}">
                        <td colspan="6">
                            &nbsp
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


        <div class="modal hide fade" id="modal-cronograma">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal">×</button>

                <h3 id="modalTitle"></h3>
            </div>

            <div class="modal-body" id="modalBody">
                <form class="form-horizontal">
                    <div class="control-group sm">
                        <div>
                            <span id="num-label" class="control-label label label-inverse">
                                Rubro N.
                            </span>
                        </div>

                        <div class="controls">
                            <span aria-labelledby="num-label" id="spanCodigo">

                            </span>
                        </div>
                    </div>

                    <div class="control-group sm">
                        <div>
                            <span id="desc-label" class="control-label label label-inverse">
                                Descripción
                            </span>
                        </div>

                        <div class="controls">
                            <span aria-labelledby="desc-label" id="spanDesc">

                            </span>
                        </div>
                    </div>

                    <div class="control-group sm">
                        <div>
                            <span id="cant-label" class="control-label label label-inverse">
                                Cantidad
                            </span>
                        </div>

                        <div class="controls">
                            <span aria-labelledby="cant-label" id="spanCant">

                            </span>
                        </div>
                    </div>

                    <div class="control-group sm">
                        <div>
                            <span id="precio-label" class="control-label label label-inverse">
                                Precio
                            </span>
                        </div>

                        <div class="controls">
                            <span aria-labelledby="precio-label" id="spanPrecio">

                            </span>
                        </div>
                    </div>

                    <div class="control-group sm">
                        <div>
                            <span id="st-label" class="control-label label label-inverse">
                                Subtotal
                            </span>
                        </div>

                        <div class="controls">
                            <span aria-labelledby="st-label" id="spanSubtotal">

                            </span>
                        </div>
                    </div>
                </form>


                <div class="well">
                    <div class="row" style="margin-bottom: 10px;">
                        <div class="span5">
                            Periodos   <input id="periodosDesde" class="spinner"/> al <input id="periodosHasta" class="spinner" value="${meses}"/>
                        </div>
                    </div>

                    <div class="row">
                        <div class="span5">
                            <input type="radio" class="radio cant" name="tipo" id="rd_cant" value="cant" checked=""/>
                            Cantidad <input type="text" class="input-mini tf" id="tf_cant"/><span class="spUnidad"></span>
                            de <span id="spCant"></span> <span class="spUnidad"></span>
                        </div>
                    </div>

                    <div class="row">
                        <div class="span5">
                            <input type="radio" class="radio prct" name="tipo" id="rd_prct" value="prct"/>
                            Porcentaje <input type="text" class="input-mini tf" id="tf_prct"/>%
                        de <span id="spPrct"></span>%
                        </div>
                    </div>

                    <div class="row">
                        <div class="span5">
                            <input type="radio" class="radio precio" name="tipo" id="rd_precio" value="prct"/>
                            Precio <input type="text" class="input-mini tf" id="tf_precio"/>$
                        de <span id="spPrecio"></span>$
                        </div>
                    </div>
                </div>

            </div>

            <div class="modal-footer" id="modalFooter">
            </div>
        </div>


        <div class="modal longModal tallModal fade hide " id="modal-graf">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal">×</button>

                <h3 id="modalTitle-graf"></h3>
            </div>

            <div class="modal-body" id="modalBody-graf">
                <div class="graf" id="graf"></div>
            </div>

            <div class="modal-footer" id="modalFooter-graf">
            </div>
        </div>


        <script type="text/javascript">

            //            var plot, redraw = false;

            function log(msg) {
                console.log(msg);
            }

            function updateTotales() {

                $("#tabla_material").children("tr").each(function () {
                    var rubro = $(this).data("id");

                    var totalFilaDol = 0;
                    var totalFilaPrct = 0;
                    var totalFilaFis = 0;

                    var $totalFilaDol = $(".total.dol" + ".rubro" + rubro);
                    var $totalFilaPrct = $(".total.prct" + ".rubro" + rubro);
                    var $totalFilaFis = $(".total.fis" + ".rubro" + rubro);

                    //calcular los totales de la fila: del rubro
                    if ($(this).hasClass("item_row")) {
                        $(".dol.rubro" + rubro).not(".total").each(function () {
                            var val = parseFloat($(this).data("val"));
                            if (val && !isNaN(val)) {
                                totalFilaDol += val;
                            }
                        });
                        $totalFilaDol.data("val", totalFilaDol).children("span").first().text(number_format(totalFilaDol, 2, ".", ","));
                    } else if ($(this).hasClass("item_prc")) {
                        $(".prct.rubro" + rubro).not(".total").each(function () {
                            var val = parseFloat($(this).data("val"));
                            if (val && !isNaN(val)) {
                                totalFilaPrct += val;
                            }
                        });
                        $totalFilaPrct.data("val", totalFilaPrct).children("span").first().text(number_format(totalFilaPrct, 2, ".", ","));
                    } else if ($(this).hasClass("item_f")) {
                        $(".fis.rubro" + rubro).not(".total").each(function () {
                            var val = parseFloat($(this).data("val"));
                            if (val && !isNaN(val)) {
                                totalFilaFis += val;
                            }
                        });
                        $totalFilaFis.data("val", totalFilaFis).children("span").first().text(number_format(totalFilaFis, 2, ".", ","));
                    }
                });

                //calcular los totales de la columna: del mes
                var totAcum = 0;
                for (var i = 0; i < parseInt("${meses}") + 1; i++) {
                    var $totalParcial = $(".totalParcial.mes" + i);
                    var $totalAcumulado = $(".totalAcumulado.mes" + i);
                    var $prctParcial = $(".prctParcial.mes" + i);
                    var $prctAcumulado = $(".prctAcumulado.mes" + i);
                    //total: .dol
                    var tot = 0;
                    $(".dol.mes" + i).not(".total").each(function () {
                        var val = parseFloat($(this).data("val"));
                        if (val && !isNaN(val)) {
                            tot += val;
                            totAcum += val;
                        }
                    });

                    var prc = tot * 100 / parseFloat("${sum}");
                    $(".total.mes" + i + ".totalParcial").text(number_format(tot, 2, ".", ",")).data("val", tot);
                    $(".total.mes" + i + ".prctParcial").text(number_format(prc, 2, ".", ",")).data("val", prc);

                    var prcAcum = totAcum * 100 / parseFloat("${sum}");
//                    console.log($(".total.mes" + i + ".totalAcumulado"), totAcum, $(".total.mes" + i + ".prctAcumulado"), prcAcum);
                    $(".total.mes" + i + ".totalAcumulado").text(number_format(totAcum, 2, ".", ",")).data("val", totAcum);
                    $(".total.mes" + i + ".prctAcumulado").text(number_format(prcAcum, 2, ".", ",")).data("val", prcAcum);
                }
            }

            function validar() {
                var periodoIni = $.trim($("#periodosDesde").val());
                var periodoFin = $.trim($("#periodosHasta").val());

                var $cant = $("#tf_cant");
                var $prct = $("#tf_prct");
                var $prec = $("#tf_precio");

                var cant = $.trim($cant.val());
                var prct = $.trim($prct.val());
                var prec = $.trim($prec.val());

                if (periodoIni == "") {
                    log("Ingrese el periodo inicial");
                    return false;
                }
                if (periodoFin == "") {
                    log("Ingrese el periodo final");
                    return false;
                }
                if (cant == "") {
                    log("Ingrese la cantidad, porcentaje o precio");
                    return false;
                }
                if (prct == "") {
                    log("Ingrese el porcentaje, cantidad o precio");
                    return false;
                }
                if (prec == "") {
                    log("Ingrese el precio, cantidad o porcentaje");
                    return false;
                }

                var maxCant = $cant.attr("max");
                var maxPrct = $prct.attr("max");
                var maxPrec = $prec.attr("max");

                try {
                    periodoIni = parseFloat(periodoIni);
                    periodoFin = parseFloat(periodoFin);

                    if (periodoFin < periodoIni) {
                        log("El periodo inicial debe ser inferior al periodo final");
                        return false;
                    }

                    cant = parseFloat(cant);
                    prct = parseFloat(prct);
                    maxCant = parseFloat(maxCant);
                    maxPrct = parseFloat(maxPrct);
                    maxPrec = parseFloat(maxPrec);

                    if (cant > maxCant) {
                        log("La cantidad debe ser menor que " + maxCant);
                        return false;
                    }
                    if (prct > maxPrct) {
                        log("El porcentaje debe ser menor que " + maxPrct);
                        return false;
                    }
                    if (prec > maxPrec) {
                        log("El precio debe ser menor que " + maxPrec);
                        return false;
                    }

                } catch (e) {
                    console.log(e);
                    return false;
                }
                return true;
            }

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
                 */
//        console.log(ev.keyCode);
                return ((ev.keyCode >= 48 && ev.keyCode <= 57) || (ev.keyCode >= 96 && ev.keyCode <= 105) || ev.keyCode == 190 || ev.keyCode == 110 || ev.keyCode == 8 || ev.keyCode == 46 || ev.keyCode == 9);
            }

            $(function () {
                updateTotales();

                $("#tabla_material").children("tr").click(function () {
                    $("#btnLimpiarRubro").removeClass("disabled");
                    $(".rowSelected").removeClass("rowSelected");
                    $(this).addClass("rowSelected");
                    if ($(this).hasClass("item_row")) {
                        $(this).next().addClass("rowSelected").next().addClass("rowSelected");
                    } else if ($(this).hasClass("item_prc")) {
                        $(this).next().addClass("rowSelected");
                        $(this).prev().addClass("rowSelected");
                    } else if ($(this).hasClass("item_f")) {
                        $(this).prev().addClass("rowSelected").prev().addClass("rowSelected");
                    }
                });

                $(".spinner").spinner({
                    min : 1,
                    max :${meses}//,
//                    spin : function (event, ui) {
//                        var val = $(this).val();
//                        if ($(this).attr("id") == "periodosDesde") {
//                            $("#periodosHasta").spinner("option", "min", val);
//                        }
                    /*else if ($(this).attr("id") == "periodosHasta") {
                     $("#periodosDesde").spinner("option", "max", val - 1);
                     }*/
//                    }
                }).keydown(function () {
                            return false;
                        });

                $(".disabled").click(function () {
                    return false;
                });

                $(".tf").keydown(function (ev) {
                    return validarNum(ev);
                }).focus(function () {
                            var id = $(this).attr("id");
                            var parts = id.split("_");
                            $("#rd_" + parts[1]).attr("checked", true);
                        });

                $("#tf_cant").keyup(function (ev) {
                    if (validarNum(ev)) {
                        var val = $.trim($(this).val());
                        if (val == "") {
                            $("#tf_prct").val("");
                            $("#tf_precio").val("");
                        } else {
                            try {
                                val = parseFloat(val);
                                var max = parseFloat($(this).data("max"));
                                if (val > max) {
                                    val = max;
                                }
                                var $precio = $("#tf_precio");
                                var total = parseFloat($(this).data("total"));
                                var prct = (val * 100) / total;
                                var dol = $precio.data("max") * (prct / 100);
                                $("#tf_prct").val(number_format(prct, 2, ".", "")).data("val", prct);
                                $precio.val(number_format(dol, 2, ".", "")).data("val", dol);
                                if (ev.keyCode != 110 && ev.keyCode != 190) {
                                    $("#tf_cant").val(val).data("val", val);
                                }
                            } catch (e) {
                                console.log(e);
                            }
                        }
                    }
                });
                $("#tf_prct").keyup(function (ev) {
                    if (validarNum(ev)) {
                        var prct = $.trim($(this).val());
                        if (prct == "") {
                            $("#tf_cant").val("");
                            $("#tf_precio").val("");
                        } else {
                            try {
                                prct = parseFloat(prct);
                                var max = parseFloat($(this).data("max"));
                                if (prct > max) {
                                    prct = max;
                                }
                                var $precio = $("#tf_precio");
                                var $cant = $("#tf_cant");
                                var total = parseFloat($cant.data("total"));
                                var val = (prct / 100) * total;
                                var dol = $precio.data("max") * (prct / 100);
                                $cant.val(number_format(val, 2, ".", "")).data("val", val);
                                $precio.val(number_format(dol, 2, ".", "")).data("val", dol);
                                if (ev.keyCode != 110 && ev.keyCode != 190) {
                                    $("#tf_prct").val(prct).data("val", prct);
                                }
                            } catch (e) {
                                console.log(e);
                            }
                        }
                    }
                });
                $("#tf_precio").keyup(function (ev) {
                    if (validarNum(ev)) {
                        var dol = $.trim($(this).val());
                        if (dol == "") {
                            $("#tf_prct").val("");
                            $("#tf_cant").val("");
                        } else {
                            try {
                                dol = parseFloat(dol);
                                var max = parseFloat($(this).data("max"));
                                if (dol > max) {
                                    dol = max;
                                }
                                var $cant = $("#tf_cant");
                                var total = parseFloat($(this).data("total"));
                                var totalCant = parseFloat($cant.data("total"));
                                var prct = (dol * 100) / total;
                                var cant = dol * totalCant / total;

                                $("#tf_prct").val(number_format(prct, 2, ".", "")).data("val", prct);
                                $cant.val(number_format(cant, 2, ".", "")).data("val", cant);
                                if (ev.keyCode != 110 && ev.keyCode != 190) {
                                    $("#tf_precio").val(dol).data("val", dol);
                                }
                            } catch (e) {
                                console.log(e);
                            }
                        }
                    }
                });

                $(".mes").dblclick(function () {
                    var $celda = $(this);
                    var $tr = $celda.parents("tr");

                    if ($tr.hasClass("item_prc")) {
                        $tr = $tr.prev();
                    } else if ($tr.hasClass("item_f")) {
                        $tr = $tr.prev().prev();
                    }

//                    console.log($tr);

                    var mes = $celda.data("mes");
                    var tipo = $celda.data("tipo");
                    var valor = $celda.data("valor");
                    var rubro = $celda.data("rubro");

                    $("#periodosDesde").val(mes);
                    $("#periodosHasta").val("${meses}");

//                    console.log($celda, mes, tipo, valor, rubro);
//                    console.log($totalFila, $totalParcial, $prctParcial, $prctAcumulado);

                    $("#rd_cant").attr("checked", true);

                    var codigo = $.trim($tr.find(".codigo").text());
                    var desc = $.trim($tr.find(".nombre").text());
                    var cant = $.trim($tr.find(".cantidad").data("valor"));
                    var precio = $.trim($tr.find(".precioU").data("valor"));
                    var subtotal = $.trim($tr.find(".subtotal").data("valor"));
                    var unidad = $.trim($tr.find(".unidad").text());

                    var dolAsignado = $.trim($(".dol.rubro" + rubro).children("span").text());
                    var prctAsignado = $.trim($(".prct.rubro" + rubro).children("span").text());
                    var fAsignado = $.trim($(".fis.rubro" + rubro).children("span").text());

                    var dolRestante = parseFloat(subtotal) - parseFloat(dolAsignado);
                    var prctRestante = 100 - parseFloat(prctAsignado);
                    var cantRestante = parseFloat(cant) - parseFloat(fAsignado);

                    $("#spCant").text(cantRestante);
                    $("#spPrct").text(prctRestante);
                    $("#spPrecio").text(number_format(dolRestante, 2, ".", ","));

                    $("#tf_cant").data({
                        max   : cantRestante,
                        total : cant
                    }).val("");
                    $("#tf_prct").data({
                        max   : prctRestante,
                        total : 100
                    }).val("");
                    $("#tf_precio").data({
                        max   : dolRestante,
                        total : subtotal
                    }).val("");

                    $("#spanCodigo").text(codigo);
                    $("#spanDesc").text(desc);
                    $("#spanCant").text(number_format(cant, 2, ".", ",") + " " + unidad);
                    $("#spanPrecio").text(number_format(precio, 2, ".", ",") + " $");
                    $("#spanSubtotal").text(number_format(subtotal, 2, ".", ",") + " $");
                    $(".spUnidad").text(unidad);

                    var $btnCancel = $('<a href="#" data-dismiss="modal" class="btn">Cerrar</a>');
                    var $btnOk = $('<a href="#" class="btn btn-success">Aceptar</a>');

                    $btnOk.click(function () {
                        if (validar()) {
                            $btnOk.replaceWith(spinner);

                            var dataAjax = "";

                            var periodoIni = parseInt($("#periodosDesde").val());
                            var periodoFin = parseInt($("#periodosHasta").val());

                            var cant = $("#tf_cant").data("val");
                            var prct = $("#tf_prct").data("val");

                            var d, i, dol;

                            if (periodoIni == periodoFin) {
                                dol = subtotal * (prct / 100);
                                $(".dol.mes" + periodoIni + ".rubro" + rubro).text(number_format(dol, 2, ".", ",")).data("val", dol);
                                $(".prct.mes" + periodoIni + ".rubro" + rubro).text(number_format(prct, 2, ".", ",")).data("val", prct);
                                $(".fis.mes" + periodoIni + ".rubro" + rubro).text(number_format(cant, 2, ".", ",")).data("val", cant);
                                dataAjax += "crono=" + rubro + "_" + periodoIni + "_" + dol + "_" + prct + "_" + cant;
                            } else {
                                var meses = periodoFin - periodoIni + 1;
                                dol = subtotal * (prct / 100);
                                dol /= meses;
                                prct /= meses;
                                cant /= meses;
                                for (i = periodoIni; i <= periodoFin; i++) {
                                    $(".dol.mes" + i + ".rubro" + rubro).text(number_format(dol, 2, ".", ",")).data("val", dol);
                                    $(".prct.mes" + i + ".rubro" + rubro).text(number_format(prct, 2, ".", ",")).data("val", prct);
                                    $(".fis.mes" + i + ".rubro" + rubro).text(number_format(cant, 2, ".", ",")).data("val", cant);

                                    dataAjax += "crono=" + rubro + "_" + i + "_" + dol + "_" + prct + "_" + cant + "&";
                                }
                            }
                            $.ajax({
                                type    : "POST",
                                url     : "${createLink(action:'saveCrono_ajax')}",
                                data    : dataAjax,
                                success : function (msg) {
                                    var parts = msg.split("_");
                                    if (parts[0] == "OK") {
                                        parts = parts[1].split(";");
                                        for (i = 0; i < parts.length; i++) {
                                            var p = parts[i].split(":");
                                            var mes = p[0];
                                            var id = p[1];
                                            $(".dol.mes" + mes + ".rubro" + rubro).data("id", id);
                                            $(".prct.mes" + mes + ".rubro" + rubro).data("id", id);
                                            $(".fis.mes" + mes + ".rubro" + rubro).data("id", id);
                                        }
                                        updateTotales();
                                        $("#modal-cronograma").modal("hide");
                                    } else {
                                        console.log("ERROR");
                                    }
                                }
                            });
                        }
                        return false;
                    });

                    $("#modalTitle").html("Registro del Cronograma");

                    $("#modalFooter").html("").append($btnCancel).append($btnOk);
                    $("#modal-cronograma").modal("show");

                });

                $("#btnLimpiarRubro").click(function () {
                    if (!$(this).hasClass("disabled")) {
                        $.box({
                            imageClass : "box_info",
                            text       : "Se limpiará el cronograma del rubro seleccionado, continuar?<br/>Los datos no serán remplazados hasta que haya ingresado datos que los remplacen",
                            title      : "Confirmación",
                            iconClose  : false,
                            dialog     : {
                                resizable : false,
                                draggable : false,
                                buttons   : {
                                    "Aceptar"  : function () {
                                        var id = $(".item_row.rowSelected").data("id");
                                        $(".mes.rubro" + id).text("").data("val", 0);
                                        updateTotales();
                                    },
                                    "Cancelar" : function () {
                                    }
                                }
                            }
                        });
                    }
                });

                $("#btnLimpiarCronograma").click(function () {
                    $.box({
                        imageClass : "box_info",
                        text       : "Se limpiará todo el cronograma, continuar?<br/>Los datos no serán remplazados hasta que haya ingresado datos que los remplacen",
                        title      : "Confirmación",
                        iconClose  : false,
                        dialog     : {
                            resizable : false,
                            draggable : false,
                            buttons   : {
                                "Aceptar"  : function () {
                                    $(".mes").text("").data("val", 0);
                                    updateTotales();
                                },
                                "Cancelar" : function () {
                                }
                            }
                        }
                    });
                });

                $("#btnDeleteCronograma").click(function () {
                    $.box({
                        imageClass : "box_info",
                        text       : "Se eliminará todo el cronograma, continuar?<br/>Los datos serán eliminados inmediatamente, y no se puede deshacer.",
                        title      : "Confirmación",
                        iconClose  : false,
                        dialog     : {
                            resizable : false,
                            draggable : false,
                            buttons   : {
                                "Aceptar"  : function () {
                                    $.ajax({
                                        type    : "POST",
                                        url     : "${createLink(action:'deleteCronograma_ajax')}",
                                        data    : {
                                            obra : ${obra.id}
                                        },
                                        success : function (msg) {
//                                            console.log("Data Saved: " + msg);
                                            $(".mes").text("").data("val", 0);
                                            updateTotales();
                                        }
                                    });
                                },
                                "Cancelar" : function () {
                                }
                            }
                        }
                    });
                });

                $("#btnXls").click(function () {
                });

                $("#btnGrafico").click(function () {
                    var dataEco = "[[";
                    var ticksXEco = "[";
                    var ticksYEco = "[";
                    var maxEco = 0;

                    $(".totalAcumulado.total").each(function () {
                        var mes = $(this).data("mes");
                        ticksXEco += mes + ",";
                        var val = $(this).data("val");
                        ticksYEco += number_format(val, 2, ".", "") + ",";
                        if (val > maxEco) {
                            maxEco = val;
                        }
                        dataEco += "[" + mes + "," + val + "],";
                    });
                    dataEco = dataEco.substr(0, dataEco.length - 1);
                    dataEco += "]]";
                    ticksXEco = ticksXEco.substr(0, ticksXEco.length - 1);
                    ticksXEco += "]";
                    ticksYEco = ticksYEco.substr(0, ticksYEco.length - 1);
                    ticksYEco += "]";

                    var dataFis = "[[";
                    var ticksXFis = "[";
                    var ticksYFis = "[";
                    var maxFis = 0;
                    $(".prctAcumulado.total").each(function () {
                        var mes = $(this).data("mes");
                        ticksXFis += mes + ",";
                        var val = $(this).data("val");
                        ticksYFis += number_format(val, 2, ".", "") + ",";
                        if (val > maxFis) {
                            maxFis = val;
                        }
                        dataFis += "[" + mes + "," + val + "],";
                    });
                    dataFis = dataFis.substr(0, dataFis.length - 1);
                    dataFis += "]]";
                    ticksXFis = ticksXFis.substr(0, ticksXFis.length - 1);
                    ticksXFis += "]";
                    ticksYFis = ticksYFis.substr(0, ticksYFis.length - 1);
                    ticksYFis += "]";

                    var tituloe = "Avance económico de la obra";
                    var colore = "5FAB78";
                    var titulof = "Avance físico de la obra";
                    var colorf = "5F81AA";

                    var d = "datae=" + dataEco + "&txe=" + ticksXEco + "&tye=" + ticksYEco + "&me=" + maxEco + "&tituloe=" + tituloe + "&colore=" + colore;
                    d += "&dataf=" + dataFis + "&txf=" + ticksXFis + "&tyf=" + ticksYFis + "&mf=" + maxFis + "&titulof=" + titulof + "&colorf=" + colorf;
                    d += "&obra=${obra.id}";

                    var url = "${createLink(action: 'graficos2')}?" + d;
//                    console.log(url);
                    location.href = url;

                    %{--$.ajax({--}%
                    %{--type    : "POST",--}%
                    %{--url     : "${createLink(action:'graficos2')}",--}%
                    %{--data    : {--}%
                    %{--datae   : dataEco,--}%
                    %{--txe     : ticksXEco,--}%
                    %{--tye     : ticksYEco,--}%
                    %{--me      : maxEco,--}%
                    %{--tituloe : "Avance económico de la obra",--}%
                    %{--colore  : "#5FAB78",--}%

                    %{--dataf   : dataFis,--}%
                    %{--txf     : ticksXFis,--}%
                    %{--tyf     : ticksYFis,--}%
                    %{--mf      : maxFis,--}%
                    %{--titulof : "Avance físico de la obra",--}%
                    %{--colorf  : "#5F81AA"--}%
                    %{--},--}%
                    %{--success : function (msg) {--}%
                    %{--console.log("Data Saved: " + msg);--}%
                    %{--$("#modalTitle-graf").html("Gráfico del Cronograma");--}%
                    %{--$("#modalBody-graf").html(msg);--}%
                    %{--$("#modal-graf").modal("show");--}%
                    %{--}--}%
                    %{--});--}%

                    return false;
                });

                %{--$("#btnGraficoEco").click(function () {--}%
                %{--var data = "[[";--}%
                %{--var ticksX = "[";--}%
                %{--var ticksY = "[";--}%
                %{--var max = 0;--}%

                %{--$(".totalAcumulado.total").each(function () {--}%
                %{--var mes = $(this).data("mes");--}%
                %{--ticksX += mes + ",";--}%
                %{--var val = $(this).data("val");--}%
                %{--ticksY += val + ",";--}%
                %{--if (val > max) {--}%
                %{--max = val;--}%
                %{--}--}%
                %{--data += "[" + mes + "," + val + "],";--}%
                %{--});--}%
                %{--data = data.substr(0, data.length - 1);--}%
                %{--data += "]]";--}%
                %{--ticksX = ticksX.substr(0, ticksX.length - 1);--}%
                %{--ticksX += "]";--}%
                %{--ticksY = ticksY.substr(0, ticksY.length - 1);--}%
                %{--ticksY += "]";--}%

                %{--var d = "data=" + data + "&tx=" + ticksX + "&ty=" + ticksY + "&m=" + max + "&obra=${obra.id}";--}%
                %{--$.ajax({--}%
                %{--type    : "POST",--}%
                %{--url     : "${createLink(action:'graficos')}",--}%
                %{--data    : {--}%
                %{--data   : data,--}%
                %{--tx     : ticksX,--}%
                %{--ty     : ticksY,--}%
                %{--m      : max,--}%
                %{--obra   :${obra.id},--}%
                %{--titulo : "Avance económico de la obra",--}%
                %{--color  : "#5FAB78"--}%
                %{--},--}%
                %{--success : function (msg) {--}%
                %{--console.log("Data Saved: " + msg);--}%
                %{--$("#modalTitle-graf").html("Gráfico del Cronograma");--}%
                %{--$("#modalBody-graf").html(msg);--}%
                %{--$("#modal-graf").modal("show");--}%
                %{--}--}%
                %{--});--}%

                %{--return false;--}%
                %{--});--}%

                %{--$("#btnGraficoFis").click(function () {--}%
                %{--var data = "[[";--}%
                %{--var ticksX = "[";--}%
                %{--var ticksY = "[";--}%
                %{--var max = 0;--}%

                %{--$(".prctAcumulado.total").each(function () {--}%
                %{--var mes = $(this).data("mes");--}%
                %{--ticksX += mes + ",";--}%
                %{--var val = $(this).data("val");--}%
                %{--ticksY += val + ",";--}%
                %{--if (val > max) {--}%
                %{--max = val;--}%
                %{--}--}%
                %{--data += "[" + mes + "," + val + "],";--}%
                %{--});--}%
                %{--data = data.substr(0, data.length - 1);--}%
                %{--data += "]]";--}%
                %{--ticksX = ticksX.substr(0, ticksX.length - 1);--}%
                %{--ticksX += "]";--}%
                %{--ticksY = ticksY.substr(0, ticksY.length - 1);--}%
                %{--ticksY += "]";--}%

                %{--var d = "data=" + data + "&tx=" + ticksX + "&ty=" + ticksY + "&m=" + max + "&obra=${obra.id}";--}%

                %{--$.ajax({--}%
                %{--type    : "POST",--}%
                %{--url     : "${createLink(action:'graficos')}",--}%
                %{--data    : {--}%
                %{--data   : data,--}%
                %{--tx--}%
                %{--:--}%
                %{--ticksX,--}%
                %{--ty--}%
                %{--:--}%
                %{--ticksY,--}%
                %{--m--}%
                %{--:--}%
                %{--max,--}%
                %{--obra--}%
                %{--:${obra.id},--}%
                %{--titulo : "Avance físico de la obra",--}%
                %{--color--}%
                %{--:--}%
                %{--"#5F81AA"--}%
                %{--}--}%
                %{--,--}%
                %{--success : function (msg) {--}%
                %{--console.log("Data Saved: " + msg);--}%
                %{--$("#modalTitle-graf").html("Gráfico del Cronograma");--}%
                %{--$("#modalBody-graf").html(msg);--}%
                %{--$("#modal-graf").modal("show");--}%
                %{--}--}%
                %{--});--}%
                %{--return false;--}%
                %{--})--}%
                %{--;--}%

                //                /****************************************************************************************************************************************************/
                //                var data = [];
                //                var serie = [];
                //                var max = 0;
                //                var ticksx = [];
                //                var ticksy = [];
                //
                //                $(".totalAcumulado.total").each(function () {
                //                    var mes = $(this).data("mes");
                //                    ticksx.push(mes);
                //                    var val = $(this).data("val");
                //                    ticksy.push(val);
                //                    if (val > max) {
                //                        max = val;
                //                    }
                //                    var dato = [mes, val];
                //                    serie.push(dato);
                ////                        console.log(mes, val);
                //                });
                //                data.push(serie);
                //
                //                grafico({
                //                    target : "grafEco",
                //                    titulo : 'Avance económico de la obra',
                //                    data   : data,
                //                    axes   : {
                //                        xaxis : {
                //                            min   : 1,
                //                            ticks : ticksx,
                //                            pad   : 5.5
                //                        },
                //                        yaxis : {
                //                            min   : 0,
                //                            max   : max,
                //                            ticks : ticksy,
                //                            pad   : 5.5
                //                        }
                //                    }
                //                });
                //
                //                data = [];
                //                serie = [];
                //                max = 0;
                //                ticksy = [];
                //
                //                $(".prctAcumulado.total").each(function () {
                //                    var mes = $(this).data("mes");
                //                    var val = $(this).data("val") * 100;
                //                    ticksy.push(val);
                //                    if (val > max) {
                //                        max = val;
                //                    }
                //                    var dato = [mes, val];
                //                    serie.push(dato);
                ////                        console.log(mes, val);
                //                });
                //                data.push(serie);
                //                console.log(serie, max);
                //                grafico({
                //                    target : "grafFis",
                //                    titulo : 'Avance físico de la obra',
                //                    data   : data,
                //                    axes   : {
                //                        xaxis : {
                //                            min   : 1,
                //                            ticks : ticksx,
                //                            pad   : 5.5
                //                        },
                //                        yaxis : {
                //                            min   : 0,
                //                            max   : max,
                //                            ticks : ticksy,
                //                            pad   : 5.5
                //                        }
                //                    }
                //                });
                //                /****************************************************************************************************************************************************/

                //                $("#btnGraficoEco").click(function () {
                //                    graph("eco");
                //                    $("#modalTitle-graf").html("Gráfico del Cronograma");
                //                    $("#modal-graf").modal("show");
                //                });
                //
                //                $("#btnGraficoFis").click(function () {
                //                    graph("fis");
                //                    $("#modalTitle-graf").html("Gráfico del Cronograma");
                //                    $("#modal-graf").modal("show");
                //                });

            })
            ;

            //            function graph(tipo) {
            //                var target = "graf";
            //                if (redraw) {
            ////                    plot.destroy();
            ////                    $("#graf").empty();
            //                } else {
            //                    redraw = true;
            //                }
            //                var data;
            //                var ticksx;
            //                var ticksy;
            //                var maxy = 230;
            //
            //                var title;
            //
            //                switch (tipo) {
            //                    case "eco":
            //                        title = "Avance económico de la obra";
            //                        data = [
            //                            [
            //                                [1, 2],
            //                                [3, 5.12],
            //                                [5, 13.1],
            //                                [7, 33.6],
            //                                [9, 85.9],
            //                                [11, 219.9]
            //                            ]
            //                        ];
            //                        ticksx = [1, 3, 5, 7, 9];
            //                        ticksy = [2, 5, 13, 33, 85, 220];
            //                        break;
            //                    case "fis":
            //                        title = "Avance físico de la obra";
            //                        data = [
            //                            [
            //                                [2, 2],
            //                                [4, 5.12],
            //                                [6, 13.1],
            //                                [8, 33.6],
            //                                [10, 85.9],
            //                                [12, 219.9]
            //                            ]
            //                        ];
            //                        ticksx = [2, 4, 6, 8, 10, 12];
            //                        ticksy = [2, 5, 13, 33, 85, 220];
            //                        break;
            //                }
            //
            //                plot = $.jqplot("graf", data,
            //                        {
            //                            title  : title,
            //                            axes   : {
            //                                xaxis : {
            //                                    min   : 1,
            //                                    ticks : ticksx,
            //                                    pad   : 5.5
            //                                },
            //                                yaxis : {
            //                                    min   : 0,
            //                                    max   : maxy,
            //                                    ticks : ticksy,
            //                                    pad   : 5.5
            //                                }
            //                            },
            //                            series : [
            //                                {
            //                                    color : '#5FAB78'
            //                                }
            //                            ]
            //                        });
            //                if (redraw) {
            ////                    plot.replot({resetAxes : true});
            ////                    plot.resetAxesScale();
            //                    plot.redraw();
            //                }
            //
            //            }

        </script>

    </body>
</html>