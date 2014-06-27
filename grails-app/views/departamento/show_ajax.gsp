<%@ page import="janus.Departamento" %>

<div id="show-departamento" class="span5" role="main">

    <form class="form-horizontal">

        <g:if test="${departamentoInstance?.descripcion}">
            <div class="control-group">
                <div>
                    <span id="descripcion-label" class="control-label label label-inverse">
                        Descripcion
                    </span>
                </div>

                <div class="controls">

                    <span aria-labelledby="descripcion-label">
                        <g:fieldValue bean="${departamentoInstance}" field="descripcion"/>
                    </span>

                </div>
            </div>
        </g:if>

        <g:if test="${departamentoInstance?.direccion}">
            <div class="control-group">
                <div>
                    <span id="direccion-label" class="control-label label label-inverse">
                        Direccion
                    </span>
                </div>

                <div class="controls">

                    <span aria-labelledby="direccion-label">
                        %{--<g:link controller="direccion" action="show" id="${departamentoInstance?.direccion?.id}">--}%
                        ${departamentoInstance?.direccion?.encodeAsHTML()}
                        %{--</g:link>--}%
                    </span>

                </div>
            </div>
        </g:if>

        <g:if test="${departamentoInstance?.permisos}">
            <div class="control-group">
                <div>
                    <span id="permisos-label" class="control-label label label-inverse">
                        Permisos
                    </span>
                </div>

                <div class="controls">

                    <span aria-labelledby="permisos-label">
                        <g:fieldValue bean="${departamentoInstance}" field="permisos"/>
                    </span>

                </div>
            </div>
        </g:if>

        <g:if test="${departamentoInstance?.codigo}">
            <div class="control-group">
                <div>
                    <span id="codigo-label" class="control-label label label-inverse">
                        Codigo
                    </span>
                </div>

                <div class="controls">

                    <span aria-labelledby="codigo-label">
                        <g:fieldValue bean="${departamentoInstance}" field="codigo"/>
                    </span>

                </div>
            </div>
        </g:if>

        <div class="control-group">
            <div>
                <span id="codigo-label" class="control-label label label-inverse">
                    Requirente de Obras:
                </span>
            </div>

            <div class="controls">

                <span aria-labelledby="codigo-label">
                    ${departamentoInstance.requirente == 1 ? 'Si' : 'No'}
                </span>

            </div>
        </div>

    </form>
</div>
