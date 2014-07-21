<%@ page import="janus.Persona" %>
<!doctype html>
<html>
    <head>
        <meta name="layout" content="main">
        <title>
            Lista de Personas
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

    <div class="row hide" id="divError">
        <div class="span12">
            <div class="alert " role="status">
                %{--<a class="close" data-dismiss="alert" href="#">×</a>--}%
                <span id="spanError"></span>
            </div>
        </div>
    </div>
    <div class="span12 hide" style="margin-bottom: 10px;" id="divOk">
        <div class="alert alert-info" role="status">
            <a class="close" data-dismiss="alert" href="#">×</a>
            <span id="spanOk"></span>
        </div>
    </div>

        <div class="span12 btn-group" role="navigation">
            <a href="#" class="btn btn-ajax btn-new">
                <i class="icon-file"></i>
                Crear  Persona
            </a>
            <a href="#" class="btn btn-ajax btn-new-of">
                <i class="icon-file"></i>
                Crear Oferente
            </a>

            <a href="#" class="btn btn-ajax btn-rol" id="btn-rol">
                <i class="icon-user"></i>
                Colocar Rol en la persona
            </a>

            <a href="#" class="btn btn-ajax btn-dir" id="btn-dir">
                <i class="icon-star"></i>
                Asignar Director
            </a>

            <a href="#" class="btn btn-ajax btn-cor" id="btn-cor">
                <i class="icon-male"></i>
                Asignar Coordinador
            </a>
        </div>

    %{--<div class="span12 btn-group" role="navigation">--}%
       %{----}%
    %{--</div>--}%


        <g:form action="delete" name="frmDelete-Persona">
            <g:hiddenField name="id"/>
        </g:form>

        <div id="list-Persona" class="span12" role="main" style="margin-top: 10px;">

            <table class="table table-bordered table-striped table-condensed table-hover">
                <thead>
                    <tr>

                        <g:sortableColumn property="cedula" title="Cedula"/>

                        <g:sortableColumn property="nombre" title="Nombre"/>

                        <g:sortableColumn property="apellido" title="Apellido"/>

                        <g:sortableColumn property="login" title="Login"/>

                        <g:sortableColumn property="departamento" title="Departamento"/>

                        <g:sortableColumn property="activo" title="Activo"/>

                        <th width="150">Acciones</th>
                    </tr>
                </thead>
                <tbody class="paginate">




                    <g:each in="${personaInstanceList}" status="i" var="personaInstance">

                                        %{--${personaInstanceList?.usuario}--}%

                    %{--<g:if test="${personaInstanceList?.sesiones != 'Oferente'}">--}%

                            <tr>

                                <td>${fieldValue(bean: personaInstance, field: "cedula")}</td>

                                <td>${fieldValue(bean: personaInstance, field: "nombre")}</td>

                                <td>${fieldValue(bean: personaInstance, field: "apellido")}</td>

                                <td>${fieldValue(bean: personaInstance, field: "login")}</td>

                                <td>${personaInstance.departamento?.descripcion}</td>

                                <td><g:formatBoolean boolean="${personaInstance.activo == 1}" true="Sí" false="No"/></td>

                                <td>
                                    <a class="btn btn-small btn-show btn-ajax" href="#" rel="tooltip" title="Ver" data-id="${personaInstance.id}">
                                        <i class="icon-zoom-in icon-large"></i>
                                    </a>
                                    <a class="btn btn-small btn-edit btn-ajax" href="#" rel="tooltip" title="Editar" data-id="${personaInstance.id}">
                                        <i class="icon-pencil icon-large"></i>
                                    </a>
                                    <a class="btn btn-small btn-password btn-ajax" href="#" rel="tooltip" title="Cambiar password" data-id="${personaInstance.id}">
                                        <i class="icon-lock icon-large"></i>
                                    </a>
                                    <a class="btn btn-small btn-cambiarEstado" href="#" rel="tooltip" title="Cambiar Estado" data-id="${personaInstance.id}" data-activo="${personaInstance?.activo}">
                                        <i class="icon-refresh icon-large"></i>
                                    </a>
                                </td>
                            </tr>
                    %{--</g:if>--}%

                    </g:each>

                </tbody>
            </table>

        </div>

        <div class="modal large hide fade" id="modal-Persona" style="width: 900px;">
            <div class="modal-header" id="modalHeader">
                <button type="button" class="close darker" data-dismiss="modal">
                    <i class="icon-remove-circle"></i>
                </button>

                <h3 id="modalTitle"></h3>
            </div>

            <div class="modal-body" id="modalBody" >
            </div>

            <div class="modal-footer" id="modalFooter">
            </div>
        </div>




        <script type="text/javascript">
            var url = "${resource(dir:'images', file:'spinner_24.gif')}";
            var spinner = $("<img style='margin-left:15px;' src='" + url + "' alt='Cargando...'/>");

            function submitForm(btn) {
                if ($("#frmSave-Persona").valid()) {
                    btn.replaceWith(spinner);
                }
                $("#frmSave-Persona").submit();
            }
            function submitFormOferente(btn) {
                if ($("#frmSave-Oferente").valid()) {
                    btn.replaceWith(spinner);
                }
                $("#frmSave-Oferente").submit();
            }

            $(function () {
                $('[rel=tooltip]').tooltip();

                $(".paginate").paginate({
                    maxRows : 15
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
                            $("#modalTitle").html("Crear Persona");
                            $("#modalBody").html(msg);
                            $("#modalFooter").html("").append(btnOk).append(btnSave);
                            $("#modal-Persona").modal("show");
                        }
                    });
                    return false;
                }); //click btn new

                $(".btn-new-of").click(function () {
                    $.ajax({
                        type    : "POST",
                        url     : "${createLink(action:'formOferente')}",
                        success : function (msg) {
                            var btnOk = $('<a href="#" data-dismiss="modal" class="btn">Cancelar</a>');
                            var btnSave = $('<a href="#"  class="btn btn-success"><i class="icon-save"></i> Guardar</a>');

                            btnSave.click(function () {
                                submitFormOferente(btnSave);
                                return false;
                            });

                            $("#modalHeader").removeClass("btn-edit btn-show btn-delete");
                            $("#modalTitle").html("Crear cuenta de Oferente");
                            $("#modalBody").html(msg);
                            $("#modalFooter").html("").append(btnOk).append(btnSave);
                            $("#modal-Persona").modal("show");
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
                            $("#modalTitle").html("Editar Persona");
                            $("#modalBody").html(msg);
                            $("#modalFooter").html("").append(btnOk).append(btnSave);
                            $("#modal-Persona").modal("show");
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
                            $("#modalTitle").html("Ver Persona");
                            $("#modalBody").html(msg);
                            $("#modalFooter").html("").append(btnOk);
                            $("#modal-Persona").modal("show");
                        }
                    });
                    return false;
                }); //click btn show


                $(".btn-cambiarEstado").click(function () {

                    var id = $(this).data("id");

                    var estado = $(this).data("activo");
//                    var activo = 0;

                    %{--if(estado == 0) {--}%
                    %{--estado = $(this).data("activo",1)--}%
                        %{--$.ajax({--}%
                            %{--type: "POST",--}%
                            %{--url: "${g.createLink(action: 'cambiarEstado')}",--}%
                            %{--data : {id: id,--}%
                                %{--activo:   '1'--}%
                            %{--},--}%
                            %{--success: function (msg){--}%
                                %{--if (msg == 'ok') {--}%
                                    %{--location.href = "${createLink(action: 'list')}"--}%
                                %{--}--}%
                            %{--}--}%
                        %{--});--}%

                    %{--}if(estado == 1) {--}%
                        %{--$(this).data("activo", 0)--}%
                       %{--$.ajax({--}%
                            %{--type: "POST",--}%
                            %{--url: "${g.createLink(action: 'cambiarEstado')}",--}%
                            %{--data : {id: id,--}%
                                %{--activo:   '0'--}%
                            %{--},--}%
                            %{--success: function (msg){--}%
                                %{--if (msg == 'ok') {--}%
                                    %{--location.href = "${createLink(action: 'list')}"--}%
                                %{--}--}%
                            %{--}--}%
                        %{--});--}%

                    %{--}--}%




                    $.ajax({
                        type: "POST",
                        url: "${g.createLink(action: 'cambiarEstado')}",
                        data : {id: id,
                            activo:   estado

                        },
                        success: function (msg){
                            if (msg == 'ok') {


                                $("#divOk").removeClass("hide");
                                $("#spanOk").html("Estado cambiado!");
                                location.href = "${createLink(action: 'list')}"

                            }else{
//                                location.reload(true);
//                                $("#divError").show();
                                $("#divError").removeClass("hide");
                                $("#spanError").html(msg);

                            }
                        }
                    });

                });

