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
            width            : 100%;
            height           : 100%;
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
        <a id="Inicio"></a>
        <div id="header2">
            <img src="${resource(dir: 'images/imagenesManuales/imagenesIngreso', file: 'encabezado.png')}"/>
        </div>

        <div class="centrado">
            <br><br><br><br>
            <img src="${resource(dir: 'images/imagenesManuales/imagenesIngreso', file: 'logo.png')}"/>
            <br><br>

            <h3>Sistema Integrado de Gesti&oacuten de Proyectos, Fiscalizaci&oacuten,
            Contrataci&oacuten y Ejecuci&oacuten de Obras</h3>
        </div>


        <br> <a id="IngresoSistema"></a>

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

        <a id="ModAdmin"></a>

        <div class="regresa">
            <p><a href="#Inicio">Volver al Inicio</a></p>
        </div>

    </body>
</html>
