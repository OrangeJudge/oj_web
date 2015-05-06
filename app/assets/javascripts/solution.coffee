$(document).ready ->
  if $("#code").length > 0
    codeView = ace.edit("code")
    codeView.setTheme("ace/theme/chrome")
    codeView.getSession().setMode("ace/mode/c_cpp")
    codeView.setReadOnly(true)