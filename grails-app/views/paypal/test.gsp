<%@  page import="org.grails.plugin.paypal.PaymentItem; org.grails.plugin.paypal.Payment"  %>
<!DOCTYPE html>
<html>
	<head>
        <meta name="layout" content="main" />
		<title>Paypal Test</title>
	</head>

	<body>
    <div id="content" role="main">
        <div class="container">
        Buy Now: item, no discount.. 
		<paypal:button itemName="iPod Nano"
		               itemNumber="IPD32048039"
		               amount="99.00"
                       discountAmount="0.00"
		               buyerId="10"
		/>

        Buy Now: item, with discount.. 
		<paypal:button itemName="iPod Nano"
		               itemNumber="IPD32048039"
		               amount="99.00"
		               discountAmount="9.00"
		               buyerId="10"
		/>

        Cart upload..
        <%
            Payment.withTransaction { status ->
                if (Payment.count() == 0) {
                    payment = new Payment(buyerId: 10)
                    payment.addToPaymentItems(
                            new PaymentItem(
                                    amount: 99,
                                    discountAmount: 9,
                                    itemName: "iPod Nano",
                                    itemNumber: "IPD32048039"
                            )
                    )
                    payment.save(flush:true)
                }

                else {
                    payment = Payment.list()[0]
                }
            }
        %>

        <g:form
                controller="paypal"
                action="uploadCart"
                params="[transactionId:payment.transactionId]"
            >
            <input type="image" class="paypal"
                src="https://www.paypalobjects.com/WEBSCR-640-20110306-1/en_US/i/btn/btn_xpressCheckout.gif"
                alt="Click to pay via PayPal - the safer, easier way to pay"/>
        </g:form>
        </div>
    </div>
	</body>
</html>
