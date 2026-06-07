import { serve } from "https://deno.land/std@0.168.0/http/server.ts"
import { createClient } from "https://esm.sh/@supabase/supabase-js@2"

const CORS_HEADERS = {
  'Access-Control-Allow-Origin': '*',
  'Access-Control-Allow-Headers': 'authorization, x-client-info, apikey, content-type',
}

serve(async (req) => {
  if (req.method === 'OPTIONS') {
    return new Response('ok', { headers: CORS_HEADERS })
  }

  try {
    const supabase = createClient(
      Deno.env.get('SUPABASE_URL')!,
      Deno.env.get('SUPABASE_SERVICE_ROLE_KEY')!
    )

    const now = Date.now()
    const nowIso = new Date(now).toISOString()
    console.log(`[MIKO Escrow] Running auto-release check at ${nowIso}`)

    const { data: expiredEscrows, error: fetchError } = await supabase
      .from('escrow_transactions')
      .select('*')
      .eq('status', 'AWAITING_CONFIRMATION')
      .lte('auto_release_at', nowIso)

    if (fetchError) {
      console.error('[MIKO Escrow] Fetch error:', fetchError)
      return jsonResponse({ error: fetchError.message }, 500)
    }

    if (!expiredEscrows || expiredEscrows.length === 0) {
      console.log('[MIKO Escrow] No escrows to release')
      return jsonResponse({
        processed: 0,
        message: 'No escrows to release at this time',
        timestamp: nowIso
      }, 200)
    }

    console.log(`[MIKO Escrow] Found ${expiredEscrows.length} escrows to release`)

    const results = []

    for (const escrow of expiredEscrows) {
      try {
        const { error: updateError } = await supabase
          .from('escrow_transactions')
          .update({
            status: 'AUTO_RELEASED',
            released_at: nowIso
          })
          .eq('id', escrow.id)

        if (updateError) {
          console.error(`[MIKO Escrow] Failed to release ${escrow.id}:`, updateError)
          results.push({ id: escrow.id, success: false, error: updateError.message })
          continue
        }

        const shortOrderId = escrow.order_id.toString().slice(0, 8).toUpperCase()

        const { error: vendorNotifError } = await supabase
          .from('notifications')
          .insert({
            user_id: escrow.vendor_id,
            type: 'PAYMENT',
            title: 'Payment Released 💰',
            message: `LKR ${escrow.vendor_payout_lkr.toLocaleString()} has been credited to your wallet for order #${shortOrderId}`,
            data: {
              order_id: escrow.order_id,
              escrow_id: escrow.id,
              amount: escrow.vendor_payout_lkr,
              channel: 'miko_seller'
            },
            is_read: false
          })

        if (vendorNotifError) {
          console.warn(`[MIKO Escrow] Vendor notification failed:`, vendorNotifError)
        }

        const { error: customerNotifError } = await supabase
          .from('notifications')
          .insert({
            user_id: escrow.customer_id,
            type: 'ORDER',
            title: 'Order Auto-Confirmed ✓',
            message: `Your order #${shortOrderId} has been automatically marked as delivered after 7 days. We hope you enjoyed your purchase!`,
            data: {
              order_id: escrow.order_id,
              escrow_id: escrow.id,
              channel: 'miko_orders'
            },
            is_read: false
          })

        if (customerNotifError) {
          console.warn(`[MIKO Escrow] Customer notification failed:`, customerNotifError)
        }

        results.push({
          id: escrow.id,
          success: true,
          order_id: escrow.order_id,
          amount: escrow.vendor_payout_lkr,
          vendor_id: escrow.vendor_id,
          customer_id: escrow.customer_id
        })

        console.log(`[MIKO Escrow] Released ${escrow.id}: LKR ${escrow.vendor_payout_lkr} to vendor ${escrow.vendor_id}`)

      } catch (err) {
        const errorMsg = err instanceof Error ? err.message : 'Unknown error'
        console.error(`[MIKO Escrow] Exception releasing ${escrow.id}:`, errorMsg)
        results.push({ id: escrow.id, success: false, error: errorMsg })
      }
    }

    const successCount = results.filter(r => r.success).length
    const failCount = results.filter(r => !r.success).length

    console.log(`[MIKO Escrow] Complete. Success: ${successCount}, Failed: ${failCount}`)

    return jsonResponse({
      processed: results.length,
      successful: successCount,
      failed: failCount,
      results: results,
      timestamp: nowIso
    }, 200)

  } catch (err) {
    const errorMsg = err instanceof Error ? err.message : 'Unknown error'
    console.error('[MIKO Escrow] Fatal error:', errorMsg)
    return jsonResponse({ error: errorMsg }, 500)
  }
})

function jsonResponse(body: object, status: number) {
  return new Response(
    JSON.stringify(body),
    {
      status,
      headers: { ...CORS_HEADERS, 'Content-Type': 'application/json' }
    }
  )
}