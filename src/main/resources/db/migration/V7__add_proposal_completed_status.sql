ALTER TABLE public.propostas DROP CONSTRAINT IF EXISTS propostas_status_check;

ALTER TABLE public.propostas ADD CONSTRAINT propostas_status_check CHECK (
    (status)::text = ANY (
        ARRAY[
            'RECEIVED',
            'IN_TRIAGE',
            'ELIGIBLE',
            'PENDING_ADJUSTMENTS',
            'COMMERCIAL_PROPOSAL',
            'COMMERCIAL_PROPOSAL_APPROVED',
            'COMMERCIAL_PROPOSAL_REJECTED',
            'READY_FOR_NEXT_STAGE',
            'COMPLETED'
        ]::text[]
    )
);
