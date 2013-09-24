<%--
  Created by IntelliJ IDEA.
  User: fabricio
  Date: 9/24/13
  Time: 2:57 PM
--%>

<!DOCTYPE html>
<html>
<head>

    %{--<meta name="layout" content="main">--}%

    <title>Manual de Usuario - Reportes</title>
    <style>

    div {
        margin : auto;
        max-width: 800px;
    }

    #header2 {
        z-index          : 1;
        position         : relative;
        width            : 97.5%;
        height           : 60px;
        text-align       : center;
        background-color : #ffffff;
        margin-top       : 0px;
    }

    .centrado {
        text-align : center;
    }

    #indice {
        width            : 650px;
        position         : relative;
        background-color : #c8cac9;
        font-family      : Verdana, sans-serif;
        font-size        : 14px;
        color            : #000000;
        margin           : auto;
        text-align       : justify;
    }

    p {
        text-align : justify;
    }

    .cuadro {
        width  : 600px;
        margin : auto;
    }

    a {
        text-decoration : none;
        color           : #000000;
    }

    a:hover {
        font-weight : bold;
    }

    .cursiva {
        font-style : italic;
    }

    .regresa {
        float            : right;
        background-color : #c8cac9;
        width            : 120px;
        border           : 2px solid black;
        border-radius    : 10px;
        margin-right     : 40px;
    }

    .regresa p {
        text-align : center;
    }

    .izquierda {
        text-align : left;
    }

    .boton {
        position : relative;
        top      : 12px;
    }

    table tr {
        text-align : justify;
    }

    </style>
</head>

<body>
<div id="header2">
    <img src="${resource(dir: 'images/imagenesManuales/imagenesReportes', file: 'encabezado.png')}"/>
</div>

<div class="centrado">
    <br><br><br><br>
    <img src="${resource(dir: 'images/imagenesManuales/imagenesReportes', file: 'logo.png')}"/>
    <br><br>

    <h3>Sistema Integrado de Gesti&oacuten de Proyectos, Fiscalizaci&oacuten,
    Contrataci&oacuten y Ejecuci&oacuten de Obras</h3>
    <br><a id="volverIndice"></a>

    <h2>Manual del Usuario</h2>
</div>

<div id="indice">
    <h3>&Iacutendice de contenido</h3>

    <p><a href="#SistInt">Sistema Integrado de Gesti&oacuten de Proyectos,
    Fiscalizaci&oacuten, Contrataci&oacuten y Ejecuci&oacuten de Obras
    </a></p>
    <ol>
        <li><a href="#reportes">Reportes del Sistema</a></li>
        <ul>
            <li><a href="#reportes1">Reportes</a></li>
            <li><a href="#ingresadas">Obras Ingresadas</a></li>
            <ul>
                <li><a href="#registro">Ir al registro de la obra</a></li>
                <li><a href="#subpresupuesto">Imprimir Subpresupuesto</a></li>
            </ul>

            <li><a href="#presupuestadas">Obras Presupuestadas</a></li>
            <li><a href="#procesos">Procesos de Contrataci&oacute;n</a></li>
            <li><a href="#contratadas">Obras Contratadas</a></li>
            <li><a href="#contratos">Contratos</a></li>
            <li><a href="#contratistas">Contratistas</a></li>
            <li><a href="#aseguradoras">Aseguradoras</a></li>
            <li><a href="#garantias">Garantías</a></li>
            <li><a href="#transferencias">Transferencias y/o cheques pagados</a></li>
            <li><a href="#avance">Avance de Obras</a></li>
            <li><a href="#finalizadas">Obras Finalizadas</a></li>

        </ul>

    </ol>
</div>

<br> <a id="reportes"></a>

<div class="regresa">
    <p><a href="#volverIndice">Volver al &Iacutendice</a></p>
</div> <br>

