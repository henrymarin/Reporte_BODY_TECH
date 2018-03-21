-- Reporte
SELECT 	id,
		DATE(c.fecha_inicio_conversacion) AS fecha,
		c.nombre_agente AS nombreAgente,
        (SELECT TIME_FORMAT(fecha_inicio_estado, '%H : %i : %s') FROM estados_por_agente WHERE UPPER(estado) = 'ON_QUEUE' AND FECHA = DATE(fecha_inicio_estado) AND c.id_agente = id_agente ORDER BY fecha_inicio_estado LIMIT 1) AS horaIngresoCola,
        (SELECT COUNT(*) FROM conversaciones_por_tipo ct WHERE FECHA = DATE(ct.fecha_inicio_segmento) AND c.id_agente = ct.id_agente AND UPPER(ct.tipo) = 'VOICE') AS numeroInteraccionesVoz,
        (SELECT COUNT(*) FROM conversaciones_por_tipo ct WHERE FECHA = DATE(ct.fecha_inicio_segmento) AND c.id_agente = ct.id_agente AND UPPER(ct.tipo) = 'CHAT') AS numeroInteraccionesChat,
        (SELECT COUNT(*) FROM conversaciones_por_tipo ct WHERE FECHA = DATE(ct.fecha_inicio_segmento) AND c.id_agente = ct.id_agente AND UPPER(ct.tipo) = 'MAIL') AS numeroInteraccionesEmail,
        (SELECT SUM(TIMESTAMPDIFF(SECOND, fecha_inicio_segmento, fecha_fin_segmento)) FROM conversaciones_por_tipo WHERE FECHA = DATE(fecha_inicio_segmento) AND c.id_agente = id_agente AND UPPER(tipo) = 'VOICE') AS tiempoIntervaloVoz,
        (SELECT SUM(TIMESTAMPDIFF(SECOND, fecha_inicio_segmento, fecha_fin_segmento)) FROM conversaciones_por_tipo WHERE FECHA = DATE(fecha_inicio_segmento) AND c.id_agente = id_agente AND UPPER(tipo) = 'CHAT') AS tiempoIntervaloChat,
        (SELECT SUM(TIMESTAMPDIFF(SECOND, fecha_inicio_segmento, fecha_fin_segmento)) FROM conversaciones_por_tipo WHERE FECHA = DATE(fecha_inicio_segmento) AND c.id_agente = id_agente AND UPPER(tipo) = 'MAIL') AS tiempoIntervaloEmail,
        (SELECT SUM(TIMESTAMPDIFF(SECOND, fecha_inicio_segmento, fecha_fin_segmento)) FROM conversaciones_por_tipo WHERE FECHA = DATE(fecha_inicio_segmento) AND c.id_agente = id_agente AND UPPER(segmento) = 'HOLD') AS tiempoPausa,
        (SELECT SUM(TIMESTAMPDIFF(SECOND, fecha_inicio_estado, fecha_fin_estado)) FROM estados_por_agente WHERE FECHA = DATE(fecha_inicio_estado) AND c.id_agente = id_agente AND UPPER(estado) = 'MEAL') AS tiempoAlmuerzo,
        (SELECT SUM(TIMESTAMPDIFF(SECOND, fecha_inicio_estado, fecha_fin_estado)) FROM estados_por_agente WHERE FECHA = DATE(fecha_inicio_estado) AND c.id_agente = id_agente AND UPPER(estado) = 'BREAK') AS tiempoBreak,
        null AS tiempoPromedioVoz,
        null AS tiempoPromedioChat,
        null AS tiempoPromedioEmail,
        (SELECT TIME_FORMAT(fecha_inicio_estado, '%H : %i : %s') FROM estados_por_agente WHERE FECHA = DATE(fecha_inicio_estado) AND c.id_agente = id_agente AND UPPER(estado) = 'OFFLINE' ORDER BY fecha_inicio_estado desc LIMIT 1) AS horaCierreSesion,
        (SELECT (SELECT SUM(TIMESTAMPDIFF(SECOND, fecha_inicio_segmento, fecha_fin_segmento)) FROM conversaciones_por_tipo WHERE FECHA = DATE(fecha_inicio_segmento) AND c.id_agente = id_agente AND UPPER(tipo) IN ('VOICE', 'CHAT', 'MAIL')) + (SELECT IFNULL(SUM(TIMESTAMPDIFF(SECOND, fecha_inicio_estado, fecha_fin_estado)),0) FROM estados_por_agente WHERE FECHA = DATE(fecha_inicio_estado) AND c.id_agente = id_agente AND UPPER(estado) = 'IDLE')) AS tiempoProductivoAgente,
        null AS porcentajeProductividadAgente 		
FROM conversacion c  
WHERE (DATE(c.fecha_inicio_conversacion) BETWEEN '2018-03-15' AND '2018-03-15') 
AND id_agente IN ('c4a291b1-153d-4150-a4e3-8dad40c48f42','8ab09adb-56ec-43ef-ba9f-811d9cc5b6d6','8c977b15-994a-4895-9671-53d4e455ecb7','b2caca88-2b32-49ab-98f6-90cdfbb048ab') 
GROUP BY fecha, nombreAgente 
ORDER BY fecha;

