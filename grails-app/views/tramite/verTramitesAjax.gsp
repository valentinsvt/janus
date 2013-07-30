<g:if test="${flash.message}">
    <div class="span12" style="height: 35px;margin-bottom: 10px;">
        <div class="alert ${flash.clase ?: 'alert-info'}" role="status">
            <a class="close" data-dismiss="alert" href="#">×</a>
            ${flash.message}
        </div>
    </div>
</g:if>
<div class="span12 btn-group" role="navigation" style="height: 60px;">
    <a href="#" class="btn  " id="refresh">
        <i class="icon-file"></i>
        Actualizar la base de datos desde el sistema S.A.D.
    </a>
</div>
<div style="border-bottom: 1px solid black;padding-left: 50px;position: relative;margin-top: 20px;">
    <p class="css-vertical-text">Memo</p>
    <div class="linea" style="height: 100px;"></div>
    <div class="row-fluid">
        <div class="span1 campo">
            Documento:
        </div>
        <div class="span2">
            ${memo}
        </div>
        <div class="span1 campo">
            Fecha:
        </div>
        <div class="span2">
            ${header["MFECHA"]}
        </div>
        <div class="span1 campo">
            Prioridad:
        </div>
        <div class="span2">
            ${header["MPRIORI"]}
        </div>
    </div>

    <div class="row-fluid">
        <div class="span1 campo">
            De:
        </div>
        <div class="span2">
            ${header["MDE"]} (${header["MCREADOR"]})
        </div>
        <div class="span1 campo">
            Para:
        </div>
        <div class="span2">
            ${header["MPARA"]} (${header["MUSDES"]})
        </div>
    </div>
    <div class="row-fluid">
        <div class="span1 campo">
            Asunto:
        </div>
        <div class="span10">
            ${header["MASUNTO"]}
        </div>

    </div>

</div>
<div style="border-bottom: 1px solid black;padding-left: 50px;position: relative;margin-top: 20px;min-height: 150px;">
    <p class="css-vertical-text">Tramites</p>
    <div class="linea" style="height: 100px;"></div>
    <table  class="table table-bordered table-striped table-condensed table-hover" style="font-size: 10px !important;">
        <thead>
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
            <tr>
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
    $("#refresh").click(function(){
        if(confirm("Esta seguro, Esta acción puede tardar varios minutos.")){
            $("#dlgLoad").parent().css("zIndex","99999")
            $("#dlgLoad").parent().parent().css("zIndex","99998")
//            console.log($("#dlgLoad").parent(),$("#dlgLoad").parent().parent())
            $("#dlgLoad").dialog("open")
            $("#modal-tramite-body").html("");
            $.ajax({type : "POST", url : "${g.createLink(controller: 'tramite',action:'cargarDatos')}",
                data     : "",
                success  : function (msg) {
                    if(msg=="ok"){
                        $.ajax({
                            type    : "POST",
                            url     : "${g.createLink(action:'verTramitesAjax',controller: 'tramite')}/"+$("#memoRequerimiento").val(),
                            success : function (msg) {
                                $("#modal-tramite-body").html(msg);
                                $("#modal-tramite-body").show("slide")
                                $("#dlgLoad").dialog("close")
                            }
                        });
                    }else{
                        alert(msg)
                    }
                }
            });
        }
    });
</script>
