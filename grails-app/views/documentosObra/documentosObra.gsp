<%--
  Created by IntelliJ IDEA.
  User: fabricio
  Date: 12/6/12
  Time: 3:11 PM
  To change this template use File | Settings | File Templates.
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <meta name="layout" content="main">

    <title>Formato de Impresión</title>
</head>

<body>

<div id="tabs" style="width: 600px; height: 400px">

    <ul>

        <li><a href="#tab-presupuesto">Presupuesto</a></li>
        <li><a href="#tab-memorando">Memorando</a></li>
        <li><a href="#tab-polinomica">F. Polinómica</a></li>
        <li><a href="#tab-textosFijos">Textos Fijos</a></li>

    </ul>

    <div id="tab-presupuesto" class="tab">

        <div class="tipoReporte">

         <fieldset class="borde">

             <p class="css-vertical-text">Tipo de Reporte</p>

             <div class="linea" style="height: 85%;"></div>

         </fieldset>

        </div>

        <div class="piePagina">

            <fieldset class="borde">

                <p class="css-vertical-text">Tipo de Reporte</p>

                <div class="linea" style="height: 85%;"></div>


            </fieldset>



        </div>


    </div>

    <div id="tab-memorando" class="tab">
        <div class="row-fluid">

        </div>

    </div>

    <div id="tab-polinomica" class="tab">
        <div class="row-fluid">

        </div>

    </div>

    <div id="tab-textosFijos" class="tab">
        <div class="row-fluid">

        </div>

    </div>

</div>

<script type="text/javascript">


    $("#tabs").tabs({
        heightStyle:"fill"
    });

</script>

</body>
</html>