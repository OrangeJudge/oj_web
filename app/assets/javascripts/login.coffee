$(document).ready ->

  # get HTML elements
  $form = $("#login-form")
  $username = $("#username")
  $password = $("#password")
  $submit = $("#submit")
  $direct = $("#redirect")
  $message = $("#message")

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

    # construct submit object
    data = {
      "username": $username.val()
      "password": $password.val()
    }
    console.log data

    # submit the data
    $.ajax
      "url": "/asyn/account/login"
      "type": "post"
      "contentType": 'application/json'
      "data": JSON.stringify(data)
      "success": (ret) ->
        if ret['status'] == 0
          window.location = "/"
        else
          displayFormError(ret['field'], ret['message'])
      "error": ->
        displayFormError(null, "Server error, please try again.")

    return false  # submitForm

  $form.on("submit", submitForm)

  $direct.click ->
    window.location = "/account/register"