<div>
    <br><br>

    <h2 class="cursiva">Reportes</h2>

    <p>
        Para generar los reportes de trámites se usan los botones que aparecen en la pantalla de lista de trámites.<br>

        El reporte de trámite en proceso no requiere de datos adicionales para su generación y simplemente aparecerá la pantalla de descarga del archivo generado del reporte.

    </p>

    <div class="centrado"><!-- pg 6 -->
        <img src="${resource(dir: 'images/imagenesManuales/imagenesReportes', file: 'img001.png')}"/>
    </div> <br>

    <p>
        Para el reporte de trámite por obra, es necesario primero seleccionar la obra a reportar.
    </p>

    <div class="centrado">
        <img src="${resource(dir: 'images/imagenesManuales/imagenesReportes', file: 'img002.png')}"/>
    </div> <br>

    <p></p>

    <p>
        Una vez seleccionada la obra presione Aceptar para que el reporte se genere.
        El combo muestra sólo la lista de obras que presentan trámites.
    </p>

    <div class="centrado"><!-- pg 7 -->
        <img src="${resource(dir: 'images/imagenesManuales/imagenesReportes', file: 'img003.png')}"/>
    </div> <br>

    <p>
        Para ingresar en la sección de reportes del Sistema, clic en “OBRAS” se desplegará un submenú; a continuación clic en “Reportes”.
    </p>


    <div class="centrado"><!-- pg 7 -->
        <img src="${resource(dir: 'images/imagenesManuales/imagenesReportes', file: 'img004.png')}" style="width: 800px"/>
    </div> <br>

</div>

<a id="ingresadas"></a>

<div class="regresa">
    <p><a href="#volverIndice">Volver al &Iacutendice</a></p>
</div>

<div>
    <h2>Obras Ingresadas</h2>
</div>

<div class="centrado">
    <p>Listado de obras que se hallan en el sistema,
    estas obras están en la fase inicial de estructuración de presupuestos
    y de documentos precontractuales. Estado = 'N' (No registrada)</p>

    <img src="${resource(dir: 'images/imagenesManuales/imagenesReportes', file: 'img005.png')}" style="width: 800px"/>

    <p>Mediante esta pantalla podemos buscar las obras que se hallan como no registradas en el sistema.</p>

    <p>El combo “Buscar Por” posee un conjunto de parámetros de búsqueda como son: Código, Nombre, Tipo,
    Cantón, Parroquia, Comunidad, Inspector, Revisor, Oficio de Ingreso, Oficio de Salida,
    Memorando de Salida, y Fórmula Polinómica.</p>

    <p>Escribimos el criterio a buscar y damos clic en el botón “Buscar”.</p>

    <img src="${resource(dir: 'images/imagenesManuales/imagenesReportes', file: 'img006.png')}"style="width: 800px"/>


    <p>
        Adicionalmente al dar clic derecho sobre la fila de cualquier obra,
        se desplegará un menú que consta de las siguientes dos acciones:
    </p>

    <img src="${resource(dir: 'images/imagenesManuales/imagenesReportes', file: 'img007.png')}" style="width: 800px"/>

</div>


<a id="registro"></a>

<div class="regresa">
    <p><a href="#volverIndice">Volver al &Iacutendice</a></p>
</div>

<div>
    <h2>Ir al Registro de la Obra</h2>
</div>

<div class="centrado">
    <p>Nos permite ir a la pantalla de registro de la obra seleccionada.</p>

    <img src="${resource(dir: 'images/imagenesManuales/imagenesReportes', file: 'img008.png')}" style="width: 800px"/>

</div>

<a id="subpresupuesto"></a>

<div>
    <h2>Imprimir Subpresupuesto</h2>
</div>

<div class="centrado">
    <p>Imprime los subpresupuestos pertenecientes a la obra seleccionada.</p>

    <img src="${resource(dir: 'images/imagenesManuales/imagenesReportes', file: 'img009.png')}" style="width: 800px"/>

</div>

<a id="presupuestadas"></a>

<div class="regresa">
    <p><a href="#volverIndice">Volver al &Iacutendice</a></p>
</div>

<div>
    <h2>Obras Presupuestadas</h2>
</div>

<div class="centrado">
    <p>
        Listado de obras que ya poseen un presupuesto elaborado y se hallan listas
        para entrar en el proceso de contratación. Estado = 'R'.
    </p>

    <img src="${resource(dir: 'images/imagenesManuales/imagenesReportes', file: 'img010.png')}" style="width: 800px"/>

    <p> El reporte nos presenta el conjunto de obras registradas en el sistema.</p>

    <img src="${resource(dir: 'images/imagenesManuales/imagenesReportes', file: 'img011.png')}" style="width: 800px"/>

