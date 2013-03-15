<%@ page import="janus.Persona" %>
<!doctype html>
<html>
    <head>
        <meta name="layout" content="main">
        <title>
            Lista de Oferentes
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
                Crear  Oferente
            </a>

    </div>


        <g:form action="delete" name="frmDelete-Oferente">
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

                    <g:each in="${sesion}" status="j" var="sesionPerfil">

                        <tr>

                        <td>${sesionPerfil?.usuario?.cedula}</td>

                        <td>${sesionPerfil?.usuario?.nombre}</td>

                        <td>${sesionPerfil?.usuario?.apellido}</td>

                        <td>${sesionPerfil?.usuario?.login}</td>

                        <td>${sesionPerfil?.usuario?.departamento?.descripcion}</td>

                        <td><g:formatBoolean boolean="${sesionPerfil?.usuario?.activo == 1}" true="Sí" false="No"/></td>

                            <td>
                                <a class="btn btn-small btn-show btn-ajax" href="#" rel="tooltip" title="Ver" data-id="${sesionPerfil?.usuario?.id}">
                                    <i class="icon-zoom-in icon-large"></i>
                                </a>
                                <a class="btn btn-small btn-edit btn-ajax" href="#" rel="tooltip" title="Editar" data-id="${sesionPerfil?.usuario?.id}">
                                    <i class="icon-pencil icon-large"></i>
                                </a>
                                <a class="btn btn-small btn-password btn-ajax" href="#" rel="tooltip" title="Cambiar password" data-id="${sesionPerfil?.usuario?.id}">
                                    <i class="icon-lock icon-large"></i>
                                </a>
                                <a class="btn btn-small btn-cambiarEstado" href="#" rel="tooltip" title="Cambiar estado" data-id="${sesionPerfil?.usuario?.id}" data-activo="${sesionPerfil?.usuario?.activo}">
                                    <i class="icon-refresh icon-large"></i>
                                </a>
                            </td>
                        </tr>

                    </g:each>
                    %{--<g:each in="${personaInstanceList}" status="i" var="personaInstance">--}%


                        %{--<tr>--}%

                            %{--<td>${fieldValue(bean: personaInstance, field: "cedula")}</td>--}%

                            %{--<td>${fieldValue(bean: personaInstance, field: "nombre")}</td>--}%

                            %{--<td>${fieldValue(bean: personaInstance, field: "apellido")}</td>--}%

                            %{--<td>${fieldValue(bean: personaInstance, field: "login")}</td>--}%

                            %{--<td>${personaInstance.departamento?.descripcion}</td>--}%

                            %{--<td><g:formatBoolean boolean="${personaInstance.activo == 1}" true="Sí" false="No"/></td>--}%

                            %{--<td>--}%
                                %{--<a class="btn btn-small btn-show btn-ajax" href="#" rel="tooltip" title="Ver" data-id="${personaInstance.id}">--}%
                                    %{--<i class="icon-zoom-in icon-large"></i>--}%
                                %{--</a>--}%
                                %{--<a class="btn btn-small btn-edit btn-ajax" href="#" rel="tooltip" title="Editar" data-id="${personaInstance.id}">--}%
                                    %{--<i class="icon-pencil icon-large"></i>--}%
                                %{--</a>--}%
                                %{--<a class="btn btn-small btn-password btn-ajax" href="#" rel="tooltip" title="Cambiar password" data-id="${personaInstance.id}">--}%
                                    %{--<i class="icon-lock icon-large"></i>--}%
                                %{--</a>--}%
                                %{--<a class="btn btn-small btn-delete" href="#" rel="tooltip" title="Eliminar" data-id="${personaInstance.id}">--}%
                                    %{--<i class="icon-trash icon-large"></i>--}%
                                %{--</a>--}%
                            %{--</td>--}%
                        %{--</tr>--}%
                    %{--</g:each>--}%
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

            <div class="modal-body" id="modalBody">
            </div>

            <div class="modal-footer" id="modalFooter">
            </div>
        </div>

        <script type="text/javascript">
            var url = "${resource(dir:'images', file:'spinner_24.gif')}";
            var spinner = $("<img style='margin-left:15px;' src='" + url + "' alt='Cargando...'/>");

            function submitForm(btn) {
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
                        url     : "${createLink(action:'formOferente')}",
                        success : function (msg) {
                            var btnOk = $('<a href="#" data-dismiss="modal" class="btn">Cancelar</a>');
                            var btnSave = $('<a href="#"  class="btn btn-success"><i class="icon-save"></i> Guardar</a>');

                            btnSave.click(function () {
                                submitForm(btnSave);
                                return false;
                            });

                            $("#modalHeader").removeClass("btn-edit btn-show btn-delete");
                            $("#modalTitle").html("Crear Oferente");
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
                        url     : "${createLink(action:'formOferente')}",
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
                            $("#modalTitle").html("Editar Oferente");
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
                        url     : "${createLink(action:'showOferente')}",
                        data    : {
                            id : id
                        },
                        success : function (msg) {
                            var btnOk = $('<a href="#" data-dismiss="modal" class="btn btn-primary">Aceptar</a>');
                            $("#modalHeader").removeClass("btn-edit btn-show btn-delete").addClass("btn-show");
                            $("#modalTitle").html("Ver Oferente");
                            $("#modalBody").html(msg);
                            $("#modalFooter").html("").append(btnOk);
                            $("#modal-Persona").modal("show");
                        }
                    });
                    return false;
                }); //click btn show



                $(".btn-cambiarEstado").click(function () {

                    var id = $(this).data("id");

                    console.log("-->>" + id)

                    var estado = $(this).data("activo");

                    console.log("estado:" + estado)

                    if(estado == 0) {


//                        estado = $(this).data("activo",1)

                        $.ajax({

                            type: "POST",
                            url: "${g.createLink(action: 'cambiarEstado')}",
                            data : {id: id,
                                activo:   '1'

                            },
                            success: function (msg){

                                if (msg == 'ok') {

                                    location.href = "${createLink(action: 'listOferente')}"

                                }

                            }



                        });

                    }if(estado == 1) {


//                        $(this).data("activo", 0)

                        $.ajax({

                            type: "POST",
                            url: "${g.createLink(action: 'cambiarEstado')}",
                            data : {id: id,
                                activo:   '0'

                            },
                            success: function (msg){

                                if (msg == 'ok') {

                                    location.href = "${createLink(action: 'listOferente')}"

                                }


                            }



                        });



                    }









                });



