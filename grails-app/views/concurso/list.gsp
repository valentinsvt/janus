<%@ page import="janus.pac.Concurso" %>
<!doctype html>
<html>
    <head>
        <meta name="layout" content="main">
        <title>
            Lista de Procesos
        </title>
        <script src="${resource(dir: 'js/jquery/plugins/jquery-validation-1.9.0', file: 'jquery.validate.min.js')}"></script>
        <script src="${resource(dir: 'js/jquery/plugins/jquery-validation-1.9.0', file: 'messages_es.js')}"></script>
        <script src="${resource(dir: 'js/jquery/plugins', file: 'jquery.livequery.min.js')}"></script>
        <script src="${resource(dir: 'js/jquery/plugins/jQuery-contextMenu-gh-pages/src', file: 'jquery.ui.position.js')}" type="text/javascript"></script>
        <script src="${resource(dir: 'js/jquery/plugins/jQuery-contextMenu-gh-pages/src', file: 'jquery.contextMenu.js')}" type="text/javascript"></script>
        <link href="${resource(dir: 'js/jquery/plugins/jQuery-contextMenu-gh-pages/src', file: 'jquery.contextMenu.css')}" rel="stylesheet" type="text/css"/>
        <style>
        td {
            line-height : 12px !important;
        }
        </style>
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
        <span style="color: navy;">Haga clic con el botón derecho del ratón sobre el concurso para acceder al menú de acciones.</span>
        <span style="color: navy;">Recuerde que el formato del código del proceso es: MCO-<strong>número</strong>-GADPP-17</span>

        <g:form action="delete" name="frmDelete-Concurso">
            <g:hiddenField name="id"/>
        </g:form>

        <div id="list-Concurso" role="main">

            <table class="table table-bordered table-striped table-condensed table-hover">
                <thead>
                    <tr>
                        <th>Obra</th>
                        <th>Pac</th>
                        <g:sortableColumn property="codigo" title="Código"/>
                        <g:sortableColumn property="objeto" title="Objeto"/>
                        <g:sortableColumn property="costoBases" title="Costo Bases"/>
                        <th>Documentos</th>
                        <th style="width: 80px">Estado</th>
                    </tr>
                </thead>
                <tbody class="paginate">
                    <g:each in="${concursoInstanceList}" status="i" var="concursoInstance">
                        <tr style="font-size: 11px" class="item_row" id="${concursoInstance.id}" reg="${concursoInstance.estado}">
                            <td>${concursoInstance.obra?.descripcion}</td>
                            <td>${concursoInstance.pac?.descripcion}</td>
                            <td>${fieldValue(bean: concursoInstance, field: "codigo")}</td>
                            <td>${fieldValue(bean: concursoInstance, field: "objeto")}</td>
                            <td style="text-align: right">${fieldValue(bean: concursoInstance, field: "costoBases")}</td>
                            <td style="text-align: center">${janus.pac.DocumentoProceso.countByConcurso(concursoInstance)}</td>
                            <td>
                                ${(concursoInstance.estado == "R") ? "Registrado" : "No registrado"}<br>
                            </td>

                        </tr>
                    </g:each>
                </tbody>
            </table>

        </div>

        <div class="modal hide fade" id="modal-Delete">
            <div class="modal-header" id="modalDeleteHeader">
                <button type="button" class="close darker" data-dismiss="modal">
                    <i class="icon-remove-circle"></i>
                </button>

                <h3 id="modalDeleteTitle"></h3>
            </div>

            <div class="modal-body" id="modalDeleteBody">
            </div>

            <div class="modal-footer" id="modalDeleteFooter">
            </div>
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
            <div class="modal-header btn-primary">
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
                $.contextMenu({
                    selector : '.item_row',
                    items    : {
                        "view"   : {
                            name     : "Ver",
                            icon     : "view",
                            callback : function (key, options) {
                                location.href = "${g.createLink(controller: 'concurso',action: 'show')}/" + $(this).attr("id")
                            }
                        },
                        "edit"   : {
                            name     : "Editar",
                            icon     : "edit",
                            callback : function (key, options) {
                                location.href = "${g.createLink(action: 'form_ajax')}/" + $(this).attr("id")
                            }
                        },
                        "delete" : {
                            name     : "Eliminar",
                            icon     : "delete",
                            callback : function (key, options) {
                                var id = $(this).attr("id");
                                $("#id").val(id);
                                var btnOk = $('<a href="#" data-dismiss="modal" class="btn">Cancelar</a>');
                                var btnDelete = $('<a href="#" class="btn btn-danger"><i class="icon-trash"></i> Eliminar</a>');

                                btnDelete.click(function () {
                                    btnDelete.replaceWith(spinner);
                                    $("#frmDelete-Concurso").submit();
                                    return false;
                                });

                                $("#modalDeleteHeader").removeClass("btn-edit btn-show btn-delete").addClass("btn-delete");
                                $("#modalDeleteTitle").html("Eliminar Concurso");
                                $("#modalDeleteBody").html("<p>¿Está seguro de querer eliminar este Proceso?</p>");
                                $("#modalDeleteFooter").html("").append(btnOk).append(btnDelete);
                                $("#modal-Delete").modal("show");
                                return false;
                            }
                        },
                        "docs"   : {
                            name     : "Documentos",
                            icon     : "doc",
                            callback : function (key, options) {
                                location.href = "${g.createLink(controller: 'documentoProceso',action: 'list')}/" + $(this).attr("id")
                            }
                        },
                        %{--"eval"   : {--}%
                        %{--name     : "Evaluación",--}%
                        %{--icon     : "eval",--}%
                        %{--callback : function (key, options) {--}%
                        %{--location.href = "${g.createLink(controller: 'parametroEvaluacion',action: 'tree')}/" + $(this).attr("id")--}%
                        %{--},--}%
                        %{--disabled : function (key, opt) {--}%
                        %{--return opt.$trigger.attr("reg") != 'R'--}%
                        %{--}--}%
                        %{--},--}%
                        "ofrt"   : {
                            name     : "Ofertas",
                            icon     : "ofrt",
                            callback : function (key, options) {
                                location.href = "${g.createLink(controller: 'oferta',action: 'list')}/" + $(this).attr("id")
                            },
                            disabled : function (key, opt) {
                                return opt.$trigger.attr("reg") != 'R'
                            }
                        }
//                "print": {name:"Imprimir",icon:"print"}
                    }
                });
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
                            $("#modalTitle").html("Crear Proceso");
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
                            $("#modalTitle").html("Editar Proceso");
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
                            $("#modalTitle").html("Ver Proceso");
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
                    $("#modalTitle").html("Eliminar Proceso");
                    $("#modalBody").html("<p>¿Está seguro de querer eliminar este Proceso?</p>");
                    $("#modalFooter").html("").append(btnOk).append(btnDelete);
                    $("#modal-Concurso").modal("show");
                    return false;
                });

            });

        </script>

    </body>
</html>
