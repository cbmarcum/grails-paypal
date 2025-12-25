package org.grails.plugin.paypal

import grails.testing.mixin.integration.Integration
import grails.gorm.transactions.Rollback
import spock.lang.Specification
import org.hibernate.SessionFactory

@Integration
@Rollback
class BuyerInformationServiceSpec extends Specification {

    BuyerInformationService buyerInformationService
    SessionFactory sessionFactory

    private Long setupData() {
        new BuyerInformation(firstName: 'John', lastName: 'Doe', email: 'john@example.com').save(flush: true, failOnError: true)
        new BuyerInformation(firstName: 'Jane', lastName: 'Smith', email: 'jane@example.com').save(flush: true, failOnError: true)
        BuyerInformation buyerInformation = new BuyerInformation(firstName: 'Bob', lastName: 'Johnson', email: 'bob@example.com').save(flush: true, failOnError: true)
        new BuyerInformation(firstName: 'Alice', lastName: 'Williams', email: 'alice@example.com').save(flush: true, failOnError: true)
        new BuyerInformation(firstName: 'Charlie', lastName: 'Brown', email: 'charlie@example.com').save(flush: true, failOnError: true)
        buyerInformation.id
    }

    void "test get"() {
        setupData()

        expect:
        buyerInformationService.get(1) != null
    }

    void "test list"() {
        setupData()

        when:
        List<BuyerInformation> buyerInformationList = buyerInformationService.list(max: 2, offset: 2)

        then:
        buyerInformationList.size() == 2
        buyerInformationList[0].email == "bob@example.com"
        buyerInformationList[1].email == "alice@example.com"
    }

    void "test count"() {
        setupData()

        expect:
        buyerInformationService.count() == 5
    }

    void "test delete"() {
        Long buyerInformationId = setupData()

        expect:
        buyerInformationService.count() == 5

        when:
        buyerInformationService.delete(buyerInformationId)
        sessionFactory.currentSession.flush()

        then:
        buyerInformationService.count() == 4
    }

    void "test save"() {
        when:
        BuyerInformation buyerInformation = new BuyerInformation(firstName: 'Bob', lastName: 'Johnson', email: 'bob@example.com')
        buyerInformationService.save(buyerInformation)

        then:
        buyerInformation.id != null
    }
}
