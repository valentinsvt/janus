<%@ page import="janus.DepartamentoItem" %>

<form class="form-horizontal">

    <div class="tituloTree">${departamentoItemInstance.descripcion}</div>
%{--<g:if test="${departamentoItemInstance?.descripcion}">--}%
%{--<div class="control-group">--}%
%{--<div>--}%
%{--<span id="descripcion-label" class="control-label label label-inverse">--}%
%{--Descripción--}%
%{--</span>--}%
%{--</div>--}%

%{--<div class="controls">--}%

%{--<span aria-labelledby="descripcion-label">--}%
%{--<g:fieldValue bean="${departamentoItemInstance}" field="descripcion"/>--}%
%{--</span>--}%

%{--</div>--}%
%{--</div>--}%
%{--</g:if>--}%

    <g:if test="${departamentoItemInstance?.codigo}">
        <div class="control-group">
            <div>
                <span id="codigo-label" class="control-label label label-inverse">
                    Código
                </span>
            </div>

            <div class="controls">

                <span aria-labelledby="codigo-label">
                    ${departamentoItemInstance?.subgrupo?.codigo.toString().padLeft(3,'0')}.${departamentoItemInstance?.codigo.toString().padLeft(3,'0')}
                </span>

            </div>
        </div>
    </g:if>

    <g:if test="${departamentoItemInstance?.subgrupo}">
        <div class="control-group">
            <div>
                <span id="subgrupo-label" class="control-label label label-inverse">
                    Grupo
                </span>
            </div>

            <div class="controls">

                <span aria-labelledby="subgrupo-label">
                    %{--<g:link controller="subgrupoItems" action="show" id="${departamentoItemInstance?.subgrupo?.id}">--}%
                    ${departamentoItemInstance?.subgrupo?.descripcion}
                    %{--</g:link>--}%
                </span>

            </div>
        </div>
    </g:if>

    <g:if test="${departamentoItemInstance?.transporte}">
        <div class="control-group">
            <div>
                <span id="transporte-label" class="control-label label label-inverse">
                    Transporte
                </span>
            </div>

            <div class="controls">

                <span aria-labelledby="transporte-label">
                    %{--<g:link controller="transporte" action="show" id="${departamentoItemInstance?.transporte?.id}">--}%
                    ${departamentoItemInstance?.transporte?.descripcion}
                    %{--</g:link>--}%
                </span>

            </div>
        </div>
    </g:if>

</form>
