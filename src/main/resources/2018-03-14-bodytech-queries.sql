-- Reporte
SELECT	 DATE(c.fecha_inicio_conversacion) AS FECHA,			 
		 c.nombre_agente AS NOMBRE_AGENTE,
         (SELECT TIME_FORMAT(fecha_inicio_estado, '%H : %m : %s') FROM estados_por_agente WHERE UPPER(estado) = 'ON_QUEUE' AND DATE(fecha_inicio_estado) = FECHA AND id_agente = c.id_agente ORDER BY fecha_inicio_estado LIMIT 1) AS HORA_INGRESO_COLA,
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
WHERE	 (DATE(c.fecha_inicio_conversacion) BETWEEN '2018-03-12' AND '2018-03-12')
AND		 id_agente IN ('c4a291b1-153d-4150-a4e3-8dad40c48f42','8ab09adb-56ec-43ef-ba9f-811d9cc5b6d6','8c977b15-994a-4895-9671-53d4e455ecb7','b2caca88-2b32-49ab-98f6-90cdfbb048ab','a29851e1-a466-45e8-a7c1-dba718a23bcf','304cc51b-f91d-4b5e-8188-2ec1b6e2479f','4d0c5f3d-8618-4c00-a87f-4de882fd2f75','f3dfd192-557f-49f8-b51e-bd6e967e08db','8ffe0bb5-e0e1-4ffa-9de2-9d9ce196da39','ab15299f-1a0c-4b49-a1d0-76fa00ec2574','41f73f05-906a-4871-a389-0708395949fb','13d7aee4-7390-4ade-8e74-d1daaed46cd4','9d29df2a-86ae-4826-b0af-f073c74bdf40','670c5ee4-7faf-49e6-ac83-9fa46cc516fc','5c580489-6447-4b53-bb3f-76edd6dae6bc','e6d343d8-0973-4bb8-bb5c-f10f45ddd8ae','8a60b964-d599-4cd7-a561-69eeb98881a7','f5579fd1-1873-4943-888a-d5ab1567a2d8','98d344f9-79f8-47b5-9532-fb01a97a4921','cf18bd98-e6ca-41d5-a70c-0600cdb19ad7','345c6e29-28c0-4960-8cb1-6c4501918b14','aa5a7a2a-5d01-4308-a1a7-122ec2079dd7','9eb6bea1-64ce-4d9d-8af1-27b34f5aaba5','0d5fc836-390d-4976-b06e-89b8d2f90191','88d35683-591c-4245-9e1c-d2dbe7d1808f','9cc35a58-7c1e-4905-a7b4-0509a8e85c3d','97055e56-4378-4e6d-86df-78d4f8241e67','58b88f85-b281-4f74-b6e1-b0a3d4f581f2','42aa51bf-a18d-4595-894a-8628c345ea41','1f5d559f-2316-4d2a-9685-7c07e4520821','777a6fc7-a97e-4f6a-ab38-bb44d5fff6fc','c889ba2e-3f06-4bdb-92b0-f4816da953a5','93cf48e8-de8f-4961-b678-5342e7a050b0','27458d36-6fd1-4195-b3f4-0e0b399eebdf','f5902c4d-5ab2-4b83-af8f-a4c7769dfe58','c417dd32-58fd-4c5f-a5ed-86d311d66000','217e3050-2d37-468d-b313-6e296f113baa','4e3c08cc-68f9-41ed-93bc-cc55ab2c2369','5a742768-af40-4637-86e8-04d54fd4decd','e92b1031-b9a8-436b-9a97-71d861f68e56')
GROUP BY FECHA, NOMBRE_AGENTE
ORDER BY FECHA;

-- Reporte conteo
SELECT COUNT(*)
FROM (
SELECT	 DATE(c.fecha_inicio_conversacion) AS FECHA,			 
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
WHERE	 (DATE(c.fecha_inicio_conversacion) BETWEEN '2018-03-12' AND '2018-03-12')
AND		 id_agente IN ('e21fc9de-f3d6-4971-ae5c-d3f09d0a70ce','64ad8c51-66ab-4d88-ac48-77dcff93d87d')
GROUP BY FECHA, NOMBRE_AGENTE) AS conteo;

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
SELECT *
FROM estados_por_agente
WHERE DATE(fecha_inicio_estado) = '2018-03-12'
AND id_agente = '777a6fc7-a97e-4f6a-ab38-bb44d5fff6fc'
AND UPPER(estado) = 'OFFLINE'
ORDER BY fecha_inicio_estado desc
limit 1
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