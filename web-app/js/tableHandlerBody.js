/**
 * Created with IntelliJ IDEA.
 * User: luz
 * Date: 11/21/12
 * Time: 1:15 PM
 * To change this template use File | Settings | File Templates.
 */

function doHighlight(params) {
    if (!params.tiempo) {
        params.tiempo = 2000;
    }
    params.elem.addClass(params.clase, params.tiempo, function () {
        params.elem.removeClass(params.clase, params.tiempo);
    });
}

function scroll() {
    var container = $('#divTabla'), scrollTo = $('.selected');
    var newPos = scrollTo.offset().top - container.offset().top + container.scrollTop() - 100;
//    container.animate({
//        scrollTop : scrollTo.offset().top - container.offset().top + container.scrollTop()
//    }, 2000);
    container.scrollTop(newPos);
}

$(document).keyup(function (ev) {
    var sel = $(".selected");
    var celdaIndex = sel.index();
    var tr = sel.parent();
    var filaIndex = tr.index();
    var ntr;
    switch (ev.keyCode) {
        case 38: //arriba
            if (filaIndex > 0) {
                ev.stopPropagation();
                ntr = tr.prev();
//                console.log(sel, celdaIndex, tr, filaIndex, ntr);
                seleccionar(ntr.children().eq(celdaIndex));
                scroll();
            }
            break;
        case 40: //abajo
            var cant = $('#tablaPrecios > tbody > tr').size();
            if (filaIndex < cant - 1) {
                ev.stopPropagation();
                ntr = tr.next();
//                console.log(sel, celdaIndex, tr, filaIndex, ntr);
                seleccionar(ntr.children().eq(celdaIndex));
                scroll();
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