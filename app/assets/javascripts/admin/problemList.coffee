setupFileUpload = ->
  dropZone = new Dropzone("#drop-file", {
    url: "/admin/problem/package"
    previewTemplate: $("#upload-progress-template").html()
    previewsContainer: "#upload-progress"
  })
  dropZone.on("processing", ->
    $("#drop-file").find(".spinner").show()
  )
  dropZone.on("queuecomplete", ->
    $("#drop-file").find(".spinner").hide()
  )
  dropZone.on("success", (e) ->
    alert "success"
    console.log e
  )

window.closeAddProblem = ->
  $("#add-problem-zone").hide(400)

window.closeImportProblem = ->
  $("#import-problem-zone").hide(400)

$(document).ready ->
  $("#add-problem").click ->
    $("#add-problem-zone").show(400)

  $("#import-problem").click ->
    $("#import-problem-zone").show(400)

  $("#add-problem-zone").submit ->
    slug = $("#slug").val()
    $.ajax
      "url": "/admin/problem/" + slug
      "type": "put"
      "success": ->
        window.location.reload()
    return false
  setupFileUpload()