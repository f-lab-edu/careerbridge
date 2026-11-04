# Payment flow

## Local setup

1. Start MySQL using the project's datasource configuration.
2. Set the `TOSS_SECRET_KEY` environment variable to a Toss test secret key.
3. Run `./gradlew.bat bootRun`.
4. Authenticate as the mentee who owns an APPROVED match backed by persisted product and user records.

The secret key belongs on the server only. Use the matching test client key in the frontend.

## Request sequence

Send the login bearer token with all payment API requests.

1. `POST /api/payments/ready` with `{"matchId":1}`.
2. Read `data.orderId`, `data.amount`, and `data.orderName` from the response.
3. Use these values with the Toss frontend SDK to open the payment window. Configure frontend `successUrl` and `failUrl` pages.
4. On the success page, read `paymentKey`, `orderId`, and `amount` from the query string.
5. `POST /api/payments/confirm` with these three values and the same mentee's bearer token.
6. `GET /api/payments/{paymentId}` to check the persisted result.

Opening the success page alone does not finish payment. The server must approve it with Toss.
The frontend SDK and redirect pages are not included in this backend project.

## Current guarantees and limits

- The server snapshots the product price at payment preparation.
- Approval checks ownership, amount, payment status, and match status before contacting Toss.
- Repeating a completed confirmation with the same payment key and amount returns the existing payment.
- Payment preparation locks the match; confirmation locks the payment row until the transaction ends.
- An HTTP failure leaves the local payment pending. It must not automatically be treated as a definitive payment failure.
- Database transactions cannot roll back an already successful external payment. Recovery by PG inquiry/webhooks is still needed if approval succeeds but local commit fails.
- Cancellation/refunds, pending-payment expiry, and reusing an existing READY payment remain follow-up work.
- Other domains still contain fake repositories; real product/mentor persistence and the match creation/approval workflow must be connected before end-to-end use.

## Tests

`./gradlew.bat test --tests 'com.careerbridge.payment.*' --tests 'com.careerbridge.CareerbridgeApplicationTests'`

These tests use mocked PG responses and H2 for application startup. They do not charge a real card or establish MySQL concurrency behavior.

Reference: https://docs.tosspayments.com/guides/v2/get-started/payment-flow
