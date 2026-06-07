-- ═══════════════════════════════════════════════════════════════
--   ESCROW PAYMENT SYSTEM — SUPABASE SCHEMA
--   Run this in Supabase SQL Editor
-- ═══════════════════════════════════════════════════════════════

-- ─── 1. ESCROW TRANSACTIONS TABLE ───
CREATE TABLE escrow_transactions (
    id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    order_id            UUID NOT NULL REFERENCES orders(id) ON DELETE CASCADE,
    customer_id         UUID NOT NULL REFERENCES auth.users(id),
    vendor_id           UUID NOT NULL REFERENCES vendors(id),
    amount_lkr          INTEGER NOT NULL,
    platform_fee_lkr    INTEGER DEFAULT 0,
    vendor_payout_lkr   INTEGER NOT NULL,
    status              TEXT NOT NULL CHECK (status IN (
        'HELD', 'DELIVERED', 'AWAITING_CONFIRMATION', 'CONFIRMED',
        'DISPUTED', 'UNDER_REVIEW', 'RELEASED_TO_VENDOR',
        'REFUNDED_TO_CUSTOMER', 'AUTO_RELEASED'
    )),
    created_at          BIGINT NOT NULL DEFAULT extract(epoch from now()) * 1000,
    delivered_at        BIGINT,
    auto_release_at     BIGINT,
    released_at         BIGINT,
    refunded_at         BIGINT
);

CREATE INDEX idx_escrow_order      ON escrow_transactions(order_id);
CREATE INDEX idx_escrow_customer   ON escrow_transactions(customer_id);
CREATE INDEX idx_escrow_vendor     ON escrow_transactions(vendor_id);
CREATE INDEX idx_escrow_status     ON escrow_transactions(status);
CREATE INDEX idx_escrow_auto       ON escrow_transactions(auto_release_at)
    WHERE status = 'AWAITING_CONFIRMATION';


-- ─── 2. DELIVERY PROOFS ───
CREATE TABLE delivery_proofs (
    id            UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    order_id      UUID NOT NULL REFERENCES orders(id) ON DELETE CASCADE,
    photo_urls    TEXT[] NOT NULL,
    rating        INTEGER NOT NULL CHECK (rating BETWEEN 1 AND 5),
    notes         TEXT,
    confirmed_at  BIGINT NOT NULL DEFAULT extract(epoch from now()) * 1000
);


-- ─── 3. DAMAGE REPORTS ───
CREATE TABLE damage_reports (
    id                  TEXT PRIMARY KEY,
    order_id            UUID NOT NULL REFERENCES orders(id) ON DELETE CASCADE,
    customer_id         UUID NOT NULL REFERENCES auth.users(id),
    type                TEXT NOT NULL CHECK (type IN (
        'PHYSICAL_DAMAGE', 'WRONG_ITEM', 'WRONG_SIZE',
        'MISSING_PARTS', 'NOT_AS_DESCRIBED', 'QUALITY_ISSUE', 'OTHER'
    )),
    description         TEXT NOT NULL,
    photo_urls          TEXT[] NOT NULL,
    video_url           TEXT,
    reported_at         BIGINT NOT NULL,
    refund_requested    BOOLEAN NOT NULL DEFAULT TRUE,
    admin_verdict       TEXT CHECK (admin_verdict IN (
        'APPROVED_REFUND', 'APPROVED_REPLACEMENT', 'PARTIAL_REFUND',
        'REJECTED', 'PENDING'
    )) DEFAULT 'PENDING',
    admin_notes         TEXT,
    resolved_at         BIGINT
);

CREATE INDEX idx_damage_order   ON damage_reports(order_id);
CREATE INDEX idx_damage_verdict ON damage_reports(admin_verdict);


-- ─── 4. VENDOR WALLETS ───
CREATE TABLE vendor_wallets (
    vendor_id            UUID PRIMARY KEY REFERENCES vendors(id),
    pending_lkr          INTEGER DEFAULT 0,
    available_lkr        INTEGER DEFAULT 0,
    total_lifetime_lkr   INTEGER DEFAULT 0,
    refunded_amount_lkr  INTEGER DEFAULT 0,
    updated_at           BIGINT NOT NULL DEFAULT extract(epoch from now()) * 1000
);


