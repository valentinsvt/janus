<%@ page import="janus.TipoObra" %>

<div id="show-tipoObra" class="span5" role="main">

    <form class="form-horizontal">

        <g:if test="${tipoObraInstance?.codigo}">
            <div class="control-group">
                <div>
                    <span id="codigo-label" class="control-label label label-inverse">
                        Código
                    </span>
                </div>

                <div class="controls">

                    <span aria-labelledby="codigo-label">
                        <g:fieldValue bean="${tipoObraInstance}" field="codigo"/>
                    </span>

                </div>
            </div>
        </g:if>

        <g:if test="${tipoObraInstance?.descripcion}">
            <div class="control-group">
                <div>
                    <span id="descripcion-label" class="control-label label label-inverse">
                        Descripción
                    </span>
                </div>

                <div class="controls">

                    <span aria-labelledby="descripcion-label">
                        <g:fieldValue bean="${tipoObraInstance}" field="descripcion"/>
                    </span>

                </div>
            </div>
        </g:if>

        <g:if test="${tipoObraInstance?.grupo}">
            <div class="control-group">
                <div>
                    <span id="grupo-label" class="control-label label label-inverse">
                        Grupo
                    </span>
                </div>

                <div class="controls">

                    <span aria-labelledby="grupo-label">
                        %{--<g:link controller="grupo" action="show" id="${tipoObraInstance?.grupo?.id}">--}%
                        ${tipoObraInstance?.grupo?.encodeAsHTML()}
                        %{--</g:link>--}%
                    </span>

                </div>
            </div>
        </g:if>

    </form>
</div>
