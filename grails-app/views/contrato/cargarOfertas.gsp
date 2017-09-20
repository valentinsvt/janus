<elm:select name="oferta.id" id="ofertas" from="${ofertas}" optionKey="id" optionValue="proveedor" noSelection="['-1': 'Seleccione']" class="required"  optionClass="${{ it.monto + "_" + it.plazo }}"/>
<script type="text/javascript">
    $("#ofertas").change(function () {
        if ($(this).val() != "-1") {
            var $selected = $("#ofertas option:selected");
            var idOferta = $selected.val()
            console.log($selected.val())
            $("#contratista").val($selected.text());
            %{--$("#fechaPresentacion").val(${janus.pac.Oferta?.get().fechaEntrega.format('dd-MM-yyyy')});--}%

                    $.ajax({
                        type    : "POST",
                        url     : "${g.createLink(action:'getFecha')}",
                        data    : {id : idOferta

                        },
                        success : function (msg) {

                            $("#filaFecha").html(msg);
                        }
                    });

            $.ajax({
                type    : "POST",
                url     : "${g.createLink(action:'getIndice')}",
                data    : {id : idOferta

                },
                success : function (msg) {

                    $("#filaIndice").html(msg);
                }
            });



            var cl = $selected.attr("class");
            var parts = cl.split("_");
            var m = parts[0];
            var p = parts[1];
            $("#monto").val(number_format(m, 2,"."));
//            $("#plazo").val(number_format(p, 0,"."));
            $("#plazo").val(p);
//            $("#ofertaId").val($(this).val());

            updateAnticipo();

        }
        else {
            $("#contratista").val("");
            $("#fechaPresentacion").val('');
        }
    });
</script>