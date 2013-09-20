<%--
  Created by IntelliJ IDEA.
  User: fabricio
  Date: 9/19/13
  Time: 3:56 PM
--%>

<!DOCTYPE html>
<html>
<head>

    %{--<meta name="layout" content="main">--}%

    <title>Manual de Usuario</title>
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
    <img src="${resource(dir: 'images/imagenesManuales/imagenesIngreso', file: 'encabezado.png')}"/>
</div>

<div class="centrado">
    <br><br><br><br>
    <img src="${resource(dir: 'images/imagenesManuales/imagenesIngreso', file: 'logo.png')}"/>
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
        <li><a href="#IngresoSistema">Ingreso al Sistema</a></li>
        %{--<li><a href="#ModAdmin">M&oacutedulo de Administraci&oacuten del Sistema</a></li>--}%
        %{--<ul>--}%
        %{--<li><a href="#ManParam">Manejo de par&aacutemetros</a></li>--}%
        %{--<li><a href="#ManPer">Manejo de perfiles y permisos</a></li>--}%
        %{--<ul>--}%
        %{--<li><a href="#GestPerm">Gesti&oacuten de Permisos y M&oacutedulos</a></li>--}%
        %{--<li><a href="#GestUsuario">Gesti&oacuten de Usuarios</a></li>--}%
        %{--</ul>--}%
        %{--</ul>--}%
        %{--<li><a href="#ModAnalisis">M&oacutedulo de An&aacutelisis de Precios Unitarios</a>--}%
        %{--</li>--}%
        %{--<ul>--}%
            %{--<li><a href="#RegMateriales">Registro de Materiales, Mano de Obra y Equipos--}%
            %{--</a></li>--}%
            %{--<li><a href="#PrecMant">Precios y Mantenimiento de &Iacutetems</a></li>--}%
            %{--<li><a href="#AnPrecios">An&aacutelisis de precios unitarios: Rubros</a></li>--}%
            %{--<li><a href="#BarraCom">Barra de comandos</a></li>--}%
        %{--</ul>--}%
        <li><a href="#RegObras">Registro de Obras</a></li>
        <ul>
            <li><a href="#Lista">Lista</a></li>
            <li><a href="#Nuevo">Nuevo</a></li>
            <li><a href="#Grabar">Grabar</a></li>
            <li><a href="#Cancelar">Cancelar</a></li>
            <li><a href="#EliminarObra">Eliminar la Obra</a></li>
            <li><a href="#Imprimir">Imprimir</a></li>
            <li><a href="#CambiarEstado">Cambiar de Estado</a></li>
            <li><a href="#CopiarObra">Copiar Obra</a></li>
            <li><a href="#CopiarOferentes">Copiar Obra a Oferentes</a></li>
            <li><a href="#RegDatObra">Registro de datos de la obra</a></li>
            <li><a href="#BotonInferior">Registro de obras &#45 Barra de botones inferior
            </a></li>
            <li><a href="#BotonInferior">Variables</a></li>
            <li><a href="#VolObra">Vol&uacutemenes de Obra</a></li>
            <li><a href="#MatrizFormula">Matriz de la F&oacutermula Polin&oacutemica</a></li>
            <li><a href="#FormulaPoli">F&oacutermula polin&oacutemica</a></li>
            <li><a href="#TrabajoPoli">Trabajando en la composici&oacuten de la f&oacutermula
            polin&oacutemica</a></li>
            <li><a href="#RubrObra">Rubros de la obra</a></li>
            <li><a href="#Cronograma">Cronograma</a></li>
            <li><a href="#CompObra">Composici&oacuten de la Obra</a></li>
            <li><a href="#DocObra">Documentos de la Obra</a></li>
        </ul>
    </ol>
</div>


%{--<a id="SistInt"></a>--}%

%{--<div><!--Sistema Integrado de Gestion de Proyectos... pg 3, 4, 5-->--}%
%{--<br><br><br>--}%

%{--<h2 class="centrado">Sistema Integrado de Gesti&oacuten de Proyectos,--}%
%{--Fiscalizaci&oacuten, Contrataci&oacuten y Ejecuci&oacuten de Obras</h2>--}%

%{--<p>El Sistema Integrado de Gesti&oacuten de Proyectos, Fiscalizaci&oacuten,--}%
%{--Contrataci&oacuten y Ejecuci&oacuten de Obras, denominado JANUS es multiusuario,--}%
%{--permite el monitoreo de todo el ciclo de vida de una Obra P&uacuteblica, el mismo--}%
%{--que se inicia con el registro de la Obra, los estudios preliminares y precontractuales--}%
%{--necesarios para determinar los vol&uacutemenes de obra, el presupuesto y llamar--}%
%{--a un proceso de ofertas donde la mejor de ellas da lugar a un contrato de--}%
%{--ejecuci&oacuten de la Obra. Con el contrato se establecen el cronograma valorado--}%
%{--de trabajo, la f&oacutermula polin&oacutemica de reajuste de precios, garant&iacuteas--}%
%{--y dem&aacutes condiciones que regular&aacuten la ejecuci&oacuten de la obra. En--}%
%{--esta fase se hace el control de pago de planillas, el control de vol&uacutemenes--}%
%{--de obra ejecutados, valores de reajuste, liquidaciones, la recepci&oacuten provisional--}%
%{--y la recepci&oacuten definitiva de la Obra.--}%
%{--</p>--}%

%{--<div class="centrado"><br>--}%
%{--<img src="${resource(dir: 'images/imagenesManuales/imagenesIngreso', file: 'imagen101.png')}"/>--}%
%{--</div> <br>--}%

%{--<p>Adicionalmente el sistema puede operar en forma independiente con tres bases--}%
%{--de datos distintas para contrataci&oacuten de obras, administraci&oacuten directa--}%
%{--y consultor&iacuteas. Adem&aacutes contar&aacute con un utilitario que permita--}%
%{--obtener informaci&oacuten de las tres bases de datos e integrarlas en reportes--}%
%{--que pueden servir como insumo para el an&aacutelisis y cuantificaci&oacuten de--}%
%{--la gesti&oacuten realizada.--}%
%{--</p>--}%

%{--<p>El sistema contempla los siguientes componentes:</p><br>--}%
%{--<ul>--}%
%{--<li>Administraci&oacuten del sistema Inform&aacutetico</li>--}%
%{--<li>Costos y an&aacutelisis de precios</li>--}%
%{--<li>Obras y consultor&iacuteas</li>--}%
%{--<li>Direcci&oacuten de compras P&uacuteblicas</li>--}%
%{--<li>Administraci&oacuten de contratos</li>--}%
%{--<li>Fiscalizaci&oacuten</li>--}%
%{--<li>Tesorer&iacutea y garant&iacuteas</li>--}%
%{--<li>Reportes para secretar&iacutea general, prefectura y gerenciales</li>--}%
%{--<li>Oferentes ganadores</li>--}%
%{--<li>Gesti&oacuten de procesos o control de tr&aacutemite y flujo de trabajo.</li>--}%
%{--</ul>--}%
%{--<!-- pg 4 --><br>--}%

%{--<p>El proceso de registro de informaci&oacuten en el sistema conforme las--}%
%{--necesidades del GADPP es el siguiente:</p>--}%

%{--<p>El GADPP recibe las solicitudes de obra, estas obras potenciales son analizadas--}%
%{--por los organismos pertinentes para determinar si se ejecutan o no, el sistema no--}%
%{--registrar&iacutea las obras solicitadas que no se vayan a ejecutar (se puede sin--}%
%{--embargo registrar todas, y s&oacutelo las obras a ejecutarse seguir&iacutean el--}%
%{--proceso normal). Dentro del organigrama del GADPP figuran:--}%
%{--</p> <br>--}%

%{--<div class="centrado">--}%
%{--<img src="${resource(dir: 'images/imagenesManuales/imagenesIngreso', file: 'imagen102.png')}"/>--}%
%{--</div> <br>--}%

%{--<p>Las Unidades de Infraestructura para el Desarrollo, Gesti&oacuten de Vialidad--}%
%{--y Gesti&oacuten de Riego reciben los proyectos de Obra seg&uacuten su competencia.--}%
%{--</p>--}%

%{--<p>Si la Obra es aprobada,  pasa al proceso de Estudios donde se definen los--}%
%{--Vol&uacutemenes de Obra, y en funci&oacuten de estos se determina su presupuesto,--}%
%{--cronograma, f&oacutermula polin&oacutemica y la cuadrilla tipo. Estos resultados--}%
%{--conforman el presupuesto referencial y las bases t&eacutecnicas para el contrato.--}%
%{--</p>--}%

%{--<p>Seguidamente se entra en el proceso de contrataci&oacuten el cual termina con--}%
%{--el registro del contrato, lo que da paso al pago del anticipo y la iniciaci&oacuten--}%
%{--de la obra.--}%
%{--</p>--}%

%{--<p>Una vez perfeccionado el contrato se procede al pago del anticipo previa--}%
%{--asignaci&oacuten de una partida y recepci&oacuten de garant&iacuteas, conforme--}%
%{--la solicitud de pago adjunta al reajuste provisional del anticipo seg&uacuten la--}%
%{--f&oacutermula polin&oacutemica.--}%
%{--</p>--}%

%{--<p>Realizado el pago del anticipo se notifica al constructor e informa a--}%
%{--Fiscalizaci&oacuten la orden de inicio de obra. La fecha de pago marca el inicio--}%
%{--de obra y es la base para el control del pago de planillas de avance de obra,--}%
%{--reajuste y cumplimiento de plazos, lo cual puede derivar en la aplicaci&oacuten--}%
%{--de multas y sanciones de ser el caso.--}%
%{--</p>--}%
%{--<!--pg 5-->--}%
%{--<p>El contrato registra tambi&eacuten el cronograma valorado de avance de obra,--}%
%{--este cronograma se organiza en per&iacuteodos mensuales, donde se detalla rubro--}%
%{--por rubro los vol&uacutemenes de obra a ejecutarse, lo cual sirve de base para--}%
%{--los c&aacutelculos de reajuste de precios y el pago de las planillas.--}%
%{--</p>--}%

%{--<p>La ejecuci&oacuten de la obra genera las planillas, las mismas que pueden ser--}%
%{--presentadas  por rubros que no figuran en el contrato o en cantidades superiores--}%
%{--a las detallas en el detalle de vol&uacutemenes de obra. Solo se pagan planillas--}%
%{--de rubros contractuales y en las cantidades contratadas, si existen cantidades--}%
%{--adicionales o rubros adicionales estos se pagan en una planilla de liquidaci&oacuten--}%
%{--si no supera el porcentaje que determina la ley.--}%
%{--</p>--}%

%{--<div class="centrado">--}%
%{--<img src="${resource(dir: 'images/imagenesManuales/imagenesIngreso', file: 'imagen103.png')}" style="width: 800px; height: 600px"/>--}%
%{--</div> <br>--}%

%{--<p>La recepci&oacuten de la obra provisional y definitiva se registran en el--}%
%{--sistema con sus respectivas fechas, esta informaci&oacuten sirve para generar--}%
%{--estad&iacutesticas y reportes de cumplimiento y gesti&oacuten de obras.--}%
%{--</p>--}%
%{--</div>--}%
<br> <a id="IngresoSistema"></a>

<div class="regresa">
    <p><a href="#volverIndice">Volver al &Iacutendice</a></p>
</div> <br>

<div><!--Ingreso al Sistema, pg 5,6,7-->
    <br><br>

    <h2 class="cursiva">Ingreso al Sistema</h2>

    <p>El sistema funciona en la plataforma web, de modo que puede ser accedido
    desde dentro del GADPP utilizando una intranet o desde el Internet.
        <br>Para poder ingresar al sistema colocamos el nombre de usuario y password
    en los campos solicitados, a continuaci&oacuten damos clic en &#34Continuar&#34.
    </p>

    <div class="centrado"><!-- pg 6 -->
        <img src="${resource(dir: 'images/imagenesManuales/imagenesIngreso', file: 'imagen104.png')}"/>
    </div> <br>

    <p>Si el usuario ha olvidado su contrase&#241a es posible solicitar una nueva
    contrase&#241a al hacer clic en la parte inferior &#34Olvid&oacute su
    contrase&#241a?&#34; ingrese su correo electr&oacutenico y haga clic en el
    bot&oacuten &#34Aceptar&#34. El sistema responder&aacute enviando a su correo
    una nueva contrase&#241a para que pueda ingresar.
    </p>

    <div class="centrado">
        <img src="${resource(dir: 'images/imagenesManuales/imagenesIngreso', file: 'imagen105.png')}"/>
    </div> <br>

    <p>Una vez que ha ingresado en el sistema este le pedir&aacute que escoja su
    perfil de usuario. Los perfiles se definen en el m&oacutedulo de
    administraci&oacuten del sistema y generalmente existe un &uacutenico perfil
    para cada usuario, con excepci&oacuten de aquellos que pueden hacer tareas de
    administraci&oacuten en el sistema. Seleccione su perfil haga clic en
    &#34Entrar&#34 para continuar.
    </p>

    <div class="centrado"><!-- pg 7 -->
        <img src="${resource(dir: 'images/imagenesManuales/imagenesIngreso', file: 'imagen106.png')}"/>
    </div> <br>

    <p>La pantalla de bienvenida presenta est&aacute formada por un men&uacute
    donde aparece como t&iacutetulo el m&oacutedulo o secci&oacuten en la que se
    halla trabajando, varias opciones de men&uacute y el tiempo de sesi&oacuten que
    le resta para que el sistema responda a los comandos ingresados sin necesidad de
    volver a conectar.
    </p>

    <p>El tiempo de la sesi&oacuten se define por cuestiones de seguridad de modo
    que si se abandona el   computador por alguna raz&oacuten, no hayan intromisiones
    ni accesos no autorizados. El sistema por defecto fija en 20 minutos el tiempo
    de sesi&oacuten, y corresponde al per&iacuteodo luego del cual el sistema expulsa
    al usuario del sistema si no existe actividad usuario&#45sistema. Este tiempo
    puede ser modificado por el administrador del sistema.
    </p> <br>

    <div class="centrado">
        <img src="${resource(dir: 'images/imagenesManuales/imagenesIngreso', file: 'imagen107.png')}"/>
    </div> <br> <br>
</div>

%{--<a id="ModAdmin"></a>--}%

%{--<div class="regresa">--}%
    %{--<p><a href="#volverIndice">Volver al &Iacutendice</a></p>--}%
%{--</div>--}%


%{--<br>--}%

