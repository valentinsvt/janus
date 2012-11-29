package janus

class VolumenObraController extends janus.seguridad.Shield{

    def volObra(){

        def obra = Obra.get(1)
        def volumenes = VolumenesObra.findAllByObra(obra)
        def subPres = volumenes.subPresupuesto


        [obra:obra,volumenes:volumenes,subPres:subPres]



    }
}
