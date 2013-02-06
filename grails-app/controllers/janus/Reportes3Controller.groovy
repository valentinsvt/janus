package janus

class Reportes3Controller {

    def preciosService

    def index() { }

    def test() {
        return [params: params]
    }

    def imprimirTablaSub(){
//        println "imprimir tabla sub "+params
        def obra = Obra.get(params.obra)
        def detalle
        if (params.sub)
            detalle= VolumenesObra.findAllByObraAndSubPresupuesto(obra,SubPresupuesto.get(params.sub),[sort:"orden"])
        else
            detalle= VolumenesObra.findAllByObra(obra,[sort:"orden"])
        def subPres = VolumenesObra.findAllByObra(obra,[sort:"orden"]).subPresupuesto.unique()

        def precios = [:]

        def indirecto = obra.totales/100
        preciosService.ac_rbroObra(obra.id)
//        println "indirecto "+indirecto

        detalle.each{
//            def parametros = ""+it.item.id+","+lugar.id+",'"+fecha.format("yyyy-MM-dd")+"',"+dsps.toDouble()+","+dsvl.toDouble()+","+rendimientos["rdps"]+","+rendimientos["rdvl"]

            def res = preciosService.presioUnitarioVolumenObra("sum(parcial)+sum(parcial_t) precio ",obra.id,it.item.id)
//            def res = preciosService.rb_precios("sum(parcial)+sum(parcial_t) precio ",parametros,"")
            precios.put(it.id.toString(),(res["precio"][0]+res["precio"][0]*indirecto).toDouble().round(2))
        }
//
//        println "precios "+precios


        [detalle:detalle,precios:precios,subPres:subPres,subPre:SubPresupuesto.get(params.sub).descripcion,obra: obra,indirectos:indirecto*100]
    }
    def imprimirRubroVolObra(){
//        println "imprimir rubro "+params
        def rubro =Item.get(params.id)
        def obra=Obra.get(params.obra)
        def fecha = new Date().parse("dd-MM-yyyy",params.fecha)
        def indi = obra.totales
        try{
            indi=indi.toDouble()
        } catch (e){
            println "error parse "+e
            indi=21.5
        }

        preciosService.ac_rbroObra(obra.id)
        def res = preciosService.presioUnitarioVolumenObra("*",obra.id,rubro.id)
        def tablaHer='<table class="table table-bordered table-striped table-condensed table-hover"> '
        def tablaMano='<table class="table table-bordered table-striped table-condensed table-hover"> '
        def tablaMat='<table class="table table-bordered table-striped table-condensed table-hover"> '
        def tablaTrans='<table class="table table-bordered table-striped table-condensed table-hover"> '
        def tablaIndi='<table class="table table-bordered table-striped table-condensed table-hover"> '
        def total = 0,totalHer=0,totalMan=0,totalMat=0
        tablaTrans+="<thead><tr><th colspan='7'>Transporte</th></tr><tr><th style='width: 80px;'>Código</th><th style='width:610px'>Descripción</th><th>Pes/Vol</th><th>Cantidad</th><th>Distancia</th><th>Unitario</th><th>C.Total</th></tr></thead><tbody>"

        tablaHer+="<thead><tr><th colspan='7'>Herramienta</th></tr><tr><th style='width: 80px;'>Código</th><th style='width:610px'>Descripción</th><th>Cantidad</th><th>Tarifa</th><th>Costo</th><th>Rendimiento</th><th>C.Total</th></tr></thead><tbody>"
        tablaMano+="<thead><tr><th colspan='7'>Mano de obra</th></tr><tr><th style='width: 80px;'>Código</th><th style='width:610px'>Descripción</th><th>Cantidad</th><th>Jornal</th><th>Costo</th><th>Rendimiento</th><th>C.Total</th></tr></thead><tbody>"
        tablaMat+="<thead><tr><th colspan='7'>Materiales</th></tr><tr><th style='width: 80px;'>Código</th><th style='width:610px'>Descripción</th><th>Cantidad</th><th>Unitario</th><th></th><th></th><th>C.Total</th></tr></thead><tbody>"
//        println "rends "+rendimientos

//        println "res "+res

        res.each {r->
            if(r["grpocdgo"]==3){
                tablaHer+="<tr>"
                tablaHer+="<td style='width: 80px;'>"+r["itemcdgo"]+"</td>"
                tablaHer+="<td>"+r["itemnmbr"]+"</td>"
                tablaHer+="<td style='width: 50px;text-align: right'>"+ g.formatNumber(number:r["rbrocntd"] ,format:"##,#####0", minFractionDigits:"5", maxFractionDigits:"5", locale: "ec")+"</td>"
                tablaHer+="<td style='width: 50px;text-align: right'>"+g.formatNumber(number:r["rbpcpcun"] ,format:"##,#####0", minFractionDigits:"5", maxFractionDigits:"5", locale: "ec")+"</td>"
                tablaHer+="<td style='width: 50px;text-align: right'>"+g.formatNumber(number:r["rbpcpcun"]*r["rbrocntd"] ,format:"##,#####0", minFractionDigits:"5", maxFractionDigits:"5", locale: "ec")+"</td>"
                tablaHer+="<td style='width: 50px;text-align: right'>"+g.formatNumber(number:r["rndm"] ,format:"##,#####0", minFractionDigits:"5", maxFractionDigits:"5", locale: "ec")+"</td>"
                tablaHer+="<td style='width: 50px;text-align: right'>"+g.formatNumber(number:r["parcial"] ,format:"##,#####0", minFractionDigits:"5", maxFractionDigits:"5", locale: "ec")+"</td>"
                totalHer+=r["parcial"]
                tablaHer+="</tr>"
            }
            if(r["grpocdgo"]==2){
                tablaMano+="<tr>"
                tablaMano+="<td style='width: 80px;'>"+r["itemcdgo"]+"</td>"
                tablaMano+="<td>"+r["itemnmbr"]+"</td>"
                tablaMano+="<td style='width: 50px;text-align: right'>"+g.formatNumber(number:r["rbrocntd"] ,format:"##,#####0", minFractionDigits:"5", maxFractionDigits:"5", locale: "ec")+"</td>"
                tablaMano+="<td style='width: 50px;text-align: right'>"+g.formatNumber(number:r["rbpcpcun"] ,format:"##,#####0", minFractionDigits:"5", maxFractionDigits:"5", locale: "ec")+"</td>"
                tablaMano+="<td style='width: 50px;text-align: right'>"+g.formatNumber(number:r["rbpcpcun"]*r["rbrocntd"] ,format:"##,#####0", minFractionDigits:"5", maxFractionDigits:"5", locale: "ec")+"</td>"
                tablaMano+="<td style='width: 50px;text-align: right'>"+g.formatNumber(number:r["rndm"] ,format:"##,#####0", minFractionDigits:"5", maxFractionDigits:"5", locale: "ec")+"</td>"
                tablaMano+="<td style='width: 50px;text-align: right'>"+g.formatNumber(number:r["parcial"] ,format:"##,#####0", minFractionDigits:"5", maxFractionDigits:"5", locale: "ec")+"</td>"
                totalMan+=r["parcial"]
                tablaMano+="</tr>"
            }
            if(r["grpocdgo"]==1){
                tablaMat+="<tr>"
                tablaMat+="<td style='width: 80px;'>"+r["itemcdgo"]+"</td>"
                tablaMat+="<td>"+r["itemnmbr"]+"</td>"
                tablaMat+="<td style='width: 50px;text-align: right'>"+g.formatNumber(number:r["rbrocntd"] ,format:"##,#####0", minFractionDigits:"5", maxFractionDigits:"5", locale: "ec")+"</td>"
                tablaMat+="<td style='width: 50px;text-align: right'>"+g.formatNumber(number:r["rbpcpcun"] ,format:"##,#####0", minFractionDigits:"5", maxFractionDigits:"5", locale: "ec")+"</td>"
                tablaMat+="<td style='width: 50px;text-align: right'></td>"
                tablaMat+="<td style='width: 50px;text-align: right'></td>"
                tablaMat+="<td style='width: 50px;text-align: right'>"+r["parcial"]+"</td>"
                totalMat+=r["parcial"]
                tablaMat+="</tr>"
            }
            if(r["parcial_t"]>0){
                tablaTrans+="<tr>"
                tablaTrans+="<td style='width: 80px;'>"+r["itemcdgo"]+"</td>"
                tablaTrans+="<td>"+r["itemnmbr"]+"</td>"
                tablaTrans+="<td style='width: 50px;text-align: right'>"+g.formatNumber(number:r["itempeso"] ,format:"##,#####0", minFractionDigits:"5", maxFractionDigits:"5", locale: "ec")+"</td>"
                tablaTrans+="<td style='width: 50px;text-align: right'>"+g.formatNumber(number:r["rbrocntd"] ,format:"##,#####0", minFractionDigits:"5", maxFractionDigits:"5", locale: "ec")+"</td>"
                tablaTrans+="<td style='width: 50px;text-align: right'>"+g.formatNumber(number:r["distancia"] ,format:"##,#####0", minFractionDigits:"5", maxFractionDigits:"5", locale: "ec")+"</td>"
                tablaTrans+="<td style='width: 50px;text-align: right'>"+g.formatNumber(number:r["parcial_t"]/(r["itempeso"]*r["rbrocntd"]*r["distancia"]) ,format:"##,#####0", minFractionDigits:"5", maxFractionDigits:"5", locale: "ec")+"</td>"
                tablaTrans+="<td style='width: 50px;text-align: right'>"+g.formatNumber(number:r["parcial_t"] ,format:"##,#####0", minFractionDigits:"5", maxFractionDigits:"5", locale: "ec")+"</td>"
                total+=r["parcial_t"]
                tablaTrans+="</tr>"
            }

        }
        tablaTrans+="<tr><td><b>SUBTOTAL</b></td><td></td><td></td><td></td><td></td><td></td><td style='width: 50px;text-align: right'>${g.formatNumber(number:total ,format:"##,#####0", minFractionDigits:"5", maxFractionDigits:"5", locale: "ec")}</td></tr>"
        tablaTrans+="</tbody></table>"

        tablaHer+="<tr><td><b>SUBTOTAL</b></td><td></td><td></td><td></td><td></td><td></td><td style='width: 50px;text-align: right'>${g.formatNumber(number:totalHer,format:"##,#####0", minFractionDigits:"5", maxFractionDigits:"5", locale: "ec")}</td></tr>"
        tablaHer+="</tbody></table>"
        tablaMano+="<tr><td><b>SUBTOTAL</b></td><td></td><td></td><td></td><td></td><td></td><td style='width: 50px;text-align: right'>${g.formatNumber(number:totalMan ,format:"##,#####0", minFractionDigits:"5", maxFractionDigits:"5", locale: "ec")}</td></tr>"
        tablaMano+="</tbody></table>"
        tablaMat+="<tr><td><b>SUBTOTAL</b></td><td></td><td></td><td></td><td></td><td></td><td style='width: 50px;text-align: right'>${g.formatNumber(number:totalMat ,format:"##,#####0", minFractionDigits:"5", maxFractionDigits:"5", locale: "ec")}</td></tr>"
        tablaMat+="</tbody></table>"
        def totalRubro=total+totalHer+totalMan+totalMat
        totalRubro=totalRubro.toDouble().round(5)
        def totalIndi=totalRubro*indi/100
        totalIndi=totalIndi.toDouble().round(5)
        tablaIndi+="<thead><tr><th colspan='3'>Costos indirectos</th></tr><tr><th style='width:550px'>Descripción</th><th>Porcentaje</th><th>Valor</th></tr></thead>"
        tablaIndi+="<tbody><tr><td>Costos indirectos</td><td style='text-align:right'>${indi}%</td><td style='text-align:right'>${g.formatNumber(number:totalIndi ,format:"##,#####0", minFractionDigits:"5", maxFractionDigits:"5", locale: "ec")}</td></tr></tbody>"
        tablaIndi+="</table>"

        if (total==0)
            tablaTrans=""
        if(totalHer==0)
            tablaHer=""
        if(totalMan==0)
            tablaMano=""
        if(totalMat==0)
            tablaMat=""
        println "fin reporte rubro"
        [rubro:rubro,fechaPrecios:fecha,tablaTrans:tablaTrans,tablaHer:tablaHer,tablaMano:tablaMano,tablaMat:tablaMat,tablaIndi:tablaIndi,totalRubro:totalRubro,totalIndi:totalIndi]



    }