%{--<div><!--Modulo de Administracion del Sistema pg 7,8 -->--}%
    %{--<br><br>--}%

    %{--<h2 class="cursiva">M&oacutedulo de Administraci&oacuten del Sistema</h2>--}%

    %{--<p>Los usuarios potenciales de este m&oacutedulo son el departamento de--}%
    %{--Gesti&oacuten de Tecnolog&iacutea. Su prop&oacutesito es encargarse del manejo--}%
    %{--y la administraci&oacuten de los par&aacutemetros iniciales o datos tipo que--}%
    %{--permitan en buen de funcionamiento del sistema. Entre los procesos m&aacutes--}%
    %{--importantes est&aacute la gesti&oacuten de los usuarios y perfiles, el acceso--}%
    %{--a la auditoria de datos y el control de seguridades den general.--}%
    %{--</p>--}%

    %{--<p>Los par&aacutemetros del sistema que se manejan en este m&oacutedulo son:</p>--}%
    %{--<ul>--}%
        %{--<li>Distribuci&oacuten geogr&aacutefica</li>--}%
        %{--<li>Partidas presupuestarias</li>--}%
        %{--<li>Tipo de obras</li>--}%
        %{--<li>Estado de obras</li>--}%
        %{--<li>Clase de obras</li>--}%
        %{--<li>Tipo de &iacutetem</li>--}%
        %{--<li>Unidades</li>--}%
        %{--<li>Grupos de &iacutetems</li>--}%
        %{--<li>Tipos de contrato</li>--}%
        %{--<li>Tipos de plazo</li>--}%
        %{--<li>Tipos de tr&aacutemite</li>--}%
        %{--<li>Tipos de planilla</li>--}%
        %{--<li>Estado de planilla</li>--}%
        %{--<li>Tipo de multa</li>--}%
        %{--<li>Tipo de pr&oacuterroga</li>--}%
        %{--<li>Estados de la garant&iacutea</li>--}%
        %{--<li>Tipos de garant&iacutea</li>--}%
    %{--</ul> <br>--}%

    %{--<p>Los procesos de administraci&oacuten y registro de informaci&oacuten son:</p>--}%
    %{--<!-- pg 8 -->--}%
    %{--<ul>--}%
        %{--<li>Registro de Personal</li>--}%
        %{--<li>Creaci&oacuten y mantenimiento usuarios.</li>--}%
        %{--<li>Asignaci&oacuten de los perfiles de usuario.</li>--}%
        %{--<li>Determinaci&oacuten de los ambientes de trabajo por perfil.</li>--}%
        %{--<li>Fijaci&oacuten de permisos de ejecuci&oacuten de cada acci&oacuten del--}%
        %{--sistema.</li>--}%
        %{--<li>Auditor&iacutea de datos y trazabilidad.</li>--}%
    %{--</ul>--}%
    %{--<br> <a id="ManParam"></a>--}%

    %{--<p>Tambi&eacuten existen algunos reportes de administraci&oacuten con--}%
    %{--informaci&oacuten de usuarios y perfiles. <br>Las opciones de men&uacute--}%
    %{--del m&oacutedulo de administraci&oacuten son: acciones, par&aacutemetros y--}%
    %{--usuarios.--}%
    %{--</p>--}%
    %{--<br><br>--}%
%{--</div>--}%


%{--<div class="regresa">--}%
    %{--<p><a href="#volverIndice">Volver al &Iacutendice</a></p>--}%
%{--</div>--}%
%{--<br>--}%

