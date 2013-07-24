<%--
  Created by IntelliJ IDEA.
  User: luz
  Date: 7/24/13
  Time: 1:09 PM
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
            <table class="table table-bordered table-condensed ">
                <thead>
                    <tr>
                        <th>Factura N.</th>
                        <th>Descripción del rubro</th>
                        <th>Unidad</th>
                        <th>Valor sin IVA</th>
                        <th>
                            Valor con IVA<br/>
                            (${iva}%)
                        </th>
                        <th>
                            % de indirectos<br/>
                            <g:select name="indirectos" class="input-mini" value="${25}" from="${0..100}"/>%
                        </th>
                        <th>Valor total</th>
                        <th>
                            <a href="#" id="btnReset" class="btn">Nuevo</a>
                        </th>
                    </tr>
                </thead>
                <tbody>
                    <tr id="trRubro">
                        <td id="tdFactura">
                            <input type="text" id="txtFactura" class="input-small"/>
                        </td>
                        <td id="tdRubro">
                            <input type="text" id="txtRubro" class="input-xxlarge"/>
                        </td>
                        <td id="tdUnidad">
                            <g:select class="input-mini" name="selUnidad" from="${janus.Unidad.list([sort: 'descripcion'])}" optionKey="id" optionValue="codigo"/>
                        </td>
                        <td id="tdValor">
                            <input type="text" id="txtValor" class="input-small number"/>
                        </td>
                        <td id="tdValorIva">
                            <input type="text" id="txtValorIva" class="input-small number"/>
                        </td>
                        <td id="tdIndirectos">
                            <input type="text" id="txtIndirectos" class="input-small number"/>
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

            var iva = ${iva}/100;

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

            function reset() {
                $("#txtRubro, #txtFactura, .number").val("");
//                $("#indirectos").val(25);

                $("#tdTotal").text("0.00");
                $("#trRubro").removeData();
                $("#btnAdd, #btnSave").hide();
            }

            $(function () {

                reset();

                $("#btnReset").click(function () {
                    reset();
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
                        $(".errorP").remove();
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
                        if ($(this).attr("id") == "txtValor") {
                            var indi = parseInt($("#indirectos").val()) / 100;
                            var valorNoIva = parseFloat($.trim($("#txtValor").val()));

                            var valorIva = valorNoIva + (valorNoIva * iva);
                            $("#txtValorIva").val(number_format(valorIva, 2, ".", "")).data("default", number_format(valorIva, 2, ".", ""));

                            var valorIndi = valorNoIva + (valorNoIva * indi);
                            $("#txtIndirectos").val(number_format(valorIndi, 2, ".", "")).data("default", number_format(valorIndi, 2, ".", ""));
                        } else if ($(this).attr("id") == "txtValorIva" || $(this).attr("id") == "txtValorIndi") {
                            var valor = parseFloat($.trim($(this).val()));
                            var def = parseFloat($(this).data("default"));
                            var dif = Math.abs(valor - def);

                            if (dif > 0.1) {
                                $(this).val(def);
                                $(this).after("<p class='errorP'>No puede ingresar un valor con una diferencia de más de un centavo</p>")
                            }
                        }

                        var factura = $.trim($("#txtFactura").val());
                        var rubro = $.trim($("#txtRubro").val());
                        var unidadId = $("#selUnidad").val();

                    }
                });
            });
        </script>
    </body>
</html>