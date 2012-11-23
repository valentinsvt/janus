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
                            <g:link controller="zona" action="arbol">Divisi&oacute;n geogr&aacute;fica del Pa&iacute;s</g:link> en cantones, parroquias y comunidades.
                        </div><br>

                        <div class="item" texto="tpit">
                            <g:link controller="tipoItem" action="list">Tipo de ítem</g:link> para diferenciar entre ítems y rubros.
                        </div><br>

                        <div class="item" texto="undd">
                            <g:link controller="unidad" action="list">Unidades</g:link> Unidades de medida para los materiales, mano de obra y equipos.
                        </div><br>

                        <div class="item" texto="grpo">
                            <g:link controller="grupo" action="list">Grupos de Ítems</g:link> para clasifiacar entre materiales, mano de obra y equipos.
                        </div><br>

                        <div class="item" texto="trnp">
                            <g:link controller="transporte" action="list">Transporte</g:link> para diferenciar los ítems que participan en el transporte.
                        </div><br>

                        <div class="item" texto="dpto">
                            <g:link controller="departamento" action="list">Departamentos del personal</g:link> para la organización de los usuarios.
                        </div><br>

                        <div class="item" texto="tpus">
                            <g:link controller="tipoUsuario" action="list">Tipo de Usuario</g:link> o de Personal, para usarse en la designación de
                            los distintos responsables de obras.
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
                            <g:link controller="claseObra" action="list">Clase de Obra</g:link> para distinguir entre varios clases de obra civiles y vials.
                        </div><br>

                        <div class="item" texto="prsp">
                            <g:link controller="presupuesto" action="list">Partida Presupuestaria</g:link> con la cual se financia o construye a obra.
                        </div><br>

                        <div class="item" texto="edob">
                            <g:link controller="estadoObra" action="list">Estado de la Obra</g:link> que distingue las distanas fases de contratación y ejecución de la obra.
                        </div><br>

                        <div class="item" texto="prog">
                            <g:link controller="programacion" action="list">Programa</g:link> del cual forma parte una obra .
                        </div><br>

                        <div class="item" texto="tppr">
                            <g:link controller="tipoPeriodo" action="list">Tipo de periodo</g:link> para la programación del cronograma de la obra.
                        </div><br>

                        <div class="item" texto="paux">
                            <g:link controller="parametros" action="list">Parámetros de costos</g:link> indirectos y valores de los indices.
                        </div><br>

                        <div class="item" texto="auxl">
                            <g:link controller="auxiliar" action="list">Textos fijos</g:link> para la generación de los documentos precontractuales.
                        </div><br>
                    </div>

                    <div id="cntr" class="ui-widget-content" style="height: 440px">
                        <div class="item" texto="tprb">
                            <g:link controller="tipoRubro" action="list">Tipo de rubro</g:link> que componen los ingresos y
                            egresos de nómina de un empleado
                        </div><br>
                    </div>

                    <div id="ejec" class="ui-widget-content" style="height: 440px">
                        <div class="item" texto="tprb">
                            <g:link controller="tipoRubro" action="list">Tipo de rubro</g:link> que componen los ingresos y
                            egresos de nómina de un empleado
                        </div><br>
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
            <h3>Tipo de Rubro</h3><br>

            <p>Estado de la obra durante el proyecto de construcción, para distinguir entre: precontractual, ofertada, contratada, etc.</p>
        </div>

        <div id="prog" style="display:none">
            <h3>Programa</h3><br>

            <p>Programa del cual forma parte una obra o proyecto.</p>
        </div>

        <div id="tppr" style="display:none">
            <h3>Tipo de período</h3><br>

            <p>Se usa para la programación o estructura del cronogrma de la obra.</p>
        </div>

        <div id="paux" style="display:none">
            <h3>Parámetros de Costos</h3><br>

            <p>Desglose de valores de costos indirectos que deben cargarse al presupuesto de las obras.</p>
        </div>

        <div id="auxl" style="display:none">
            <h3>Textos fijos</h3><br>

            <p>Textos para los documentos precontractuales de presupuesto, volúmenes de obra y fórmula polinómica.</p>
        </div>


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
                    cookie  : { expires : 30 },
                    event   : 'click', fx : {
                        opacity  : 'toggle',
                        duration : 'fast'
                    },
                    spinner : 'Cargando...',
                    cache   : true
                });
            });
        </script>
    </body>
</html>
