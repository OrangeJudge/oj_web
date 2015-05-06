IMAGE_EXTENSIONS = ["jpg", "png"]

contentEdited = false

descriptionEditor = null

setupEditor = ->
  descriptionEditor = ace.edit("descriptionEditor")
  descriptionEditor.setTheme("ace/theme/chrome")
  descriptionEditor.getSession().setMode("ace/mode/markdown")
  descriptionEditor.getSession().on('change', ->
    contentEdited = true
    $("#description").val(descriptionEditor.getSession().getValue())
  )

setupFileUpload = ->
  dropZone = new Dropzone("#drop-file", {
    url: "/admin/problem/" + window.problemId + "/basic/file"
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

deleteFile = (file) ->
  $.ajax
    url: "/admin/problem/" + window.problemId + "/basic/file/" + file["filename"]
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
    $li.find(".insert").on("click", do(file) ->
        return (e) ->
          e.stopPropagation()
          insertFile(file)
    )
    $uploadFileList.append($li)

insertFile = (file) ->
  extension = file["filename"].split('.').pop()
  filename = file["filename"]
  filepath = "__ASSETS__/" + file["filename"]
  markdownContent = ""
  if extension in IMAGE_EXTENSIONS
    console.log "insert image"
    markdownContent = "![#{filename}](#{filepath})"
  else
    markdownContent = "[#{filename}](#{filepath})"
  descriptionEditor.insert markdownContent

displayContentPopup = (content) ->
  $content = $("<pre>").html String(content)
  popup = new $.Popup()
  popup.open($content, "html")

previewFile = (file) ->
  path = "/problem/" + window.problemId + "/assets/" + file["filename"]
  extension = file["filename"].split('.').pop()
  if extension in IMAGE_EXTENSIONS
    popup = new $.Popup()
    popup.open(path)
  else
    open = window.confirm "Preview not available. Download this attachment?"
    if open
      window.open path

loadFileList = ->
  $.ajax
    url: "/admin/problem/" + window.problemId + "/basic/file"
    success: (ret) ->
      displayFileList(ret["data"])

setupClosingNotice = ->
  $("input, textarea").on "change keydown", ->
    contentEdited = true
  window.onbeforeunload = ->
    if contentEdited
      'Changes have been made, and have not been safed yet.'
  $("#problem-edit-form").submit ->
    contentEdited = false
    return true

$(document).ready ->
  setupEditor()
  setupClosingNotice()
  setupFileUpload()
