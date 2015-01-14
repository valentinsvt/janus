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

            thead tr {
                margin : 0px
            }

            th, td {
                font-size : 10px !important;

            }

            .tituloChevere {
                color       : #0088CC;
                border      : 0px solid red;
                white-space : nowrap;
                display     : block;
                width       : 98%;
                height      : 30px;
                font-weight : bold;
                font-size   : 22px;
                text-shadow : -2px 2px 1px rgba(0, 0, 0, 0.25);
                margin-top  : 10px;
                line-height : 22px;
            }

            </style>
        </g:if>

    </head>

    <body>
        <div class="hoja">
            <div class="tituloChevere">Valores del VAE, para la Obra: ${obra?.descripcion}</div>

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
                    </div>


                    <div class="btn-group" data-toggle="buttons-radio">
                        <g:link action="composicionVae" id="${obra?.id}" params="[tipo: -1, sp: spsel]" class="btn btn-info toggle pdf ${tipo.contains(',') ? 'active' : ''} -1">
                            <i class="icon-cogs"></i>
                            Todos
                        </g:link>
                        <g:link action="composicionVae" id="${obra?.id}" params="[tipo: 1, sp: spsel]" class="btn btn-info toggle pdf ${tipo == '1' ? 'active' : ''} 1">
                            <i class="icon-briefcase"></i>
                            Materiales
                        </g:link>
                        <g:link action="composicionVae" id="${obra?.id}" params="[tipo: 2, sp: spsel]" class="btn btn-info toggle pdf ${tipo == '2' ? 'active' : ''} 2">
                            <i class="icon-group"></i>
                            Mano de obra
                        </g:link>
                        <g:link action="composicionVae" id="${obra?.id}" params="[tipo: 3, sp: spsel]" class="btn btn-info toggle pdf ${tipo == '3' ? 'active' : ''} 3">
                            <i class="icon-truck"></i>
                            Equipos
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
                                <th>VAE (%)</th>
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
                                <th>VAE (%)</th>
                            </g:elseif>
                            <g:elseif test="${tipo == '3'}">
                                <th>Código</th>
                                <th>Equipo</th>
                                <th>U</th>
                                <th>Cantidad</th>
                                <th>Tarifa</th>
                                <th>Costo</th>
                                <th>Total</th>
                                <th>VAE (%)</th>
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
                                <td class="numero cantidad texto " iden="${r.codigo}">
                                    <g:formatNumber number="${r.cantidad}" minFractionDigits="2" maxFractionDigits="7" format="##,##0" locale="ec"/>
                                </td>
                            </tr>
                        </g:each>
                    </tbody>
                </table>
                <input type='text' id='txt' style='height:20px;width:110px;margin: 0px;padding: 0px;padding-right:2px;text-align: right !important;display: none;margin-left: 0px;margin-right: 0px;'>
            </div>
        </div>

        <g:if test="${rend == 'screen'}">
            <script type="text/javascript">
                $(function () {

                    function updateTotal(celda) {
                        if(celda.parent().hasClass("precio")){
                            var val = celda.val()
                            val = val.replace(",", "")
                            var cant = celda.parent().parent().find(".cantidad").html()
                            cant = cant.replace(",", "")
                            celda.parent().parent().find(".total").html(number_format(cant * val, 2, ".", ""))
                        }else{
                            var val = celda.val()
                            val = val.replace(",", "")
                            var precio = celda.parent().parent().find(".precio").html()
                            precio = precio.replace(",", "")
                            celda.parent().parent().find(".total").html(number_format(precio * val, 2, ".", ""))
                        }
                    }

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

                    $("#txt").blur(function () {
//            console.log("blur")
                        if ($("#txt").val() != "") {
                            updateTotal($(this))
                            var valor = $(this).val()
                            $(this).val("")
                            $(this).hide()
                            var padre = $(this).parent()
                            $("#totales").append($(this))
                            padre.addClass("changed")
                            padre.html(valor)
                            if(padre.hasClass("precio"))
                                padre.addClass("textoPrecio")
                            else
                                padre.addClass("texto")
                        }
                    })

                    var txt = $("#txt")
                    $(".cantidad").click(function () {
                        if ($(this).hasClass("texto")) {
                            txt.width($(this).innerWidth() - 25)
                            var valor = $(this).html().trim()
                            $(this).html("")
                            txt.val(valor)
                            $(this).append(txt)
                            txt.show()
                            $(this).removeClass("texto")
                            txt.focus()
                        }

                    });

                });
            </script>
        </g:if>

    </body>
</html>