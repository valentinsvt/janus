<div style="">

    <table  class="table table-bordered table-striped table-condensed table-hover" style="font-size: 10px !important;">
        <thead>
        <tr>
            <th colspan="9">Seguimiento del tramite ${memo}</th>
        </tr>
        <tr>
            <th width="110px;">Trámite Principal</th>
            <th width="110px;">Trámite</th>
            <th>Fecha</th>
            <th>De</th>
            <th>Para</th>
            <th>Límite</th>
            <th>Asunto</th>
            <th>Recibido</th>
            <th>Fec. Recibido</th>
        </tr>
        </thead>
        <tbody>
        <g:each in="${tramites}" var="t">
            <tr class="item_row" fecha="${(t["TFRECEP"])?t["TFRECEP"].format('dd-MM-yyyy'):'-1'}">
                <td>${t["NMASTER"]}</td>
                <td>${t["NTRAMITE"]}</td>
                <td>${t["TFECHA"]}</td>
                <td>${t["TCREADOR"]}</td>
                <td>${t["TUSDES"]}</td>
                <td>${t["TFLIMITE"]}</td>
                <td>${t["TASUNTO"]}</td>
                <td>${(t["TRECIBIDO"].toString()=="1")?"Si":"No"}</td>
                <td>${(t["TFRECEP"])?t["TFRECEP"]:""}</td>

            </tr>
        </g:each>
        </tbody>
    </table>
</div>

<script type="text/javascript">

    $.contextMenu({
        selector: '.item_row',
        callback: function (key, options) {

            var fecha= $(this).attr("fecha");

            var concurso = $("#con_id").val()
//            window.console && //console.log(m) || alert(m)
//
//            if (key == "edit") {
//                $(this).dblclick()
//            }


            if (key == "1" || key == "2" || key == "3" ) {
                var tipo = key;
                if(fecha!="-1"){
                    $.ajax({type : "POST", url : "${g.createLink(controller: 'concurso',action:'setEtapa')}",
                        data     : "fecha=" + fecha+"&tipo="+tipo+"&id="+concurso,
                        success  : function (msg) {
                            window.location.reload(true)
                        }
                    });
                }else{
                    $.box({
                        imageClass : "box_info",
                        text       : "El tramite no tiene fecha de recepción. Espere a que sea recibido o seleccione otro tramite",
                        title      : "Errores",
                        iconClose  : false,
                        dialog     : {
                            resizable : false,
                            draggable : false,
                            buttons   : {
                                "Aceptar" : function () {
                                }
                            },
                            width     : 500
                        }
                    });
                }

            }
        },
        %{--<g:if test="${obra?.estado!='R'}">--}%
        items: {
//            "edit": {name: "Editar", icon: "edit"},
            "1": {name: "Establecer como fin de la etapa 1",icon:"info"},
            "2": {name: "Establecer como fin de la etapa 2",icon:"info"},
            "3": {name: "Establecer como fin de la etapa 3",icon:"info"}
        }
        %{--</g:if>--}%
        %{--<g:else>--}%
        %{--items: {--}%
        %{--"print": {name: "Imprimir", icon: "print"},--}%
        %{--"foto": {name: "Foto", icon: "doc"}--}%
        %{--}--}%
        %{--</g:else>--}%
    });






</script>