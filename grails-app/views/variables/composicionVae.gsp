<%@ page contentType="text/html;charset=UTF-8" %>
<html>
    <head>
            <meta name="layout" content="main">
            <script src="${resource(dir: 'js/jquery/plugins/DataTables-1.9.4/media/js', file: 'jquery.dataTables.min.js')}"></script>
            <link href="${resource(dir: 'js/jquery/plugins/DataTables-1.9.4/media/css', file: 'jquery.dataTables.css')}" rel="stylesheet">

        <script type="text/javascript" src="${resource(dir: 'js', file: 'tableHandlerBody.js')}"></script>
            <link rel="stylesheet" href="${resource(dir: 'css', file: 'tableHandler.css')}"/>

        <title>Composición de la obra</title>

        <style type="text/css">

        .editable {
            background    : url(${resource(dir:'images', file:'edit.gif')}) right no-repeat;
            padding-right : 18px !important;
        }


        </style>


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

                    <div class="btn-group">
                        <a href="${g.createLink(controller: 'obra', action: 'registroObra', params: [obra: obra?.id])}" class="btn btn-success" title="Regresar a la obra">
                            <i class="icon-save"></i>
                            Guardar
                        </a>
                    </div>

                </div>
            </g:if>

            <div class="body">
                <table class="table table-bordered table-condensed table-hover table-striped" id="tbl">
                    <thead>
                        <tr>
                            <g:if test="${tipo.contains(",") || tipo == '1'}">
                                <th>Código</th>
                                <th width="500px">Item</th>
                                <th>U</th>
                                <th>Cantidad</th>
                                <th>P. Unitario</th>
                                <th>Transporte</th>
                                <th>Costo</th>
                                <th>Total</th>
                                <g:if test="${tipo.contains(",")}">
                                    <th>Tipo</th>
                                </g:if>
                                <th>VAE (%)</th>
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
                        <g:set var="sumaVaeEq" value="${0}"/>                               sumaVaeMt/totalMaterial*100
                        <g:set var="sumaVaeMt" value="${0}"/>
                        <g:set var="sumaVaeMo" value="${0}"/>
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
                                            <g:set var="sumaVaeMt" value="${sumaVaeMt}"/>

                                        </g:if>
                                        <g:else>

                                            <g:set var="totalMaterial" value="${totalMaterial + r?.total}"/>
                                            <g:set var="sumaVaeMt" value="${sumaVaeMt + r?.total * r?.tpbnpcnt/100}"/>
                                        </g:else>

                                    </g:if>
                                    <g:elseif test="${r?.grid == 2}">
                                        <g:if test="${r?.total == null}">
                                            <g:set var="totalMano" value="${totalMano}"/>
                                        </g:if>
                                        <g:else>
                                            <g:set var="totalMano" value="${totalMano + r?.total}"/>
                                            <g:set var="sumaVaeMo" value="${sumaVaeMo + r?.total * r?.tpbnpcnt/100}"/>
                                        </g:else>
                                    </g:elseif>
                                    <g:elseif test="${r?.grid == 3}">
                                        <g:if test="${r?.total == null}">
                                            <g:set var="totalEquipo" value="${totalEquipo}"/>

                                        </g:if>
                                        <g:else>
                                            <g:set var="totalEquipo" value="${totalEquipo + r?.total}"/>
                                            <g:set var="sumaVaeEq" value="${sumaVaeEq + r?.total * r?.tpbnpcnt/100}"/>

                                        </g:else>

                                    </g:elseif>


                                </td>
                                <g:if test="${tipo.contains(",")}">
                                    <td>${r?.grupo}</td>
                                </g:if>
                                <td class="editable numero cantidad texto" data-original="${r?.tpbnpcnt}"
                                    data-id="${r?.voit__id}" data-valor='00'> ${r?.tpbnpcnt}
                                    %{--<g:formatNumber number="${r?.tpbnpcnt}" minFractionDigits="2" maxFractionDigits="2" format="###,#0" locale="ec"/>--}%
                                </td>
                            </tr>
                        </g:each>
                    </tbody>
                </table>

                <div style="width:100%;">
                    <table class="table table-bordered table-condensed pull-right" style="width: 20%;">
                        <tr>
                            <th>Equipos</th>
                            <td class="numero"><g:formatNumber number="${totalEquipo}" minFractionDigits="2" maxFractionDigits="2" format="##,##0" locale="ec"/></td>
                            <td class="numero"><g:formatNumber number="${sumaVaeEq/totalEquipo*100}" minFractionDigits="2" maxFractionDigits="2" format="##,##0" locale="ec"/>%</td>
                        </tr>
                        <tr>
                            <th>Mano de obra</th>
                            <td class="numero"><g:formatNumber number="${totalMano}" minFractionDigits="2" maxFractionDigits="2" format="##,##0" locale="ec"/></td>
                            <td class="numero"><g:formatNumber number="${sumaVaeMo/totalMano*100}" minFractionDigits="2" maxFractionDigits="2" format="##,##0" locale="ec"/>%</td>
                        </tr>
                        <tr>
                            <th>Materiales</th>
                            <td class="numero"><g:formatNumber number="${totalMaterial}" minFractionDigits="2" maxFractionDigits="2" format="##,##0" locale="ec"/></td>
                            <td class="numero"><g:formatNumber number="${sumaVaeMt/totalMaterial*100}" minFractionDigits="2" maxFractionDigits="2" format="##,##0" locale="ec"/>%</td>
                        </tr>
                        <tr>
                            <th>TOTAL OBRA</th>
                            <td class="numero"><g:formatNumber number="${totalEquipo + totalMano + totalMaterial}" minFractionDigits="2" maxFractionDigits="2" format="##,##0" locale="ec"/></td>
                            <td class="numero"><g:formatNumber number="${(sumaVaeEq/totalEquipo + sumaVaeMo/totalMano + sumaVaeMt/totalMaterial)/3*100}" minFractionDigits="2" maxFractionDigits="2" format="##,##0" locale="ec"/>%</td>
                        </tr>
                    </table>
                </div>

                <input type='text' id='txt' style='height:20px;width:110px;margin: 0px;padding: 0px;padding-right:2px;text-align: right !important;display: none;margin-left: 0px;margin-right: 0px;'>
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


                    function stopEditing() {
//                        console.log("stop editing txt", $("#txt"), "val=",$("#txt").val());
                        var valor = $("#txt").val();
                        $("#txt").val("");
                        $("#txt").hide();
                        var padre = $("td.editando");
                        console.log("padre: ",padre);
//                        $("#totales").append($("#txt"));
                        padre.addClass("changed");
                        padre.html(valor);
                        padre.addClass("texto");
                        padre.removeClass("editando");
                    }

                    var txt = $("#txt")
                    txt.keyup(function(ev) {
                        console.log('key:' , ev.keyCode)
                        if(ev.keyCode==13) {
                            stopEditing();
                        }
                    });

                    $(".cantidad").click(function () {
                        console.log("click", $(this));
                        if ($(this).hasClass("texto")) {
                            stopEditing();
                            txt.width($(this).innerWidth() - 25);
                            var valor = $(this).html().trim();
                            console.log("...." + valor);
                            $(this).html("");
                            txt.val(valor);
                            $(this).append(txt);
                            txt.show();
                            $(this).removeClass("texto");
                            txt.focus();
                            $(this).addClass("editando");
                        }
                    });

                });
            </script>
        </g:if>

    </body>
</html>