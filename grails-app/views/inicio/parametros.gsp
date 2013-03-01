<%@ page contentType="text/html" %>

<html>
<head>
    <meta name="layout" content="main"/>
    <title>Parámetros</title>

    <script type="text/javascript" src="${resource(dir: 'js/jquery/plugins', file: 'jquery.cookie.js')}"></script>

</head>

<body>

<div class="container ui-corner-all " style="">
    <div style="float:left; width:600px;">
        <div id="info">
            <ul id="info-nav">
                <li><a href="#gnrl">Generales</a></li>
                <li><a href="#obra">Obras</a></li>
                <li><a href="#cntr">Contratación</a></li>
                <li><a href="#ejec">Ejecución</a></li>
            </ul>

            <div id="gnrl" class="ui-widget-content" style="height: 440px">
                <div class="item" texto="grgf">
                    <g:link controller="canton"
                            action="arbol">Divisi&oacute;n geogr&aacute;fica del Pa&iacute;s</g:link> en cantones, parroquias y comunidades.
                </div><br>

                <div class="item" texto="tpit">
                    <g:link controller="tipoItem"
                            action="list">Tipo de ítem</g:link> para diferenciar entre ítems y rubros.
                </div><br>

                <div class="item" texto="undd">
                    <g:link controller="unidad"
                            action="list">Unidades</g:link> de medida para los materiales, mano de obra y equipos.
                </div><br>

                <div class="item" texto="grpo">
                    <g:link controller="grupo"
                            action="list">Grupos de Ítems</g:link> para clasificar entre materiales, mano de obra y equipos.
                </div><br>

                <div class="item" texto="trnp">
                    <g:link controller="transporte"
                            action="list">Transporte</g:link> para diferenciar los ítems que participan en el transporte.
                </div><br>

                <div class="item" texto="dpto">
                    <g:link controller="departamento"
                            action="list">Departamentos del personal</g:link> para la organización de los usuarios.
                </div><br>

                <div class="item" texto="tpus">
                    <g:link controller="tipoUsuario"
                            action="list">Tipo de Usuario</g:link> o de Personal, para usarse en la designación de
                    los distintos responsables de obras.
                </div><br>

                <div class="item" texto="func">
                    <g:link controller="funcion"
                            action="list">Funciones del personal</g:link> que pueden desempeñar en la construcción de la obra
                    o en los  distintos momentos de la contratación y ejecución de obras.
                </div><br>

                <div class="item" texto="tpin">
                    <g:link controller="tipoIndice" action="list">Tipo de Indice</g:link> según el INEC.
                </div><br>
            </div>

            <div id="obra" class="ui-widget-content" style="height: 440px">
                <div class="item" texto="tpob">
                    <g:link controller="tipoObra" action="list">Tipo de Obras</g:link> a ejecutarse en un proyecto.
                </div><br>

                <div class="item" texto="csob">
                    <g:link controller="claseObra"
                            action="list">Clase de Obra</g:link> para distinguir entre varios clases de obra civiles y viales.
                </div><br>

                <div class="item" texto="prsp">
                    <g:link controller="presupuesto"
                            action="list">Partida Presupuestaria</g:link> con la cual se financia o construye a obra.
                </div><br>

                <div class="item" texto="edob">
                    <g:link controller="estadoObra"
                            action="list">Estado de la Obra</g:link> que distingue las distintas fases de contratación y ejecución de la obra.
                </div><br>

                <div class="item" texto="prog">
                    <g:link controller="programacion" action="list">Programa</g:link> del cual forma parte una obra .
                </div><br>

                <div class="item" texto="paux">
                    <g:link controller="parametros"
                            action="list">Parámetros de costos</g:link> indirectos y valores de los indices.
                </div><br>

                <div class="item" texto="auxl">
                    <g:link controller="auxiliar"
                            action="list">Textos fijos</g:link> para la generación de los documentos precontractuales.
                </div><br>

                <div class="item" texto="tpfp">
                    <g:link controller="tipoFormulaPolinomica" action="list">Tipo de fórmula polinómica</g:link> de reajuste de
                    precios que puede tener un contrato.
                </div><br>
            </div>

            <div id="cntr" class="ui-widget-content" style="height: 440px">
                <div class="item" texto="tpcr">
                    <g:link controller="tipoContrato"
                            action="list">Tipo de contrato</g:link> que puede registrarse en el sistema para
                    la ejecución de una Obra.
                </div><br>

                <div class="item" texto="tpgr">
                    <g:link controller="tipoGarantia" action="list">Tipo de Garantía</g:link> que se puede recibir
                    en un contrato.
                </div><br>

                <div class="item" texto="tdgr">
                    <g:link controller="tipoDocumentoGarantia" action="list">Tipo de documento de garantía</g:link>
                    que se puede recibir para garantizar las distintas estipulaciones de una contrato.
                </div><br>

                <div class="item" texto="edgr">
                    <g:link controller="estadoGarantia" action="list">Estado de la garantía</g:link>
                    dentro del período contractual.
                </div><br>

                <div class="item" texto="mnda">
                    <g:link controller="moneda" action="list">Moneda</g:link>
                    en la cual se recibe la garantía.
                </div><br>

                <div class="item" texto="tpas">
                    <g:link controller="tipoAseguradora" action="list">Tipo de aseguradora</g:link>
                    que emite la garantía.
                </div><br>

                <div class="item" texto="asgr">
                    <g:link controller="aseguradora" action="list">Aseguradora</g:link>
                    o institución bancaria que emite la garantía.
                </div><br>

                <div class="item" texto="itun">
                    <g:link controller="unidadIncop" action="list">Unidad del Item</g:link>

                </div><br>

                <div class="item" texto="tppt">
                    <g:link controller="tipoProcedimiento" action="list">Tipo de Procedimiento</g:link>

                </div><br>
                <div class="item" texto="tpcp">
                    <g:link controller="tipoCompra" action="list">Tipo de Compra</g:link>


                </div><br>


            </div>

            <div id="ejec" class="ui-widget-content" style="height: 440px">
                <div class="item" texto="edpl">
                    <g:link controller="estadoPlanilla" action="list">Estado de la planilla</g:link> que puede
                    tener dentro del proceso de ejecución de la obra: ingresada, pagada, anulada.
                </div><br>

                <div class="item" texto="tppl">
                    <g:link controller="tipoPlanilla" action="list">Tipo de planilla</g:link> que puede
                    tener el proceso de ejecución de la obra: anticipo, liquidación, avance de obra, reajuste,
                    etc.
                </div><br>

                %{--<div class="item" texto="tpds">--}%
                    %{--<g:link controller="tipoDescuento" action="list">Tipo de descuento</g:link> que puede--}%
                    %{--tener una planilla, pueden ser: anticipo, fiscalización, timbres, etc.--}%
                %{--</div><br>--}%

                <div class="item" texto="dstp">
                    <g:link controller="descuentoTipoPlanilla" action="list">Descuentos que se aplican</g:link>
                    a cada tipo de planilla.
                </div><br>

                <div class="item" texto="tpml">
                    <g:link controller="tipoMulta" action="list">Tipo de multa</g:link> que se puede
                    aplicar a una planilla.
                </div><br>

                %{--<div class="item" texto="tppo">--}%
                    %{--<g:link controller="tipoProrroga" action="list">Tipo de prórroga</g:link> que se puede--}%
                    %{--aplicar a una obra durante su ejecución. Puede ser ampliación o suspensión.--}%
                %{--</div><br>--}%
            </div>
        </div>
    </div>

    <div id="tool" style="float:left; width: 160px; height: 300px; margin: 30px; display: none; padding:25px;"
         class="ui-widget-content ui-corner-all">
    </div>
