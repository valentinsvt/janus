
<%@ page import="janus.TipoCuenta" %>
<!doctype html>
<html>
    <head>
        <meta name="layout" content="main">
        <title>
            Lista de Tipos de Cuenta
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
                Nuevo Tipo de Cuenta
            </a>
        </div>

        <g:form action="delete" name="frmDelete-tipoCuentaInstance">
            <g:hiddenField name="id"/>
        </g:form>

        <div id="list-tipoCuenta" class="span12" role="main" style="margin-top: 10px;">

            <table class="table table-bordered table-striped table-condensed table-hover">
                <thead>
                    <tr>
                    
                        <g:sortableColumn property="codigo" title="Código" />
                    
                        <g:sortableColumn property="descripcion" title="Descripción" />
                    
                        <th width="150">Acciones</th>
                    </tr>
                </thead>
                <tbody>
                <g:each in="${tipoCuentaInstanceList}" status="i" var="tipoCuentaInstance">
                    <tr>
                    
                        <td>${fieldValue(bean: tipoCuentaInstance, field: "codigo")}</td>
                    
                        <td>${fieldValue(bean: tipoCuentaInstance, field: "descripcion")}</td>
                    
                        <td>
                            <a class="btn btn-small btn-show btn-ajax" href="#" rel="tooltip" title="Ver" data-id="${tipoCuentaInstance.id}">
                                <i class="icon-zoom-in"></i>
                            </a>
                            <a class="btn btn-small btn-edit btn-ajax" href="#" rel="tooltip" title="Editar" data-id="${tipoCuentaInstance.id}">
                                <i class="icon-pencil"></i>
                            </a>

                            <a class="btn btn-small btn-delete" href="#" rel="tooltip" title="Eliminar" data-id="${tipoCuentaInstance.id}">
                                <i class="icon-trash"></i>
                            </a>
                        </td>
                    </tr>
                </g:each>
                </tbody>
            </table>
            <div class="pagination">
                <elm:paginate total="${tipoCuentaInstanceTotal}" params="${params}" />
            </div>
        </div>

        <div class="modal hide fade" id="modal-tipoCuenta">
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
                                if ($("#frmSave-tipoCuentaInstance").valid()) {
                                    btnSave.replaceWith(spinner);
                                }
                                $("#frmSave-tipoCuentaInstance").submit();
                                return false;
                            });

                            $("#modalTitle").html("Crear Tipo de Cuenta");
                            $("#modalBody").html(msg);
                            $("#modalFooter").html("").append(btnOk).append(btnSave);
                            $("#modal-tipoCuenta").modal("show");
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
                                if ($("#frmSave-tipoCuentaInstance").valid()) {
                                    btnSave.replaceWith(spinner);
                                }
                                $("#frmSave-tipoCuentaInstance").submit();
                                return false;
                            });

                            $("#modalTitle").html("Editar Tipo de Cuenta");
                            $("#modalBody").html(msg);
                            $("#modalFooter").html("").append(btnOk).append(btnSave);
                            $("#modal-tipoCuenta").modal("show");
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
                            $("#modalTitle").html("Ver Tipo de Cuenta");
                            $("#modalBody").html(msg);
                            $("#modalFooter").html("").append(btnOk);
                            $("#modal-tipoCuenta").modal("show");
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
                        $("#frmDelete-tipoCuentaInstance").submit();
                        return false;
                    });

                    $("#modalTitle").html("Eliminar Tipo de Cuenta");
                    $("#modalBody").html("<p>¿Está seguro de querer eliminar este Tipo de Cuenta?</p>");
                    $("#modalFooter").html("").append(btnOk).append(btnDelete);
                    $("#modal-tipoCuenta").modal("show");
                    return false;
                });

            });

        </script>

    </body>
</html>
