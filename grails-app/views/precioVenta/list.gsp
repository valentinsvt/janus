
<%@ page import="janus.PrecioVenta" %>
<!doctype html>
<html>
    <head>
        <meta name="layout" content="main">
        <title>
            Lista de Precio de Venta
        </title>
        <script src="${resource(dir: 'js/jquery/plugins/jquery-validation-1.9.0', file: 'jquery.validate.min.js')}"></script>
        <script src="${resource(dir: 'js/jquery/plugins/jquery-validation-1.9.0', file: 'messages_es.js')}"></script>
    </head>
    <body>

        <div class="span12">
            <g:if test="${flash.message}">
                <div class="alert ${flash.clase ?: 'alert-info'}" role="status">
                    <a class="close" data-dismiss="alert" href="#">×</a>
                    ${flash.message}
                </div>
            </g:if>
        </div>

        <div class="span12 btn-group" role="navigation">
            <a href="#" class="btn btn-ajax btn-new">
                <i class="icon-file"></i>
                Nuevo Precio de Venta
            </a>
        </div>

        <g:form action="delete" name="frmDelete-precioVentaInstance">
            <g:hiddenField name="id"/>
        </g:form>

        <div id="list-precioVenta" class="span12" role="main" style="margin-top: 10px;">

            <table class="table table-bordered table-striped table-condensed table-hover">
                <thead>
                    <tr>
                    
                        <g:sortableColumn property="nombre" title="Nombre" />
                    
                        <g:sortableColumn property="sigla" title="Sigla" />
                    
                        <g:sortableColumn property="ruc" title="Ruc" />
                    
                        <g:sortableColumn property="responsable" title="Responsable" />
                    
                        <th>Especialidad Proveedor</th>
                    
                        <th width="150">Acciones</th>
                    </tr>
                </thead>
                <tbody>
                <g:each in="${precioVentaInstanceList}" status="i" var="precioVentaInstance">
                    <tr>
                    
                        <td>${fieldValue(bean: precioVentaInstance, field: "nombre")}</td>
                    
                        <td>${fieldValue(bean: precioVentaInstance, field: "sigla")}</td>
                    
                        <td>${fieldValue(bean: precioVentaInstance, field: "ruc")}</td>
                    
                        <td>${fieldValue(bean: precioVentaInstance, field: "responsable")}</td>
                    
                        <td>${fieldValue(bean: precioVentaInstance, field: "especialidadProveedor")}</td>
                    
                        <td>${fieldValue(bean: precioVentaInstance, field: "direccion")}</td>
                    
                        <td>
                            <a class="btn btn-small btn-show btn-ajax" href="#" rel="tooltip" title="Ver" data-id="${precioVentaInstance.id}">
                                <i class="icon-zoom-in"></i>
                            </a>
                            <a class="btn btn-small btn-edit btn-ajax" href="#" rel="tooltip" title="Editar" data-id="${precioVentaInstance.id}">
                                <i class="icon-pencil"></i>
                            </a>

                            <a class="btn btn-small btn-delete" href="#" rel="tooltip" title="Eliminar" data-id="${precioVentaInstance.id}">
                                <i class="icon-trash"></i>
                            </a>
                        </td>
                    </tr>
                </g:each>
                </tbody>
            </table>
            <div class="pagination">
                <elm:paginate total="${precioVentaInstanceTotal}" params="${params}" />
            </div>
        </div>

        <div class="modal hide fade" id="modal-precioVenta">
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
            var url = "${resource(dir:'images', file:'spinner_24.gif')}";
            var spinner = $("<img style='margin-left:15px;' src='" + url + "' alt='Cargando...'/>");


            $(function () {
                $('[rel=tooltip]').tooltip();

                $(".btn-new").click(function () {
                    $.ajax({
                        type    : "POST",
                        url     : "${createLink(action:'form_ajax')}",
                        success : function (msg) {
                            var btnOk = $('<a href="#" data-dismiss="modal" class="btn">Cancelar</a>');
                            var btnSave = $('<a href="#"  class="btn btn-success"><i class="icon-ok"></i> Guardar</a>');

                            btnSave.click(function () {
                                if ($("#frmSave-precioVentaInstance").valid()) {
                                    btnSave.replaceWith(spinner);
                                }
                                $("#frmSave-precioVentaInstance").submit();
                                return false;
                            });

                            $("#modalTitle").html("Crear Precio de Venta");
                            $("#modalBody").html(msg);
                            $("#modalFooter").html("").append(btnOk).append(btnSave);
                            $("#modal-precioVenta").modal("show");
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
                            var btnSave = $('<a href="#"  class="btn btn-success"><i class="icon-ok"></i> Guardar</a>');

                            btnSave.click(function () {
                                if ($("#frmSave-precioVentaInstance").valid()) {
                                    btnSave.replaceWith(spinner);
                                }
                                $("#frmSave-precioVentaInstance").submit();
                                return false;
                            });

                            $("#modalTitle").html("Editar Precio de Venta");
                            $("#modalBody").html(msg);
                            $("#modalFooter").html("").append(btnOk).append(btnSave);
                            $("#modal-precioVenta").modal("show");
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
                            $("#modalTitle").html("Ver Precio de Venta");
                            $("#modalBody").html(msg);
                            $("#modalFooter").html("").append(btnOk);
                            $("#modal-precioVenta").modal("show");
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
                        $("#frmDelete-precioVentaInstance").submit();
                        return false;
                    });

                    $("#modalTitle").html("Eliminar Precio de Venta");
                    $("#modalBody").html("<p>¿Está seguro de querer eliminar este Precio de Venta?</p>");
                    $("#modalFooter").html("").append(btnOk).append(btnDelete);
                    $("#modal-precioVenta").modal("show");
                    return false;
                });

            });

        </script>

    </body>
</html>
