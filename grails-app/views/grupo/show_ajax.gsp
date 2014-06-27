<%@ page import="janus.Grupo" %>

<div id="show-grupo" class="span5" role="main">

    <form class="form-horizontal">

        <g:if test="${grupoInstance?.codigo}">
            <div class="control-group">
                <div>
                    <span id="codigo-label" class="control-label label label-inverse">
                        Código
                    </span>
                </div>

                <div class="controls">

                    <span aria-labelledby="codigo-label">
                        <g:fieldValue bean="${grupoInstance}" field="codigo"/>
                    </span>

                </div>
            </div>
        </g:if>

        <g:if test="${grupoInstance?.descripcion}">
            <div class="control-group">
                <div>
                    <span id="descripcion-label" class="control-label label label-inverse">
                        Descripción
                    </span>
                </div>

                <div class="controls">

                    <span aria-labelledby="descripcion-label">
                        <g:fieldValue bean="${grupoInstance}" field="descripcion"/>
                    </span>

                </div>
            </div>
        </g:if>

        <g:if test="${grupoInstance?.direccion}">
            <div class="control-group">
                <div>
                    <span id="direccion-label" class="control-label label label-inverse">
                        Dirección
                    </span>
                </div>

                <div class="controls">

                    <span aria-labelledby="direccion-label">
                        %{--<g:link controller="direccion" action="show" id="${grupoInstance?.direccion?.id}">--}%
                        ${grupoInstance?.direccion?.encodeAsHTML()}
                        %{--</g:link>--}%
                    </span>

                </div>
            </div>
        </g:if>

    </form>
</div>
