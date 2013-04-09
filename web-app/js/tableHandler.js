/**
 * Created with IntelliJ IDEA.
 * User: luz
 * Date: 11/21/12
 * Time: 1:15 PM
 * To change this template use File | Settings | File Templates.
 */

function doEdit(sel) {
    var texto = $.trim(sel.text());
    console.log("doEdit", sel, texto, sel.data("valor"));
    if (texto == "" && sel.data("valor") > 0) {
        texto = sel.data("valor");
    }
    var w = sel.width();
    var w = 100;
    var textField = $('<input type="text" class="editando" value="' + texto + '"/>');
    textField.width(w - 5);
    sel.html(textField);
    textField.focus();
    sel.data("valor", texto);
    console.log(sel, texto, sel.data("valor"));
}

function stopEdit() {
//    var value = $(".editando").val(); //valor del texfield (despues de editar)
    var $sel = $(".selected");
//    if (parseFloat(value) == 0 || value == "") {
    var value = $sel.data("valor"); //valor antes de la edicion

    console.log("stopEdit", $sel, value);

//    }
    if (value) {
        $sel.html(number_format(value, 5, ".", ""));
        if ($sel.data("original") != $sel.data("valor")) {
            $sel.addClass("changed");
        }
    } else {
        $sel.html("");
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

$(".editable").bind({
    click    : function (ev) {
        if ($(ev.target).hasClass("editable")) {
            seleccionar($(this));
        }
    },
    dblclick : function (ev) {
        if ($(ev.target).hasClass("editable")) {
            seleccionar($(this));
            doEdit($(this));
        }
    }
});