//                $(".btn-delete").click(function () {
//                    var id = $(this).data("id");
//                    $("#id").val(id);
//                    var btnOk = $('<a href="#" data-dismiss="modal" class="btn">Cancelar</a>');
//                    var btnDelete = $('<a href="#" class="btn btn-danger"><i class="icon-trash"></i> Eliminar</a>');
//
//                    btnDelete.click(function () {
//                        btnDelete.replaceWith(spinner);
//                        $("#frmDelete-Persona").submit();
//                        return false;
//                    });
//
//                    $("#modalHeader").removeClass("btn-edit btn-show btn-delete").addClass("btn-delete");
//                    $("#modalTitle").html("Eliminar Persona");
//                    $("#modalBody").html("<p>¿Está seguro de querer eliminar esta Persona?</p>");
//                    $("#modalFooter").html("").append(btnOk).append(btnDelete);
//                    $("#modal-Persona").modal("show");
//                    return false;
//                });
                $(".btn-password").click(function () {
                    var id = $(this).data("id");
                    $.ajax({
                        type    : "POST",
                        url     : "${createLink(action:'pass_ajax')}",
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

                            $("#modalHeader").removeClass("btn-edit btn-show btn-delete");
                            $("#modalTitle").html("Cambiar password de la Persona");
                            $("#modalBody").html(msg);
                            $("#modalFooter").html("").append(btnOk).append(btnSave);
                            $("#modal-Persona").modal("show");
                        }
                    });
                    return false;
                }); //click btn password


                $("#btn-rol").click(function () {

                    location.href= "${createLink(controller: 'personaRol', action: 'registroPersonaRol')}"

                });

                $("#btn-dir").click(function () {

                    location.href= "${createLink(controller: 'asignarDirector', action: 'asignarDirector')}"

                });

                $("#btn-cor").click(function () {

                    location.href= "${createLink(controller: 'asignarCoordinador', action: 'asignarCoordinador')}"

                });

            });

        </script>

    </body>
</html>
