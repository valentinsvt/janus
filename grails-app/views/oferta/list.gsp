<%@ page import="janus.pac.Oferta" %>
<!doctype html>
<html>
    <head>
        <meta name="layout" content="main">
        <title>
            Lista de Ofertas
        </title>
        <script src="${resource(dir: 'js/jquery/plugins/jquery-validation-1.9.0', file: 'jquery.validate.min.js')}"></script>
        <script src="${resource(dir: 'js/jquery/plugins/jquery-validation-1.9.0', file: 'messages_es.js')}"></script>
    </head>

    <body>

        <div class="tituloTree">
            Ofertas de <span style="font-weight: bold; font-style: italic;">${concurso.objeto}</span>
        </div>

        <g:if test="${flash.message}">
            <div class="span12">
                <div class="alert ${flash.clase ?: 'alert-info'}" role="status">
                    <a class="close" data-dismiss="alert" href="#">×</a>
                    ${flash.message}
                </div>
            </div>
        </g:if>

        <div class="row">
            <div class="span9 btn-group" role="navigation">
                <g:link controller="concurso" action="list" class="btn">
                    <i class="icon-caret-left"></i>
                    Regresar
                </g:link>
                <a href="#" class="btn btn-ajax btn-new">
                    <i class="icon-file"></i>
                    Crear  Oferta
                </a>
                <g:link controller="proveedor" action="list" class="btn">
                    <i class="icon-group"></i>
                    Proveedores
                </g:link>
            </div>

            <div class="span3" id="busca">
            </div>
        </div>

        <g:form action="delete" name="frmDelete-Oferta">
            <g:hiddenField name="id"/>
        </g:form>

        <div id="list-Oferta" role="main" style="margin-top: 10px;">

            <table class="table table-bordered table-striped table-condensed table-hover">
                <thead>
                    <tr>
                        <th>Proveedor</th>
                        <g:sortableColumn property="descripcion" title="Descripcion"/>
                        <g:sortableColumn property="monto" title="Monto"/>
                        <g:sortableColumn property="fechaEntrega" title="Fecha Entrega"/>
                        <g:sortableColumn property="plazo" title="Plazo"/>
                        <th width="120">Acciones</th>
                    </tr>
                </thead>
                <tbody class="paginate">
                    <g:each in="${ofertaInstanceList}" status="i" var="ofertaInstance">
                        <tr>
                            <td>${ofertaInstance?.proveedor?.nombre}</td>
                            <td>${fieldValue(bean: ofertaInstance, field: "descripcion")}</td>
                            <td>${fieldValue(bean: ofertaInstance, field: "monto")}</td>
                            <td><g:formatDate date="${ofertaInstance.fechaEntrega}" format="dd-MM-yyyy"/></td>
                            <td>${fieldValue(bean: ofertaInstance, field: "plazo")}</td>
                            <td>
                                <a class="btn btn-small btn-show btn-ajax" href="#" rel="tooltip" title="Ver" data-id="${ofertaInstance.id}">
                                    <i class="icon-zoom-in icon-large"></i>
                                </a>
                                <a class="btn btn-small btn-edit btn-ajax" href="#" rel="tooltip" title="Editar" data-id="${ofertaInstance.id}">
                                    <i class="icon-pencil icon-large"></i>
                                </a>

                                <a class="btn btn-small btn-delete" href="#" rel="tooltip" title="Eliminar" data-id="${ofertaInstance.id}">
                                    <i class="icon-trash icon-large"></i>
                                </a>
%{--
                                <g:link class="btn btn-small btn-warning btn-ajax" href="#" rel="tooltip" title="Calificar" id="${ofertaInstance.id}" controller="registroParametros" action="registro">
                                    <i class="icon-zoom-in icon-tasks"></i>
                                </g:link>
--}%
                            </td>
                        </tr>
                    </g:each>
                </tbody>
            </table>

        </div>

        <div class="modal mediumModal hide fade" id="modal-Oferta">
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
                if ($("#frmSave-Oferta").valid()) {
                    btn.replaceWith(spinner);
                }
                $("#frmSave-Oferta").submit();
            }

            $(function () {
                $('[rel=tooltip]').tooltip();

                $(".paginate").paginate({
                    maxRows        : 10,
                    searchPosition : $("#busca"),
                    float          : "right"
                });

                $(".btn-new").click(function () {
                    $.ajax({
                        type    : "POST",
                        url     : "${createLink(action:'form_ajax')}",
                        data    : {
                            cncr : ${concurso.id}
                        },
                        success : function (msg) {
                            var btnOk = $('<a href="#" data-dismiss="modal" class="btn">Cancelar</a>');
                            var btnSave = $('<a href="#"  class="btn btn-success"><i class="icon-save"></i> Guardar</a>');

                            btnSave.click(function () {
                                submitForm(btnSave);
                                return false;
                            });

                            $("#modalHeader").removeClass("btn-edit btn-show btn-delete");
                            $("#modalTitle").html("Crear Oferta");
                            $("#modalBody").html(msg);
                            $("#modalFooter").html("").append(btnOk).append(btnSave);
                            $("#modal-Oferta").modal("show");
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
                            $("#modalTitle").html("Editar Oferta");
                            $("#modalBody").html(msg);
                            $("#modalFooter").html("").append(btnOk).append(btnSave);
                            $("#modal-Oferta").modal("show");
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
                            $("#modalTitle").html("Ver Oferta");
                            $("#modalBody").html(msg);
                            $("#modalFooter").html("").append(btnOk);
                            $("#modal-Oferta").modal("show");
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
                        $("#frmDelete-Oferta").submit();
                        return false;
                    });

                    $("#modalHeader").removeClass("btn-edit btn-show btn-delete").addClass("btn-delete");
                    $("#modalTitle").html("Eliminar Oferta");
                    $("#modalBody").html("<p>¿Está seguro de querer eliminar esta Oferta?</p>");
                    $("#modalFooter").html("").append(btnOk).append(btnDelete);
                    $("#modal-Oferta").modal("show");
                    return false;
                });

            });

        </script>

    </body>
</html>
