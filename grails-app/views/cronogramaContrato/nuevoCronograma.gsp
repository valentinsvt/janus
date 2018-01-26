<%--
  Created by IntelliJ IDEA.
  User: gato
  Date: 28/09/17
  Time: 10:54
--%>

<%@ page import="janus.pac.CronogramaContratado; janus.pac.CronogramaContrato" %>
<!doctype html>
<html>
<head>
    <meta name="layout" content="main">


    <link href="${resource(dir: 'js/jquery/plugins/box/css', file: 'jquery.luz.box.css')}" rel="stylesheet">
    <script src="${resource(dir: 'js/jquery/plugins/box/js', file: 'jquery.luz.box.js')}"></script>
    <link href="${resource(dir: 'css', file: 'cronograma.css')}" rel="stylesheet">
    <title>Cronograma</title>
</head>

<body>
<g:set var="meses" value="${Math.ceil(contrato.plazo/30).toInteger()}"/>
%{--<g:set var="plazoOk" value="${detalle.findAll { it.dias && it.dias > 0 }.size() > 0}"/>--}%
<g:set var="sum" value="${0}"/>

<div class="tituloTree">
    Cronograma del contrato de la obra ${obra.descripcion} (${meses} mes${meses == 1 ? "" : "es"})
</div>

<div class="btn-toolbar">
    <div class="btn-group">
        <a href="${g.createLink(controller: 'contrato', action: 'registroContrato', params: [contrato: contrato?.id])}" class="btn btn-ajax btn-new" id="atras" title="Regresar al contrato">
            <i class="icon-arrow-left"></i>
            Regresar
        </a>
        %{--<g:if test="${meses > 0 && plazoOk && contrato.estado != 'R'}">--}%
            <a href="#" class="btn disabled" id="btnDeleteRubro">
                <i class="icon-minus"></i>
                Eliminar Rubro
            </a>
            <a href="#" class="btn" id="btnDeleteCronograma">
                <i class="icon-trash"></i>
                Eliminar Cronograma
            </a>
        %{--</g:if>--}%
    </div>

    %{--<g:if test="${meses > 0 && plazoOk}">--}%
        <div class="btn-group">
            <a href="#" class="btn" id="btnGrafico">
                <i class="icon-bar-chart"></i>
                Gráficos de avance
            </a>
            <a href="#" id="btnReporte" class="btn">
                <i class="icon-print"></i>
                Imprimir
            </a>
        </div>
    %{--</g:if>--}%
</div>

<div style="margin-bottom: 5px;">
    <strong>Subpresupuesto:</strong> <g:select name="subpresupuesto" from="${subpres}" optionKey="id" optionValue="descripcion"
                              style="width: 300px;font-size: 10px" id="subpres" value="${subpre}"
                              noSelection="['-1': 'TODOS']"/>
    <a href="#" class="btn" style="margin-top: -10px;" id="btnSubpre"><i class="fa icon-double-angle-down"></i> Cambiar</a>

    <g:if test="${contrato.estado != 'R'}">
        <a href="#" class="btn" style="margin-top: -10px;" id="btnDesmarcar"><i class="fa icon-eraser"></i> Desmarcar todo</a>
    </g:if>
</div>

<div>
    La ruta crítica se muestra con los rubros marcados en amarillo
</div>

