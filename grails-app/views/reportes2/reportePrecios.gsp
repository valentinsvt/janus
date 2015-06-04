<%@ page contentType="text/html;charset=UTF-8" %>
<html>
    <head>
        <title>Reporte de precios</title>

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

        table {
            border-collapse : collapse;
            width           : 100%;
        }

        th, td {
            vertical-align : middle;
        }

        th {
            background : #bbb;
            font-size  : 10px;
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

        .strong {
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
        </style>

    </head>

    <body>
        <div class="hoja">
            <h1 style="font-size: 16px">SEP - G.A.D. PROVINCIA DE PICHINCHA</h1>

            <h2>REPORTE DE COSTOS DE ${grupo?.descripcion?.toUpperCase()}</h2>

            <div style="height: 30px;">
                <div class="left strong"> LISTA DE PRECIOS: ${lugar?.descripcion?.toUpperCase()}</div>

                <div class="right strong">FECHA DE CONSULTA: <g:formatDate date="${new Date()}" format="dd-MM-yyyy"/></div>
            </div>


            <g:if test="${precios?.size() > 0 }">
                <table border="1">
                    <thead>
                    <tr>
                        <th>
                            CODIGO
                        </th>
                        <th>
                            ${grupo?.descripcion?.toUpperCase()}
                        </th>
                        <g:if test="${cols?.contains('u')}">
                            <th>
                                UNIDAD
                            </th>
                        </g:if>
                        <g:if test="${cols?.contains('t')}">
                            <th>
                                PESO/VOL
                            </th>
                        </g:if>
                        <g:if test="${cols?.contains('p')}">
                            <th>
                                COSTO
                            </th>
                        </g:if>
                        <g:if test="${cols?.contains('f')}">
                            <th>
                                FECHA ACT.
                            </th>
                        </g:if>
                    </tr>
                    </thead>
                    <tbody>
                    <g:each in="${precios}" var="precio" status="i">
                        <tr class="${i % 2 == 0 ? 'even' : 'odd'}">
                            <td>
                                ${precio?.item?.codigo}
                            </td>
                            <td>
                                ${precio?.item?.nombre}
                            </td>
                            <g:if test="${cols?.contains('u')}">
                                <td class="tcenter">
                                    ${precio?.item?.unidad?.codigo}
                                </td>
                            </g:if>
                            <g:if test="${cols?.contains('t')}">
                                <td>
                                    %{--${precio.item.peso}--}%
                                    <g:formatNumber number="${precio?.item?.peso}" minFractionDigits="6" maxFractionDigits="6" format="##,##0" locale='ec'/>

                                    <g:if test="${precio?.item?.transporte == 'P'}">
                                        Tn
                                    </g:if>
                                    <g:elseif test="${precio?.item?.transporte == 'V'}">
                                        M3
                                    </g:elseif>
                                </td>
                            </g:if>
                            <g:if test="${cols?.contains('p')}">
                                <td class="tright">
                                    <g:formatNumber number="${precio?.precioUnitario}" minFractionDigits="5" maxFractionDigits="5" format="##,##0" locale='ec'/>
                                </td>
                            </g:if>
                            <g:if test="${cols?.contains('f')}">
                                <td class="tright">
                                    <g:formatDate date="${precio?.fecha}" format="dd-MM-yyyy"/>
                                </td>
                            </g:if>
                        </tr>
                    </g:each>
                    </tbody>
                </table>
            </g:if>
            <g:else>
                No se encontraron datos
            </g:else>

        </div>
    </body>
</html>