//                $(".btn-delete").click(function () {
//                    var id = $(this).data("id");
//                    $("#id").val(id);
//                    var btnOk = $('<a href="#" data-dismiss="modal" class="btn">Cancelar</a>');
//                    var btnDelete = $('<a href="#" class="btn btn-danger"><i class="icon-trash"></i> Eliminar</a>');
//
//                    btnDelete.click(function () {
//                        btnDelete.replaceWith(spinner);
//                        $("#frmDelete-Oferente").submit();
//                        return false;
//                    });
//
//                    $("#modalHeader").removeClass("btn-edit btn-show btn-delete").addClass("btn-delete");
//                    $("#modalTitle").html("Eliminar Oferente");
//                    $("#modalBody").html("<p>¿Está seguro de querer eliminar este Oferente?</p>");
//                    $("#modalFooter").html("").append(btnOk).append(btnDelete);
//                    $("#modal-Persona").modal("show");
//                    return false;
//                });
                $(".btn-password").click(function () {
                    var id = $(this).data("id");
                    $.ajax({
                        type    : "POST",
                        url     : "${createLink(action:'passOferente')}",
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
                            $("#modalTitle").html("Cambiar password del Oferente");
                            $("#modalBody").html(msg);
                            $("#modalFooter").html("").append(btnOk).append(btnSave);
                            $("#modal-Persona").modal("show");
                        }
                    });
                    return false;
                }); //click btn password



            });

        </script>

    </body>
</html>
