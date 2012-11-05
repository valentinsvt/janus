<%@ page import="janus.seguridad.Usro" %>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="main"/>

    <script type="text/javascript"
            src="${resource(dir: 'js/jquery/plugins/validation', file: 'jquery.validate.min.js')}"></script>
    <script type="text/javascript"
            src="${resource(dir: 'js/jquery/plugins/validation', file: 'additional-methods.js')}"></script>
    <script type="text/javascript"
            src="${resource(dir: 'js/jquery/plugins/validation', file: 'messages_es.js')}"></script>

    <script type="text/javascript"
            src="${resource(dir: 'js/jquery/plugins/qtip', file: 'jquery.qtip.min.js')}"></script>

    <link rel="stylesheet" href="${resource(dir: 'js/jquery/plugins/qtip', file: 'jquery.qtip.css')}"/>

    <title>${title}</title>
</head>

<body>
<div class="dialog" title="${title}">

    <div id="" class="toolbar ui-widget-header ui-corner-all">
        <g:link class="button list" action="list"><g:message code="usro.list" default="Lista de Usuarios"/></g:link>
    </div>

    <div class="body">
        <g:if test="${flash.message}">
            <div class="message ui-state-highlight ui-corner-all">
                <g:message code="${flash.message}" args="${flash.args}" default="${flash.defaultMessage}"/>
            </div>
        </g:if>
        <g:hasErrors bean="${usroInstance}">
            <div class="errors ui-state-error ui-corner-all">
                <g:renderErrors bean="${usroInstance}" as="list"/>
            </div>
        </g:hasErrors>
        <g:form action="save" class="frmUsro"
                method="post">
            <g:hiddenField name="id" value="${usroInstance?.id}"/>
            <g:hiddenField name="version" value="${usroInstance?.version}"/>
            <div>
                <fieldset class="ui-corner-all">
                    <legend class="ui-widget ui-widget-header ui-corner-all">
                        <g:if test="${source == 'edit'}">
                            <g:message code="usro.edit.legend" default="Editar datos del Usuario"/>
                        </g:if>
                        <g:else>
                            <g:message code="usro.create.legend" default="Ingreso de datos del Usuario"/>
                        </g:else>
                    </legend>


                    <div class="prop mandatory ${hasErrors(bean: usroInstance, field: 'persona', 'error')}">
                        <label for="persona">
                            <g:message code="usro.persona.label" default="Persona"/>
                            <span class="indicator">*</span>
                        </label>

                        <div class="campo">
                            <g:select class="10 field ui-widget-content ui-corner-all" name="persona.id" title="${Usro.constraints.persona.attributes.mensaje}"
                                      from="${janus.Persona.list()}" optionKey="id"
                                      value="${usroInstance?.persona?.id}"/>
                        </div>
                    </div>

                    <div class="prop ${hasErrors(bean: usroInstance, field: 'cargoPersonal', 'error')}">
                        <label for="cargoPersonal">
                            <g:message code="usro.cargoPersonal.label" default="Cargo Personal"/>

                        </label>

                        <div class="campo">
                            <g:select class="10 field ui-widget-content ui-corner-all" name="cargoPersonal.id"
                                      title="${Usro.constraints.cargoPersonal.attributes.mensaje}" from="${janus.CargoPersonal.list()}" optionKey="id"
                                      value="${usroInstance?.cargoPersonal?.id}" noSelection="['null': '']"/>
                        </div>
                    </div>

                    <div class="prop mandatory ${hasErrors(bean: usroInstance, field: 'usroLogin', 'error')}">
                        <label for="usroLogin">
                            <g:message code="usro.usroLogin.label" default="Usro Login"/>
                            <span class="indicator">*</span>
                        </label>

                        <div class="campo">
                            <g:textField name="usroLogin" id="usroLogin" title="${Usro.constraints.usroLogin.attributes.mensaje}"
                                         class="6 field required ui-widget-content ui-corner-all" minLenght="1"
                                         maxLenght="15" style="width: 150px;" value="${usroInstance?.usroLogin}"/>
                        </div>
                    </div>

                    <div class="prop mandatory ${hasErrors(bean: usroInstance, field: 'usroPassword', 'error')}">
                        <label for="usroPassword">
                            <g:message code="usro.usroPassword.label" default="Usro Password"/>
                            <span class="indicator">*</span>
                        </label>

                        <div class="campo">
                            <g:passwordField name="usroPassword" id="usroPassword" title="${Usro.constraints.usroPassword.attributes.mensaje}"
                                             class="6 field required ui-widget-content ui-corner-all" minLenght="1"
                                             maxLenght="64"/>
                        </div>
                    </div>

                    <div class="prop mandatory ${hasErrors(bean: usroInstance, field: 'autorizacion', 'error')}">
                        <label for="autorizacion">
                            <g:message code="usro.autorizacion.label" default="Autorizacion"/>
                            <span class="indicator">*</span>
                        </label>

                        <div class="campo">
                            <g:passwordField class="6 field required ui-widget-content ui-corner-all" minLenght="1"
                                             maxLenght="64" name="autorizacion" id="autorizacion" title="${Usro.constraints.autorizacion.attributes.mensaje}"/>
                        </div>
                    </div>

                    <div class="prop mandatory ${hasErrors(bean: usroInstance, field: 'sigla', 'error')}">
                        <label for="sigla">
                            <g:message code="usro.sigla.label" default="Sigla"/>
                            <span class="indicator">*</span>
                        </label>

                        <div class="campo">
                            <g:textField name="sigla" id="sigla" title="${Usro.constraints.sigla.attributes.mensaje}"
                                         class="6 field required ui-widget-content ui-corner-all" minLenght="1"
                                         maxLenght="8" style="width: 80px;" value="${usroInstance?.sigla}"/>
                        </div>
                    </div>

                    <div class="prop mandatory ${hasErrors(bean: usroInstance, field: 'usroActivo', 'error')}">
                        <label for="usroActivo">
                            <g:message code="usro.usroActivo.label" default="Usro Activo"/>
                            <span class="indicator">*</span>
                        </label>

                        <div class="campo">
                            <g:select from="${[0,1]}" name="usroActivo"
                                      value="${fieldValue(bean: usroInstance, field: 'usroActivo')}"/>
                        </div>
                    </div>

                    <div class="prop ${hasErrors(bean: usroInstance, field: 'fechaPass', 'error')}">
                        <label for="fechaPass">
                            <g:message code="usro.fechaPass.label" default="Fecha de próximo cambio de password"/>

                        </label>

                        <div class="campo">
                            <input type="hidden" value="date.struct" name="fechaPass">
                            <input type="hidden" name="fechaPass_day" id="fechaPass_day"
                                   value="${usroInstance?.fechaPass?.format('dd')}">
                            <input type="hidden" name="fechaPass_month" id="fechaPass_month"
                                   value="${usroInstance?.fechaPass?.format('MM')}">
                            <input type="hidden" name="fechaPass_year" id="fechaPass_year"
                                   value="${usroInstance?.fechaPass?.format('yyyy')}">
                            <g:textField class="25 datepicker field ui-widget-content ui-corner-all" name="fechaPass"
                                         title="${Usro.constraints.fechaPass.attributes.mensaje}" id="fechaPass" style="width: 120px;"
                                         value="${usroInstance?.fechaPass?.format('dd-MM-yyyy')}"/>
                            <script type='text/javascript'>
                                $('#fechaPass').datepicker({
                                    changeMonth: true,
                                    changeYear:true,
                                    dateFormat: 'dd-mm-yy',
                                    onClose: function(dateText, inst) {
                                        var date = $(this).datepicker('getDate');
                                        var day, month, year;
                                        if (date != null) {
                                            day = date.getDate();
                                            month = parseInt(date.getMonth()) + 1;
                                            year = date.getFullYear();
                                        } else {
                                            day = '';
                                            month = '';
                                            year = '';
                                        }
                                        var id = $(this).attr('id');
                                        $('#' + id + '_day').val(day);
                                        $('#' + id + '_month').val(month);
                                        $('#' + id + '_year').val(year);
                                    }
                                });
                            </script>
                        </div>
                    </div>

                    <div class="prop ${hasErrors(bean: usroInstance, field: 'observaciones', 'error')}">
                        <label for="observaciones">
                            <g:message code="usro.observaciones.label" default="Observaciones"/>

                        </label>

                        <div class="campo">
                            <g:textArea class="4 field ui-widget-content ui-corner-all" minLenght="1" maxLenght="255"
                                        name="observaciones" id="observaciones" title="${Usro.constraints.observaciones.attributes.mensaje}" cols="40" rows="1"
                                        style="width: 600px;" value="${usroInstance?.observaciones}"/>
                        </div>
                    </div>


                    <div class="buttons">
                        <g:if test="${source == 'edit'}">
                            <a href="#" class="button save"><g:message code="update" default="Actualizar"/></a>
                            <g:link class="button delete" action="delete" id="${usroInstance?.id}">
                                <g:message code="default.button.delete.label" default="Eliminar"/>
                            </g:link>
                            <g:link class="button show" action="show" id="${usroInstance?.id}">
                                <g:message code="default.button.show.label" default="Ver"/>
                            </g:link>
                        </g:if>
                        <g:else>
                            <a href="#" class="button save">
                                <g:message code="create" default="Crear"/>
                            </a>
                        </g:else>
                    </div>

                </fieldset>
            </div>
        </g:form>
    </div>
