Para desplegar el tiempo :

  \time

----------------------------- cambios en rubros ------------------------------
-- el rendimiento es 0.03 para herramienta menores, cntd = 1
update rbro set rbrocntd = 1 where item__id in 
  (select item__id from item where itemcdgo like '103.001.00%');

select item__id, itemcdgo, itemnmbr from item where itemcdgo like '103.001.00%';
 item__id |  itemcdgo   |      itemnmbr       
----------+-------------+---------------------
     2868 | 103.001.003 | HERRAMIENTA MENOR 2
     2869 | 103.001.002 | HERRAMIENTA MENOR 5
     2870 | 103.001.001 | HERRAMIENTA MENOR 3
(3 rows)

janus2=# update rbro set rbrorndt = 0.03 where item__id = 2870;
UPDATE 1582
janus2=# update rbro set rbrorndt = 0.05 where item__id = 2869;
UPDATE 276
janus2=# update rbro set rbrorndt = 0.02 where item__id = 2868;
UPDATE 13

---------------- problemas: ...................
 El suma de MO no puede ir como pcun porque debe haber un solo precio para 
 cada item en la obra. Entonces figuraría un precio distintopara cada rubro.

 Se debe mandar la suma(MO) a cantidad, manejar rendimiento de 1 y la 
 tarifa de 0.03 o 0.02 según sea el caso. Cambiar procedimientos:
   rb_precios, ac_rbro_hr, ac_rbro_hr1.

-----------------...................-------------------............---------


sp:

Procedimientos a implementar:
  (x) precios
  (*) rbpc_fcha (tp_pcun)
  (*) item_pcun (tp_rbpc) usado en --> RGST_OBRA, PCUN_ITEM, TRANSPORTE.
  (x) rbro_item == select rbrocdgo, item__id, rbrocntd from rbro 
                   where rbrocdgo = :rbro;
  (*) rb_precios (tp_apus) usado en --> AC_RBRO_HR, AC_RBRO_HR1, AC_RBRO_HR_RPT, 
                                    --> PCUN_RBRO, RBRO_PCUN, RB_PCOBRA, RGST_OBRA, 
                                    --> VERIFICA_PRECIOS
  (*) ac_rbro-hr1 (void) se ejecuta con: select * from ac_rbro_hr1(293, 4, '1-feb-2008'); 


