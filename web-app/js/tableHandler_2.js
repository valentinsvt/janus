/**
 * Created with IntelliJ IDEA.
 * User: luz
 * Date: 11/21/12
 * Time: 1:15 PM
 * To change this template use File | Settings | File Templates.
 */

var decimales = 2;  /* Modificación para trabajar sólo con 2 decimales */

var beforeStopEdit = function (selected) {
};
var afterStopEdit = function (selected) {
};

var beforeDoEdit = function (selected, textField) {
};
var afterDoEdit = function (selected, textField) {
};

var textFieldBinds = {
};

function doEdit(sel) {
    var texto = $.trim(sel.text());
//    console.log("doEdit", sel,sel.text(), texto, sel.data("valor"));
    if (texto == "" && sel.data("valor") > 0) {
        texto = sel.data("valor");
    }
    var w = sel.width();
    var w = 80;
    var textField = $('<input type="text" class="editando" value="' + texto + '"/>');
    textField.width(w - 5);
    textField.bind(textFieldBinds);
    beforeDoEdit(sel, textField);
    sel.html(textField);
    textField.focus();
    sel.data("valor", texto);
    afterDoEdit(sel, textField);
//    console.log(sel, texto, sel.data("valor"));
}

function stopEdit() {
//    console.log("-->")
    var $sel = $(".selected");
    beforeStopEdit($sel);
//    if (parseFloat(value) == 0 || value == "") {
//    var value = $sel.data("valor"); //valor antes de la edicion
    var valueTF = $(".editando").val(); //valor del texfield (despues de editar)
    var valueData = $sel.data("valor"); //valor del data nuevo
    var valueOriginal =  $sel.data("valor"); //valororiginal cargado de la base
    console.log("stopEdit", $sel, valueTF, valueData, valueOriginal);

//    }
    if (valueTF || parseInt(valueTF) == 0) {
        $sel.html(number_format(valueTF, decimales, ".", ""));
        if (valueOriginal != valueTF) {
            $sel.addClass("changed");
            $sel.data("valor",number_format(valueTF, decimales, ".", ""));
        }
    } else if(valueData) {
        $sel.html(number_format(valueData, decimales, ".", ""));
        if (valueOriginal != valueData) {
            $sel.addClass("changed");
        }
    } else if(valueOriginal) {
        $sel.html(number_format(valueOriginal, decimales, ".", ""));
    } else {
        $sel.html("");
    }
    afterStopEdit($sel);
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
