package org.grails.plugin.paypal

import grails.validation.ValidationException
import static org.springframework.http.HttpStatus.*

class BuyerInformationController {

    BuyerInformationService buyerInformationService

    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]

    def index(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        respond buyerInformationService.list(params), model:[buyerInformationCount: buyerInformationService.count()]
    }

    def show(Long id) {
        respond buyerInformationService.get(id)
    }

    def create() {
        respond new BuyerInformation(params)
    }

    def save(BuyerInformation buyerInformation) {
        if (buyerInformation == null) {
            notFound()
            return
        }

        try {
            buyerInformationService.save(buyerInformation)
        } catch (ValidationException e) {
            respond buyerInformation.errors, view:'create'
            return
        }

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.created.message', args: [message(code: 'buyerInformation.label', default: 'BuyerInformation'), buyerInformation.id])
                redirect buyerInformation
            }
            '*' { respond buyerInformation, [status: CREATED] }
        }
    }

    def edit(Long id) {
        respond buyerInformationService.get(id)
    }

    def update(BuyerInformation buyerInformation) {
        if (buyerInformation == null) {
            notFound()
            return
        }

        try {
            buyerInformationService.save(buyerInformation)
        } catch (ValidationException e) {
            respond buyerInformation.errors, view:'edit'
            return
        }

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.updated.message', args: [message(code: 'buyerInformation.label', default: 'BuyerInformation'), buyerInformation.id])
                redirect buyerInformation
            }
            '*'{ respond buyerInformation, [status: OK] }
        }
    }

    def delete(Long id) {
        if (id == null) {
            notFound()
            return
        }

        buyerInformationService.delete(id)

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.deleted.message', args: [message(code: 'buyerInformation.label', default: 'BuyerInformation'), id])
                redirect action:"index", method:"GET"
            }
            '*'{ render status: NO_CONTENT }
        }
    }

    protected void notFound() {
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'buyerInformation.label', default: 'BuyerInformation'), params.id])
                redirect action: "index", method: "GET"
            }
            '*'{ render status: NOT_FOUND }
        }
    }
}
