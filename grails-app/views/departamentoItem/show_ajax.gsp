
<%@ page import="janus.DepartamentoItem" %>

<div id="show-departamentoItem" class="span5" role="main">

    <form class="form-horizontal">
    
    <g:if test="${departamentoItemInstance?.descripcion}">
        <div class="control-group">
            <div>
                <span id="descripcion-label" class="control-label label label-inverse">
                    Descripcion
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="descripcion-label">
                    <g:fieldValue bean="${departamentoItemInstance}" field="descripcion"/>
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
                    ${departamentoItemInstance?.transporte?.encodeAsHTML()}
        %{--</g:link>--}%
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${departamentoItemInstance?.codigo}">
        <div class="control-group">
            <div>
                <span id="codigo-label" class="control-label label label-inverse">
                    Codigo
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="codigo-label">
                    <g:fieldValue bean="${departamentoItemInstance}" field="codigo"/>
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${departamentoItemInstance?.subgrupo}">
        <div class="control-group">
            <div>
                <span id="subgrupo-label" class="control-label label label-inverse">
                    Subgrupo
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="subgrupo-label">
        %{--<g:link controller="subgrupoItems" action="show" id="${departamentoItemInstance?.subgrupo?.id}">--}%
                    ${departamentoItemInstance?.subgrupo?.encodeAsHTML()}
        %{--</g:link>--}%
                </span>
        
            </div>
        </div>
    </g:if>
    
    </form>
</div>
