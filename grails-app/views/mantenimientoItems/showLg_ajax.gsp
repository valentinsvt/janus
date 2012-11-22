<div class="tituloTree">Precios de ${item.nombre} en ${lugarNombre}</div>

<div style="height: 35px; width: 100%;">
    <div class="btn-group pull-left">
        <a href="#" class="btn" id="btnNew">
            <i class="icon-money"></i>
            Nuevo Precio
        </a>
        <a href="#" class="btn" id="btnCopy">
            <i class="icon-copy"></i>
            Copiar Precios
        </a>
        <a href="#" class="btn btn-success" id="btnSave">
            <i class="icon-save"></i>
            Guardar
        </a>
    </div>
</div>

<div id="divTabla" style="height: 630px; width: 100%; overflow-x: hidden; overflow-y: auto;">
    <table class="table table-striped table-bordered table-hover table-condensed" id="tablaPrecios">
        <thead>
            <tr>
                <g:if test="${lgar}">
                    <th>Lugar</th>
                </g:if>
                <th>Fecha</th>
                <th class="precio">Precio</th>
                <th class="delete"></th>
            </tr>
        </thead>
        <tbody>
            <g:each in="${precios}" var="precio" status="i">
                <tr>
                    <g:if test="${lgar}">
                        <td>
                            ${precio.lugar.descripcion} (${precio.lugar.tipo})
                        </td>
                    </g:if>
                    <td>
                        <g:formatDate date="${precio.fecha}" format="dd-MM-yyyy"/>
                    </td>
                    <td class="precio textRight editable ${i == 0 ? 'selected' : ''}" data-original="${precio.precioUnitario}" id="${precio.id}">
                        <g:formatNumber number="${precio.precioUnitario}" maxFractionDigits="5" minFractionDigits="5"/>
                    </td>
                    <td class="delete">
                        <a href="#" class="btn btn-danger btn-small btnDelete" rel="tooltip" title="Eliminar">
                            <i class="icon-trash icon-large"></i>
                        </a>
                    </td>
                </tr>
            </g:each>
        </tbody>
    </table>
</div>

<div class="modal hide fade" id="modal">
    <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal">×</button>

        <h3 id="modalTitle"></h3>
    </div>

    <div class="modal-body" id="modalBody">
    </div>

    <div class="modal-footer" id="modalFooter">
    </div>
</div>

<script type="text/javascript">
    $('[rel=tooltip]').tooltip();

    $("#btnNew").click(function () {
        $.ajax({
            type    : "POST",
            url     : "${createLink(action:'formPrecio_ajax')}",
            success : function (msg) {
                var btnOk = $('<a href="#" data-dismiss="modal" class="btn">Cancelar</a>');
                var btnSave = $('<a href="#"  class="btn btn-success"><i class="icon-ok"></i> Guardar</a>');

                btnSave.click(function () {
                    if ($("#frmSave").valid()) {
                        btnSave.replaceWith(spinner);
                    }
                    $("#frmSave").submit();
                    return false;
                });

                $("#modalTitle").html("Crear Precio");
                $("#modalBody").html(msg);
                $("#modalFooter").html("").append(btnOk).append(btnSave);
                $("#modal-item").modal("show");
            }
        });
        return false;
    });

    $("#btnSave").click(function () {
        $("#dlgLoad").dialog("open");
        var data = "";
        $(".editable").each(function () {
            var id = $(this).attr("id");
            var valor = $(this).data("valor");

            if (parseFloat(valor) > 0 && parseFloat($(this).data("original")) != parseFloat(valor)) {
                if (data != "") {
                    data += "&";
                }
                data += "item=" + id + "_" + valor;
            }
        });
        $.ajax({
            type    : "POST",
            url     : "${createLink(action: 'actualizarPrecios_ajax')}",
            data    : data,
            success : function (msg) {
                $("#dlgLoad").dialog("close");
                var parts = msg.split("_");
                var ok = parts[0];
                var no = parts[1];
                $(ok).addClass("ok", 1000, function () {
                    $(ok).removeClass("ok", 1000);
                });
                $(no).addClass("no", 1000, function () {
                    $(no).removeClass("no");
                });
            }
        });
        return false;
    }); //btnSave

    $(".btnDelete").click(function () {
        var btnOk = $('<a href="#" data-dismiss="modal" class="btn">Cancelar</a>');
        var btnSave = $('<a href="#"  class="btn btn-danger"><i class="icon-trash"></i> Eliminar</a>');

        btnSave.click(function () {
            btnSave.replaceWith(spinner);
            $.ajax({
                type    : "POST",
                url     : "${createLink(action: 'deletePrecio_ajax')}",
                data    : {
                    name     : "John",
                    location : "Boston"
                },
                success : function (msg) {
                    alert("Data Saved: " + msg);
                }
            });
            return false;
        });

        $("#modalTitle").html("Confirmación");
        $("#modalBody").html("Está seguro de querer eliminar este precio?");
        $("#modalFooter").html("").append(btnOk).append(btnSave);
        $("#modal").modal("show");
        return false;
    });

</script>
<script type="text/javascript" src="${resource(dir: 'js', file: 'tableHandler.js')}"></script>