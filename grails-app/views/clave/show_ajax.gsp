
<%@ page import="janus.Clave" %>

<div id="show-clave" class="span5" role="main">

    <form class="form-horizontal">
    
    <g:if test="${claveInstance?.clave}">
        <div class="control-group">
            <div>
                <span id="clave-label" class="control-label label label-inverse">
                    Clave
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="clave-label">
                    <g:fieldValue bean="${claveInstance}" field="clave"/>
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${claveInstance?.dlgd}">
        <div class="control-group">
            <div>
                <span id="dlgd-label" class="control-label label label-inverse">
                    Dlgd
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="dlgd-label">
                    <g:fieldValue bean="${claveInstance}" field="dlgd"/>
                </span>
        
            </div>
        </div>
    </g:if>
    
    </form>
</div>
