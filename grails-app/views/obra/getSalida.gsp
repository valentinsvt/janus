<%--
  Created by IntelliJ IDEA.
  User: gato
  Date: 24/04/14
  Time: 03:46 PM
--%>

    <div class="span2 formato" style="width: 230px;">Destino: Dirección
    <g:select style="width: 230px;" name="direccionDestino.id" from="${dire}" optionKey="id" optionValue="nombre" value="${obra?.direccionDestino?.id}" title="Destino de documentos" noSelection="['null': 'Seleccione ...']"/>
    </div>

    <div class="span2 formato" style="width: 230px;">Destino: Coordinación
    <g:select style="width: 230px;" name="departamentoDestino.id" from="${depar}" optionKey="id" optionValue="descripcion" value="${obra?.departamentoDestino?.id}" title="Destino de documentos" noSelection="['null': 'Seleccione ...']"/>
    </div>

    <div class="span1 formato" style="width: 120px;margin-left: 30px;">Oficio
    <g:textField name="oficioSalida" class="span2 allCaps" value="${obra?.oficioSalida}" maxlength="20" title="Número Oficio de Salida" style="width: 120px;"/>
    </div>

    <div class="span1 formato" style="width: 120px; margin-left: 20px;">Memorando
    <g:textField name="memoSalida" class="span2 allCaps" value="${obra?.memoSalida}" maxlength="20" title="Memorandum de salida" style="width: 120px;"/>
    </div>

    <g:if test="${obra?.id && obra?.tipo != 'D'}">
        <div class="span1 formato" style="width: 120px; margin-left: 20px;">Fórmula P.
            <g:if test="${obra?.formulaPolinomica && obra?.formulaPolinomica != ''}">
                <div style="font-weight: normal;">${obra?.formulaPolinomica}</div>
            </g:if>
            <g:else>
                <a href="#" id="btnGenerarFP" class="btn btn-info" style="font-weight: normal;">
                    Generar
                </a>
            </g:else>
        </div>
    </g:if>

    <div class="span1 formato" style="width: 100px; margin-left: 40px;">Fecha
    <elm:datepicker name="fechaOficioSalida" class="span1 datepicker input-small"
                    value="${obra?.fechaOficioSalida}" style="width: 120px; margin-left: -20px;"/>
    </div>
