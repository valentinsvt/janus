<%@ page import="janus.Item" %>

<form class="form-horizontal">

    <div class="tituloTree">${itemInstance.nombre}</div>
%{--<g:if test="${itemInstance?.nombre}">--}%
%{--<div class="control-group">--}%
%{--<div>--}%
%{--<span id="nombre-label" class="control-label label label-inverse">--}%
%{--Nombre--}%
%{--</span>--}%
%{--</div>--}%

%{--<div class="controls">--}%

%{--<span aria-labelledby="nombre-label">--}%
%{--<g:fieldValue bean="${itemInstance}" field="nombre"/>--}%
%{--</span>--}%

%{--</div>--}%
%{--</div>--}%
%{--</g:if>--}%

    <g:if test="${itemInstance?.codigo}">
        <div class="control-group">
            <div>
                <span id="codigo-label" class="control-label label label-inverse">
                    Código
                </span>
            </div>

            <div class="controls">

                <span aria-labelledby="codigo-label">
                    <g:fieldValue bean="${itemInstance}" field="codigo"/>
                </span>

            </div>
        </div>
    </g:if>

    <g:if test="${itemInstance?.unidad}">
        <div class="control-group">
            <div>
                <span id="unidad-label" class="control-label label label-inverse">
                    Unidad
                </span>
            </div>

            <div class="controls">

                <span aria-labelledby="unidad-label">
                    %{--<g:link controller="unidad" action="show" id="${itemInstance?.unidad?.id}">--}%
                    ${itemInstance?.unidad?.codigo}
                    %{--</g:link>--}%
                </span>

            </div>
        </div>
    </g:if>

    <g:if test="${itemInstance.departamento.subgrupo.grupo.id.toString() == '1'}">
        <div class="control-group">
            <div>
                <span id="peso-label" class="control-label label label-inverse">
                    %{--Peso--}%
                    %{--${(itemInstance?.transporte == 'P' || itemInstance?.transporte == 'P1') ? 'Peso' : 'Volumen'}--}%
                    ${(itemInstance?.tipoLista?.codigo[0] == 'P') ? 'Peso' : 'Volumen'}
                </span>
            </div>

            <div class="controls">

                <span aria-labelledby="peso-label">
                    <g:formatNumber number="${itemInstance.peso}" maxFractionDigits="6" minFractionDigits="6" format='##,######0' locale='ec'/>
                    %{--${(itemInstance?.transporte == 'P' || itemInstance?.transporte == 'P1') ? 'Ton' : 'M<sup>3</sup>'}--}%
                    ${itemInstance?.tipoLista?.unidad}
                </span>

            </div>
        </div>
    </g:if>

    <g:if test="${itemInstance?.departamento}">
        <div class="control-group">
            <div>
                <span id="departamento-label" class="control-label label label-inverse">
                    Subgrupo
                </span>
            </div>

            <div class="controls">

                <span aria-labelledby="departamento-label">
                    %{--<g:link controller="departamentoItem" action="show" id="${itemInstance?.departamento?.id}">--}%
                    ${itemInstance?.departamento?.descripcion}
                    %{--</g:link>--}%
                </span>

            </div>
        </div>
    </g:if>

    <g:if test="${itemInstance?.estado}">
        <div class="control-group">
            <div>
                <span id="estado-label" class="control-label label label-inverse">
                    Estado
                </span>
            </div>

            <div class="controls">

                <span aria-labelledby="estado-label">
                    ${itemInstance.estado == 'A' ? 'ACTIVO' : itemInstance.estado == 'B' ? 'DADO DE BAJA' : ''}
                </span>

            </div>
        </div>
    </g:if>

    <g:if test="${itemInstance?.fecha}">
        <div class="control-group">
            <div>
                <span id="fecha-label" class="control-label label label-inverse">
                    Fecha creación
                </span>
            </div>

            <div class="controls">

                <span aria-labelledby="fecha-label">
                    <g:formatDate date="${itemInstance?.fecha}" format="dd-MM-yyyy"/>
                </span>

            </div>
        </div>
    </g:if>

    <g:if test="${itemInstance?.fechaModificacion}">
        <div class="control-group">
            <div>
                <span id="fechaModificacion-label" class="control-label label label-inverse">
                    Fecha última modificación
                </span>
            </div>

            <div class="controls">

                <span aria-labelledby="fecha-label">
                    <g:formatDate date="${itemInstance?.fechaModificacion}" format="dd-MM-yyyy"/>
                </span>

            </div>
        </div>
    </g:if>

