@otg
#Feature: OntheGo Application End-to-End Validation
  #As an OTG user
  #I want to validate core app workflows
  #So that I can ensure the login, navigation, directory search, and logout features work reliably


  Scenario: User performs a complete OTG journey
    Given the user opens the OTG application
    And the user reaches the SSO login screen
    When the user signs in using valid SSO credentials
    Then the user should be successfully authenticated
    And the user profile name should be visible on the home screen

    When the user opens the Explore module
    And the user accesses the Brillio Directory section
    And the user searches for a specific employee
    Then the application should display the employee’s profile details
    And the profile should show correct email and gender information

    When the user opens the main menu
    And the user initiates logout
    Then the user should be logged out successfully
    And the login button should be displayed again on the screen

