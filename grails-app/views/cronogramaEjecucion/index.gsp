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

    <link href="${resource(dir: 'css', file: 'cronograma.css')}" rel="stylesheet">
    <title>Cronograma ejecución</title>
</head>

<body>
<g:set var="meses" value="${obra.plazoEjecucionMeses + (obra.plazoEjecucionDias > 0 ? 1 : 0)}"/>

<div class="tituloTree">
    Cronograma de ejecución de la obra ${obra.nombre} (${meses} mes${meses == 1 ? "" : "es"})
</div>

<div class="btn-toolbar hide" id="toolbar">
    <div class="btn-group">
        <a href="${g.createLink(controller: 'contrato', action: 'verContrato', params: [contrato: contrato?.id])}"
           class="btn btn-ajax btn-new" id="atras" rel="tooltip" title="Regresar al contrato">
            <i class="icon-arrow-left"></i>
            Regresar
        </a>
    </div>

    <g:if test="${meses > 0}">
        <g:if test="${contrato.fiscalizador?.id == session.usuario.id}">
            <div class="btn-group">
                <g:if test="${suspensiones.size() == 0}">
                    <a href="#" class="btn btn-info" id="btnAmpl">
                        <i class="icon-resize-full"></i>
                        Ampliación
                    </a>
                    <a href="#" class="btn btn-info" id="btnModif">
                        <i class="icon-retweet"></i>
                        Modificación
                    </a>
                    <a href="#" class="btn btn-info" id="btnSusp">
                        <i class="icon-resize-small"></i>
                        Suspensión
                    </a>
                </g:if>
                <g:else>
                    <a href="#" class="btn btn-info" id="btnEndSusp">
                        <i class="icon-resize-small"></i>
                        Terminar Suspensión
                    </a>
                </g:else>
            </div>

            <div class="btn-group">
                <g:if test="${suspensiones.size() == 0}">
                    <a href="${g.createLink(action: 'actualizaPrej', params: [contrato: contrato?.id])}"
                       class="btn btn-info">
                        <i class="icon-resize-full"></i>
                        Actualizar Periodos
                    </a>
                </g:if>
            </div>

%{--
            <div class="btn-group">
                <g:if test="${complementario}">
                    <a href="#" class="btn btn-warning" id="btnComp">
                        <i class="icon-calendar"></i>
                        Complementario
                    </a>
                </g:if>
            </div>
--}%

            <div class="btn" style="height: 30px">
                De:
                <g:field type="number" name="desde" step="1" pattern="#" value="${desde}" min="0" max="${hasta+1}" class="input-mini"/>
                a:
                <g:field type="number" name="hasta" step="1" pattern="#" value="${hasta}" min="0" max="${maximo}" class="input-mini"/>
                <a href="#" id="btnRango" class="btn" style="margin-top: -8px;"> <i class="icon-check"></i> Ir</a>
                <a href="#" id="btnTodos" class="btn" style="margin-top: -8px;"> <i class="icon-all"></i>Todo</a>
            </div>
        </g:if>

        <a href="#" id="btnReporte" class="btn">
            <i class="icon-print"></i>
            Imprimir
        </a>
    </g:if>
</div>

<g:if test="${flash.message}">
    <div class="row">
        <div class="span12">
            <div class="alert ${flash.clase ?: 'alert-info'}" role="status">
                <a class="close" data-dismiss="alert" href="#">×</a>
                ${flash.message}
            </div>
        </div>
    </div>
</g:if>


<div>
    La ruta crítica se muestra con los rubros marcados en amarillo
</div>

<g:if test="${suspensiones.size() != 0}">
    <div class="alert alert-danger">
        <strong>La obra se encuentra suspendida desde ${ini*.format("dd-MM-yyyy")}</strong>
    </div>
</g:if>

<div id="divTabla" style="max-height: 650px; overflow: auto;">

</div>

<div class="modal fade hide long" id="modal-forms" style="height: 600px">
    <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal">×</button>

        <h3 id="modalTitle-forms"></h3>
    </div>

    <div class="modal-body" id="modalBody-forms" style="max-height: 450px;">

    </div>

    <div class="modal-footer" id="modalFooter-forms">
    </div>
