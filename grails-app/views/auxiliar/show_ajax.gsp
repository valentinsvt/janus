
<%@ page import="janus.Auxiliar" %>

<div id="show-auxiliar" class="span5" role="main">

    <form class="form-horizontal">
    
    <g:if test="${auxiliarInstance?.subPrograma}">
        <div class="control-group">
            <div>
                <span id="subPrograma-label" class="control-label label label-inverse">
                    Sub Programa
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="subPrograma-label">
                    <g:fieldValue bean="${auxiliarInstance}" field="subPrograma"/>
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${auxiliarInstance?.general}">
        <div class="control-group">
            <div>
                <span id="general-label" class="control-label label label-inverse">
                    General
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="general-label">
                    <g:fieldValue bean="${auxiliarInstance}" field="general"/>
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${auxiliarInstance?.baseCont}">
        <div class="control-group">
            <div>
                <span id="baseCont-label" class="control-label label label-inverse">
                    Base Cont
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="baseCont-label">
                    <g:fieldValue bean="${auxiliarInstance}" field="baseCont"/>
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${auxiliarInstance?.presupuestoRef}">
        <div class="control-group">
            <div>
                <span id="presupuestoRef-label" class="control-label label label-inverse">
                    Presupuesto Ref
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="presupuestoRef-label">
                    <g:fieldValue bean="${auxiliarInstance}" field="presupuestoRef"/>
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${auxiliarInstance?.retencion}">
        <div class="control-group">
            <div>
                <span id="retencion-label" class="control-label label label-inverse">
                    Retencion
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="retencion-label">
                    <g:fieldValue bean="${auxiliarInstance}" field="retencion"/>
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${auxiliarInstance?.notaAuxiliar}">
        <div class="control-group">
            <div>
                <span id="notaAuxiliar-label" class="control-label label label-inverse">
                    Nota Auxiliar
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="notaAuxiliar-label">
                    <g:fieldValue bean="${auxiliarInstance}" field="notaAuxiliar"/>
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${auxiliarInstance?.nota}">
        <div class="control-group">
            <div>
                <span id="nota-label" class="control-label label label-inverse">
                    Nota
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="nota-label">
                    <g:fieldValue bean="${auxiliarInstance}" field="nota"/>
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${auxiliarInstance?.nota1}">
        <div class="control-group">
            <div>
                <span id="nota1-label" class="control-label label label-inverse">
                    Nota1
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="nota1-label">
                    <g:fieldValue bean="${auxiliarInstance}" field="nota1"/>
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${auxiliarInstance?.nota2}">
        <div class="control-group">
            <div>
                <span id="nota2-label" class="control-label label label-inverse">
                    Nota2
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="nota2-label">
                    <g:fieldValue bean="${auxiliarInstance}" field="nota2"/>
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${auxiliarInstance?.memo1}">
        <div class="control-group">
            <div>
                <span id="memo1-label" class="control-label label label-inverse">
                    Memo1
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="memo1-label">
                    <g:fieldValue bean="${auxiliarInstance}" field="memo1"/>
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${auxiliarInstance?.notaFormula}">
        <div class="control-group">
            <div>
                <span id="notaFormula-label" class="control-label label label-inverse">
                    Nota Formula
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="notaFormula-label">
                    <g:fieldValue bean="${auxiliarInstance}" field="notaFormula"/>
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${auxiliarInstance?.titulo}">
        <div class="control-group">
            <div>
                <span id="titulo-label" class="control-label label label-inverse">
                    Titulo
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="titulo-label">
                    <g:fieldValue bean="${auxiliarInstance}" field="titulo"/>
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${auxiliarInstance?.memo2}">
        <div class="control-group">
            <div>
                <span id="memo2-label" class="control-label label label-inverse">
                    Memo2
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="memo2-label">
                    <g:fieldValue bean="${auxiliarInstance}" field="memo2"/>
                </span>
        
            </div>
        </div>
    </g:if>
    
    </form>
</div>
