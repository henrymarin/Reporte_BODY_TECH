SELECT *
FROM conversacion;
 
SELECT *
FROM conversaciones_por_tipo;

SELECT *
FROM estados_por_agente;

-- CONSULTA PRIMERA OCURRENCIA COLA - COLUMNA C
SELECT  fecha_inicio_estado
FROM 	estados_por_agente
WHERE 	id_agente = '88d35683-591c-4245-9e1c-d2dbe7d1808f' 
AND		estado = 'ON_QUEUE'
AND 	DATE(fecha_inicio_estado) = '2018-03-01'
ORDER BY fecha_inicio_estado
LIMIT 1;
				
-- CONSULTA NUMERO INTERACCIONES VOZ - COLUMNA D
SELECT count(*)
FROM conversaciones_por_tipo
WHERE id_conversacion = '4bb0c48c-a9b8-4fb0-82e4-78249aff05f9'
AND tipo = 'voice';

-- COLUMNA G - TIEMPO INTERVALO VOZ
SELECT ct.*, TIMESTAMPDIFF(SECOND, ct.fecha_inicio_segmento, ct.fecha_fin_segmento) AS DIFERENCE
FROM conversaciones_por_tipo ct
WHERE id_conversacion = '4bb0c48c-a9b8-4fb0-82e4-78249aff05f9'
AND id_agente = 'e21fc9de-f3d6-4971-ae5c-d3f09d0a70ce'
AND tipo = 'voice';

SELECT SUM(TIMESTAMPDIFF(SECOND, ct.fecha_inicio_segmento, ct.fecha_fin_segmento)) AS TIEMPOVOZ
FROM conversaciones_por_tipo ct
WHERE id_conversacion = '4bb0c48c-a9b8-4fb0-82e4-78249aff05f9'
AND id_agente = 'e21fc9de-f3d6-4971-ae5c-d3f09d0a70ce'
AND tipo = 'voice';

-- TIEMPO EN PAUSA (segundos) - COLUMNA J
SELECT SUM(TIMESTAMPDIFF(SECOND, ct.fecha_inicio_segmento, ct.fecha_fin_segmento)) AS TIEMPOPAUSA
FROM conversaciones_por_tipo ct
WHERE id_conversacion = '4bb0c48c-a9b8-4fb0-82e4-78249aff05f9'
AND id_agente = 'e21fc9de-f3d6-4971-ae5c-d3f09d0a70ce'
AND segmento = 'hold';

-- TIEMPO LUNCH (segundos) - COLUMNA K
SELECT  TIMESTAMPDIFF(SECOND, fecha_inicio_estado, fecha_fin_estado) AS TIEMPOLUNCH
FROM 	estados_por_agente
WHERE 	id_agente = '88d35683-591c-4245-9e1c-d2dbe7d1808f' 
AND		estado = 'meal'
AND 	DATE(fecha_inicio_estado) = '2018-03-01';

-- TIEMPO BREAK (segundos) - COLUMNA L
SELECT  TIMESTAMPDIFF(SECOND, fecha_inicio_estado, fecha_fin_estado) AS TIEMPOBREAK
FROM 	estados_por_agente
WHERE 	id_agente = '88d35683-591c-4245-9e1c-d2dbe7d1808f' 
AND		estado = 'break'
AND 	DATE(fecha_inicio_estado) = '2018-03-01';

-- COLUMNA M - TIEMPO PROMEDIO VOZ
SELECT  c.fecha_inicio_conversacion, c.fecha_fin_conversacion, AVG(TIMESTAMPDIFF(SECOND, c.fecha_inicio_conversacion, c.fecha_fin_conversacion)) AS PROMEDIOVOZ
FROM 	conversacion c,
		conversaciones_por_tipo ct
WHERE 	c.id_conversacion = ct.id_conversacion
AND 	DATE(c.fecha_inicio_conversacion) = '2018-02-28'
AND		c.id_agente = '8ab09adb-56ec-43ef-ba9f-811d9cc5b6d6'
AND 	ct.tipo = 'voice';


