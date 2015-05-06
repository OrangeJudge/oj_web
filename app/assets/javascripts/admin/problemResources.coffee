onPageLoad = ->
  dropZone = new Dropzone("#drop-file", {
    url: "/admin/problem/" + window.problemId + "/resource/file"
    previewTemplate: $("#upload-progress-template").html()
    previewsContainer: "#upload-progress"
  })
  dropZone.on("processing", ->
    $("#drop-file").find(".spinner").show()
  )
  dropZone.on("queuecomplete", ->
    $("#drop-file").find(".spinner").hide()
    loadFileList()
  )
  loadFileList()

previewFile = (file) ->
  $("#preview-frame").html ""
  $("#resource-preview").find(".spinner").show()
  $.ajax
    url: "/admin/problem/" + window.problemId + "/resource/file/" + file["filename"]
    success: (ret) ->
      $("#resource-preview").find(".spinner").hide()
      if ! ret.status
        if ret["data"].trim() == ""
          $("#preview-frame").html "[Nothing to Preview]"
        else
          $("#preview-frame").html ret["data"]

deleteFile = (file) ->
  $.ajax
    url: "/admin/problem/" + window.problemId + "/resource/file/" + file["filename"]
    type: "DELETE"
    success: (ret) ->
      if ret.status
        alert ret.message
      else
        loadFileList()

displayFileList = (fileList) ->
  fileItemTemplate = $("#file-list-item-template").html()
  $uploadFileList = $("#upload-file-list")
  $uploadFileList.html ""
  for file in fileList
    $li = $("<li>")
    $li.html fileItemTemplate
    $li.find(".filename").html file["filename"]
    $li.find(".fa").addClass("fa-file")
    $li.on("click", do(file) ->
      return ->
        previewFile(file)
    )
    $li.find(".delete").on("click", do(file) ->
      return (e) ->
        e.stopPropagation()
        deleteFile(file)
    )
    $uploadFileList.append($li)

loadFileList = ->
  $.ajax
    url: "/admin/problem/" + window.problemId + "/resource/file"
    success: (ret) ->
      displayFileList(ret["data"])

$(document).ready onPageLoad