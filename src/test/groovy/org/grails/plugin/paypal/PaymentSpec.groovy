package org.grails.plugin.paypal

import grails.testing.gorm.DomainUnitTest
import spock.lang.Specification

class PaymentSpec extends Specification implements DomainUnitTest<Payment> {

    void "test Payment validates with valid data"() {
        when:
        def payment = new Payment(
            status: Payment.PENDING,
            buyerId: 1L
        )

        then:
        payment.validate()
    }

    void "test Payment status must be in valid list"() {
        when:
        def payment = new Payment(
            status: 'INVALID_STATUS',
            buyerId: 1L
        )

        then:
        !payment.validate()
        payment.errors['status']
    }

    void "test Payment status defaults to PENDING"() {
        when:
        def payment = new Payment()

        then:
        payment.status == Payment.PENDING
    }

    void "test Payment transactionId is nullable"() {
        when:
        def payment = new Payment(
            status: Payment.PENDING,
            buyerId: 1L,
            transactionId: null
        )

        then:
        payment.validate()
    }

    void "test Payment paypalTransactionId is nullable"() {
        when:
        def payment = new Payment(
            status: Payment.PENDING,
            buyerId: 1L,
            paypalTransactionId: null
        )

        then:
        payment.validate()
    }

    void "test Payment buyerInformation is nullable"() {
        when:
        def payment = new Payment(
            status: Payment.PENDING,
            buyerId: 1L,
            buyerInformation: null
        )

        then:
        payment.validate()
    }

    void "test Payment beforeInsert generates transactionId"() {
        when:
        def payment = new Payment(
            status: Payment.PENDING,
            buyerId: 123L,
            transactionIdPrefix: 'TRANS'
        )
        payment.beforeInsert.call()

        then:
        payment.transactionId != null
        payment.transactionId.startsWith('TRANS-123-')
    }

    void "test Payment hasMany paymentItems relationship"() {
        given:
        def payment = new Payment(
            status: Payment.PENDING,
            buyerId: 1L
        )
        def paymentItem = new PaymentItem(
            itemName: 'Test Item',
            itemNumber: 'ITEM-001',
            amount: 10.00
        )

        when:
        payment.addToPaymentItems(paymentItem)

        then:
        payment.paymentItems.size() == 1
        payment.paymentItems[0] == paymentItem
    }

    void "test Payment default currency is USD"() {
        when:
        def payment = new Payment()

        then:
        payment.currency == Currency.getInstance("USD")
    }

    void "test Payment default values are set correctly"() {
        when:
        def payment = new Payment()

        then:
        payment.status == Payment.PENDING
        payment.tax == 0
        payment.discountCartAmount == 0
        payment.currency == Currency.getInstance("USD")
        payment.shipping == 0.0
        payment.gross == 0.0
        payment.transactionIdPrefix == "TRANS"
    }

    void "test Payment toString returns transaction id when saved"() {
        when:
        def payment = new Payment(
            transactionId: 'TRANS-123-456'
        )

        then:
        payment.toString() == 'Payment: TRANS-123-456'
    }

    void "test Payment toString returns 'not saved' when transactionId is null"() {
        when:
        def payment = new Payment()

        then:
        payment.toString() == 'Payment: not saved'
    }

    void "test Payment fails validation with invalid status"() {
        when:
        def payment = new Payment(
            status: 'BOGUS',
            buyerId: 1L
        )

        then:
        !payment.validate()
        payment.errors['status'].code == 'not.inList'
    }
}
