/**
 * Created with IntelliJ IDEA.
 * User: svt
 * Date: 10/25/12
 * Time: 12:11 PM
 * To change this template use File | Settings | File Templates.
 */

function extendRemove(target, props) {
    $.extend(target, props);
    for (var name in props) {
        if (props[name] == null || props[name] == undefined) {
            target[name] = props[name]
        }
    }
    return target
}

jQuery.fn.paginate = function (settings) {
    var defaults = {
        maxRows        : 10,
        hideOnePage    : true,
        searchPosition : "default", //default para q se ubique arriba de la tabla, un elemento jquery para q se ubique en append
        search         : true, //true para mostrar la cajita de busqueda
        searchSize     : "span2", //tamanio del textfield
        float          : "left" //float del form
    };

    defaults = extendRemove(defaults, settings || {});

    //save th jquery object
    var elements = this;

    //go through each obj in the selector
    var ids = 0;
    return this.each(function () {
        //save the current element
        var originalElement = this;
        var $element = $(originalElement);

        var id = $element.attr("id") ? $element.attr("id") : ids;
        ids++;

        var num = defaults.maxRows;
        var rows = $element.find("tr");
        var cantRows = rows.size();
        var paginas = Math.ceil(cantRows / num);
        var fila, padre;

        var show = function (pag) {
            var body = $element;
            body.find("tr").addClass("hidden");
            body.find("." + pag).removeClass("hidden").show();
            $(".paginateButton").parents("li").removeClass("active");
            $(".b" + pag).parents("li").addClass("active");
            $(".hidden").hide();
            return false;
        };

        rows.each(function (i) {
            if (i >= num) {
                $(this).hide().addClass("hidden " + (Math.ceil((i + 1) / num)));
            } else {
                $(this).addClass("1");
            }
        });
        padre = $element.parent().parent();
        if (defaults.search) {
            var doSearch = function () {
                var strSearch = $.trim($inputSearch.val());
                rows.addClass("hidden").hide();
                $("td").unhighlight();
                if (strSearch == "") {
                    show(1);
                    $(".pagination").show();
                } else {
                    $(".pagination").hide();
                    rows.each(function () {
                        var tds = $(this).find("td:containsCI('" + strSearch + "')");
                        $(this).find("td:containsCI('" + strSearch + "')").highlight(strSearch);
                        if (tds.size() > 0) {
                            $(this).show().removeClass("hidden");
                        }
                    });
                }
            };

            var inputEvents = function (tipo) {
                switch (tipo) {
                    case "click":
                        $btnSearch.click(function () {
                            doSearch();
                        });
                        $inputSearch.keyup(function (ev) {
                            if (ev.keyCode == 27) {
                                $inputSearch.val("");
                                $("td").unhighlight();
                                show(1);
                                $(".pagination").show();
                                $inputSearch.val("");
                                return false;
                            } else if (ev.keyCode == 13) {
                                doSearch();
                                return false;
                            }
                        });
                        break;
                    case "keyup":
                        $inputSearch.keyup(function (ev) {
                            if (ev.keyCode == 27) {
                                $inputSearch.val("");
                                $("td").unhighlight();
                                show(1);
                                $(".pagination").show();
                            } else {
                                doSearch();
                            }
                        });
                        break;
                }
            };

            var $formSearch = $("<form class='form-search' style='float:" + defaults.float + ";'></form>");
            $formSearch.css({
                width : "auto"
            });
            var $divSearch = $("<div class='input-append'></div>");
            var $inputSearch = $("<input type='text' class='" + defaults.searchSize + " search-query' />");
            var $btnSearch = "";
            if (defaults.searchButton) {
                $btnSearch = $("<a href='#' class='btn'><i class='icon-zoom-in'></i> " + defaults.searchButton + "</button>");
                inputEvents("click");
            } else {
                $btnSearch = $("<span class='add-on'><i class='icon-zoom-in'></i></span>");
                inputEvents("keyup");
            }

            $divSearch.append($inputSearch).append($btnSearch);
            $formSearch.append($divSearch);
            if (defaults.searchPosition == "default") {
                padre.prepend($formSearch);
            } else {
                defaults.searchPosition.append($formSearch);
            }
        }

        if ((paginas == 1 && !defaults.hideOnePage) || paginas > 1) {

            fila = $("<div>");
            fila.addClass("pagination");
            fila.width($element.parent().width() - 10);
            fila.height(20);
            fila.css("padding-left", 5).css("padding-right", 5).css("padding-top", 2);

            var $ul = $("<ul/>");

            for (var i = 0; i < paginas; i++) {
                var $li = $("<li/>");
                var $a = $("<a href='#'/>");
                //                $a.html(i + 1).bind("click", show).data("body", id).addClass("b" + (i + 1) + " paginateButton");
                $a.html(i + 1).click(function () {
                    show($(this).html());
                }).data("body", id).addClass("b" + (i + 1) + " paginateButton");
                if (i == 0) {
                    $li.addClass("active");
                }
                $li.html($a);
                $ul.append($li);
            }
            fila.append($ul);
            padre.append(fila);
        }
    });
};