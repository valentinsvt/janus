<style type="text/css">
fieldset {
    margin-bottom : 15px;
}
</style>

<div class="tituloTree">${grupo.descripcion}</div>
<g:form controller="reportes2" action="reportePrecios">
    <fieldset>
        <legend>Columnas a imprimir</legend>

        <div class="btn-group" data-toggle="buttons-checkbox">
            <g:if test="${grupo.id == 1}">
                <a href="#" id="t" class="col btn">
                    Transporte
                </a>
            </g:if>
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
    <fieldset>
        <legend>Orden de impresión</legend>

        <div class="btn-group" data-toggle="buttons-radio">
            <a href="#" id="a" class="orden btn active">
                Alfabético
            </a>
            <a href="#" id="n" class="orden btn">
                Numérico
            </a>
        </div>
    </fieldset>
    <fieldset class="form-inline">
        <legend>Lugar y fecha de referencia</legend>

        %{--<div class="btn-group noMargin" data-toggle="buttons-radio">--}%
        %{--<a href="#" id="c" class="tipo btn active">--}%
        %{--Civil--}%
        %{--</a>--}%
        %{--<a href="#" id="v" class="tipo btn">--}%
        %{--Vial--}%
        %{--</a>--}%
        %{--</div>--}%
        <g:set var="tipoMQ" value="${janus.TipoLista.findAllByCodigo('MQ')}"/>

        <g:if test="${grupo.id == 1}">
            <g:select name="lugarRep" from="${janus.Lugar.findAllByTipoListaNotInList(tipoMQ, [sort: 'descripcion'])}" optionKey="id" optionValue="descripcion"/>
        </g:if>
        <g:else>
            <g:select name="lugarRep" from="${janus.Lugar.findAllByTipoListaInList(tipoMQ, [sort: 'descripcion'])}" optionKey="id" optionValue="descripcion"/>
        </g:else>

        %{--<g:set var="d" value="${new Date()}"/>--}%

        <elm:datepicker name="fechaRep" class="datepicker required" style="width: 90px" value="${new Date()}"
                        yearRange="${(new Date().format('yyyy').toInteger() - 40).toString() + ':' + new Date().format('yyyy')}"
                        maxDate="${(new Date().format('dd').toInteger() + 31)}"/>

        %{--<div class="row-fluid" style="margin-top: 10px">--}%

        %{--<div class="span3">--}%
        %{--Cantón--}%
        %{--</div>--}%


        %{--<div class="span6">--}%

        %{--<g:select name="lugar.id" from="${janus.Lugar.findAll('from Lugar  where tipoLista=1')}" optionKey="id" optionValue="descripcion" class="span6" noSelection="['null': 'Seleccione...']" />--}%

        %{--</div>--}%
        %{--</div>--}%

        %{--<div class="row-fluid" style="margin-top: 10px">--}%

        %{--<div class="span3">--}%
        %{--Petreos Hormigones--}%
        %{--</div>--}%

        %{--<div class="span6">--}%

        %{--<g:select name="listaVolumen0.id" from="${janus.Lugar.findAll('from Lugar  where tipoLista=3')}" optionKey="id" optionValue="descripcion" class="span6" noSelection="['null': 'Seleccione...']"/>--}%
        %{--</div>--}%
        %{--</div>--}%


        %{--<div class="row-fluid" style="margin-top: 10px">--}%
        %{--<div class="span3">--}%
        %{--Especial--}%
        %{--</div>--}%
        %{--<div class="span6">--}%

        %{--<g:select name="listaPeso1.id" from="${janus.Lugar.findAll('from Lugar  where tipoLista=2')}" optionKey="id" optionValue="descripcion" class="span6" noSelection="['null': 'Seleccione...']"/>--}%
        %{--</div>--}%



        %{--</div>--}%


        %{--<div class="row-fluid" style="margin-top: 10px">--}%
        %{--<div class="span3">--}%
        %{--Mejoramiento--}%
        %{--</div>--}%
        %{--<div class="span6">--}%
        %{--<g:select name="listaVolumen1.id" from="${janus.Lugar.findAll('from Lugar  where tipoLista=4')}" optionKey="id" optionValue="descripcion"  class="span6" noSelection="['null': 'Seleccione...']"/>--}%
        %{--</div>--}%
        %{--</div>--}%

        %{--<div class="row-fluid" style="margin-top: 10px">--}%

        %{--<div class="span3">--}%
        %{--Carpeta Asfáltica--}%
        %{--</div>--}%

        %{--<div class="span6">--}%
        %{--<g:select name="listaVolumen2.id" from="${janus.Lugar.findAll('from Lugar  where tipoLista=5')}" optionKey="id" optionValue="descripcion" class="span6" noSelection="['null': 'Seleccione...']"/>--}%

        %{--</div>--}%
        %{--</div>--}%

    </fieldset>
</g:form>

<script type="text/javascript">
    %{--$(".tipo").click(function () {--}%
    %{--if (!$(this).hasClass("active")) {--}%
    %{--var tipo = $(this).attr("id");--}%
    %{--$.ajax({--}%
    %{--type    : "POST",--}%
    %{--url     : "${createLink(action:'loadLugarPorTipo')}",--}%
    %{--data    : {--}%
    %{--tipo : tipo--}%
    %{--},--}%
    %{--success : function (msg) {--}%
    %{--$("#lugar").replaceWith(msg);--}%
    %{--}--}%
    %{--});--}%
    %{--}--}%
    %{--});--}%
</script>