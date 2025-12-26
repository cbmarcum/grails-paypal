package org.grails.plugin.paypal

import grails.validation.ValidationException
import static org.springframework.http.HttpStatus.*

class PaymentItemController {

    PaymentItemService paymentItemService

    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]

    def index(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        respond paymentItemService.list(params), model:[paymentItemCount: paymentItemService.count()]
    }

    def show(Long id) {
        respond paymentItemService.get(id)
    }

    def create() {
        respond new PaymentItem(params)
    }

    def save(PaymentItem paymentItem) {
        if (paymentItem == null) {
            notFound()
            return
        }

        try {
            paymentItemService.save(paymentItem)
        } catch (ValidationException e) {
            respond paymentItem.errors, view:'create'
            return
        }

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.created.message', args: [message(code: 'paymentItem.label', default: 'PaymentItem'), paymentItem.id])
                redirect paymentItem
            }
            '*' { respond paymentItem, [status: CREATED] }
        }
    }

    def edit(Long id) {
        respond paymentItemService.get(id)
    }

    def update(PaymentItem paymentItem) {
        if (paymentItem == null) {
            notFound()
            return
        }

        try {
            paymentItemService.save(paymentItem)
        } catch (ValidationException e) {
            respond paymentItem.errors, view:'edit'
            return
        }

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.updated.message', args: [message(code: 'paymentItem.label', default: 'PaymentItem'), paymentItem.id])
                redirect paymentItem
            }
            '*'{ respond paymentItem, [status: OK] }
        }
    }

    def delete(Long id) {
        if (id == null) {
            notFound()
            return
        }

        paymentItemService.delete(id)

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.deleted.message', args: [message(code: 'paymentItem.label', default: 'PaymentItem'), id])
                redirect action:"index", method:"GET"
            }
            '*'{ render status: NO_CONTENT }
        }
    }

    protected void notFound() {
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'paymentItem.label', default: 'PaymentItem'), params.id])
                redirect action: "index", method: "GET"
            }
            '*'{ render status: NOT_FOUND }
        }
    }
}
