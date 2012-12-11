<div class="row-fluid" style="margin-left: 0px">
    <div class="span5">
        <b>Subpresupuesto:</b>
        <g:select name="subpresupuesto" from="${subPres}" optionKey="id" optionValue="descripcion" style="width: 300px;font-size: 10px" id="subPres_desc" value="${subPre}"></g:select>
    </div>
    <div class="span1">
        <div class="btn-group" data-toggle="buttons-checkbox">
            <button type="button" id="ver_todos" class="btn btn-info ${(!subPre)?'active':''} " style="font-size: 10px">Ver todos</button>

        </div>

    </div>
    <div class="span4">
        <a href="#" class="btn  " id="imprimir_sub">
            <i class="icon-file"></i>
            Imprimir Subpresupuesto
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
        <th style="width: 600px;">
            Rubro
        </th>
        <th style="width: 60px" class="col_unidad">
            Unidad
        </th>
        <th style="width: 80px">
            Cantidad
        </th>
        <th class="col_precio" style="display: none;">Unitario</th>
        <th class="col_total" style="display: none;">C.Total</th>
        <th style="width: 40px" class="col_delete"></th>
    </tr>
    </thead>
    <tbody id="tabla_material">

    <g:each in="${detalle}" var="vol" status="i">

        <tr class="item_row" id="${vol.id}" item="${vol.item.id}" sub="${vol.subPresupuesto.id}">
            <td style="width: 20px" class="orden">${vol.orden}</td>
            <td class="cdgo">${vol.item.codigo}</td>
            <td class="nombre">${vol.item.nombre}</td>
            <td style="width: 60px !important;text-align: center" class="col_unidad">${vol.item.unidad.codigo}</td>
            <td style="text-align: right" class="cant">
                <g:formatNumber number="${vol.cantidad}" format="##,##0" minFractionDigits="2" maxFractionDigits="2" locale="ec"/>
            </td>
            <td class="col_precio" style="display: none;text-align: right" id="i_${vol.item.id}"><g:formatNumber number="${precios[vol.id.toString()]}" format="##,##0" minFractionDigits="2" maxFractionDigits="2" locale="ec"/></td>
            <td class="col_total total" style="display: none;text-align: right"><g:formatNumber number="${precios[vol.id.toString()]*vol.cantidad}" format="##,##0" minFractionDigits="2" maxFractionDigits="2" locale="ec"/></td>
            <td style="width: 40px;text-align: center" class="col_delete">
                <a class="btn btn-small btn-danger borrarItem" href="#" rel="tooltip" title="Eliminar" iden="${vol.id}">
                    <i class="icon-trash"></i></a>
            </td>
        </tr>

    </g:each>
    </tbody>
</table>
<script type="text/javascript">

    $.contextMenu({
        selector: '.item_row',
        callback: function(key, options) {
            if(key=="edit"){
                $(this).dblclick()
            }
            if(key="print"){
                var dsps=${obra.distanciaPeso}
                var dsvs=${obra.distanciaVolumen}
                var volqueta=${precioVol}
                var chofer=${precioChof}
                %{--var datos = "?dsps="+dsps+"&dsvs="+dsvs+"&prvl="+volqueta+"&prch="+chofer+"&fecha="+$("#fecha_precios").val()+"&id=${rubro?.id}&lugar="+$("#ciudad").val()--}%
                %{--location.href="${g.createLink(controller: 'reportes3',action: 'imprimirRubro')}"+datos--}%
                var datos = "?dsps="+dsps+"Wdsvs="+dsvs+"Wprvl="+volqueta+"Wprch="+chofer+"Wfecha=${obra.fechaPreciosRubros.format('dd-MM-yyyy')}Wid="+$(this).attr("item")+"Wlugar=${obra.lugar.id}Windi=${indirectos}"
                var url = "${g.createLink(controller: 'reportes3',action: 'imprimirRubro')}"+datos
                location.href="${g.createLink(controller: 'pdf',action: 'pdfLink')}?url="+url
            }
        },
        items: {
            "edit": {name: "Editar", icon: "edit"},
            "print": {name: "Imprimir", icon: "print"}
        }
    });

    $("#imprimir_sub").click(function(){
        var dsps=${obra.distanciaPeso}
        var dsvs=${obra.distanciaVolumen}
        var volqueta=${precioVol}
        var chofer=${precioChof}
        %{--var datos = "?dsps="+dsps+"&dsvs="+dsvs+"&prvl="+volqueta+"&prch="+chofer+"&fecha="+$("#fecha_precios").val()+"&id=${rubro?.id}&lugar="+$("#ciudad").val()--}%
        %{--location.href="${g.createLink(controller: 'reportes3',action: 'imprimirRubro')}"+datos--}%
        var datos = "?obra=${obra.id}Wsub="+$("#subPres_desc").val()
        var url = "${g.createLink(controller: 'reportes3',action: 'imprimirTablaSub')}"+datos
        location.href="${g.createLink(controller: 'pdf',action: 'pdfLink')}?url="+url
    });

    $("#subPres_desc").change(function(){
        $("#ver_todos").removeClass("active")
        $("#divTotal").html("")
        $("#calcular").removeClass("active")
        var datos = "obra=${obra.id}&sub="+$("#subPres_desc").val()
        $.ajax({type : "POST", url : "${g.createLink(controller: 'volumenObra',action:'tabla')}",
            data     : datos,
            success  : function (msg) {
                $("#detalle").html(msg)
            }
        });
    });

    $("#ver_todos").click(function(){
        $("#calcular").removeClass("active")
        $("#divTotal").html("")
        if ($(this).hasClass("active")) {
            $(this).removeClass("active")
            var datos = "obra=${obra.id}&sub="+$("#subPres_desc").val()
            $.ajax({type : "POST", url : "${g.createLink(controller: 'volumenObra',action:'tabla')}",
                data     : datos,
                success  : function (msg) {
                    $("#detalle").html(msg)
                }
            });

        }else{
            $(this).addClass("active")
            var datos = "obra=${obra.id}"
            $.ajax({type : "POST", url : "${g.createLink(controller: 'volumenObra',action:'tabla')}",
                data     : datos,
                success  : function (msg) {
                    $("#detalle").html(msg)
                }
            });
        }
        return false

    }) ;


    $(".item_row").dblclick(function(){
        $("#calcular").removeClass("active")
        $(".col_delete").show()
        $(".col_precio").hide()
        $(".col_total").hide()
        $("#divTotal").html("")
        $("#vol_id").val($(this).attr("id"))
        $("#item_codigo").val($(this).find(".cdgo").html())
        $("#item_id").val($(this).attr("item"))
        $("#subPres").val($(this).attr("sub"))
        $("#item_nombre").val($(this).find(".nombre").html())
        $("#item_cantidad").val($(this).find(".cant").html().toString().trim())
        $("#item_orden").val($(this).find(".orden").html() )

    });
    $(".borrarItem").click(function(){
        if(confirm("Esta seguro de eliminar el rubro?")){
            $.ajax({type : "POST", url : "${g.createLink(controller: 'volumenObra',action:'eliminarRubro')}",
                data     : "id=" + $(this).attr("iden"),
                success  : function (msg) {
                    $("#detalle").html(msg)

                }
            });
        }
    });
</script>