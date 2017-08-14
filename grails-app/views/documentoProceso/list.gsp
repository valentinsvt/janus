<%@ page import="janus.pac.DocumentoProceso" %>
<!doctype html>
<html>
    <head>
        <meta name="layout" content="main">
        <title>
            Lista de Documentos
        </title>
        <script src="${resource(dir: 'js/jquery/plugins/jquery-validation-1.9.0', file: 'jquery.validate.min.js')}"></script>
        <script src="${resource(dir: 'js/jquery/plugins/jquery-validation-1.9.0', file: 'messages_es.js')}"></script>
    </head>

    <body>

        <div class="tituloTree">
            <g:if test="${contrato}">
                Documentos del contrato de la obra: <span style="font-weight: bold;">${contrato.obra.descripcion?.toUpperCase()}</span>
            </g:if>
            <g:else>
                Documentos de <span style="font-weight: bold; font-style: italic;">${concurso.objeto}</span>
            </g:else>
        </div>

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
                <g:if test="${contrato}">
                    <g:if test="${params.show == '1'}">
                        <g:link controller="contrato" action="registroContrato" class="btn" params="[contrato: contrato.id]">
                            <i class="icon-caret-left"></i>
                            Regresar
                        </g:link>
                    </g:if>
                    <g:else>
                        <g:link controller="contrato" action="verContrato" class="btn" params="[contrato: contrato?.id]">
                            <i class="icon-caret-left"></i>
                            Regresar
                        </g:link>
                    </g:else>
                </g:if>
                <g:else>
                    <g:link controller="concurso" action="list" class="btn">
                        <i class="icon-caret-left"></i>
                        Regresar
                    </g:link>
                </g:else>
                <a href="#" class="btn btn-ajax btn-new">
                    <i class="icon-file"></i>
                    Nuevo Documento
                </a>
                <a href="#" class="btn btn-ajax btn-copy">
                    <i class="icon-copy"></i>
                    Copiar documentos de la obra
                </a>

                <a href="#" class="btn btn-ajax btn-info" id="docRespaldo">
                    <i class="icon-file"></i>
                    Respaldo para Obras Adicionales
                </a> <a href="#" class="btn btn-ajax btn-info" id="docCmasS">
                    <i class="icon-file"></i>
                    Respaldo para Costo + %
                </a>
            </div>

            <div class="span3" id="busca">
            </div>
        </div>

        <g:form action="delete" name="frmDelete-DocumentoProceso">
            <g:hiddenField name="id"/>
            <g:hiddenField name="contrato" value="${contrato?.id}"/>
            <g:hiddenField name="show" value="${params.show}"/>
        </g:form>

        <div id="list-DocumentoProceso" role="main">

            <table class="table table-bordered table-striped table-condensed table-hover">
                <thead>
                    <tr>
                        <th>Etapa</th>
                        <g:sortableColumn property="nombre" title="Nombre"/>
                        <g:sortableColumn property="descripcion" title="Descripcion"/>
                        <g:sortableColumn property="resumen" title="Resumen"/>
                        <g:sortableColumn property="palabrasClave" title="Palabras Clave"/>
                        <th>Tipo de archivo</th>
                        <th width="150">Acciones</th>
                    </tr>
                </thead>
                <tbody class="paginate">
                    <g:each in="${documentoProcesoInstanceList}" status="i" var="documentoProcesoInstance">
                        <tr>
                            <td>${documentoProcesoInstance?.etapa?.descripcion}</td>
                            <td>${fieldValue(bean: documentoProcesoInstance, field: "nombre")}</td>
                            <td>${fieldValue(bean: documentoProcesoInstance, field: "descripcion")}</td>
                            <td>${fieldValue(bean: documentoProcesoInstance, field: "resumen")}</td>
                            <td>${fieldValue(bean: documentoProcesoInstance, field: "palabrasClave")}</td>
                            <td>
                                <g:set var="p" value="${documentoProcesoInstance.path.split("\\.")}"/>
                                ${p[p.size() - 1]}
                            </td>
                            <td>
                                <a class="btn btn-small btn-show btn-ajax" href="#" rel="tooltip" title="Ver" data-id="${documentoProcesoInstance.id}">
                                    <i class="icon-zoom-in icon-large"></i>
                                </a>
                                <a class="btn btn-small btn-edit btn-ajax" href="#" rel="tooltip" title="Editar" data-id="${documentoProcesoInstance.id}">
                                    <i class="icon-pencil icon-large"></i>
                                </a>
                                <g:link action="downloadFile" class="btn btn-small btn-docs" rel="tooltip" title="Descargar" id="${documentoProcesoInstance.id}">
                                    <i class="icon-download-alt icon-large"></i>
                                </g:link>
                                <a class="btn btn-small btn-delete" href="#" rel="tooltip" title="Eliminar" data-id="${documentoProcesoInstance.id}">
                                    <i class="icon-trash icon-large"></i>
                                </a>
                            </td>
                        </tr>
                    </g:each>
                </tbody>
            </table>

        </div>

        <div class="modal hide fade" id="modal-DocumentoProceso">
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
                if ($("#frmSave-DocumentoProceso").valid()) {
                    btn.replaceWith(spinner);
                }
                $("#frmSave-DocumentoProceso").submit();
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
                            concurso : ${concurso.id},
                            contrato : "${contrato?.id}",
                            show     : "${params.show}"
                        },
                        success : function (msg) {
                            var btnOk = $('<a href="#" data-dismiss="modal" class="btn">Cancelar</a>');
                            var btnSave = $('<a href="#"  class="btn btn-success"><i class="icon-save"></i> Guardar</a>');

                            btnSave.click(function () {
                                submitForm(btnSave);
                                return false;
                            });

                            $("#modalHeader").removeClass("btn-edit btn-show btn-delete");
                            $("#modalTitle").html("Crear Documento");
                            $("#modalBody").html(msg);
                            $("#modalFooter").html("").append(btnOk).append(btnSave);
                            $("#modal-DocumentoProceso").modal("show");
                        }
                    });
                    return false;
                }); //click btn new

                $("#docRespaldo").click(function () {
                    $.ajax({
                        type    : "POST",
                        url     : "${createLink(action:'form_ajax')}",
                        data    : {
                            concurso : ${concurso.id},
                            contrato : "${contrato?.id}",
                            show     : "${params.show}",
                            docuResp : 'R'
                        },
                        success : function (msg) {
                            var btnOk = $('<a href="#" data-dismiss="modal" class="btn">Cancelar</a>');
                            var btnSave = $('<a href="#"  class="btn btn-success"><i class="icon-save"></i> Guardar</a>');

                            btnSave.click(function () {
                                submitForm(btnSave);
                                return false;
                            });

                            $("#modalHeader").removeClass("btn-edit btn-show btn-delete");
                            $("#modalTitle").html("Crear Documento");
                            $("#modalBody").html(msg);
                            $("#modalFooter").html("").append(btnOk).append(btnSave);
                            $("#modal-DocumentoProceso").modal("show");
                        }
                    });
                    return false;
                }); //click btn new

               $("#docCmasS").click(function () {
                    $.ajax({
                        type    : "POST",
                        url     : "${createLink(action:'form_ajax')}",
                        data    : {
                            concurso : ${concurso.id},
                            contrato : "${contrato?.id}",
                            show     : "${params.show}",
                            docuResp : 'C'
                        },
                        success : function (msg) {
                            var btnOk = $('<a href="#" data-dismiss="modal" class="btn">Cancelar</a>');
                            var btnSave = $('<a href="#"  class="btn btn-success"><i class="icon-save"></i> Guardar</a>');

                            btnSave.click(function () {
                                submitForm(btnSave);
                                return false;
                            });

                            $("#modalHeader").removeClass("btn-edit btn-show btn-delete");
                            $("#modalTitle").html("Crear Documento");
                            $("#modalBody").html(msg);
                            $("#modalFooter").html("").append(btnOk).append(btnSave);
                            $("#modal-DocumentoProceso").modal("show");
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
                            id       : id,
                            concurso : ${concurso.id},
                            contrato : "${contrato?.id}",
                            show     : "${params.show}"
                        },
                        success : function (msg) {
                            var btnOk = $('<a href="#" data-dismiss="modal" class="btn">Cancelar</a>');
                            var btnSave = $('<a href="#"  class="btn btn-success"><i class="icon-save"></i> Guardar</a>');

                            btnSave.click(function () {
                                submitForm(btnSave);
                                return false;
                            });

                            $("#modalHeader").removeClass("btn-edit btn-show btn-delete").addClass("btn-edit");
                            $("#modalTitle").html("Editar Documento");
                            $("#modalBody").html(msg);
                            $("#modalFooter").html("").append(btnOk).append(btnSave);
                            $("#modal-DocumentoProceso").modal("show");
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
                            $("#modalTitle").html("Ver Documento");
                            $("#modalBody").html(msg);
                            $("#modalFooter").html("").append(btnOk);
                            $("#modal-DocumentoProceso").modal("show");
                        }
                    });
                    return false;
                }); //click btn show

                $(".btn-copy").click(function () {
                    $(this).replaceWith(spinner);
                    var id = $(this).data("id");
                    $.ajax({
                        type    : "POST",
                        url     : "${createLink(action:'copiarDocumentos')}",
                        data    : {
                            id : "${concurso.id}",
                            contrato : "${contrato?.id}"
                        },
                        success : function (msg) {
                            location.reload();
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
                        $("#frmDelete-DocumentoProceso").submit();
                        return false;
                    });

                    $("#modalHeader").removeClass("btn-edit btn-show btn-delete").addClass("btn-delete");
                    $("#modalTitle").html("Eliminar Documento de la Biblioteca");
                    $("#modalBody").html("<p>¿Está seguro de querer eliminar este Documento Proceso?</p>");
                    $("#modalFooter").html("").append(btnOk).append(btnDelete);
                    $("#modal-DocumentoProceso").modal("show");
                    return false;
                });

            });

        </script>

    </body>
</html>
