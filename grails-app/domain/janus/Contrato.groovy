package janus

import janus.pac.Oferta
import janus.ejecucion.PeriodosInec
import janus.pac.Proveedor
import janus.pac.TipoContrato
import janus.pac.TipoPlazo

class Contrato implements Serializable {

    Oferta oferta
    TipoContrato tipoContrato
    TipoPlazo tipoPlazo
    Contrato padre
    PeriodosInec periodoInec    /** antes PeriodoValidez **/
    String codigo
    String objeto
    Date fechaSubscripcion
    Date fechaIngreso
    Date fechaInicio
    Date fechaFin
    Double monto
    Double financiamiento
    Double porcentajeAnticipo
    Double anticipo
    Double multas
    Double plazo
    String estado
    String responsableTecnico
    Date fechaFirma
    String cuentaContable
    String prorroga
    String observaciones
    String memo

    Double multaRetraso                 //multa por retraso de obra (solo en la ultima planilla de avance)
    Double multaPlanilla                //multa por no presentacion de la planilla (retraso en la presentacion)

    Double multaIncumplimiento
    //multa por incumplimiento del cronograma (retraso de obra en las planillas de avance)
    Double multaDisposiciones           //multa por no acatar las disposiciones del fiscalizador

    Date fechaPedidoRecepcionContratista
    Date fechaPedidoRecepcionFiscalizador

    Departamento depAdministrador

//    Persona administrador
    Persona delegadoPrefecto
    Persona delegadoFiscalizacion

    String clausula
    String numeralPlazo
    String numeralAnticipo
    Double indirectos

    Obra obraContratada
    Proveedor contratista
    int conReajuste = 0
    String adicionales
    int aplicaReajuste = 1
    int saldoMulta = 0



    static auditable = true
    static mapping = {

        table 'cntr'
        cache usage: 'read-write', include: 'non-lazy'
        id column: 'cntr__id'
        id generator: 'identity'
        version false
        columns {
            id column: 'cntr__id'

            oferta column: 'ofrt__id'
            tipoContrato column: 'tpcr__id'
            tipoPlazo column: 'tppz__id'
            padre column: 'cntrpdre'
            periodoInec column: 'prin__id'
            codigo column: 'cntrcdgo'
            objeto column: 'cntrobjt'
            fechaSubscripcion column: 'cntrfcsb'
            fechaIngreso column: 'cntrfcig'
            fechaInicio column: 'cntrfcin'
            fechaFin column: 'cntrfcfn'
            monto column: 'cntrmnto'
            financiamiento column: 'cntrfina'
            porcentajeAnticipo column: 'cntrpcan'
            anticipo column: 'cntrantc'
            multas column: 'cntrmlta'
            estado column: 'cntretdo'
            responsableTecnico column: 'cntrrptc'
            fechaFirma column: 'cntrfcfr'
            cuentaContable column: 'cntrcnta'
            prorroga column: 'cntrprrg'
            observaciones column: 'cntrobsr'
            memo column: 'cntrmemo'
            plazo column: 'cntrplzo'

            multaRetraso column: "cntrmlrt"
            multaPlanilla column: "cntrmlpl"

            multaIncumplimiento column: 'cntrmlin'
            multaDisposiciones column: 'cntrmlds'

            fechaPedidoRecepcionContratista column: 'cntrfccn'
            fechaPedidoRecepcionFiscalizador column: 'cntrfcfs'

            depAdministrador column: 'dptoadmn'

//            administrador column: 'prsnadmn'
            delegadoPrefecto column: 'prsndlpr'
            delegadoFiscalizacion column: 'prsndlfs'

            clausula column: 'cntrclsl'
            numeralPlazo column: 'cntrnmpl'
            numeralAnticipo column: 'cntrnman'
            indirectos column: 'cntrindi'
            obraContratada column: 'obra__id'
            contratista column: 'prve__id'
            conReajuste column: 'cntrrjst'
            adicionales column: 'cntradcn'
            aplicaReajuste column: 'cntraprj'
            saldoMulta column: 'cntrsldo'
        }
    }

