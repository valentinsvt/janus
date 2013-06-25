<html>
    <head>
        <title>
            Planilla
        </title>

        <style type="text/css">
        @page {
            size   : 21cm 29.7cm ;  /*width height */
            margin : 1.5cm;
        }

        html {
            font-family : Verdana, Arial, sans-serif;
            font-size   : 8px;
        }

        .hoja {
            width      : 17.5cm;
            /*background : #ffebcd;*/
            /*border     : solid 1px black;*/
            min-height : 200px;
        }

        h1, h2, h3 {
            text-align : center;
        }

        h1 {
            font-size : 14px;
        }

        h2 {
            font-size : 12px;
        }

        h3 {
            font-size : 11px;
        }

        table {
            border-collapse : collapse;
            width           : 100%;
            border          : solid 1px black;
        }

        td, th {
            border         : solid 1px black;
            vertical-align : middle !important;
        }

        th {
            background : #bbb;
            font-size  : 10px;
        }

        td {
            font-size : 8px;
        }

        tbody th {
            background : #ECECEC !important;
            color      : #000000 !important;
        }

        .number {
            text-align : right !important;
        }

        .area {
            /*border-bottom : 1px solid black;*/
            padding-left  : 50px;
            position      : relative;
            overflow-x    : auto;
            /*min-height    : 150px;*/
        }

        .nb {
            border-left : none !important;
        }

        .bold {
            font-weight : bold;
        }

        .noborder, table.noborder, table.noborder td, table.noborder th {
            border : none !important;
        }

        .tal {
            text-align : left !important;
        }
        </style>

    </head>

    <body>
        <div class="hoja">
            <elm:headerPlanillaReporte planilla="${planilla}"/>

            <div id="list-grupo" class="span12" role="main" style="margin-top: 10px;margin-left: -10px">
                <div class="area">
                    <h2>Cálculo de B<sub>0</sub></h2>
                    ${tablaB0}
                </div> <!-- B0 -->

                <div class="area">
                    <h2>Cálculo de P<sub>0</sub></h2>
                    ${tablaP0}
                </div> <!-- P0 -->

                <div class="area" style="min-height: 190px; margin-bottom: 10px;">
                    <h2>Cálculo de F<sub>r</sub> y P<sub>r</sub></h2>
                    ${tablaFr}
                </div> <!-- Fr y Pr -->

                <g:if test="${planilla.tipoPlanilla.codigo != 'A'}">
                    <div class="area" style="min-height: 190px; margin-bottom: 10px;">
                        <h2>Multas</h2>

                        <h3>Multa por no presentación de planilla</h3>
                        ${pMl}
                        <h3>Multa por retraso de obra</h3>
                        ${tablaMl}
                    </div> <!-- Multas -->
                </g:if>
            </div>

        </div>
    </body>
</html>