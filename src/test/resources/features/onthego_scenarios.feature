@otg
Feature: OntheGo Application End-to-End Validation
  #As an OTG user
  #I want to validate core app workflows
  #So that I can ensure the login, navigation, directory search, and logout features work reliably

  Scenario: User performs a complete OTG journey
    Given the user opens the OTG application
    And the user reaches the SSO login screen
    When the user signs in using valid SSO credentials
   # Then the user should be successfully authenticated
   # And the user profile name should be visible on the home screen

#  @otg_with_Test_Data
#  Feature: OntheGo Application End-to-End Validation
#  #As an OTG user
#  #I want to validate core app workflows
#  #So that I can ensure the login, navigation, directory search, and logout features work reliably
#
#  Scenario Outline: User performs a complete OTG journey
#    Given the user opens the OTG application
#    And the user reaches the SSO login screen
#    When the user signs in using valid SSO credentials with "<username>" and "<password>"
#    Then the user should be successfully authenticated
#    And the user profile name should be visible on the home screen
#
#    Examples:
#      | username                 | password   |
#      | user1@example.com        | Pass123!   |




    #When the user navigates to Project Allocation through Tools & Utilities
    #Then the user should see the current allocation details
    #And the user should see the past allocation details if any

    When the user opens the Explore module
    And the user accesses the Brillio Directory section
    And the user searches for a specific employee
    Then the application should display the employee’s profile details
    And the profile should show correct email and gender information

    When the user opens the main menu
    And the user initiates logout
    Then the user should be logged out successfully
    And the login button should be displayed again on the screen
