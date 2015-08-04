<%--
  Created by IntelliJ IDEA.
  User: luz
  Date: 04/08/15
  Time: 08:47 AM
--%>
<script src="${resource(dir: 'js/jquery/plugins/igorescobar-jQuery-Mask-Plugin-535b4e4', file: 'jquery.mask.min.js')}" type="text/javascript"></script>

<div class="btn-toolbar">
    <div class="btn-group">
        <a href="#" id="btnSaveAll" class="btn btn-success">
            <i class="icon-save"></i> Guardar todo
        </a>
    </div>
</div>

<table class="table table-condensed table-hover table-bordered table-striped">
    <thead>
        <tr>
            <th>Componente</th>
            <th>Índice oferta</th>
            <th>Valor oferta</th>
            <th>Índice periodo</th>
            <th>Valor periodo</th>
            <th>Valor</th>
        </tr>
    </thead>
    <tbody>
        <g:set var="totalOf" value="${0}"/>
        <g:set var="totalPer" value="${0}"/>
        <g:set var="total" value="${0}"/>
        <g:each in="${detalles}" var="det">
            <g:set var="totalOf" value="${totalOf + det.valorIndcOfrt}"/>
            <g:set var="totalPer" value="${totalPer + det.valorIndcPrdo}"/>
            <g:set var="total" value="${total + det.valor}"/>

            <g:set var="indiceOferta" value="${g.formatNumber(number: det.indiceOferta, maxFractionDigits: 3, minFractionDigits: 3)}"/>
            <g:set var="valorOferta" value="${g.formatNumber(number: det.valorIndcOfrt, maxFractionDigits: 3, minFractionDigits: 3)}"/>
            <g:set var="indicePer" value="${g.formatNumber(number: det.indicePeriodo, maxFractionDigits: 3, minFractionDigits: 3)}"/>
            <g:set var="valorPer" value="${g.formatNumber(number: det.valorIndcPrdo, maxFractionDigits: 3, minFractionDigits: 3)}"/>
            <g:set var="valor" value="${g.formatNumber(number: det.valor, maxFractionDigits: 3, minFractionDigits: 3)}"/>
            <g:set var="indice" value="${g.formatNumber(number: det.fpContractual.valor, maxFractionDigits: 3, minFractionDigits: 3)}"/>

            <tr data-id="${det.id}" data-indice="${indice}" style="height: 20px">
                <td>${det.fpContractual.indice.descripcion} (${indice})</td>
                <td>
                    <input class="span2 num mask io" type="text" value="${indiceOferta}" data-original="${indiceOferta}" data-tipo="io">
                </td>
                <td>
                    <input class="span2 num mask vo readonly" type="text" value="${valorOferta}" data-original="${valorOferta}" readonly data-tipo="vo">
                </td>
                <td>
                    <input class="span2 num mask ip" type="text" value="${indicePer}" data-original="${indicePer}" data-tipo="ip">
                </td>
                <td>
                    <input class="span2 num mask vp readonly" type="text" value="${valorPer}" data-original="${valorPer}" readonly data-tipo="vp">
                </td>
                <td>
                    <input class="span2 num mask v" type="text" value="${valor}" data-original="${valor}" data-tipo="v">
                </td>
            </tr>
        </g:each>
    </tbody>
    <tfoot>
        <tr>
            <th class="num"></th>
            <th class="num" id="io"></th>
            <th class="num" id="vo">${g.formatNumber(number: totalOf, minFractionDigits: 3, maxFractionDigits: 3)}</th>
            <th class="num" id="ip"></th>
            <th class="num" id="vp">${g.formatNumber(number: totalPer, minFractionDigits: 3, maxFractionDigits: 3)}</th>
            <th class="num" id="v">${g.formatNumber(number: total, minFractionDigits: 3, maxFractionDigits: 3)}</th>
        </tr>
    </tfoot>
</table>

<div id="dlgLoading" title="Por favor espere">
    <h3><i class="icon-spinner icon-spin icon-large"></i> Por favor espere</h3>
</div>

<script type="text/javascript">
    function calcularTotal(tipo) {
        if (tipo != "io" && tipo != "ip") {
            var tot = 0;
            $("." + tipo).each(function () {
                tot += parseFloat($(this).val());
            });
            tot = number_format(tot, 3, ".", ",");
            $("#" + tipo).html(tot);
        } else if (tipo == "io") {
            calcularTotal("vo");
        } else if (tipo == "ip") {
            calcularTotal("vp");
        }
    }
    $(function () {
        $('.mask').blur(function () {
            var $this = $(this);
            var tipo = $this.data("tipo");
            var val = str_replace(',', '', $this.val());
            var original = $this.data("original");
            var indice = $this.parents("tr").data("indice");
            if (val != original) {
                $this.addClass("changed");
            } else {
                $this.removeClass("changed");
            }
            if (tipo == "io" || tipo == "ip") {
                var valTx = number_format(parseFloat(val) * parseFloat(indice), 3, ".", ",");
                var $tx = $this.parent().next().find(".mask");
                var originalTx = $tx.data("original");
                if (valTx != originalTx) {
                    $tx.addClass("changed").val(valTx);
                } else {
                    $tx.removeClass("changed");
                }
            }
            calcularTotal(tipo);
        }).mask('###,##0.000', {reverse : true});

        $("#dlgLoading").dialog({
            modal         : true,
            buttons       : null,
            closeOnEscape : false,
            autoOpen      : false,
            open          : function (event, ui) {
                $(event.target).prev().find(".ui-dialog-titlebar-close").remove();
            }
        });

        $("#btnSaveAll").click(function () {
            var str = "";
            $(".changed").each(function () {
                var $this = $(this);
                var id = $this.parents("tr").data("id");
                var val = str_replace(',', '', $this.val());
                var tipo = $this.data("tipo");
                str += id + "_" + tipo + "=" + val + "&";
            });
            if (str != "") {
                $("#dlgLoading").dialog("open");
                $.ajax({
                    type    : "POST",
                    url     : "${createLink(action: 'guardarCambios')}",
                    data    : str,
                    success : function (msg) {
                        $("#dlgLoading").dialog("close");
                        var parts = msg.split("*");
                        log(parts[1], parts[0] == "NO");
                        if (parts[0] == "OK") {
                            $(".changed").each(function () {
                                var $this = $(this);
                                var id = $this.parents("tr").data("id");
                                var val = str_replace(',', '', $this.val());
                                $this.removeClass("changed").data("original", val);
                            });
                        }
                    }
                });
            }
        });
    });
</script>