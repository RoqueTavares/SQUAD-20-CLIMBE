-- V4: Add pdfUrl and externalSignatureId to contratos table

ALTER TABLE contratos ADD COLUMN url_pdf character varying(1000);
ALTER TABLE contratos ADD COLUMN id_assinatura_externa character varying(255);
