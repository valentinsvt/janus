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

        <tr class="item_row" id="${vol.id}">
            <td style="width: 20px">${vol.orden}</td>
            <td class="cdgo">${vol.item.codigo}</td>
            <td>${vol.item.nombre}</td>
            <td style="width: 60px !important;text-align: center" class="col_unidad">${vol.item.unidad.codigo}</td>
            <td style="text-align: right" class="cant">
                <g:formatNumber number="${vol.cantidad}" format="##,#####0" minFractionDigits="5" maxFractionDigits="7"/>
            </td>
            <td class="col_precio" style="display: none;text-align: right" id="i_${vol.item.id}"><g:formatNumber number="${precios[vol.id.toString()]}" format="##,#####0" minFractionDigits="5" maxFractionDigits="7"/></td>
            <td class="col_total total" style="display: none;text-align: right"><g:formatNumber number="${precios[vol.id.toString()]*vol.cantidad}" format="##,#####0" minFractionDigits="5" maxFractionDigits="7"/></td>
            <td style="width: 40px;text-align: center" class="col_delete">
                <a class="btn btn-small btn-danger borrarItem" href="#" rel="tooltip" title="Eliminar" iden="${vol.id}">
                    <i class="icon-trash"></i></a>
            </td>
        </tr>

    </g:each>
    </tbody>
</table>
<script type="text/javascript">
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