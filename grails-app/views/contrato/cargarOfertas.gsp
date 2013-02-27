<elm:select name="oferta.id" id="ofertas" from="${ofertas}" optionKey="id" optionValue="proveedor" noSelection="['-1': 'Seleccione']" optionClass="${{ it.monto + "_" + it.plazo }}"/>
<script type="text/javascript">
    $("#ofertas").change(function () {
        if ($(this).val() != "-1") {
            var $selected = $("#ofertas option:selected");
            $("#contratista").val($selected.text());
            var cl = $selected.attr("class");
            var parts = cl.split("_");
            var m = parts[0];
            var p = parts[1];
            $("#monto").val(number_format(m, 2,"."));
            $("#plazo").val(number_format(p, 2,"."));
//            $("#ofertaId").val($(this).val());
        }
        else {
            $("#contratista").val("")
        }
    });
</script>