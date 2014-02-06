
<%@ page import="janus.ValoresAnuales" %>

<div id="show-valoresAnuales" class="span5" role="main">

    <form class="form-horizontal">
    
    <g:if test="${valoresAnualesInstance?.anio}">
        <div class="control-group">
            <div>
                <span id="anio-label" class="control-label label label-inverse">
                    Año
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="anio-label">
                    <g:fieldValue bean="${valoresAnualesInstance}" field="anio"/>
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${valoresAnualesInstance?.costoDiesel}">
        <div class="control-group">
            <div>
                <span id="costoDiesel-label" class="control-label label label-inverse">
                    Costo Diesel
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="costoDiesel-label">
                    <g:fieldValue bean="${valoresAnualesInstance}" field="costoDiesel"/>
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${valoresAnualesInstance?.costoGrasa}">
        <div class="control-group">
            <div>
                <span id="costoGrasa-label" class="control-label label label-inverse">
                    Costo Grasa
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="costoGrasa-label">
                    <g:fieldValue bean="${valoresAnualesInstance}" field="costoGrasa"/>
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${valoresAnualesInstance?.costoLubricante}">
        <div class="control-group">
            <div>
                <span id="costoLubricante-label" class="control-label label label-inverse">
                    Costo Lubricante
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="costoLubricante-label">
                    <g:fieldValue bean="${valoresAnualesInstance}" field="costoLubricante"/>
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${valoresAnualesInstance?.factorCostoRepuestosReparaciones}">
        <div class="control-group">
            <div>
                <span id="factorCostoRepuestosReparaciones-label" class="control-label label label-inverse">
                    %{--Factor Costo Repuestos Reparaciones--}%
                    Factor CRR
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="factorCostoRepuestosReparaciones-label">
                    <g:fieldValue bean="${valoresAnualesInstance}" field="factorCostoRepuestosReparaciones"/>
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${valoresAnualesInstance?.sueldoBasicoUnificado}">
        <div class="control-group">
            <div>
                <span id="sueldoBasicoUnificado-label" class="control-label label label-inverse">
                    Sueldo Básico Unificado
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="sueldoBasicoUnificado-label">
                    <g:fieldValue bean="${valoresAnualesInstance}" field="sueldoBasicoUnificado"/>
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${valoresAnualesInstance?.tasaInteresAnual}">
        <div class="control-group">
            <div>
                <span id="tasaInteresAnual-label" class="control-label label label-inverse">
                    Tasa Interés Anual
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="tasaInteresAnual-label">
                    <g:fieldValue bean="${valoresAnualesInstance}" field="tasaInteresAnual"/>
                </span>
        
            </div>
        </div>
    </g:if>

        <g:if test="${valoresAnualesInstance?.tasaInteresAnual}">
            <div class="control-group">
                <div>
                    <span id="seguro-label" class="control-label label label-inverse">
                        Seguro
                    </span>
                </div>
                <div class="controls">

                    <span aria-labelledby="tasaInteresAnual-label">
                        <g:fieldValue bean="${valoresAnualesInstance}" field="seguro"/>
                    </span>

                </div>
            </div>
        </g:if>



        <g:if test="${valoresAnualesInstance?.inflacion}">
            <div class="control-group">
                <div>
                    <span id="inflacion-label" class="control-label label label-inverse">
                       Inflación
                    </span>
                </div>
                <div class="controls">

                    <span aria-labelledby="tasaInteresAnual-label">
                        <g:fieldValue bean="${valoresAnualesInstance}" field="inflacion"/>
                    </span>

                </div>
            </div>
        </g:if>



    
    </form>
</div>