</div>

<div id="grgf" style="display:none">
    <h3>Divisi&oacute;n geogr&aacute;fica del Pa&iacute;s</h3><br>

    <p>Provincias, cantones, parroquias y comunidades</p>
</div>

<div id="tpit" style="display:none">
    <h3>Tipo de ítem</h3><br>

    <p>Diferencia entre los ítems y los rubros. Un rubro se halla conformado por ítems de los grupos: Materiales, Mano de obra y Equipos.</p>
</div>

<div id="undd" style="display:none">
    <h3>Unidad de Medida</h3><br>

    <p>Unidades de medida utilizadas para materiales, mano de obra y equipos</p>
</div>

<div id="grpo" style="display:none">
    <h3>Grupos de Ítems</h3><br>

    <p>Grupos para la clasificación de los ítems que conforman los rubros
    para el análisis de precios unitarios</p>
</div>

<div id="trnp" style="display:none">
    <h3>Transporte</h3><br>

    <p>Variable de transporte que define entre Chofer y medio o vehículo de transporte, como Volquetas.</p>
</div>

<div id="dpto" style="display:none">
    <h3>Departamento</h3><br>

    <p>Distribución administrativa de la institución: Departamentos que la conforman, para la asociación de los
    empleados a cada uno de ellos.</p>
