<%--
  Created by IntelliJ IDEA.
  User: fabricio
  Date: 2/5/13
  Time: 12:28 PM
  To change this template use File | Settings | File Templates.
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <meta name="layout" content="main">
  <title>Registro Rol</title>
</head>
<body>


<div class="span6">

    <div class="span1">Persona:</div>
    <g:select name="persona" from="${janus.Persona?.list()}" optionValue="${{it.nombre + ' ' + it.apellido}}"/>


    <div class="span6" style="width: 500px">

        <table class="table table-bordered table-striped table-hover table-condensed " id="tablaFuncion">

            <thead>
            <tr>
                <th style="width: 50px">N°</th>
                <th style="width: 250px">Función</th>
                <th style="width: 20px"> </th>
            </tr>

            </thead>

            <tbody id="funcionPersona">


            </tbody>

        </table>


    </div>



</div>

<div class="span6">

    <div class="span1">Función: </div>
    <g:select name="funcion" from="${janus.Funcion?.list()}" optionValue="descripcion" optionKey="id"/>


    <div class="btn-group" style="margin-left: 400px; margin-top: -60px; margin-bottom: 10px">
        <button class="btn btnAdicionar" id="adicionar">Adicionar</button>


    </div>


</div>



</body>
</html>