</div>

<script type="text/javascript">
    $(function() {
        var myForm = $(".frmUsro");

        // Tooltip de informacion para cada field (utiliza el atributo title del textfield)
        var elems = $('.field')
                .each(function(i) {
                    $.attr(this, 'oldtitle', $.attr(this, 'title'));
                })
                .removeAttr('title');
        $('<div />').qtip(
                {
                    content: ' ', // Can use any content here :)
                    position: {
                        target: 'event' // Use the triggering element as the positioning target
                    },
                    show: {
                        target: elems,
                        event: 'click mouseenter focus'
                    },
                    hide: {
                        target: elems
                    },
                    events: {
                        show: function(event, api) {
                            // Update the content of the tooltip on each show
                            var target = $(event.originalEvent.target);
                            api.set('content.text', target.attr('title'));
                        }
                    },
                    style: {
                        classes: 'ui-tooltip-rounded ui-tooltip-cream'
                    }
                });
        // fin del codigo para los tooltips

        // Validacion del formulario
        myForm.validate({
            errorClass: "errormessage",
            onkeyup: false,
            errorElement: "em",
            errorClass: 'error',
            validClass: 'valid',
            errorPlacement: function(error, element) {
                // Set positioning based on the elements position in the form
                var elem = $(element),
                        corners = ['right center', 'left center'],
                        flipIt = elem.parents('span.right').length > 0;

                // Check we have a valid error message
                if (!error.is(':empty')) {
                    // Apply the tooltip only if it isn't valid
                    elem.filter(':not(.valid)').qtip({
                        overwrite: false,
                        content: error,
                        position: {
                            my: corners[ flipIt ? 0 : 1 ],
                            at: corners[ flipIt ? 1 : 0 ],
                            viewport: $(window)
                        },
                        show: {
                            event: false,
                            ready:
                                    true
                        },
                        hide: false,
                        style: {
                            classes: 'ui-tooltip-rounded ui-tooltip-red' // Make it red... the classic error colour!
                        }
                    })

                        // If we have a tooltip on this element already, just update its content
                            .qtip('option', 'content.text', error);
                }

                // If the error is empty, remove the qTip
                else {
                    elem.qtip('destroy');
                }
            },
            success: $.noop // Odd workaround for errorPlacement not firing!
        })
                ;
        //fin de la validacion del formulario


        $(".button").button();
        $(".home").button("option", "icons", {primary:'ui-icon-home'});
        $(".list").button("option", "icons", {primary:'ui-icon-clipboard'});
        $(".show").button("option", "icons", {primary:'ui-icon-bullet'});
        $(".save").button("option", "icons", {primary:'ui-icon-disk'}).click(function() {
            myForm.submit();
            return false;
        });
        $(".delete").button("option", "icons", {primary:'ui-icon-trash'}).click(function() {
            if (confirm("${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}")) {
                return true;
            }
            return false;
        });
    });
</script>

</body>
</html>