toastr.options =
  positionClass: "toast-bottom-center"

#ace.config.set("basePath", "/assets/javascripts/ace2/")

window.displayContentPopup = (content) -> ->
  $content = $("<pre>").html content
  $content.addClass("mono-font")
  popup = new $.Popup()
  popup.open($content, "html")
  return false

preDefinedResult = {
  "0": "Submitted"
  "200": "Accepted"
}

window.getSolutionResultBlock = (result, message) ->
  if result.toString() of preDefinedResult
    block = preDefinedResult[result.toString()]
  else if result == 400
    block = $("<a>").attr("href", "#").html("Compilation Error")
    .on("click", displayContentPopup(message))
  else if result >= 100 and message != null
    block = message.split("\n")[0]
  return block

window.closeTopNotice = ->
  $(".top-notice").hide(700)

window.getUrlParameter = (sParam) ->
  sPageURL = window.location.search.substring(1)
  sURLVariables = sPageURL.split('&')
  for sParameterName in sURLVariables
    sParameterName = sParameterName.split("=")
    if sParameterName[0] == sParam
      return sParameterName[1]

window.enableButton = ($button) ->
  $button.removeAttr("disabled")

window.disableButton = ($button) ->
  $button.attr("disabled", "disabled")

$(document).ready ->
  $(".rating-display").raty
    path: '/assets/images/raty'
    readOnly: true
    score: ->
      $(this).attr('data-score')