-- ═══════════════════════════════════════════════════════════════
--   TRIGGERS
-- ═══════════════════════════════════════════════════════════════

-- ─── Auto-update vendor wallet when escrow status changes ───
CREATE OR REPLACE FUNCTION update_vendor_wallet()
RETURNS TRIGGER AS $$
BEGIN
    -- Money moving from PENDING to AVAILABLE
    IF NEW.status IN ('RELEASED_TO_VENDOR', 'AUTO_RELEASED', 'CONFIRMED')
       AND OLD.status NOT IN ('RELEASED_TO_VENDOR', 'AUTO_RELEASED', 'CONFIRMED') THEN

        UPDATE vendor_wallets
        SET pending_lkr        = pending_lkr - NEW.vendor_payout_lkr,
            available_lkr      = available_lkr + NEW.vendor_payout_lkr,
            total_lifetime_lkr = total_lifetime_lkr + NEW.vendor_payout_lkr,
            updated_at         = extract(epoch from now()) * 1000
        WHERE vendor_id = NEW.vendor_id;

    -- Refunded to customer
    ELSIF NEW.status = 'REFUNDED_TO_CUSTOMER' AND OLD.status != 'REFUNDED_TO_CUSTOMER' THEN

        UPDATE vendor_wallets
        SET pending_lkr         = pending_lkr - NEW.vendor_payout_lkr,
            refunded_amount_lkr = refunded_amount_lkr + NEW.vendor_payout_lkr,
            updated_at          = extract(epoch from now()) * 1000
        WHERE vendor_id = NEW.vendor_id;
    END IF;

    -- Initial HELD → add to pending
    IF TG_OP = 'INSERT' AND NEW.status = 'HELD' THEN
        INSERT INTO vendor_wallets (vendor_id, pending_lkr, available_lkr, total_lifetime_lkr)
        VALUES (NEW.vendor_id, NEW.vendor_payout_lkr, 0, 0)
        ON CONFLICT (vendor_id) DO UPDATE
        SET pending_lkr = vendor_wallets.pending_lkr + NEW.vendor_payout_lkr,
            updated_at  = extract(epoch from now()) * 1000;
    END IF;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trigger_vendor_wallet
AFTER INSERT OR UPDATE ON escrow_transactions
FOR EACH ROW EXECUTE FUNCTION update_vendor_wallet();


-- ═══════════════════════════════════════════════════════════════
--   ROW LEVEL SECURITY (RLS)
-- ═══════════════════════════════════════════════════════════════

ALTER TABLE escrow_transactions ENABLE ROW LEVEL SECURITY;
ALTER TABLE delivery_proofs     ENABLE ROW LEVEL SECURITY;
ALTER TABLE damage_reports      ENABLE ROW LEVEL SECURITY;
ALTER TABLE vendor_wallets      ENABLE ROW LEVEL SECURITY;

-- Customers can see only their own escrows
CREATE POLICY "Customers see own escrows" ON escrow_transactions
    FOR SELECT TO authenticated
    USING (customer_id = auth.uid());

-- Vendors can see only their own escrows
CREATE POLICY "Vendors see own escrows" ON escrow_transactions
    FOR SELECT TO authenticated
    USING (vendor_id IN (SELECT id FROM vendors WHERE user_id = auth.uid()));

-- Admins see everything
CREATE POLICY "Admins see all escrows" ON escrow_transactions
    FOR ALL TO authenticated
    USING (EXISTS (SELECT 1 FROM users WHERE id = auth.uid() AND role = 'admin'));

-- Customers can submit damage reports for own orders
CREATE POLICY "Customers create damage reports" ON damage_reports
    FOR INSERT TO authenticated
    WITH CHECK (customer_id = auth.uid());

-- Customers see own damage reports
CREATE POLICY "Customers see own damage reports" ON damage_reports
    FOR SELECT TO authenticated
    USING (customer_id = auth.uid() OR
           EXISTS (SELECT 1 FROM users WHERE id = auth.uid() AND role = 'admin'));

-- Vendors see own wallet
CREATE POLICY "Vendors see own wallet" ON vendor_wallets
    FOR SELECT TO authenticated
    USING (vendor_id IN (SELECT id FROM vendors WHERE user_id = auth.uid()));
