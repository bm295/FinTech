Feature: Transparent money movement
  As a consumer
  I want to see the current status of my payment
  So that I can understand where my money is

  Scenario: View a completed payment in the real-time ledger
    Given the consumer has a completed payment of 50.00 USD
    When the consumer opens the payment activity
    Then the ledger shows the payment amount as 50.00 USD
    And the payment status is "completed"
