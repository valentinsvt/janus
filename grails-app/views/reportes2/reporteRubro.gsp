<%--
  Created by IntelliJ IDEA.
  User: luz
  Date: 3/6/13
  Time: 4:34 PM
  To change this template use File | Settings | File Templates.
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<html>
    <head>
        <title>
            Rubros
        </title>

        <style type="text/css">
        @page {
            size   : 21cm 29.7cm ;  /*width height */
            margin : 1.5cm;
        }

        body {
            background : none !important;
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

        h1 {
            font-size : 14px;
        }

        h2 {
            font-size : 12px;
        }

        table {
            border-collapse : collapse;
            /*width           : 100%;*/
            margin-bottom   : 10px;
        }

        th, td {
            padding       : 5px;
            border-bottom : solid 1px #555;
        }

        th {
            /*background : #bbb;*/
            font-size  : 9px;
            text-align : left;
        }

        td {
            font-size : 8px;
        }

        .even {
            background : #ddd;
        }

        .odd {
            background : #efefef;
        }

        .left {
            float : left;
        }

        .right {
            float : right;
        }

        .bold {
            font-weight : bold;
        }

        table {
            border-collapse : collapse;
        }

        .tright {
            text-align : right;
        }

        .tcenter {
            text-align : center;
        }

        .fila {
            clear  : both;
            height : 12px;
        }

        .celda {
            float : left;
        }

        .rubro {
            page-break-after : always;
            /*margin-bottom    : 50px;*/
        }
        </style>

    </head>

    <body>

        <div class="hoja">
            <g:each in="${rubros}" var="rubro">
                <h3>${rubro.nombre} id: ${rubro.id}</h3>
                <table border="0" class="rubro">
                    <tr>
                        <th>Código</th>
                        <td>${rubro.codigo}</td>
                        <th>Unidad</th>
                        <td>${rubro?.unidad?.descripcion}</td>
                    </tr>
                    <tr>
                        <th>Fecha creación</th>
                        <td>${rubro.fecha?.format("dd-MM-yyyy")}</td>
                        <th>Fecha modificación</th>
                        <td>${rubro.fechaModificacion?.format("dd-MM-yyyy")}</td>
                    </tr>
                    <tr>
                        <th>Solicitante</th>
                        <td>${rubro?.departamento?.subgrupo?.grupo?.descripcion}</td>
                        <th>Grupo</th>
                        <td>${rubro?.departamento?.subgrupo?.descripcion}</td>
                        <th>Subgrupo</th>
                        <td>${rubro?.departamento?.descripcion}</td>
                    </tr>
                    <tr>
                        <th>Especificación</th>
                        <td colspan="5">${rubro?.especificaciones}</td>
                    </tr>
                    <tr>
                        <th>Ilustración</th>
                        <td colspan="5">${rubro?.foto}</td>
                    </tr>
                </table>
            </g:each>
        </div>
    </body>
</html>