    def imprimirRubro(){
//        println "imprimir rubro "+params
        def rubro = Item.get(params.id)
        def fecha = new Date().parse("dd-MM-yyyy",params.fecha)
        def lugar = params.lugar
        def indi = params.indi
        try{
            indi=indi.toDouble()
        } catch (e){
            println "error parse "+e
            indi=21.5
        }
        if (!params.dsps){
            params.dsps=0 //distancia peso
            params.prch=0 //precio chofer
            params.prvl=0 //precio volquete
            params.dsvs=0 //distancia volumen
        }
        if (!params.dsvs){
            params.dsps=0
            params.dsvs=0
            params.prvl=0
            params.prch=0
        }
        if (!params.prch)
            params.prch=0
        if (!params.prvl)
            params.prvl=0
        def rendimientos
        if(!params.obra)
            rendimientos = preciosService.rendimientoTranposrte(params.dsps.toDouble(),params.dsvs.toDouble(),params.prch.toDouble(),params.prvl.toDouble())
        else
            rendimientos = preciosService.rendimientoTransporteLuz(Obra.get(params.obra),params.prch.toDouble(),params.prvl.toDouble())

//        println "rends "+rendimientos
        if (rendimientos["rdps"].toString()=="NaN" || rendimientos["rdps"].toString()=="Infinity"){
            rendimientos["rdps"]=0
            rendimientos["rdvl"]=0
        }
        if (rendimientos["rdvl"].toString()=="NaN" || rendimientos["rdvl"].toString()=="Infinity"){
            rendimientos["rdvl"]=0
            rendimientos["rdps"]=0
        }
        def parametros = ""+params.id+","+params.lugar+",'"+fecha.format("yyyy-MM-dd")+"',"+params.dsps.toDouble()+","+params.dsvs.toDouble()+","+rendimientos["rdps"]+","+rendimientos["rdvl"]
        preciosService.ac_rbro(params.id,params.lugar,fecha.format("yyyy-MM-dd"))
        def res = preciosService.rb_precios(parametros,"")
        def tablaHer='<table class="table table-bordered table-striped table-condensed table-hover"> '
        def tablaMano='<table class="table table-bordered table-striped table-condensed table-hover"> '
        def tablaMat='<table class="table table-bordered table-striped table-condensed table-hover"> '
        def tablaTrans='<table class="table table-bordered table-striped table-condensed table-hover"> '
        def tablaIndi='<table class="table table-bordered table-striped table-condensed table-hover"> '
        def total = 0,totalHer=0,totalMan=0,totalMat=0
        tablaTrans+="<thead><tr><th colspan='7'>Transporte</th></tr><tr><th style='width: 80px;'>Código</th><th style='width:610px'>Descripción</th><th>Pes/Vol</th><th>Cantidad</th><th>Distancia</th><th>Unitario</th><th>C.Total</th></tr></thead><tbody>"

        tablaHer+="<thead><tr><th colspan='7'>Herramienta</th></tr><tr><th style='width: 80px;'>Código</th><th style='width:610px'>Descripción</th><th>Cantidad</th><th>Tarifa</th><th>Costo</th><th>Rendimiento</th><th>C.Total</th></tr></thead><tbody>"
        tablaMano+="<thead><tr><th colspan='7'>Mano de obra</th></tr><tr><th style='width: 80px;'>Código</th><th style='width:610px'>Descripción</th><th>Cantidad</th><th>Jornal</th><th>Costo</th><th>Rendimiento</th><th>C.Total</th></tr></thead><tbody>"
        tablaMat+="<thead><tr><th colspan='7'>Materiales</th></tr><tr><th style='width: 80px;'>Código</th><th style='width:610px'>Descripción</th><th>Cantidad</th><th>Unitario</th><th></th><th></th><th>C.Total</th></tr></thead><tbody>"
//        println "rends "+rendimientos

//        println "res "+res
               
        res.each {r->
            if(r["grpocdgo"]==3){
                tablaHer+="<tr>"
                tablaHer+="<td style='width: 80px;'>"+r["itemcdgo"]+"</td>"
                tablaHer+="<td>"+r["itemnmbr"]+"</td>"
                tablaHer+="<td style='width: 50px;text-align: right'>"+ g.formatNumber(number:r["rbrocntd"] ,format:"##,#####0", minFractionDigits:"5", maxFractionDigits:"5", locale: "ec")+"</td>"
                tablaHer+="<td style='width: 50px;text-align: right'>"+g.formatNumber(number:r["rbpcpcun"] ,format:"##,#####0", minFractionDigits:"5", maxFractionDigits:"5", locale: "ec")+"</td>"
                tablaHer+="<td style='width: 50px;text-align: right'>"+g.formatNumber(number:r["rbpcpcun"]*r["rbrocntd"] ,format:"##,#####0", minFractionDigits:"5", maxFractionDigits:"5", locale: "ec")+"</td>"
                tablaHer+="<td style='width: 50px;text-align: right'>"+g.formatNumber(number:r["rndm"] ,format:"##,#####0", minFractionDigits:"5", maxFractionDigits:"5", locale: "ec")+"</td>"
                tablaHer+="<td style='width: 50px;text-align: right'>"+g.formatNumber(number:r["parcial"] ,format:"##,#####0", minFractionDigits:"5", maxFractionDigits:"5", locale: "ec")+"</td>"
                totalHer+=r["parcial"]
                tablaHer+="</tr>"
            }
            if(r["grpocdgo"]==2){
                tablaMano+="<tr>"
                tablaMano+="<td style='width: 80px;'>"+r["itemcdgo"]+"</td>"
                tablaMano+="<td>"+r["itemnmbr"]+"</td>"
                tablaMano+="<td style='width: 50px;text-align: right'>"+g.formatNumber(number:r["rbrocntd"] ,format:"##,#####0", minFractionDigits:"5", maxFractionDigits:"5", locale: "ec")+"</td>"
                tablaMano+="<td style='width: 50px;text-align: right'>"+g.formatNumber(number:r["rbpcpcun"] ,format:"##,#####0", minFractionDigits:"5", maxFractionDigits:"5", locale: "ec")+"</td>"
                tablaMano+="<td style='width: 50px;text-align: right'>"+g.formatNumber(number:r["rbpcpcun"]*r["rbrocntd"] ,format:"##,#####0", minFractionDigits:"5", maxFractionDigits:"5", locale: "ec")+"</td>"
                tablaMano+="<td style='width: 50px;text-align: right'>"+g.formatNumber(number:r["rndm"] ,format:"##,#####0", minFractionDigits:"5", maxFractionDigits:"5", locale: "ec")+"</td>"
                tablaMano+="<td style='width: 50px;text-align: right'>"+g.formatNumber(number:r["parcial"] ,format:"##,#####0", minFractionDigits:"5", maxFractionDigits:"5", locale: "ec")+"</td>"
                totalMan+=r["parcial"]
                tablaMano+="</tr>"
            }
            if(r["grpocdgo"]==1){
                tablaMat+="<tr>"
                tablaMat+="<td style='width: 80px;'>"+r["itemcdgo"]+"</td>"
                tablaMat+="<td>"+r["itemnmbr"]+"</td>"
                tablaMat+="<td style='width: 50px;text-align: right'>"+g.formatNumber(number:r["rbrocntd"] ,format:"##,#####0", minFractionDigits:"5", maxFractionDigits:"5", locale: "ec")+"</td>"
                tablaMat+="<td style='width: 50px;text-align: right'>"+g.formatNumber(number:r["rbpcpcun"] ,format:"##,#####0", minFractionDigits:"5", maxFractionDigits:"5", locale: "ec")+"</td>"
                tablaMat+="<td style='width: 50px;text-align: right'></td>"
                tablaMat+="<td style='width: 50px;text-align: right'></td>"
                tablaMat+="<td style='width: 50px;text-align: right'>"+r["parcial"]+"</td>"
                totalMat+=r["parcial"]
                tablaMat+="</tr>"
            }
            if(r["parcial_t"]>0){
                tablaTrans+="<tr>"
                tablaTrans+="<td style='width: 80px;'>"+r["itemcdgo"]+"</td>"
                tablaTrans+="<td>"+r["itemnmbr"]+"</td>"
                tablaTrans+="<td style='width: 50px;text-align: right'>"+g.formatNumber(number:r["itempeso"] ,format:"##,#####0", minFractionDigits:"5", maxFractionDigits:"5", locale: "ec")+"</td>"
                tablaTrans+="<td style='width: 50px;text-align: right'>"+g.formatNumber(number:r["rbrocntd"] ,format:"##,#####0", minFractionDigits:"5", maxFractionDigits:"5", locale: "ec")+"</td>"
                tablaTrans+="<td style='width: 50px;text-align: right'>"+g.formatNumber(number:r["distancia"] ,format:"##,#####0", minFractionDigits:"5", maxFractionDigits:"5", locale: "ec")+"</td>"
                tablaTrans+="<td style='width: 50px;text-align: right'>"+g.formatNumber(number:r["parcial_t"]/(r["itempeso"]*r["rbrocntd"]*r["distancia"]) ,format:"##,#####0", minFractionDigits:"5", maxFractionDigits:"5", locale: "ec")+"</td>"
                tablaTrans+="<td style='width: 50px;text-align: right'>"+g.formatNumber(number:r["parcial_t"] ,format:"##,#####0", minFractionDigits:"5", maxFractionDigits:"5", locale: "ec")+"</td>"
                total+=r["parcial_t"]
                tablaTrans+="</tr>"
            }

        }
        tablaTrans+="<tr><td><b>SUBTOTAL</b></td><td></td><td></td><td></td><td></td><td></td><td style='width: 50px;text-align: right'>${g.formatNumber(number:total ,format:"##,#####0", minFractionDigits:"5", maxFractionDigits:"5", locale: "ec")}</td></tr>"
        tablaTrans+="</tbody></table>"

        tablaHer+="<tr><td><b>SUBTOTAL</b></td><td></td><td></td><td></td><td></td><td></td><td style='width: 50px;text-align: right'>${g.formatNumber(number:totalHer,format:"##,#####0", minFractionDigits:"5", maxFractionDigits:"5", locale: "ec")}</td></tr>"
        tablaHer+="</tbody></table>"
        tablaMano+="<tr><td><b>SUBTOTAL</b></td><td></td><td></td><td></td><td></td><td></td><td style='width: 50px;text-align: right'>${g.formatNumber(number:totalMan ,format:"##,#####0", minFractionDigits:"5", maxFractionDigits:"5", locale: "ec")}</td></tr>"
        tablaMano+="</tbody></table>"
        tablaMat+="<tr><td><b>SUBTOTAL</b></td><td></td><td></td><td></td><td></td><td></td><td style='width: 50px;text-align: right'>${g.formatNumber(number:totalMat ,format:"##,#####0", minFractionDigits:"5", maxFractionDigits:"5", locale: "ec")}</td></tr>"
        tablaMat+="</tbody></table>"
        def totalRubro=total+totalHer+totalMan+totalMat
        def totalIndi=totalRubro*indi/100
        tablaIndi+="<thead><tr><th colspan='3'>Costos indirectos</th></tr><tr><th style='width:550px'>Descripción</th><th>Porcentaje</th><th>Valor</th></tr></thead>"
        tablaIndi+="<tbody><tr><td>Costos indirectos</td><td style='text-align:right'>${indi}%</td><td style='text-align:right'>${g.formatNumber(number:totalIndi ,format:"##,#####0", minFractionDigits:"5", maxFractionDigits:"5")}</td></tr></tbody>"
        tablaIndi+="</table>"

        if (total==0)
            tablaTrans=""
        if(totalHer==0)
            tablaHer=""
        if(totalMan==0)
            tablaMano=""
        if(totalMat==0)
            tablaMat=""
        println "fin reporte rubro"
        [rubro:rubro,fechaPrecios:fecha,tablaTrans:tablaTrans,tablaHer:tablaHer,tablaMano:tablaMano,tablaMat:tablaMat,tablaIndi:tablaIndi,totalRubro:totalRubro,totalIndi:totalIndi]



    }
}
