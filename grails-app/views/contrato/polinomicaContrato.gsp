<%--
  Created by IntelliJ IDEA.
  User: fabricio
  Date: 1/17/13
  Time: 4:34 PM
  To change this template use File | Settings | File Templates.
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>

    <meta name="layout" content="main">
    <script src="${resource(dir: 'js/jquery/plugins/jquery-validation-1.9.0', file: 'jquery.validate.min.js')}"></script>
    <script src="${resource(dir: 'js/jquery/plugins/jquery-validation-1.9.0', file: 'messages_es.js')}"></script>

    <script src="${resource(dir: 'js/jquery/plugins/', file: 'jquery.livequery.js')}"></script>

    <style type="text/css">

    .formato {
        font-weight : bolder;
    }

    </style>


  <title>Formula Polinómica</title>
</head>
<body>

<div id="tabs" style="width: 700px; height: 800px; text-align: center">

  <ul>
      <li><a href="#tab-formulaPolinomica">Formula Polinómica</a></li>
      <li><a href="#tab-cuadrillaTipo">Cuadrilla Tipo</a></li>



  </ul>

    <div id="tab-formulaPolinomica" class="tab">

        <div class="formula">

            <fieldset class="borde">
                <legend>Formula Polinómica</legend>

            </fieldset>


        </div>


    </div>
    <div id="tab-cuadrillaTipo" class="tab">

        <fieldset class="borde">

            <legend>Cuadrilla Tipo</legend>


        </fieldset>



    </div>



</div>


<script type="text/javascript">

    $("#tabs").tabs ( {

        heightStyle:"fill"


    });




</script>

</body>
</html>