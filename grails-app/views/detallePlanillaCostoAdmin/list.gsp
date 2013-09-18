
<%@ page import="janus.ejecucion.DetallePlanillaCostoAdmin" %>
<!doctype html>
<html>
    <head>
        <meta name="layout" content="main">
        <title>
            Lista de Detalle Planilla Costo Admins
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

        <div class="row">
            <div class="span9 btn-group" role="navigation">
                <a href="#" class="btn btn-ajax btn-new">
                    <i class="icon-file"></i>
                    Crear  Detalle Planilla Costo Admin
                </a>
            </div>
            <div class="span3" id="busqueda-DetallePlanillaCostoAdmin"></div>
        </div>

        <g:form action="delete" name="frmDelete-DetallePlanillaCostoAdmin">
            <g:hiddenField name="id"/>
        </g:form>

        <div id="list-DetallePlanillaCostoAdmin" role="main" style="margin-top: 10px;">

            <table class="table table-bordered table-striped table-condensed table-hover">
                <thead>
                    <tr>
                    
                        <g:sortableColumn property="factura" title="Factura" />
                    
                        <g:sortableColumn property="indirectos" title="Indirectos" />
                    
                        <g:sortableColumn property="monto" title="Monto" />
                    
                        <g:sortableColumn property="montoIndirectos" title="Monto Indirectos" />
                    
                        <g:sortableColumn property="montoIva" title="Monto Iva" />
                    
                        <th>Planilla</th>
                    
                        <th width="150">Acciones</th>
                    </tr>
                </thead>
                <tbody class="paginate">
                <g:each in="${detallePlanillaCostoAdminInstanceList}" status="i" var="detallePlanillaCostoAdminInstance">
                    <tr>
                    
                        <td>${fieldValue(bean: detallePlanillaCostoAdminInstance, field: "factura")}</td>
                    
                        <td>${fieldValue(bean: detallePlanillaCostoAdminInstance, field: "indirectos")}</td>
                    
                        <td>${fieldValue(bean: detallePlanillaCostoAdminInstance, field: "monto")}</td>
                    
                        <td>${fieldValue(bean: detallePlanillaCostoAdminInstance, field: "montoIndirectos")}</td>
                    
                        <td>${fieldValue(bean: detallePlanillaCostoAdminInstance, field: "montoIva")}</td>
                    
                        <td>${fieldValue(bean: detallePlanillaCostoAdminInstance, field: "planilla")}</td>
                    
                        <td>
                            <a class="btn btn-small btn-show btn-ajax" href="#" rel="tooltip" title="Ver" data-id="${detallePlanillaCostoAdminInstance.id}">
                                <i class="icon-zoom-in icon-large"></i>
                            </a>
                            <a class="btn btn-small btn-edit btn-ajax" href="#" rel="tooltip" title="Editar" data-id="${detallePlanillaCostoAdminInstance.id}">
                                <i class="icon-pencil icon-large"></i>
                            </a>

                            <a class="btn btn-small btn-delete" href="#" rel="tooltip" title="Eliminar" data-id="${detallePlanillaCostoAdminInstance.id}">
                                <i class="icon-trash icon-large"></i>
                            </a>
                        </td>
                    </tr>
                </g:each>
                </tbody>
            </table>

        </div>

        <div class="modal hide fade" id="modal-DetallePlanillaCostoAdmin">
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

        <script type="text/javascript">
            var url = "${resource(dir:'images', file:'spinner_24.gif')}";
            var spinner = $("<img style='margin-left:15px;' src='" + url + "' alt='Cargando...'/>");

            function submitForm(btn) {
                if ($("#frmSave-DetallePlanillaCostoAdmin").valid()) {
                    btn.replaceWith(spinner);
                }
                $("#frmSave-DetallePlanillaCostoAdmin").submit();
            }

            $(function () {
                $('[rel=tooltip]').tooltip();

                $(".paginate").paginate({
                    maxRows        : 10,
                    searchPosition : $("#busqueda-DetallePlanillaCostoAdmin"),
                    float          : "right"
                });

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
                            $("#modalTitle").html("Crear Detalle Planilla Costo Admin");
                            $("#modalBody").html(msg);
                            $("#modalFooter").html("").append(btnOk).append(btnSave);
                            $("#modal-DetallePlanillaCostoAdmin").modal("show");
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
                            $("#modalTitle").html("Editar Detalle Planilla Costo Admin");
                            $("#modalBody").html(msg);
                            $("#modalFooter").html("").append(btnOk).append(btnSave);
                            $("#modal-DetallePlanillaCostoAdmin").modal("show");
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
                            $("#modalTitle").html("Ver Detalle Planilla Costo Admin");
                            $("#modalBody").html(msg);
                            $("#modalFooter").html("").append(btnOk);
                            $("#modal-DetallePlanillaCostoAdmin").modal("show");
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
                        $("#frmDelete-DetallePlanillaCostoAdmin").submit();
                        return false;
                    });

                    $("#modalHeader").removeClass("btn-edit btn-show btn-delete").addClass("btn-delete");
                    $("#modalTitle").html("Eliminar Detalle Planilla Costo Admin");
                    $("#modalBody").html("<p>¿Está seguro de querer eliminar este Detalle Planilla Costo Admin?</p>");
                    $("#modalFooter").html("").append(btnOk).append(btnDelete);
                    $("#modal-DetallePlanillaCostoAdmin").modal("show");
                    return false;
                });

            });

        </script>

    </body>
</html>
