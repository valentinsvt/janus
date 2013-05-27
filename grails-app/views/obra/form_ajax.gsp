
<%@ page import="janus.TipoObra; janus.Obra" %>

<div id="create-obraInstance" class="span" role="main">
    <g:form class="form-horizontal" name="frmSave-obraInstance" action="save">
        <g:hiddenField name="id" value="${obraInstance?.id}"/>
                
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Código
                </span>
            </div>

            <div class="controls">
                <g:textField name="codigo" maxlength="10"  style="width: 100px" class=" required" value="${obraInstance?.codigo}"/>
                <span class="mandatory">*</span>
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
                
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Nombre
                </span>
            </div>

            <div class="controls">
                <g:textField name="nombre" maxlength="127" style="width: 300px" class="" value="${obraInstance?.nombre}"/>
                
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
                
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Responsable de la Obra
                </span>
            </div>

            <div class="controls">
                <g:select id="responsableObra" name="responsableObra.id" from="${janus.Persona.list()}" optionKey="id" class="many-to-one " value="${obraInstance?.responsableObra?.id}" noSelection="['null': '']"/>
                
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
                
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Revisor
                </span>
            </div>

            <div class="controls">
                <g:select id="revisor" name="revisor.id" from="${janus.Persona.list()}" optionKey="id" class="many-to-one " value="${obraInstance?.revisor?.id}" noSelection="['null': '']"/>
                
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
                
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Lugar
                </span>
            </div>

            <div class="controls">
                <g:select id="lugar" name="lugar.id" from="${janus.Lugar.list()}" optionKey="id" class="many-to-one " value="${obraInstance?.lugar?.id}" noSelection="['null': '']"/>
                
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
                
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Comunidad
                </span>
            </div>

            <div class="controls">
                <g:select id="comunidad" name="comunidad.id" from="${janus.Comunidad.list()}" optionKey="id" class="many-to-one " value="${obraInstance?.comunidad?.id}" noSelection="['null': '']"/>
                
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
                
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Parroquia
                </span>
            </div>

            <div class="controls">
                <g:select id="parroquia" name="parroquia.id" from="${janus.Parroquia.list()}" optionKey="id" class="many-to-one " value="${obraInstance?.parroquia?.id}" noSelection="['null': '']"/>
                
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
                
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Tipo Objetivo
                </span>
            </div>

            <div class="controls">
                <g:select id="tipoObjetivo" name="tipoObjetivo.id" from="${TipoObra.list()}" optionKey="id" class="many-to-one " value="${obraInstance?.tipoObjetivo?.id}" noSelection="['null': '']"/>
                
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
                
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Clase Obra
                </span>
            </div>

            <div class="controls">
                <g:select id="claseObra" name="claseObra.id" from="${janus.ClaseObra.list()}" optionKey="id" class="many-to-one " value="${obraInstance?.claseObra?.id}" noSelection="['null': '']"/>
                
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
                
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Estado Obra
                </span>
            </div>

            <div class="controls">
                <g:select id="estadoObra" name="estadoObra.id" from="${janus.EstadoObra.list()}" optionKey="id" class="many-to-one " value="${obraInstance?.estadoObra?.id}" noSelection="['null': '']"/>
                
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
                
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Programación
                </span>
            </div>

            <div class="controls">
                <g:select id="programacion" name="programacion.id" from="${janus.Programacion.list()}" optionKey="id" class="many-to-one " value="${obraInstance?.programacion?.id}" noSelection="['null': '']"/>
                
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
                
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Coordinación
                </span>
            </div>

            <div class="controls">
                <g:select id="departamento" name="departamento.id" from="${janus.Departamento.list()}" optionKey="id" class="many-to-one " value="${obraInstance?.departamento?.id}" noSelection="['null': '']"/>
                
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
                
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Descripción
                </span>
            </div>

            <div class="controls">
                <g:textArea name="descripcion" cols="40" rows="5" maxlength="511" class="" value="${obraInstance?.descripcion}"/>
                
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
                
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Fecha Inicio
                </span>
            </div>

            <div class="controls">
                <g:textField name="fechaInicio" class="datepicker" style="width: 90px" value="${obraInstance?.fechaInicio}"/>
