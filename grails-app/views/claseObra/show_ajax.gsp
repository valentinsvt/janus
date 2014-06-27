<%@ page import="janus.ClaseObra" %>

<div id="show-claseObra" class="span5" role="main">

    <form class="form-horizontal">

        <g:if test="${claseObraInstance?.codigo}">
            <div class="control-group">
                <div>
                    <span id="codigo-label" class="control-label label label-inverse">
                        Código
                    </span>
                </div>

                <div class="controls">

                    <span aria-labelledby="codigo-label">
                        <g:fieldValue bean="${claseObraInstance}" field="codigo"/>
                    </span>

                </div>
            </div>
        </g:if>

        <g:if test="${claseObraInstance?.descripcion}">
            <div class="control-group">
                <div>
                    <span id="descripcion-label" class="control-label label label-inverse">
                        Descripción
                    </span>
                </div>

                <div class="controls">

                    <span aria-labelledby="descripcion-label">
                        <g:fieldValue bean="${claseObraInstance}" field="descripcion"/>
                    </span>

                </div>
            </div>
        </g:if>

        <g:if test="${claseObraInstance?.tipo}">
            <div class="control-group">
                <div>
                    <span id="tipo-label" class="control-label label label-inverse">
                        Tipo
                    </span>
                </div>

                <div class="controls">

                    <span aria-labelledby="tipo-label">
                        <g:fieldValue bean="${claseObraInstance}" field="tipo"/>
                    </span>

                </div>
            </div>
        </g:if>

        <g:if test="${claseObraInstance?.grupo}">
            <div class="control-group">
                <div>
                    <span id="grupo-label" class="control-label label label-inverse">
                        Grupo
                    </span>
                </div>

                <div class="controls">

                    <span aria-labelledby="grupo-label">
                        %{--<g:link controller="grupo" action="show" id="${claseObraInstance?.grupo?.id}">--}%
                        ${claseObraInstance?.grupo?.encodeAsHTML()}
                        %{--</g:link>--}%
                    </span>

                </div>
            </div>
        </g:if>

    </form>
</div>
