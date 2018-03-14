-- Reporte
SELECT	 DATE(c.fecha_inicio_conversacion) AS FECHA,	
		 c.id_agente AS ID_AGENTE,
		 c.nombre_agente AS NOMBRE_AGENTE,
         (SELECT fecha_inicio_estado FROM estados_por_agente WHERE UPPER(estado) = 'ON_QUEUE' AND DATE(fecha_inicio_estado) = FECHA AND id_agente = c.id_agente ORDER BY fecha_inicio_estado LIMIT 1) AS HORA_INGRESO_COLA,
         (SELECT COUNT(*) FROM conversaciones_por_tipo ct WHERE DATE(ct.fecha_inicio_segmento) = FECHA AND ct.id_agente = c.id_agente AND UPPER(ct.tipo) = 'VOICE') AS NUM_INTERACCIONES_VOZ,
         (SELECT COUNT(*) FROM conversaciones_por_tipo ct WHERE DATE(ct.fecha_inicio_segmento) = FECHA AND ct.id_agente = c.id_agente AND UPPER(ct.tipo) = 'CHAT') AS NUM_INTERACCIONES_CHAT,
         (SELECT COUNT(*) FROM conversaciones_por_tipo ct WHERE DATE(ct.fecha_inicio_segmento) = FECHA AND ct.id_agente = c.id_agente AND UPPER(ct.tipo) = 'MAIL') AS NUM_INTERACCIONES_MAIL,
		 (SELECT SUM(TIMESTAMPDIFF(SECOND, fecha_inicio_segmento, fecha_fin_segmento)) FROM conversaciones_por_tipo WHERE DATE(fecha_inicio_segmento) = FECHA AND id_agente = c.id_agente AND UPPER(tipo) = 'VOICE') AS TIEMPO_INTERVALO_VOZ,
         (SELECT SUM(TIMESTAMPDIFF(SECOND, fecha_inicio_segmento, fecha_fin_segmento)) FROM conversaciones_por_tipo WHERE DATE(fecha_inicio_segmento) = FECHA AND id_agente = c.id_agente AND UPPER(tipo) = 'CHAT') AS TIEMPO_INTERVALO_CHAT,
         (SELECT SUM(TIMESTAMPDIFF(SECOND, fecha_inicio_segmento, fecha_fin_segmento)) FROM conversaciones_por_tipo WHERE DATE(fecha_inicio_segmento) = FECHA AND id_agente = c.id_agente AND UPPER(tipo) = 'MAIL') AS TIEMPO_INTERVALO_MAIL,
         (SELECT SUM(TIMESTAMPDIFF(SECOND, fecha_inicio_segmento, fecha_fin_segmento)) FROM conversaciones_por_tipo WHERE DATE(fecha_inicio_segmento) = FECHA AND id_agente = c.id_agente AND UPPER(segmento) = 'HOLD') AS SUM_HOLD,
         (SELECT SUM(TIMESTAMPDIFF(SECOND, fecha_inicio_estado, fecha_fin_estado)) FROM estados_por_agente WHERE DATE(fecha_inicio_estado) = FECHA AND id_agente = c.id_agente AND UPPER(estado) = 'MEAL') AS SUM_MEAL,
         (SELECT SUM(TIMESTAMPDIFF(SECOND, fecha_inicio_estado, fecha_fin_estado)) FROM estados_por_agente WHERE DATE(fecha_inicio_estado) = FECHA AND id_agente = c.id_agente AND UPPER(estado) = 'BREAK') AS SUM_BREAK,         
         null AS TIEMPO_PROMEDIO_VOZ,
         null AS TIEMPO_PROMEDIO_CHAT,
         null AS TIEMPO_PROMEDIO_MAIL,
         (SELECT TIME_FORMAT(fecha_inicio_estado, '%H : %m : %s') FROM estados_por_agente WHERE DATE(fecha_inicio_estado) = FECHA AND id_agente = c.id_agente AND UPPER(estado) = 'OFFLINE' ORDER BY fecha_inicio_estado desc LIMIT 1) AS HORA_LOGOUT,
         (SELECT (SELECT SUM(TIMESTAMPDIFF(SECOND, fecha_inicio_segmento, fecha_fin_segmento)) FROM conversaciones_por_tipo WHERE DATE(fecha_inicio_segmento) = FECHA AND id_agente = c.id_agente AND UPPER(tipo) IN ('VOICE', 'CHAT', 'MAIL')) + (SELECT IFNULL(SUM(TIMESTAMPDIFF(SECOND, fecha_inicio_estado, fecha_fin_estado)),0) FROM estados_por_agente WHERE DATE(fecha_inicio_estado) = FECHA AND id_agente = c.id_agente AND UPPER(estado) = 'IDLE')) AS TIEMPO_PRODUCTIVO_AGENTE,         
         null AS PORCENTAJE_PRODUCTIVIDAD
FROM     conversacion c
WHERE	 (DATE(c.fecha_inicio_conversacion) BETWEEN '2018-02-09' AND '2018-02-12')
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
SELECT *
FROM estados_por_agente
WHERE DATE(fecha_inicio_estado) = '2018-02-10'
AND id_agente = 'e92b1031-b9a8-436b-9a97-71d861f68e56'
AND UPPER(estado) = 'OFFLINE'
ORDER BY fecha_inicio_estado desc
;

-- Tiempo improductivo por dia por agente
SELECT SUM(TIMESTAMPDIFF(SECOND, fecha_inicio_estado, fecha_fin_estado)) AS TIEMPO_NO_PRODUCTIVO
FROM estados_por_agente
WHERE DATE(fecha_inicio_estado) = '2018-02-10'
AND id_agente = 'e92b1031-b9a8-436b-9a97-71d861f68e56'
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