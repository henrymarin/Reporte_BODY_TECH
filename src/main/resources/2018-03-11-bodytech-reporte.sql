
SELECT	c.id_conversacion  AS IDCONVERSACION,
		c.fecha_inicio_conversacion AS INICIO,
        c.fecha_fin_conversacion AS FIN,
		ct.tipo AS TIPO, -- Columna A
        c.nombre_agente AS NOMBREAGENTE, -- Columna B
        (SELECT fecha_inicio_estado FROM estados_por_agente WHERE id_agente = c.id_agente AND estado = 'ON_QUEUE' AND DATE(fecha_inicio_estado) = DATE(c.fecha_inicio_conversacion) ORDER BY fecha_inicio_estado LIMIT 1) AS INGRESOCOLA, -- Columna C
        (SELECT count(*) FROM conversaciones_por_tipo WHERE id_conversacion = c.id_conversacion AND tipo = 'voice') AS NUMINTERACIONESVOZ, -- Columna D
        (SELECT count(*) FROM conversaciones_por_tipo WHERE id_conversacion = c.id_conversacion AND tipo = 'chat') AS NUMINTERACIONESCHAT, -- Columna E
        (SELECT count(*) FROM conversaciones_por_tipo WHERE id_conversacion = c.id_conversacion AND tipo = 'mail') AS NUMINTERACIONESEMAIL, -- Columna F
        (SELECT SUM(TIMESTAMPDIFF(SECOND, ct.fecha_inicio_segmento, ct.fecha_fin_segmento)) FROM conversaciones_por_tipo ct WHERE id_conversacion = c.id_conversacion AND id_agente = c.id_agente AND tipo = 'voice') AS TIEMPOVOZ, -- Columna G
        (SELECT SUM(TIMESTAMPDIFF(SECOND, ct.fecha_inicio_segmento, ct.fecha_fin_segmento)) FROM conversaciones_por_tipo ct WHERE id_conversacion = c.id_conversacion AND id_agente = c.id_agente AND tipo = 'chat') AS TIEMPOCHAT, -- Columna H
        (SELECT SUM(TIMESTAMPDIFF(SECOND, ct.fecha_inicio_segmento, ct.fecha_fin_segmento)) FROM conversaciones_por_tipo ct WHERE id_conversacion = c.id_conversacion AND id_agente = c.id_agente AND tipo = 'mail') AS TIEMPOMAIL, -- Columna I
        (SELECT SUM(TIMESTAMPDIFF(SECOND, ct.fecha_inicio_segmento, ct.fecha_fin_segmento)) FROM conversaciones_por_tipo ct WHERE id_conversacion = c.id_conversacion AND id_agente = c.id_agente AND segmento = 'hold') AS TIEMPOPAUSA,  -- Columna J
        (SELECT TIMESTAMPDIFF(SECOND, fecha_inicio_estado, fecha_fin_estado) FROM estados_por_agente WHERE id_agente = c.id_agente AND estado = 'meal' AND DATE(fecha_inicio_estado) = DATE(c.fecha_inicio_conversacion)) AS TIEMPOLUNCH, -- Columna K
        (SELECT TIMESTAMPDIFF(SECOND, fecha_inicio_estado, fecha_fin_estado) FROM estados_por_agente WHERE id_agente = c.id_agente AND estado = 'break' AND DATE(fecha_inicio_estado) = DATE(c.fecha_inicio_conversacion)) AS TIEMPOBREAK, -- Columna L
        (SELECT AVG(TIMESTAMPDIFF(SECOND, c.fecha_inicio_conversacion, c.fecha_fin_conversacion)) FROM conversacion c, conversaciones_por_tipo ct WHERE c.id_conversacion = ct.id_conversacion AND DATE(c.fecha_inicio_conversacion) = '2018-02-28' AND c.id_agente = '8ab09adb-56ec-43ef-ba9f-811d9cc5b6d6' AND ct.tipo = 'voice') AS PROMEDIOVOZ -- Columna M
FROM 	conversacion c,
		conversaciones_por_tipo ct
WHERE	c.id_conversacion = ct.id_conversacion
order by c.id_conversacion	
;


