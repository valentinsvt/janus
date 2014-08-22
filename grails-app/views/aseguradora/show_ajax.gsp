<%@ page import="janus.pac.Aseguradora" %>

<div id="show-aseguradora" class="span5" role="main">

    <form class="form-horizontal">

        <g:if test="${aseguradoraInstance?.nombre}">
            <div class="control-group">
                <div>
                    <span id="nombre-label" class="control-label label label-inverse">
                        Nombre
                    </span>
                </div>

                <div class="controls">

                    <span aria-labelledby="nombre-label">
                        <g:fieldValue bean="${aseguradoraInstance}" field="nombre"/>
                    </span>

                </div>
            </div>
        </g:if>

        <g:if test="${aseguradoraInstance?.fax}">
            <div class="control-group">
                <div>
                    <span id="fax-label" class="control-label label label-inverse">
                        Fax
                    </span>
                </div>

                <div class="controls">

                    <span aria-labelledby="fax-label">
                        <g:fieldValue bean="${aseguradoraInstance}" field="fax"/>
                    </span>

                </div>
            </div>
        </g:if>

        <g:if test="${aseguradoraInstance?.telefonos}">
            <div class="control-group">
                <div>
                    <span id="telefonos-label" class="control-label label label-inverse">
                        Teléfonos
                    </span>
                </div>

                <div class="controls">

                    <span aria-labelledby="telefonos-label">
                        <g:fieldValue bean="${aseguradoraInstance}" field="telefonos"/>
                    </span>

                </div>
            </div>
        </g:if>

        <g:if test="${aseguradoraInstance?.mail}">
            <div class="control-group">
                <div>
                    <span id="mail-label" class="control-label label label-inverse">
                        Mail
                    </span>
                </div>

                <div class="controls">

                    <span aria-labelledby="mail-label">
                        <g:fieldValue bean="${aseguradoraInstance}" field="mail"/>
                    </span>

                </div>
            </div>
        </g:if>

        <g:if test="${aseguradoraInstance?.responsable}">
            <div class="control-group">
                <div>
                    <span id="responsable-label" class="control-label label label-inverse">
                        Responsable
                    </span>
                </div>

                <div class="controls">

                    <span aria-labelledby="responsable-label">
                        <g:fieldValue bean="${aseguradoraInstance}" field="responsable"/>
                    </span>

                </div>
            </div>
        </g:if>

        <g:if test="${aseguradoraInstance?.fechaContacto}">
            <div class="control-group">
                <div>
                    <span id="fechaContacto-label" class="control-label label label-inverse">
                        Fecha Contacto
                    </span>
                </div>

                <div class="controls">

                    <span aria-labelledby="fechaContacto-label">
                        <g:formatDate date="${aseguradoraInstance?.fechaContacto}" format="dd-MM-yyyy"/>
                    </span>

                </div>
            </div>
        </g:if>

        <g:if test="${aseguradoraInstance?.direccion}">
            <div class="control-group">
                <div>
                    <span id="direccion-label" class="control-label label label-inverse">
                        Dirección
                    </span>
                </div>

                <div class="controls">

                    <span aria-labelledby="direccion-label">
                        <g:fieldValue bean="${aseguradoraInstance}" field="direccion"/>
                    </span>

                </div>
            </div>
        </g:if>

        <g:if test="${aseguradoraInstance?.observaciones}">
            <div class="control-group">
                <div>
                    <span id="observaciones-label" class="control-label label label-inverse">
                        Observaciones
                    </span>
                </div>

                <div class="controls">

                    <span aria-labelledby="observaciones-label">
                        <g:fieldValue bean="${aseguradoraInstance}" field="observaciones"/>
                    </span>

                </div>
            </div>
        </g:if>

        <g:if test="${aseguradoraInstance?.tipo}">
            <div class="control-group">
                <div>
                    <span id="tipo-label" class="control-label label label-inverse">
                        Tipo
                    </span>
                </div>

                <div class="controls">

                    <span aria-labelledby="tipo-label">
                        %{--<g:link controller="tipoAseguradora" action="show" id="${aseguradoraInstance?.tipo?.id}">--}%
                        ${aseguradoraInstance?.tipo?.descripcion?.encodeAsHTML()}
                        %{--</g:link>--}%
                    </span>

                </div>
            </div>
        </g:if>

    </form>
</div>
