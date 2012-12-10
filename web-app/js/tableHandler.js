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


