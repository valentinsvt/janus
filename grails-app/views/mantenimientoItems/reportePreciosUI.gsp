
<script src="${resource(dir: 'js', file: 'jquery.switcher.js')}"></script>
<link href="${resource(dir: 'css', file: 'switcher.css')}" rel="stylesheet">


<style type="text/css">
fieldset {
    margin-bottom : 15px;
}

    .izq{
        margin-left: 20px;
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
            <a href="#" id="r" class="col btn">
                Rubros
            </a>
            <a href="#" id="o" class="col btn">
                Obras
            </a>
        </div>
    </fieldset>
    <fieldset>
        <legend>Orden de impresión - Activos/Inactivos</legend>
        %{--<div class="col-md-12">--}%
        %{--<div class="col-md-6">--}%
        <div class="btn-group" data-toggle="buttons-radio">
            <a href="#" id="a" class="orden btn active">
                Alfabético
            </a>
            <a href="#" id="n" class="orden btn" style="margin-right: 60px">
                Numérico
            </a>
            <input class="form-check-input revisar" type="checkbox" value="option1" checked="checked">
        </div>
        %{--<div class="form-check form-check-inline col-md-3">--}%

        %{--</div>--}%

    </fieldset>

    <fieldset class="form-inline">
        <legend>Lugar y fecha de referencia</legend>

        <g:set var="tipoMQ" value="${janus.TipoLista.findAllByCodigo('MQ')}"/>

        <g:if test="${grupo.id == 1}">
            <g:select name="lugarRep" from="${janus.Lugar.findAllByTipoListaNotInList(tipoMQ, [sort: 'descripcion'])}" optionKey="id" optionValue="descripcion"/>
        </g:if>
        <g:else>
            <g:select name="lugarRep" from="${janus.Lugar.findAllByTipoListaInList(tipoMQ, [sort: 'descripcion'])}" optionKey="id" optionValue="descripcion"/>
        </g:else>

        <elm:datepicker name="fechaRep" class="datepicker required" style="width: 90px" value="${new Date()}"
                        yearRange="${(new Date().format('yyyy').toInteger() - 40).toString() + ':' + new Date().format('yyyy')}"
                        maxDate="${(new Date().format('dd').toInteger() + 31)}"/>

    </fieldset>
</g:form>

<script type="text/javascript">
    $.switcher('input[type=checkbox]');
</script>