$(document).ready ->
  $("#add-contest").click ->
    $("#add-contest-zone").show(400)

  $("#add-contest-zone").submit ->
    title = $("#title").val()
    postData =
      title: title
    $.ajax
      url: "/admin/contest"
      type: "post"
      contentType: "application/json"
      data: JSON.stringify(postData)
      success: ->
        window.location.reload()
    return false
