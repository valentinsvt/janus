/**
 * Created with IntelliJ IDEA.
 * User: luz
 * Date: 11/21/12
 * Time: 1:15 PM
 * To change this template use File | Settings | File Templates.
 */

function doEdit(sel) {
    var texto = $.trim(sel.text());
    var w = sel.width();
    var w = 100;
    var textField = $('<input type="text" class="editando" value="' + texto + '"/>');
    textField.width(w - 5);
    sel.html(textField);
    textField.focus();
    sel.data("valor", texto);
}

function stopEdit() {
    //var value = $(".editando").val(); //valor del texfield (despues de editar)
    var value = $(".selected").data("valor"); //valor antes de la edicion
    if (value) {
        $(".selected").html(number_format(value, 5, ".", ""));
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

$(".disabled").click(function () {
    return false;
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
    switch (ev.keyCode) {
        case 38: //arriba
            if (filaIndex > 0) {
                ntr = tr.prev();
//                console.log(sel, celdaIndex, tr, filaIndex, ntr);
                seleccionar(ntr.children().eq(celdaIndex));
            }
            break;
        case 40: //abajo
            var cant = $('#tablaPrecios > tbody > tr').size();
            if (filaIndex < cant - 1) {
                ntr = tr.next();
//                console.log(sel, celdaIndex, tr, filaIndex, ntr);
                seleccionar(ntr.children().eq(celdaIndex));
            }
            break;
        case 13: //enter
            var target = $(ev.target);
            if (target.hasClass("editando")) {
                var value = target.val();
                $(".selected").html(number_format(value, 5, ".", ""));
                sel.data("valor", value);
            } else {
                doEdit(sel);
            }
            break;
        case 27: //esc
            stopEdit();
            break;
        default:
            return true;
    }
});