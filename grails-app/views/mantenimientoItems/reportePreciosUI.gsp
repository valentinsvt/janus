<style type="text/css">
fieldset {
    margin-bottom : 15px;
}
</style>

<div class="tituloTree">Materiales</div>
<g:form controller="reportes2" action="reportePrecios">
    <fieldset>
        <legend>Columnas a imprimir</legend>

        <div class="btn-group" data-toggle="buttons-checkbox">
            <a href="#" id="t" class="col btn">
                Transporte
            </a>
            <a href="#" id="u" class="col btn active">
                Unidad
            </a>
            <a href="#" id="p" class="col btn active">
                Precio
            </a>
            <a href="#" id="f" class="col btn">
                Fecha de Act.
            </a>
        </div>
    </fieldset>
    %{--<fieldset>--}%
        %{--<legend>Orden de impresión</legend>--}%

        %{--<div class="btn-group" data-toggle="buttons-radio">--}%
            %{--<a href="#" id="a" class="orden btn active">--}%
                %{--Alfabético--}%
            %{--</a>--}%
            %{--<a href="#" id="n" class="orden btn">--}%
                %{--Numérico--}%
            %{--</a>--}%
        %{--</div>--}%
    %{--</fieldset>--}%
    <fieldset class="form-inline">
        <legend>Lugar y fecha de referencia</legend>

        <div class="btn-group noMargin" data-toggle="buttons-radio">
            <a href="#" id="c" class="tipo btn active">
                Civil
            </a>
            <a href="#" id="v" class="tipo btn">
                Vial
            </a>
        </div>
        <g:select name="lugarRep" from="${janus.Lugar.findAllByTipo('C', [sort: 'descripcion'])}" optionKey="id" optionValue="${{it.descripcion + ' (' + it.tipo + ')'}}"/>
        <elm:datepicker name="fechaRep" class="datepicker required" style="width: 90px" value="${new Date()}"
                        yearRange="${(new Date().format('yyyy').toInteger() - 40).toString() + ':' + new Date().format('yyyy')}"
                        maxDate="new Date()"/>

    </fieldset>
</g:form>

<script type="text/javascript">
    $(".tipo").click(function () {
        if (!$(this).hasClass("active")) {
            var tipo = $(this).attr("id");
            $.ajax({
                type    : "POST",
                url     : "${createLink(action:'loadLugarPorTipo')}",
                data    : {
                    tipo : tipo
                },
                success : function (msg) {
                    $("#lugar").replaceWith(msg);
                }
            });
        }
    });
</script>