-- Reporte conteo
SELECT COUNT(*)
FROM (
SELECT 	DATE(c.fecha_inicio_conversacion) AS fecha,
		c.nombre_agente AS nombreAgente,
        (SELECT TIME_FORMAT(fecha_inicio_estado, '%H : %i : %s') FROM estados_por_agente WHERE UPPER(estado) = 'ON_QUEUE' AND FECHA = DATE(fecha_inicio_estado) AND c.id_agente = id_agente ORDER BY fecha_inicio_estado LIMIT 1) AS horaIngresoCola,
        (SELECT COUNT(*) FROM conversaciones_por_tipo ct WHERE FECHA = DATE(ct.fecha_inicio_segmento) AND c.id_agente = ct.id_agente AND UPPER(ct.tipo) = 'VOICE') AS numeroInteraccionesVoz,
        (SELECT COUNT(*) FROM conversaciones_por_tipo ct WHERE FECHA = DATE(ct.fecha_inicio_segmento) AND c.id_agente = ct.id_agente AND UPPER(ct.tipo) = 'CHAT') AS numeroInteraccionesChat,
        (SELECT COUNT(*) FROM conversaciones_por_tipo ct WHERE FECHA = DATE(ct.fecha_inicio_segmento) AND c.id_agente = ct.id_agente AND UPPER(ct.tipo) = 'MAIL') AS numeroInteraccionesEmail,
        (SELECT SUM(TIMESTAMPDIFF(SECOND, fecha_inicio_segmento, fecha_fin_segmento)) FROM conversaciones_por_tipo WHERE FECHA = DATE(fecha_inicio_segmento) AND c.id_agente = id_agente AND UPPER(tipo) = 'VOICE') AS tiempoIntervaloVoz,
        (SELECT SUM(TIMESTAMPDIFF(SECOND, fecha_inicio_segmento, fecha_fin_segmento)) FROM conversaciones_por_tipo WHERE FECHA = DATE(fecha_inicio_segmento) AND c.id_agente = id_agente AND UPPER(tipo) = 'CHAT') AS tiempoIntervaloChat,
        (SELECT SUM(TIMESTAMPDIFF(SECOND, fecha_inicio_segmento, fecha_fin_segmento)) FROM conversaciones_por_tipo WHERE FECHA = DATE(fecha_inicio_segmento) AND c.id_agente = id_agente AND UPPER(tipo) = 'MAIL') AS tiempoIntervaloEmail,
        (SELECT SUM(TIMESTAMPDIFF(SECOND, fecha_inicio_segmento, fecha_fin_segmento)) FROM conversaciones_por_tipo WHERE FECHA = DATE(fecha_inicio_segmento) AND c.id_agente = id_agente AND UPPER(segmento) = 'HOLD') AS tiempoPausa,
        (SELECT SUM(TIMESTAMPDIFF(SECOND, fecha_inicio_estado, fecha_fin_estado)) FROM estados_por_agente WHERE FECHA = DATE(fecha_inicio_estado) AND c.id_agente = id_agente AND UPPER(estado) = 'MEAL') AS tiempoAlmuerzo,
        (SELECT SUM(TIMESTAMPDIFF(SECOND, fecha_inicio_estado, fecha_fin_estado)) FROM estados_por_agente WHERE FECHA = DATE(fecha_inicio_estado) AND c.id_agente = id_agente AND UPPER(estado) = 'BREAK') AS tiempoBreak,
        null AS tiempoPromedioVoz,
        null AS tiempoPromedioChat,
        null AS tiempoPromedioEmail,
        (SELECT TIME_FORMAT(fecha_inicio_estado, '%H : %i : %s') FROM estados_por_agente WHERE FECHA = DATE(fecha_inicio_estado) AND c.id_agente = id_agente AND UPPER(estado) = 'OFFLINE' ORDER BY fecha_inicio_estado desc LIMIT 1) AS horaCierreSesion,
        (SELECT (SELECT SUM(TIMESTAMPDIFF(SECOND, fecha_inicio_segmento, fecha_fin_segmento)) FROM conversaciones_por_tipo WHERE FECHA = DATE(fecha_inicio_segmento) AND c.id_agente = id_agente AND UPPER(tipo) IN ('VOICE', 'CHAT', 'MAIL')) + (SELECT IFNULL(SUM(TIMESTAMPDIFF(SECOND, fecha_inicio_estado, fecha_fin_estado)),0) FROM estados_por_agente WHERE FECHA = DATE(fecha_inicio_estado) AND c.id_agente = id_agente AND UPPER(estado) = 'IDLE')) AS tiempoProductivoAgente,
        null AS porcentajeProductividadAgente 		
FROM conversacion c  
WHERE (DATE(c.fecha_inicio_conversacion) BETWEEN '2018-03-15' AND '2018-03-15') 
AND id_agente IN ('c4a291b1-153d-4150-a4e3-8dad40c48f42','8ab09adb-56ec-43ef-ba9f-811d9cc5b6d6','8c977b15-994a-4895-9671-53d4e455ecb7','b2caca88-2b32-49ab-98f6-90cdfbb048ab') 
GROUP BY fecha, nombreAgente 
ORDER BY fecha) AS conteo;

