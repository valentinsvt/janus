<%@ page import="janus.ejecucion.Planilla" %>

<div id="show-planilla" class="span5" role="main">

    <form class="form-horizontal">

        <g:if test="${planillaInstance?.contrato}">
            <div class="control-group">
                <div>
                    <span id="contrato-label" class="control-label label label-inverse">
                        Contrato
                    </span>
                </div>

                <div class="controls">

                    <span aria-labelledby="contrato-label">
                        %{--<g:link controller="contrato" action="show" id="${planillaInstance?.contrato?.id}">--}%
                        ${planillaInstance?.contrato?.encodeAsHTML()}
                        %{--</g:link>--}%
                    </span>

                </div>
            </div>
        </g:if>

        <g:if test="${planillaInstance?.tipoPlanilla}">
            <div class="control-group">
                <div>
                    <span id="tipoPlanilla-label" class="control-label label label-inverse">
                        Tipo Planilla
                    </span>
                </div>

                <div class="controls">

                    <span aria-labelledby="tipoPlanilla-label">
                        %{--<g:link controller="tipoPlanilla" action="show" id="${planillaInstance?.tipoPlanilla?.id}">--}%
                        ${planillaInstance?.tipoPlanilla?.encodeAsHTML()}
                        %{--</g:link>--}%
                    </span>

                </div>
            </div>
        </g:if>

        <g:if test="${planillaInstance?.estadoPlanilla}">
            <div class="control-group">
                <div>
                    <span id="estadoPlanilla-label" class="control-label label label-inverse">
                        Estado Planilla
                    </span>
                </div>

                <div class="controls">

                    <span aria-labelledby="estadoPlanilla-label">
                        %{--<g:link controller="estadoPlanilla" action="show" id="${planillaInstance?.estadoPlanilla?.id}">--}%
                        ${planillaInstance?.estadoPlanilla?.encodeAsHTML()}
                        %{--</g:link>--}%
                    </span>

                </div>
            </div>
        </g:if>

        <g:if test="${planillaInstance?.periodoIndices}">
            <div class="control-group">
                <div>
                    <span id="periodoIndices-label" class="control-label label label-inverse">
                        Periodo Indices
                    </span>
                </div>

                <div class="controls">

                    <span aria-labelledby="periodoIndices-label">
                        %{--<g:link controller="periodoInec" action="show" id="${planillaInstance?.periodoIndices?.id}">--}%
                        ${planillaInstance?.periodoIndices?.encodeAsHTML()}
                        %{--</g:link>--}%
                    </span>

                </div>
            </div>
        </g:if>

        <g:if test="${planillaInstance?.numero}">
            <div class="control-group">
                <div>
                    <span id="numero-label" class="control-label label label-inverse">
                        Numero
                    </span>
                </div>

                <div class="controls">

                    <span aria-labelledby="numero-label">
                        <g:fieldValue bean="${planillaInstance}" field="numero"/>
                    </span>

                </div>
            </div>
        </g:if>

        <g:if test="${planillaInstance?.numeroFactura}">
            <div class="control-group">
                <div>
                    <span id="numeroFactura-label" class="control-label label label-inverse">
                        Numero Factura
                    </span>
                </div>

                <div class="controls">

                    <span aria-labelledby="numeroFactura-label">
                        <g:fieldValue bean="${planillaInstance}" field="numeroFactura"/>
                    </span>

                </div>
            </div>
        </g:if>

        <g:if test="${planillaInstance?.fechaPresentacion}">
            <div class="control-group">
                <div>
                    <span id="fechaPresentacion-label" class="control-label label label-inverse">
                        Fecha Presentacion
                    </span>
                </div>

                <div class="controls">

                    <span aria-labelledby="fechaPresentacion-label">
                        <g:formatDate date="${planillaInstance?.fechaPresentacion}"/>
                    </span>

                </div>
            </div>
        </g:if>

        <g:if test="${planillaInstance?.fechaIngreso}">
            <div class="control-group">
                <div>
                    <span id="fechaIngreso-label" class="control-label label label-inverse">
                        Fecha Ingreso
                    </span>
                </div>

                <div class="controls">

                    <span aria-labelledby="fechaIngreso-label">
                        <g:formatDate date="${planillaInstance?.fechaIngreso}"/>
                    </span>

                </div>
            </div>
        </g:if>

        <g:if test="${planillaInstance?.fechaPago}">
            <div class="control-group">
                <div>
                    <span id="fechaPago-label" class="control-label label label-inverse">
                        Fecha Pago
                    </span>
                </div>

                <div class="controls">

                    <span aria-labelledby="fechaPago-label">
                        <g:formatDate date="${planillaInstance?.fechaPago}"/>
                    </span>

                </div>
            </div>
        </g:if>

        <g:if test="${planillaInstance?.descripcion}">
            <div class="control-group">
                <div>
                    <span id="descripcion-label" class="control-label label label-inverse">
                        Descripcion
                    </span>
                </div>

                <div class="controls">

                    <span aria-labelledby="descripcion-label">
                        <g:fieldValue bean="${planillaInstance}" field="descripcion"/>
                    </span>

                </div>
            </div>
        </g:if>

        <g:if test="${planillaInstance?.valor}">
            <div class="control-group">
                <div>
                    <span id="valor-label" class="control-label label label-inverse">
                        Valor
                    </span>
                </div>

                <div class="controls">

                    <span aria-labelledby="valor-label">
                        <g:fieldValue bean="${planillaInstance}" field="valor"/>
                    </span>

                </div>
            </div>
        </g:if>

        <g:if test="${planillaInstance?.descuentos}">
            <div class="control-group">
                <div>
                    <span id="descuentos-label" class="control-label label label-inverse">
                        Descuentos
                    </span>
                </div>

                <div class="controls">

                    <span aria-labelledby="descuentos-label">
                        <g:fieldValue bean="${planillaInstance}" field="descuentos"/>
                    </span>

                </div>
            </div>
        </g:if>

        <g:if test="${planillaInstance?.reajustada}">
            <div class="control-group">
                <div>
                    <span id="reajustada-label" class="control-label label label-inverse">
                        Reajustada
                    </span>
                </div>

                <div class="controls">

                    <span aria-labelledby="reajustada-label">
                        <g:fieldValue bean="${planillaInstance}" field="reajustada"/>
                    </span>

                </div>
            </div>
        </g:if>

        <g:if test="${planillaInstance?.reajuste}">
            <div class="control-group">
                <div>
                    <span id="reajuste-label" class="control-label label label-inverse">
                        Reajuste
                    </span>
                </div>

                <div class="controls">

                    <span aria-labelledby="reajuste-label">
                        <g:fieldValue bean="${planillaInstance}" field="reajuste"/>
                    </span>

                </div>
            </div>
        </g:if>

        <g:if test="${planillaInstance?.fechaReajuste}">
            <div class="control-group">
                <div>
                    <span id="fechaReajuste-label" class="control-label label label-inverse">
                        Fecha Reajuste
                    </span>
                </div>

                <div class="controls">

                    <span aria-labelledby="fechaReajuste-label">
                        <g:formatDate date="${planillaInstance?.fechaReajuste}"/>
                    </span>

                </div>
            </div>
        </g:if>

        <g:if test="${planillaInstance?.diferenciaReajuste}">
            <div class="control-group">
                <div>
                    <span id="diferenciaReajuste-label" class="control-label label label-inverse">
                        Diferencia Reajuste
                    </span>
                </div>

                <div class="controls">

                    <span aria-labelledby="diferenciaReajuste-label">
                        <g:fieldValue bean="${planillaInstance}" field="diferenciaReajuste"/>
                    </span>

                </div>
            </div>
        </g:if>

        <g:if test="${planillaInstance?.observaciones}">
            <div class="control-group">
                <div>
                    <span id="observaciones-label" class="control-label label label-inverse">
                        Observaciones
                    </span>
                </div>

                <div class="controls">

                    <span aria-labelledby="observaciones-label">
                        <g:fieldValue bean="${planillaInstance}" field="observaciones"/>
                    </span>

                </div>
            </div>
        </g:if>

        <g:if test="${planillaInstance?.fechaInicio}">
            <div class="control-group">
                <div>
                    <span id="fechaInicio-label" class="control-label label label-inverse">
                        Fecha Inicio
                    </span>
                </div>

                <div class="controls">

                    <span aria-labelledby="fechaInicio-label">
                        <g:formatDate date="${planillaInstance?.fechaInicio}"/>
                    </span>

                </div>
            </div>
        </g:if>

        <g:if test="${planillaInstance?.fechaFin}">
            <div class="control-group">
                <div>
                    <span id="fechaFin-label" class="control-label label label-inverse">
                        Fecha Fin
                    </span>
                </div>

                <div class="controls">

                    <span aria-labelledby="fechaFin-label">
                        <g:formatDate date="${planillaInstance?.fechaFin}"/>
                    </span>

                </div>
            </div>
        </g:if>

        <g:if test="${planillaInstance?.oficioSalida}">
            <div class="control-group">
                <div>
                    <span id="oficioSalida-label" class="control-label label label-inverse">
                        Oficio Salida
                    </span>
                </div>

                <div class="controls">

                    <span aria-labelledby="oficioSalida-label">
                        <g:fieldValue bean="${planillaInstance}" field="oficioSalida"/>
                    </span>

                </div>
            </div>
        </g:if>

        <g:if test="${planillaInstance?.fechaOficioSalida}">
            <div class="control-group">
                <div>
                    <span id="fechaOficioSalida-label" class="control-label label label-inverse">
                        Fecha Oficio Salida
                    </span>
                </div>

                <div class="controls">

                    <span aria-labelledby="fechaOficioSalida-label">
                        <g:formatDate date="${planillaInstance?.fechaOficioSalida}"/>
                    </span>

                </div>
            </div>
        </g:if>

        <g:if test="${planillaInstance?.oficioPago}">
            <div class="control-group">
                <div>
                    <span id="oficioPago-label" class="control-label label label-inverse">
                        Oficio Pago
                    </span>
                </div>

                <div class="controls">

                    <span aria-labelledby="oficioPago-label">
                        <g:fieldValue bean="${planillaInstance}" field="oficioPago"/>
                    </span>

                </div>
            </div>
        </g:if>

        <g:if test="${planillaInstance?.fechaOficioPago}">
            <div class="control-group">
                <div>
                    <span id="fechaOficioPago-label" class="control-label label label-inverse">
                        Fecha Oficio Pago
                    </span>
                </div>

                <div class="controls">

                    <span aria-labelledby="fechaOficioPago-label">
                        <g:formatDate date="${planillaInstance?.fechaOficioPago}"/>
                    </span>

                </div>
            </div>
        </g:if>

        <g:if test="${planillaInstance?.aprobado}">
            <div class="control-group">
                <div>
                    <span id="aprobado-label" class="control-label label label-inverse">
                        Aprobado
                    </span>
                </div>

                <div class="controls">

                    <span aria-labelledby="aprobado-label">
                        <g:fieldValue bean="${planillaInstance}" field="aprobado"/>
                    </span>

                </div>
            </div>
        </g:if>

    </form>
</div>