%{--<div><!--Manejo de Parametros pg 8,9, 10, 11, 12-->--}%
    %{--<br>--}%

    %{--<h2>Manejo de par&aacutemetros</h2>--}%

    %{--<p>Para que el sistema pueda ser utilizado es necesario contar con una serie--}%
    %{--de par&aacutemetros sobre los cuales se ha de ir construyendo los datos e--}%
    %{--informaci&oacuten para los distintos m&oacutedulos. <br>Los par&aacutemetros se--}%
    %{--hallan organizados en grupos de acuerdo a su afinidad, as&iacute, existen--}%
    %{--par&aacutemetros generales, de obras, contrataci&oacuten y de ejecuci&oacuten.--}%
    %{--</p>--}%

    %{--<p>Los par&aacutemetros generales son:</p>--}%
    %{--<ul>--}%
        %{--<li>Divisi&oacuten geogr&aacutefica del Pa&iacutes en cantones, parroquias--}%
        %{--y comunidades.</li>--}%
        %{--<li>Tipo de &iacutetem para diferenciar entre &iacutetems y rubros.</li>--}%
        %{--<li>Unidades de medida para los materiales, mano de obra y equipos.</li>--}%
        %{--<li>Grupos de &iacutetems para clasificar entre materiales, mano de obra y--}%
        %{--equipos.</li>--}%
        %{--<li>Transporte para diferenciar los &iacutetems que participan en el--}%
        %{--transporte.</li>--}%
        %{--<li>Coordinaci&oacuten del personal para la organizaci&oacuten de los--}%
        %{--usuarios.</li>--}%
        %{--<li>Tipo de Usuario o de Personal, para usarse en la designaci&oacuten--}%
        %{--de los distintos responsables de obras.</li>--}%
        %{--<li>Funciones del personal que pueden desempe&#241ar en la construcci&oacuten--}%
        %{--de la obra o en los distintos momentos de la contrataci&oacuten y--}%
        %{--ejecuci&oacuten de obras.</li>--}%
        %{--<li>Tipo de Indice seg&uacuten el INEC.</li>--}%
        %{--<li>Tipo de Tr&aacutemite.</li>--}%
        %{--<li>Rol de la persona en el Tr&aacutemite.</li>--}%
    %{--</ul> <br>--}%

    %{--<p>Par&aacutemetros de obras:</p> <!-- pg 9 -->--}%
    %{--<ul>--}%
        %{--<li>Tipo de Obras a ejecutarse en un proyecto.</li>--}%
        %{--<li>Clase de Obra para distinguir entre varios clases de obra civiles,--}%
        %{--viales y otras.</li>--}%
        %{--<li>Partida Presupuestaria con la cual se financia o construye a obra.</li>--}%
        %{--<li>Estado de la Obra que distingue las distintas fases de contrataci&oacuten--}%
        %{--y ejecuci&oacuten de la obra.</li>--}%
        %{--<li>Programa del cual forma parte una obra.</li>--}%
        %{--<li>Tipo de f&oacutermula polin&oacutemica de reajuste de precios que puede--}%
        %{--tener un contrato.</li>--}%
        %{--<li>Par&aacutemetros de costos indirectos y valores de los indices.</li>--}%
        %{--<li>Textos fijos para la generaci&oacuten de los documentos precontractuales.</li>--}%
    %{--</ul> <br>--}%

    %{--<p>Par&aacutemetros de contrataci&oacuten:</p>--}%
    %{--<ul>--}%
        %{--<li>Tipo de contrato que puede registrarse en el sistema para la ejecuci&oacuten--}%
        %{--de una Obra.</li>--}%
        %{--<li>Tipo de Garant&iacutea que se puede recibir en un contrato.</li>--}%
        %{--<li>Tipo de documento de garant&iacutea que se puede recibir para garantizar--}%
        %{--las distintas estipulaciones de una contrato.</li>--}%
        %{--<li>Estado de la garant&iacutea dentro del per&iacuteodo contractual.</li>--}%
        %{--<li>Moneda en la cual se recibe la garant&iacutea.</li>--}%
        %{--<li>Tipo de aseguradora que emite la garant&iacutea.</li>--}%
        %{--<li>Aseguradora o instituci&oacuten bancaria que emite la garant&iacutea.</li>--}%
        %{--<li>Unidad del Item.</li>--}%
        %{--<li>Tipo de Procedimiento.</li>--}%
        %{--<li>Tipo de Compra.</li>--}%
    %{--</ul> <br>--}%

    %{--<p>Par&aacutemetros de ejecuci&oacuten:</p>--}%
    %{--<ul>--}%
        %{--<li>Estado de la planilla que puede tener dentro del proceso de ejecuci&oacuten--}%
        %{--de la obra: ingresada, pagada, anulada.</li>--}%
        %{--<li>Tipo de planilla que puede tener el proceso de ejecuci&oacuten de la obra:--}%
        %{--anticipo, liquidaci&oacuten, avance de obra, reajuste, etc.</li>--}%
        %{--<li>Descuentos que se aplican a cada tipo de planilla.</li>--}%
        %{--<li>Tipo de multa que se puede aplicar a una planilla.</li>--}%
    %{--</ul> <br>--}%

    %{--<p>El sistema presenta una interfaz &uacutenica para el manejo de los--}%
    %{--par&aacutemetros, organizados en grupos por cada uno de estos tipos. A la--}%
    %{--derecha de ventana que contiene los par&aacutemetros se despliega otra de--}%
    %{--explicaci&oacuten de cada uno de ellos que describe mas detalladamente su--}%
    %{--concepto, uso y a veces presenta ejemplos.--}%
    %{--</p>--}%
    %{--<br><br> <!--pg 10 -->--}%
    %{--<div class="centrado">--}%
        %{--<img src="${resource(dir: 'images/imagenesManuales/imagenesIngreso', file: 'imagen109.png')}"/>--}%
    %{--</div>--}%
    %{--<br><br>--}%

    %{--<p>Si se hace un clic en el texto subrayado de la lista de par&aacutemetros--}%
    %{--aparecer&aacute la pantalla con los datos registrados y la posibilidad de--}%
    %{--a&#241adir o editar esos datos. Por ejemplo al hacer clic en Coordinaci&oacuten--}%
    %{--del personal se muestra.--}%
    %{--</p>--}%
    %{--<br><br>--}%

    %{--<div class="centrado">--}%
        %{--<img src="${resource(dir: 'images/imagenesManuales/imagenesIngreso', file: 'imagen110.png')}"/>--}%
    %{--</div>--}%
    %{--<br><br>--}%

    %{--<p>En la zona inferior aparece un enlace para la siguiente p&aacutegina de datos--}%
    %{--en el caso de que no se hayan desplegado todos.<br> Si se desea a&#241adir un--}%
    %{--nuevo departamento se debe hacer un clic en el bot&oacuten &#34Crear--}%
    %{--Coordinaciones&#34. Para editar la informaci&oacuten ingresada se debe hacer un--}%
    %{--clic en el &iacutecono &#34Editar&#34. Tambi&eacuten existen los &iacuteconos ver--}%
    %{--y eliminar.--}%
    %{--</p>--}%
    %{--<br><br> <!-- pg 11 -->--}%
    %{--<div class="centrado">--}%
        %{--<img src="${resource(dir: 'images/imagenesManuales/imagenesIngreso', file: 'imagen111.png')}"/>--}%
    %{--</div>--}%

    %{--<p>Par&aacutemetros de Obras:</p>--}%
    %{--<br>--}%

    %{--<div class="centrado">--}%
        %{--<img src="${resource(dir: 'images/imagenesManuales/imagenesIngreso', file: 'imagen112.png')}"/>--}%
    %{--</div>--}%
    %{--<br><br>--}%

    %{--<p>Par&aacutemetros de contrataci&oacuten:</p>--}%
    %{--<br>--}%

    %{--<div class="centrado">--}%
        %{--<img src="${resource(dir: 'images/imagenesManuales/imagenesIngreso', file: 'imagen113.png')}"/>--}%
    %{--</div>--}%

    %{--<p>Par&aacutemetros de ejecuci&oacuten:</p> <!-- pg 12 -->--}%
    %{--<br><br>--}%

    %{--<div class="centrado">--}%
        %{--<img src="${resource(dir: 'images/imagenesManuales/imagenesIngreso', file: 'imagen114.png')}"/>--}%
    %{--</div> <br><br>--}%
%{--</div>--}%

%{--<a id="ManPer"></a>--}%

%{--<div class="regresa">--}%
    %{--<p><a href="#volverIndice">Volver al &Iacutendice</a></p>--}%
%{--</div> <br>--}%


%{--<div><!-- Manejo de perfiles y permisos pg 12, 13, 14, 15-->--}%
    %{--<br><br>--}%

    %{--<h2>Manejo de perfiles y permisos</h2>--}%

    %{--<p>Desde esta secci&oacuten se administran las opciones que aparecen en el--}%
    %{--men&uacute del usuario de acuerdo a cada perfil que se haya definido en el--}%
    %{--sistema. La pantalla de esta secci&oacuten es:--}%
    %{--</p>--}%

    %{--<div class="centrado">--}%
        %{--<img src="${resource(dir: 'images/imagenesManuales/imagenesIngreso', file: 'imagen115.png')}"/>--}%
    %{--</div>--}%
    %{--<br><br>--}%

    %{--<p>Los elementos de esta pantalla se describen a continuaci&oacuten:</p> <br>--}%

    %{--<p><strong>Tipo de acci&oacuten:</strong> permite seleccionar entre Men&uacute--}%
    %{--y Proceso, donde men&uacute se refiere a las opciones que pueden aparecer en el--}%
    %{--men&uacute de usuario (secci&oacuten izquierda de la pantalla) y los procesos a--}%
    %{--cada acci&oacuten que puede ejecutar un usuario en el sistema.--}%
    %{--</p>--}%

    %{--<p><strong>Gestionar Permisos y M&oacutedulos:</strong>sirve para acceder a la--}%
    %{--pantalla de gesti&oacuten de los permisos asignados a cada perfil de usuario que--}%
    %{--se detalla m&aacutes adelante.--}%
    %{--</p>--}%

    %{--<p><strong>Cargar Controladores y Cargar Acciones:</strong>se trata de comandos--}%
    %{--de uso interno del sistema.--}%
    %{--</p>--}%

    %{--<p>La segunda fila de botones corresponde a los m&oacutedulos del sistema, que--}%
    %{--se hallan dispuestos de acuerdo al orden definido por el administrador (ver--}%
    %{--secci&oacuten Permisos y M&oacutedulos). Estos botones de los m&oacutedulos nos--}%
    %{--muestran al hacer clic en ellos las opciones de men&uacute o los procesos que--}%
    %{--contienen, dependiendo del tipo de acci&oacuten seleccionado. El color celeste--}%
    %{--indica el bot&oacuten seleccionado, tanto del tipo de acci&oacuten como del--}%
    %{--m&oacutedulo.--}%
    %{--</p> <br><br>--}%

    %{--<div class="centrado">--}%
        %{--<img src="${resource(dir: 'images/imagenesManuales/imagenesIngreso', file: 'imagen116.png')}"/>--}%
    %{--</div>--}%
    %{--<br> <!-- pg 14 -->--}%
    %{--<p>Al hacer clic en el m&oacutedulo se despliegan sus las acciones junto con--}%
    %{--los comandos: &#34Eliminar del M&oacutedulo&#34, &#34Cambiar Men&uacute <-->--}%
    %{--Proceso&#34.--}%
    %{--</p>--}%
    %{--<br>--}%

    %{--<p><strong>Eliminar del M&oacutedulo:</strong>Rompe la asociaci&oacuten de--}%
    %{--esta acci&oacuten con el men&uacute actual. Las acciones no pueden borrarse--}%
    %{--puesto que comprenden la estructura del sistema, s&oacutelo se las puede asociar--}%
    %{--a un m&oacutedulo u otro.--}%
    %{--</p>--}%

    %{--<p><strong>Cambiar Men&uacute <--> Proceso:</strong>Cambia la acci&oacuten que--}%
    %{--actualmente pertenece al Men&uacute a una acci&oacuten del tipo proceso o--}%
    %{--viceversa. La acci&oacuten es cambiada de tipo pero permanece asociada al--}%
    %{--m&oacutedulo.--}%
    %{--</p>--}%

    %{--<p>En el caso de que se haga clic en el m&oacutedulo &#34noAsignado&#34, el--}%
    %{--comando &#34Eliminar del M&oacutedulo&#34 es reemplazado por un combo desde el--}%
    %{--cual se puede seleccionar un m&oacutedulo y el comando &#34Agregar al--}%
    %{--M&oacutedulo&#34.--}%
    %{--</p>--}%

    %{--<p>Tanto las acciones de men&uacute como las de proceso que no se hallan--}%
    %{--asignados a un m&oacutedulo se muestran bajo el m&oacutedulo llamado--}%
    %{--&#34noAsignado&#34, de esta manera, si una acci&oacuten es &#34eliminada&#34 de--}%
    %{--un m&oacutedulo, el sistema la env&iacutea a &#34noAsignado&#34 para que pueda--}%
    %{--ser ubicada o asociada a otro m&oacutedulo. De este modo, se puede asociar las--}%
    %{--acciones o modificar el m&oacutedulo al que pertenecen.--}%
    %{--</p>--}%
    %{--<br>--}%

    %{--<div class="centrado"><!-- pg 15 -->--}%
        %{--<img src="${resource(dir: 'images/imagenesManuales/imagenesIngreso', file: 'imagen117.png')}"/>--}%
    %{--</div>--}%
    %{--<a id="GestPerm"></a>--}%
    %{--<br> <br> <br> <br>--}%
%{--</div>--}%


%{--<div class="regresa">--}%
    %{--<p><a href="#volverIndice">Volver al &Iacutendice</a></p>--}%
%{--</div> <br> <br> <br>--}%


%{--<div><!--Gestion de Permisos y Modulos pg 15, 16, 17-->--}%
    %{--<h3 class="cursiva">Gesti&oacuten de Permisos y M&oacutedulos</h3>--}%

    %{--<p>El comando &#34Gestionar Permisos y M&oacutedulos&#34 nos lleva a una pantalla--}%
    %{--donde se pueden administrar los perfiles y los m&oacutedulos del sistema.--}%
    %{--</p>--}%

    %{--<div class="centrado">--}%
        %{--<img src="${resource(dir: 'images/imagenesManuales/imagenesIngreso', file: 'imagen118.png')}"/>--}%
    %{--</div><br>--}%

    %{--<p>En el caso de los perfiles, estos pueden crearse en funci&oacuten de otros--}%
    %{--existentes, de tal forma que todos los permisos del perfil &#34padre&#34 son--}%
    %{--incluidos en el perfil nuevo. El prop&oacutesito es usar el perfil padre como--}%
    %{--plantilla y aumentar o quitar permisos para definir el nuevo perfil. La pantalla--}%
    %{--de Gesti&oacuten de permisos y m&oacutedulos se muestra a continuaci&oacuten:--}%
        %{--<br><br> Sus comandos son:--}%
    %{--</p> <br>--}%

    %{--<p><strong>Crear Perfil:</strong> Permite crear un nuevo perfil en el sistema.</p>--}%
    %{--<!-- pg 16 -->--}%
    %{--<div class="centrado">--}%
        %{--<img src="${resource(dir: 'images/imagenesManuales/imagenesIngreso', file: 'imagen137.png')}"/>--}%
    %{--</div> <br>--}%

    %{--<p><strong>Editar Perfil:</strong>Edita el perfil seleccionado en el combo--}%
    %{--&#34Seleccione un Perfil&#34, en el caso ilustrado, el perfil se&#241alado es--}%
    %{--&#34Administrativo&#34.--}%
    %{--</p>--}%

    %{--<p><strong>Borrar Perfil:</strong>Elimina el perfil seleccionado del sistema,--}%
    %{--siempre y cuando no haya informaci&oacuten asociada, es decir, el perfil no--}%
    %{--tenga permisos asociados.--}%
    %{--</p>--}%

    %{--<p><strong>Crear M&oacutedulo:</strong>Crea un nuevo m&oacutedulo en el sistema.--}%
    %{--Cada m&oacutedulo es una entrada de men&uacute, si un m&oacutedulo no tiene--}%
    %{--acciones asociadas para un perfil determinado no aparecer&aacute en el men&uacute--}%
    %{--del usuario.--}%
    %{--</p>--}%
    %{--<br>--}%

    %{--<div class="centrado">--}%
        %{--<img src="${resource(dir: 'images/imagenesManuales/imagenesIngreso', file: 'imagen119.png')}"/>--}%
    %{--</div>--}%
    %{--<br>--}%

    %{--<p>El valor que se ingrese en orden determina la posici&oacuten en la que aparece--}%
    %{--el m&oacutedulo en el men&uacute.--}%
    %{--</p>--}%

    %{--<p><strong>Editar M&oacutedulo:</strong>Edita el m&oacutedulo seleccionado.--}%
    %{--Se selecciona un m&oacutedulo haciendo clic en la secci&oacuten &#34Seleccione--}%
    %{--el m&oacutedulo y fije los permisos&#34 el m&oacutedulo seleccionado aparece con--}%
    %{--un fondo verde.--}%
    %{--</p>--}%

    %{--<p><strong>Borrar M&oacutedulo:</strong>Elimina el m&oacutedulo del sistema,--}%
    %{--s&oacutelo es posible eliminar cuando no hayan acciones asociadas al m&oacutedulo.--}%
    %{--</p>--}%

    %{--<p>Al hacer un clic para seleccionar un m&oacutedulo aparecen todas las acciones--}%
    %{--que posee el m&oacutedulo y con una se&#241al aquellas que pueden ser vistas por el--}%
    %{--perfil seleccionado.--}%
    %{--</p>--}%

    %{--<div class="centrado"><!-- pg 17 -->--}%
        %{--<img src="${resource(dir: 'images/imagenesManuales/imagenesIngreso', file: 'imagen120.png')}"/>--}%
    %{--</div> <br>--}%

    %{--<p>Para cambiar los permisos simplemente seleccione las acciones permitidas--}%
    %{--haciendo un clic para poner un visto en la columna &#34Permisos&#34 y luego haga--}%
    %{--un clic en &#34Fijar permisos del Men&uacute&#34. <br>Para asegurarse de que los--}%
    %{--permisos han quedado bien definido use el comando &#34Ver men&uacute del usuario&#34.--}%
    %{--</p>--}%
    %{--<a id="GestUsuario"></a>--}%

    %{--<p>En resumen, use &#34Acciones&#34 para asociar las acciones a los distintos--}%
    %{--m&oacutedulos y luego &#34Gesti&oacuten de Permisos y M&oacutedulos&#34 para--}%
    %{--fijar los permisos de cada perfil.--}%
    %{--</p>--}%
    %{--<br><br>--}%
%{--</div>--}%


%{--<div class="regresa">--}%
    %{--<p><a href="#volverIndice">Volver al &Iacutendice</a></p>--}%
%{--</div> <br>--}%

%{--<div><!-- Gestion de Usuarios pg 17, 18, 19-->--}%
    %{--<br>--}%

    %{--<h3 class="cursiva">Gesti&oacuten de Usuarios</h3> <!-- pg 17 -->--}%
    %{--<p>Todas las personas registradas en el sistema pueden ser tambi&eacuten sus--}%
    %{--usuarios. Cada persona debe tener como m&iacutenimo los siguientes datos:--}%
    %{--</p>--}%
    %{--<ul>--}%
        %{--<li>Nombre</li>--}%
        %{--<li>Apellidos</li>--}%
        %{--<li>Login o nombre de usuario</li>--}%
        %{--<li>Contrase&#241a</li>--}%
        %{--<li>Contrase&#241a para autorizaciones electr&oacutenicas</li>--}%
        %{--<li>Correo electr&oacutenico</li>--}%
    %{--</ul>--}%

    %{--<div class="centrado"><!-- pg 18 -->--}%
        %{--<img src="${resource(dir: 'images/imagenesManuales/imagenesIngreso', file: 'imagen121.png')}"/>--}%
    %{--</div>--}%
    %{--<br>--}%

    %{--<p>Pantalla de edici&oacuten de datos de un usuario:</p>--}%
    %{--<br>--}%

    %{--<div class="centrado"><!-- pg 19 -->--}%
        %{--<img src="${resource(dir: 'images/imagenesManuales/imagenesIngreso', file: 'imagen122.png')}"/>--}%
    %{--</div>--}%
    %{--<a id="ModAnalisis"></a>--}%
    %{--<br><br><br>--}%
%{--</div>--}%


%{--<div><!--Modulo de Analisis de Precios Unitarios pg 19 -->--}%
    %{--<h2 class="cursiva">M&oacutedulo de An&aacutelisis de Precios Unitarios</h2>--}%

    %{--<p>Antes de comenzar con el An&aacutelisis de precios unitarios, es necesario--}%
    %{--que los datos necesarios para el correcto funcionamiento se encuentren--}%
    %{--registrados en el sistema. El ingreso de estos datos deben llevar un orden--}%
    %{--l&oacutegico, el cual se detalla a continuaci&oacuten.--}%
    %{--</p>--}%
    %{--<a id="RegMateriales"></a> <br>--}%
%{--</div>--}%
%{--<br>--}%


%{--<div class="regresa">--}%
    %{--<p><a href="#volverIndice">Volver al &Iacutendice</a></p>--}%
%{--</div> <br> <br>--}%

%{--<div><!--Registro de Materiales, Mano de Obra y Equipos pg 19, 20, 21, 22, 23 -->--}%
    %{--<h2>Registro de Materiales, Mano de Obra y Equipos</h2>--}%

    %{--<p>Las tres partes principales con las que consta la pantalla de Registro y--}%
    %{--mantenimiento de &iacutetems son: Materiales, Mano de Obra y Equipos. <br>--}%
        %{--Desde esta pantalla se puede ingresar directamente a trabajar ya sea con--}%
        %{--Materiales, Mano de Obra o Equipos.--}%
    %{--</p>--}%
    %{--<br>--}%

    %{--<div class="centrado"><!-- pg 20 -->--}%
        %{--<img src="${resource(dir: 'images/imagenesManuales/imagenesIngreso', file: 'imagen123.png')}"/>--}%
    %{--</div><br>--}%

    %{--<p>En la zona superior de la p&aacutegina se halla una barra de herramientas--}%
    %{--con tres botones que permiten acceder a cada tipo de &iacutetem (grupos), y un--}%
    %{--buscador para localizar un determinado &iacutetem en el &aacuterbol de los--}%
    %{--diferentes grupos.--}%
    %{--</p>--}%
    %{--<br>--}%

    %{--<div class="centrado">--}%
        %{--<img src="${resource(dir: 'images/imagenesManuales/imagenesIngreso', file: 'imagen124.png')}"/>--}%
    %{--</div>--}%
    %{--<br>--}%

    %{--<p>En el buscador, al escribir el nombre de un &iacutetem, se despliega en forma--}%
    %{--autom&aacutetica un men&uacute de posibilidades de orientar la b&uacutesqueda.--}%
    %{--</p>--}%
    %{--<br>--}%

    %{--<div class="centrado">--}%
        %{--<img src="${resource(dir: 'images/imagenesManuales/imagenesIngreso', file: 'imagen125.png')}"/>--}%
    %{--</div>--}%
    %{--<br>--}%

    %{--<p>Al hacer un clic en buscar, aparecen se&#241alados en el &aacuterbol las--}%
    %{--coincidencias encontradas.--}%
    %{--</p>--}%
    %{--<br>--}%

    %{--<div class="centrado"><!-- pg 21 -->--}%
        %{--<img src="${resource(dir: 'images/imagenesManuales/imagenesIngreso', file: 'imagen126.png')}"/>--}%
    %{--</div>--}%
    %{--<br>--}%

    %{--<p>Si se hace un clic con el bot&oacuten derecho del rat&oacuten sobre un--}%
    %{--elemento del &aacuterbol de &iacutetems, aparece un men&uacute correspondiente al--}%
    %{--tipo de elemento se&#241alado.--}%
    %{--</p>--}%
    %{--<br>--}%

    %{--<div class="centrado">--}%
        %{--<img src="${resource(dir: 'images/imagenesManuales/imagenesIngreso', file: 'imagen127.png')}"/>--}%
    %{--</div>--}%
    %{--<br>--}%

    %{--<p>La zona derecha de la pantalla muestra los datos m&aacutes importantes de cada--}%
    %{--&iacutetem, subgrupo o grupo. Al hacer un clic en el tri&aacutengulo que aparece--}%
    %{--antes del nombre del grupo o subgrupo se muestra u oculta su contenido a manera--}%
    %{--de &aacuterbol.--}%
    %{--</p>--}%
    %{--<br>--}%

    %{--<div class="centrado">--}%
        %{--<img src="${resource(dir: 'images/imagenesManuales/imagenesIngreso', file: 'imagen128.png')}"/> <!--pg 22 -->--}%
    %{--</div>--}%
    %{--<br>--}%

    %{--<p>Equipos:</p>--}%
    %{--<br>--}%

    %{--<div class="centrado">--}%
        %{--<img src="${resource(dir: 'images/imagenesManuales/imagenesIngreso', file: 'imagen129.png')}"/>--}%
    %{--</div>--}%
    %{--<br>--}%

    %{--<p>Es posible editar los grupos, crear nuevos grupos y subgrupos, al dar clic--}%
    %{--derecho sobre un grupo espec&iacutefico. Eliminar grupo aparecer&aacute solo el--}%
    %{--momento que dicho grupo no contenga ning&uacuten subgrupo.--}%
    %{--</p>--}%
    %{--<br>--}%

    %{--<div class="centrado">--}%
        %{--<img src="${resource(dir: 'images/imagenesManuales/imagenesIngreso', file: 'imagen130.png')}"/>--}%
    %{--</div>--}%
    %{--<br>--}%

    %{--<p>Al igual que con los grupos es posible editar, crear y agregar un nuevo--}%
    %{--&iacutetem haciendo uso del men&uacute que aparece al hacer clic derecho sobre--}%
    %{--un subgrupo. La opci&oacuten de &#34eliminar subgrupo&#34 aparecer&aacute solo--}%
    %{--si este se halla vac&iacuteo, es decir, que no contiene ning&uacuten &iacutetem.--}%
    %{--</p>--}%
    %{--<br>--}%

    %{--<div class="centrado"><!-- pg 23 -->--}%
        %{--<img src="${resource(dir: 'images/imagenesManuales/imagenesIngreso', file: 'imagen131.png')}"/>--}%
    %{--</div>--}%

    %{--<p>Tambi&eacuten podemos eliminar, crear un nuevo &iacutetem o editar el--}%
    %{--&iacutetem dando clic derecho sobre el mismo--}%
    %{--.</p>--}%

    %{--<div class="centrado">--}%
        %{--<img src="${resource(dir: 'images/imagenesManuales/imagenesIngreso', file: 'imagen132.png')}" style="width: 544px; height: 325px" />--}%
    %{--</div>--}%
    %{--<br>--}%

    %{--<p>Esta pantalla es similar a la presentada para crear nuevos &iacutetems.</p><br>--}%

    %{--<div class="centrado">--}%
        %{--<img src="${resource(dir: 'images/imagenesManuales/imagenesIngreso', file: 'imagen133.png')}" style="width: 544px; height: 325px" />--}%
    %{--</div>--}%
    %{--<br>--}%

    %{--<p>Nueva mano de obra:</p>--}%
    %{--<br>--}%

    %{--<div class="centrado"><!-- pg 24 -->--}%
        %{--<img src="${resource(dir: 'images/imagenesManuales/imagenesIngreso', file: 'imagen134.png')}" style="width: 544px; height: 325px" />--}%
    %{--</div>--}%
    %{--<br>--}%

    %{--<p>Nuevo equipo:</p>--}%

    %{--<div class="centrado">--}%
        %{--<img src="${resource(dir: 'images/imagenesManuales/imagenesIngreso', file: 'imagen135.png')}" style="width: 544px; height: 325px" />--}%
    %{--</div>--}%
%{--</div>--}%
%{--<br>--}%

%{--<a id="PrecMant"></a>--}%

%{--<div class="regresa">--}%
    %{--<p><a href="#volverIndice">Volver al &Iacutendice</a></p>--}%
%{--</div> <br> <br>--}%

%{--<div><!--Precios y Mantenimiento de Items pg 24-->--}%
    %{--<h2>Precios y Mantenimiento de &Iacutetems</h2>--}%

    %{--<p>La pantalla de registro de precios es similar a la de registro de--}%
    %{--&iacutetems. Esta est&aacute organizada como un &aacuterbol con los diferentes--}%
    %{--materiales organizados por subgrupos. Dentro de cada material aparecen las--}%
    %{--distintas listas de precios y al hacer un clic en ella, aparece en la zona--}%
    %{--derecha de la pantalla el detalle de los precios y las fechas en que se definieron.--}%
    %{--</p>--}%

    %{--<div class="centrado">--}%
        %{--<img src="${resource(dir: 'images/imagenesManuales/imagenesIngreso', file: 'imagen136.png')}"/>--}%
    %{--</div>--}%

    %{--<p>El casillero con borde morado que muestra el precio del &iacutetem, en el--}%
    %{--cual se puede escribir para modificarlo. Para escribir en el se debe hacer doble--}%
    %{--clic, y para aceptar el valor introducido se usa la tecla Enter. Los valores no--}%
    %{--se almacenan en el sistema hasta que no se haya presionado el bot&oacuten--}%
    %{--&#34Guardar&#34. El bot&oacuten &#34Nuevo precio&#34 permite crear un nuevo precio--}%
    %{--para el &iacutetem en la lista se&#241alada.--}%
    %{--</p>--}%
%{--</div>--}%


%{--<!--ROBERT-->--}%
%{--<div class="centrado">--}%
    %{--<br><img src="${resource(dir: 'images/imagenesManuales/imagenesIngreso', file: 'imagen301.png')}" style="width: 544px; height: 325px"/><br><br>  <!-- Pag 25 -->--}%
%{--</div>--}%
%{--<div class="centrado">--}%
    %{--<p>--}%
        %{--Los elementos que dispone esta pantalla son: botones de acceso a materiales,--}%
        %{--mano de obra y equipos.Los elementos que dispone esta pantalla son: botones de--}%
        %{--acceso a materiales, mano de obra y equipos.--}%
    %{--</p>--}%


    %{--<br><img src="${resource(dir: 'images/imagenesManuales/imagenesIngreso', file: 'imagen302.png')}"></img><br><br>--}%


    %{--<p>El buscador tiene la misma funcionalidad que en la pantalla de registro de--}%
    %{--&iacutetems, al igual que el bot&oacuten &#34cerrar todo&#34.--}%
    %{--</p>--}%
%{--</div>--}%
%{--<div class="centrado">--}%
    %{--<br><img src="${resource(dir: 'images/imagenesManuales/imagenesIngreso', file: 'imagen303.png')}"></img><br><br>--}%


    %{--<p>Finalmente tenemos una fila de botones que nos permite interaccionar con--}%
    %{--esta pantalla.--}%
    %{--</p>--}%
    %{--<br><img src="${resource(dir: 'images/imagenesManuales/imagenesIngreso', file: 'imagen304.png')}" style="width: 800px;/> <br>--}%
%{--<br><img src="${resource(dir: 'images/imagenesManuales/imagenesIngreso', file: 'imagen305.png')}"/> <br><br>--}%



    %{--<p>--}%
        %{--El primero de los botones es “Todos los lugares” y se refiere a que la información--}%
        %{--desplegada en la zona derecha de la pantalla ser&aacute; una lista de precios espec&iacute;fica o--}%
    %{--que se muestren los precios de todas las listas. Al hacer un clic en este bot&oacute;n el &aacute;rbol--}%
    %{--reemplaza los nombres de los lugares o listas de precios por un sólo elemento etiquetado--}%
    %{--“Todos los lugares” y al hacer un clic en &eacute;l, se despliega en la zona derecha los precios--}%
    %{--del &iacute;tem de las distintas listas y fechas.--}%
    %{--</p><br>--}%

    %{--<img src="${resource(dir: 'images/imagenesManuales/imagenesIngreso', file: 'imagen306-1.png')}" style="width: 544px; height: 325px" /><br><br>--}%


    %{--<p>En estas condiciones, el bot&oacuten &#34Nuevo Precio&#34 permite crear--}%
    %{--un nuevo precio del &iacutetem en todas las listas.--}%
    %{--</p>--}%

    %{--<p>--}%
        %{--El men&uacute que se despleiga al hacer un clic derecho sobre la lista--}%
        %{--de precios desaparece mientras se visualice el elemento &#34todos los--}%
        %{--lugares&#34. Para regresar a ver las listas de precios en forma individual se--}%
        %{--debe hacer otro clic sobre el bot&oacuten &#34Todos los lugares&#34, con lo cual--}%
        %{--el color de este bot&oacuten cambia a desactivado y nuevamente se despliegan los--}%
        %{--nombres de las listas de precios.--}%
    %{--</p><br>--}%
%{--</div>--}%
%{--<div class="centrado">--}%
    %{--<img src="${resource(dir: 'images/imagenesManuales/imagenesIngreso', file: 'imagen306.png')}" style="width: 544px; height: 325px" /><br><br>--}%




    %{--<p>El men&uacute que se despliega al hacer clic derecho sobre el lugar (lista de--}%
    %{--precios) nosp ermite crear nuevas listas, eliminarlas o editar sus datos. Si--}%
    %{--se selecciona eliminar, el sistema muestra un mensaje de confirmaci&oacuten--}%
    %{--antes de proceder. S&oacutelo es posible eliminar listas de precios que no hayan--}%
    %{--sido utilizadas en alguna obra. Si una lista de precios ya ha sido utilizada en--}%
    %{--una obra, la opci&oacuten del men&uacute aparece desactivada.--}%
    %{--</p>--}%
%{--</div>--}%
%{--<div class="centrado">--}%
    %{--<br><img src="${resource(dir: 'images/imagenesManuales/imagenesIngreso', file: 'imagen307.png')}"></img> <br> <br>--}%


    %{--<p>El segundo bot&oacuten es &#34Todas las fechas&#34, que al estar desactivado--}%
    %{--muestra en la zona derecha los precios de todas las fechas. Si se requiere--}%
    %{--consultar los precios a una fecha determinada se debe usar esta opci&oacuten.--}%
    %{--Al hacer un clic en este bot&oacuten aparece un men&uacute:--}%
    %{--</p>--}%
%{--</div>--}%
%{--<div class="centrado">--}%
    %{--<br><img src="${resource(dir: 'images/imagenesManuales/imagenesIngreso', file: 'imagen308.png')}"></img><br><br>--}%


    %{--<p>Al seleccionar una de las opciones aparece un espacio para ingresa la fecha.</p>--}%
%{--</div>--}%
%{--<div class="centrado">--}%
    %{--<br><img src="${resource(dir: 'images/imagenesManuales/imagenesIngreso', file: 'imagen309.png')}" /><br><br>--}%


    %{--<p>Una vez que se haya ingresado la fecha se debe presionar el bot&oacuten--}%
    %{--&#34Refrescar&#34 para volver a cargar los precios de acuerdo a la condici&oacuten--}%
    %{--y a la fecha seleccionada.--}%
    %{--</p>--}%
%{--</div>--}%
%{--<div class="centrado">--}%
    %{--<br><img src="${resource(dir: 'images/imagenesManuales/imagenesIngreso', file: 'imagen310.png')}"/><br><br>--}%

    %{--<p>El siguiente bot&oacuten es &#34Reporte&#34 y sirve para obtener un reporte de--}%
    %{--precios del sistema. Al hacer un clic sobre este bot&oacuten se muestra un--}%
    %{--men&uacute donde se deben definir las condiciones para la generaci&oacuten del--}%
    %{--reporte. Estas son:--}%
    %{--</p>--}%
    %{--<br><!--alinear-->--}%

    %{--<ul class="izquierda">--}%
        %{--<li>Columnas a imprimir adem&aacutes del c&oacutedigo y el nombre del &iacutetem--}%
            %{--<ul>--}%
                %{--<li>Transporte</li>--}%
                %{--<li>Unidad</li>--}%
                %{--<li>Precio</li>--}%
                %{--<li>Fecha de actualizaci&oacuten</li>--}%
            %{--</ul>--}%
        %{--</li>--}%
        %{--<li>Orden de impresi&oacuten--}%
            %{--<ul>--}%
                %{--<li>Alfab&eacutetico: por nombre del &iacutetem</li>--}%
                %{--<li>Num&eacuterico: por c&oacutedigo del &iacutetem.</li>--}%
            %{--</ul>--}%
        %{--</li>--}%
        %{--<li>Lugar y fecha de referencia--}%
            %{--<ul>--}%
                %{--<li>Lugar o lista de precios</li>--}%
                %{--<li>Fecha a la cual se imprimen los precios m&aacutes recientes--}%
                %{--o vigentes.</li>--}%
            %{--</ul>--}%
        %{--</li>--}%
    %{--</ul>--}%
    %{--<br>--}%

    %{--<p>El siguiente bot&oacuten es &#34Reporte&#34 y sirve para obtener un reporte--}%
    %{--de precios del sistema. Al hacer un clic sobre este bot&oacuten se muestra un--}%
    %{--men&uacute donde se deben definir las condiciones para la generaci&oacuten del--}%
    %{--reporte. Estas son:--}%
    %{--</p><br>--}%
    %{--<ul class="izquierda">--}%
    %{--<li>Columnas a imprimir adem&aacutes del c&oacutedigo y el nombre del &iacutetem--}%
    %{--<ul>--}%
    %{--<li>Transporte</li>--}%
    %{--<li>Unidad</li>--}%
    %{--<li>Precio</li>--}%
    %{--<li>Fecha de actualizaci&oacuten</li>--}%
    %{--</ul>--}%
    %{--</li>--}%
    %{--<li>Orden de impresi&oacuten--}%
    %{--<ul>--}%
    %{--<li>Alfab&eacutetico: por nombre del &iacutetem</li>--}%
    %{--<li>Num&eacuterico: por c&oacutedigo del &iacutetem.</li>--}%
    %{--</ul>--}%
    %{--</li>--}%
    %{--<li>Lugar y fecha de referencia--}%
    %{--<ul>--}%
    %{--<li>Lugar o lista de precios</li>--}%
    %{--<li>Fecha a la cual se imprimen los precios m&aacutes recientes o--}%
    %{--vigentes.</li>--}%
    %{--</ul>--}%
    %{--</li>--}%
%{--</ul>--}%
%{--</div>--}%
%{--<div class="centrado">--}%
    %{--<br><img src="${resource(dir: 'images/imagenesManuales/imagenesIngreso', file: 'imagen311.png')}"> </img><br><br>--}%


    %{--<p>En la ilustraci&oacuten aparecen seleccionadas las columnas Unidad y Precio,--}%
    %{--el orden es alfab&eacutetico y se imprimir&aacute el reporte de la lista--}%
    %{--Cayambe con prcios al 17 de enero de 2007.--}%
    %{--</p>--}%

    %{--<p>Mientras se genera el reporte aparece la leyenda:</p>--}%
%{--</div>--}%
%{--<div class="centrado">--}%
    %{--<br><img src="${resource(dir: 'images/imagenesManuales/imagenesIngreso', file: 'imagen312.png')}"></img><br><br>--}%


    %{--<p>Una vez que se haya generado el reporte se debe hacer un clic en cerrar.--}%
    %{--Al hacer un clic en cerrar el reporte se sigue generando y aparecer&aacute--}%
    %{--una ventana de descarga cuando este est&eacute listo.--}%
    %{--</p>--}%
%{--</div>--}%
%{--<div class="centrado">--}%
    %{--<br><img src="${resource(dir: 'images/imagenesManuales/imagenesIngreso', file: 'imagen313.png')}"></img><br><br>--}%


    %{--<p>Un ejemplo de reporte se muestra a continuaci&oacuten.</p>--}%
%{--</div>--}%
%{--<div class="centrado">--}%
    %{--<br> <img src="${resource(dir: 'images/imagenesManuales/imagenesIngreso', file: 'imagen314.png')}"><br><br>--}%
    %{--<br> <img src="${resource(dir: 'images/imagenesManuales/imagenesIngreso', file: 'imagen315.png')}"></img>--}%


    %{--<p>El bot&oacuten Items sirve para ir a la pantalla de registro de &Iacutetems.</p>--}%

    %{--<p>El bot&oacuten <b>Mantenimiento de precios</b> nos lleva a una pantalla que ayuda--}%
    %{--de una forma m&aacutes &aacutegil al mantenimiento de precios de una lista y--}%
    %{--fecha espec&iacutefica.--}%
    %{--</p>--}%

    %{--<p>En esta pantalla se debe escoger el lugar o lista de precios, la fecha de--}%
    %{--referencia, y  materiales, mano de obra, equipos o &#34Todos&#34 los &iacutetems,--}%
    %{--y luego hacer un clic en &#34Consultar&#34.--}%
    %{--</p>--}%
%{--</div>--}%
%{--<div class="centrado">--}%
    %{--<br><img src="${resource(dir: 'images/imagenesManuales/imagenesIngreso', file: 'imagen316.png')}"  style="width: 800px; height: 263px;"/><br><br>--}%


    %{--<p>En la ilustraci&oacuten aparecen desplegados las opciones de cada--}%
    %{--secci&oacuten de esta pantalla. Una vez definidos los valores se debe presionar--}%
    %{--en Consultar para que se muestren los precios vigentes a la fecha.--}%
    %{--</p>--}%

    %{--<p><br> Si se desea crear nuevos precios, seleccione la fecha a la cual se--}%
    %{--desea crear nuevos precios, presione Consultar y edite los precios de los--}%
    %{--&iacutetems deseados. Al presionar Guardar, se crean los nuevos precios--}%
    %{--(s&oacutelo de los modificados) a la fecha que se seleccion&oacute. Para--}%
    %{--verificar se debe visualizar la fecha que se actualiza una vez que se edita el--}%
    %{--valor del precio de un &iacutetem y se le da un clic en el bot&oacuten Guardar.--}%
    %{--</p>--}%
%{--</div>--}%
%{--<div class="centrado">--}%
    %{--<br><img src="${resource(dir: 'images/imagenesManuales/imagenesIngreso', file: 'imagen317.png')}" style="width: 800px; height: 243px;"/><br>--}%


    %{--<p>--}%
        %{--Esta pantalla sirve de igual forma para el mantenimiento de precios de Materiales, Mano de obra y Equipos.<br>--}%

        %{--La pantalla muestra todos los precios de la lista vigentes a la fecha. En la parte interior de la misma aparece una estadística del número de registros mostrados.--}%
    %{--</p><br>--}%


    %{--<br><img src="${resource(dir: 'images/imagenesManuales/imagenesIngreso', file: 'imagen318.png')}" style="width: 800px; height: 563px;"/><br><br>--}%


    %{--<p>--}%
        %{--Pantalla para el registro de precios por Volumen--}%
    %{--</p>--}%
    %{--<br><img src="${resource(dir: 'images/imagenesManuales/imagenesIngreso', file: 'imagen319.png')}" style="width: 800px; height: 243px;"/><br><br><br>--}%
%{--</div>--}%

%{--</div>--}%


%{--<a id="AnPrecios"></a>--}%

%{--<div class="regresa">--}%
    %{--<p><a href="#volverIndice">Volver al &Iacutendice</a></p>--}%
%{--</div> <br> <br> <br>--}%


%{--<div class="centrado"><!-- Analisis de Precios Unitarios Rubros-->--}%
    %{--<h2>An&aacutelisis de precios unitarios: Rubros</h2> <br>--}%

    %{--<p>La opci&oacuten del men&uacute APU &#8211&#62;  Rubros nos lleva la pantalla--}%
    %{--de an&aacutelisis de precios unitarios o administraci&oacuten de rubros.--}%
    %{--</p>--}%
    %{--<br><img src="${resource(dir: 'images/imagenesManuales/imagenesIngreso', file: 'imagen320.png')}"  style="width: 800px; height: 400px;"/><br><br>--}%

    %{--<p>Desde esta pantalla se puede consultar la estructura de un rubro existente--}%
    %{--en el sistema, crear uno nuevo, borrar uno existente, modificar la--}%
    %{--composici&oacuten de un rubro y ver el detalle de precios con o sin desglose--}%
    %{--de transporte de un rubro espec&iacutefico.--}%
    %{--</p>--}%
    %{--<br><table>--}%
    %{--<tbody>--}%
    %{--<tr>--}%
        %{--<td style="padding-right: 10px">--}%
            %{--<strong>Nota</strong>--}%
        %{--</td>--}%
        %{--<td>Para el c&aacutelculo de transporte en esta pantalla se usa una--}%
        %{--sola distancia al peso y al volumen. Para el c&aacutelculo de precios--}%
        %{--en las obras si se pueden manejar varias distancias al  peso (P:--}%
        %{--capital de cant&oacuten y P1: especial) y al volumen (V: materiales--}%
        %{--p&eacutetreos para hormigones, V1: materiales p&eacutetreos para mejoramiento--}%
        %{--y V2: materiales p&eacutetreos para carpeta asf&aacuteltica).--}%
        %{--</td>--}%
    %{--</tr>--}%
    %{--</tbody>--}%
%{--</table><br>--}%

    %{--<p>La pantalla de ingreso est&aacute dividida en tres partes: barra de comandos,--}%
    %{--datos del rubro, lista de precios e ingreso de &iacutetemes en la--}%
    %{--composici&oacuten del rubro y el detalle de su composici&oacuten organizada por--}%
    %{--equipos, mano de obra y materiales. Esta estructura se modifica ligeramente--}%
    %{--cuando se presenta adem&aacutes los valores de los precios incluyendo transporte--}%
    %{--y los costos indirectos.--}%
    %{--</p>--}%
%{--</div>--}%

%{--<a id="BarraCom"></a>--}%

%{--<div class="regresa">--}%
    %{--<p><a href="#volverIndice">Volver al &Iacutendice</a></p>--}%
%{--</div> <br> <br> <br>--}%


%{--<div><!--Barra de Comandos-->--}%
    %{--<h2>Barra de comandos</h2>--}%
    %{--<br>--}%

    %{--<p>Los comandos disponibles en esta pantalla son los siguientes:</p>--}%
%{--</div>--}%

%{--<div class="centrado">--}%
    %{--<br><img src="${resource(dir: 'images/imagenesManuales/imagenesIngreso', file: 'imagen321.png')}"></img><br>--}%


    %{--<p><strong>Lista:</strong> Muestra una ventana donde se pueden buscar rubros--}%
    %{--ingresados al sistema.--}%
    %{--</p>--}%
%{--</div>--}%
%{--<div class="centrado">--}%
    %{--<br><img src="${resource(dir: 'images/imagenesManuales/imagenesIngreso', file: 'imagen322.png')}" style="width: 800px; height: 193px;"/><br><br>--}%

    %{--<p>En esta pantalla se puede realizar b&uacutesquedas por c&oacutedigo o por el--}%
    %{--nombre o descripci&oacuten del rubro. Las b&uacutesquedas pueden ser con--}%
    %{--criterios como &#34Empieza con&#34, &#34Contiene&#34 o &#34es igual a&#34,--}%
    %{--adem&aacutes se puede obtener el resultado de la b&uacutesqueda ordenado por--}%
    %{--el c&oacutedigo o la descripci&oacuten en forma ascendente o descendente. Al--}%
    %{--presionar el bot&oacuten Buscar se muestra el resultado dela b&uacutesqueda.--}%
    %{--</p>--}%

    %{--<p><br>La ilustraci&oacuten a continuaci&oacuten muestra el resultado de buscar--}%
    %{--por descripci&oacuten, rubros que contiene la palabra &#34cemento&#34, ordenado--}%
    %{--por c&oacutedigo en forma ascendente.--}%
    %{--</p>--}%
%{--</div>--}%

%{--<div class="centrado">--}%
    %{--<br><img src="${resource(dir: 'images/imagenesManuales/imagenesIngreso', file: 'imagen323.png')}" style="width: 800px; height: 518px;"/><br><br>--}%

    %{--<p>El bot&oacuten Reporte de esta lista permite exportar esta lista a un archivo--}%
    %{--pdf, esta lista genera el siguiente reporte.--}%
    %{--</p>--}%
%{--</div>--}%

%{--<div class="centrado">--}%
    %{--<br><img src="${resource(dir: 'images/imagenesManuales/imagenesIngreso', file: 'imagen324.png')}" style="width: 800px; height: 400px;" /><br><br>--}%


    %{--<p>Para seleccionar un rubro se debe hacer un clic en el bot&oacuten--}%
        %{--<img src="${resource(dir: 'images/imagenesManuales/imagenesIngreso', file: 'imagen325.png')}"></img> junto al registro deseado.--}%
    %{--</p>--}%

    %{--<p>El sistema carga en la pantalla de rubros el registro seleccionado y lo--}%
    %{--muestra en detalle organizando los &iacutetems en equipos, manos de obra y--}%
    %{--materiales. Por ejemplo, al buscar y cargar un rubro con la palabra concreto se--}%
    %{--tendr&iacutea:--}%
    %{--</p>--}%
%{--</div>--}%
%{--<div class="centrado">--}%
    %{--<br> <img src="${resource(dir: 'images/imagenesManuales/imagenesIngreso', file: 'imagen326.png')}" style="width: 800px; height: 202px;" /><br><br>--}%


    %{--<p>El rubro en detalle se muestra como:</p>--}%
%{--</div>--}%
%{--<div class="centrado">--}%
    %{--<br><img src="${resource(dir: 'images/imagenesManuales/imagenesIngreso', file: 'imagen327.png')}" style="width: 800px; height: 442px;"/><br><br>--}%

    %{--<p><strong>Nuevo:</strong> El bot&oacuten nuevo limpia la pantalla y la prepara--}%
    %{--para el registro de un nuevo rubro.--}%
    %{--</p>--}%
%{--</div>--}%

%{--<div class="centrado">--}%
    %{--<br><img src="${resource(dir: 'images/imagenesManuales/imagenesIngreso', file: 'imagen328.png')}" style="width: 800px; height: 315px;"/><br><br>--}%
%{--</br>--}%
    %{--<p>En esta pantalla se deben llenar todos los campos de la secci&oacuten rubro--}%
    %{--antes de proceder a ingresar los &iacutetems de su composici&oacuten, los cuales--}%
    %{--pueden ser ingresados en cualquier orden. Para a&#241adir &iacutetems use la--}%
    %{--secci&oacuten:--}%
    %{--</p>--}%

    %{--<div class="centrado">--}%
        %{--<br><img src="${resource(dir: 'images/imagenesManuales/imagenesIngreso', file: 'imagen329.png')}" style="width: 800px; height: 53px;"/><br><br>--}%
    %{--</div>--}%

    %{--<p>--}%
        %{--Al hacer un clic en el casillero de c&oacutedigo, vuelve a aparecer la--}%
        %{--pantalla de b&uacutesquedas para permitirnos buscar el &iacutetem deseado luego--}%
        %{--al seleccionarlo de la lista se regresa a la pantalla de rubros y se completan--}%
        %{--los datos de cantidad y rendimiento. El rendimiento s&oacutelo se define para--}%
        %{--equipos y manos de obra, si se ingresa un valor de rendimiento para materiales,--}%
        %{--el sistema lo desecha autom&aacuteticamente.--}%
    %{--</p>--}%

    %{--<div class="centrado">--}%
        %{--<br><img src="${resource(dir: 'images/imagenesManuales/imagenesIngreso', file: 'imagen330.png')}" style="width: 800px; height: 53px;"/><br><br>--}%
    %{--</div>--}%

    %{--<p>--}%
        %{--Para completar el proceso de inclusión del ítem en el rubro se debe presionar el botón Agregar (“+”).--}%
    %{--</p><br>--}%

    %{--<p>--}%
        %{--Los botones Guardar, Canelar y Borrar sirven para almacenar los datos del rubro en el sistema,--}%
        %{--descartar cambios o eliminar el rubro del sistema respectivamente. Sólo los rubros que no hayan sido utilizados en una obra podrán ser eliminados.--}%
    %{--</p><br>--}%

    %{--<p>--}%
        %{--El botón “Copiar composición” permite crear un rubro en base a uno existente.--}%
        %{--Al hacer un clic en este botón aparece una ventana donde se selecciona el rubro desde el cual--}%
        %{--se ha de copiar la composición al rubro en pantalla que se está creando. Los ítems del rubro--}%
        %{--seleccionado se añaden a los existentes del rubro en pantalla.--}%

    %{--</p><br>--}%

    %{--<p>--}%
        %{--Para poder usar este botón se debe crear primero la cabecera del rubro y hacer un clic en el--}%
        %{--botón Guardar. Con esto se llenan los datos de la sección Rubro de esta pantalla.--}%
    %{--</p><br>--}%

    %{--<br><img src="${resource(dir: 'images/imagenesManuales/imagenesIngreso', file: 'imagen330-1.png')}" style="width: 800px; height: 445px;"/><br><br>--}%

    %{--<p>Para copiar la composici&oacuten se hace un clic en el &iacutecono--}%
        %{--<img src="${resource(dir: 'images/imagenesManuales/imagenesIngreso', file: '350.png')}"/> y seguidamente confirmar la orden.--}%
    %{--Los &iacutetems del rubro seleccionado se a&#241aden a los del rubro que se--}%
    %{--tiene en pantalla.--}%
    %{--</p>--}%

    %{--<p>Si se desea corregir la composici&oacuten, se puede dar doble clic en el--}%
    %{--&iacutetem en cuesti&oacuten, para que sus datos se copien en la zona de--}%
    %{--edici&oacuten y se poderlos corregir.--}%
    %{--</p>--}%

    %{--<p>En el caso de que se desee eliminar un &iacutetem se debe usar el bot&oacuten--}%
    %{--&#34Eliminar&#34 que aparece al lado derecho de cada &iacutetem en la--}%
    %{--composici&oacuten del rubro.--}%
    %{--</p>--}%

    %{--<div class="centrado">--}%
    %{--<br><img src="${resource(dir: 'images/imagenesManuales/imagenesIngreso', file: '351.png')}"></img><br><br>--}%
    %{--</div>--}%

    %{--<p><strong>Calcular:</strong> este bot&oacuten cambia el aspecto de la pantalla--}%
    %{--para mostrar los precios de los componentes del rubro incluyendo los costos--}%
    %{--indirectos.--}%
    %{--</p>--}%

    %{--<div class="centrado">--}%
        %{--<br><img src="${resource(dir: 'images/imagenesManuales/imagenesIngreso', file: '352.png')}" style="width: 800px; height: 510px;"/><br><br>--}%
    %{--</div>--}%

    %{--<p>Por defecto se toman los valores de 0 km para las distancias y los valores--}%
    %{--de VOLQUETA 8 M3 para volquete y CHOFER LIC. TIPO E para chofer. Es por tanto--}%
    %{--necesario fijar valores de las distancias para que se calculen los costos del--}%
    %{--transporte.--}%
    %{--</p>--}%

    %{--<div class="centrado">--}%
        %{--<br><img src="${resource(dir: 'images/imagenesManuales/imagenesIngreso', file: '353.png')}"></img><br><br>--}%
    %{--</div>--}%

    %{--<p>El resultado final incluyendo valores de transporte es:</p>--}%

    %{--<div class="centrado">--}%
        %{--<br><img src="${resource(dir: 'images/imagenesManuales/imagenesIngreso', file: '354.png')}" style="width: 800px; height: 257px;"/><br><br>--}%
    %{--</div>--}%

    %{--<p>Los precios que se despliegan son los correspondientes a la lista de precios--}%
    %{--seleccionada, a la fecha fijada en pantalla y al valor del porcentaje de costos--}%
    %{--indirectos.--}%
    %{--</p>--}%

    %{--<p><strong>Borrar:</strong> Borra el Rubro y toda su composici&oacuten de insumos.--}%
    %{--Para poder borrar es necesario que el Rubro no se encuentre registrado y que no--}%
    %{--sea utilizado en una obra.--}%
    %{--</p>--}%

    %{--<div class="centrado">--}%
        %{--<br><img src="${resource(dir: 'images/imagenesManuales/imagenesIngreso', file: '355.png')}"></img><br><br>--}%
    %{--</div>--}%

    %{--<p><b>Registrado:</b> Sirve para registrar o desregistrar el rubro. Al presionar este--}%
    %{--bot&oacuten, aparecer&aacute un mensaje que confirme si desea registrar el Rubro.--}%
    %{--</p>--}%

    %{--<p>Y una vez registrado el rubro, no se podr&aacute realizar modificaciones en--}%
    %{--el mismo, a menos que se lo desregistre.  Al registrar el rubro, se asigna la--}%
    %{--fecha del registro, al lado derecho del mismo.--}%
    %{--</p>--}%
    %{--<img src="${resource(dir: 'images/imagenesManuales/imagenesIngreso', file: 'imagen356.png')}"></img>--}%

    %{--<p><strong>Transporte:</strong> Sirve para ingresar las variables de transporte.--}%
    %{--Al presionarlo, aparecer&aacute una ventana donde se deben ingresar los valores del--}%
    %{--transporte al peso y volumen.  Estos valores van a afectar directamente al--}%
    %{--c&aacutelculo del precio unitario del Rubro.  Este precio, incluido el transporte--}%
    %{--se lo puede ver solamente cuando se presiona el bot&oacuten de Impresi&oacuten del--}%
    %{--Rubro.--}%
    %{--</p>--}%
    %{--<img src="${resource(dir: 'images/imagenesManuales/imagenesIngreso', file: '353.png')}"></img>--}%

    %{--<p>Imprimir: genera un reporte del rubro en formato dpf, con los par&aacutemetros--}%
    %{--seleccionados de lista de precios, fecha, costos indirectos y las variables--}%
    %{--de transporte ingresadas.--}%
    %{--</p>--}%
    %{--<img src="${resource(dir: 'images/imagenesManuales/imagenesIngreso', file: 'imagen358.png')}"></img><br><br>--}%
%{--</div>--}%





<a id="RegObras"></a>

<div class="regresa">
    <p><a href="#volverIndice">Volver al &Iacutendice</a></p>
</div> <br> <br> <br>

<div class="centrado">
    <br>

    <h2 class="izquierda" class="cursiva">Registro de Obras</h2>

    <p>El ingreso de las Obras al sistema, es el primer paso para poder realizar
    todos los procesos referentes a la Obra, sean estos ingresos de vol&uacutemenes
    de obra, c&aacutelculo de presupuesto, f&oacutermula polin&oacutemica, procesos,
    contrataci&oacuten, garant&iacuteas, etc.
    </p>
    <br><img src="${resource(dir: 'images/imagenesManuales/imagenesIngreso', file: 'imagen359.png')}" style="width: 800px; height: 494px;"/><br>
    <a id="Lista"></a>

    <p>La parte superior de esta pantalla presenta un conjunto de comandos que
    permiten controlar el ingreso de datos de la obra. Adem&aacutes una vez que
    exista la obra, se presenta en la zona inferior otros comandos que ayudan a
    ejecutar procesos relativos al registro de la obra, generaci&oacuten de documentos
    y c&aacutelculos de la f&oacutermula polin&oacutemica, composici&oacuten y otros.
    A continuaci&oacuten se ir&aacuten explicando cada uno de estas funcionalidades.
    </p>
</div>

<div class="centrado">
    <br>

    <h3 class="izquierda">Lista</h3>

    <p>Muestra una pantalla donde se puede listar las obras que se encuentran ingresadas
    en el sistema, conforme a criterios de b&uacutesqueda que ayudan a localizarlas
    r&aacutepidamente.
    </p>

    <p>Con esta pantalla se pueden ubicar obras aplicando varios criterios de
    b&uacutesqueda como son: c&oacutedigo, nombre, descripci&oacuten, memorando de
    ingreso, memorando de salida, sitio, plazo, parroquia y comunidad.
    </p>


    <img src="${resource(dir: 'images/imagenesManuales/imagenesIngreso', file: 'imagen360.png')}" style="width: 800px; height: 191px;"/>

    <p>Para cada criterio de b&uacutesqueda se pueden aplicar operaciones como
    contiene, empieza o igual. Estas operaciones se aplican al texto de los datos
    de la obra, as&iacute, si se selecciona  en &#34Buscar por&#34 nombre, se aplica
    &#34Contiene&#34 y se ingresa un criterio como calles, se listar&aacuten toas
    las obras que contengan la palabra calles como nombre de la obra.
    </p>
    <img src="${resource(dir: 'images/imagenesManuales/imagenesIngreso', file: 'imagen361.png')}" style="width: 800px; height: 278px;"/>

    <p>Para seleccionar una de las obras se debe hacer un clic en el bot&oacuten
        <img src="${resource(dir: 'images/imagenesManuales/imagenesIngreso', file: '350.png')}"/> de la obra deseada.
    </p>

    <p>El bot&oacuten reporte permite obtener un reporte en formato pdf que contiene
    el listado de todas las obras que coinciden con el criterio de b&uacutesqueda
    aplicado, es decir, de las obras listas en pantalla. Si la b&uacutesqueda retorna
    m&aacutes de 12 obras, estas se organizan en varias p&aacuteginas de 12 registros
    cada una.
    </p>
</div>

<div class="centrado">

    <a id="Nuevo"></a>

    <div class="regresa">
        <p><a href="#volverIndice">Volver al &Iacutendice</a></p>
    </div> <br> <br> <br>
    <a id="Grabar"></a>

    <h3 class="izquierda">Nuevo</h3>

    <p>Limpia la pantalla para el registro de una nueva obra.</p><br>

    <br>

    <h3 class="izquierda">Grabar</h3>

    <p>Guarda los cambios realizados en la pantalla de registro de obras.</p>
    <br><img src="${resource(dir: 'images/imagenesManuales/imagenesIngreso', file: 'imagen363.png')}" style="width: 800px; height: 760px;"/>
    <a id="Cancelar"></a><br><br><br>

    <p>Una vez que existe una obra en pantalla aparece la barra de botones de la parte
    inferior.
    </p>

    <a id="EliminarObra"></a><br>
    <br><br>

    <h3 class="izquierda">Cancelar</h3>

    <p>Deshace cualquier cambio realizado en la pantalla de registro de obra.</p>
    <!-- Empieza rob  -->
    <a id="Imprimir"></a><br>
    <br>

    <h3 class="izquierda">Eliminar la Obra</h3>

    <p>Luego de confirmar la orden y si la obra no posee informaci&oacuten asociada,
    elimina la obra en pantalla del sistema
    .</p>
    <!-- Empieza rob  -->
    <a id="CambiarEstado"></a><br>
    <br>

    <h3 class="izquierda">Imprimir</h3>

    <p>Imprime los datos en pantalla.</p>
    <!-- Empieza rob  -->
    <a id="CopiarObra"></a><br>
    <br>

    <h3 class="izquierda">Cambiar de Estado</h3>

    <p>Permite cambiar el estado de la obra de &#34No Registrado&#34 a &#34Registrado&#34,
    o viceversa.
    </p>
    <a id="CopiarOferentes"></a><br>
    <br>

    <h3 class="izquierda">Copiar Obra</h3>

    <p>Permite copiar la obra con un nuevo c&oacutedigo.</p><br>
    <!-- Empieza rob  -->

    <br>

    <h3 class="izquierda">Copiar Obra a Oferentes</h3>

    <p>Permite copiar la obra al m&oacutedulo de oferentes, verificando si esta no ha
    sido ya copiada; adem&aacutes de que exista un oferente para dicha obra.
    </p><br><br>
</div>


<a id="RegDatObra"></a><br><br>

<div class="regresa">
    <p><a href="#volverIndice">Volver al &Iacutendice</a></p>
</div> <br> <br> <br>


<div class="centrado">
    <h2 class="izquierda">Registro de datos de la obra</h2>

    <p>El registro de datos de la obra consta de 3 partes principales: datos de
    ingreso, datos de la obra, y datos de salida
    .</p>

    <p><strong>Datos de ingreso:</strong> Donde se registra los datos referentes al
    tr&aacutemite previo al registro de la obra.
    </p>
    <table>
        <tr>
            <td><strong>Estado,</strong> solo puede ser cambiado mediante el
            bot&oacuten <strong>Cambiar de Estado,</strong> que se encuentra en
            la parte superior derecha; se pedir&aacute confirmaci&oacuten antes de
            cambiar el estado.</td>
            <td><img src="${resource(dir: 'images/imagenesManuales/imagenesIngreso', file: 'imagen364.png')}"></img>
            </td>
        </tr>
    </table>
    <br><img src="${resource(dir: 'images/imagenesManuales/imagenesIngreso', file: 'imagen365.png')}" style="width: 800px; height: 107px;"/><br><br>

    <p><strong>Datos de la Obra:</strong> Contiene datos como descripci&oacuten;
        fecha de inicio; fecha de finalizaci&oacuten; y observaciones entre los
    m&aacutes importantes.
    </p>
    <br><img src="${resource(dir: 'images/imagenesManuales/imagenesIngreso', file: 'imagen366.png')}" style="width: 800px; height: 423px;"><br><br>

    <p>Los datos registrados son:</p>

    <p><strong>C&oacutedigo</strong> de la obra.</p>

    <p><strong>Nombre</strong> de la obra</p>

    <p><strong>Programa,</strong> el tipo de programa al que pertenece la obra.</p>

    <p><strong>Tipo</strong> de la obra</p>

    <p><strong>Clase</strong> de obra</p>

    <p><strong>Cant&oacuten, Parroquia y Comunidad,</strong> permite colocar los
    datos referentes al lugar en el que se realizar&aacute la obra, para lo cual
    debemos dar clic en el bot&oacuten Buscar; se desplegar&aacute la siguiente
    pantalla de b&uacutesqueda.
    </p>
    <br><img src="${resource(dir: 'images/imagenesManuales/imagenesIngreso', file: 'imagen367.png')}"><br><br>

    <p>Mediante los diferentes filtros podemos buscar el ubicaci&oacuten
    geogr&aacutefica de la obra, clic en <sttong>Consultar</sttong> para desplegar
    la tabla con los resultados.
    </p>

    <p>Una vez se ha elegido la ubicaci&oacuten procedemos a dar clic en el
    bot&oacuten &#34visto&#34 para agregar la parroquia y comunidad a la pantalla de
    registro.
    </p>

    <p><strong>Lista de Precios:</strong> registra el lugar y la fecha de
    referencia con la que ha de calcularse el presupuesto de la obra.
    </p>
    <br><img src="${resource(dir: 'images/imagenesManuales/imagenesIngreso', file: 'imagen368.png')}"><br><br>

    <p><strong>Datos de Salida:</strong> Registra los datos de los oficios
    y memorando con los que se env&iacutea los datos de presupuesto de la obra.
    </p>
    <br><img src="${resource(dir: 'images/imagenesManuales/imagenesIngreso', file: 'imagen369.png')}" style="width: 800px; height: 88px;"/><br><br>
</div>


<a id="BotonInferior"></a><br><br>

<div class="regresa">
    <p><a href="#volverIndice">Volver al &Iacutendice</a></p>
</div> <br> <br> <br>

<div class="centrado">
    <h2 class="izquierda">Registro de obras &#8211 Barra de botones inferior</h2>
    <br><img src="${resource(dir: 'images/imagenesManuales/imagenesIngreso', file: 'imagen370.png')}"><br><br><br>

    <h2 class="izquierda">Variables</h2>

    <p>Esta secci&oacuten permite el registro de las variables asociadas a la obra,
    estas son las de transporte, factores que se aplican y costos indirectos.
    </p>
    <br><img src="${resource(dir: 'images/imagenesManuales/imagenesIngreso', file: 'imagen371.png')}"><br><br>
    <br><img src="${resource(dir: 'images/imagenesManuales/imagenesIngreso', file: 'imagen372.png')}"><br><br>
    <br><img src="${resource(dir: 'images/imagenesManuales/imagenesIngreso', file: 'imagen373.png')}"><br><br>
    <br><img src="${resource(dir: 'images/imagenesManuales/imagenesIngreso', file: 'imagen374.png')}"><br><br>
</div>


<a id="VolObra"></a><br><br>

<div class="regresa">
    <p><a href="#volverIndice">Volver al &Iacutendice</a></p>
</div> <br> <br> <br>

<div class="centrado">
    <h2 class="izquierda">Vol&uacutemenes de Obra</h2><br>

    <p>Este bot&oacuten nos lleva al registro de los vol&uacutemenes de obra. Esta
    pantalla presenta dos secciones, una destinada al ingreso de datos del rubro
    dentro de un subpresupuesto y la otra, denominada composici&oacuten, que muestra
    una lista de los rubros.
    </p>
    <br><img src="${resource(dir: 'images/imagenesManuales/imagenesIngreso', file: 'imagen375.png')}" style="width: 800px; height: 573px;"/><br><br>

    <p>Al presionar el bot&oacuten &#34Calcular&#34 se presentan los valores.</p>

    <br><img src="${resource(dir: 'images/imagenesManuales/imagenesIngreso', file: 'imagen376.png')}" style="width: 800px; height: 538px;"/><br><br>

    <p>El bot&oacuten <strong>Regresar</strong> sirve para volver a la pantalla de registro
    de obras.
    </p>

    <p>El bot&oacuten <strong>Reporte Grupos/Subgrupos,</strong> permite imprimir en
    formato PDF el conjunto de rubros con el respectivo grupo y subgrupo al que
    pertenecen, as&iacute como tambi&eacuten el subpresupuesto del que forman parte.
    </p>
</div>




<!--JUANES-->
<div><!--50, 51, 52, 53, 54-->
    <img src="${resource(dir: 'images/imagenesManuales/imagenesIngreso', file: 'img201.png')}" style="width: 800px; height: 588px;"/><br><br>

    <p>Cada l&iacutenea de los rubros que conforman el volumen de obra, posee
    un men&uacute de se muestra al hacer clic con el bot&oacuten derecho del
    rat&oacuten, tal como se ve en la siguiente ilustraci&oacuten.
    </p>
    <br><br><img src="${resource(dir: 'images/imagenesManuales/imagenesIngreso', file: 'img202.png')}" style="width: 800px; height: 577px;"/><br><br>

    <p>La opci&oacuten Editar, copia los datos del rubro en la zona de edici&oacuten:
    </p>
    <br><img src="${resource(dir: 'images/imagenesManuales/imagenesIngreso', file: 'img203.png')}"/><br><br>

    <p>Al hacer clic en Editar se copian los datos a los casilleros (campos) respectivos:
    </p>
    <br><img src="${resource(dir: 'images/imagenesManuales/imagenesIngreso', file: 'img204.png')}"/><br><br> <!--51-->
    <p>Luego de hacer los cambios deseados se guardan los datos haciendo un clic en el
    bot&oacuten &quot+&quot.
    </p>

    <p><br>La otra opci&oacuten, &quotImprimir&quot presenta un reporte pdf del
    an&aacutelisis de precios unitarios del rubro seleccionado. Este reporte se
    hace para un s&oacutelo rubro.
    </p>

    <p>Si se dese reportar todos los an&aacutelisis de precios de la obra, se
    debe ingresar mediante el menu superior APU, y posteriormente usar el
    bot&oacuten &quotRubros&quot.
    </p>

    <p>En la zona superior de la secci&oacuten de Composici&oacuten de se halla
    un combo que permite seleccionar el subpresupuesto a visualizarse,
    tambi&eacuten se halla el bot&oacuten &quotVer todos&quot que sirve para
    desplegar los rubros de todos los subpresupuestos.
    </p>

    <p><br>A continuaci&oacuten se encuentra el bot&oacuten &quotCopiar
    Rubros&quot el mismo que nos redireccionar&aacute a la siguiente pantalla.
    </p>
    <br><img src="${resource(dir: 'images/imagenesManuales/imagenesIngreso', file: 'img206.png')}" style="width: 800px; height: 428px;"/><br><br><br> <!--52-->
    <p>Esta pantalla nos permite copiar rubros desde un subpresupuesto a otro,
    ya sea solo los rubros seleccionados o todos los rubros pertenecientes a un
    subpresupuesto a la vez.
    </p>

    <p>Para poder copiar los rubros es mandatorio seleccionar tanto un
    subpresupuesto de origen como uno de destino.
    </p>

    <div class="centrado">
        <br><img src="${resource(dir: 'images/imagenesManuales/imagenesIngreso', file: 'img207.png')}"> <br>
    </div>
    <br>

    <p>Si se desea copiar solo los rubros seleccionados, marcamos los mismos
    usando el checkbox que se encuetra en la parte izquierda de cada registro;
    posteriormente procedemos a dar clic en el bot&oacuten &quotCopiar rubros
    seleccionado&quot.
    </p>

    <p>Si se desea copiar todos los rubros, elegimos solo los subpresupuestos de
    origen, destino y procedemos a dar clic en &quotCopiar todos los Rubros&quot,
    NO es necesario marcar todos los rubros usando el checkbox.
    </p>

    <p>El bot&oacuten &quotRegresar&quot nos permite volver a la pantalla de
    Vol&uacutemenes de Obra.
    </p>

    <p><br>Finalmente, el bot&oacuten Imprimir subpresupuesto genera un reporte en
    formato pdf del presupuesto de la obra, incluyendo todos sus rubros.
    </p>

    <div class="centrado">
        <br><img src="${resource(dir: 'images/imagenesManuales/imagenesIngreso', file: 'img208.png')}"/> <!--53--><br><br>
    </div>

    <p><br>Para el registro de un nuevo rubro se debe empezar por seleccionar
    el subpresupuesto, para lo cual se puede ayudar presionando la primera
    letra de su nombre o digitar parte del nombre, el sistema ayuda a ubicar
    los supresupuestos que tienen esas letras. Luego se debe seleccionar el
    rubro, para esto se debe hacer doble clic en el casillero de c&oacutedigo
    para que se despliegue una ventana de b&uacutesqueda de rubros.
    </p>

    <div class="centrado">
        <br><img src="${resource(dir: 'images/imagenesManuales/imagenesIngreso', file: 'img209.png')}" style="width: 800px; height: 407px;"/><br><br>
    </div>

    <p>Desde esta pantalla se selecciona el rubro deseado y se retorna a la anterior
    presionando el bot&oacuten &quotvisto&quot junto al rubro en cuesti&oacuten.
    </p>

    <p><br>Finalmente, se ingresa la cantidad, el orden en que debe aparecer este
    rubro dentro del subpresupuesto y se hace un clic en el bot&oacuten
    &quotAgregar&quot
    </p>
    <br><br><img src="${resource(dir: 'images/imagenesManuales/imagenesIngreso', file: 'img210.png')}" style="width: 800px; height: 65px;"/><!--54--> <br> <br> <br>
</div>

<a id="MatrizFormula"></a>

<div class="regresa">
    <p><a href="#volverIndice">Volver al &Iacutendice</a></p>
</div> <br> <br><br>


<div><!--Matriz de Formula Polinomica pg 54, 55, 56-->
    <h2>Matriz de la F&oacutermula Polin&oacutemica</h2>
    <br>

    <p>El bot&oacuten Matriz FP, nos lleva a ejecutar un proceso de c&aacutelculo
    de la matriz de la f&oacutermula polin&oacutemica. Como prerrequisitos,
    se debe contar con:
    </p>
    <ol>
        <li>Que se hallen ingresados los vol&uacutemenes de Obra</li>
        <li>Que se halle definida la lista precios para esta Obra</li>
        <li>Que se hayan ingresado todas las distancias al Peso y al Volumen, en el
        caso de manejarse una sola distancia al peso o al volumen, este valor se
        debe repetir en las otras distancias.
        </li>
        <li>Todos los rubros de vol&uacutemenes de obras deben tener cantidades
        positivas y mayores que cero.
        </li>
        <li>Todos los &iacutetems que componen los rubros deben contar con nombres
        cortos &uacutenicos y sin caracteres especiales.
        </li>
    </ol>

    <p>Al presionar el bot&oacuten Matriz FP se presenta una ventana de
    confirmaci&oacuten de la orden:
    </p>

    <div class="centrado">
        <br><img src="${resource(dir: 'images/imagenesManuales/imagenesIngreso', file: 'img211.png')}"/><br>
    </div>

    <p><br>Si ya ha generado la matriz y desea conservar los valores en el caso de
    que no se hayan cambiado los vol&uacutemenes de obra ni actualizado precios,
    conteste &quotNo&quot a esta pregunta, caso contrario haga un clic en
    &quotSi&quot. Seguidamente aparecer&aacute:
    </p>

    <div class="centrado">
        <br><img src="${resource(dir: 'images/imagenesManuales/imagenesIngreso', file: 'img212.png')}"/>
    </div>

    <p><br>Si se desea hacer una matriz de un subpresupuesto espec&iacutefico,
    selecci&oacutenelo desde el combo, caso contrario, s&oacutelo verifique que la
    casilla de &quotGenerar con transporte&quot est&eacute de acuerdo a lo que
    requiera, es decir, con un visto si si desea hacer el desglose de transporte
    o sin el visto si no. Presione Generar para iniciar el proceso.
    </p>

    <div class="centrado">
        <br><img src="${resource(dir: 'images/imagenesManuales/imagenesIngreso', file: 'img213.png')}"/><br> <!--55-->
    </div>

    <p><br>Esta ventana se mantendr&aacute mientras se realizan los c&aacutelculos
    de la f&oacutermula polin&oacutemica y se despliega la matriz
    .</p>

    <p>Luego de completado el proceso, se despliega la matriz de la
    f&oacutermula polin&oacutemica.
    </p>
    <br><img src="${resource(dir: 'images/imagenesManuales/imagenesIngreso', file: 'img214.png')}"/>

    <p><br>En esta pantalla existen varios botones en la zona superior:</p>

    <p><strong>Regresar:</strong> sirve para volver a la pantalla de la obra.</p>

    <p><strong>Coeficientes de la matriz:</strong> invoca a la pantalla de
    definici&oacuten de la f&oacutermula polin&oacutemica.
    </p>

    <p><strong>Imprimir:</strong> genera un reporte en pdf de la matriz,
    este halla organizado en forma primero vertical y luego horizontal.
    </p>

    <div class="centrado">
        <br><img src="${resource(dir: 'images/imagenesManuales/imagenesIngreso', file: 'img215.png')}"/><!--56-->
    </div>

    <p><br><br>Un buscador para poder ubicar f&aacutecilmente las columnas
    de los distintos &iacutetems, para ello, escriba parte del nombre del
    &iacutetem (nombre corto) en la casilla de b&uacutesqueda y haga un
    clic en el bot&oacuten Buscar. La siguiente ilustraci&oacuten muestra
    el resultado de buscar la columna de transporte.
    </p>

    <div class="centrado">
        <br><img src="${resource(dir: 'images/imagenesManuales/imagenesIngreso', file: 'img216.png')}"/>
    </div>

    <p><br>Finalmente, el bot&oacuten limpiar selecci&oacuten sirve para
    desmarcar las columnas que han sido resaltadas como resultado de la
    b&uacutesqueda.
    </p><br>
</div>

<a id="FormulaPoli"></a>

<div class="regresa">
    <p><a href="#volverIndice">Volver al &Iacutendice</a></p>
</div> <br> <br>


<div><!-- Formula polinomica pg 57, 58-->
    <br><br>

    <h2>F&oacutermula polin&oacutemica</h2>

    <p><br>Para acceder a los ajustes de la f&oacutermula polin&oacutemica
    use el bot&oacuten &quotCoeficientes F&oacutermula Polin&oacutemica&quot,
    una vez que se haya corrido el proceso de c&aacutelculo de la matriz de
    la f&oacutermula polin&oacutemica
    .</p>
    <br><img src="${resource(dir: 'images/imagenesManuales/imagenesIngreso', file: 'img217.png')}"/>

    <p><br>Esta pantalla sirve para componer la f&oacutermula polin&oacutemica
    en funci&oacuten de los distintos aportes de materiales obtenidos de la
    matriz de la f&oacutermula.
    </p>

    <p>En la parte superior de esta pantalla figuran 3 botones, Regresar para
    volver a la pantalla de obras, F&oacutermula polin&oacutemica y Cuadrilla
    tipo.
    </p>

    <p>Por defecto la pantalla que se muestra corresponde a la de la f&oacutermula
    polin&oacutemica, lo que equivale a presionar el bot&oacuten F&oacutermula
    polin&oacutemica, al presionar el bot&oacuten Cuadrilla tipo, se muestra
    la pantalla para trabajar en la composici&oacuten de la cuadrilla tipo.
    </p>
    <br><img src="${resource(dir: 'images/imagenesManuales/imagenesIngreso', file: 'img218.png')}"/> <!--58--> <br> <br>
</div>

<a id="TrabajoPoli"></a>

<div class="regresa">
    <p><a href="#volverIndice">Volver al &Iacutendice</a></p>
</div> <br> <br>

<div><!-- Trabajando en la composicion de la formula polinomica pg 58,59-->
    <br><br>

    <h2>Trabajando en la composici&oacuten de la f&oacutermula polin&oacutemica
    </h2>

    <p><br>
        Para incluir varios materiales en un coeficiente, siga el siguiente proceso:
    <ol class="izquierda">
        <li>Haga clic en el coeficiente al cual desea a&ntildeadir la
        participaci&oacuten del o de los materiales. Con esto queda
        se&ntildealado el coeficiente.
        </li>
        <li>Haga clic sobre los materiales que desea adicionar o incluir en el
        coeficiente.
        </li>
        <li>Haga un clic en el bot&oacuten verde Agregar a ... que aparece sobre
        la columna de materiales.
        </li>
    </ol>
</p>
    <p>Como resultado de este proceso, se reestructura la composici&oacuten de los
    coeficientes, recalcul&aacutendose el valor del coeficiente. La pantalla quedar&aacute
    similar a:
    </p>
    <br><img src="${resource(dir: 'images/imagenesManuales/imagenesIngreso', file: 'img219.png')}"/>

    <p><br>Si se desea ajustar el valor del coeficiente o cambiar la descripci&oacuten
    o material del &iacutendice, haga clic derecho sobre el &iacutecono del
    coeficiente y seleccione Editar.
    </p>

    <div class="centrado">
        <br><img src="${resource(dir: 'images/imagenesManuales/imagenesIngreso', file: 'img220.png')}"/> <!--59-->
    </div>

    <p><br>Al seleccionar editar aparecer&aacute la pantalla ilustrada a
    continuaci&oacuten en la cual se podr&aacute cambiar el material y ajustar
    el valor del coeficiente.
    </p>

    <div class="centrado">
        <br><img src="${resource(dir: 'images/imagenesManuales/imagenesIngreso', file: 'img221.png')}"/>
    </div>

    <p><br>
        Para quitar un material de un coeficiente se debe seguir el siguiente proceso:
    <ol>
        <li>Haga clic derecho en el material a eliminar, y seleccione Eliminar.</li>
        <li>Confirme la orden dada.</li>
    </ol>
</p>
    <div class="centrado">
        <br><img src="${resource(dir: 'images/imagenesManuales/imagenesIngreso', file: 'img222.png')}"/>
    </div>

    <p>Tan s&oacutelo el concepto de p01 no puede se ajustado, todos los valores de los
    coeficientes se pueden ajustar para que no haya descuadre en los decimales.
    </p>

    <p>En el caso de que la pantalla no muestre todos los datos, se debe presionar F5
    o refrescarla para que se desplieguen todos los datos.
    </p><br><br>
</div>


<a id="RubrObra"></a>

<div class="regresa">
    <p><a href="#volverIndice">Volver al &Iacutendice</a></p>
</div> <br> <br>


<div><!-- Rubros de la obra pg 60-->
    <h2><br>Rubros de la obra</h2>

    <p><br>Haciendo un clic en el bot&oacuten Rubros podemos imprimir el todos
    los rubros de la obra con o sin desglose de transporte.
    </p>

    <div class="centrado">
        <br><img src="${resource(dir: 'images/imagenesManuales/imagenesIngreso', file: 'img223.png')}"/>
    </div>

    <p><br>De acuerdo como se desee, se generar&aacute un reporte en formato pdf con los
    an&aacutelisis de precios de todos los rubros que se hallan en la obra, en hojas
    separadas. El reporte es extenso y puede tomar unos pocos minutos su generaci&oacuten.
    </p>

    <div class="centrado">
        <br><img src="${resource(dir: 'images/imagenesManuales/imagenesIngreso', file: 'img224.png')}"/><br><br>
    </div>
</div>


<a id="Cronograma"></a>

<div class="regresa">
    <p><a href="#volverIndice">Volver al &Iacutendice</a></p>
</div> <br> <br>

<div><!--Cronograma pg 61, 62, 63, 64-->
    <br><br>

    <h2>Cronograma</h2>

    <p><br>El cronograma de la obra se lo puede trabajar en base a las cantidades de obra o
    en base a porcentajes de avance.
    </p>

    <p>Para acceder a la pantalla del cronograma en el bot&oacuten "Cronograma".
    Al abrirla por primera vez, se muestra la tabla vac&iacutea. Las columnas
    &quotMes #&quot se generan autom&aacuteticamente seg&uacuten el plazo
    asignado a la obra.
    </p>

    <div class="centrado">
        <br><img src="${resource(dir: 'images/imagenesManuales/imagenesIngreso', file: 'img225.png')}"/>
    </div>

    <p>El bot&oacuten <img class="boton" src="${resource(dir: 'images/imagenesManuales/imagenesIngreso', file: 'img226.png')}"/> permite regresar
    a la pantalla de  registro de la obra.
    </p>
    <br>

    <p>Al hacer clic en una fila, se marcan en azul las 3 filas correspondientes
    al rubro correspondiente. Este es el rubro seleccionado.
    </p>
    <br><img src="${resource(dir: 'images/imagenesManuales/imagenesIngreso', file: 'img227.png')}"/> <!--62-->
    <p>Las celdas de los meses es donde se deben ingresar los datos de la
    planificaci&oacuten. Para hacer esto, se da doble clic en cualquiera de las
    tres celdas que corresponden a los per&iacuteodos mensuales "Mes #", ya sea
    $, % o F. Esto abre una ventana que muestra los datos del rubro y un
    formulario para asignar un valor.
    </p>

    <div class="centrado">
        <br><img src="${resource(dir: 'images/imagenesManuales/imagenesIngreso', file: 'img228.png')}"/>
    </div>
    <!--63-->
    <p><br>El &aacuterea de per&iacuteodos permite seleccionar un periodo, o un rango
    de periodos. Al seleccionar un rango se dividir&aacute lo asignado entre el
    n&uacutemero de periodos que contenga el rango; por ejemplo, al asignar 50%
    a los periodos del 1 al 2 asignar&aacute 25% al periodo 1 y 25% al periodo 2.
    Se puede ingresar la cantidad en cualquiera de los campos de cantidad,
    porcentaje o precio, y los otros campos se calcular&aacuten de acuerdo al
    rango de per&iacuteodos especificado. El sistema valida los valores que ya
    est&eacuten ingresados y no permite ingresar valores superiores al total.
    Al hacer clic en el bot&oacuten Aceptar se guardan los valores y se actualiza
    la tabla del cronograma.
    </p>

    <div class="centrado">
        <br><img src="${resource(dir: 'images/imagenesManuales/imagenesIngreso', file: 'img229.png')}"/>
    </div>

    <p><br>El bot&oacuten <img class="boton" src="${resource(dir: 'images/imagenesManuales/imagenesIngreso', file: 'img230.png')}"/> limpia los
    campos del rubro seleccionado, despu&eacutes de mostrar una pantalla de
    confirmaci&oacuten de la orden.
    </p>

    <div class="centrado">
        <br><img src="${resource(dir: 'images/imagenesManuales/imagenesIngreso', file: 'img231.png')}"/>
    </div>

    <p><br>Esta modificaci&oacuten es solo visual, es decir, los datos existentes
    no se eliminan, s&oacutelo se limpian los casilleros. Los valores ser&aacuten
    modificados s&oacutelo si se ingresan nuevos datos en estos casilleros.
    Esto quiere decir que si no se ingresa ning&uacuten dato que sobreescriba
    los limpiados, la siguiente vez que se abra el cronograma volver&aacuten a
    aparecer.
    </p>

    <p>El bot&oacuten <img class="boton" src="${resource(dir: 'images/imagenesManuales/imagenesIngreso', file: 'img232.png')}"/> limpia los
    campos de todos los rubros de la tabla, despu&eacutes de mostrar una
    notificaci&oacuten.
    </p>

    <div class="centrado">
        <br><img src="${resource(dir: 'images/imagenesManuales/imagenesIngreso', file: 'img233.png')}"/>
    </div>
    <!--64-->
    <p><br>Al igual que la opci&oacuten anterior, esta modificaci&oacuten
    es solo visual, es decir, los datos existentes solo se reemplazar&aacuten
    si se ingresan nuevos datos. Esto quiere decir que si no se ingresa
    ning&uacuten dato que sobreescriba los limpiados, la siguiente vez que se
    abra el cronograma volver&aacuten a aparecer.
    </p>

    <p>Si se desea eliminar todos los datos del cronograma se debe hacer un clic
    en el bot&oacuten  &quotEliminar cronograma&quot y  confirmar la orden.
    </p>

    <div class="centrado">
        <br><img src="${resource(dir: 'images/imagenesManuales/imagenesIngreso', file: 'img234.png')}"/>
    </div>

    <p><br>Esta modificaci&oacuten es inmediata, es decir, al dar clic en
    Aceptar se eliminar&aacuten los valores de la base de datos y no hay
    manera de recuperarlos.
    </p>

    <p>El bot&oacuten <img class="boton" src="${resource(dir: 'images/imagenesManuales/imagenesIngreso', file: 'img235.png')}"/> exporta
    la tabla del cronograma a una hoja Excel.
    </p>

    <p>El bot&oacuten <img class="boton" src="${resource(dir: 'images/imagenesManuales/imagenesIngreso', file: 'img236.png')}"/> permite
    ver los gr&aacuteficos de avance econ&oacutemico y f&iacutesico de la obra.
    </p>

    <div class="centrado">
        <br><br><img src="${resource(dir: 'images/imagenesManuales/imagenesIngreso', file: 'img237.png')}"/>
    </div>

    <p>El bot&oacuten <img class="boton" src="${resource(dir: 'images/imagenesManuales/imagenesIngreso', file: 'img238.png')}"/> permite
    regresar a la pantalla del cronograma.
    /p>
</div>


<a id="CompObra"></a>

<div class="regresa">
    <p><a href="#volverIndice">Volver al &Iacutendice</a></p>
</div> <br> <br><br>

<div><!-- Composicion de la Obra pg 65, 66-->
    <br><br>

    <h2>Composici&oacuten de la Obra</h2>
    <br>

    <p>Los &iacutetems que componen la obra se muestra al hacer un clic en
    el bot&oacuten &quotComposici&oacuten&quot, con ello, se muestra la pantalla
    de composici&oacuten organizada por grupos de &iacutetems, es decir, por
    materiales, mano de obra y equipos.
    </p>

    <p>Este es b&aacutesicamente un reporte de la obra y puede ser obtenido en
    pdf o exportado a excel.
    </p>

    <div class="centrado">
        <br><img src="${resource(dir: 'images/imagenesManuales/imagenesIngreso', file: 'img239.png')}">
    </div>

    <p><br>Los comandos de esta pantalla son:</p>

    <p><strong>Regresar:</strong> que sirve para regresar a la pantalla de la obra.
    </p>

    <p><strong>Todos:</strong> muestra todos los &iacutetems de la obra:
    materiales, mano de obra y equipos.
    </p>

    <p><strong>Materiales:</strong> muestra s&oacutelo los materiales que
    componen la obra.
    </p>

    <p><strong>Mano de obra:</strong> muestra los &iacutetems de mano de obra.</p>

    <p><strong>Equipos:</strong> muestra los &iacutetems de equipos</p>

    <p><strong>Pdf:</strong> genera un reporte en formato pdf de los &iacutetems
    mostrados en pantalla.
    </p>

    <p><strong>Excel:</strong> genera un archivo de hoja de c&aacutelculo xls,
    de los &iacutetems mostrados en pantalla.
    </p>

    <div class="centrado">
        <br><br><img src="${resource(dir: 'images/imagenesManuales/imagenesIngreso', file: 'img240.png')}"/> <!--66-->
    </div>
</div>

<a id="DocObra"></a>
<br><br>

<div class="regresa">
    <p><a href="#volverIndice">Volver al &Iacutendice</a></p>
</div> <br> <br><br>

<div><!-- Documentos de la Obra pg 66,-->
<h2>Documentos de la obra</h2>

<p><br>Genera los documentos precontratuales t&eacutecnicos que sirven para
establecer los elementos necesarios para la formulaci&oacuten del proceso
de contrataci&oacuten.
</p>

<p>La pantalla est&aacute dividida en cuatro tabs: Presupuesto, Memorando,
F&oacutermula Polin&oacutemica y Textos Fijos.
</p>

<p><br>En la parte inferior de la pantalla se encuentran los botones
Regresar, Imprimir y Presupuesto a Excel
</p>

<div class="centrado">
    <br><img src="${resource(dir: 'images/imagenesManuales/imagenesIngreso', file: 'img241.png')}"/><br> <!--67-->
    <img src="${resource(dir: 'images/imagenesManuales/imagenesIngreso', file: 'img242.png')}"/>
</div>

<p><br>Regresar, retorna a la pantalla de registro de obra.</p>

<p>Imprimir, imprimir&aacute un documento diferente dependiendo del tab
que se encuentre seleccionado en ese momento.
</p>

<p>Presupuesto a Excel, muestra en formato de hoja de c&aacutelculo el
presupuesto de la obra. Este bot&oacuten es indiferente a la
pesta&ntildea que se encuentre seleccionada.
</p>

<p><br><strong>PRESUPUESTO</strong>

<p/>

<p><br>La presente pantalla esta dividida en tres partes, las mismas que
son: Tipo de Reporte, Pie de P&aacutegina, y Set de Firmas
</p>
<br><br>

<p><strong>Tipo de Reporte</strong></p>
<br>

<p>En esta secci&oacuten debemos obligatoriamente seleccionar una de
los dos tipos de reporte que se presentan.
</p>

<div class="centrado">
    <br><img src="${resource(dir: 'images/imagenesManuales/imagenesIngreso', file: 'img243.png')}"/>
</div>

<p><br>Tipo de Obra, es un campo no editable que indica si la obra es de
tipo Civil o Vial.
</p>
<!--68-->
<p>Forzar nueva p&aacutegina para las notas de Pie de P&aacutegina,
agrega una p&aacutegina adicional al final del documento a imprimir,
la misma contendr&aacute las notas de pie de p&aacutegina, notas adicionales
y el set de firmas.
</p>

<div class="centrado">
    <br><img src="${resource(dir: 'images/imagenesManuales/imagenesIngreso', file: 'img244.png')}"/>
</div>

<p><br><br><strong>Pie de P&aacutegina</strong></p>
<br>

<p>Permite seleccionar el pie de p&aacutegina ha ser agregado en
nuestro documento, asi tambi&eacuten podemos crear o editar un pie de
pagina ya existente.
</p>

<div class="centrado">
    <img src="${resource(dir: 'images/imagenesManuales/imagenesIngreso', file: 'img245.png')}"/>
</div>

<p><br><strong>Firmas</strong></p>
<br>

<p>Son el conjunto de firmas que se colocaran al final del documento.</p>
<!--69-->
<p>Existen dos firmas por defecto a colocarse en el documento de Presupuesto;
a parte de eso es posible agregar m&aacutes firmas las cuales pueden ser
seleccionadas del combo y agregadas con el bot&oacuten Adicionar.
</p>

<div class="centrado">
    <br><img src="${resource(dir: 'images/imagenesManuales/imagenesIngreso', file: 'img246.png')}"/>
</div>

<p>El bot&oacuten rojo al lado de cada firma permite retirar de la lista la
firma ha ser agregada al documento.</p>
<br><br>

<p><strong>Reajuste del Presupuesto</strong></p>
<br>

<p>Una vez se da clic sobre el bot&oacuten imprimir se nos
presentar&aacute un conjunto de opciones adicionales para la impresi&oacuten,
las cuales incluyen agregar el IVA y el reajuste del presupuesto.
</p>

<div class="centrado">
    <br><img src="${resource(dir: 'images/imagenesManuales/imagenesIngreso', file: 'img247.png')}"/>
    <img src="${resource(dir: 'images/imagenesManuales/imagenesIngreso', file: 'img248.png')}"/>
</div>

<p><br><strong>MEMORANDO</strong></p>  <!--70-->
<p><br>Consta de cuatro partes, las mismas que son: Tipo de Reporte,
Cabecera, Texto y Set de Firmas.
</p>

<p><br><br><strong>Tipo de Reporte</strong></p>

<div class="centrado">
    <br><img src="${resource(dir: 'images/imagenesManuales/imagenesIngreso', file: 'img249.png')}"/>
</div>

<p>Las dos opciones presentar&aacuten datos distintos en el documento impreso,
adem&aacutes al seleccionar Presupuesto Referencial se bloquear&aacuten
las siguientes opciones en Cabecera, las cuales no aparecer&aacuten en el
documento:
</p>

<p><br><br><strong>Cabecera</strong></p>
<br>

<p>Muestra un conjunto de datos de la obra registrada; en la parte inferior
es posible calcular el valor de reajuste del valor total de la base, para
lo cual colocamos un porcentaje en la caja de texto y damos clic en el
bot&oacuten para calcular dicho valor.
</p>

<div class="centrado">
    <br><img src="${resource(dir: 'images/imagenesManuales/imagenesIngreso', file: 'img250.png')}"/>
</div>

<p><br><br><strong>Texto</strong></p>
<br>

<p>Nos presenta los textos a ser colocados en el documento impreso.</p>

<p>El primero corresponde a texto que se colocar&aacute en la parte superior del
documento despues de los datos de la cabecera.
</p>

<p>El segundo texto se colocar&aacute despues de los valores calculados.</p>

<div class="centrado">
    <br><img src="${resource(dir: 'images/imagenesManuales/imagenesIngreso', file: 'img251.png')}"/>
</div>

<p><br><br><strong>Firmas del Memorando</strong></p> <!--71-->
<br>

<p>Son el conjunto de firmas que se colocar&aacuten al final del documento.</p>

<p>Existe una firma por defecto a colocarse en el documento de Presupuesto; a
parte de eso es posible agregar m&aacutes firmas las cuales pueden ser
seleccionadas del combo y agregadas con el bot&oacuten Adicionar.
</p>

<div class="centrado">
    <br><img src="${resource(dir: 'images/imagenesManuales/imagenesIngreso', file: 'img252.png')}"/>
</div>

<p>El bot&oacuten rojo al lado de cada firma permite retirar de la lista la
firma ha ser agregada al documento.
</p>

<p><br><br><strong>F&OacuteRMULA POLIN&OacuteMICA</strong></p>
<br>

<p><strong>Datos</strong></p>
<br>

<p>Presenta un conjunto de datos provenientes del registro de la Obra.</p>

<div class="centrado">
    <br><img src="${resource(dir: 'images/imagenesManuales/imagenesIngreso', file: 'img253.png')}"/>
</div>

<p><br><strong>Nota</strong></p>
<br>

<p>Este texto se colocar&aacute luego de la tabla con los coeficientes
de los componentes.
</p>

<div class="centrado">
    <br><img src="${resource(dir: 'images/imagenesManuales/imagenesIngreso', file: 'img254.png')}"/>
</div>

<p><br><br><strong>Firmas de la F&oacutermula polin&oacutemica</strong></p>
<!--72-->
<br>

<p>Son el conjunto de firmas que se colocaran al final del documento.</p>

<p>Existen dos firmas por defecto a colocarse en el documento de Presupuesto;
a parte de eso es posible agregar m&aacutes firmas las cuales pueden ser
seleccionadas del combo y agregadas con el bot&oacuten Adicionar.
</p>

<div class="centrado">
    <br><img src="${resource(dir: 'images/imagenesManuales/imagenesIngreso', file: 'img255.png')}"/>
</div>

<p>El bot&oacuten rojo al lado de cada firma permite retirar de la lista la
firma ha ser agregada al documento.
</p>

<p><br><br><strong>TEXTOS FIJOS</strong></p>
<br>

<p>Muestra un conjunto de textos que se colocar&aacuten en los diferentes
documentos a ser impresos.</p>

<p>Consta de dos partes principales, Cabecera y Pie de P&aacutegina.</p>

<p><br><strong>Cabecera</strong></p>

<p>Aquellos textos que se colocar&aacuten en la parte superior de los documentos
a ser impresos. Los textos son editables, al dar clic en el bot&oacuten
Editar se habilitar&aacute los campos de texto para la edici&oacuten;
    posteriormente al dar clic en Aceptar se guardar&aacuten los cambios.
</p>

<div class="centrado">
    <br><img src="${resource(dir: 'images/imagenesManuales/imagenesIngreso', file: 'img256.png')}"/>
</div>

<p><br><br><strong>Pie de P&aacutegina</strong></p> <!--73-->
<p>Este texto se colocar&aacute en la parte correspondiente a condiciones
del contrato, en el documento correspondiente a Presupuesto.
</p>

<p>Igualmente el texto es editable al hacer clic en el bot&oacuten
Editar se habilitar&aacuten las cajas de texto, posteriormente se
pueden guardar los cambios al hacer clic en el bot&oacuten Aceptar.
</p>

<div class="centrado">
    <br><img src="${resource(dir: 'images/imagenesManuales/imagenesIngreso', file: 'img257.png')}"/>
</div> <br><br>
</div>
<br>


<div class="regresa">
    <p><a href="#volverIndice">Volver al &Iacutendice</a></p>
</div> <br> <br><br>

</div>

</body>
</html>
