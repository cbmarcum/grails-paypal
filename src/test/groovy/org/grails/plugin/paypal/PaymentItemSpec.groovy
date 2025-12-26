package org.grails.plugin.paypal

import grails.testing.gorm.DomainUnitTest
import spock.lang.Specification

class PaymentItemSpec extends Specification implements DomainUnitTest<PaymentItem> {

    void "test PaymentItem requires itemName"() {
        when:
        def paymentItem = new PaymentItem(
            itemNumber: 'ITEM-001',
            amount: 10.00
        )

        then:
        !paymentItem.validate()
        paymentItem.errors['itemName']
    }

    void "test PaymentItem requires itemNumber"() {
        when:
        def paymentItem = new PaymentItem(
            itemName: 'Test Item',
            amount: 10.00
        )

        then:
        !paymentItem.validate()
        paymentItem.errors['itemNumber']
    }

    void "test PaymentItem validates with valid data"() {
        given:
        def payment = new Payment(
                status: Payment.PENDING,
                buyerId: 1L
        )

        when:
        def paymentItem = new PaymentItem(
            itemName: 'Test Item',
            itemNumber: 'ITEM-001',
            amount: 10.00
        )
        and:
        payment.addToPaymentItems(paymentItem)

        then:
        paymentItem.validate()
    }

    void "test PaymentItem fails validation when itemName is blank"() {
        when:
        def paymentItem = new PaymentItem(
            itemName: '',
            itemNumber: 'ITEM-001',
            amount: 10.00
        )

        then:
        !paymentItem.validate()
        paymentItem.errors['itemName']
    }

    void "test PaymentItem fails validation when itemNumber is blank"() {
        when:
        def paymentItem = new PaymentItem(
            itemName: 'Test Item',
            itemNumber: '',
            amount: 10.00
        )

        then:
        !paymentItem.validate()
        paymentItem.errors['itemNumber']
    }

    void "test PaymentItem default values are set correctly"() {
        when:
        def paymentItem = new PaymentItem()

        then:
        paymentItem.discountAmount == 0
        paymentItem.weight == 0.0
        paymentItem.quantity == 1
    }

    void "test PaymentItem belongs to Payment"() {
        given:
        def payment = new Payment(
            status: Payment.PENDING,
            buyerId: 1L
        )

        when:
        def paymentItem = new PaymentItem(
            itemName: 'Test Item',
            itemNumber: 'ITEM-001',
            amount: 10.00,
            payment: payment
        )

        then:
        paymentItem.payment == payment
    }

    void "test PaymentItem quantity defaults to 1"() {
        when:
        def paymentItem = new PaymentItem()

        then:
        paymentItem.quantity == 1
    }

    void "test PaymentItem weight defaults to 0.0"() {
        when:
        def paymentItem = new PaymentItem()

        then:
        paymentItem.weight == 0.0
    }

    void "test PaymentItem discountAmount defaults to 0"() {
        when:
        def paymentItem = new PaymentItem()

        then:
        paymentItem.discountAmount == 0
    }
}