SELECT	 DATE(c.fecha_inicio_conversacion) AS FECHA,			 
		 c.nombre_agente AS NOMBRE_AGENTE,
		 count(*)
FROM     conversacion c
WHERE	 (DATE(c.fecha_inicio_conversacion) BETWEEN '2018-03-12' AND '2018-03-12')
AND		 id_agente IN ('e21fc9de-f3d6-4971-ae5c-d3f09d0a70ce','64ad8c51-66ab-4d88-ac48-77dcff93d87d')
GROUP BY FECHA, NOMBRE_AGENTE;

-- Numero interacciones voz | chat | mail
select	date(fecha_inicio_segmento) as fecha, 			
        id_agente AS agente,
		count(*)
from 	conversaciones_por_tipo
where	tipo = 'voice' -- voice | chat | mail
group by fecha, id_agente
order by fecha;

-- Hora ingreso cola por dia para un agente
SELECT fecha_inicio_estado
FROM estados_por_agente
WHERE estado = 'ON_QUEUE'
AND DATE(fecha_inicio_estado) = '2018-02-10'
AND id_agente = '345c6e29-28c0-4960-8cb1-6c4501918b14' -- Luis
ORDER BY fecha_inicio_estado
LIMIT 1; -- 2018-02-10 14:46:41

-- TIEMPO INTERVALO VOZ
SELECT ct.*, TIMESTAMPDIFF(SECOND, ct.fecha_inicio_segmento, ct.fecha_fin_segmento) AS DIFERENCE
FROM conversaciones_por_tipo ct
WHERE id_conversacion = '00ca421c-ff26-438f-98f2-049b4ac5f888'
-- AND id_agente = '88d35683-591c-4245-9e1c-d2dbe7d1808f'
AND tipo = 'voice';

-- Tiempo voz por agente y por dia
select SUM(TIMESTAMPDIFF(SECOND, fecha_inicio_segmento, fecha_fin_segmento)) AS SUM_VOICE
from conversaciones_por_tipo
where DATE(fecha_inicio_segmento) = '2018-02-10'
AND id_agente = '345c6e29-28c0-4960-8cb1-6c4501918b14'
and tipo = 'voice';

-- Tiempo HOLD por dia por agente
SELECT SUM(TIMESTAMPDIFF(SECOND, fecha_inicio_segmento, fecha_fin_segmento)) AS SUM_HOLD
FROM conversaciones_por_tipo
WHERE DATE(fecha_inicio_segmento) = '2018-02-10'
AND id_agente = '345c6e29-28c0-4960-8cb1-6c4501918b14'
AND segmento = 'hold';

-- Tiempo MEAL por dia por agente
SELECT SUM(TIMESTAMPDIFF(SECOND, fecha_inicio_estado, fecha_fin_estado)) AS SUM_MEAL
FROM estados_por_agente
WHERE DATE(fecha_inicio_estado) = '2018-02-10'
AND id_agente = '345c6e29-28c0-4960-8cb1-6c4501918b14'
AND UPPER(estado) = 'MEAL'
;

-- Hora deslogeo por dia por agente
SELECT *, TIME_FORMAT(fecha_inicio_estado, '%H : %i : %s')
FROM estados_por_agente
WHERE DATE(fecha_inicio_estado) = '2018-03-15'
AND id_agente = 'a29851e1-a466-45e8-a7c1-dba718a23bcf'
AND UPPER(estado) = 'OFFLINE'
ORDER BY fecha_inicio_estado desc
limit 1
;

-- Tiempo improductivo por dia por agente
SELECT SUM(TIMESTAMPDIFF(SECOND, fecha_inicio_estado, fecha_fin_estado)) AS TIEMPO_NO_PRODUCTIVO
FROM estados_por_agente
WHERE DATE(fecha_inicio_estado) = '2018-03-15'
AND id_agente = 'c417dd32-58fd-4c5f-a5ed-86d311d66000'
AND UPPER(estado) = 'IDLE'
;


-- Tiempo productivo agente por dia
SELECT 
(SELECT SUM(TIMESTAMPDIFF(SECOND, fecha_inicio_segmento, fecha_fin_segmento)) AS SUM_VOICE
FROM conversaciones_por_tipo
WHERE DATE(fecha_inicio_segmento) = '2018-02-10'
AND id_agente = '345c6e29-28c0-4960-8cb1-6c4501918b14'
AND tipo IN ('voice', 'chat', 'mail')) 
+ 
(SELECT IFNULL(SUM(TIMESTAMPDIFF(SECOND, fecha_inicio_estado, fecha_fin_estado)),0) AS TIEMPO_NO_PRODUCTIVO
FROM estados_por_agente
WHERE DATE(fecha_inicio_estado) = '2018-02-10'
AND id_agente = '345c6e29-28c0-4960-8cb1-6c4501918b14'
AND UPPER(estado) = 'IDLE')
AS TIEMPO_PRODUCTIVO_AGENTE;