</div>

<div id="tpus" style="display:none">
    <h3>Tipo de Usuario</h3><br>

    <p>Tipo de usuario o tipo de personal técnico, para usarse en la designación de
    los distintos responsables de obras, como responsable de obra, revisor e inspector.</p>
</div>

<div id="func" style="display:none">
    <h3>Funciones del Personal</h3><br>

    <p>Funciones que una persona puede desempeñar en los procesos de
    contratación y ejecución de obras.</p>
</div>

<div id="tpin" style="display:none">
    <h3>Tipo de Indice</h3><br>

    <p>Según la clasificación del INEC que se aplica a la fórmula polinómica y cada uno de sus coeficientes.
    Tanto para la cuadrilla tipo como para fórmula general.</p>
</div>

<div id="tpob" style="display:none">
    <h3>Tipo de Obra</h3><br>
    <p>Tipo de obra a ejecutarse.</p>
</div>
<div id="csob" style="display:none">
    <h3>Clase de Obra</h3><br>
    <p>Clase de obra, ejemplo: aulas, pavimento, cubierta, estructuras, adoquinado, puentes, mejoramiento, etc.</p>
</div>
<div id="prsp" style="display:none">
    <h3>Partida presupuestaria</h3><br>
    <p>Con la cual se financia la obra. El registro se debe hacer conforme se obtenga la partida desde el financiero,
    una vez que se haya expedido la certificación presupuestaria para la obra.</p>
</div>
<div id="edob" style="display:none">
    <h3>Estado de la Obra</h3><br>
    <p>Estado de la obra durante el proyecto de construcción, para distinguir entre: precontractual, ofertada, contratada, etc.</p>
</div>
<div id="prog" style="display:none">
    <h3>Programa</h3><br>
    <p>Programa del cual forma parte una obra o proyecto.</p>
</div>
<div id="paux" style="display:none">
    <h3>Parámetros de Costos</h3><br>
    <p>Desglose de valores de costos indirectos que deben cargarse al presupuesto de las obras.</p>
</div>
<div id="auxl" style="display:none">
    <h3>Textos fijos</h3><br>
    <p>Textos para los documentos precontractuales de presupuesto, volúmenes de obra y fórmula polinómica.</p>
</div>
<div id="tpfp" style="display:none">
    <h3>Tipo de fórmula polinómica</h3><br>
    <p>Tipo de forma polínomica que tiene el contrato, puede ser contractual o de liquidación.</p>
</div>

<div id="tpcr" style="display:none">
    <h3>Tipo de contrato</h3><br>
    <p>Tipo de contrato que se puede registrar en el sistema, por ejemplo: COntrato, escritura pública, convenio.</p>
