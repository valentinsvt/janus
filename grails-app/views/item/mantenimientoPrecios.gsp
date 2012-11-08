<%@ page contentType="text/html;charset=UTF-8" %>
<html>

<head>

    <meta name="layout" content="main">
    <title>Mantenimiento de Precios</title>

    <script src="${resource(dir: 'js/jquery/plugins/DataTables-1.9.4/media/js', file: 'jquery.dataTables.min.js')}"></script>

    <script src="${resource(dir: 'js/jquery/plugins/DataTables-1.9.4/media/js', file: 'jquery.jeditable.js')}"></script>

    <link rel="stylesheet"
          href="${resource(dir: 'js/jquery/plugins/DataTables-1.9.4/media/css', file: 'jquery.dataTables.css')}"/>
    <script src="${resource(dir: 'js/jquery/plugins/DataTables-1.9.4/extras/KeyTable/js', file: 'KeyTable.min.js')}"></script>


    <style type="text/css">


    .selected {

        border: solid 2px blue !important;

    }


    </style>

</head>

<body>

<fieldset class="borde">

    <div class="span12">

        <div class="span2" align="center">Lista de Precios</div>

        <div class="span1" style="margin-left: 130px" align="center">Fecha</div>

        <div class="span1" align="center" style="margin-left: 40px">Todos</div>

    </div>

    <div class="span12">

        <div class="span2" align="center"><g:select class="precioRubro" name="precioRubro"
                                                    from="${janus.Lugar.list([sort: "id"])}"
                                                    noSelection="['': 'Seleccione']" optionKey="id"
                                                    optionValue="descripcion" style="width: 270px"/></div>

        <div class="span1" align="center" style="margin-left: 130px"><elm:datepicker name="fecha" class="datepicker"
                                                                                     style="width: 90px"/></div>

        <div class="span1" align="center" style="margin-left: 40px"><g:checkBox name="todosPrecios"
                                                                                checked="false"/></div>

        <div class="btn-group span1">
            <a href="#" class="btn btn-consultar"><i class="icon-search"></i>Consultar</a>
            <a href="#" class="btn btn-generar"><i class="icon-edit"></i>Generar Nuevos Precios</a>
            <a href="#" class="btn btn-cargar"><i class="icon-edit"></i> Cargar Precios a la Fecha</a>
            <a href="#" class="btn btn-actualizar"><i class="icon-refresh"></i>Actualizar</a>
        </div>
    </div>

</fieldset>


<fieldset class="borde">

    <div class="span12">

        <table class="table table-bordered table-striped table-hover table-condensed" id="tablaPrecios">
            <thead style="background-color:#0074cc;">

            <th>Item</th>
            <th>Nombre del Item</th>
            <th>U</th>
            <th>Precio</th>
            <th class="precioAL hidden">Precio Anterior</th>
            <th>Fecha</th>

            </thead>
            <tbody>

            <g:each in="${rubroPrecio}" var="rubro" status="i">

                <tr>

                    <td class="itemId" align="center" style="width: 150px; color: #009926;">

                        ${rubro?.item?.id}

                    </td>

                    <td class="itemNombre" align="center">

                        ${rubro?.item?.nombre}

                    </td>

                    <td class="unidad" align="center" style="width: 150px">

                        ${rubro?.item?.unidad?.descripcion}

                    </td>
                    <td class="editable ${i == 0 ? 'selected' : ''}" id="${rubro?.item?.id}" align="center"
                        data-original="${rubro?.precioUnitario}"
                        style="width: 150px">

                        ${rubro?.precioUnitario}

                    </td>

                    <td class="precioAnterior hidden" align="center" style="width: 105px">
                        0.00
                    </td>

                    <td class="fecha" align="center" style="width: 150px">

                        <g:formatDate date="${rubro?.fecha}" format="dd-MM-yyyy"/>

                    </td>

                </tr>

            </g:each>

            </tbody>

        </table>

    </div>

</fieldset>

<script type="text/javascript">

    function doEdit(sel) {

        var texto = $.trim(sel.text());
        var w = sel.width();
        textField = $('<input type="text" class="editando" value="' + texto + '"/>');
        textField.width(w - 5);
        sel.html(textField);
        textField.focus();
        sel.data("valor", texto);

    }

    function stopEdit() {

        //var value = $(".editando").val(); //valor del texfield (despues de editar)
        var value = $(".selected").data("valor"); //valor antes de la edicion
        if (value) {


            $(".selected").html(number_format(value, 2, ".", ""));

        }
    }

    function seleccionar(elm) {
        deseleccionar($(".selected"));
        elm.addClass("selected");
    }

    function deseleccionar(elm) {
        stopEdit();
        elm.removeClass("selected");
    }

    $(function () {

        $(".btn-consultar").click(function () {


        });


        $(".btn-generar").click(function () {

            $(".precioAL").show();
            $(".precioAnterior").show();


        });

        $(".btn-actualizar").click(function () {

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
            console.log(data);

        });

        $(".editable").click(function (ev) {
            if ($(ev.target).hasClass("editable")) {
                seleccionar($(this));
            }
        });

        $(".editable").dblclick(function (ev) {


            if ($(ev.target).hasClass("editable")) {
                seleccionar($(this));
                doEdit($(this));
            }
        });

        $(document).keyup(function (ev) {
            var sel = $(".selected");
            var celdaIndex = sel.index();
            var tr = sel.parent();
            var filaIndex = tr.index();
            var ntr;

            var textField;

            switch (ev.keyCode) {
                case 38: //arriba
                    if (filaIndex > 0) {
                        ntr = tr.prev();
                        seleccionar(ntr.children().eq(celdaIndex));
                    }
                    break;
                case 40: //abajo
                    var cant = $('#tablaPrecios > tbody > tr').size();
                    if (filaIndex < cant - 1) {
                        ntr = tr.next();
                        seleccionar(ntr.children().eq(celdaIndex));
                    }
                    break;
                case 13: //enter
                    var target = $(ev.target);

                    if (target.hasClass("editando")) {


                        var value = target.val();

                        $(".selected").html(number_format(value, 2, ".", ""));
                        sel.data("valor", value);

                    }

                    else {


                        doEdit(sel);

                    }
                    break;


                case 27: //esc

                    stopEdit();

                    break;

                default:

                    return true;
            }
        })
    });




</script>
</body>
</html>