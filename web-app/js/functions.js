/**
 * Created with IntelliJ IDEA.
 * User: luz
 * Date: 8/18/12
 * Time: 11:05 PM
 * To change this template use File | Settings | File Templates.
 */

//pads left
String.prototype.lpad = function(padString, length) {
    var str = this;
    while (str.length < length)
        str = padString + str;
    return str;
};

//pads right
String.prototype.rpad = function(padString, length) {
    var str = this;
    while (str.length < length)
        str = str + padString;
    return str;
};


jQuery.expr[":"].icontains = jQuery.expr.createPseudo(function (arg) {
    return function (elem) {
//        console.log(arg, elem, jQuery(elem).text().toUpperCase().indexOf(arg.toUpperCase()) >= 0);
        return jQuery(elem).text().toUpperCase().indexOf(arg.toUpperCase()) >= 0;
    };
});