</div>
<div id="tpgr" style="display:none">
    <h3>Tipo de garantía</h3><br>
    <p>Tipo de garantía que se debe presentar en el contrato. Puden ser por ejemplo: Buen uso del anticipo, fiel cumplimiento,
    buena calidad de materiales, etc.</p>
</div>
<div id="tdgr" style="display:none">
    <h3>Documento de la garantía</h3><br>
    <p>Documento que se presenta como garantía.</p>
</div>
<div id="edgr" style="display:none">
    <h3>Estado de la garantía</h3><br>
    <p>Estado que puede tener la garantía. Puden ser por ejemplo: Vigente, pedido de cobro, devuelta, efectivizada, pasivo,
    renovada, vencida, etc.</p>
</div>
<div id="mnda" style="display:none">
    <h3>Moneda de la garantía</h3><br>
    <p>Moneda en la que se entrega la garantía.</p>
</div>
<div id="tpas" style="display:none">
    <h3>Tipo de Aseguradora</h3><br>
    <p>Tipo de aseguradora que emite la garantía, puede ser Banco, Cooperativa, Aseguradora.</p>
</div>
<div id="asgr" style="display:none">
    <h3>Aseguradora</h3><br>
    <p>Aseguradora que emite la garantía, puede ser Banco, Cooperativa, Aseguradora. Se registran los datos de la
    aseguradora</p>
</div>
<div id="itun" style="display: none">
    <h3>Unidad del item</h3>
    <p>Unidad del item</p>


</div>

<div id="tppr" style="display:none">
    <h3>Tipo de Procedimiento</h3><br>
    <p>Tipo de Procedimiento</p>
</div>
<div id="tpcp" style="display: none">
    <h3>Tipo de Compra</h3>
       <p>Tipo de Compra</p>
</div>







<div id="edpl" style="display:none">
    <h3>Estado de la Planilla</h3><br>
    <p>Estado que puede tener una planilla dentro del proceso de ejecución de la obra: ingresada, pagada, anulada.</p>
</div>
<div id="tppl" style="display:none">
    <h3>Tipo de Planilla</h3><br>
    <p>Tipo de planilla, pueden ser: anticipo, liquidación, avance de obra, reajuste, etc. </p>
</div>
%{--<div id="tpds" style="display:none">--}%
    %{--<h3>Tipo de descuento de una Planilla</h3><br>--}%
    %{--<p>Tipo de descuento que puede tener una planilla, pueden ser: anticipo, fiscalización, timbres, etc. </p>--}%
%{--</div>--}%
<div id="dstp" style="display:none">
    <h3>Descuento por tipo de Planilla</h3><br>
    <p>Descuentos que se aplican a cada tipo de planilla. </p>
</div>
<div id="tpml" style="display:none">
    <h3>Tipo de multa</h3><br>
    <p>Tipo de multa que puede tener una planilla. Pueden ser: retraso de obra, atraso de planilla, etc.</p>
</div>
%{--<div id="tppo" style="display:none">--}%
    %{--<h3>Tipo de prórroga</h3><br>--}%
    %{--<p>Tipo de prórroga que se pueda aplicar a una obra, puede ser ampliación o suspensión.</p>--}%
%{--</div>--}%


<script type="text/javascript">
    $(document).ready(function () {
        $('.item').hover(function () {
            //$('.item').click(function(){
            //entrar
            $('#tool').html($("#" + $(this).attr('texto')).html());
            $('#tool').show();
        }, function () {
            //sale
            $('#tool').hide();
        });

        $('#info').tabs({
            //event: 'mouseover', fx: {
            cookie:{ expires:30 },
            event:'click', fx:{
                opacity:'toggle',
                duration:'fast'
            },
            spinner:'Cargando...',
            cache:true
        });
    });
</script>
</body>
</html>
