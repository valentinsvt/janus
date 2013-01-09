<%@ page import="janus.pac.Concurso" %>
<!doctype html>
<html>
    <head>
        <meta name="layout" content="main">
        <title>
            Lista de Concursos
        </title>
        <script src="${resource(dir: 'js/jquery/plugins/jquery-validation-1.9.0', file: 'jquery.validate.min.js')}"></script>
        <script src="${resource(dir: 'js/jquery/plugins/jquery-validation-1.9.0', file: 'messages_es.js')}"></script>

        <script src="${resource(dir: 'js/jquery/plugins', file: 'jquery.livequery.min.js')}"></script>
    </head>

    <body>

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
                <a href="#" class="btn btn-ajax " id="btnPac">
                    <i class="icon-file"></i>
                    Nuevo Proceso
                </a>
            </div>

            <div class="span3" id="busca">
            </div>
        </div>

        <g:form action="delete" name="frmDelete-Concurso">
            <g:hiddenField name="id"/>
        </g:form>

        <div id="list-Concurso" role="main">

            <table class="table table-bordered table-striped table-condensed table-hover">
                <thead>
                    <tr>
                        <th>Obra</th>
                        <th>Administracion</th>
                        <th>Pac</th>
                        <g:sortableColumn property="codigo" title="Codigo"/>
                        <g:sortableColumn property="objeto" title="Objeto"/>
                        <g:sortableColumn property="costoBases" title="Costo Bases"/>
                        <th>Documentos</th>
                        <th style="width:110px;">Acciones</th>
                    </tr>
                </thead>
                <tbody class="paginate">
                    <g:each in="${concursoInstanceList}" status="i" var="concursoInstance">
                        <tr>
                            <td>${concursoInstance.obra?.descripcion}</td>
                            <td>${concursoInstance.administracion?.descripcion}</td>
                            <td>${concursoInstance.pac?.descripcion}</td>
                            <td>${fieldValue(bean: concursoInstance, field: "codigo")}</td>
                            <td>${fieldValue(bean: concursoInstance, field: "objeto")}</td>
                            <td>${fieldValue(bean: concursoInstance, field: "costoBases")}</td>
                            <td>${janus.pac.DocumentoProceso.countByConcurso(concursoInstance)}</td>
                            <td>
                                <p>
                                    <a class="btn btn-small btn-show btn-ajax" href="#" rel="tooltip" title="Ver" data-id="${concursoInstance.id}">
                                        <i class="icon-zoom-in icon-large"></i>
                                    </a>
                                    <a class="btn btn-small btn-edit btn-ajax" href="#" rel="tooltip" title="Editar" data-id="${concursoInstance.id}">
                                        <i class="icon-pencil icon-large"></i>
                                    </a>
                                    <a class="btn btn-small btn-delete" href="#" rel="tooltip" title="Eliminar" data-id="${concursoInstance.id}">
                                        <i class="icon-trash icon-large"></i>
                                    </a>
                                </p>

                                <p>
                                    <g:link controller="documentoProceso" action="list" class="btn btn-small btn-docs" href="#" rel="tooltip" title="Documentos" id="${concursoInstance.id}">
                                        <i class="icon-folder-open-alt icon-large"></i>
                                    </g:link>
                                    <g:link controller="parametroEvaluacion" action="list" class="btn btn-small btn-params" href="#" rel="tooltip" title="Parámetros de evaluación" id="${concursoInstance.id}">
                                        <i class="icon-reorder icon-large"></i>
                                    </g:link>
                                </p>
                            </td>
                        </tr>
                    </g:each>
                </tbody>
            </table>

        </div>

        <div class="modal grande2 hide fade" id="modal-Concurso">
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

        <div class="modal grande hide fade" id="modal-pac" style="overflow: hidden;">
            <div class="modal-header btn-info">
                <button type="button" class="close" data-dismiss="modal">×</button>

                <h3 id="modalTitle-obra">Buscar PAC</h3>
            </div>

            <div class="modal-body" id="modalBody-obra">
                <bsc:buscador name="pac" value="" accion="buscaPac" controlador="concurso" campos="${campos}" label="PAC" tipo="lista"/>
            </div>

            <div class="modal-footer" id="modalFooter-obra">
            </div>
        </div>


        <script type="text/javascript">
            var url = "${resource(dir:'images', file:'spinner_24.gif')}";
            var spinner = $("<img style='margin-left:15px;' src='" + url + "' alt='Cargando...'/>");

            function submitForm(btn) {
                if ($("#frmSave-Concurso").valid()) {
                    btn.replaceWith(spinner);
                }
                $("#frmSave-Concurso").submit();
            }

            $(function () {
                $('[rel=tooltip]').tooltip();

                $(".paginate").paginate({
                    maxRows        : 10,
                    searchPosition : $("#busca"),
                    float          : "right"
                });

                $("#btnPac").click(function () {
                    $("#modal-pac").modal("show");
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
                            $("#modalTitle").html("Crear Concurso");
                            $("#modalBody").html(msg);
                            $("#modalFooter").html("").append(btnOk).append(btnSave);
                            $("#modal-Concurso").modal("show");
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
                            $("#modalTitle").html("Editar Concurso");
                            $("#modalBody").html(msg);
                            $("#modalFooter").html("").append(btnOk).append(btnSave);
                            $("#modal-Concurso").modal("show");
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
                            $("#modalTitle").html("Ver Concurso");
                            $("#modalBody").html(msg);
                            $("#modalFooter").html("").append(btnOk);
                            $("#modal-Concurso").modal("show");
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
                        $("#frmDelete-Concurso").submit();
                        return false;
                    });

                    $("#modalHeader").removeClass("btn-edit btn-show btn-delete").addClass("btn-delete");
                    $("#modalTitle").html("Eliminar Concurso");
                    $("#modalBody").html("<p>¿Está seguro de querer eliminar este Concurso?</p>");
                    $("#modalFooter").html("").append(btnOk).append(btnDelete);
                    $("#modal-Concurso").modal("show");
                    return false;
                });

            });

        </script>

    </body>
</html>