<script type="text/javascript">
$("#fechaInicio").datepicker({
changeMonth: true,
changeYear: true,
showOn: "both",
buttonImage: "${resource(dir:'images', file:'calendar.png')}",
buttonImageOnly: true
});
</script>
                
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
                
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Fecha Fin
                </span>
            </div>

            <div class="controls">
                <g:textField name="fechaFin" class="datepicker" style="width: 90px" value="${obraInstance?.fechaFin}"/>
<script type="text/javascript">
$("#fechaFin").datepicker({
changeMonth: true,
changeYear: true,
showOn: "both",
buttonImage: "${resource(dir:'images', file:'calendar.png')}",
buttonImageOnly: true
});
</script>
                
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
                
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Distancia Peso
                </span>
            </div>

            <div class="controls">
                <g:field type="number" name="distanciaPeso" class=" required" value="${fieldValue(bean: obraInstance, field: 'distanciaPeso')}"/>
                <span class="mandatory">*</span>
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
                
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Distancia Volumen
                </span>
            </div>

            <div class="controls">
                <g:field type="number" name="distanciaVolumen" class=" required" value="${fieldValue(bean: obraInstance, field: 'distanciaVolumen')}"/>
                <span class="mandatory">*</span>
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
                
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Latitud
                </span>
            </div>

            <div class="controls">
                <g:textField name="latitud" maxlength="12" class="" value="${obraInstance?.latitud}"/>
                
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
                
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Longitud
                </span>
            </div>

            <div class="controls">
                <g:textField name="longitud" maxlength="12" class="" value="${obraInstance?.longitud}"/>
                
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
                
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Beneficiarios Directos
                </span>
            </div>

            <div class="controls">
                <g:field type="number" name="beneficiariosDirectos" class=" required" value="${fieldValue(bean: obraInstance, field: 'beneficiariosDirectos')}"/>
                <span class="mandatory">*</span>
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
                
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Benificiarios Indirectos
                </span>
            </div>

            <div class="controls">
                <g:field type="number" name="benificiariosIndirectos" class=" required" value="${fieldValue(bean: obraInstance, field: 'benificiariosIndirectos')}"/>
                <span class="mandatory">*</span>
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
                
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Beneficiarios Potenciales
                </span>
            </div>

            <div class="controls">
                <g:field type="number" name="beneficiariosPotenciales" class=" required" value="${fieldValue(bean: obraInstance, field: 'beneficiariosPotenciales')}"/>
                <span class="mandatory">*</span>
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
                
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Estado
                </span>
            </div>

            <div class="controls">
                <g:textField name="estado" maxlength="1" style="width: 20px" class="" value="${obraInstance?.estado}"/>
                
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
                
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Referencia
                </span>
            </div>

            <div class="controls">
                <g:textArea cols="5" rows="3" style="resize: none" name="referencia" maxlength="127" class="" value="${obraInstance?.referencia}"/>
                
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
                
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Fecha de Creación de la Obra
                </span>
            </div>

            <div class="controls">
                <g:textField name="fechaCreacionObra" class="datepicker" style="width: 90px" value="${obraInstance?.fechaCreacionObra}"/>