</div>

<a id="procesos"></a>

<div class="regresa">
    <p><a href="#volverIndice">Volver al &Iacutendice</a></p>
</div>

<div>
    <h2>Procesos de Contratación</h2>
</div>

<div class="centrado">
    <p>
        Listado de procesos de contratación para la construcción de obras y para consultorías.
    </p>

    <img src="${resource(dir: 'images/imagenesManuales/imagenesReportes', file: 'img012.png')}" style="width: 800px"/>

    <p> El combo “Buscar por” posee un conjunto de parámetros de búsqueda como son: Código,  Objeto, Fecha Inicio, Presupuesto, Obra.

    El reporte nos presenta el conjunto de procesos de contratación para cada obra.
    </p>

    <img src="${resource(dir: 'images/imagenesManuales/imagenesReportes', file: 'img013.png')}" style="width: 800px"/>

</div>

<a id="contratadas"></a>

<div class="regresa">
    <p><a href="#volverIndice">Volver al &Iacutendice</a></p>
</div>

<div>
    <h2>Obras Contratadas</h2>
</div>

<div class="centrado">
    <p>
        Listado de obras que se encuentran contratadas.
    </p>

    <img src="${resource(dir: 'images/imagenesManuales/imagenesReportes', file: 'img014.png')}" style="width: 800px"/>

    <p> El combo “Buscar Por” posee un conjunto de parámetros de búsqueda como son: Código, Nombre, Tipo,
    Cantón, Parroquia, Comunidad, Inspector, Revisor, Oficio de Ingreso, Oficio de Salida, Memorando de Salida,
    y Fórmula Polinómica.

    El reporte nos presenta todas las obras contratadas.

    </p>

    <img src="${resource(dir: 'images/imagenesManuales/imagenesReportes', file: 'img015.png')}" style="width: 800px"/>

</div>

<a id="contratos"></a>

<div class="regresa">
    <p><a href="#volverIndice">Volver al &Iacutendice</a></p>
</div>

<div>
    <h2>Contratos</h2>
</div>

<div class="centrado">
    <p>
        Listado de contratos de obras y consultorías registrados en el sistema.
    </p>

    <img src="${resource(dir: 'images/imagenesManuales/imagenesReportes', file: 'img016.png')}" style="width: 800px"/>

    <p> El combo “Buscar Por” posee un conjunto de parámetros de búsqueda como son: N° Contrato,
    Memo, Fecha Suscripción, Tipo de contrato, Concurso, Obra, Nombre , Cantón Parroquia, Clase,
    Monto, Contratista, Tipo Plazo, Fecha Inicio, Fecha Fin.

    Al seleccionar Fecha Suscripción, Fecha Inicio o Fecha Fin, se habilitará el campo “Fecha” en donde
    podremos elegir la fecha a ser buscada.
    </p>

    <img src="${resource(dir: 'images/imagenesManuales/imagenesReportes', file: 'img017.png')}" style="width: 800px"/>

</div>

<a id="contratistas"></a>

<div class="regresa">
    <p><a href="#volverIndice">Volver al &Iacutendice</a></p>
</div>

<div>
    <h2>Contratistas</h2>
</div>

<div class="centrado">
    <p>
        Listado de contratistas que han firmado contratos de obras y consultoría con el GADPP.
    </p>

    <img src="${resource(dir: 'images/imagenesManuales/imagenesReportes', file: 'img018.png')}" style="width: 800px"/>

    <p> El combo “Buscar Por” nos permite seleccionar los contratistas ya sea por Nombre, Cédula, Especialidad.
    </p>

    <img src="${resource(dir: 'images/imagenesManuales/imagenesReportes', file: 'img019.png')}" style="width: 800px"/>

</div>


<a id="aseguradoras"></a>

<div class="regresa">
    <p><a href="#volverIndice">Volver al &Iacutendice</a></p>
</div>

