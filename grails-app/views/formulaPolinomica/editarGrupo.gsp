<div>
    <div class="row">
        <div class="span">
            Cambiar <strong>${formula.indice.descripcion}</strong>
        </div>
    </div>

    <div class="row">
        <div class="span">
            por
            %{--<g:select name="indice" from="${janus.Indice.list([sort: 'descripcion'])}" optionKey="id" optionValue="descripcion" class="span4" value="${formula.indiceId}"/>--}%
            <g:select name="indice" from="${indices}" optionKey="id" optionValue="descripcion" class="span4" value="${formula.indiceId}"/>
        </div>
    </div>

    <div class="row">
        <div class="span">
            Modificar valor &nbsp;&nbsp;&nbsp;
            %{--<input type="number" step="0.001" pattern="#.###"/>--}%
            <g:field type="number" name="valor" step="0.001" pattern="#.###" value="${formula.valor}" class="input-mini"/>
            (suma <g:formatNumber number="${total}" format="##,##0.#####" locale="ec"/>)
        </div>
    </div>
</div>

<script type="text/javascript">
    function validarNum(ev) {
        /*
         48-57      -> numeros
         96-105     -> teclado numerico
         188        -> , (coma)
         190        -> . (punto) teclado
         110        -> . (punto) teclado numerico
         8          -> backspace
         46         -> delete
         9          -> tab
         37         -> flecha izq
         39         -> flecha der
         */
        return ((ev.keyCode >= 48 && ev.keyCode <= 57) ||
                (ev.keyCode >= 96 && ev.keyCode <= 105) ||
                ev.keyCode == 190 || ev.keyCode == 110 ||
                ev.keyCode == 8 || ev.keyCode == 46 || ev.keyCode == 9 ||
                ev.keyCode == 37 || ev.keyCode == 39);
    }

    $("#valor").bind({
        keydown : function (ev) {
            if (ev.keyCode == 190 || ev.keyCode == 110) {
                var val = $(this).val();
                if (val.length == 0) {
                    $(this).val("0");
                }
                return val.indexOf(".") == -1;
            } else {
                return validarNum(ev);
            }
        }/*,
         keyup   : function () {
         if ($.trim($(this).val() == "")) {
         ////console.log("aqui", $(this).val());
         $(this).val("0");
         } else {
         ////console.log("?");
         }
         }*/
    });

    //    $("#valor").keydown(function (ev) {
    //        if (ev.keyCode == 190 || ev.keyCode == 110) {
    //            var val = $(this).val();
    //            if (val.length == 0) {
    //                $(this).val("0");
    //            }
    //            return val.indexOf(".") == -1;
    //        } else {
    //            return validarNum(ev);
    //        }
    //    });

</script>