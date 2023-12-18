SELECT DISTINCT id_empresa, cod_concepto, substr(descrip_concepto,1,20) FROM transaccion 
ORDER BY id_empresa, cod_concepto
--WHERE cod_concepto = 1276;