</div>


<script type="text/javascript">
    function daydiff(first, second) {
        return (second - first) / (1000 * 60 * 60 * 24)
    }

    function updateDias() {
        var ini = $("#ini").datepicker("getDate");
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
            type: "POST",
            url: "${createLink(action: 'tabla')}",
            data: {
                id: ${obra.id},
                desde: $("#desde").val(),
                hasta: $("#hasta").val()
            },
            success: function (msg) {
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
                    type: "POST",
                    url: "${createLink(action:'modificarVolumen')}",
                    data: {
                        vol: vol
                    },
                    success: function (msg) {
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
                type: "POST",
                url: "${createLink(action:'ampliacion_ajax')}",
                success: function (msg) {
                    var btnCancel = $('<a href="#" data-dismiss="modal" class="btn">Cancelar</a>');
                    var btnSave = $('<a href="#"  class="btn btn-success"><i class="icon-save"></i> Guardar</a>');

                    btnSave.click(function () {
                        if ($("#frmSave-ampliacion").valid()) {
                            btnSave.replaceWith(spinner);
                            var data = $("#frmSave-ampliacion").serialize();
                            data += "&obra=${obra.id}&contrato=${contrato.id}";
                            $.ajax({
                                type: "POST",
                                url: "${createLink(action:'ampliacion')}",
                                data: data,
                                success: function (msg) {
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

        $("#btnEndSusp").click(function () {
            $.ajax({
                type: "POST",
                url: "${createLink(action:'terminaSuspension_ajax')}",
                data: {
                    obra: "${obra.id}"
                },
                success: function (msg) {
                    var btnCancel = $('<a href="#" data-dismiss="modal" class="btn">Cancelar</a>');
                    var btnSave = $('<a href="#"  class="btn btn-success"><i class="icon-save"></i> Guardar</a>');

                    btnSave.click(function () {
                        if ($("#frmSave-terminaSuspension").valid()) {
                            btnSave.replaceWith(spinner);
                            var data = $("#frmSave-terminaSuspension").serialize();
                            data += "&cntr=${contrato.id}";
                            $.ajax({
                                type: "POST",
                                url: "${createLink(action:'terminaSuspensionTemp')}",
                                data: data,
                                success: function (msg) {
//                                            ////console.log(msg);
                                    $("#modal-forms").modal("hide");
                                    location.reload(true);
                                }
                            });
                        }
                        return false;
                    });

                    $("#modalTitle-forms").html("Terminar Suspensión");
                    $("#modalBody-forms").html(msg);
                    $("#modalFooter-forms").html("").append(btnCancel).append(btnSave);
                    $("#modal-forms").modal("show");

                }
            });
        });

        $("#btnSusp").click(function () {
            $.ajax({
                type: "POST",
                url: "${createLink(action:'suspension_ajax')}",
                data: {
                    obra: "${obra.id}"
                },
                success: function (msg) {
                    var btnCancel = $('<a href="#" data-dismiss="modal" class="btn">Cancelar</a>');
                    var btnSave = $('<a href="#"  class="btn btn-success"><i class="icon-save"></i> Guardar</a>');

                    btnSave.click(function () {
                        if ($("#frmSave-suspension").valid()) {
                            btnSave.replaceWith(spinner);
                            var data = $("#frmSave-suspension").serialize();
                            data += "&obra=${obra.id}";
                            data += "&cntr=${contrato.id}";
                            $.ajax({
                                type: "POST",
                                url: "${createLink(action:'suspensionNueva')}",
                                data: data,
                                success: function (msg) {
//                                            ////console.log(msg);
                                    $("#modal-forms").modal("hide");
                                    location.reload(true);
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

        $("#btnModif").click(function () {
            var vol = $(".rowSelected").first().data("vol");
            if (vol) {
                $.ajax({
                    type: "POST",
                    url: "${createLink(action: 'modificacion_ajax')}",
                    data: {
                        obra: "${obra.id}",
                        contrato: "${contrato.id}",
                        vol: vol
                    },
                    success: function (msg) {
                        var btnCancel = $('<a href="#" data-dismiss="modal" class="btn">Cancelar</a>');
                        var btnSave = $('<a href="#"  class="btn btn-success"><i class="icon-save"></i> Guardar</a>');

                        btnSave.click(function () {
                            btnSave.replaceWith(spinner);
                            var data = "obra=${obra.id}";
                            $(".tiny").each(function () {
                                var tipo = $(this).data("tipo");
                                var val = parseFloat($(this).val()) /*+ parseFloat($(this).data("val1"))*/;
                                var crono = $(this).data("id");
                                var periodo = $(this).data("id2");
                                var vol = $(this).data("id3");
                                data += "&" + (tipo + "=" + val + "_" + periodo + "_" + vol + "_" + crono);
                            });
                            $.ajax({
                                type: "POST",
                                url: "${createLink(action:'modificacion')}",
                                data: data,
                                success: function (msg) {
//                                            console.log(msg);
                                    $("#modal-forms").modal("hide");
                                    updateTabla();
                                }
                            });
                            return false;
                        });

                        $("#modalTitle-forms").html("Modificación");
                        $("#modalBody-forms").html(msg);
                        $("#modalFooter-forms").html("").append(btnCancel).append(btnSave);
                        $("#modal-forms").modal("show");
                    }
                });
            } else {
                var btnCancel = $('<a href="#" data-dismiss="modal" class="btn">Aceptar</a>');
                $("#modalTitle-forms").html("Modificación");
                $("#modalBody-forms").html("Seleccione el rubro a modificar haciendo click sobre la fila adecuada (la fila tomará un color azul - o verde si es parte de la ruta crítica)");
                $("#modalFooter-forms").html("").append(btnCancel);
                $("#modal-forms").modal("show");
            }
            return false;
        });

        $("#btnComp").click(function () {
            $.box({
                imageClass: "box_info",
                title: "Integrar Cronograma del Complementario",
                text: "Está seguro de querer integrar el cronograma de contrato complementario?<br>Esta acción no se puede deshacer.",
                iconClose: false,
                dialog: {resizable: false, draggable: false, closeOnEscape: false,
                    buttons: {
                        "Aceptar": function () {
                            $.ajax({
                                type: "POST",
                                url: "${createLink(action: 'armaCrcrComp')}",
                                data: {
                                    contrato: "${contrato.id}"
                                },
                                success: function (msg) {
                                    console.log(msg);
                                }
                            })
                        },
                        "Cancelar": function () {
                        }
                    }
                }
            });
            return false;
        });

        $("#btnFecha").click(function () {
            $.ajax({
                type: "POST",
                url: "${createLink(action:'cambioFecha_ajax')}",
                data: {
                    obra: "${obra.id}"
                },
                success: function (msg) {
                    var btnCancel = $('<a href="#" data-dismiss="modal" class="btn">Cancelar</a>');
                    var btnSave = $('<a href="#"  class="btn btn-success"><i class="icon-save"></i> Guardar</a>');

                    btnSave.click(function () {
                        if ($("#frmSave-suspension").valid()) {
                            btnSave.replaceWith(spinner);
                            $.ajax({
                                type: "POST",
                                url: "${createLink(action:'cambioFecha')}",
                                data: {
                                    obra: "${obra.id}",
                                    fecha: $("#fecha").val()
                                },
                                success: function (msg) {
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

        $("#btnReporte").click(function () {
            %{--todo: hacer otro reporte para el cronograma de ejecución, puesto que se usa prej en lugar de meses --}%
            location.href = "${createLink(controller: 'reportes2', action:'reporteCronogramaEjec', id:contrato.id)}";
            return false;
        });

        $("#btnRango").click(function () {
            var dsde = $("#desde").val()
            var hsta = $("#hasta").val()
            location.href = "${createLink(action:'index', id:contrato.id)}" + "?desde=" + dsde + "&hasta=" + hsta;
            return false;
        });

        $("#btnTodos").click(function () {
            location.href = "${createLink(action:'index', id:contrato.id)}" + "?desde=1&hasta=1000";
            return false;
        });

    });
</script>
</body>
</html>