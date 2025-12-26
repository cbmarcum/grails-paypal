package org.grails.plugin.paypal

import grails.gorm.transactions.Transactional

@Transactional
class PaymentItemService {

    PaymentItem get(Serializable id) {
        PaymentItem.get(id)
    }

    List<PaymentItem> list(Map args) {
        PaymentItem.list(args)
    }

    Long count() {
        PaymentItem.count()
    }

    void delete(Serializable id) {
        PaymentItem paymentItem = PaymentItem.get(id)
        if (paymentItem) {
            if (paymentItem.payment) {
                paymentItem.payment.removeFromPaymentItems(paymentItem)
            }
            paymentItem.delete()
        }
    }

    PaymentItem save(PaymentItem paymentItem) {
        paymentItem.save()
    }

}