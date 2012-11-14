/**
 * Created with IntelliJ IDEA.
 * User: luz
 * Date: 11/13/12
 * Time: 2:39 PM
 * To change this template use File | Settings | File Templates.
 */
$.validator.addMethod(
    "regex",
    function (value, element, regexp) {
        var check = false;
        var re = new RegExp(regexp);
        return this.optional(element) || re.test(value);
    },
    "No special Characters allowed here."
);