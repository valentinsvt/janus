
<%@ page import="janus.Parametros" %>
<!doctype html>
<html>
    <head>
        <meta name="layout" content="main">
        <title>
            Lista de Parametross
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

%{--
        <div class="row">
            <div class="span9 btn-group" role="navigation">
                <a href="#" class="btn btn-ajax btn-new">
                    <i class="icon-file"></i>
                    Crear  Parametros
                </a>
            </div>
            <div class="span3" id="busqueda-Parametros"></div>
        </div>
--}%

        <g:form action="delete" name="frmDelete-Parametros">
            <g:hiddenField name="id"/>
        </g:form>

        <div id="list-Parametros" role="main" style="margin-top: 10px;">

            <table class="table table-bordered table-striped table-condensed table-hover">
                <thead>
                    <tr>
                    
                        <g:sortableColumn property="factorReduccion" title="Factor Reduccion" />
                    
                        <g:sortableColumn property="factorVelocidad" title="Factor Velocidad" />
                    
                        <g:sortableColumn property="capacidadVolquete" title="Capacidad Volquete" />
                    
                        <g:sortableColumn property="factorVolumen" title="Factor Volumen" />
                    
                        <g:sortableColumn property="factorReduccionTiempo" title="Factor Reduccion Tiempo" />
                    
                        <g:sortableColumn property="factorPeso" title="Factor Peso" />
                    
                        <th width="150">Acciones</th>
                    </tr>
                </thead>
                <tbody class="paginate">
                <g:each in="${parametrosInstanceList}" status="i" var="parametrosInstance">
                    <tr>
                    
                        <td>${fieldValue(bean: parametrosInstance, field: "factorReduccion")}</td>
                    
                        <td>${fieldValue(bean: parametrosInstance, field: "factorVelocidad")}</td>
                    
                        <td>${fieldValue(bean: parametrosInstance, field: "capacidadVolquete")}</td>
                    
                        <td>${fieldValue(bean: parametrosInstance, field: "factorVolumen")}</td>
                    
                        <td>${fieldValue(bean: parametrosInstance, field: "factorReduccionTiempo")}</td>
                    
                        <td>${fieldValue(bean: parametrosInstance, field: "factorPeso")}</td>
                    
                        <td>
                            <a class="btn btn-small btn-show btn-ajax" href="#" rel="tooltip" title="Ver" data-id="${parametrosInstance.id}">
                                <i class="icon-zoom-in icon-large"></i>
                            </a>
                            <a class="btn btn-small btn-edit btn-ajax" href="#" rel="tooltip" title="Editar" data-id="${parametrosInstance.id}">
                                <i class="icon-pencil icon-large"></i>
                            </a>

%{--
                            <a class="btn btn-small btn-delete" href="#" rel="tooltip" title="Eliminar" data-id="${parametrosInstance.id}">
                                <i class="icon-trash icon-large"></i>
                            </a>
--}%
                        </td>
                    </tr>
                </g:each>
                </tbody>
            </table>

        </div>

        <div class="modal hide fade" id="modal-Parametros">
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
                if ($("#frmSave-Parametros").valid()) {
                    btn.replaceWith(spinner);
                }
                $("#frmSave-Parametros").submit();
            }

            $(function () {
                $('[rel=tooltip]').tooltip();

                $(".paginate").paginate({
                    maxRows        : 10,
                    searchPosition : $("#busqueda-Parametros"),
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
                            $("#modalTitle").html("Crear Parametros");
                            $("#modalBody").html(msg);
                            $("#modalFooter").html("").append(btnOk).append(btnSave);
                            $("#modal-Parametros").modal("show");
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
                            $("#modalTitle").html("Editar Parametros");
                            $("#modalBody").html(msg);
                            $("#modalFooter").html("").append(btnOk).append(btnSave);
                            $("#modal-Parametros").modal("show");
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
                            $("#modalTitle").html("Ver Parametros");
                            $("#modalBody").html(msg);
                            $("#modalFooter").html("").append(btnOk);
                            $("#modal-Parametros").modal("show");
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
                        $("#frmDelete-Parametros").submit();
                        return false;
                    });

                    $("#modalHeader").removeClass("btn-edit btn-show btn-delete").addClass("btn-delete");
                    $("#modalTitle").html("Eliminar Parametros");
                    $("#modalBody").html("<p>¿Está seguro de querer eliminar este Parametros?</p>");
                    $("#modalFooter").html("").append(btnOk).append(btnDelete);
                    $("#modal-Parametros").modal("show");
                    return false;
                });

            });

        </script>

    </body>
</html>
