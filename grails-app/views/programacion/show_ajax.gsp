<%@ page import="janus.Programacion" %>

<div id="show-programacion" class="span5" role="main">

    <form class="form-horizontal">

        <g:if test="${programacionInstance?.descripcion}">
            <div class="control-group">
                <div>
                    <span id="descripcion-label" class="control-label label label-inverse">
                        Descripcion
                    </span>
                </div>

                <div class="controls">

                    <span aria-labelledby="descripcion-label">
                        <g:fieldValue bean="${programacionInstance}" field="descripcion"/>
                    </span>

                </div>
            </div>
        </g:if>

        <g:if test="${programacionInstance?.fechaInicio}">
            <div class="control-group">
                <div>
                    <span id="fechaInicio-label" class="control-label label label-inverse">
                        Fecha Inicio
                    </span>
                </div>

                <div class="controls">

                    <span aria-labelledby="fechaInicio-label">
                        <g:formatDate date="${programacionInstance?.fechaInicio}"/>
                    </span>

                </div>
            </div>
        </g:if>

        <g:if test="${programacionInstance?.fechaFin}">
            <div class="control-group">
                <div>
                    <span id="fechaFin-label" class="control-label label label-inverse">
                        Fecha Fin
                    </span>
                </div>

                <div class="controls">

                    <span aria-labelledby="fechaFin-label">
                        <g:formatDate date="${programacionInstance?.fechaFin}"/>
                    </span>

                </div>
            </div>
        </g:if>

        <g:if test="${programacionInstance?.grupo}">
            <div class="control-group">
                <div>
                    <span id="grupo-label" class="control-label label label-inverse">
                        Grupo
                    </span>
                </div>

                <div class="controls">

                    <span aria-labelledby="grupo-label">
                        %{--<g:link controller="grupo" action="show" id="${programacionInstance?.grupo?.id}">--}%
                        ${programacionInstance?.grupo?.encodeAsHTML()}
                        %{--</g:link>--}%
                    </span>

                </div>
            </div>
        </g:if>

    </form>
</div>