%{--<g:if test="${meses > 0 && plazoOk}">--}%
    <div class="divTabla">
        <table class="table table-bordered table-condensed table-hover">
            <thead>
            <tr>
                <th class="codigo">
                    Código
                </th>
                <th class="nombre">
                    Rubro
                </th>
                <th class="unidad">
                    Unidad
                </th>
                <th class="cantidad">
                    Cantidad
                </th>
                <th class="precioU">
                    Unitario
                </th>
                <th class="subtotal">
                    C.Total
                </th>
                <th class="tiny">
                    T.
                </th>
                <g:each in="${0..meses - 1}" var="i">
                    <th class="meses">
                        Mes ${i + 1}
                    </th>
                </g:each>
                <th class="totalRubro">
                    Total Rubro
                </th>
            </tr>
            </thead>
            <tbody id="tabla_material">


            <g:each in="${detalle}" var="vol" status="s">

                %{--<g:set var="cronos" value="${CronogramaContrato.findAllByVolumenObra(vol)}"/>--}%
                <g:set var="cronos" value="${CronogramaContratado.findAllByVolumenContrato(vol)}"/>


                <tr class="item_row ${vol.rutaCritica == 'S' ? 'rutaCritica' : ''}" id="${vol.id} " data-id="${vol.id}">
                    <td class="codigo">
                        ${vol.item.codigo}
                    </td>
                    <td class="nombre">
                        ${vol.item.nombre}
                    </td>
                    <td style="text-align: center" class="unidad">
                        ${vol.item.unidad.codigo}
                    </td>
                    <td class="num cantidad" data-valor="${vol.volumenCantidad + vol.cantidadComplementaria}">
                        %{--<g:formatNumber number="${vol.volumenCantidad}" format="##,##0" minFractionDigits="2" maxFractionDigits="2" locale="ec"/>--}%
                        <g:formatNumber number="${vol.volumenCantidad + vol.cantidadComplementaria}" format="##,##0" minFractionDigits="2" maxFractionDigits="2" locale="ec"/>
                    </td>
                    %{--<td class="num precioU" data-valor="${precios[vol.id.toString()]}">--}%
                    <td class="num precioU" data-valor="${vol.volumenPrecio}">
                        %{--<g:formatNumber number="${precios[vol.id.toString()]/vol.volumenCantidad}" format="##,##0" minFractionDigits="2" maxFractionDigits="2" locale="ec"/>--}%
                        <g:formatNumber number="${vol.volumenPrecio}" format="##,##0" minFractionDigits="2" maxFractionDigits="2" locale="ec"/>
                    </td>
                    %{--<g:set var="parcial" value="${precios[vol.id.toString()]}"/>--}%
                    <g:set var="parcial" value="${(vol.volumenCantidad + vol.cantidadComplementaria)* vol.volumenPrecio}"/>
                    <td class="num subtotal" data-valor="${parcial}">
                        <g:formatNumber number="${parcial}" format="##,##0" minFractionDigits="2" maxFractionDigits="2" locale="ec"/>
                        <g:set var="sum" value="${sum + parcial}"/>
                    </td>
                    <td class="tiny">
                        $
                    </td>
                    <g:each in="${0..meses - 1}" var="i">
                        <g:set var="prec" value="${cronos.find { it.periodo == i + 1 }}"/>
                        <td class="dol mes meses num mes${i + 1} rubro${vol.id}" data-mes="${i + 1}" data-rubro="${vol.id}" data-valor="0"
                            data-tipo="dol" data-val="${prec?.precio ?: 0}" data-id="${prec?.id ?: ''}">
                            <g:formatNumber number="${prec?.precio}" format="##,##0" minFractionDigits="2" maxFractionDigits="2" locale="ec"/>
                        </td>
                    </g:each>
                    <td class="num rubro${vol.id} dol total totalRubro">
                        <span>
                        </span> $
                    </td>
                </tr>

                <tr class="item_prc ${vol.rutaCritica == 'S' ? 'rutaCritica' : ''}" data-id="${vol.id}">
                    <td colspan="3">
                        &nbsp
                    </td>
                    <td style="text-align: center">
                        %{--<a href="#" class="btn btn-success btn-small btnEditar" data-id="${vol?.id}" data-cantidad="${vol?.volumenCantidad}" title="Editar cantidad complementaria">--}%
                        <a href="#" class="btn btn-success btn-small btnEditar" data-id="${vol?.id}" data-cantidad="${vol.volumenCantidad + vol.cantidadComplementaria}" title="Editar cantidad complementaria">
                            <i class="fa icon-pencil"></i>
                        </a>
                    </td>
                    <td colspan="2">
                        &nbsp
                    </td>
                    <td>
                        %
                    </td>
                    <g:each in="${0..meses - 1}" var="i">
                        <g:set var="porc" value="${cronos.find { it.periodo == i + 1 }}"/>
                        <td class="prct mes num mes${i + 1} rubro${vol.id}" data-mes="${i + 1}" data-rubro="${vol.id}" data-valor="0"
                            data-tipo="prct" data-val="${porc?.porcentaje ?: 0}" data-id="${porc?.id ?: ''}">
                            <g:formatNumber number="${porc?.porcentaje}" format="##,##0" minFractionDigits="2" maxFractionDigits="2" locale="ec"/>
                        </td>
                    </g:each>
                    <td class="num rubro${vol.id} prct total totalRubro">
                        <span>
                        </span> %
                    </td>
                </tr>
                <tr class="item_f ${vol.rutaCritica == 'S' ? 'rutaCritica' : ''}" data-id="${vol.id}">
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
                            <g:formatNumber number="${cant?.cantidad}" format="##,##0" minFractionDigits="2" maxFractionDigits="2" locale="ec"/>
                        </td>
                    </g:each>
                    <td class="num rubro${vol.id} fis total totalRubro">
                        <span>
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
    </div>

