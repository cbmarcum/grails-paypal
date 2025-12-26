<html>
	<head>
        <meta name="layout" content="main" />
		<title>Transaction Complete</title>
	</head>
	<body>
    <div id="content" role="main">
        <div class="container">
		Your purchase is complete. Information for your reference can be seen below:
		<div id="transactionSummary" class="transactionSummary">
			<div class="transSummaryItem">
				<span class="transSummaryItemName">Transaction ID:</span>
				<span class="transSummaryItemValue">${payment.transactionId}</span>
			</div>
			<g:render template="txsummary" model="[payment:payment]" plugin="paypal" />
		</div>
        </div>
    </div>
	</body>
</html>