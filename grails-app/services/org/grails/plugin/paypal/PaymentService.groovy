package org.grails.plugin.paypal

import grails.gorm.transactions.Transactional

@Transactional
class PaymentService {

    Payment get(Serializable id) {
        return Payment.get(id)
    }

    List<Payment> list(Map args) {
        return Payment.list(args)
    }

    Long count() {
        return Payment.count()
    }

    void delete(Serializable id) {
        Payment.get(id)?.delete()
    }

    Payment save(Payment payment) {
        return payment.save()
    }

    Payment findByTransactionId(String transactionId) {
        return Payment.findByTransactionId(transactionId)
    }

    Payment createPayment(Map params) {
        def payment = new Payment(params)
        payment.addToPaymentItems(new PaymentItem(params))
        // payment.save(flush: true, failOnError: true) // saved in PaypalController buy (after validate)
        return payment
    }

    void updatePaymentFromPaypal(Payment payment, Map params, String status) {
        payment.paypalTransactionId = params.txn_id
        payment.status = status
        updateBuyerInformation(payment, params)
        updateTotal(payment, params)
        payment.save(flush: true)
    }

    void cancelPayment(Payment payment) {
        payment.status = Payment.CANCELLED
        payment.save(flush: true)
    }

    private void updateBuyerInformation(Payment payment, Map params) {
        BuyerInformation buyerInfo = payment.buyerInformation ?: new BuyerInformation()
        buyerInfo.populateFromPaypal(params)
        payment.buyerInformation = buyerInfo
    }

    private void updateTotal(Payment payment, Map params) {
        if (params?.mc_shipping) {
            payment.shipping = new BigDecimal(params?.mc_shipping)
        }
        if (params.containsKey("tax")) {
            payment.tax = Double.valueOf(params?.tax)
        }
        if (params.containsKey("mc_gross")) {
            payment.gross = new BigDecimal(params?.mc_gross)
        }
    }
}