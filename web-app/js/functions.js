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

jQuery.extend(
    jQuery.expr[':'].containsCI = function (a, i, m) {
        //-- faster than jQuery(a).text()
        var sText = (a.textContent || a.innerText || "");
        var zRegExp = new RegExp(m[3], 'i');
//        console.log(sText, zRegExp.test(sText));
        return zRegExp.test(sText);
    }
);