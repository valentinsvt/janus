<%@ page import="janus.seguridad.Usro" %>

<div id="show-usro" class="span5" role="main">

    <form class="form-horizontal">

        <g:if test="${usroInstance?.persona}">
            <div class="control-group">
                <div>
                    <span id="persona-label" class="control-label label label-inverse">
                        Persona
                    </span>
                </div>

                <div class="controls">

                    <span aria-labelledby="persona-label">
                        %{--<g:link controller="persona" action="show" id="${usroInstance?.persona?.id}">--}%
                        ${usroInstance?.persona?.encodeAsHTML()}
                        %{--</g:link>--}%
                    </span>

                </div>
            </div>
        </g:if>

        <g:if test="${usroInstance?.login}">
            <div class="control-group">
                <div>
                    <span id="login-label" class="control-label label label-inverse">
                        Login
                    </span>
                </div>

                <div class="controls">

                    <span aria-labelledby="login-label">
                        <g:fieldValue bean="${usroInstance}" field="login"/>
                    </span>

                </div>
            </div>
        </g:if>

        <g:if test="${usroInstance?.password}">
            <div class="control-group">
                <div>
                    <span id="password-label" class="control-label label label-inverse">
                        Password
                    </span>
                </div>

                <div class="controls">

                    <span aria-labelledby="password-label">
                        <g:fieldValue bean="${usroInstance}" field="password"/>
                    </span>

                </div>
            </div>
        </g:if>

        <g:if test="${usroInstance?.autorizacion}">
            <div class="control-group">
                <div>
                    <span id="autorizacion-label" class="control-label label label-inverse">
                        Autorizacion
                    </span>
                </div>

                <div class="controls">

                    <span aria-labelledby="autorizacion-label">
                        <g:fieldValue bean="${usroInstance}" field="autorizacion"/>
                    </span>

                </div>
            </div>
        </g:if>

        <g:if test="${usroInstance?.sigla}">
            <div class="control-group">
                <div>
                    <span id="sigla-label" class="control-label label label-inverse">
                        Sigla
                    </span>
                </div>

                <div class="controls">

                    <span aria-labelledby="sigla-label">
                        <g:fieldValue bean="${usroInstance}" field="sigla"/>
                    </span>

                </div>
            </div>
        </g:if>

        <g:if test="${usroInstance?.activo}">
            <div class="control-group">
                <div>
                    <span id="activo-label" class="control-label label label-inverse">
                        Activo
                    </span>
                </div>

                <div class="controls">

                    <span aria-labelledby="activo-label">
                        <g:fieldValue bean="${usroInstance}" field="activo"/>
                    </span>

                </div>
            </div>
        </g:if>

        <g:if test="${usroInstance?.fechaPass}">
            <div class="control-group">
                <div>
                    <span id="fechaPass-label" class="control-label label label-inverse">
                        Fecha Pass
                    </span>
                </div>

                <div class="controls">

                    <span aria-labelledby="fechaPass-label">
                        <g:formatDate date="${usroInstance?.fechaPass}"/>
                    </span>

                </div>
            </div>
        </g:if>

        <g:if test="${usroInstance?.observaciones}">
            <div class="control-group">
                <div>
                    <span id="observaciones-label" class="control-label label label-inverse">
                        Observaciones
                    </span>
                </div>

                <div class="controls">

                    <span aria-labelledby="observaciones-label">
                        <g:fieldValue bean="${usroInstance}" field="observaciones"/>
                    </span>

                </div>
            </div>
        </g:if>

    </form>
</div>
