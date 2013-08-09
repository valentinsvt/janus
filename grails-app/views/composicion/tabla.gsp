<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <meta name="layout" content="main">
    <script src="${resource(dir: 'js/jquery/plugins/DataTables-1.9.4/media/js', file: 'jquery.dataTables.min.js')}"></script>
    <link href="${resource(dir: 'js/jquery/plugins/DataTables-1.9.4/media/css', file: 'jquery.dataTables.css')}" rel="stylesheet">

    <title>Composición de la obra</title>
</head>

<body>
<div class="hoja">
    <div class="tituloChevere">Composición de ${obra?.descripcion}</div>

    <g:if test="${flash.message}">
        <div class="span12">
            <div class="alert ${flash.clase ?: 'alert-info'}" role="status">
                <a class="close" data-dismiss="alert" href="#">×</a>
                ${flash.message}
            </div>
        </div>
    </g:if>

    <div class="btn-toolbar" style="margin-top: 15px;">
        <div class="btn-group">
            <a href="${g.createLink(controller: 'obra', action: 'registroObra', params: [obra: obra?.id])}" class="btn " title="Regresar a la obra" >
                <i class="icon-arrow-left"></i>
                Regresar
            </a>
        </div>

        <div class="btn-group">
            <a href="#" class="btn btn-primary " title="Guardar" id="guardar">
                <i class="icon-save"></i>
                Guardar
            </a>
        </div>

        <div class="btn-group" data-toggle="buttons-radio">
            <g:link action="tabla" id="${obra?.id}" params="[tipo: -1]" class="btn btn-info toggle pdf ${tipo.contains(',') ? 'active' : ''} -1">
                <i class="icon-cogs"></i>
                Todos
            </g:link>
            <g:link action="tabla" id="${obra?.id}" params="[tipo: 1]" class="btn btn-info toggle pdf ${tipo == '1' ? 'active' : ''} 1">
                <i class="icon-briefcase"></i>
                Materiales
            </g:link>
            <g:link action="tabla" id="${obra?.id}" params="[tipo: 2]" class="btn btn-info toggle pdf ${tipo == '2' ? 'active' : ''} 2">
                <i class="icon-group"></i>
                Mano de obra
            </g:link>
            <g:link action="tabla" id="${obra?.id}" params="[tipo: 3]" class="btn btn-info toggle pdf ${tipo == '3' ? 'active' : ''} 3">
                <i class="icon-truck"></i>
                Equipos
            </g:link>
        </div>

    </div>


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
            <g:each in="${res}" var="r" >
                <tr>
                    <td class="">${r.item.codigo}</td>
                    <td class="">${r.item.nombre}</td>
                    <td>${r.item.unidad.codigo}</td>
                    <td class="numero cantidad texto " iden="${r.id}">
                        <g:formatNumber number="${r.cantidad}" minFractionDigits="2" maxFractionDigits="7" format="##,#######0" locale="ec"/>
                    </td>
                    <td class="numero ${r.id}">
                        <g:formatNumber number="${r.precio}" minFractionDigits="3" maxFractionDigits="3" format="##,##0" locale="ec"/>
                    </td>
                    <g:if test="${tipo.contains(",") || tipo == '1'}">
                        <td class="numero ${r.id}">
                            <g:formatNumber number="${r.transporte}" minFractionDigits="4" maxFractionDigits="4" format="##,##0" locale="ec"/>
                        </td>
                    </g:if>
                    <td class="numero ${r.id}">
                        <g:formatNumber number="${r.transporte+r.precio}" minFractionDigits="4" maxFractionDigits="4" format="##,##0" locale="ec"/>
                    </td>
                    <td class="numero ${r.id} total">
                        <g:formatNumber number="${(r.transporte+r.precio)*r.cantidad}" minFractionDigits="2" maxFractionDigits="2" format="##,##0" locale="ec"/>

                        <g:if test="${r?.grupo.id == 1}">
                            <g:set var="totalMaterial" value="${totalMaterial + ((r.transporte+r.precio)*r.cantidad)}"/>

                        </g:if>
                        <g:elseif test="${r.grupo.id == 2}">
                            <g:set var="totalMano" value="${totalMano + ((r.transporte+r.precio)*r.cantidad)}"/>

                        </g:elseif>
                        <g:elseif test="${r.grupo.id == 3}">
                            <g:set var="totalEquipo" value="${totalEquipo + ((r.transporte+r.precio)*r.cantidad)}"/>
                        </g:elseif>

                    </td>
                    <g:if test="${tipo.contains(",")}">
                        <td>${r?.grupo}</td>
                    </g:if>
                </tr>
            </g:each>
            </tbody>
        </table>

        <div id="totales" style="width:100%;">
            <input type='text' id='txt' style='height:20px;width:110px;margin: 0px;padding: 0px;padding-right:2px;text-align: right !important;display: none;margin-left: 0px;margin-right: 0px;'>
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


