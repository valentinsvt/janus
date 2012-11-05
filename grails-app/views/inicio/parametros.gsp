<%@ page contentType="text/html" %>

<html>
<head>
    <meta name="layout" content="admin"/>
    <title>Parámetros</title>

    <script type="text/javascript" src="${resource(dir: 'js/jquery/plugins', file: 'jquery.cookie.js')}"></script>

</head>

<body>

<div class="container ui-corner-all " style="">
  <div style="float:left; width:640px;">
     <div id="info">
        <ul id="info-nav">
            <li><a href="#gnrl">Generales</a></li>
            <li><a href="#obra">Obras</a></li>
            <li><a href="#cntr">Ejecución</a></li>
        </ul>
        <div id="gnrl" class="ui-widget-content" style="height: 440px">
            <div class="item" texto="grgf">
                <g:link  controller="zona" action="arbol">Divisi&oacute;n geogr&aacute;fica del Pa&iacute;s</g:link> en cantones, parroquias y comunidades.
            </div><br>
            <div class="item" texto="undd">
                <g:link  controller="unidad" action="list">Unidades</g:link> Unidades de medida para los materiales, mano de obra y equipos.
            </div><br>
            <div class="item" texto="grpo">
                <g:link  controller="grupo" action="list">Grupos de Ítems</g:link> para clasifiacar entre materiales, mano de obra y equipos.
            </div><br>
            <div class="item" texto="dpto">
                <g:link  controller="departamento" action="list">Departamentos del personal</g:link> para la organización de los usuarios.
            </div><br>
        </div>
        <div id="obra" class="ui-widget-content" style="height: 440px">
            <div class="item" texto="tpob">
                <g:link controller="tipoObra" action="list">Tipo de Obras</g:link> a ejecutarse en un proyecto.
            </div><br>
            <div class="item" texto="csob">
                <g:link controller="claseObra" action="list">Clase de Obra</g:link> para distinguir entre varios clases de obra civiles y vials.
            </div><br>
            <div class="item" texto="edob">
                <g:link controller="estadoObra" action="list">Estado de la Obra</g:link> que distingue las distanas fases de contratación y ejecución de la obraa.
            </div><br>
        </div>

        <div id="cntr" class="ui-widget-content" style="height: 440px">
            <div class="item" texto="tprb">
                <g:link controller="tipoRubro" action="list">Tipo de rubro</g:link> que componen los ingresos y
                egresos de nómina de un empleado
            </div><br>
        </div>
     </div>
</div>

<div id="tool" style="float:left; width: 200px; height: 300px; margin-top: 80px; margin-left: 20px; display: none; padding:10px;"
     class="ui-widget-content ui-corner-all">
</div>

<div id="grgf" style="display:none">
    <h3>Divisi&oacute;n geogr&aacute;fica del Pa&iacute;s</h3><br>
    <p>Provincias, cantones, parroquias y comunidades</p>
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
            $('#tool').hide('');
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
