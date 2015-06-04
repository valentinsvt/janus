<%--
  Created by IntelliJ IDEA.
  User: fabricio
  Date: 11/22/12
  Time: 4:05 PM
  To change this template use File | Settings | File Templates.
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<html>
    <head>
        <title>Análisis de Precios Unitarios de Presupuesto</title>

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
            background : #ffebcd;
            border     : 1px solid #000000;

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
            font-size : 10px;
        }

        table {
            border-collapse : collapse;
            width           : 100%;
        }

        th, td {
            vertical-align : middle;
        }

        th {
            background : #bbb;
        }


        </style>

    </head>

    <body>

        <div class="hoja">

            <h1>SEP - G.A.D. PROVINCIA DE PICHINCHA</h1>

            <h2>Coordinación de Costos</h2>

            <h2>Análisis de Precios Unitarios de Presupuesto</h2>

            <div class="span10">

                <div class="span3">Fecha:</div>

                <div class="span3">Fecha Act.P.U.:</div>

            </div>

            <div class="span10">

                <div class="span3">Código:</div>

                <div class="span3">Unidad:</div>

            </div>

            <div class="span10">
                <div class="span3">Descripción:</div>

                <div class="span3">Rendimiento:</div>
            </div>


            <div class="span5">
                <div align="left">EQUIPOS</div>
            </div>
            <table border='1' style="font-size: 10px !important;">
                <thead>
                    <tr>
                        <th>CODIGO</th>
                        <th>DESCRIPCION</th>
                        <th>CANTIDAD</th>
                        <th>TARIFA</th>

                    </tr>
                </thead>
                <tbody>

                    <g:each in="${item}" var="item">

                    </g:each>

                </tbody>
            </table>

            <div class="span5">

                <div align="left">MANO DE OBRA</div>

            </div>

            <table border='1' style="font-size: 10px !important;">
                <thead>
                    <tr>
                        <th>CODIGO</th>
                        <th>DESCRIPCION</th>
                        <th>CANTIDAD</th>
                        <th>JORNAL</th>

                    </tr>
                </thead>
                <tbody>

                </tbody>
            </table>

            <div class="span5">

                <div align="left">MATERIALES</div>

            </div>

            <table border='1' style="font-size: 10px !important;">
                <thead>
                    <tr>
                        <th>CODIGO</th>
                        <th>DESCRIPCION</th>
                        <th>UNIDAD</th>
                        <th>CANTIDAD</th>

                    </tr>
                </thead>
                <tbody>

                </tbody>
            </table>
        </div>

    </body>
</html>