<div class="modal hide fade" id="modal-cronograma">
    <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal">×</button>

        <h3 id="modalTitle"></h3>
    </div>

    <div class="modal-body" id="modalBody">
        <form class="form-horizontal" id="frmRubro">
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

        <div id="divRubro">
            Múltiples rubros
        </div>


        <div class="well">
            <div class="row" style="margin-bottom: 10px;">
                <div class="span5">
                    Períodos   <input id="periodosDesde" class="spinner"/> al <input id="periodosHasta" class="spinner" value="${meses}"/>
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


%{--<div id="modificarCantidadDialog">--}%
    %{--<fieldset>--}%
        %{--<div class="span4">--}%
            %{--<strong></strong>--}%
        %{--</div>--}%
    %{--</fieldset>--}%
    %{--<fieldset style="margin-top: 10px">--}%
        %{--<div class="span4">--}%

        %{--</div>--}%
    %{--</fieldset>--}%
%{--</div>--}%


<div class="modal hide fade mediumModal" id="modal-TipoObra" style=";overflow: hidden;">
    <div class="modal-header btn-primary">
        <button type="button" class="close" data-dismiss="modal">×</button>

        <h3 id="modalTitle_tipo">
        </h3>
    </div>

    <div class="modal-body" id="modalBody_tipo">

    </div>

    <div class="modal-footer" id="modalFooter_tipo">
    </div>
</div>


