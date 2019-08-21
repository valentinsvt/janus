<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <meta name="layout" content="main"/>
    <title>Gráficos</title>
    <script src="${resource(dir: 'js', file: 'Chart.min.js')}"></script>
    <style type="text/css">

    .grafico {
        border-style: solid;
        border-color: #606060;
        border-width: 1px;
        width: 100%;
        float: left;
        text-align: center;
        height: auto;
        border-radius: 8px;
        margin: 10px;
    }

    .bajo {
        margin-bottom: 20px;
    }

    .centrado {
        text-align: center;
    }

    canvas {
        -moz-user-select: none;
        -webkit-user-select: none;
        -ms-user-select: none;
    }


    </style>
</head>

<body>
<div align="center">
    %{--<h1>Inversión en los Cantones</h1>--}%
    %{--<g:if test="${session.perfil.codigo != 'ADMG'}">--}%
    %{--<p style="font-size: 28px; color: rgba(63,113,186,0.9)">${seguridad.Persona.get(session.usuario.id)?.universidad?.nombre}</p>--}%
    %{--</g:if>--}%
</div>


<div class="btn btn-info graficar ">
    <i class="fa fa-pie-chart"></i> Inversión por Cantón
</div>

<div class="btn btn-info" id="graficar4">
    <i class="fa fa-pie-chart"></i> Inversión por Cantón (Pie)
</div>

<div class="btn btn-info" id="graficar2" style="margin-left: 2px">
    <i class="fa fa-pie-chart"></i> Tipos de Obra
</div>

<a href="#" class="btn btn-info" id="graficar3">
    <i class="fa fa-pencil"></i> Estado de Obras
</a>

<a href="#" class="btn btn-info" id="graficar5">
    <i class="fa fa-pencil"></i> Avance de Obras
</a>



<div class="col-md-5"></div>

<div style="background-color: #fdfdff" class="chart-container grafico" id="chart-area" hidden>
    <h3 id="titulo"></h3>

    <div id="graf">
        <canvas id="clases" style="margin-top: 20px"></canvas>
    </div>

</div>


<div style="width: 75%">
    <canvas id="canvas4"></canvas>
</div>


