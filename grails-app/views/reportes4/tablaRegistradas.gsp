
<%--
  Created by IntelliJ IDEA.
  User: fabricio
  Date: 8/20/13
  Time: 12:10 PM
--%>

<g:if test="${flash.message}">
    <div class="span12" style="height: 35px;margin-bottom: 10px; margin-left: -25px">
        <div class="alert ${flash.clase ?: 'alert-info'}" role="status" style="text-align: center">
            <a class="close" data-dismiss="alert" href="#">×</a>
            ${flash.message}
        </div>
    </div>
</g:if>


<div class="row-fluid">
    <div class="span7">

        <b>Buscar Por: </b>
        <g:select name="buscador" from="${["Codigo", "Nombre", "Tipo"]}" id="buscador_reg"/>
        <b style="margin-left: 10px">Estado: </b>
        <g:select name="estado" from="${['1':'Todas', '2':'Ingresadas', '3':'Registradas']}" optionKey="key" optionValue="value" id="estado_reg"/>

    </div>

    <div class="span12" style="margin-left: 5px">
       <b>Criterio: </b>
        <g:textField name="criterio" style="width: 600px; margin-right: 10px"/>
        <a href="#" class="btn  " id="buscar">
            <i class="icon-search"></i>
            Buscar
        </a>
        <a href="#" class="btn  " id="imprimir">
            <i class="icon-print"></i>
            Imprimir
        </a>
        <a href="#" class="btn" id="regresar">
            <i class="icon-arrow-left"></i>
            Regresar
        </a>
    </div>



</div>



<table class="table table-bordered table-striped table-condensed table-hover">
    <thead>
    <tr>
        <th style="width: 20px;">
            #
        </th>
        <th style="width: 80px;">
            Código
        </th>
        <th style="width: 200px;">
            Nombre
        </th>
        <th style="width: 80px;">
            Tipo
        </th>
        <th style="width: 80px">
            Fecha Reg
        </th>
        <th style="width: 200px">
            Cantón-Parroquia-Comunidad
        </th>
        <th style="width: 100px">
            Valor
        </th>
        <th style="width: 80px">
            Of. Salida
        </th>
        <th style="width: 80px">
            MM Salida
        </th>
        <th style="width: 100px">
            Destino
        </th>
        <th style="width: 100px">
            Elaborado
        </th>

    </tr>
    </thead>


    <tbody id="tabla_material">

    <g:each in="${obras}" var="obra" status="">

        %{--<tr class="item_row" id="${}" item="${val}" sub="${val.sbpr__id}">--}%

            %{--<td style="width: 10px" class="sel"><g:checkBox class="chec" name="selec" checked="false" id="seleccionar1" value="${val.item__id}"/></td>--}%
            %{--<td style="width: 10px" class="sel"><input type="checkbox" id="seleccionar1" class="chec" checked="false" value="${val.item__id}"></td>--}%
            %{--<td style="width: 20px" class="orden">${val.vlobordn}</td>--}%
            %{--<td style="width: 200px" class="sub">${val.sbprdscr.trim()}</td>--}%
            %{--<td class="cdgo">${val.rbrocdgo.trim()}</td>--}%
            %{--<td class="nombre">${val.rbronmbr.trim()}</td>--}%
            %{--<td style="width: 60px !important;text-align: center" class="col_unidad">${val.unddcdgo.trim()}</td>--}%
            %{--<td style="text-align: right" class="cant">--}%
                %{--<g:formatNumber number="${val.vlobcntd}" format="##,##0" minFractionDigits="2" maxFractionDigits="2" locale="ec"/>--}%
            %{--</td>--}%
            %{--<td class="col_precio" style="display: none;text-align: right" id="i_${val.item__id}"><g:formatNumber number="${val.pcun}" format="##,##0" minFractionDigits="2" maxFractionDigits="2" locale="ec"/></td>--}%
            %{--<td class="col_total total" style="display: none;text-align: right"><g:formatNumber number="${val.totl}" format="##,##0" minFractionDigits="2" maxFractionDigits="2" locale="ec"/></td>--}%
        %{--</tr>--}%

    </g:each>




    </tbody>
</table>



<script type="text/javascript">

    var checkeados = []

    $("#buscar").click(function(){

        var datos = "si=${"si"}&buscador=" + $("#buscador_reg").val() + "&estado=" + $("#estado_reg").val()
        var interval = loading("detalle")
        $.ajax({type : "POST", url : "${g.createLink(controller: 'reportes4',action:'tablaRegistradas')}",
            data     : datos,
            success  : function (msg) {
                clearInterval(interval)
                $("#detalle").html(msg)
            }
        });
    });



    $("#regresar").click(function () {

        location.href = "${g.createLink(controller: 'reportes', action: 'index')}"

    });

</script>