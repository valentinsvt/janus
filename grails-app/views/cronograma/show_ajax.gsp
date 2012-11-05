
<%@ page import="janus.Cronograma" %>

<div id="show-cronograma" class="span5" role="main">

    <form class="form-horizontal">
    
    <g:if test="${cronogramaInstance?.nombre}">
        <div class="control-group">
            <div>
                <span id="nombre-label" class="control-label label label-inverse">
                    Nombre
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="nombre-label">
                    <g:fieldValue bean="${cronogramaInstance}" field="nombre"/>
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${cronogramaInstance?.codigo}">
        <div class="control-group">
            <div>
                <span id="codigo-label" class="control-label label label-inverse">
                    Codigo
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="codigo-label">
                    <g:fieldValue bean="${cronogramaInstance}" field="codigo"/>
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${cronogramaInstance?.obra}">
        <div class="control-group">
            <div>
                <span id="obra-label" class="control-label label label-inverse">
                    Obra
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="obra-label">
        %{--<g:link controller="obra" action="show" id="${cronogramaInstance?.obra?.id}">--}%
                    ${cronogramaInstance?.obra?.encodeAsHTML()}
        %{--</g:link>--}%
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${cronogramaInstance?.orden}">
        <div class="control-group">
            <div>
                <span id="orden-label" class="control-label label label-inverse">
                    Orden
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="orden-label">
                    <g:fieldValue bean="${cronogramaInstance}" field="orden"/>
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${cronogramaInstance?.codigoCronograma}">
        <div class="control-group">
            <div>
                <span id="codigoCronograma-label" class="control-label label label-inverse">
                    Codigo Cronograma
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="codigoCronograma-label">
                    <g:fieldValue bean="${cronogramaInstance}" field="codigoCronograma"/>
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${cronogramaInstance?.cantidad}">
        <div class="control-group">
            <div>
                <span id="cantidad-label" class="control-label label label-inverse">
                    Cantidad
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="cantidad-label">
                    <g:fieldValue bean="${cronogramaInstance}" field="cantidad"/>
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${cronogramaInstance?.precioUnitario}">
        <div class="control-group">
            <div>
                <span id="precioUnitario-label" class="control-label label label-inverse">
                    Precio Unitario
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="precioUnitario-label">
                    <g:fieldValue bean="${cronogramaInstance}" field="precioUnitario"/>
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${cronogramaInstance?.sbtl}">
        <div class="control-group">
            <div>
                <span id="sbtl-label" class="control-label label label-inverse">
                    Sbtl
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="sbtl-label">
                    <g:fieldValue bean="${cronogramaInstance}" field="sbtl"/>
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${cronogramaInstance?.tipoCronograma}">
        <div class="control-group">
            <div>
                <span id="tipoCronograma-label" class="control-label label label-inverse">
                    Tipo Cronograma
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="tipoCronograma-label">
                    <g:fieldValue bean="${cronogramaInstance}" field="tipoCronograma"/>
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${cronogramaInstance?.tipoPeriodo}">
        <div class="control-group">
            <div>
                <span id="tipoPeriodo-label" class="control-label label label-inverse">
                    Tipo Periodo
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="tipoPeriodo-label">
                    <g:fieldValue bean="${cronogramaInstance}" field="tipoPeriodo"/>
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${cronogramaInstance?.periodo}">
        <div class="control-group">
            <div>
                <span id="periodo-label" class="control-label label label-inverse">
                    Periodo
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="periodo-label">
                    <g:fieldValue bean="${cronogramaInstance}" field="periodo"/>
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${cronogramaInstance?.crno__p1}">
        <div class="control-group">
            <div>
                <span id="crno__p1-label" class="control-label label label-inverse">
                    Crnop1
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="crno__p1-label">
                    <g:fieldValue bean="${cronogramaInstance}" field="crno__p1"/>
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${cronogramaInstance?.crno__p2}">
        <div class="control-group">
            <div>
                <span id="crno__p2-label" class="control-label label label-inverse">
                    Crnop2
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="crno__p2-label">
                    <g:fieldValue bean="${cronogramaInstance}" field="crno__p2"/>
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${cronogramaInstance?.crno_p50}">
        <div class="control-group">
            <div>
                <span id="crno_p50-label" class="control-label label label-inverse">
                    Crnop50
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="crno_p50-label">
                    <g:fieldValue bean="${cronogramaInstance}" field="crno_p50"/>
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${cronogramaInstance?.ttal}">
        <div class="control-group">
            <div>
                <span id="ttal-label" class="control-label label label-inverse">
                    Ttal
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="ttal-label">
                    <g:fieldValue bean="${cronogramaInstance}" field="ttal"/>
                </span>
        
            </div>
        </div>
    </g:if>
    
    </form>
</div>
