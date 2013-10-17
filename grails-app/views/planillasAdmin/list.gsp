<%@ page import="janus.ejecucion.PlanillaAdmin" %>
<!doctype html>
<html>
    <head>
        <meta name="layout" content="main">
        <title>
            Planillas Adm. directa
        </title>
        <script src="${resource(dir: 'js/jquery/plugins/jquery-validation-1.9.0', file: 'jquery.validate.min.js')}"></script>
        <script src="${resource(dir: 'js/jquery/plugins/jquery-validation-1.9.0', file: 'messages_es.js')}"></script>
    </head>

    <body>

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

        <g:set var="cont" value="${1}"/>


        <div class="tituloTree">
            Planillas de administración directa de la obra ${obra.descripcion}
        </div>

        <div class="row">
            <div class="span9" role="navigation">
                <div class="btn-group">

                    <g:link action="form" class="btn" params="[obra: obra.id]">
                        <i class="icon-file"></i>
                        Nueva planilla
                    </g:link>
                </div>
            </div>

            <div class="span3" id="busqueda-Planilla"></div>
        </div>

        %{--<div class="row">--}%
        %{--<div class="span12" role="navigation">--}%
        %{--<g:if test="${obra.fechaInicio}">--}%
        %{--<a href="#" class="btn  " id="imprimir">--}%
        %{--<i class="icon-print"></i>--}%
        %{--Imprimir Orden de Inicio de Obra--}%
        %{--</a>--}%
        %{--</g:if>--}%
        %{--</div>--}%
        %{--</div>--}%

        <g:form action="delete" name="frmDelete-Planilla">
            <g:hiddenField name="id"/>
        </g:form>

        <div id="list-Planilla" role="main" style="margin-top: 10px;">

            <table class="table table-bordered table-striped table-condensed table-hover">
                <thead>
                    <tr>
                        <g:sortableColumn property="numero" title="#"/>
                        <g:sortableColumn property="tipoPlanilla" title="Tipo"/>
                        %{--<g:sortableColumn property="estadoPlanilla" title="Estado"/>--}%
                        <g:sortableColumn property="fechaIngreso" title="Fecha ingreso"/>
                        <g:sortableColumn property="descripcion" title="Descripcion"/>
                        <g:sortableColumn property="valor" title="Valor"/>
                        <th width="160">Acciones</th>
                    </tr>
                </thead>
                <tbody class="paginate">
                    <g:each in="${list}" status="i" var="planillaInstance">
                        <tr style="font-size: 10px">
                            <td>${fieldValue(bean: planillaInstance, field: "numero")}</td>
                            <td>
                                ${planillaInstance.tipoPlanilla.nombre}

                            </td>
                            <td>
                                <g:formatDate date="${planillaInstance.fechaIngreso}" format="dd-MM-yyyy"/>
                            </td>
                            <td>${planillaInstance.descripcion}</td>
                            <td class="numero">
                                <g:formatNumber number="${planillaInstance.valor}" maxFractionDigits="2" minFractionDigits="2" format="##,##0" locale="ec"/>
                            </td>
                            <td>
                                <g:if test="${planillaInstance.tipoPlanilla.codigo == 'M'}">
                                    <g:link controller="detallePlanillaCostoAdmin" action="detalleCosto" id="${planillaInstance.id}" params="[obra: obra.id]" rel="tooltip" title="Detalles" class="btn btn-small">
                                        <i class="icon-reorder icon-large"></i>
                                    </g:link>
                                    <g:link controller="reportesPlanillasAdmin" action="reporteMateriales" id="${planillaInstance.id}" params="[obra: obra.id]" rel="tooltip" title="Imprimir" class="btn btn-small">
                                        <i class="icon-print icon-large"></i>
                                    </g:link>
                                </g:if>
                                <g:else>
                                    <g:link action="detalle" id="${planillaInstance.id}" params="[obra: obra.id]" rel="tooltip" title="Detalles" class="btn btn-small">
                                        <i class="icon-reorder icon-large"></i>
                                    </g:link>
                                    <g:link controller="reportesPlanillasAdmin" action="reporteDetalle" id="${planillaInstance.id}" params="[obra: obra.id]" rel="tooltip" title="Imprimir" class="btn btn-small">
                                        <i class="icon-print icon-large"></i>
                                    </g:link>
                                </g:else>
                            %{--<g:link controller="reportesPlanillas" action="reportePlanilla" id="${planillaInstance.id}" class="btn btnPrint  btn-small btn-ajax" rel="tooltip" title="Imprimir">--}%
                            %{--<i class="icon-print"></i>--}%
                            %{--</g:link>--}%
                            </td>

                        </tr>
                    </g:each>
                </tbody>
            </table>

        </div>

        <div class="modal hide fade mediumModal" id="modal-Planilla">
            <div class="modal-header" id="modalHeader">
                <button type="button" class="close darker" data-dismiss="modal">
                    <i class="icon-remove-circle"></i>
                </button>

                <h3 id="modalTitle"></h3>
            </div>

            <div class="modal-body" id="modalBody">
            </div>

            <div class="modal-footer" id="modalFooter">
            </div>
        </div>


        <div id="errorImpresion">
            <fieldset>
                <div class="spa3" style="margin-top: 30px; margin-left: 10px">

                    Debe ingresar un número de Oficio!

                </div>
            </fieldset>
        </div>



        <script type="text/javascript">
            var url = "${resource(dir:'images', file:'spinner_24.gif')}";
            var spinner = $("<img style='margin-left:15px;' src='" + url + "' alt='Cargando...'/>");

            function submitForm(btn) {
                if ($("#frmSave-Planilla").valid()) {
                    btn.replaceWith(spinner);
                }
                $("#frmSave-Planilla").submit();
            }

            $(function () {
                $('[rel=tooltip]').tooltip();

                $(".paginate").paginate({
                    maxRows        : 10,
                    searchPosition : $("#busqueda-Planilla"),
                    float          : "right"
                });

                $(".btnPedidoPagoAnticipo").click(function () {
                    %{--var url = "${createLink(controller: 'reportes',action: 'anticipoReporte')}/" + $(this).data("id");--}%
                    %{--location.href = "${g.createLink(controller: 'pdf',action: 'pdfLink')}?url=" + url + "&filename=Memo_pedido_pago_" + "${new Date().format('ddMMyyyy_hhmm')}" + ".pdf";--}%
                    %{--var url = "${createLink(controller: 'reportes',action: 'anticipoReporte')}/" + $(this).data("id");--}%
                    %{--location.href = "${g.createLink(controller: 'pdf',action: 'pdfLink')}?url=" + url + "&filename=Memo_pedido_pago_" + "${new Date().format('ddMMyyyy_hhmm')}" + ".pdf";--}%
                    location.href = "${g.createLink(controller: 'reportesPlanillas',action: 'memoPedidoPagoAnticipo')}/" + $(this).data("id");
                    return false;
                });

                $(".btnPedidoPago").click(function () {
                    %{--var url = "${createLink(controller: 'reportes',action: 'anticipoReporte')}/" + $(this).data("id");--}%
                    %{--location.href = "${g.createLink(controller: 'pdf',action: 'pdfLink')}?url=" + url + "&filename=Memo_pedido_pago_" + "${new Date().format('ddMMyyyy_hhmm')}" + ".pdf";--}%
                    location.href = "${g.createLink(controller: 'reportesPlanillas',action: 'memoPedidoPago')}/" + $(this).data("id");
                    return false;
                });

                $(".btn-pagar").click(function () {
                    var $btn = $(this);
                    var tipo = $btn.data("tipo").toString();
                    $.ajax({
                        type    : "POST",
                        url     : "${createLink(action:'pago_ajax')}",
                        data    : {
                            id   : $btn.data("id"),
                            tipo : tipo
                        },
                        success : function (msg) {
                            var btnOk = $('<a href="#" data-dismiss="modal" class="btn">Cancelar</a>');
                            var btnSave = $('<a href="#"  class="btn btn-success"><i class="icon-save"></i> Guardar</a>');

                            btnSave.click(function () {
                                submitForm(btnSave);
                                return false;
                            });
                            $("#modalTitle").html($btn.text());
//                            switch (tipo) {
//                                case "2":
//                                    $("#modalTitle").html("Enviar reajuste");
//                                    break;
//                                case "3":
//                                    $("#modalTitle").html("Pedir pago");
//                                    break;
//                                case "4":
//                                    $("#modalTitle").html("Informar pago");
//                                    break;
//                                case "5":
//                                    $("#modalTitle").html("Iniciar obra");
//                                    break;
//                            }

                            $("#modalHeader").removeClass("btn-edit btn-show btn-delete");

                            if (msg == "NO") {
                                $("#modalBody").html("Ha ocurrido un error: No se encontró un administrador activo para el contrato.<br/>Por favor asigne uno desde la página del contrato en la opción Administrador.");
                                btnOk.text("Aceptar");
                                $("#modalFooter").html("").append(btnOk);
                            } else {
                                $("#modalBody").html(msg);
                                if (msg.startsWith("No")) {
                                    btnOk.text("Aceptar");
                                    $("#modalFooter").html("").append(btnOk);
                                } else {
                                    $("#modalFooter").html("").append(btnOk).append(btnSave);
                                }
                            }

                            $("#modal-Planilla").modal("show");
                        }
                    });
                    return false;
                }); //click btn new

                $(".btn-new").click(function () {
                    $.ajax({
                        type    : "POST",
                        url     : "${createLink(action:'form_ajax')}",
                        success : function (msg) {
                            var btnOk = $('<a href="#" data-dismiss="modal" class="btn">Cancelar</a>');
                            var btnSave = $('<a href="#"  class="btn btn-success"><i class="icon-save"></i> Guardar</a>');

                            btnSave.click(function () {
                                submitForm(btnSave);
                                return false;
                            });

                            $("#modalHeader").removeClass("btn-edit btn-show btn-delete");
                            $("#modalTitle").html("Crear Planilla");
                            $("#modalBody").html(msg);
                            $("#modalFooter").html("").append(btnOk).append(btnSave);
                            $("#modal-Planilla").modal("show");
                        }
                    });
                    return false;
                }); //click btn new

                $(".btn-edit").click(function () {
                    var id = $(this).data("id");
                    $.ajax({
                        type    : "POST",
                        url     : "${createLink(action:'form_ajax')}",
                        data    : {
                            id : id
                        },
                        success : function (msg) {
                            var btnOk = $('<a href="#" data-dismiss="modal" class="btn">Cancelar</a>');
                            var btnSave = $('<a href="#"  class="btn btn-success"><i class="icon-save"></i> Guardar</a>');

                            btnSave.click(function () {
                                submitForm(btnSave);
                                return false;
                            });

                            $("#modalHeader").removeClass("btn-edit btn-show btn-delete").addClass("btn-edit");
                            $("#modalTitle").html("Editar Planilla");
                            $("#modalBody").html(msg);
                            $("#modalFooter").html("").append(btnOk).append(btnSave);
                            $("#modal-Planilla").modal("show");
                        }
                    });
                    return false;
                }); //click btn edit

                $(".btn-show").click(function () {
                    var id = $(this).data("id");
                    $.ajax({
                        type    : "POST",
                        url     : "${createLink(action:'show_ajax')}",
                        data    : {
                            id : id
                        },
                        success : function (msg) {
                            var btnOk = $('<a href="#" data-dismiss="modal" class="btn btn-primary">Aceptar</a>');
                            $("#modalHeader").removeClass("btn-edit btn-show btn-delete").addClass("btn-show");
                            $("#modalTitle").html("Ver Planilla");
                            $("#modalBody").html(msg);
                            $("#modalFooter").html("").append(btnOk);
                            $("#modal-Planilla").modal("show");
                        }
                    });
                    return false;
                }); //click btn show

                $(".btn-delete").click(function () {
                    var id = $(this).data("id");
                    $("#id").val(id);
                    var btnOk = $('<a href="#" data-dismiss="modal" class="btn">Cancelar</a>');
                    var btnDelete = $('<a href="#" class="btn btn-danger"><i class="icon-trash"></i> Eliminar</a>');

                    btnDelete.click(function () {
                        btnDelete.replaceWith(spinner);
                        $("#frmDelete-Planilla").submit();
                        return false;
                    });

                    $("#modalHeader").removeClass("btn-edit btn-show btn-delete").addClass("btn-delete");
                    $("#modalTitle").html("Eliminar Planilla");
                    $("#modalBody").html("<p>¿Está seguro de querer eliminar esta Planilla?</p>");
                    $("#modalFooter").html("").append(btnOk).append(btnDelete);
                    $("#modal-Planilla").modal("show");
                    return false;
                });

                $("#imprimir").click(function () {
                    location.href = "${g.createLink(controller: 'reportesPlanillas', action: 'reporteContrato', id: obra?.id)}?oficio=" + $("#oficio").val() + "&firma=" + $("#firma").val();
//                    $("#imprimirDialog").dialog("open");

                });

                $("#errorImpresion").dialog({


                    autoOpen  : false,
                    resizable : false,
                    modal     : true,
                    draggable : false,
                    width     : 320,
                    height    : 200,
                    position  : 'center',
                    title     : 'Error',
                    buttons   : {
                        "Aceptar" : function () {

                            $("#errorImpresion").dialog("close")

                        }
                    }


                });

            });

        </script>

    </body>
</html>