<script type="text/javascript">
$("#fechaCreacionObra").datepicker({
changeMonth: true,
changeYear: true,
showOn: "both",
buttonImage: "${resource(dir:'images', file:'calendar.png')}",
buttonImageOnly: true
});
</script>
                
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
                
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Oficio Ingreso
                </span>
            </div>

            <div class="controls">
                <g:textField name="oficioIngreso" maxlength="15" style=" width: 140px" class="" value="${obraInstance?.oficioIngreso}"/>
                
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
                
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Oficio Salida
                </span>
            </div>

            <div class="controls">
                <g:textField name="oficioSalida" maxlength="15" style="width: 140px" class="" value="${obraInstance?.oficioSalida}"/>
                
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
                
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Plazo
                </span>
            </div>

            <div class="controls">
                <g:field type="number" name="plazo" class=" required" value="${fieldValue(bean: obraInstance, field: 'plazo')}"/>
                <span class="mandatory">*</span>
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
                
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Observaciones
                </span>
            </div>

            <div class="controls">
                <g:textArea cols="5" rows="5" style="resize: none" name="observaciones" maxlength="127" class="" value="${obraInstance?.observaciones}"/>
                
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
                
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Tipo
                </span>
            </div>

            <div class="controls">
                <g:textField name="tipo" maxlength="1" style="width: 20px" class="" value="${obraInstance?.tipo}"/>
                
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
                
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Fecha Precios Rubros
                </span>
            </div>

            <div class="controls">
                <g:textField name="fechaPreciosRubros" class="datepicker" style="width: 90px" value="${obraInstance?.fechaPreciosRubros}"/>
<script type="text/javascript">
$("#fechaPreciosRubros").datepicker({
changeMonth: true,
changeYear: true,
showOn: "both",
buttonImage: "${resource(dir:'images', file:'calendar.png')}",
buttonImageOnly: true
});
</script>
                
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
                
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Item Chofer
                </span>
            </div>

            <div class="controls">
                <g:textField name="itemChofer" maxlength="31" style="width: 300px" class="" value="${obraInstance?.itemChofer}"/>
                
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
                
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Item Por Volquete
                </span>
            </div>

            <div class="controls">
                <g:textField name="itemPorVolquete" maxlength="31" style="width: 300px" class="" value="${obraInstance?.itemPorVolquete}"/>
                
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
                
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Memo Cantidad Obra
                </span>
            </div>

            <div class="controls">
                <g:textField name="memoCantidadObra" maxlength="15" style="width: 140px" class="" value="${obraInstance?.memoCantidadObra}"/>
                
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
                
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Memo Salida
                </span>
            </div>

            <div class="controls">
                <g:textField name="memoSalida" maxlength="15" style="width: 140px" class="" value="${obraInstance?.memoSalida}"/>
                
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
                
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Fecha Oficio Salida
                </span>
            </div>

            <div class="controls">
                <g:textField name="fechaOficioSalida" class="datepicker" style="width: 90px" value="${obraInstance?.fechaOficioSalida}"/>
