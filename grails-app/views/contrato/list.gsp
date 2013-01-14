
<%@ page import="janus.Contrato" %>
<!doctype html>
<html>
    <head>
        <meta name="layout" content="main">
        <title>
            Lista de Contratos
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
                    Crear  Contrato
                </a>
            </div>
            <div class="span3" id="busqueda-Contrato"></div>
        </div>

        <g:form action="delete" name="frmDelete-Contrato">
            <g:hiddenField name="id"/>
        </g:form>

        <div id="list-Contrato" role="main" style="margin-top: 10px;">

            <table class="table table-bordered table-striped table-condensed table-hover">
                <thead>
                    <tr>
                    
                        <th>Oferta</th>
                    
                        <th>Tipo Contrato</th>
                    
                        <th>Tipo Plazo</th>
                    
                        <th>Padre</th>
                    
                        <th>Periodo Validez</th>
                    
                        <g:sortableColumn property="codigo" title="Codigo" />
                    
                        <th width="150">Acciones</th>
                    </tr>
                </thead>
                <tbody class="paginate">
                <g:each in="${contratoInstanceList}" status="i" var="contratoInstance">
                    <tr>
                    
                        <td>${fieldValue(bean: contratoInstance, field: "oferta")}</td>
                    
                        <td>${fieldValue(bean: contratoInstance, field: "tipoContrato")}</td>
                    
                        <td>${fieldValue(bean: contratoInstance, field: "tipoPlazo")}</td>
                    
                        <td>${fieldValue(bean: contratoInstance, field: "padre")}</td>
                    
                        <td>${fieldValue(bean: contratoInstance, field: "periodoValidez")}</td>
                    
                        <td>${fieldValue(bean: contratoInstance, field: "codigo")}</td>
                    
                        <td>
                            <a class="btn btn-small btn-show btn-ajax" href="#" rel="tooltip" title="Ver" data-id="${contratoInstance.id}">
                                <i class="icon-zoom-in icon-large"></i>
                            </a>
                            <a class="btn btn-small btn-edit btn-ajax" href="#" rel="tooltip" title="Editar" data-id="${contratoInstance.id}">
                                <i class="icon-pencil icon-large"></i>
                            </a>

                            <a class="btn btn-small btn-delete" href="#" rel="tooltip" title="Eliminar" data-id="${contratoInstance.id}">
                                <i class="icon-trash icon-large"></i>
                            </a>
                        </td>
                    </tr>
                </g:each>
                </tbody>
            </table>

        </div>

        <div class="modal hide fade" id="modal-Contrato">
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
                if ($("#frmSave-Contrato").valid()) {
                    btn.replaceWith(spinner);
                }
                $("#frmSave-Contrato").submit();
            }

            $(function () {
                $('[rel=tooltip]').tooltip();

                $(".paginate").paginate({
                    maxRows        : 10,
                    searchPosition : $("#busqueda-Contrato"),
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
                            $("#modalTitle").html("Crear Contrato");
                            $("#modalBody").html(msg);
                            $("#modalFooter").html("").append(btnOk).append(btnSave);
                            $("#modal-Contrato").modal("show");
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
                            $("#modalTitle").html("Editar Contrato");
                            $("#modalBody").html(msg);
                            $("#modalFooter").html("").append(btnOk).append(btnSave);
                            $("#modal-Contrato").modal("show");
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
                            $("#modalTitle").html("Ver Contrato");
                            $("#modalBody").html(msg);
                            $("#modalFooter").html("").append(btnOk);
                            $("#modal-Contrato").modal("show");
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
                        $("#frmDelete-Contrato").submit();
                        return false;
                    });

                    $("#modalHeader").removeClass("btn-edit btn-show btn-delete").addClass("btn-delete");
                    $("#modalTitle").html("Eliminar Contrato");
                    $("#modalBody").html("<p>¿Está seguro de querer eliminar este Contrato?</p>");
                    $("#modalFooter").html("").append(btnOk).append(btnDelete);
                    $("#modal-Contrato").modal("show");
                    return false;
                });

            });

        </script>

    </body>
</html>