%{--
    <g:if test="${itemInstance?.fechaModificacion}">
        <div class="control-group">
            <div>
                <span id="fechaModificacion-label" class="control-label label label-inverse">
                    Tipo de Bien
                </span>
            </div>

            <div class="controls">

                <span aria-labelledby="fecha-label">
                    ${itemInstance?.tipoDeBien} (${itemInstance?.tipoDeBien?.codigo})
                </span>

            </div>
        </div>
    </g:if>
--}%

    <g:if test="${itemInstance?.fechaModificacion}">
        <div class="control-group">
            <div>
                <span id="fechaModificacion-label" class="control-label label label-inverse">
                    Código CPC
                </span>
            </div>

            <div class="controls">

                <span aria-labelledby="fecha-label">
                    ${itemInstance?.codigoComprasPublicas?.numero}
                </span>

            </div>
        </div>
    </g:if>

    <g:if test="${itemInstance?.transportePeso}">
        <div class="control-group">
            <div>
                <span id="transportePeso-label" class="control-label label label-inverse">
                    Transporte Peso
                </span>
            </div>

            <div class="controls">

                <span aria-labelledby="transportePeso-label">
                    <g:fieldValue bean="${itemInstance}" field="transportePeso"/>
                </span>

            </div>
        </div>
    </g:if>

    <g:if test="${itemInstance?.transporteVolumen}">
        <div class="control-group">
            <div>
                <span id="transporteVolumen-label" class="control-label label label-inverse">
                    Transporte Volumen
                </span>
            </div>

            <div class="controls">

                <span aria-labelledby="transporteVolumen-label">
                    <g:fieldValue bean="${itemInstance}" field="transporteVolumen"/>
                </span>

            </div>
        </div>
    </g:if>

    <g:if test="${itemInstance?.padre}">
        <div class="control-group">
            <div>
                <span id="padre-label" class="control-label label label-inverse">
                    Padre
                </span>
            </div>

            <div class="controls">

                <span aria-labelledby="padre-label">
                    <g:fieldValue bean="${itemInstance}" field="padre"/>
                </span>

            </div>
        </div>
    </g:if>

    <g:if test="${itemInstance?.inec}">
        <div class="control-group">
            <div>
                <span id="inec-label" class="control-label label label-inverse">
                    Inec
                </span>
            </div>

            <div class="controls">

                <span aria-labelledby="inec-label">
                    <g:fieldValue bean="${itemInstance}" field="inec"/>
                </span>

            </div>
        </div>
    </g:if>

    <g:if test="${itemInstance?.rendimiento}">
        <div class="control-group">
            <div>
                <span id="rendimiento-label" class="control-label label label-inverse">
                    Rendimiento
                </span>
            </div>

            <div class="controls">

                <span aria-labelledby="rendimiento-label">
                    <g:fieldValue bean="${itemInstance}" field="rendimiento"/>
                </span>

            </div>
        </div>
    </g:if>

    <g:if test="${itemInstance?.tipo}">
        <div class="control-group">
            <div>
                <span id="tipo-label" class="control-label label label-inverse">
                    Tipo
                </span>
            </div>

            <div class="controls">

                <span aria-labelledby="tipo-label">
                    <g:fieldValue bean="${itemInstance}" field="tipo"/>
                </span>

            </div>
        </div>
    </g:if>

    %{--<g:if test="${itemInstance?.campo}">--}%
        %{--<div class="control-group">--}%
            %{--<div>--}%
                %{--<span id="campo-label" class="control-label label label-inverse">--}%
                    %{--Nombre corto--}%
                %{--</span>--}%
            %{--</div>--}%

            %{--<div class="controls">--}%

                %{--<span aria-labelledby="campo-label">--}%
                    %{--<g:fieldValue bean="${itemInstance}" field="campo"/>--}%
                %{--</span>--}%

            %{--</div>--}%
        %{--</div>--}%
    %{--</g:if>--}%

    <g:if test="${itemInstance?.registro}">
        <div class="control-group">
            <div>
                <span id="registro-label" class="control-label label label-inverse">
                    Registro
                </span>
            </div>

            <div class="controls">

                <span aria-labelledby="registro-label">
                    <g:fieldValue bean="${itemInstance}" field="registro"/>
                </span>

            </div>
        </div>
    </g:if>

    <g:if test="${itemInstance?.transporte}">
        <div class="control-group">
            <div>
                <span id="transporte-label" class="control-label label label-inverse">
                    Transporte
                </span>
            </div>

            <div class="controls">

                <span aria-labelledby="transporte-label">
                    ${(itemInstance.transporte == 'P') ? 'PESO' : itemInstance.transporte == 'V' ? 'VOLUMEN' : ''}
                    <g:if test="${itemInstance.transporte == 'P'}">
                        Peso (capital de cantón)
                    </g:if>
                    <g:elseif test="${itemInstance.transporte == 'P1'}">
                        Peso (especial)
                    </g:elseif>
                    <g:elseif test="${itemInstance.transporte == 'V'}">
                        Volumen (materiales pétreos para hormigones)
                    </g:elseif>
                    <g:elseif test="${itemInstance.transporte == 'V1'}">
                        Volumen (materiales pétreos para mejoramiento)
                    </g:elseif>
                    <g:elseif test="${itemInstance.transporte == 'V2'}">
                        Volumen (materiales pétreos para carpeta asfáltica)
                    </g:elseif>

                </span>

            </div>
        </div>
    </g:if>

    <g:if test="${itemInstance?.combustible}">
        <div class="control-group">
            <div>
                <span id="combustible-label" class="control-label label label-inverse">
                    Combustible
                </span>
            </div>

            <div class="controls">

                <span aria-labelledby="combustible-label">
                    ${itemInstance.combustible == 'S' ? 'SI' : itemInstance.combustible == 'N' ? 'NO' : ''}
                </span>

            </div>
        </div>
    </g:if>

    <g:if test="${itemInstance?.observaciones}">
        <div class="control-group">
            <div>
                <span id="observaciones-label" class="control-label label label-inverse">
                    Observaciones
                </span>
            </div>

            <div class="controls">

                <span aria-labelledby="observaciones-label">
                    <g:fieldValue bean="${itemInstance}" field="observaciones"/>
                </span>

            </div>
        </div>
    </g:if>

</form>
