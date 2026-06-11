-- V7: Adds contract total value used by dashboard revenue metrics.
ALTER TABLE contratos
    ADD COLUMN IF NOT EXISTS valor_total NUMERIC(15, 2);
