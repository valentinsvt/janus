/**
 * Created with IntelliJ IDEA.
 * User: luz
 * Date: 6/11/13
 * Time: 11:28 AM
 * To change this template use File | Settings | File Templates.
 */


$(function () {
    var circular = false;
    var tmp = 500;

    var $body = $("body");

    //el titulo
    var titulo = $(".centrado").first().find("h4").text();

    //quitar el 1r centrado
    $(".centrado").first().remove();

    //el indice
    var $indice = $("#indice").find("ol");

    //quitar el indice
    $("#indice").remove();

    //los contenidos
    var $items = $(".centrado");
    var totalItems = $items.length;

    $body.empty();

    //las dimensiones de los elementos
    var screenWidth = $(window).width();
    var screenHeight = $(window).height();

    var paneHeight = screenHeight - 70;
    var contentWidth = screenWidth * totalItems;
    var timelineHeight = (paneHeight * 0.2);
    var timelineTextHeight = 20;
    var itemHeight = paneHeight * 0.8;
    var timeLineaHeight = timelineHeight - timelineTextHeight;
    var timeLineaLeft = screenWidth / 2;

    var timelineLabelWidth = 200;
    var timelineLabelHeight = 25;
    var labelStart = (screenWidth / 2);

//    var timelineWidth = screenWidth * (totalItems / 5) + (screenWidth / 2);
    var timelineWidth = contentWidth;

    //barra para el titulo
    var $barra = $("<div class='navbar'></div>");
    var $barraInner = $('<div class="navbar-inner"></div>');
    var $titulo = $('<a class="brand" href="#">' + titulo + '</a>');
    $titulo.click(function () {
        goto(0);
    });
    $barraInner.append($titulo);
    $barra.append($barraInner);

    //el contenedor para todo
    var $pane = $("<div class='lz-pane'></div>").height(paneHeight);

    //el contenedor para los contenidos
    var $content = $("<div class='lz-content'></div>").width(contentWidth);

    //el contenedor para el timeline
    var $timeline = $("<div class='lz-timeline'></div>").width(timelineWidth).height(timelineHeight);
    //el texto debajo del timeline
    var $timelineText = $("<div class='lz-timelineText'></div>").width(timelineWidth).height(timelineTextHeight);
    $timelineText.appendTo($timeline);
    //la linea central en el timeline
    var $timeLinea = $("<div class='lz-linea'>&nbsp;</div>").appendTo($pane);
    $timeLinea.css({
        width  : 3,
        height : timeLineaHeight,
        left   : timeLineaLeft,
        top    : itemHeight
    });
    //timeline draggable
    $timeline.draggable({
        axis : "x",
//        delay : 100,
        drag : function () {
            var currentLeft = $timeline.position().left;

            if (currentLeft > 0) {
                $timeline.css({
                    left : 0
                });
                return false;
            } else {
                var max = timelineLabelWidth * itemCount * -1;
                if (currentLeft < max) {
                    $timeline.css({
                        left : max
                    });
                    return false;
                }
            }
            return true;
        }
    });

    //la navegacion
    var change = function ($currentItem, $nextItem) {
        if ($currentItem && $nextItem) {
            var nextIndex = $nextItem.index();
            goto(nextIndex);
        }
    };

    var prevNext = function (tipo) {
        tipo = tipo.toLowerCase();
        var $current = $(".lz-current");
        var currentIndex = $current.index();
        var $next = null;

        switch (tipo) {
            case "prev":
                if (currentIndex > 0) {
                    $next = $current.prev();
                } else {
                    if (circular) {
                        $next = $items.last();
                    }
                }
                break;
            case "next":
                if (currentIndex < totalItems - 1) {
                    $next = $current.next();
                } else {
                    if (circular) {
                        $next = $items.first();
                    }
                }
                break;
        }
        change($current, $next);
    };

    var goto = function (slide) {
        $(".lz-current").removeClass("lz-current");
        $("#item_" + slide).scrollTop(0).addClass("lz-current");
        $(".lz-currentLabel").removeClass("lz-currentLabel");
        $("[href=item_" + slide + "]").addClass("lz-currentLabel");
        $("." + slide).addClass("lz-currentLabel");
        slide = parseInt(slide);
        var nextXContent = (-1 * slide) * screenWidth;
        var nextXTimeline = (-1 * slide) * timelineLabelWidth;
        $content.animate({
            left : nextXContent
        }, tmp);
        $timeline.animate({
            left : nextXTimeline
        }, tmp);

        if (slide > 0) {
            $(".lz-nav-left").show();
        } else {
            $(".lz-nav-left").hide();
        }

        if (slide == totalItems - 1) {
            $(".lz-nav-right").hide();
        } else {
            $(".lz-nav-right").show();
        }
    };

//    var $prevBtn = $("<a href='#'> </a>");
    var $prev = $("<div class='lz-nav lz-nav-left'></div>").css({
        top : itemHeight / 2
    });
    $prev.click(function () {
        prevNext("prev");
    });
//    $prev.append($prevBtn);
//    var $nextBtn = $("<a href='#'> </a>");
    var $next = $("<div class='lz-nav lz-nav-right'></div>").css({
        top : itemHeight / 2
    });
    $next.click(function () {
        prevNext("next");
    });
    if (totalItems > 1) {
        $next.show();
    }
//    $next.append($nextBtn);

    $(window).keyup(function (ev) {
        switch (ev.keyCode) {
            case 37: //izq
                prevNext("prev");
                break;
            case 39: //der
                prevNext("next");
                break;
        }
    });

    /***************** aqui convierte el contenido en items ************************************/
    $items.first().addClass("lz-current");
    var itemCount = 0;
    $items.each(function () {
        var $centrado = $(this);
        var $item = $("<div class='lz-item' id='item_" + itemCount + "'></div>");
        if ($centrado.hasClass("lz-current")) {
            $centrado.removeClass("lz-current");
            $item.addClass("lz-current");
        }
        $item.append($centrado);
        $centrado.css({
            margin : "0 45px 0 45px"
        });
        $item.width(screenWidth).height(itemHeight).css({
            overflowY : "auto"
        });
        $content.append($item);
        itemCount++;
    });

    /***************** aqui convierte el indice en timeline ************************************/
    //1ro los titulos grandes en la parte del texto del timeline
    var posLeft = labelStart;
    itemCount = 0;
    $indice.children("li").each(function () {
        var $item = $(this);
        var txt = $.trim($item.children("a").text());
        var $hijos = $item.children("ul").children("li");
        var w = $hijos.length * timelineLabelWidth;
        var clase = "";
        var first = 0;
        if (w == 0) {
            w = timelineLabelWidth;
        }
        var $lbl = $("<div class='lz-timetextLabel'></div>").html("<div class='lz-tooltip'>" + txt + "</div>").attr("title", txt).css({
            width : w,
            left  : posLeft
        });
        if (posLeft == labelStart) {
            $lbl.addClass("lz-currentLabel");
        }
        $timelineText.append($lbl);

        var j = 0;
        var top = 0;
        var mult = 1;
        if ($hijos.length == 0) {
            clase += itemCount + ' ';
            first = itemCount;
            var $label = $("<div class='lz-timelineLabel ui-corner-all' href='item_" + itemCount + "'></div>").text(txt).attr("title", txt).css({
                width     : timelineLabelWidth,
                minHeight : timelineLabelHeight,
                left      : posLeft
            });
            if (posLeft == labelStart && j == 0) {
                $label.addClass("lz-currentLabel");
            }
            $timeline.append($label);
            itemCount++;
        }

        //la linea divisoria de los temas grandes
        var $division = $("<div class='lz-division'>&nbsp;</div>").appendTo($timeline);
        $division.css({
            width  : 1,
            height : timelineHeight,
            left   : posLeft + w
        });

        $hijos.each(function () {
            clase += itemCount + ' ';
            var text = $.trim($(this).text());
            var $label = $("<div class='lz-timelineLabel ui-corner-all' href='item_" + itemCount + "'></div>").text(text).data("tooltip", text).css({
                width     : timelineLabelWidth,
                minHeight : timelineLabelHeight,
                left      : posLeft + (timelineLabelWidth * j),
                top       : top
            });

            if (j == 0) {
                first = itemCount;
                if (posLeft == labelStart) {
                    $label.addClass("lz-currentLabel");
                }
            }
            top += mult * timelineLabelHeight;
            if (top > timeLineaHeight) {
                mult = -1;
                top -= (2 * timelineLabelHeight);
            } else if (top < 0) {
                mult = 1;
                top += (2 * timelineLabelHeight);
            }
            $timeline.append($label);
            itemCount++;
            j++;
        });
        $lbl.addClass(clase);
        $lbl.data("first", first);
        posLeft += w;
    });

    $pane.append($content);
    $pane.append($prev);
    $pane.append($next);
    $pane.append($timeline);

    $body.append($barra);
    $body.append($pane);

    $(".lz-timelineLabel").click(function () {
        if (!$(this).hasClass("lz-currentLabel")) {
            var item = $(this).attr("href");
            var parts = item.split("_");
            goto(parts[1]);
        }
    });
    $(".lz-timetextLabel").click(function () {
        if (!$(this).hasClass("lz-currentLabel")) {
            var item = $(this).data("first");
            goto(item);
        }
    });
});