<script type="text/javascript">
$("#fechaOficioSalida").datepicker({
changeMonth: true,
changeYear: true,
showOn: "both",
buttonImage: "${resource(dir:'images', file:'calendar.png')}",
buttonImageOnly: true
});
</script>
                
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
                
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Factor Reducción
                </span>
            </div>

            <div class="controls">
                <g:field type="number" name="factorReduccion" class=" required" value="${fieldValue(bean: obraInstance, field: 'factorReduccion')}"/>
                <span class="mandatory">*</span>
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
                
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Factor Velocidad
                </span>
            </div>

            <div class="controls">
                <g:field type="number" name="factorVelocidad" class=" required" value="${fieldValue(bean: obraInstance, field: 'factorVelocidad')}"/>
                <span class="mandatory">*</span>
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
                
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Capacidad Volquete
                </span>
            </div>

            <div class="controls">
                <g:field type="number" name="capacidadVolquete" class=" required" value="${fieldValue(bean: obraInstance, field: 'capacidadVolquete')}"/>
                <span class="mandatory">*</span>
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
                
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Factor Volumen
                </span>
            </div>

            <div class="controls">
                <g:textField name="factorVolumen" class="" value="${obraInstance?.factorVolumen}"/>
                
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
                
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Factor Peso
                </span>
            </div>

            <div class="controls">
                <g:textField name="factorPeso" class="" value="${obraInstance?.factorPeso}"/>
                
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
                
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Factor Reducción Tiempo
                </span>
            </div>

            <div class="controls">
                <g:field type="number" name="factorReduccionTiempo" class=" required" value="${fieldValue(bean: obraInstance, field: 'factorReduccionTiempo')}"/>
                <span class="mandatory">*</span>
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
                
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Sitio
                </span>
            </div>

            <div class="controls">
                <g:textArea cols="5" rows="5" style="resize: none" name="sitio" maxlength="63" class="" value="${obraInstance?.sitio}"/>
                
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
                
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Plazo Ejecución Anios
                </span>
            </div>

            <div class="controls">
                <g:field type="number" name="plazoEjecucionAnios" class=" required" value="${fieldValue(bean: obraInstance, field: 'plazoEjecucionAnios')}"/>
                <span class="mandatory">*</span>
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
                
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Plazo Ejecución Meses
                </span>
            </div>

            <div class="controls">
                <g:field type="number" name="plazoEjecucionMeses" class=" required" value="${fieldValue(bean: obraInstance, field: 'plazoEjecucionMeses')}"/>
                <span class="mandatory">*</span>
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
                
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Plazo Ejecución Días
                </span>
            </div>

            <div class="controls">
                <g:field type="number" name="plazoEjecucionDias" class=" required" value="${fieldValue(bean: obraInstance, field: 'plazoEjecucionDias')}"/>
                <span class="mandatory">*</span>
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
                
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Fórmula Polinómica
                </span>
            </div>

            <div class="controls">
                <g:textField name="formulaPolinomica" maxlength="15" class="" value="${obraInstance?.formulaPolinomica}"/>
                
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
                
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Indicador
                </span>
            </div>

            <div class="controls">
                <g:field type="number" name="indicador" class=" required" value="${fieldValue(bean: obraInstance, field: 'indicador')}"/>
                <span class="mandatory">*</span>
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
                
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Indice Gastos Generales
                </span>
            </div>

            <div class="controls">
                <g:field type="number" name="indiceGastosGenerales" class=" required" value="${fieldValue(bean: obraInstance, field: 'indiceGastosGenerales')}"/>
                <span class="mandatory">*</span>
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
                
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Impreso
                </span>
            </div>

            <div class="controls">
                <g:field type="number" name="impreso" class=" required" value="${fieldValue(bean: obraInstance, field: 'impreso')}"/>
                <span class="mandatory">*</span>
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
                
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Indice Utilidad
                </span>
            </div>

            <div class="controls">
                <g:field type="number" name="indiceUtilidad" class=" required" value="${fieldValue(bean: obraInstance, field: 'indiceUtilidad')}"/>
                <span class="mandatory">*</span>
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
                
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Contrato
                </span>
            </div>

            <div class="controls">
                <g:field type="number" name="contrato" class=" required" value="${fieldValue(bean: obraInstance, field: 'contrato')}"/>
                <span class="mandatory">*</span>
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
                
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Totales
                </span>
            </div>

            <div class="controls">
                <g:field type="number" name="totales" class=" required" value="${fieldValue(bean: obraInstance, field: 'totales')}"/>
                <span class="mandatory">*</span>
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
                
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Período
                </span>
            </div>

            <div class="controls">
                <g:textField name="periodo" maxlength="1" style="width: 20px" class="" value="${obraInstance?.periodo}"/>
                
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
                
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Valor
                </span>
            </div>

            <div class="controls">
                <g:textField name="valor" maxlength="10" style="width: 100px" class="" value="${obraInstance?.valor}"/>
                
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
                
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Partida Obra
                </span>
            </div>

            <div class="controls">
                <g:textField name="partidaObra" maxlength="10" style="width: 100px" class="" value="${obraInstance?.partidaObra}"/>
                
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
                
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Memo Certificación Partida
                </span>
            </div>

            <div class="controls">
                <g:textField name="memoCertificacionPartida" maxlength="10" style="width: 100px" class="" value="${obraInstance?.memoCertificacionPartida}"/>
                
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
                
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Memo Actualización Prefecto
                </span>
            </div>

            <div class="controls">
                <g:textField name="memoActualizacionPrefecto" maxlength="10" style="width: 100px" class="" value="${obraInstance?.memoActualizacionPrefecto}"/>
                
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
                
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Memo Partida Presupuestaria
                </span>
            </div>

            <div class="controls">
                <g:textField name="memoPartidaPresupuestaria" maxlength="10" style="width: 100px" class="" value="${obraInstance?.memoPartidaPresupuestaria}"/>
                
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
                
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Porcentaje Anticipo
                </span>
            </div>

            <div class="controls">
                <g:textField name="porcentajeAnticipo" maxlength="10" style="width: 100px" class="" value="${obraInstance?.porcentajeAnticipo}"/>
                
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
                
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Porcentaje Reajuste
                </span>
            </div>

            <div class="controls">
                <g:textField name="porcentajeReajuste" maxlength="10" style="width: 100px" class="" value="${obraInstance?.porcentajeReajuste}"/>
                
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
                
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Indice Costos Indirectos Obra
                </span>
            </div>

            <div class="controls">
                <g:field type="number" name="indiceCostosIndirectosObra" class=" required" value="${fieldValue(bean: obraInstance, field: 'indiceCostosIndirectosObra')}"/>
                <span class="mandatory">*</span>
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
                
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Indice Costos Indirectos Mantenimiento
                </span>
            </div>

            <div class="controls">
                <g:field type="number" name="indiceCostosIndirectosMantenimiento" class=" required" value="${fieldValue(bean: obraInstance, field: 'indiceCostosIndirectosMantenimiento')}"/>
                <span class="mandatory">*</span>
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
                
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Administración
                </span>
            </div>

            <div class="controls">
                <g:field type="number" name="administracion" class=" required" value="${fieldValue(bean: obraInstance, field: 'administracion')}"/>
                <span class="mandatory">*</span>
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
                
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Indice Costos Indirectos Garantias
                </span>
            </div>

            <div class="controls">
                <g:field type="number" name="indiceCostosIndirectosGarantias" class=" required" value="${fieldValue(bean: obraInstance, field: 'indiceCostosIndirectosGarantias')}"/>
                <span class="mandatory">*</span>
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
                
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Indice Costos Indirectos Costos Financieros
                </span>
            </div>

            <div class="controls">
                <g:field type="number" name="indiceCostosIndirectosCostosFinancieros" class=" required" value="${fieldValue(bean: obraInstance, field: 'indiceCostosIndirectosCostosFinancieros')}"/>
                <span class="mandatory">*</span>
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
                
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Indice Costos Indirectos Vehículos
                </span>
            </div>

            <div class="controls">
                <g:field type="number" name="indiceCostosIndirectosVehiculos" class=" required" value="${fieldValue(bean: obraInstance, field: 'indiceCostosIndirectosVehiculos')}"/>
                <span class="mandatory">*</span>
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
                
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Indice Costos Indirectos Promocion
                </span>
            </div>

            <div class="controls">
                <g:field type="number" name="indiceCostosIndirectosPromocion" class=" required" value="${fieldValue(bean: obraInstance, field: 'indiceCostosIndirectosPromocion')}"/>
                <span class="mandatory">*</span>
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
                
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Indice Costos Indirectos Timbres Provinciales
                </span>
            </div>

            <div class="controls">
                <g:field type="number" name="indiceCostosIndirectosTimbresProvinciales" class=" required" value="${fieldValue(bean: obraInstance, field: 'indiceCostosIndirectosTimbresProvinciales')}"/>
                <span class="mandatory">*</span>
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
                
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Inspector
                </span>
            </div>

            <div class="controls">
                <g:select id="inspector" name="inspector.id" from="${janus.Persona.list()}" optionKey="id" class="many-to-one  required" value="${obraInstance?.inspector?.id}"/>
                <span class="mandatory">*</span>
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
                
    </g:form>

<script type="text/javascript">
    var url = "${resource(dir:'images', file:'spinner_24.gif')}";
    var spinner = $("<img style='margin-left:15px;' src='" + url + "' alt='Cargando...'/>")

    $("#frmSave-obraInstance").validate({
        errorPlacement : function (error, element) {
            element.parent().find(".help-block").html(error).show();
        },
        success        : function (label) {
            label.parent().hide();
        },
        errorClass     : "label label-important",
        submitHandler  : function(form) {
            $("[name=btnSave-obraInstance]").replaceWith(spinner);
            form.submit();
        }
    });
</script>
