<html>

<head>

    <style type="text/css">


    .selected {

        border: solid 2px blue !important;

    }


    </style>

</head>

<body>

<div class="span12" id="tabla">

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

            <td class="itemId" align="center" style="width: 150px;">

                ${rubro?.item?.codigo}

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

%{--MAX: ${params.max}<br/>--}%
%{--OFFSET: ${params.offset}<br/>--}%
%{--PAG: ${params.pag}<br/>--}%
%{--TOTAL ROWS: ${params.totalRows}<br/>--}%
%{--TOTAL PAGS: ${params.totalPags}<br/>--}%
%{--1st PAG: ${params.first}<br/>--}%
%{--LAST: ${params.last}<br/>--}%

<div>

    <g:if test="${params.totalPags == 0}">

        <div class="alert alert-error">

            <h4 style="margin-left: 450px">No existen datos!!</h4>

            <div style="margin-left: 420px">
                Ingrese los parámetros de búsqueda!

            </div>
        </div>

    </g:if>

    <g:else>

        <div class="pagination pagination-centered" style="margin-bottom: 40px">
            <div>
                Página: ${params.pag} de <g:formatNumber number="${params.totalPags}" minFractionDigits="0"/>
            </div>

            <ul>
                <li class="${params.pag == 1 ? 'disabled' : ''}">
                    <a href="${1}" class="num ">
                        <i class="icon-step-backward"></i>
                    </a>
                </li>
                <g:if test="${params.pag - params.first > 0}">
                    <li class="">
                        <a href="${params.pag - 1}" class="num">
                            <i class="icon-backward"></i>
                        </a>
                    </li>
                </g:if>
                <g:else>
                    <li class="disabled"><a href="#"><i class="icon-backward"></i></a></li>
                </g:else>
                <g:if test="${params.first > 1}">
                    <li class="disabled puntos">
                        <a href="#">...</a>
                    </li>
                </g:if>

                <g:each in="${0..params.last - params.first}" var="p">
                    <li class="${params.first + p == params.pag ? 'active' : ''}">
                        <a href="${params.first + p}" class="num">${params.first + p}</a>
                    </li>
                </g:each>

                <g:if test="${params.last < params.totalPags}">
                    <li class="disabled puntos">
                        <a href="#">...</a>
                    </li>
                </g:if>
                <g:if test="${params.last - params.pag > 0}">
                    <li class="">
                        <a href="${params.pag + 1}" class="num">
                            <i class="icon-forward"></i>
                        </a>
                    </li>
                </g:if>
                <g:else>
                    <li class="disabled">
                        <a href="#">
                            <i class="icon-forward"></i>
                        </a>
                    </li>
                </g:else>
                <li class="${params.pag == params.totalPags ? 'disabled' : ''}">
                    <a href="${params.totalPags}" class="num">
                        <i class="icon-step-forward"></i>
                    </a>
                </li>
            </ul>
        </div>
    </g:else>

</div>



<script type="text/javascript">

    function enviar(pag) {


        $.ajax({
            type:"POST",
            url:"${createLink(action:'tabla')}",
            data:{
                lgar:"${params.lgar}",
                fecha:"${params.fecha}",
                todos:"${params.todos}",
                max:100,
                pag:pag
            },
            success:function (msg) {
                $("#tablaPrecios").html(msg);
            }
        });

    }


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

        $(".disabled").click(function () {
            return false;
        });

        $(".num").click(function () {
            var num = $(this).attr("href");
            console.log(num);
            enviar(num);
            return false;
        });

//        $(".btn-generar").click(function () {
//
////            $(".precioAL").show();
////            $(".precioAnterior").show();
//
//
//        });

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