package org.grails.plugin.paypal

import grails.testing.mixin.integration.Integration
import grails.gorm.transactions.Rollback
import spock.lang.Specification
import org.hibernate.SessionFactory

@Integration
@Rollback
class PaymentServiceSpec extends Specification {

    PaymentService paymentService
    SessionFactory sessionFactory

    private Long setupData() {
        new Payment(status: Payment.PENDING, buyerId: 1L).save(flush: true, failOnError: true)
        new Payment(status: Payment.COMPLETE, buyerId: 2L).save(flush: true, failOnError: true)
        Payment payment = new Payment(status: Payment.PENDING, buyerId: 3L).save(flush: true, failOnError: true)
        new Payment(status: Payment.CANCELLED, buyerId: 4L).save(flush: true, failOnError: true)
        new Payment(status: Payment.FAILED, buyerId: 5L).save(flush: true, failOnError: true)
        payment.id
    }

    void "test get"() {
        def paymentId = setupData()

        expect:
        paymentService.get(paymentId) != null
    }

    void "test list"() {
        setupData()

        when:
        List<Payment> paymentList = paymentService.list(max: 2, offset: 2)

        then:
        paymentList.size() == 2
        paymentList[0].buyerId == 3
        paymentList[1].buyerId == 4
    }

    void "test count"() {
        setupData()

        expect:
        paymentService.count() == 5
    }

    void "test delete"() {
        Long paymentId = setupData()

        expect:
        paymentService.count() == 5

        when:
        paymentService.delete(paymentId)
        sessionFactory.currentSession.flush()

        then:
        paymentService.count() == 4
    }

    void "test save"() {
        when:
        Payment payment =  new Payment(status: Payment.PENDING, buyerId: 3L)
        paymentService.save(payment)

        then:
        payment.id != null
    }
}
