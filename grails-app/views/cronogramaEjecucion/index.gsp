<%--
  Created by IntelliJ IDEA.
  User: luz
  Date: 1/28/13
  Time: 3:39 PM
  To change this template use File | Settings | File Templates.
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<html>
    <head>
        <meta name="layout" content="main">


        <link href="${resource(dir: 'js/jquery/plugins/box/css', file: 'jquery.luz.box.css')}" rel="stylesheet">
        <script src="${resource(dir: 'js/jquery/plugins/box/js', file: 'jquery.luz.box.js')}"></script>

        <script src="${resource(dir: 'js/jquery/plugins/jquery-validation-1.9.0', file: 'jquery.validate.min.js')}"></script>
        <script src="${resource(dir: 'js/jquery/plugins/jquery-validation-1.9.0', file: 'custom-methods.js')}"></script>
        <script src="${resource(dir: 'js/jquery/plugins/jquery-validation-1.9.0', file: 'messages_es.js')}"></script>

        <script src="${resource(dir: 'js/jquery/i18n', file: 'jquery.ui.datepicker-es.js')}"></script>

        <title>Cronograma ejecución</title>


        <style type="text/css">
        th {
            vertical-align : middle !important;
        }

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

            /*.P {*/
            /*background : red !important;*/
            /*}*/

        th.S {
            background  : #f8f8ff !important;
            color       : #10718C !important;
            text-shadow : -2px 1px 3px rgba(0, 0, 0, 0.6) !important;
        }

        .item_row td.S {
            background : #BFBFC1 !important;
        }

        .item_prc td.S {
            background : #D1D1D6 !important;
        }

        .item_f td.S {
            background : #E5E5EA !important;
        }

        .selected, th.selected.S {
            background  : #10718C !important;
            color       : #f8f8ff !important;
            text-shadow : -2px 1px 3px rgba(255, 255, 255, 0.6) !important;
        }

            /*.click {*/
            /*cursor : pointer;*/
            /*}*/

        #divTabla {
            max-height : 705px;
            overflow   : auto;
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

        </style>

    </head>

    <body>
        <g:set var="meses" value="${obra.plazoEjecucionMeses + (obra.plazoEjecucionDias > 0 ? 1 : 0)}"/>

        <div class="tituloTree">
            Cronograma de ejecución de la obra ${obra.descripcion} (${meses} mes${obra.plazoEjecucionMeses == 1 ? "" : "es"})
        </div>

        <div class="btn-toolbar hide" id="toolbar">
            <div class="btn-group">
                <a href="${g.createLink(controller: 'contrato', action: 'verContrato', params: [contrato: contrato?.id])}" class="btn btn-ajax btn-new" id="atras" rel="tooltip" title="Regresar al contrato">
                    <i class="icon-arrow-left"></i>
                    Regresar
                </a>
            </div>

            <g:if test="${meses > 0}">
                <div class="btn-group">
                    <a href="#" class="btn btn-info" id="btnAmpl">
                        <i class="icon-resize-full"></i>
                        Ampliación
                    </a>
                    <a href="#" class="btn btn-info" id="btnSusp">
                        <i class="icon-resize-small"></i>
                        Suspensión
                    </a>
                    <a href="#" class="btn btn-info" id="btnFecha">
                        <i class="icon-calendar"></i>
                        Cambiar fecha de fin
                    </a>
                    %{--<a href="#" class="btn btn-danger disabled" id="btnDelSusp">--}%
                    %{--<i class="icon-trash"></i>--}%
                    %{--Eliminar Suspensión--}%
                    %{--</a>--}%
                </div>

                %{--<div class="btn-group">--}%
                    %{--<a href="#" class="btn btn-info disabled" id="btnCambio">--}%
                        %{--<i class="icon-exchange"></i>--}%
                        %{--Modificar--}%
                    %{--</a>--}%
                %{--</div>--}%

            %{--<div class="btn-group">--}%
            %{--<a href="#" class="btn" id="btnGrafico">--}%
            %{--<i class="icon-bar-chart"></i>--}%
            %{--Gráficos de avance--}%
            %{--</a>--}%
            %{--<a href="#" class="btn" id="btnGraficoEco">--}%
            %{--<i class="icon-bar-chart"></i>--}%
            %{--Gráfico de avance económico--}%
            %{--</a>--}%
            %{--<a href="#" class="btn" id="btnGraficoFis">--}%
            %{--<i class="icon-bar-chart"></i>--}%
            %{--Gráfico de avance físico--}%
            %{--</a>--}%
            %{--<g:link action="excel" class="btn" id="${obra.id}">--}%
            %{--<i class="icon-table"></i>--}%
            %{--Exportar a Excel--}%
            %{--</g:link>--}%
            %{--</div>--}%
            </g:if>
        </div>

        <div id="divTabla">

        </div>

        <div class="modal fade hide " id="modal-forms">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal">×</button>

                <h3 id="modalTitle-forms"></h3>
            </div>

            <div class="modal-body" id="modalBody-forms">

            </div>

            <div class="modal-footer" id="modalFooter-forms">
            </div>
        </div>


        <script type="text/javascript">
            function daydiff(first, second) {
                return (second - first) / (1000 * 60 * 60 * 24)
            }

            function updateDias() {
                var ini = $("#inicio").datepicker("getDate");
                var fin = $("#fin").datepicker("getDate");
                if (ini && fin) {
                    var dif = daydiff(ini, fin);
                    if (dif < 0) {
                        dif = 0;
                    }
                    $("#diasSuspension").text(dif + " día" + (dif == 1 ? "" : "s"));
                }
                if (ini) {
                    $("#fin").datepicker("option", "minDate", ini.add(1).days());
                }
                if (fin) {
                    $("#inicio").datepicker("option", "maxDate", fin.add(-1).days());
                }
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
                 37         -> flecha izq
                 39         -> flecha der
                 */
                return ((ev.keyCode >= 48 && ev.keyCode <= 57) ||
                        (ev.keyCode >= 96 && ev.keyCode <= 105) ||
//                        ev.keyCode == 190 || ev.keyCode == 110 ||
                        ev.keyCode == 8 || ev.keyCode == 46 || ev.keyCode == 9 ||
                        ev.keyCode == 37 || ev.keyCode == 39);
            }

            function updateTabla() {
                var divLoad = $("<div style='text-align: center;'></div>").html(spinnerBg).append("<br/>Cargando...Por favor espere...");
                $("#toolbar").hide();
                $("#divTabla").html(divLoad);
                $.ajax({
                    type    : "POST",
                    url     : "${createLink(action: 'tabla')}",
                    data    : {
                        id : ${obra.id}
                    },
                    success : function (msg) {
                        $("#divTabla").html(msg);
                        $("#toolbar").show();
                    }
                });
            }

            function log(msg) {
                ////console.log(msg);
            }

            $(function () {
                updateTabla();
            });

            $(function () {
                $("#btnCambio").click(function () {
                    if (!$(this).hasClass("disabled")) {
                        var $row = $(".item_row.rowSelected");
                        var vol = $row.data("vol");

                        $.ajax({
                            type    : "POST",
                            url     : "${createLink(action:'modificarVolumen')}",
                            data    : {
                                vol : vol
                            },
                            success : function (msg) {
                                var btnCancel = $('<a href="#" data-dismiss="modal" class="btn">Cancelar</a>');
                                var btnSave = $('<a href="#"  class="btn btn-success"><i class="icon-save"></i> Guardar</a>');

                                btnSave.click(function () {
                                    if ($("#frmSave-modificacion").valid()) {
                                        btnSave.replaceWith(spinner);
                                        %{--$.ajax({--}%
                                        %{--type    : "POST",--}%
                                        %{--url     : "${createLink(action:'ampliacion')}",--}%
                                        %{--data    : {--}%
                                        %{--obra : "${obra.id}",--}%
                                        %{--dias : $("#dias").val()--}%
                                        %{--},--}%
                                        %{--success : function (msg) {--}%
                                        %{--$("#modal-forms").modal("hide");--}%
                                        %{--updateTabla();--}%
                                        %{--}--}%
                                        %{--});--}%
                                    }
                                    return false;
                                });

                                $("#modalTitle-forms").html("Modificación");
                                $("#modalBody-forms").html(msg);
                                $("#modalFooter-forms").html("").append(btnCancel).append(btnSave);
                                $("#modal-forms").modal("show");
                            }
                        });

                    }
                    return false;
                });

                $("#btnAmpl").click(function () {
                    $.ajax({
                        type    : "POST",
                        url     : "${createLink(action:'ampliacion_ajax')}",
                        success : function (msg) {
                            var btnCancel = $('<a href="#" data-dismiss="modal" class="btn">Cancelar</a>');
                            var btnSave = $('<a href="#"  class="btn btn-success"><i class="icon-save"></i> Guardar</a>');

                            btnSave.click(function () {
                                if ($("#frmSave-ampliacion").valid()) {
                                    btnSave.replaceWith(spinner);
                                    $.ajax({
                                        type    : "POST",
                                        url     : "${createLink(action:'ampliacion')}",
                                        data    : {
                                            obra : "${obra.id}",
                                            dias : $("#dias").val()
                                        },
                                        success : function (msg) {
                                            $("#modal-forms").modal("hide");
                                            updateTabla();
                                        }
                                    });
                                }
                                return false;
                            });

                            $("#modalTitle-forms").html("Ampliación");
                            $("#modalBody-forms").html(msg);
                            $("#modalFooter-forms").html("").append(btnCancel).append(btnSave);
                            $("#modal-forms").modal("show");

                        }
                    });
                    return false;
                });
                $("#btnSusp").click(function () {
                    $.ajax({
                        type    : "POST",
                        url     : "${createLink(action:'suspension_ajax')}",
                        data    : {
                            obra : "${obra.id}"
                        },
                        success : function (msg) {
                            var btnCancel = $('<a href="#" data-dismiss="modal" class="btn">Cancelar</a>');
                            var btnSave = $('<a href="#"  class="btn btn-success"><i class="icon-save"></i> Guardar</a>');

                            btnSave.click(function () {
                                if ($("#frmSave-suspension").valid()) {
                                    btnSave.replaceWith(spinner);
                                    $.ajax({
                                        type    : "POST",
                                        url     : "${createLink(action:'suspension')}",
                                        data    : {
                                            obra : "${obra.id}",
                                            ini  : $("#inicio").val(),
                                            fin  : $("#fin").val()
                                        },
                                        success : function (msg) {
//                                            ////console.log(msg);
                                            $("#modal-forms").modal("hide");
                                            updateTabla();
                                        }
                                    });
                                }
                                return false;
                            });

                            $("#modalTitle-forms").html("Suspensión");
                            $("#modalBody-forms").html(msg);
                            $("#modalFooter-forms").html("").append(btnCancel).append(btnSave);
                            $("#modal-forms").modal("show");

                        }
                    });
                    return false;
                });

//                $("#btnDelSusp").click(function () {
//                    if (!$(this).hasClass("disabled")) {
//                        var per = $(".selected").data("periodo");
//
//                        $.box({
//                            imageClass : "box_info",
//                            title      : "Confirmación",
//                            text       : "Está seguro de querer eliminar esta suspensión? Esta acción no se puede deshacer.",
//                            iconClose  : false,
//                            dialog     : {
//                                resizable     : false,
//                                draggable     : false,
//                                closeOnEscape : false,
//                                buttons       : {
//                                    "Aceptar"  : function () {
//                                        ////console.log("KK")
//                                    },
//                                    "Cancelar" : function () {
//                                    }
//                                }
//                            }
//                        });
//                    }
//                    return false;
//                });

                $("#btnFecha").click(function () {
                    $.ajax({
                        type    : "POST",
                        url     : "${createLink(action:'cambioFecha_ajax')}",
                        data    : {
                            obra : "${obra.id}"
                        },
                        success : function (msg) {
                            var btnCancel = $('<a href="#" data-dismiss="modal" class="btn">Cancelar</a>');
                            var btnSave = $('<a href="#"  class="btn btn-success"><i class="icon-save"></i> Guardar</a>');

                            btnSave.click(function () {
                                if ($("#frmSave-suspension").valid()) {
                                    btnSave.replaceWith(spinner);
                                    $.ajax({
                                        type    : "POST",
                                        url     : "${createLink(action:'cambioFecha')}",
                                        data    : {
                                            obra  : "${obra.id}",
                                            fecha : $("#fecha").val()
                                        },
                                        success : function (msg) {
                                            //                                            ////console.log(msg);
                                            $("#modal-forms").modal("hide");
                                            updateTabla();
                                        }
                                    });
                                }
                                return false;
                            });

                            $("#modalTitle-forms").html("Cambio de fecha");
                            $("#modalBody-forms").html(msg);
                            $("#modalFooter-forms").html("").append(btnCancel).append(btnSave);
                            $("#modal-forms").modal("show");

                        }
                    });
                    return false;
                });

            });


        </script>

    </body>
</html>