$(document).ready ->

  # get HTML elements
  $form = $("#login-form")
  $username = $("#username")
  $email = $("#email")
  $displayName = $("#displayName")
  $country = $("#country")
  $school = $("#school")
  $gender = $("#gender")
  $password = $("#password")
  $password2 = $("#password2")
  $submit = $("#submit")
  $direct = $("#redirect")
  $message = $("#message")

  # Display country list
  $country.html ""
  $option = $("<option>")
  $option.html "Please select your country..."
  $option.attr('disabled', 'disabled')
  $option.attr('selected', 'selected')
  $country.append $option
  for name in window.countries
    code = window.country_to_code[name]
    $option = $("<option>")
    $option.html name
    $option.val code
    $country.append $option

  # Get current country
  $.ajax
    url: '//freegeoip.net/json/',
    type: 'POST',
    dataType: 'jsonp',
    success: (location) ->
      if ! $country.val()
        # If the user already make decision before getting current location
        $country.val location["country_code"]

  displayFormError = (field, message) ->
    if field != null
      $("#" + field).select()
    $message.html(message)
    $submit.removeAttr('disabled')

  submitForm = (e) ->
    e.preventDefault()
    $submit.attr('disabled','disabled')
    $message.html("Submitting...")
    console.log "prepare to submit form"

    # validate form
    if $password.val() != $password2.val()
      displayFormError("password", "Passwords are not the same.")
      return false
    if $password.val().length < 6
      displayFormError("password", "We recommend a stronger password with minimum length of 6.")
      return false

    # construct submit object
    data = {
      "username": $username.val()
      "email": $email.val()
      "displayName": $displayName.val()
      "country": $country.val()
      "school": $school.val()
      "gender": $gender.val() == "1"
      "password": $password.val()
    }
    console.log data

    # submit the data
    $.ajax
      "url": "/asyn/v1/account/register"
      "type": "post"
      "contentType": 'application/json'
      "data": JSON.stringify(data)
      "success": (ret) ->
        if ret['status'] == 0
          alert "Account has been created successfully!\nPlease wait while we are logging you in."
          loginDate = {
            "username": $username.val()
            "password": $password.val()
          }
          $.ajax
            "url": "/asyn/v1/account/login"
            "type": "post"
            "contentType": 'application/json'
            "data": JSON.stringify(loginDate)
            "success": (ret) ->
              window.location = "/account/settings"
        else
          displayFormError(ret['field'], ret['message'])
      "error": ->
        displayFormError(null, "Server error, please try again.")

    return false  # submitForm

  $form.on("submit", submitForm)

  $direct.click ->
    window.location = "/account/login"


