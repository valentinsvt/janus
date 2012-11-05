<% import grails.persistence.Event %>
<%=packageName%>
<!doctype html>
<html>
    <head>
        <meta name="layout" content="main">
        <title>
            Lista de${className.replaceAll('[A-Z]') {' ' + it}}s
        </title>
        <script src="\${resource(dir: 'js/jquery/plugins/jquery-validation-1.9.0', file: 'jquery.validate.min.js')}"></script>
        <script src="\${resource(dir: 'js/jquery/plugins/jquery-validation-1.9.0', file: 'messages_es.js')}"></script>
    </head>
    <body>

        <div class="span12">
            <g:if test="\${flash.message}">
                <div class="alert \${flash.clase ?: 'alert-info'}" role="status">
                    <a class="close" data-dismiss="alert" href="#">×</a>
                    \${flash.message}
                </div>
            </g:if>
        </div>

        <div class="span12 btn-group" role="navigation">
            <a href="#" class="btn btn-ajax btn-new">
                <i class="icon-file"></i>
                Crear ${className.replaceAll('[A-Z]') {' ' + it}}
            </a>
        </div>

        <g:form action="delete" name="frmDelete-${className}">
            <g:hiddenField name="id"/>
        </g:form>

        <div id="list-${className}" class="span12" role="main" style="margin-top: 10px;">

            <table class="table table-bordered table-striped table-condensed table-hover">
                <thead>
                    <tr>
                    <%  excludedProps = Event.allEvents.toList() << 'id' << 'version'
                        allowedNames = domainClass.persistentProperties*.name << 'dateCreated' << 'lastUpdated'
                        props = domainClass.properties.findAll { allowedNames.contains(it.name) && !excludedProps.contains(it.name) && it.type != null && !Collection.isAssignableFrom(it.type) }
                        Collections.sort(props, comparator.constructors[0].newInstance([domainClass] as Object[]))
                        props.eachWithIndex { p, i ->
                            if (i < 5) {
                                if (p.isAssociation()) { %>
                        <th>${p.naturalName}</th>
                    <%      } else { %>
                        <g:sortableColumn property="${p.name}" title="${p.naturalName}" />
                    <%  }   }   } %>
                        <th width="150">Acciones</th>
                    </tr>
                </thead>
                <tbody class="paginate">
                <g:each in="\${${propertyName}List}" status="i" var="${propertyName}">
                    <tr>
                    <%  props.eachWithIndex { p, i ->
                            if (i == 0) { %>
                        <td>\${fieldValue(bean: ${propertyName}, field: "${p.name}")}</td>
                    <%      } else if (i < 6) {
                                if (p.type == Boolean || p.type == boolean) { %>
                        <td><g:formatBoolean boolean="\${${propertyName}.${p.name}}" /></td>
                    <%          } else if (p.type == Date || p.type == java.sql.Date || p.type == java.sql.Time || p.type == Calendar) { %>
                        <td><g:formatDate date="\${${propertyName}.${p.name}}" /></td>
                    <%          } else { %>
                        <td>\${fieldValue(bean: ${propertyName}, field: "${p.name}")}</td>
                    <%  }   }   } %>
                        <td>
                            <a class="btn btn-small btn-show btn-ajax" href="#" rel="tooltip" title="Ver" data-id="\${${propertyName}.id}">
                                <i class="icon-zoom-in icon-large"></i>
                            </a>
                            <a class="btn btn-small btn-edit btn-ajax" href="#" rel="tooltip" title="Editar" data-id="\${${propertyName}.id}">
                                <i class="icon-pencil icon-large"></i>
                            </a>

                            <a class="btn btn-small btn-delete" href="#" rel="tooltip" title="Eliminar" data-id="\${${propertyName}.id}">
                                <i class="icon-trash icon-large"></i>
                            </a>
                        </td>
                    </tr>
                </g:each>
                </tbody>
            </table>

        </div>

        <div class="modal hide fade" id="modal-${className}">
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
            var url = "\${resource(dir:'images', file:'spinner_24.gif')}";
            var spinner = \$("<img style='margin-left:15px;' src='" + url + "' alt='Cargando...'/>");

            function submitForm(btn) {
                if (\$("#frmSave-${className}").valid()) {
                    btn.replaceWith(spinner);
                }
                \$("#frmSave-${className}").submit();
            }

            \$(function () {
                \$('[rel=tooltip]').tooltip();

                \$(".paginate").paginate({
                    maxRows: 10
                });

                \$(".btn-new").click(function () {
                    \$.ajax({
                        type    : "POST",
                        url     : "\${createLink(action:'form_ajax')}",
                        success : function (msg) {
                            var btnOk = \$('<a href="#" data-dismiss="modal" class="btn">Cancelar</a>');
                            var btnSave = \$('<a href="#"  class="btn btn-success"><i class="icon-save"></i> Guardar</a>');

                            btnSave.click(function () {
                                submitForm(btnSave);
                                return false;
                            });

                            \$("#modalHeader").removeClass("btn-edit btn-show btn-delete");
                            \$("#modalTitle").html("Crear${className.replaceAll('[A-Z]') {' ' + it}}");
                            \$("#modalBody").html(msg);
                            \$("#modalFooter").html("").append(btnOk).append(btnSave);
                            \$("#modal-${className}").modal("show");
                        }
                    });
                    return false;
                }); //click btn new

                \$(".btn-edit").click(function () {
                    var id = \$(this).data("id");
                    \$.ajax({
                        type    : "POST",
                        url     : "\${createLink(action:'form_ajax')}",
                        data    : {
                            id : id
                        },
                        success : function (msg) {
                            var btnOk = \$('<a href="#" data-dismiss="modal" class="btn">Cancelar</a>');
                            var btnSave = \$('<a href="#"  class="btn btn-success"><i class="icon-save"></i> Guardar</a>');

                            btnSave.click(function () {
                                submitForm(btnSave);
                                return false;
                            });

                            \$("#modalHeader").removeClass("btn-edit btn-show btn-delete").addClass("btn-edit");
                            \$("#modalTitle").html("Editar${className.replaceAll('[A-Z]') {' ' + it}}");
                            \$("#modalBody").html(msg);
                            \$("#modalFooter").html("").append(btnOk).append(btnSave);
                            \$("#modal-${className}").modal("show");
                        }
                    });
                    return false;
                }); //click btn edit

                \$(".btn-show").click(function () {
                    var id = \$(this).data("id");
                    \$.ajax({
                        type    : "POST",
                        url     : "\${createLink(action:'show_ajax')}",
                        data    : {
                            id : id
                        },
                        success : function (msg) {
                            var btnOk = \$('<a href="#" data-dismiss="modal" class="btn btn-primary">Aceptar</a>');
                            \$("#modalHeader").removeClass("btn-edit btn-show btn-delete").addClass("btn-show");
                            \$("#modalTitle").html("Ver${className.replaceAll('[A-Z]') {' ' + it}}");
                            \$("#modalBody").html(msg);
                            \$("#modalFooter").html("").append(btnOk);
                            \$("#modal-${className}").modal("show");
                        }
                    });
                    return false;
                }); //click btn show

                \$(".btn-delete").click(function () {
                    var id = \$(this).data("id");
                    \$("#id").val(id);
                    var btnOk = \$('<a href="#" data-dismiss="modal" class="btn">Cancelar</a>');
                    var btnDelete = \$('<a href="#" class="btn btn-danger"><i class="icon-trash"></i> Eliminar</a>');

                    btnDelete.click(function () {
                        btnDelete.replaceWith(spinner);
                        \$("#frmDelete-${className}").submit();
                        return false;
                    });

                    \$("#modalHeader").removeClass("btn-edit btn-show btn-delete").addClass("btn-delete");
                    \$("#modalTitle").html("Eliminar${className.replaceAll('[A-Z]') {' ' + it}}");
                    \$("#modalBody").html("<p>¿Está seguro de querer eliminar est${className.endsWith("a")?"a":"e"}${className.replaceAll('[A-Z]') {' ' + it}}?</p>");
                    \$("#modalFooter").html("").append(btnOk).append(btnDelete);
                    \$("#modal-${className}").modal("show");
                    return false;
                });

            });

        </script>

    </body>
</html>