---------------------------- cálculo del transporte ----------------------------
  Distancia al peso:    dato     --> dsps
  Distancia al volumen: datos    --> dtvl
  Factor de rendimiento al peso: --> rdps
  Factor de rendimiento volumen: --> rdps
  
  Factor de reducción(10):       --> ftrd
  Velocidad(40):                 --> vlcd
  Capacidad del volquete(8):     --> cpvl
  Factor Volumen (0.8):          --> ftvl
  Reducción / Tiempo (24):       --> rdtp
  Factor de Peso (1.7):          --> ftps

  Precio unitario de chofer:   ---> pcunchfr
  Precio unitario de volqueta: ---> pcunvlqt

  rdvl = (pcuncfr + pcunvlqt) / ((ftrd * vlcd * cpvl * ftvl * dsvl) / (vlcd + (rdtp * dsvl));
  rdps = (pcuncfr + pcunvlqt) / ((ftrd * vlcd * cpvl * ftvl * dsps * ftps) / (vlcd + (rdtp * dsps));

  o mejor:

  rdvl = (ftrd * vlcd * cpvl * ftvl * dsvl) / (vlcd + (rdtp * dsvl));
  rdvl = (pcuncfr + pcunvlqt) / rdvl;

  rdps = (ftrd * vlcd * cpvl * ftvl * dsps * ftps) / (vlcd + (rdtp * dsps));
  rdps = (pcuncfr + pcunvlqt) / rdps;

--------------------------------------------------------------------------------

Probar con: 
  fb: select * from rb_precios ('C-3007', 4, '1-feb-2008', 50, 70, 0.1015477897561282, 0.1710401760227313) order by grpocdgo desc; 
  datos de transporte: select * from transporte('212USCA07');  // se pasa la obracdgo

  pg: select * from rb_precios(293, 4, '1-feb-2008', 50, 70, 0.1015477897561282, 0.1710401760227313);

      select * from ac_rbro_hr1(293, 4, '1-feb-2008');

----------- usando setof:
DROP FUNCTION rbpc_fcha(fcha date, lgar integer);
drop type tp_pcun;

create type tp_pcun as (item__id int, lgar int, fcha date, pcun numeric(14,3),
   grpo__id int);

CREATE OR REPLACE FUNCTION rbpc_fcha(p_fcha date, p_lgar integer)
  RETURNS 
    setof tp_pcun AS
$BODY$

/* ************************   Estructura y Algoritmo  *********************** */
/* Muestra solamente los últimos precios de cada item                         */
declare r_d record;
declare val tp_pcun%rowtype;

begin
  for r_d in select max(rbpcfcha) as fcha, item__id
      from   rbpc
      where rbpcfcha <= p_fcha and lgar__id = p_lgar
      group by item__id
      order by item__id
  loop 
    select rbpcpcun, sbgr.grpo__id into val.pcun, val.grpo__id
    from rbpc, item, dprt, sbgr
    where item.item__id = rbpc.item__id and
          dprt.dprt__id = item.dprt__id and
          sbgr.sbgr__id = dprt.sbgr__id and
          rbpc.item__id = r_d.item__id and rbpcfcha = r_d.fcha and
          lgar__id = p_lgar;
    val.item__id = r_d.item__id;
    val.lgar = p_lgar;
    val.fcha = r_d.fcha;
    return next val;
  end loop;
end
$BODY$
LANGUAGE plpgsql;


------------ Usando table:

DROP FUNCTION rbpc_fcha(fcha date, lgar integer);

CREATE OR REPLACE FUNCTION rbpc_fcha(p_fcha date, p_lgar integer)
  RETURNS 
    table (v_item__id int, lgar int, fcha date, pcun numeric(14,3),
   grpo__id int) AS
$BODY$

/* ************************   Estructura y Algoritmo  *********************** */
/* Muestra solamente los últimos precios de cada item                         */
declare r_d record;

begin
  for r_d in select max(rbpcfcha) as fcha, item__id
      from   rbpc
      where rbpcfcha <= p_fcha and lgar__id = p_lgar
      group by item__id
      order by item__id
  loop 
    select rbpcpcun, sbgr.grpo__id into pcun, grpo__id
    from rbpc, item, dprt, sbgr
    where item.item__id = rbpc.item__id and
          dprt.dprt__id = item.dprt__id and
          sbgr.sbgr__id = dprt.sbgr__id and
          rbpc.item__id = r_d.item__id and rbpcfcha = r_d.fcha and
          lgar__id = p_lgar;
    v_item__id = r_d.item__id;
    lgar = p_lgar;
    fcha = r_d.fcha;
    return next;
  end loop;
end
$BODY$
LANGUAGE plpgsql;



***************************** Indices *********************************

create unique index rbpciu01 on rbpc(item__id, rbpcfcha, lgar__id);



****************************** tipos de datos ************************
los tipso de datos creados con "create type" se los puede ver con \d tipo
por estándar se debe usar tp_tipo para evitar confusiones.

* tp_pcun    --->  función:  rbpc_fcha.




insert into paux( trnpftrd, trnpvlcd, trnpcpvl, trnpftvl, trnprdtp,
 trnpftps, indignrl, indiimpr, indiutil, indicntr,
 inditotl, indidrob, indimntn, indiadmn,
 indtgrnt, indicsfn, indivhcl, indiprmo, inditmbr, paux__id)
values (10, 40, 8, 0.8, 24, 
  1.7, 7, 2.5, 9, 4,
  21, 3.26, 0.16, 1.10, 
  1.81, 0.43, 0.09, 0.05, 2.5, 1);

***************************** Notas al pie *********************************
alter table nota add notatipo varchar(15);


