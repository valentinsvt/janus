
<head>


    <script type="text/javascript" src="${resource(dir: 'js', file: 'tableHandler.js')}"></script>
    <script type="text/javascript" src="${resource(dir: 'js', file: 'tableHandlerBody.js')}"></script>
    <link rel="stylesheet" href="${resource(dir: 'css', file: 'tableHandler.css')}">

</head>

<body>

<table class="table table-bordered table-striped table-hover table-condensed" id="tabla">

    <thead>

    <th style="background-color: ${colorProv};">Provincia</th>
    <th style="background-color: ${colorCant};">Cantón</th>
    <th style="background-color: ${colorParr};">Parroquia</th>
    <th style="background-color: ${colorComn};">Comunidad</th>

    </thead>

    <tbody>

                <g:each in="${comunidades}" var="comn" status="i">
                    <tr>

                        <td>${comn.parroquia.canton.provincia.nombre}</td>
                        <td>${comn.parroquia.canton.nombre}</td>
                        <td>${comn.parroquia.nombre}</td>
                        <td>${comn.nombre}</td>


                    </tr>

    </g:each>
    </tbody>

</table>


<script type="text/javascript" src="${resource(dir: 'js', file: 'tableHandler.js')}"></script>
<script type="text/javascript" src="${resource(dir: 'js', file: 'tableHandlerBody.js')}"></script>


</body>
