$(document).ready ->
  $submit = $('#submit')
  username = $('#username').val()
  token = $('#token').val()
  $password = $('#password')
  $password2 = $('#password2')
  $('#reset-password').submit ->
    if $password.val() != $password2.val()
      toastr.error "Passwords are different."
      return false
    disableButton($submit)
    data =
      username: username
      token: token
      password: $password.val()
    $.ajax
      url: '/account/password/reset'
      type: 'post'
      contentType: 'application/json'
      data: JSON.stringify(data)
      success: (ret) ->
        if ret['status']
          toastr.error ret['message']
        else
          toastr.success 'Password reset successfully. Please use the new password to login.'
          setTimeout( ->
            window.location = '/account/login'
          , 1000)
          return false
        enableButton($submit)
    return false