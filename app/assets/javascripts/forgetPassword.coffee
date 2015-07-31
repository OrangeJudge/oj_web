$(document).ready ->
  $submit = $('#submit')
  $email = $('#email')
  $('#request-reset-password').submit ->
    disableButton($submit)
    data =
      'email': $email.val()
    $.ajax
      url: '/asyn/v1/account/email/password-request'
      type: 'post'
      contentType: 'application/json'
      data: JSON.stringify(data)
      success: (ret) ->
        if ret['status']
          toastr.error ret['message']
        else
          toastr.success 'We have send you an email with the instruction to reset your password.'
        enableButton($submit)
    return false