<script type="text/javascript">
    var canvas = $("#clases");
    var myChart;


    //barras
    $(".graficar").click(function () {
        $("#chart-area").removeClass('hidden');
        $(this).addClass("active");
        $("#graficar2,  #graficar3, #graficar4, #graficar5").removeClass("active");
        $.ajax({
            type: 'POST',
            url: '${createLink(controller: 'obrasRprt', action: 'tpobData')}',
            data: {cntn: 2},
            success: function (json) {
//                console.log("json:", json.cabecera)

                $("#titulo").html(json.titulo)
                $("#clases").remove();
                $("#chart-area").removeAttr('hidden')

                /* se crea dinámicamente el canvas y la función "click" */
                $('#graf').append('<canvas id="clases" style="margin-top: 30px"></canvas>');

                canvas = $("#clases")

                var chartData = {
//                    type: 'polarArea',
                    type: 'bar',
                    data: {
//                            labels: ['Evaluación Total', 'Potenciadores', 'Factores de Éxito', 'Cuellos de Botella', 'Recomendaciones'],
                        labels: json.cabecera.split(','),
                        datasets: [
                            {
                                backgroundColor: ["#20a5da", "#00af30", "#80ff80", "#d45840", "#be5882", "#80af30", "#d0bf80", "#4e68a2"],
//                                 backgroundColor: ["rgba(32,165,218,0.5)", "#00af30","#80ff80", "#d45840", "rgba(206,88,130,0.6)"],
//                                 data: [json.promedio, json.ptnv, json.fcex, json.ccbb, json.rcmn] }
                                data: json.datos.split(',')
                            }
                        ]
                    },
                    options: {
                        legend: {
                            display: false,
                            pointLabels: {
                                fontSize: 16
                            }
                        }

                    }
                };

                myChart = new Chart(canvas, chartData, 1);
            }
        });
    });


    $("#graficar2").click(function () {
        $("#chart-area").removeClass('hidden');
        $(this).addClass("active");
        $(".graficar,  #graficar3, #graficar4, #graficar5").removeClass("active");
        $.ajax({
            type: 'POST',
            url: '${createLink(controller: 'obrasRprt', action: 'cantones')}',
            data: {cntn: 2},
            success: function (json) {
//                console.log("json:", json.cabecera)

                $("#titulo").html(json.titulo)
                $("#clases").remove();
                $("#chart-area").removeAttr('hidden')

                /* se crea dinámicamente el canvas y la función "click" */
                $('#graf').append('<canvas id="clases" style="margin-top: 30px"></canvas>');

                canvas = $("#clases")

                var chartData = {
                    type: 'bar',
                    data: {
                        labels: json.cabecera.split(','),
                        datasets: [
                            {
                                label: 'Obras Viales',
                                backgroundColor: "#205060",
                                stack: 'Stack 1',
                                data: json.vias.split(',')
                            },
                            {
                                label: 'Infraestructura',
                                backgroundColor: "#d45840",
                                stack: 'Stack 1',
                                data: json.infra.split(',')
                            },
                            {
                                label: 'Obras de Riego',
                                backgroundColor: "#00af80",
                                stack: 'Stack 1',
                                data: json.riego.split(',')
                            }
                        ]
                    },
                    options: {
                        legend: {
                            display: true,
                            labels: {
                                fontColor: 'rgb(20, 80, 100)',
                                fontSize: 14
                            },
                            pointLabels: {
                                fontSize: 16
                            }
                        }

                    }
                };

                myChart = new Chart(canvas, chartData, 1);
            }
        });
    });


    $("#graficar3").click(function () {
        $("#chart-area").removeClass('hidden');
        $(this).addClass("active");
        $(".graficar,  #graficar2, #graficar4, #graficar5").removeClass("active");
        $.ajax({
            type: 'POST',
            url: "${createLink(controller: 'obrasRprt', action: 'estadosObras')}",
            data: {cntn: 2},
            success: function (json) {
//                console.log("json:", json.cabecera)

                $("#titulo").html(json.titulo)
                $("#clases").remove();
                $("#chart-area").removeAttr('hidden')

                /* se crea dinámicamente el canvas y la función "click" */
                $('#graf').append('<canvas id="clases" style="margin-top: 30px"></canvas>');

                canvas = $("#clases")

                var chartData = {
                    type: 'bar',
                    data: {
                        labels: json.cabecera.split(','),
                        datasets: [
                            {
                                label: 'Presupuestadas',
                                backgroundColor: "#205060",
                                stack: 'Stack 1',
//                                data: json.vias.split(',')
                                data: json.presupuestadas.split(',')
                            },
                            {
                                label: 'Contratadas',
                                backgroundColor: "#d45840",
                                stack: 'Stack 2',
                                data: json.contratadas.split(',')
                            },
                            {
                                label: 'En Construcción',
                                backgroundColor: "#00af80",
                                stack: 'Stack 3',
                                data: json.construccion.split(',')
                            },
                            {
                                label: 'Finalizadas',
                                backgroundColor: "#9a53af",
                                stack: 'Stack 4',
                                data: json.terminadas.split(',')
                            }
                        ]
                    },
                    options: {
                        legend: {
                            display: true,
                            labels: {
                                fontColor: 'rgb(20, 80, 100)',
                                fontSize: 14
                            },
                            pointLabels: {
                                fontSize: 16
                            }
                        }

                    }
                };

                myChart = new Chart(canvas, chartData, 1);
            }
        });
    });

    //pie
    $("#graficar4").click(function () {
        $("#chart-area").removeClass('hidden');
        $(this).addClass("active");
        $("#graficar2,  #graficar3, .graficar, #graficar5").removeClass("active");
        $.ajax({
            type: 'POST',
            url: '${createLink(controller: 'obrasRprt', action: 'tpobData')}',
            data: {cntn: 2},
            success: function (json) {
//                console.log("json:", json.cabecera)

                $("#titulo").html(json.titulo)
                $("#clases").remove();
                $("#chart-area").removeAttr('hidden')

                /* se crea dinámicamente el canvas y la función "click" */
                $('#graf').append('<canvas id="clases" style="margin-top: 30px"></canvas>');

                canvas = $("#clases")

                var chartData = {
                    type: 'pie',
                    data: {
//                            labels: ['Evaluación Total', 'Potenciadores', 'Factores de Éxito', 'Cuellos de Botella', 'Recomendaciones'],
                        labels: json.cabecera.split(','),
                        datasets: [
                            {
                                backgroundColor: ["#20a5da", "#00af30", "#80ff80", "#d45840", "#be5882", "#80af30", "#d0bf80", "#4e68a2"],
//                                 backgroundColor: ["rgba(32,165,218,0.5)", "#00af30","#80ff80", "#d45840", "rgba(206,88,130,0.6)"],
//                                 data: [json.promedio, json.ptnv, json.fcex, json.ccbb, json.rcmn] }
                                data: json.datos.split(',')
                            }
                        ]
                    },
                    options: {
                        legend: {
                            display: true,
                            pointLabels: {
                                fontSize: 16
                            }
                        }

                    }
                };

                myChart = new Chart(canvas, chartData, 1);
            }
        });
    });


    //grafico de avance
    $("#graficar5").click(function () {
        $("#chart-area").removeClass('hidden');
        $(this).addClass("active");
        $(".graficar,  #graficar2, #graficar3, #graficar4").removeClass("active");
        $.ajax({
            type: 'POST',
            url: "${createLink(controller: 'obrasRprt', action: 'avanceObras')}",
            data: {cntn: 2},
            success: function (json) {

                $("#titulo").html(json.titulo);
                $("#clases").remove();
                $("#chart-area").removeAttr('hidden');

                /* se crea dinámicamente el canvas y la función "click" */
                $('#graf').append('<canvas id="clases" style="margin-top: 30px"></canvas>');

                canvas = $("#clases")

                var chartData = {
                    type: 'bar',
                    data: {
                        labels: json.cabecera.split(','),
                        datasets: [
                            {
                                label: 'Obras Contratadas',
                                backgroundColor: "#205060",
                                stack: 'Stack 1',
                                data: json.contratado.split(',')
                            },
                            {
                                label: 'Avance económico',
                                backgroundColor: "#d45840",
                                stack: 'Stack 2',
                                data: json.economico.split(',')
                            },
                            {
                                label: 'Avance físico',
                                backgroundColor: "#00af80",
                                stack: 'Stack 3',
                                data: json.fisico.split(',')
                            }
                        ]
                    },
                    options: {
                        legend: {
                            display: true,
                            labels: {
                                fontColor: 'rgb(20, 80, 100)',
                                fontSize: 14
                            },
                            pointLabels: {
                                fontSize: 16
                            }
                        }

                    }
                };

                myChart = new Chart(canvas, chartData, 1);
            }
        });
    });


</script>

</body>
</html>