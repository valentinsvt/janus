<%@ page import="janus.RolTramite; janus.TipoTramite" %>
<!doctype html>
<html>
    <head>
        <meta name="layout" content="main">
        <title>
            Lista de Tipo Trámites
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
                    Crear  Tipo Trámite
                </a>
            </div>

            <div class="span3" id="busqueda-TipoTramite"></div>
        </div>

        <g:form action="delete" name="frmDelete-TipoTramite">
            <g:hiddenField name="id"/>
        </g:form>

        <div id="list-TipoTramite" role="main" style="margin-top: 10px;">

            <table class="table table-bordered table-striped table-condensed table-hover">
                <thead>
                    <tr>

                        <g:sortableColumn property="codigo" title="Código"/>

                        <g:sortableColumn property="descripcion" title="Descripción"/>

                        <th>Padre</th>

                        <g:sortableColumn property="tiempo" title="Tiempo"/>

                        <g:sortableColumn property="tipo" title="Tipo"/>

                        <g:sortableColumn property="requiereRespuesta" title="Requiere Respuesta"/>

                        <th>Configurado</th>

                        <th width="160">Acciones</th>
                    </tr>
                </thead>
                <tbody class="paginate">
                    <g:each in="${tipoTramiteInstanceList}" status="i" var="tipoTramiteInstance">
                        <tr>

                            <td>${fieldValue(bean: tipoTramiteInstance, field: "codigo")}</td>

                            <td>${fieldValue(bean: tipoTramiteInstance, field: "descripcion")}</td>

                            <td>${fieldValue(bean: tipoTramiteInstance, field: "padre")}</td>

                            <td>${fieldValue(bean: tipoTramiteInstance, field: "tiempo")} días</td>

                            <td><g:message code="tipoTramite.tipo.${tipoTramiteInstance.tipo}"/></td>

                            <td>${tipoTramiteInstance.requiereRespuesta == 'S' ? 'Sí' : 'No'}</td>

                            <td>
                                <g:set var="de" value="${janus.DepartamentoTramite.findAllByTipoTramiteAndRolTramite(tipoTramiteInstance, RolTramite.findByCodigo('DE'))}"/>
                                <g:set var="para" value="${janus.DepartamentoTramite.findAllByTipoTramiteAndRolTramite(tipoTramiteInstance, RolTramite.findByCodigo('PARA'))}"/>
                                ${de.size() > 0 && para.size() > 0 ? 'Sí' : 'No'}
                            </td>

                            <td>
                                <a class="btn btn-small btn-show btn-ajax" href="#" rel="tooltip" title="Ver" data-id="${tipoTramiteInstance.id}">
                                    <i class="icon-zoom-in icon-large"></i>
                                </a>
                                <a class="btn btn-small btn-edit btn-ajax" href="#" rel="tooltip" title="Editar" data-id="${tipoTramiteInstance.id}">
                                    <i class="icon-pencil icon-large"></i>
                                </a>
                                <a class="btn btn-small btn-dep btn-ajax" href="#" rel="tooltip" title="Departamentos" data-id="${tipoTramiteInstance.id}">
                                    <i class="icon-group icon-large"></i>
                                </a>
                                <a class="btn btn-small btn-delete" href="#" rel="tooltip" title="Eliminar" data-id="${tipoTramiteInstance.id}">
                                    <i class="icon-trash icon-large"></i>
                                </a>
                            </td>
                        </tr>
                    </g:each>
                </tbody>
            </table>

        </div>

        <div class="modal hide mediumModal fade" id="modal-TipoTramite">
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
                if ($("#frmSave-TipoTramite").valid()) {
                    btn.replaceWith(spinner);
                }
                $("#frmSave-TipoTramite").submit();
            }

            $(function () {
                $('[rel=tooltip]').tooltip();

                $(".paginate").paginate({
                    maxRows        : 10,
                    searchPosition : $("#busqueda-TipoTramite"),
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
                            $("#modalTitle").html("Crear Tipo Trámite");
                            $("#modalBody").html(msg);
                            $("#modalFooter").html("").append(btnOk).append(btnSave);
                            $("#modal-TipoTramite").modal("show");
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
                            var btnSave = $('<a href="#" class="btn btn-success"><i class="icon-save"></i> Guardar</a>');

                            btnSave.click(function () {
                                submitForm(btnSave);
                                return false;
                            });

                            $("#modalHeader").removeClass("btn-edit btn-show btn-delete").addClass("btn-edit");
                            $("#modalTitle").html("Editar Tipo Trámite");
                            $("#modalBody").html(msg);
                            $("#modalFooter").html("").append(btnOk).append(btnSave);
                            $("#modal-TipoTramite").modal("show");
                        }
                    });
                    return false;
                }); //click btn edit

                $(".btn-dep").click(function () {
                    var id = $(this).data("id");
                    $.ajax({
                        type    : "POST",
                        url     : "${createLink(action:'departamentos_ajax')}",
                        data    : {
                            tramite : id
                        },
                        success : function (msg) {
                            var btnOk = $('<a href="#" data-dismiss="modal" class="btn">Cancelar</a>');
                            var btnSave = $('<a href="#"  data-dismiss="modal" class="btn btn-success"><i class="icon-save"></i> Guardar</a>');

//                            btnSave.click(function () {
//                                submitForm(btnSave);
//                                return false;
//                            });

                            $("#modalHeader").removeClass("btn-edit btn-show btn-delete");
                            $("#modalTitle").html("Departamentos de Tipo Trámite");
                            $("#modalBody").html(msg);
                            $("#modalFooter").html("").append(btnOk).append(btnSave);
                            $("#modal-TipoTramite").modal("show");
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
                            $("#modalTitle").html("Ver Tipo Tramite");
                            $("#modalBody").html(msg);
                            $("#modalFooter").html("").append(btnOk);
                            $("#modal-TipoTramite").modal("show");
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
                        $("#frmDelete-TipoTramite").submit();
                        return false;
                    });

                    $("#modalHeader").removeClass("btn-edit btn-show btn-delete").addClass("btn-delete");
                    $("#modalTitle").html("Eliminar Tipo Trámite");
                    $("#modalBody").html("<p>¿Está seguro de querer eliminar este Tipo Tramite?</p>");
                    $("#modalFooter").html("").append(btnOk).append(btnDelete);
                    $("#modal-TipoTramite").modal("show");
                    return false;
                });

            });

        </script>

    </body>
</html>
