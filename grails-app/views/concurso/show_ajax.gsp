
<%@ page import="janus.Concurso" %>

<div id="show-concurso" class="span5" role="main">

    <form class="form-horizontal">
    
    <g:if test="${concursoInstance?.obra}">
        <div class="control-group">
            <div>
                <span id="obra-label" class="control-label label label-inverse">
                    Obra
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="obra-label">
        %{--<g:link controller="obra" action="show" id="${concursoInstance?.obra?.id}">--}%
                    ${concursoInstance?.obra?.encodeAsHTML()}
        %{--</g:link>--}%
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${concursoInstance?.administracion}">
        <div class="control-group">
            <div>
                <span id="administracion-label" class="control-label label label-inverse">
                    Administración
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="administracion-label">
        %{--<g:link controller="administracion" action="show" id="${concursoInstance?.administracion?.id}">--}%
                    ${concursoInstance?.administracion?.encodeAsHTML()}
        %{--</g:link>--}%
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${concursoInstance?.codigo}">
        <div class="control-group">
            <div>
                <span id="codigo-label" class="control-label label label-inverse">
                    Código
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="codigo-label">
                    <g:fieldValue bean="${concursoInstance}" field="codigo"/>
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${concursoInstance?.objetivo}">
        <div class="control-group">
            <div>
                <span id="objetivo-label" class="control-label label label-inverse">
                    Objetivo
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="objetivo-label">
                    <g:fieldValue bean="${concursoInstance}" field="objetivo"/>
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${concursoInstance?.base}">
        <div class="control-group">
            <div>
                <span id="base-label" class="control-label label label-inverse">
                    Base
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="base-label">
                    <g:fieldValue bean="${concursoInstance}" field="base"/>
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${concursoInstance?.tipoCuenta}">
        <div class="control-group">
            <div>
                <span id="tipoCuenta-label" class="control-label label label-inverse">
                    Tipo Cuenta
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="tipoCuenta-label">
        %{--<g:link controller="tipoCuenta" action="show" id="${concursoInstance?.tipoCuenta?.id}">--}%
                    ${concursoInstance?.tipoCuenta?.encodeAsHTML()}
        %{--</g:link>--}%
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${concursoInstance?.estado}">
        <div class="control-group">
            <div>
                <span id="estado-label" class="control-label label label-inverse">
                    Estado
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="estado-label">
                    <g:fieldValue bean="${concursoInstance}" field="estado"/>
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${concursoInstance?.fechaInicio}">
        <div class="control-group">
            <div>
                <span id="fechaInicio-label" class="control-label label label-inverse">
                    Fecha Inicio
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="fechaInicio-label">
                    <g:formatDate date="${concursoInstance?.fechaInicio}" />
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${concursoInstance?.fechaCierre}">
        <div class="control-group">
            <div>
                <span id="fechaCierre-label" class="control-label label label-inverse">
                    Fecha Cierre
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="fechaCierre-label">
                    <g:formatDate date="${concursoInstance?.fechaCierre}" />
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${concursoInstance?.observaciones}">
        <div class="control-group">
            <div>
                <span id="observaciones-label" class="control-label label label-inverse">
                    Observaciones
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="observaciones-label">
                    <g:fieldValue bean="${concursoInstance}" field="observaciones"/>
                </span>
        
            </div>
        </div>
    </g:if>
    
    </form>
</div>