<script type="text/javascript">

    $(function () {
        $('#tbl').dataTable({
            sScrollY        : "600px",
            bPaginate       : false,
            bScrollCollapse : true,
            bFilter         : false,
            bSort: false,
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

        $("#guardar").click(function(){
//            console.log("guardar")
            var data="data="
            $(".changed").each(function(){
//                console.log($(this))
                data+=$(this).attr("iden")+"I"+$(this).html()+"X"
            });
//            console.log(data)
            $.ajax({
                type    : "POST",
                url     : "${g.createLink(controller: 'composicion',action:'save')}",
                data    : data,
                success : function (msg) {
                    if(msg=="ok")
                        window.location.reload(true)
                }
            });

        });

        $("#txt").keyup(function(event){
//            console.log(event.which)
            if (event.which == 13) {
                var valor=$(this).val()
                $(this).val("")
                $(this).hide()
                var padre =   $(this).parent()
                $("#totales").append($(this))
                padre.html(valor)
                padre.addClass("changed")
                padre.addClass("texto")
            }
            if(event.which==40 ){
                var valor=$(this).val()
                $(this).val("")
                $(this).hide()
                var padre =   $(this).parent()
                $("#totales").append($(this))
                padre.html(valor)
                padre.addClass("texto")
                padre.addClass("changed")
//                console.log(padre.parent(),padre.parent(),padre.parent().next())
                padre.parent().next().find(".cantidad").click()
            }
            if(event.which==38 ){
                var valor=$(this).val()
                $(this).val("")
                $(this).hide()
                var padre =   $(this).parent()
                $("#totales").append($(this))
                padre.html(valor)
                padre.addClass("changed")
                padre.addClass("texto")
//                console.log(padre.parent(),padre.parent(),padre.parent().next())
                padre.parent().prev().find(".cantidad").click()
            }

        })
        $("#txt").blur(function(){
            var valor=$(this).val()
            $(this).val("")
            $(this).hide()
            var padre =   $(this).parent()
            $("#totales").append($(this))
            padre.addClass("changed")
            padre.html(valor)
            padre.addClass("texto")
        })
        var txt =$("#txt")
        $(".cantidad").click(function(){
            if($(this).hasClass("texto")){
                txt.width($(this).innerWidth()-25)
                var valor = $(this).html().trim()
                $(this).html("")
                txt.val(valor)
                $(this).append(txt)
                txt.show()
                $(this).removeClass("texto")
                txt.focus()
            }

        });


        $("#imprimirPdf").click(function () {

//                       console.log("-->" + $(".pdf.active").attr("class"))
//                       console.log("-->" + $(".pdf.active").hasClass('2'))

            if($(".pdf.active").hasClass("1") == true){

                location.href = "${g.createLink(controller: 'reportes' ,action: 'reporteComposicionMat',id: obra?.id)}?sp=${sub}"
            }else {
            }
            if($(".pdf.active").hasClass("2") == true){
                location.href = "${g.createLink(controller: 'reportes' ,action: 'reporteComposicionMano',id: obra?.id)}?sp=${sub}"
            }else {


            }
            if($(".pdf.active").hasClass("3") == true){
                location.href = "${g.createLink(controller: 'reportes' ,action: 'reporteComposicionEq',id: obra?.id)}?sp=${sub}"

            }else {


            }
            if($(".pdf.active").hasClass("-1") == true){


                location.href = "${g.createLink(controller: 'reportes' ,action: 'reporteComposicion',id: obra?.id)}?sp=${sub}"
            }
        });

    });
</script>


</body>
</html>