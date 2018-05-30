package janus

class Obra implements Serializable {
    Persona responsableObra
    Persona revisor
    Persona inspector
    Comunidad comunidad
    Parroquia parroquia
    TipoObra tipoObjetivo
    Programacion programacion
    EstadoObra estadoObra
    ClaseObra claseObra
    Departamento departamento
    Departamento departamentoDestino
    Direccion direccionDestino
    Lugar lugar
    String barrio
    String codigo
    String nombre
    String descripcion
    Date fechaInicio
    Date fechaFin
    double distanciaPeso
    double distanciaVolumen
    double latitud
    double longitud
    int beneficiariosDirectos
    int benificiariosIndirectos
    int beneficiariosPotenciales
    String estado
    String referencia
    Date fechaPreciosRubros
    String oficioIngreso
    String oficioSalida
    int plazo
    String observaciones
    String tipo
    Date fechaCreacionObra
    Item chofer
    Item volquete
    String memoCantidadObra
    String memoSalida
    Date fechaOficioSalida
    int factorReduccion
    int factorVelocidad
    int capacidadVolquete
    double factorVolumen
    double factorPeso
    int factorReduccionTiempo
    String sitio
    int plazoEjecucionAnios
    int plazoEjecucionMeses
    int plazoEjecucionDias
    String formulaPolinomica
    int indicador
    double indiceGastosGenerales
    double impreso
    double indiceUtilidad
    int contrato
    double totales
    double valor
    Presupuesto partidaObra
    String memoCertificacionPartida
    String memoActualizacionPrefecto
    String memoPartidaPresupuestaria
    int porcentajeAnticipo
    int porcentajeReajuste
    double indiceCostosIndirectosObra
    double indiceCostosIndirectosMantenimiento
    double administracion
    double indiceCostosIndirectosGarantias
    double indiceCostosIndirectosCostosFinancieros
    double indiceCostosIndirectosVehiculos
    double indiceCostosIndirectosPromocion
    double indiceCostosIndirectosTimbresProvinciales
    double distanciaPesoEspecial
    double distanciaVolumenMejoramiento
    double distanciaVolumenCarpetaAsfaltica
    Lugar listaPeso1
    Lugar listaVolumen0
    Lugar listaVolumen1
    Lugar listaVolumen2
    Lugar listaManoObra

    int plazoPersonas = 8
    int plazoMaquinas = 1

    double desgloseEquipo = 0.52
    double desgloseRepuestos = 0.26
    double desgloseCombustible = 0.08
    double desgloseMecanico = 0.11
    double desgloseSaldo = 0.03

    String desgloseTransporte

    String coordenadas //coordenadas en sistema WGS84: -0.21,-78.5199 => S 0 12.5999999 W 78 31.194

    int liquidacion = 0

    Item transporteCamioneta
    Item transporteAcemila

    double distanciaCamioneta = 0
    double distanciaAcemila = 0

    String memoInicioObra
    Persona firmaInicioObra
    String anexos


    String observacionesInicioObra
	Date fechaImpresionInicioObra

	double longitudVia = 0
    double anchoVia = 0

    String memoSif
    String estadoSif
    Persona oferente=null
    double distanciaDesalojo = 0

    double indiceAlquiler = 0
    double indiceProfesionales = 0
    double indiceSeguros = 0
    double indiceSeguridad = 0
    double indiceCampo = 0
    double indiceCampamento = 0
    double indiceGastoObra = 0

