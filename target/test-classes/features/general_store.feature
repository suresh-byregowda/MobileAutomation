@general
Feature: General Store POC

  @poc
  Scenario Outline: Launch, fill form and navigate to products - <user>

    Given the app is launched
    When the user selects country "<country>"
    And the user enters name "<user>"
    And the user taps Let's Shop
    Then the product list should be displayed

    Examples:
      | user        | country    |
      | User01      | Argentina  |
      | User02      | Brazil     |
      #| User03      | India      |
      #| User04      | USA        |
      #| User05      | Canada     |
      #| User06      | Germany    |
      #| User07      | France     |
      #| User08      | Spain      |
      | User09      | Italy      |
      #| User10      | Mexico     |
      #| User11      | Chile      |
      #| User12      | Peru       |
      #| User13      | Japan      |
      | User14      | China      |
      #| User15      | Korea      |
      #| User16      | Australia  |
      #| User17      | UK         |
      #| User18      | Ireland    |
      #| User19      | Sweden     |
      #| User20      | Norway     |
      #| User21      | Denmark    |
      #| User22      | Finland    |
      #| User23      | Poland     |
      #| User24      | Austria    |
      #| User25      | Belgium    |
      #| User26      | Netherlands|
      #| User27      | Portugal   |
      #| User28      | Greece     |
      #| User29      | Turkey     |
      | User30      | UAE        |


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
