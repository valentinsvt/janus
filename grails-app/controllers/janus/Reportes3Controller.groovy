package janus

class Reportes3Controller {

    def preciosService

    def index() { }

    def test() {
        return [params: params]
    }

    def imprimirRubro(){
        println "imprimir rubro "+params
        def rubro = Item.get(params.id)
        def fecha = new Date().parse("dd-MM-yyyy",params.fecha)
        def lugar = params.lugar
        if (!params.dsps)
            params.dsps=0
        if (!params.dsvs)
            params.dsvs=0
        if (!params.prch)
            params.prch=0
        if (!params.prvl)
            params.prvl=0
        def rendimientos = preciosService.rendimientoTranposrte(params.dsps.toDouble(),params.dsvs.toDouble(),params.prch.toDouble(),params.prvl.toDouble())
        println "rends "+rendimientos
        if (rendimientos["rdps"].toString()=="NaN")
            rendimientos["rdps"]=0
        if (rendimientos["rdvl"].toString()=="NaN")
            rendimientos["rdvl"]=0
        def parametros = ""+params.id+","+params.lugar+",'"+fecha.format("yyyy-MM-dd")+"',"+params.dsps.toDouble()+","+params.dsvs.toDouble()+","+rendimientos["rdps"]+","+rendimientos["rdvl"]
        def res = preciosService.rb_precios(parametros,"")
        def tablaHer='<table class="table table-bordered table-striped table-condensed table-hover"> '
        def tablaMano='<table class="table table-bordered table-striped table-condensed table-hover"> '
        def tablaMat='<table class="table table-bordered table-striped table-condensed table-hover"> '
        def tablaTrans='<table class="table table-bordered table-striped table-condensed table-hover"> '
        def tablaIndi='<table class="table table-bordered table-striped table-condensed table-hover"> '
        def total = 0,totalHer=0,totalMan=0,totalMat=0
        tablaTrans+="<thead><tr><th colspan='7'>Transporte</th></tr><tr><th style='width: 80px;'>Código</th><th style='width:610px'>Descripción</th><th>Pes/Vol</th><th>Cantidad</th><th>Distancia</th><th>Unitario</th><th>C.Total</th></tr></thead><tbody>"

        tablaHer+="<thead><tr><th colspan='7'>Herramienta</th></tr><tr><th style='width: 80px;'>Código</th><th style='width:610px'>Descripción</th><th>Cantidad</th><th>Taria</th><th>Costo</th><th>Rendimiento</th><th>C.Total</th></tr></thead><tbody>"
        tablaMano+="<thead><tr><th colspan='7'>Mano de obra</th></tr><tr><th style='width: 80px;'>Código</th><th style='width:610px'>Descripción</th><th>Cantidad</th><th>Jornal</th><th>Costo</th><th>Rendimiento</th><th>C.Total</th></tr></thead><tbody>"
        tablaMat+="<thead><tr><th colspan='7'>Materiales</th></tr><tr><th style='width: 80px;'>Código</th><th style='width:610px'>Descripción</th><th>Cantidad</th><th>Unitario</th><th></th><th></th><th>C.Total</th></tr></thead><tbody>"
//        println "rends "+rendimientos

//        println "res "+res
               
        res.each {r->
            if(r["grpocdgo"]==3){
                tablaHer+="<tr>"
                tablaHer+="<td style='width: 80px;'>"+r["itemcdgo"]+"</td>"
                tablaHer+="<td>"+r["itemnmbr"]+"</td>"
                tablaHer+="<td style='width: 50px;text-align: right'>"+ g.formatNumber(number:r["rbrocntd"] ,format:"##,#####0", minFractionDigits:"5", maxFractionDigits:"5")+"</td>"
                tablaHer+="<td style='width: 50px;text-align: right'>"+g.formatNumber(number:r["rbrocntd"] ,format:"##,#####0", minFractionDigits:"5", maxFractionDigits:"5")+"</td>"
                tablaHer+="<td style='width: 50px;text-align: right'>"+g.formatNumber(number:r["rbpcpcun"]*r["rbrocntd"] ,format:"##,#####0", minFractionDigits:"5", maxFractionDigits:"5")+"</td>"
                tablaHer+="<td style='width: 50px;text-align: right'>"+g.formatNumber(number:r["rndm"] ,format:"##,#####0", minFractionDigits:"5", maxFractionDigits:"5")+"</td>"
                tablaHer+="<td style='width: 50px;text-align: right'>"+g.formatNumber(number:r["parcial"] ,format:"##,#####0", minFractionDigits:"5", maxFractionDigits:"5")+"</td>"
                totalHer+=r["parcial"]
                tablaHer+="</tr>"
            }
            if(r["grpocdgo"]==2){
                tablaMano+="<tr>"
                tablaMano+="<td style='width: 80px;'>"+r["itemcdgo"]+"</td>"
                tablaMano+="<td>"+r["itemnmbr"]+"</td>"
                tablaMano+="<td style='width: 50px;text-align: right'>"+g.formatNumber(number:r["rbrocntd"] ,format:"##,#####0", minFractionDigits:"5", maxFractionDigits:"5")+"</td>"
                tablaMano+="<td style='width: 50px;text-align: right'>"+g.formatNumber(number:r["rbpcpcun"] ,format:"##,#####0", minFractionDigits:"5", maxFractionDigits:"5")+"</td>"
                tablaMano+="<td style='width: 50px;text-align: right'>"+g.formatNumber(number:r["rbpcpcun"]*r["rbrocntd"] ,format:"##,#####0", minFractionDigits:"5", maxFractionDigits:"5")+"</td>"
                tablaMano+="<td style='width: 50px;text-align: right'>"+g.formatNumber(number:r["rndm"] ,format:"##,#####0", minFractionDigits:"5", maxFractionDigits:"5")+"</td>"
                tablaMano+="<td style='width: 50px;text-align: right'>"+g.formatNumber(number:r["parcial"] ,format:"##,#####0", minFractionDigits:"5", maxFractionDigits:"5")+"</td>"
                totalMan+=r["parcial"]
                tablaMano+="</tr>"
            }
            if(r["grpocdgo"]==1){
                tablaMat+="<tr>"
                tablaMat+="<td style='width: 80px;'>"+r["itemcdgo"]+"</td>"
                tablaMat+="<td>"+r["itemnmbr"]+"</td>"
                tablaMat+="<td style='width: 50px;text-align: right'>"+g.formatNumber(number:r["rbrocntd"] ,format:"##,#####0", minFractionDigits:"5", maxFractionDigits:"5")+"</td>"
                tablaMat+="<td style='width: 50px;text-align: right'>"+g.formatNumber(number:r["rbpcpcun"] ,format:"##,#####0", minFractionDigits:"5", maxFractionDigits:"5")+"</td>"
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
                tablaTrans+="<td style='width: 50px;text-align: right'>"+g.formatNumber(number:r["itempeso"] ,format:"##,#####0", minFractionDigits:"5", maxFractionDigits:"5")+"</td>"
                tablaTrans+="<td style='width: 50px;text-align: right'>"+g.formatNumber(number:r["rbrocntd"] ,format:"##,#####0", minFractionDigits:"5", maxFractionDigits:"5")+"</td>"
                tablaTrans+="<td style='width: 50px;text-align: right'>"+g.formatNumber(number:r["distancia"] ,format:"##,#####0", minFractionDigits:"5", maxFractionDigits:"5")+"</td>"
                tablaTrans+="<td style='width: 50px;text-align: right'>"+g.formatNumber(number:r["parcial_t"]/(r["itempeso"]*r["rbrocntd"]*r["distancia"]) ,format:"##,#####0", minFractionDigits:"5", maxFractionDigits:"5")+"</td>"
                tablaTrans+="<td style='width: 50px;text-align: right'>"+g.formatNumber(number:r["parcial_t"] ,format:"##,#####0", minFractionDigits:"5", maxFractionDigits:"5")+"</td>"
                total+=r["parcial_t"]
                tablaTrans+="</tr>"
            }

        }
        tablaTrans+="<tr><td><b>SUBTOTAL</b></td><td></td><td></td><td></td><td></td><td></td><td style='width: 50px;text-align: right'>${g.formatNumber(number:total ,format:"##,#####0", minFractionDigits:"5", maxFractionDigits:"5")}</td></tr>"
        tablaTrans+="</tbody></table>"

        tablaHer+="<tr><td><b>SUBTOTAL</b></td><td></td><td></td><td></td><td></td><td></td><td style='width: 50px;text-align: right'>${g.formatNumber(number:totalHer,format:"##,#####0", minFractionDigits:"5", maxFractionDigits:"5")}</td></tr>"
        tablaHer+="</tbody></table>"
        tablaMano+="<tr><td><b>SUBTOTAL</b></td><td></td><td></td><td></td><td></td><td></td><td style='width: 50px;text-align: right'>${g.formatNumber(number:totalMan ,format:"##,#####0", minFractionDigits:"5", maxFractionDigits:"5")}</td></tr>"
        tablaMano+="</tbody></table>"
        tablaMat+="<tr><td><b>SUBTOTAL</b></td><td></td><td></td><td></td><td></td><td></td><td style='width: 50px;text-align: right'>${g.formatNumber(number:totalMat ,format:"##,#####0", minFractionDigits:"5", maxFractionDigits:"5")}</td></tr>"
        tablaMat+="</tbody></table>"
        def totalRubro=total+totalHer+totalMan+totalMat
        def totalIndi=totalRubro*0.32
        tablaIndi+="<thead><tr><th colspan='3'>Costos indirectos</th></tr><tr><th style='width:550px'>Descripción</th><th>Porcentaje</th><th>Valor</th></tr></thead>"
        tablaIndi+="<tbody><tr><td>Costos indirectos</td><td style='text-alagin:right'>32%</td><td style='text-alagin:right'>${g.formatNumber(number:totalIndi ,format:"##,#####0", minFractionDigits:"5", maxFractionDigits:"5")}</td></tr></tbody>"
        tablaIndi+="</table>"

        /*todo descomentar esto una vez que sirva el proceso */
        if (total==0)
            tablaTrans=""
        if(totalHer==0)
            tablaHer=""
        if(totalMan==0)
            tablaMano=""
        if(totalMat==0)
            tablaMat=""
        [rubro:rubro,fechaPrecios:fecha,tablaTrans:tablaTrans,tablaHer:tablaHer,tablaMano:tablaMano,tablaMat:tablaMat,tablaIndi:tablaIndi,totalRubro:totalRubro,totalIndi:totalIndi]



    }
}
