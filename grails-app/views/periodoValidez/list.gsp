<%@ page import="janus.pac.PeriodoValidez" %>
<!doctype html>
<html>
    <head>
        <meta name="layout" content="main">
        <title>
            Lista de Periodo Validez
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
                    Crear  Periodo Validez
                </a>
            </div>

            <div class="span3" id="busqueda-PeriodoValidez"></div>
        </div>

        <g:form action="delete" name="frmDelete-PeriodoValidez">
            <g:hiddenField name="id"/>
        </g:form>

        <div id="list-PeriodoValidez" role="main" style="margin-top: 10px;">

            <table class="table table-bordered table-striped table-condensed table-hover">
                <thead>
                    <tr>

                        <g:sortableColumn property="descripcion" title="Descripcion"/>

                        <g:sortableColumn property="cierre" title="Cierre"/>

                        <g:sortableColumn property="fechaInicio" title="Fecha Inicio"/>

                        <g:sortableColumn property="fechaFin" title="Fecha Fin"/>

                        <th width="150">Acciones</th>
                    </tr>
                </thead>
                <tbody class="paginate">
                    <g:each in="${periodoValidezInstanceList}" status="i" var="periodoValidezInstance">
                        <tr>

                            <td>${fieldValue(bean: periodoValidezInstance, field: "descripcion")}</td>

                            <td>${fieldValue(bean: periodoValidezInstance, field: "cierre")}</td>

                            <td><g:formatDate date="${periodoValidezInstance.fechaInicio}" format="dd-MM-yyyy"/></td>

                            <td><g:formatDate date="${periodoValidezInstance.fechaFin}" format="dd-MM-yyyy"/></td>

                            <td>
                                <a class="btn btn-small btn-show btn-ajax" href="#" rel="tooltip" title="Ver" data-id="${periodoValidezInstance.id}">
                                    <i class="icon-zoom-in icon-large"></i>
                                </a>
                                <a class="btn btn-small btn-edit btn-ajax" href="#" rel="tooltip" title="Editar" data-id="${periodoValidezInstance.id}">
                                    <i class="icon-pencil icon-large"></i>
                                </a>

                                <a class="btn btn-small btn-delete" href="#" rel="tooltip" title="Eliminar" data-id="${periodoValidezInstance.id}">
                                    <i class="icon-trash icon-large"></i>
                                </a>
                            </td>
                        </tr>
                    </g:each>
                </tbody>
            </table>

        </div>

        <div class="modal hide fade" id="modal-PeriodoValidez">
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
                if ($("#frmSave-PeriodoValidez").valid()) {
                    btn.replaceWith(spinner);
                }
                $("#frmSave-PeriodoValidez").submit();
            }

            $(function () {
                $('[rel=tooltip]').tooltip();

                $(".paginate").paginate({
                    maxRows        : 20,
                    searchPosition : $("#busqueda-PeriodoValidez"),
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
                            $("#modalTitle").html("Crear Periodo Validez");
                            $("#modalBody").html(msg);
                            $("#modalFooter").html("").append(btnOk).append(btnSave);
                            $("#modal-PeriodoValidez").modal("show");
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
                            $("#modalTitle").html("Editar Periodo Validez");
                            $("#modalBody").html(msg);
                            $("#modalFooter").html("").append(btnOk).append(btnSave);
                            $("#modal-PeriodoValidez").modal("show");
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
                            $("#modalTitle").html("Ver Periodo Validez");
                            $("#modalBody").html(msg);
                            $("#modalFooter").html("").append(btnOk);
                            $("#modal-PeriodoValidez").modal("show");
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
                        $("#frmDelete-PeriodoValidez").submit();
                        return false;
                    });

                    $("#modalHeader").removeClass("btn-edit btn-show btn-delete").addClass("btn-delete");
                    $("#modalTitle").html("Eliminar Periodo Validez");
                    $("#modalBody").html("<p>¿Está seguro de querer eliminar este Periodo Validez?</p>");
                    $("#modalFooter").html("").append(btnOk).append(btnDelete);
                    $("#modal-PeriodoValidez").modal("show");
                    return false;
                });

            });

        </script>

    </body>
</html>
