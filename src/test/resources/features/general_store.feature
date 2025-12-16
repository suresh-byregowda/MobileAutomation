@general
Feature: General Store POC

  @poc
  Scenario: Launch, fill form and navigate to products
    Given the app is launched
    When the user selects country "Argentina"
    And the user enters name "TestUser"
    #And the user selects gender "Male"
    And the user taps Let's Shop
    Then the product list should be displayed

  @poc_add
  Scenario: Add products and verify cart total
    Given user is on product list page
    When the user adds product "Air Jordan 4 Retro" and "Jordan 6 Rings"
    And the user navigates to cart
    Then the cart total should equal sum of item prices


  @poc_long
  Scenario: Long press Terms and validate modal
    Given user is on cart page
    When the user long presses on Terms and Conditions
    Then the Terms modal should be displayed