    static auditable = true
    static mapping = {
        table 'obra'
        cache usage: 'read-write', include: 'non-lazy'
        id column: 'obra__id'
        id generator: 'identity'
        version false
        columns {
            id column: 'obra__id'
            responsableObra column: 'prsn__id'
            revisor column: 'obrarvsr'
            inspector column: 'obrainsp'
            comunidad column: 'cmnd__id'
            parroquia column: 'parr__id'
            tipoObjetivo column: 'tpob__id'
            programacion column: 'prog__id'
            estadoObra column: 'edob__id'
            claseObra column: 'csob__id'
            departamento column: 'dpto__id'
            departamentoDestino column: 'dptodstn'
            direccionDestino column: 'dircdstn'
            lugar column: 'lgar__id'
            codigo column: 'obracdgo'
            nombre column: 'obranmbr'
            descripcion column: 'obradscr'
            fechaInicio column: 'obrafcin'
            fechaFin column: 'obrafcfn'
            distanciaPeso column: 'obradsps'
            distanciaVolumen column: 'obradsvl'
            latitud column: 'obralatt'
            longitud column: 'obralong'
            beneficiariosDirectos column: 'obrabfdi'
            benificiariosIndirectos column: 'obrabfin'
            beneficiariosPotenciales column: 'obrabfpt'
            estado column: 'obraetdo'
            referencia column: 'obrarefe'
            fechaCreacionObra column: 'obrafcha'
            oficioIngreso column: 'obraofig'
            oficioSalida column: 'obraofsl'
            plazo column: 'obraplzo'
            observaciones column: 'obraobsr'
            tipo column: 'obratipo'
            fechaPreciosRubros column: 'rbpcfcha'
            chofer column: 'obrachfr'
            volquete column: 'obravlqt'
            memoCantidadObra column: 'obrammco'
            memoSalida column: 'obrammsl'
            fechaOficioSalida column: 'obrafcsl'
            factorReduccion column: 'obraftrd'
            factorVelocidad column: 'obravlcd'
            capacidadVolquete column: 'obracpvl'
            factorVolumen column: 'obraftvl'
            factorPeso column: 'obraftps'
            factorReduccionTiempo column: 'obrardtp'
            sitio column: 'obrasito'
            plazoEjecucionAnios column: 'obrapz_a'
            plazoEjecucionMeses column: 'obrapz_m'
            plazoEjecucionDias column: 'obrapz_d'
            formulaPolinomica column: 'obrafrpl'
            indicador column: 'obraindi'
            indiceGastosGenerales column: 'indignrl'
            impreso column: 'indiimpr'
            indiceUtilidad column: 'indiutil'
            contrato column: 'indicntr'
            totales column: 'inditotl'
            valor column: 'obravlor'
            partidaObra column: 'prsp__id'
            memoCertificacionPartida column: 'obrammpr'
            memoActualizacionPrefecto column: 'obraprft'
            memoPartidaPresupuestaria column: 'obrammfn'
            porcentajeAnticipo column: 'obraantc'
            porcentajeReajuste column: 'obrarjst'
            indiceCostosIndirectosObra column: 'indidrob'
            indiceCostosIndirectosMantenimiento column: 'indimntn'
            administracion column: 'indiadmn'
            indiceCostosIndirectosGarantias column: 'indigrnt'
            indiceCostosIndirectosCostosFinancieros column: 'indicsfn'
            indiceCostosIndirectosVehiculos column: 'indivhcl'
            indiceCostosIndirectosPromocion column: 'indiprmo'
            indiceCostosIndirectosTimbresProvinciales column: 'inditmbr'
            distanciaPesoEspecial column: 'obradses'
            distanciaVolumenMejoramiento column: 'obradsmj'
            distanciaVolumenCarpetaAsfaltica column: 'obradsca'
            barrio column: 'obrabarr'

            listaPeso1 column: 'lgarps01'
            listaVolumen0 column: 'lgarvl00'
            listaVolumen1 column: 'lgarvl01'
            listaVolumen2 column: 'lgarvl02'
            listaManoObra column: 'lgarlsmq'

            plazoPersonas column: 'obraplpr'
            plazoMaquinas column: 'obraplmq'

            desgloseEquipo column: 'obradseq'
            desgloseRepuestos column: 'obradsrp'
            desgloseCombustible column: 'obradscb'
            desgloseMecanico column: 'obradsmc'
            desgloseSaldo column: 'obradssl'
            desgloseTransporte column: 'obratrnp'

            coordenadas column: 'obracrdn'
            liquidacion column: 'obralqdc'

            transporteCamioneta column: 'itemtrcm'
            transporteAcemila column: 'itemtrac'

            distanciaCamioneta column: 'obratrcm'
            distanciaAcemila column: 'obratrac'

            memoInicioObra column: 'obrammio'
            firmaInicioObra column: 'prsnfrio'
            anexos column: 'obraanxo'

            observacionesInicioObra column: 'obraobin'
            fechaImpresionInicioObra column: 'obrafcii'

            longitudVia column: 'obralgvi'
            anchoVia column: 'obraanvi'
            oferente column: 'ofrt__id'
            memoSif column: 'obrammsf'
            estadoSif column: 'obraetsf'
            distanciaDesalojo column: 'obradsda'

            indiceAlquiler column: 'indialqr'
            indiceProfesionales column: 'indiprof'
            indiceSeguros column: 'indimate'
            indiceSeguridad column: 'indisgro'
            indiceCampo column: 'indicmpo'
            indiceCampamento column: 'indicmpm'
            indiceGastoObra column: 'indigaob'

        }
    }
    static constraints = {

        codigo(size: 1..25, blank: false, attributes: [title: 'numero'])
        nombre(size: 1..127, blank: true, nullable: true, attributes: [title: 'nombre'])
        responsableObra(blank: true, nullable: true, attributes: [title: 'responsableObra'])
        revisor(blank: true, nullable: true, attributes: [title: 'revisor'])
        lugar(blank: true, nullable: true, attributes: [title: 'lugar'])
        listaPeso1(blank: true, nullable: true, attributes: [title: 'Lista al peso 1'])
        listaVolumen0(blank: true, nullable: true, attributes: [title: 'Lista al volumen 1'])
        listaVolumen1(blank: true, nullable: true, attributes: [title: 'Lista al volumen 1'])
        listaVolumen2(blank: true, nullable: true, attributes: [title: 'Lista al volumen 1'])
        listaManoObra(blank: true, nullable: true, attributes: [title: 'Lista de MO y Equipos'])

        comunidad(blank: true, nullable: true, attributes: [title: 'comunidad'])
        parroquia(blank: true, nullable: true, attributes: [title: 'parroquia'])
        tipoObjetivo(blank: true, nullable: true, attributes: [title: 'tipoObjetivo'])
        claseObra(blank: true, nullable: true, attributes: [title: 'claseObra'])
        estadoObra(blank: true, nullable: true, attributes: [title: 'estadoObra'])
        programacion(blank: true, nullable: true, attributes: [title: 'programacion'])
        departamento(blank: true, nullable: true, attributes: [title: 'departamento'])
        departamentoDestino(blank: true, nullable: true, attributes: [title: 'departamento destino de la documentación'])
        direccionDestino(blank: true, nullable: true, attributes: [title: 'dirección destino de la documentación'])
        descripcion(size: 1..511, blank: true, nullable: true, attributes: [title: 'descripcion'])
        fechaInicio(blank: true, nullable: true, attributes: [title: 'fechaInicio'])
        fechaFin(blank: true, nullable: true, attributes: [title: 'fechaFin'])
        distanciaPeso(blank: true, nullable: true, attributes: [title: 'distanciaPeso'])
        distanciaVolumen(blank: true, nullable: true, attributes: [title: 'distanciaVolumen'])
        latitud(blank: true, nullable: true, attributes: [title: 'latitud'])
        longitud(blank: true, nullable: true, attributes: [title: 'longitud'])
        beneficiariosDirectos(blank: true, nullable: true, attributes: [title: 'beneficiariosDirectos'])
        benificiariosIndirectos(blank: true, nullable: true, attributes: [title: 'benificiariosIndirectos'])
        beneficiariosPotenciales(blank: true, nullable: true, attributes: [title: 'beneficiariosPotenciales'])
        estado(size: 1..1, blank: true, nullable: true, attributes: [title: 'estado'])
        referencia(size: 1..127, blank: true, nullable: true, attributes: [title: 'referencia'])
        fechaCreacionObra(blank: true, nullable: true, attributes: [title: 'fecha'])
        oficioIngreso(size: 1..20, blank: true, nullable: true, attributes: [title: 'oficioIngreso'])
        oficioSalida(size: 1..20, blank: true, nullable: true, attributes: [title: 'oficioSalida'])
        plazo(blank: true, nullable: true, attributes: [title: 'plazo'])
        observaciones(size: 1..127, blank: true, nullable: true, attributes: [title: 'observaciones'])
        tipo(size: 1..1, blank: true, nullable: true, attributes: [title: 'tipo'])
        fechaPreciosRubros(blank: true, nullable: true, attributes: [title: 'fecha'])
        chofer(size: 1..31, blank: true, nullable: true, attributes: [title: 'itemChofer'])
        volquete(size: 1..31, blank: true, nullable: true, attributes: [title: 'item/volquete'])
        memoCantidadObra(size: 1..20, blank: true, nullable: true, attributes: [title: 'memoCantidadObra'])
        memoSalida(size: 1..20, blank: true, nullable: true, attributes: [title: 'memoSalida'])
        fechaOficioSalida(blank: true, nullable: true, attributes: [title: 'fechaOficioSalida'])
        factorReduccion(blank: true, nullable: true, attributes: [title: 'factorReduccion'])
        factorVelocidad(blank: true, nullable: true, attributes: [title: 'factorVelocidad'])
        capacidadVolquete(blank: true, nullable: true, attributes: [title: 'capacidadVolquete'])
        factorVolumen(blank: true, nullable: true, attributes: [title: 'factorVolumen'])
        factorPeso(blank: true, nullable: true, attributes: [title: 'factorPeso'])
        factorReduccionTiempo(blank: true, nullable: true, attributes: [title: 'factorReduccionTiempo'])
        sitio(size: 1..63, blank: true, nullable: true, attributes: [title: 'sitio'])
        plazoEjecucionAnios(blank: true, nullable: true, attributes: [title: 'plazoEjecucionAnios'])
        plazoEjecucionMeses(blank: true, nullable: true, attributes: [title: 'plazoEjecucionMeses'])
        plazoEjecucionDias(blank: true, nullable: true, attributes: [title: 'plazoEjecucionDias'])
        formulaPolinomica(size: 1..20, blank: true, nullable: true, attributes: [title: 'formulaPolinomica'])
        indicador(blank: true, nullable: true, attributes: [title: 'indicador'])
        indiceGastosGenerales(blank: true, nullable: true, attributes: [title: 'indiceGastosGenerales'])
        impreso(blank: true, nullable: true, attributes: [title: 'impreso'])
        indiceUtilidad(blank: true, nullable: true, attributes: [title: 'indiceUtilidad'])
        contrato(blank: true, nullable: true, attributes: [title: 'contrato'])
        totales(blank: true, nullable: true, attributes: [title: 'totales'])
        valor(size: 1..10, blank: true, nullable: true, attributes: [title: 'valor'])
        partidaObra(size: 1..10, blank: true, nullable: true, attributes: [title: 'partidaObra'])
        memoCertificacionPartida(size: 1..10, blank: true, nullable: true, attributes: [title: 'memoCertificacionPartida'])
        memoActualizacionPrefecto(size: 1..10, blank: true, nullable: true, attributes: [title: 'memoActualizacionPrefecto'])
        memoPartidaPresupuestaria(size: 1..10, blank: true, nullable: true, attributes: [title: 'memoPartidaPresupuestaria'])
        porcentajeAnticipo(size: 1..10, blank: true, nullable: true, attributes: [title: 'porcentajeAnticipo'])
        porcentajeReajuste(size: 1..10, blank: true, nullable: true, attributes: [title: 'porcentajeReajuste'])
        indiceCostosIndirectosObra(blank: true, nullable: true, attributes: [title: 'indiceCostosIndirectosObra'])
        indiceCostosIndirectosMantenimiento(blank: true, nullable: true, attributes: [title: 'indiceCostosIndirectosMantenimiento'])
        administracion(blank: true, nullable: true, attributes: [title: 'administracion'])
        indiceCostosIndirectosGarantias(blank: true, nullable: true, attributes: [title: 'indiceCostosIndirectosGarantias'])
        indiceCostosIndirectosCostosFinancieros(blank: true, nullable: true, attributes: [title: 'indiceCostosIndirectosCostosFinancieros'])
        indiceCostosIndirectosVehiculos(blank: true, nullable: true, attributes: [title: 'indiceCostosIndirectosVehiculos'])
        indiceCostosIndirectosPromocion(blank: true, nullable: true, attributes: [title: 'indiceCostosIndirectosPromocion'])
        indiceCostosIndirectosTimbresProvinciales(blank: true, nullable: true, attributes: [title: 'indiceCostosIndirectosTimbresProvinciales'])

        distanciaPesoEspecial(blank: true, nullable: true, attributes: [title: 'distanciaPesoEspecial'])
        distanciaVolumenMejoramiento(blank: true, nullable: true, attributes: [title: 'distanciaVolumenMejoramiento'])
        distanciaVolumenCarpetaAsfaltica(blank: true, nullable: true, attributes: [title: 'distanciaVolumenCarpetaAsfaltica'])
        barrio(size: 1..127, blank: true, nullable: true, attributes: [title: 'barrio'])

        plazoPersonas(attributes: [title: 'Número de integrantes de cuadrillas'])
        plazoPersonas(attributes: [title: 'Número de equipos completos de maquinaria'])

        desgloseEquipo(attributes: [title: 'Desglose del transporte y equipos en Equipos'])
        desgloseRepuestos(attributes: [title: 'Desglose del transporte y equipos en Repuestos'])
        desgloseCombustible(attributes: [title: 'Desglose del transporte y equipos en Combustible'])
        desgloseMecanico(attributes: [title: 'Desglose del transporte y equipos en Mecánico'])
        desgloseSaldo(attributes: [title: 'Desglose del transporte y equipos en Saldo'])
        desgloseTransporte(size: 1..1, blank: true, nullable: true, attributes: [title: 'Desglose de transporte'])

        coordenadas(size: 0..254, blank: true, nullable: true, attributes: [title: 'Coordenadas en formato WGS84'])


        transporteCamioneta(blank: true, nullable: true, attibutes: [title: 'Transporte en Camioneta'])
        transporteAcemila(blank: true, nullable: true, attributes: [title: 'Transporte en Acémila'])

        distanciaCamioneta(blank: true, nullable: true)
        distanciaAcemila(blank: true, nullable: true)

        memoInicioObra(blank: true, nullable: true, maxSize: 20, attributes: [title: 'Memo de inicio de obra'])
        firmaInicioObra(blank: true, nullable: true, attributes: [title: 'Firma para el memo de inicio de obra'])
        anexos(blank: true, nullable: true, maxSize: 255, attributes: [title: 'Anexos y planos ingresados a la biblioteca'])

        observacionesInicioObra(blank: true, nullable: true, size: 1..255)

        fechaImpresionInicioObra(blank: true, nullable: true)
        longitudVia(blank: true, nullable: true, attributes: [title: 'longitud de la vía'])
        anchoVia(blank: true, nullable: true, attributes: [title: 'ancho de la vía'])

        memoSif(blank: true, maxSize: 20, nullable: true)
        estadoSif(blank: true, maxSize: 1, nullable: true)
        oferente(blank:true,nullable:true)
        distanciaDesalojo(blank:true,nullable:true)

        indiceAlquiler(blank: true, nullable: true)
        indiceProfesionales(blank: true, nullable: true)
        indiceSeguros(blank:true, nullable:true)
        indiceSeguridad(blank:true, nullable:true)
        indiceCampo(blank:true, nullable:true)
        indiceCampamento(blank:true, nullable:true)
        indiceGastoObra(blank:true, nullable:true)
    }

    String toString() {
        return this.nombre
    }
}