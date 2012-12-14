<div id="graf" class="graf"></div>

<script type="text/javascript">
    var plot = $.jqplot("graf", ${params.data},
            {
                title  : "${params.titulo}",
                axes   : {
                    xaxis : {
                        min   : 1,
                        ticks : ${params.tx},
                        pad   : 5.5
                    },
                    yaxis : {
                        min   : 0,
                        max   : ${params.m},
                        ticks : ${params.ty},
                        pad   : 5.5
                    }
                },
                series : [
                    {
                        color : '${params.color}'
                    }
                ]
            });
</script>