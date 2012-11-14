<%@ page import="janus.Grupo" %>


<form class="form-horizontal">

    <div class="tituloTree">${grupoInstance.descripcion}</div>

%{--<g:if test="${grupoInstance?.descripcion}">--}%
%{--<div class="control-group">--}%
%{--<div>--}%
%{--<span id="descripcion-label" class="control-label label label-inverse">--}%
%{--Descripción--}%
%{--</span>--}%
%{--</div>--}%

%{--<div class="controls">--}%

%{--<span aria-labelledby="descripcion-label">--}%
%{--<g:fieldValue bean="${grupoInstance}" field="descripcion"/>--}%
%{--</span>--}%

%{--</div>--}%
%{--</div>--}%
%{--</g:if>--}%

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

</form>
