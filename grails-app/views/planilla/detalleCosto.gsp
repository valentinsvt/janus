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
        <script src="${resource(dir: 'js/jquery/plugins/box/js', file: 'jquery.luz.box.js')}"></script>
        <script src="${resource(dir: 'js/jquery/plugins/', file: 'jquery.livequery.min.js')}"></script>
        <link href="${resource(dir: 'js/jquery/plugins/box/css', file: 'jquery.luz.box.css')}" rel="stylesheet">
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

        .borderLeft {
            border-left : #5E8E9B double 3px !important;
        }

        .borderTop {
            border-top : #5E8E9B double 3px !important;
        }
        </style>

    </head>

    <body>

        <div class="row" style="margin-bottom: 10px;">
            <div class="span9 btn-group" role="navigation">
                <g:link controller="contrato" action="registroContrato" params="[contrato: contrato?.id]" class="btn btn-ajax btn-new" title="Regresar al contrato">
                    <i class="icon-double-angle-left"></i>
                    Contrato
                </g:link>
                <g:link controller="planilla" action="list" params="[id: contrato?.id]" class="btn btn-ajax btn-new" title="Regresar a las planillas del contrato">
                    <i class="icon-angle-left"></i>
                    Planillas
                </g:link>
            </div>

            <div class="span3" id="busqueda-Planilla"></div>
        </div>

        <elm:headerPlanilla planilla="${planilla}"/>

        <g:if test="${editable}">
            <table class="table table-bordered table-condensed " style="width: auto;">
                <thead>
                    <tr>
                        <th>Rubro</th>
                        <th>Precio unitario</th>
                        <th>Cantidad</th>
                        <th>Total</th>
                        <th>
                            <a href="#" id="btnReset" class="btn">Nuevo</a>
                        </th>
                    </tr>
                </thead>
                <tbody>
                    <tr id="trRubro">
                        <td id="tdRubro">
                            <input type="text" id="txtRubro" class="input-xxlarge"/>
                        </td>
                        <td id="tdPrecio">
                            <input type="text" id="txtPrecio" class="input-small number"/>
                        </td>
                        <td id="tdCantidad">
                            <input type="text" id="txtCantidad" class="input-small number"/>
                        </td>
                        <td id="tdTotal" class="num bold">
                            0.00
                        </td>
                        <td>
                            <a href="#" class="btn btn-success hide" id="btnAdd">
                                <i class="icon-plus"></i> Agregar
                            </a>
                            <a href="#" class="btn btn-primary hide" id="btnSave">
                                <i class="icon-save"></i> Guardar
                            </a>
                        </td>
                    </tr>
                </tbody>
            </table>
        </g:if>

        <table class="table table-bordered table-striped table-condensed table-hover">
            <thead>
                <tr>
                    <th style="width: 50px;">N.</th>
                    <th>Descripción del rubro</th>
                    <th style="width: 70px;">U.</th>
                    <th style="width: 100px;">Precio unitario</th>
                    <th style="width: 100px;">Cantidad</th>
                    <th style="width: 100px;">Total</th>
                    <g:if test="${editable}">
                        <th style="width: 120px;"></th>
                    </g:if>
                </tr>
            </thead>
            <tbody id="tbRubros">

            </tbody>
            <tfoot>
                <tr>
                    <th colspan="2">TOTAL</th>
                    <td colspan="4" id="tdTotalFinal" class="num bold" data-max="${contrato.monto * 0.1}">0.00</td>
                    <g:if test="${editable}">
                        <td></td>
                    </g:if>
                </tr>
            </tfoot>
        </table>

        <div class="modal grande hide fade " id="modal-rubro" style=";overflow: hidden;">
            <div class="modal-header btn-info">
                <button type="button" class="close" data-dismiss="modal">×</button>

                <h3 id="modalTitle"></h3>
            </div>

            <div class="modal-body" id="modalBody">
                <bsc:buscador name="rubro.buscador.id" value="" accion="buscaRubro" controlador="planilla" campos="${campos}" label="Rubro" tipo="lista"/>
            </div>

            <div class="modal-footer" id="modalFooter">
            </div>
        </div>

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

            function numero($item, val) {
                if ($item.is("input")) {
                    if (val != 0 && val != "" && !isNaN(val)) {
                        $item.val(number_format(val, 2, ".", ",")).data("valor", val);
                    } else {
                        $item.val("").data("valor", 0);
                    }
                } else {
                    if (val != 0 && val != "" && !isNaN(val)) {
                        $item.text(number_format(val, 2, ".", ",")).data("valor", val);
                    } else {
                        $item.text("").data("valor", 0);
                    }
                }
            }

            function clickBuscar($btn) {
                var $tr = $("#trRubro");
                $tr.data({
                    edit   : false,
                    item   : $.trim($btn.attr("regid")),
                    unidad : $.trim($btn.attr("prop_unidad")),
                    nombre : $.trim($btn.attr("prop_nombre")),
                    codigo : $.trim($btn.attr("prop_codigo"))
                });

                $("#txtRubro").val($.trim($btn.attr("prop_nombre")));
                $("#modal-rubro").modal("hide");
            }

            function updateTotal() {
                var total = 0;
                $("#tbRubros").children("tr").each(function () {
                    var tot = $(this).data("total");
                    total += tot;
                });
                $("#tdTotalFinal").text(number_format(total, 2, ".", ",")).data("valor", total);
            }

            function initRows() {
                var rows = ${detalles};
                var total = 0;
                for (var i = 0; i < rows.length; i++) {
                    var data = rows[i];
                    total += parseFloat(data.total);
                    addRow(data);
                }
                $("#tdTotalFinal").text(number_format(total, 2, ".", ",")).data("valor", total);
            }

            function loadData(data) {
                var $tr = $("#trRubro");
                $tr.data(data);
                $("#txtRubro").val(data.nombre);
                $("#txtPrecio").val(data.precio);
                $("#txtCantidad").val(data.cantidad);
                $("#tdTotal").text(number_format(data.total, 2, ".", ","));
                $("#btnSave").show();
            }

            function updateRow(data) {
                $("#tbRubros").children("tr").each(function () {
                    var idAct = $(this).data("id");
                    if (idAct == data.id) {
                        $(this).remove();
                        addRow(data);
                    }
                });
            }

            function addRow(data) {
                //N. 	Descripción del rubro 	U. 	Precio unitario 	Cantidad 	Total

                var $tr = $("<tr>").data(data);
                var $tdCod = $("<td>").text(data.codigo).appendTo($tr);
                var $tdDes = $("<td>").text(data.nombre).appendTo($tr);
                var $tdUni = $("<td>").css("text-align", "center").text(data.unidad).appendTo($tr);
                var $tdPrec = $("<td>").css("text-align", "right").text(number_format(data.precio, 2, ".", ",")).appendTo($tr);
                var $tdCant = $("<td>").css("text-align", "right").text(number_format(data.cantidad, 2, ".", ",")).appendTo($tr);
                var $tdTotal = $("<td>").css("text-align", "right").text(number_format(data.total, 2, ".", ",")).appendTo($tr);

                if (${editable}) {
                    var $tdBtn = $("<td>");
                    var $btnEdit = $("<a href='#' class='btn btn-primary' style='margin-left: 10px;' title='Editar'><i class='icon-pencil'></i></a>").appendTo($tdBtn);
                    var $btnDelete = $("<a href='#' class='btn btn-danger' style='margin-left: 10px;' title='Eliminar'><i class='icon-trash'></i></a>").appendTo($tdBtn);

                    $btnDelete.click(function () {
                        $.box({
                            imageClass : "box_info",
                            title      : "Alerta",
                            text       : "Está seguro de eliminar el rubro " + data.nombre + "? Esta acción no se puede deshacer.",
                            iconClose  : false,
                            dialog     : {
                                resizable     : false,
                                draggable     : false,
                                closeOnEscape : false,
                                buttons       : {
                                    "Aceptar"  : function () {
                                        $.ajax({
                                            type    : "POST",
                                            url     : "${createLink(action:'deleteDetalleCosto')}",
                                            data    : {
                                                pln  : "${planilla.id}",
                                                item : data.item
                                            },
                                            success : function (msg) {
                                                $tr.remove();
                                                updateTotal();
                                            }
                                        });
                                    },
                                    "Cancelar" : function () {
                                    }
                                }
                            }
                        });
                    });

                    $btnEdit.click(function () {
                        loadData(data);
                    });

                    $tdBtn.appendTo($tr);
                }

                $tr.appendTo($("#tbRubros"));
            }

            function noDuplicados(item) {
                var noExiste = true;
                $("#tbRubros").children("tr").each(function () {
                    var itemAct = $(this).data("item");
                    if (itemAct == item) {
                        noExiste = false;
                    }
                });
                return noExiste;
            }

            function reset() {
                $("#txtRubro, .number").val("");
                $("#tdTotal").text("0.00");
                $("#trRubro").removeData();
                $("#btnAdd, #btnSave").hide();
            }

            $(function () {
                reset();
//                $("#tbDetalle").children("tr").each(function () {
//                    updateRow($(this));
//                });

                initRows();

                $("#btnReset").click(function () {
                    reset();
                });

                $("#btnSave").click(function () {
                    $(this).hide().after(spinner);
                    var $tr = $("#trRubro");
                    var data = $tr.data();
                    var $tdTotal = $("#tdTotalFinal");
                    var max = parseFloat($tdTotal.data("max"));
                    var tot = parseFloat($tdTotal.data("valor"));
                    var val = data.total + tot;
                    if (val <= max) {
                        $.ajax({
                            type    : "POST",
                            url     : "${createLink(action:'addDetalleCosto')}",
                            data    : {
                                id      : data.id,
                                pln     : "${planilla.id}",
                                item    : data.item,
                                cant    : data.cantidad,
                                prec    : data.precio,
                                totalPl : val
                            },
                            success : function (msg) {
                                var parts = msg.split("_");
                                if (parts[0] == "OK") {
                                    data.id = parts[1];
                                    data.edit = true;
                                    updateRow(data);
                                    reset();
                                    spinner.remove();
                                }
                            }
                        });
                    } else {
                        $.box({
                            imageClass : "box_info",
                            title      : "Error",
                            text       : "El total " + number_format(val, 2, ".", ",") + " sobrepasa el 10% del monto del contrato " + number_format(max, 2, ".", ","),
                            iconClose  : false,
                            dialog     : {
                                resizable     : false,
                                draggable     : false,
                                closeOnEscape : false,
                                buttons       : {
                                    "Aceptar" : function () {
                                        spinner.remove();
                                        reset();
                                    }
                                }
                            }
                        });
                    }
                    return false;
                });

                $("#btnAdd").click(function () {
                    $(this).hide().after(spinner);
                    var $tr = $("#trRubro");
                    var data = $tr.data();
                    var $tdTotal = $("#tdTotalFinal");
                    var max = parseFloat($tdTotal.data("max"));
                    var tot = parseFloat($tdTotal.data("valor"));
                    var val = data.total + tot;
                    if (val <= max) {
                        if (noDuplicados(data.item)) {
                            $.ajax({
                                type    : "POST",
                                url     : "${createLink(action:'addDetalleCosto')}",
                                data    : {
                                    pln     : "${planilla.id}",
                                    item    : data.item,
                                    cant    : data.cantidad,
                                    prec    : data.precio,
                                    totalPl : val
                                },
                                success : function (msg) {
                                    var parts = msg.split("_");
                                    if (parts[0] == "OK") {
                                        data.id = parts[1];
                                        data.edit = true;
                                        addRow(data);
                                        reset();
                                        spinner.remove();
                                    }
                                }
                            });
                        } else {
                            $.box({
                                imageClass : "box_info",
                                title      : "Alerta",
                                text       : "El rubro " + data.nombre + " ya está ingresado, edítelo.",
                                iconClose  : false,
                                dialog     : {
                                    resizable     : false,
                                    draggable     : false,
                                    closeOnEscape : false,
                                    buttons       : {
                                        "Aceptar" : function () {
                                            spinner.remove();
                                            reset();
                                        }
                                    }
                                }
                            });
                        }
                    } else {
                        $.box({
                            imageClass : "box_info",
                            title      : "Error",
                            text       : "El total " + number_format(val, 2, ".", ",") + " sobrepasa el 10% del monto del contrato " + number_format(max, 2, ".", ","),
                            iconClose  : false,
                            dialog     : {
                                resizable     : false,
                                draggable     : false,
                                closeOnEscape : false,
                                buttons       : {
                                    "Aceptar" : function () {
                                        spinner.remove();
                                        reset();
                                    }
                                }
                            }
                        });
                    }
                    return false;
                });

                $(".number").bind({
                    keydown : function (ev) {
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
                    }, //keydown
                    keyup   : function () {
                        var val = $(this).val();
                        // esta parte valida q no ingrese mas de 2 decimales
                        var parts = val.split(".");
                        if (parts.length > 1) {
                            if (parts[1].length > 2) {
                                parts[1] = parts[1].substring(0, 2);
                                val = parts[0] + "." + parts[1];
                                $(this).val(val);
                            }
                        }
                        // esta parte hace los calculos
                        var $tr = $("#trRubro");
                        var cant = $.trim($("#txtCantidad").val());
                        var prec = $.trim($("#txtPrecio").val());
                        if ($tr.data() && cant != "" && prec != "") {
                            var tot = cant * prec;
                            $tr.data({
                                cantidad : cant,
                                precio   : prec,
                                total    : tot
                            });
                            $("#tdTotal").text(number_format(tot, 2, ".", ","));
                            if (!$tr.data("edit")) {
                                $("#btnAdd").show();
                            }
                        }
                    }
                });

                $("#txtRubro").dblclick(function () {
                    var btnOk = $('<a href="#" data-dismiss="modal" class="btn">Cerrar</a>');
                    $("#modalTitle").html("Lista de rubros");
                    $("#modalFooter").html("").append(btnOk);
                    $(".contenidoBuscador").html("")
                    $("#modal-rubro").modal("show");

                });

            });
        </script>

    </body>
</html>