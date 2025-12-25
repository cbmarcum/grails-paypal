package org.grails.plugin.paypal

import grails.testing.mixin.integration.Integration
import grails.gorm.transactions.Rollback
import spock.lang.Specification
import org.hibernate.SessionFactory

@Integration
@Rollback
class PaymentItemServiceSpec extends Specification {

    PaymentItemService paymentItemService
    SessionFactory sessionFactory

    private Long setupData() {
        Payment payment = new Payment(status: Payment.PENDING, buyerId: 1L)
        payment.save()
        PaymentItem paymentItem1 = new PaymentItem(itemName: 'Widget A', itemNumber: 'ITEM-001', amount: 10.00)
        PaymentItem paymentItem2 = new PaymentItem(itemName: 'Widget B', itemNumber: 'ITEM-002', amount: 20.00)
        PaymentItem paymentItem3 = new PaymentItem(itemName: 'Widget C', itemNumber: 'ITEM-003', amount: 30.00)
        PaymentItem paymentItem4 = new PaymentItem(itemName: 'Widget D', itemNumber: 'ITEM-004', amount: 40.00)
        PaymentItem paymentItem5 = new PaymentItem(itemName: 'Widget E', itemNumber: 'ITEM-005', amount: 50.00)
        payment.addToPaymentItems(paymentItem1)
        payment.addToPaymentItems(paymentItem2)
        payment.addToPaymentItems(paymentItem3)
        payment.addToPaymentItems(paymentItem4)
        payment.addToPaymentItems(paymentItem5)
        paymentItem1.save(flush: true, failOnError: true)
        paymentItem2.save(flush: true, failOnError: true)
        paymentItem3.save(flush: true, failOnError: true)
        paymentItem4.save(flush: true, failOnError: true)
        paymentItem5.save(flush: true, failOnError: true)

        paymentItem3.id
    }

    void "test get"() {
        setupData()

        expect:
        paymentItemService.get(1) != null
    }

    void "test list"() {
        setupData()

        when:
        List<PaymentItem> paymentItemList = paymentItemService.list(max: 2, offset: 2)

        then:
        paymentItemList.size() == 2
        paymentItemList[0].itemNumber == "ITEM-003"
        paymentItemList[1].itemNumber == "ITEM-004"

    }

    void "test count"() {
        setupData()

        expect:
        paymentItemService.count() == 5
    }

    void "test delete"() {
        Long paymentItemId = setupData()

        expect:
        paymentItemService.count() == 5

        when:
        paymentItemService.delete(paymentItemId)
        sessionFactory.currentSession.flush()

        then:
        paymentItemService.count() == 4
    }

    void "test save"() {
        when:
        Payment payment = new Payment(status: Payment.PENDING, buyerId: 1L)
        payment.save()
        PaymentItem paymentItem = new PaymentItem(itemName: 'Widget C', itemNumber: 'ITEM-003', amount: 30.00)
        payment.addToPaymentItems(paymentItem)
        paymentItemService.save(paymentItem)

        then:
        paymentItem.id != null
    }
}