    static constraints = {
        oferta(blank: true, nullable: true)
        tipoContrato(blank: true, nullable: true)
        tipoPlazo(blank: true, nullable: true)
        padre(blank: true, nullable: true)
        periodoInec(blank: true, nullable: true)
        codigo(blank: true, nullable: true)
        objeto(size: 1..1023, blank: true, nullable: true)
        fechaSubscripcion(blank: true, nullable: true)
        fechaIngreso(blank: true, nullable: true)
        fechaInicio(blank: true, nullable: true)
        fechaFin(blank: true, nullable: true)
        monto(blank: true, nullable: true)
        financiamiento(blank: true, nullable: true)
        porcentajeAnticipo(blank: true, nullable: true)
        anticipo(blank: true, nullable: true)
        multas(blank: true, nullable: true)
        estado(blank: true, nullable: true)
        responsableTecnico(blank: true, nullable: true)
        fechaFirma(blank: true, nullable: true)
        cuentaContable(blank: true, nullable: true)
        prorroga(blank: true, nullable: true)
        observaciones(blank: true, nullable: true)
        memo(blank: true, nullable: true, maxSize: 20)
        plazo(blank: true, nullable: true)

        fechaPedidoRecepcionContratista(blank: true, nullable: true)
        fechaPedidoRecepcionFiscalizador(blank: true, nullable: true)

        depAdministrador(blank: true, nullable: true)

//        administrador(blank: true, nullable: true)
        delegadoPrefecto(blank: true, nullable: true)
        delegadoFiscalizacion(blank: true, nullable: true)

        clausula(blank: true, nullable: true, maxSize: 20)
        numeralAnticipo(blank: true, nullable: true, maxSize: 10)
        numeralPlazo(blank: true, nullable: true, maxSize: 10)
        indirectos(blank: true, nullable: true)
        obraContratada(blank: true, nullable: true)
        contratista(blank: true, nullable: true)
        adicionales(maxSize: 20, blank: true, nullable: true)

    }

    def getObra() {
        println "(obraContratada): ${this.obraContratada}, concurso: ${this.oferta?.concurso?.obra?.codigo}"
        if(this.obraContratada == null){
            def tmp_obra = Obra.findByCodigo(this.oferta?.concurso?.obra?.codigo+"-OF")
            if(!tmp_obra) {
                if(this.obraContratada == null) this.obraContratada = this.oferta?.concurso?.obra
            } else {
                if(this.obraContratada == null) this.obraContratada = tmp_obra
            }
        }
        if(this.contratista == null){
            this.contratista = this.oferta?.proveedor
        }
        return this.obraContratada
    }

    def getAdministradorContrato() {
        def admins = AdministradorContrato.withCriteria {
            eq("contrato", this)
            le("fechaInicio", new Date().clearTime())
            or {
                ge("fechaFin", new Date().clearTime())
                isNull("fechaFin")
            }
            order("fechaInicio", "desc")
        }
//        println admins
        if (admins.size() == 0) {
            return new AdministradorContrato()
        } else if (admins.size() == 1) {
            return admins.first()
        } else {
            println "Alerta hay varios admins: contrato: " + this.id + " admins: " + admins.id
            return admins.first()
        }
    }

    def getAdministrador() {
        return this.administradorContrato?.administrador
    }

    def getFiscalizadorContrato() {
        def fiscs = FiscalizadorContrato.withCriteria {
            eq("contrato", this)
            le("fechaInicio", new Date().clearTime())
            or {
                ge("fechaFin", new Date().clearTime())
                isNull("fechaFin")
            }
            order("fechaInicio", "desc")
        }
//        println fiscs
        if (fiscs.size() == 0) {
            return new FiscalizadorContrato()
        } else if (fiscs.size() == 1) {
            return fiscs.first()
        } else {
            println "Alerta hay varios fiscalizadores: contrato: " + this.id + " fiscs: " + fiscs.id
            return fiscs.first()
        }
    }

    def getFiscalizador() {
        return this.fiscalizadorContrato?.fiscalizador
    }

}
