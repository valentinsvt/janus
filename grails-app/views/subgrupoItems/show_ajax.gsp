
<%@ page import="janus.SubgrupoItems" %>

<div id="show-subgrupoItems" class="span5" role="main">

    <form class="form-horizontal">
    
    <g:if test="${subgrupoItemsInstance?.grupo}">
        <div class="control-group">
            <div>
                <span id="grupo-label" class="control-label label label-inverse">
                    Grupo
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="grupo-label">
        %{--<g:link controller="grupo" action="show" id="${subgrupoItemsInstance?.grupo?.id}">--}%
                    ${subgrupoItemsInstance?.grupo?.encodeAsHTML()}
        %{--</g:link>--}%
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${subgrupoItemsInstance?.codigo}">
        <div class="control-group">
            <div>
                <span id="codigo-label" class="control-label label label-inverse">
                    Codigo
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="codigo-label">
                    <g:fieldValue bean="${subgrupoItemsInstance}" field="codigo"/>
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${subgrupoItemsInstance?.descripcion}">
        <div class="control-group">
            <div>
                <span id="descripcion-label" class="control-label label label-inverse">
                    Descripcion
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="descripcion-label">
                    <g:fieldValue bean="${subgrupoItemsInstance}" field="descripcion"/>
                </span>
        
            </div>
        </div>
    </g:if>
    
    </form>
</div>