<script type="text/javascript">

        $(".btnEditar").click(function () {
            var id = $(this).data("id")
            var cantidad = $(this).data("cantidad")
//            $("#modificarCantidadDialog").dialog("open")



            $.ajax({
                type: 'POST',
                url:'${createLink(controller: 'cronogramaContrato', action: 'modificarCantidad_ajax')}',
                data:{
                    id: id
                },
                success: function (msg) {
                    var btnOk = $('<a href="#" data-dismiss="modal" class="btn">Cancelar</a>');
                    var btnSave = $('<a href="#"  class="btn btn-success"><i class="icon-save"></i> Guardar</a>');

                    btnSave.click(function () {
                        $(this).replaceWith(spinner);
                        $.ajax({
                            type: "POST",
                            url: "${createLink(controller: 'cronogramaContrato', action:'guardarCantidad_ajax')}",
                            data: $("#frmSave-Programacion").serialize(),
                            success: function (msg) {
                                if (msg == 'ok') {
                                    alert("Cantidad modificada!");
                                    setTimeout(function () {
                                        location.href = "${g.createLink(controller: 'cronogramaContrato', action: 'nuevoCronograma')}/" + "${contrato?.id}";
                                    }, 700);
                                } else {
                                    alert("Error al modificar la cantidad!")
                                }
                                $("#modal-TipoObra").modal("hide");
                            }
                        });
                        return false;
                    });
                    $("#modalHeader_tipo").removeClass("btn-edit btn-show btn-delete");
                    $("#modalTitle_tipo").html("Cambiar cantidad complementaria");
                    $("#modalBody_tipo").html(msg);
                    $("#modalFooter_tipo").html("").append(btnOk).append(btnSave);
                    $("#modal-TipoObra").modal("show");
                }
            });



        });

        %{--$("#modificarCantidadDialog").dialog({--}%
            %{--autoOpen  : false,--}%
            %{--resizable : false,--}%
            %{--modal     : true,--}%
            %{--draggable : false,--}%
            %{--width     : 450,--}%
            %{--height    : 180,--}%
            %{--position  : 'center',--}%
            %{--title     : 'Editar cantidad complementaria',--}%
            %{--buttons   : {--}%
                %{--"Cerrar": function () {--}%
                    %{--$("#modificarCantidadDialog").dialog("close")--}%
                %{--},--}%
                %{--"Guardar": function () {--}%
                    %{--$.ajax({--}%
                       %{--type: 'POST',--}%
                        %{--url:'${createLink(controller: 'cronogramaContrato', action: 'modificarCantidad_ajax')}',--}%
                        %{--data:{--}%
                            %{--id: $(".btnEditar").data("id")--}%
                        %{--},--}%
                        %{--success: function (msg) {--}%
                            %{--$("#modificarCantidadDialog").dialog("close")--}%
                        %{--}--}%
                    %{--});--}%
                %{--}--}%

            %{--}--}%
        %{--});--}%

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
            return false;
        }
        return true;
    }

    function validar2() {
        var periodoIni = $.trim($("#periodosDesde").val());
        var periodoFin = $.trim($("#periodosHasta").val());

        var $prct = $("#tf_prct");

        var prct = $.trim($prct.val());

        if (periodoIni == "") {
            log("Ingrese el periodo inicial");
            return false;
        }
        if (periodoFin == "") {
            log("Ingrese el periodo final");
            return false;
        }
        if (prct == "") {
            log("Ingrese el porcentaje, cantidad o precio");
            return false;
        }

        var maxPrct = $prct.attr("max");

        try {
            periodoIni = parseFloat(periodoIni);
            periodoFin = parseFloat(periodoFin);

            if (periodoFin < periodoIni) {
                log("El periodo inicial debe ser inferior al periodo final");
                return false;
            }

            prct = parseFloat(prct);
            maxPrct = parseFloat(maxPrct);

            if (prct > maxPrct) {
                log("El porcentaje debe ser menor que " + maxPrct);
                return false;
            }

        } catch (e) {
//                    ////console.log(e);
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
//        ////console.log(ev.keyCode);
        return ((ev.keyCode >= 48 && ev.keyCode <= 57) || (ev.keyCode >= 96 && ev.keyCode <= 105) || ev.keyCode == 190 || ev.keyCode == 110 || ev.keyCode == 8 || ev.keyCode == 46 || ev.keyCode == 9);
    }

    function getSelected() {
        var selected = $(".item_row").filter(".rowSelected");
        return selected;
    }

    $(function () {

        $("#subpres").val(${subpre});

        updateTotales();

        $("#btnDesmarcar").click(function () {
            $(".rowSelected").removeClass("rowSelected");
        });

        $("#btnSubpre").click(function () {
            $.box({
                imageClass : "box_info",
                text       : "Cargando... Por favor espere...",
                title      : "Cargando",
                iconClose  : false,
                dialog     : {
                    resizable     : false,
                    draggable     : false,
                    closeOnEscape : false,
                    buttons       : false
                }
            });
            location.href = "${createLink(action: 'nuevoCronograma')}/${contrato.id}?subpre=" + $("#subpres").val();
        });

        <g:if test="${contrato.estado!='R'}">
        $("#tabla_material").children("tr").click(function () {

            if ($(this).hasClass("rowSelected")) {
                $(this).removeClass("rowSelected");
                if ($(this).hasClass("item_row")) {
                    $(this).next().removeClass("rowSelected").next().removeClass("rowSelected");
                } else if ($(this).hasClass("item_prc")) {
                    $(this).next().removeClass("rowSelected");
                    $(this).prev().removeClass("rowSelected");
                } else if ($(this).hasClass("item_f")) {
                    $(this).prev().removeClass("rowSelected").prev().removeClass("rowSelected");
                }
            } else {
                $(this).addClass("rowSelected");
                if ($(this).hasClass("item_row")) {
                    $(this).next().addClass("rowSelected").next().addClass("rowSelected");
                } else if ($(this).hasClass("item_prc")) {
                    $(this).next().addClass("rowSelected");
                    $(this).prev().addClass("rowSelected");
                } else if ($(this).hasClass("item_f")) {
                    $(this).prev().addClass("rowSelected").prev().addClass("rowSelected");
                }
            }

            var sel = getSelected();
            if (sel.length == 0) {
                $("#btnLimpiarRubro, #btnDeleteRubro").addClass("disabled");
            } else {
                $("#btnLimpiarRubro, #btnDeleteRubro").removeClass("disabled");
            }
        });
        </g:if>

        $(".spinner").spinner({
            min : 1,
            max :${meses}//,

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
//                                ////console.log(e);
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
                    var $sel = getSelected();
                    if ($sel.length == 1) {
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
//                                    ////console.log(e);
                        }
                    } //if $sel.lenght = 1
                    else {
                        try {
                            prct = parseFloat(prct);
                            if (prct > 100) {
                                prct = 100;
                            }
                            if (ev.keyCode != 110 && ev.keyCode != 190) {
                                $("#tf_prct").val(prct).data("val", prct);
                                $("#tf_cant").val("").data("val", null);
                                $("#tf_precio").val("").data("val", null);
                            }
                        } catch (e) {
//                                    ////console.log(e);
                        }
                    } //$sel.lenght > 1
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
//                                ////console.log(e);
                    }
                }
            }
        });

        function clickOne($celda) {
            var $tr = $celda.parents("tr");
            if ($tr.hasClass("item_prc")) {
                $tr = $tr.prev();
            } else if ($tr.hasClass("item_f")) {
                $tr = $tr.prev().prev();
            }

            var mes = $celda.data("mes");
            var tipo = $celda.data("tipo");
            var valor = $celda.data("valor");
            var rubro = $celda.data("rubro");

            $("#periodosDesde").val(mes);
            $("#periodosHasta").val("${meses}");
            $("#divRubro").hide();
            $("#frmRubro").show();

            $("#rd_cant,#tf_cant,#rd_precio,#tf_precio").removeAttr("disabled");
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
                        dataAjax += "&crono=" + rubro + "_" + periodoIni + "_" + dol + "_" + prct + "_" + cant;
                    } else {
                        var meses = periodoFin - periodoIni + 1;
                        dol = subtotal * (prct / 100);

                        var dolCalc = dol, prctCalc = prct, cantCalc = cant;
                        dol = Math.round((dol / meses) * 100) / 100;
                        prct = Math.round((prct / meses) * 100) / 100;
                        cant = Math.round((cant / meses) * 100) / 100;

                        for (i = periodoIni; i <= periodoFin; i++) {
                            if (i == periodoFin) {
                                dol = dolCalc;
                                prct = prctCalc;
                                cant = cantCalc;
                            }
                            dolCalc -= dol;
                            prctCalc -= prct;
                            cantCalc -= cant;
                            $(".dol.mes" + i + ".rubro" + rubro).text(number_format(dol, 2, ".", ",")).data("val", dol);
                            $(".prct.mes" + i + ".rubro" + rubro).text(number_format(prct, 2, ".", ",")).data("val", prct);
                            $(".fis.mes" + i + ".rubro" + rubro).text(number_format(cant, 2, ".", ",")).data("val", cant);

                            dataAjax += "&crono=" + rubro + "_" + i + "_" + dol + "_" + prct + "_" + cant + "&";
                        }
                    }
                    dataAjax += "&cont=${contrato.id}";
                    $.ajax({
                        type    : "POST",
                        url     : "${createLink(action:'saveCronoNuevo_ajax')}",
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
                                ////console.log("ERROR");
                            }
                            $(".rowSelected").removeClass("rowSelected");
                        }
                    });
                }
                return false;
            });

            $("#modalTitle").html("Registro del Cronograma");

            $("#modalFooter").html("").append($btnCancel).append($btnOk);
            $("#modal-cronograma").modal("show");
        }

        <g:if test="${contrato.estado!='R'}">
        $(".mes").dblclick(function () {
            var $sel = getSelected();
            var $celda = $(this);

            if ($sel.length == 1) {
                clickOne($celda);
            } else {
                if ($sel.length > 1) {
                    var mes = $celda.data("mes");
                    $("#rd_cant,#tf_cant,#rd_precio,#tf_precio").attr("disabled", "true");
                    $("#periodosDesde").val(mes);
                    $("#periodosHasta").val("${meses}");
                    $("#rd_prct").attr("checked", true);

                    $("#frmRubro").hide();
                    $("#divRubro").show();

                    $("#tf_prct").data({
                        max   : 100,
                        total : 100
                    }).val("");

                    $("#spCant").text("");
                    $("#spPrecio").text("");
                    $("#spPrct").text(100);

                    var $btnCancel = $('<a href="#" data-dismiss="modal" class="btn">Cerrar</a>');
                    var $btnOk = $('<a href="#" class="btn btn-success">Aceptar</a>');

                    $btnOk.click(function () {
                        if (validar2()) {
                            $btnOk.replaceWith(spinner);

                            $(".item_row.rowSelected").each(function () {
                                var id = $(this).data("id");
                                $.ajax({
                                    async   : false,
                                    type    : "POST",
                                    url     : "${createLink(action:'deleteRubroNuevo_ajax')}",
                                    data    : {
                                        id : id
                                    },
                                    success : function (msg) {
                                        $(".mes.rubro" + id).text("").data("val", 0);
                                        updateTotales();
                                    }
                                });
                            });

                            var dataAjax = "";

                            var periodoIni = parseInt($("#periodosDesde").val());
                            var periodoFin = parseInt($("#periodosHasta").val());

                            var prct = $("#tf_prct").data("val");

                            $sel.each(function () {
                                var $tr = $(this);

                                var rubro = $tr.data("id");

                                var cantTot = $tr.find(".cantidad").data("valor");
                                var precTot = $tr.find(".subtotal").data("valor");

                                var cantCal = cantTot * (prct / 100);
                                var precCal = precTot * (prct / 100);

                                if (periodoIni == periodoFin) {
                                    $(".dol.mes" + periodoIni + ".rubro" + rubro).text(number_format(precCal, 2, ".", ",")).data("val", precCal);
                                    $(".prct.mes" + periodoIni + ".rubro" + rubro).text(number_format(prct, 2, ".", ",")).data("val", prct);
                                    $(".fis.mes" + periodoIni + ".rubro" + rubro).text(number_format(cantCal, 2, ".", ",")).data("val", cantCal);
                                    dataAjax += "&crono=" + rubro + "_" + periodoIni + "_" + precCal + "_" + prct + "_" + cantCal;
                                } else {
                                    var meses = periodoFin - periodoIni + 1;

                                    var pr = Math.round((prct / meses) * 100) / 100;
                                    var cn = Math.round((cantCal / meses) * 100) / 100;
                                    var pe = Math.round((precCal / meses) * 100) / 100;

                                    var prRest = prct, cnRest = cantCal, peRest = precCal;

                                    for (var i = periodoIni; i <= periodoFin; i++) {
                                        if (i == periodoFin) {
                                            pr = prRest;
                                            cn = cnRest;
                                            pe = peRest;
                                        }
                                        prRest -= pr;
                                        cnRest -= cn;
                                        peRest -= pe;

                                        $(".dol.mes" + i + ".rubro" + rubro).text(number_format(pe, 2, ".", ",")).data("val", pe);
                                        $(".prct.mes" + i + ".rubro" + rubro).text(number_format(pr, 2, ".", ",")).data("val", pr);
                                        $(".fis.mes" + i + ".rubro" + rubro).text(number_format(cn, 2, ".", ",")).data("val", cn);

                                        dataAjax += "&crono=" + rubro + "_" + i + "_" + pe + "_" + pr + "_" + cn + "&";
                                    }
                                }
                            });
                            dataAjax += "&cont=${contrato.id}";
//                                    console.log("-->>" + dataAjax)
                            $.ajax({
                                type    : "POST",
                                url     : "${createLink(action:'saveCronoNuevo_ajax')}",
                                data    : dataAjax,
                                success : function (msg) {
                                    var parts = msg.split("_");
                                    if (parts[0] == "OK") {
                                        parts = parts[1].split(";");
                                        for (var i = 0; i < parts.length; i++) {
                                            var p = parts[i].split(":");
                                            var mes = p[0];
                                            var id = p[1];
                                            var rubro = p[2];
                                            $(".dol.mes" + mes + ".rubro" + rubro).data("id", id);
                                            $(".prct.mes" + mes + ".rubro" + rubro).data("id", id);
                                            $(".fis.mes" + mes + ".rubro" + rubro).data("id", id);
                                        }
                                        updateTotales();
                                        $("#modal-cronograma").modal("hide");

                                        $(".rowSelected").removeClass("rowSelected");

                                    } else {
//                                                ////console.log("ERROR");
                                    }
                                }
                            });
                        } //if validar
                    });

                    $("#modalTitle").html("Registro del Cronograma");
                    $("#modalFooter").html("").append($btnCancel).append($btnOk);
                    $("#modal-cronograma").modal("show");
                }
            }

        }); //fin dblclick


        $("#btnDeleteRubro").click(function () {
            $.box({
                imageClass : "box_info",
                text       : "Se eliminarán los rubros marcados, continuar?<br/>Los datos serán eliminados inmediatamente, y no se puede deshacer.",
                title      : "Confirmación",
                iconClose  : false,
                dialog     : {
                    resizable : false,
                    draggable : false,
                    buttons   : {
                        "Aceptar"  : function () {

                            $(".item_row.rowSelected").each(function () {
                                var id = $(this).data("id");
                                $.ajax({
                                    type    : "POST",
                                    url     : "${createLink(action:'deleteRubroNuevo_ajax')}",
                                    data    : {
                                        id : id
                                    },
                                    success : function (msg) {
                                        $(".mes.rubro" + id).text("").data("val", 0);
                                        updateTotales();
                                    }
                                });
                            });
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
                                url     : "${createLink(action:'deleteCronogramaNuevo_ajax')}",
                                data    : {
                                    obra : ${obra.id}
                                },
                                success : function (msg) {
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
        </g:if>
        $("#btnReporte").click(function () {
            location.href = "${createLink(controller: 'reportes5', action:'reporteCronogramaNuevoPdf', id:contrato.id, params:[tipo:'contrato'])}";
            return false;
        });

        $("#btnGrafico").click(function () {
            var dataEco = "[[";
            dataEco += "[0,0],";
            var ticksXEco = "[0,";
            var ticksYEco = "[0,";
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
            ticksYEco += number_format(${sum}, 2, ".", "");

            ticksYEco += "]";

            var dataFis = "[[";
            dataFis += "[0,0],";
            var ticksXFis = "[0,";
            var ticksYFis = "[0,";
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

            ticksYFis += number_format(100, 2, ".", "");

            ticksYFis += "]";

            var tituloe = "Avance económico de la obra";
            var colore = "5FAB78";
            var titulof = "Avance físico de la obra";
            var colorf = "5F81AA";

            maxFis = 100;
            maxEco = ${sum};

            var d = "datae=" + dataEco + "&txe=" + ticksXEco + "&tye=" + ticksYEco + "&me=" + maxEco + "&tituloe=" + tituloe + "&colore=" + colore;
            d += "&dataf=" + dataFis + "&txf=" + ticksXFis + "&tyf=" + ticksYFis + "&mf=" + maxFis + "&titulof=" + titulof + "&colorf=" + colorf;
            d += "&obra=${obra.id}&contrato=${contrato.id}";
            d += "&subpre=${subpre}";

            var url = "${createLink(action: 'graficos2')}?" + d + "&nuevo=" + 1;
//                    ////console.log(url);
            location.href = url;

            return false;
        });
    });
</script>

</body>
</html>