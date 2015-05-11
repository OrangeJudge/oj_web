enableButton = ($button) ->
  $button.removeAttr("disabled")

disableButton = ($button) ->
  $button.attr("disabled", "disabled")

$(document).ready ->
  # get HTML elements
  $form = $(".form")
  $username = $("#username")
  $email = $("#email")
  $displayName = $("#displayName")
  $country = $("#country")
  $school = $("#school")
  $gender = $("#gender")
  $password = $("#password")
  $password2 = $("#password2")
  $passwordOld = $("#password-old")
  $submit = $("#submit")
  $direct = $("#redirect")
  $message = $("#message")
  $basicForm = $("#basic-form")
  $submitBasicForm = $("#submit-basic-form")
  $passwordForm = $("#password-form")
  $submitPasswordForm = $("#submit-password-form")

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
  if window.userCountry && window.userCountry.length > 0
    $country.val window.userCountry

  # Setup Avatar Dropzone
  dropzoneConfig =
    url: "/asyn/account/settings/profile-image"
    maxFiles: 1
    previewsContainer: false
    init: ->
      # A hack to disable multiple
      this.hiddenFileInput.removeAttribute('multiple')
  avatarDropzone = new Dropzone("#upload-avatar", dropzoneConfig)
  avatarDropzone.on 'complete', (file) ->
    console.log file
    if file.status == 'error'
      avatarDropzone.removeAllFiles()
      if file.xhr and file.xhr.response
        response = JSON.parse(file.xhr.response.toString())
        alert response.message
      else
        alert "Error!"
    else
      window.location.reload()

  # Basic form
  $basicForm.find("input, select").on "change keypress", ->
    enableButton $submitBasicForm
  $submitBasicForm.click ->
    $basicForm.find(".message").html "Saving changes..."
    # construct submit object
    data = {
      "email": $email.val()
      "displayName": $displayName.val()
      "country": $country.val()
      "school": $school.val()
      "gender": $gender.val() == "1"
    }
    $.ajax
      url: "/asyn/account/settings/basic"
      type: "post"
      contentType: 'application/json'
      data: JSON.stringify(data)
      success: (ret)->
        console.log ret
        disableButton $submitBasicForm
        if ret['status'] == 0
          $basicForm.find(".message").html "Changes saved!"
        else
          $basicForm.find(".message").html ret.message
    return false

  $passwordForm.find("input").on "change keyup", ->
    if $password.val() != $password2.val()
      $passwordForm.find(".message").html "New passwords are different."
    else
      if  $passwordForm.find(".message").html() == "New passwords are different."
        $passwordForm.find(".message").html ""
    if $passwordOld.val() != "" && $password.val() != "" && $password.val() == $password2.val()
      enableButton $submitPasswordForm
    else
      disableButton $submitPasswordForm

  $submitPasswordForm.click ->
    $passwordForm.find(".message").html "Submitting new password..."
    data =
      password: $password.val()
      passwordOld: $passwordOld.val()
    $.ajax
      url: "/asyn/account/settings/password"
      type: "post"
      contentType: 'application/json'
      data: JSON.stringify(data)
      success: (ret)->
        console.log ret
        disableButton $submitPasswordForm
        if ret['status'] == 0
          $passwordForm.find(".message").html "Password changed!"
        else
          $passwordForm.find(".message").html ret.message
    return false

window.resendVerification = ->
  $.post "/asyn/account/email/verify-request", (ret) ->
    if ret['status']
      toastr.error ret['message']
    else
      toastr.success 'A verification email is sent.'