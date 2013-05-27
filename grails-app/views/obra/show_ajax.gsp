
<%@ page import="janus.Obra" %>

<div id="show-obra" class="span5" role="main">

    <form class="form-horizontal">
    
    <g:if test="${obraInstance?.codigo}">
        <div class="control-group">
            <div>
                <span id="codigo-label" class="control-label label label-inverse">
                    Codigo
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="codigo-label">
                    <g:fieldValue bean="${obraInstance}" field="codigo"/>
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${obraInstance?.nombre}">
        <div class="control-group">
            <div>
                <span id="nombre-label" class="control-label label label-inverse">
                    Nombre
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="nombre-label">
                    <g:fieldValue bean="${obraInstance}" field="nombre"/>
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${obraInstance?.responsableObra}">
        <div class="control-group">
            <div>
                <span id="responsableObra-label" class="control-label label label-inverse">
                    Responsable Obra
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="responsableObra-label">
        %{--<g:link controller="persona" action="show" id="${obraInstance?.responsableObra?.id}">--}%
                    ${obraInstance?.responsableObra?.encodeAsHTML()}
        %{--</g:link>--}%
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${obraInstance?.revisor}">
        <div class="control-group">
            <div>
                <span id="revisor-label" class="control-label label label-inverse">
                    Revisor
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="revisor-label">
        %{--<g:link controller="persona" action="show" id="${obraInstance?.revisor?.id}">--}%
                    ${obraInstance?.revisor?.encodeAsHTML()}
        %{--</g:link>--}%
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${obraInstance?.lugar}">
        <div class="control-group">
            <div>
                <span id="lugar-label" class="control-label label label-inverse">
                    Lugar
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="lugar-label">
        %{--<g:link controller="lugar" action="show" id="${obraInstance?.lugar?.id}">--}%
                    ${obraInstance?.lugar?.encodeAsHTML()}
        %{--</g:link>--}%
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${obraInstance?.comunidad}">
        <div class="control-group">
            <div>
                <span id="comunidad-label" class="control-label label label-inverse">
                    Comunidad
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="comunidad-label">
        %{--<g:link controller="comunidad" action="show" id="${obraInstance?.comunidad?.id}">--}%
                    ${obraInstance?.comunidad?.encodeAsHTML()}
        %{--</g:link>--}%
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${obraInstance?.parroquia}">
        <div class="control-group">
            <div>
                <span id="parroquia-label" class="control-label label label-inverse">
                    Parroquia
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="parroquia-label">
        %{--<g:link controller="parroquia" action="show" id="${obraInstance?.parroquia?.id}">--}%
                    ${obraInstance?.parroquia?.encodeAsHTML()}
        %{--</g:link>--}%
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${obraInstance?.tipoObjetivo}">
        <div class="control-group">
            <div>
                <span id="tipoObjetivo-label" class="control-label label label-inverse">
                    Tipo Objetivo
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="tipoObjetivo-label">
        %{--<g:link controller="tipoObjetivo" action="show" id="${obraInstance?.tipoObjetivo?.id}">--}%
                    ${obraInstance?.tipoObjetivo?.encodeAsHTML()}
        %{--</g:link>--}%
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${obraInstance?.claseObra}">
        <div class="control-group">
            <div>
                <span id="claseObra-label" class="control-label label label-inverse">
                    Clase Obra
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="claseObra-label">
        %{--<g:link controller="claseObra" action="show" id="${obraInstance?.claseObra?.id}">--}%
                    ${obraInstance?.claseObra?.encodeAsHTML()}
        %{--</g:link>--}%
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${obraInstance?.estadoObra}">
        <div class="control-group">
            <div>
                <span id="estadoObra-label" class="control-label label label-inverse">
                    Estado Obra
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="estadoObra-label">
        %{--<g:link controller="estadoObra" action="show" id="${obraInstance?.estadoObra?.id}">--}%
                    ${obraInstance?.estadoObra?.encodeAsHTML()}
        %{--</g:link>--}%
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${obraInstance?.programacion}">
        <div class="control-group">
            <div>
                <span id="programacion-label" class="control-label label label-inverse">
                    Programacion
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="programacion-label">
        %{--<g:link controller="programacion" action="show" id="${obraInstance?.programacion?.id}">--}%
                    ${obraInstance?.programacion?.encodeAsHTML()}
        %{--</g:link>--}%
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${obraInstance?.departamento}">
        <div class="control-group">
            <div>
                <span id="departamento-label" class="control-label label label-inverse">
                    Coordinación
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="departamento-label">
        %{--<g:link controller="departamento" action="show" id="${obraInstance?.departamento?.id}">--}%
                    ${obraInstance?.departamento?.encodeAsHTML()}
        %{--</g:link>--}%
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${obraInstance?.descripcion}">
        <div class="control-group">
            <div>
                <span id="descripcion-label" class="control-label label label-inverse">
                    Descripcion
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="descripcion-label">
                    <g:fieldValue bean="${obraInstance}" field="descripcion"/>
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${obraInstance?.fechaInicio}">
        <div class="control-group">
            <div>
                <span id="fechaInicio-label" class="control-label label label-inverse">
                    Fecha Inicio
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="fechaInicio-label">
                    <g:formatDate date="${obraInstance?.fechaInicio}" />
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${obraInstance?.fechaFin}">
        <div class="control-group">
            <div>
                <span id="fechaFin-label" class="control-label label label-inverse">
                    Fecha Fin
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="fechaFin-label">
                    <g:formatDate date="${obraInstance?.fechaFin}" />
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${obraInstance?.distanciaPeso}">
        <div class="control-group">
            <div>
                <span id="distanciaPeso-label" class="control-label label label-inverse">
                    Distancia Peso
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="distanciaPeso-label">
                    <g:fieldValue bean="${obraInstance}" field="distanciaPeso"/>
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${obraInstance?.distanciaVolumen}">
        <div class="control-group">
            <div>
                <span id="distanciaVolumen-label" class="control-label label label-inverse">
                    Distancia Volumen
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="distanciaVolumen-label">
                    <g:fieldValue bean="${obraInstance}" field="distanciaVolumen"/>
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${obraInstance?.latitud}">
        <div class="control-group">
            <div>
                <span id="latitud-label" class="control-label label label-inverse">
                    Latitud
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="latitud-label">
                    <g:fieldValue bean="${obraInstance}" field="latitud"/>
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${obraInstance?.longitud}">
        <div class="control-group">
            <div>
                <span id="longitud-label" class="control-label label label-inverse">
                    Longitud
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="longitud-label">
                    <g:fieldValue bean="${obraInstance}" field="longitud"/>
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${obraInstance?.beneficiariosDirectos}">
        <div class="control-group">
            <div>
                <span id="beneficiariosDirectos-label" class="control-label label label-inverse">
                    Beneficiarios Directos
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="beneficiariosDirectos-label">
                    <g:fieldValue bean="${obraInstance}" field="beneficiariosDirectos"/>
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${obraInstance?.benificiariosIndirectos}">
        <div class="control-group">
            <div>
                <span id="benificiariosIndirectos-label" class="control-label label label-inverse">
                    Benificiarios Indirectos
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="benificiariosIndirectos-label">
                    <g:fieldValue bean="${obraInstance}" field="benificiariosIndirectos"/>
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${obraInstance?.beneficiariosPotenciales}">
        <div class="control-group">
            <div>
                <span id="beneficiariosPotenciales-label" class="control-label label label-inverse">
                    Beneficiarios Potenciales
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="beneficiariosPotenciales-label">
                    <g:fieldValue bean="${obraInstance}" field="beneficiariosPotenciales"/>
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${obraInstance?.estado}">
        <div class="control-group">
            <div>
                <span id="estado-label" class="control-label label label-inverse">
                    Estado
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="estado-label">
                    <g:fieldValue bean="${obraInstance}" field="estado"/>
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${obraInstance?.referencia}">
        <div class="control-group">
            <div>
                <span id="referencia-label" class="control-label label label-inverse">
                    Referencia
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="referencia-label">
                    <g:fieldValue bean="${obraInstance}" field="referencia"/>
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${obraInstance?.fechaCreacionObra}">
        <div class="control-group">
            <div>
                <span id="fechaCreacionObra-label" class="control-label label label-inverse">
                    Fecha Creacion Obra
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="fechaCreacionObra-label">
                    <g:formatDate date="${obraInstance?.fechaCreacionObra}" />
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${obraInstance?.oficioIngreso}">
        <div class="control-group">
            <div>
                <span id="oficioIngreso-label" class="control-label label label-inverse">
                    Oficio Ingreso
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="oficioIngreso-label">
                    <g:fieldValue bean="${obraInstance}" field="oficioIngreso"/>
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${obraInstance?.oficioSalida}">
        <div class="control-group">
            <div>
                <span id="oficioSalida-label" class="control-label label label-inverse">
                    Oficio Salida
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="oficioSalida-label">
                    <g:fieldValue bean="${obraInstance}" field="oficioSalida"/>
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${obraInstance?.plazo}">
        <div class="control-group">
            <div>
                <span id="plazo-label" class="control-label label label-inverse">
                    Plazo
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="plazo-label">
                    <g:fieldValue bean="${obraInstance}" field="plazo"/>
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${obraInstance?.observaciones}">
        <div class="control-group">
            <div>
                <span id="observaciones-label" class="control-label label label-inverse">
                    Observaciones
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="observaciones-label">
                    <g:fieldValue bean="${obraInstance}" field="observaciones"/>
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${obraInstance?.tipo}">
        <div class="control-group">
            <div>
                <span id="tipo-label" class="control-label label label-inverse">
                    Tipo
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="tipo-label">
                    <g:fieldValue bean="${obraInstance}" field="tipo"/>
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${obraInstance?.fechaPreciosRubros}">
        <div class="control-group">
            <div>
                <span id="fechaPreciosRubros-label" class="control-label label label-inverse">
                    Fecha Precios Rubros
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="fechaPreciosRubros-label">
                    <g:formatDate date="${obraInstance?.fechaPreciosRubros}" />
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${obraInstance?.itemChofer}">
        <div class="control-group">
            <div>
                <span id="itemChofer-label" class="control-label label label-inverse">
                    Item Chofer
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="itemChofer-label">
                    <g:fieldValue bean="${obraInstance}" field="itemChofer"/>
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${obraInstance?.itemPorVolquete}">
        <div class="control-group">
            <div>
                <span id="itemPorVolquete-label" class="control-label label label-inverse">
                    Item Por Volquete
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="itemPorVolquete-label">
                    <g:fieldValue bean="${obraInstance}" field="itemPorVolquete"/>
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${obraInstance?.memoCantidadObra}">
        <div class="control-group">
            <div>
                <span id="memoCantidadObra-label" class="control-label label label-inverse">
                    Memo Cantidad Obra
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="memoCantidadObra-label">
                    <g:fieldValue bean="${obraInstance}" field="memoCantidadObra"/>
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${obraInstance?.memoSalida}">
        <div class="control-group">
            <div>
                <span id="memoSalida-label" class="control-label label label-inverse">
                    Memo Salida
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="memoSalida-label">
                    <g:fieldValue bean="${obraInstance}" field="memoSalida"/>
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${obraInstance?.fechaOficioSalida}">
        <div class="control-group">
            <div>
                <span id="fechaOficioSalida-label" class="control-label label label-inverse">
                    Fecha Oficio Salida
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="fechaOficioSalida-label">
                    <g:formatDate date="${obraInstance?.fechaOficioSalida}" />
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${obraInstance?.factorReduccion}">
        <div class="control-group">
            <div>
                <span id="factorReduccion-label" class="control-label label label-inverse">
                    Factor Reduccion
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="factorReduccion-label">
                    <g:fieldValue bean="${obraInstance}" field="factorReduccion"/>
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${obraInstance?.factorVelocidad}">
        <div class="control-group">
            <div>
                <span id="factorVelocidad-label" class="control-label label label-inverse">
                    Factor Velocidad
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="factorVelocidad-label">
                    <g:fieldValue bean="${obraInstance}" field="factorVelocidad"/>
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${obraInstance?.capacidadVolquete}">
        <div class="control-group">
            <div>
                <span id="capacidadVolquete-label" class="control-label label label-inverse">
                    Capacidad Volquete
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="capacidadVolquete-label">
                    <g:fieldValue bean="${obraInstance}" field="capacidadVolquete"/>
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${obraInstance?.factorVolumen}">
        <div class="control-group">
            <div>
                <span id="factorVolumen-label" class="control-label label label-inverse">
                    Factor Volumen
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="factorVolumen-label">
                    <g:fieldValue bean="${obraInstance}" field="factorVolumen"/>
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${obraInstance?.factorPeso}">
        <div class="control-group">
            <div>
                <span id="factorPeso-label" class="control-label label label-inverse">
                    Factor Peso
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="factorPeso-label">
                    <g:fieldValue bean="${obraInstance}" field="factorPeso"/>
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${obraInstance?.factorReduccionTiempo}">
        <div class="control-group">
            <div>
                <span id="factorReduccionTiempo-label" class="control-label label label-inverse">
                    Factor Reduccion Tiempo
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="factorReduccionTiempo-label">
                    <g:fieldValue bean="${obraInstance}" field="factorReduccionTiempo"/>
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${obraInstance?.sitio}">
        <div class="control-group">
            <div>
                <span id="sitio-label" class="control-label label label-inverse">
                    Sitio
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="sitio-label">
                    <g:fieldValue bean="${obraInstance}" field="sitio"/>
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${obraInstance?.plazoEjecucionAnios}">
        <div class="control-group">
            <div>
                <span id="plazoEjecucionAnios-label" class="control-label label label-inverse">
                    Plazo Ejecucion Anios
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="plazoEjecucionAnios-label">
                    <g:fieldValue bean="${obraInstance}" field="plazoEjecucionAnios"/>
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${obraInstance?.plazoEjecucionMeses}">
        <div class="control-group">
            <div>
                <span id="plazoEjecucionMeses-label" class="control-label label label-inverse">
                    Plazo Ejecucion Meses
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="plazoEjecucionMeses-label">
                    <g:fieldValue bean="${obraInstance}" field="plazoEjecucionMeses"/>
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${obraInstance?.plazoEjecucionDias}">
        <div class="control-group">
            <div>
                <span id="plazoEjecucionDias-label" class="control-label label label-inverse">
                    Plazo Ejecucion Dias
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="plazoEjecucionDias-label">
                    <g:fieldValue bean="${obraInstance}" field="plazoEjecucionDias"/>
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${obraInstance?.formulaPolinomica}">
        <div class="control-group">
            <div>
                <span id="formulaPolinomica-label" class="control-label label label-inverse">
                    Formula Polinomica
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="formulaPolinomica-label">
                    <g:fieldValue bean="${obraInstance}" field="formulaPolinomica"/>
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${obraInstance?.indicador}">
        <div class="control-group">
            <div>
                <span id="indicador-label" class="control-label label label-inverse">
                    Indicador
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="indicador-label">
                    <g:fieldValue bean="${obraInstance}" field="indicador"/>
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${obraInstance?.indiceGastosGenerales}">
        <div class="control-group">
            <div>
                <span id="indiceGastosGenerales-label" class="control-label label label-inverse">
                    Indice Gastos Generales
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="indiceGastosGenerales-label">
                    <g:fieldValue bean="${obraInstance}" field="indiceGastosGenerales"/>
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${obraInstance?.impreso}">
        <div class="control-group">
            <div>
                <span id="impreso-label" class="control-label label label-inverse">
                    Impreso
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="impreso-label">
                    <g:fieldValue bean="${obraInstance}" field="impreso"/>
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${obraInstance?.indiceUtilidad}">
        <div class="control-group">
            <div>
                <span id="indiceUtilidad-label" class="control-label label label-inverse">
                    Indice Utilidad
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="indiceUtilidad-label">
                    <g:fieldValue bean="${obraInstance}" field="indiceUtilidad"/>
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${obraInstance?.contrato}">
        <div class="control-group">
            <div>
                <span id="contrato-label" class="control-label label label-inverse">
                    Contrato
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="contrato-label">
                    <g:fieldValue bean="${obraInstance}" field="contrato"/>
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${obraInstance?.totales}">
        <div class="control-group">
            <div>
                <span id="totales-label" class="control-label label label-inverse">
                    Totales
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="totales-label">
                    <g:fieldValue bean="${obraInstance}" field="totales"/>
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${obraInstance?.periodo}">
        <div class="control-group">
            <div>
                <span id="periodo-label" class="control-label label label-inverse">
                    Periodo
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="periodo-label">
                    <g:fieldValue bean="${obraInstance}" field="periodo"/>
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${obraInstance?.valor}">
        <div class="control-group">
            <div>
                <span id="valor-label" class="control-label label label-inverse">
                    Valor
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="valor-label">
                    <g:fieldValue bean="${obraInstance}" field="valor"/>
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${obraInstance?.partidaObra}">
        <div class="control-group">
            <div>
                <span id="partidaObra-label" class="control-label label label-inverse">
                    Partida Obra
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="partidaObra-label">
                    <g:fieldValue bean="${obraInstance}" field="partidaObra"/>
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${obraInstance?.memoCertificacionPartida}">
        <div class="control-group">
            <div>
                <span id="memoCertificacionPartida-label" class="control-label label label-inverse">
                    Memo Certificacion Partida
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="memoCertificacionPartida-label">
                    <g:fieldValue bean="${obraInstance}" field="memoCertificacionPartida"/>
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${obraInstance?.memoActualizacionPrefecto}">
        <div class="control-group">
            <div>
                <span id="memoActualizacionPrefecto-label" class="control-label label label-inverse">
                    Memo Actualizacion Prefecto
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="memoActualizacionPrefecto-label">
                    <g:fieldValue bean="${obraInstance}" field="memoActualizacionPrefecto"/>
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${obraInstance?.memoPartidaPresupuestaria}">
        <div class="control-group">
            <div>
                <span id="memoPartidaPresupuestaria-label" class="control-label label label-inverse">
                    Memo Partida Presupuestaria
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="memoPartidaPresupuestaria-label">
                    <g:fieldValue bean="${obraInstance}" field="memoPartidaPresupuestaria"/>
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${obraInstance?.porcentajeAnticipo}">
        <div class="control-group">
            <div>
                <span id="porcentajeAnticipo-label" class="control-label label label-inverse">
                    Porcentaje Anticipo
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="porcentajeAnticipo-label">
                    <g:fieldValue bean="${obraInstance}" field="porcentajeAnticipo"/>
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${obraInstance?.porcentajeReajuste}">
        <div class="control-group">
            <div>
                <span id="porcentajeReajuste-label" class="control-label label label-inverse">
                    Porcentaje Reajuste
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="porcentajeReajuste-label">
                    <g:fieldValue bean="${obraInstance}" field="porcentajeReajuste"/>
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${obraInstance?.indiceCostosIndirectosObra}">
        <div class="control-group">
            <div>
                <span id="indiceCostosIndirectosObra-label" class="control-label label label-inverse">
                    Indice Costos Indirectos Obra
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="indiceCostosIndirectosObra-label">
                    <g:fieldValue bean="${obraInstance}" field="indiceCostosIndirectosObra"/>
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${obraInstance?.indiceCostosIndirectosMantenimiento}">
        <div class="control-group">
            <div>
                <span id="indiceCostosIndirectosMantenimiento-label" class="control-label label label-inverse">
                    Indice Costos Indirectos Mantenimiento
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="indiceCostosIndirectosMantenimiento-label">
                    <g:fieldValue bean="${obraInstance}" field="indiceCostosIndirectosMantenimiento"/>
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${obraInstance?.administracion}">
        <div class="control-group">
            <div>
                <span id="administracion-label" class="control-label label label-inverse">
                    Administracion
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="administracion-label">
                    <g:fieldValue bean="${obraInstance}" field="administracion"/>
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${obraInstance?.indiceCostosIndirectosGarantias}">
        <div class="control-group">
            <div>
                <span id="indiceCostosIndirectosGarantias-label" class="control-label label label-inverse">
                    Indice Costos Indirectos Garantias
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="indiceCostosIndirectosGarantias-label">
                    <g:fieldValue bean="${obraInstance}" field="indiceCostosIndirectosGarantias"/>
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${obraInstance?.indiceCostosIndirectosCostosFinancieros}">
        <div class="control-group">
            <div>
                <span id="indiceCostosIndirectosCostosFinancieros-label" class="control-label label label-inverse">
                    Indice Costos Indirectos Costos Financieros
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="indiceCostosIndirectosCostosFinancieros-label">
                    <g:fieldValue bean="${obraInstance}" field="indiceCostosIndirectosCostosFinancieros"/>
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${obraInstance?.indiceCostosIndirectosVehiculos}">
        <div class="control-group">
            <div>
                <span id="indiceCostosIndirectosVehiculos-label" class="control-label label label-inverse">
                    Indice Costos Indirectos Vehiculos
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="indiceCostosIndirectosVehiculos-label">
                    <g:fieldValue bean="${obraInstance}" field="indiceCostosIndirectosVehiculos"/>
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${obraInstance?.indiceCostosIndirectosPromocion}">
        <div class="control-group">
            <div>
                <span id="indiceCostosIndirectosPromocion-label" class="control-label label label-inverse">
                    Indice Costos Indirectos Promocion
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="indiceCostosIndirectosPromocion-label">
                    <g:fieldValue bean="${obraInstance}" field="indiceCostosIndirectosPromocion"/>
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${obraInstance?.indiceCostosIndirectosTimbresProvinciales}">
        <div class="control-group">
            <div>
                <span id="indiceCostosIndirectosTimbresProvinciales-label" class="control-label label label-inverse">
                    Indice Costos Indirectos Timbres Provinciales
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="indiceCostosIndirectosTimbresProvinciales-label">
                    <g:fieldValue bean="${obraInstance}" field="indiceCostosIndirectosTimbresProvinciales"/>
                </span>
        
            </div>
        </div>
    </g:if>
    
    <g:if test="${obraInstance?.inspector}">
        <div class="control-group">
            <div>
                <span id="inspector-label" class="control-label label label-inverse">
                    Inspector
                </span>
            </div>
            <div class="controls">
        
                <span aria-labelledby="inspector-label">
        %{--<g:link controller="persona" action="show" id="${obraInstance?.inspector?.id}">--}%
                    ${obraInstance?.inspector?.encodeAsHTML()}
        %{--</g:link>--}%
                </span>
        
            </div>
        </div>
    </g:if>
    
    </form>
</div>
