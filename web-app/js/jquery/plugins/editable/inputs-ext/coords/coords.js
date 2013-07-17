/**
 Coords editable input.
 Internally value stored as {city: "Moscow", street: "Lenina", building: "15"}

 @class coords
 @extends abstractinput
 @final
 @example
 <a href="#" id="coords" data-type="coords" data-pk="1">awesome</a>
 <script>
 $(function(){
    $('#coords').editable({
        url: '/post',
        title: 'Enter city, street and building #',
        value: {
            city: "Moscow",
            street: "Lenina",
            building: "15"
        }
    });
});
 </script>
 **/
(function ($) {
    "use strict";

    var Coords = function (options) {
        this.init('coords', options, Coords.defaults);
    };

    //inherit from Abstract input
    $.fn.editableutils.inherit(Coords, $.fn.editabletypes.abstractinput);

    $.extend(Coords.prototype, {
        /**
         Renders input from tpl

         @method render()
         **/
        render : function () {
            this.$input = this.$tpl.find('input, select');
        },

        /**
         Default method to show value in element. Can be overwritten by display option.

         @method value2html(value, element)
         **/
        value2html : function (value, element) {
            if (!value) {
                $(element).empty();
                return;
            }
            var ns = $.trim(value.NS);
            var ng = $.trim(value.NG);
            var nm = $.trim(value.NM);
            var ew = $.trim(value.EW);
            var eg = $.trim(value.EG);
            var em = $.trim(value.EM);
            ng = parseInt(ng);
            eg = parseInt(eg);
            nm = parseFloat(nm);
            em = parseFloat(em);
            var html = $('<div>').text(ns).html() + ' ' + $('<div>').text(ng).html() + ' ' + $('<div>').text(nm).html() + ' ' + $('<div>').text(ew).html() + ' ' + $('<div>').text(eg).html() + ' ' + $('<div>').text(em).html();
            $(element).html(html);
        },

        /**
         Gets value from element's html

         @method html2value(html)
         **/
        html2value : function (html) {
            /*
             you may write parsing method to get value by element's html
             e.g. "Moscow, st. Lenina, bld. 15" => {city: "Moscow", street: "Lenina", building: "15"}
             but for complex structures it's not recommended.
             Better set value directly via javascript, e.g.
             editable({
             value: {
             city: "Moscow",
             street: "Lenina",
             building: "15"
             }
             });
             */
            return null;
        },

        /**
         Converts value to string.
         It is used in internal comparing (not for sending to server).

         @method value2str(value)
         **/
        value2str : function (value) {
            var str = '';
            if (value) {
                for (var k in value) {
                    str = str + k + ':' + value[k] + ';';
                }
            }
            return str;
        },

        /*
         Converts string to value. Used for reading value from 'data-value' attribute.

         @method str2value(str)
         */
        str2value : function (str) {
            /*
             this is mainly for parsing value defined in data-value attribute.
             If you will always set value by javascript, no need to overwrite it
             */
            return str;
        },

        /**
         Sets value of input.

         @method value2input(value)
         @param {mixed} value
         **/
        value2input : function (value) {
            if (!value) {
                return;
            }
            this.$input.filter('[name="NS"]').val(value.NS);
            this.$input.filter('[name="NG"]').val(value.NG);
            this.$input.filter('[name="NM"]').val(value.NM);
            this.$input.filter('[name="EW"]').val(value.EW);
            this.$input.filter('[name="EG"]').val(value.EG);
            this.$input.filter('[name="EM"]').val(value.EM);
        },

        /**
         Returns value of input.

         @method input2value()
         **/
        input2value : function () {
            var ns = this.$input.filter('[name="NS"]').val();
            var ng = this.$input.filter('[name="NG"]').val();
            var nm = this.$input.filter('[name="NM"]').val();
            var ew = this.$input.filter('[name="EW"]').val();
            var eg = this.$input.filter('[name="EG"]').val();
            var em = this.$input.filter('[name="EM"]').val();
            ng = parseInt(ng);
            eg = parseInt(eg);
            nm = parseFloat(nm);
            em = parseFloat(em);
            return {
                NS : ns,
                NG : ng,
                NM : nm,
                EW : ew,
                EG : eg,
                EM : em
            };
        },

        /**
         Activates input: sets focus on the first field.

         @method activate()
         **/
        activate : function () {
            this.$input.filter('[name="NG"]').focus();
        },

        /**
         Attaches handler to submit form in case of 'showbuttons=false' mode

         @method autosubmit()
         **/
        autosubmit : function () {
            this.$input.keydown(function (e) {
                if (e.which === 13) {
                    $(this).closest('form').submit();
                }
            });
        }
    });

    Coords.defaults = $.extend({}, $.fn.editabletypes.abstractinput.defaults, {
        tpl        : '<div class="editable-coords"><select class="input-mini" name="NS"><option value="N">N</option><option value="S">S</option></select><input type="text" name="NG" class="input-mini"><input type="text" name="NM" class="input-mini"></div>' +
                     '<div class="editable-coords"><select class="input-mini" name="EW"><option value="E">E</option><option value="W">W</option></select><input type="text" name="EG" class="input-mini"><input type="text" name="EM" class="input-mini"></div>',
        inputclass : ''
    });

    $.fn.editabletypes.coords = Coords;

}(window.jQuery));