<div>
    <h2>Aseguradoras</h2>
</div>

<div class="centrado">
    <p>
        Listado de aseguradoras que se hallan registradas en el sistema que han emitido garantías.
    </p>

    <img src="${resource(dir: 'images/imagenesManuales/imagenesReportes', file: 'img020.png')}" style="width: 800px"/>

    <p> Es posible filtrar los resultados ya sea por Nombre, Tipo, Teléfono, Fax, Contacto, Dirección; usando el combo “Buscar Por”.

    </p>

    <img src="${resource(dir: 'images/imagenesManuales/imagenesReportes', file: 'img021.png')}" style="width: 800px"/>

</div>

<a id="garantias"></a>

<div class="regresa">
    <p><a href="#volverIndice">Volver al &Iacutendice</a></p>
</div>

<div>
    <h2>Garantías</h2>
</div>

<div class="centrado">
    <p>

        Garantías registradas de los distintos contratos para obras y cosultoría; detalladas por contratos.
    </p>

    <img src="${resource(dir: 'images/imagenesManuales/imagenesReportes', file: 'img022.png')}" style="width: 800px"/>

    <p> El combo “Buscar Por” nos permite filtrar los resultados que se muestren en pantalla, el combo posee los siguientes
    parámetros de búsqueda: N° Contrato, Garantía, Renovación, Tipo de Garantía, Documento, Aseguradora, Contratista,
    Estado, Monto, Moneda, Emisión, Vencimiento, Días.
    </p><br>
    <p>
        Si se desea buscar por fecha elegimos en “Buscar Por”  los filtros: Emisión o Vencimiento,
        y se habilitará “Fecha” la cual nos permitirá seleccionar una fecha para realizar la búsqueda.
    </p>

    <img src="${resource(dir: 'images/imagenesManuales/imagenesReportes', file: 'img023.png')}" style="width: 800px"/>

</div>

<a id="transferencias"></a>

<div class="regresa">
    <p><a href="#volverIndice">Volver al &Iacutendice</a></p>
</div>

<div>
    <h2>Transferencias y/o cheques pagados</h2>
</div>

<div class="centrado">
    <p>

        Listado de pagos realizados a partir de la solicitud de pagos relativos a las obras.
    </p>

    <img src="${resource(dir: 'images/imagenesManuales/imagenesReportes', file: 'img024.png')}" style="width: 800px"/>

    <img src="${resource(dir: 'images/imagenesManuales/imagenesReportes', file: 'img025.png')}" style="width: 800px"/>

</div>


<a id="avance"></a>

<div class="regresa">
    <p><a href="#volverIndice">Volver al &Iacutendice</a></p>
</div>

<div>
    <h2>Avance de Obras</h2>
</div>

<div class="centrado">
    <p>

        Listado de obras con el respectivo porcentaje de avance.
    </p>

    <img src="${resource(dir: 'images/imagenesManuales/imagenesReportes', file: 'img026.png')}" style="width: 800px"/>

    <p>
        Los filtros de búsqueda en “Buscar Por”  son: Código, Nombre, Tipo, Cantón, Parroquia,  Comunidad, Número de Contrato, Contratista.
    </p><br>

    <img src="${resource(dir: 'images/imagenesManuales/imagenesReportes', file: 'img027.png')}" style="width: 800px"/>

</div>

<a id="finalizadas"></a>

<div class="regresa">
    <p><a href="#volverIndice">Volver al &Iacutendice</a></p>
</div>

<div>
    <h2>Obras Finalizadas</h2>
</div>

<div class="centrado">
    <p>

        Listado de obras finalizadas.
    </p>

    <img src="${resource(dir: 'images/imagenesManuales/imagenesReportes', file: 'img028.png')}" style="width: 800px"/>

    <p>
        Los filtros de búsqueda nos permiten buscar por Código, Nombre, Descripción,
        Memo Ingreso, Memo Salida, Sitio, Plazo, Parroquia, Comunidad, Dirección, Fecha.

    </p><br>

    <img src="${resource(dir: 'images/imagenesManuales/imagenesReportes', file: 'img029.png')}" style="width: 800px"/>

</div>












</body>
</html>
