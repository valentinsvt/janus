<%--
  Created by IntelliJ IDEA.
  User: luz
  Date: 12/19/12
  Time: 3:31 PM
  To change this template use File | Settings | File Templates.
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<html>
    <head>
        <g:if test="${rend == 'screen'}">
            <meta name="layout" content="main">
            <script src="${resource(dir: 'js/jquery/plugins/DataTables-1.9.4/media/js', file: 'jquery.dataTables.min.js')}"></script>
            <link href="${resource(dir: 'js/jquery/plugins/DataTables-1.9.4/media/css', file: 'jquery.dataTables.css')}" rel="stylesheet">
        </g:if>
        <title>Composición de la obra</title>

        <g:if test="${rend == 'pdf'}">
            <style type="text/css">
            @page {
                size   : 21cm 29.7cm ;  /*width height */
                margin : 1.5cm;
            }

            html {
                font-family : Verdana, Arial, sans-serif;
                font-size   : 8px;
            }

            .tituloPdf {
                height        : 100px;
                font-size     : 11px;
                /*font-weight   : bold;*/
                text-align    : center;
                margin-bottom : 5px;
                width         : 95%;
                /*font-family       : 'Tulpen One', cursive !important;*/
                /*font-family : "Open Sans Condensed" !important;*/
            }

            .totales {
                font-weight : bold;
            }

            .num {
                text-align : right;
            }

            .header {
                background : #333333 !important;
                color      : #AAAAAA;
            }

            .total {
                background : #000000 !important;
                color      : #FFFFFF !important;
            }

                /*th {*/
                /*background : #cccccc;*/
                /*}*/

                /*tbody tr:nth-child(2n+1) {*/
                /*background : none repeat scroll 0 0 #E1F1F7;*/
                /*}*/

                /*tbody tr:nth-child(2n) {*/
                /*background : none repeat scroll 0 0 #F5F5F5;*/
                /*}*/
            thead tr {
                margin : 0px
            }

            th, td {
                font-size : 10px !important;

            }

            .sorting_desc {

                class : sorting !important;

            }

            .row-fluid {
                width  : 100%;
                height : 20px;
            }

            .span3 {
                width  : 29%;
                float  : left;
                height : 100%;
            }

            .span8 {
                width  : 79%;
                float  : left;
                height : 100%;
            }

            .span7 {
                width  : 69%;
                float  : left;
                height : 100%;
            }

            .tituloChevere {
                color       : #0088CC;
                border      : 0px solid red;
                white-space : nowrap;
                display     : block;
                width       : 98%;
                height      : 30px;
                font-weight : bold;
                font-size   : 14px;
                text-shadow : -2px 2px 1px rgba(0, 0, 0, 0.25);
                margin-top  : 10px;
                line-height : 25px;
            }

            </style>
        </g:if>

    </head>

    <body>
        <div class="hoja">
            %{--<div class="tituloChevere">Composición de la obra: ${obra?.descripcion}</div>--}%
            <div class="span12" style="color: #1a7031; font-size: 18px; margin-bottom: 10px"><strong>Composición de la obra:</strong> ${obra?.descripcion}</div>

            <g:if test="${flash.message}">
                <div class="span12">
                    <div class="alert ${flash.clase ?: 'alert-info'}" role="status">
                        <a class="close" data-dismiss="alert" href="#">×</a>
                        ${flash.message}
                    </div>
                </div>
            </g:if>

            <g:if test="${rend == 'screen'}">
                <div class="btn-toolbar" style="margin-top: 15px;">
                    <div class="btn-group">
                        <a href="${g.createLink(controller: 'obra', action: 'registroObra', params: [obra: obra?.id])}" class="btn " title="Regresar a la obra">
                            <i class="icon-arrow-left"></i>
                            Regresar
                        </a>
                        <g:link action="validacion" id="${obra?.id}" controller="composicion" class="btn" title="Cantidades reales de Materiales, M.O. y Equipos">
                            <i class="icon-list"></i>
                            Adm. Directa
                        </g:link>
                    </div>


                    <div class="btn-group" data-toggle="buttons-radio">
                        <g:link action="composicion" id="${obra?.id}" params="[tipo: -1, sp: spsel]" class="btn btn-info toggle pdf ${tipo.contains(',') ? 'active' : ''} -1">
                            <i class="icon-cogs"></i>
                            Todos
                        </g:link>
                        <g:link action="composicion" id="${obra?.id}" params="[tipo: 1, sp: spsel]" class="btn btn-info toggle pdf ${tipo == '1' ? 'active' : ''} 1">
                            <i class="icon-briefcase"></i>
                            Materiales
                        </g:link>
                        <g:link action="composicion" id="${obra?.id}" params="[tipo: 2, sp: spsel]" class="btn btn-info toggle pdf ${tipo == '2' ? 'active' : ''} 2">
                            <i class="icon-group"></i>
                            Mano de obra
                        </g:link>
                        <g:link action="composicion" id="${obra?.id}" params="[tipo: 3, sp: spsel]" class="btn btn-info toggle pdf ${tipo == '3' ? 'active' : ''} 3">
                            <i class="icon-truck"></i>
                            Equipos
                        </g:link>
                    </div>

                    <div class="btn-group">
                        <a class="btn btn-info dropdown-toggle" data-toggle="dropdown" href="#">
                            <g:if test="${spsel.toString() == '-1'}">
                                Todos los subpresupuestos
                            </g:if>
                            <g:else>
                                ${sp.find { it.id.toString() == spsel.toString() }.dsc}
                            </g:else>
                            <span class="caret"></span>
                        </a>
                        <ul class="dropdown-menu">
                            <li class="sp ${spsel.toString() == '-1' ? 'active' : ''}">
                                <g:link action="composicion" id="${obra?.id}" params="[tipo: tipo, sp: -1]">
                                    <g:if test="${spsel.toString() == '-1'}">
                                        <i class="icon-chevron-right"></i>
                                    </g:if>
                                    Todos los subpresupuestos
                                </g:link>
                            </li>
                            <g:each in="${sp}" var="s">
                                <li class="sp ${spsel.toString() == s.id.toString() ? 'active' : ''}">
                                    <g:link action="composicion" id="${obra?.id}" params="[tipo: tipo, sp: s.id]">
                                        <g:if test="${spsel.toString() == s.id.toString()}">
                                            <i class="icon-chevron-right"></i>
                                        </g:if>
                                        ${s.dsc}
                                    </g:link>
                                </li>
                            </g:each>
                        </ul>
                    </div>

                    <div class="btn-group">
                        %{--<g:link action="composicion" id="${obra?.id}" params="[tipo: tipo, rend: 'pdf']" class="btn btn-print btnPdf">--}%
                        %{--<i class="icon-print"></i>--}%
                        %{--Pdf--}%
                        %{--</g:link>--}%
                        %{--<g:link action="composicion" id="${obra.id}" params="[tipo: tipo, rend: 'xls']" class="btn btn-print btnExcel"> </g:link>--}%

                        <a href="#" class="btn  " id="imprimirPdf">
                            <i class="icon-print"></i>
                            PDF
                        </a>
                        <g:link controller="reportes2" action="reporteExcelComposicion" class="btn btn-print btnExcel" id="${obra?.id}" params="[sp: sub, tipo: tipo]">
                            <i class="icon-table"></i> Excel
                        </g:link>
                        <g:link controller="reportes2" action="reporteExcelComposicionTotales" class="btn btn-print btnExcel" id="${obra?.id}" params="[sp: sub, tipo: tipo]" title="Exportar a excel para definir las cantidades reales de Materiales, M.O. y Equipos">
                            <i class="icon-table"></i> Adm. Directa
                        </g:link>
                    </div>
                </div>
            </g:if>

            <div class="body">
                <table class="table table-bordered table-condensed table-hover table-striped" id="tbl">
                    <thead>
                        <tr>
                            <g:if test="${tipo.contains(",") || tipo == '1'}">
                                <th>Código</th>
                                <th>Item</th>
                                <th>U</th>
                                <th>Cantidad</th>
                                <th>P. Unitario</th>
                                <th>Transporte</th>
                                <th>Costo</th>
                                <th>Total</th>
                                <g:if test="${tipo.contains(",")}">
                                    <th>Tipo</th>
                                </g:if>
                            </g:if>
                            <g:elseif test="${tipo == '2'}">
                                <th>Código</th>
                                <th>Mano de obra</th>
                                <th>U</th>
                                <th>Horas hombre</th>
                                <th>Sal. / hora</th>
                                <th>Costo</th>
                                <th>Total</th>
                            </g:elseif>
                            <g:elseif test="${tipo == '3'}">
                                <th>Código</th>
                                <th>Equipo</th>
                                <th>U</th>
                                <th>Cantidad</th>
                                <th>Tarifa</th>
                                <th>Costo</th>
                                <th>Total</th>
                            </g:elseif>

                        </tr>
                    </thead>
                    <tbody>
                        <g:set var="totalEquipo" value="${0}"/>
                        <g:set var="totalMano" value="${0}"/>
                        <g:set var="totalMaterial" value="${0}"/>
                        <g:each in="${res}" var="r">
                            <tr>
                                <td class="">${r.codigo}</td>
                                <td class="">${r.item}</td>
                                <td>${r.unidad}</td>
                                <td class="numero">
                                    <g:formatNumber number="${r.cantidad}" minFractionDigits="3" maxFractionDigits="3" format="##,##0" locale="ec"/>
                                </td>
                                <td class="numero">
                                    <g:formatNumber number="${r.punitario}" minFractionDigits="3" maxFractionDigits="3" format="##,##0" locale="ec"/>
                                </td>
                                <g:if test="${tipo.contains(",") || tipo == '1'}">
                                    <td class="numero">
                                        <g:formatNumber number="${r.transporte}" minFractionDigits="4" maxFractionDigits="4" format="##,##0" locale="ec"/>
                                    </td>
                                </g:if>
                                <td class="numero">
                                    <g:formatNumber number="${r.costo}" minFractionDigits="4" maxFractionDigits="4" format="##,##0" locale="ec"/>
                                </td>
                                <td class="numero">
                                    <g:formatNumber number="${r?.total}" minFractionDigits="2" maxFractionDigits="2" format="##,##0" locale="ec"/>

                                    <g:if test="${r?.grid == 1}">

                                        <g:if test="${r?.total == null}">

                                            <g:set var="totalMaterial" value="${totalMaterial}"/>

                                        </g:if>
                                        <g:else>

                                            <g:set var="totalMaterial" value="${totalMaterial + r?.total}"/>
                                        </g:else>

                                    </g:if>
                                    <g:elseif test="${r?.grid == 2}">
                                        <g:if test="${r?.total == null}">
                                            <g:set var="totalMano" value="${totalMano}"/>
                                        </g:if>
                                        <g:else>
                                            <g:set var="totalMano" value="${totalMano + r?.total}"/>
                                        </g:else>
                                    </g:elseif>
                                    <g:elseif test="${r?.grid == 3}">
                                        <g:if test="${r?.total == null}">
                                            <g:set var="totalEquipo" value="${totalEquipo}"/>

                                        </g:if>
                                        <g:else>
                                            <g:set var="totalEquipo" value="${totalEquipo + r?.total}"/>

                                        </g:else>

                                    </g:elseif>

                                </td>
                                <g:if test="${tipo.contains(",")}">
                                    <td>${r?.grupo}</td>
                                </g:if>
                            </tr>
                        </g:each>
                    </tbody>
                </table>

                <div style="width:100%;">
                    <table class="table table-bordered table-condensed pull-right" style="width: 20%;">
                        <tr>
                            <th>Equipos</th>
                            <td class="numero"><g:formatNumber number="${totalEquipo}" minFractionDigits="2" maxFractionDigits="2" format="##,##0" locale="ec"/></td>
                        </tr>
                        <tr>
                            <th>Mano de obra</th>
                            <td class="numero"><g:formatNumber number="${totalMano}" minFractionDigits="2" maxFractionDigits="2" format="##,##0" locale="ec"/></td>
                        </tr>
                        <tr>
                            <th>Materiales</th>
                            <td class="numero"><g:formatNumber number="${totalMaterial}" minFractionDigits="2" maxFractionDigits="2" format="##,##0" locale="ec"/></td>
                        </tr>
                        <tr>
                            <th>TOTAL DIRECTO</th>
                            <td class="numero"><g:formatNumber number="${totalEquipo + totalMano + totalMaterial}" minFractionDigits="2" maxFractionDigits="2" format="##,##0" locale="ec"/></td>
                        </tr>
                    </table>
                </div>
            </div>
        </div>

        <g:if test="${rend == 'screen'}">
            <script type="text/javascript">
                $(function () {
                    $('#tbl').dataTable({
                        sScrollY        : "600px",
                        bPaginate       : false,
                        bScrollCollapse : true,
                        bFilter         : false,
                        bSort           : false,
                        oLanguage       : {
                            sZeroRecords : "No se encontraron datos",
                            sInfo        : "",
                            sInfoEmpty   : ""
                        }
                    });

                    $(".btn, .sp").click(function () {
                        if ($(this).hasClass("active")) {
                            return false;
                        }
                    });

                    $("#imprimirPdf").click(function () {

//                       console.log("-->" + $(".pdf.active").attr("class"))
//                       console.log("-->" + $(".pdf.active").hasClass('2'))

                        if ($(".pdf.active").hasClass("1") == true) {

                            location.href = "${g.createLink(controller: 'reportes' ,action: 'reporteComposicionMat',id: obra?.id)}?sp=${sub}"
                        } else {
                        }
                        if ($(".pdf.active").hasClass("2") == true) {
                            location.href = "${g.createLink(controller: 'reportes' ,action: 'reporteComposicionMano',id: obra?.id)}?sp=${sub}"
                        } else {

                        }
                        if ($(".pdf.active").hasClass("3") == true) {
                            location.href = "${g.createLink(controller: 'reportes' ,action: 'reporteComposicionEq',id: obra?.id)}?sp=${sub}"

                        } else {

                        }
                        if ($(".pdf.active").hasClass("-1") == true) {

                            location.href = "${g.createLink(controller: 'reportes' ,action: 'reporteComposicion',id: obra?.id)}?sp=${sub}"
                        }
                    });

                });
            </script>
        </g:if>

    </body>
</html>