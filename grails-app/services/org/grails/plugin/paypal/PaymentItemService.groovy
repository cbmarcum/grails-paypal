package org.grails.plugin.paypal

import grails.gorm.services.Service

@Service(PaymentItem)
interface PaymentItemService {

    PaymentItem get(Serializable id)

    List<PaymentItem> list(Map args)

    Long count()

    void delete(Serializable id)

    PaymentItem save(PaymentItem paymentItem)

}